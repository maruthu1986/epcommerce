/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.test;

import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;

import com.elasticpath.commons.util.Pair;

/**
 * A utility class to help the testing of Log4J output.
 */
public class TestLog4jLoggingAppender extends ConsoleAppender {

	private final Map<EventKey, State> called = new HashMap<>();

	/**
	 * Typedef for may key to keep the rest of the code cleaner.
	 */
	class EventKey extends Pair<Level, String> {
		private static final long serialVersionUID = 107774455892787679L;

		/**
		 * Creates an instance.
		 * @param level the logging level.
		 * @param message the logging message.
		 */
		EventKey(final Level level, final String message) {
			super(level, message);
		}
	}

	/**
	 * Verify that the expected log messages were received and that no unexpected
	 * messages were received.  Calls Assert.fail with details of the failure, if any.
	 */
	public void verify() {
		boolean result = true;
		for (final Map.Entry<EventKey, State> callEntry : called.entrySet()) {
			State state = callEntry.getValue();
			if (!state.shouldCall() && state.wasCalled()
					|| state.shouldCall() && !state.wasCalled()) {
				result = false;
			}
		}
		if (!result) {
			fail(getResults());
		}
	}

	private String getResults() {
		StringBuilder builder = new StringBuilder();
		for (final Map.Entry<EventKey, State> callEntry : called.entrySet()) {
			State state = callEntry.getValue();
			builder.append("Logging message '").append(state.getLevel()).append(": ").append(state.getMsg()).append(
					"' was ");
			if (state.shouldCall()) {
				builder.append("expected ");
			} else {
				builder.append("not expected ");
			}
			if (state.wasCalled()) {
				builder.append("and called");
			} else {
				builder.append("and was not called");
			}
			builder.append('\n');
		}
		return builder.toString();
	}

	/**
	 * Adds an logging expectation.
	 * @param level the level at which the message should be logged.
	 * @param msg the message that is expected to be logged.
	 */
	public void addMessageToVerify(final Level level, final String msg) {
		called.put(new EventKey(level, msg), new State(msg, true, false, level));
	}

	/**
	 * The tie into log4j - listen for log events and add them for later verification.
	 * @param loggingEvent the log event that the code under test has emitted.
	 */
	@Override
	@SuppressWarnings("PMD.AvoidSynchronizedAtMethodLevel")
	public synchronized void doAppend(final LoggingEvent loggingEvent) {
		EventKey key = new EventKey(loggingEvent.getLevel(), loggingEvent.getMessage().toString());
		if (called.containsKey(key)) {
			called.get(key).setCalled(true);
		} else {
			called.put(key, new State(loggingEvent.getMessage().toString(), false, true, loggingEvent.getLevel()));
		}
	}

	/**
	 * The details of a single log message.
	 */
	@SuppressWarnings("PMD.AvoidFieldNameMatchingMethodName")
	private class State {

		private final String msg;
		private final boolean shouldCall;
		private boolean called;
		private final Level level;

		State(final String msg, final boolean shouldCall,
				final boolean wasCalled, final Level level) {
			this.msg = msg;
			this.shouldCall = shouldCall;
			this.called = wasCalled;
			this.level = level;
		}

		public String getMsg() {
			return msg;
		}

		public boolean shouldCall() {
			return shouldCall;
		}

		public boolean wasCalled() {
			return called;
		}

		public void setCalled(final boolean called) {
			this.called = called;
		}

		public Level getLevel() {
			return level;
		}
	}
}
