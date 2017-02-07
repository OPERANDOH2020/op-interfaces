package eu.operando.interfaces.oapi.impl;

import static org.junit.Assert.assertEquals;
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
import eu.operando.api.AuthenticationService;
import eu.operando.api.model.AnalyticsReport;
import eu.operando.interfaces.oapi.BigDataAnalyticsApi;
import eu.operando.interfaces.oapi.BigDataAnalyticsApiService;

@RunWith(MockitoJUnitRunner.class)
public class BigDataAnalyticsApiTests {

	private static final String SERVICE_ID = "GET/bda/jobs/{job-id}/reports/latest";
	
	@Mock
	private AuthenticationService authenticationDelegate;
	
	@Mock
	private BigDataAnalyticsApiService bigDataDelegate;
	
	@InjectMocks
	private BigDataAnalyticsApi api = new BigDataAnalyticsApi();
	
	@Test
	public void testGetReport_Authentication_CorrectParameters() throws OperandoCommunicationException{
		String serviceTicket = "A123";
		setUpServices(true);
		
		api.getBdaReport(serviceTicket, "C456");
		
		verify(authenticationDelegate).isAuthenticatedForService(serviceTicket, SERVICE_ID);
	}
	
	@Test
	public void testGetReport_Authentication_OtherModulesNotCalledIfNotAuthenticated() throws OperandoCommunicationException{
		setUpServices(false);
		
		api.getBdaReport("A123", "C456");
		
		verify(bigDataDelegate, never()).getBdaReport(anyString());
	}
	
	@Test
	public void testGetReport_Authentication_UnauthorisedCodeReturnedIfNotAuthenticated() throws OperandoCommunicationException{
		setUpServices(false);
		
		Response response = api.getBdaReport("A123", "C456");
		
		int statusCodeResponse = response.getStatus();
		assertEquals(
			"If the OSP is not authenticated, the OAPI should return an unauthorised code.", 
			Status.UNAUTHORIZED.getStatusCode(), statusCodeResponse
		);
	}
	
	@Test
	public void testGetReport_Authentication_InternalErrorCodeReturnedIfNotAuthenticated() throws OperandoCommunicationException{
		setUpServices(new OperandoCommunicationException(CommunicationError.REQUESTED_RESOURCE_NOT_FOUND));
		
		Response response = api.getBdaReport("A123", "C456");
		
		int statusCodeResponse = response.getStatus();
		assertEquals(
			"If the authentication server returns an error, the OAPI should return an internal error code.",
			Status.INTERNAL_SERVER_ERROR.getStatusCode(), statusCodeResponse
		);
	}
	
	@Test
	public void testGetReport_Report_CorrectParameters() throws OperandoCommunicationException{
		String serviceTicket = "A123";
		String jobId = "C456";
		setUpServices(true);
		
		api.getBdaReport(serviceTicket, jobId);
		
		verify(bigDataDelegate).getBdaReport(jobId);
	}
	
	@Test
	public void testGetReport_Report_OkCodeReturnedIfFound() throws OperandoCommunicationException{
		setUpServices(true);
		
		Response response = api.getBdaReport("A123", "C456");
		
		int statusCodeResponse = response.getStatus();
		assertEquals(
			"If the report is found successfully, the OAPI should return an ok code.",
			Status.OK.getStatusCode(), statusCodeResponse
		);
	}
	
	@Test
	public void testGetReport_Report_ReportReturnedIfFound() throws OperandoCommunicationException{
		AnalyticsReport report = new AnalyticsReport();
		setUpServices(true, report);
		
		Response response = api.getBdaReport("A123", "C456");
		
		AnalyticsReport returnedReport = (AnalyticsReport) response.getEntity();
		assertEquals(
			"If the report is found successfully, the OAPI should return an ok code.",
			report, returnedReport
		);
	}
	
	@Test
	public void testGetReport_Report_NotFoundCodeReturnedIfCantFindReport() throws OperandoCommunicationException{
		setUpServices(true, new OperandoCommunicationException(CommunicationError.REQUESTED_RESOURCE_NOT_FOUND));
		
		Response response = api.getBdaReport("A123", "C456");
		
		int statusCodeResponse = response.getStatus();
		assertEquals(
			"If the report is not found, the OAPI should return a not found code.",
			Status.NOT_FOUND.getStatusCode(), statusCodeResponse
		);
	}
	
	@Test
	public void testGetReport_Report_InternalErrorCodeReturnedIfCantGetReport() throws OperandoCommunicationException{
		setUpServices(true, new OperandoCommunicationException(CommunicationError.ERROR_FROM_OTHER_MODULE));
		
		Response response = api.getBdaReport("A123", "C456");
		
		int statusCodeResponse = response.getStatus();
		assertEquals(
			"If the back end cannot be contacted, the OAPI should return an internal error code.",
			Status.INTERNAL_SERVER_ERROR.getStatusCode(), statusCodeResponse
		);
	}
	
	private void setUpServices(boolean shouldAuthenticate) throws OperandoCommunicationException{
		setUpServices(shouldAuthenticate, (AnalyticsReport) null);
	}
	
	private void setUpServices(OperandoCommunicationException ex) throws OperandoCommunicationException{
		when(authenticationDelegate.isAuthenticatedForService(anyString(), anyString())).thenThrow(ex);
	}
	
	private void setUpServices(boolean shouldAuthenticate, AnalyticsReport toReturn) throws OperandoCommunicationException{
		when(authenticationDelegate.isAuthenticatedForService(anyString(), anyString())).thenReturn(shouldAuthenticate);
		when(bigDataDelegate.getBdaReport(anyString())).thenReturn(toReturn);
	}
	
	private void setUpServices(boolean shouldAuthenticate, OperandoCommunicationException ex) throws OperandoCommunicationException{
		when(authenticationDelegate.isAuthenticatedForService(anyString(), anyString())).thenReturn(shouldAuthenticate);
		when(bigDataDelegate.getBdaReport(anyString())).thenThrow(ex);
	}
}
