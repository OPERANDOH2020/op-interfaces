/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.operando.interfaces.aapi.ticket.controller;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.logging.Logger;

public class RestClient {
	
	private static final Logger LOGGER = Logger.getLogger(RestClient.class.getName());

	public URL createUrl(String url) throws MalformedURLException {
                LOGGER.info("Creating url '" + url +"'");
		return new URL(url);
	}
	
	public HttpURLConnection get(String url) throws IOException {
		LOGGER.info("Getting from url '" + url +"'");
		
		URL connectionUrl = createUrl(url);
		return (HttpURLConnection) connectionUrl.openConnection();
		
	}
	
	/**
	 * Helper Method to post data to the given url and with the given params
	 * @param url
	 * @param params
	 * @return
	 * @throws IOException
	 */
	public HttpURLConnection post(String url, Map<String, Object> params) throws IOException {
            LOGGER.info("Posting to url '" + url +"' w/ params '" + params.toString() + "'");

            URL connectionUrl = createUrl(url);
            byte[] postDataBytes = convertParamMapToBytes(params);
            HttpURLConnection conn = (HttpURLConnection)connectionUrl.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
            conn.setDoOutput(true);
            conn.getOutputStream().write(postDataBytes);
            return conn;
	}
	
	/**
	 * Helper method to convert a map to POST bytes
	 * @param params
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private byte[] convertParamMapToBytes(Map<String, Object> params) throws UnsupportedEncodingException {
            StringBuilder postData = new StringBuilder();
            for (Map.Entry<String,Object> param : params.entrySet()) {
                if (postData.length() != 0) postData.append('&');
                postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                postData.append('=');
                postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
            }
            return postData.toString().getBytes("UTF-8");
	}
	
}