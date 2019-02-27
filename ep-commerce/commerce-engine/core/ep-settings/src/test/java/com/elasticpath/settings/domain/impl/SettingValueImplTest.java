/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.settings.domain.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.Test;

import com.elasticpath.settings.domain.SettingDefinition;
import com.elasticpath.settings.domain.SettingValue;

/**
 * Test case for <code>SettingValueImpl</code>. 
 */
public class SettingValueImplTest {

	private final SettingValueImpl settingValueImpl = new SettingValueImpl();

	private static final String CONTEXT = "SOME-CONTEXT";
	private static final String VALUE = "SomeValue";
	private static final String BOOLEAN = "Boolean";
	private static final String PATH = "SOMEPATH";
	private static final Date DATE = new Date();

	private SettingValueImpl createNewSettingValue(
			final SettingDefinition settingDefinition, final String value, final String context) {
		SettingValueImpl settingValue = new SettingValueImpl();
		settingValue.setSettingDefinition(settingDefinition);
		settingValue.setValue(value);
		settingValue.setContext(context);
		return settingValue;
	}
	
	private SettingDefinition createSettingDefinition(final String path, final String type, final String defaultValue) {
		final SettingDefinition mockDef = mock(SettingDefinition.class);
		when(mockDef.getPath()).thenReturn(path);
		when(mockDef.getValueType()).thenReturn(type);
		when(mockDef.getDefaultValue()).thenReturn(defaultValue);
		when(mockDef.getLastModifiedDate()).thenReturn(DATE);
		return mockDef;
	}

	/**
	 * Test that you get the same context that you set.
	 */
	@Test
	public void testGetSetContext() {
		this.settingValueImpl.setContext(CONTEXT);
		assertThat(this.settingValueImpl.getContext()).isEqualTo(CONTEXT);
	}

	/**
	 * Test that you get the same value that you set.
	 */
	@Test
	public void testGetValue() {
		this.settingValueImpl.setValue(VALUE);
		assertThat(this.settingValueImpl.getValue()).isEqualTo(VALUE);
	}

	/**
	 * Test that the last modified date is the definition's date when the object has not been persisted,
	 * falls back to the setting definition's last modified date.
	 */
	@Test
	public void testGetLastModifiedDate() {
		SettingDefinition def = createSettingDefinition(PATH, BOOLEAN, VALUE);
		settingValueImpl.setSettingDefinition(def);
		assertThat(settingValueImpl.getLastModifiedDate()).isEqualTo(DATE);
	}

	/**
	 * Test that the setter for last modified date exists.
	 */
	@Test
	public void testSetLastModifiedDate() {
		final Date newDate = new Date();
		settingValueImpl.setLastModifiedDateInternal(newDate);
		assertThat(settingValueImpl.getLastModifiedDate()).isSameAs(newDate);
	}
	
	/**
	 * Test that the method gets a true boolean value when the setting's value is 
	 * a "true" string.
	 */
	@Test
	public void testGetBooleanValueTrue() {
		final String context = "testContext";
		final String value1 = "true";
		final String path1 = "COMMERCE/STORE/theme";
		final String defaultValue1 = "DefaultValue1";
		final String type1 = BOOLEAN;
		final SettingDefinition settingsDefinition1 = createSettingDefinition(path1, type1, defaultValue1);
		final SettingValue settingValueOne = createNewSettingValue(settingsDefinition1, value1, context);
		assertThat(settingValueOne.getBooleanValue()).isTrue();


	}
	
	/**
	 * Test that the method gets a false boolean value when the setting's value is 
	 * a "false" string.
	 */
	@Test
	public void testGetBooleanValueFalse() {
		final String newcontext = "NewContext";
		final String value2 = "false";
		final String path2 = "COMMERCE/SYSTEM/ASSETS/assetLocation";
		final String defaultValue2 = "DefaultValue2";
		final String type2 = BOOLEAN;
		final SettingDefinition settingsDefinition2 = createSettingDefinition(path2, type2, defaultValue2);
		final SettingValue settingValueTwo = createNewSettingValue(settingsDefinition2, value2, newcontext);
		assertThat(settingValueTwo.getBooleanValue()).isFalse();
	}
	
