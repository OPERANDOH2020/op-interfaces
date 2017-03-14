package eu.operando.interfaces.oapi.impl;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.operando.OperandoAuthenticationException;
import eu.operando.OperandoCommunicationException;
import eu.operando.OperandoCommunicationException.CommunicationError;
import eu.operando.UnableToGetDataException;
import eu.operando.api.model.AnalyticsReport;
import eu.operando.interfaces.oapi.BigDataAnalyticsApi;
import eu.operando.interfaces.oapi.BigDataAnalyticsApiService;
import eu.operando.moduleclients.ClientBigDataAnalytics;

public class BigDataAnalyticsApiServiceImpl extends BigDataAnalyticsApiService
{
	final Logger LOGGER;
	
	private ClientBigDataAnalytics clientBigDataAnalytics;

	public BigDataAnalyticsApiServiceImpl(ClientBigDataAnalytics clientBigDataAnalytics)
	{
		this.clientBigDataAnalytics = clientBigDataAnalytics;
		LOGGER = LogManager.getLogger(BigDataAnalyticsApiServiceImpl.class);
	}

	@Override
	public AnalyticsReport getBdaReport(String jobId, String userId) throws UnableToGetDataException {
		try{
			AnalyticsReport report = clientBigDataAnalytics.getBdaReport(jobId, userId);
			return report;
		}
		catch(OperandoCommunicationException ex){
			LOGGER.warn("Unable to get specified Bda Report for Big Data Analytics Api: " + ex.toString());
			if(ex.getCommunitcationError() == CommunicationError.REQUESTED_RESOURCE_NOT_FOUND){
				return null;
			} 
			else {
				throw new UnableToGetDataException(ex);
			}
		}
		catch(OperandoAuthenticationException ex){
			LOGGER.error("Error getting Bda Report for Big Data Analytics Api: " + ex.toString());
			throw new UnableToGetDataException(ex);
		}
	}
}
