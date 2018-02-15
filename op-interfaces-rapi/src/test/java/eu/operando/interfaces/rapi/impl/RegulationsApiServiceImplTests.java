package eu.operando.interfaces.rapi.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import eu.operando.OperandoCommunicationException;
import eu.operando.OperandoCommunicationException.CommunicationError;
import eu.operando.api.model.PrivacyRegulation;
import eu.operando.api.model.PrivacyRegulationInput;
import eu.operando.moduleclients.ClientOspEnforcement;
import eu.operando.moduleclients.ClientPolicyComputation;
import eu.operando.moduleclients.ClientPolicyDb;

@RunWith(MockitoJUnitRunner.class)
public class RegulationsApiServiceImplTests
{
	// Dummy variables to assist testing.
	private static final String REGULATION_ID = "123";

	@Mock
	private ClientPolicyDb clientPolicyDb;
	@Mock
	private ClientPolicyComputation clientPolicyComputation;
	@Mock
	private ClientOspEnforcement clientOspEnforcement;

	@InjectMocks
	private RegulationsApiServiceImpl implementation;

	@Test
	public void testProcessNewRegulation_RegulationSentToPdb() throws OperandoCommunicationException
	{
		// Set up
		setUpResponsesFromOtherModulesForNewRegulation(false, false, false);

		// Exercise
		PrivacyRegulationInput input = new PrivacyRegulationInput();
		implementation.processNewRegulation(input);

		// Verify
		verify(clientPolicyDb).createNewRegulationOnPolicyDb(input);
	}

	@Test
	public void testProcessNewRegulation_PostToPdbUnsuccessful_RegulationNotSentToPcOrOse() throws OperandoCommunicationException
	{
		// Set up
		setUpResponsesFromOtherModulesForNewRegulation(false, false, false);

		// Exercise
		implementation.processNewRegulation(new PrivacyRegulationInput());

		// Verify
		verify(clientPolicyComputation, never()).sendNewRegulationToPolicyComputation((any(PrivacyRegulation.class)));
		verify(clientOspEnforcement, never()).sendNewRegulationToOspEnforcement((any(PrivacyRegulation.class)));
	}

	@Test
	public void testProcessNewRegulation_PostToPdbUnsuccessful_UnavailableResponseReturned() throws OperandoCommunicationException
	{
		// Set up
		setUpResponsesFromOtherModulesForNewRegulation(false, false, false);

		// Exercise
		Response responseToRegulator = implementation.processNewRegulation(new PrivacyRegulationInput());
		int statusCodeResponse = responseToRegulator.getStatus();

		// Verify
		assertEquals("When posting to the PDB is unsuccessful, an unavailable status should be set on the response.", Status.SERVICE_UNAVAILABLE.getStatusCode(),
				statusCodeResponse);
	}

	@Test
	public void testProcessNewRegulation_PostToPdbSuccessful_RegulationSentToPcAndOse() throws OperandoCommunicationException
	{
		// Set up
		PrivacyRegulation privacyRegulationFromPdb = new PrivacyRegulation("", "", "", null, "", null);
		setUpResponsesFromOtherModulesForNewRegulation(true, privacyRegulationFromPdb, false, false);

		// Exercise
		implementation.processNewRegulation(new PrivacyRegulationInput());

		// Verify
		verify(clientPolicyComputation).sendNewRegulationToPolicyComputation(privacyRegulationFromPdb);
		verify(clientOspEnforcement).sendNewRegulationToOspEnforcement(privacyRegulationFromPdb);
	}

	@Test
	public void testProcessNewRegulation_PostToPdbSuccessful_PcAndOseRejectRegulation_UnavailableResponseReturned() throws OperandoCommunicationException
	{
		// Set up
		setUpResponsesFromOtherModulesForNewRegulation(true, false, false);

		// Exercise
		Response response = implementation.processNewRegulation(new PrivacyRegulationInput());

		// Verify
		int status = response.getStatus();
		assertEquals("When neither PC nor OSE returns a success code, the RAPI should return an unavailable status code.", Status.SERVICE_UNAVAILABLE.getStatusCode(),
				status);
	}

	@Test
	public void testProcessNewRegulation_PostToPdbSuccessful_PcRejectsRegulation_OseAcceptsRegulation_UnavailableResponseReturned()
			throws OperandoCommunicationException
	{
		// Set up
		setUpResponsesFromOtherModulesForNewRegulation(true, false, true);

		// Exercise
		Response response = implementation.processNewRegulation( new PrivacyRegulationInput());

		// Verify
		int status = response.getStatus();
		assertEquals("When the PC does not return a success code, the RAPI should return an unavailable status code.", Status.SERVICE_UNAVAILABLE.getStatusCode(),
				status);
	}

