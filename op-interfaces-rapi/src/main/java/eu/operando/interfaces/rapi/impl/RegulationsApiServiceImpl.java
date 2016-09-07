package eu.operando.interfaces.rapi.impl;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.http.HttpException;

import eu.operando.OperandoCommunicationException;
import eu.operando.Utils;
import eu.operando.api.model.PrivacyRegulation;
import eu.operando.api.model.PrivacyRegulationInput;
import eu.operando.interfaces.rapi.RegulationsApiService;
import eu.operando.interfaces.rapi.model.RegulationBody;
import eu.operando.moduleclients.ClientAuthenticationService;
import eu.operando.moduleclients.ClientOspEnforcement;
import eu.operando.moduleclients.ClientPolicyComputation;
import eu.operando.moduleclients.ClientPolicyDb;
import eu.operando.moduleclients.ClientReportGenerator;

@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2016-07-12T14:06:26.001Z")
public class RegulationsApiServiceImpl extends RegulationsApiService
{
	// Location of properties file.
	private static final String PROPERTIES_FILE_RAPI = "config.properties";

	// Properties file property names.
	private static final String PROPERTY_NAME_ORIGIN_AUTHENTICATION_API = "originAuthenticationApi";
	private static final String PROPERTY_NAME_ORIGIN_OSP_ENFORCEMENT = "originOspEnforcement";
	private static final String PROPERTY_NAME_ORIGIN_POLICY_DB = "originPolicyDb";
	private static final String PROPERTY_NAME_ORIGIN_POLICY_COMPUTATION = "originPolicyComputation";

	// Properties file property values.
	private static final String ORIGIN_AUTHENTICATION_API = Utils.loadPropertyString(PROPERTIES_FILE_RAPI, PROPERTY_NAME_ORIGIN_AUTHENTICATION_API);
	private static final String ORIGIN_OSP_ENFORCEMENT = Utils.loadPropertyString(PROPERTIES_FILE_RAPI, PROPERTY_NAME_ORIGIN_OSP_ENFORCEMENT);
	private static final String ORIGIN_POLICY_DB = Utils.loadPropertyString(PROPERTIES_FILE_RAPI, PROPERTY_NAME_ORIGIN_POLICY_DB);
	private static final String ORIGIN_POLICY_COMPUTATION = Utils.loadPropertyString(PROPERTIES_FILE_RAPI, PROPERTY_NAME_ORIGIN_POLICY_COMPUTATION);

	private ClientAuthenticationService clientAuthenticationService = new ClientAuthenticationService(ORIGIN_AUTHENTICATION_API);
	private ClientPolicyDb clientPolicyDb = new ClientPolicyDb(ORIGIN_POLICY_DB);
	private ClientPolicyComputation clientPolicyComputation = new ClientPolicyComputation(ORIGIN_POLICY_COMPUTATION);
	private ClientOspEnforcement clientOspEnforcement = new ClientOspEnforcement(ORIGIN_OSP_ENFORCEMENT);

	@Override
	public Response regulationsPost(RegulationBody regulationBody)
	{
		return checkAuthenticationThenForwardRegulation(regulationBody, null);
	}

	@Override
	public Response regulationsRegIdPut(RegulationBody regulationBody, String regId)
	{
		return checkAuthenticationThenForwardRegulation(regulationBody, regId);
	}

	/**
	 * Verifies that the caller is authenticated, then checks sends the regulation to the relevant modules.
	 * 
	 * @param regulationBody
	 *        The body of the incoming HTTP request.
	 * @param regId
	 *        The ID of the incoming regulation. Should be null if the regulation does not yet have an ID (e.g. if it is new).
	 * @return The HTTP response to be returned to the caller.
	 */
	private Response checkAuthenticationThenForwardRegulation(RegulationBody regulationBody, String regId)
	{
		// The status to be returned in the response.
		Status status = null;

		// Check that the caller is authenticated with the platform.
		String serviceTicket = regulationBody.getServiceTicket();
		boolean ospAuthenticated = clientAuthenticationService.isOspAuthenticated(serviceTicket);

		if (ospAuthenticated)
		{
			// Send the regulation to the PDB.
			PrivacyRegulationInput regulation = regulationBody.getRegulation();
			PrivacyRegulation regulationFromPolicyDb = sendRegulationToPdb(regulation, regId);

			if (regulationFromPolicyDb != null)
			{
				// If the response from the PDB is good, then send the regulation to the PC and OSE, using the regId to determine if the regulation is
				// new.
				boolean successfulRequestToPc = false;
				boolean successfulRequestToOse = false;
				boolean newRegulation = regId == null;
				if (newRegulation)
				{
					successfulRequestToPc = clientPolicyComputation.sendNewRegulationToPolicyComputation(regulationFromPolicyDb);
					successfulRequestToOse = clientOspEnforcement.sendNewRegulationToOspEnforcement(regulationFromPolicyDb);
				}
				else
				{
					successfulRequestToPc = clientPolicyComputation.sendExistingRegulationToPolicyComputation(regulationFromPolicyDb);
					successfulRequestToOse = clientOspEnforcement.sendExistingRegulationToOspEnforcement(regulationFromPolicyDb);
				}

				if (successfulRequestToPc
					&& successfulRequestToOse)
				{
					// Let regulator know that all modules have been informed.
					status = Status.ACCEPTED;
				}
				else
				{
					// Let regulator know that the service is currently unavailable, but may be made available soon.
					status = Status.SERVICE_UNAVAILABLE;
				}
			}
			else
			{
				// Let regulator know that the service is currently unavailable, but may be made available soon.
				status = Status.SERVICE_UNAVAILABLE;
			}
		}
		else
		{
			status = Status.UNAUTHORIZED;
		}

		// Return the response.
		return Response.status(status)
			.build();
	}

	/**
	 * Send the regulation to the PDB and return the privacy regulation in the response body (or null if this is not possible).
	 * 
	 * @param regulationBody
	 *        The regulation to send.
	 * @param newRegulation
	 *        Whether the regulation is new.
	 * @param regId
	 *        the ID of the regulation. Should be null if the regulation doesn't have an ID.
	 * @return the privacy regulation in the response body, if possible.
	 */
	private PrivacyRegulation sendRegulationToPdb(PrivacyRegulationInput regulation, String regId)
	{
		PrivacyRegulation regulationFromPolicyDb = null;

		try
		{
			boolean isRegulationNew = regId == null;
			if (isRegulationNew)
			{
				regulationFromPolicyDb = clientPolicyDb.createNewRegulationOnPolicyDb(regulation);
			}
			else
			{
				regulationFromPolicyDb = clientPolicyDb.updateExistingRegulationOnPolicyDb(regId, regulation);
			}
		}
		catch (OperandoCommunicationException e)
		{
			e.printStackTrace();
		}

		return regulationFromPolicyDb;
	}
}