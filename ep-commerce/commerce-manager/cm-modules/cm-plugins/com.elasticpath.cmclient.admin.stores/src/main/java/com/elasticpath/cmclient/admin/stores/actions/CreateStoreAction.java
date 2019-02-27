/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.admin.stores.actions;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;

import com.elasticpath.cmclient.admin.stores.AdminStoresMessages;
import com.elasticpath.cmclient.admin.stores.editors.StoreEditor;
import com.elasticpath.cmclient.admin.stores.editors.StoreEditorInput;
import com.elasticpath.cmclient.core.helpers.store.StoreEditorModel;

/**
 * Create store action.
 */
public class CreateStoreAction extends Action {

	/** The logger. */
	private static final Logger LOG = Logger.getLogger(CreateStoreAction.class);

	private final IWorkbenchPartSite site;

	/**
	 * The constructor.
	 * 
	 * @param site the workbench part site.
	 * @param text the tool tip text.
	 * @param imageDescriptor the image is shown at the title.
	 */
	public CreateStoreAction(final IWorkbenchPartSite site, final String text, final ImageDescriptor imageDescriptor) {
		super(text, imageDescriptor);
		this.site = site;
	}

	@Override
	public void run() {
		LOG.debug("CreateStore Action called."); //$NON-NLS-1$

		final StoreEditorInput editorInput = new StoreEditorInput(AdminStoresMessages.get().NewStoreName, 0, AdminStoresMessages.get().NewStoreCode,
				StoreEditorModel.class);
		try {
			site.getWorkbenchWindow().getActivePage().openEditor(editorInput, StoreEditor.ID_EDITOR);
		} catch (PartInitException e) {
			LOG.error(e.getMessage());
			MessageDialog.openInformation(site.getShell(), AdminStoresMessages.get().CanNotCreateStoreMsgBoxTitle, e.getLocalizedMessage());
		}
	}
}
