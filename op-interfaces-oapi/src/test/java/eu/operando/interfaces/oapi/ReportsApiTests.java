package eu.operando.interfaces.oapi;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.glassfish.jersey.internal.inject.UriInfoInjectee;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import eu.operando.OperandoCommunicationException;
import eu.operando.UnableToGetDataException;
import eu.operando.api.AuthenticationService;

@RunWith(MockitoJUnitRunner.class)
public class ReportsApiTests
{
	// Variables to be tested.
	private static final String SERVICE_ID_GET_REPORT = "/operando/webui/reports/";

	@Mock
	private AuthenticationService authenticationDelegate;
	
	@Mock
	private ReportsService reportsDelegate;
	
	@InjectMocks
	private ReportsApi api = new ReportsApi();
	
	@Test
	public void testReportsReportIdGet_AuthenticationDelegateInvoked() throws OperandoCommunicationException, UnableToGetDataException
	{
		// Set up
		String serviceTicket = "st";
		String reportId = "r1";
		String format = "pdf";
		UriInfo uriInfo = Mockito.mock(UriInfo.class);
		when(authenticationDelegate.isAuthenticatedForService(serviceTicket, SERVICE_ID_GET_REPORT)).thenReturn(false);

		// Exercise
		api.reportsReportIdGet(serviceTicket, reportId, format, uriInfo);

		// Verify
		verify(authenticationDelegate).isAuthenticatedForService(serviceTicket, SERVICE_ID_GET_REPORT);
	}

	@Test(expected = UnableToGetDataException.class)
	public void testReportsReportIdGet_CannotAuthenticate_ThrowException() throws OperandoCommunicationException, UnableToGetDataException
	{
		// Set up
		String serviceTicket = "st";
		String reportId = "r1";
		String format = "pdf";
		UriInfo uriInfo = Mockito.mock(UriInfo.class);
		when(authenticationDelegate.isAuthenticatedForService(serviceTicket, SERVICE_ID_GET_REPORT)).thenThrow(UnableToGetDataException.class);

		// Exercise
		api.reportsReportIdGet(serviceTicket, reportId, format, uriInfo);
		
		// Verify - expect exception in attribute
	}

	@Test
	public void testReportsReportIdGet_NotAuthenticated_DelegateNotCalled() throws OperandoCommunicationException, UnableToGetDataException
	{
		// Set up
		String serviceTicket = "badSt";
		String reportId = "r1";
		String format = "pdf";
		UriInfo uriInfo = Mockito.mock(UriInfo.class);
		when(authenticationDelegate.isAuthenticatedForService(serviceTicket, SERVICE_ID_GET_REPORT)).thenReturn(false);

		// Exercise
		api.reportsReportIdGet(serviceTicket, reportId, format, uriInfo);

		// Verify
		verifyZeroInteractions(reportsDelegate);
	}

	@Test
	public void testReportsReportIdGet_NotAuthenticated_ReturnUnauthorisedCode() throws OperandoCommunicationException, UnableToGetDataException
	{
		// Set up
		String serviceTicket = "badSt";
		String reportId = "r1";
		String format = "pdf";
		UriInfo uriInfo = Mockito.mock(UriInfo.class);
		when(authenticationDelegate.isAuthenticatedForService(serviceTicket, SERVICE_ID_GET_REPORT)).thenReturn(false);

		// Exercise
		Response response = api.reportsReportIdGet(serviceTicket, reportId, format, uriInfo);

		// Verify
		int statusCodeResponse = response.getStatus();
		assertEquals("If the OSP is not authenticated, the API should return an unauthorized code.",
				Status.UNAUTHORIZED.getStatusCode(), statusCodeResponse);
	}
}
