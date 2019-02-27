/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.admin.stores.actions;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IWorkbenchPartSite;

import com.elasticpath.cmclient.admin.stores.AdminStoresMessages;
import com.elasticpath.cmclient.admin.stores.event.AdminStoresEventService;
import com.elasticpath.cmclient.admin.stores.views.StoreSelector;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.cmclient.core.event.ItemChangeEvent.EventType;
import com.elasticpath.cmclient.core.helpers.store.StoreEditorModel;
import com.elasticpath.cmclient.core.helpers.store.StoreEditorModelHelper;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.service.store.StoreService;

/**
 * Delete store action.
 */
public class DeleteStoreAction extends Action {

	/**
	 * The logger.
	 */
	private static final Logger LOG = Logger.getLogger(DeleteStoreAction.class);

	private final StoreSelector storeSelector;

	private final IWorkbenchPartSite site;

	/**
	 * The Constructor.
	 *
	 * @param site            the workbench part site
	 * @param selector        the store selector.
	 * @param text            the tool tip text.
	 * @param imageDescriptor the image is shown at the title.
	 */
	public DeleteStoreAction(final IWorkbenchPartSite site, final StoreSelector selector, final String text, final ImageDescriptor imageDescriptor) {
		super(text, imageDescriptor);
		this.site = site;
		this.storeSelector = selector;
	}

	@Override

	// ---- DOCrun
	public void run() {
		LOG.debug("DeleteStore Action called."); //$NON-NLS-1$
		StoreEditorModel selectedStoreEditorModel = storeSelector.getSelectedStoreEditorModel();

		StoreService storeService = ServiceLocator.getService(ContextIdNames.STORE_SERVICE);

		if (storeService.getStore(selectedStoreEditorModel.getUidPk()) == null) {
			MessageDialog.openInformation(site.getShell(), AdminStoresMessages.get().NoLongerExistStoreMsgBoxTitle,
				NLS.bind(AdminStoresMessages.get().NoLongerExistStoreMsgBoxText,
				new String[]{selectedStoreEditorModel.getName(),
				selectedStoreEditorModel.getUrl()}));
			AdminStoresEventService.getInstance().fireStoreChangeEvent(
					new ItemChangeEvent<>(this, selectedStoreEditorModel, EventType.REMOVE));
			return;
		}

		if (storeService.storeInUse(selectedStoreEditorModel.getUidPk())) {
			MessageDialog.openInformation(site.getShell(), AdminStoresMessages.get().StoreInUseTitle,
				NLS.bind(AdminStoresMessages.get().StoreInUseMessage,
				selectedStoreEditorModel.getName()));
			return;
		}

		boolean confirmed = MessageDialog.openConfirm(site.getShell(), AdminStoresMessages.get().ConfirmDeleteStoreMsgBoxTitle,
			NLS.bind(AdminStoresMessages.get().ConfirmDeleteStoreMsgBoxText,
			new String[]{selectedStoreEditorModel.getName(),
			selectedStoreEditorModel.getUrl()}));

		if (confirmed) {
			final StoreEditorModelHelper editorModelHelper = StoreEditorModelHelper.createStoreEditorModelHelper();
			editorModelHelper.destroyModel(selectedStoreEditorModel);
			AdminStoresEventService.getInstance().fireStoreChangeEvent(
					new ItemChangeEvent<>(this, selectedStoreEditorModel, EventType.REMOVE));
		}
	}
	// ---- DOCrun
}
