/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.warehouse.views;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.AggregateValidationStatus;
import org.eclipse.core.runtime.IStatus;

import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.EpUiException;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.ControlModificationListener;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.ui.framework.IEpTabFolder;
import com.elasticpath.cmclient.core.views.AbstractCmClientView;
import com.elasticpath.cmclient.warehouse.WarehouseMessages;
import com.elasticpath.cmclient.warehouse.perspective.WarehousePerspectiveFactory;

/**
 * //TODO: copy of fulfillment SearchView. Need to be abstracted out. This sample class demonstrates how to plug-in a new workbench view. The view
 * shows data obtained from the model. The sample creates a dummy model on the fly, but a real implementation would connect to the model available
 * either in this or another plug-in (e.g. the workspace). The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be presented in the view. Each view can present the same model objects using
 * different labels and icons, if needed. Alternatively, a single label provider can be shared between views in order to ensure that objects of the
 * same type are presented in the same way everywhere.
 * <p>
 */

public class SearchView extends AbstractCmClientView implements SelectionListener, ControlModificationListener {

	private static final Logger LOG = Logger.getLogger(SearchView.class);

	/**
	 * SearchView ID specified in the plugin.xml file. It is the same as the class name
	 */
	public static final String ID_SEARCH_VIEW = SearchView.class.getName();

	private IEpTabFolder tabFolder;

	private Button searchButton;

	private Button clearButton;

	private final List<ITab> tabs;

	private IEpLayoutComposite searchButtonsomposite;

	private SearchSelectionListener searchSelectionListener;

	/**
	 * The constructor.
	 */
	public SearchView() {
		super();
		tabs = new ArrayList<>();
	}

	@Override
	protected void createViewPartControl(final Composite parentComposite) {

		final IEpLayoutComposite parentEpComposite = CompositeFactory.createGridLayoutComposite(parentComposite, 1, false);

		tabFolder = parentEpComposite.addTabFolder(parentEpComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true));
		int tabIndex = 0;
		int inventoryTabIndex = tabIndex;
		InventorySearchTab inventorySearchTab = new InventorySearchTab(tabFolder, inventoryTabIndex);
		tabs.add(inventorySearchTab);

