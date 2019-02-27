/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
/**
 * 
 */
package com.elasticpath.cmclient.fulfillment.editors.order.ui.impl;

import com.elasticpath.cmclient.fulfillment.editors.order.ui.ManagedModel;
import com.elasticpath.cmclient.fulfillment.editors.order.ui.ManagedModelFactory;
import com.elasticpath.cmclient.fulfillment.editors.order.ui.UiProperty;

/**
 * An implementation for {@link ManagedModelFactory). 
 *
 */
public class ManagedModelFactoryImpl implements ManagedModelFactory<String, String> {

	@Override
	public ManagedModel<String, String> create(final String key, final String value, final UiProperty uiProperty) {
		return new ManagedModelImpl(key, value, uiProperty);
	}

	@Override
	public ManagedModel<String, String> create(final ManagedModel<String, String> managedModel, final UiProperty uiProperty) {
		return new ManagedModelImpl(managedModel, uiProperty);
	}

}
