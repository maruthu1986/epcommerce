/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
/**
 * 
 */
package com.elasticpath.cmclient.store.targetedselling.conditionalexpression.model.impl;

import java.util.ArrayList;
import java.util.List;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.event.SearchResultEvent;
import com.elasticpath.cmclient.store.targetedselling.TargetedSellingMessages;
import com.elasticpath.cmclient.store.targetedselling.conditionalexpression.model.ComboViewerModelBuilder;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.targetedselling.DynamicContentDelivery;
import com.elasticpath.service.targetedselling.DynamicContentDeliveryService;

/**
 * DynamicContentDeliveryModelBuilderImpl.
 *
 */
public class DynamicContentDeliveryModelBuilderImpl implements ComboViewerModelBuilder<DynamicContentDelivery> {

	private DynamicContentDeliveryService service;

	private List<DynamicContentDelivery> dynamicContentDeliveryList;
	
	/**
	 * Default constructor. 
	 */
	public DynamicContentDeliveryModelBuilderImpl() {
		super();
	}

	/**
	 * Constructor for event object. 
	 * @param eventObject event object with data
	 */
	public DynamicContentDeliveryModelBuilderImpl(final SearchResultEvent<DynamicContentDelivery> eventObject) {
		super();
		this.dynamicContentDeliveryList = eventObject.getItems();
	}

	@Override
	public DynamicContentDelivery[] getModel() {

		if (this.dynamicContentDeliveryList == null) {
			this.service = this.getDynamicContentDeliveryService();
			this.dynamicContentDeliveryList = new ArrayList<>(this.service.findAll());
		}
//		DynamicContentDelivery dynamicContentDeliveryAll = getAllEntry();
//
//		this.dynamicContentDeliveryList.add(0, dynamicContentDeliveryAll);
		DynamicContentDelivery[] result = new DynamicContentDelivery[this.dynamicContentDeliveryList.size()];
		result = this.dynamicContentDeliveryList.toArray(result);
		return result;
	}

	/**
	 * @return non-persistent DCD that is used as 'All' entry in the DCD combo on the search tab
	 */
	public DynamicContentDelivery getAllEntry() {
		DynamicContentDelivery dynamicContentDeliveryAll = ServiceLocator.getService(ContextIdNames.DYNAMIC_CONTENT_DELIVERY);
		dynamicContentDeliveryAll.setName(TargetedSellingMessages.get().ConditionalExpressionAll);
		return dynamicContentDeliveryAll;
	} 

	private DynamicContentDeliveryService getDynamicContentDeliveryService() {
		return ServiceLocator.getService(ContextIdNames.DYNAMIC_CONTENT_DELIVERY_SERVICE);
	}
}
