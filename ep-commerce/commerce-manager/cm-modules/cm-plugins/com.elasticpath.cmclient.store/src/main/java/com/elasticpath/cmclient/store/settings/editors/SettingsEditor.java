/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.store.settings.editors;

import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;

import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.helpers.store.StoreEditorModel;
import com.elasticpath.cmclient.core.helpers.store.StoreEditorModelHelper;
import com.elasticpath.cmclient.store.StorePlugin;
import com.elasticpath.cmclient.store.settings.SettingsMessages;

/**
 * Implements a multi-page editor for displaying and editing Stores.
 */
public class SettingsEditor extends AbstractCmClientFormEditor {

	/**
	 * ID of the editor. It is the same as the class name.
	 */
	public static final String ID_EDITOR = SettingsEditor.class.getName();

	private StoreEditorModel storeEditorModel;

	private final StoreEditorModelHelper editorModelHelper;

	/**
	 * Prepares fetchGroupLoadTuner to retrieve StoreEditorModels ready to be edited.
	 */
	public SettingsEditor() {
		editorModelHelper = StoreEditorModelHelper.createStoreEditorModelHelper();
	}

	@Override
	public void initEditor(final IEditorSite site, final IEditorInput input) throws PartInitException {
		final Long storeUid = input.getAdapter(Long.class);
		try {
			storeEditorModel = editorModelHelper.createStoreEditorModel(storeUid, true);
		} catch (final EpServiceException exception) {
			throw new PartInitException("Store with UID " + storeUid + " does not exist", exception); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	@Override
	@SuppressWarnings("PMD.AvoidThrowingRawExceptionTypes")
	protected void addPages() {
		try {
			addPage(new SettingsMarketingPage(this));
			addExtensionPages(getClass().getSimpleName(), StorePlugin.PLUGIN_ID);
		} catch (final PartInitException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Collection<Locale> getSupportedLocales() {
		return Collections.emptyList();
	}

	@Override
	public void reloadModel() {
		editorModelHelper.reload(storeEditorModel);
	}

	@Override
	protected void saveModel(final IProgressMonitor monitor) {
		monitor.beginTask("Save the Store", 2); //$NON-NLS-1$

		try {
			editorModelHelper.flush(storeEditorModel);
		} catch (final EpServiceException exception) {
			MessageDialog.openWarning(getSite().getShell(),	SettingsMessages.get().CanNotCreateStoreMsgBoxTitle, exception.getLocalizedMessage());
			monitor.setCanceled(true);
			return;
		}
		refreshEditorPages();

		monitor.worked(1);
		monitor.done();
	}

	@Override
	public StoreEditorModel getModel() {
		return storeEditorModel;
	}

	@Override
	public Locale getDefaultLocale() {
		return getModel().getDefaultLocale();
	}

	@Override
	protected String getSaveOnCloseMessage() {
		return
			NLS.bind(SettingsMessages.get().SettingEditor_OnSavePrompt,
			getEditorName());
	}
	
	@Override
	protected String getEditorName() {
		return getEditorInput().getName();
	}
	
	
}
