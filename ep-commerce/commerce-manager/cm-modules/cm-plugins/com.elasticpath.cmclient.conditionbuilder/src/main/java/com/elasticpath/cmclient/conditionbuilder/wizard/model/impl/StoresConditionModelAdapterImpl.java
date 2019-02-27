/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.conditionbuilder.wizard.model.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.elasticpath.cmclient.conditionbuilder.adapter.impl.tag.BaseModelAdapterImpl;
import com.elasticpath.cmclient.conditionbuilder.wizard.conditions.handlers.ConditionHandler;
import com.elasticpath.cmclient.conditionbuilder.wizard.model.StoresConditionModelAdapter;
import com.elasticpath.cmclient.core.LoginManager;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.StoreState;
import com.elasticpath.service.store.StoreService;
import com.elasticpath.tags.domain.Condition;
import com.elasticpath.tags.domain.LogicalOperator;
import com.elasticpath.tags.domain.LogicalOperatorType;

/**
 * StoresConditionModelAdapterImpl condition model adapter.
 */
public class StoresConditionModelAdapterImpl extends BaseModelAdapterImpl<LogicalOperator> 
				implements StoresConditionModelAdapter {

	private static final String SELLING_CHANNEL = "SELLING_CHANNEL"; //$NON-NLS-1$
	private static final String EQUAL_TO = "equalTo";  //$NON-NLS-1$

	private List<Store> stores = new ArrayList<>();

	private boolean editorUsage;
	
	private final ConditionHandler conditionHandler = new ConditionHandler();

	/**
	 * Default constructor that loads the model with all stores that are open or restricted.
	 */
	public StoresConditionModelAdapterImpl() {
		super(new LogicalOperator(LogicalOperatorType.OR));
		
		final StoreService storeService = ServiceLocator.getService(ContextIdNames.STORE_SERVICE);
		
		final List<Store> allCurrentStores = storeService.findAllStores();
		this.stores = new LinkedList<>();
		for (Store store : allCurrentStores) {
			if (store.getStoreState().equals(StoreState.OPEN) || store.getStoreState().equals(StoreState.RESTRICTED)) {
				stores.add(store);		
			}
		}
		
		refreshModelWithSelectedStores();
		
		this.addPropertyChangeListener(event -> refreshModelWithSelectedStores());
	}
	
	
	/**
	 * Default constructor.
	 * @param model LogicalOperator
	 */
	public StoresConditionModelAdapterImpl(final LogicalOperator model) {
		super(model);
		
		this.stores = this.extractStores(model);
		this.addPropertyChangeListener(event -> refreshModelWithSelectedStores());
	}
	
	private void refreshModelWithSelectedStores() {
		for (Condition condition : new HashSet<>(getModel().getConditions())) {
			getModel().removeCondition(condition);
		}
		
		if (stores != null) {
			for (Store store : stores) {
				Condition condition = conditionHandler.buildCondition(SELLING_CHANNEL, EQUAL_TO, store.getCode());
				getModel().addCondition(condition);
			}
		}
	}


	@Override
	public boolean isEditorUsage() {
		return editorUsage;
	}

	@Override
	public void setEditorUsage(final boolean isEditorUsage) {
		this.editorUsage = isEditorUsage;
	}
	
	@Override
	public List<Store> getStores() {
		return new ArrayList<>(stores);
	}

	@Override
	public void setStores(final List<Store> stores) {
		Object oldValue = this.stores;
		this.stores = stores;
		
		this.getPropertyChangeSupport().firePropertyChange(StoresConditionModelAdapter.PROPERTY_STORES, oldValue, this.stores);
	}

	private List<Store> extractStores(final LogicalOperator logicalOperator) {
		Set<Condition> conditions = logicalOperator.getConditions();
		List<Store> stores = new LinkedList<>();
		List<Store> all = this.getAllStores();
		for (Condition condition : conditions) {
			String storeName = (String) condition.getTagValue();
			Store store = getStoreByCode(all, storeName);
			if (null != store) {
				stores.add(store);
			}
		}
		return stores;
	}

	private Store getStoreByCode(final List<Store> stores, final String name) {
		for (Store store : stores) {
			if (store.getCode().equals(name)) {
				return store;
			}
		}
		return null;
	}

	/**
	 * Get list of all stores.
	 * @return List of stores
	 */
	public final List<Store> getAllStores() {
		StoreService storeService = ServiceLocator.getService(ContextIdNames.STORE_SERVICE);
		return storeService.findAllStores(LoginManager.getCmUser());
	}
}
