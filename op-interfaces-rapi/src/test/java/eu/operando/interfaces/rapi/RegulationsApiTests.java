package eu.operando.interfaces.rapi;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import eu.operando.OperandoCommunicationException;
import eu.operando.UnableToGetDataException;
import eu.operando.api.AuthenticationService;
import eu.operando.api.model.PrivacyRegulationInput;

@RunWith(MockitoJUnitRunner.class)
public class RegulationsApiTests
{
	// Variables to be tested.
	private static final String SERVICE_ID_PROCESS_NEW_REGULATION = "POST/regulator/regulations";
	private static final String SERVICE_ID_PROCESS_EXISTING_REGULATION = "PUT/regulator/regulations/{reg-id}";

	@Mock
	private AuthenticationService authenticationDelegate;
	
	@Mock
	private RegulationsApiService regulationsDelegate;
	
	@InjectMocks
	private RegulationsApi api = new RegulationsApi();
	
	@Test
	public void testRegulationsPost_AuthenticationDelegateInvoked() throws OperandoCommunicationException, UnableToGetDataException
	{
		// Set up
		String serviceTicket = "st";
		when(authenticationDelegate.isAuthenticatedForService(serviceTicket, SERVICE_ID_PROCESS_NEW_REGULATION)).thenReturn(true);
		PrivacyRegulationInput regulation = new PrivacyRegulationInput();

		// Exercise
		api.regulationsPost(serviceTicket, regulation);

		// Verify
		verify(authenticationDelegate).isAuthenticatedForService(serviceTicket, SERVICE_ID_PROCESS_NEW_REGULATION);
	}

	@Test(expected = UnableToGetDataException.class)
	public void testRegulationsPost_CannotAuthenticate_ThrowException() throws OperandoCommunicationException, UnableToGetDataException
	{
		// Set up
		String serviceTicket = "st";
		when(authenticationDelegate.isAuthenticatedForService(serviceTicket, SERVICE_ID_PROCESS_NEW_REGULATION)).thenThrow(UnableToGetDataException.class);
		PrivacyRegulationInput regulation = new PrivacyRegulationInput();

		// Exercise
		api.regulationsPost(serviceTicket, regulation);
		
		// Verify - expect exception in attribute
	}

	@Test
	public void testRegulationsPost_NotAuthenticated_DelegateNotCalled() throws OperandoCommunicationException, UnableToGetDataException
	{
		// Set up
		String serviceTicket = "badSt";
		when(authenticationDelegate.isAuthenticatedForService(serviceTicket, SERVICE_ID_PROCESS_NEW_REGULATION)).thenReturn(false);
		PrivacyRegulationInput regulation = new PrivacyRegulationInput();

		// Exercise
		api.regulationsPost(serviceTicket, regulation);

		// Verify
		verifyZeroInteractions(regulationsDelegate);
	}

	@Test
	public void testRegulationsPost_NotAuthenticated_ReturnUnauthorisedCode() throws OperandoCommunicationException, UnableToGetDataException
	{
		// Set up
		String serviceTicket = "badSt";
		when(authenticationDelegate.isAuthenticatedForService(serviceTicket, SERVICE_ID_PROCESS_NEW_REGULATION)).thenReturn(false);
		PrivacyRegulationInput regulation = new PrivacyRegulationInput();

		// Exercise
		Response response = api.regulationsPost(serviceTicket, regulation);

		// Verify
		int statusCodeResponse = response.getStatus();
		assertEquals("If the OSP is not authenticated, the Regulations Api should return an unauthorised code.", Status.UNAUTHORIZED.getStatusCode(), statusCodeResponse);
	}

	@Test
	public void testRegulationsPost_Authenticated_RegulationDelegateInvoked() throws OperandoCommunicationException, UnableToGetDataException
	{
		// Set up
		String serviceTicket = "st";
		when(authenticationDelegate.isAuthenticatedForService(serviceTicket, SERVICE_ID_PROCESS_NEW_REGULATION)).thenReturn(true);
		PrivacyRegulationInput regulation = new PrivacyRegulationInput();

		// Exercise
		api.regulationsPost(serviceTicket, regulation);

		// Verify
		verify(regulationsDelegate).processNewRegulation(serviceTicket, regulation);
	}
	
