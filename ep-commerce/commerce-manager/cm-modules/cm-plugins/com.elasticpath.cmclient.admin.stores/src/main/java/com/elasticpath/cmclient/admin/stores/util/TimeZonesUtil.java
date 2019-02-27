/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.admin.stores.util;

import java.util.Formatter;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;
import java.util.TreeMap;

import org.eclipse.osgi.util.NLS;

import com.elasticpath.cmclient.admin.stores.AdminStoresMessages;

//TODO: consider to place this class to cmclient.core utils.
/**
 * Class contains default time zones for each unique time.
 */
public final class TimeZonesUtil {

	private static final int FIRST_GMT = -12;

	private static final int LAST_GMT = 14;

	private static int indexTimeZone = FIRST_GMT;

	private static Map<Integer, String> timeZoneIdsMap = new TreeMap<>();

	private TimeZonesUtil() {
		// empty constructor
	}

	static {
		timeZoneIdsMap.put(indexTimeZone++, "Etc/GMT+12"); //$NON-NLS-1$
		timeZoneIdsMap.put(indexTimeZone++, "US/Samoa"); //$NON-NLS-1$
		timeZoneIdsMap.put(indexTimeZone++, "Pacific/Honolulu"); //$NON-NLS-1$
		timeZoneIdsMap.put(indexTimeZone++, "America/Anchorage"); //$NON-NLS-1$
		timeZoneIdsMap.put(indexTimeZone++, "America/Vancouver"); //$NON-NLS-1$
		timeZoneIdsMap.put(indexTimeZone++, "America/Denver"); //$NON-NLS-1$
		timeZoneIdsMap.put(indexTimeZone++, "America/Chicago"); //$NON-NLS-1$
		timeZoneIdsMap.put(indexTimeZone++, "America/New_York"); //$NON-NLS-1$
		timeZoneIdsMap.put(indexTimeZone++, "America/Halifax"); //$NON-NLS-1$
		timeZoneIdsMap.put(indexTimeZone++, "America/Argentina/Buenos_Aires"); //$NON-NLS-1$
		timeZoneIdsMap.put(indexTimeZone++, "America/Noronha"); //$NON-NLS-1$
		timeZoneIdsMap.put(indexTimeZone++, "America/Scoresbysund"); //$NON-NLS-1$
		timeZoneIdsMap.put(indexTimeZone++, "Europe/London"); //$NON-NLS-1$
		timeZoneIdsMap.put(indexTimeZone++, "Europe/Paris"); //$NON-NLS-1$
		timeZoneIdsMap.put(indexTimeZone++, "Europe/Bucharest"); //$NON-NLS-1$
		timeZoneIdsMap.put(indexTimeZone++, "Europe/Moscow"); //$NON-NLS-1$
		timeZoneIdsMap.put(indexTimeZone++, "Asia/Dubai"); //$NON-NLS-1$
		timeZoneIdsMap.put(indexTimeZone++, "Asia/Tashkent"); //$NON-NLS-1$
		timeZoneIdsMap.put(indexTimeZone++, "Asia/Omsk"); //$NON-NLS-1$
		timeZoneIdsMap.put(indexTimeZone++, "Asia/Jakarta"); //$NON-NLS-1$
		timeZoneIdsMap.put(indexTimeZone++, "Asia/Hong_Kong"); //$NON-NLS-1$
		timeZoneIdsMap.put(indexTimeZone++, "Asia/Tokyo"); //$NON-NLS-1$
		timeZoneIdsMap.put(indexTimeZone++, "Australia/Sydney"); //$NON-NLS-1$
		timeZoneIdsMap.put(indexTimeZone++, "Pacific/Noumea"); //$NON-NLS-1$
		timeZoneIdsMap.put(indexTimeZone++, "Pacific/Auckland"); //$NON-NLS-1$
		timeZoneIdsMap.put(indexTimeZone++, "Pacific/Enderbury"); //$NON-NLS-1$
		timeZoneIdsMap.put(indexTimeZone, "Pacific/Kiritimati"); //$NON-NLS-1$
	}

	/**
	 * Get Available Map of TimeZone ids and Time Zone names in selected locale.
	 *
	 * @param locale the selected locale
	 * @return <code>Map</code> between time zone id and time zone name in selected locale
	 */
	public static Map<String, String> getAvailableTimeZones(final Locale locale) {
		Map<String, String> timeZonesMap = new LinkedHashMap<>();
		for (Entry<Integer, String> entry : timeZoneIdsMap.entrySet()) {
			timeZonesMap.put(entry.getValue(), getTimeZoneName(entry.getKey(), locale));
		}
		return timeZonesMap;
	}

	/**
	 * Get time zone id by offset.
	 *
	 * @param offset the offset in hours
	 * @return time zone id
	 */
	public static String getTimeZoneId(final int offset) {
		return timeZoneIdsMap.get(offset);
	}

	/**
	 * Get time zone name by offset in selected locale.
	 *
	 * @param offset the offset in hours
	 * @param locale the selected locale
	 * @return time zone name by offset in selected locale
	 */
	public static String getTimeZoneName(final int offset, final Locale locale) {
		String result = null;
		if (offset >= FIRST_GMT && offset <= LAST_GMT) {
			TimeZone timeZone = TimeZone.getTimeZone(timeZoneIdsMap.get(offset));
			StringBuffer offsetString = new StringBuffer();

			try (Formatter formatter = new Formatter(offsetString)) {
				formatter.format("%+d:00", offset); //$NON-NLS-1$
			}

			result =
				NLS.bind(AdminStoresMessages.get().GmtLabel,
				new String[]{offsetString.toString(),
				timeZone.getDisplayName(false, TimeZone.LONG, locale)});
		}
		return result;
	}

}
