/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
/**
 * 
 */
package com.elasticpath.cmclient.store.targetedselling.dynamiccontent.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ITableLabelProvider;

import com.elasticpath.cmclient.core.event.ChangeSetMemberSelectionProvider;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.cmclient.core.views.AbstractCreateEditDeleteToolbar;
import com.elasticpath.cmclient.store.StorePlugin;
import com.elasticpath.cmclient.store.targetedselling.TargetedSellingImageRegistry;
import com.elasticpath.cmclient.store.targetedselling.TargetedSellingMessages;
import com.elasticpath.cmclient.store.targetedselling.dynamiccontent.actions.CreateDynamicContentAction;
import com.elasticpath.cmclient.store.targetedselling.dynamiccontent.actions.DeleteDynamicContentAction;
import com.elasticpath.cmclient.store.targetedselling.dynamiccontent.actions.EditDynamicContentAction;
import com.elasticpath.domain.contentspace.DynamicContent;

/**
 * DynamicContentSearchResultsView Displays the results search.
 * 
 */
public class DynamicContentSearchResultsView extends
		AbstractCreateEditDeleteToolbar<DynamicContent> implements ChangeSetMemberSelectionProvider {

	/** The View's ID. */
	public static final String VIEW_ID = 
		"com.elasticpath.cmclient.store.targetedselling.dynamiccontent.views.DynamicContentSearchResultsView"; //$NON-NLS-1$

	private static final String DYNAMIC_CONTENT_TABLE = "Dynamic Content"; //$NON-NLS-1$

	private final CreateDynamicContentAction createDynamicContentAction = new CreateDynamicContentAction(
			TargetedSellingMessages.get().DynamicContentToolbar_CreateAction,
			TargetedSellingImageRegistry.IMAGE_DYNAMIC_CONTENT_CREATE_ACTION);
	
	private final DeleteDynamicContentAction deleteDynamicContentAction = new DeleteDynamicContentAction(this,
			TargetedSellingMessages.get().DynamicContentToolbar_DeleteAction,
			TargetedSellingImageRegistry.IMAGE_DYNAMIC_CONTENT_DELETE_ACTION);
	
	private final EditDynamicContentAction editDynamicContentAction = new EditDynamicContentAction(this,
			TargetedSellingMessages.get().DynamicContentToolbar_EditAction,
			TargetedSellingImageRegistry.IMAGE_DYNAMIC_CONTENT_EDIT_ACTION);

	/**
	 * Default constructor.
	 */
	public DynamicContentSearchResultsView() {
		super(false, DYNAMIC_CONTENT_TABLE);
	}

	@Override
	protected ITableLabelProvider getViewLabelProvider() {
		return new DynamicContentSearchResultsViewLabelProvider();
	}

	@Override
	protected boolean isAuthorized() {
		// TODO: need to add permissions
		return true;
	}
	@Override
	protected Action getCreateAction() {
		return createDynamicContentAction;
	}
	
	@Override
	protected Action getDeleteAction() {
		return deleteDynamicContentAction;
	}

	@Override
	protected Action getEditAction() {
		return editDynamicContentAction;
	}

	@Override	protected String getCreateActionTooltip() {
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
	protected String[] getListTableColumns() {
		return new String[] {
				"", //$NON-NLS-1$
				TargetedSellingMessages.get().DynamicContentName,
				TargetedSellingMessages.get().DynamicContentDescription };
	}

	@Override
	protected String getPluginId() {
		return StorePlugin.PLUGIN_ID;
	}

	@Override
	protected void initializeTable(final IEpTableViewer epTableViewer) {
		final String[] columnNames = getListTableColumns();
		final int[] columnWidths = new int[] { 21, 160, 160 };

		for (int i = 0; i < columnNames.length; i++) {
			epTableViewer.addTableColumn(columnNames[i], columnWidths[i]);
		}
	}	

	@Override
	protected String getSeparatorName() {
		return "dynamicContentActionGroup"; //$NON-NLS-1$
	}

	/**
	 * Converts the given parameter to a object member class.
	 * In this case we directly return the object as it is the one we use in the table.
	 * 
	 * @param changeSetObjectSelection the object selection
	 * @return the resolved object
	 */
	@Override
	public Object resolveObjectMember(final Object changeSetObjectSelection) {
		return changeSetObjectSelection;
	}
	
	@Override
	protected void updateActions(final boolean enabled) {
		createDynamicContentAction.reApplyStatePolicy();
		editDynamicContentAction.reApplyStatePolicy();
		deleteDynamicContentAction.reApplyStatePolicy();
	}

	@Override
	protected String getPartId() {
		return VIEW_ID;
	}
}
