/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.pricelistmanager.views;

import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.widgets.Display;

import com.elasticpath.cmclient.core.comparator.PriceListDescriptorDTOComparator;
import com.elasticpath.cmclient.core.event.ChangeSetMemberSelectionProvider;
import com.elasticpath.cmclient.core.event.EventType;
import com.elasticpath.cmclient.core.event.SearchResultEvent;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.cmclient.core.views.AbstractCreateEditDeleteToolbar;
import com.elasticpath.cmclient.pricelistmanager.PriceListManagerImageRegistry;
import com.elasticpath.cmclient.pricelistmanager.PriceListManagerMessages;
import com.elasticpath.cmclient.pricelistmanager.PriceListManagerPlugin;
import com.elasticpath.cmclient.pricelistmanager.actions.CreatePriceList;
import com.elasticpath.cmclient.pricelistmanager.actions.CsvExportPricelistAction;
import com.elasticpath.cmclient.pricelistmanager.actions.DeletePriceList;
import com.elasticpath.cmclient.pricelistmanager.actions.EditPriceList;
import com.elasticpath.cmclient.pricelistmanager.event.PriceListChangedEvent;
import com.elasticpath.cmclient.pricelistmanager.event.PricingEventService;
import com.elasticpath.cmclient.pricelistmanager.event.listeners.PriceListChangedEventListener;
import com.elasticpath.cmclient.pricelistmanager.event.listeners.PriceListSearchResultUpdateListener;
import com.elasticpath.common.dto.pricing.PriceListDescriptorDTO;

/**
 * View to display the results of a Price List Search.
 */
