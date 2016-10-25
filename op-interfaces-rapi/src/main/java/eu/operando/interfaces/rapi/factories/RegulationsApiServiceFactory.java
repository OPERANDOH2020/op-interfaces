package eu.operando.interfaces.rapi.factories;

import eu.operando.Utils;
import eu.operando.interfaces.rapi.RegulationsApiService;
import eu.operando.interfaces.rapi.impl.RegulationsApiServiceImpl;
import eu.operando.moduleclients.ClientAuthenticationApiOperandoService;
import eu.operando.moduleclients.ClientOspEnforcement;
import eu.operando.moduleclients.ClientPolicyComputation;
import eu.operando.moduleclients.ClientPolicyDb;

@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2016-07-12T14:06:26.001Z")
public class RegulationsApiServiceFactory
{
	// Location of properties file.
	private static final String PROPERTIES_FILE_RAPI = "config.properties";

	// Property file property names.
	private static final String PROPERTY_NAME_ORIGIN_AUTHENTICATION_API = "originAuthenticationApi";
	//private static final String PROPERTY_NAME_SERVICE_ID_REGULATIONS_API = "serviceId";
	private static final String PROPERTY_NAME_ORIGIN_OSP_ENFORCEMENT = "originOspEnforcement";
	private static final String PROPERTY_NAME_ORIGIN_POLICY_DB = "originPolicyDb";
	private static final String PROPERTY_NAME_ORIGIN_POLICY_COMPUTATION = "originPolicyComputation";

	private static RegulationsApiService service;

	public static RegulationsApiService getRegulationsApi()
	{
		if (service == null)
		{
			service = configureService();
		}
		return service;
	}

	private static RegulationsApiService configureService()
	{
		// Property file property values.
		String originAuthenticationApi = Utils.loadPropertyString(PROPERTIES_FILE_RAPI, PROPERTY_NAME_ORIGIN_AUTHENTICATION_API);
		//String serviceIdRegulationsApi = Utils.loadPropertyString(PROPERTIES_FILE_RAPI, PROPERTY_NAME_SERVICE_ID_REGULATIONS_API);
		String originPolicyDb = Utils.loadPropertyString(PROPERTIES_FILE_RAPI, PROPERTY_NAME_ORIGIN_POLICY_DB);
		String originPolicyComputation = Utils.loadPropertyString(PROPERTIES_FILE_RAPI, PROPERTY_NAME_ORIGIN_POLICY_COMPUTATION);
		String originOspEnforcement = Utils.loadPropertyString(PROPERTIES_FILE_RAPI, PROPERTY_NAME_ORIGIN_OSP_ENFORCEMENT);
		
		// Create the clients based on the properties file.
		ClientAuthenticationApiOperandoService clientAuthenticationService = new ClientAuthenticationApiOperandoService(originAuthenticationApi);
		ClientPolicyDb clientPolicyDb = new ClientPolicyDb(originPolicyDb);
		ClientPolicyComputation clientPolicyComputation = new ClientPolicyComputation(originPolicyComputation);
		ClientOspEnforcement clientOspEnforcement = new ClientOspEnforcement(originOspEnforcement);
		
		// Configure the service.
		return new RegulationsApiServiceImpl(clientAuthenticationService, clientPolicyDb, clientPolicyComputation, clientOspEnforcement);
	}
}
