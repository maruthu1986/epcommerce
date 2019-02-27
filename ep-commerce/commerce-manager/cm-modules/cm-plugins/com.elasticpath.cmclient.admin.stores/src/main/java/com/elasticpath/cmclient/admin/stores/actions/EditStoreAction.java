/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.admin.stores.actions;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;

import com.elasticpath.cmclient.admin.stores.AdminStoresMessages;
import com.elasticpath.cmclient.admin.stores.editors.StoreEditor;
import com.elasticpath.cmclient.admin.stores.editors.StoreEditorInput;
import com.elasticpath.cmclient.admin.stores.views.StoreSelector;
import com.elasticpath.cmclient.core.helpers.store.StoreEditorModel;

/**
 * Edit store action.
 */
public class EditStoreAction extends Action {

	/** The logger. */
	private static final Logger LOG = Logger.getLogger(EditStoreAction.class);

	private final StoreSelector storeSelector;

	private final IWorkbenchPartSite site;

	/**
	 * The constructor.
	 *
	 * @param site the workbench part site
	 * @param selector the store selector.
	 * @param text the tool tip text.
	 * @param imageDescriptor the image is shown at the title.
	 */
	public EditStoreAction(final IWorkbenchPartSite site, final StoreSelector selector, final String text, final ImageDescriptor imageDescriptor) {
		super(text, imageDescriptor);
		this.site = site;
		this.storeSelector = selector;
	}

	@Override
	public void run() {
		LOG.debug("EditStore Action called."); //$NON-NLS-1$

		StoreEditorModel selectedStoreEditorModel = storeSelector.getSelectedStoreEditorModel();

		final StoreEditorInput editorInput = new StoreEditorInput(selectedStoreEditorModel.getName(), selectedStoreEditorModel.getUidPk(),
				selectedStoreEditorModel.getCode(), StoreEditorModel.class);
		try {
			site.getWorkbenchWindow().getActivePage().openEditor(editorInput, StoreEditor.ID_EDITOR);
		} catch (PartInitException e) {
			LOG.error(e.getMessage());
			MessageDialog.openInformation(site.getShell(), AdminStoresMessages.get().NoLongerExistStoreMsgBoxTitle,
				NLS.bind(AdminStoresMessages.get().NoLongerExistStoreMsgBoxText,
				new String[]{selectedStoreEditorModel.getName(),
				selectedStoreEditorModel.getUrl()}));
		}
	}
}
