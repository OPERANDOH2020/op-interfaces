package eu.operando.interfaces.oapi;

import eu.operando.UnableToGetDataException;
import eu.operando.api.model.AnalyticsReport;

public abstract class BigDataAnalyticsApiService
{
	public abstract AnalyticsReport getBdaReport(String jobId, String userId) throws UnableToGetDataException;
}