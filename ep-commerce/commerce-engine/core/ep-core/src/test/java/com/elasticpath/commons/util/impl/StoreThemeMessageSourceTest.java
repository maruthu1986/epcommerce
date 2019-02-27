/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.commons.util.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.service.catalogview.StoreConfig;
import com.elasticpath.settings.provider.SettingValueProvider;

/**
 * StoreThemeMessageSource tests.
 */
@RunWith(MockitoJUnitRunner.class)
public class StoreThemeMessageSourceTest {
	
	@Mock
	private StoreConfig storeConfig;

	@Mock
	private SettingValueProvider<String> themeProvider;

	@InjectMocks
	private StoreThemeMessageSource messageSource;

	/**
	 * Test that getThemeCode() will throw an exception if the setting value doesn't exist.
	 */
	@Test(expected = EpServiceException.class)
	public void testThrowExceptionWhenThemeSettingNonExistent() {
		doThrow(EpServiceException.class).when(storeConfig).getSettingValue(themeProvider);

		messageSource.getThemeCode();
	}
	
	/**
	 * Test that getThemeCode() will throw an exception if the theme code is null.
	 */
	@Test(expected = EpSystemException.class)
	public void testThrowExceptionWhenThemeNull() {
		when(storeConfig.getSettingValue(themeProvider)).thenReturn(null);

		messageSource.getThemeCode();
	}
	
	/**
	 * Test that getThemeCode() will throw an exception if the theme code is of zero length.
	 */
	@Test(expected = EpSystemException.class)
	public void testThrowExceptionWhenThemeEmpty() {
		when(storeConfig.getSettingValue(themeProvider)).thenReturn(StringUtils.EMPTY);

		messageSource.getThemeCode();
	}

	@Test
	public void verifyThemeCodeFoundInStoreConfigAndSettingValueProvider() throws Exception {
		final String themeCode = "MYTHEMECODE";

		when(storeConfig.getSettingValue(themeProvider)).thenReturn(themeCode);

		assertThat(messageSource.getThemeCode())
				.isEqualTo(themeCode);
	}

}
