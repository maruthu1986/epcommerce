/**
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.cmclient.core.formatting;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;

import com.elasticpath.cmclient.core.CmSingletonUtil;
import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.common.CMContextIdNames;
import com.elasticpath.cmclient.core.util.CookieUtil;
import com.elasticpath.cmclient.core.util.DateTimeUtilFactory;

/**
 * Class for getting and storing time zone info from cookie.
 */
public class TimeZoneInfo {

	/**
	 * constant to identify the timezone cookie id.
	 */
	public static final String CM_TIMEZONE_ID = "CM_TIMEZONE_ID";

	/**
	 * Constant for identifying browser-based timezone.
	 */
	public static final String BROWSER = "BROWSER";

	private final Map<String, TimeZone> timeZoneDisplays = new LinkedHashMap<>();
	private String usersTimezone;

	/**
		 * Returns a list of timezones formatted for display.
		 *
		 * @return a list of timezones formatted for display.
		 */
		public List<String> getTimezoneDisplayStrings() {
			if (timeZoneDisplays.isEmpty()) {

				Map<String, String> timezoneFormats = getTimezoneFormats();
				timezoneFormats.keySet().forEach(timeZoneId ->timeZoneDisplays.put(
						this.getLocalizedStringFor(timezoneFormats.get(timeZoneId)), TimeZone.getTimeZone(timeZoneId)));
			}
		return timeZoneDisplays.keySet().stream().distinct().collect(Collectors.toList());

		}

	private String getLocalizedStringFor(final String localizationKey) {
		return localizationKey == null ? localizationKey : CoreMessages.get().getMessage(localizationKey);
	}


	/**
	 * Gets the TimeZone for a given display string.
	 * @param displayString the display string.
	 * @return the TimeZone,  or null of not found.
	 */
		public TimeZone getTimeZoneForDisplayString(final String displayString) {
			if (timeZoneDisplays.isEmpty()) {
				getTimezoneDisplayStrings();
			}
			return timeZoneDisplays.get(displayString);
		}


	/**
	 * Gets the display string for the given timezone.
	 * @param timeZone the timezone.
	 * @return the formatted display string, or the timezone's displayName if not found.
	 */
	public String getDisplayStringForTimeZone(final TimeZone timeZone) {
		if (timeZone != null) {
			String formattedTimeZone = getLocalizedStringFor(getTimezoneFormats().get(timeZone.getID()));
			return formattedTimeZone == null ? timeZone.getDisplayName() : formattedTimeZone;
		}
		return  null;
	}

	/**
	 * Get an instance of TimeZoneInfo.
	 *
	 * @return an instance of the TimeZoneInfo class.
	 */
	public static TimeZoneInfo getInstance() {
		return CmSingletonUtil.getSessionInstance(TimeZoneInfo.class);
	}

	/**
	 * Sets the timezoneId for the application.
	 *
	 * @param timezoneId the timezoneId the application
	 */
	public void setTimezone(final String timezoneId) {
		this.usersTimezone = timezoneId;
		CookieUtil.setCookie(CM_TIMEZONE_ID, timezoneId);
	}

	/**
	 * Gets the user timezone.
	 * @return the user timezone, or their browser's timezone.
	 */
	public TimeZone getTimezone() {
		// check cookie, if timezone not set.
		if (usersTimezone == null) {
			usersTimezone = CookieUtil.getCookieValue(CM_TIMEZONE_ID);
		}
		//return browser timezone if == BROWSER, or not set.
		if (BROWSER.equals(usersTimezone) || usersTimezone == null) {
			return getBrowserTimezone();
		}

		//return timezone for not-null timezoneId
		return TimeZone.getTimeZone(usersTimezone);
	}

	/**
	 * Sets the timezone for the application.
	 *
	 * @param timeZone the timezone the application
	 */
	public void setTimezone(final TimeZone timeZone) {

		if (timeZone != null) {
			CookieUtil.setCookie(CM_TIMEZONE_ID, timeZone.getID());
			this.usersTimezone = timeZone.getID();
		}
	}

	/**
	 * Gets the current timezone Id.
	 * @return the currentTimezone, or BROWSER.
	 */
		public String getTimezoneId() {

			if (usersTimezone == null) {
				usersTimezone = CookieUtil.getCookieValue(CM_TIMEZONE_ID);
				if (usersTimezone == null) {
					usersTimezone = BROWSER;
				}
			}

			return usersTimezone;
		}

	/**
	 * Gets the timezone from the browser.
	 * @return the browser's timezone.
	 */
	public TimeZone getBrowserTimezone() {
		return DateTimeUtilFactory.getDateUtil().getTimeZoneFromBrowser();
	}

	private Map<String, String> getTimezoneFormats() {
		return ServiceLocator.getService(CMContextIdNames.TIMEZONE_FORMAT_MAP);
	}
}
