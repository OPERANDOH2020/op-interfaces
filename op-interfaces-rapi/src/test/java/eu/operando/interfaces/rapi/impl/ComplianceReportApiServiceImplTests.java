package eu.operando.interfaces.rapi.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import eu.operando.OperandoCommunicationException;
import eu.operando.OperandoCommunicationException.CommunicationError;
import eu.operando.api.model.ComplianceReport;
import eu.operando.api.model.PrivacyPolicy;
import eu.operando.moduleclients.ClientPolicyDb;

@RunWith(MockitoJUnitRunner.class)
public class ComplianceReportApiServiceImplTests {

	@Mock
	private ClientPolicyDb clientPolicyDb;
	
	@InjectMocks
	private ComplianceReportApiServiceImpl implementation;
	
	@Test
	public void testGetComplianceReport_CorrectParameters() throws OperandoCommunicationException{
		setUpServices((PrivacyPolicy) null);
		
		String ospId = "B987";
		
		implementation.getComplianceReportForOsp(ospId);
		
		verify(clientPolicyDb).getPrivacyPolicyForOsp(ospId);
	}
	
	@Test
	public void testGetComplianceReport_ReturnComplianceReportWithCorrectPolicyIfFound() throws OperandoCommunicationException{
		PrivacyPolicy policy = new PrivacyPolicy(null, null);
		
		setUpServices(policy);
		
		ComplianceReport report = implementation.getComplianceReportForOsp("B987");
		
		assertEquals("", policy, report.getPrivacyPolicy());
	}
	
	@Test
	public void testGetComplianceReport_ReturnNotFoundExceptionIfPolicyNotFound() throws OperandoCommunicationException{
		setUpServices(new OperandoCommunicationException(CommunicationError.REQUESTED_RESOURCE_NOT_FOUND));
		try{
			implementation.getComplianceReportForOsp("B987");
			fail("If no policy is found, an exception should be thrown");
		}
		catch (OperandoCommunicationException ex){
			assertEquals(CommunicationError.REQUESTED_RESOURCE_NOT_FOUND, ex.getCommunitcationError());
		}
	}
	
	@Test
	public void testGetComplianceReport_ReturnExceptionIfCantGetPolicy() throws OperandoCommunicationException{
		setUpServices(new OperandoCommunicationException(CommunicationError.ERROR_FROM_OTHER_MODULE));
		try{
			implementation.getComplianceReportForOsp("B987");
			fail("If other modules cannot be called, an exception should be thrown");
		}
		catch (OperandoCommunicationException ex){
			assertEquals(CommunicationError.ERROR_FROM_OTHER_MODULE, ex.getCommunitcationError());
		}
	}
	
	private void setUpServices(PrivacyPolicy toReturn) throws OperandoCommunicationException{
		when(clientPolicyDb.getPrivacyPolicyForOsp(anyString())).thenReturn(toReturn);
	}
	
	private void setUpServices(OperandoCommunicationException ex) throws OperandoCommunicationException{
		when(clientPolicyDb.getPrivacyPolicyForOsp(anyString())).thenThrow(ex);
	}
	
}
