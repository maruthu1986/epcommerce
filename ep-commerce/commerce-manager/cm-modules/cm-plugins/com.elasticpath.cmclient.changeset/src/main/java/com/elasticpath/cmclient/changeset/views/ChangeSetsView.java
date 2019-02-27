/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.changeset.views;

import java.util.Collection;

import org.apache.commons.lang.ArrayUtils;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;

import com.elasticpath.cmclient.changeset.ChangeSetImageRegistry;
import com.elasticpath.cmclient.changeset.ChangeSetMessages;
import com.elasticpath.cmclient.changeset.ChangeSetPermissions;
import com.elasticpath.cmclient.changeset.ChangeSetPlugin;
import com.elasticpath.cmclient.changeset.actions.CreateChangeSetAction;
import com.elasticpath.cmclient.changeset.actions.DeleteChangeSetAction;
import com.elasticpath.cmclient.changeset.actions.OpenChangeSetAction;
import com.elasticpath.cmclient.changeset.event.ChangeSetEventService;
import com.elasticpath.cmclient.changeset.event.ChangeSetSearchEventListener;
import com.elasticpath.cmclient.changeset.helpers.impl.ChangeSetSearchRequestJob;
import com.elasticpath.cmclient.core.event.SearchResultEvent;
import com.elasticpath.cmclient.core.helpers.AbstractSearchRequestJob;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.ui.framework.IEpTableColumn;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.cmclient.core.views.AbstractSortListView;
import com.elasticpath.domain.changeset.ChangeSet;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.service.search.query.SortBy;
import com.elasticpath.service.search.query.StandardSortBy;

/**
 * A view for change sets and all the actions related to them.
 */
public class ChangeSetsView extends AbstractSortListView implements ChangeSetSearchEventListener {

	/** The view ID. */
	public static final String ID_CHANGESETS_VIEW = ChangeSetsView.class.getName();

	private static final String CHANGESET_VIEW_TABLE = "Changeset View Table";

	private DeleteChangeSetAction deleteChangeSetAction;

	private OpenChangeSetAction openChangeSetAction;

	private Collection<ChangeSet> changeSets;
	
	private ChangeSetSearchRequestJob changeSetSearchRequestJob;

	/**
	 * Constructs a new view.
	 */
	public ChangeSetsView() {
		super(true, CHANGESET_VIEW_TABLE);
		registerListeners();
	}

	/**
	 * Register listeners.
	 */
	private void registerListeners() {
		ChangeSetEventService.getInstance().registerChangeSetSearchEventListener(this);
	}

	
	@Override
	protected Object[] getViewInput() {

		// Need to update change sets again
		if (changeSets != null) {
			return changeSets.toArray();
		}
		return ArrayUtils.EMPTY_OBJECT_ARRAY;
	}
	
	/**
	 * ChangeSetsUpdated.
	 * @param event contains list of change sets.
	 */
	@Override
	public void changeSetSearchUpdated(final SearchResultEvent<ChangeSet> event) {
		changeSetSearchRequestJob = (ChangeSetSearchRequestJob) event.getSource();
		getViewer().getTable().getDisplay().syncExec((Runnable) () -> {
			setResultsStartIndex(event.getStartIndex());
			setResultsCount(event.getTotalNumberFound());

			if (event.getItems().isEmpty() && event.getStartIndex() <= 0) {
				ChangeSetsView.this.showMessage(ChangeSetMessages.get().NoMatchingChangeSets);
			} else {
				ChangeSetsView.this.hideErrorMessage();
			}

			ChangeSetsView.this.changeSets = event.getItems();
			getViewer().setInput(ChangeSetsView.this.changeSets.toArray());
			ChangeSetsView.this.updateSortingOrder(changeSetSearchRequestJob.getSearchCriteria());
			ChangeSetsView.this.updateNavigationComponents();
			ChangeSetsView.this.refreshViewerInput();
			IStructuredSelection selection = (IStructuredSelection) getViewer().getSelection();
			updateSelection(selection);
		});
	
	}

