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

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.putRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.junit.Test;

import eu.operando.OperandoApiModuleClientTests;

public class OspApiClientTests extends OperandoApiModuleClientTests
{
	private OspApiClient client = new OspApiClient(PROTOCOL_AND_HOST, PROTOCOL_AND_HOST,
			PROTOCOL_AND_HOST, PROTOCOL_AND_HOST, PROTOCOL_AND_HOST, PROTOCOL_AND_HOST);
	
	/**
	 * PfB
	 */
	@Test
	public void testGetPfbDeal_CorrectHttpRequest()
	{
		//Set Up
		int dealId = 1;
		String endpoint = String.format(ENDPOINT_PRIVACY_FOR_BENEFIT_DEALS_VARIABLE_DEAL_ID, dealId);
		getWireMockRule().stubFor(get(urlPathEqualTo(endpoint))
				.willReturn(aResponse()));
		
		//Exercise
		client.getPfbDeal(dealId);
		
		//Verify
		getWireMockRule().verify(getRequestedFor(urlPathEqualTo(endpoint)));
	}
	@Test
	public void testGetPfbDeal_ResponseHandledCorrectly()
	{
		//TODO - maybe there does not need to be complicated response handling - the JSON from the response body could just be passed back,
		//could just check that the returned JSON string is correct
		//Set Up
		int id = 1;
		int userId = 2;
		int offerId = 3;
		Date createdAt =  new Date(0);
		Date canceledAt = new Date(0);
		
		PfbDeal dealExpected = new PfbDeal(id, userId, offerId, createdAt, canceledAt);
		String strJson = getStringJsonFollowingOperandoConventions(dealExpected);
				
		String endpoint = String.format(ENDPOINT_PRIVACY_FOR_BENEFIT_DEALS_VARIABLE_DEAL_ID, id);
		getWireMockRule().stubFor(get(urlPathEqualTo(endpoint))
				.willReturn(aResponse()
						.withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
						.withBody(strJson)));
		
		//Exercise
		PfbDeal dealActual = client.getPfbDeal(id);
		
		//Verify
		assertTrue("The client did not correctly interpret the deal JSON", EqualsBuilder.reflectionEquals(dealExpected,dealActual));
	}
	@Test
	public void testCreatePfbDealAcknowledgement_CorrectHttpRequest()
	{
		//Set Up
		int dealId = 1;
		int ospId = 2;
		int offerId = 3;
		int token = 4;
		String endpoint = String.format(ENDPOINT_PRIVACY_FOR_BENEFIT_DEALS_VARIABLE_DEAL_ID_ACKNOWLEDGEMENT, dealId);
		getWireMockRule().stubFor(post(urlPathEqualTo(endpoint))
				.willReturn(aResponse()));
		
		//Exercise
		client.createPfbDealAcknowldgement(dealId, ospId, offerId, token);
		
		//Verify
		getWireMockRule().verify(postRequestedFor(urlPathEqualTo(endpoint))
				.withQueryParam("osp_id", equalTo("" + ospId))
				.withQueryParam("offer_id", equalTo("" + offerId))
				.withQueryParam("token", equalTo("" + token))); //TODO - what should the token be?
	}
	@Test
	public void testCreateOffer_CorrectHttpRequest()
	{
		//Set Up
		getWireMockRule().stubFor(post(urlPathEqualTo(ENDPOINT_PRIVACY_FOR_BENEFIT_OFFERS))
				.willReturn(aResponse()));
		
		PfbOffer offer = new PfbOffer(1, 2, "title", "description", "serviceWebsite", true, "ospCallbackUrl", new Date());
		
		//Exercise
		client.createPfbOffer(offer);
		
		//Verify
		String strJson = getStringJsonFollowingOperandoConventions(offer);
		getWireMockRule().verify(postRequestedFor(urlPathEqualTo(ENDPOINT_PRIVACY_FOR_BENEFIT_OFFERS))
				.withRequestBody(equalToJson(strJson)));
	}
	@Test
	public void testGetOffer_CorrectHttpRequest()
	{
		//Set Up
		int offerId = 1;
		String endpoint = String.format(ENDPOINT_PRIVACY_FOR_BENEFIT_OFFERS_VARIABLE_OFFER_ID, offerId);
		getWireMockRule().stubFor(get(urlPathEqualTo(endpoint))
				.willReturn(aResponse()));

		//Exercise
		client.getPfbOffer(offerId);

		//Verify
		getWireMockRule().verify(getRequestedFor(urlPathEqualTo(endpoint)));
	}
	@Test
	public void testUpdateOffer_CorrectHttpRequest()
	{
		//Set Up
		int offerId = 1;
		String endpoint = String.format(ENDPOINT_PRIVACY_FOR_BENEFIT_OFFERS_VARIABLE_OFFER_ID, offerId);
		getWireMockRule().stubFor(put(urlPathEqualTo(endpoint))
				.willReturn(aResponse()));
		
		PfbOffer offer = new PfbOffer(offerId, 2, "title", "description", "serviceWebsite", true, "ospCallbackUrl", new Date());
		
		//Exercise
		client.updatePfbOffer(offer);
		
		//Verify
		String strJson = getStringJsonFollowingOperandoConventions(offer);
		getWireMockRule().verify(putRequestedFor(urlPathEqualTo(endpoint))
				.withRequestBody(equalToJson(strJson)));
	}
	@Test
	public void testGetOspDeals_CorrectHttpRequest()
	{
		//Set Up
		int ospId = 1;
		String endpoint = String.format(ENDPOINT_PRIVACY_FOR_BENEFIT_OSPS_VARIABLE_OSP_ID_DEALS, ospId);
		getWireMockRule().stubFor(put(urlPathEqualTo(endpoint))
				.willReturn(aResponse()));

				//Exercise
		client.getOspDeals(ospId);

		//Verify
		getWireMockRule().verify(getRequestedFor(urlPathEqualTo(endpoint)));
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
}
