/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.store.shipping.views;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.event.SearchResultEvent;
import com.elasticpath.cmclient.core.helpers.AbstractSearchRequestJob;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.ui.framework.IEpTableColumn;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.cmclient.core.views.AbstractSortListView;
import com.elasticpath.cmclient.store.StorePlugin;
import com.elasticpath.cmclient.store.shipping.ShippingImageRegistry;
import com.elasticpath.cmclient.store.shipping.ShippingLevelsMessages;
import com.elasticpath.cmclient.store.shipping.ShippingLevelsPermissions;
import com.elasticpath.cmclient.store.shipping.actions.CreateShippingLevelAction;
import com.elasticpath.cmclient.store.shipping.actions.DeleteShippingLevelAction;
import com.elasticpath.cmclient.store.shipping.actions.EditShippingLevelAction;
import com.elasticpath.cmclient.store.shipping.events.ShippingLevelsEventListener;
import com.elasticpath.cmclient.store.shipping.events.ShippingLevelsEventService;
import com.elasticpath.cmclient.store.shipping.helpers.ShippingLevelSearchRequestJob;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.shipping.ShippingServiceLevel;
import com.elasticpath.domain.store.Store;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.service.search.query.SortBy;
import com.elasticpath.service.search.query.StandardSortBy;
import com.elasticpath.service.store.StoreService;

/**
 * View to show and allow the manipulation of the available Shipping Service Levels in CM.
 */
public class ShippingLevelsSearchResultsView extends AbstractSortListView implements ShippingLevelsEventListener {

	/** The View's ID. */
	public static final String VIEW_ID = "com.elasticpath.cmclient.store.shipping.views.ShippingLevelsSearchResultsView"; //$NON-NLS-1$

	private static final String SHIPPING_LEVELS_SEARCH_RESULTS_TABLE = "Shipping Levels Search Results"; //$NON-NLS-1$

	/** Column indices. */
	private static final int INDEX_STORE = 0;

	private static final int INDEX_REGION = 1;

	private static final int INDEX_CARIER = 2;

	private static final int INDEX_CODE = 3;

	private static final int INDEX_SERVICE_LEVEL_NAME = 4;

	private static final int INDEX_CALC_METHOD = 5;

	private static final int INDEX_ACTIVE = 6;

	/** Actions. */
	private Action createShippingLevelAction;

	private Action editShippingLevelAction;

	private Action deleteShippingLevelAction;

	private final boolean hasAssignedStores;

	private ShippingLevelSearchRequestJob serviceLevelsSearchRequestJob;
	
	private Object[] objects;
	
	/**
	 * The constructor.
	 */
	public ShippingLevelsSearchResultsView() {
		super(true, SHIPPING_LEVELS_SEARCH_RESULTS_TABLE);
		StoreService storeService = ServiceLocator.getService(ContextIdNames.STORE_SERVICE);
		List<Store> stores = storeService.findAllCompleteStores();
		AuthorizationService.getInstance().filterAuthorizedStores(stores);
		hasAssignedStores = !stores.isEmpty(); //NOPMD
	}

	@Override
	protected Object[] getViewInput() {
		if (objects == null) {
			return new Object[0];
		}
		return objects.clone();
	}

	@Override
	protected ITableLabelProvider getViewLabelProvider() {
		return new ShippingLevelListViewLabelProvider();
	}

	@Override
	protected void initializeTable(final IEpTableViewer table) {
		final String[] columnNames =
				new String[] {
						ShippingLevelsMessages.get().ShippingLevelStoreColumnLabel,
						ShippingLevelsMessages.get().ShippingLevelRegionColumnLabel,
						ShippingLevelsMessages.get().ShippingLevelCarierColumnLabel,
						ShippingLevelsMessages.get().ShippingLevelCode,
						ShippingLevelsMessages.get().ShippingLevelNameColumnLabel,
						ShippingLevelsMessages.get().ShippingLevelCalcMethodColumnLabel,
						ShippingLevelsMessages.get().ShippingLevelActiveColumnLabel
				};

		final int[] columnWidths = new int[]{160, 120, 120, 180, 200, 120, 60};
		final SortBy[] sortBy = new SortBy[]{
				StandardSortBy.STORE_NAME, 
				StandardSortBy.REGION, 
				StandardSortBy.CARRIER, 
				StandardSortBy.SERVICE_LEVEL_CODE, 
				StandardSortBy.SERVICE_LEVEL_NAME,
				null, 
				StandardSortBy.ACTIVE };
		for (int i = 0; i < columnNames.length; i++) {
			IEpTableColumn tableColumn = table.addTableColumn(columnNames[i], columnWidths[i]);
			registerTableColumn(tableColumn, sortBy[i]);
		}
		
		getSite().setSelectionProvider(table.getSwtTableViewer());
	}