	@Test
	public void testProcessNewRegulation_PostToPdbSuccessful_PcAcceptsRegulation_OseRejectsRegulation_UnavailableResponseReturned()
			throws OperandoCommunicationException
	{
		// Set up
		setUpResponsesFromOtherModulesForNewRegulation(true, true, false);

		// Exercise
		Response response = implementation.processNewRegulation(new PrivacyRegulationInput());

		// Verify
		int status = response.getStatus();
		assertEquals("When the OSE does not return a success code, the RAPI should return an unavailable status code.", Status.SERVICE_UNAVAILABLE.getStatusCode(),
				status);
	}

	@Test
	public void testProcessNewRegulation_PostToPdbSuccessful_PcAndOseAcceptRegulation_AcceptResponseReturned() throws OperandoCommunicationException
	{
		// Set up
		setUpResponsesFromOtherModulesForNewRegulation(true, true, true);

		// Exercise
		Response response = implementation.processNewRegulation(new PrivacyRegulationInput());

		// Verify
		int status = response.getStatus();
		assertEquals("When all modules return success codes, the RAPI should return an accepted status code.", Status.ACCEPTED.getStatusCode(), status);
	}

	/**
	 * Can some responses from the mocked client.
	 * @param pdbSuccessful
	 *        whether the client should return a response indicating successful submission to the PDB. If false, calls to
	 *        client.createNewRegulationOnPolicyDb will throw an exception.
	 * @param successFromPc
	 *        whether the client should return a response indicating successful submission to the PC.
	 * @param successFromOse
	 *        whether the client should return a response indicating successful submission to the OSE.
	 */
	private void setUpResponsesFromOtherModulesForNewRegulation(boolean pdbSuccessful, boolean successFromPc, boolean successFromOse)
			throws OperandoCommunicationException
	{
		setUpResponsesFromOtherModulesForNewRegulation(pdbSuccessful, new PrivacyRegulation("", "", "", null, "", null), successFromPc, successFromOse);
	}

