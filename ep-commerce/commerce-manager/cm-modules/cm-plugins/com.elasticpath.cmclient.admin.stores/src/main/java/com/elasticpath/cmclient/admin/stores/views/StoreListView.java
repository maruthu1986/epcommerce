/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.admin.stores.views;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.TableItem;

import com.elasticpath.cmclient.admin.stores.AdminStoresImageRegistry;
import com.elasticpath.cmclient.admin.stores.AdminStoresMessages;
import com.elasticpath.cmclient.admin.stores.AdminStoresPermissions;
import com.elasticpath.cmclient.admin.stores.AdminStoresPlugin;
import com.elasticpath.cmclient.admin.stores.actions.CreateStoreAction;
import com.elasticpath.cmclient.admin.stores.actions.DeleteStoreAction;
import com.elasticpath.cmclient.admin.stores.actions.EditStoreAction;
import com.elasticpath.cmclient.admin.stores.event.AdminStoresEventService;
import com.elasticpath.cmclient.admin.stores.event.StoreEventListener;
import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.cmclient.core.helpers.store.StoreEditorModel;
import com.elasticpath.cmclient.core.helpers.store.StoreEditorModelHelper;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.cmclient.core.views.AbstractListView;

/**
 * View to show and allow the manipulation of the available stores in CM.
 */
public class StoreListView extends AbstractListView implements StoreEventListener, StoreSelector {

	/** The View's ID. */
	public static final String VIEW_ID = "com.elasticpath.cmclient.admin.stores.views.StoreListView"; //$NON-NLS-1$

	private static final String STORE_TABLE = "Store"; //$NON-NLS-1$

	/** Column indices. */

	private static final int INDEX_STORE_CODE = 0;

	private static final int INDEX_STORE_NAME = 1;

	private static final int INDEX_URL = 2;

	private static final int INDEX_STORE_STATE = 3;

	private final StoreEditorModelHelper editorModelHelper;

	/** Actions. */
	private Action createStoreAction;

	private Action editStoreAction;

	private Action deleteStoreAction;

	/**
	 * The constructor.
	 */
	public StoreListView() {
		super(false, STORE_TABLE);
		AdminStoresEventService.getInstance().registerStoreEventListener(this);
		editorModelHelper = StoreEditorModelHelper.createStoreEditorModelHelper();
	}

	@Override
	public void dispose() {
		AdminStoresEventService.getInstance().unregisterStoreEventListener(this);
		super.dispose();
	}

	@Override
	protected Object[] getViewInput() {
		List<StoreEditorModel> storeEditorModelList = editorModelHelper.findAllStoreEditorModels();
		return storeEditorModelList.toArray(new StoreEditorModel[storeEditorModelList.size()]);
	}

	@Override
	protected ITableLabelProvider getViewLabelProvider() {
		return new StoreListViewLabelProvider();
	}

	@Override
	protected void initializeTable(final IEpTableViewer table) {
		String[] columnNames = new String[]{AdminStoresMessages.get().StoreCode, AdminStoresMessages.get().StoreName, //$NON-NLS-1$
				AdminStoresMessages.get().StoreUrl, AdminStoresMessages.get().StoreState };

		final int[] columnWidths = new int[]{100, 150, 200, 200};

		for (int i = 0; i < columnNames.length; i++) {
			table.addTableColumn(columnNames[i], columnWidths[i]);
		}

	}

	@Override
	protected String getPluginId() {
		return AdminStoresPlugin.PLUGIN_ID;
	}

