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
package eu.operando.interfaces.aapi.ticket.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import eu.operando.interfaces.aapi.ticket.controller.exceptions.TicketingException;
import eu.operando.interfaces.aapi.ticket.model.Credentials;
import eu.operando.interfaces.aapi.user.model.Attribute;
import eu.operando.interfaces.aapi.user.model.PrivacySetting;
import eu.operando.interfaces.aapi.user.model.User;
import eu.operando.interfaces.aapi.utils.Log;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.client.model.LogRequest;
import java.net.URLEncoder;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

@RestController
@RequestMapping(value = "aapi/tickets")
public class TicketController {

	@Value("${cas.protocol}")
	private String casProtocol;
	
	@Value("${cas.host}")
	private String casHost;
	
	@Value("${cas.port}")
	private String casPort;
	
	@Value("${cas.webApp}")
	private String casWebApp;	
        
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
		
	@Autowired
	CloseableHttpClient httpClient;    
	
	Log log = new Log();

	@RequestMapping(value = "", method = RequestMethod.POST)
	@ApiOperation(value = "SingleSignOnOperando", notes = "This operation makes a request for a ticket granting ticket (TGT) to the AAPI, "
			+ "which is the session key for the application?'s SSO session. "
			+ "This operation should be called the very first time for an application to be authenticated to OPERANDOs CAS server, through a login form.", response = String.class)
	@ApiResponses(value = { 
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 400, message = "Unable to create a ticket granting ticket (TGT)"),
			@ApiResponse(code = 201, message = "ticket granting ticket (TGT) created") })
	@ApiImplicitParams({
		@ApiImplicitParam(name = "userCredential", value = "User's credential", required = true, dataType = "Credentials", paramType = "body") })
	public final ResponseEntity<String> createTicketGrantingTicket( @RequestBody Credentials userCredential) throws TicketingException {

		if (userCredential == null || 
				(userCredential.getUsername() == null || "".equals(userCredential.getUsername().trim())) ||
				(userCredential.getPassword() == null || "".equals(userCredential.getPassword().trim()))) {

			log.logMe(LogRequest.LogLevelEnum.WARN, "", "Missing credentials", LogRequest.LogPriorityEnum.NORMAL.toString(), "op-interfaces-aapi");
			
			throw new TicketingException(HttpStatus.BAD_REQUEST.toString(), " Missing credentials", null);
		}

		HttpPost httpPost = null;
		CloseableHttpResponse response = null;
		try {
                        
			httpPost = new HttpPost(casProtocol + "://" + casHost + ":" + casPort + "/" + casWebApp + "/v1/tickets");
                        
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair("username", userCredential.getUsername()));
			nvps.add(new BasicNameValuePair("password", userCredential.getPassword()));
			httpPost.setEntity(new UrlEncodedFormEntity(nvps));

			response = httpClient.execute(httpPost);

			// Get CAS's server response HEADERS
			HttpHeaders headers = new HttpHeaders();
			for (Header header : response.getAllHeaders()) {
				headers.add(header.getName(), header.getValue());
			}

			// Get CAS's server response BODY
			String body = null;
			HttpEntity entity = response.getEntity();
			body = EntityUtils.toString(entity);

			if (HttpStatus.CREATED.value() == response.getStatusLine().getStatusCode()) {

				body = body.substring(body.indexOf("v1/tickets/") + "v1/tickets/".length()).split("\"")[0];
				headers.add("Content-Length", String.valueOf(body.length()));
				return new ResponseEntity<String>(body, headers, HttpStatus.CREATED);
			} else {

				return new ResponseEntity<String>(body, headers, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception ex) {
			
			log.logMe(LogRequest.LogLevelEnum.WARN, "", ex.getMessage(), LogRequest.LogPriorityEnum.CRITICAL.toString(), "op-interfaces-aapi");
			System.out.println(ex.getMessage());
			return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
                        
		} finally {
			try {
                            if(response!=null) response.close();
			} catch (IOException e) {
                            System.out.println(e.getMessage());
                        }
		}
	}

	@RequestMapping(value = "/{tgt}", method = RequestMethod.POST)
	@ApiOperation(value = "getServiceTicket", notes = "This operation makes a request for a service ticket (ST) to the AAPI, "
			+ "which is the authorization ticket for a specific protected service of OPERANDO's system. "
			+ "This operation should be called each time the user tried to access a protected service", response = String.class)
	@ApiResponses(value = { 
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 400, message = "Unable to create a service ticket (ST)"),
			@ApiResponse(code = 201, message = "service ticket (ST) created") })
	@ApiImplicitParams({
		@ApiImplicitParam(name = "tgt", value = "user's session ticket (TGT)", required = true, dataType = "string", paramType = "path"),    
		@ApiImplicitParam(name = "serviceId", value = "service's identifier", required = true, dataType = "string", paramType = "body")})	    
	public final ResponseEntity<String> createServiceTicket( @PathVariable("tgt") final String tgt, @RequestBody final String serviceId) throws TicketingException {
		
		if ((serviceId == null || "".equals(serviceId.trim())) || (tgt == null || "".equals(tgt.trim()))) {

			log.logMe(LogRequest.LogLevelEnum.WARN, "", "Neither serviceId, nor tgt can be EMPTY || NULL", LogRequest.LogPriorityEnum.NORMAL.toString(), "op-interfaces-aapi");
						
			throw new TicketingException(HttpStatus.BAD_REQUEST.toString(), "Neither serviceId, nor tgt can be NULL", null);
		} else{
			
			log.logMe(LogRequest.LogLevelEnum.INFO, "", String.format("User with tgt %s requests a ticket for serviceId %s ", tgt, serviceId), LogRequest.LogPriorityEnum.NORMAL.toString(), "op-interfaces-aapi");
		}

		HttpPost httpPost = null;
		CloseableHttpResponse response = null;
		try {

			httpPost = new HttpPost(casProtocol + "://" + casHost + ":" + casPort + "/" + casWebApp + "/v1/tickets/" + tgt);

			httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");

			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair("service", serviceId.replaceAll("\"","").replaceAll("'","")));
			httpPost.setEntity(new UrlEncodedFormEntity(nvps));

			response = httpClient.execute(httpPost);

			// Get CAS's server response HEADERS
			HttpHeaders headers = new HttpHeaders();
			for (Header header : response.getAllHeaders()) {
				headers.add(header.getName(), header.getValue());
			}

			// Get CAS's server response BODY
			String body = null;
			HttpEntity entity = response.getEntity();
			body = EntityUtils.toString(entity);

			if (HttpStatus.OK.value() == response.getStatusLine().getStatusCode() || HttpStatus.CREATED.value() == response.getStatusLine().getStatusCode()) {
				
				log.logMe(LogRequest.LogLevelEnum.INFO, "", String.format("Ticket %s created for user with tgt %s and serviceId %s ", body, tgt, serviceId), LogRequest.LogPriorityEnum.NORMAL.toString(), "op-interfaces-aapi");
				return new ResponseEntity<String>(body, headers, HttpStatus.OK);
			} else if (HttpStatus.BAD_REQUEST.value() == response.getStatusLine().getStatusCode()) {
				
				log.logMe(LogRequest.LogLevelEnum.WARN, "", body, LogRequest.LogPriorityEnum.HIGH.toString(), "op-interfaces-aapi");
				return new ResponseEntity<String>(body, headers, HttpStatus.BAD_REQUEST);				
			} else {
				
				log.logMe(LogRequest.LogLevelEnum.WARN, "", body, LogRequest.LogPriorityEnum.HIGH.toString(), "op-interfaces-aapi");
				return new ResponseEntity<String>(body, headers, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} catch (Exception ex) {
			
			log.logMe(LogRequest.LogLevelEnum.WARN, "", ex.getMessage(), LogRequest.LogPriorityEnum.CRITICAL.toString(), "op-interfaces-aapi");			
			return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
		} finally {
			try {
				response.close();
			} catch (IOException e) {
			}
		}
	}
   
	@RequestMapping(value = "{st}/validate", method = RequestMethod.GET)
	@ApiOperation(value = "validateServiceTicket", notes = "This operation is used to validate a service ticket (st) generated by the /tickets/{tgt} service. "
			+ "It is called by the protected service to ensure the proper authorization of the user to the protected information.")
	@ApiResponses(value = { 
			@ApiResponse(code = 500, message = "Internal Server Error"),
			@ApiResponse(code = 400, message = "service ticket (ST) is invalid"), 
			@ApiResponse(code = 200, message = "service ticket (ST) is valid"), })
	@ApiImplicitParams({
		@ApiImplicitParam(name = "st", value = "service ticket (ST)", required = true, dataType = "string", paramType = "path"),
		@ApiImplicitParam(name = "serviceId", value = "Service's identifier", required = true, dataType = "string", paramType = "query")})
	public final ResponseEntity<String> validateServiceTicket(
			@PathVariable("st") final String serviceTicket,
			@RequestParam("serviceId") final String service) throws TicketingException {

		if (serviceTicket == null || "".equals(serviceTicket.trim())) {
			
			log.logMe(LogRequest.LogLevelEnum.WARN, "", "serviceId can be EMPTY  || NULL", LogRequest.LogPriorityEnum.NORMAL.toString(), "op-interfaces-aapi");
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		} else{
			
			log.logMe(LogRequest.LogLevelEnum.INFO, "", String.format("Validate st %s for serviceId %s", serviceTicket, service), LogRequest.LogPriorityEnum.NORMAL.toString(), "op-interfaces-aapi");
		}
		
		HttpGet httpGet = null;
		CloseableHttpResponse response = null;
		
		try {
			String baseURL =  casProtocol + "://" + casHost + ":" + casPort + "/" + casWebApp + "/serviceValidate";
			
                        httpGet = new HttpGet(baseURL + "?ticket=" + serviceTicket + "&service=" + URLEncoder.encode(service.replaceAll("\"","").replaceAll("'",""), "UTF-8")) ;
			
			response = httpClient.execute(httpGet);
			
			// Get CAS's server response HEADERS
			HttpHeaders headers = new HttpHeaders();
			for (Header header : response.getAllHeaders()) {
				if(!header.getName().equalsIgnoreCase("Content-Length"))
                                    headers.add(header.getName(), header.getValue());
			}
			
			// Get CAS's server response BODY
			String body = null;
			HttpEntity entity = response.getEntity();
			body = EntityUtils.toString(entity);						

			if (body.contains("<cas:authenticationSuccess>")){
				//replace the username with the uuid
                                int usernameStart = body.indexOf("<cas:user>")+10;
                                int usernameStop = body.indexOf("</cas:user>");
                                String username = body.substring(usernameStart, usernameStop);
                                String uuid = getUuidFromUsername(username, getSearchControls());
                                body = body.replace(username, uuid);
                                
				log.logMe(LogRequest.LogLevelEnum.INFO, "", String.format("st %s for serviceId %s is valid", serviceTicket, service), LogRequest.LogPriorityEnum.NORMAL.toString(), "op-interfaces-aapi");
				return new ResponseEntity<String>(body, headers, HttpStatus.OK);
			} else if (body.contains("<cas:authenticationFailure code=\'INVALID_TICKET\'>")){
				
				log.logMe(LogRequest.LogLevelEnum.WARN, "", body, LogRequest.LogPriorityEnum.HIGH.toString(), "op-interfaces-aapi");
				return new ResponseEntity<String>(body, headers, HttpStatus.BAD_REQUEST);
                                
			} else if (body.contains("<cas:authenticationFailure code=\'INVALID_SERVICE\'>")){
				
				log.logMe(LogRequest.LogLevelEnum.WARN, "", body, LogRequest.LogPriorityEnum.HIGH.toString(), "op-interfaces-aapi");
				return new ResponseEntity<String>(body, headers, HttpStatus.BAD_REQUEST);
			}else {
				
				log.logMe(LogRequest.LogLevelEnum.WARN, "", body, LogRequest.LogPriorityEnum.HIGH.toString(), "op-interfaces-aapi");			
				return new ResponseEntity<String>(body, headers, HttpStatus.INTERNAL_SERVER_ERROR);				
			}
			
		} catch (Exception ex){
			
			log.logMe(LogRequest.LogLevelEnum.WARN, "", ex.getMessage(), LogRequest.LogPriorityEnum.CRITICAL.toString(), "op-interfaces-aapi");			
			return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
		}finally{
			try {
				response.close();
			} catch (IOException e) {
                            
			}
		}
                
           
	}
        
        private String getUuidFromUsername(String username, SearchControls searchControls) throws IOException {
            String uuid = "";
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

                    //get required attributes in order to extract uuid
                    List<Attribute> myReqAttrsList = new ArrayList();
                    if(attrs.get("employeeType")!=null){
                       String reqAsString = attrs.get("employeeType").toString();
                        String[] reqRows = reqAsString.split(";;");
                        for (int i = 0; i < reqRows.length; i++) {
                            String[] reqAttrParts = reqRows[i].split("::");
                            Attribute reqAttr = new Attribute();
                            if(reqAttrParts[0]!=null && reqAttrParts[0].contains(":")){
                                if(reqAttrParts[0].split(":")[1].trim().equals("uuid")){
                                    uuid = reqAttrParts[1];
                                }
                            }else{
                                if(reqAttrParts[0].trim().equals("uuid")){
                                     uuid = reqAttrParts[1];
                                };
                            }
                        }
                    }
                    if (uuid.equals("")){
                        uuid = username;
                    }
                } else {
                    uuid = username;
                }
            } catch (Exception e) {
                uuid = username;
                e.printStackTrace();
                
            }         
            return uuid;
    }

    private SearchControls getSearchControls() {
	SearchControls cons = new SearchControls();
	cons.setSearchScope(SearchControls.SUBTREE_SCOPE);
	String[] attrIDs = { "employeeType" };
	cons.setReturningAttributes(attrIDs);
	return cons;
    }
}
