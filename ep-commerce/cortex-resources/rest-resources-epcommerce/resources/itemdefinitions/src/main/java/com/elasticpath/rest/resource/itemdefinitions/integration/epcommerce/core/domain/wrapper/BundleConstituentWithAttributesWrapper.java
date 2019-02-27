/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.itemdefinitions.integration.epcommerce.core.domain.wrapper;

import java.util.Collection;

import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.domain.catalog.BundleConstituent;

/**
 * Wrapper for Bundle Constituents.
 */
public class BundleConstituentWithAttributesWrapper {

	/**
	 * The bundle constituent.
	 */
	private BundleConstituent bundleConstituent;

	/**
	 * The attributes.
	 */
	private Collection<AttributeValue> attributes;

	private String standaloneItemId;

	/**
	 * Sets the bundle constituent.
	 *
	 * @param bundleConstituent the bundle constituent
	 * @return the bundle constituent with attributes
	 */
	public BundleConstituentWithAttributesWrapper setBundleConstituent(final BundleConstituent bundleConstituent) {
		this.bundleConstituent = bundleConstituent;
		return this;
	}

	/**
	 * Gets the bundle constituent.
	 *
	 * @return the bundle constituent
	 */
	public BundleConstituent getBundleConstituent() {
		return bundleConstituent;
	}

	/**
	 * Sets the attributes.
	 *
	 * @param attributes the attributes
	 * @return the bundle constituent with attributes
	 */
	public BundleConstituentWithAttributesWrapper setAttributes(final Collection<AttributeValue> attributes) {
		this.attributes = attributes;
		return this;
	}

	/**
	 * Gets the attributes.
	 *
	 * @return the attributes
	 */
	public Collection<AttributeValue> getAttributes() {
		return attributes;
	}

	/**
	 * Sets the standalone item id.
	 *
	 * @param standaloneItemId the new standalone item id
	 */
	public void setStandaloneItemId(final String standaloneItemId) {
		this.standaloneItemId = standaloneItemId;
	}

	/**
	 * Gets the standalone item id.
	 *
	 * @return the standalone item id
	 */
	public String getStandaloneItemId() {
		return standaloneItemId;
	}
}
