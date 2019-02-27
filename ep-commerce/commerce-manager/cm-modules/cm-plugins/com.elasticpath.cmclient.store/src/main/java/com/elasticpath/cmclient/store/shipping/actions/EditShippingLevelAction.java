/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */

package com.elasticpath.cmclient.store.shipping.actions;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.util.NLS;

import com.elasticpath.cmclient.core.CorePlugin;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.store.shipping.ShippingLevelsMessages;
import com.elasticpath.cmclient.store.shipping.dialogs.ShippingLevelDialog;
import com.elasticpath.cmclient.store.shipping.views.ShippingLevelsSearchResultsView;
import com.elasticpath.commons.constants.EpShippingContextIdNames;
import com.elasticpath.domain.shipping.ShippingServiceLevel;
import com.elasticpath.service.shipping.ShippingServiceLevelService;

/**
 * Edit shipping service level implementation.
 */
public class EditShippingLevelAction extends Action {

	/** The logger. */
	private static final Logger LOG = Logger.getLogger(EditShippingLevelAction.class);

	/** ShippingLevelsSearchResultsView list view. */
	private final ShippingLevelsSearchResultsView listView;

	/**
	 * The constructor.
	 *
	 * @param listView the shippingLevels list view.
	 * @param text the tool tip text.
	 * @param imageDescriptor the image is shown at the title.
	 */
	public EditShippingLevelAction(final ShippingLevelsSearchResultsView listView, final String text, final ImageDescriptor imageDescriptor) {
		super(text, imageDescriptor);
		this.listView = listView;
	}

	@Override
	public void run() {
		LOG.debug("EditShippingLevel Action called."); //$NON-NLS-1$

		final ShippingServiceLevelService shippingService = ServiceLocator.getService(
				EpShippingContextIdNames.SHIPPING_SERVICE_LEVEL_SERVICE);

		final ShippingServiceLevel selectedShippingLevel = listView.getSelectedShippingLevel();
		final ShippingServiceLevel selectedShippingLevelToEdit = (ShippingServiceLevel) shippingService.getObject(selectedShippingLevel.getUidPk());

		if (selectedShippingLevelToEdit == null) {
			MessageDialog.openInformation(listView.getSite().getShell(), ShippingLevelsMessages.get().NoLongerExistShippingLevelMsgBoxTitle,

					NLS.bind(ShippingLevelsMessages.get().NoLongerExistShippingLevelMsgBoxText,
					selectedShippingLevel.getName(CorePlugin.getDefault().getDefaultLocale())));
			listView.refreshViewerInput();
			return;
		}

		if (ShippingLevelDialog.openEditDialog(listView.getSite().getShell(), selectedShippingLevelToEdit)) {
			shippingService.update(selectedShippingLevelToEdit);
			listView.refreshViewerInput();
		}
	}
}

