package eu.operando.interfaces.oapi.impl;

import static org.junit.Assert.fail;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import eu.operando.OperandoCommunicationException;
import eu.operando.OperandoCommunicationException.CommunicationError;
import eu.operando.api.model.AnalyticsReport;
import eu.operando.moduleclients.ClientBigDataAnalytics;

@RunWith(MockitoJUnitRunner.class)
public class BigDataAnalyticsApiServiceImplTests
{
	@Mock
	private ClientBigDataAnalytics clientBigDataAnalytics;
	
	@InjectMocks
	private BigDataAnalyticsApiServiceImpl implementation;
	
	@Test
	public void testGetBdaReport_CorrectParameters() throws OperandoCommunicationException
	{
		String jobId = "C456";
		String userId = "D000";
		setUpServices(null);
		
		implementation.getBdaReport(jobId, userId);
		
		fail("class not ready for testing yet");
	}
	
	@Test
	public void testGetBdaReport_ReturnCorrectReportIfFound() throws OperandoCommunicationException
	{
		Object toReturn = null;
		setUpServices(toReturn);
		
		AnalyticsReport report = implementation.getBdaReport("C456", "D000");
		
		fail("class not ready for testing yet");
	}
	
	@Test
	public void testGetBdaReport_ReturnNotFoundExceptionIfReportNotFound()
	{
		setUpServices(new OperandoCommunicationException(CommunicationError.REQUESTED_RESOURCE_NOT_FOUND));
		
		try{
			implementation.getBdaReport("C456", "D000");
			fail("If the report cannot be found an exception should be returned");
		}
		catch(OperandoCommunicationException ex){
			assertEquals(CommunicationError.REQUESTED_RESOURCE_NOT_FOUND, ex.getCommunitcationError());
		}
	}
	
	@Test
	public void testGetBdaReport_ReturnInternalErrorExceptionIfCantGetReport()
	{
		setUpServices(new OperandoCommunicationException(CommunicationError.ERROR_FROM_OTHER_MODULE));
		
		try{
			implementation.getBdaReport("C456", "D000");
			fail("If the report cannot be retrieved an exception should be returned");
		}
		catch(OperandoCommunicationException ex){
			assertEquals(CommunicationError.ERROR_FROM_OTHER_MODULE, ex.getCommunitcationError());
		}
	}
	
	private void setUpServices(Object toReturn){
		
	}
	
	private void setUpServices(OperandoCommunicationException ex){

	}
}
