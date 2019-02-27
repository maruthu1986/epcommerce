/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.service.tax;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.order.OrderReturn;
import com.elasticpath.domain.order.OrderShipment;

/**
 * Container class for data required by tax documents modification.
 *
 */
public class TaxDocumentModificationContext implements Serializable {
	
	/**
	 * Serial version id. 
	 */
	private static final long serialVersionUID = 500000000001L;
	
	private final Map<String, TaxDocumentModificationItem> taxDocumentModificationItems = new HashMap<>();
	
	/**
	 * Adds order shipment for modification.
	 * 
	 * @param orderShipment the order shipment
	 * @param address the order shipment address
	 * @param modificationType the tax document modification type
	 */
	public void add(final OrderShipment orderShipment, final Address address, final TaxDocumentModificationType modificationType) {
		
		String taxDocumentReferenceId = orderShipment.getShipmentNumber();
		TaxDocumentModificationItem item = get(taxDocumentReferenceId);		
		
		item.setTaxDocumentReferenceId(taxDocumentReferenceId);
		
		switch(modificationType) {
		case NEW:
			item.setModificationType(modificationType);
			break;
		case UPDATE:
		case CANCEL:
			if (item.getModificationType() == null || item.getModificationType() != TaxDocumentModificationType.NEW) {				
				item.setPreviousTaxDocumentId(orderShipment.getTaxDocumentId().toString());
				item.setPreviousAddress(address);
				item.setModificationType(modificationType);
			}
			break;
		default:
			break;
		}
		
		taxDocumentModificationItems.put(taxDocumentReferenceId, item);
	}
	
	/**
	 * Adds order return for modification.
	 * 
	 * @param orderReturn the order return
	 * @param address the order return address
	 * @param modificationType the tax document modification type
	 */
	public void add(final OrderReturn orderReturn, final Address address, final TaxDocumentModificationType modificationType) {
		
		String taxDocumentReferenceId = orderReturn.getRmaCode();
		TaxDocumentModificationItem item = get(taxDocumentReferenceId);		
		
		item.setTaxDocumentReferenceId(taxDocumentReferenceId);
		item.setPreviousTaxDocumentId(orderReturn.getTaxDocumentId().toString());
		item.setPreviousAddress(address);
		item.setModificationType(modificationType);
		
		taxDocumentModificationItems.put(taxDocumentReferenceId, item);
	}
	
	/**
	 * Gets a collection of the tax documents for modification.
	 * 
	 * @param modificationType the given tax document modification type
	 * @return the collection of the tax document modification item to add
	 */
	@SuppressWarnings("unchecked")
	public Collection<TaxDocumentModificationItem> get(final TaxDocumentModificationType modificationType) {
		
		if (modificationType == null) {
			return  Collections.unmodifiableCollection(taxDocumentModificationItems.values());
		}
		
		return Collections.unmodifiableCollection(CollectionUtils.select(taxDocumentModificationItems.values(), new Predicate() {
			@Override
			public boolean evaluate(final Object item) {
				return ((TaxDocumentModificationItem) item).getModificationType().equals(modificationType);
			}
		}));
	}
	
	/**
	 * Clears the elements of the tax document modification items.
	 */
	public void clear() {
		taxDocumentModificationItems.clear();
	}

	/**
	 * Gets an item by its tax document reference id.
     *
	 * @param taxDocumentReferenceId the tax document reference id
	 * @return an tax document modification item for the given reference id
	 */
	public TaxDocumentModificationItem get(final String taxDocumentReferenceId) {
		
		TaxDocumentModificationItem item = taxDocumentModificationItems.get(taxDocumentReferenceId);
		if (item == null) {
			return new TaxDocumentModificationItem();
		}
		return item;
	}
}
