/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.domain.catalog.impl;

import com.elasticpath.domain.catalog.ProductCharacteristics;

/**
 * Represents product characteristics.
 */
public class ProductCharacteristicsImpl implements ProductCharacteristics {
	private static final long serialVersionUID = 1L;
	
	private boolean requiresSelection;
	private boolean bundle;
	private boolean calculatedBundle;
	private boolean dynamicBundle;
	private boolean isMultiSku;
	private Long bundleUid;
	
	@Override
	public boolean offerRequiresSelection() {
		return requiresSelection;
	}

	public void setRequiresSelection(final boolean requiresSelection) {
		this.requiresSelection = requiresSelection;
	}

	@Override
	public boolean isBundle() {
		return bundle;
	}

	public void setBundle(final boolean bundle) {
		this.bundle = bundle;
	}

	@Override
	public boolean isCalculatedBundle() {
		return calculatedBundle;
	}

	public void setCalculatedBundle(final boolean calculatedBundle) {
		this.calculatedBundle = calculatedBundle;
	}

	@Override
	public boolean isDynamicBundle() {
		return dynamicBundle;
	}

	public void setDynamicBundle(final boolean dynamicBundle) {
		this.dynamicBundle = dynamicBundle;
	}

	@Override
	public Long getBundleUid() {
		return bundleUid;
	}

	@Override
	public boolean hasMultipleSkus() {
		return isMultiSku;
	}

	public void setIsMultiSku(final boolean isMultiSku) {
		this.isMultiSku = isMultiSku;
	}
	
	public void setBundleUid(final Long bundleUid) {
		this.bundleUid = bundleUid;
	}

}
