/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.uat.email.cmuser;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

/**
 * This class bootstraps Cucumber.
 * <p/>
 * This will run all Cucumber features found on the classpath.
 */
@RunWith(Cucumber.class)
@CucumberOptions(format = { "pretty", "html:target/cucumber", "json:target/cucumber.json" },
		features = {"src/test/cucumber/com/elasticpath/uat/email"}, glue = "com.elasticpath.uat")
public class RunCucumberIT {

}