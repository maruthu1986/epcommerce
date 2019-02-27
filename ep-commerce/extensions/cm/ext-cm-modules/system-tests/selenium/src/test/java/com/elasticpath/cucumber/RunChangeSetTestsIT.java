package com.elasticpath.cucumber;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

/**
 * This class is used to run Cucumber Features test scenarios.
 */
@RunWith(Cucumber.class)
@CucumberOptions(format = {"pretty", "html:target/cucumber-html-reports/changeset", "json:target/changeset.json"},
		glue = {"classpath:com.elasticpath.cucumber", "classpath:com.elasticpath.jms.cucumber"},
		tags = {"@changeset"},
		features = "src/test/resources/com.elasticpath.cucumber/changeset")
public class RunChangeSetTestsIT {

}
