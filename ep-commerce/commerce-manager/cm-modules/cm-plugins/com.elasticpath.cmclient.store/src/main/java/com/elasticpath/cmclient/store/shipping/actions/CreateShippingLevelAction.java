/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */

package com.elasticpath.cmclient.store.shipping.actions;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.store.shipping.dialogs.ShippingLevelDialog;
import com.elasticpath.cmclient.store.shipping.views.ShippingLevelsSearchResultsView;
import com.elasticpath.commons.constants.EpShippingContextIdNames;
import com.elasticpath.domain.shipping.ShippingServiceLevel;
import com.elasticpath.service.shipping.ShippingServiceLevelService;

/**
 * Create shipping service level action.
 */
public class CreateShippingLevelAction extends Action {

	/** The logger. */
	private static final Logger LOG = Logger.getLogger(CreateShippingLevelAction.class);

	/** ShippingLevelsSearchResultsView list view. */
	private final ShippingLevelsSearchResultsView listView;

	/**
	 * The constructor.
	 * 
	 * @param listView the shippingLevels list view.
	 * @param text the tool tip text.
	 * @param imageDescriptor the image is shown at the title.
	 */
	public CreateShippingLevelAction(final ShippingLevelsSearchResultsView listView, final String text, final ImageDescriptor imageDescriptor) {
		super(text, imageDescriptor);
		this.listView = listView;
	}

	@Override
	public void run() {
		LOG.debug("CreateShippingLevel Action called."); //$NON-NLS-1$

		ShippingServiceLevel shippingLevel = ServiceLocator.getService(EpShippingContextIdNames.SHIPPING_SERVICE_LEVEL);

		if (ShippingLevelDialog.openCreateDialog(listView.getSite().getShell(), shippingLevel)) {
			ShippingServiceLevelService shippingService = ServiceLocator.getService(
					EpShippingContextIdNames.SHIPPING_SERVICE_LEVEL_SERVICE);
			shippingService.add(shippingLevel);
			listView.refreshViewerInput();
		}
	}
}
