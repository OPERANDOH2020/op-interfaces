package eu.operando.interfaces.oapi.impl;

import javax.ws.rs.core.Response;

import eu.operando.interfaces.oapi.ApiResponseMessage;
import eu.operando.interfaces.oapi.BigDataAnalyticsApiService;
import eu.operando.interfaces.oapi.model.WrapperBdaRequestBody;
import eu.operando.moduleclients.ClientAuthenticationApiOperandoService;
import eu.operando.moduleclients.ClientBigDataAnalytics;

public class BigDataAnalyticsApiServiceImpl extends BigDataAnalyticsApiService
{
	private ClientAuthenticationApiOperandoService clientAuthenticationService = null;
	private ClientBigDataAnalytics clientBigDataAnalytics = null;

	public BigDataAnalyticsApiServiceImpl(ClientAuthenticationApiOperandoService clientAuthenticationService, ClientBigDataAnalytics clientBigDataAnalytics)
	{
		this.clientAuthenticationService = clientAuthenticationService;
		this.clientBigDataAnalytics = clientBigDataAnalytics;
	}

	@Override
	public Response getBdaReport(String serviceTicket, WrapperBdaRequestBody wrapper, String jobId)
	{
		// do some magic!
		return Response.ok()
			.entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!"))
			.build();
	}
}
