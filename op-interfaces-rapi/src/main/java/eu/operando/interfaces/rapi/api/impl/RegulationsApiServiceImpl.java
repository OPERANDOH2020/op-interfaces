package eu.operando.interfaces.rapi.api.impl;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.apache.http.HttpException;
import javax.ws.rs.core.Response.Status;

import eu.operando.Utils;
import eu.operando.interfaces.rapi.api.ApiResponseMessage;
import eu.operando.interfaces.rapi.api.NotFoundException;
import eu.operando.interfaces.rapi.api.RegulationsApiService;
import eu.operando.interfaces.rapi.model.PrivacyRegulation;
import eu.operando.interfaces.rapi.model.PrivacyRegulationInput;
import eu.operando.interfaces.rapi.model.RegulationBody;

@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2016-07-12T14:06:26.001Z")
public class RegulationsApiServiceImpl extends RegulationsApiService
{
	// Location of properties file.
	private static final String PROPERTIES_FILE_GATEKEEPER = "config.properties";

	// Properties file property names.
	private static final String PROPERTY_NAME_ORIGIN_AUTHENTICATION_API = "originAuthenticationApi";
	private static final String PROPERTY_NAME_ORIGIN_OSP_ENFORCEMENT = "originOspEnforcement";
	private static final String PROPERTY_NAME_ORIGIN_REPORT_GENERATOR = "originReportGenerator";
	private static final String PROPERTY_NAME_ORIGIN_LOG_DB = "originLogDb";
	private static final String PROPERTY_NAME_ORIGIN_POLICY_DB = "originPolicyDb";
	private static final String PROPERTY_NAME_ORIGIN_POLICY_COMPUTATION = "originPolicyComputation";

	// Properties file property values.
	private static final String ORIGIN_AUTHENTICATION_API = Utils.loadPropertyString(PROPERTIES_FILE_GATEKEEPER, PROPERTY_NAME_ORIGIN_AUTHENTICATION_API);
	private static final String ORIGIN_OSP_ENFORCEMENT = Utils.loadPropertyString(PROPERTIES_FILE_GATEKEEPER, PROPERTY_NAME_ORIGIN_OSP_ENFORCEMENT);
	private static final String ORIGIN_REPORT_GENERATOR = Utils.loadPropertyString(PROPERTIES_FILE_GATEKEEPER, PROPERTY_NAME_ORIGIN_REPORT_GENERATOR);
	private static final String ORIGIN_LOG_DB = Utils.loadPropertyString(PROPERTIES_FILE_GATEKEEPER, PROPERTY_NAME_ORIGIN_LOG_DB);
	private static final String ORIGIN_POLICY_DB = Utils.loadPropertyString(PROPERTIES_FILE_GATEKEEPER, PROPERTY_NAME_ORIGIN_POLICY_DB);
	private static final String ORIGIN_POLICY_COMPUTATION = Utils.loadPropertyString(PROPERTIES_FILE_GATEKEEPER, PROPERTY_NAME_ORIGIN_POLICY_COMPUTATION);

	private RegulatorApiClient client = new RegulatorApiClient(ORIGIN_AUTHENTICATION_API, ORIGIN_OSP_ENFORCEMENT, ORIGIN_REPORT_GENERATOR, ORIGIN_LOG_DB,
			ORIGIN_POLICY_DB, ORIGIN_POLICY_COMPUTATION);

	@Override
	public Response regulationsPost(RegulationBody regulationBody)
	{
		// The status code to return to the caller.
		Status statusCode = null;

		// Check that the caller is authenticated with the platform.
		String serviceTicket = regulationBody.getServiceTicket();
		boolean ospAuthenticated = client.isOspAuthenticated(serviceTicket);

		if (ospAuthenticated)
		{

			// Ask the Policy DB to add the regulation.
			PrivacyRegulation regulationFromPolicyDb = null;
			boolean successfulRequestToPdb = true;
			try
			{
				PrivacyRegulationInput regulation = regulationBody.getRegulation();
				regulationFromPolicyDb = client.createNewRegulationOnPolicyDb(regulation);
			}
			catch (HttpException e)
			{
				successfulRequestToPdb = false;
				e.printStackTrace();
			}

			if (successfulRequestToPdb)
			{
				// Inform modules who want to know about new regulations.
				boolean successfulRequestToPc = client.sendNewRegulationToPolicyComputation(regulationFromPolicyDb);
				boolean successfulRequestToOse = client.sendNewRegulationToOspEnforcement(regulationFromPolicyDb);

				if (successfulRequestToPc && successfulRequestToOse)
				{
					// Let regulator know that all modules have been informed.
					statusCode = Status.ACCEPTED;
				}
				else
				{
					// Let regulator know that the service is currently unavailable, but may be made available soon.
					statusCode = Status.SERVICE_UNAVAILABLE;
				}

			}
			else
			{
				// Let regulator know that the service is currently unavailable, but may be made available soon.
				statusCode = Status.SERVICE_UNAVAILABLE; 
			}
		}
		else
		{
			// Let regulator know that they are currently not authorised with the PSP.
			statusCode = Status.UNAUTHORIZED;
		}

		// Return the response.
		return Response.status(statusCode).build();
	}

	@Override
	public Response regulationsRegIdPut(RegulationBody regulationBody, String regId)
	{
		// do some magic!
		return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
	}
}
