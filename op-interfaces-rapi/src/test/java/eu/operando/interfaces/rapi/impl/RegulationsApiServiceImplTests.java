package eu.operando.interfaces.rapi.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
	private RegulationsServiceImpl implementation;

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
	public void testProcessNewRegulation_SendToPdbUnsuccessful_RegulationNotSentToPcOrOse() throws OperandoCommunicationException
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
	public void testProcessNewRegulation_SendToPdbUnsuccessful_ReturnFalse() throws OperandoCommunicationException
	{
		// Set up
		setUpResponsesFromOtherModulesForNewRegulation(false, false, false);

		// Exercise
		boolean success = implementation.processNewRegulation(new PrivacyRegulationInput());

		// Verify
		assertEquals("When sending to the PDB is unsuccessful, the service should return false.", false, success);
	}

	@Test
	public void testProcessNewRegulation_SendToPdbSuccessful_RegulationSentToPcAndOse() throws OperandoCommunicationException
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
	public void testProcessNewRegulation_SendToPdbSuccessful_PcAndOseRejectRegulation_FalseReturned() throws OperandoCommunicationException
	{
		// Set up
		setUpResponsesFromOtherModulesForNewRegulation(true, false, false);

		// Exercise
		boolean success = implementation.processNewRegulation(new PrivacyRegulationInput());

		// Verify
		assertEquals("When both sending to PC and sending to OSE are unsucessful, the service should return false.", false, success);
	}

	@Test
	public void testProcessNewRegulation_SendToPdbSuccessful_PcRejectsRegulation_OseAcceptsRegulation_FalseReturned()
			throws OperandoCommunicationException
	{
		// Set up
		setUpResponsesFromOtherModulesForNewRegulation(true, false, true);

		// Exercise
		boolean success = implementation.processNewRegulation(new PrivacyRegulationInput());

		// Verify
		assertEquals("When sending to PC is unsuccessful, the service should return false.", false, success);
	}

	@Test
	public void testProcessNewRegulation_SendToPdbSuccessful_PcAcceptsRegulation_OseRejectsRegulation_FalseResponseReturned()
			throws OperandoCommunicationException
	{
		// Set up
		setUpResponsesFromOtherModulesForNewRegulation(true, true, false);

		// Exercise
		boolean success = implementation.processNewRegulation(new PrivacyRegulationInput());

		// Verify
		assertEquals("When sending to OSE is unsuccessful, the service should return false.", false, success);
	}

	@Test
	public void testProcessNewRegulation_SendToPdbSuccessful_PcAndOseAcceptRegulation_TrueReturned() throws OperandoCommunicationException
	{
		// Set up
		setUpResponsesFromOtherModulesForNewRegulation(true, true, true);

		// Exercise
		boolean success = implementation.processNewRegulation(new PrivacyRegulationInput());

		// Verify
		assertEquals("When sending to all modules is sucessful, the service should return true.", true, success);
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
	public void testProcessExistingRegulation_SendToPdbUnsuccessful_RegulationNotSentToPcOrOse() throws OperandoCommunicationException
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
	public void testProcessExistingRegulation_SendToPdbUnsuccessful_FalseReturned() throws OperandoCommunicationException
	{
		// Set up
		setUpResponsesFromOtherModulesForExistingRegulation(false, false, false);

		// Exercise
		boolean success = implementation.processExistingRegulation(new PrivacyRegulationInput(), REGULATION_ID);

		// Verify
		assertEquals("When sending to the PDB is unsuccessful, the service should return false.", false, success);
	}

	@Test
	public void testProcessExistingRegulation_SendToPdbSuccessful_RegulationSentToPcAndOse() throws OperandoCommunicationException
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
	public void testProcessExistingRegulation_SendToPdbSuccessful_PcAndOseRejectRegulation_FalseReturned() throws OperandoCommunicationException
	{
		// Set up
		setUpResponsesFromOtherModulesForExistingRegulation(true, false, false);

		// Exercise
		boolean success = implementation.processExistingRegulation(new PrivacyRegulationInput(), REGULATION_ID);

		// Verify
		assertEquals("When both sending to PC and sending to OSE are unsuccessful, the service should return false.", false, success);
	}

	@Test
	public void testProcessExistingRegulation_SendToPdbSuccessful_PcRejectsRegulation_OseAcceptsRegulation_FalseReturned()
			throws OperandoCommunicationException
	{
		// Set up
		setUpResponsesFromOtherModulesForExistingRegulation(true, false, true);

		// Exercise
		boolean success = implementation.processExistingRegulation(new PrivacyRegulationInput(), REGULATION_ID);

		// Verify
		assertEquals("When sending to PC is unsucessful, the service should return false.", false, success);
	}

	@Test
	public void testProcessExistingRegulation_SendToPdbSuccessful_PcAcceptsRegulation_OseRejectsRegulation_FalseReturned()
			throws OperandoCommunicationException
	{
		// Set up
		setUpResponsesFromOtherModulesForExistingRegulation(true, true, false);

		// Exercise
		boolean success = implementation.processExistingRegulation(new PrivacyRegulationInput(), REGULATION_ID);

		// Verify
		assertEquals("When sending to OSE is unsuccessful, the service should return false.", false, success);
	}

	@Test
	public void testProcessExistingRegulation_SendToPdbSuccessful_PcAndOseAcceptRegulation_TrueReturned() throws OperandoCommunicationException
	{
		// Set up
		setUpResponsesFromOtherModulesForExistingRegulation(true, true, true);

		// Exercise
		boolean success = implementation.processExistingRegulation(new PrivacyRegulationInput(), REGULATION_ID);

		// Verify
		assertEquals("When sending to all modules is successful, the service should return true.", true, success);
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
			when(clientPolicyDb.getRegulation(anyString())).thenReturn(regulationFromPdb);
		}
		else
		{
			doThrow(new OperandoCommunicationException(CommunicationError.ERROR_FROM_OTHER_MODULE))
				.when(clientPolicyDb).updateExistingRegulationOnPolicyDb(anyString(), any(PrivacyRegulationInput.class));
		}

		when(clientPolicyComputation.sendExistingRegulationToPolicyComputation(any(PrivacyRegulation.class))).thenReturn(successFromPc);
		when(clientOspEnforcement.sendExistingRegulationToOspEnforcement(any(PrivacyRegulation.class))).thenReturn(successFromOse);
	}
}
