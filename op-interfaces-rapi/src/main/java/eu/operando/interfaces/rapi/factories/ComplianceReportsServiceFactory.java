package eu.operando.interfaces.rapi.factories;

import eu.operando.Utils;
import eu.operando.interfaces.rapi.ComplianceReportsService;
import eu.operando.interfaces.rapi.impl.ComplianceReportsServiceImpl;
import eu.operando.moduleclients.ClientPolicyDb;

public class ComplianceReportsServiceFactory {

	// Location of properties file.
	private static final String PROPERTIES_FILE_RAPI = "config.properties";
	
	// Property file property names.
	private static final String PROPERTY_NAME_ORIGIN_POLICY_DB = "originPolicyDb";
	
	private static ComplianceReportsService service;
	
	public static ComplianceReportsService getComplienceReportApiService(){
		if (service == null)
		{
			service = configureService();
		}
		return service;
	}
	
	private static ComplianceReportsService configureService(){
		// Property file property values.
		String originPolicyDb = Utils.loadPropertyString(PROPERTIES_FILE_RAPI, PROPERTY_NAME_ORIGIN_POLICY_DB);
		
		// Create the clients based on the properties file.
		ClientPolicyDb clientPolicyDb = new ClientPolicyDb(originPolicyDb);
		
		// Configure the service.
		return new ComplianceReportsServiceImpl(clientPolicyDb);
	}
}
