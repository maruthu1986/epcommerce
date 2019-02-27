/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.plugin.tax.resolver;

/**
 * A marker interface that is passed to tax plugins to provide access to EP platform tax services.
 * Currently implemented by @{link TaxRateDescriptorResolver}s and @{link TaxDocumentResolver}s, but may be extended to
 * provide additional platform functionality to tax plugins.
 */
public interface TaxOperationResolver {

}
