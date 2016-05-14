/*******************************************************************************
 *  # Copyright (c) 2016 {UPRC}.
 *  # All rights reserved. This program and the accompanying materials
 *  # are made available under the terms of the The MIT License (MIT).
 *  # which accompanies this distribution, and is available at
 *  # http://opensource.org/licenses/MIT
 *
 *  # Contributors:
 *  #    {Constantinos Patsakis} {UPRC}
 *  #    {Stamatis Glykos} {UPRC}
 *  #    {Constantinos Alexandris} {UPRC}
 *  # Initially developed in the context of OPERANDO EU project www.operando.eu 
 *******************************************************************************/
package eu.operando.interfaces.aapi.user.controller;

import eu.operando.interfaces.aapi.user.controller.exceptions.UserException;
import eu.operando.interfaces.aapi.user.model.Attribute;
import eu.operando.interfaces.aapi.user.model.PrivacySetting;
import eu.operando.interfaces.aapi.user.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Properties;
import javax.naming.Context;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

@RestController
@RequestMapping(value = "/aapi/user")
public class UserController {

	@RequestMapping(value="/register", method = RequestMethod.POST)
	@ApiOperation(value = "registerUser", notes = "This operation registers a user to OPERANDO's platform.")	
	public ResponseEntity<User> addUser(@RequestBody User user) throws UserException {
		/*try to store the new user into ldap directory. 
                storeUserToLdap : boolean function that returns true in the case that the user is successfully inserted in ldap
                It takes as argument the User object to be inserted. If the function returns false then an Failure Alert is returned.
                */
                try{
                    if(storeUserToLdap(user)){
                        return new ResponseEntity<User>(user, HttpStatus.CREATED);
                    }else{
                        return new ResponseEntity<User>(user, HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                }catch(Exception e){
                    return new ResponseEntity<User>(user, HttpStatus.INTERNAL_SERVER_ERROR);
                }
                
        
		
	}

	@RequestMapping(value="/{username}", method = RequestMethod.GET)
	@ApiOperation(value = "getUser", notes = "This operation returns the OPERANDO's registed user with given username")
	public ResponseEntity<User> getUser(@PathVariable("username") String username) throws UserException {

		return new ResponseEntity<User>(new User(), HttpStatus.OK);
	}

	@RequestMapping(value="/{username}", method = RequestMethod.PUT)
	@ApiOperation(value = "modifyUser", notes = "This operation updates the OPERANDO's registed user with given username")
	public ResponseEntity<User> modifyUser(@PathVariable("username") String username, @RequestBody User user) throws UserException {

		return new ResponseEntity<User>(new User(), HttpStatus.CREATED);
	}

	@RequestMapping(value="/{username}", method = RequestMethod.DELETE)
	@ApiOperation(value = "deleteUser", notes = "This operation deletes the OPERANDO's registed user with given username")
	public ResponseEntity<User> deleteUser(@PathVariable("username") String username) throws UserException {

		return new ResponseEntity<User>(new User(), HttpStatus.ACCEPTED);
	}
        
        private boolean storeUserToLdap(User user) throws IOException{
            InputStream inputStream = null;
            
            try {
                //Get the properties file with the ldap connection info
                Properties prop = new Properties();
                String propFileName = "my.properties";

                inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

                if (inputStream != null) {
                        prop.load(inputStream);
                } else {
                        throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
                }
                        
                
                //Make the connection with LDAP
                Hashtable env = new Hashtable();
                env.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
                //LDAP URL
                env.put(Context.PROVIDER_URL, prop.getProperty("ldap.URL"));
                //Type of Authentication
                env.put(Context.SECURITY_AUTHENTICATION,"simple");
                //admin username
                env.put(Context.SECURITY_PRINCIPAL,prop.getProperty("ldap.username")); // specify the username
                //admin psw - TO BE FIXED - in config file
                env.put(Context.SECURITY_CREDENTIALS,prop.getProperty("ldap.password"));           // specify the password
                //init the connection
                
                DirContext ctx = new InitialDirContext(env);

                //Fill in LDAP basic attrs
                Attributes matchAttrs = new BasicAttributes();
                matchAttrs.put(new BasicAttribute("uid", user.getUsername()));
                matchAttrs.put(new BasicAttribute("displayName", user.getUsername()));
                matchAttrs.put(new BasicAttribute("cn", user.getUsername()));   
                matchAttrs.put(new BasicAttribute("sn", user.getUsername()));           
                matchAttrs.put(new BasicAttribute("userPassword", user.getPassword())); 
                matchAttrs.put(new BasicAttribute("objectClass", "inetOrgPerson")); 
                //When the final scheme is ready, the fields below will be changed. At the moment they are placeholders
                matchAttrs.put(new BasicAttribute("departmentNumber", getOptionalAttributesAsString(user))); 
                matchAttrs.put(new BasicAttribute("employeeNumber", getPrivacySettingsAsString(user))); 
                matchAttrs.put(new BasicAttribute("employeeType", getRequiredAttributesAsString(user))); 
                //Finally add the user
                ctx.createSubcontext("uid="+user.getUsername()+",ou=People,dc=nodomain", matchAttrs); 
               
                
                
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }finally{
                inputStream.close();
            }
            
            return true;
        }
        //gets the optional attributes as a string with specific seperators from the request
        private String getOptionalAttributesAsString(User user){
            String optionalAttributesAsString = "";
            Attribute[] myOptionalAttrs = user.getOptionalAttrs();
            for(int i=0; i<myOptionalAttrs.length;i++){
                Attribute curAttr = myOptionalAttrs[i];
                optionalAttributesAsString += curAttr.getAttrName()+"::"+curAttr.getAttrValue()+";;";
                
            }
            return optionalAttributesAsString;
        }
        
        //gets the privacy settings as a string with specific seperators from the request
        private String getPrivacySettingsAsString(User user){
            String privacySettingsAsString = "";
            PrivacySetting[] myPrivacySettings = user.getPrivacySettings();
            for(int i=0; i<myPrivacySettings.length;i++){
                PrivacySetting curPrivacySetting = myPrivacySettings[i];
                privacySettingsAsString += curPrivacySetting.getSettingName()+"::"+curPrivacySetting.getSettingValue()+";;";
                
            }
            return privacySettingsAsString;
        }
        //gets the required attributes as a string with specific seperators from the request
        private String getRequiredAttributesAsString(User user){
            String requiredAttributesAsString = "";
            Attribute[] myRequiredAttrs = user.getRequiredAttrs();
            for(int i=0; i<myRequiredAttrs.length;i++){
                Attribute curAttr = myRequiredAttrs[i];
                requiredAttributesAsString += curAttr.getAttrName()+"::"+curAttr.getAttrValue()+";;";
                
            }
            return requiredAttributesAsString;
        }
}
