package eu.operando.interfaces.rapi.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.http.HttpStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import eu.operando.OperandoCommunicationException;
import eu.operando.OperandoCommunicationException.CommunicationError;
import eu.operando.api.model.PrivacyRegulation;
import eu.operando.api.model.PrivacyRegulationInput;
import eu.operando.moduleclients.ClientAuthenticationApiOperandoService;
import eu.operando.moduleclients.ClientOspEnforcement;
import eu.operando.moduleclients.ClientPolicyComputation;
import eu.operando.moduleclients.ClientPolicyDb;

@RunWith(MockitoJUnitRunner.class)
public class RegulationsApiServiceImplTests
{
	// Variables to be tested.
	private static final String SERVICE_ID_PROCESS_NEW_REGULATION = "POST/regulator/regulations";
	private static final String SERVICE_ID_PROCESS_EXISTING_REGULATION = "PUT/regulator/regulations/{reg-id}";

	// Dummy variables to assist testing.
	private static final String REGULATION_ID = "123";

	@Mock
	private ClientAuthenticationApiOperandoService clientAuthenticationService;
	@Mock
	private ClientPolicyDb clientPolicyDb;
	@Mock
	private ClientPolicyComputation clientPolicyComputation;
	@Mock
	private ClientOspEnforcement clientOspEnforcement;

	@InjectMocks
	private RegulationsApiServiceImpl implementation;

	@Test
	public void testProcessNewRegulation_CommunicationException_InternalServerErrorCodeReturned() throws OperandoCommunicationException
	{
		// Set up
		when(clientAuthenticationService.isOspAuthenticatedForRequestedService(anyString(), anyString())).thenThrow(new OperandoCommunicationException(CommunicationError.ERROR_FROM_OTHER_MODULE));
		
		// Exercise
		String serviceTicket = "ST-1234";
		Response responseToRegulator = implementation.processNewRegulation(serviceTicket, new PrivacyRegulationInput());
		
		// Verify
		assertEquals(Response.Status.INTERNAL_SERVER_ERROR, responseToRegulator.getStatusInfo());
		
	}
	
	@Test
	public void testProcessNewRegulation_CorrectArgumentsToVerifyAuthentication() throws OperandoCommunicationException
	{
		// Set up
		setUpResponsesFromOtherModulesForNewRegulation(false, false, false, false);

		// Exercise
		String serviceTicket = "ST-1234";
		implementation.processNewRegulation(serviceTicket, new PrivacyRegulationInput());

		// Verify
		verify(clientAuthenticationService).isOspAuthenticatedForRequestedService(serviceTicket, SERVICE_ID_PROCESS_NEW_REGULATION);
	}

	@Test
	public void testProcessNewRegulation_OspNotAuthenticated_RegulationNotSentToOtherModules() throws OperandoCommunicationException
	{
		// Set up
		setUpResponsesFromOtherModulesForNewRegulation(false, false, false, false);

		// Exercise
		implementation.processNewRegulation("ST-1234", new PrivacyRegulationInput());

		// Verify
		verify(clientPolicyDb, never()).createNewRegulationOnPolicyDb(any(PrivacyRegulationInput.class));
		verify(clientPolicyComputation, never()).sendNewRegulationToPolicyComputation((any(PrivacyRegulation.class)));
		verify(clientOspEnforcement, never()).sendNewRegulationToOspEnforcement((any(PrivacyRegulation.class)));
	}

	@Test
	public void testProcessNewRegulation_OspNotAuthenticated_UnauthorisedCodeReturned() throws OperandoCommunicationException
	{
		// Set up
		setUpResponsesFromOtherModulesForNewRegulation(false, false, false, false);

		// Exercise
		Response responseToRegulator = implementation.processNewRegulation("ST-1234", new PrivacyRegulationInput());

		// Verify
		int statusCodeResponse = responseToRegulator.getStatus();
		assertEquals("If the OSP is not authenticated, the RAPI should return an unauthorised code.", Status.UNAUTHORIZED.getStatusCode(), statusCodeResponse);
	}

