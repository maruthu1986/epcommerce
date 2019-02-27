/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.admin.shipping.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

import com.elasticpath.cmclient.admin.shipping.dialogs.ShippingRegionDialog;
import com.elasticpath.cmclient.admin.shipping.views.ShippingRegionListView;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.commons.constants.EpShippingContextIdNames;
import com.elasticpath.domain.shipping.ShippingRegion;
import com.elasticpath.service.shipping.ShippingRegionService;

/**
 * Create shipping region action.
 */
public class CreateShippingRegionAction extends Action {

	private final ShippingRegionListView listView;

	/**
	 * The constructor.
	 * 
	 * @param listView owning view for this action.
	 * @param text action title.
	 * @param image descriptor for action icon image.
	 */
	public CreateShippingRegionAction(final ShippingRegionListView listView, final String text, final ImageDescriptor image) {
		super(text, image);
		this.listView = listView;
	}

	@Override
	public void run() {
		ShippingRegion shippingRegion = ServiceLocator.getService(EpShippingContextIdNames.SHIPPING_REGION);

		if (ShippingRegionDialog.openCreateDialog(listView.getSite().getShell(), shippingRegion)) {
			ShippingRegionService shippingRegionService = ServiceLocator.getService(
					EpShippingContextIdNames.SHIPPING_REGION_SERVICE);

			shippingRegionService.add(shippingRegion);
			listView.refreshViewerInput();
		}
	}
}
