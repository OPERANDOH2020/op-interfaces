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

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents the Privacy Regulation object which is passed from the regulator to the
 * OPERANDO platform via the Regulator API.
 */
@XmlRootElement
public class PrivacyRegulation
{
	private long regId = -1; //long since API defines it as int64.
	private String legislationSector = "";
	private String privateInformationSource = "";
	private String privateInformationType = "";
	private String action = "";
	private String requiredConsent = "";
	
	/**
	 * Zero argument constructor for JAXB.
	 */
	public PrivacyRegulation()
	{
		
	}

	public PrivacyRegulation(long regId, String legislationSector, String privateInformationSource, String privateInformationType,
			String action, String requiredConsent)
	{
		this.setRegId(regId);
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
	public void setRegId(long regId)
	{
		this.regId = regId;
	}
	public String getLegislationSector()
	{
		return legislationSector;
	}
	public void setLegislationSector(String legislationSector)
	{
		this.legislationSector = legislationSector;
	}
	public String getPrivateInformationSource()
	{
		return privateInformationSource;
	}
	public void setPrivateInformationSource(String privateInformationSource)
	{
		this.privateInformationSource = privateInformationSource;
	}
	public String getPrivateInformationType()
	{
		return privateInformationType;
	}
	public void setPrivateInformationType(String privateInformationType)
	{
		this.privateInformationType = privateInformationType;
	}
	public String getAction()
	{
		return action;
	}
	public void setAction(String action)
	{
		this.action = action;
	}
	public String getRequiredConsent()
	{
		return requiredConsent;
	}
	public void setRequiredConsent(String requiredConsent)
	{
		this.requiredConsent = requiredConsent;
	}
}
