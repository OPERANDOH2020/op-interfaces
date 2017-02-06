package eu.operando.interfaces.rapi.impl;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import eu.operando.api.AuthenticationServiceImplTests;
import eu.operando.moduleclients.ClientAuthenticationApiOperandoServiceTests;
import eu.operando.moduleclients.ClientOspEnforcementTests;
import eu.operando.moduleclients.ClientPolicyComputationTests;
import eu.operando.moduleclients.ClientPolicyDbTests;
import eu.operando.moduleclients.ClientReportGeneratorTests;

@RunWith(Suite.class)
@Suite.SuiteClasses
({
	RegulationsApiServiceImplTests.class,
	ReportsApiServiceImplTests.class,
	ComplianceReportApiTests.class,
	ComplianceReportApiServiceImplTests.class,
	AuthenticationServiceImplTests.class,
	ClientAuthenticationApiOperandoServiceTests.class,
	ClientPolicyDbTests.class,
	ClientPolicyComputationTests.class,
	ClientOspEnforcementTests.class,
	ClientReportGeneratorTests.class
})
public class RegulatorApiTestSuite
{
	
}
