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

import eu.operando.OperandoAuthenticationException;
import eu.operando.OperandoCommunicationException;
import eu.operando.OperandoCommunicationException.CommunicationError;
import eu.operando.UnableToGetDataException;
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
	public void testGetBdaReport_CorrectParameters() throws OperandoCommunicationException, UnableToGetDataException, OperandoAuthenticationException
	{
		String jobId = "C456";
		String userId = "D000";
		setUpServices((AnalyticsReport) null);
		
		implementation.getBdaReport(jobId, userId);
		
		verify(clientBigDataAnalytics).getBdaReport(jobId, userId);
	}
	
	@Test
	public void testGetBdaReport_ReturnCorrectReportIfFound() throws UnableToGetDataException, OperandoCommunicationException, OperandoAuthenticationException
	{
		AnalyticsReport toReturn = new AnalyticsReport("2", "Report", "a report", "CgoKCgoKCgoKCgo8IURPQ1RZUEUg");
		setUpServices(toReturn);
		
		AnalyticsReport report = implementation.getBdaReport("C456", "D000");
		
		assertEquals(toReturn, report);
	}
	
	@Test
	public void testGetBdaReport_ReturnNullIfReportNotFound() throws UnableToGetDataException, OperandoCommunicationException, OperandoAuthenticationException
	{
		setUpServices(CommunicationError.REQUESTED_RESOURCE_NOT_FOUND);
		
		AnalyticsReport report = implementation.getBdaReport("C456", "D000");
		
		assertEquals(null, report);

	}
	
	@Test(expected = UnableToGetDataException.class)
	public void testGetBdaReport_ReturnInternalErrorExceptionIfCantGetReport() throws OperandoCommunicationException, UnableToGetDataException, OperandoAuthenticationException
	{
		setUpServices(CommunicationError.ERROR_FROM_OTHER_MODULE);
		
		implementation.getBdaReport("C456", "D000");
		fail("If the report cannot be retrieved an exception should be returned");


	}
	
	private void setUpServices(AnalyticsReport toReturn) throws OperandoCommunicationException, OperandoAuthenticationException{
		when(clientBigDataAnalytics.getBdaReport(anyString(), anyString())).thenReturn(toReturn);
	}
	
	private void setUpServices(CommunicationError err) throws OperandoCommunicationException, OperandoAuthenticationException{
		when(clientBigDataAnalytics.getBdaReport(anyString(), anyString())).thenThrow(new OperandoCommunicationException(err));
	}
}