		tabs.add(new OrderReturnSearchTab(tabFolder, ++tabIndex));
		// create search and clear buttons
		searchSelectionListener = new SearchSelectionListener();
		searchButtonsomposite = createButtonsPane(parentEpComposite);
		setSelectedTab(inventoryTabIndex);
		tabFolder.getSwtTabFolder().addSelectionListener(this);
		initTabs();
		setPartName(
			NLS.bind(WarehouseMessages.get().Warehouse_Title,
			WarehousePerspectiveFactory.getCurrentWarehouse().getName()));
	}
	
	/**
	 * Refreshes all registered tabs.
	 */
	public void refresh() {
		for (ITab tab : tabs) {
			tab.refresh();
		}
	}

	private void initTabs() {
		for (ITab tab1 : tabs) {
			tab1.refresh();
			if (!(tab1 instanceof ISearchTab)) {
				continue;
			}
			final ISearchTab tab = (ISearchTab) tab1;
			tab.bindControls(getBindingProvider(), getDataBindingContext());
			tab.setControlModificationListener(this);
			tab.setSelectionListener(searchSelectionListener);
			final AggregateValidationStatus aggregateStatus = new AggregateValidationStatus(getDataBindingContext().getBindings(),
					AggregateValidationStatus.MAX_SEVERITY);
			aggregateStatus.addValueChangeListener(event -> {
				final IStatus currentStatus = event.diff.getNewValue();
				final ITab selectedTab = tabs.get(tabFolder.getSelectedTabIndex());

				if (selectedTab instanceof ISearchTab) {
					final boolean isValid = currentStatus.isOK() && ((ISearchTab) selectedTab).validateSearchTermEntered();
					searchButton.setEnabled(isValid);
				}
			});
		}
	}

	private IEpLayoutComposite createButtonsPane(final IEpLayoutComposite parentComposite) {

		final IEpLayoutData wrapCompositeData = parentComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false);
		final IEpLayoutComposite wrapComposite = parentComposite.addTableWrapLayoutComposite(1, false, wrapCompositeData);
		wrapComposite.getSwtComposite().setVisible(false);

		// alter the wrapper composite by removing the margins and vertical
		// spacing between the two components
		final TableWrapLayout tableWrapLayout = (TableWrapLayout) wrapComposite.getSwtComposite().getLayout();
		tableWrapLayout.verticalSpacing = 0;
		tableWrapLayout.bottomMargin = 0;
		tableWrapLayout.leftMargin = 0;
		tableWrapLayout.rightMargin = 0;

		wrapComposite.addHorizontalSeparator(wrapComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.BEGINNING, true, false));

		final IEpLayoutData buttonsCompositeData = parentComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL, true, false);
		// wrapper composite for the buttons in order to make them right aligned
		final IEpLayoutComposite buttonsWrapComposite = wrapComposite.addGridLayoutComposite(1, false, buttonsCompositeData);
		// buttons composite holding the buttons and setting them to the right
		final IEpLayoutComposite buttonsComposite = buttonsWrapComposite.addGridLayoutComposite(2, true, buttonsCompositeData);
		this.searchButton = buttonsComposite.addPushButton(WarehouseMessages.get().SearchView_SearchButton, CoreImageRegistry
				.getImage(CoreImageRegistry.IMAGE_SEARCH_ACTIVE), EpState.READ_ONLY, null);
		this.clearButton = buttonsComposite.addPushButton(WarehouseMessages.get().SearchView_ClearButton, EpState.EDITABLE, null);

		// add selection listener for search, clear buttons
		this.searchButton.addSelectionListener(searchSelectionListener);
		this.clearButton.addSelectionListener(searchSelectionListener);
		return wrapComposite;
	}

	private void setSelectedTab(final int tabIndex) {
		tabFolder.setSelection(tabIndex);
		final ITab selectedTab = tabs.get(tabFolder.getSelectedTabIndex());

		if (selectedTab instanceof ISearchTab) {
			searchButtonsomposite.getSwtComposite().setVisible(true);
		} else {
			searchButtonsomposite.getSwtComposite().setVisible(false);
		}
	}

	@Override
	public void setFocus() {
		tabs.get(tabFolder.getSelectedTabIndex()).setFocus();
	}

	@Override
	protected Object getModel() {
		return null;
	}

	@Override
	public void widgetDefaultSelected(final SelectionEvent event) {
		// default implementation
	}

	@Override
	public void widgetSelected(final SelectionEvent event) {
		final ITab selectedTab = tabs.get(tabFolder.getSelectedTabIndex());

		if (event.getSource() == tabFolder.getSwtTabFolder()) {
			if (selectedTab instanceof ISearchTab) {
				searchButtonsomposite.getSwtComposite().setVisible(true);				
				selectedTab.setFocus();
				updateSearchButtonsPane();
			} else {
				searchButtonsomposite.getSwtComposite().setVisible(false);
			}
			selectedTab.tabActivated();
		}

	}

	private void doSearch() {
		final ITab selectedTab = tabs.get(tabFolder.getSelectedTabIndex());
		if (!(selectedTab instanceof ISearchTab)) {
			return;
		}

		final ISearchTab searchTab = (ISearchTab) selectedTab;

		if (searchTab.validateSearchTermEntered() && searchButton.isEnabled()) {
			final IWorkbenchPage workbenchPage = getSite().getPage();
			try {
				workbenchPage.showView(searchTab.getResultViewId(), null, IWorkbenchPage.VIEW_ACTIVATE);
			} catch (PartInitException exception) {
				throw new EpUiException("Unable to show search results view", exception); //$NON-NLS-1$
			}
			searchTab.search();
		}
	}

	/**
	 * 
	 */
	@Override
	public void controlModified() {
		updateSearchButtonsPane();
	}

	private void updateSearchButtonsPane() {
		final ITab selectedTab = tabs.get(tabFolder.getSelectedTabIndex());
		if (!(selectedTab instanceof ISearchTab)) {
			return;
		}

		final ISearchTab searchTab = (ISearchTab) selectedTab;

		if (searchTab.validateSearchTermEntered()) {
			EpControlFactory.changeEpState(searchButton, EpState.EDITABLE);
		} else {
			EpControlFactory.changeEpState(searchButton, EpState.READ_ONLY);
		}
	}

	/**
	 * Selection listener for search.
	 */
	private class SearchSelectionListener implements SelectionListener {
		/**
		 * Handle user pressing enter.
		 */
		@Override
		public void widgetDefaultSelected(final SelectionEvent event) {
			if (event.getSource() instanceof Text) {
				doSearch();
			}
		}

		/**
		 * Handle user clicking on Search or Clear button.
		 */
		@Override
		public void widgetSelected(final SelectionEvent event) {
			final ISearchTab selectedTab = (ISearchTab) tabs.get(tabFolder.getSelectedTabIndex());
			if (event.getSource() == searchButton) {
				doSearch();
			} else if (event.getSource() == clearButton) {
				selectedTab.clear();
				selectedTab.setFocus();
			}
		}

	}

	@Override
	public void setPartName(final String partName) {
		LOG.debug(partName);
		super.setPartName(partName);
	}

	@Override
	protected String getPartId() {
		return ID_SEARCH_VIEW;
	}
}