	@Override
	protected void initializeViewToolbar() {

		final Separator shippingLevelActionGroup = new Separator("shippingLevelActionGroup"); //$NON-NLS-1$
		getToolbarManager().add(shippingLevelActionGroup);

		createShippingLevelAction =
				new CreateShippingLevelAction(this, ShippingLevelsMessages.get().CreateShippingLevelAction,
						ShippingImageRegistry.IMAGE_SHIPPING_LEVEL_CREATE);
		createShippingLevelAction.setToolTipText(ShippingLevelsMessages.get().CreateShippingLevelAction);
		editShippingLevelAction =
				new EditShippingLevelAction(this, ShippingLevelsMessages.get().EditShippingLevelAction,
						ShippingImageRegistry.IMAGE_SHIPPING_LEVEL);
		editShippingLevelAction.setToolTipText(ShippingLevelsMessages.get().EditShippingLevelAction);
		addDoubleClickAction(editShippingLevelAction);
		deleteShippingLevelAction =
				new DeleteShippingLevelAction(this, ShippingLevelsMessages.get().DeleteShippingLevelAction,
						ShippingImageRegistry.IMAGE_SHIPPING_LEVEL_DELETE);
		deleteShippingLevelAction.setToolTipText(ShippingLevelsMessages.get().DeleteShippingLevelAction);

		final ActionContributionItem createShippingLevelActionContributionItem = new ActionContributionItem(createShippingLevelAction);
		final ActionContributionItem editShippingLevelActionContributionItem = new ActionContributionItem(editShippingLevelAction);
		final ActionContributionItem deleteShippingLevelActionContributionItem = new ActionContributionItem(deleteShippingLevelAction);

		createShippingLevelActionContributionItem.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		editShippingLevelActionContributionItem.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		deleteShippingLevelActionContributionItem.setMode(ActionContributionItem.MODE_FORCE_TEXT);

		getToolbarManager().appendToGroup(shippingLevelActionGroup.getGroupName(), editShippingLevelActionContributionItem);
		getToolbarManager().appendToGroup(shippingLevelActionGroup.getGroupName(), createShippingLevelActionContributionItem);
		getToolbarManager().appendToGroup(shippingLevelActionGroup.getGroupName(), deleteShippingLevelActionContributionItem);

		// Disable buttons until a row is selected.
		editShippingLevelAction.setEnabled(false);
		deleteShippingLevelAction.setEnabled(false);

		createShippingLevelAction.setEnabled(hasAssignedStores && isAuthorized());

		this.getViewer().addSelectionChangedListener(event -> {
			final IStructuredSelection strSelection = (IStructuredSelection) event.getSelection();
			final ShippingServiceLevel firstSelectedLevel = (ShippingServiceLevel) strSelection.getFirstElement();

			final boolean enabled = firstSelectedLevel != null && isAuthorized()
					&& AuthorizationService.getInstance().isAuthorizedForStore(firstSelectedLevel.getStore());

			deleteShippingLevelAction.setEnabled(enabled);
			editShippingLevelAction.setEnabled(enabled);
		});
	}

	/**
	 * ShippingLevel list view label provider.
	 */
	protected class ShippingLevelListViewLabelProvider extends LabelProvider implements ITableLabelProvider {

		@Override
		public Image getColumnImage(final Object element, final int columnIndex) {
			return null;
		}

