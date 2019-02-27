/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.admin.stores.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.actions.ActionDelegate;

import com.elasticpath.cmclient.admin.stores.AdminStoresImageRegistry;
import com.elasticpath.cmclient.admin.stores.AdminStoresMessages;
import com.elasticpath.cmclient.admin.stores.editors.StoreEditor;
import com.elasticpath.cmclient.admin.stores.event.AdminStoresEventService;
import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.cmclient.core.helpers.store.StoreEditorModel;
import com.elasticpath.cmclient.core.helpers.store.StoreEditorModelHelper;
import com.elasticpath.cmclient.core.helpers.store.StoreStateValidator;
import com.elasticpath.domain.store.StoreState;

/**
 * Change Store State Action (Drop Down Menu).
 */
public class ChangeStoreStateAction extends Action implements IMenuCreator {

	private final Action restrictAction;

	private final Action openAction;

	private final StoreEditor storeEditor;

	private final ActionDelegate delegate;

	private Menu fMenu;

	/**
	 * Creates ViewStoreAction.
	 *
	 * @param storeEditor the AbstractStorePage instance.
	 * @param delegate the action delegate
	 */
	public ChangeStoreStateAction(final StoreEditor storeEditor, final ActionDelegate delegate) {
		super(AdminStoresMessages.get().ChangeStoreState, AdminStoresImageRegistry.IMAGE_STORE_PROMOTE_ACTION);

		this.storeEditor = storeEditor;
		this.delegate = delegate;

		this.restrictAction = createSetStoreStateAction(AdminStoresMessages.get().StoreState_Restricted,
				AdminStoresImageRegistry.IMAGE_STORE_PROMOTE_ACTION, StoreState.RESTRICTED);

		this.openAction = createSetStoreStateAction(AdminStoresMessages.get().StoreState_Open, AdminStoresImageRegistry.IMAGE_STORE_PROMOTE_ACTION,
				StoreState.OPEN);

		setMenuCreator(this);

		final StoreEditorModel model = storeEditor.getModel();
		restrictAction.setEnabled(!StoreState.RESTRICTED.equals(model.getStoreState()));
		openAction.setEnabled(!StoreState.OPEN.equals(model.getStoreState()));
		this.setActionDefinitionId(this.getClass().getName());
	}

	private Action createSetStoreStateAction(final String title, final ImageDescriptor image, final StoreState state) {
		return new Action(title, image) {
			@Override
			public void run() {
				if (storeEditor.isDirty()) { // check if store is in editing then we can't edit store states.
					MessageDialog.openInformation(Display.getCurrent().getActiveShell(), AdminStoresMessages.get().StoreStateChangeError_Title,
							AdminStoresMessages.get().StoreStateChangeError_Message);
					return;
				}
				final StoreStateValidator validator = new StoreStateValidator();
				if (!validator.isStateAccessible(getSelectedStore(), state)) {
					MessageDialog.openError(Display.getCurrent().getActiveShell(),
							AdminStoresMessages.get().StoreStateChangeError_Title, validator.getErrorMessage());
					return;
				}
				if (storeEditor.isUrlNotUniqueStore()) {
					MessageDialog.openError(Display.getCurrent().getActiveShell(), 
							AdminStoresMessages.get().CreateStoreErrorMsgBoxTitle, AdminStoresMessages.get().CreateStoreError_UrlNotUnique);
					return;
				}
				if (confirmStateChange()) {
					final StoreEditorModelHelper editorModelHelper = StoreEditorModelHelper.createStoreEditorModelHelper();
					getSelectedStore().setStoreState(state);
					editorModelHelper.flush(getSelectedStore());
					storeEditor.refreshEditorPages();

					AdminStoresEventService.getInstance().fireStoreChangeEvent(new ItemChangeEvent<>(this, getSelectedStore()));
				}
			}

			private boolean confirmStateChange() {
				return MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), AdminStoresMessages.get().ConfirmStoreStateChange_Title,

						NLS.bind(AdminStoresMessages.get().ConfirmStoreStateChange_Message,
						getSelectedStore().getCode(), "'" + getText() + "'")); //$NON-NLS-1$//$NON-NLS-2$
			}
		};
	}

	/*
	 * Gets selected StoreEditorModel form the storeEditor. @return StoreEditorModel instance.
	 */
	private StoreEditorModel getSelectedStore() {
		return storeEditor.getModel();
	}

	@Override
	public Menu getMenu(final Menu parent) {
		return null;
	}

	@Override
	public Menu getMenu(final Control parent) {
		if (fMenu != null) {
			return fMenu; // we can do fMenu.dispose() here if we need recreate menu every time it accessed.
		}

		fMenu = new Menu(parent);

		addActionToMenu(fMenu, restrictAction);
		addActionToMenu(fMenu, openAction);

		return fMenu;
	}

	private void addActionToMenu(final Menu parent, final Action action) {
		final ActionContributionItem item = new ActionContributionItem(action);
		item.fill(parent, -1);
	}

	@Override
	public void dispose() {
		if (fMenu != null) {
			fMenu.dispose();
			fMenu = null;
		}
	}

	@Override
	public void runWithEvent(final Event event) {
		delegate.runWithEvent(this, event);
	}
}
