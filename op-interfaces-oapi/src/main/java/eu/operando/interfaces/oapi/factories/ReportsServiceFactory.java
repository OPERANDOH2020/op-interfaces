package eu.operando.interfaces.oapi.factories;

import eu.operando.Utils;
import eu.operando.interfaces.oapi.Config;
import eu.operando.interfaces.oapi.ReportsService;
import eu.operando.interfaces.oapi.impl.ReportsServiceImpl;
import eu.operando.moduleclients.ClientReportGenerator;

@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2016-07-12T14:06:26.001Z")
public class ReportsServiceFactory
{
	// Properties file property names.
	private static final String PROPERTY_NAME_ORIGIN_REPORT_GENERATOR = "originReportGenerator";

	private static ReportsService service = null;

	public static ReportsService getReportsService()
	{
		if (service == null)
		{
			service = configureService();
		}
		return service;
	}
	
	private static ReportsService configureService()
	{
		String originReportGenerator = Utils.loadPropertyString(Config.PROPERTIES_FILE_OAPI, PROPERTY_NAME_ORIGIN_REPORT_GENERATOR);
		ClientReportGenerator clientReportGenerator = new ClientReportGenerator(originReportGenerator);
		
		return new ReportsServiceImpl(clientReportGenerator);
	}
}
