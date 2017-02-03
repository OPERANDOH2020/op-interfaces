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

import eu.operando.api.AuthenticationService;
import eu.operando.api.model.ComplianceReport;
import eu.operando.interfaces.rapi.ComplianceReportApi;
import eu.operando.interfaces.rapi.ComplianceReportApiService;

@RunWith(MockitoJUnitRunner.class)
public class ComplianceReportApiTests {

	private static final String SERVICE_ID = "";
	
	@Mock
	private AuthenticationService authenticationService;
	
	@Mock
	private ComplianceReportApiService complianceReportApiService;
	
	@InjectMocks
	private ComplianceReportApi api;
	
	@Test
	public void testGetComplianceReport_CorrectArgumentsToVerifyAuthentication(){
		String serviceTicket = "A123";
		setUpServices(true);
		
		api.complianceReportGet(serviceTicket, "B987");
		
		verify(authenticationService.isAuthenticatedForService(serviceTicket, SERVICE_ID));
	}
	
	@Test
	public void testGetComplianceReport_NotAuthenticated_OtherModulesNotCalled(){
		String serviceTicket = "bad";
		setUpServices(false);
		
		api.complianceReportGet(serviceTicket, "B987");
		
		verify(complianceReportApiService, never()).getComplianceReport(anyString());
	}
	
	@Test
	public void testGetComplianceReport_NotAuthenticated_ReturnUnauthrosiedCode(){
		String serviceTicket = "bad";
		setUpServices(false);
		
		api.complianceReportGet(serviceTicket, "B987");
		
		Response response = api.complianceReportGet(serviceTicket, "B987");
		
		int statusCodeResponse = response.getStatus();
		assertEquals("If the OSP is not authenticated, the CRAPI should return an unauthorised code.", Status.UNAUTHORIZED.getStatusCode(), statusCodeResponse);
	}
	
	
	private void setUpServices(boolean shouldAuthenticate){
		setUpServices(shouldAuthenticate, null);
	}
	
	private void setUpServices(boolean shouldAuthenticate, ComplianceReport toReturn){
		when(authenticationService.isAuthenticatedForService(anyString(), anyString())).thenReturn(shouldAuthenticate);
	}
	
}
