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
To report an issue, please use GitHub's built-in issue-tracking system for this repository, or send an email to bug-report@operando.eu.

## Contributing code
If you'd like to contribute code, we'd be happy to have your support, so please contact us at os-contributions@operando.eu! You can find some examples of how you might help us on the [contributions page](https://www.operando.eu) of our website.

N.B. The copyright for any code will be owned by the OPERANDOH2020 organisation, and can therefore be used by any of the partner organisations that make up the consortium in other applications.

## Functional Description of Modules
### B2C
#### Email Services (ES)
#### User Agent Middleware (UAM)
### G2C
#### Authentication API (AAPI)
The Authentication Service is the initial control point for security control, based on a ticketing system. As such, the Authentication Service can validate a ticket, and map users to specific roles, providing fine-grained role-based access control (RBAC) guaranteeing that only the agreed roles and/or users will have access to the user data.
The Authentication Service (AS) operates at the PA core container, which implements the business logic of the PA and the privacy services. It is the main component that responsible for the correct adherence of all aspects of the online authentication procedure. 
With the use of this component:
 * Users are able to use their real or substitute credentials gaining direct access into a service
 * OSPs and Regulators are also able to use their digital identities, which are properly validated
 * Users are able to interact with OPERANDO through the use of remote agents such as browser add-ons and mobile apps, which are properly authenticated from the AS component

The AS component provides the following core functionality:
* Validation of users with identity providers
* Authentication of users with the proper enforcement of world-wide accepted protocols (i.e. OAuth, OpenID, etc.)
* Management of user credentials and client applications

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
The source code of the AAPI module can be found at the official project GitHub here:
https://github.com/OPERANDOH2020/op-interfaces/tree/master/op-interfaces-aapi
In order to build the source code, the steps below should be followed:
* Checkout the project from Github.
* Navigate to the root folder of the project.
* Build the project using the maven command "mvn package".
The generated war(authentication.war) file is the one that will be deployed in the application server.
In order to run the aapi module, the generated war file (authentication.war) has to be copied under the deployment directory of the corresponding application server (e.g. the “webapps” folder in Tomcat AS). 
The module will start running as soon as the application server is restarted. 
While deploying the AAPI module, two configuration files should be properly adjusted. Particularly:
* aapi-config.properties: Contains information for the connection to CAS server and the LDAP directory.
* Log4j.properties: Contains information for the local logging component that is integrated in the AAPI module (using the log4j Apache Logging Services)

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
The AAPI module provides complete documentation of the available API through the implemented Swagger user interface. At the moment this interface can be accessible here:
http://aapi.integration.operando.esilab.org:8135/operando/interfaces/aapi/swagger-ui.html#/

AAPI contains two basic cores:
* User Controller is responsible for the user management (registration, deletion, modification and retrieval of a user). 
* Ticket Controller is responsible for all the authentication mechanisms developed in order to ensure the secure communication of the modules and specifically the authentication ticket generation and validation (TGT/ST generation and validation).


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
Users will be able to login by using both a user-friendly username and a unique identifier inside the Operando ecosystem.

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
