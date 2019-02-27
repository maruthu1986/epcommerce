/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.test.util;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * Utility timer to track time to run a test.
 */
public final class TestTimer {

	private static final Logger LOG = Logger.getLogger(TestTimer.class);

	private static final Map<String, Long> TIMER_MAP = new HashMap<>();

	private TestTimer() {
	}

	/**
	 * Starts new timer.
	 *
	 * @param timerName name of the timer.
	 */
	public static void start(final String timerName) {
		TIMER_MAP.put(timerName, System.currentTimeMillis());
		if (LOG.isDebugEnabled()) {
			LOG.debug("start() - elasped time:" + formatTime(0L) + " - " + timerName);
		}
	}

	/**
	 * Stops the timer.
	 *
	 * @param timerName name of the timer.
	 * @return time as <code>Long</code>
	 */
	public static Long stop(final String timerName) {
		long time = System.currentTimeMillis() - TIMER_MAP.remove(timerName);
		if (LOG.isDebugEnabled()) {
			LOG.debug("stop() - elasped time:" + formatTime(time) + " - " + timerName);
		}
		return time;
	}

	/**
	 * Splits timer.
	 *
	 * @param timerName name of the timer.
	 * @return time as <code>Long</code>
	 */
	public static Long split(final String timerName) {
		long time = System.currentTimeMillis() - TIMER_MAP.get(timerName);
		if (LOG.isDebugEnabled()) {
			LOG.debug("split() - elasped time:" + formatTime(time) + " - " + timerName);
		}

		return time;
	}

	/**
	 * Formats <code>Long</code> as <code>String</code>.
	 *
	 * @param time to format
	 * @return <code>String</code> representaion of time.
	 */
	public static String formatTime(final Long time) {
		final int thousand = 1000;
		final int sixtySeconds = 60;
		long seconds = time / thousand;
		long hours = seconds / (sixtySeconds * sixtySeconds);
		seconds = seconds % (sixtySeconds * sixtySeconds);
		long minutes = seconds / sixtySeconds;
		seconds = seconds % sixtySeconds;
		long millis = time % thousand;
		NumberFormat numformat = NumberFormat.getInstance();
		StringBuilder builder = new StringBuilder();
		numformat.setMinimumIntegerDigits(2);
		builder.append(numformat.format(hours)).append("h:");
		builder.append(numformat.format(minutes)).append("m:");
		builder.append(numformat.format(seconds)).append("s:");
		final int minInteger = 3;
		numformat.setMinimumIntegerDigits(minInteger);
		builder.append(numformat.format(millis)).append("ms");
		return builder.toString();
	}

}
