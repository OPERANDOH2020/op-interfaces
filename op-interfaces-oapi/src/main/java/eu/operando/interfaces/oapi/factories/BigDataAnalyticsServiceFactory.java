package eu.operando.interfaces.oapi.factories;

import eu.operando.CredentialsOperando;
import eu.operando.Utils;
import eu.operando.interfaces.oapi.BigDataAnalyticsService;
import eu.operando.interfaces.oapi.impl.BigDataAnalyticsApiServiceImpl;
import eu.operando.moduleclients.ClientAuthenticationApiOperandoClient;
import eu.operando.moduleclients.ClientBigDataAnalytics;
import eu.operando.moduleclients.RequestBuilderAuthenticationApi;
import eu.operando.moduleclients.http.HttpRequestBuilderAuthenticationApi;

public class BigDataAnalyticsServiceFactory
{
	// Location of properties file.
	private static final String PROPERTIES_FILE_OAPI = "config.properties";

	// Property file property names.
	private static final String PROPERTY_NAME_ORIGIN_BIG_DATA_ANALYTICS = "originBigDataAnalytics";
	private static final String PROPERTY_NAME_ORIGIN_AUTHENTICATION_API = "originAuthenticationApi";
	private static final String PROPERTY_NAME_USERNAME_OAPI = "usernameOapi";
	private static final String PROPERTY_NAME_PASSWORD_OAPI = "passwordOapi";
	
	private static BigDataAnalyticsService service;

	public static BigDataAnalyticsService getBdaService()
	{
		if (service == null)
		{
			service = configureService();
		}
		return service;
	}

	private static BigDataAnalyticsService configureService()
	{
		// String serviceIdRegulationsApi = Utils.loadPropertyString(PROPERTIES_FILE_RAPI, PROPERTY_NAME_SERVICE_ID_REGULATIONS_API);
		String originBda = Utils.loadPropertyString(PROPERTIES_FILE_OAPI, PROPERTY_NAME_ORIGIN_BIG_DATA_ANALYTICS);
		String originAuthenticationApi = Utils.loadPropertyString(PROPERTIES_FILE_OAPI, PROPERTY_NAME_ORIGIN_AUTHENTICATION_API);

		// Create the authentication client
		String usernameOapi = Utils.loadPropertyString(PROPERTIES_FILE_OAPI, PROPERTY_NAME_USERNAME_OAPI);
		String passwordOapi = Utils.loadPropertyString(PROPERTIES_FILE_OAPI, PROPERTY_NAME_PASSWORD_OAPI);
		CredentialsOperando credentials = new CredentialsOperando(usernameOapi, passwordOapi);
		RequestBuilderAuthenticationApi requestBuilderAuthenticatoinApi = new HttpRequestBuilderAuthenticationApi(originAuthenticationApi, credentials);
		ClientAuthenticationApiOperandoClient clientAuthenticationServiceOperandoClient = new ClientAuthenticationApiOperandoClient(requestBuilderAuthenticatoinApi);
		
		// Create the clients based on the properties file.
		ClientBigDataAnalytics clientBigDataAnalytics = new ClientBigDataAnalytics(originBda, clientAuthenticationServiceOperandoClient);

		// Configure the service.
		return new BigDataAnalyticsApiServiceImpl(clientBigDataAnalytics);
	}
}
