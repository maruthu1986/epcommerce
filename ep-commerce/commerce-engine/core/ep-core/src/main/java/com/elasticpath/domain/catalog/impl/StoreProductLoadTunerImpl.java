/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
/**
 *
 */
package com.elasticpath.domain.catalog.impl;

import java.util.Objects;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.elasticpath.domain.catalog.ProductLoadTuner;
import com.elasticpath.domain.catalog.StoreProductLoadTuner;
import com.elasticpath.persistence.api.LoadTuner;

/**
 * Adds functionality to the ProductLoadTuner that is specific for Stores.
 * Since a Store knows what Catalog it's using, you can specify whether
 * or not to load the ProductAssociations from that catalog for the
 * StoreProduct.
 */
public class StoreProductLoadTunerImpl extends ProductLoadTunerImpl implements StoreProductLoadTuner {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;

	private boolean loadingProductAssociations;

	/**
	 * Return <code>true</code> if product association is requested.
	 *
	 * @return <code>true</code> if product association is requested.
	 */
	@Override
	public boolean isLoadingProductAssociations() {
		return loadingProductAssociations;
	}

	/**
	 * Sets the flag of loading product associations.
	 *
	 * @param flag sets it to <code>true</code> to request loading product associations.
	 */
	@Override
	public void setLoadingProductAssociations(final boolean flag) {
		loadingProductAssociations = flag;
	}

	/**
	 * Returns <code>true</code> if this load tuner is super set of the given load tuner, otherwise, <code>false</code>.
	 *
	 * @param productLoadTuner the product load tuner
	 * @return <code>true</code> if this load tuner is super set of the given load tuner, otherwise, <code>false</code>
	 */
	@Override
	public boolean contains(final ProductLoadTuner productLoadTuner) {
		if (!super.contains(productLoadTuner)) {
			return false;
		}

		return !(!loadingProductAssociations
			&& productLoadTuner instanceof StoreProductLoadTuner
			&& ((StoreProductLoadTuner) productLoadTuner).isLoadingProductAssociations());
	}

	/**
	 * Merges the given product load tuner with this one and returns the merged product load tuner.
	 *
	 * @param productLoadTuner the product load tuner
	 * @return the merged product load tuner
	 */
	@Override
	public ProductLoadTuner merge(final ProductLoadTuner productLoadTuner) {
		ProductLoadTuner mergedTuner = super.merge(productLoadTuner);

		if (mergedTuner instanceof StoreProductLoadTuner) {
			((StoreProductLoadTuner) mergedTuner).setLoadingProductAssociations(((StoreProductLoadTuner) mergedTuner).isLoadingProductAssociations());
		}
		return mergedTuner;
	}

	/**
	 * Hash code.
	 *
	 * @return the hash code.
	 */
	@Override
	public int hashCode() {
		return Objects.hash(loadingProductAssociations, super.hashCode());
	}

	/**
	 * Implements equals semantics.<br>
	 * Because load tuners are concerned with field states within the class, it acts as a value type. In this case, content is crucial in the equals
	 * comparison. Using getClass() within the equals method ensures strict comparison between content state in this class where symmetry is
	 * maintained. If instanceof was used in the comparison this could potentially cause symmetry violations when extending this class.
	 *
	 * @param obj the other object to compare
	 * @return true if equal
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}

		if (!super.equals(obj)) {
			return false;
		}

		StoreProductLoadTunerImpl other = (StoreProductLoadTunerImpl) obj;
		return Objects.equals(loadingProductAssociations, other.loadingProductAssociations);
	}

	@Override
	public boolean contains(final LoadTuner loadTuner) {
		if (!(loadTuner instanceof StoreProductLoadTuner)) {
			return false;
		}
		return contains((StoreProductLoadTuner) loadTuner);
	}

	@Override
	public LoadTuner merge(final LoadTuner loadTuner) {
		if (!(loadTuner instanceof StoreProductLoadTuner)) {
			return this;
		}
		return merge((StoreProductLoadTuner) loadTuner);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
			.append("loadingProductAssociations", isLoadingProductAssociations())
			.appendSuper(super.toString())
			.toString();
	}

}