	/**
	 * Test that the method gets a false boolean value when the setting's value is 
	 * a string value that is not "true" or "false".
	 */
	@Test
	public void testGetBooleanValueNeither() {
		final String newercontext = "NewerContext";
		final String value3 = "one";
		final String path3 = "COMMERCE/SYSTEM/seoEnabled";
		final String defaultValue3 = "DefaultValue3";
		final String type3 = "Integer";
		final SettingDefinition settingsDefinition3 = createSettingDefinition(path3, type3, defaultValue3);
		final SettingValue settingValueThree = createNewSettingValue(settingsDefinition3, value3, newercontext);
		assertThat(settingValueThree.getBooleanValue()).isFalse();
	}

	/**
	 * Test that the method sets a "true" string when the parameter value is true.
	 */
	@Test
	public void testSetBooleanValueTrue() {
		final String context = "testContext";
		final String value1 = "one";
		final String path1 = "COMMERCE/STORE/theme";
		final String defaultValue1 = "DefaultValue1";
		final String type1 = BOOLEAN;
		final SettingDefinition settingsDefinition1 = createSettingDefinition(path1, type1, defaultValue1);
		SettingValue settingValueOne = createNewSettingValue(settingsDefinition1, value1, context);
		settingValueOne.setBooleanValue(true);
		assertThat(settingValueOne.getBooleanValue()).isTrue();
	}
	
	/**
	 * Test that the method sets a "false" string when the parameter value is false.
	 */
	@Test
	public void testSetBooleanValueFalse() {
		final String newcontext = "NewContext";
		final String value2 = "two";
		final String path2 = "COMMERCE/SYSTEM/ASSETS/assetLocation";
		final String defaultValue2 = "DefaultValue2";
		final String type2 = BOOLEAN;
		final SettingDefinition settingsDefinition2 = createSettingDefinition(path2, type2, defaultValue2);
		SettingValue settingValueTwo = createNewSettingValue(settingsDefinition2, value2, newcontext);
		settingValueTwo.setBooleanValue(false);
		assertThat(settingValueTwo.getBooleanValue()).isFalse();

	}
	/**
	 * Test that two SettingValue objects are considered equal if their Contexts and their
	 * SettingDefinitions are both equal.
	 */
	@Test
	public void testEquals() {

		final String context = "testContext";
		final String newcontext = "NewContext";
		final String value1 = "value1";
		final String value2 = "value2";

		final String path1 = "COMMERCE/STORE/theme";
		final String path2 = "COMMERCE/SYSTEM/ASSETS/assetLocation";
		final String defaultValue1 = "DefaultValue1";
		final String defaultValue2 = "DefaultValue2";
		final String type1 = "Type1";
		final String type2 = "Type2";

		final SettingDefinition settingsDefinition1 = createSettingDefinition(path1, type1, defaultValue1);
		final SettingDefinition settingsDefinition2 = createSettingDefinition(path2, type2, defaultValue2);

		final SettingValue settingValueOne = createNewSettingValue(settingsDefinition1, value1, context);
		final SettingValue settingValueTwo = createNewSettingValue(settingsDefinition1, value2, context);

		assertThat(settingValueTwo)
			.isEqualTo(settingValueOne)
			.as("These two settingValues should be equal since they both have the same contexts and settingsdefinitions");

		final SettingValue settingValueThree = createNewSettingValue(settingsDefinition1, value1, context);
		final SettingValue settingValueFour = createNewSettingValue(settingsDefinition1, value1, newcontext);

		assertThat(settingValueThree)
			.isNotEqualTo(settingValueFour)
			.as("These two settingValues should not be equal since they have different contexts");

		final SettingValue settingValueFive = createNewSettingValue(settingsDefinition1, value1, context);
		final SettingValue settingValueSix = createNewSettingValue(settingsDefinition2, value1, context);

		assertThat(settingValueFive)
			.isNotEqualTo(settingValueSix)
			.as("These two settingValues should not be equal since they have different settingDefinitions");
	}
}
