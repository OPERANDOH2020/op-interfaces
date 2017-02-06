package eu.operando.interfaces.rapi.impl;

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
import eu.operando.api.model.ComplianceReport;
import eu.operando.interfaces.rapi.ComplianceReportApi;
import eu.operando.interfaces.rapi.ComplianceReportApiService;

@RunWith(MockitoJUnitRunner.class)
public class ComplianceReportApiTests {

	private static final String SERVICE_ID = "GET/osps/{osp-id}/compliance-report";
	
	@Mock
	private AuthenticationService authenticationDelegate;
	
	@Mock
	private ComplianceReportApiService reportDelegate;
	
	@InjectMocks
	private ComplianceReportApi api = new ComplianceReportApi();
	
	@Test
	public void testGetComplianceReport_Authentication_CorrectParameters() throws OperandoCommunicationException{
		String serviceTicket = "A123";
		setUpServices(true);
		
		api.complianceReportGet(serviceTicket, "B987");
		
		verify(authenticationDelegate).isAuthenticatedForService(serviceTicket, SERVICE_ID);
	}
	
	@Test
	public void testGetComplianceReport_Authentication_OtherModulesNotCalledIfNotAuthenticated() throws OperandoCommunicationException{
		setUpServices(false);
		
		api.complianceReportGet("bad", "B987");
		
		verify(reportDelegate, never()).getComplianceReport(anyString());
	}
	
	@Test
	public void testGetComplianceReport_Authentication_ReturnUnauthorisedCodeIfNotAuthenticated() throws OperandoCommunicationException{
		setUpServices(false);
		
		Response response = api.complianceReportGet("bad", "B987");
		
		int statusCodeResponse = response.getStatus();
		assertEquals(
			"If the OSP is not authenticated, the CRAPI should return an unauthorised code.", 
			Status.UNAUTHORIZED.getStatusCode(), statusCodeResponse
		);
	}
	
	@Test
	public void testGetComplianceReport_Authentication_ReturnInternalErrorCodeIfCantAuthenticate() throws OperandoCommunicationException{
		setUpServices(new OperandoCommunicationException(null));
		
		Response response = api.complianceReportGet("A123", "B987");
		
		int statusCodeResponse = response.getStatus();
		assertEquals(
			"If the authentication server returns an error, the CRAPI should return an internal error code.",
			Status.INTERNAL_SERVER_ERROR.getStatusCode(), statusCodeResponse
		);
	}
	
	@Test
	public void testGetComplianceReport_Report_CorrectParameters() throws OperandoCommunicationException{
		String ospId = "B987";
		setUpServices(true);
		
		api.complianceReportGet("A123", ospId);
		
		verify(reportDelegate).getComplianceReport(ospId);
	}
	
	@Test
	public void testGetComplianceReport_Report_ReturnsOkCodeIfFound() throws OperandoCommunicationException{
		setUpServices(true);
		
		Response response = api.complianceReportGet("A123", "B987");
		
		int statusCodeResponse = response.getStatus();
		assertEquals(
			"If the report is found successfully, the CRAPI should return an ok code.", 
			Status.OK.getStatusCode(), statusCodeResponse
		);
	}
	
	@Test
	public void testGetComplianceReport_Report_ReturnsReportIfFound() throws OperandoCommunicationException{
		ComplianceReport report = new ComplianceReport();
		setUpServices(true, report);
		
		Response response = api.complianceReportGet("A123", "B987");
		
		ComplianceReport returnedReport = (ComplianceReport) response.getEntity();
		assertEquals(
			"If the report is found successfully, the CRAPI should return it.", 
			report, returnedReport
		);
	}
	
	@Test
	public void testGetComplianceReport_Report_ReturnNotFoundCodeIfCantFindReport() throws OperandoCommunicationException{
		setUpServices(true, new OperandoCommunicationException(CommunicationError.REQUESTED_RESOURCE_NOT_FOUND));
		
		Response response = api.complianceReportGet("A123", "B987");
		
		int statusCodeResponse = response.getStatus();
		assertEquals(
			"If the authentication server returns an error, the CRAPI should return an internal error code.",
			Status.NOT_FOUND.getStatusCode(), statusCodeResponse
		);
	}
	
	@Test
	public void testGetComplianceReport_Report_ReturnInternalErrorCodeIfCantGetReport() throws OperandoCommunicationException{
		setUpServices(true, new OperandoCommunicationException(null));
		
		Response response = api.complianceReportGet("A123", "B987");
		
		int statusCodeResponse = response.getStatus();
		assertEquals(
			"If the authentication server returns an error, the CRAPI should return an internal error code.",
			Status.INTERNAL_SERVER_ERROR.getStatusCode(), statusCodeResponse
		);
	}
	
	
	private void setUpServices(boolean shouldAuthenticate) throws OperandoCommunicationException{
		setUpServices(shouldAuthenticate, (ComplianceReport) null);
	}
	
	private void setUpServices(OperandoCommunicationException ex) throws OperandoCommunicationException{
		when(authenticationDelegate.isAuthenticatedForService(anyString(), anyString())).thenThrow(ex);
	}
	
	private void setUpServices(boolean shouldAuthenticate, ComplianceReport toReturn) throws OperandoCommunicationException{
		when(authenticationDelegate.isAuthenticatedForService(anyString(), anyString())).thenReturn(shouldAuthenticate);
		when(reportDelegate.getComplianceReport(anyString())).thenReturn(toReturn);
	}
	
	private void setUpServices(boolean shouldAuthenticate, OperandoCommunicationException ex) throws OperandoCommunicationException{
		when(authenticationDelegate.isAuthenticatedForService(anyString(), anyString())).thenReturn(shouldAuthenticate);
		when(reportDelegate.getComplianceReport(anyString())).thenThrow(ex);
	}
	
}
