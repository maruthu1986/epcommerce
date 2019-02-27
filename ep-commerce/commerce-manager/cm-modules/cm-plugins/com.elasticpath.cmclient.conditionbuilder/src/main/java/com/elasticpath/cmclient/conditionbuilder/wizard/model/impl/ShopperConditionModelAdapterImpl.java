/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.conditionbuilder.wizard.model.impl;

import com.elasticpath.cmclient.conditionbuilder.adapter.impl.tag.BaseModelAdapterImpl;
import com.elasticpath.cmclient.conditionbuilder.wizard.model.ShopperConditionModelAdapter;
import com.elasticpath.tags.domain.LogicalOperator;

/**
 * ShopperConditionModelAdapterImpl is a SHOPPER condition model adapter.
 */
public class ShopperConditionModelAdapterImpl extends BaseModelAdapterImpl<LogicalOperator> 
		implements ShopperConditionModelAdapter {

	/**
	 * Default constructor.
	 * @param logicalOperator logical operator
	 */
	public ShopperConditionModelAdapterImpl(final LogicalOperator logicalOperator) {
		super(logicalOperator);
	}

}
