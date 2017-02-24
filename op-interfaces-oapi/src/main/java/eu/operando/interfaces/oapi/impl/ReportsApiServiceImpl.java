package eu.operando.interfaces.oapi.impl;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import eu.operando.OperandoCommunicationException;
import eu.operando.OperandoCommunicationException.CommunicationError;
import eu.operando.interfaces.oapi.ReportsApiService;
import eu.operando.moduleclients.ClientAuthenticationApiOperandoService;
import eu.operando.moduleclients.ClientReportGenerator;

@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2016-07-12T14:06:26.001Z")
public class ReportsApiServiceImpl extends ReportsApiService
{
	private ClientAuthenticationApiOperandoService clientAuthenticationService = null;
	private ClientReportGenerator clientReportGenerator = null;
	private static final String SERVICE_ID_GET_REPORT = "/operando/webui/reports/";

	public ReportsApiServiceImpl(ClientAuthenticationApiOperandoService clientAuthenticationService, ClientReportGenerator clientReportGenerator)
	{
		this.clientAuthenticationService = clientAuthenticationService;
		this.clientReportGenerator = clientReportGenerator;
	}

	@Override
	public Response reportsGetReport(String serviceTicket, String reportId, String format, MultivaluedMap<String, String> parametersOptional)
	{
		Status statusToReturn = null;
		String encodeReportToReturn = "";

		try
		{
			boolean ospAuthenticated = clientAuthenticationService.isOspAuthenticatedForRequestedService(serviceTicket, SERVICE_ID_GET_REPORT);
			if (ospAuthenticated)
			{
				encodeReportToReturn = clientReportGenerator.getReport(reportId, format, parametersOptional);
				statusToReturn = Status.OK;
			}
			else
			{
				statusToReturn = Status.UNAUTHORIZED;
			}
		}
		catch (OperandoCommunicationException ex)
		{
			ex.printStackTrace();
			statusToReturn = determineStatusToReturnFromOperandoCommunicationException(ex);
		}

		return Response.status(statusToReturn)
			.entity(encodeReportToReturn)
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
