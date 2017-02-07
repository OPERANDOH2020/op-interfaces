package eu.operando.interfaces.oapi.impl;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import eu.operando.api.AuthenticationServiceImplTests;

@RunWith(Suite.class)
@Suite.SuiteClasses
({
	BigDataAnalyticsApiTests.class,
	BigDataAnalyticsApiServiceImplTests.class
})
public class BigDataAnalyticsTestSuite {

}
