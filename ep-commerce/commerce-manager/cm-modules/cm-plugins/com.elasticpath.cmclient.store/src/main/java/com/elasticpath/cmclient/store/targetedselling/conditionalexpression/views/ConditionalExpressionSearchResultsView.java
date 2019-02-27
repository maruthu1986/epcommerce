/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
/**
 * 
 */
package com.elasticpath.cmclient.store.targetedselling.conditionalexpression.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ITableLabelProvider;

import com.elasticpath.cmclient.core.event.ChangeSetMemberSelectionProvider;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.cmclient.core.views.AbstractCreateEditDeleteToolbar;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareAction;
import com.elasticpath.cmclient.store.StorePlugin;
import com.elasticpath.cmclient.store.targetedselling.TargetedSellingImageRegistry;
import com.elasticpath.cmclient.store.targetedselling.TargetedSellingMessages;
import com.elasticpath.cmclient.store.targetedselling.TargetedSellingPermissions;
import com.elasticpath.cmclient.store.targetedselling.conditionalexpression.action.CreateConditionalExpressionAction;
import com.elasticpath.cmclient.store.targetedselling.conditionalexpression.action.DeleteConditionalExpressionAction;
import com.elasticpath.cmclient.store.targetedselling.conditionalexpression.action.EditConditionalExpressionAction;
import com.elasticpath.tags.domain.ConditionalExpression;

/**
 * ConditionalExpressionSearchResultsView.
 *
 */
public class ConditionalExpressionSearchResultsView extends
		AbstractCreateEditDeleteToolbar<ConditionalExpression> implements ChangeSetMemberSelectionProvider {

	/** The View's ID. */
	public static final String VIEW_ID = 
		"com.elasticpath.cmclient.store.targetedselling.conditionalexpression.views.ConditionalExpressionSearchResultsView"; //$NON-NLS-1$

	private static final String CONDITIONAL_EXPRESSION_TABLE = "Conditional Expression"; //$NON-NLS-1$


	private AbstractPolicyAwareAction createPolicyAwareAction;
	private AbstractPolicyAwareAction editPolicyAwareAction;
	private AbstractPolicyAwareAction deletePolicyAwareAction;
	
	/**
	 * Default Constructor.
	 */
	public ConditionalExpressionSearchResultsView() {
		super(false, CONDITIONAL_EXPRESSION_TABLE);
	}

	@Override
	protected Action getCreateAction() {
		if (createPolicyAwareAction == null) {
			createPolicyAwareAction = new CreateConditionalExpressionAction(this,
				TargetedSellingMessages.get().ConditionalExpressionToolbar_CreateAction,
				TargetedSellingImageRegistry.IMAGE_CONDITIONAL_EXPRESSION_CREATE_ACTION);
		}
		return createPolicyAwareAction;
	}

	@Override
	protected String getCreateActionTooltip() {
		return TargetedSellingMessages.get().ConditionalExpressionToolbar_CreateAction;
	}

	@Override
	protected Action getDeleteAction() {
		if (deletePolicyAwareAction == null) {
			deletePolicyAwareAction = new DeleteConditionalExpressionAction(this,
				TargetedSellingMessages.get().ConditionalExpressionToolbar_DeleteAction,
				TargetedSellingImageRegistry.IMAGE_CONDITIONAL_EXPRESSION_DELETE_ACTION);
		}
		return deletePolicyAwareAction;
	}

	@Override
	protected String getDeleteActionTooltip() {
		return TargetedSellingMessages.get().ConditionalExpressionToolbar_DeleteAction;
	}

	@Override
	protected Action getEditAction() {
		if (editPolicyAwareAction == null) {
			editPolicyAwareAction = new EditConditionalExpressionAction(this,
				TargetedSellingMessages.get().ConditionalExpressionToolbar_EditAction,
				TargetedSellingImageRegistry.IMAGE_CONDITIONAL_EXPRESSION_EDIT_ACTION);
		}
		return editPolicyAwareAction;
	}

	@Override
	protected String getEditActionTooltip() {
		return TargetedSellingMessages.get().ConditionalExpressionToolbar_EditAction;
	}

	@Override
	protected String[] getListTableColumns() {
		return new String[] {
				"", //$NON-NLS-1$
				TargetedSellingMessages.get().ConditionalExpressionName,
				TargetedSellingMessages.get().ConditionalExpressionDescription,
				TargetedSellingMessages.get().ConditionalExpressionType
				};
	}

	@Override
	protected String getSeparatorName() {
		return "conditionalExpressionActionGroup"; //$NON-NLS-1$
	}

	@Override
	protected boolean isAuthorized() {
		return AuthorizationService.getInstance().isAuthorizedWithPermission(TargetedSellingPermissions.CONDITIONAL_EXPRESSION_MANAGE);
	}

	@Override
	protected ITableLabelProvider getViewLabelProvider() {
		return new ConditionalExpressionSearchResultViewLabelProvider();
	}

	@Override
	protected String getPluginId() {
		return StorePlugin.PLUGIN_ID;
	}

	@Override
	protected void initializeTable(final IEpTableViewer epTableViewer) {
		final String[] columnNames = getListTableColumns();
		final int[] columnWidths = new int[] { 21, 160, 360, 160 };

		for (int i = 0; i < columnNames.length; i++) {
			epTableViewer.addTableColumn(columnNames[i], columnWidths[i]);
		}
	}

	@Override
	protected void updateActions(final boolean enabled) {
		editPolicyAwareAction.reApplyStatePolicy();
		createPolicyAwareAction.reApplyStatePolicy();
		deletePolicyAwareAction.reApplyStatePolicy();
	}

	@Override
	public Object resolveObjectMember(final Object changeSetObjectSelection) {
		return changeSetObjectSelection;
	}

	@Override
	protected String getPartId() {
		return VIEW_ID;
	}
}
