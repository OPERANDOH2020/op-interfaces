# op-interfaces
For the modules in the interfaces container:

B2C:
* Email Services (ES)
* User Agent Middleware (UAM)

G2C:
* Authentication API (AAPI)
* Online Service Provider API (OSP API or OAPI)
* Regulator API (RAPI)

# Dependencies
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
