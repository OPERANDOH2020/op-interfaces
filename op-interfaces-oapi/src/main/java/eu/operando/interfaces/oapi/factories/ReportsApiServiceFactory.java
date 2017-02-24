package eu.operando.interfaces.oapi.factories;

import eu.operando.Utils;
import eu.operando.interfaces.oapi.ReportsApiService;
import eu.operando.interfaces.oapi.impl.ReportsApiServiceImpl;
import eu.operando.moduleclients.ClientAuthenticationApiOperandoService;
import eu.operando.moduleclients.ClientReportGenerator;

@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2016-07-12T14:06:26.001Z")
public class ReportsApiServiceFactory
{
	// Location of properties file.
	private static final String PROPERTIES_FILE_RAPI = "config.properties";

	// Properties file property names.
	private static final String PROPERTY_NAME_ORIGIN_AUTHENTICATION_API = "originAuthenticationApi";
	private static final String PROPERTY_NAME_ORIGIN_REPORT_GENERATOR = "originReportGenerator";
	private static final String PROPERTY_NAME_SERVICE_ID_GET_REPORT = "serviceIdGetReport";

	private static ReportsApiService service = null;

	public static ReportsApiService getReportsApi()
	{
		if (service == null)
		{
			//TODO - untested
			service = configureService();
		}
		return service;
	}
	
	private static ReportsApiService configureService()
	{
		// Properties file property values.
		String originAuthenticationApi = Utils.loadPropertyString(PROPERTIES_FILE_RAPI, PROPERTY_NAME_ORIGIN_AUTHENTICATION_API);
		String originReportGenerator = Utils.loadPropertyString(PROPERTIES_FILE_RAPI, PROPERTY_NAME_ORIGIN_REPORT_GENERATOR);
		String serviceIdGetReport = Utils.loadPropertyString(PROPERTIES_FILE_RAPI, PROPERTY_NAME_SERVICE_ID_GET_REPORT);

		ClientAuthenticationApiOperandoService clientAuthenticationService = new ClientAuthenticationApiOperandoService(originAuthenticationApi);
		ClientReportGenerator clientReportGenerator = new ClientReportGenerator(originReportGenerator);
		
		return new ReportsApiServiceImpl(clientAuthenticationService, clientReportGenerator);
	}
}
