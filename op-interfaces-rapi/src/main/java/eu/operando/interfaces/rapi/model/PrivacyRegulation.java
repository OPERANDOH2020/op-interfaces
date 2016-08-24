package eu.operando.interfaces.rapi.model;

import java.util.Objects;

import javax.ws.rs.core.Response;

import org.apache.http.HttpException;

import com.fasterxml.jackson.annotation.JsonProperty;

import eu.operando.ClientOperandoModule;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * A privacy rule that reflects a given privacy legislation as described by a
 * particular set of laws in a given jurisdiction.
 **/

@ApiModel(description = "A privacy rule that reflects a given privacy legislation as described by a particular set of laws in a given jurisdiction. ")
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2016-07-12T14:06:26.001Z")
public class PrivacyRegulation extends DtoPrivacyRegulation
{
	private String regId = "";

	public PrivacyRegulation(String regId, String legislationSector, String privateInformationSource, PrivateInformationTypeEnum privateInformationType, String action,
			RequiredConsentEnum requiredConsent)
	{
		super(legislationSector, privateInformationSource, privateInformationType, action, requiredConsent);
		this.regId = regId;
	}

	/**
	 **/
	public PrivacyRegulation regId(String regId)
	{
		this.regId = regId;
		return this;
	}
	@ApiModelProperty(value = "")
	@JsonProperty("reg_id")
	public String getRegId()
	{
		return regId;
	}
	public void setRegId(String regId)
	{
		this.regId = regId;
	}

	/**
	 * Instantiates a new PrivacyRegulationInput with fields matching those of this object, and returns it. 
	 * @return
	 * 		a new PrivacyRegulationInput with fields matching those of this object
	 */
	public PrivacyRegulationInput getInputObject()
	{
		return new PrivacyRegulationInput(getLegislationSector(), getPrivateInformationSource(), getPrivateInformationType(), getAction(), getRequiredConsent());
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (o == null || getClass() != o.getClass())
		{
			return false;
		}
		PrivacyRegulation privacyRegulation = (PrivacyRegulation) o;
		return Objects.equals(regId, privacyRegulation.regId)
				&& Objects.equals(getLegislationSector(), privacyRegulation.getLegislationSector())
				&& Objects.equals(getPrivateInformationSource(), privacyRegulation.getPrivateInformationSource())
				&& Objects.equals(getPrivateInformationType(), privacyRegulation.getPrivateInformationType())
				&& Objects.equals(getAction(), privacyRegulation.getAction())
				&& Objects.equals(getRequiredConsent(), privacyRegulation.getRequiredConsent());
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(getRegId(), getLegislationSector(), getPrivateInformationSource(), getPrivateInformationType(), getAction(), getRequiredConsent());
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("class PrivacyRegulation {\n");

		sb.append("    regId: ").append(toIndentedString(regId)).append("\n");
		sb.append("    legislationSector: ").append(toIndentedString(getLegislationSector())).append("\n");
		sb.append("    privateInformationSource: ").append(toIndentedString(getPrivateInformationSource())).append("\n");
		sb.append("    privateInformationType: ").append(toIndentedString(getPrivateInformationType())).append("\n");
		sb.append("    action: ").append(toIndentedString(getAction())).append("\n");
		sb.append("    requiredConsent: ").append(toIndentedString(getRequiredConsent())).append("\n");
		sb.append("}");
		return sb.toString();
	}

	/**
	 * Convert the given object to string with each line indented by 4 spaces
	 * (except the first line).
	 */
	private String toIndentedString(Object o)
	{
		if (o == null)
		{
			return "null";
		}
		return o.toString().replace("\n", "\n    ");
	}
}
