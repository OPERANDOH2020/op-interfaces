package eu.operando.interfaces.oapi;

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

import eu.operando.AuthenticationWrapper;
import eu.operando.OperandoCommunicationException;
import eu.operando.OperandoCommunicationException.CommunicationError;
import eu.operando.UnableToGetDataException;
import eu.operando.api.AuthenticationService;
import eu.operando.api.model.AnalyticsReport;
import eu.operando.interfaces.oapi.BigDataAnalyticsApi;
import eu.operando.interfaces.oapi.BigDataAnalyticsService;

@RunWith(MockitoJUnitRunner.class)
public class BigDataAnalyticsApiTests {

	private static final String SERVICE_ID = "GET/bda/jobs/{job-id}/reports/latest";
	
	@Mock
	private AuthenticationService authenticationDelegate;
	
	@Mock
	private BigDataAnalyticsService bigDataDelegate;
	
	@InjectMocks
	private BigDataAnalyticsApi api = new BigDataAnalyticsApi();
	
	@Test
	public void testGetReport_Authentication_CorrectParameters() throws OperandoCommunicationException, UnableToGetDataException{
		String serviceTicket = "A123";
		setUpAuth(true);
		
		api.getBdaReport(serviceTicket, "C456");
		
		verify(authenticationDelegate).requestAuthenticationDetails(serviceTicket, SERVICE_ID);
	}
	
	@Test
	public void testGetReport_Authentication_OtherModulesNotCalledIfNotAuthenticated() throws OperandoCommunicationException, UnableToGetDataException{
		setUpAuth(false);
		
		api.getBdaReport("A123", "C456");
		
		verify(bigDataDelegate, never()).getBdaReport(anyString(), anyString());
	}
	
	@Test
	public void testGetReport_Authentication_UnauthorisedCodeReturnedIfNotAuthenticated() throws OperandoCommunicationException, UnableToGetDataException{
		setUpAuth(false);
		
		Response response = api.getBdaReport("A123", "C456");
		
		int statusCodeResponse = response.getStatus();
		assertEquals(
			"If the OSP is not authenticated, the OAPI should return an unauthorised code.", 
			Status.UNAUTHORIZED.getStatusCode(), statusCodeResponse
		);
	}
	
	@Test
	public void testGetReport_Authentication_InternalErrorCodeReturnedIfCannotNotAuthenticate() throws OperandoCommunicationException, UnableToGetDataException{
		setUpAuth(CommunicationError.REQUESTED_RESOURCE_NOT_FOUND);
		
		Response response = api.getBdaReport("A123", "C456");
		
		int statusCodeResponse = response.getStatus();
		assertEquals(
			"If the authentication server returns an error, the OAPI should return an internal error code.",
			Status.INTERNAL_SERVER_ERROR.getStatusCode(), statusCodeResponse
		);
	}
	
	@Test
	public void testGetReport_Report_CorrectParameters() throws OperandoCommunicationException, UnableToGetDataException{
		String jobId = "C456";
		String userId = "D000";
		setUpAuth(true, userId);
		
		api.getBdaReport("A123", jobId);
		
		verify(bigDataDelegate).getBdaReport(jobId, userId);
	}
	
	@Test
	public void testGetReport_Report_OkCodeReturnedIfFound() throws OperandoCommunicationException, UnableToGetDataException{
		AnalyticsReport report = new AnalyticsReport("2", "Report", "a report", "CgoKCgoKCgoKCgo8IURPQ1RZUEUg");
		setUpServices(report);
		
		Response response = api.getBdaReport("A123", "C456");
		
		int statusCodeResponse = response.getStatus();
		assertEquals(
			"If the report is found successfully, the OAPI should return an ok code.",
			Status.OK.getStatusCode(), statusCodeResponse
		);
	}
	
	@Test
	public void testGetReport_Report_ReportReturnedIfFound() throws OperandoCommunicationException, UnableToGetDataException{
		AnalyticsReport report = new AnalyticsReport("2", "Report", "a report", "CgoKCgoKCgoKCgo8IURPQ1RZUEUg");
		setUpServices(report);
		
		Response response = api.getBdaReport("A123", "C456");
		
		AnalyticsReport returnedReport = (AnalyticsReport) response.getEntity();
		assertEquals(
			"If the report is found successfully, the OAPI should return an ok code.",
			report, returnedReport
		);
	}
	
	@Test
	public void testGetReport_Report_NotFoundCodeReturnedIfCantFindReport() throws OperandoCommunicationException, UnableToGetDataException{
		setUpServices((AnalyticsReport) null);
		
		Response response = api.getBdaReport("A123", "C456");
		
		int statusCodeResponse = response.getStatus();
		assertEquals(
			"If the report is not found, the OAPI should return a not found code.",
			Status.NOT_FOUND.getStatusCode(), statusCodeResponse
		);
	}
	
	@Test
	public void testGetReport_Report_InternalErrorCodeReturnedIfCantGetReport() throws OperandoCommunicationException, UnableToGetDataException{
		setUpServices(CommunicationError.ERROR_FROM_OTHER_MODULE);
		
		Response response = api.getBdaReport("A123", "C456");
		
		int statusCodeResponse = response.getStatus();
		assertEquals(
			"If the back end cannot be contacted, the OAPI should return an internal error code.",
			Status.INTERNAL_SERVER_ERROR.getStatusCode(), statusCodeResponse
		);
	}
	
	private void setUpAuth(boolean shouldAuthenticate) throws OperandoCommunicationException, UnableToGetDataException{
		setUpAuth(shouldAuthenticate, null);
	}
	
	private void setUpAuth(boolean shouldAuthenticate, String userName) throws OperandoCommunicationException, UnableToGetDataException{
		when(authenticationDelegate.requestAuthenticationDetails(anyString(), anyString()))
			.thenReturn(new AuthenticationWrapper(shouldAuthenticate, userName));
		when(bigDataDelegate.getBdaReport(anyString(), anyString())).thenReturn(null);
	}
	
	private void setUpAuth(CommunicationError err) throws UnableToGetDataException{
		when(authenticationDelegate.requestAuthenticationDetails(anyString(), anyString()))
			.thenThrow(new UnableToGetDataException(new OperandoCommunicationException(err)));
	}
	
	private void setUpServices(AnalyticsReport toReturn) throws OperandoCommunicationException, UnableToGetDataException{
		when(authenticationDelegate.requestAuthenticationDetails(anyString(), anyString()))
		.thenReturn(new AuthenticationWrapper(true, ""));
		when(bigDataDelegate.getBdaReport(anyString(), anyString())).thenReturn(toReturn);
	}
	
	private void setUpServices(CommunicationError err) throws OperandoCommunicationException, UnableToGetDataException{
		when(authenticationDelegate.requestAuthenticationDetails(anyString(), anyString()))
			.thenReturn(new AuthenticationWrapper(true, ""));
		when(bigDataDelegate.getBdaReport(anyString(), anyString())).thenThrow(new UnableToGetDataException(new OperandoCommunicationException(err)));
	}
}
