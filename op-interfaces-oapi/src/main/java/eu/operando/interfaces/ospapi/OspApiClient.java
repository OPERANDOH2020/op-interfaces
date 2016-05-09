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

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;

import eu.operando.ClientOperandoModuleApi;

public class OspApiClient extends ClientOperandoModuleApi
{
	private String protocolAndHostBigDataAnalytics = ""; //TODO - implement
	private String protocolAndHostPrivacyForBenefit = "";

	public OspApiClient(String protocolAndHostAuthenticationApi, String protocolAndHostReportGenerator, String protocolAndHostLogDb,
			 String protocolAndHostOspEnforcement, String protocolAndHostBigDataAnalytics, String protocolAndHostPrivacyForBenefit)
	{
		super(protocolAndHostAuthenticationApi, protocolAndHostOspEnforcement, protocolAndHostReportGenerator, protocolAndHostLogDb);
		this.protocolAndHostBigDataAnalytics = protocolAndHostBigDataAnalytics;
		this.protocolAndHostPrivacyForBenefit = protocolAndHostPrivacyForBenefit;
	}
	
	/**
	 * Privacy for Benefit
	 */
	public PfbDeal getPfbDeal(int dealId)
	{
		//Create a web target for the correct url.
		WebTarget target = getClient().target(protocolAndHostPrivacyForBenefit);
		String endpoint = String.format(ENDPOINT_PRIVACY_FOR_BENEFIT_DEALS_VARIABLE_DEAL_ID, dealId);
		target = target.path(endpoint);
		
		//Request the deal.
		Builder requestBuilder = target.request();
		String strJson = requestBuilder.get(String.class);
		
		//Turn the JSON into a deal.
		PfbDeal deal = getObjectFromJsonFollowingOperandoConventions(strJson, PfbDeal.class);
		return deal;
	}
	public void createPfbDealAcknowledgement(int dealId, int ospId, int offerId, int token)
	{
		//Create a web target for the correct url.
		WebTarget target = getClient().target(protocolAndHostPrivacyForBenefit);
		String path = String.format(ENDPOINT_PRIVACY_FOR_BENEFIT_DEALS_VARIABLE_DEAL_ID_ACKNOWLEDGEMENT, dealId);
		target = target.path(path);
		target = target.queryParam("osp_id", ospId);

		Builder requestBuilder = target.request();
		//This is a pretty horrible workaround to post with an empty body. See https://java.net/jira/browse/JERSEY-2370.
		requestBuilder.post(Entity.entity(null, "foo/bar"));
	}
	public void createPfbOffer(PfbOffer offer)
	{
		//Create a web target for the correct url.
		WebTarget target = getClient().target(protocolAndHostPrivacyForBenefit);
		target = target.path(ENDPOINT_PRIVACY_FOR_BENEFIT_OFFERS);

		//Send off a request to the web target with the offer encoded in JSON.
		Builder requestBuilder = target.request();
		requestBuilder.post(createEntityStringJsonFromObject(offer));
	}
	public void getPfbOffer(int offerId)
	{
		//Create a web target for the correct url.
		WebTarget target = getClient().target(protocolAndHostPrivacyForBenefit);
		String path = String.format(ENDPOINT_PRIVACY_FOR_BENEFIT_OFFERS_VARIABLE_OFFER_ID, offerId);
		target = target.path(path);
		
		//Send off a request to get the offer.
		Builder requestBuilder = target.request();
		requestBuilder.get();
	}
	public void updatePfbOffer(PfbOffer offer)
	{
		//Create a web target for the correct url.
		WebTarget target = getClient().target(protocolAndHostPrivacyForBenefit);
		String path = String.format(ENDPOINT_PRIVACY_FOR_BENEFIT_OFFERS_VARIABLE_OFFER_ID, offer.getId());
		target = target.path(path);

		//Send off a request to update the offer.
		Builder requestBuilder = target.request();
		requestBuilder.put(createEntityStringJsonFromObject(offer));
	}
	public void getOspDeals(int ospId)
	{
		//Create a web target for the correct url.
		WebTarget target = getClient().target(protocolAndHostPrivacyForBenefit);
		String path = String.format(ENDPOINT_PRIVACY_FOR_BENEFIT_OSPS_VARIABLE_OSP_ID_DEALS, ospId);
		target = target.path(path);

		//Send off a request to get the deals.
		Builder requestBuilder = target.request();
		requestBuilder.get();
	}
	
	/**
	 * Big Data Analytics
	 */
	public void getBdaReport(int ospId)
	{
		//Create a web target for the correct url.
		WebTarget target = getClient().target(protocolAndHostBigDataAnalytics);
		String path = String.format(ENDPOINT_BIG_DATA_ANALYTICS_REPORTS_VARIABLE_REPORT_ID, ospId);
		target = target.path(path);

		//Send off a request to get the deals.
		Builder requestBuilder = target.request();
		requestBuilder.get();
	}
}
