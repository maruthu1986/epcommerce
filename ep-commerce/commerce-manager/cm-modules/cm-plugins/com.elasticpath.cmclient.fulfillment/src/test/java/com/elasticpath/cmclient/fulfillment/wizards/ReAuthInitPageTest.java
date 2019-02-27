/*
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.cmclient.fulfillment.wizards;

import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.Rule;
import org.junit.Test;

import org.eclipse.rap.rwt.testfixture.TestContext;

import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.impl.OrderImpl;

/**
 * Test class for the ReAuthInitPage.
 */
public class ReAuthInitPageTest {

	@Rule
	public TestContext context = new TestContext();

	/**
	 * There should be no reusable payments since credit cards are no longer stored.
	 */
	@Test
	public void testGetReusablePaymentsNoReusablePayments() {
		Collection<OrderPayment> result = new ReAuthInitPage("", null, new OrderImpl()).getReusablePayments(); //$NON-NLS-1$
		assertTrue(result.isEmpty());
	}
}