public class PriceListSearchResultsView extends AbstractCreateEditDeleteToolbar<PriceListDescriptorDTO>
	implements PriceListSearchResultUpdateListener, PriceListChangedEventListener, ChangeSetMemberSelectionProvider {

	private static final String PRICE_LIST_SEARCH_RESULT_TABLE = "Price List Search Result"; //$NON-NLS-1$

	/**
	 * 
	 */
	private final EditPriceList editPriceListAction = new EditPriceList(
					this,
					PriceListManagerMessages.get().PriceListSearchResult_EditAction,
					PriceListManagerImageRegistry.IMAGE_PRICE_LIST_OPEN
					);
	/**
	 * 
	 */
	private final DeletePriceList deletePriceListAction = new DeletePriceList(
					this,
					PriceListManagerMessages.get().PriceListSearchResult_DeleteAction,
					PriceListManagerImageRegistry.IMAGE_PRICE_LIST_DELETE			
					);
	/**
	 * 
	 */
	private final CreatePriceList createPriceListAction = new CreatePriceList(
					PriceListManagerMessages.get().PriceListSearchResult_CreateAction,
					PriceListManagerImageRegistry.IMAGE_PRICE_LIST_ADD			
					);
	/**
	 * 
	 */
	private final CsvExportPricelistAction csvExportPriceListAction = new CsvExportPricelistAction(this,
			PriceListManagerMessages.get().PriceListCsvExport_Action,
			PriceListManagerImageRegistry.IMAGE_CSV_EXPORT, Display.getDefault());
	
	private static final Logger LOG = Logger.getLogger(PriceListSearchResultsView.class);
	/** The View ID. */
	public static final String VIEW_ID = "com.elasticpath.cmclient.pricelistmanager.views.PriceListSearchResultsView";

	private final PricingEventService eventService;

	@Override
	protected String getPluginId() {
		return PriceListManagerPlugin.PLUGIN_ID;
	}

	@Override
	protected void initializeToolbar(final IToolBarManager manager) {
		super.initializeToolbar(manager);
		csvExportPriceListAction.setToolTipText(PriceListManagerMessages.get().PriceListCsvExport_ActionTooltip);
		final ActionContributionItem csvExportActionContributionItem = new ActionContributionItem(
				csvExportPriceListAction);
		csvExportActionContributionItem.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		manager.appendToGroup(getActionGroupName(),
				csvExportActionContributionItem);
	}

	/**
	 * Constructor.
	 */
	public PriceListSearchResultsView() {
		super(false, PRICE_LIST_SEARCH_RESULT_TABLE);
		LOG.info("Creating PriceListSearchResultsView"); //$NON-NLS-1$
		eventService = PricingEventService.getInstance();
		eventService.addPriceListSearchResultUpdateListener(this);
		eventService.addPriceListChangedEventListener(this);
		//TODO: whether this should really be in this method is up for debate.
		//this.addDoubleClickAction(getEditAction());

	}
	
	@Override
	protected boolean isAuthorized() {		
		return true; // TODO: need to add permissions
	}
	
	@Override
	protected ITableLabelProvider getViewLabelProvider() {
		return new PriceListSearchResultsViewLabelProvider();
	}

	@Override
	protected void initializeTable(final IEpTableViewer table) {
		final String[] columnNames = getListTableColumns();

		final int[] columnWidths = new int[] { 250, 80, 700 };
		for (int i = 0; i < columnNames.length; i++) {
			table.addTableColumn(columnNames[i], columnWidths[i]);
		}
		getSite().setSelectionProvider(table.getSwtTableViewer());
	}
	

	/**
	 * Called by the event service when a new search has been performed.
	 * Sets the viewer's input to the search results.
	 * @param event the search result event
	 */
	@Override
	public void searchResultUpdated(final SearchResultEvent<PriceListDescriptorDTO> event) {
		Collections.sort(event.getItems(), new PriceListDescriptorDTOComparator()); 
		this.setSearchResultEvent(event);
	}

	@Override
	public void dispose() {
		super.dispose();
		eventService.removePriceListSearchResultUpdateListener(this);
		eventService.removePriceListChangedEventListener(this);
	}

	@Override
	public void priceListChanged(final PriceListChangedEvent event) {

		final PriceListDescriptorDTO changedPriceListDescriptorDTO = (PriceListDescriptorDTO) event.getSource();

		if (EventType.DELETE == event.getEventType()) {
			this.getPageNavigator().getItemList().remove(changedPriceListDescriptorDTO);
		} else {
			boolean found = false;
			List<PriceListDescriptorDTO> listDTO = this.getPageNavigator().getItemList();
			for (int i = 0; i < listDTO.size(); ++i) {
				if (listDTO.get(i).getGuid().equals(changedPriceListDescriptorDTO.getGuid())) {
					listDTO.set(i, changedPriceListDescriptorDTO);
					found = true;
					break;
				}
			}
	
			if (!found) { // new dto
				listDTO.add(changedPriceListDescriptorDTO);
			}
		}
		this.firePropertyChange(PROPERTY_MODEL);
	}
	
	@Override
	protected Action getCreateAction() {
		return createPriceListAction;
	}

	@Override
	protected String getCreateActionTooltip() {
		return PriceListManagerMessages.get().PriceListSearchResult_CreateActionTooltip;
	}

	@Override
	protected Action getDeleteAction() {
		return deletePriceListAction;
	}

	@Override
	protected String getDeleteActionTooltip() {
		return PriceListManagerMessages.get().PriceListSearchResult_DeleteActionTooltip;
	}

	@Override
	protected Action getEditAction() {
		return editPriceListAction;
	}

	@Override
	protected String getEditActionTooltip() {
		return PriceListManagerMessages.get().PriceListSearchResult_EditActionTooltip;
	}


	@Override
	protected String[] getListTableColumns() {
		return new String[] {
				PriceListManagerMessages.get().PriceListSearchResults_TableColumnTitle_Name,
				PriceListManagerMessages.get().PriceListSearchResults_TableColumnTitle_CurrencyCode,
				PriceListManagerMessages.get().PriceListSearchResults_TableColumnTitle_Description,
		};
	}
	@Override
	protected String getSeparatorName() {
		return "priceListActionGroup"; //$NON-NLS-1$
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
		// the actions of the price list view are policy aware
		
		editPriceListAction.reApplyStatePolicy();
		createPriceListAction.reApplyStatePolicy();
		deletePriceListAction.reApplyStatePolicy();
		csvExportPriceListAction.reApplyStatePolicy();
	}

	@Override
	protected String getPartId() {
		return VIEW_ID;
	}
}
