package eu.operando.interfaces.oapi.impl;


import eu.operando.OperandoCommunicationException;
import eu.operando.api.model.AnalyticsReport;
import eu.operando.interfaces.oapi.BigDataAnalyticsApiService;
import eu.operando.moduleclients.ClientBigDataAnalytics;

public class BigDataAnalyticsApiServiceImpl extends BigDataAnalyticsApiService
{
	private ClientBigDataAnalytics clientBigDataAnalytics;

	public BigDataAnalyticsApiServiceImpl(ClientBigDataAnalytics clientBigDataAnalytics)
	{
		this.clientBigDataAnalytics = clientBigDataAnalytics;
	}

	@Override
	public AnalyticsReport getBdaReport(String jobId, String userId) throws OperandoCommunicationException {
		// TODO Auto-generated method stub
		return null;
	}
}
