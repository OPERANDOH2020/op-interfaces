package eu.operando.interfaces.rapi.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import eu.operando.OperandoCommunicationException;
import eu.operando.OperandoCommunicationException.CommunicationError;
import eu.operando.UnableToGetDataException;
import eu.operando.api.model.ComplianceReport;
import eu.operando.api.model.PrivacyPolicy;
import eu.operando.moduleclients.ClientPolicyDb;

@RunWith(MockitoJUnitRunner.class)
public class ComplianceReportsServiceImplTests
{

	private ClientPolicyDb mockClientPolicyDb = Mockito.mock(ClientPolicyDb.class);

	private ComplianceReportsServiceImpl implementation = new ComplianceReportsServiceImpl(mockClientPolicyDb);

	@Test
	public void testGetComplianceReport_CorrectMethodCallToClient() throws OperandoCommunicationException, UnableToGetDataException
	{
		setUpServices((PrivacyPolicy) null);

		String ospId = "B987";

		implementation.getComplianceReportForOsp(ospId);

		verify(mockClientPolicyDb).getPrivacyPolicyForOsp(ospId);
	}

	@Test
	public void testGetComplianceReport_PolicyFound_ReturnComplianceReportWithCorrectPolicy() throws OperandoCommunicationException, UnableToGetDataException
	{
		PrivacyPolicy policy = new PrivacyPolicy(null, null);

		setUpServices(policy);

		ComplianceReport report = implementation.getComplianceReportForOsp("B987");

		assertEquals("", policy, report.getPrivacyPolicy());
	}

	@Test
	public void testGetComplianceReport_PolicyNotFound_ReturnNull() throws OperandoCommunicationException, UnableToGetDataException
	{
		setUpServices(new OperandoCommunicationException(CommunicationError.REQUESTED_RESOURCE_NOT_FOUND));

		ComplianceReport report = implementation.getComplianceReportForOsp("B987");
		
		assertEquals(null, report);
	}

	@Test(expected = UnableToGetDataException.class)
	public void testGetComplianceReport_ErrorInCommunication_ThrowException() throws OperandoCommunicationException, UnableToGetDataException
	{
		setUpServices(new OperandoCommunicationException(CommunicationError.ERROR_FROM_OTHER_MODULE));

		implementation.getComplianceReportForOsp("B987");
	}

	private void setUpServices(PrivacyPolicy toReturn) throws OperandoCommunicationException
	{
		when(mockClientPolicyDb.getPrivacyPolicyForOsp(anyString())).thenReturn(toReturn);
	}

	private void setUpServices(OperandoCommunicationException ex) throws OperandoCommunicationException
	{
		when(mockClientPolicyDb.getPrivacyPolicyForOsp(anyString())).thenThrow(ex);
	}

}