	@Test
	public void testProcessNewRegulation_OspAuthenticated_RegulationSentToPdb() throws OperandoCommunicationException
	{
		// Set up
		setUpResponsesFromOtherModulesForNewRegulation(true, false, false, false);

		// Exercise
		PrivacyRegulationInput input = new PrivacyRegulationInput();
		implementation.processNewRegulation("ST-1234", input);

		// Verify
		verify(clientPolicyDb).createNewRegulationOnPolicyDb(input);
	}

	@Test
	public void testProcessNewRegulation_OspAuthenticated_PostToPdbUnsuccessful_RegulationNotSentToPcOrOse() throws OperandoCommunicationException
	{
		// Set up
		setUpResponsesFromOtherModulesForNewRegulation(true, false, false, false);

		// Exercise
		implementation.processNewRegulation("ST-1234", new PrivacyRegulationInput());

		// Verify
		verify(clientPolicyComputation, never()).sendNewRegulationToPolicyComputation((any(PrivacyRegulation.class)));
		verify(clientOspEnforcement, never()).sendNewRegulationToOspEnforcement((any(PrivacyRegulation.class)));
	}

	@Test
	public void testProcessNewRegulation_OspAuthenticated_PostToPdbUnsuccessful_UnavailableResponseReturned() throws OperandoCommunicationException
	{
		// Set up
		setUpResponsesFromOtherModulesForNewRegulation(true, false, false, false);

		// Exercise
		Response responseToRegulator = implementation.processNewRegulation("ST-1234", new PrivacyRegulationInput());
		int statusCodeResponse = responseToRegulator.getStatus();

		// Verify
		assertEquals("When posting to the PDB is unsuccessful, an unavailable status should be set on the response.", Status.SERVICE_UNAVAILABLE.getStatusCode(),
				statusCodeResponse);
	}

	@Test
	public void testProcessNewRegulation_OspAuthenticated_PostToPdbSuccessful_RegulationSentToPcAndOse() throws OperandoCommunicationException
	{
		// Set up
		PrivacyRegulation privacyRegulationFromPdb = new PrivacyRegulation("", "", "", null, "", null);
		setUpResponsesFromOtherModulesForNewRegulation(true, true, privacyRegulationFromPdb, false, false);

		// Exercise
		implementation.processNewRegulation("ST-1234", new PrivacyRegulationInput());

		// Verify
		verify(clientPolicyComputation).sendNewRegulationToPolicyComputation(privacyRegulationFromPdb);
		verify(clientOspEnforcement).sendNewRegulationToOspEnforcement(privacyRegulationFromPdb);
	}

	@Test
	public void testProcessNewRegulation_OspAuthenticated_PostToPdbSuccessful_PcAndOseRejectRegulation_UnavailableResponseReturned() throws OperandoCommunicationException
	{
		// Set up
		setUpResponsesFromOtherModulesForNewRegulation(true, true, false, false);

		// Exercise
		Response response = implementation.processNewRegulation("ST-1234", new PrivacyRegulationInput());

		// Verify
		int status = response.getStatus();
		assertEquals("When neither PC nor OSE returns a success code, the RAPI should return an unavailable status code.", Status.SERVICE_UNAVAILABLE.getStatusCode(),
				status);
	}

	@Test
	public void testProcessNewRegulation_OspAuthenticated_PostToPdbSuccessful_PcRejectsRegulation_OseAcceptsRegulation_UnavailableResponseReturned()
			throws OperandoCommunicationException
	{
		// Set up
		setUpResponsesFromOtherModulesForNewRegulation(true, true, false, true);

		// Exercise
		Response response = implementation.processNewRegulation("ST-1234", new PrivacyRegulationInput());

		// Verify
		int status = response.getStatus();
		assertEquals("When the PC does not return a success code, the RAPI should return an unavailable status code.", Status.SERVICE_UNAVAILABLE.getStatusCode(),
				status);
	}

