/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shipping.transformers;

import java.util.function.Function;

import com.elasticpath.domain.customer.Address;
import com.elasticpath.shipping.connectivity.dto.ShippingAddress;

/**
 * Interface defining adapter from {@link Address} to {@link ShippingAddress}.
 */
public interface ShippingAddressTransformer extends Function<Address, ShippingAddress> {
}
