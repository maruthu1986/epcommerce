/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.admin.shipping.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.util.NLS;

import com.elasticpath.cmclient.admin.shipping.AdminShippingMessages;
import com.elasticpath.cmclient.admin.shipping.dialogs.ShippingRegionDialog;
import com.elasticpath.cmclient.admin.shipping.views.ShippingRegionListView;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.commons.constants.EpShippingContextIdNames;
import com.elasticpath.domain.shipping.ShippingRegion;
import com.elasticpath.service.shipping.ShippingRegionService;

/**
 * Edit shipping region action.
 */
public class EditShippingRegionAction extends Action {

	private final ShippingRegionListView listView;

	/**
	 * The constructor.
	 *
	 * @param listView owning view for this action.
	 * @param text action title.
	 * @param image descriptor for action icon image.
	 */
	public EditShippingRegionAction(final ShippingRegionListView listView, final String text, final ImageDescriptor image) {
		super(text, image);
		this.listView = listView;
	}

	@Override
	public void run() {

		ShippingRegion shippingRegion = listView.getSelectedShippingRegion();
		ShippingRegionService shippingRegionService = ServiceLocator.getService(
				EpShippingContextIdNames.SHIPPING_REGION_SERVICE);

		/** Get the most recent version of the selected ShippingRegion. */
		ShippingRegion shippingRegionToEdit = shippingRegionService.get(shippingRegion.getUidPk());

		if (shippingRegionToEdit == null) {
			MessageDialog.openInformation(listView.getSite().getShell(), AdminShippingMessages.get().EditShippingRegion,
				NLS.bind(AdminShippingMessages.get().ShippingRegionNoLongerExists,
				shippingRegion.getName()));
			listView.refreshViewerInput();
			return;
		}

		if (ShippingRegionDialog.openEditDialog(listView.getSite().getShell(), shippingRegionToEdit)) {

			shippingRegionService.update(shippingRegionToEdit);
			listView.refreshViewerInput();
		}
	}
}
