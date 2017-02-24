package eu.operando.interfaces.oapi.impl;


import eu.operando.OperandoCommunicationException;
import eu.operando.OperandoCommunicationException.CommunicationError;
import eu.operando.UnableToGetDataException;
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
	public AnalyticsReport getBdaReport(String jobId, String userId) throws UnableToGetDataException {
		try{
			AnalyticsReport report = clientBigDataAnalytics.getBdaReport(jobId, userId);
			return report;
		}
		catch(OperandoCommunicationException ex){
			if(ex.getCommunitcationError() == CommunicationError.REQUESTED_RESOURCE_NOT_FOUND){
				return null;
			} 
			else {
				throw new UnableToGetDataException(ex);
			}
		}
	}
}
