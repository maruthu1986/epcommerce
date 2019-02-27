/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.fulfillment.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.AggregateValidationStatus;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.ControlModificationListener;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.ui.framework.IEpTabFolder;
import com.elasticpath.cmclient.core.views.AbstractCmClientView;
import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;
import com.elasticpath.cmclient.fulfillment.perspective.FulfillmentPerspectiveFactory;

/**
 * This sample class demonstrates how to plug-in a new workbench view. The view shows data obtained from the model. The sample creates a dummy model
 * on the fly, but a real implementation would connect to the model available either in this or another plug-in (e.g. the workspace). The view is
 * connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be presented in the view. Each view can present the same model objects using
 * different labels and icons, if needed. Alternatively, a single label provider can be shared between views in order to ensure that objects of the
 * same type are presented in the same way everywhere.
 * <p>
 */
public class SearchView extends AbstractCmClientView implements SelectionListener, ControlModificationListener, IPerspectiveListener {

	private static final Logger LOG = Logger.getLogger(SearchView.class);

	private static final int TAB_CUSTOMERS = 1;

	private static final int TAB_ORDERS = 0;

	// private static final int TAB_RETURNS = 2;

	/**
	 * SearchView ID specified in the plugin.xml file. It is the same as the class name
	 */
	public static final String ID_SEARCH_VIEW = SearchView.class.getName();

	private IEpTabFolder tabFolder;

	private Button searchButton;

	private Button clearButton;

	private final List<ISearchTab> tabs;

	private OrderSearchTab orderSearchTab;
	
	private CustomerSearchTab customerSearchTab;

	/** associate separate databinding context with each tab. **/
	private final Map<ISearchTab, DataBindingContext> bindingContexts = new HashMap<>();
	
	/**
	 * The constructor.
	 */
	public SearchView() {
		super();
		tabs = new ArrayList<>();
	}
	
	@Override
	public void init(final IViewSite site) throws PartInitException {
		super.init(site);
		site.getWorkbenchWindow().addPerspectiveListener(this);
	}

	@Override
	protected void createViewPartControl(final Composite parentComposite) {

		final IEpLayoutComposite parentEpComposite = CompositeFactory.createGridLayoutComposite(parentComposite, 1, false);

		tabFolder = parentEpComposite.addTabFolder(parentEpComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true));

		orderSearchTab = new OrderSearchTab(tabFolder, TAB_ORDERS); 
		tabs.add(orderSearchTab);

		customerSearchTab = new CustomerSearchTab(tabFolder, TAB_CUSTOMERS); 
		tabs.add(customerSearchTab);

