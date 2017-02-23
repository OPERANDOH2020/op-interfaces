package eu.operando.interfaces.rapi;

import eu.operando.UnableToGetDataException;
import eu.operando.api.model.ComplianceReport;

public interface ComplianceReportsService
{
	public ComplianceReport getComplianceReportForOsp(String ospId) throws UnableToGetDataException;
}
