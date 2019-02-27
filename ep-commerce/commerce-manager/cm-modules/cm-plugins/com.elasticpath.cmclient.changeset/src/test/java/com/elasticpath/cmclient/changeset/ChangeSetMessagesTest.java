/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.cmclient.changeset;

import static org.junit.Assert.assertEquals;

import org.apache.commons.lang.StringUtils;

import org.junit.Rule;
import org.junit.Test;
import org.eclipse.rap.rwt.testfixture.TestContext;

import com.elasticpath.cmclient.ChangeSetTestBase;
import com.elasticpath.domain.changeset.ChangeSetStateCode;

/**
 * A test case for {@link ChangeSetMessages}.
 */
public class ChangeSetMessagesTest extends ChangeSetTestBase {

	@Rule
	public TestContext context = new TestContext();

	/**
	 * Tests that messages can be retrieved for {@link ChangeSetStateCode}.
	 */
	@Test
	public void testGetMessage() {
		ChangeSetMessages.get().FINALIZED = "Closed"; //$NON-NLS-1$

		assertEquals(ChangeSetMessages.get().FINALIZED, ChangeSetMessages.get().getMessage(ChangeSetStateCode.FINALIZED));
	}

	/**
	 * Tests that messages can be retrieved for {@link ChangeSetStateCode}.
	 */
	@Test
	public void testGetMessageNoValue() {
		ChangeSetMessages.get().LOCKED = null;

		assertEquals(ChangeSetStateCode.LOCKED.getName(), ChangeSetMessages.get().getMessage(ChangeSetStateCode.LOCKED));
	}

	/**
	 * Tests that null argument won't fail and will return an empty string.
	 */
	@Test
	public void testGetMessageNullArgument() {
		assertEquals(StringUtils.EMPTY, ChangeSetMessages.get().getMessage(null));
	}

}