		tabFolder.setSelection(TAB_ORDERS);
		tabFolder.getSwtTabFolder().addSelectionListener(this);
		// create search and clear buttons
		createButtonsPane(parentEpComposite);
		initTabs();
	}

	/**
	 *
	 */
	private void initTabs() {
		for (int i = 0; i < tabs.size(); i++) {
			final ISearchTab tab = tabs.get(i);
			DataBindingContext context = new DataBindingContext();
			tab.bindControls(getBindingProvider(), context);
			tab.setControlModificationListener(this);
			tab.setSelectionListener(this);
			final AggregateValidationStatus aggregateStatus = new AggregateValidationStatus(context.getBindings(),
					AggregateValidationStatus.MAX_SEVERITY);			
			aggregateStatus.addValueChangeListener(event -> {
				final IStatus currentStatus = event.diff.getNewValue();
				final ISearchTab selectedTab = tabs.get(tabFolder.getSelectedTabIndex());

				final boolean isValid = currentStatus.isOK() && selectedTab.hasSearchTermEntered();
				searchButton.setEnabled(isValid);
			});
			bindingContexts.put(tab, context);
		}
	}

	private void createButtonsPane(final IEpLayoutComposite parentComposite) {

		final IEpLayoutData wrapCompositeData = parentComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false);
		final IEpLayoutComposite wrapComposite = parentComposite.addTableWrapLayoutComposite(1, false, wrapCompositeData);

		// alter the wrapper composite by removing the margins and vertical spacing between the two components
		final TableWrapLayout tableWrapLayout = (TableWrapLayout) wrapComposite.getSwtComposite().getLayout();
		tableWrapLayout.verticalSpacing = 0;
		tableWrapLayout.bottomMargin = 0;
		tableWrapLayout.leftMargin = 0;
		tableWrapLayout.rightMargin = 0;

		wrapComposite.addHorizontalSeparator(wrapComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.BEGINNING, true, false));

		final IEpLayoutData buttonsCompositeData = parentComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL, true, false);
		// wrapper composite for the buttons in order to make them be right aligned. Without that
		final IEpLayoutComposite buttonsWrapComposite = wrapComposite.addGridLayoutComposite(1, false, buttonsCompositeData);
		// buttons composite holding the buttons and setting them to the right
		final IEpLayoutComposite buttonsComposite = buttonsWrapComposite.addGridLayoutComposite(2, true, buttonsCompositeData);
		this.searchButton = buttonsComposite.addPushButton(FulfillmentMessages.get().SearchView_SearchButton, CoreImageRegistry
				.getImage(CoreImageRegistry.IMAGE_SEARCH_ACTIVE), EpState.EDITABLE, null);
		this.clearButton = buttonsComposite.addPushButton(FulfillmentMessages.get().SearchView_ClearButton, EpState.EDITABLE, null);

		// add selection listener for search, clear buttons
		this.searchButton.addSelectionListener(this);
		this.clearButton.addSelectionListener(this);
	}

	@Override
	public void setFocus() {
		tabs.get(tabFolder.getSelectedTabIndex()).setFocus();
		updateSearchButtonsPane();
	}

	@Override
	protected Object getModel() {
		return null;
	}

	@Override
	public void widgetDefaultSelected(final SelectionEvent event) {
		if (event.getSource() instanceof Text) {
			doSearch();
		}
	}

	@Override
	public void widgetSelected(final SelectionEvent event) {
		final ISearchTab selectedTab = tabs.get(tabFolder.getSelectedTabIndex());
		if (event.getSource() == searchButton) {
			doSearch();
		} else if (event.getSource() == clearButton) {
			selectedTab.clear();
			selectedTab.setFocus();
		} else if (event.getSource() == tabFolder.getSwtTabFolder()) {
			selectedTab.tabActivated();
			selectedTab.setFocus();
			updateSearchButtonsPane();
		}

	}

	private void doSearch() {
		final ISearchTab selectedTab = tabs.get(tabFolder.getSelectedTabIndex());
		if (selectedTab.hasSearchTermEntered() && searchButton.isEnabled()) {
			final IWorkbenchPage workbenchPage = getSite().getPage();
			try {
				workbenchPage.showView(selectedTab.getResultViewId(), null, IWorkbenchPage.VIEW_ACTIVATE);
			} catch (final PartInitException e) {
				// Nothing to do.
				LOG.error(e);
			}

			selectedTab.search();
		}
	}

	/**
	 *
	 */
	@Override
	public void controlModified() {
		updateSearchButtonsPane();
	}

	/**
	 *
	 */
	private void updateSearchButtonsPane() {
		final ISearchTab selectedTab = tabs.get(tabFolder.getSelectedTabIndex());
		if (selectedTab.hasSearchTermEntered() 
				&& AggregateValidationStatus.getStatusMerged(bindingContexts.get(selectedTab).getBindings()).isOK()) {
			EpControlFactory.changeEpState(searchButton, EpState.EDITABLE);
		} else {
			EpControlFactory.changeEpState(searchButton, EpState.READ_ONLY);
		}
	}
	
	@Override
	public void perspectiveActivated(final IWorkbenchPage page, final IPerspectiveDescriptor perspective) {
		if (perspective.getId().equals(FulfillmentPerspectiveFactory.PERSPECTIVE_ID)) {
			if (orderSearchTab != null) {
				orderSearchTab.reInitializationStores();
			}
			if (customerSearchTab != null) {
				customerSearchTab.reInitializationStores();
			}
		}
		
	}

	@Override
	public void perspectiveChanged(final IWorkbenchPage page, final IPerspectiveDescriptor perspective, final String changeId) {
		// do nothing
	}

	@Override
	protected String getPartId() {
		return ID_SEARCH_VIEW;
	}
}