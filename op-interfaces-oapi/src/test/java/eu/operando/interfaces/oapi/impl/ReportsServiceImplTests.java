package eu.operando.interfaces.oapi.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.internal.util.collection.MultivaluedStringMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import eu.operando.OperandoCommunicationException;
import eu.operando.OperandoCommunicationException.CommunicationError;
import eu.operando.moduleclients.ClientAuthenticationApiOperandoService;
import eu.operando.moduleclients.ClientReportGenerator;

@RunWith(MockitoJUnitRunner.class)
public class ReportsServiceImplTests
{
	// Variables to test.
	private static final String SERVICE_ID_GET_REPORT = "/operando/webui/reports/";
	
	// Dummy variables.
	private static final String REPORT_FROM_CLIENT = "reportFromClient";
	private static final String SERVICE_TICKET = "ticket-ST";
	private static final String REPORT_ID = "123";
	private static final String FORMAT = "pdf";
	private static final MultivaluedMap<String, String> PARAMETERS_OPTIONAL = new MultivaluedStringMap();

	static
	{
		PARAMETERS_OPTIONAL.putSingle("key", "value");
	}
	
	@Mock
	private ClientAuthenticationApiOperandoService clientAuthenticationService;
	@Mock
	private ClientReportGenerator clientReportGenerator;

	@InjectMocks
	private ReportsServiceImpl implementation;

