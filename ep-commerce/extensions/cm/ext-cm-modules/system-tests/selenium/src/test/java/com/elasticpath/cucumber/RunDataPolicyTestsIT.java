package com.elasticpath.cucumber;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

/**
 * This class is used to run Cucumber Features test scenarios.
 */
@RunWith(Cucumber.class)
@CucumberOptions(format = {"pretty", "html:target/cucumber-html-reports/dataPolicy", "json:target/dataPolicy.json"},
		tags = {"@smoketest", "@datapolicy"},
		features = "src/test/resources/com.elasticpath.cucumber/dataPolicy")
public class RunDataPolicyTestsIT {
}
