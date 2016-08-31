package eu.operando.interfaces.rapi.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.ws.rs.core.Response;

import org.apache.http.HttpException;
import javax.ws.rs.core.Response.Status;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import eu.operando.interfaces.rapi.impl.RegulationsApiServiceImpl;
import eu.operando.interfaces.rapi.impl.RegulatorApiClient;
import eu.operando.interfaces.rapi.model.PrivacyRegulation;
import eu.operando.interfaces.rapi.model.PrivacyRegulationInput;
import eu.operando.interfaces.rapi.model.RegulationBody;

@RunWith(MockitoJUnitRunner.class)
// TODO - test that correct service ticket is sent to the AAPI
// TODO - combine "ForNew" and "ForExisting" setup methods.
public class RegulationsApiServiceImplTests
{
	@Mock
	private RegulatorApiClient client;

	@InjectMocks
	private RegulationsApiServiceImpl implementation;

	@Test
	public void testRegulationsPost_OspNotAuthenticated_RegulationNotSentToOtherModules() throws HttpException
	{
		// Set up
		setUpResponsesFromOtherModulesForNewRegulation(false, false, false, false);

		// Exercise
		implementation.regulationsPost(new RegulationBody());

		// Verify
		verify(client, never()).createNewRegulationOnPolicyDb(any(PrivacyRegulationInput.class));
		verify(client, never()).sendNewRegulationToPolicyComputation((any(PrivacyRegulation.class)));
		verify(client, never()).sendNewRegulationToOspEnforcement((any(PrivacyRegulation.class)));
	}
	
	@Test
	public void testRegulationsPost_OspNotAuthenticated_UnauthorisedCodeReturned() throws HttpException
	{
		// Set up
		setUpResponsesFromOtherModulesForNewRegulation(false, false, false, false);

		// Exercise
		Response responseToRegulator = implementation.regulationsPost(new RegulationBody());

		// Verify
		int statusCodeResponse = responseToRegulator.getStatus();
		assertEquals("If the OSP is not authenticated, the RAPI should return an unauthorised code.", Status.UNAUTHORIZED.getStatusCode(), statusCodeResponse);
	}

	@Test
	public void testRegulationsPost_OspAuthenticated_RegulationSentToPdb() throws HttpException
	{
		// Set up
		setUpResponsesFromOtherModulesForNewRegulation(true, false, false, false);
		RegulationBody regulationBody = new RegulationBody();

		// Exercise
		implementation.regulationsPost(regulationBody);

		// Verify
		PrivacyRegulationInput input = regulationBody.getRegulation();
		verify(client).createNewRegulationOnPolicyDb(input);
	}

	@Test
	public void testRegulationsPost_OspAuthenticated_PostToPdbUnsuccessful_RegulationNotSentToPcOrOse() throws HttpException
	{
		// Set up
		setUpResponsesFromOtherModulesForNewRegulation(true, false, false, false);

		// Exercise
		implementation.regulationsPost(new RegulationBody());

		// Verify
		verify(client, never()).sendNewRegulationToPolicyComputation((any(PrivacyRegulation.class)));
		verify(client, never()).sendNewRegulationToOspEnforcement((any(PrivacyRegulation.class)));
	}
	
	@Test
	public void testRegulationsPost_OspAuthenticated_PostToPdbUnsuccessful_UnavailableResponseReturned() throws HttpException
	{
		// Set up
		setUpResponsesFromOtherModulesForNewRegulation(true, false, false, false);

		// Exercise
		Response responseToRegulator = implementation.regulationsPost(new RegulationBody());
		int statusCodeResponse = responseToRegulator.getStatus();

		// Verify
		assertEquals("When posting to the PDB is unsuccessful, an unavailable status should be set on the response.", Status.SERVICE_UNAVAILABLE.getStatusCode(), statusCodeResponse);
	}

	@Test
	public void testRegulationsPost_OspAuthenticated_PostToPdbSuccessful_RegulationSentToPcAndOse() throws HttpException
	{
		// Set up
		PrivacyRegulation privacyRegulationFromPdb = new PrivacyRegulation("", "", "", null, "", null);
		setUpResponsesFromOtherModulesForNewRegulation(true, true, privacyRegulationFromPdb, false, false);

		// Exercise
		implementation.regulationsPost(new RegulationBody());

		// Verify
		verify(client).sendNewRegulationToPolicyComputation(privacyRegulationFromPdb);
		verify(client).sendNewRegulationToOspEnforcement(privacyRegulationFromPdb);
	}
	
