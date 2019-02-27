/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
/**
 *
 */
package com.elasticpath.cmclient.store.targetedselling.conditionalexpression.action;

import org.eclipse.jface.resource.ImageDescriptor;

import com.elasticpath.cmclient.core.event.UIEvent;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareAction;
import com.elasticpath.cmclient.store.StorePlugin;
import com.elasticpath.cmclient.store.targetedselling.conditionalexpression.model.ConditionalExpressionSearchTabModel;
import com.elasticpath.cmclient.store.targetedselling.conditionalexpression.views.ConditionalExpressionSearchResultsView;

/**
 * BaseConditionalExpressionAction
 * is a base class for add/delete/edit actions.
 */
public class BaseConditionalExpressionAction extends AbstractPolicyAwareAction {

	private final ConditionalExpressionSearchResultsView view;

	/**
	 * Default constructor for generic action.
	 *
	 * @param view  the view
	 * @param text  the text
	 * @param image the icon
	 */
	public BaseConditionalExpressionAction(final ConditionalExpressionSearchResultsView view,
										   final String text, final ImageDescriptor image) {
		super(text, image);
		this.view = view;
	}

	/**
	 * fire event for any success operation.
	 *
	 * @param event action event
	 */
	protected void fireEvent(final UIEvent<ConditionalExpressionSearchTabModel> event) {
		StorePlugin.getDefault().getConditionalExpressionListController().onEvent(event);
	}

	/**
	 * @return the view
	 */
	protected ConditionalExpressionSearchResultsView getView() {
		return view;
	}

	@Override
	protected Object getDependentObject() {
		if (view == null) {
			return null;
		}
		return view.getSelectedItem();
	}

	@Override
	public String getTargetIdentifier() {
		return "!replace me on valid value!"; //$NON-NLS-1$
	}
}
