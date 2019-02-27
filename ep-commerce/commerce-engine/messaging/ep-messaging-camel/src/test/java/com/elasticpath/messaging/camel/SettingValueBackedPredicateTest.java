/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.messaging.camel;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.settings.SettingsReader;
import com.elasticpath.settings.domain.SettingValue;


/**
 * Test class for {@link SettingValueBackedPredicate}.
 */
@RunWith(MockitoJUnitRunner.class)
public class SettingValueBackedPredicateTest {

	public static final String SETTING_PATH = "COMMERCE/TEST/SETTINGS/testSettingPath";
	public static final String SETTING_CONTEXT = "A_Context";

	private SettingValueBackedPredicate predicate;

	@Mock
	private SettingsReader settingsReader;

	@Mock
	private SettingValue settingValue;

	@Before
	public void setUp() {
		predicate = new SettingValueBackedPredicate();
		predicate.setSettingsReader(settingsReader);
		predicate.setPath(SETTING_PATH);
		predicate.setContext(SETTING_CONTEXT);
	}

	@Test
	public void verifyApplyReturnsTrueWhenSettingValueTrue() throws Exception {
		when(settingsReader.getSettingValue(SETTING_PATH, SETTING_CONTEXT)).thenReturn(settingValue);
		when(settingValue.getBooleanValue()).thenReturn(true);

		assertTrue("Expected return value of true when the SettingValue returns true", predicate.matches(null));
		verify(settingsReader).getSettingValue(SETTING_PATH, SETTING_CONTEXT);
		verify(settingValue).getBooleanValue();

	}

	@Test
	public void verifyApplyReturnsFalseWhenSettingValueFalse() throws Exception {
		when(settingsReader.getSettingValue(SETTING_PATH, SETTING_CONTEXT)).thenReturn(settingValue);
		when(settingValue.getBooleanValue()).thenReturn(false);

		assertFalse("Expected return value of true when the SettingValue returns true", predicate.matches(null));
		verify(settingsReader).getSettingValue(SETTING_PATH, SETTING_CONTEXT);
		verify(settingValue).getBooleanValue();
	}

}
