# op-interfaces
For the modules in the interfaces container:

B2C:
* Email Services (ES)
* User Agent Middleware (UAM)

G2C:
* Authentication API (AAPI)
* Online Service Provider API (OSP API or OAPI)
* Regulator API (RAPI)

## Reporting an issue

## Contributing to the code

## Functional Description of Modules
### B2C
#### Email Services (ES)
#### User Agent Middleware (UAM)
### G2C
#### Authentication API (AAPI)
#### Online Service Provider API (OAPI)
The OAPI module facilitates OSP-PSP communication for additional features beyond the fundamental communication such as data access. It acts as an abstraction layer, which adds a level of separation between external entities and the PA internal services. It should delegate inbound requests from an OSP to the relevant core modules. If necessary in the future, it will also handle outbound communication from the PSP to the OSP.
In deployments where the PA and the OSP are tightly integrated, it will be possible to extend the OAPI to support bespoke workflows.

There are two workflows handled by the OAPI -- requests for reports, and requests for Big Data extracts. Each can be done according to the specifications described in the Swagger documentation.
In each workflow any incoming request is directed to the relevant interface (Reports, Big Data Analytics), which interprets the request. The JSON from the body is deserialised into a Java object, and the service ticket string – provided to the caller by the Authentication API (AAPI) – is read from a custom header.

#### Regulator API (RAPI)
The Regulator API (RAPI) is intended to facilitate compliance with privacy laws, and for regulators to audit and pro-actively supervise OSPs.
Via the RAPI, agencies will be able to submit new and updated regulations to the platform in order to trigger changes to UPPs for immediate compliance, and to trigger compliance checks against an OSP’s registered workflows. They will also be able to request reports detailing an OSP’s compliance with statements in their privacy policy, and with regulations which have been submitted via the RAPI at an earlier date.

There are two workflows handled by the RAPI – adding or updating a regulation, and requesting a compliance report. Either can be done according to the specifications described in the Swagger documentation.
In both workflows any incoming request is directed to the relevant interface (Regulations or Reports) by the Jersey library. The interface interprets the incoming HTTP request, any JSON from the body is deserialised into a Java object, and the service ticket string – provided to the caller by the Authentication API (AAPI) – is read from a custom header.

## Installation Instructions
### B2C
#### Email Services (ES)
#### User Agent Middleware (UAM)
### G2C
#### Authentication API (AAPI)
#### Online Service Provider API (OAPI)
#### Regulator API (RAPI)

## Usage Instructions
### B2C
#### Email Services (ES)
#### User Agent Middleware (UAM)
### G2C
#### Authentication API (AAPI)
#### Online Service Provider API (OAPI)
#### Regulator API (RAPI)

## Future Plans
### B2C
#### Email Services (ES)
#### User Agent Middleware (UAM)
### G2C
#### Authentication API (AAPI)
#### Online Service Provider API (OAPI)
#### Regulator API (RAPI)

## Dependencies
Dependency Name|Description|Link|Module|Test-only?
---------------|-----------|----|------|----------
Java|A general-purpose computer programming language|https://java.com/en/download/|RAPI, OAPI|
Maven|Tool for managing software dependencies and packaging software|https://maven.apache.org/|RAPI, OAPI|
Apache Tomcat|Web Container for Java Servlets|http://tomcat.apache.org/|RAPI, OAPI|
Jersey|Java library for writing REST-based web applications|https://jersey.java.net/|RAPI, OAPI|
Swagger Core|Java library for producing interactive API documentation directly from source code|https://github.com/swagger-api/swagger-core|RAPI, OAPI|
Gson|Java library for converting between Java objects and JSON|https://github.com/google/gson|RAPI, OAPI|
JUnit|Java library for writing and executing automated unit tests|http://junit.org/junit4/|RAPI, OAPI|Y
Hamcrest|Java library for wording unit tests more naturally|http://hamcrest.org/JavaHamcrest/|RAPI, OAPI|Y
Wiremock|Java library for testing interactions via HTTP|http://wiremock.org/|RAPI, OAPI|Y
Mockito|Java library for mocking and stubbing out software components depended upon by the system under test|https://github.com/mockito/mockito|RAPI, OAPI|Y
