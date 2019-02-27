package com.elasticpath.cucumber;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

/**
 * This class is used to run Cucumber Features test scenarios.
 */
@RunWith(Cucumber.class)
@CucumberOptions(format = {"pretty", "html:target/cucumber-html-reports/priceList", "json:target/priceListCucumber.json"},
		tags = {"@smoketest", "@priceList", "~@notready"},
		features = "src/test/resources/com.elasticpath.cucumber/priceListManager")
public class RunPriceListManagerTestsIT {
}
