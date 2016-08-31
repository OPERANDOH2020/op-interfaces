package eu.operando.interfaces.rapi.impl;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses
({
	RegulationsApiServiceImplTests.class,
	ReportsApiServiceImplTests.class,
	RegulatorApiClientTests.class
})
public class RegulatorApiTestSuite
{
	
}
