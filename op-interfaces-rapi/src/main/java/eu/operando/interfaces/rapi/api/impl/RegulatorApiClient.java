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

import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.apache.http.HttpException;
import javax.ws.rs.core.Response.Status;

import eu.operando.ClientOperandoModuleApi;
import eu.operando.interfaces.rapi.model.PrivacyRegulation;
import eu.operando.interfaces.rapi.model.PrivacyRegulationInput;

/**
 * This component of the Regulator API handles all requests for information that
 * the Regulator API sends to other OPERANDO modules.
 * 
 * <p>
 * It converts objects, which represent information that needs to be sent
 * somewhere else, to an appropriate HTTP request message (using JAX-RS). The
 * response message is converted back to an appropriate object (using JAX-RS).
 */
public class RegulatorApiClient extends ClientOperandoModuleApi
{
	private String originPolicyDb = "";
	private String originComputation = "";

	public RegulatorApiClient(String originAuthenticationApi, String originOspEnforcement, String originReportGenerator, String originLogDb, String originPolicyDb,
			String originPolicyComputation)
	{
		super(originAuthenticationApi, originOspEnforcement, originReportGenerator, originLogDb);
		this.originPolicyDb = originPolicyDb;
		this.originComputation = originPolicyComputation;
	}

	/**
	 * PDB
	 */
	public PrivacyRegulation createNewRegulationOnPolicyDb(PrivacyRegulationInput privacyReulationInput) throws HttpException
	{
		// Create a web target for the correct end-point.
		WebTarget target = getClient().target(originPolicyDb);
		target = target.path(ENDPOINT_POLICY_DB_REGULATIONS);

		// Send the request with the regulation encoded as JSON in the body.
		Builder requestBuilder = target.request();
		Response response = requestBuilder.post(createEntityStringJsonFromObject(privacyReulationInput));
		
		// Only try to interpret the response if the status code is a success code.
		if (response.getStatus() != Status.OK.getStatusCode())
		{
			throw new HttpException();
		}
		
		return PrivacyRegulation.readPrivacyRegulationFromHttpResponse(response);
	}

	public PrivacyRegulation updateExistingRegulationOnPolicyDb(PrivacyRegulation regulation)
	{
		// Create a web target for the correct end-point.
		WebTarget target = getClient().target(originPolicyDb);
		String regId = regulation.getRegId();
		String path = ENDPOINT_POLICY_DB_REGULATIONS_VARIABLE_REG_ID.replace("{reg_id}", regId);
		target = target.path(path);

		// Send the request with the regulation encoded as JSON in the body.
		Builder requestBuilder = target.request();
		PrivacyRegulationInput inputObject = regulation.getInputObject();
		Response response = requestBuilder.put(createEntityStringJsonFromObject(inputObject));
		return PrivacyRegulation.readPrivacyRegulationFromHttpResponse(response);
	}

	/**
	 * PC
	 * @return 
	 */
	public boolean sendNewRegulationToPolicyComputation(PrivacyRegulation regulation)
	{
		// Create a web target for the correct end-point.
		WebTarget target = getClient().target(originComputation);
		String path = ENDPOINT_POLICY_COMPUTATION_REGULATIONS;
		target = target.path(path);

		// Send the request with the regulation encoded as JSON in the body.
		Builder requestBuilder = target.request();
		PrivacyRegulationInput inputObject = regulation.getInputObject();
		Response responseFromPc = requestBuilder.post(createEntityStringJsonFromObject(inputObject));
		
		// Return a boolean to indicate whether the request was successful.
		int statusCodeFromPc = responseFromPc.getStatus();
		boolean requestWasSuccessful = statusCodeFromPc == Status.ACCEPTED.getStatusCode();
		return requestWasSuccessful;
	}

	public void sendExistingRegulationToPolicyComputation(PrivacyRegulation regulation)
	{
		// Create a web target for the correct end-point.
		WebTarget target = getClient().target(originComputation);
		String regId = regulation.getRegId();
		String path = ENDPOINT_POLICY_COMPUTATION_REGULATIONS_VARIABLE_REG_ID.replace("{reg_id}", regId);
		target = target.path(path);

		// Send the request with the regulation encoded as JSON in the body.
		Builder requestBuilder = target.request();
		PrivacyRegulationInput inputObject = regulation.getInputObject();
		requestBuilder.put(createEntityStringJsonFromObject(inputObject));
	}

	/**
	 * OSE
	 * @return 
	 */
	public boolean sendNewRegulationToOspEnforcement(PrivacyRegulation regulation)
	{
		// Create a web target for the correct end-point.
		WebTarget target = getClient().target(getOriginOspEnforcement());
		String path = ENDPOINT_OSP_ENFORCEMENT_REGULATIONS;
		target = target.path(path);

		// Send the request with the regulation encoded as JSON in the body.
		Builder requestBuilder = target.request();
		PrivacyRegulationInput inputObject = regulation.getInputObject();
		Response responseFromOse = requestBuilder.post(createEntityStringJsonFromObject(inputObject));
		
		// Return a boolean to indicate whether the request was successful.
		int statusCodeFromOse = responseFromOse.getStatus();
		boolean requestWasSuccessful = statusCodeFromOse == Status.ACCEPTED.getStatusCode();
		return requestWasSuccessful;
	}

	public void sendExistingRegulationToOspEnforcement(PrivacyRegulation regulation)
	{
		// Create a web target for the correct end-point.
		WebTarget target = getClient().target(getOriginOspEnforcement());
		String regId = regulation.getRegId();
		String path = ENDPOINT_OSP_ENFORCEMENT_REGULATIONS_VARIABLE_REG_ID.replace("{reg_id}", regId);
		target = target.path(path);

		// Send the request with the regulation encoded as JSON in the body.
		Builder requestBuilder = target.request();
		PrivacyRegulationInput inputObject = regulation.getInputObject();
		requestBuilder.put(createEntityStringJsonFromObject(inputObject));
	}
}
