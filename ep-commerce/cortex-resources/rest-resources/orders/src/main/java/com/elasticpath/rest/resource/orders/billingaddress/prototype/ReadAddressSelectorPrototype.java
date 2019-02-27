/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.orders.billingaddress.prototype;

import io.reactivex.Single;

import com.elasticpath.rest.definition.orders.BillingaddressInfoSelectorResource;
import com.elasticpath.rest.selector.SelectorEntity;

/**
 * READ operation on the {@link BillingaddressInfoSelectorResource} for selecting a choice and promoting it to be chosen.
 */
public class ReadAddressSelectorPrototype implements BillingaddressInfoSelectorResource.Select {

	private static final String SELECTOR_NAME = "billing-address-selector";

	private static final String SELECTION_RULE = "1";

	@Override
	public Single<SelectorEntity> onRead() {
		return Single.just(
				SelectorEntity
						.builder()
						.withName(SELECTOR_NAME)
						.withSelectionRule(SELECTION_RULE)
						.build());
	}
}