	@Test
	public void testProcessNewRegulation_OspAuthenticated_PostToPdbSuccessful_PcAcceptsRegulation_OseRejectsRegulation_UnavailableResponseReturned()
			throws OperandoCommunicationException
	{
		// Set up
		setUpResponsesFromOtherModulesForNewRegulation(true, true, true, false);

		// Exercise
		Response response = implementation.processNewRegulation("ST-1234", new PrivacyRegulationInput());

		// Verify
		int status = response.getStatus();
		assertEquals("When the OSE does not return a success code, the RAPI should return an unavailable status code.", Status.SERVICE_UNAVAILABLE.getStatusCode(),
				status);
	}

	@Test
	public void testProcessNewRegulation_OspAuthenticated_PostToPdbSuccessful_PcAndOseAcceptRegulation_AcceptResponseReturned() throws OperandoCommunicationException
	{
		// Set up
		setUpResponsesFromOtherModulesForNewRegulation(true, true, true, true);

		// Exercise
		Response response = implementation.processNewRegulation("ST-1234", new PrivacyRegulationInput());

		// Verify
		int status = response.getStatus();
		assertEquals("When all modules return success codes, the RAPI should return an accepted status code.", Status.ACCEPTED.getStatusCode(), status);
	}

	/**
	 * Can some responses from the mocked client.
	 * 
	 * @param ospAuthenticated
	 *        whether the client should indicate that the OSP is authenticated.
	 * @param pdbSuccessful
	 *        whether the client should return a response indicating successful submission to the PDB. If false, calls to
	 *        client.createNewRegulationOnPolicyDb will throw an exception.
	 * @param successFromPc
	 *        whether the client should return a response indicating successful submission to the PC.
	 * @param successFromOse
	 *        whether the client should return a response indicating successful submission to the OSE.
	 */
	private void setUpResponsesFromOtherModulesForNewRegulation(boolean ospAuthenticated, boolean pdbSuccessful, boolean successFromPc, boolean successFromOse)
			throws OperandoCommunicationException
	{
		setUpResponsesFromOtherModulesForNewRegulation(ospAuthenticated, pdbSuccessful, new PrivacyRegulation("", "", "", null, "", null), successFromPc, successFromOse);
	}

	/**
	 * Stub some responses from the mocked client.
	 * 
	 * @param ospAuthenticated
	 *        whether the client should indicate that the OSP is authenticated.
	 * @param pdbSuccessful
	 *        whether the client should return a response indicating successful submission to the PDB. If false, calls to
	 *        client.createNewRegulationOnPolicyDb will throw an exception.
	 * @param regulationFromPdb
	 *        the regulation to be returned when client.createNewRegulationOnPolicyDb is called, if pdbSuccessful is true.
	 * @param successFromPc
	 *        whether the client should return a response indicating successful submission to the PC.
	 * @param successFromOse
	 *        whether the client should return a response indicating successful submission to the OSE.
	 */
	private void setUpResponsesFromOtherModulesForNewRegulation(boolean ospAuthenticated, boolean pdbSuccessful, PrivacyRegulation regulationFromPdb,
			boolean successFromPc, boolean successFromOse) throws OperandoCommunicationException
	{
		when(clientAuthenticationService.isOspAuthenticatedForRequestedService(anyString(), anyString())).thenReturn(ospAuthenticated);

		if (pdbSuccessful)
		{
			when(clientPolicyDb.createNewRegulationOnPolicyDb(any(PrivacyRegulationInput.class))).thenReturn(regulationFromPdb);
		}
		else
		{
			when(clientPolicyDb.createNewRegulationOnPolicyDb(any(PrivacyRegulationInput.class)))
				.thenThrow(new OperandoCommunicationException(CommunicationError.ERROR_FROM_OTHER_MODULE));
		}

		when(clientPolicyComputation.sendNewRegulationToPolicyComputation(any(PrivacyRegulation.class))).thenReturn(successFromPc);
		when(clientOspEnforcement.sendNewRegulationToOspEnforcement(any(PrivacyRegulation.class))).thenReturn(successFromOse);
	}

