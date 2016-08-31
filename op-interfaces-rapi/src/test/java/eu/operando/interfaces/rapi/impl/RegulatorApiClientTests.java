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
package eu.operando.interfaces.rapi.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.http.HttpException;
import org.junit.Test;

import eu.operando.ClientOperandoModuleApiTests;
import eu.operando.OperandoCommunicationException;
import eu.operando.interfaces.rapi.model.DtoPrivacyRegulation.PrivateInformationTypeEnum;
import eu.operando.interfaces.rapi.model.DtoPrivacyRegulation.RequiredConsentEnum;
import eu.operando.interfaces.rapi.impl.RegulatorApiClient;
import eu.operando.interfaces.rapi.model.PrivacyRegulation;
import eu.operando.interfaces.rapi.model.PrivacyRegulationInput;

public class RegulatorApiClientTests extends ClientOperandoModuleApiTests
{
	// TODO - the client should be replaced by static methods. Injecting origins might be a bit tricky, but it would make the source code better. 
	private RegulatorApiClient client = new RegulatorApiClient(ORIGIN_WIREMOCK, ORIGIN_WIREMOCK, ORIGIN_WIREMOCK, ORIGIN_WIREMOCK, ORIGIN_WIREMOCK, ORIGIN_WIREMOCK);

	/**
	 * Policy DB
	 */
	@Test
	public void testCreateNewRegulationOnPolicyDb_CorrectHttpRequest() throws HttpException
	{
		testSendRegulation_CorrectHttpRequest(ModuleRapiCommunicatesWith.PDB, true);
	}

	@Test(expected = HttpException.class)
	public void testCreateNewRegulationOnPolicyDb_FailedPost_HttpExceptionThrown() throws HttpException
	{
		testSendRegulationToPdb(true, false);
	}

	@Test
	public void testCreateNewRegulationOnPolicyDb_SuccessfulPost_ResponseBodyInterpretedCorrectly() throws HttpException
	{
		testSendRegulationToPdb(true, true);
	}

	@Test
	public void testUpdateExistingRegulationOnPolicyDb_CorrectHttpRequest() throws HttpException
	{
		testSendRegulation_CorrectHttpRequest(ModuleRapiCommunicatesWith.PDB, false);
	}

	@Test(expected = HttpException.class)
	public void testUpdateExistingRegulationOnPolicyDb_FailedPost_HttpExceptionThrown() throws HttpException
	{
		testSendRegulationToPdb(false, false);
	}

	@Test
	public void testUpdateExistingRegulationOnPolicyDb_SuccessfulPost_ResponseBodyInterpretedCorrectly() throws HttpException
	{
		testSendRegulationToPdb(false, true);
	}

	/**
	 * Policy Computation
	 */
	@Test
	public void testSendNewRegulationToPolicyComputation_CorrectHttpRequest() throws HttpException
	{
		testSendRegulation_CorrectHttpRequest(ModuleRapiCommunicatesWith.PC, true);
	}

	@Test
	public void testSendNewRegulationToPolicyComputation_FailureFromPolicyComputation_FalseReturned() throws HttpException
	{
		testSendRegulationToProcessingModule_ReturnValue(ModuleRapiCommunicatesWith.PC, true, Status.NOT_FOUND, false);
	}

	@Test
	public void testSendNewRegulationToPolicyComputation_SuccessFromPolicyComputation_TrueReturned() throws HttpException
	{
		testSendRegulationToProcessingModule_ReturnValue(ModuleRapiCommunicatesWith.PC, true, Status.ACCEPTED, true);
	}

	@Test
	public void testSendExistingRegulationToPolicyComputation_CorrectHttpRequest() throws HttpException
	{
		testSendRegulation_CorrectHttpRequest(ModuleRapiCommunicatesWith.PC, false);
	}

