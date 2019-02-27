/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.messaging.camel.jackson;

import com.elasticpath.messaging.EventType;
import com.elasticpath.messaging.spi.EventTypeLookup;

/**
 * A sample EventType implementation used for testing.
 */
public final class SampleEventType implements EventType {
	private static final long serialVersionUID = 7939445226007268335L;

	/** Instance used in testing. */
	public static final SampleEventType SAMPLE = new SampleEventType("SAMPLE");

	private final String name;

	/**
	 * Constructor.
	 * 
	 * @param name a name
	 */
	SampleEventType(final String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return getName();
	}

	/**
	 * Returns a {@link SampleEventType} instance corresponding to the given name.
	 * 
	 * @param name the name by which to look up a SampleEventType
	 * @return a SampleEventType instance
	 */
	public static SampleEventType valueOf(final String name) {
		return SAMPLE;
	}

	/**
	 * Lookup implementation for testing SampleEventType class.
	 */
	public static class SampleEventTypeLookup implements EventTypeLookup<SampleEventType> {
		@Override
		public SampleEventType lookup(final String name) {
			return SampleEventType.valueOf(name);
		}
	}

}
