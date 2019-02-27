/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.store.shipping.actions;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;

import com.elasticpath.cmclient.core.CorePlugin;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.store.shipping.ShippingLevelsMessages;
import com.elasticpath.cmclient.store.shipping.views.ShippingLevelsSearchResultsView;
import com.elasticpath.commons.constants.EpShippingContextIdNames;
import com.elasticpath.domain.shipping.ShippingServiceLevel;
import com.elasticpath.service.shipping.ShippingServiceLevelService;

/**
 * Delete shipping service level implementation.
 */
public class DeleteShippingLevelAction extends Action {

	/** The logger. */
	private static final Logger LOG = Logger.getLogger(DeleteShippingLevelAction.class);

	/** ShippingLevelsSearchResultsView list view. */
	private final ShippingLevelsSearchResultsView listView;

	/**
	 * The constructor.
	 *
	 * @param listView the shippingLevels list view.
	 * @param text the tool tip text.
	 * @param imageDescriptor the image is shown at the title.
	 */
	public DeleteShippingLevelAction(final ShippingLevelsSearchResultsView listView, final String text, final ImageDescriptor imageDescriptor) {
		super(text, imageDescriptor);
		this.listView = listView;
	}

	@Override
	public void run() {
		LOG.debug("DeleteShippingLevel Action called."); //$NON-NLS-1$

		final ShippingServiceLevel selectedServiceLevel = listView.getSelectedShippingLevel();

		final ShippingServiceLevelService shippingService = ServiceLocator.getService(
				EpShippingContextIdNames.SHIPPING_SERVICE_LEVEL_SERVICE);

		Shell parent = listView.getSite().getShell();

		final ShippingServiceLevel serviceLevelToDelete = shippingService.get(selectedServiceLevel.getUidPk());
		if (serviceLevelToDelete == null) {
			MessageDialog.openInformation(parent, ShippingLevelsMessages.get().NoLongerExistShippingLevelMsgBoxTitle,

					NLS.bind(ShippingLevelsMessages.get().NoLongerExistShippingLevelMsgBoxText,
					selectedServiceLevel.getName(CorePlugin.getDefault().getDefaultLocale())));
			listView.refreshViewerInput();
			return;
		}

		if (shippingService.isShippingServiceLevelInUse(serviceLevelToDelete.getCode())) {
			MessageDialog.openInformation(parent, ShippingLevelsMessages.get().UsedShippingServiceLevelDialogTitle,

					NLS.bind(ShippingLevelsMessages.get().UsedShippingServiceLevelDialogText,
					serviceLevelToDelete.getName(CorePlugin.getDefault().getDefaultLocale())));
			return;
		}

		boolean confirmed = MessageDialog.openConfirm(listView.getSite().getShell(),
			ShippingLevelsMessages.get().ConfirmDeleteShippingLevelMsgBoxTitle,

				NLS.bind(ShippingLevelsMessages.get().ConfirmDeleteShippingLevelMsgBoxText,
				serviceLevelToDelete.getName(CorePlugin.getDefault().getDefaultLocale())));

		if (confirmed) {
			shippingService.remove(serviceLevelToDelete);
			listView.refreshViewerInput();
		}
	}
}

