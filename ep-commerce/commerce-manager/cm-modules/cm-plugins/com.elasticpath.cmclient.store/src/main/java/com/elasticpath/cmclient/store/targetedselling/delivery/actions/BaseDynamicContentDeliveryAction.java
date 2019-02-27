/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
/**
 * 
 */
package com.elasticpath.cmclient.store.targetedselling.delivery.actions;

import org.eclipse.jface.resource.ImageDescriptor;

import com.elasticpath.cmclient.core.event.EventType;
import com.elasticpath.cmclient.core.event.UIEvent;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareAction;
import com.elasticpath.cmclient.store.StorePlugin;
import com.elasticpath.cmclient.store.targetedselling.delivery.model.DynamicContentDeliverySearchTabModel;
import com.elasticpath.cmclient.store.targetedselling.delivery.model.impl.DynamicContentDeliverySearchTabModelImpl;
import com.elasticpath.domain.targetedselling.DynamicContentDelivery;

/**
 * BaseDynamicContentDeliveryAction
 * is a base class for add/delete/edit actions.
 */
public class BaseDynamicContentDeliveryAction extends AbstractPolicyAwareAction {

	/**
	 * Default constructor.
	 */
	public BaseDynamicContentDeliveryAction() {
		super();
	}

	/**
	 * Default constructor.
	 * @param text the text
	 * @param image the icon
	 */
	public BaseDynamicContentDeliveryAction(final String text, final ImageDescriptor image) {
		super(text, image);
	}

	/**
	 * Default constructor.
	 * @param text the text
	 */
	public BaseDynamicContentDeliveryAction(final String text) {
		super(text);
	}

	/**
	 * fire event for any success operation.
	 * @param eventType event type for fire
	 * @param dynamicContentDelivery object for event
	 */
	protected void fireEvent(final EventType eventType, final DynamicContentDelivery dynamicContentDelivery) {
		UIEvent<DynamicContentDeliverySearchTabModel> eventForList = new UIEvent<>(
				new DynamicContentDeliverySearchTabModelImpl(),
				eventType,
				false
		);

		UIEvent<DynamicContentDelivery> event = new UIEvent<>(
				dynamicContentDelivery,
				eventType,
				false
		);

		StorePlugin.getDefault().getDynamicContentDeliveryListController().onEvent(eventForList);
		StorePlugin.getDefault().getDynamicContentDeliveryController().onEvent(event);
	}

	@Override
	public String getTargetIdentifier() {
		return null;
	}
}
