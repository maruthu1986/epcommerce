/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.service.search.impl;

import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.assertj.core.api.SoftAssertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.misc.SearchConfig;
import com.elasticpath.domain.misc.impl.SearchConfigImpl;
import com.elasticpath.service.search.SearchHostLocator;
import com.elasticpath.settings.test.support.SimpleSettingValueProvider;

/**
 * Test class for {@link AbstractSettingsSearchConfigFactory}.
 */
@RunWith(MockitoJUnitRunner.class)
public class AbstractSettingsSearchConfigFactoryTest {

	private static final int MAXIMUM_RETURN_NUMBER = 1;
	private static final BigDecimal MINIMUM_SIMILARITY = new BigDecimal("2.1");
	private static final int PREFIX_LENGTH = 3;
	private static final int MINIMUM_RESULTS_THRESHOLD = 4;
	private static final int MAXIMUM_RESULTS_THRESHOLD = 5;
	private static final int MAXIMUM_SUGGESTIONS_PER_WORD = 6;
	private static final BigDecimal ACCURACY = new BigDecimal("7.1");
	private static final Set<String> EXCLUSIVE_ATTRIBUTE_LIST = ImmutableSet.of("Foo", "Bar");
	private static final String SEARCH_HOST_LOCATION = "/foo/bar";

	@Mock
	private BeanFactory beanFactory;

	@Mock
	private SearchHostLocator searchHostLocator;

	@InjectMocks
	private TestSettingsSearchConfigFactory factory;

	@Test
	public void verifySearchConfigPopulatedFromProviders() throws Exception {
		final String indexName = "Index Name";
		final String settingsContext = "Settings Context";

		final String fooKey = "foo";
		final String fooVal = "1.2";

		final String barKey = "bar";
		final String barVal = "2.3";

		final Map<String, String> boostValueStringMap = ImmutableMap.of(
				fooKey, fooVal,
				barKey, barVal
		);

		factory.setAccuracyProvider(new SimpleSettingValueProvider<>(settingsContext, ACCURACY));
		factory.setBoostValuesProvider(new SimpleSettingValueProvider<>(settingsContext, boostValueStringMap));
		factory.setExclusiveAttributeListProvider(new SimpleSettingValueProvider<>(settingsContext, EXCLUSIVE_ATTRIBUTE_LIST));
		factory.setMaximumResultsThresholdProvider(new SimpleSettingValueProvider<>(settingsContext, MAXIMUM_RESULTS_THRESHOLD));
		factory.setMaximumReturnNumberProvider(new SimpleSettingValueProvider<>(settingsContext, MAXIMUM_RETURN_NUMBER));
		factory.setMaximumSuggestionsPerWordProvider(new SimpleSettingValueProvider<>(settingsContext, MAXIMUM_SUGGESTIONS_PER_WORD));
		factory.setMinimumResultsThresholdProvider(new SimpleSettingValueProvider<>(settingsContext, MINIMUM_RESULTS_THRESHOLD));
		factory.setMinimumSimilarityProvider(new SimpleSettingValueProvider<>(settingsContext, MINIMUM_SIMILARITY));
		factory.setPrefixLengthProvider(new SimpleSettingValueProvider<>(settingsContext, PREFIX_LENGTH));

		when(beanFactory.getBean(ContextIdNames.SEARCH_CONFIG)).thenReturn(new SearchConfigImpl());
		when(searchHostLocator.getSearchHostLocation()).thenReturn(SEARCH_HOST_LOCATION);

		final SearchConfig searchConfig = factory.getSearchConfig(indexName, settingsContext);

		final SoftAssertions softly = new SoftAssertions();

		softly.assertThat(searchConfig.getAccuracy()).isEqualTo(ACCURACY.floatValue());
		softly.assertThat(searchConfig.getExclusiveAttributes()).isEqualTo(EXCLUSIVE_ATTRIBUTE_LIST);
		softly.assertThat(searchConfig.getMaximumResultsThreshold()).isEqualTo(MAXIMUM_RESULTS_THRESHOLD);
		softly.assertThat(searchConfig.getMaxReturnNumber()).isEqualTo(MAXIMUM_RETURN_NUMBER);
		softly.assertThat(searchConfig.getMaximumSuggestionsPerWord()).isEqualTo(MAXIMUM_SUGGESTIONS_PER_WORD);
		softly.assertThat(searchConfig.getMinimumResultsThreshold()).isEqualTo(MINIMUM_RESULTS_THRESHOLD);
		softly.assertThat(searchConfig.getMinimumSimilarity()).isEqualTo(MINIMUM_SIMILARITY.floatValue());
		softly.assertThat(searchConfig.getPrefixLength()).isEqualTo(PREFIX_LENGTH);

		softly.assertThat(searchConfig.getSearchHost()).isEqualTo(SEARCH_HOST_LOCATION);

		softly.assertThat(searchConfig.getBoostValue(fooKey)).isEqualTo(Float.valueOf(fooVal));
		softly.assertThat(searchConfig.getBoostValue(barKey)).isEqualTo(Float.valueOf(barVal));

		softly.assertAll();
	}

	private static final class TestSettingsSearchConfigFactory extends AbstractSettingsSearchConfigFactory {

		@Override
		public SearchConfig getSearchConfig(final String accessKey) {
			return null;
		}

	}

}