	@Override
	protected void initializeViewToolbar() {

		Separator storeActionGroup = new Separator("storeActionGroup"); //$NON-NLS-1$
		getToolbarManager().add(storeActionGroup);

		createStoreAction = new CreateStoreAction(this.getSite(), AdminStoresMessages.get().CreateStore,
				AdminStoresImageRegistry.IMAGE_STORE_CREATE_ACTION);
		createStoreAction.setToolTipText(AdminStoresMessages.get().CreateStore);
		editStoreAction = new EditStoreAction(getSite(), this, AdminStoresMessages.get().EditStore, AdminStoresImageRegistry.IMAGE_STORE_EDIT_ACTION);
		editStoreAction.setToolTipText(AdminStoresMessages.get().EditStore);
		addDoubleClickAction(editStoreAction);
		deleteStoreAction = new DeleteStoreAction(getSite(), this, AdminStoresMessages.get().DeleteStore,
				AdminStoresImageRegistry.IMAGE_STORE_DELETE_ACTION);
		deleteStoreAction.setToolTipText(AdminStoresMessages.get().DeleteStore);

		ActionContributionItem createStoreActionContributionItem = new ActionContributionItem(createStoreAction);
		ActionContributionItem editStoreActionContributionItem = new ActionContributionItem(editStoreAction);
		ActionContributionItem deleteStoreActionContributionItem = new ActionContributionItem(deleteStoreAction);

		createStoreActionContributionItem.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		editStoreActionContributionItem.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		deleteStoreActionContributionItem.setMode(ActionContributionItem.MODE_FORCE_TEXT);

		getToolbarManager().appendToGroup(storeActionGroup.getGroupName(), editStoreActionContributionItem);
		getToolbarManager().appendToGroup(storeActionGroup.getGroupName(), createStoreActionContributionItem);
		getToolbarManager().appendToGroup(storeActionGroup.getGroupName(), deleteStoreActionContributionItem);

		// Disable buttons until a row is selected.
		editStoreAction.setEnabled(false);
		deleteStoreAction.setEnabled(false);

		this.getViewer().addSelectionChangedListener(event -> {
			final IStructuredSelection strSelection = (IStructuredSelection) event.getSelection();
			Object firstSelection = strSelection.getFirstElement();

			boolean isEnabled = firstSelection != null;
			deleteStoreAction.setEnabled(isEnabled && isStoreEditAuthorized());
			editStoreAction.setEnabled(isEnabled && isStoreEditAuthorized());
		});
	}

	/**
	 * @return true if current CM User is authorised to edit selected Store.
	 */
	boolean isStoreEditAuthorized() {
		return AuthorizationService.getInstance().isAuthorizedWithPermission(AdminStoresPermissions.ADMIN_STORES_MANAGE)
		&& AuthorizationService.getInstance().isAuthorizedForStore(getSelectedStoreEditorModel().getStore());
	}

	/**
	 * Store list view label provider.
	 */
	protected class StoreListViewLabelProvider extends LabelProvider implements ITableLabelProvider {

		@Override
		public Image getColumnImage(final Object element, final int columnIndex) {
			return null;
		}

		@Override
		public String getColumnText(final Object element, final int columnIndex) {
			StoreEditorModel storeEditorModel = (StoreEditorModel) element;

			switch (columnIndex) {
			case StoreListView.INDEX_STORE_CODE:
				return storeEditorModel.getCode();
			case StoreListView.INDEX_STORE_NAME:
				return storeEditorModel.getName();
			case StoreListView.INDEX_URL:
				return storeEditorModel.getUrl();
			case StoreListView.INDEX_STORE_STATE:
				return AdminStoresMessages.get().getMessage(storeEditorModel.getStoreState().getNameMessageKey());
			default:
				return ""; //$NON-NLS-1$
			}
		}
	}

	@Override
	public StoreEditorModel getSelectedStoreEditorModel() {
		IStructuredSelection selection = (IStructuredSelection) getViewer().getSelection();
		StoreEditorModel storeEditorModel = null;
		if (!selection.isEmpty()) {
			storeEditorModel = (StoreEditorModel) selection.getFirstElement();
		}
		return storeEditorModel;
	}

	@Override
	public void storeChanged(final ItemChangeEvent<StoreEditorModel> event) {
		final StoreEditorModel changedStoreEditorModel = event.getItem();
		switch (event.getEventType()) {
		case ADD:
			getViewer().add(changedStoreEditorModel);
			break;
		case CHANGE:
			updateStore(changedStoreEditorModel);
			break;
		case REMOVE:
			getViewer().remove(changedStoreEditorModel);
			break;
		default:
			break;
		}

		//refresh selection to notify selection listener to update action buttons
		getViewer().setSelection(getViewer().getSelection());
	}

	private void updateStore(final StoreEditorModel changedStoreEditorModel) {
		for (final TableItem currTableItem : getViewer().getTable().getItems()) {
			final StoreEditorModel currStoreEditorModel = (StoreEditorModel) currTableItem.getData();
			if (currStoreEditorModel.getUidPk() == changedStoreEditorModel.getUidPk()) {
				currTableItem.setData(changedStoreEditorModel);
				getViewer().update(changedStoreEditorModel, null);
				break;
			}
		}
	}

	@Override
	protected String getPartId() {
		return VIEW_ID;
	}
}
