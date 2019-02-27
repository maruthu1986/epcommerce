/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.domain.catalog;

import com.elasticpath.persistence.api.LoadTuner;

/**
 * Represents a tuner to control category load. A category load tuner can be used in some services to fine control what data to be loaded for a
 * category. The main purpose is to achieve better performance for some specific performance-critical pages.
 */
public interface CategoryLoadTuner extends LoadTuner {

	/**
	 * Return <code>true</code> if master is requested.
	 *
	 * @return <code>true</code> if master is requested.
	 */
	boolean isLoadingMaster();

	/**
	 * Return <code>true</code> if category type requested.
	 *
	 * @return <code>true</code> if category type requested.
	 */
	boolean isLoadingCategoryType();

	/**
	 * Return <code>true</code> if attribute value is requested.
	 *
	 * @return <code>true</code> if attribute value is requested.
	 */
	boolean isLoadingAttributeValue();

	/**
	 * Sets the flag of loading master.
	 *
	 * @param flag sets it to <code>true</code> to request loading master.
	 */
	void setLoadingMaster(boolean flag);

	/**
	 * Sets the flag of loading category type.
	 *
	 * @param flag sets it to <code>true</code> to request loading category type.
	 */
	void setLoadingCategoryType(boolean flag);

	/**
	 * Sets the flag of loading attribute values.
	 *
	 * @param flag sets it to <code>true</code> to request loading attribute values.
	 */
	void setLoadingAttributeValue(boolean flag);

	/**
	 * Sets the <code>CategoryTypeLoadTuner</code>.
	 *
	 * @param tuner the <code>CategoryTypeLoadTuner</code>
	 */
	void setCategoryTypeLoadTuner(CategoryTypeLoadTuner tuner);

	/**
	 * Returns the <code>CategoryTypeLoadTuner</code>.
	 *
	 * @return the <code>CategoryTypeLoadTuner</code>
	 */
	CategoryTypeLoadTuner getCategoryTypeLoadTuner();

	/**
	 * Gets whether we are loading locale dependant fields.
	 *
	 * @return whether we are loading locale dependant fields
	 */
	boolean isLoadingLocaleDependantFields();

	/**
	 * Sets whether we are loading locale dependant fields.
	 *
	 * @param loadingLocaleDependantFields whether we are loading locale dependant fields
	 */
	void setLoadingLocaleDependantFields(boolean loadingLocaleDependantFields);

	/**
	 * Returns <code>true</code> if this load tuner is super set of the given load tuner,
	 * otherwise, <code>false</code>.
	 *
	 * @param categoryLoadTuner the category load tuner
	 * @return <code>true</code> if this load tuner is super set of the given load tuner,
	 *         otherwise, <code>false</code>
	 */
	boolean contains(CategoryLoadTuner categoryLoadTuner);

	/**
	 * Merges the given load tuner with this one and returns the merged load tuner.
	 *
	 * @param categoryLoadTuner the category load tuner
	 * @return the merged load tuner
	 */
	CategoryLoadTuner merge(CategoryLoadTuner categoryLoadTuner);

}
