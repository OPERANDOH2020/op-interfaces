package eu.operando.interfaces.rapi;

import eu.operando.api.model.ComplianceReport;

public interface ComplianceReportApiService {
	public ComplianceReport getComplianceReport(String ospId);
}