	@Test
	public void testRegulationsPost_OspAuthenticated_PostToPdbSuccessful_PcAndOseRejectRegulation_UnavailableResponseReturned() throws HttpException
	{
		// Set up
		setUpResponsesFromOtherModulesForNewRegulation(true, true, false, false);

		// Exercise
		Response response = implementation.regulationsPost(new RegulationBody());

		// Verify
		int status = response.getStatus();
		assertEquals("When neither PC nor OSE returns a success code, the RAPI should return an unavailable status code.", Status.SERVICE_UNAVAILABLE.getStatusCode(), status);
	}
	
	@Test
	public void testRegulationsPost_OspAuthenticated_PostToPdbSuccessful_PcRejectsRegulation_OseAcceptsRegulation_UnavailableResponseReturned() throws HttpException
	{
		// Set up
		setUpResponsesFromOtherModulesForNewRegulation(true, true, false, true);

		// Exercise
		Response response = implementation.regulationsPost(new RegulationBody());

		// Verify
		int status = response.getStatus();
		assertEquals("When the PC does not return a success code, the RAPI should return an unavailable status code.", Status.SERVICE_UNAVAILABLE.getStatusCode(), status);
	}

	@Test
	public void testRegulationsPost_OspAuthenticated_PostToPdbSuccessful_PcAcceptsRegulation_OseRejectsRegulation_UnavailableResponseReturned() throws HttpException
	{
		// Set up
		setUpResponsesFromOtherModulesForNewRegulation(true, true, true, false);

		// Exercise
		Response response = implementation.regulationsPost(new RegulationBody());

		// Verify
		int status = response.getStatus();
		assertEquals("When the OSE does not return a success code, the RAPI should return an unavailable status code.", Status.SERVICE_UNAVAILABLE.getStatusCode(), status);
	}
	
	@Test
	public void testRegulationsPost_OspAuthenticated_PostToPdbSuccessful_PcAndOseAcceptRegulation_AcceptResponseReturned() throws HttpException
	{
		// Set up
		setUpResponsesFromOtherModulesForNewRegulation(true, true, true, true);
		
		// Exercise
		Response response = implementation.regulationsPost(new RegulationBody());

		// Verify
		int status = response.getStatus();
		assertEquals("When all modules return success codes, the RAPI should return an accepted status code.", Status.ACCEPTED.getStatusCode(), status);
	}
	
	/**
	 * Can some responses from the mocked client.
	 * @param ospAuthenticated
	 * 	whether the client should indicate that the OSP is authenticated.
	 * @param pdbSuccessful
	 * 	whether the client should return a response indicating successful submission to the PDB. If false, calls to client.createNewRegulationOnPolicyDb will throw an exception.
	 * @param successFromPc
	 * 	whether the client should return a response indicating successful submission to the PC.
	 * @param successFromOse
	 * 	whether the client should return a response indicating successful submission to the OSE.
	 */
	private void setUpResponsesFromOtherModulesForNewRegulation(boolean ospAuthenticated, boolean pdbSuccessful, boolean successFromPc, boolean successFromOse) throws HttpException
	{
		setUpResponsesFromOtherModulesForNewRegulation(ospAuthenticated, pdbSuccessful, new PrivacyRegulation("", "", "", null, "", null), successFromPc, successFromOse);
	}
	
	/**
	 * Stub some responses from the mocked client.
	 * @param ospAuthenticated
	 * 	whether the client should indicate that the OSP is authenticated.
	 * @param pdbSuccessful
	 * 	whether the client should return a response indicating successful submission to the PDB. If false, calls to client.createNewRegulationOnPolicyDb will throw an exception.
	 * @param regulationFromPdb
	 * 	the regulation to be returned when client.createNewRegulationOnPolicyDb is called, if pdbSuccessful is true.
	 * @param successFromPc
	 * 	whether the client should return a response indicating successful submission to the PC.
	 * @param successFromOse
	 * 	whether the client should return a response indicating successful submission to the OSE.
	 */
	private void setUpResponsesFromOtherModulesForNewRegulation(boolean ospAuthenticated, boolean pdbSuccessful, PrivacyRegulation regulationFromPdb, boolean successFromPc, boolean successFromOse) throws HttpException
	{
		when(client.isOspAuthenticated(any(String.class))).thenReturn(ospAuthenticated);
		
		if (pdbSuccessful)
		{
			when(client.createNewRegulationOnPolicyDb(any(PrivacyRegulationInput.class))).thenReturn(regulationFromPdb);
		}
		else
		{
			when(client.createNewRegulationOnPolicyDb(any(PrivacyRegulationInput.class))).thenThrow(new HttpException());
		}
		
		when(client.sendNewRegulationToPolicyComputation(any(PrivacyRegulation.class))).thenReturn(successFromPc);
		when(client.sendNewRegulationToOspEnforcement(any(PrivacyRegulation.class))).thenReturn(successFromOse);
	}
	
