/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.admin.shipping.actions;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;

import com.elasticpath.cmclient.admin.shipping.AdminShippingMessages;
import com.elasticpath.cmclient.admin.shipping.views.ShippingRegionListView;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.commons.constants.EpShippingContextIdNames;
import com.elasticpath.service.shipping.ShippingServiceLevelService;
import com.elasticpath.service.shipping.ShippingRegionService;

/**
 * Delete shipping region action.
 */
public class DeleteShippingRegionAction extends Action {
	private final ShippingRegionListView listView;

	private final ShippingServiceLevelService serviceLevelService;

	private final ShippingRegionService shippingRegionService;

	/**
	 * The constructor.
	 *
	 * @param listView owning view for this action.
	 * @param text action title.
	 * @param image descriptor for action icon image.
	 */
	public DeleteShippingRegionAction(final ShippingRegionListView listView, final String text, final ImageDescriptor image) {
		super(text, image);
		this.listView = listView;

		serviceLevelService = ServiceLocator.getService(
				EpShippingContextIdNames.SHIPPING_SERVICE_LEVEL_SERVICE);
		shippingRegionService = ServiceLocator.getService(
				EpShippingContextIdNames.SHIPPING_REGION_SERVICE);
	}

	@Override
	public void run() {
		Shell shell = listView.getSite().getShell();
		List<Long> shippingRegionsInUse = serviceLevelService.getShippingRegionInUseUidList();
		if (shippingRegionsInUse.contains(listView.getSelectedShippingRegion().getUidPk())) {
			MessageDialog.openError(shell, AdminShippingMessages.get().CantDeleteShippingRegionDialogTitle,
					AdminShippingMessages.get().CantDeleteShippingRegionDialogText);

			return;
		}

		boolean okPressed = MessageDialog.openConfirm(shell, AdminShippingMessages.get().DeleteShippingRegionDialogTitle,
			NLS.bind(AdminShippingMessages.get().DeleteShippingRegionDialogText,
			listView.getSelectedShippingRegion().getName()));
		if (okPressed) {
			shippingRegionService.remove(listView.getSelectedShippingRegion());
			listView.refreshViewerInput();
		}
	}
}
