/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.test.integration.shipping;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.shipping.connectivity.service.ShippingCalculationService;
import com.elasticpath.test.db.DbTestCase;

/**
 * Integration tests for {@link com.elasticpath.shipping.connectivity.service.ShippingCalculationService}.
 */
public class ShippingCalculationServiceImplTest extends DbTestCase {

	@Autowired
	private ShippingCalculationService shippingCalculationService;

	// We want to make sure that the shipping calculation methods run in separate transaction so that if any callers decide to handle exceptions
	// (because an external integration could not be stable and shipping options are not absolutely required by certain calls)
	// then the current transaction should not be marked for rollback since the exception has been handled

	@Test
	public void verifyGetUnpricedShippingOptionsRunsInSeparateTransaction() {
		verifyMethodRunsInSeparateTransaction(() -> shippingCalculationService.getUnpricedShippingOptions(null));
	}

	@Test
	public void verifyGetPricedShippingOptionsRunsInSeparateTransaction() {
		verifyMethodRunsInSeparateTransaction(() -> shippingCalculationService.getPricedShippingOptions(null));
	}

	private void verifyMethodRunsInSeparateTransaction(final Runnable runnableThrowingNPE) {
		getTxTemplate().execute(status -> {
			try {
				runnableThrowingNPE.run();
				fail("We should not have got here, as a NullPointerException should have been thrown");
			} catch (final NullPointerException e) {
				// Do nothing as expected
			}

			assertFalse("The outer transaction should not be marked for rollback even if an exception was thrown by ShippingCalculationService as "
							+ "ShippingCalculationService should be run in a separate transaction to allow for recovery in non-fatal scenarios.",
					status.isRollbackOnly());

			return null;
		});
	}
}