	@Test
	public void testSendExistingRegulationToPolicyComputation_FailureFromPolicyComputation_FalseReturned() throws HttpException
	{
		testSendRegulationToProcessingModule_ReturnValue(ModuleRapiCommunicatesWith.PC, false, Status.NOT_FOUND, false);
	}

	@Test
	public void testSendExistingRegulationToPolicyComputation_SuccessFromPolicyComputation_TrueReturned() throws HttpException
	{
		testSendRegulationToProcessingModule_ReturnValue(ModuleRapiCommunicatesWith.PC, false, Status.ACCEPTED, true);
	}

	/**
	 * OSP Enforcement
	 */
	@Test
	public void testSendNewRegulationToOspEnforcement_CorrectHttpRequest() throws HttpException
	{
		testSendRegulation_CorrectHttpRequest(ModuleRapiCommunicatesWith.OSE, true);
	}

	@Test
	public void testSendNewRegulationToOspEnforcement_FailureFromOspEnforcement_FalseReturned() throws HttpException
	{
		testSendRegulationToProcessingModule_ReturnValue(ModuleRapiCommunicatesWith.OSE, true, Status.NOT_FOUND, false);
	}

	@Test
	public void testSendNewRegulationToOspEnforcement_SuccessFromOspEnforcement_TrueReturned() throws HttpException
	{
		testSendRegulationToProcessingModule_ReturnValue(ModuleRapiCommunicatesWith.OSE, true, Status.ACCEPTED, true);
	}

	@Test
	public void testSendExistingRegulationToOspEnforcement_CorrectHttpRequest() throws HttpException
	{
		testSendRegulation_CorrectHttpRequest(ModuleRapiCommunicatesWith.OSE, false);
	}

	@Test
	public void testSendExistingRegulationToOspEnforcement_FailureFromOspEnforcement_FalseReturned() throws HttpException
	{
		testSendRegulationToProcessingModule_ReturnValue(ModuleRapiCommunicatesWith.OSE, false, Status.NOT_FOUND, false);
	}

	@Test
	public void testSendExistingRegulationToOspEnforcement_SuccessFromOspEnforcement_TrueReturned() throws HttpException
	{
		testSendRegulationToProcessingModule_ReturnValue(ModuleRapiCommunicatesWith.OSE, false, Status.ACCEPTED, true);
	}

	/**
	 * Test the client uses a correct HTTP request when sending a regulation to another module.
	 * 
	 * @param module
	 *        the module to send the regulation to.
	 * @param newRegulation
	 *        whether the regulation is new.
	 * @throws HttpException
	 */
	private void testSendRegulation_CorrectHttpRequest(ModuleRapiCommunicatesWith module, boolean newRegulation) throws HttpException
	{
		// Set up
		PrivacyRegulation regulation = new PrivacyRegulation("1", "sector", "source", PrivateInformationTypeEnum.BEHAVIOURAL, "action", RequiredConsentEnum.IN);
		String httpMethod = determineHttpMethod(newRegulation);
		String endpointExpected = determineEndpoint(module, regulation, newRegulation);
		stub(httpMethod, endpointExpected);

		// Exercise
		sendRegulation(module, regulation, newRegulation);

		// Verify
		PrivacyRegulationInput inputObject = regulation.getInputObject();
		verifyCorrectHttpRequestNoQueryParams(httpMethod, endpointExpected, inputObject);
	}

