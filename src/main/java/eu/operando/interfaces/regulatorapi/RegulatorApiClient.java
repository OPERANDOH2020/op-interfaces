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

import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import eu.operando.ClientOperandoModuleApi;

/**
 * This component of the Regulator API handles all requests for information that
 * the Regulator API sends to other OPERANDO modules.
 * 
 * <p>
 * It converts objects, which represent information that needs to be sent
 * somewhere else, to an appropriate HTTP request message (using JAX-RS).
 * The response message is converted back to an appropriate object (using JAX-RS).
 */
public class RegulatorApiClient extends ClientOperandoModuleApi
{
	private String protocolAndHostPolicyDb = "";
	private String protocolAndHostPolicyComputation = "";

	public RegulatorApiClient(String protocolAndHostAuthenticationService, String protocolAndHostOspEnforcement, String protocolAndHostReportGenerator, String protocolAndHostLogDb, 
			 String protocolAndHostPolicyDb, String protocolAndHostPolicyComputation)
	{
		super(protocolAndHostAuthenticationService, protocolAndHostOspEnforcement, protocolAndHostReportGenerator, protocolAndHostLogDb);
		this.protocolAndHostPolicyDb = protocolAndHostPolicyDb;
		this.protocolAndHostPolicyComputation = protocolAndHostPolicyComputation;
	}

	/**
	 * PDB
	 */
	public PrivacyRegulation createNewRegulationOnPolicyDb(PrivacyRegulation regulation)
	{
		//Create a web target for the correct end-point.
		WebTarget target = getClient().target(protocolAndHostPolicyDb);
		target = target.path(ENDPOINT_POLICY_DB_REGULATIONS);

		//Send the request with the regulation encoded as JSON in the body.
		Builder requestBuilder = target.request();
		Response response = requestBuilder.post(createEntityStringJsonFromObject(regulation));
		return readPrivacyRegulationFromResponse(response);
	}
	public PrivacyRegulation updateExistingRegulationOnPolicyDb(PrivacyRegulation regulation)
	{
		//Create a web target for the correct end-point.
		WebTarget target = getClient().target(protocolAndHostPolicyDb);
		String path = String.format(ENDPOINT_POLICY_DB_REGULATIONS_VARIABLE_REG_ID, regulation.getRegId());
		target = target.path(path);

		//Send the request with the regulation encoded as JSON in the body.
		Builder requestBuilder = target.request();
		Response response = requestBuilder.put(createEntityStringJsonFromObject(regulation));
		return readPrivacyRegulationFromResponse(response);
	}

	/**
	 * Helper
	 */
	private PrivacyRegulation readPrivacyRegulationFromResponse(Response response)
	{
		String strJson = response.readEntity(String.class);
		return getStringJsonFollowingOperandoConventions(strJson, PrivacyRegulation.class);
	}
	
	/**
	 * PC
	 */
	public void sendNewRegulationToPolicyComputation(PrivacyRegulation regulation)
	{
		//Create a web target for the correct end-point.
		WebTarget target = getClient().target(protocolAndHostPolicyComputation);
		String path = String.format(ENDPOINT_POLICY_COMPUTATION_REGULATIONS_VARIABLE_REG_ID, regulation.getRegId());
		target = target.path(path);

		//Send the request with the regulation encoded as JSON in the body.
		Builder requestBuilder = target.request();
		requestBuilder.post(createEntityStringJsonFromObject(regulation));
	}
	public void sendExistingRegulationToPolicyComputation(int regulationId, PrivacyRegulation regulation)
	{
		//Create a web target for the correct end-point.
		WebTarget target = getClient().target(protocolAndHostPolicyComputation);
		String path = String.format(ENDPOINT_POLICY_COMPUTATION_REGULATIONS_VARIABLE_REG_ID, regulation.getRegId());
		target = target.path(path);

		//Send the request with the regulation encoded as JSON in the body.
		Builder requestBuilder = target.request();
		requestBuilder.put(createEntityStringJsonFromObject(regulation));
	}
	
	/**
	 * OSE
	 */
	public void sendNewRegulationToOspEnforcement(PrivacyRegulation regulation)
	{
		//Create a web target for the correct end-point.
		WebTarget target = getClient().target(getProtocolAndHostOspEnforcement());
		String path = String.format(ENDPOINT_OSP_ENFORCEMENT_REGULATIONS_VARIABLE_REG_ID, regulation.getRegId());
		target = target.path(path);

		//Send the request with the regulation encoded as JSON in the body.
		Builder requestBuilder = target.request();
		requestBuilder.post(createEntityStringJsonFromObject(regulation));
	}
	public void sendExistingRegulationToOspEnforcement(int regulationId, PrivacyRegulation regulation)
	{
		//Create a web target for the correct end-point.
		WebTarget target = getClient().target(getProtocolAndHostOspEnforcement());
		String path = String.format(ENDPOINT_OSP_ENFORCEMENT_REGULATIONS_VARIABLE_REG_ID, regulation.getRegId());
		target = target.path(path);

		//Send the request with the regulation encoded as JSON in the body.
		Builder requestBuilder = target.request();
		requestBuilder.put(createEntityStringJsonFromObject(regulation));
	}
}