	@Test
	public void testProcessExistingRegulation_CommunicationException_InternalServerErrorCodeReturned() throws OperandoCommunicationException
	{
		// Set up
		when(clientAuthenticationService.isOspAuthenticatedForRequestedService(anyString(), anyString())).thenThrow(new OperandoCommunicationException(CommunicationError.ERROR_FROM_OTHER_MODULE));
		
		// Exercise
		String serviceTicket = "ST-1234";
		Response responseToRegulator = implementation.processNewRegulation(serviceTicket, new PrivacyRegulationInput());
		
		// Verify
		assertEquals(Response.Status.INTERNAL_SERVER_ERROR, responseToRegulator.getStatusInfo());
	}
	
	@Test
	public void testProcessExistingRegulation_CorrectArgumentsToClientForAuthenticationVerification() throws OperandoCommunicationException
	{
		// Set up
		setUpResponsesFromOtherModulesForNewRegulation(false, false, false, false);

		// Exercise
		String serviceTicket = "ST-1234";
		implementation.processExistingRegulation(serviceTicket, new PrivacyRegulationInput(), REGULATION_ID);

		// Verify
		verify(clientAuthenticationService).isOspAuthenticatedForRequestedService(serviceTicket, SERVICE_ID_PROCESS_EXISTING_REGULATION);

	}

	@Test
	public void testProcessExistingRegulation_OspNotAuthenticated_RegulationNotSentToOtherModules() throws OperandoCommunicationException
	{
		// Set up
		setUpResponsesFromOtherModulesForExistingRegulation(false, false, false, false);

		// Exercise
		implementation.processExistingRegulation("ST-1234", new PrivacyRegulationInput(), REGULATION_ID);

		// Verify
		verify(clientPolicyDb, never()).updateExistingRegulationOnPolicyDb(anyString(), any(PrivacyRegulationInput.class));
		verify(clientPolicyComputation, never()).sendExistingRegulationToPolicyComputation((any(PrivacyRegulation.class)));
		verify(clientOspEnforcement, never()).sendExistingRegulationToOspEnforcement((any(PrivacyRegulation.class)));
	}

	@Test
	public void testProcessExistingRegulation_OspNotAuthenticated_UnauthorisedCodeReturned() throws OperandoCommunicationException
	{
		// Set up
		setUpResponsesFromOtherModulesForExistingRegulation(false, false, false, false);

		// Exercise
		Response responseToRegulator = implementation.processExistingRegulation("ST-1234", new PrivacyRegulationInput(), REGULATION_ID);

		// Verify
		int statusCodeResponse = responseToRegulator.getStatus();
		assertEquals("If the OSP is not authenticated, the RAPI should return an unauthorised code.", Status.UNAUTHORIZED.getStatusCode(), statusCodeResponse);
	}

	@Test
	public void testProcessExistingRegulation_OspAuthenticated_RegulationSentToPdb() throws OperandoCommunicationException
	{
		// Set up
		setUpResponsesFromOtherModulesForExistingRegulation(true, false, false, false);
		String regId = REGULATION_ID;

		// Exercise
		PrivacyRegulationInput input = new PrivacyRegulationInput();
		implementation.processExistingRegulation("ST-1234", input, REGULATION_ID);

		// Verify
		verify(clientPolicyDb).updateExistingRegulationOnPolicyDb(regId, input);
	}

