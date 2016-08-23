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
package eu.operando.interfaces.rapi.api.impl;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import javax.ws.rs.HttpMethod;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.http.HttpException;
import javax.ws.rs.core.Response.Status;
import org.junit.Test;

import eu.operando.ClientOperandoModuleApiTests;
import eu.operando.interfaces.rapi.api.impl.RegulatorApiClient;
import eu.operando.interfaces.rapi.model.DtoPrivacyRegulation.PrivateInformationTypeEnum;
import eu.operando.interfaces.rapi.model.DtoPrivacyRegulation.RequiredConsentEnum;
import eu.operando.interfaces.rapi.model.PrivacyRegulation;
import eu.operando.interfaces.rapi.model.PrivacyRegulationInput;

public class RegulatorApiClientTests extends ClientOperandoModuleApiTests
{
	private RegulatorApiClient client = new RegulatorApiClient(ORIGIN_WIREMOCK, ORIGIN_WIREMOCK, ORIGIN_WIREMOCK, ORIGIN_WIREMOCK, ORIGIN_WIREMOCK, ORIGIN_WIREMOCK);

	/**
	 * Policy DB
	 * @throws HttpException 
	 */
	@Test
	public void testCreateNewRegulationOnPolicyDb_CorrectHttpRequest() throws HttpException
	{
		// Set up
		String endpoint = ENDPOINT_POLICY_DB_REGULATIONS;
		stub(HttpMethod.POST, endpoint);

		PrivacyRegulationInput regulationInput = new PrivacyRegulationInput("sector", "source", PrivateInformationTypeEnum.BEHAVIOURAL, "action", RequiredConsentEnum.IN);

		// Exercise
		client.createNewRegulationOnPolicyDb(regulationInput);

		// Verify
		verifyCorrectHttpRequestNoQueryParams(HttpMethod.POST, endpoint, regulationInput);
	}

	@Test(expected=HttpException.class)
	public void testCreateNewRegulationOnPolicyDb_FailedPost_HttpExceptionThrown() throws HttpException
	{
		// Set up
		PrivacyRegulationInput regulationToPost =
				new PrivacyRegulationInput("sector", "source", PrivateInformationTypeEnum.BEHAVIOURAL, "action", RequiredConsentEnum.IN);
		String endpoint = ENDPOINT_POLICY_DB_REGULATIONS;
		stub(HttpMethod.POST, endpoint, "", Status.INTERNAL_SERVER_ERROR);
		
		// Exercise
		client.createNewRegulationOnPolicyDb(regulationToPost);
		
		// Verify - done in test annotation.
	}
	
	@Test
	public void testCreateNewRegulationOnPolicyDb_SuccessfulPost_ResponseBodyInterpretedCorrectly() throws HttpException
	{
		// Set up
		PrivacyRegulationInput regulationToPost =
				new PrivacyRegulationInput("sector", "source", PrivateInformationTypeEnum.BEHAVIOURAL, "action", RequiredConsentEnum.IN);
		PrivacyRegulation regulationReturnedFromPdb = new PrivacyRegulation("1", regulationToPost.getLegislationSector(), regulationToPost.getPrivateInformationSource(),
				regulationToPost.getPrivateInformationType(), regulationToPost.getAction(), regulationToPost.getRequiredConsent());
		String endpoint = ENDPOINT_POLICY_DB_REGULATIONS;
		stub(HttpMethod.POST, endpoint, regulationReturnedFromPdb);

		// Exercise
		PrivacyRegulation regulationReturnedFromClient = client.createNewRegulationOnPolicyDb(regulationToPost);

		// Verify
		assertTrue("The client did not correctly interpret the returned JSON", EqualsBuilder.reflectionEquals(regulationReturnedFromPdb, regulationReturnedFromClient));
	}

