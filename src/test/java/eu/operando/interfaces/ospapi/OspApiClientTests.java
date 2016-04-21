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

import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.HashMap;

import javax.ws.rs.HttpMethod;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.junit.Test;

import eu.operando.ClientOperandoModuleApiTests;

public class OspApiClientTests extends ClientOperandoModuleApiTests
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
		stub(HttpMethod.GET, endpoint);
		
		//Exercise
		client.getPfbDeal(dealId);
		
		//Verify
		verify(HttpMethod.GET, endpoint);
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
		String strJsonDeal = getStringJsonFollowingOperandoConventions(dealExpected);
				
		String endpoint = String.format(ENDPOINT_PRIVACY_FOR_BENEFIT_DEALS_VARIABLE_DEAL_ID, id);
		stub(HttpMethod.GET, endpoint, strJsonDeal);
		
		//Exercise
		PfbDeal dealActual = client.getPfbDeal(id);
		
		//Verify
		boolean isDealActualEqualToDealExpected = EqualsBuilder.reflectionEquals(dealExpected,dealActual);
		assertTrue("The client did not correctly interpret the deal JSON", isDealActualEqualToDealExpected);
	}
	@Test
	public void testCreatePfbDealAcknowledgement_CorrectHttpRequest()
	{
		//Set Up
		int dealId = 1;
		int ospId = 2;
		int offerId = 3;
		int token = 4;
		
		//Exercise
		client.createPfbDealAcknowledgement(dealId, ospId, offerId, token);
		
		//Verify
		HashMap<String, String> queriesParamToValue = new HashMap<String, String>();
		queriesParamToValue.put("osp_id", "" + ospId);
		queriesParamToValue.put("offer_id", "" + offerId);
		queriesParamToValue.put("token", "" + token); //TODO - what should the token be?
		String endpoint = String.format(ENDPOINT_PRIVACY_FOR_BENEFIT_DEALS_VARIABLE_DEAL_ID_ACKNOWLEDGEMENT, dealId);
		verifyWithoutBody(HttpMethod.POST, endpoint, queriesParamToValue);
	}
	@Test
	public void testCreateOffer_CorrectHttpRequest()
	{
		//Set Up		
		PfbOffer offer = new PfbOffer(1, 2, "title", "description", "serviceWebsite", true, "ospCallbackUrl", new Date());
		
		//Exercise
		client.createPfbOffer(offer);
		
		//Verify
		verifyWithoutQueryParams(HttpMethod.POST, ENDPOINT_PRIVACY_FOR_BENEFIT_OFFERS, offer);
	}
	@Test
	public void testGetOffer_CorrectHttpRequest()
	{
		//Set Up
		int offerId = 1;

		//Exercise
		client.getPfbOffer(offerId);

		//Verify
		String endpoint = String.format(ENDPOINT_PRIVACY_FOR_BENEFIT_OFFERS_VARIABLE_OFFER_ID, offerId);
		verify(HttpMethod.GET, endpoint);
	}
	@Test
	public void testUpdateOffer_CorrectHttpRequest()
	{
		//Set Up
		int offerId = 1;
		
		PfbOffer offer = new PfbOffer(offerId, 2, "title", "description", "serviceWebsite", true, "ospCallbackUrl", new Date());
		
		//Exercise
		client.updatePfbOffer(offer);
		
		//Verify
		String endpoint = String.format(ENDPOINT_PRIVACY_FOR_BENEFIT_OFFERS_VARIABLE_OFFER_ID, offerId);
		verifyWithoutQueryParams(HttpMethod.PUT, endpoint, offer);
	}
	@Test
	public void testGetOspDeals_CorrectHttpRequest()
	{
		//Set Up
		int ospId = 1;

				//Exercise
		client.getOspDeals(ospId);

		//Verify
		String endpoint = String.format(ENDPOINT_PRIVACY_FOR_BENEFIT_OSPS_VARIABLE_OSP_ID_DEALS, ospId);
		verify(HttpMethod.GET, endpoint);
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
	
	/**
	 * Authentication API
	 */
	@Test
	public void testAuthoriseOsp_CorrectHttpRequest()
	{
		testAuthoriseOsp_CorrectHttpRequest(client);
	}
	
	/**
	 * Log DB
	 */
	@Test
	public void testLogActivity_CorrectHttpRequest()
	{
		testLogActivity_CorrectHttpRequest(client);
	}
}