	@Test
	public void testProcessExistingRegulation_OspAuthenticated_PutToPdbUnsuccessful_RegulationNotSentToPcOrOse() throws OperandoCommunicationException
	{
		// Set up
		setUpResponsesFromOtherModulesForExistingRegulation(true, false, false, false);

		// Exercise
		implementation.processExistingRegulation("ST-1234", new PrivacyRegulationInput(), REGULATION_ID);

		// Verify
		verify(clientPolicyComputation, never()).sendExistingRegulationToPolicyComputation((any(PrivacyRegulation.class)));
		verify(clientOspEnforcement, never()).sendExistingRegulationToOspEnforcement((any(PrivacyRegulation.class)));
	}

	@Test
	public void testProcessExistingRegulation_OspAuthenticated_PutToPdbUnsuccessful_UnavailableResponseReturned() throws OperandoCommunicationException
	{
		// Set up
		setUpResponsesFromOtherModulesForExistingRegulation(true, false, false, false);

		// Exercise
		Response responseToRegulator = implementation.processExistingRegulation("ST-1234", new PrivacyRegulationInput(), REGULATION_ID);
		int statusCodeResponse = responseToRegulator.getStatus();

		// Verify
		assertEquals("When putting to the PDB is unsuccessful, an unavailable status should be set on the response.", Status.SERVICE_UNAVAILABLE.getStatusCode(),
				statusCodeResponse);
	}

	@Test
	public void testProcessExistingRegulation_OspAuthenticated_PutToPdbSuccessful_RegulationSentToPcAndOse() throws OperandoCommunicationException
	{
		// Set up
		PrivacyRegulation privacyRegulationFromPdb = new PrivacyRegulation("", "", "", null, "", null);
		setUpResponsesFromOtherModulesForExistingRegulation(true, true, privacyRegulationFromPdb, false, false);

		// Exercise
		implementation.processExistingRegulation("ST-1234", new PrivacyRegulationInput(), REGULATION_ID);

		// Verify
		verify(clientPolicyComputation).sendExistingRegulationToPolicyComputation(privacyRegulationFromPdb);
		verify(clientOspEnforcement).sendExistingRegulationToOspEnforcement(privacyRegulationFromPdb);
	}

	@Test
	public void testProcessExistingRegulation_OspAuthenticated_PutToPdbSuccessful_PcAndOseRejectRegulation_UnavailableResponseReturned() throws OperandoCommunicationException
	{
		// Set up
		setUpResponsesFromOtherModulesForExistingRegulation(true, true, false, false);

		// Exercise
		Response response = implementation.processExistingRegulation("ST-1234", new PrivacyRegulationInput(), REGULATION_ID);

		// Verify
		int status = response.getStatus();
		assertEquals("When neither PC nor OSE returns a success code, the RAPI should return an unavailable status code.", Status.SERVICE_UNAVAILABLE.getStatusCode(),
				status);
	}

	@Test
	public void testProcessExistingRegulation_OspAuthenticated_PutToPdbSuccessful_PcRejectsRegulation_OseAcceptsRegulation_UnavailableResponseReturned()
			throws OperandoCommunicationException
	{
		// Set up
		setUpResponsesFromOtherModulesForExistingRegulation(true, true, false, true);

		// Exercise
		Response response = implementation.processExistingRegulation("ST-1234", new PrivacyRegulationInput(), REGULATION_ID);

		// Verify
		int status = response.getStatus();
		assertEquals("When the PC does not return a success code, the RAPI should return an unavailable status code.", Status.SERVICE_UNAVAILABLE.getStatusCode(),
				status);
	}

	@Test
	public void testProcessExistingRegulation_OspAuthenticated_PutToPdbSuccessful_PcAcceptsRegulation_OseRejectsRegulation_UnavailableResponseReturned()
			throws OperandoCommunicationException
	{
		// Set up
		setUpResponsesFromOtherModulesForExistingRegulation(true, true, true, false);

		// Exercise
		Response response = implementation.processExistingRegulation("ST-1234", new PrivacyRegulationInput(), REGULATION_ID);

		// Verify
		int status = response.getStatus();
		assertEquals("When the OSE does not return a success code, the RAPI should return an unavailable status code.", Status.SERVICE_UNAVAILABLE.getStatusCode(),
				status);
	}

