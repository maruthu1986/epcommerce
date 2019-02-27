/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.schema.uri;

import javax.inject.Provider;

/**
 * A factory for creating {@link AddressFormUriBuilder} objects.
 *
 * @deprecated remove once dependent resources are converted to Helix.
 */
@Deprecated
public interface AddressFormUriBuilderFactory extends Provider<AddressFormUriBuilder> {

}
