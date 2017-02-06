package eu.operando.interfaces.rapi.impl;

import eu.operando.OperandoCommunicationException;
import eu.operando.api.model.ComplianceReport;
import eu.operando.api.model.PrivacyPolicy;
import eu.operando.interfaces.rapi.ComplianceReportApiService;
import eu.operando.moduleclients.ClientPolicyDb;

public class ComplianceReportApiServiceImpl implements ComplianceReportApiService {

	private ClientPolicyDb clientPolicyDb = null;
	
	public ComplianceReportApiServiceImpl(ClientPolicyDb clientPolicyDb){
		this.clientPolicyDb = clientPolicyDb;
	}
	
	@Override
	public ComplianceReport getComplianceReportForOsp(String ospId) throws OperandoCommunicationException{
		PrivacyPolicy policy = clientPolicyDb.getPrivacyPolicyForOsp(ospId);
		return new ComplianceReport(policy);
	}

}