	@Test
	public void testRegulationsRegIdPut_AuthenticationDelegateInvoked() throws OperandoCommunicationException, UnableToGetDataException
	{
		// Set up
		String serviceTicket = "st";
		when(authenticationDelegate.isAuthenticatedForService(serviceTicket, SERVICE_ID_PROCESS_EXISTING_REGULATION)).thenReturn(true);
		PrivacyRegulationInput regulation = new PrivacyRegulationInput();
		String regId = "reg1";

		// Exercise
		api.regulationsRegIdPut(serviceTicket, regulation, regId);

		// Verify
		verify(authenticationDelegate).isAuthenticatedForService(serviceTicket, SERVICE_ID_PROCESS_EXISTING_REGULATION);
	}

	@Test(expected = UnableToGetDataException.class)
	public void testRegulationsRegIdPut_CannotAuthenticate_ThrowException() throws OperandoCommunicationException, UnableToGetDataException
	{
		// Set up
		String serviceTicket = "st";
		when(authenticationDelegate.isAuthenticatedForService(serviceTicket, SERVICE_ID_PROCESS_EXISTING_REGULATION)).thenThrow(UnableToGetDataException.class);
		PrivacyRegulationInput regulation = new PrivacyRegulationInput();
		String regId = "reg1";

		// Exercise
		api.regulationsRegIdPut(serviceTicket, regulation, regId);
		
		// Verify - expect exception in attribute
	}

	@Test
	public void testRegulationsRegIdPut_NotAuthenticated_DelegateNotCalled() throws OperandoCommunicationException, UnableToGetDataException
	{
		// Set up
		String serviceTicket = "badSt";
		when(authenticationDelegate.isAuthenticatedForService(serviceTicket, SERVICE_ID_PROCESS_EXISTING_REGULATION)).thenReturn(false);
		PrivacyRegulationInput regulation = new PrivacyRegulationInput();
		String regId = "reg1";

		// Exercise
		api.regulationsRegIdPut(serviceTicket, regulation, regId);

		// Verify
		verifyZeroInteractions(regulationsDelegate);
	}

	@Test
	public void testRegulationsRegIdPut_NotAuthenticated_ReturnUnauthorisedCode() throws OperandoCommunicationException, UnableToGetDataException
	{
		// Set up
		String serviceTicket = "badSt";
		when(authenticationDelegate.isAuthenticatedForService(serviceTicket, SERVICE_ID_PROCESS_EXISTING_REGULATION)).thenReturn(false);
		PrivacyRegulationInput regulation = new PrivacyRegulationInput();
		String regId = "reg1";

		// Exercise
		Response response = api.regulationsRegIdPut(serviceTicket, regulation, regId);

		// Verify
		int statusCodeResponse = response.getStatus();
		assertEquals("If the OSP is not authenticated, the Regulations Api should return an unauthorised code.", Status.UNAUTHORIZED.getStatusCode(), statusCodeResponse);
	}

	@Test
	public void testRegulationsRegIdPut_Authenticated_RegulationDelegateInvoked() throws OperandoCommunicationException, UnableToGetDataException
	{
		// Set up
		String serviceTicket = "st";
		when(authenticationDelegate.isAuthenticatedForService(serviceTicket, SERVICE_ID_PROCESS_EXISTING_REGULATION)).thenReturn(true);
		PrivacyRegulationInput regulation = new PrivacyRegulationInput();
		String regId = "reg1";

		// Exercise
		api.regulationsRegIdPut(serviceTicket, regulation, regId);

		// Verify
		verify(regulationsDelegate).processExistingRegulation(serviceTicket, regulation, regId);
	}
}