	@Test
	public void testRegulationsRegIdPut_OspNotAuthenticated_RegulationNotSentToOtherModules() throws HttpException
	{
		// Set up
		setUpResponsesFromOtherModulesForExistingRegulation(false, false, false, false);

		// Exercise
		implementation.regulationsRegIdPut(new RegulationBody(), "123");

		// Verify
		verify(client, never()).updateExistingRegulationOnPolicyDb(anyString(), any(PrivacyRegulationInput.class));
		verify(client, never()).sendExistingRegulationToPolicyComputation((any(PrivacyRegulation.class)));
		verify(client, never()).sendExistingRegulationToOspEnforcement((any(PrivacyRegulation.class)));
	}
	
	@Test
	public void testRegulationsRegIdPut_OspNotAuthenticated_UnauthorisedCodeReturned() throws HttpException
	{
		// Set up
		setUpResponsesFromOtherModulesForExistingRegulation(false, false, false, false);

		// Exercise
		Response responseToRegulator = implementation.regulationsRegIdPut(new RegulationBody(), "123");

		// Verify
		int statusCodeResponse = responseToRegulator.getStatus();
		assertEquals("If the OSP is not authenticated, the RAPI should return an unauthorised code.", Status.UNAUTHORIZED.getStatusCode(), statusCodeResponse);
	}

	@Test
	public void testRegulationsRegIdPut_OspAuthenticated_RegulationSentToPdb() throws HttpException
	{
		// Set up
		setUpResponsesFromOtherModulesForExistingRegulation(true, false, false, false);
		RegulationBody regulationBody = new RegulationBody();
		String regId = "123";
		regulationBody.setRegulation(new PrivacyRegulationInput());

		// Exercise
		implementation.regulationsRegIdPut(regulationBody, regId);

		// Verify
		PrivacyRegulationInput input = regulationBody.getRegulation();
		verify(client).updateExistingRegulationOnPolicyDb(regId, input);
	}

	@Test
	public void testRegulationsRegIdPut_OspAuthenticated_PutToPdbUnsuccessful_RegulationNotSentToPcOrOse() throws HttpException
	{
		// Set up
		setUpResponsesFromOtherModulesForExistingRegulation(true, false, false, false);

		// Exercise
		implementation.regulationsRegIdPut(new RegulationBody(), "123");

		// Verify
		verify(client, never()).sendExistingRegulationToPolicyComputation((any(PrivacyRegulation.class)));
		verify(client, never()).sendExistingRegulationToOspEnforcement((any(PrivacyRegulation.class)));
	}
	
	@Test
	public void testRegulationsRegIdPut_OspAuthenticated_PutToPdbUnsuccessful_UnavailableResponseReturned() throws HttpException
	{
		// Set up
		setUpResponsesFromOtherModulesForExistingRegulation(true, false, false, false);

		// Exercise
		Response responseToRegulator = implementation.regulationsRegIdPut(new RegulationBody(), "123");
		int statusCodeResponse = responseToRegulator.getStatus();

		// Verify
		assertEquals("When putting to the PDB is unsuccessful, an unavailable status should be set on the response.", Status.SERVICE_UNAVAILABLE.getStatusCode(), statusCodeResponse);
	}

	@Test
	public void testRegulationsRegIdPut_OspAuthenticated_PutToPdbSuccessful_RegulationSentToPcAndOse() throws HttpException
	{
		// Set up
		PrivacyRegulation privacyRegulationFromPdb = new PrivacyRegulation("", "", "", null, "", null);
		setUpResponsesFromOtherModulesForExistingRegulation(true, true, privacyRegulationFromPdb, false, false);

		// Exercise
		implementation.regulationsRegIdPut(new RegulationBody(), "123");

		// Verify
		verify(client).sendExistingRegulationToPolicyComputation(privacyRegulationFromPdb);
		verify(client).sendExistingRegulationToOspEnforcement(privacyRegulationFromPdb);
	}
	
	@Test
	public void testRegulationsRegIdPut_OspAuthenticated_PutToPdbSuccessful_PcAndOseRejectRegulation_UnavailableResponseReturned() throws HttpException
	{
		// Set up
		setUpResponsesFromOtherModulesForExistingRegulation(true, true, false, false);

		// Exercise
		Response response = implementation.regulationsRegIdPut(new RegulationBody(), "123");

		// Verify
		int status = response.getStatus();
		assertEquals("When neither PC nor OSE returns a success code, the RAPI should return an unavailable status code.", Status.SERVICE_UNAVAILABLE.getStatusCode(), status);
	}
	
