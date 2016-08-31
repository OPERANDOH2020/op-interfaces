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

import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.http.HttpException;

import eu.operando.ClientOperandoModuleApi;
import eu.operando.Utils;
import eu.operando.interfaces.rapi.model.PrivacyRegulation;
import eu.operando.interfaces.rapi.model.PrivacyRegulationInput;
import io.swagger.models.HttpMethod;

/**
 * This component of the Regulator API handles all requests for information that the Regulator API sends to other OPERANDO modules.
 * 
 * <p>
 * It converts objects, which represent information that needs to be sent somewhere else, to an appropriate HTTP request message (using JAX-RS). The
 * response message is converted back to an appropriate object (using JAX-RS).
 */
public class RegulatorApiClient extends ClientOperandoModuleApi
{
	// Location of properties file.
	private static final String PROPERTIES_FILE_RAPI;

	// Properties file property names.
	private static final String PROPERTY_NAME_ORIGIN_AUTHENTICATION_API;
	private static final String PROPERTY_NAME_ORIGIN_OSP_ENFORCEMENT;
	private static final String PROPERTY_NAME_ORIGIN_REPORT_GENERATOR;
	private static final String PROPERTY_NAME_ORIGIN_LOG_DB;
	private static final String PROPERTY_NAME_ORIGIN_POLICY_DB;
	private static final String PROPERTY_NAME_ORIGIN_POLICY_COMPUTATION;

	// Properties file property values.
	private static final String ORIGIN_AUTHENTICATION_API;
	private static final String ORIGIN_OSP_ENFORCEMENT;
	private static final String ORIGIN_REPORT_GENERATOR;
	private static final String ORIGIN_LOG_DB;
	private static final String ORIGIN_POLICY_DB;
	private static final String ORIGIN_POLICY_COMPUTATION;

	static
	{
		// Location of properties file.
		PROPERTIES_FILE_RAPI = "config.properties";

		// Properties file property names.
		PROPERTY_NAME_ORIGIN_AUTHENTICATION_API = "originAuthenticationApi";
		PROPERTY_NAME_ORIGIN_OSP_ENFORCEMENT = "originOspEnforcement";
		PROPERTY_NAME_ORIGIN_REPORT_GENERATOR = "originReportGenerator";
		PROPERTY_NAME_ORIGIN_LOG_DB = "originLogDb";
		PROPERTY_NAME_ORIGIN_POLICY_DB = "originPolicyDb";
		PROPERTY_NAME_ORIGIN_POLICY_COMPUTATION = "originPolicyComputation";

		// Properties file property values.
		ORIGIN_AUTHENTICATION_API = Utils.loadPropertyString(PROPERTIES_FILE_RAPI, PROPERTY_NAME_ORIGIN_AUTHENTICATION_API);
		ORIGIN_OSP_ENFORCEMENT = Utils.loadPropertyString(PROPERTIES_FILE_RAPI, PROPERTY_NAME_ORIGIN_OSP_ENFORCEMENT);
		ORIGIN_REPORT_GENERATOR = Utils.loadPropertyString(PROPERTIES_FILE_RAPI, PROPERTY_NAME_ORIGIN_REPORT_GENERATOR);
		ORIGIN_LOG_DB = Utils.loadPropertyString(PROPERTIES_FILE_RAPI, PROPERTY_NAME_ORIGIN_LOG_DB);
		ORIGIN_POLICY_DB = Utils.loadPropertyString(PROPERTIES_FILE_RAPI, PROPERTY_NAME_ORIGIN_POLICY_DB);
		ORIGIN_POLICY_COMPUTATION = Utils.loadPropertyString(PROPERTIES_FILE_RAPI, PROPERTY_NAME_ORIGIN_POLICY_COMPUTATION);
	}

	public RegulatorApiClient()
	{
		super(ORIGIN_AUTHENTICATION_API, ORIGIN_OSP_ENFORCEMENT, ORIGIN_REPORT_GENERATOR, ORIGIN_LOG_DB);
		this.originPolicyDb = ORIGIN_POLICY_DB;
		this.originPolicyComputation = ORIGIN_POLICY_COMPUTATION;
	}
	
	private String originPolicyDb = "";
	private String originPolicyComputation = "";
	
	public RegulatorApiClient(String originAuthenticationApi, String originOspEnforcement, String originReportGenerator, String originLogDb, String originPolicyDb,
			String originPolicyComputation)
	{
		super(originAuthenticationApi, originOspEnforcement, originReportGenerator, originLogDb);
		this.originPolicyDb = originPolicyDb;
		this.originPolicyComputation = originPolicyComputation;
	}

	/**
	 * PDB
	 */
	public PrivacyRegulation createNewRegulationOnPolicyDb(PrivacyRegulationInput privacyReulationInput) throws HttpException
	{
		return sendRegulationToPdb(privacyReulationInput, ENDPOINT_POLICY_DB_REGULATIONS, HttpMethod.POST);
	}

	public PrivacyRegulation updateExistingRegulationOnPolicyDb(String regId, PrivacyRegulationInput input) throws HttpException
	{
		String endpoint = ENDPOINT_POLICY_DB_REGULATIONS_VARIABLE_REG_ID.replace("{reg_id}", regId);
		return sendRegulationToPdb(input, endpoint, HttpMethod.PUT);
	}

