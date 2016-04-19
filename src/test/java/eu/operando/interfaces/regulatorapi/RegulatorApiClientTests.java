/*
 * Copyright (c) 2016 Oxford Computer Consultants Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the The MIT License (MIT).
 * which accompanies this distribution, and is available at
 * http://opensource.org/licenses/MIT
 *
 * Contributors:
 *    Matthew Gallagher (Oxford Computer Consultants) - Creation.
 * Initially developed in the context of OPERANDO EU project www.operando.eu
 */
package eu.operando.interfaces.regulatorapi;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.putRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.junit.Assert.assertTrue;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.junit.Test;

import eu.operando.ClientOperandoModuleApiTests;

public class RegulatorApiClientTests extends ClientOperandoModuleApiTests
{
	private RegulatorApiClient client = new RegulatorApiClient(PROTOCOL_AND_HOST, PROTOCOL_AND_HOST,
			PROTOCOL_AND_HOST, PROTOCOL_AND_HOST, PROTOCOL_AND_HOST, PROTOCOL_AND_HOST);
	
	/**
	 * Policy DB
	 */
	@Test
	public void testCreateNewRegulationOnPolicyDb_CorrectHttpRequest()
	{
		//Set up
		String endpoint = ENDPOINT_POLICY_DB_REGULATIONS;
		getWireMockRule().stubFor(post(urlPathEqualTo(endpoint))
				.willReturn(aResponse()));

		PrivacyRegulation regulation = new PrivacyRegulation(-1, "sector", "source", "type", "action", "consent");

		//Exercise
		client.createNewRegulationOnPolicyDb(regulation);

		//Verify
		verifyPostToEndpointWithBodyJsonForRegulation(endpoint, regulation);
	}
	@Test
	public void testCreateNewRegulationOnPolicyDb_ResponseHandledCorrectly()
	{
		//Set up
		PrivacyRegulation regulationToPost = new PrivacyRegulation(-1, "sector", "source", "type", "action", "consent");
		PrivacyRegulation regulationReturnedExpected = new PrivacyRegulation(1, "sector", "source", "type", "action", "consent");
		String endpoint = ENDPOINT_POLICY_DB_REGULATIONS;
		String json = getStringJsonFollowingOperandoConventions(regulationReturnedExpected);
		getWireMockRule().stubFor(post(urlPathEqualTo(endpoint))
				.willReturn(aResponse()
						.withBody(json)));

		
		//Exercise
		PrivacyRegulation regulationReturnedActual = client.createNewRegulationOnPolicyDb(regulationToPost);

		//Verify
		assertTrue("The client did not correctly interpret the returned JSON",
				EqualsBuilder.reflectionEquals(regulationReturnedExpected,regulationReturnedActual));
	}
	@Test
	public void testUpdateExistingRegulationOnPolicyDb_CorrectHttpRequest()
	{
		//Set up
		int regulationId = 1;
		String endpoint = String.format(ENDPOINT_POLICY_DB_REGULATIONS_VARIABLE_REG_ID, regulationId);
		getWireMockRule().stubFor(post(urlPathEqualTo(endpoint))
				.willReturn(aResponse()));

		PrivacyRegulation regulation = new PrivacyRegulation(regulationId, "sector", "source", "type", "action", "consent");

		//Exercise
		client.updateExistingRegulationOnPolicyDb(regulation);

		//Verify
		verifyPutToEndpointWithBodyJsonForRegulation(endpoint, regulation);
	}
	@Test
	public void testUpdateExistingRegulationOnPolicyDb_ResponseHandledCorrectly()
	{
		//Set up
		int regulationId = 1;
		PrivacyRegulation regulationToPut = new PrivacyRegulation(regulationId, "sector", "source", "type", "action", "consent");
		PrivacyRegulation regulationReturnedExpected = regulationToPut;
		String endpoint = String.format(ENDPOINT_POLICY_DB_REGULATIONS_VARIABLE_REG_ID, regulationId);
		String json = getStringJsonFollowingOperandoConventions(regulationReturnedExpected);
		getWireMockRule().stubFor(put(urlPathEqualTo(endpoint))
				.willReturn(aResponse()
						.withBody(json)));


		//Exercise
		PrivacyRegulation regulationReturnedActual = client.updateExistingRegulationOnPolicyDb(regulationToPut);

		//Verify
		assertTrue("The client did not correctly interpret the returned JSON",
				EqualsBuilder.reflectionEquals(regulationReturnedExpected,regulationReturnedActual));
	}
	
