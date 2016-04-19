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
package eu.operando.interfaces.ospapi;

import java.util.Date;

/**
 * A wrapper class for a the offer of a deal by OSPs as part of the privacy for benefit function of OPERANDO.
 * 
 * It is used to make the JSON objects that are passed between modules;
 * it is okay that the fields are unused. 
 */
@SuppressWarnings("unused")
public class PfbOffer
{
	private static final Date DATE_START_OF_TIME = new Date(0);
	private int id = -1;
	private int ospId = -1;
	private String title = "";
	private String description = "";
	private String serviceWebsite = "";
	private boolean isEnabled = false;
	private String ospCallbackUrl = "";
	private Date expirationDate = DATE_START_OF_TIME;

	public PfbOffer(){}
	
	public PfbOffer(int id, int ospId, String title, String description, String serviceWebsite, boolean isEnabled,
			String ospCallbackUrl, Date expirationDate)
	{
		this.id = id;
		this.ospId = ospId;
		this.title = title;
		this.description = description;
		this.serviceWebsite = serviceWebsite;
		this.isEnabled = isEnabled;
		this.ospCallbackUrl = ospCallbackUrl;
		this.expirationDate = expirationDate;
	}

	public int getId()
	{
		return id;
	}
}
