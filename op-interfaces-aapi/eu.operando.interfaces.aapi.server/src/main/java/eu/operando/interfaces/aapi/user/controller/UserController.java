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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

@RestController
@RequestMapping(value = "/aapi/user")
public class UserController {
	
	@Value("${ldap.protocol}")
	private String ldapProtocol;
	
	@Value("${ldap.host}")
	private String ldapHost;
	
	@Value("${ldap.port}")
	private String ldapPort;
	
	@Value("${ldap.username}")
	private String ldapUsername;
	
	@Value("${ldap.password}")
	private String ldapPassword;
	
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ApiOperation(value = "registerUser", notes = "This operation registers a user to OPERANDO's platform.")
    @ApiResponses(value = { 
	    @ApiResponse(code = 500, message = "Internal Server Error"),
	    @ApiResponse(code = 201, message = "User created") })
    @ApiImplicitParams({
	    @ApiImplicitParam(name = "user", value = "User's data", required = true, dataType = "User", paramType = "body") })
    public ResponseEntity<User> addUser(@RequestBody User user) throws UserException {
	/*
	 * try to store the new user into ldap directory. storeUserToLdap :
	 * boolean function that returns true in the case that the user is
	 * successfully inserted in ldap It takes as argument the User object to
	 * be inserted. If the function returns false then an Failure Alert is
	 * returned.
	 */
	try {
	    if (storeUserToLdap(user)) {
		return new ResponseEntity<User>(user, HttpStatus.CREATED);
	    } else {
		return new ResponseEntity<User>(user, HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	} catch (Exception e) {
	    return new ResponseEntity<User>(user, HttpStatus.INTERNAL_SERVER_ERROR);
	}

    }

    @RequestMapping(value = "/{username:.+}", method = RequestMethod.GET)
    @ApiOperation(value = "getUser", notes = "This operation returns the OPERANDO's registed user with given username")
    @ApiResponses(value = {
	    @ApiResponse(code = 500, message = "Internal Server Error"),
	    @ApiResponse(code = 404, message = "User not found"),
	    @ApiResponse(code = 200, message = "User returned") })
    @ApiImplicitParams({
	    @ApiImplicitParam(name = "username", value = "User's username", required = true, dataType = "string", paramType = "path")})
    public ResponseEntity<User> getUser(@PathVariable("username") String username) throws UserException {
	User currentUser = new User();
	try {
	    currentUser = getUserFromLdap(username, getSearchControls());
	} catch (IOException ex) {
	    System.out.println("Error");
	}

	if (currentUser != null) {
	    return new ResponseEntity<User>(currentUser, HttpStatus.OK);
	} else {
	    return new ResponseEntity<User>(currentUser, HttpStatus.NOT_FOUND);
	}

    }

    @RequestMapping(value = "/{username}", method = RequestMethod.PUT)
    @ApiOperation(value = "modifyUser", notes = "This operation updates the OPERANDO's registed user with given username")
    @ApiResponses(value = { 
	    @ApiResponse(code = 500, message = "Internal Server Error"),
	    @ApiResponse(code = 404, message = "User not found"),
	    @ApiResponse(code = 202, message = "User updated") })
    @ApiImplicitParams({
	@ApiImplicitParam(name = "username", value = "user's session ticket (TGT)", required = true, dataType = "string", paramType = "path"),    
	@ApiImplicitParam(name = "user", value = "Users data", required = true, dataType = "User", paramType = "body")})
    public ResponseEntity<User> modifyUser(@PathVariable("username") String username, @RequestBody User user)
	    throws UserException {
        User currentUser = new User();
	try {
            
            //STEP 1:delete user
            deleteUserFromLdap(username);
            
             
//            //STEP 2: recreate user
            if (storeUserToLdap(user)) {
		return new ResponseEntity<User>(user, HttpStatus.CREATED);
	    } else {
		return new ResponseEntity<User>(user, HttpStatus.INTERNAL_SERVER_ERROR);
	    }
//            return new ResponseEntity<User>(user, HttpStatus.CREATED);
            
        } catch (Exception ex) {
	    ex.printStackTrace();
	}
        
	return new ResponseEntity<User>(user, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void deleteUserFromLdap(String username){
        try {
            Hashtable env = new Hashtable();
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            env.put(Context.PROVIDER_URL, (ldapProtocol + "://" + ldapHost + ":" + ldapPort));
            env.put(Context.SECURITY_AUTHENTICATION, "simple");
            env.put(Context.SECURITY_PRINCIPAL, ldapUsername); 
            env.put(Context.SECURITY_CREDENTIALS, ldapPassword);
            env.put("java.naming.ldap.factory.socket", "eu.operando.interfaces.aapi.socketfactory.LdapDefaultSSLSocketFactory");
            // init the connection

            DirContext ctx = new InitialDirContext(env);
//            ctx.destroySubcontext("cn="+username+",ou=People,dc=nodomain");
            ctx.unbind("uid=" + username + ",ou=People,dc=nodomain");
            ctx.destroySubcontext("uid=" + username + ",ou=People,dc=nodomain");
            ctx.close();
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Make the connection with LDAP
        
    }
    @RequestMapping(value = "/{username}", method = RequestMethod.DELETE)
    @ApiOperation(value = "deleteUser", notes = "This operation deletes the OPERANDO's registed user with given username")
    @ApiResponses(value = { 
	    @ApiResponse(code = 500, message = "Internal Server Error"),	    
	    @ApiResponse(code = 404, message = "User not found"),
	    @ApiResponse(code = 202, message = "User deleted") })
    @ApiImplicitParams({
	@ApiImplicitParam(name = "username", value = "User's username", required = true, dataType = "string", paramType = "path")})
    public ResponseEntity<User> deleteUser(@PathVariable("username") String username) throws UserException {
        
        markUserAsDeleted(username);

	return new ResponseEntity<User>(new User(), HttpStatus.ACCEPTED);
    }
    
    @RequestMapping(value = "/getOspList", method = RequestMethod.GET)
    @ApiOperation(value = "getOspList", notes = "This operation gets all available OSPs (deleted OSPs are excluded)")
    @ApiResponses(value = { 
	    @ApiResponse(code = 500, message = "Internal Server Error"),
	    @ApiResponse(code = 201, message = "List created") })
    public ResponseEntity getOspList(){
	try {
            String ospList = "{ \"osps\":\n" +
                             "    [";
                String namesList = getOSPList();
                if (namesList.endsWith(",")) namesList = namesList.substring(0,namesList.length()-1);
                ospList += namesList;
                
                ospList += "]\n" +
                            "}";
	   	return new ResponseEntity(ospList, HttpStatus.CREATED);
	    
	} catch (Exception e) {
	   return new ResponseEntity("", HttpStatus.CREATED);
	}

    }
    private String getOSPList(){
       String names = "";
       try {
            
	    // Make the connection with LDAP
	    Hashtable env = new Hashtable();
	    env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
	    env.put(Context.PROVIDER_URL, (ldapProtocol + "://" + ldapHost + ":" + ldapPort));
	    env.put(Context.SECURITY_AUTHENTICATION, "simple");
	    env.put(Context.SECURITY_PRINCIPAL, ldapUsername);
	    env.put(Context.SECURITY_CREDENTIALS, ldapPassword);
	    // init the connection

	    env.put(Context.REFERRAL, "follow");

	    LdapContext ctx = new InitialLdapContext(env, null);

	    NamingEnumeration<SearchResult> answer = ctx.search("dc=nodomain", "employeeType=*role::OSP*" , getOSPSearchControls());
	    while (answer.hasMore()) {
                
		Attributes attrs = answer.next().getAttributes();
                
                boolean isDeleted = false;
                if(attrs.get("o")!=null && attrs.get("o").contains("deleted")){
                    isDeleted = true;
                }
                if(attrs.get("cn")!=null && !isDeleted)
                {
                    names += "\""+attrs.get("cn").toString().replace("cn: ", "")+"\""+","; 
                    
                }
		

	    } 

	} catch (Exception e) {
	    e.printStackTrace();
	    return null;

	} 

	return names;
    }
    private void markUserAsDeleted(String username){
        Hashtable env = new Hashtable();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, (ldapProtocol + "://" + ldapHost + ":" + ldapPort));
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, ldapUsername); 
        env.put(Context.SECURITY_CREDENTIALS, ldapPassword);
        env.put("java.naming.ldap.factory.socket", "eu.operando.interfaces.aapi.socketfactory.LdapDefaultSSLSocketFactory");
        // init the connection
        try {
             DirContext ctx = new InitialDirContext(env);
             ModificationItem[] mods = new ModificationItem[1];
             javax.naming.directory.Attribute mod0 = new BasicAttribute("o", "deleted");
             
             mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, mod0);
             
             ctx.modifyAttributes("uid=" + username + ",ou=People,dc=nodomain", mods);
        } catch (Exception e) {
            e.printStackTrace();
        }
       
        
    }

    private boolean storeUserToLdap(User user) throws IOException {
    	
	try {
	    // Make the connection with LDAP
	    Hashtable env = new Hashtable();
	    env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
	    env.put(Context.PROVIDER_URL, (ldapProtocol + "://" + ldapHost + ":" + ldapPort));
	    env.put(Context.SECURITY_AUTHENTICATION, "simple");
	    env.put(Context.SECURITY_PRINCIPAL, ldapUsername); 
	    env.put(Context.SECURITY_CREDENTIALS, ldapPassword);
	    env.put("java.naming.ldap.factory.socket", "eu.operando.interfaces.aapi.socketfactory.LdapDefaultSSLSocketFactory");
	    // init the connection

	    DirContext ctx = new InitialDirContext(env);

	    // Fill in LDAP basic attrs
	    Attributes matchAttrs = new BasicAttributes();
	    matchAttrs.put(new BasicAttribute("uid", user.getUsername()));
	    matchAttrs.put(new BasicAttribute("displayName", user.getUsername()));
	    matchAttrs.put(new BasicAttribute("cn", user.getUsername()));
	    matchAttrs.put(new BasicAttribute("sn", user.getUsername()));
	    matchAttrs.put(new BasicAttribute("userPassword", user.getPassword()));
	    matchAttrs.put(new BasicAttribute("objectClass", "inetOrgPerson"));
	    // When the final scheme is ready, the fields below will be changed.
	    // At the moment they are placeholders
            String currentOptionalAttributes = getOptionalAttributesAsString(user);
            if(currentOptionalAttributes!=null && !currentOptionalAttributes.trim().equals(""))  matchAttrs.put(new BasicAttribute("departmentNumber", currentOptionalAttributes));
	   
            String currentPrivacySettings = getPrivacySettingsAsString(user);
            if(currentPrivacySettings!=null && !currentPrivacySettings.trim().equals("")) matchAttrs.put(new BasicAttribute("employeeNumber", currentPrivacySettings));
	    
            String currentRequiredAttributes = getRequiredAttributesAsString(user);
            if(currentRequiredAttributes!=null && !currentRequiredAttributes.trim().equals("")) matchAttrs.put(new BasicAttribute("employeeType", currentRequiredAttributes));
	   
            // Finally add the user
	    ctx.createSubcontext("uid=" + user.getUsername() + ",ou=People,dc=nodomain", matchAttrs);

	} catch (Exception e) {
	    e.printStackTrace();
	    return false;
	} 
	
	return true;
    }

    // gets the optional attributes as a string with specific seperators from
    // the request
    private String getOptionalAttributesAsString(User user) {
	String optionalAttributesAsString = "";
	Attribute[] myOptionalAttrs = user.getOptionalAttrs();
	for (int i = 0; myOptionalAttrs!=null && i < myOptionalAttrs.length; i++) {
            Attribute curAttr = myOptionalAttrs[i];
	    optionalAttributesAsString += curAttr.getAttrName() + "::" + curAttr.getAttrValue() + ";;";

	}
	return optionalAttributesAsString;
    }

    // gets the privacy settings as a string with specific seperators from the
    // request
    private String getPrivacySettingsAsString(User user) {
	String privacySettingsAsString = "";
	PrivacySetting[] myPrivacySettings = user.getPrivacySettings();
	for (int i = 0; myPrivacySettings!=null && i < myPrivacySettings.length; i++) {
	    PrivacySetting curPrivacySetting = myPrivacySettings[i];
	    privacySettingsAsString += curPrivacySetting.getSettingName() + "::" + curPrivacySetting.getSettingValue()
		    + ";;";

	}
	return privacySettingsAsString;
    }

    // gets the required attributes as a string with specific seperators from
    // the request
    private String getRequiredAttributesAsString(User user) {
	String requiredAttributesAsString = "";
	Attribute[] myRequiredAttrs = user.getRequiredAttrs();
	for (int i = 0; myRequiredAttrs!=null && i < myRequiredAttrs.length; i++) {
	    Attribute curAttr = myRequiredAttrs[i];
	    requiredAttributesAsString += curAttr.getAttrName() + "::" + curAttr.getAttrValue() + ";;";

	}
	return requiredAttributesAsString;
    }

    //
    private User getUserFromLdap(String username, SearchControls searchControls) throws IOException {
    	
	User currentUser = new User();
	try {
	    // Make the connection with LDAP
	    Hashtable env = new Hashtable();
	    env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
	    env.put(Context.PROVIDER_URL, (ldapProtocol + "://" + ldapHost + ":" + ldapPort));
	    env.put(Context.SECURITY_AUTHENTICATION, "simple");
	    env.put(Context.SECURITY_PRINCIPAL, ldapUsername);
	    env.put(Context.SECURITY_CREDENTIALS, ldapPassword);
	    // init the connection

	    env.put(Context.REFERRAL, "follow");

	    LdapContext ctx = new InitialLdapContext(env, null);

	    NamingEnumeration<SearchResult> answer = ctx.search("dc=nodomain", "cn=" + username, searchControls);
	    if (answer.hasMore()) {
		Attributes attrs = answer.next().getAttributes();
                
                if(attrs.get("o")!=null && attrs.get("o").toString().equals("o: deleted"))
                {
                    return null; 
                }

		List<Attribute> myOptionalAttrsList = new ArrayList();
                if(attrs.get("departmentNumber")!=null)
                {
                    String optAsString = attrs.get("departmentNumber").toString();
                    String[] optionalRows = optAsString.split(";;");
                    for (int i = 0; i < optionalRows.length; i++) {
                        String[] optAttrParts = optionalRows[i].split("::");
                        Attribute optAttr = new Attribute();
                        if(optAttrParts[0]!=null && optAttrParts[0].contains(":")){
                            optAttr.setAttrName(optAttrParts[0].split(":")[1].trim());
                        }else{
                            optAttr.setAttrName(optAttrParts[0].trim());
                        }
                        optAttr.setAttrValue(optAttrParts[1]);
                        myOptionalAttrsList.add(optAttr);
                    }
                }
		

		Attribute[] myOptionalAttrsArr = new Attribute[myOptionalAttrsList.size()];
		myOptionalAttrsArr = myOptionalAttrsList.toArray(myOptionalAttrsArr);
		currentUser.setOptionalAttrs(myOptionalAttrsArr);

		List<PrivacySetting> myPrivAttrsList = new ArrayList();
                if(attrs.get("employeeNumber")!=null){
                    String privAsString = attrs.get("employeeNumber").toString();
                    String[] privRows = privAsString.split(";;");
                    for (int i = 0; i < privRows.length; i++) {
                        String[] privAttrParts = privRows[i].split("::");
                        PrivacySetting privAttr = new PrivacySetting();
                       if(privAttrParts[0]!=null && privAttrParts[0].contains(":")){
                           privAttr.setSettingName(privAttrParts[0].split(":")[1].trim());
                       }else{
                           privAttr.setSettingName(privAttrParts[0].trim());
                       }
                        
                        privAttr.setSettingValue(privAttrParts[1]);
                        myPrivAttrsList.add(privAttr);
                    }
                }
		

		PrivacySetting[] myPrivAttrsArr = new PrivacySetting[myPrivAttrsList.size()];
		myPrivAttrsArr = myPrivAttrsList.toArray(myPrivAttrsArr);
		currentUser.setPrivacySettings(myPrivAttrsArr);

		List<Attribute> myReqAttrsList = new ArrayList();
                if(attrs.get("employeeType")!=null){
                   String reqAsString = attrs.get("employeeType").toString();
                    String[] reqRows = reqAsString.split(";;");
                    for (int i = 0; i < reqRows.length; i++) {
                        String[] reqAttrParts = reqRows[i].split("::");
                        Attribute reqAttr = new Attribute();
                        if(reqAttrParts[0]!=null && reqAttrParts[0].contains(":")){
                            reqAttr.setAttrName(reqAttrParts[0].split(":")[1].trim());
                        }else{
                            reqAttr.setAttrName(reqAttrParts[0].trim());
                        }
                        reqAttr.setAttrValue(reqAttrParts[1]);
                        myReqAttrsList.add(reqAttr);
                    }
                }
		

		Attribute[] myReqAttrsArr = new Attribute[myReqAttrsList.size()];
		myReqAttrsArr = myReqAttrsList.toArray(myReqAttrsArr);
		currentUser.setRequiredAttrs(myReqAttrsArr);

	    } else {
		System.out.println("user not found.");
		return null;
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	    return null;

	} 

	currentUser.setUsername(username);
	currentUser.setPassword("******");
	return currentUser;
    }

    private SearchControls getSearchControls() {
	SearchControls cons = new SearchControls();
	cons.setSearchScope(SearchControls.SUBTREE_SCOPE);
	String[] attrIDs = { "departmentNumber", "employeeNumber", "employeeType", "o" };
	cons.setReturningAttributes(attrIDs);
	return cons;
    }
    
    private SearchControls getOSPSearchControls() {
	SearchControls cons = new SearchControls();
	cons.setSearchScope(SearchControls.SUBTREE_SCOPE);
	String[] attrIDs = { "cn" };
	cons.setReturningAttributes(attrIDs);
	return cons;
    }
}