/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.messaging.predicate.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.Before;
import org.junit.Test;

import com.elasticpath.messaging.EventMessage;
import com.elasticpath.messaging.EventType;
import com.elasticpath.messaging.impl.EventMessageImpl;

/**
 * Test class for {@link CompatibleEventTypePredicate}.
 */
public class CompatibleEventTypePredicateTest {

	private CompatibleEventTypePredicate predicate;

	@Before
	public void setUp() {
		predicate = new CompatibleEventTypePredicate();
	}

	@Test
	public void testApplyReturnsTrueWhenEventMessageHasCompatibleEventType() throws Exception {
		predicate.setCompatibleEventTypes(new BasicEventType("one"), new BasicEventType("two"), new BasicEventType("three"));

		final EventMessage eventMessage = new EventMessageImpl(new BasicEventType("two"), "guid");

		assertTrue("Predicate did not return true for message with compatible Event Type", predicate.apply(eventMessage));
	}

	@Test
	public void testApplyReturnsFalseWhenEventMessageHasIncompatibleEventType() throws Exception {
		predicate.setCompatibleEventTypes(new BasicEventType("alpha"), new BasicEventType("beta"), new BasicEventType("gamma"));

		final EventMessage eventMessage = new EventMessageImpl(new BasicEventType("omega"), "guid");

		assertFalse("Predicate did not return false for message with incompatible Event Type", predicate.apply(eventMessage));
	}

	private final class BasicEventType implements EventType {
		private static final long serialVersionUID = -5294655962068485522L;
		private final String name;

		BasicEventType(final String name) {
			this.name = name;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public boolean equals(final Object that) {
			return EqualsBuilder.reflectionEquals(this, that);
		}

		@Override
		public int hashCode() {
			return name.hashCode();
		}
	}

}
