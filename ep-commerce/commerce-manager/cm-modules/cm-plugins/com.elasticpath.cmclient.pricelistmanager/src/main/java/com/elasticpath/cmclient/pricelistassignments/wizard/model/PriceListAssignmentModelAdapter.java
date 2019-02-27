/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.pricelistassignments.wizard.model;

import com.elasticpath.cmclient.conditionbuilder.wizard.pages.AbstractSellingContextAdapter;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.pricing.PriceListAssignment;
import com.elasticpath.domain.pricing.PriceListDescriptor;

/**
 * Class wraps PriceListAssignment entity.
 */
public class PriceListAssignmentModelAdapter extends
		AbstractSellingContextAdapter {

	private final PriceListAssignment priceListAssignment;

	/**
	 * Constructor.
	 * 
	 * @param priceListAssignment
	 *            PriceListAssignment to be wrapped.
	 */
	public PriceListAssignmentModelAdapter(
			final PriceListAssignment priceListAssignment) {
		this.priceListAssignment = priceListAssignment;
		populateSellingContext(priceListAssignment.getSellingContext(), priceListAssignment.getName());
		this.priceListAssignment.setSellingContext(getSellingContext());
	}

	@Override
	public int hashCode() {
		return priceListAssignment.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		return priceListAssignment
				.equals(((PriceListAssignmentModelAdapter) obj).priceListAssignment);
	}

	/**
	 * Returns PriceListAssignment priority.
	 * 
	 * @return PriceListAssignment priority
	 */
	public int getPriority() {
		return priceListAssignment.getPriority();
	}

	/**
	 * Sets {@link PriceListAssignment} priority.
	 * 
	 * @param priority
	 *            - {@link PriceListAssignment} priority
	 */
	public void setPriority(final int priority) {
		priceListAssignment.setPriority(priority);
	}
	
	/**
	 * Returns name of wrapped {@link PriceListAssignment}.
	 * 
	 * @return name
	 */
	public String getName() {
		return priceListAssignment.getName();
	}

	/**
	 * Returns description of wrapped {@link PriceListAssignment}.
	 * 
	 * @return description
	 */
	public String getDescription() {
		return priceListAssignment.getDescription();
	}

	 /**
	 * Returns PriceListDescriptor of wrapped {@link PriceListAssignment}.
	 *
	 * @return PriceListDescriptor
	 */
	public PriceListDescriptor getPriceListDescriptor() {
		return priceListAssignment.getPriceListDescriptor();
	}

	/**
	 * Returns GUID of wrapped {@link PriceListAssignment}.
	 * 
	 * @return GUID
	 */
	public final String getGuid() {
		return priceListAssignment.getGuid();
	}

	 /**
	 * Sets {@link PriceListDescriptor} for wrapped {@link PriceListAssignment}.
	 *
	 * @param priceListDescriptor - {@link PriceListDescriptor}
	 */
	public void setPriceListDescriptor(final PriceListDescriptor priceListDescriptor) {
		priceListAssignment.setPriceListDescriptor(priceListDescriptor);
	}

	/**
	 * Returns wrapped {@link PriceListAssignment}.
	 * 
	 * @return {@link PriceListAssignment}
	 */
	public final PriceListAssignment getPriceListAssignment() {
		return priceListAssignment;
	}

	@Override
	public void clearSellingContext() {
		priceListAssignment.setSellingContext(null);
	}

	/**
	 * Returns wrapped {@link Catalog}.
	 * 
	 * @return {@link Catalog}
	 */
	public Catalog getCatalog() {
		return priceListAssignment.getCatalog();
	}

	 /**
	 * Sets {@link Catalog} for wrapped {@link PriceListAssignment}.
	 *
	 * @param catalog - {@link Catalog}
	 */
	public void setCatalog(final Catalog catalog) {
		priceListAssignment.setCatalog(catalog);
	}
}
