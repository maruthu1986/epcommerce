/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails.shippingoption.prototypes;

import io.reactivex.Single;

import com.elasticpath.rest.definition.shipmentdetails.ShippingOptionInfoSelectorResource;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipmentdetails.ShipmentDetailsConstants;
import com.elasticpath.rest.selector.SelectorEntity;

/**
 * Read shipping option info selector.
 */
public class ReadShippingOptionInfoSelectorPrototype implements ShippingOptionInfoSelectorResource.Select {

	@Override
	public Single<SelectorEntity> onRead() {
		return Single.just(SelectorEntity.builder()
				.withSelectionRule(ShipmentDetailsConstants.SELECTION_RULE)
				.withName(ShipmentDetailsConstants.SHIPPING_OPTION_SELECTOR_NAME)
				.build());
	}
}
