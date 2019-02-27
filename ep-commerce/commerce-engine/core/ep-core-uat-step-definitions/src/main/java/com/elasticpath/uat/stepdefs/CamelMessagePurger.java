/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.uat.stepdefs;

import java.util.List;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import org.apache.camel.CamelContext;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.messaging.camel.test.support.CamelContextMessagePurger;

/**
 * Responsible for purging messages created within the UAT test context.
 */
public class CamelMessagePurger {

	@Autowired(required = false)
	private List<CamelContext> contexts;

	@Autowired
	private CamelContextMessagePurger camelContextMessagePurger;

	@After
	@Before
	public void drainCamelEndpoints() throws Exception {
		if (contexts != null) {
			for (final CamelContext context : contexts) {
				camelContextMessagePurger.purgeMessages(context);
			}
		}
	}

}
