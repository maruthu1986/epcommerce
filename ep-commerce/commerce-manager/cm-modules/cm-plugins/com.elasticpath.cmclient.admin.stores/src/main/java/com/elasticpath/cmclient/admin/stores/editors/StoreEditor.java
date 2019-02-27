/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.admin.stores.editors;

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
import com.elasticpath.cmclient.admin.stores.AdminStoresMessages;
import com.elasticpath.cmclient.admin.stores.AdminStoresPlugin;
import com.elasticpath.cmclient.admin.stores.event.AdminStoresEventService;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.cmclient.core.event.ItemChangeEvent.EventType;
import com.elasticpath.cmclient.core.helpers.store.StoreEditorModel;
import com.elasticpath.cmclient.core.helpers.store.StoreEditorModelHelper;
import com.elasticpath.cmclient.core.service.AuthorizationService;

/**
 * Implements a multi-page editor for displaying and editing Stores.
 */
@SuppressWarnings({ "PMD.PrematureDeclaration" })
public class StoreEditor extends AbstractCmClientFormEditor {

	/**
	 * ID of the editor. It is the same as the class name.
	 */
	public static final String ID_EDITOR = StoreEditor.class.getName();

	private StoreEditorModel storeEditorModel;

	private final StoreEditorModelHelper editorModelHelper;

	private boolean currentUserAuthorizedForStore;

	/**
	 * Prepares fetchGroupLoadTuner to retrieve StoreEditorModels ready to be edited.
	 */
	public StoreEditor() {
		editorModelHelper = StoreEditorModelHelper.createStoreEditorModelHelper();
	}

	@Override
	public void initEditor(final IEditorSite site, final IEditorInput input) throws PartInitException {
		final Long storeUid = input.getAdapter(Long.class);
		try {
			storeEditorModel = editorModelHelper.createStoreEditorModel(storeUid);
			this.currentUserAuthorizedForStore = determineAuthorized(storeEditorModel.getStoreCode());
		} catch (final EpServiceException exception) {
			throw new PartInitException("Store with UID " + storeUid + " does not exist", exception); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	/**
	 * Determines whether the current CM User is authorized to modify the store with the given code.
	 * Calls the {@link AuthorizationService}.
	 * @param storeCode the store code
	 * @return true if authorized, false if not
	 */
	boolean determineAuthorized(final String storeCode) {
		return AuthorizationService.getInstance().isAuthorizedForStore(storeCode);
	}

	/**
	 * @return true if the current user is authorized to modify the store being currently edited
	 */
	boolean isCurrentUserAuthorizedForStore() {
		return currentUserAuthorizedForStore;
	}

	@Override
	@SuppressWarnings("PMD.AvoidThrowingRawExceptionTypes")
	protected void addPages() {
		boolean authorized = isCurrentUserAuthorizedForStore();
		try {
			addPage(new StoreSummaryPage(this, authorized));
			addPage(new StoreLocalizationPage(this, authorized));
			addPage(new StoreCatalogPage(this, authorized));
			addPage(new StoreWarehousePage(this, authorized));
			addPage(new StoreTaxesPage(this, authorized));
			addPage(new PaymentPage(this, authorized));
			addPage(new SharedCustomerAccountsPage(this, authorized));
			addPage(new StoreMarketingPage(this, authorized));
			addPage(new StoreSystemPage(this, authorized));

			getCustomData().put("authorized", authorized);
			addExtensionPages(getClass().getSimpleName(), AdminStoresPlugin.PLUGIN_ID);
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
		final EventType eventType = getEventType();

		if (isUrlNotUniqueStore()) {
			MessageDialog.openWarning(getSite().getShell(),
					AdminStoresMessages.get().CreateStoreWarningMsgBoxTitle, AdminStoresMessages.get().CreateStoreWarning_UrlNotUnique);
		}

		monitor.beginTask("Save the Store", 2); //$NON-NLS-1$
		try {
			editorModelHelper.flush(storeEditorModel);
		} catch (final EpServiceException exception) {
			MessageDialog.openWarning(getSite().getShell(), AdminStoresMessages.get().CanNotCreateStoreMsgBoxTitle, exception.getLocalizedMessage());
			monitor.setCanceled(true);
			return;
		}
		final ItemChangeEvent<StoreEditorModel> itemChangeEvent = new ItemChangeEvent<>(this, storeEditorModel, eventType);
		AdminStoresEventService.getInstance().fireStoreChangeEvent(itemChangeEvent);
		final StoreEditorInput editorInput = (StoreEditorInput) getEditorInput();
		editorInput.setCode(storeEditorModel.getCode());
		editorInput.setName(storeEditorModel.getName());
		editorInput.setUid(storeEditorModel.getUidPk());
		refreshEditorPages();
		monitor.worked(1);
		monitor.done();
	}

	private EventType getEventType() {
		EventType eventType = EventType.ADD;
		if (storeEditorModel.isPersistent()) {
			eventType = EventType.CHANGE;
		}
		return eventType;
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
			NLS.bind(AdminStoresMessages.get().StoreEditor_OnSavePrompt,
			getEditorName());
	}

	@Override
	protected String getEditorName() {
		return getEditorInput().getName();
	}

	@Override
	public Object getDependentObject() {
		return getModel().getStore();
	}

	/**
	 * @return - true if url is unique.
	 * @see com.elasticpath.cmclient.core.helpers.store.StoreEditorModelHelper
	 * 		#isUrlNotUniqueForOpenStore(com.elasticpath.cmclient.core.helpers.store.StoreEditorModel)
	 */
	public boolean isUrlNotUniqueStore() {
		return editorModelHelper.isUrlNotUniqueStore(storeEditorModel);
	}

	@Override
	protected String getDialogMessageOnSave() {
		return AdminStoresMessages.get().StoreCreationValidationErrors;
	}
}