	/**
	 * PC
	 */
	public boolean sendNewRegulationToPolicyComputation(PrivacyRegulation regulation)
	{
		return sendRegulationToProcessingModule(regulation, originPolicyComputation, ENDPOINT_POLICY_COMPUTATION_REGULATIONS, HttpMethod.POST);
	}

	public boolean sendExistingRegulationToPolicyComputation(PrivacyRegulation regulation)
	{
		String regId = regulation.getRegId();
		String endpoint = ENDPOINT_POLICY_COMPUTATION_REGULATIONS_VARIABLE_REG_ID.replace("{reg_id}", regId);
		return sendRegulationToProcessingModule(regulation, originPolicyComputation, endpoint, HttpMethod.PUT);
	}

	/**
	 * OSE
	 */
	public boolean sendNewRegulationToOspEnforcement(PrivacyRegulation regulation)
	{
		return sendRegulationToProcessingModule(regulation, getOriginOspEnforcement(), ENDPOINT_OSP_ENFORCEMENT_REGULATIONS, HttpMethod.POST);
	}

	public boolean sendExistingRegulationToOspEnforcement(PrivacyRegulation regulation)
	{
		String regId = regulation.getRegId();
		String endpoint = ENDPOINT_OSP_ENFORCEMENT_REGULATIONS_VARIABLE_REG_ID.replace("{reg_id}", regId);
		return sendRegulationToProcessingModule(regulation, getOriginOspEnforcement(), endpoint, HttpMethod.PUT);
	}

	/**
	 * Encodes a regulation in JSON and sends it to the specified endpoint of the PDB, using the specified HTTP method. If possible, a
	 * PrivacyRegulation is read from the response. If not, an HttpException is thrown.
	 * 
	 * @param privacyRegulationInput
	 *            the regulation to send to the PDB.
	 * @param endpoint
	 *            the endpoint of the PDB to send the request to.
	 * @param httpMethod
	 *            the HTTP method to send in the request.
	 * @return the privacy regulation in the response from the PDB.
	 * @throws HttpException
	 *             if the request is not successful
	 */
	private PrivacyRegulation sendRegulationToPdb(PrivacyRegulationInput privacyRegulationInput, String endpoint, HttpMethod httpMethod) throws HttpException
	{
		Response response = sendRegulationToModule(privacyRegulationInput, originPolicyDb, endpoint, httpMethod);

		// Only try to interpret the response if the status code is a success code.
		int statusCodeResponse = response.getStatus();
		boolean responseSuccessful = Utils.statusCodeIsInFamily(statusCodeResponse, Status.Family.SUCCESSFUL);
		if (!responseSuccessful)
		{
			throw new HttpException("A response from the PDB had status code: " + statusCodeResponse);
		}

		String strJson = response.readEntity(String.class);
		return getObjectFromJsonFollowingOperandoConventions(strJson, PrivacyRegulation.class);
	}

	/**
	 * Send an HTTP request, with a regulation encoded as JSON.
	 * 
	 * @param regulation
	 *            The regulation to be encoded as JSON in the body.
	 * @param originTargetModule
	 *            The origin of the target.
	 * @param endpointTargetModule
	 *            The path component of the destination URL.
	 * @param httpMethod
	 *            The method with which the HTTP request should be sent.
	 * @return An indication as to whether the response indicates success.
	 */
	private boolean sendRegulationToProcessingModule(PrivacyRegulation regulation, String originTargetModule, String endpointTargetModule, HttpMethod httpMethod)
	{
		Response responseFromModule = sendRegulationToModule(regulation.getInputObject(), originTargetModule, endpointTargetModule, httpMethod);

		// Return a boolean to indicate whether the request was successful.
		int statusCodeFromPc = responseFromModule.getStatus();
		boolean requestWasSuccessful = statusCodeFromPc == Status.ACCEPTED.getStatusCode();
		return requestWasSuccessful;
	}

	/**
	 * Send an HTTP request, with a regulation encoded as JSON.
	 * 
	 * @param inputObject
	 *            The regulation to be encoded as JSON in the body.
	 * @param originTargetModule
	 *            The origin of the target.
	 * @param endpointTargetModule
	 *            The path component of the destination URL.
	 * @param httpMethod
	 *            The method with which the HTTP request should be sent.
	 * @return The Response from the module.
	 */
	private Response sendRegulationToModule(PrivacyRegulationInput inputObject, String originTargetModule, String endpointTargetModule, HttpMethod httpMethod)
	{
		// Create a web target for the correct end-point.
		WebTarget target = getClient().target(originTargetModule);
		target = target.path(endpointTargetModule);

		// Send the request with the regulation encoded as JSON in the body, using the appropriate HTTP method.
		Builder requestBuilder = target.request();
		Response responseFromPc = null;
		switch (httpMethod)
		{
		case POST:
			responseFromPc = requestBuilder.post(createEntityStringJsonFromObject(inputObject));
			break;
		case PUT:
			responseFromPc = requestBuilder.put(createEntityStringJsonFromObject(inputObject));
			break;
		default:
			throw new NotImplementedException(
					"eu.operando.interfaces.rapi.api.impl.RegulatorApiClient.sendRegulationToModule has only been implemented for POST and PUT");
		}
		return responseFromPc;
	}
}
