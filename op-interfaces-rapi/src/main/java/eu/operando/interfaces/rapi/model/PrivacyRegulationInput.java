package eu.operando.interfaces.rapi.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public final class PrivacyRegulationInput extends DtoPrivacyRegulation
{
	/**
	 * Zero-argument constructor for JAX-B
	 */
	public PrivacyRegulationInput()
	{
		super();
	}
	
	public PrivacyRegulationInput(String legislationSector, String privateInformationSource, PrivateInformationTypeEnum privateInformationType, String action,
			RequiredConsentEnum requiredConsent)
	{
		super(legislationSector, privateInformationSource, privateInformationType, action, requiredConsent);
	}
}
