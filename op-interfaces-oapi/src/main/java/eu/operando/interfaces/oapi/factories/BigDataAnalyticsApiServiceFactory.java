package eu.operando.interfaces.oapi.factories;

import eu.operando.Utils;
import eu.operando.interfaces.oapi.BigDataAnalyticsApiService;
import eu.operando.interfaces.oapi.impl.BigDataAnalyticsApiServiceImpl;
import eu.operando.moduleclients.ClientBigDataAnalytics;

public class BigDataAnalyticsApiServiceFactory
{
	// Location of properties file.
	private static final String PROPERTIES_FILE_OAPI = "config.properties";

	// Property file property names.
	private static final String PROPERTY_NAME_ORIGIN_BIG_DATA_ANALYTICS = "originBigDataAnalytics";
	
	private static BigDataAnalyticsApiService service;

	public static BigDataAnalyticsApiService getBdaApi()
	{
		if (service == null)
		{
			service = configureService();
		}
		return service;
	}

	private static BigDataAnalyticsApiService configureService()
	{
		// String serviceIdRegulationsApi = Utils.loadPropertyString(PROPERTIES_FILE_RAPI, PROPERTY_NAME_SERVICE_ID_REGULATIONS_API);
		String originBda = Utils.loadPropertyString(PROPERTIES_FILE_OAPI, PROPERTY_NAME_ORIGIN_BIG_DATA_ANALYTICS);

		// Create the clients based on the properties file.
		ClientBigDataAnalytics clientBigDataAnalytics = new ClientBigDataAnalytics(originBda);

		// Configure the service.
		return new BigDataAnalyticsApiServiceImpl(clientBigDataAnalytics);
	}
}
