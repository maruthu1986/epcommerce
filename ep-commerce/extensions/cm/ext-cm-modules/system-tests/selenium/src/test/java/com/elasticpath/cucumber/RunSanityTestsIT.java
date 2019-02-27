package com.elasticpath.cucumber;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

/**
 * This class is used to run sanity tests.
 */
@RunWith(Cucumber.class)
@CucumberOptions(format = {"pretty", "html:target/cucumber-html-reports/sanity", "json:target/sanitytest.json"},
		tags = {"@sanity"},
		features = "src/test/resources/com.elasticpath.cucumber/sanity")
public class RunSanityTestsIT {
}
