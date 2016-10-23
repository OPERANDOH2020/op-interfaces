package io.swagger.client.model;

import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;


/**
 * LogRequest
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaClientCodegen", date = "2016-06-28T09:19:14.539Z")
public class LogRequest   {
  

  /**
   * Source type from which comes the request.
   */
  public enum RequesterTypeEnum {
    PROCESS("PROCESS"),
    MODULE("MODULE");

    private String value;

    RequesterTypeEnum(String value) {
      this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(value);
    }
  }

  private RequesterTypeEnum requesterType = null;
  private String requesterId = null;

  /**
   * Priority level of the data to be logged.
   */
  public enum LogPriorityEnum {
    LOW("LOW"),
    NORMAL("NORMAL"),
    HIGH("HIGH"),
    CRITICAL("CRITICAL");

    private String value;

    LogPriorityEnum(String value) {
      this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(value);
    }
  }

  private LogPriorityEnum logPriority = null;

  /**
   * Type of the data to be logged.
   */
  public enum LogDataTypeEnum {
    INFO("INFO"),
    WARN("WARN"),
    ERROS("ERROS"),
    FATAL("FATAL");

    private String value;

    LogDataTypeEnum(String value) {
      this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(value);
    }
  }

  private LogDataTypeEnum logDataType = null;
  private String title = null;
  private String description = null;
  private List<String> keywords = new ArrayList<String>();

  
  /**
   * Source type from which comes the request.
   **/
  public LogRequest requesterType(RequesterTypeEnum requesterType) {
    this.requesterType = requesterType;
    return this;
  }
  
  @ApiModelProperty(example = "null", value = "Source type from which comes the request.")
  @JsonProperty("requesterType")
  public RequesterTypeEnum getRequesterType() {
    return requesterType;
  }
  public void setRequesterType(RequesterTypeEnum requesterType) {
    this.requesterType = requesterType;
  }


  /**
   * Id of the requester (e.g the id of an OSP).
   **/
  public LogRequest requesterId(String requesterId) {
    this.requesterId = requesterId;
    return this;
  }
  
  @ApiModelProperty(example = "null", value = "Id of the requester (e.g the id of an OSP).")
  @JsonProperty("requesterId")
  public String getRequesterId() {
    return requesterId;
  }
  public void setRequesterId(String requesterId) {
    this.requesterId = requesterId;
  }


  /**
   * Priority level of the data to be logged.
   **/
  public LogRequest logPriority(LogPriorityEnum logPriority) {
    this.logPriority = logPriority;
    return this;
  }
  
  @ApiModelProperty(example = "null", value = "Priority level of the data to be logged.")
  @JsonProperty("logPriority")
  public LogPriorityEnum getLogPriority() {
    return logPriority;
  }
  public void setLogPriority(LogPriorityEnum logPriority) {
    this.logPriority = logPriority;
  }


  /**
   * Type of the data to be logged.
   **/
  public LogRequest logDataType(LogDataTypeEnum logDataType) {
    this.logDataType = logDataType;
    return this;
  }
  
  @ApiModelProperty(example = "null", value = "Type of the data to be logged.")
  @JsonProperty("logDataType")
  public LogDataTypeEnum getLogDataType() {
    return logDataType;
  }
  public void setLogDataType(LogDataTypeEnum logDataType) {
    this.logDataType = logDataType;
  }


  /**
   * Subject of the event to be logged.
   **/
  public LogRequest title(String title) {
    this.title = title;
    return this;
  }
  
  @ApiModelProperty(example = "null", value = "Subject of the event to be logged.")
  @JsonProperty("title")
  public String getTitle() {
    return title;
  }
  public void setTitle(String title) {
    this.title = title;
  }


  /**
   * Description of the event to be logged.
   **/
  public LogRequest description(String description) {
    this.description = description;
    return this;
  }
  
  @ApiModelProperty(example = "null", value = "Description of the event to be logged.")
  @JsonProperty("description")
  public String getDescription() {
    return description;
  }
  public void setDescription(String description) {
    this.description = description;
  }


  /**
   * Array of keywords to facilitate search
   **/
  public LogRequest keywords(List<String> keywords) {
    this.keywords = keywords;
    return this;
  }
  
  @ApiModelProperty(example = "null", value = "Array of keywords to facilitate search")
  @JsonProperty("keywords")
  public List<String> getKeywords() {
    return keywords;
  }
  public void setKeywords(List<String> keywords) {
    this.keywords = keywords;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LogRequest logRequest = (LogRequest) o;
    return Objects.equals(this.requesterType, logRequest.requesterType) &&
        Objects.equals(this.requesterId, logRequest.requesterId) &&
        Objects.equals(this.logPriority, logRequest.logPriority) &&
        Objects.equals(this.logDataType, logRequest.logDataType) &&
        Objects.equals(this.title, logRequest.title) &&
        Objects.equals(this.description, logRequest.description) &&
        Objects.equals(this.keywords, logRequest.keywords);
  }

  @Override
  public int hashCode() {
    return Objects.hash(requesterType, requesterId, logPriority, logDataType, title, description, keywords);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LogRequest {\n");
    
    sb.append("    requesterType: ").append(toIndentedString(requesterType)).append("\n");
    sb.append("    requesterId: ").append(toIndentedString(requesterId)).append("\n");
    sb.append("    logPriority: ").append(toIndentedString(logPriority)).append("\n");
    sb.append("    logDataType: ").append(toIndentedString(logDataType)).append("\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    keywords: ").append(toIndentedString(keywords)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