	/**
	 * Test the client behaves correctly when sending a regulation to the PDB.
	 * 
	 * @param newRegulation
	 *        whether the regulation is new.
	 * @param successfulRequest
	 *        whether the request should return a (stubbed) successful response.
	 * @throws HttpException
	 */
	private void testSendRegulationToPdb(boolean newRegulation, boolean successfulRequest) throws HttpException
	{
		// Set up
		String httpMethod = determineHttpMethod(newRegulation);
		PrivacyRegulation regulationToSend = new PrivacyRegulation("1", "sector", "source", PrivateInformationTypeEnum.BEHAVIOURAL, "action", RequiredConsentEnum.IN);
		String endpoint = determineEndpoint(ModuleRapiCommunicatesWith.PDB, regulationToSend, newRegulation);
		PrivacyRegulation regulationReturnedFromPdb = regulationToSend;
		if (successfulRequest)
		{
			stub(httpMethod, endpoint, regulationReturnedFromPdb);
		}
		else
		{
			stub(httpMethod, endpoint, "", Status.INTERNAL_SERVER_ERROR);
		}

		// Exercise
		PrivacyRegulation regulationReturnedFromClient = sendRegulationToPdb(regulationToSend, newRegulation);

		// Verify
		if (successfulRequest)
		{
			assertTrue("The client did not correctly interpret the returned JSON",
					EqualsBuilder.reflectionEquals(regulationReturnedFromPdb, regulationReturnedFromClient));
		}
	}

	/**
	 * Test that the client behaves correctly when sending a regulation to the PC or OSE.
	 * 
	 * @param module
	 *        the module to send the regulation to.
	 * @param newRegulation
	 *        whether the regulation is new.
	 * @param statusInResponse
	 *        the status to return in the (stubbed) response to the request.
	 * @param expectedReturnValue
	 *        the value which is expected from the client.
	 * @throws HttpException
	 */
	private void testSendRegulationToProcessingModule_ReturnValue(ModuleRapiCommunicatesWith module, boolean newRegulation, Status statusInResponse,
			boolean expectedReturnValue) throws HttpException
	{
		// Set up
		PrivacyRegulation regulation = new PrivacyRegulation("1", "sector", "source", PrivateInformationTypeEnum.BEHAVIOURAL, "action", RequiredConsentEnum.IN);
		String httpMethod = determineHttpMethod(newRegulation);
		String endpoint = determineEndpoint(module, regulation, newRegulation);
		stub(httpMethod, endpoint, "", statusInResponse);

		// Exercise
		boolean success = sendRegulation(module, regulation, newRegulation);

		// Verify
		assertEquals("When the processing module successfully processes the regulation, the RAPI client should return true", expectedReturnValue, success);
	}

	/**
	 * Ask the client to send a regulation to another module.
	 * 
	 * @param module
	 *        the module to send the regulation to.
	 * @param regulation
	 *        the regulation to send.
	 * @param newRegulation
	 *        whether the regulation is new.
	 * @return whether the response from the module indicates success.
	 * @throws HttpException
	 */
	private boolean sendRegulation(ModuleRapiCommunicatesWith module, PrivacyRegulation regulation, boolean newRegulation) throws HttpException
	{
		boolean success;

		switch (module)
		{
			case PDB:
				sendRegulationToPdb(regulation, newRegulation);
				success = true;
				break;
			case PC:
				if (newRegulation)
				{
					success = client.sendNewRegulationToPolicyComputation(regulation);
				}
				else
				{
					success = client.sendExistingRegulationToPolicyComputation(regulation);
				}
				break;
			case OSE:
				if (newRegulation)
				{
					success = client.sendNewRegulationToOspEnforcement(regulation);
				}
				else
				{
					success = client.sendExistingRegulationToOspEnforcement(regulation);
				}
				break;
			default:
				throw new NotImplementedException("This module is not supported.");
		}

		return success;
	}

	/**
	 * Ask the client to send a regulation to the PDB.
	 * 
	 * @param regulation
	 *        the regulation to send.
	 * @param newRegulation
	 *        whether the regulation is new.
	 * @return the regulation the PDB returns.
	 * @throws HttpException
	 */
	private PrivacyRegulation sendRegulationToPdb(PrivacyRegulation regulation, boolean newRegulation) throws HttpException
	{
		PrivacyRegulation regulationFromPdb = null;

		if (newRegulation)
		{
			regulationFromPdb = client.createNewRegulationOnPolicyDb(regulation.getInputObject());
		}
		else
		{
			regulationFromPdb = client.updateExistingRegulationOnPolicyDb(regulation.getRegId(), regulation.getInputObject());
		}

		return regulationFromPdb;
	}

