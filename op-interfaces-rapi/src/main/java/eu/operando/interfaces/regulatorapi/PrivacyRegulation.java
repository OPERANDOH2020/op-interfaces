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

import javax.ws.rs.core.Response;

import eu.operando.ClientOperandoModule;

/**
 * Represents the Privacy Regulation object which is passed from the regulator to the
 * OPERANDO platform via the Regulator API.
 * 
 * It is used to make the JSON objects that are used in HTTP messaging;
 * it is okay that the fields are unused. 
 */
@SuppressWarnings("unused")
public class PrivacyRegulation
{
	private long regId = -1; //long since API defines it as int64.
	private String legislationSector = "";
	private String privateInformationSource = "";
	private String privateInformationType = "";
	private String action = "";
	private String requiredConsent = "";

	public PrivacyRegulation(long regId, String legislationSector, String privateInformationSource, String privateInformationType,
			String action, String requiredConsent)
	{
		this.regId = regId;
		this.legislationSector = legislationSector;
		this.privateInformationSource = privateInformationSource;
		this.privateInformationType = privateInformationType;
		this.action = action;
		this.requiredConsent = requiredConsent;
	}
	
	public long getRegId()
	{
		return regId;
	}

	/**
	 * Converts a response which contains a Privacy Regulation encoded in JSON in its body
	 * to a PrivacyRegulation Java object.
	 */
	public static PrivacyRegulation readPrivacyRegulationFromHttpResponse(Response response)
	{
		String strJson = response.readEntity(String.class);
		return ClientOperandoModule.getObjectFromJsonFollowingOperandoConventions(strJson, PrivacyRegulation.class);
	}
}