	private void updateSelection(final IStructuredSelection selection) {
		
		if (selection != null) {
			ChangeSet selectedChangeSet = (ChangeSet) selection.getFirstElement();

			IStructuredSelection newSelection = null;
			for (ChangeSet changeSet : changeSets) {
				if (changeSet.equals(selectedChangeSet)) {
					newSelection = new StructuredSelection(changeSet);
				}
			}
			getViewer().setSelection(newSelection);
		}
	}


	@Override
	protected ITableLabelProvider getViewLabelProvider() {
		return new ChangeSetsLabelProvider();
	}

	@Override
	protected void initializeTable(final IEpTableViewer epTableViewer) {
		final int[] widths = new int[] { 200, 150, 150, 100, 300 };
		
		final String[] columnNames = new String[] {
			ChangeSetMessages.get().ChangeSetsView_NameColumn,
			ChangeSetMessages.get().ChangeSetsView_CreatorColumn,
			ChangeSetMessages.get().ChangeSetsView_CreatedDateColumn,
			ChangeSetMessages.get().ChangeSetsView_State,
			ChangeSetMessages.get().ChangeSetsView_DescriptionColumn
		};
		
		final SortBy[] sortBy = new SortBy[] {
			StandardSortBy.NAME,
			null,
			StandardSortBy.CREATED_DATE,
			StandardSortBy.STATE,
			StandardSortBy.DESCRIPTION
		};
		
		for (int i = 0; i < columnNames.length; i++) {
			IEpTableColumn addTableColumn = epTableViewer.addTableColumn(columnNames[i], widths[i]);
			registerTableColumn(addTableColumn, sortBy[i]);
		}
		
		epTableViewer.getSwtTableViewer().addSelectionChangedListener((ISelectionChangedListener) event -> {
			openChangeSetAction.setEnabled(getSelectedChangeSet() != null && isAuthorized());
			deleteChangeSetAction.setEnabled(getSelectedChangeSet() != null && isAuthorized());
		});
		// set the table viewer to provide selection events to all the registered listeners on the view site
		getViewSite().setSelectionProvider(epTableViewer.getSwtTableViewer());
	}

	private ChangeSet getSelectedChangeSet() {
		final IStructuredSelection selection = (IStructuredSelection) getViewer().getSelection();
		return (ChangeSet) selection.getFirstElement();
	}

	@Override
	protected String getPluginId() {
		return ChangeSetPlugin.PLUGIN_ID;
	}

	@Override
	protected ToolBarManager createOrRetrieveToolbarManager() {
		return (ToolBarManager) getViewSite().getActionBars().getToolBarManager();
	}

	@Override
	protected String getPluginContributionSeparatorGroup() {
		return "changeSet1"; //$NON-NLS-1$
	}

