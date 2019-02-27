/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.taxes.transform;

import java.util.Collection;

import io.reactivex.Single;

import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.order.TaxJournalRecord;
import com.elasticpath.rest.definition.taxes.TaxesEntity;

/**
 * Creates {@link TaxesEntity} for a single shipment line item.
 */
public interface ShipmentLineItemTaxesEntityTransformer {

	/**
	 * Returns a new {@link TaxesEntity} based on the given {@link TaxJournalRecord}s.
	 *
	 * @param orderSku the line item's {@link OrderSku}
	 * @param taxJournalRecords a collection of {@link TaxJournalRecord}s detailing the per tax amount breakdown
	 * @return the {@link TaxesEntity}
	 */
	Single<TaxesEntity> transform(OrderSku orderSku, Collection<TaxJournalRecord> taxJournalRecords);
}
