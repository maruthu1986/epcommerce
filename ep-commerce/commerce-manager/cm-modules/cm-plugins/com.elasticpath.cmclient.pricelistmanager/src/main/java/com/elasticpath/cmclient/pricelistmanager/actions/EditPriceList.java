/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.pricelistmanager.actions;

import org.eclipse.jface.resource.ImageDescriptor;

import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareAction;
import com.elasticpath.cmclient.pricelistmanager.event.PriceListSelectedEvent;
import com.elasticpath.cmclient.pricelistmanager.event.PricingEventService;
import com.elasticpath.cmclient.pricelistmanager.views.PriceListSearchResultsView;

/**
 * Edit price list action.
 */
public class EditPriceList extends AbstractPolicyAwareAction {

	private final PriceListSearchResultsView view;
	
	/**
	 * The constructor.
	 * 
	 * @param view the results from search list view.
	 * @param text the tool tip text.
	 * @param imageDescriptor the image is shown at the title.
	 */	
	public EditPriceList(final PriceListSearchResultsView view,
			final String text, final ImageDescriptor imageDescriptor) {
		super(text, imageDescriptor);
		this.view = view;
	}
	
	@Override
	public void run() {
		PricingEventService.getInstance().fireSelectedEvent(
				new PriceListSelectedEvent(view.getSelectedItem())
				);
	}

	@Override
	public String getTargetIdentifier() {
		return "editPriceListAction"; //$NON-NLS-1$
	}
}
