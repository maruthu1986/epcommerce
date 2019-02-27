/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */

package com.elasticpath.cmclient.store.targetedselling.delivery.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ITableLabelProvider;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.event.ChangeSetMemberSelectionProvider;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.cmclient.core.views.AbstractCreateEditDeleteToolbar;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareAction;
import com.elasticpath.cmclient.store.StorePlugin;
import com.elasticpath.cmclient.store.targetedselling.TargetedSellingImageRegistry;
import com.elasticpath.cmclient.store.targetedselling.TargetedSellingMessages;
import com.elasticpath.cmclient.store.targetedselling.TargetedSellingPermissions;
import com.elasticpath.cmclient.store.targetedselling.delivery.actions.CreateDynamicContentDeliveryAction;
import com.elasticpath.cmclient.store.targetedselling.delivery.actions.DeleteDynamicContentDeliveryAction;
import com.elasticpath.cmclient.store.targetedselling.delivery.actions.EditDynamicContentDeliveryAction;
import com.elasticpath.cmclient.store.targetedselling.delivery.wizard.model.DynamicContentDeliveryModelAdapter;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.service.store.StoreService;
import com.elasticpath.service.targetedselling.DynamicContentDeliveryService;

/**
 * Dynamic content assignment list view Displays the results of DynamicContentDelivery search.
 * 
 */
public class DynamicContentDeliverySearchResultsView extends
		AbstractCreateEditDeleteToolbar<DynamicContentDeliveryModelAdapter> implements ChangeSetMemberSelectionProvider {

	private static final String CONTENT_DELIVERY_TABLE = "Content Delivery"; //$NON-NLS-1$

	private final DynamicContentDeliverySearchResultsViewLabelProvider labelProvider;
	
	private final StoreService storeService;

	private final AbstractPolicyAwareAction createDcdAction = new CreateDynamicContentDeliveryAction(
			TargetedSellingMessages.get().DynamicContentDeliveryToolbar_CreateAction,
			TargetedSellingImageRegistry.IMAGE_DYNAMIC_CONTENT_DELIVERY_CREATE_ACTION);
	
	private final AbstractPolicyAwareAction deleteDcdAction = new DeleteDynamicContentDeliveryAction(
			this,
			TargetedSellingMessages.get().DynamicContentDeliveryToolbar_DeleteAction,
			TargetedSellingImageRegistry.IMAGE_DYNAMIC_CONTENT_DELIVERY_DELETE_ACTION);
	
	private final AbstractPolicyAwareAction editDcdAction = new EditDynamicContentDeliveryAction(
			this,
			TargetedSellingMessages.get().DynamicContentDeliveryToolbar_EditAction,
			TargetedSellingImageRegistry.IMAGE_DYNAMIC_CONTENT_DELIVERY_EDIT_ACTION);
	
	/**
	 * Constructor for DynamicContentDeliverySearchResultsView.
	 */
	public DynamicContentDeliverySearchResultsView() {
		super(false, CONTENT_DELIVERY_TABLE);
		labelProvider = new DynamicContentDeliverySearchResultsViewLabelProvider(ServiceLocator.getService(ContextIdNames.UTILITY));
		storeService = ServiceLocator.getService(ContextIdNames.STORE_SERVICE);
		labelProvider.setAllStores(storeService.findAllStores());
	}

	/** The View's ID. */
	public static final String VIEW_ID = 
		"com.elasticpath.cmclient.store.targetedselling.delivery.views.DynamicContentDeliverySearchResultsView"; //$NON-NLS-1$

	@Override
	protected ITableLabelProvider getViewLabelProvider() {
		return labelProvider;
	}

	@Override
	protected String getPluginId() {
		return StorePlugin.PLUGIN_ID;
	}

	@Override
	protected void initializeTable(final IEpTableViewer epTableViewer) {
		final String[] columnNames = getListTableColumns();
		final int[] columnWidths = new int[] { 21, 160, 160, 140, 140, 180, 180 };

		for (int i = 0; i < columnNames.length; i++) {
			epTableViewer.addTableColumn(columnNames[i], columnWidths[i]);
		}
	}

	@Override
	protected boolean isAuthorized() {
		return AuthorizationService.getInstance().isAuthorizedWithPermission(TargetedSellingPermissions.DYNAMIC_CONTENT_DELIVERY_MANAGE);
	}

	@Override
	protected Action getCreateAction() {
		return createDcdAction;

	}

	@Override
	protected Action getDeleteAction() {
		return deleteDcdAction;
	}

	@Override
	protected Action getEditAction() {
		return editDcdAction;
	}

	@Override
	protected String getCreateActionTooltip() {
		return TargetedSellingMessages.get().DynamicContentToolbar_CreateAction;
	}

	@Override
	protected String getDeleteActionTooltip() {
		return TargetedSellingMessages.get().DynamicContentToolbar_DeleteAction;
	}

	@Override
	protected String getEditActionTooltip() {
		return TargetedSellingMessages.get().DynamicContentToolbar_EditAction;
	}

	@Override
	protected String getSeparatorName() {
		return "dynamicContentAssignmentActionGroup"; //$NON-NLS-1$	
	}

	@Override
	protected String[] getListTableColumns() {
		return new String[] {
				"", //$NON-NLS-1$
				TargetedSellingMessages.get().DynamicContentDeliveryName,
				TargetedSellingMessages.get().DynamicContentDeliveryDescription,
				TargetedSellingMessages.get().DynamicContentDelivery_Store,
				TargetedSellingMessages.get().DynamicContentDelivery_ContentSpace,
				TargetedSellingMessages.get().DynamicContentDelivery_StartDate,
				TargetedSellingMessages.get().DynamicContentDelivery_EndDate };
	}

	@Override
	protected void updateActions(final boolean enabled) {
		createDcdAction.reApplyStatePolicy();
		deleteDcdAction.reApplyStatePolicy();
		editDcdAction.reApplyStatePolicy();
	}

	@Override
	public Object resolveObjectMember(final Object changeSetObjectSelection) {
		DynamicContentDeliveryModelAdapter adapter = (DynamicContentDeliveryModelAdapter) changeSetObjectSelection;
		DynamicContentDeliveryService dcdService = ServiceLocator.getService(ContextIdNames.DYNAMIC_CONTENT_DELIVERY_SERVICE);
		return dcdService.findByGuid(adapter.getGuid());
	}

	@Override
	protected String getPartId() {
		return VIEW_ID;
	}
}