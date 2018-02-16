package eu.operando.interfaces.rapi.impl;

import eu.operando.OperandoCommunicationException;
import eu.operando.api.model.PrivacyRegulation;
import eu.operando.api.model.PrivacyRegulationInput;
import eu.operando.interfaces.rapi.RegulationsApiService;
import eu.operando.moduleclients.ClientOspEnforcement;
import eu.operando.moduleclients.ClientPolicyComputation;
import eu.operando.moduleclients.ClientPolicyDb;

@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2016-07-12T14:06:26.001Z")
public class RegulationsApiServiceImpl implements RegulationsApiService
{	
	private ClientPolicyDb clientPolicyDb = null;
	private ClientPolicyComputation clientPolicyComputation = null;
	private ClientOspEnforcement clientOspEnforcement = null;

	public RegulationsApiServiceImpl(ClientPolicyDb clientPolicyDb,
			ClientPolicyComputation clientPolicyComputation, ClientOspEnforcement clientOspEnforcement)
	{
		this.clientPolicyDb = clientPolicyDb;
		this.clientPolicyComputation = clientPolicyComputation;
		this.clientOspEnforcement = clientOspEnforcement;
	}

	@Override
	public boolean processNewRegulation(PrivacyRegulationInput regulation)
	{
		return forwardRegulationToInterestedModules(regulation, null);
	}

	@Override
	public boolean processExistingRegulation(PrivacyRegulationInput regulation, String regId)
	{
		return forwardRegulationToInterestedModules(regulation, regId);
	}

	private boolean forwardRegulationToInterestedModules(PrivacyRegulationInput regulation, String regId)
	{		
		// Keep track of the status of the requests.
		boolean successfulRequestToPdb = false;
		boolean successfulRequestToPc = false;
		boolean successfulRequestToOse = false;

		// Send the regulation to the PDB.
		PrivacyRegulation regulationFromPolicyDb = sendRegulationToPdb(regulation, regId);

		successfulRequestToPdb = regulationFromPolicyDb != null;
		if (successfulRequestToPdb)
		{
			boolean newRegulation = regId == null;
			if (newRegulation)
			{
				successfulRequestToPc = clientPolicyComputation.sendNewRegulationToPolicyComputation(regulationFromPolicyDb);
				successfulRequestToOse = clientOspEnforcement.sendNewRegulationToOspEnforcement(regulationFromPolicyDb);
			}
			else
			{
				successfulRequestToPc = clientPolicyComputation.sendExistingRegulationToPolicyComputation(regulationFromPolicyDb);
				successfulRequestToOse = clientOspEnforcement.sendExistingRegulationToOspEnforcement(regulationFromPolicyDb);
			}
		}

		// Return whether all requests were successful.
		return successfulRequestToPdb && successfulRequestToPc && successfulRequestToOse;
	}

	/**
	 * Send the regulation to the PDB and return the privacy regulation in the response body (or null if this is not possible).
	 * 
	 * @param regulationBody
	 *        The regulation to send.
	 * @param regId
	 *        the ID of the regulation. Should be null if the regulation doesn't have an ID.
	 * @return the privacy regulation in the response body, if possible.
	 */
	private PrivacyRegulation sendRegulationToPdb(PrivacyRegulationInput regulation, String regId)
	{
		PrivacyRegulation regulationFromPolicyDb = null;

		try
		{
			boolean newRegulation = regId == null;
			if (newRegulation)
			{
				regulationFromPolicyDb = clientPolicyDb.createNewRegulationOnPolicyDb(regulation);
			}
			else
			{
				regulationFromPolicyDb = clientPolicyDb.updateExistingRegulationOnPolicyDb(regId, regulation);
			}
		}
		catch (OperandoCommunicationException e)
		{
			e.printStackTrace();
		}

		return regulationFromPolicyDb;
	}
}