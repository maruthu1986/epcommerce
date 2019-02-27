/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails.destinationinfo.prototypes;

import io.reactivex.Single;

import com.elasticpath.rest.definition.shipmentdetails.DestinationInfoSelectorResource;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipmentdetails.ShipmentDetailsConstants;
import com.elasticpath.rest.selector.SelectorEntity;

/**
 * Read destination info selector.
 */
public class SelectDestinationInfoSelectorResourcePrototype implements DestinationInfoSelectorResource.Select {

	@Override
	public Single<SelectorEntity> onRead() {
		return Single.just(SelectorEntity.builder()
				.withName(ShipmentDetailsConstants.DESTINATION_SELECTOR_NAME)
				.withSelectionRule(ShipmentDetailsConstants.SELECTION_RULE)
				.build());
	}
}
