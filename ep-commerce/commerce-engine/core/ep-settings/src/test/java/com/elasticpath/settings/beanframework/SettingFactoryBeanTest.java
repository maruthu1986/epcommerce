/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.settings.beanframework;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.settings.SettingsReader;
import com.elasticpath.settings.domain.SettingValue;

/**
 * Test class for {@link com.elasticpath.settings.beanframework.SettingFactoryBean}.
 */
@RunWith(MockitoJUnitRunner.class)
public class SettingFactoryBeanTest {

	@InjectMocks
	private SettingFactoryBean settingFactoryBean;

	@Mock
	private SettingsReader settingsReader;

	/**
	 * Test that createInstance returns the expected value when a context is present.
	 *
	 * @throws Exception in case of error
	 */
	@Test
	public void testCreateInstanceUsesContextWhenPresent() throws Exception {
		final String settingContext = "context";
		final String path = "/TEST/PATH/TO/setting";
		final String expectedSettingValue = "value";

		settingFactoryBean.setContext(settingContext);
		settingFactoryBean.setPath(path);

		final SettingValue settingValue = mock(SettingValue.class);

		when(settingsReader.getSettingValue(path, settingContext)).thenReturn(settingValue);

		when(settingValue.getValue()).thenReturn(expectedSettingValue);

		final String actualSettingValue = settingFactoryBean.createInstance();
		assertThat(actualSettingValue)
			.isEqualTo(expectedSettingValue)
			.as("Expected the value returned by the settings reader");
		verify(settingsReader).getSettingValue(path, settingContext);
	}

	/**
	 * Test that createInstance returns the expected value when no context is given.
	 *
	 * @throws Exception in case of error
	 */
	@Test
	public void testCreateInstanceIgnoresNullContext() throws Exception {
		final String path = "/TEST/PATH/TO/setting";
		final String expectedSettingValue = "value";

		settingFactoryBean.setPath(path);

		final SettingValue settingValue = mock(SettingValue.class);

		when(settingsReader.getSettingValue(path)).thenReturn(settingValue);
		when(settingValue.getValue()).thenReturn(expectedSettingValue);

		final String actualSettingValue = settingFactoryBean.createInstance();
		assertThat(actualSettingValue)
			.isEqualTo(expectedSettingValue)
			.as("Expected the value returned by the settings reader");
		verify(settingsReader).getSettingValue(path);
	}

	/**
	 * Test that createInstance throws an exception when the path is null.
	 *
	 * @throws Exception in case of error
	 */
	@Test(expected = IllegalStateException.class)
	public void testCreateInstanceThrowsExceptionWhenPathIsNull() throws Exception {
		settingFactoryBean.createInstance();
	}

	/**
	 * Test that createInstance throws an exception when the path is not found.
	 *
	 * @throws Exception in case of error
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testCreateInstanceThrowsExceptionWhenNoSuchSettingPath() throws Exception {
		final String path = "/NO/SUCH/setting";

		settingFactoryBean.setPath(path);

		when(settingsReader.getSettingValue(path)).thenReturn(null);

		settingFactoryBean.createInstance();
		verify(settingsReader).getSettingValue(path);
	}

	/**
	 * Test that setPath throws an exception when the path is null. 
	 *
	 * @throws Exception in case of error
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetPathThrowsExceptionForNullPath() throws Exception {
		settingFactoryBean.setPath(null);
	}

	/**
	 * Test that setPath throws an exception when the path is blank.
	 *
	 * @throws Exception in case of error
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetPathThrowsExceptionForEmptyStringPath() throws Exception {
		settingFactoryBean.setPath("");
	}

}