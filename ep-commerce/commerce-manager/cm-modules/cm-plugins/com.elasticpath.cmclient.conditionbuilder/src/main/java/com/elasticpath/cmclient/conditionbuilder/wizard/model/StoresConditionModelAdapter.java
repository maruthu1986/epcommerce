/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.conditionbuilder.wizard.model;

import java.util.List;

import com.elasticpath.cmclient.conditionbuilder.adapter.BaseModelAdapter;
import com.elasticpath.domain.store.Store;
import com.elasticpath.tags.domain.LogicalOperator;
/**
 * StoresConditionModelAdapter
 * interface for STORES.
 */
public interface StoresConditionModelAdapter extends BaseModelAdapter<LogicalOperator> {
	
	/** Stores property name. */
	String PROPERTY_STORES = "stores"; //$NON-NLS-1$
	
	/**
	 * Indicates if this wrapper is used in the condition editor model.
	 * @return true, if it is. Otherwise returns false
	 */
	boolean isEditorUsage();
	
	/**
	 * Sets isEditorUsage flag.
	 * @param isEditorUsage isEditorUsage 
	 */
	void setEditorUsage(boolean isEditorUsage);
	
	/**
	 * * Get the start date that this campaign will become available. * *
	 * 
	 * @return the start date
	 */
	
	/**
	 * Get the list of assigned stores.
	 * 
	 * @return List of assigned stores
	 */
	List<Store> getStores();
	/**
	 * Set assigned stores.
	 * 
	 * @param store
	 *            List of assigned stores
	 */
	void setStores(List<Store> store);

}
