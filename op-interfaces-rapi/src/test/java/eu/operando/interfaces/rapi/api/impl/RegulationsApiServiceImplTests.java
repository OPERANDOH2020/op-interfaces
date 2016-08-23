package eu.operando.interfaces.rapi.api.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.ws.rs.core.Response;

import org.apache.http.HttpException;
import org.apache.http.HttpStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import eu.operando.interfaces.rapi.model.PrivacyRegulation;
import eu.operando.interfaces.rapi.model.PrivacyRegulationInput;
import eu.operando.interfaces.rapi.model.RegulationBody;

@RunWith(MockitoJUnitRunner.class)
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
		setUpResponsesFromOtherModulesNewRegulation(false, false, false, false);

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
		setUpResponsesFromOtherModulesNewRegulation(false, false, false, false);

		// Exercise
		Response responseToRegulator = implementation.regulationsPost(new RegulationBody());

		// Verify
		int statusCodeResponse = responseToRegulator.getStatus();
		assertEquals("If the OSP is not authenticated, the RAPI should return an unauthorised code.", HttpStatus.SC_UNAUTHORIZED, statusCodeResponse);
	}

	@Test
	public void testRegulationsPost_OspAuthenticated_RegulationSentToPdb() throws HttpException
	{
		// Set up
		setUpResponsesFromOtherModulesNewRegulation(true, false, false, false);

		// Exercise
		implementation.regulationsPost(new RegulationBody());

		// Verify
		verify(client).createNewRegulationOnPolicyDb(any(PrivacyRegulationInput.class));
	}

	@Test
	public void testRegulationsPost_OspAuthenticated_PostToPdbUnsuccessful_RegulationNotSentToPcOrOse() throws HttpException
	{
		// Set up
		setUpResponsesFromOtherModulesNewRegulation(true, false, false, false);

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
		setUpResponsesFromOtherModulesNewRegulation(true, false, false, false);

		// Exercise
		Response responseToRegulator = implementation.regulationsPost(new RegulationBody());
		int statusCodeResponse = responseToRegulator.getStatus();

		// Verify
		assertEquals("When posting to the PDB is unsuccessful, an unavailable status should be set on the response.", HttpStatus.SC_SERVICE_UNAVAILABLE, statusCodeResponse);
	}

	@Test
	public void testRegulationsPost_OspAuthenticated_PostToPdbSuccessful_RegulationSentToPcAndOse() throws HttpException
	{
		// Set up
		PrivacyRegulation privacyRegulationFromPdb = new PrivacyRegulation("", "", "", null, "", null);
		setUpResponsesFromOtherModulesNewRegulation(true, privacyRegulationFromPdb, false, false);

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
		setUpResponsesFromOtherModulesNewRegulation(true, true, false, false);

		// Exercise
		Response response = implementation.regulationsPost(new RegulationBody());

		// Verify
		int status = response.getStatus();
		assertEquals("When neither PC nor OSE returns a success code, the RAPI should return an unavailable status code.", HttpStatus.SC_SERVICE_UNAVAILABLE, status);
	}
	
	@Test
	public void testRegulationsPost_OspAuthenticated_PostToPdbSuccessful_PcRejectsRegulation_OseAcceptsRegulation_UnavailableResponseReturned() throws HttpException
	{
		// Set up
		setUpResponsesFromOtherModulesNewRegulation(true, true, false, true);

		// Exercise
		Response response = implementation.regulationsPost(new RegulationBody());

		// Verify
		int status = response.getStatus();
		assertEquals("When the PC does not return a success code, the RAPI should return an unavailable status code.", HttpStatus.SC_SERVICE_UNAVAILABLE, status);
	}

	@Test
	public void testRegulationsPost_OspAuthenticated_PostToPdbSuccessful_PcAcceptsRegulation_OseRejectsRegulation_UnavailableResponseReturned() throws HttpException
	{
		// Set up
		setUpResponsesFromOtherModulesNewRegulation(true, true, true, false);

		// Exercise
		Response response = implementation.regulationsPost(new RegulationBody());

		// Verify
		int status = response.getStatus();
		assertEquals("When the OSE does not return a success code, the RAPI should return an unavailable status code.", HttpStatus.SC_SERVICE_UNAVAILABLE, status);
	}
	
	@Test
	public void testRegulationsPost_OspAuthenticated_PostToPdbSuccessful_PcAndOseAcceptRegulation_AcceptResponseReturned() throws HttpException
	{
		// Set up
		setUpResponsesFromOtherModulesNewRegulation(true, true, true, true);
		
		// Exercise
		Response response = implementation.regulationsPost(new RegulationBody());

		// Verify
		int status = response.getStatus();
		assertEquals("When all modules return success codes, the RAPI should return an accepted status code.", HttpStatus.SC_ACCEPTED, status);
	}
	
	private void setUpResponsesFromOtherModulesNewRegulation(boolean ospAuthenticated, boolean pdbSuccessful, boolean successFromPc, boolean successFromOse) throws HttpException
	{
		if (pdbSuccessful)
		{
			setUpResponsesFromOtherModulesNewRegulation(ospAuthenticated, new PrivacyRegulation("", "", "", null, "", null), successFromPc, successFromOse);
		}
		else
		{
			when(client.isOspAuthenticated(any(String.class))).thenReturn(ospAuthenticated);
			when(client.createNewRegulationOnPolicyDb(any(PrivacyRegulationInput.class))).thenThrow(new HttpException());
			when(client.sendNewRegulationToPolicyComputation(any(PrivacyRegulation.class))).thenReturn(successFromPc);
			when(client.sendNewRegulationToOspEnforcement(any(PrivacyRegulation.class))).thenReturn(successFromOse);
		}		
	}
	
	private void setUpResponsesFromOtherModulesNewRegulation(boolean ospAuthenticated, PrivacyRegulation regulationFromPdb, boolean successFromPc, boolean successFromOse) throws HttpException
	{
		when(client.isOspAuthenticated(any(String.class))).thenReturn(ospAuthenticated);
		when(client.createNewRegulationOnPolicyDb(any(PrivacyRegulationInput.class))).thenReturn(regulationFromPdb);
		when(client.sendNewRegulationToPolicyComputation(any(PrivacyRegulation.class))).thenReturn(successFromPc);
		when(client.sendNewRegulationToOspEnforcement(any(PrivacyRegulation.class))).thenReturn(successFromOse);
	}
}
