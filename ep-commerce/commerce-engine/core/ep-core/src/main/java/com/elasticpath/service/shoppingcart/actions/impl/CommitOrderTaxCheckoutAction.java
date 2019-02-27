/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.service.shoppingcart.actions.impl;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.shipping.ShipmentType;
import com.elasticpath.plugin.tax.domain.TaxDocument;
import com.elasticpath.service.shoppingcart.actions.CheckoutActionContext;
import com.elasticpath.service.shoppingcart.actions.ReversibleCheckoutAction;
import com.elasticpath.service.tax.TaxOperationService;

/**
 * Commits the taxes by committing order related tax documents using the tax manager.
 */
public class CommitOrderTaxCheckoutAction implements ReversibleCheckoutAction {

	private TaxOperationService taxOperationService;
	
	/**
	 * Commits all tax documents for the existing order.
	 * 
	 * @param context the context
	 * @throws EpSystemException on error
	 */
	@Override
	public void execute(final CheckoutActionContext context) throws EpSystemException {
		
		//Exchange orders' tax documents are committed with its OrderReturn by <code>ReturnAndExchangeService<code>
		if (context.getOrder().isExchangeOrder()) {
			return;
		}
		
		for (OrderShipment shipment : context.getOrder().getAllShipments()) {
			if (!ShipmentType.SERVICE.equals(shipment.getOrderShipmentType())) {
				
				TaxDocument	taxDocument = shipment.calculateTaxes().getTaxDocument();
				context.getTaxDocuments().put(taxDocument.getDocumentId(), taxDocument);
				
				getTaxOperationService().commitDocument(taxDocument, shipment);
			}
		}
	}
	
	/**
	 * Deletes all the tax documents for the existing order.
	 * 
	 * @param context the context
	 * @throws EpSystemException on error
	 */
	@Override
	public void rollback(final CheckoutActionContext context) throws EpSystemException {
		
		if (context.getOrder().isExchangeOrder()) {
			return;
		}
		
		for (TaxDocument taxDocument : context.getTaxDocuments().values()) {
			getTaxOperationService().deleteDocument(taxDocument);
		}
	}

	/**
	 * @return the taxOperationService
	 */
	protected TaxOperationService getTaxOperationService() {
		return taxOperationService;
	}

	/**
	 * @param taxOperationService the taxOperationService to set
	 */
	public void setTaxOperationService(final TaxOperationService taxOperationService) {
		this.taxOperationService = taxOperationService;
	}
}
