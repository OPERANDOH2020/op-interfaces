package eu.operando.interfaces.oapi.impl;

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
		setUpServices((AnalyticsReport) null);
		
		implementation.getBdaReport(jobId, userId);
		
		verify(clientBigDataAnalytics).getBdaReport(jobId, userId);
	}
	
	@Test
	public void testGetBdaReport_ReturnCorrectReportIfFound() throws OperandoCommunicationException
	{
		AnalyticsReport toReturn = new AnalyticsReport("2", "Report", "a report", "CgoKCgoKCgoKCgo8IURPQ1RZUEUg");
		setUpServices(toReturn);
		
		AnalyticsReport report = implementation.getBdaReport("C456", "D000");
		
		assertEquals(toReturn, report);
	}
	
	@Test
	public void testGetBdaReport_ReturnNotFoundExceptionIfReportNotFound() throws OperandoCommunicationException
	{
		setUpServices(CommunicationError.REQUESTED_RESOURCE_NOT_FOUND);
		
		try{
			implementation.getBdaReport("C456", "D000");
			fail("If the report cannot be found an exception should be returned");
		}
		catch(OperandoCommunicationException ex){
			assertEquals(CommunicationError.REQUESTED_RESOURCE_NOT_FOUND, ex.getCommunitcationError());
		}
	}
	
	@Test
	public void testGetBdaReport_ReturnInternalErrorExceptionIfCantGetReport() throws OperandoCommunicationException
	{
		setUpServices(CommunicationError.ERROR_FROM_OTHER_MODULE);
		
		try{
			implementation.getBdaReport("C456", "D000");
			fail("If the report cannot be retrieved an exception should be returned");
		}
		catch(OperandoCommunicationException ex){
			assertEquals(CommunicationError.ERROR_FROM_OTHER_MODULE, ex.getCommunitcationError());
		}
	}
	
	private void setUpServices(AnalyticsReport toReturn) throws OperandoCommunicationException{
		when(clientBigDataAnalytics.getBdaReport(anyString(), anyString())).thenReturn(toReturn);
	}
	
	private void setUpServices(CommunicationError err) throws OperandoCommunicationException{
		when(clientBigDataAnalytics.getBdaReport(anyString(), anyString())).thenThrow(new OperandoCommunicationException(err));
	}
}