	@Test
	public void testProcessExistingRegulation_OspAuthenticated_PutToPdbSuccessful_PcAndOseAcceptRegulation_AcceptResponseReturned() throws OperandoCommunicationException
	{
		// Set up
		setUpResponsesFromOtherModulesForExistingRegulation(true, true, true, true);

		// Exercise
		Response response = implementation.processExistingRegulation("ST-1234", new PrivacyRegulationInput(), REGULATION_ID);

		// Verify
		int status = response.getStatus();
		assertEquals("When all modules return success codes, the RAPI should return an accepted status code.", Status.ACCEPTED.getStatusCode(), status);
	}

	/**
	 * Can some responses from the mocked client.
	 * 
	 * @param ospAuthenticated
	 *        whether the client should indicate that the OSP is authenticated.
	 * @param pdbSuccessful
	 *        whether the client should return a response indicating successful submission to the PDB. If false, calls to
	 *        client.createExistingRegulationOnPolicyDb will throw an exception.
	 * @param successFromPc
	 *        whether the client should return a response indicating successful submission to the PC.
	 * @param successFromOse
	 *        whether the client should return a response indicating successful submission to the OSE.
	 * @throws OperandoCommunicationException
	 */
	private void setUpResponsesFromOtherModulesForExistingRegulation(boolean ospAuthenticated, boolean pdbSuccessful, boolean successFromPc, boolean successFromOse)
			throws OperandoCommunicationException
	{
		setUpResponsesFromOtherModulesForExistingRegulation(ospAuthenticated, pdbSuccessful, new PrivacyRegulation("", "", "", null, "", null), successFromPc,
				successFromOse);
	}

	/**
	 * Can some responses from the mocked client.
	 * 
	 * @param ospAuthenticated
	 *        whether the client should indicate that the OSP is authenticated.
	 * @param pdbSuccessful
	 *        whether the client should return a response indicating successful submission to the PDB. If false, calls to
	 *        client.createExistingRegulationOnPolicyDb will throw an exception.
	 * @param regulationFromPdb
	 *        the regulation to be returned when client.createExistingRegulationOnPolicyDb is called, if pdbSuccessful is true.
	 * @param successFromPc
	 *        whether the client should return a response indicating successful submission to the PC.
	 * @param successFromOse
	 *        whether the client should return a response indicating successful submission to the OSE.
	 * @throws OperandoCommunicationException
	 */
	private void setUpResponsesFromOtherModulesForExistingRegulation(boolean ospAuthenticated, boolean pdbSuccessful, PrivacyRegulation regulationFromPdb,
			boolean successFromPc, boolean successFromOse) throws OperandoCommunicationException
	{
		when(clientAuthenticationService.isOspAuthenticatedForRequestedService(anyString(), anyString())).thenReturn(ospAuthenticated);

		if (pdbSuccessful)
		{
			when(clientPolicyDb.updateExistingRegulationOnPolicyDb(anyString(), any(PrivacyRegulationInput.class))).thenReturn(regulationFromPdb);
		}
		else
		{
			when(clientPolicyDb.updateExistingRegulationOnPolicyDb(anyString(), any(PrivacyRegulationInput.class)))
				.thenThrow(new OperandoCommunicationException(CommunicationError.ERROR_FROM_OTHER_MODULE));
		}

		when(clientPolicyComputation.sendExistingRegulationToPolicyComputation(any(PrivacyRegulation.class))).thenReturn(successFromPc);
		when(clientOspEnforcement.sendExistingRegulationToOspEnforcement(any(PrivacyRegulation.class))).thenReturn(successFromOse);
	}
}