	@Test
	public void testUpdateExistingRegulationOnPolicyDb_CorrectHttpRequest()
	{
		// Set up
		PrivacyRegulation regulation = new PrivacyRegulation("1", "sector", "source", PrivateInformationTypeEnum.BEHAVIOURAL, "action", RequiredConsentEnum.IN);

		String regulationId = regulation.getRegId();
		String endpoint = ENDPOINT_POLICY_DB_REGULATIONS_VARIABLE_REG_ID.replace("{reg_id}", regulationId);
		stub(HttpMethod.PUT, endpoint);

		// Exercise
		client.updateExistingRegulationOnPolicyDb(regulation);

		// Verify
		PrivacyRegulationInput inputObject = regulation.getInputObject();
		verifyCorrectHttpRequestNoQueryParams(HttpMethod.PUT, endpoint, inputObject);
	}

	@Test
	public void testUpdateExistingRegulationOnPolicyDb_ResponseHandledCorrectly()
	{
		// Set up
		PrivacyRegulation regulationToPut = new PrivacyRegulation("1", "sector", "source", PrivateInformationTypeEnum.BEHAVIOURAL, "action", RequiredConsentEnum.IN);

		String regulationId = regulationToPut.getRegId();
		String endpoint = ENDPOINT_POLICY_DB_REGULATIONS_VARIABLE_REG_ID.replace("{reg_id}", regulationId);
		PrivacyRegulation regulationReturnedFromPdb = regulationToPut;
		
		stub(HttpMethod.PUT, endpoint, regulationReturnedFromPdb);

		// Exercise
		PrivacyRegulation regulationReturnedFromClient = client.updateExistingRegulationOnPolicyDb(regulationToPut);

		// Verify
		assertTrue("The client did not correctly interpret the returned JSON", EqualsBuilder.reflectionEquals(regulationReturnedFromPdb, regulationReturnedFromClient));
	}

	/**
	 * Policy Computation
	 */
	@Test
	public void testSendNewRegulationToPolicyComputation_CorrectHttpRequest()
	{
		// Set up
		PrivacyRegulation regulation = new PrivacyRegulation("1", "sector", "source", PrivateInformationTypeEnum.BEHAVIOURAL, "action", RequiredConsentEnum.IN);

		// Exercise
		client.sendNewRegulationToPolicyComputation(regulation);

		// Verify
		String endpoint = ENDPOINT_POLICY_COMPUTATION_REGULATIONS;
		PrivacyRegulationInput inputObject = regulation.getInputObject();
		verifyCorrectHttpRequestNoQueryParams(HttpMethod.POST, endpoint, inputObject);
	}
	
	@Test
	public void testSendNewRegulationToPolicyComputation_FailureFromPolicyComputation_FalseReturned()
	{
		// Set up
		PrivacyRegulation regulation = new PrivacyRegulation("1", "sector", "source", PrivateInformationTypeEnum.BEHAVIOURAL, "action", RequiredConsentEnum.IN);
		stub(HttpMethod.POST, ENDPOINT_POLICY_COMPUTATION_REGULATIONS, "", Status.NOT_FOUND);

		// Exercise
		boolean success = client.sendNewRegulationToPolicyComputation(regulation);
		
		// Verify
		assertFalse("When PC fails to process the new regulation, the RAPI client should return false", success);
	}

	@Test
	public void testSendNewRegulationToPolicyComputation_SuccessFromPolicyComputation_TrueReturned()
	{
		// Set up
		PrivacyRegulation regulation = new PrivacyRegulation("1", "sector", "source", PrivateInformationTypeEnum.BEHAVIOURAL, "action", RequiredConsentEnum.IN);
		stub(HttpMethod.POST, ENDPOINT_POLICY_COMPUTATION_REGULATIONS, "", Status.ACCEPTED);

		// Exercise
		boolean success = client.sendNewRegulationToPolicyComputation(regulation);
		
		// Verify
		assertTrue("When PC successfully processes the new regulation, the RAPI client should return true", success);
	}
	
