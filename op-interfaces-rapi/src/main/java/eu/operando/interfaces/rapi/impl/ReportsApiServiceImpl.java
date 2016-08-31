package eu.operando.interfaces.rapi.impl;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import eu.operando.OperandoCommunicationException;
import eu.operando.OperandoCommunicationException.CommunicationError;
import eu.operando.ReportOperando;
import eu.operando.interfaces.rapi.ReportsApiService;

@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2016-07-12T14:06:26.001Z")
public class ReportsApiServiceImpl extends ReportsApiService
{
	private RegulatorApiClient client = new RegulatorApiClient();

	@Override
	public Response reportsReportIdGet(String serviceTicket, String reportId, String format, MultivaluedMap<String, String> parametersOptional)
	{
		Status statusToReturn = null;
		ReportOperando reportToReturn = null;

		boolean ospAuthenticated = client.isOspAuthenticated(serviceTicket);
		if (ospAuthenticated)
		{
			try
			{
				statusToReturn = Status.OK;
				reportToReturn = client.getReport(reportId, format, parametersOptional);
			}
			catch (OperandoCommunicationException ex)
			{
				statusToReturn = determineStatusToReturnFromOperandoCommunicationException(ex);
			}
		}
		else
		{
			statusToReturn = Status.UNAUTHORIZED;
		}

		Entity<ReportOperando> entityToReturn = Entity.json(reportToReturn);
		return Response.status(statusToReturn)
			.entity(entityToReturn)
			.build();
	}

	/**
	 * Takes in an OperandoCommunicationException Exception, and determines what status the RAPI should return based on this exception.
	 * @param e
	 * 	An HTTP Exception detailing an error returned when trying to get the requested report.
	 * @return
	 * 	The status that should be returned to the caller of the RAPI.
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
