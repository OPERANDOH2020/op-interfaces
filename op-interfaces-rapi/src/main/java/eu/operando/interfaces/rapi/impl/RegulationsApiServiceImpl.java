package eu.operando.interfaces.rapi.impl;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.operando.OperandoCommunicationException;
import eu.operando.api.model.PrivacyRegulation;
import eu.operando.api.model.PrivacyRegulationInput;
import eu.operando.interfaces.rapi.RegulationsApiService;
import eu.operando.moduleclients.ClientAuthenticationApiOperandoService;
import eu.operando.moduleclients.ClientOspEnforcement;
import eu.operando.moduleclients.ClientPolicyComputation;
import eu.operando.moduleclients.ClientPolicyDb;

@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2016-07-12T14:06:26.001Z")
public class RegulationsApiServiceImpl implements RegulationsApiService
{
	private static final Logger LOGGER = LogManager.getLogger(RegulationsApiServiceImpl.class);
	
	// Service IDs.
	private static final String SERVICE_ID_PROCESS_NEW_REGULATION = "POST/regulator/regulations";
	private static final String SERVICE_ID_PROCESS_EXISTING_REGULATION = "PUT/regulator/regulations/{reg-id}";

	private ClientAuthenticationApiOperandoService clientAuthenticationService = null;
	private ClientPolicyDb clientPolicyDb = null;
	private ClientPolicyComputation clientPolicyComputation = null;
	private ClientOspEnforcement clientOspEnforcement = null;

	public RegulationsApiServiceImpl(ClientAuthenticationApiOperandoService clientAuthenticationService, ClientPolicyDb clientPolicyDb,
			ClientPolicyComputation clientPolicyComputation, ClientOspEnforcement clientOspEnforcement)
	{
		this.clientAuthenticationService = clientAuthenticationService;
		this.clientPolicyDb = clientPolicyDb;
		this.clientPolicyComputation = clientPolicyComputation;
		this.clientOspEnforcement = clientOspEnforcement;
	}

	@Override
	public Response processNewRegulation(String serviceTicket, PrivacyRegulationInput regulation)
	{
		return checkAuthenticationThenForwardRegulation(serviceTicket, regulation, null);
	}

	@Override
	public Response processExistingRegulation(String serviceTicket, PrivacyRegulationInput regulation, String regId)
	{
		return checkAuthenticationThenForwardRegulation(serviceTicket, regulation, regId);
	}

	/**
	 * Verifies that the caller is authenticated, then checks sends the regulation to the relevant modules.
	 * @param serviceTicket 
	 * 
	 * @param regulation
	 *        The body of the incoming HTTP request.
	 * @param regId
	 *        The ID of the incoming regulation. Should be null if the regulation does not yet have an ID (e.g. if it is new).
	 * @return The HTTP response to be returned to the caller.
	 */
	private Response checkAuthenticationThenForwardRegulation(String serviceTicket, PrivacyRegulationInput regulation, String regId)
	{
		// Check that the caller is authenticated with the platform.
		boolean newRegulation = regId == null;
		Status statusToReturn = null;
		try
		{
			boolean ospAuthenticated = checkAuthentication(newRegulation, serviceTicket);

			if (ospAuthenticated)
			{
				boolean success = forwardRegulationToInterestedModules(regulation, regId, newRegulation, ospAuthenticated);
				if (success)
				{
					// Let caller know that the request was processed as expected.
					statusToReturn = Status.ACCEPTED;
				}
				else
				{
					// Let caller know that the service is currently unavailable, but may be made available soon.
					statusToReturn = Status.SERVICE_UNAVAILABLE;
				}
			}
			else
			{
				// Let the caller know that there is a problem with their authentication.
				statusToReturn = Status.UNAUTHORIZED;
			}
		}
		catch (OperandoCommunicationException e)
		{
			// Let caller know that the service is currently unavailable, and requires some work to fix.
			LOGGER.error("There was an error communicating with another module.");
			e.printStackTrace();
			statusToReturn = Status.INTERNAL_SERVER_ERROR;
		}

		// Return the response.
		return Response.status(statusToReturn)
			.build();
	}

	/**
	 * Verify that the caller is authenticated.
	 * 
	 * @param newRegulation
	 *        Whether the regulation is new.
	 * @param serviceTicket
	 *        the service ticket which should give the caller permission to access this service.
	 * @return a boolean indicating whether the caller is authenticated to use the requested service.
	 * @throws OperandoCommunicationException
	 */
	private boolean checkAuthentication(boolean newRegulation, String serviceTicket) throws OperandoCommunicationException
	{
		boolean ospAuthenticated = false;

		if (newRegulation)
		{
			ospAuthenticated = clientAuthenticationService.isOspAuthenticatedForRequestedService(serviceTicket, SERVICE_ID_PROCESS_NEW_REGULATION);
		}
		else
		{
			ospAuthenticated = clientAuthenticationService.isOspAuthenticatedForRequestedService(serviceTicket, SERVICE_ID_PROCESS_EXISTING_REGULATION);
		}

		return ospAuthenticated;
	}

	/**
	 * 
	 * @param regulation
	 * @param regId
	 * @param newRegulation
	 * @param ospAuthenticated
	 * @return
	 */
	private boolean forwardRegulationToInterestedModules(PrivacyRegulationInput regulation, String regId, boolean newRegulation, boolean ospAuthenticated)
	{
		// Keep track of the status of the requests.
		boolean successfulRequestToPdb = false;
		boolean successfulRequestToPc = false;
		boolean successfulRequestToOse = false;

		// Send the regulation to the PDB.
		PrivacyRegulation regulationFromPolicyDb = sendRegulationToPdb(regulation, regId);

		successfulRequestToPdb = regulationFromPolicyDb != null;
		if (successfulRequestToPdb)
		{
			// If the response from the PDB is good, then send the regulation to the PC and OSE, using the regId to determine if the regulation is
			// new.
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
		}

		// Return whether all requests were successful.
		return successfulRequestToPdb && successfulRequestToPc && successfulRequestToOse;
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
			boolean newRegulation = regId == null;
			if (newRegulation)
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