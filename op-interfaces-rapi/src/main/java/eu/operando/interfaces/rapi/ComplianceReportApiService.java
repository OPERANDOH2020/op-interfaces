package eu.operando.interfaces.rapi;

import javax.ws.rs.core.Response;

public interface ComplianceReportApiService {
	public Response ComplianceReportGet(String serviceTicket, String ospId);
}
