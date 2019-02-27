/**
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.cmclient.core.formatting;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.*;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.eclipse.rap.rwt.testfixture.TestContext;

import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.common.CMContextIdNames;
import com.elasticpath.commons.beanframework.BeanFactory;


/**
 * Test class for {@link TimeZoneInfo}}
 */
public class TimeZoneInfoTest {

	/**
	 * Using 3 timezones that do not use DST to ensure the expected values don't change throughout the year.
	 */
	private static final String HIGH_OFFSET_DISPLAYNAME = "(UTC+12:00) Coordinated Universal Time +12:00";
	private static final String UTC_DISPLAYNAME = "(UTC+00:00) Coordinated Universal Time";
	private static final String LOW_OFFSET_DISPLAYNAME = "(UTC-05:00) Coordinated Universal Time -5:00";

	private static final String LOW_OFFSET_LOCALIZATION_KEY = "UTCm5";
	private static final String GMT_LOCALIZATION_KEY = "UTC";
	private static final String HIGH_OFFSET_LOCALIZATION_KEY = "UTCp12";

	private static final String LOW_OFFSET_TIMEZONE_ID = "Etc/GMT+5";
	private static final String GMT_TIMEZONE_ID = "Etc/GMT";
	private static final String HIGHT_OFFSET_TIMEZONE_ID = "Etc/GMT-12";

	@Rule
	public TestContext context = new TestContext();

	private final UIDateTimeUtil mockDateTimeUtil = mock(UIDateTimeUtil.class);

	private TimeZoneInfo instance;
	private List<String> timezoneDisplayStrings;

	private final Map<String, String> timezoneFormatMap = new LinkedHashMap<>();

	@Before
	public void setUp() {
		CoreMessages.get();
		instance = TimeZoneInfo.getInstance();
		timezoneFormatMap.put(HIGHT_OFFSET_TIMEZONE_ID, HIGH_OFFSET_LOCALIZATION_KEY);
		timezoneFormatMap.put(GMT_TIMEZONE_ID, GMT_LOCALIZATION_KEY);
		timezoneFormatMap.put(LOW_OFFSET_TIMEZONE_ID, LOW_OFFSET_LOCALIZATION_KEY);

		BeanFactory mockBeanFactory = mock(BeanFactory.class);
		ServiceLocator.setBeanFactory(mockBeanFactory);

		when(mockBeanFactory.getBean((CMContextIdNames.UI_DATE_FORMATTER)))
				.thenReturn(mockDateTimeUtil);

		when(mockBeanFactory.getBean((CMContextIdNames.TIMEZONE_FORMAT_MAP)))
				.thenReturn(timezoneFormatMap);

		timezoneDisplayStrings = instance.getTimezoneDisplayStrings();

	}


	/**
	 * Verify that the timezones used in the test do not observe DST.
	 * so that their expected results won't change over time.
	 */
	@Test
	public void testNotInDaylightSavings() {

		assertThat(instance.getTimeZoneForDisplayString(HIGH_OFFSET_DISPLAYNAME).observesDaylightTime()).isFalse();
		assertThat(instance.getTimeZoneForDisplayString(UTC_DISPLAYNAME).observesDaylightTime()).isFalse();
		assertThat(instance.getTimeZoneForDisplayString(LOW_OFFSET_DISPLAYNAME).observesDaylightTime()).isFalse();

	}
	@Test
	public void testSortOrdering() {

		assertThat(timezoneDisplayStrings).containsAll(Arrays.asList(HIGH_OFFSET_DISPLAYNAME, UTC_DISPLAYNAME, LOW_OFFSET_DISPLAYNAME));
		assertThat(timezoneDisplayStrings.indexOf(HIGH_OFFSET_DISPLAYNAME)).isLessThan(timezoneDisplayStrings.indexOf(UTC_DISPLAYNAME));
		assertThat(timezoneDisplayStrings.indexOf(UTC_DISPLAYNAME)).isLessThan(timezoneDisplayStrings.indexOf(LOW_OFFSET_DISPLAYNAME));
	}

	@Test
	public void testRetrievingTimeZoneForDisplayString(){

		TimeZone timeZone = instance.getTimeZoneForDisplayString(LOW_OFFSET_DISPLAYNAME);
		assertThat(timeZone).isNotNull();
		assertThat(timeZone.getID()).isEqualTo(LOW_OFFSET_TIMEZONE_ID);
	}


	@Test
	public void testRetrievingDisplayStringForTimeZone(){

		String displayString = instance.getDisplayStringForTimeZone(TimeZone.getTimeZone(LOW_OFFSET_TIMEZONE_ID));
		assertThat(displayString).isEqualTo(LOW_OFFSET_DISPLAYNAME);

	}

	@Test
	public void testSettingAndRetrievingCookie() {

		TimeZone browserTimezone = TimeZone.getTimeZone(LOW_OFFSET_LOCALIZATION_KEY);

		when(mockDateTimeUtil.getTimeZoneFromBrowser()).thenReturn(browserTimezone);

		instance.setTimezone(TimeZoneInfo.BROWSER);
		assertThat(instance.getTimezone()).isEqualTo(browserTimezone);

	}

}