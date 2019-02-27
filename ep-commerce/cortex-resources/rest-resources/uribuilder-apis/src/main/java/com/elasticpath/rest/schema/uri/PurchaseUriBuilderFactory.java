/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.schema.uri;

import javax.inject.Provider;

/**
 * A factory for creating {@link PurchaseUriBuilder} instances.
 *
 * @deprecated remove once dependent resources are converted to Helix.
 */
@Deprecated
public interface PurchaseUriBuilderFactory extends Provider<PurchaseUriBuilder> {

	// Marker interface
	
}
