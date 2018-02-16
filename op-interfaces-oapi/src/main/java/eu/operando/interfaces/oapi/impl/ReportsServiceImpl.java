package eu.operando.interfaces.oapi.impl;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import eu.operando.OperandoCommunicationException;
import eu.operando.OperandoCommunicationException.CommunicationError;
import eu.operando.interfaces.oapi.ReportsService;
import eu.operando.moduleclients.ClientReportGenerator;

@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2016-07-12T14:06:26.001Z")
public class ReportsServiceImpl extends ReportsService
{
	private ClientReportGenerator clientReportGenerator;

	public ReportsServiceImpl(ClientReportGenerator clientReportGenerator)
	{
		this.clientReportGenerator = clientReportGenerator;
	}

	@Override
	public Response getReport(String reportId, String format, MultivaluedMap<String, String> parametersOptional)
	{
		Status statusToReturn = null;
		String encodedReportToReturn = "";

		try
		{
			encodedReportToReturn = clientReportGenerator.getReport(reportId, format, parametersOptional);
			statusToReturn = Status.OK;
		}
		catch (OperandoCommunicationException ex)
		{
			ex.printStackTrace();
			statusToReturn = determineStatusToReturnFromOperandoCommunicationException(ex);
		}

		return Response.status(statusToReturn)
			.entity(encodedReportToReturn)
			.build();
	}

	/**
	 * Takes in an OperandoCommunicationException Exception, and determines what status the RAPI should return based on this exception.
	 * 
	 * @param e
	 *        An HTTP Exception detailing an error returned when trying to get the requested report.
	 * @return The status that should be returned to the caller of the RAPI.
	 */
	private Status determineStatusToReturnFromOperandoCommunicationException(OperandoCommunicationException e)
	{
		Status statusToReturn;
		CommunicationError error = e.getCommunitcationError();
		if (error == CommunicationError.REQUESTED_RESOURCE_NOT_FOUND)
		{
			statusToReturn = Status.NOT_FOUND;
		}
		else
		{
			statusToReturn = Status.SERVICE_UNAVAILABLE;
			e.printStackTrace();
		}
		return statusToReturn;
	}
}
