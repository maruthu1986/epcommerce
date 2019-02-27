/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.messaging.camel.itest;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;

import java.util.Map;
import java.util.UUID;

import com.google.common.collect.ImmutableMap;
import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.messaging.camel.SettingValueBackedPredicate;
import com.elasticpath.settings.SettingsReader;
import com.elasticpath.settings.SettingsService;
import com.elasticpath.settings.domain.SettingDefinition;
import com.elasticpath.settings.domain.SettingMetadata;
import com.elasticpath.settings.domain.SettingValue;
import com.elasticpath.test.integration.junit.DatabaseHandlingTestExecutionListener;
import com.elasticpath.test.support.junit.JmsRegistrationTestExecutionListener;

/**
 * Functional test class for {@link com.elasticpath.messaging.camel.SettingValueBackedPredicate} that verifies that uncached settings are refreshed
 * on each invocation of the route.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/integration-context.xml")
@TestExecutionListeners({
		JmsRegistrationTestExecutionListener.class,
		DatabaseHandlingTestExecutionListener.class,
		DependencyInjectionTestExecutionListener.class,
		DirtiesContextTestExecutionListener.class,
		TransactionalTestExecutionListener.class
})
public class SettingValueBackedPredicateITest extends CamelTestSupport {

	private static final String SETTING_PATH = "COMMERCE/ITEST/SETTINGS/%/sampleBooleanSetting";

	@Autowired
	@Qualifier("ep-messaging-camel-itest")
	private CamelContext context;

	@Autowired
	private BeanFactory beanFactory;

	@Autowired
	private SettingsService settingsService;

	@Autowired
	private SettingsReader settingsReader;

	@Test
	public void verifyPredicateRespectsDefaultSettingValue() throws Exception {
		final Endpoint incomingEndpoint = context.getEndpoint("direct:incoming." + UUID.randomUUID());
		final MockEndpoint outgoingEndpoint = context.getEndpoint("mock:outgoing."  + UUID.randomUUID(), MockEndpoint.class);

		// Given the default value of the setting is false
		final SettingDefinition settingDefinition = createSettingDefinitionWithDefaultValueOf(false);
		final SettingValueBackedPredicate predicate = createSettingValueBackedPredicateForSetting(settingDefinition);

		// And a route that forward messages when the setting value is true
		context.addRoutes(new PredicateRoute(incomingEndpoint, outgoingEndpoint, predicate));

		// When I send a message
		context.createProducerTemplate().sendBody(incomingEndpoint, "Message Body");

		// Then no messages are received
		assertThat(outgoingEndpoint.getReceivedExchanges(), is(empty()));
	}

	@Test
	public void verifyRoutesUsingSettingValueBackedPredicatesAlwaysReflectCurrentState() throws Exception {
		final Endpoint incomingEndpoint = context.getEndpoint("direct:incoming." + UUID.randomUUID());
		final MockEndpoint outgoingEndpoint = context.getEndpoint("mock:outgoing." + UUID.randomUUID(), MockEndpoint.class);
		final String message = "Message Body";

		// Given the default value of the setting is false
		final SettingDefinition settingDefinition = createSettingDefinitionWithDefaultValueOf(false);
		final SettingValueBackedPredicate predicate = createSettingValueBackedPredicateForSetting(settingDefinition);

		// And a route that forward messages when the setting value is true
		context.addRoutes(new PredicateRoute(incomingEndpoint, outgoingEndpoint, predicate));

		// And the setting value is switched to true
		final SettingValue settingValue = settingsService.createSettingValue(settingDefinition);
		settingValue.setBooleanValue(true);
		settingsService.updateSettingValue(settingValue);

		outgoingEndpoint.expectedMessageCount(1);

		// When I send a message
		context.createProducerTemplate().sendBody(incomingEndpoint, message);

		// Then the message is forwarded
		assertThat(outgoingEndpoint.getReceivedExchanges(), hasSize(1));
		assertEquals(outgoingEndpoint.getReceivedExchanges().get(0).getIn().getBody(), message);
	}

	private SettingDefinition createSettingDefinitionWithDefaultValueOf(final boolean defaultValue) {
		final String settingPath = SETTING_PATH.replace("%", UUID.randomUUID().toString());
		final SettingDefinition settingDefinition = createSettingDefinition(settingPath);
		settingDefinition.setMaxOverrideValues(-1);
		settingDefinition.setDefaultValue(String.valueOf(defaultValue));

		return settingsService.updateSettingDefinition(settingDefinition);
	}

	private SettingValueBackedPredicate createSettingValueBackedPredicateForSetting(final SettingDefinition settingDefinition) {
		final SettingValueBackedPredicate predicate = new SettingValueBackedPredicate();
		predicate.setSettingsReader(settingsReader);
		predicate.setPath(settingDefinition.getPath());
		return predicate;
	}

	private SettingDefinition createSettingDefinition(final String settingPath) {
		final String refreshStrategyKey = "coreRefreshStrategy";

		final SettingMetadata settingMetaData = beanFactory.getBean(ContextIdNames.SETTING_METADATA);
		settingMetaData.setKey(refreshStrategyKey);
		settingMetaData.setValue("immediate");

		final Map<String, SettingMetadata> metadata = ImmutableMap.of(
				refreshStrategyKey, settingMetaData
		);

		final SettingDefinition settingDefinition = beanFactory.getBean(ContextIdNames.SETTING_DEFINITION);
		settingDefinition.setPath(settingPath);
		settingDefinition.setMetadata(metadata);
		settingDefinition.setValueType("Boolean");

		return settingDefinition;
	}

	private class PredicateRoute extends RouteBuilder {

		private final SettingValueBackedPredicate predicate;
		private final Endpoint incomingEndpoint;
		private final Endpoint outgoingEndpoint;

		public PredicateRoute(final Endpoint incomingEndpoint, final Endpoint outgoingEndpoint, final SettingValueBackedPredicate predicate) {
			this.incomingEndpoint = incomingEndpoint;
			this.outgoingEndpoint = outgoingEndpoint;
			this.predicate = predicate;
		}

		@Override
		public void configure() throws Exception {
			from(incomingEndpoint)
					.filter(predicate)
					.to(outgoingEndpoint);
		}

	}
}