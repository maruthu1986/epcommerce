/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.uat.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@link com.elasticpath.uat.impl.ScenarioContextValueHolderImpl}.
 */
public class ScenarioContextValueHolderImplTest {

	private ScenarioContextValueHolderImpl<Object> contextValueHolder;

	@Before
	public void setUp() {
		contextValueHolder = new ScenarioContextValueHolderImpl<>();
	}

	@Test
	public void verifyGetBeforeSetReturnsNull() throws Exception {
		assertNull("Expected default value to be null", contextValueHolder.get());
	}

	@Test
	public void verifyGetWillReturnWhateverWasSet() throws Exception {
		final Object expected = "foo";
		contextValueHolder.set(expected);
		assertEquals("Unexpected value returned from get", expected, contextValueHolder.get());
	}

	@Test
	public void verifyContainedValueCanBeUpdated() throws Exception {
		final Object expected = "foo";

		contextValueHolder.set("bar");
		contextValueHolder.set(expected);

		assertEquals("Unexpected value returned from get", expected, contextValueHolder.get());
	}

}