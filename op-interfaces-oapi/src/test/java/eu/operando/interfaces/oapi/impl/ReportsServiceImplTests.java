package eu.operando.interfaces.oapi.impl;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import javax.ws.rs.core.MultivaluedMap;

import org.glassfish.jersey.internal.util.collection.MultivaluedStringMap;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import eu.operando.OperandoCommunicationException;
import eu.operando.OperandoCommunicationException.CommunicationError;
import eu.operando.moduleclients.ClientReportGenerator;

@RunWith(MockitoJUnitRunner.class)
public class ReportsServiceImplTests
{
	// Variables to test.
	
	// Dummy variables.
	private static final String REPORT_FROM_CLIENT = "reportFromClient";
	private static final String REPORT_ID = "123";
	private static final String FORMAT = "pdf";
	private static final MultivaluedMap<String, String> PARAMETERS_OPTIONAL = new MultivaluedStringMap();

	static
	{
		PARAMETERS_OPTIONAL.putSingle("key", "value");
	}
	
	@Mock
	private ClientReportGenerator clientReportGenerator;

	@InjectMocks
	private ReportsServiceImpl implementation;

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	@Test
	public void testGetReport_ClientThrowsOperandoCommunicationExceptionError_SameExceptionThrown() throws OperandoCommunicationException
	{
		// Set up
		OperandoCommunicationException communicationException = new OperandoCommunicationException(CommunicationError.ERROR_FROM_OTHER_MODULE);
		when(clientReportGenerator.getReport(anyString(), anyString(), any(MultivaluedMap.class))).thenThrow(communicationException);

		exception.expect(OperandoCommunicationException.class);
		exception.expect(hasProperty("communitcationError", is(CommunicationError.ERROR_FROM_OTHER_MODULE)));
		
		// Exercise
		implementation.getReport(REPORT_ID, FORMAT, PARAMETERS_OPTIONAL);

		// Verify -- expect exception
	}

	@Test
	public void testGetReport_ClientThrowsOperandoCommunicationExceptionNotFound_SameExceptionThrown() throws OperandoCommunicationException
	{
		// Set up
		OperandoCommunicationException communicationException = new OperandoCommunicationException(CommunicationError.REQUESTED_RESOURCE_NOT_FOUND);
		when(clientReportGenerator.getReport(anyString(), anyString(), any(MultivaluedMap.class))).thenThrow(communicationException);

		exception.expect(OperandoCommunicationException.class);
		exception.expect(hasProperty("communitcationError", is(CommunicationError.REQUESTED_RESOURCE_NOT_FOUND)));
		
		// Exercise
		implementation.getReport(REPORT_ID, FORMAT, PARAMETERS_OPTIONAL);

		// Verify -- expect exception
	}

	@Test
	public void testGetReport_ReportGeneratorReturnsReport_ResponseContainsReturnedReport() throws OperandoCommunicationException
	{
		// Set up
		when(clientReportGenerator.getReport(anyString(), anyString(), any(MultivaluedMap.class))).thenReturn(REPORT_FROM_CLIENT);

		// Exercise
		String encodedReport = implementation.getReport(REPORT_ID, FORMAT, PARAMETERS_OPTIONAL);

		// Verify
		assertEquals("In the event of successful report retrieval, the RAPI should return the report it gets from elsewhere in the platform.",
				REPORT_FROM_CLIENT, encodedReport);
	}
}
