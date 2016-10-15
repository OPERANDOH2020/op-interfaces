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
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;
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
import eu.operando.interfaces.aapi.user.model.User;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping(value = "aapi/tickets")
public class TicketController {
    
    private static final String CAS_HOSTNAME = "snf-706921.vm.okeanos.grnet.gr";
    
    CloseableHttpClient httpclient = HttpClients.createDefault();

    public TicketController() {
	try {
	    SSLContextBuilder builder = new SSLContextBuilder();
	    builder.loadTrustMaterial(null, new TrustStrategy() {
		public boolean isTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
		    return true;
		}
	    });
	    
	    SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(builder.build(), new HostnameVerifier() {
		public boolean verify(String hostname, SSLSession session) {
		    return true;
		}
	    });
	    
	    httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
	} catch (Exception ex) {
	    System.out.println(ex.getMessage());

	    httpclient = null;
	}

    }

    /**
    *
    * 
    */
    @RequestMapping(value = "", method = RequestMethod.POST)
    @ApiOperation(value = "SingleSignOnOperando", notes = "This operation makes a request for a ticket granting ticket (TGT) to the AAPI, "
    	+ "which is the session key for the applicationï's SSO session. "
    	+ "This operation should be called the very first time for an application to be authenticated to OPERANDOs CAS server, through a login form.", response = String.class)
    @ApiResponses(value = { 
	    @ApiResponse(code = 500, message = "Internal Server Error"),
	    @ApiResponse(code = 400, message = "Unable to create a ticket granting ticket (TGT)"),
	    @ApiResponse(code = 201, message = "ticket granting ticket (TGT) created") })
    @ApiImplicitParams({
	    @ApiImplicitParam(name = "userCredential", value = "User's credential", required = true, dataType = "Credentials", paramType = "body") })
    public final ResponseEntity<String> createTicketGrantingTicket(
	    @RequestBody Credentials userCredential) throws TicketingException {

	if (userCredential == null || 
		(userCredential.getUsername() == null || "".equals(userCredential.getUsername().trim())) ||
		(userCredential.getPassword() == null || "".equals(userCredential.getPassword().trim()))) {
	    
	    System.out.println(HttpStatus.BAD_REQUEST.toString() + " Missing credentials");
	    throw new TicketingException(HttpStatus.BAD_REQUEST.toString(), " Missing credentials", null);
	}

	HttpPost httpPost = null;
	CloseableHttpResponse response = null;
	try {
	    httpPost = new HttpPost("https://" + CAS_HOSTNAME + ":8443/casv415/v1/tickets");

	    List<NameValuePair> nvps = new ArrayList<NameValuePair>();
	    nvps.add(new BasicNameValuePair("username", userCredential.getUsername()));
	    nvps.add(new BasicNameValuePair("password", userCredential.getPassword()));
	    httpPost.setEntity(new UrlEncodedFormEntity(nvps));

	    response = httpclient.execute(httpPost);

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
	    System.out.println(ex.getMessage());
	    return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
	} finally {
	    try {
		response.close();
	    } catch (IOException e) {}
	}
    }

    /**
    *
    * 
    */
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
    public final ResponseEntity<String> createServiceTicket(
	    @PathVariable("tgt") final String tgt,
	    @RequestBody final String serviceId) throws TicketingException {

	if ((serviceId == null || "".equals(serviceId.trim())) || 
		(tgt == null || "".equals(tgt.trim()))) {
	    
	    System.out.println(HttpStatus.BAD_REQUEST.toString() + " Neither serviceId, nor tgt can be NULL");
	    throw new TicketingException(HttpStatus.BAD_REQUEST.toString(), "Neither serviceId, nor tgt can be NULL", null);
	}

	HttpPost httpPost = null;
	CloseableHttpResponse response = null;
	try {

	    httpPost = new HttpPost("https://" + CAS_HOSTNAME + ":8443/casv415/v1/tickets/" + tgt);

	    httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");

	    List<NameValuePair> nvps = new ArrayList<NameValuePair>();
	    nvps.add(new BasicNameValuePair("service", serviceId));
	    httpPost.setEntity(new UrlEncodedFormEntity(nvps));

	    response = httpclient.execute(httpPost);

	    // Get CAS's server response HEADERS
	    HttpHeaders headers = new HttpHeaders();
	    for (Header header : response.getAllHeaders()) {
		headers.add(header.getName(), header.getValue());
	    }

	    // Get CAS's server response BODY
	    String body = null;
	    HttpEntity entity = response.getEntity();
	    body = EntityUtils.toString(entity);
	    	    
	    if (HttpStatus.OK.value() == response.getStatusLine().getStatusCode() ||
		HttpStatus.CREATED.value() == response.getStatusLine().getStatusCode()) {

		return new ResponseEntity<String>(body, headers, HttpStatus.OK);
	    } else if (HttpStatus.BAD_REQUEST.value() == response.getStatusLine().getStatusCode()) {
		System.out.println(HttpStatus.BAD_REQUEST + body);
		return new ResponseEntity<String>(body, headers, HttpStatus.BAD_REQUEST);
	    } else {
		System.out.println(HttpStatus.INTERNAL_SERVER_ERROR + body);
		return new ResponseEntity<String>(body, headers, HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	} catch (Exception ex) {
	    System.out.println(ex.getMessage());
	    return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
	} finally {
	    try {
		response.close();
	    } catch (IOException e) {
	    }
	}
    }
        
    /**
     *
     * 
     */    
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
	    return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
	}
	
	RestClient restClient = null;
	HttpURLConnection huc = null;
	try{
	    restClient =  new RestClient();

	    String baseURL =  "https://" + CAS_HOSTNAME + ":8443" + "/casv415/serviceValidate";

	    //Make the connection to the rest api
	    huc =  restClient.get(baseURL + "?ticket=" + serviceTicket + "&service=" + service) ; 

	    //Get the result
	    StringBuffer content=new StringBuffer();
	    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(huc.getInputStream()));

	    String line;

	    // read from the urlconnection via the bufferedreader
	    while ((line = bufferedReader.readLine()) != null)
	    {
		content.append(line + "\n");
	    }
	    bufferedReader.close();
	    
	    if (content.toString().contains("<cas:authenticationSuccess>")){
                
		return new ResponseEntity<String>(content.toString(), HttpStatus.OK);
	    } else if (content.toString().contains("<cas:authenticationFailure code=\'INVALID_TICKET\'>")){
		return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
	    } else {
		System.out.println(content.toString());
		return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
	    }		
	} catch (Exception ex){
	    System.out.println(ex.getMessage());
	    return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
	}finally{
	    huc.disconnect();
	}
    }
}
