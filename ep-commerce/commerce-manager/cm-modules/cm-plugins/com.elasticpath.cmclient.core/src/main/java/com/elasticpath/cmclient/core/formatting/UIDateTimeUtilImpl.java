/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.formatting;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.service.ClientInfo;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.util.CookieUtil;
import com.elasticpath.cmclient.core.util.DateTimeUtilFactory;
import com.elasticpath.service.misc.TimeService;

/**
 * Provides an uniform way to display date & date time all across cm client.
 */
public class UIDateTimeUtilImpl implements UIDateTimeUtil {
	private static final Logger LOG = Logger.getLogger(UIDateTimeUtilImpl.class);

	private static final int MINUTES_TO_HOURS = 60;

	@Override
	public String formatAsDate(final Date date) {
		return formatAsDate(date, StringUtils.EMPTY);
	}

	@Override
	public String formatAsDate(final Date date, final String nullDateRepresentation) {
		return formatDateTime(date, getDefaultDateFormatter(), nullDateRepresentation);
	}

	@Override
	public String formatAsDate(final Date date, final TimeZone timezone) {
		DateFormat formatter = getDefaultDateFormatter();
		formatter.setTimeZone(timezone);
		return formatDateTime(date, formatter, StringUtils.EMPTY);
	}

	@Override
	public String formatAsDateTime(final Date date) {
		return formatAsDateTime(date, StringUtils.EMPTY);
	}

	@Override
	public String formatAsDateTime(final Date date, final String nullDateRepresentation) {
		return formatDateTime(date, getDefaultDateTimeFormatter(), nullDateRepresentation);
	}

	@Override
	public String formatAsDateTime(final Date date, final TimeZone timezone) {
		DateFormat formatter = getDefaultDateTimeFormatter();
		formatter.setTimeZone(timezone);
		return formatDateTime(date, formatter, StringUtils.EMPTY);
	}

	@Override
	public Date getDateWithFormat(final Date date) {
		return new FormattedDate(date);
	}

	@Override
	public Date parseDate(final String dateString) throws ParseException {
		return getDefaultDateFormatter().parse(dateString);
	}

	@Override
	public Date parseDate(final String dateString, final ParsePosition parsePosition) {
		return getDefaultDateFormatter().parse(dateString, parsePosition);
	}

	@Override
	public Date parseDateTime(final String dateString, final ParsePosition parsePosition) {
		return getDefaultDateTimeFormatter().parse(dateString, parsePosition);
	}

	@Override
	public Date parseDateTime(final String dateString) throws ParseException {
		return getDefaultDateTimeFormatter().parse(dateString);
	}

	@Override
	public DateFormat[] getFormatters() {
		return new DateFormat[]{getDefaultDateFormatter(), getDefaultDateTimeFormatter()};
	}


	@Override
	public TimeZone getTimeZoneFromBrowser() {
		ClientInfo service = RWT.getClient().getService(ClientInfo.class);

		int timezoneOffset = service.getTimezoneOffset();
		int hours = timezoneOffset / MINUTES_TO_HOURS;
		int minutes = timezoneOffset - (hours * MINUTES_TO_HOURS);
		ZoneOffset zoneOffset = ZoneOffset.ofHoursMinutes(-hours, -minutes);
		return TimeZone.getTimeZone(ZoneId.of(zoneOffset.getId()));
	}


	/**
	 * Formats a date to the given format and returns nullDateRepresentation in case of null date.
	 *
	 * @param object                 the object to be formatted
	 * @param dateFormat             the date format
	 * @param nullDateRepresentation the null date representation
	 * @return the string formatted date
	 */
	protected static String formatDateTime(final Object object, final DateFormat dateFormat, final String nullDateRepresentation) {
		if (object == null) {
			return nullDateRepresentation;
		}
		return dateFormat.format(object);
	}

	/**
	 * Returns the singleton instance of a Date & Time formatter.
	 *
	 * @return DateFormat
	 */
	protected DateFormat getDefaultDateTimeFormatter() {

		DateFormat dateTimeFormatter = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, getDefaultLocale());

		TimeZone timeZone = getUsersTimezone();

		dateTimeFormatter.setTimeZone(timeZone);
		return dateTimeFormatter;
	}

	private TimeZone getUsersTimezone() {
		TimeZone timezone = TimeZone.getDefault();
		try {
			timezone = TimeZoneInfo.getInstance().getTimezone();
		} catch (Exception e) {
			LOG.warn("Could not get user's timezone. Probably accessing it from a service thread");
		}
		return timezone;
	}

	/**
	 * Checks if there is a timezone cookie set for the user, and sets it to 'BROWSER' if not set.
	 */
	@Override
	public void initializeTimezone() {
		String usersTimezone = CookieUtil.getCookieValue(TimeZoneInfo.CM_TIMEZONE_ID);
		if (StringUtils.isEmpty(usersTimezone)) {
			CookieUtil.setCookie(TimeZoneInfo.CM_TIMEZONE_ID, TimeZoneInfo.BROWSER);
		}

		TimeService timeservice = ServiceLocator.getService("timeService");
		timeservice.getCurrentTime();
	}

	/**
	 * Returns the session instance of a Date formatter.
	 *
	 * @return DateFormat
	 */
	protected DateFormat getDefaultDateFormatter() {
		DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.MEDIUM, getDefaultLocale());

		TimeZone timeZone = getUsersTimezone();

		dateFormatter.setTimeZone(timeZone);

		return dateFormatter;
	}

	/**
	 * Retrieves the current value of the default locale for this instance of the JVM.
	 *
	 * @return the current value of the default locale for this instance of the JVM.
	 */
	protected Locale getDefaultLocale() {
		Locale locale = RWT.getClient().getService(ClientInfo.class).getLocale();
		if (locale == null) {
			return Locale.getDefault();
		}
		return locale;

	}


	/**
	 * Helper class for maintaining a Date object to be passed to the table viewer, but use our own formatting to display.
	 */
	class FormattedDate extends Date {
		/**
		 * Constructor.
		 *
		 * @param date the date
		 */
		FormattedDate(final Date date) {
			super(date.getTime());
		}

		@Override
		public String toString() {
			return DateTimeUtilFactory.getDateUtil().formatAsDate(this);
		}
	}

}
