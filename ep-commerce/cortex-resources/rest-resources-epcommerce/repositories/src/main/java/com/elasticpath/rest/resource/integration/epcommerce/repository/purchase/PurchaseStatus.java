/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.purchase;

/**
 * The set of purchase states.
 */
public enum PurchaseStatus {

	/**
	 * The initial state of the purchase and requires further processing.
	 */
	IN_PROGRESS,

	/**
	 * There is nothing remaining to do for this purchase.
	 */
	COMPLETED,

	/**
	 * Purchase has been canceled.
	 */
	CANCELED
}
