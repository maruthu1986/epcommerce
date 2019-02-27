/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.plugin.tax.manager;

import com.elasticpath.plugin.tax.domain.TaxDocument;
import com.elasticpath.plugin.tax.domain.TaxOperationContext;
import com.elasticpath.plugin.tax.domain.TaxableItemContainer;

/**
 * The internal API for high-level tax operations. The {@code TaxManager}  provides methods to calculate taxes for a
 * {@link TaxableItemContainer}, to commit the returned {@link TaxDocument} and to delete a previously committed
 * tax document.
 */
public interface TaxManager {

	/**
	 * Calculates taxes for a {@link TaxableItemContainer} and results the result in a {@link TaxDocument}.
	 * 
	 * @param taxableContainer the taxable container
	 * @return the created tax document
	 */
	TaxDocument calculate(TaxableItemContainer taxableContainer);

	/**
	 * Commits a {@link TaxDocument} to storage managed by {@link TaxDocumentArchiver}. Tax provider plugins
     * that implement the {@code StorageCapability} may also commit the document to their storage.
	 * 
	 * @param document the tax document
	 * @param taxOperationContext the context for the tax operation
	 */
	void commitDocument(TaxDocument document, TaxOperationContext taxOperationContext);
	
	/**
	 * Deletes a document from storage managed by {@link TaxDocumentArchiver}. Tax provider plugins
     * that implement {@code StorageCapability} may also delete the document from their storage.
	 * 
	 * @param document the tax document
	 * @param taxOperationContext the context for tax the operation
	 */
	void deleteDocument(TaxDocument document, TaxOperationContext taxOperationContext);
}
