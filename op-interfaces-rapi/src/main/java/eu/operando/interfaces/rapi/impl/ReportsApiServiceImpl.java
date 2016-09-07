package eu.operando.interfaces.rapi.impl;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import eu.operando.OperandoCommunicationException;
import eu.operando.Utils;
import eu.operando.OperandoCommunicationException.CommunicationError;
import eu.operando.api.model.ReportOperando;
import eu.operando.interfaces.rapi.ReportsApiService;
import eu.operando.moduleclients.ClientAuthenticationService;
import eu.operando.moduleclients.ClientOspEnforcement;
import eu.operando.moduleclients.ClientPolicyComputation;
import eu.operando.moduleclients.ClientPolicyDb;
import eu.operando.moduleclients.ClientReportGenerator;

@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2016-07-12T14:06:26.001Z")
public class ReportsApiServiceImpl extends ReportsApiService
{
	// Location of properties file.
	private static final String PROPERTIES_FILE_RAPI = "config.properties";
	
	// Properties file property names.
	private static final String PROPERTY_NAME_ORIGIN_AUTHENTICATION_API = "originAuthenticationApi";
	private static final String PROPERTY_NAME_ORIGIN_REPORT_GENERATOR = "originReportGenerator";
	
	// Properties file property values.
	private static final String ORIGIN_AUTHENTICATION_API = Utils.loadPropertyString(PROPERTIES_FILE_RAPI, PROPERTY_NAME_ORIGIN_AUTHENTICATION_API);
	private static final String ORIGIN_REPORT_GENERATOR = Utils.loadPropertyString(PROPERTIES_FILE_RAPI, PROPERTY_NAME_ORIGIN_REPORT_GENERATOR);
	
	private ClientAuthenticationService clientAuthenticationService = new ClientAuthenticationService(ORIGIN_AUTHENTICATION_API);
	private ClientReportGenerator clientReportGenerator = new ClientReportGenerator(ORIGIN_REPORT_GENERATOR);

	@Override
	public Response reportsReportIdGet(String serviceTicket, String reportId, String format, MultivaluedMap<String, String> parametersOptional)
	{
		Status statusToReturn = null;
		ReportOperando reportToReturn = null;

		boolean ospAuthenticated = clientAuthenticationService.isOspAuthenticated(serviceTicket);
		if (ospAuthenticated)
		{
			try
			{
				statusToReturn = Status.OK;
				reportToReturn = clientReportGenerator.getReport(reportId, format, parametersOptional);
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
