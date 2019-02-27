/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.pricelistassignments.views;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ITableLabelProvider;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.event.ChangeSetMemberSelectionProvider;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.cmclient.core.views.AbstractCreateEditDeleteToolbar;
import com.elasticpath.cmclient.pricelistassignments.actions.CreatePriceListAssigment;
import com.elasticpath.cmclient.pricelistassignments.actions.DeletePriceListAssigment;
import com.elasticpath.cmclient.pricelistassignments.actions.EditPriceListAssigment;
import com.elasticpath.cmclient.pricelistmanager.PriceListManagerImageRegistry;
import com.elasticpath.cmclient.pricelistmanager.PriceListManagerMessages;
import com.elasticpath.cmclient.pricelistmanager.PriceListManagerPlugin;
import com.elasticpath.common.dto.pricing.PriceListAssignmentsDTO;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.service.pricing.PriceListAssignmentService;

/**
 * View to display the results of a Price List Assignment Search.
 */
public class PriceListAssigmentsSearchView extends AbstractCreateEditDeleteToolbar<PriceListAssignmentsDTO> 
	implements ChangeSetMemberSelectionProvider {

	private static final String PRICE_LIST_ASSIGNMENT_TABLE = "Price List Assignment Search Result"; //$NON-NLS-1$

	private final EditPriceListAssigment editPriceListAssignmentAction = new EditPriceListAssigment(
					this,
					PriceListManagerMessages.get().PriceListAssignmentSearchResult_EditAction,
					PriceListManagerImageRegistry.IMAGE_PRICE_LIST_ASSIGN_OPEN
					);

	private final DeletePriceListAssigment deletePriceListAssignmentAction = new DeletePriceListAssigment(
					this,
					PriceListManagerMessages.get().PriceListAssignmentSearchResult_DeleteAction,
					PriceListManagerImageRegistry.IMAGE_PRICE_LIST_ASSIGN_DELETE				
					);

	private final CreatePriceListAssigment createPriceListAssignmentAction = new CreatePriceListAssigment(
					PriceListManagerMessages.get().PriceListAssignmentSearchResult_CreateAction,
					PriceListManagerImageRegistry.IMAGE_PRICE_LIST_ASSIGN_ADD				
					);
	
	private static final Logger LOG = Logger.getLogger(PriceListAssigmentsSearchView.class);
	
	/** The View ID. */
	public static final String VIEW_ID = "com.elasticpath.cmclient.pricelistassignments.views.PriceListAssigmentsSearchView";
	
	/**
	 * Constructor.
	 */
	public PriceListAssigmentsSearchView() {
		super(false, PRICE_LIST_ASSIGNMENT_TABLE);
		LOG.info("Created:" + VIEW_ID); //$NON-NLS-1$
	}
	
	@Override
	protected boolean isAuthorized() {		
		return true; // TODO: need to add permissions
	}

	@Override
	protected Action getCreateAction() {		
		return createPriceListAssignmentAction;
	}

	@Override
	protected Action getDeleteAction() {
		return deletePriceListAssignmentAction;
	}

	@Override
	protected Action getEditAction() {
		return editPriceListAssignmentAction;
	}

	@Override
	protected String getCreateActionTooltip() {
		return PriceListManagerMessages.get().PriceListAssignmentSearchResult_CreateActionTooltip;
	}

	@Override
	protected String getDeleteActionTooltip() {
		return PriceListManagerMessages.get().PriceListAssignmentSearchResult_DeleteActionTooltip;
	}

	@Override
	protected String getEditActionTooltip() {
		return PriceListManagerMessages.get().PriceListAssignmentSearchResult_EditActionTooltip;
	}

	@Override
	protected String getSeparatorName() {
		return "priceListAssignmentAssignmentActionGroup"; //$NON-NLS-1$	
	}

	@Override
	protected String getPluginId() {
		return PriceListManagerPlugin.PLUGIN_ID;
	}

	@Override
	protected void initializeTable(final IEpTableViewer epTableViewer) {
		final String[] columnNames = getListTableColumns();
		final int[] columnWidths = new int[] { 200, 230, 140, 70, 180, 100, 100 };

		for (int i = 0; i < columnNames.length; i++) {
			epTableViewer.addTableColumn(columnNames[i], columnWidths[i]);
		}
	}
	
	@Override
	protected String[] getListTableColumns() {
		return new String[] {
				PriceListManagerMessages.get().PriceListAssignmentSearchResult_Name,
				PriceListManagerMessages.get().PriceListAssignmentSearchResult_Description,
				PriceListManagerMessages.get().PriceListAssignmentSearchResult_CatalogName,
				PriceListManagerMessages.get().PriceListAssignmentSearchResult_Priority,
				PriceListManagerMessages.get().PriceListAssignmentSearchResult_PriceListName,
				PriceListManagerMessages.get().PriceListAssignmentSearchResult_StartDate,
				PriceListManagerMessages.get().PriceListAssignmentSearchResult_EndDate };
	}	
	
	
	@Override
	protected ITableLabelProvider getViewLabelProvider() {
		return new PriceListAssignmentSearchResultsViewLabelProvider();
	}

	@Override
	protected void updateActions(final boolean enabled) {
		createPriceListAssignmentAction.reApplyStatePolicy();
		deletePriceListAssignmentAction.reApplyStatePolicy();
		editPriceListAssignmentAction.reApplyStatePolicy();
	}

	/**
	 * Resolves an object from the table and loads the price list assignment.
	 * 
	 * @param changeSetObjectSelection the object to resolve
	 * @return a PriceListAssignment instance
	 */
	@Override
	public Object resolveObjectMember(final Object changeSetObjectSelection) {
		PriceListAssignmentsDTO dto = (PriceListAssignmentsDTO) changeSetObjectSelection;
		PriceListAssignmentService priceListAssignmentService = ServiceLocator.getService(ContextIdNames.PRICE_LIST_ASSIGNMENT_SERVICE);
		return priceListAssignmentService.findByGuid(dto.getGuid());
	}

	@Override
	protected String getPartId() {
		return VIEW_ID;
	}
}
