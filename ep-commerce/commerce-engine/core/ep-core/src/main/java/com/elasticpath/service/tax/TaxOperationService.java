/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.service.tax;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderAddress;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.plugin.tax.domain.TaxDocument;
import com.elasticpath.plugin.tax.domain.TaxOperationContext;

/**
 * A service that performs the taxes related operations for a <code>Order</code> and <code>OrderShipment</code>.
 */
public interface TaxOperationService {

	/**
	 * Calculates tax for the given shipment.
	 * 
	 * @param orderShipment the given order shipment
	 * @throws EpServiceException - in case of any errors
	 * @return the tax calculation result
	 *
	 */
	TaxCalculationResult calculateTaxes(OrderShipment orderShipment) throws EpServiceException;
	
	/**
	 * Calculates tax for the given shipment and a given tax operation context.
	 * 
	 * @param orderShipment the given order shipment
	 * @param taxOperationContext the tax operation context
	 * 
	 * @throws EpServiceException - in case of any errors
	 * @return the tax calculation result
	 *
	 */
	TaxCalculationResult calculateTaxes(OrderShipment orderShipment, TaxOperationContext taxOperationContext) throws EpServiceException;
	
	/**
	 * Returns the given order taxes.
	 *
	 * @param order the order to add
	 * @throws EpServiceException - in case of any errors
	 */
	void reverseTaxes(Order order) throws EpServiceException;

	/**
	 * Returns the given order shipment tax document using its tax journal records.
	 *
	 * @param orderShipment the orderShipment
	 * @param address the given order shipment address
	 * @throws EpServiceException - in case of any errors
	 */
	void reverseTaxes(OrderShipment orderShipment, OrderAddress address) throws EpServiceException;
	
	/**
	 * Commits the tax calculation document for the given order shipment.
	 *
	 * @param taxDocument the taxDocument
	 * @param orderShipment the orderShipmen
	 * @throws EpServiceException - in case of any errors
	 */
	void commitDocument(TaxDocument taxDocument, OrderShipment orderShipment) throws EpServiceException;

	/**
	 * Updated the order taxes to reflect the order's tax related changes.
	 * 
	 * @param order the order with tax changes
	 * @param taxDocumentModificationContext the tax document modification context
	 */
	void updateTaxes(Order order, TaxDocumentModificationContext taxDocumentModificationContext);
	
	/**
	 * Deletes tax document from tax archiver.
	 * 
	 * @param taxDocument the tax document
	 * @throws EpServiceException - in case of any errors
	 */
	void deleteDocument(TaxDocument taxDocument) throws EpServiceException;
	
}
