/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
/**
 * 
 */
package com.elasticpath.cmclient.store.targetedselling.dynamiccontent.actions;

import org.eclipse.jface.resource.ImageDescriptor;

import com.elasticpath.cmclient.core.event.EventType;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareAction;
import com.elasticpath.domain.contentspace.DynamicContent;

/**
 * BaseDynamicContentAction
 * is a base class for add/delete/edit actions.
 */
@SuppressWarnings("PMD.AbstractClassWithoutAbstractMethod")
public abstract class AbstractBaseDynamicContentAction extends AbstractPolicyAwareAction {

	/**
	 * Default constructor.
	 */
	public AbstractBaseDynamicContentAction() {
		super();
	}

	/**
	 * Default constructor.
	 * @param text the text
	 * @param image the icon
	 */
	public AbstractBaseDynamicContentAction(final String text, final ImageDescriptor image) {
		super(text, image);
	}

	/**
	 * Default constructor.
	 * @param text the text
	 */
	public AbstractBaseDynamicContentAction(final String text) {
		super(text);
	}

	/**
	 * Fire event for any success operation.
	 * @param eventType event type for fire
	 * @param dynamicContent object for event
	 */
	protected void fireEvent(final EventType eventType, final DynamicContent dynamicContent) {
		DynamicContentChangeEventUtil eventUtil = new DynamicContentChangeEventUtil();
		eventUtil.fireEvent(eventType, dynamicContent);
	}

}