	@Test
	public void testSendExistingRegulationToPolicyComputation_CorrectHttpRequest()
	{
		// Set up
		PrivacyRegulation regulation = new PrivacyRegulation("1", "sector", "source", PrivateInformationTypeEnum.BEHAVIOURAL, "action", RequiredConsentEnum.IN);

		// Exercise
		client.sendExistingRegulationToPolicyComputation(regulation);

		// Verify
		String regulationId = regulation.getRegId();
		String endpoint = ENDPOINT_POLICY_COMPUTATION_REGULATIONS_VARIABLE_REG_ID.replace("{reg_id}", regulationId);
		PrivacyRegulationInput inputObject = regulation.getInputObject();
		verifyCorrectHttpRequestNoQueryParams(HttpMethod.PUT, endpoint, inputObject);
	}

	/**
	 * OSP Enforcement
	 */
	@Test
	public void testSendNewRegulationToOspEnforcement_CorrectHttpRequest()
	{
		// Set up
		PrivacyRegulation regulation = new PrivacyRegulation("1", "sector", "source", PrivateInformationTypeEnum.BEHAVIOURAL, "action", RequiredConsentEnum.IN);

		// Exercise
		client.sendNewRegulationToOspEnforcement(regulation);

		// Verify
		String endpoint = ENDPOINT_OSP_ENFORCEMENT_REGULATIONS;
		PrivacyRegulationInput inputObject = regulation.getInputObject();
		verifyCorrectHttpRequestNoQueryParams(HttpMethod.POST, endpoint, inputObject);
	}
	
	@Test
	public void testSendNewRegulationToOspEnforcement_FailureFromOspEnforcement_FalseReturned()
	{
		// Set up
		PrivacyRegulation regulation = new PrivacyRegulation("1", "sector", "source", PrivateInformationTypeEnum.BEHAVIOURAL, "action", RequiredConsentEnum.IN);
		stub(HttpMethod.POST, ENDPOINT_OSP_ENFORCEMENT_REGULATIONS, "", Status.NOT_FOUND);

		// Exercise
		boolean success = client.sendNewRegulationToOspEnforcement(regulation);
		
		// Verify
		assertFalse("When OSE fails to process the new regulation, the RAPI client should return false", success);
	}

	@Test
	public void testSendNewRegulationToOspEnforcement_SuccessFromOspEnforcement_TrueReturned()
	{
		// Set up
		PrivacyRegulation regulation = new PrivacyRegulation("1", "sector", "source", PrivateInformationTypeEnum.BEHAVIOURAL, "action", RequiredConsentEnum.IN);
		stub(HttpMethod.POST, ENDPOINT_OSP_ENFORCEMENT_REGULATIONS, "", Status.ACCEPTED);

		// Exercise
		boolean success = client.sendNewRegulationToOspEnforcement(regulation);
		
		// Verify
		assertTrue("When OSE successfully processes the new regulation, the RAPI client should return true", success);
	}

	@Test
	public void testSendExistingRegulationToOspEnforcement_CorrectHttpRequest()
	{
		// Set up
		PrivacyRegulation regulation = new PrivacyRegulation("1", "sector", "source", PrivateInformationTypeEnum.BEHAVIOURAL, "action", RequiredConsentEnum.IN);

		// Exercise
		client.sendExistingRegulationToOspEnforcement(regulation);

		// Verify
		String regulationId = regulation.getRegId();
		String endpoint = ENDPOINT_OSP_ENFORCEMENT_REGULATIONS_VARIABLE_REG_ID.replace("{reg_id}", regulationId);
		PrivacyRegulationInput inputObject = regulation.getInputObject();
		verifyCorrectHttpRequestNoQueryParams(HttpMethod.PUT, endpoint, inputObject);
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
		testIsOspAuthenticated_CorrectHttpRequest(client);
	}

	/**
	 * Log DB
	 */
	@Test
	public void testLogActivity_CorrectHttpRequest()
	{
		testLogActivity_CorrectHttpRequest(client);
	}
}