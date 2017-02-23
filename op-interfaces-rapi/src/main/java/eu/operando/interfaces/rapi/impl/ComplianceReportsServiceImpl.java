package eu.operando.interfaces.rapi.impl;

import eu.operando.OperandoCommunicationException;
import eu.operando.UnableToGetDataException;
import eu.operando.OperandoCommunicationException.CommunicationError;
import eu.operando.api.model.ComplianceReport;
import eu.operando.api.model.PrivacyPolicy;
import eu.operando.interfaces.rapi.ComplianceReportsService;
import eu.operando.moduleclients.ClientPolicyDb;

public class ComplianceReportsServiceImpl implements ComplianceReportsService
{

	private ClientPolicyDb clientPolicyDb = null;

	public ComplianceReportsServiceImpl(ClientPolicyDb clientPolicyDb)
	{
		this.clientPolicyDb = clientPolicyDb;
	}

	/**
	 * Generates and returns the compliance report for this OSP.
	 * 
	 * Returns null if details for the OSP cannot be found.
	 * 
	 * Throws an <code>UnableToGetDataException</code> if there is some problem in getting the necessary data.
	 */
	@Override
	public ComplianceReport getComplianceReportForOsp(String ospId) throws UnableToGetDataException
	{
		ComplianceReport complianceReport = null;
		PrivacyPolicy policy = getPolicyFromPdb(ospId);

		if (policy != null)
		{
			complianceReport = new ComplianceReport(policy);
		}
		
		return complianceReport;
	}

	private PrivacyPolicy getPolicyFromPdb(String ospId) throws UnableToGetDataException
	{
		PrivacyPolicy policy = null;

		try
		{
			policy = clientPolicyDb.getPrivacyPolicyForOsp(ospId);
		}
		catch (OperandoCommunicationException ex)
		{
			ex.printStackTrace();
			CommunicationError communitcationError = ex.getCommunitcationError();
			if (!communitcationError.equals(CommunicationError.REQUESTED_RESOURCE_NOT_FOUND))
			{
				throw new UnableToGetDataException(ex);
			}
		}
		
		return policy;
	}

}