	@Test
	public void testReportsReportIdGet_CorrectParametersSentToAuthenticationService() throws OperandoCommunicationException
	{
		// Exercise
		implementation.reportsGetReport(SERVICE_TICKET, REPORT_ID, FORMAT, PARAMETERS_OPTIONAL);

		// Verify
		verify(clientAuthenticationService).isOspAuthenticatedForRequestedService(SERVICE_TICKET, SERVICE_ID_GET_REPORT);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testReportsReportIdGet_OspNotAuthenticated_RequestNotSentToReportGenerator() throws OperandoCommunicationException
	{
		// Set up
		setUpResponseFromOtherModules(false, null, null);

		// Exercise
		implementation.reportsGetReport(SERVICE_TICKET, REPORT_ID, FORMAT, PARAMETERS_OPTIONAL);

		// Verify
		verify(clientReportGenerator, never()).getReport(anyString(), anyString(), any(MultivaluedMap.class));
	}

	@Test
	public void testReportsReportIdGet_OspNotAuthenticated_UnauthorisedCodeReturned() throws OperandoCommunicationException
	{
		// Set up
		setUpResponseFromOtherModules(false, null, null);

		Response responseToReturn = implementation.reportsGetReport(SERVICE_TICKET, REPORT_ID, FORMAT, PARAMETERS_OPTIONAL);

		// Verify
		int statusCodeResponse = responseToReturn.getStatus();
		assertEquals("If the OSP is not authenticated, the RAPI should return an unauthorised code.", Status.UNAUTHORIZED.getStatusCode(), statusCodeResponse);
	}

	@Test
	public void testReportsReportIdGet_OspAuthenticated_ClientAskedToGetReport() throws OperandoCommunicationException
	{
		// Set up
		setUpResponseFromOtherModules(true, null, null);

		// Exercise
		implementation.reportsGetReport(SERVICE_TICKET, REPORT_ID, FORMAT, PARAMETERS_OPTIONAL);

		// Verify
		verify(clientReportGenerator).getReport(REPORT_ID, FORMAT, PARAMETERS_OPTIONAL);
	}

	@Test
	public void testReportsReportIdGet_OspAuthenticated_ClientThrowsHttpExceptionServerError_UnavailableCodeReturned() throws OperandoCommunicationException
	{
		// Set up
		setUpResponseFromOtherModules(true, CommunicationError.ERROR_FROM_OTHER_MODULE, null);

		Response responseToReturn = implementation.reportsGetReport(SERVICE_TICKET, REPORT_ID, FORMAT, PARAMETERS_OPTIONAL);

		// Verify
		int statusCodeResponse = responseToReturn.getStatus();
		assertEquals("If the client returns an OperandoCommunicationException with ERROR_FROM_OTHER_MODULE status, the RAPI should return an unavailable code.",
				Status.SERVICE_UNAVAILABLE.getStatusCode(), statusCodeResponse);
	}

	@Test
	public void testReportsReportIdGet_OspAuthenticated_ClientThrowsHttpExceptionNotFound_NotFoundCodeReturned() throws OperandoCommunicationException
	{
		// Set up
		setUpResponseFromOtherModules(true, CommunicationError.REQUESTED_RESOURCE_NOT_FOUND, null);

		Response responseToReturn = implementation.reportsGetReport(SERVICE_TICKET, REPORT_ID, FORMAT, PARAMETERS_OPTIONAL);

		// Verify
		int statusCodeResponse = responseToReturn.getStatus();
		assertEquals("If the client returns an OperandoCommunicationException with ERROR_FROM_OTHER_MODULE status, the RAPI should return a NOT_FOUND code.",
				Status.NOT_FOUND.getStatusCode(), statusCodeResponse);
	}

	@Test
	public void testReportsReportIdGet_OspAuthenticated_ClientReturnsReport_ResponseReturnedWithOkCode() throws OperandoCommunicationException
	{
		// Set up
		setUpResponseFromOtherModules(true, null, REPORT_FROM_CLIENT);

		Response responseToReturn = implementation.reportsGetReport(SERVICE_TICKET, REPORT_ID, FORMAT, PARAMETERS_OPTIONAL);

		// Verify
		int statusCodeResponse = responseToReturn.getStatus();
		assertEquals("In the event of successful report retrieval, the RAPI should return an OK code.", Status.OK.getStatusCode(), statusCodeResponse);
	}

	@Test
	public void testReportsReportIdGet_OspAuthenticated_ReportGeneratorReturnsReport_ResponseContainsReturnedReport() throws OperandoCommunicationException
	{
		// Set up
		setUpResponseFromOtherModules(true, null, REPORT_FROM_CLIENT);

		// Exercise
		Response responseFromReportGenerator = implementation.reportsGetReport(SERVICE_TICKET, REPORT_ID, FORMAT, PARAMETERS_OPTIONAL);

		// Verify
		Object objectInBody = responseFromReportGenerator.getEntity();
		assertEquals("In the event of successful report retrieval, the RAPI should return the report it gets from elsewhere in the platform.", REPORT_FROM_CLIENT,
				objectInBody);
	}

	/**
	 * @param ospAuthenticated
	 *        whether the AAPI indicates that the OSP is allowed to use this service.
	 * @param statusCodeHttpException
	 *        the status on the HTTP Exception that is thrown when getReport is called.
	 * @param reportFromClient
	 *        the report that should be returned by the client.
	 * @throws OperandoCommunicationException
	 */
	@SuppressWarnings("unchecked")
	private void setUpResponseFromOtherModules(boolean ospAuthenticated, CommunicationError errorOnOperandoCommunicationExceptionFromClient,
			String reportFromClient) throws OperandoCommunicationException
	{
		when(clientAuthenticationService.isOspAuthenticatedForRequestedService(anyString(), anyString())).thenReturn(ospAuthenticated);
		if (errorOnOperandoCommunicationExceptionFromClient != null)
		{
			OperandoCommunicationException communicationException = new OperandoCommunicationException(errorOnOperandoCommunicationExceptionFromClient);
			when(clientReportGenerator.getReport(anyString(), anyString(), any(MultivaluedMap.class))).thenThrow(communicationException);
		}
		if (reportFromClient != null)
		{
			when(clientReportGenerator.getReport(anyString(), anyString(), any(MultivaluedMap.class))).thenReturn(reportFromClient);
		}
	}
}
