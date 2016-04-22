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

import static org.junit.Assert.assertTrue;

import javax.ws.rs.HttpMethod;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.junit.Test;

import eu.operando.ClientOperandoModuleApiTests;

public class RegulatorApiClientTests extends ClientOperandoModuleApiTests
{
	private RegulatorApiClient client = new RegulatorApiClient(PROTOCOL_AND_HOST_HTTP_LOCALHOST, PROTOCOL_AND_HOST_HTTP_LOCALHOST,
			PROTOCOL_AND_HOST_HTTP_LOCALHOST, PROTOCOL_AND_HOST_HTTP_LOCALHOST, PROTOCOL_AND_HOST_HTTP_LOCALHOST, PROTOCOL_AND_HOST_HTTP_LOCALHOST);
	
	/**
	 * Policy DB
	 */
	@Test
	public void testCreateNewRegulationOnPolicyDb_CorrectHttpRequest()
	{
		//Set up
		String endpoint = ENDPOINT_POLICY_DB_REGULATIONS;
		stub(HttpMethod.POST, endpoint);

		PrivacyRegulation regulation = new PrivacyRegulation(-1, "sector", "source", "type", "action", "consent");

		//Exercise
		client.createNewRegulationOnPolicyDb(regulation);

		//Verify
		verifyWithoutQueryParams(HttpMethod.POST, endpoint, regulation);
	}
	@Test
	public void testCreateNewRegulationOnPolicyDb_ResponseHandledCorrectly()
	{
		//Set up
		PrivacyRegulation regulationToPost = new PrivacyRegulation(-1, "sector", "source", "type", "action", "consent");
		PrivacyRegulation regulationReturnedExpected = new PrivacyRegulation(1, "sector", "source", "type", "action", "consent");
		String endpoint = ENDPOINT_POLICY_DB_REGULATIONS;
		String jsonRegulation = getStringJsonFollowingOperandoConventions(regulationReturnedExpected);
		stub(HttpMethod.POST, endpoint, jsonRegulation);

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
		PrivacyRegulation regulation = new PrivacyRegulation(regulationId, "sector", "source", "type", "action", "consent");

		String endpoint = String.format(ENDPOINT_POLICY_DB_REGULATIONS_VARIABLE_REG_ID, regulationId);
		stub(HttpMethod.PUT, endpoint);

		//Exercise
		client.updateExistingRegulationOnPolicyDb(regulation);

		//Verify
		verifyWithoutQueryParams(HttpMethod.PUT, endpoint, regulation);
	}
	@Test
	public void testUpdateExistingRegulationOnPolicyDb_ResponseHandledCorrectly()
	{
		//Set up
		int regulationId = 1;
		PrivacyRegulation regulationToPut = new PrivacyRegulation(regulationId, "sector", "source", "type", "action", "consent");
		
		String endpoint = String.format(ENDPOINT_POLICY_DB_REGULATIONS_VARIABLE_REG_ID, regulationId);
		PrivacyRegulation regulationReturnedExpected = regulationToPut;
		String jsonRegulation = getStringJsonFollowingOperandoConventions(regulationReturnedExpected);
		stub(HttpMethod.PUT, endpoint, jsonRegulation);

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
		
		PrivacyRegulation regulation = new PrivacyRegulation(regulationId, "sector", "source", "type", "action", "consent");
		
		//Exercise
		client.sendNewRegulationToPolicyComputation(regulation);
		
		//Verify
		String endpoint = String.format(ENDPOINT_POLICY_COMPUTATION_REGULATIONS_VARIABLE_REG_ID, regulationId);
		verifyWithoutQueryParams(HttpMethod.POST, endpoint, regulation);
	}
	@Test
	public void testSendExistingRegulationToPolicyComputation_CorrectHttpRequest()
	{
		//Set up
		int regulationId = 1;		
		PrivacyRegulation regulation = new PrivacyRegulation(regulationId, "sector", "source", "type", "action", "consent");
		
		//Exercise
		client.sendExistingRegulationToPolicyComputation(regulationId, regulation);
		
		//Verify
		String endpoint = String.format(ENDPOINT_POLICY_COMPUTATION_REGULATIONS_VARIABLE_REG_ID, regulationId);
		verifyWithoutQueryParams(HttpMethod.PUT, endpoint, regulation);
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
		PrivacyRegulation regulation = new PrivacyRegulation(1, "sector", "source", "type", "action", "consent");
		
		//Exercise
		client.sendNewRegulationToOspEnforcement(regulation);
		
		//Verify
		verifyWithoutQueryParams(HttpMethod.POST, endpoint, regulation);
	}
	@Test
	public void testSendExistingRegulationToOspEnforcement_CorrectHttpRequest()
	{
		//Set up
		int regulationId = 1;
		String endpoint = String.format(ENDPOINT_OSP_ENFORCEMENT_REGULATIONS_VARIABLE_REG_ID, regulationId);		
		PrivacyRegulation regulation = new PrivacyRegulation(regulationId, "sector", "source", "type", "action", "consent");
		
		//Exercise
		client.sendExistingRegulationToOspEnforcement(regulationId, regulation);
		
		//Verify
		verifyWithoutQueryParams(HttpMethod.PUT, endpoint, regulation);
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