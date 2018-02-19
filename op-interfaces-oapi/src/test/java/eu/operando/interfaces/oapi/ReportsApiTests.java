package eu.operando.interfaces.oapi;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.glassfish.jersey.internal.inject.UriInfoInjectee;
import org.glassfish.jersey.internal.util.collection.MultivaluedStringMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import eu.operando.OperandoCommunicationException;
import eu.operando.OperandoCommunicationException.CommunicationError;
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
		assertEquals("If the OSP is not authenticated, the API should return an unauthorized code.", Status.UNAUTHORIZED.getStatusCode(), statusCodeResponse);
	}

	@Test
	public void testReportsReportIdGet_Authenticated_DelegateThrowsOperandoCommunicationExceptionError_UnavailableCodeReturned() throws OperandoCommunicationException, UnableToGetDataException
	{
		// Set up
		String serviceTicket = "st";
		String reportId = "r1";
		String format = "pdf";
		UriInfo uriInfo = Mockito.mock(UriInfo.class);
		MultivaluedStringMap queryParams = new MultivaluedStringMap();
		when(uriInfo.getQueryParameters()).thenReturn(queryParams);
		when(authenticationDelegate.isAuthenticatedForService(serviceTicket, SERVICE_ID_GET_REPORT)).thenReturn(true);
		when(reportsDelegate.getReport(reportId, format, queryParams)).thenThrow(new OperandoCommunicationException(CommunicationError.ERROR_FROM_OTHER_MODULE));

		// Exercise
		Response response = api.reportsReportIdGet(serviceTicket, reportId, format, uriInfo);

		// Verify
		int statusCodeResponse = response.getStatus();
		assertEquals("If the delegate throws an ERROR_FROM_OTHER_MODULE OperandoCommunicationException, the RAPI should return an unavailable code.",
				Status.SERVICE_UNAVAILABLE.getStatusCode(), statusCodeResponse);
	}

	@Test
	public void testReportsReportIdGet_Authenticated_DelegateThrowsOperandoCommunicationExceptionNotFound_NotFoundCodeReturned() throws OperandoCommunicationException, UnableToGetDataException
	{
		// Set up
		String serviceTicket = "st";
		String reportId = "r1";
		String format = "pdf";
		UriInfo uriInfo = Mockito.mock(UriInfo.class);
		MultivaluedStringMap queryParams = new MultivaluedStringMap();
		when(uriInfo.getQueryParameters()).thenReturn(queryParams);
		when(authenticationDelegate.isAuthenticatedForService(serviceTicket, SERVICE_ID_GET_REPORT)).thenReturn(true);
		when(reportsDelegate.getReport(reportId, format, queryParams)).thenThrow(new OperandoCommunicationException(CommunicationError.REQUESTED_RESOURCE_NOT_FOUND));

		// Exercise
		Response response = api.reportsReportIdGet(serviceTicket, reportId, format, uriInfo);

		// Verify
		int statusCodeResponse = response.getStatus();
		assertEquals("If the client returns an OperandoCommunicationException with ERROR_FROM_OTHER_MODULE status, the RAPI should return a NOT_FOUND code.",
				Status.NOT_FOUND.getStatusCode(), statusCodeResponse);
	}

	@Test
	public void testReportsReportIdGet_Authenticated_DelegateReturnsReport_OkResponseWithReportReturned() throws OperandoCommunicationException, UnableToGetDataException
	{
		// Set up
		String serviceTicket = "st";
		String reportId = "r1";
		String format = "pdf";
		UriInfo uriInfo = Mockito.mock(UriInfo.class);
		MultivaluedStringMap queryParams = new MultivaluedStringMap();
		when(uriInfo.getQueryParameters()).thenReturn(queryParams);
		when(authenticationDelegate.isAuthenticatedForService(serviceTicket, SERVICE_ID_GET_REPORT)).thenReturn(true);
		String report = "reportContents";
		when(reportsDelegate.getReport(reportId, format, queryParams)).thenReturn(report);

		// Exercise
		Response response = api.reportsReportIdGet(serviceTicket, reportId, format, uriInfo);

		// Verify
		int statusCodeResponse = response.getStatus();
		assertEquals("In the event of successful report retrieval, the RAPI should return an OK code.", Status.OK.getStatusCode(), statusCodeResponse);
		Object objectInBody = response.getEntity();
		assertEquals("In the event of successful report retrieval, the RAPI should return the report it gets from elsewhere in the platform.",
				report, objectInBody);
	}
}
