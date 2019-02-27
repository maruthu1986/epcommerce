/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.schema.uri;


import javax.inject.Provider;

/**
 * A factory for creating {@link com.elasticpath.rest.schema.uri.ShippingAddressUriBuilder}s.
 */
public interface ShippingAddressUriBuilderFactory extends Provider<ShippingAddressUriBuilder> {
}
