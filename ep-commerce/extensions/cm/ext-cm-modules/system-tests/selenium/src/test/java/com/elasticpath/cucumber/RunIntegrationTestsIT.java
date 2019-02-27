package com.elasticpath.cucumber;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

/**
 * This class is used to run integration tests.
 */
@RunWith(Cucumber.class)
@CucumberOptions(format = {"pretty", "html:target/cucumber-html-reports/integrationTest", "json:target/integrationTest.json"},
		tags = {"@smoketest", "@integration"},
		features = "src/test/resources/com.elasticpath.cucumber/integrationTest")
public class RunIntegrationTestsIT {
}