		@Override
		public String getColumnText(final Object element, final int columnIndex) {

			final ShippingServiceLevel shippingLevel = (ShippingServiceLevel) element;

			switch (columnIndex) {
				case ShippingLevelsSearchResultsView.INDEX_STORE:
					return shippingLevel.getStore().getName();
				case ShippingLevelsSearchResultsView.INDEX_REGION:
					return shippingLevel.getShippingRegion().getName();
				case ShippingLevelsSearchResultsView.INDEX_CARIER:
					return shippingLevel.getCarrier();
				case ShippingLevelsSearchResultsView.INDEX_CODE:
					return shippingLevel.getCode();
				case ShippingLevelsSearchResultsView.INDEX_SERVICE_LEVEL_NAME:
					return shippingLevel.getDisplayName(shippingLevel.getStore().getCatalog().getDefaultLocale(), true);
				case ShippingLevelsSearchResultsView.INDEX_CALC_METHOD:
					return ShippingLevelsMessages.get().localizeCalcParam(shippingLevel.getShippingCostCalculationMethod());
				case ShippingLevelsSearchResultsView.INDEX_ACTIVE:
					return getLocalizedValue(shippingLevel.isEnabled());
				default:
					return ShippingLevelsMessages.EMPTY_STRING;
			}
		}

		private String getLocalizedValue(final boolean enabled) {
			if (enabled) {
				return ShippingLevelsMessages.get().Yes;
			}
			return ShippingLevelsMessages.get().ActiveNo;
		}
	}

	/**
	 * Gets the currently selected Shipping Service Level.
	 * 
	 * @return the currently selected Shipping Service Level.
	 */
	public ShippingServiceLevel getSelectedShippingLevel() {
		final IStructuredSelection selection = (IStructuredSelection) getViewer().getSelection();
		ShippingServiceLevel shippingLevel = null;
		if (!selection.isEmpty()) {
			shippingLevel = (ShippingServiceLevel) selection.getFirstElement();
		}
		return shippingLevel;
	}

	@Override
	public void dispose() {
		ShippingLevelsEventService.getInstance().unregisterShippingLevelEventListener(this);
		super.dispose();
	}

	@Override
	public void init(final IViewSite site) throws PartInitException {
		super.init(site);
		ShippingLevelsEventService.getInstance().registerShippingLevelListener(this);
	}

	/**
	 * Checks whether manage shipping level action is authorized.
	 *
	 * @return boolean
	 */
	protected boolean isAuthorized() {
		return AuthorizationService.getInstance().isAuthorizedWithPermission(ShippingLevelsPermissions.SHIPPING_SERVICE_LEVELS_MANAGE);
	}

	@Override
	public void refreshViewerInput() {
		if (serviceLevelsSearchRequestJob != null) {
			serviceLevelsSearchRequestJob.executeSearchFromIndex(null, getResultsStartIndex());
		}
	}

	@Override
	protected String getPluginId() {
		return StorePlugin.PLUGIN_ID;
	}

	@Override
	public void searchResultsUpdate(final SearchResultEvent<ShippingServiceLevel> event) {
		serviceLevelsSearchRequestJob = (ShippingLevelSearchRequestJob) event.getSource();
		getViewer().getTable().getDisplay().syncExec(() -> {
			ShippingLevelsSearchResultsView.this.setResultsCount(event.getTotalNumberFound());
			ShippingLevelsSearchResultsView.this.getViewer().getTable().clearAll();
			if (event.getItems().isEmpty() && event.getStartIndex() <= 0) {
				ShippingLevelsSearchResultsView.this.showMessage(CoreMessages.get().NoSearchResultsError);
			} else {
				ShippingLevelsSearchResultsView.this.hideErrorMessage();
			}
			ShippingLevelsSearchResultsView.this.getViewer().setInput(event.getItems().toArray());
			ShippingLevelsSearchResultsView.this.setResultsStartIndex(event.getStartIndex());
			ShippingLevelsSearchResultsView.this.updateNavigationComponents();
			ShippingLevelsSearchResultsView.this.updateSortingOrder(serviceLevelsSearchRequestJob.getSearchCriteria());
		});
	}

	@Override
	public AbstractSearchRequestJob < ? extends Persistable > getSearchRequestJob() {
		return serviceLevelsSearchRequestJob;
	}

	@Override
	protected String getPartId() {
		return VIEW_ID;
	}
}
