/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;


/**
 * Class abstracts standard behaviour of menu with three items - Delete, Create,
 * Edit.
 *
 * @param <Entity>
 */
public abstract class AbstractCreateEditDeleteToolbar<Entity> extends
	AbstractPageNavigationListView<Entity> {

	/**
	 * Actions.
	 */
	private Action createAction;
	private Action deleteAction;
	private Action editAction;

	private String groupName;

	/**
	 * Constructor.
	 *
	 * @param checkable will be view use a checkable editor
	 * @param tableName name of the table
	 */
	public AbstractCreateEditDeleteToolbar(final boolean checkable, final String tableName) {
		super(checkable, tableName);
	}

	/**
	 * Gets the currently selected item.
	 *
	 * @return the currently item.
	 */
	public Entity getSelectedItem() {
		if (getViewer() == null || getViewer().getSelection() == null) {
			return null;
		}
		final IStructuredSelection selection = (IStructuredSelection) this
			.getViewer().getSelection();
		Entity shippingLevel = null;
		if (!selection.isEmpty()) {
			shippingLevel = (Entity) selection.getFirstElement();
		}
		return shippingLevel;
	}

	@Override
	public void createViewPartControl(final Composite parent) {
		super.createViewPartControl(parent);

		this.addDoubleClickAction(editAction);

		this.getViewer().addSelectionChangedListener(event -> {
			final IStructuredSelection strSelection = (IStructuredSelection) event
				.getSelection();

			final boolean enabled = !strSelection.isEmpty()
				&& isAuthorized();

			updateActions(enabled);
		});

		if (getPaginationControl() != null) {
			getPaginationControl().updateNavigationComponents();
		}
	}

	@Override
	protected void initializeToolbar(final IToolBarManager manager) { // NOPMD

		final Separator actionGroup = new Separator(
			getSeparatorName());
		manager.add(actionGroup);

		groupName = actionGroup.getGroupName();

		editAction = addToolbarButton(manager, getEditActionTooltip(), getEditAction());
		createAction = addToolbarButton(manager, getCreateActionTooltip(), getCreateAction());
		deleteAction = addToolbarButton(manager, getDeleteActionTooltip(), getDeleteAction());

		updateActions(false);

		super.initializeToolbar(manager);
	}

	private Action addToolbarButton(final IToolBarManager manager, final String tooltip, final Action action) {
		if (action != null) {
			action.setToolTipText(tooltip);
			final ActionContributionItem actionContributionItem = new ActionContributionItem(action);
			actionContributionItem.setMode(ActionContributionItem.MODE_FORCE_TEXT);
			manager.appendToGroup(groupName, actionContributionItem);
		}
		return action;
	}

	/**
	 * @return action toolbar group name
	 */
	protected String getActionGroupName() {
		return groupName;
	}

	/**
	 * Updates the actions with the value of the parameter.
	 *
	 * @param enabled should the actions get enabled/disabled
	 */
	protected void updateActions(final boolean enabled) {
		if (deleteAction != null) {
			deleteAction.setEnabled(enabled);
		}

		if (createAction != null) {
			createAction.setEnabled(isAuthorized());
		}

		if (editAction != null) {
			editAction.setEnabled(enabled);
		}
	}

	/**
	 * Returns create item action.
	 *
	 * @return create item action
	 */
	protected abstract Action getCreateAction();

	/**
	 * Returns tooltip for create item action.
	 *
	 * @return create item action tooltip
	 */
	protected abstract String getCreateActionTooltip();

	/**
	 * Returns delete item action.
	 *
	 * @return delete item action
	 */
	protected abstract Action getDeleteAction();

	/**
	 * Returns tooltip for delete item action.
	 *
	 * @return delete item action tooltip
	 */
	protected abstract String getDeleteActionTooltip();

	/**
	 * Returns edit item action.
	 *
	 * @return edit item action
	 */
	protected abstract Action getEditAction();

	/**
	 * Returns tooltip for edit item action.
	 *
	 * @return edit item action tooltip
	 */
	protected abstract String getEditActionTooltip();

	/**
	 * Checks whether action is authorized.
	 *
	 * @return boolean - true if authorized, false otherwise.
	 */

	protected abstract boolean isAuthorized();

	/**
	 * Returns array of columns to build result view table.
	 *
	 * @return - list of columns.
	 */
	protected abstract String[] getListTableColumns();

	/**
	 * Name used to create toolbar separator.
	 *
	 * @return name of separator
	 */
	protected abstract String getSeparatorName();
}