	/**
	 * Stub some responses from the mocked client.
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
	private void setUpResponsesFromOtherModulesForNewRegulation(boolean pdbSuccessful, PrivacyRegulation regulationFromPdb, boolean successFromPc,
			boolean successFromOse) throws OperandoCommunicationException
	{
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
	public void testProcessExistingRegulation_RegulationSentToPdb() throws OperandoCommunicationException
	{
		// Set up
		setUpResponsesFromOtherModulesForExistingRegulation(false, false, false);
		String regId = REGULATION_ID;

		// Exercise
		PrivacyRegulationInput input = new PrivacyRegulationInput();
		implementation.processExistingRegulation(input, REGULATION_ID);

		// Verify
		verify(clientPolicyDb).updateExistingRegulationOnPolicyDb(regId, input);
	}

	@Test
	public void testProcessExistingRegulation_PutToPdbUnsuccessful_RegulationNotSentToPcOrOse() throws OperandoCommunicationException
	{
		// Set up
		setUpResponsesFromOtherModulesForExistingRegulation(false, false, false);

		// Exercise
		implementation.processExistingRegulation(new PrivacyRegulationInput(), REGULATION_ID);

		// Verify
		verify(clientPolicyComputation, never()).sendExistingRegulationToPolicyComputation((any(PrivacyRegulation.class)));
		verify(clientOspEnforcement, never()).sendExistingRegulationToOspEnforcement((any(PrivacyRegulation.class)));
	}

	@Test
	public void testProcessExistingRegulation_PutToPdbUnsuccessful_UnavailableResponseReturned() throws OperandoCommunicationException
	{
		// Set up
		setUpResponsesFromOtherModulesForExistingRegulation(false, false, false);

		// Exercise
		Response responseToRegulator = implementation.processExistingRegulation(new PrivacyRegulationInput(), REGULATION_ID);
		int statusCodeResponse = responseToRegulator.getStatus();

		// Verify
		assertEquals("When putting to the PDB is unsuccessful, an unavailable status should be set on the response.", Status.SERVICE_UNAVAILABLE.getStatusCode(),
				statusCodeResponse);
	}

	@Test
	public void testProcessExistingRegulation_PutToPdbSuccessful_RegulationSentToPcAndOse() throws OperandoCommunicationException
	{
		// Set up
		PrivacyRegulation privacyRegulationFromPdb = new PrivacyRegulation("", "", "", null, "", null);
		setUpResponsesFromOtherModulesForExistingRegulation(true, privacyRegulationFromPdb, false, false);

		// Exercise
		implementation.processExistingRegulation(new PrivacyRegulationInput(), REGULATION_ID);

		// Verify
		verify(clientPolicyComputation).sendExistingRegulationToPolicyComputation(privacyRegulationFromPdb);
		verify(clientOspEnforcement).sendExistingRegulationToOspEnforcement(privacyRegulationFromPdb);
	}

	@Test
	public void testProcessExistingRegulation_PutToPdbSuccessful_PcAndOseRejectRegulation_UnavailableResponseReturned() throws OperandoCommunicationException
	{
		// Set up
		setUpResponsesFromOtherModulesForExistingRegulation(true, false, false);

		// Exercise
		Response response = implementation.processExistingRegulation(new PrivacyRegulationInput(), REGULATION_ID);

		// Verify
		int status = response.getStatus();
		assertEquals("When neither PC nor OSE returns a success code, the RAPI should return an unavailable status code.", Status.SERVICE_UNAVAILABLE.getStatusCode(),
				status);
	}

	@Test
	public void testProcessExistingRegulation_PutToPdbSuccessful_PcRejectsRegulation_OseAcceptsRegulation_UnavailableResponseReturned()
			throws OperandoCommunicationException
	{
		// Set up
		setUpResponsesFromOtherModulesForExistingRegulation(true, false, true);

		// Exercise
		Response response = implementation.processExistingRegulation(new PrivacyRegulationInput(), REGULATION_ID);

		// Verify
		int status = response.getStatus();
		assertEquals("When the PC does not return a success code, the RAPI should return an unavailable status code.", Status.SERVICE_UNAVAILABLE.getStatusCode(),
				status);
	}

	@Test
	public void testProcessExistingRegulation_PutToPdbSuccessful_PcAcceptsRegulation_OseRejectsRegulation_UnavailableResponseReturned()
			throws OperandoCommunicationException
	{
		// Set up
		setUpResponsesFromOtherModulesForExistingRegulation(true, true, false);

		// Exercise
		Response response = implementation.processExistingRegulation(new PrivacyRegulationInput(), REGULATION_ID);

		// Verify
		int status = response.getStatus();
		assertEquals("When the OSE does not return a success code, the RAPI should return an unavailable status code.", Status.SERVICE_UNAVAILABLE.getStatusCode(),
				status);
	}

	@Test
	public void testProcessExistingRegulation_PutToPdbSuccessful_PcAndOseAcceptRegulation_AcceptResponseReturned() throws OperandoCommunicationException
	{
		// Set up
		setUpResponsesFromOtherModulesForExistingRegulation(true, true, true);

		// Exercise
		Response response = implementation.processExistingRegulation(new PrivacyRegulationInput(), REGULATION_ID);

		// Verify
		int status = response.getStatus();
		assertEquals("When all modules return success codes, the RAPI should return an accepted status code.", Status.ACCEPTED.getStatusCode(), status);
	}

	/**
	 * Can some responses from the mocked client.
	 * @param pdbSuccessful
	 *        whether the client should return a response indicating successful submission to the PDB. If false, calls to
	 *        client.createExistingRegulationOnPolicyDb will throw an exception.
	 * @param successFromPc
	 *        whether the client should return a response indicating successful submission to the PC.
	 * @param successFromOse
	 *        whether the client should return a response indicating successful submission to the OSE.
	 * 
	 * @throws OperandoCommunicationException
	 */
	private void setUpResponsesFromOtherModulesForExistingRegulation(boolean pdbSuccessful, boolean successFromPc, boolean successFromOse)
			throws OperandoCommunicationException
	{
		setUpResponsesFromOtherModulesForExistingRegulation(pdbSuccessful, new PrivacyRegulation("", "", "", null, "", null), successFromPc, successFromOse);
	}

	/**
	 * Can some responses from the mocked client.
	 * @param pdbSuccessful
	 *        whether the client should return a response indicating successful submission to the PDB. If false, calls to
	 *        client.createExistingRegulationOnPolicyDb will throw an exception.
	 * @param regulationFromPdb
	 *        the regulation to be returned when client.createExistingRegulationOnPolicyDb is called, if pdbSuccessful is true.
	 * @param successFromPc
	 *        whether the client should return a response indicating successful submission to the PC.
	 * @param successFromOse
	 *        whether the client should return a response indicating successful submission to the OSE.
	 * 
	 * @throws OperandoCommunicationException
	 */
	private void setUpResponsesFromOtherModulesForExistingRegulation(boolean pdbSuccessful, PrivacyRegulation regulationFromPdb, boolean successFromPc,
			boolean successFromOse) throws OperandoCommunicationException
	{
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
