/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.settings.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static com.elasticpath.settings.impl.CachedSettingsReaderImpl.getSettingData;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.settings.SettingsService;
import com.elasticpath.settings.domain.SettingDefinition;
import com.elasticpath.settings.domain.SettingMetadata;
import com.elasticpath.settings.domain.SettingValue;
import com.elasticpath.settings.refreshstrategy.SettingRefreshStrategy;

/**
 * Test that the Cached Settings Reader is properly delegating to refresh strategies when retrieving setting values.
 */
@RunWith(MockitoJUnitRunner.class)
public class CachedSettingsReaderImplTest {

	private static final String[] SETTING_CONTEXTS = { "SNAPITUP", "SNAPITUPUK", "SLRWORLD" };

	private static final String SETTING_PATH = "COMMERCE/Store/theme";

	private static final String SETTING_PATH_2 = "COMMERCE/Store/theme2";

	private static final String SETTING_PATH_3 = "COMMERCE/Store/theme3";

	@Mock
	private SettingsService reader;

	@Mock
	private SettingRefreshStrategy refreshStrategy;

	@InjectMocks
	private CachedSettingsReaderImpl cachedSettingsReader;

	@Mock
	private SettingDefinition definition;

	/**
	 * Set up common objects needed by the tests.
	 */
	@Before
	public void runBeforeEveryTest() {
		final Map<String, SettingRefreshStrategy> refreshStrategies = new HashMap<>();
		refreshStrategies.put("immediate", refreshStrategy);
		cachedSettingsReader.setRefreshStrategies(refreshStrategies);
		cachedSettingsReader.setRefreshStrategyKey("apiRefreshStrategy");
		final SettingMetadata strategyMetadata = mock(SettingMetadata.class);
		final Map<String, SettingMetadata> metadata = new HashMap<>();
		metadata.put("apiRefreshStrategy", strategyMetadata);

		when(definition.getMetadata()).thenReturn(metadata);
		when(strategyMetadata.getValue()).thenReturn("immediate");
	}

	/**
	 * Test method for {@link CachedSettingsReaderImpl#getSettingValue(String, String)}.
	 * Tests that the setting definition is being checked and that the value is being retrieved from the refresh strategy.
	 */
	@Test
	public void testGetSettingValue() {
		final SettingValue value = mock(SettingValue.class);

		when(reader.getSettingDefinition(same(SETTING_PATH))).thenReturn(definition);
		when(value.getValue()).thenReturn("value");
		when(refreshStrategy.retrieveSetting(any(String.class), any(String.class), any(String.class))).thenReturn(value);

		SettingValue settingValue = cachedSettingsReader.getSettingValue(SETTING_PATH, "SNAPITUP");
		assertThat(value.getValue())
			.isEqualTo(settingValue.getValue())
			.as("Should be getting back the same value returned from the refresh strategy.");
		verify(reader).getSettingDefinition(same(SETTING_PATH));
		verify(refreshStrategy).retrieveSetting(any(String.class), any(String.class), any(String.class));
	}

	/**
	 * Test method for {@link CachedSettingsReaderImpl#getSettingValue(String)}.
	 * Tests that the setting definition is being checked and that the value is being retrieved from the refresh strategy.
	 */
	@Test
	public void testGetSettingValueWithNoContext() {
		final SettingValue value = mock(SettingValue.class);

		when(reader.getSettingDefinition(same(SETTING_PATH_2))).thenReturn(definition);
		when(value.getValue()).thenReturn("value");
		when(refreshStrategy.retrieveSetting(any(String.class), any(String.class))).thenReturn(value);

		SettingValue settingValue = cachedSettingsReader.getSettingValue(SETTING_PATH_2);
		assertThat(value.getValue())
			.isEqualTo(settingValue.getValue())
			.as("Should be getting back the same value returned from the refresh strategy.");
		verify(reader).getSettingDefinition(same(SETTING_PATH_2));
		verify(refreshStrategy).retrieveSetting(any(String.class), any(String.class));
	}

	@Test
	public void testThatGetSettingValueThrowsEpServiceExceptionWhenNoSuchSettingDefinition() {
		when(reader.getSettingDefinition(SETTING_PATH)).thenReturn(null);

		assertThatThrownBy(() -> cachedSettingsReader.getSettingValue(SETTING_PATH))
				.isInstanceOf(EpServiceException.class);
	}

	@Test
	public void testThatGetSettingValueWithContextThrowsEpServiceExceptionWhenNoSuchSettingDefinition() {
		when(reader.getSettingDefinition(SETTING_PATH)).thenReturn(null);

		assertThatThrownBy(() -> cachedSettingsReader.getSettingValue(SETTING_PATH, SETTING_CONTEXTS[0]))
				.isInstanceOf(EpServiceException.class);

	}

	/**
	 * Test method for {@link CachedSettingsReaderImpl#getSettingValues(String, String[])}.
	 * Tests that the setting definition is being checked and that the values are being retrieved from the refresh strategy.
	 */
	@Test
	public void testGetSettingValues() {
		final SettingValue value1 = mock(SettingValue.class, "value1");
		final SettingValue value2 = mock(SettingValue.class, "value2");
		final SettingValue value3 = mock(SettingValue.class, "value3");

		final Set<SettingValue> expectedSettingValues = new HashSet<>();
		expectedSettingValues.add(value1);
		expectedSettingValues.add(value2);
		expectedSettingValues.add(value3);

		when(reader.getSettingDefinition(same(SETTING_PATH_3))).thenReturn(definition);
		when(value1.getValue()).thenReturn("value1");
		when(value2.getValue()).thenReturn("value2");
		when(value3.getValue()).thenReturn("value3");
		when(refreshStrategy.retrieveSetting(any(String.class), any(String.class), any(String.class)))
			.thenReturn(value1)
			.thenReturn(value2)
			.thenReturn(value3);

		Set<SettingValue> settingValues = cachedSettingsReader.getSettingValues(SETTING_PATH_3, SETTING_CONTEXTS);
		for (SettingValue settingValue : settingValues) {
			boolean valueWasExpected = false;
			for (SettingValue expectedSettingValue : expectedSettingValues) {
				if (expectedSettingValue.getValue().equals(settingValue.getValue())) {
					valueWasExpected = true;
				}
			}
			assertThat(valueWasExpected)
				.isTrue()
				.as("The value \"" + settingValue.getValue() + " was not expected. Should be getting back the same values we expected.");
		}
		verify(reader).getSettingDefinition(same(SETTING_PATH_3));
		verify(refreshStrategy, times(expectedSettingValues.size())).retrieveSetting(any(String.class), any(String.class), any(String.class));
	}

	/**
	 * Test that the static setting data map is cleared when {@link CachedSettingsReaderImpl#destroy()} is called.
	 * 
	 * @throws Exception to accommodate the method signature of {@link org.springframework.beans.factory.DisposableBean#destroy()}
	 */
	@Test
	public void testSettingDataMapClearedOnDestroy() throws Exception {
		getSettingData().put("key", null);
		assertThat(getSettingData())
			.isNotEmpty()
			.as("Setting data map should have one entry");
		CachedSettingsReaderImpl cachedSettingsReader = new CachedSettingsReaderImpl();
		cachedSettingsReader.destroy();
		assertThat(getSettingData())
			.as("Setting data map should be cleared on destroy")
			.isEmpty();
	}

}
