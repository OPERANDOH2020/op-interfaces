package eu.operando.interfaces.oapi;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import eu.operando.interfaces.oapi.impl.BigDataAnalyticsApiServiceImplTests;

@RunWith(Suite.class)
@Suite.SuiteClasses
({
	BigDataAnalyticsApiTests.class,
	BigDataAnalyticsApiServiceImplTests.class
})
public class BigDataAnalyticsTestSuite {

}
