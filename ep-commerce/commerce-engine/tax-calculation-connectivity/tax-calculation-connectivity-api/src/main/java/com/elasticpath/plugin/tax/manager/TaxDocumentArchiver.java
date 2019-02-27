/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.plugin.tax.manager;

import com.elasticpath.plugin.tax.domain.TaxDocument;
import com.elasticpath.plugin.tax.domain.TaxOperationContext;
import com.elasticpath.plugin.tax.resolver.TaxOperationResolvers;

/**
 * Interface for archiving/deleting {@link TaxDocument}s to/from storage.
 */
public interface TaxDocumentArchiver {

	/**
	 * Persists a tax document.
	 *
	 * @param document the tax document
	 * @param taxOperationContext the tax operation context
	 * @param taxOperationResolvers the tax operation resolvers
	 */
	void archive(TaxDocument document, TaxOperationContext taxOperationContext, TaxOperationResolvers taxOperationResolvers);
	
	/**
	 * Deletes a previously persisted tax document.
	 *
	 * @param document the tax document
	 */
	void delete(TaxDocument document);
	
}
