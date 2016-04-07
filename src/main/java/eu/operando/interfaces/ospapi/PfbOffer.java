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

	public void setId(int id)
	{
		this.id = id;
	}

	public int getOspId()
	{
		return ospId;
	}

	public void setOspId(int ospId)
	{
		this.ospId = ospId;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getServiceWebsite()
	{
		return serviceWebsite;
	}

	public void setServiceWebsite(String serviceWebsite)
	{
		this.serviceWebsite = serviceWebsite;
	}

	public boolean isEnabled()
	{
		return isEnabled;
	}

	public void setEnabled(boolean isEnabled)
	{
		this.isEnabled = isEnabled;
	}

	public String getOspCallbackUrl()
	{
		return ospCallbackUrl;
	}

	public void setOspCallbackUrl(String ospCallbackUrl)
	{
		this.ospCallbackUrl = ospCallbackUrl;
	}

	public Date getExpirationDate()
	{
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate)
	{
		this.expirationDate = expirationDate;
	}
	
	
}
