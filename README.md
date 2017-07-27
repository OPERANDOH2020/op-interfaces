# op-interfaces
For the modules in the interfaces container:

B2C:
* Email Services (ES)
* User Agent Middleware (UAM)

G2C:
* Authentication API (AAPI)
* Online Service Provider API (OSP API or OAPI)
* Regulator API (RAPI)

You can find a description of OPERANDO's architecture on the [project website](https://www.operando.eu). You can find detailed specifications and descriptions of each of the modules in this repository in D5.5 (to be relased in October 2017) of [OPERANDO's public deliverables](https://www.operando.eu/servizi/moduli/moduli_fase01.aspx?mp=1&fn=6&Campo_78=&Campo_126=68&AggiornaDB=search&moduli1379178994=&__VIEWSTATEGENERATOR=D6660DC7&__EVENTVALIDATION=/wEWCAKInYjvBwK46/eoCgLW6PifAQLM6NSfAQLP6LicAQLM6NifAQLPm7uVCQKtvouLDQGIwuPU0XcXVk7W8FmpEwz15iKL).

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
To compile and package the source code into a .war file:
 * Download and install Java from https://java.com/en/download/
 * Download and install Maven from https://maven.apache.org/download.cgi
 * Navigate to a directory containing both the “op-interfaces” and the “op-other” repositories. This top-level directory should also contain a “pom.xml” file (see https://maven.apache.org/pom.html for reference), whose modules tag contains the content below .
 * Run “mvn clean package” – Maven will then download any dependencies it needs, and then compile the dependencies in ./op-other, before compiling the .war file from the code in ./op-interfaces/op-interfaces-oapi
 * Once this is complete, you can find the .war file at ./op-interfaces/op-interfaces-rapi/target/operando#interfaces#oapi.war

```xml
<modules>
	<module>op-other/test-dependencies</module>
	<module>op-other/op-other-common-java</module>
	<module>op-interfaces/op-interfaces-oapi</module>
</modules>
```

#### Regulator API (RAPI)
To compile and package the source code into a .war file:
 * Download and install Java from https://java.com/en/download/
 * Download and install Maven from https://maven.apache.org/download.cgi
 * Navigate to a directory containing both the “op-interfaces” and the “op-other” repositories. This top-level directory should also contain a “pom.xml” file (see https://maven.apache.org/pom.html for reference), whose modules tag contains the content below .
 * Run “mvn clean package” – Maven will then download any dependencies it needs, and then compile the dependencies in ./op-other, before compiling the .war file from the code in ./op-interfaces/op-interfaces-rapi
 * Once this is complete, you can find the .war file at ./op-interfaces/op-interfaces-rapi/target/operando#interfaces#rapi.war

```xml
<modules>
	<module>op-other/test-dependencies</module>
	<module>op-other/op-other-common-java</module>
	<module>op-interfaces/op-interfaces-rapi</module>
</modules>
```

## Usage Instructions
### B2C
#### Email Services (ES)
#### User Agent Middleware (UAM)
### G2C
#### Authentication API (AAPI)

#### Online Service Provider API (OAPI)
After installation, you can run the OAPI as follows:
 * Download and install Apache Tomcat from http://tomcat.apache.org/download-80.cgi
 * Copy operando#interfaces#oapi.war into Tomcat’s “webapps” directory
 * Run Tomcat, following the instructions at http://tomcat.apache.org/tomcat-8.0-doc/setup.html

The OAPI is a web based API, and can be interacted with via HTTP requests as described in the Swagger documentation. The address and port depend on server setup. Otherwise, the module can be accessed at **`[address]:[port] /operando/interfaces/oapi/osp`** with the HTTP method and additional path sections on the URL defining which part of the API is called. Each method requires a service ticket passed in as an HTTP header labelled “service-ticket”. The service ticket can be retrieved from the AAPI.

**`GET /reports/{report_id}?format={format}`**
This endpoint returns the report with ID described in the path in the format specified in the path. When the API receives this call, the request is delegated to the RG to gather the relevant data. The response is interpreted and an appropriate response is returned to the caller.

**`GET /bda/jobs/{job-id}/reports/latest`**
This endpoint returns a report generated by the BDA as part of the job specified in the path. When the API receives this call, the request is delegated to the BDA to gather the relevant data. The response is interpreted and an appropriate response is returned to the caller.

#### Regulator API (RAPI)
After installation, you can run the RAPI as follows:
 * Download and install Apache Tomcat from http://tomcat.apache.org/download-80.cgi
 * Copy operando#interfaces#rapi.war into Tomcat’s “webapps” directory
 * Run Tomcat, following the instructions at http://tomcat.apache.org/tomcat-8.0-doc/setup.html

The RAPI is a web based API, and can be interacted with via HTTP requests as described in the Swagger documentation.  The address and port depend on server setup. Otherwise, the module can be accessed at **`[address]:[port] /operando/interfaces/rapi/regulator`** with the HTTP method and additional path sections on the URL defining which part of the API is called. Each method requires a service ticket passed in as an HTTP header labelled “service-ticket”. The service ticket can be retrieved from the AAPI.

**`POST /regulations and PUT /regulations/{reg-id}`**
These endpoints are used to insert regulations into the system and update existing regulations, respectively. The regulation to store must be included as part of the HTTP body, and the RAPI processes this new regulation and forwards it to the PDB, PC and OSE modules.

**`GET /osps/{osp-id}/compliance-report`**
This endpoint returns a compliance report for the OSP specified in the path. When the API receives this call, a request is sent to the PDB to gather the relevant data. This data is then accumulated into a compliance report, and returned to the caller.

## Future Plans
### B2C
#### Email Services (ES)
#### User Agent Middleware (UAM)
### G2C
#### Authentication API (AAPI)

#### Online Service Provider API (OAPI)
Interactive Swagger API documentation will be added to the module, to make understanding the interfaces easier.

#### Regulator API (RAPI)
Interactive Swagger API documentation will be added to the module, to make understanding the interfaces easier.

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