	/**
	 * Policy Computation
	 */
	@Test
	public void testSendNewRegulationToPolicyComputation_CorrectHttpRequest()
	{
		//Set up
		int regulationId = 1;
		String endpoint = String.format(ENDPOINT_POLICY_COMPUTATION_REGULATIONS_VARIABLE_REG_ID, regulationId);
		getWireMockRule().stubFor(post(urlPathEqualTo(endpoint))
				.willReturn(aResponse()));
		
		PrivacyRegulation regulation = new PrivacyRegulation(regulationId, "sector", "source", "type", "action", "consent");
		
		//Exercise
		client.sendNewRegulationToPolicyComputation(regulation);
		
		//Verify
		verifyPostToEndpointWithBodyJsonForRegulation(endpoint, regulation);
	}
	@Test
	public void testSendExistingRegulationToPolicyComputation_CorrectHttpRequest()
	{
		//Set up
		int regulationId = 1;
		String endpoint = String.format(ENDPOINT_POLICY_COMPUTATION_REGULATIONS_VARIABLE_REG_ID, regulationId);
		getWireMockRule().stubFor(put(urlPathEqualTo(endpoint))
				.willReturn(aResponse()));
		
		PrivacyRegulation regulation = new PrivacyRegulation(regulationId, "sector", "source", "type", "action", "consent");
		
		//Exercise
		client.sendExistingRegulationToPolicyComputation(regulationId, regulation);
		
		//Verify
		verifyPutToEndpointWithBodyJsonForRegulation(endpoint, regulation);
	}
	
	/**
	 * OSP Enforcement
	 */
	@Test
	public void testSendNewRegulationToOspEnforcement_CorrectHttpRequest()
	{
		//Set up
		int regulationId = 1;
		String endpoint = String.format(ENDPOINT_OSP_ENFORCEMENT_REGULATIONS_VARIABLE_REG_ID, regulationId);
		getWireMockRule().stubFor(post(urlPathEqualTo(endpoint))
				.willReturn(aResponse()));
		
		PrivacyRegulation regulation = new PrivacyRegulation(1, "sector", "source", "type", "action", "consent");
		
		//Exercise
		client.sendNewRegulationToOspEnforcement(regulation);
		
		//Verify
		verifyPostToEndpointWithBodyJsonForRegulation(endpoint, regulation);
	}
	@Test
	public void testSendExistingRegulationToOspEnforcement_CorrectHttpRequest()
	{
		//Set up
		int regulationId = 1;
		String endpoint = String.format(ENDPOINT_OSP_ENFORCEMENT_REGULATIONS_VARIABLE_REG_ID, regulationId);
		getWireMockRule().stubFor(put(urlPathEqualTo(endpoint))
				.willReturn(aResponse()));
		
		PrivacyRegulation regulation = new PrivacyRegulation(regulationId, "sector", "source", "type", "action", "consent");
		
		//Exercise
		client.sendExistingRegulationToOspEnforcement(regulationId, regulation);
		
		//Verify
		verifyPutToEndpointWithBodyJsonForRegulation(endpoint, regulation);
	}
	
	/**
	 * Helpers
	 */
	private void verifyPostToEndpointWithBodyJsonForRegulation(String endpoint, PrivacyRegulation regulation)
	{
		String jsonRegulation = getStringJsonFollowingOperandoConventions(regulation);
		
		getWireMockRule().verify(postRequestedFor(urlPathEqualTo(endpoint))
				.withRequestBody(equalToJson(jsonRegulation)));
	}
	private void verifyPutToEndpointWithBodyJsonForRegulation(String endpoint, PrivacyRegulation regulation)
	{
		String jsonRegulation = getStringJsonFollowingOperandoConventions(regulation);
		
		getWireMockRule().verify(putRequestedFor(urlPathEqualTo(endpoint))
				.withRequestBody(equalToJson(jsonRegulation)));
	}
	
	/**
	 * Report Generator
	 */
	@Test
	public void testGetReport_NoOptionalParameters_CorrectHttpRequest()
	{
		testGetReport_NoOptionalParameters_CorrectHttpRequest(client);
	}
	@Test
	public void testGetReport_TwoOptionalParameters_CorrectHttpRequest()
	{
		testGetReport_TwoOptionalParameters_CorrectHttpRequest(client);
	}
	
	/**
	 * Authentication API
	 */
	@Test
	public void testAuthoriseOsp_CorrectHttpRequest()
	{
		testAuthoriseOsp_CorrectHttpRequest(client);
	}
}