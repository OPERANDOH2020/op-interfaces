package eu.operando.interfaces.oapi;

import javax.ws.rs.core.Response;

import eu.operando.interfaces.oapi.model.WrapperBdaRequestBody;

public abstract class BigDataAnalyticsApiService
{
	public abstract Response getBdaReport(String serviceTicket, WrapperBdaRequestBody wrapper, String jobId);
}