	/**
	 * Determines what HTTP method should be used when sending a regulation to another module.
	 * 
	 * @param newRegulation
	 *        whether the regulation is new.
	 * @return
	 */
	private String determineHttpMethod(boolean newRegulation)
	{
		String httpMethod = HttpMethod.POST;
		if (!newRegulation)
		{
			httpMethod = HttpMethod.PUT;
		}
		return httpMethod;
	}

	/**
	 * Determines what endpoint the client should use when sending a regulation.
	 * 
	 * @param module
	 *        the module to send the regulation to.
	 * @param regulation
	 *        the regulation to send.
	 * @param newRegulation
	 *        whether the regulation is new.
	 * @return
	 */
	private String determineEndpoint(ModuleRapiCommunicatesWith module, PrivacyRegulation regulation, boolean newRegulation)
	{
		String endpoint = "";

		switch (module)
		{
			case PDB:
				if (newRegulation)
				{
					endpoint = ENDPOINT_POLICY_DB_REGULATIONS;
				}
				else
				{
					String regulationId = regulation.getRegId();
					endpoint = ENDPOINT_POLICY_DB_REGULATIONS_VARIABLE_REG_ID.replace("{reg_id}", regulationId);
				}
				break;
			case PC:
				if (newRegulation)
				{
					endpoint = ENDPOINT_POLICY_COMPUTATION_REGULATIONS;
				}
				else
				{
					String regulationId = regulation.getRegId();
					endpoint = ENDPOINT_POLICY_COMPUTATION_REGULATIONS_VARIABLE_REG_ID.replace("{reg_id}", regulationId);
				}
				break;
			case OSE:
				if (newRegulation)
				{
					endpoint = ENDPOINT_OSP_ENFORCEMENT_REGULATIONS;
				}
				else
				{
					String regulationId = regulation.getRegId();
					endpoint = ENDPOINT_OSP_ENFORCEMENT_REGULATIONS_VARIABLE_REG_ID.replace("{reg_id}", regulationId);
				}
				break;
			default:
				throw new NotImplementedException("This module is not supported.");
		}

		return endpoint;
	}

	/**
	 * Report Generator
	 */
	@Test
	public void testGetReport_NoOptionalParameters_CorrectHttpRequest() throws OperandoCommunicationException
	{
		testGetReport_NoOptionalParameters_CorrectHttpRequest(client);
	}

	@Test
	public void testGetReport_OneOptionalParameter_CorrectHttpRequest() throws OperandoCommunicationException
	{
		testGetReport_OneOptionalParameter_CorrectHttpRequest(client);
	}

	@Test
	public void testGetReport_TwoOptionalParameters_CorrectHttpRequest() throws OperandoCommunicationException
	{
		testGetReport_TwoOptionalParameters_CorrectHttpRequest(client);
	}

	@Test
	public void testGetReport_NotFoundFromReportGenerator_OperandoCommunicationExceptionThrownWithNotFoundError()
	{
		testGetReport_NotFoundFromReportGenerator_OperandoCommunicationExceptionThrownWithNotFoundError(client);
	}

	@Test
	public void testGetReport_ServerErrorFromReportGenerator_OperandoCommunicationExceptionThrownWithErrorFromOtherModule()
	{
		testGetReport_ServerErrorFromReportGenerator_OperandoCommunicationExceptionThrownWithErrorFromOtherModule(client);
	}

	@Test
	public void testGetReport_OkFromReportGenerator_ReportInterpretedCorreclty()
	{
		// TODO - this is yet to be implemented.
		fail();
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

	private enum ModuleRapiCommunicatesWith
	{
		PDB,
		PC,
		OSE
	}
}