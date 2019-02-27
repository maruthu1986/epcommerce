/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.settings.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import com.elasticpath.settings.provider.SettingValueProvider;
import com.elasticpath.test.integration.junit.DatabaseHandlingTestExecutionListener;

/**
 * <p>
 * Integration test class for the {@code <setting />} Spring tag.
 * </p>
 * <p>
 * This test expects the Spring context to autowire beans defined in ep-settings-itest-context.xml.
 * </p>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/ep-settings-itest-context.xml")
@TestExecutionListeners({
		DatabaseHandlingTestExecutionListener.class,
		DependencyInjectionTestExecutionListener.class
})
public class SettingsCustomSpringTagITest {

	private static final String UNEXPECTED_INJECTED_VALUE_MESSAGE = "Unexpected injected value";

	// All expected values defined in insert-setting-definitions-for-itests.xml Liquibase change set
	private static final BigDecimal EXPECTED_BIGDECIMAL_VALUE = BigDecimal.valueOf(123.4);
	private static final Boolean EXPECTED_BOOLEAN_VALUE = Boolean.TRUE;
	private static final Integer EXPECTED_INTEGER_VALUE = 123;
	private static final Collection<String> EXPECTED_COLLETION_VALUE = Arrays.asList("VAL1", "VAL2", "VAL3");
	private static final Map<String, String> EXPECTED_MAP_VALUE = new HashMap<>();
	private static final String EXPECTED_STRING_VALUE = "Expected value";
	private static final String EXPECTED_URL_VALUE = "http://domain:1234/path";
	private static final String EXPECTED_XML_VALUE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><xmldocument><name>Sample XML for "
			+ "testing</name></xmldocument>";
	private static final String EXPECTED_CONTEXTUAL_DEFAULT_VALUE = "Default value";
	private static final String EXPECTED_CONTEXT_ONE_VALUE = "Context value 1";
	private static final String EXPECTED_CONTEXT_TWO_VALUE = "Context value 2";

	static {
		EXPECTED_MAP_VALUE.put("KEY1", "VAL1");
		EXPECTED_MAP_VALUE.put("KEY2", "VAL2");
		EXPECTED_MAP_VALUE.put("KEY3", "VAL3");
	}

	@Autowired
	@Qualifier("bigDecimalSetting")
	private SettingValueProvider<BigDecimal> bigDecimalSettingValueProvider;

	@Autowired
	@Qualifier("booleanSetting")
	private SettingValueProvider<Boolean> booleanSettingValueProvider;

	@Autowired
	@Qualifier("integerSetting")
	private SettingValueProvider<Integer> integerSettingValueProvider;

	@Autowired
	@Qualifier("collectionSetting")
	private SettingValueProvider<Collection<String>> collectionSettingValueProvider;

	@Autowired
	@Qualifier("mapSetting")
	private SettingValueProvider<Map<String, String>> mapSettingValueProvider;

	@Autowired
	@Qualifier("stringSetting")
	private SettingValueProvider<String> stringSettingValueProvider;

	@Autowired
	@Qualifier("urlSetting")
	private SettingValueProvider<String> urlSettingValueProvider;

	@Autowired
	@Qualifier("xmlSetting")
	private SettingValueProvider<String> xmlSettingValueProvider;

	@Autowired
	@Qualifier("contextDefaultSetting")
	private SettingValueProvider<String> contextualDefaultSettingValueProvider;

	@Autowired
	@Qualifier("contextOneSetting")
	private SettingValueProvider<String> contextOneSettingValueProvider;

	@Autowired
	@Qualifier("contextTwoSetting")
	private SettingValueProvider<String> contextTwoValueProvider;

	@Test
	public void verifyBigDecimalSettingValuesCanBeProvided() {
		assertThat(bigDecimalSettingValueProvider.get())
				.as(UNEXPECTED_INJECTED_VALUE_MESSAGE)
				.isEqualTo(EXPECTED_BIGDECIMAL_VALUE);
	}

	@Test
	public void verifyBooleanSettingValuesCanBeProvided() {
		assertThat(booleanSettingValueProvider.get())
				.as(UNEXPECTED_INJECTED_VALUE_MESSAGE)
				.isEqualTo(EXPECTED_BOOLEAN_VALUE);
	}

	@Test
	public void verifyIntegerSettingValuesCanBeProvided() {
		assertThat(integerSettingValueProvider.get())
				.as(UNEXPECTED_INJECTED_VALUE_MESSAGE)
				.isEqualTo(EXPECTED_INTEGER_VALUE);
	}

	@Test
	public void verifyCollectionSettingValuesCanBeProvided() {
		assertThat(collectionSettingValueProvider.get())
				.as(UNEXPECTED_INJECTED_VALUE_MESSAGE)
				.isEqualTo(EXPECTED_COLLETION_VALUE);
	}

	@Test
	public void verifyMapSettingValuesCanBeProvided() {
		assertThat(mapSettingValueProvider.get())
				.as(UNEXPECTED_INJECTED_VALUE_MESSAGE)
				.isEqualTo(EXPECTED_MAP_VALUE);
	}

	@Test
	public void verifyStringSettingValuesCanBeProvided() {
		assertThat(stringSettingValueProvider.get())
				.as(UNEXPECTED_INJECTED_VALUE_MESSAGE)
				.isEqualTo(EXPECTED_STRING_VALUE);
	}

	@Test
	public void verifyUrlSettingValuesCanBeProvided() {
		assertThat(urlSettingValueProvider.get())
				.as(UNEXPECTED_INJECTED_VALUE_MESSAGE)
				.isEqualTo(EXPECTED_URL_VALUE);
	}

	@Test
	public void verifyXmlSettingValuesCanBeProvided() {
		assertThat(xmlSettingValueProvider.get())
				.as(UNEXPECTED_INJECTED_VALUE_MESSAGE)
				.isEqualTo(EXPECTED_XML_VALUE);
	}

	@Test
	public void verifyContextualSettingValuesCanBeProvided() {
		assertThat(contextualDefaultSettingValueProvider.get())
				.as(UNEXPECTED_INJECTED_VALUE_MESSAGE)
				.isEqualTo(EXPECTED_CONTEXTUAL_DEFAULT_VALUE);

		assertThat(contextOneSettingValueProvider.get())
				.as(UNEXPECTED_INJECTED_VALUE_MESSAGE)
				.isEqualTo(EXPECTED_CONTEXT_ONE_VALUE);

		assertThat(contextTwoValueProvider.get())
				.as(UNEXPECTED_INJECTED_VALUE_MESSAGE)
				.isEqualTo(EXPECTED_CONTEXT_TWO_VALUE);
	}

	@Test
	public void verifyContextualSettingValuesCanBeRequestedByCaller() throws Exception {
		assertThat(contextualDefaultSettingValueProvider.get("CONTEXT1"))
				.as(UNEXPECTED_INJECTED_VALUE_MESSAGE)
				.isEqualTo(EXPECTED_CONTEXT_ONE_VALUE);

		assertThat(contextualDefaultSettingValueProvider.get("CONTEXT2"))
				.as(UNEXPECTED_INJECTED_VALUE_MESSAGE)
				.isEqualTo(EXPECTED_CONTEXT_TWO_VALUE);
	}

}