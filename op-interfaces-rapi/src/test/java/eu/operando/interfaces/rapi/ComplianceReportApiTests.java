package eu.operando.interfaces.rapi;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Vector;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import eu.operando.OperandoCommunicationException;
import eu.operando.UnableToGetDataException;
import eu.operando.OperandoCommunicationException.CommunicationError;
import eu.operando.api.AuthenticationService;
import eu.operando.api.model.ComplianceReport;
import eu.operando.api.model.PrivacyPolicy;
import eu.operando.api.model.PrivacyPolicy.AccessPolicy;

@RunWith(MockitoJUnitRunner.class)
public class ComplianceReportApiTests
{

	private static final String SERVICE_ID = "GET/osps/{osp-id}/compliance-report";

	@Mock
	private AuthenticationService authenticationDelegate;

	@Mock
	private ComplianceReportsService reportDelegate;

	@InjectMocks
	private ComplianceReportApi api = new ComplianceReportApi();

	@Test
	public void testGetComplianceReport_Authentication_DelegatedToAuthenticationService() throws OperandoCommunicationException, UnableToGetDataException
	{
		String serviceTicket = "A123";
		setUpServices(true);

		api.complianceReportGet(serviceTicket, "B987");

		verify(authenticationDelegate).isAuthenticatedForService(serviceTicket, SERVICE_ID);
	}

	@Test
	public void testGetComplianceReport_Authentication_OtherModulesNotCalledIfNotAuthenticated() throws OperandoCommunicationException, UnableToGetDataException
	{
		setUpServices(false);

		api.complianceReportGet("bad", "B987");

		verify(reportDelegate, never()).getComplianceReportForOsp(anyString());
	}

	@Test
	public void testGetComplianceReport_Authentication_ReturnUnauthorisedCodeIfNotAuthenticated() throws OperandoCommunicationException, UnableToGetDataException
	{
		setUpServices(false);

		Response response = api.complianceReportGet("bad", "B987");

		int statusCodeResponse = response.getStatus();
		assertEquals("If the OSP is not authenticated, the CRAPI should return an unauthorised code.", Status.UNAUTHORIZED.getStatusCode(), statusCodeResponse);
	}

	@Test
	public void testGetComplianceReport_Authentication_ReturnInternalErrorCodeIfCantAuthenticate() throws OperandoCommunicationException, UnableToGetDataException
	{
		UnableToGetDataException exceptionToThrow = new UnableToGetDataException(new OperandoCommunicationException(CommunicationError.REQUESTED_RESOURCE_NOT_FOUND));
		setUpServices(exceptionToThrow);

		Response response = api.complianceReportGet("A123", "B987");

		int statusCodeResponse = response.getStatus();
		assertEquals("If the authentication server returns an error, the CRAPI should return an internal error code.", Status.INTERNAL_SERVER_ERROR.getStatusCode(),
				statusCodeResponse);
	}

	@Test
	public void testGetComplianceReport_Report_DelegatedToComplianceReportsService() throws OperandoCommunicationException, UnableToGetDataException
	{
		String ospId = "B987";
		setUpServices(true);

		api.complianceReportGet("A123", ospId);

		verify(reportDelegate).getComplianceReportForOsp(ospId);
	}

	@Test
	public void testGetComplianceReport_Report_ReturnsOkCodeIfFound() throws OperandoCommunicationException, UnableToGetDataException
	{
		ComplianceReport report = new ComplianceReport(new PrivacyPolicy("1", new Vector<AccessPolicy>()));
		setUpServices(true, report);

		Response response = api.complianceReportGet("A123", "B987");

		int statusCodeResponse = response.getStatus();
		assertEquals("If the report is found successfully, the CRAPI should return an ok code.", Status.OK.getStatusCode(), statusCodeResponse);
	}

	@Test
	public void testGetComplianceReport_Report_ReturnsReportIfFound() throws OperandoCommunicationException, UnableToGetDataException
	{
		ComplianceReport report = new ComplianceReport(new PrivacyPolicy("1", new Vector<AccessPolicy>()));
		setUpServices(true, report);

		Response response = api.complianceReportGet("A123", "B987");

		ComplianceReport returnedReport = (ComplianceReport) response.getEntity();
		assertEquals("If the report is found successfully, the CRAPI should return it.", report, returnedReport);
	}

	@Test
	public void testGetComplianceReport_Report_ReturnNotFoundCodeIfCantFindReport() throws OperandoCommunicationException, UnableToGetDataException
	{
		setUpServices(true, (ComplianceReport) null);

		Response response = api.complianceReportGet("A123", "B987");

		int statusCodeResponse = response.getStatus();
		assertEquals("If the OSP for the ID cannot be found, the CRAPI should return a not found error code.", Status.NOT_FOUND.getStatusCode(), statusCodeResponse);
	}

	@Test
	public void testGetComplianceReport_Report_ReturnInternalErrorCodeIfCantGetReport() throws OperandoCommunicationException, UnableToGetDataException
	{
		setUpServices(true, new UnableToGetDataException(new OperandoCommunicationException(CommunicationError.OTHER)));

		Response response = api.complianceReportGet("A123", "B987");

		int statusCodeResponse = response.getStatus();
		assertEquals("If it's not possible to get the data to generate the report, the CRAPI should return an internal error code.",
				Status.INTERNAL_SERVER_ERROR.getStatusCode(), statusCodeResponse);
	}

	private void setUpServices(boolean ospAuthenticationIsValid) throws OperandoCommunicationException, UnableToGetDataException
	{
		setUpServices(ospAuthenticationIsValid, (ComplianceReport) null);
	}

	private void setUpServices(UnableToGetDataException exceptionFromAuthenticationDelegate) throws UnableToGetDataException
	{
		when(authenticationDelegate.isAuthenticatedForService(anyString(), anyString())).thenThrow(exceptionFromAuthenticationDelegate);
	}

	private void setUpServices(boolean ospAuthenticationIsValid, ComplianceReport toReturn) throws OperandoCommunicationException, UnableToGetDataException
	{
		when(authenticationDelegate.isAuthenticatedForService(anyString(), anyString())).thenReturn(ospAuthenticationIsValid);
		when(reportDelegate.getComplianceReportForOsp(anyString())).thenReturn(toReturn);
	}

	private void setUpServices(boolean ospAuthenticationIsValid, UnableToGetDataException exceptionFromReportDelegate)
			throws OperandoCommunicationException, UnableToGetDataException
	{
		when(authenticationDelegate.isAuthenticatedForService(anyString(), anyString())).thenReturn(ospAuthenticationIsValid);
		when(reportDelegate.getComplianceReportForOsp(anyString())).thenThrow(exceptionFromReportDelegate);
	}

}
