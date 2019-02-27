/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.service.tax;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.domain.order.OrderAddress;
import com.elasticpath.domain.order.OrderReturn;
import com.elasticpath.plugin.tax.domain.TaxDocument;

/**
 * A service that performs the taxes related operations for a <code>OrderReturn</code>.
 */
public interface ReturnTaxOperationService {

	/**
	 * Calculates tax for this order return.
	 * 
	 * @param orderReturn the given order return
	 * @throws EpServiceException - in case of any errors
	 * @return the tax calculation result
	 *
	 */
	TaxCalculationResult calculateTaxes(OrderReturn orderReturn) throws EpServiceException;
	
	/**
	 * Commits the tax calculation document for the given order return.
	 *
	 * @param taxDocument the taxDocument
	 * @param orderReturn the orderReturn
	 * @throws EpServiceException - in case of any errors
	 */
	void commitDocument(TaxDocument taxDocument, OrderReturn orderReturn) throws EpServiceException;

	/**
	 * Returns the given order return tax document using its tax journal records.
	 *
	 * @param orderReturn the orderReturn
	 * @param address the given order return address
	 * @throws EpServiceException - in case of any errors
	 */
	void reverseTaxes(OrderReturn orderReturn, OrderAddress address) throws EpServiceException;
	
	/**
	 * Updated the order return taxes to reflect the order return's tax related changes.
	 * 
	 * @param orderReturn the orderReturn with tax changes
	 * @param taxDocumentModificationContext the tax document modification context
	 * @throws EpServiceException - in case of any errors
	 */
	void updateTaxes(OrderReturn orderReturn, TaxDocumentModificationContext taxDocumentModificationContext) throws EpServiceException;

}
