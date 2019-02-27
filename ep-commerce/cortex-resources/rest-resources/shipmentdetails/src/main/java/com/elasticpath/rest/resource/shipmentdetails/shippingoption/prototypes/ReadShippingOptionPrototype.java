/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.shipmentdetails.shippingoption.prototypes;

import javax.inject.Inject;

import io.reactivex.Single;

import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.shipmentdetails.ShippingOptionEntity;
import com.elasticpath.rest.definition.shipmentdetails.ShippingOptionIdentifier;
import com.elasticpath.rest.definition.shipmentdetails.ShippingOptionResource;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;

/**
 * Read a shipping option info.
 */
public class ReadShippingOptionPrototype implements ShippingOptionResource.Read {

	private final ShippingOptionIdentifier shippingOptionIdentifier;
	private final Repository<ShippingOptionEntity, ShippingOptionIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param shippingOptionIdentifier	identifier
	 * @param repository				repository
	 */
	@Inject
	public ReadShippingOptionPrototype(@RequestIdentifier final ShippingOptionIdentifier shippingOptionIdentifier,
									   @ResourceRepository final Repository<ShippingOptionEntity, ShippingOptionIdentifier> repository) {
		this.shippingOptionIdentifier = shippingOptionIdentifier;
		this.repository = repository;
	}

	@Override
	public Single<ShippingOptionEntity> onRead() {
		return repository.findOne(shippingOptionIdentifier);
	}
}