	@Override
	protected void initializeViewToolbar() {
		final Separator changeSetActionGroup = new Separator("changeSetActionGroup"); //$NON-NLS-1$
		final IToolBarManager manager = getViewSite().getActionBars().getToolBarManager();
		manager.add(changeSetActionGroup);

		CreateChangeSetAction createChangeSetAction = new CreateChangeSetAction(getViewSite(), 
				ChangeSetMessages.get().ChangeSetsView_CreateChangeSet, ChangeSetImageRegistry.CHANGESET_ADD);
		createChangeSetAction.setToolTipText(ChangeSetMessages.get().ChangeSetsView_CreateChangeSetTooltip);
		createChangeSetAction.setEnabled(isAuthorized());
		
		openChangeSetAction = new OpenChangeSetAction(getViewer(), getViewSite(), 
				ChangeSetMessages.get().ChangeSetsView_EditChangeSet, ChangeSetImageRegistry.CHANGESET);
		openChangeSetAction.setEnabled(false);
		openChangeSetAction.setToolTipText(ChangeSetMessages.get().ChangeSetsView_EditChangeSetTooltip);
		addDoubleClickAction(openChangeSetAction);

		deleteChangeSetAction = new DeleteChangeSetAction(this, getSite(),
				ChangeSetMessages.get().ChangeSetsView_DeleteChangeSet, ChangeSetImageRegistry.CHANGESET_DELETE);
		deleteChangeSetAction.setToolTipText(ChangeSetMessages.get().ChangeSetsView_DeleteChangeSetTooltip);
		deleteChangeSetAction.setEnabled(false);

		final ActionContributionItem createChangeSetActionContributionItem = new ActionContributionItem(createChangeSetAction);
		final ActionContributionItem editChangeSetActionContributionItem = new ActionContributionItem(openChangeSetAction);
		final ActionContributionItem removeChangeSetActionContributionItem = new ActionContributionItem(deleteChangeSetAction);

		createChangeSetActionContributionItem.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		editChangeSetActionContributionItem.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		removeChangeSetActionContributionItem.setMode(ActionContributionItem.MODE_FORCE_TEXT);

		manager.appendToGroup(changeSetActionGroup.getGroupName(), editChangeSetActionContributionItem);
		manager.appendToGroup(changeSetActionGroup.getGroupName(), createChangeSetActionContributionItem);
		manager.appendToGroup(changeSetActionGroup.getGroupName(), removeChangeSetActionContributionItem);
	}

	/**
	 * Checks whether manage change sets action is authorized.
	 *
	 * @return boolean
	 */
	protected boolean isAuthorized() {
		return isAuthorized(ChangeSetPermissions.CHANGE_SET_PERMISSIONS_MANAGE)
			|| isAuthorized(ChangeSetPermissions.WORK_WITH_CHANGE_SETS_PERMISSION);
	}


	/**
	 *
	 */
	private boolean isAuthorized(final String permissionId) {
		return AuthorizationService.getInstance().isAuthorizedWithPermission(permissionId);
	}


	@Override
	public void dispose() {
		ChangeSetEventService.getInstance().unregisterChangeSetSearchEventListener(this);
		super.dispose();
	}

	@Override
	public AbstractSearchRequestJob< ? extends Persistable> getSearchRequestJob() {
		return changeSetSearchRequestJob;
	}
	
	@Override
	protected void navigateFirst() {
		getViewer().setInput(null);
		super.navigateFirst();

		changeSetSearchRequestJob.executeSearchFromIndex(null, getResultsStartIndex());
		refreshViewerInput();
	}

	@Override
	protected void navigateNext() {
		getViewer().setInput(null);
		super.navigateNext();

		changeSetSearchRequestJob.executeSearchFromIndex(null, getResultsStartIndex());
		refreshViewerInput();
	}

	@Override
	protected void navigatePrevious() {
		getViewer().setInput(null);
		super.navigatePrevious();

		changeSetSearchRequestJob.executeSearchFromIndex(null, getResultsStartIndex());
		refreshViewerInput();
	}

	@Override
	protected void navigateLast() {
		getViewer().setInput(null);
		super.navigateLast();

		changeSetSearchRequestJob.executeSearchFromIndex(null, getResultsStartIndex());
		refreshViewerInput();
	}
	
	@Override
	protected void navigateTo(final int pageNumber) {
		getViewer().setInput(null);
		
		changeSetSearchRequestJob.executeSearchFromIndex(null, getStartIndexByPageNumber(pageNumber, getResultsPaging()));
		refreshViewerInput();
	}
	
	@Override
	public void paginationChange(final int newValue) {
		getViewer().setInput(null);
		super.paginationChange(newValue);
		if (changeSetSearchRequestJob != null) {
			super.navigateFirst();
			changeSetSearchRequestJob.executeSearchFromIndex(null, getResultsStartIndex());
		}
		refreshViewerInput();
	}

	@Override
	protected String getPartId() {
		return ID_CHANGESETS_VIEW;
	}
}
