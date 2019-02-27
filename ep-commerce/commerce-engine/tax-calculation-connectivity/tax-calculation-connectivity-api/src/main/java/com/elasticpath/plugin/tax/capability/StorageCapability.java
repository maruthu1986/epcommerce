/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.plugin.tax.capability;

import com.elasticpath.plugin.tax.domain.TaxDocument;
import com.elasticpath.plugin.tax.domain.TaxOperationContext;

/**
 * A capability that Tax providers which support the committing a tax document should implement.
 *
 */
public interface StorageCapability extends TaxProviderCapability {
	
	/**
	 * Archives the tax document for this provider.
	 *
	 * @param taxDocument the tax document
	 * @param taxOperationContext the tax operation context
	 */
	void archive(TaxDocument taxDocument, TaxOperationContext taxOperationContext);
	
	/**
	 * Deletes/void the tax document for this provider.
	 *
	 * @param taxDocument the tax document
	 * @param taxOperationContext the tax operation context
	 */
	void delete(TaxDocument taxDocument, TaxOperationContext taxOperationContext);
	
}