	@Test
	public void testRegulationsRegIdPut_OspAuthenticated_PutToPdbSuccessful_PcRejectsRegulation_OseAcceptsRegulation_UnavailableResponseReturned() throws HttpException
	{
		// Set up
		setUpResponsesFromOtherModulesForExistingRegulation(true, true, false, true);

		// Exercise
		Response response = implementation.regulationsRegIdPut(new RegulationBody(), "123");

		// Verify
		int status = response.getStatus();
		assertEquals("When the PC does not return a success code, the RAPI should return an unavailable status code.", Status.SERVICE_UNAVAILABLE.getStatusCode(), status);
	}

	@Test
	public void testRegulationsRegIdPut_OspAuthenticated_PutToPdbSuccessful_PcAcceptsRegulation_OseRejectsRegulation_UnavailableResponseReturned() throws HttpException
	{
		// Set up
		setUpResponsesFromOtherModulesForExistingRegulation(true, true, true, false);

		// Exercise
		Response response = implementation.regulationsRegIdPut(new RegulationBody(), "123");

		// Verify
		int status = response.getStatus();
		assertEquals("When the OSE does not return a success code, the RAPI should return an unavailable status code.", Status.SERVICE_UNAVAILABLE.getStatusCode(), status);
	}
	
	@Test
	public void testRegulationsRegIdPut_OspAuthenticated_PutToPdbSuccessful_PcAndOseAcceptRegulation_AcceptResponseReturned() throws HttpException
	{
		// Set up
		setUpResponsesFromOtherModulesForExistingRegulation(true, true, true, true);
		
		// Exercise
		Response response = implementation.regulationsRegIdPut(new RegulationBody(), "123");

		// Verify
		int status = response.getStatus();
		assertEquals("When all modules return success codes, the RAPI should return an accepted status code.", Status.ACCEPTED.getStatusCode(), status);
	}
	
	/**
	 * Can some responses from the mocked client.
	 * @param ospAuthenticated
	 * 	whether the client should indicate that the OSP is authenticated.
	 * @param pdbSuccessful
	 * 	whether the client should return a response indicating successful submission to the PDB. If false, calls to client.createExistingRegulationOnPolicyDb will throw an exception.
	 * @param successFromPc
	 * 	whether the client should return a response indicating successful submission to the PC.
	 * @param successFromOse
	 * 	whether the client should return a response indicating successful submission to the OSE.
	 */
	private void setUpResponsesFromOtherModulesForExistingRegulation(boolean ospAuthenticated, boolean pdbSuccessful, boolean successFromPc, boolean successFromOse) throws HttpException
	{
		setUpResponsesFromOtherModulesForExistingRegulation(ospAuthenticated, pdbSuccessful, new PrivacyRegulation("", "", "", null, "", null), successFromPc, successFromOse);	
	}
	
	/**
	 * Can some responses from the mocked client.
	 * @param ospAuthenticated
	 * 	whether the client should indicate that the OSP is authenticated.
	 * @param pdbSuccessful
	 * 	whether the client should return a response indicating successful submission to the PDB. If false, calls to client.createExistingRegulationOnPolicyDb will throw an exception.
	 * @param regulationFromPdb
	 * 	the regulation to be returned when client.createExistingRegulationOnPolicyDb is called, if pdbSuccessful is true.
	 * @param successFromPc
	 * 	whether the client should return a response indicating successful submission to the PC.
	 * @param successFromOse
	 * 	whether the client should return a response indicating successful submission to the OSE.
	 */
	private void setUpResponsesFromOtherModulesForExistingRegulation(boolean ospAuthenticated, boolean pdbSuccessful, PrivacyRegulation regulationFromPdb, boolean successFromPc, boolean successFromOse) throws HttpException
	{
		when(client.isOspAuthenticated(any(String.class))).thenReturn(ospAuthenticated);
		
		if (pdbSuccessful)
		{
			when(client.updateExistingRegulationOnPolicyDb(anyString(), any(PrivacyRegulationInput.class))).thenReturn(regulationFromPdb);
		}
		else
		{
			when(client.updateExistingRegulationOnPolicyDb(anyString(), any(PrivacyRegulationInput.class))).thenThrow(new HttpException());
		}
		
		when(client.sendExistingRegulationToPolicyComputation(any(PrivacyRegulation.class))).thenReturn(successFromPc);
		when(client.sendExistingRegulationToOspEnforcement(any(PrivacyRegulation.class))).thenReturn(successFromOse);
	}
}
