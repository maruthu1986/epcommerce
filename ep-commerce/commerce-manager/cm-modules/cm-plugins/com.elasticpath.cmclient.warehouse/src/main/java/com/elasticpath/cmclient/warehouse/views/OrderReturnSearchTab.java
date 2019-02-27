/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.warehouse.views;

import com.elasticpath.cmclient.warehouse.WarehouseImageRegistry;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.CmClientResources;
import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.search.SafeSearchCodes;
import com.elasticpath.cmclient.core.search.impl.SafeSearchCodesImpl;
import com.elasticpath.cmclient.core.ui.framework.ControlModificationListener;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.ui.framework.IEpTabFolder;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.cmclient.warehouse.WarehouseMessages;
import com.elasticpath.cmclient.warehouse.helpers.OrderReturnSearchRequestJob;
import com.elasticpath.cmclient.warehouse.perspective.WarehousePerspectiveFactory;
import com.elasticpath.cmclient.warehouse.views.orderreturn.OrderReturnSearchResultsView;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.service.search.query.OrderReturnSearchCriteria;

/**
 * The order search tab in the search view.
 */
public class OrderReturnSearchTab implements ISearchTab {

	private static final String EMPTY_STRING = ""; //$NON-NLS-1$

	private Text orderNumberText;

	private Text firstNameText;

	private Text lastNameText;

	private Text rmaCodeText;

	private OrderReturnSearchCriteria searchCriteria;

	private final OrderReturnSearchRequestJob searchJob = new OrderReturnSearchRequestJob();

	private final EpState epState = EpState.EDITABLE;

	private IEpLayoutComposite searchTermsGroup;

	private Composite errorComposite;

	/**
	 * Construct the customer search tab.
	 * 
	 * @param tabFolder the tabFolder
	 * @param tabIndex the tab index
	 */
	public OrderReturnSearchTab(final IEpTabFolder tabFolder, final int tabIndex) {
		final IEpLayoutComposite compositeTab = tabFolder.addTabItem(WarehouseMessages.get().SearchView_RMATab,
				WarehouseImageRegistry.getImage(WarehouseImageRegistry.IMAGE_RETURN), tabIndex, 1, false);
		this.createSearchTermsGroup(compositeTab);
	}

	/*
	 * Creates the search terms group.
	 */
	private void createSearchTermsGroup(final IEpLayoutComposite parentComposite) {
		final IEpLayoutData data = parentComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false, 2, 1);
		searchTermsGroup = parentComposite.addGroup(WarehouseMessages.get().SearchView_SearchTermsGroup, 2, false, data);

		createErrorComposite();

		searchTermsGroup.addLabelBold(WarehouseMessages.get().SearchView_RMA, data);
		this.rmaCodeText = searchTermsGroup.addTextField(epState, data);

		searchTermsGroup.addLabelBold(WarehouseMessages.get().SearchView_OrderNumber, data);
		this.orderNumberText = searchTermsGroup.addTextField(epState, data);

		createSearchTermsGroupCustomer(data, searchTermsGroup);
	}

	private void createErrorComposite() {
		final IEpLayoutComposite epErrorComposite = searchTermsGroup.addTableWrapLayoutComposite(2, false, searchTermsGroup.createLayoutData(
				IEpLayoutData.CENTER, IEpLayoutData.FILL, true, false));
		errorComposite = epErrorComposite.getSwtComposite();
		// errorComposite.setVisible(false);

		epErrorComposite.addImage(CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_ERROR_SMALL), null);
		final Label errorMessageLabel = epErrorComposite.getFormToolkit().createLabel(epErrorComposite.getSwtComposite(),
			WarehouseMessages.get().SearchView_Error_NoSearchTerms, SWT.WRAP);
		errorMessageLabel.setForeground(CmClientResources.getColor(CmClientResources.COLOR_RED));
	}

	private void createSearchTermsGroupCustomer(final IEpLayoutData data, final IEpLayoutComposite searchTermsGroup) {
		searchTermsGroup.addLabelBold(WarehouseMessages.get().CustomerDetails_FirstNameLabel, data);
		this.firstNameText = searchTermsGroup.addTextField(epState, data);

		searchTermsGroup.addLabelBold(WarehouseMessages.get().CustomerDetails_LastNameLabel, data);
		this.lastNameText = searchTermsGroup.addTextField(epState, data);

	}

	@Override
	public void setFocus() {
		if (this.rmaCodeText != null) {
			this.rmaCodeText.setFocus();
		}
	}

	/**
	 * Gets the model object.
	 * 
	 * @return model object
	 */
	protected OrderReturnSearchCriteria getModel() {
		if (searchCriteria == null) {
			searchCriteria = ServiceLocator.getService(
					ContextIdNames.ORDER_RETURN_SEARCH_CRITERIA);
			searchCriteria.setCustomerSearchCriteria(ServiceLocator.getService(
					ContextIdNames.CUSTOMER_SEARCH_CRITERIA));
		}

		return searchCriteria;
	}

	@Override
	public void clear() {
		this.orderNumberText.setText(EMPTY_STRING);
		this.firstNameText.setText(EMPTY_STRING);
		this.lastNameText.setText(EMPTY_STRING);
		this.rmaCodeText.setText(EMPTY_STRING);
	}

	@Override
	public void search() {
		SafeSearchCodes warehouseCodes = new SafeSearchCodesImpl();
		warehouseCodes.extractAndAdd(WarehousePerspectiveFactory.getCurrentWarehouse(), "code");  //$NON-NLS-1$
		searchCriteria.setWarehouseCodes(warehouseCodes.asSet());
		
		if (validateSearchTermEntered()) {
			searchJob.setSearchCriteria(searchCriteria);
			searchJob.executeSearch(null);
		}
	}

	@Override
	public void tabActivated() {
		setFocus();
	}

	@Override
	public void bindControls(final EpControlBindingProvider bindingProvider, final DataBindingContext context) {
		bindingProvider.bind(context, this.rmaCodeText, this.getModel(), "rmaCode", EpValidatorFactory.MAX_LENGTH_255, null, true); //$NON-NLS-1$
		bindingProvider.bind(context, this.orderNumberText, this.getModel(), "orderNumber", EpValidatorFactory.LONG, null, false); //$NON-NLS-1$
		bindingProvider.bind(context, this.firstNameText, this.getModel().getCustomerSearchCriteria(),
				"firstName", EpValidatorFactory.MAX_LENGTH_255, null, true); //$NON-NLS-1$
		bindingProvider.bind(context, this.lastNameText, this.getModel().getCustomerSearchCriteria(),
				"lastName", EpValidatorFactory.MAX_LENGTH_255, null, true); //$NON-NLS-1$
	}

	@Override
	public String getResultViewId() {
		return OrderReturnSearchResultsView.VIEW_ID;
	}

	@Override
	public boolean validateSearchTermEntered() {
		final boolean isValid = hasSearchTerms();
		errorComposite.setVisible(!isValid);
		return isValid;
	}

	private boolean hasSearchTerms() {
		return hasSearchTermsFor(rmaCodeText) || hasSearchTermsFor(orderNumberText) || hasSearchTermsFor(firstNameText)
				|| hasSearchTermsFor(lastNameText);
	}

	private boolean hasSearchTermsFor(final Text text) {
		boolean res = false;
		if ((text.getText() != null) && (text.getText().length() > 0)) {
			res = true;
		}
		return res;
	}

	@Override
	public void setControlModificationListener(final ControlModificationListener listener) {
		searchTermsGroup.setControlModificationListener(listener);
	}

	@Override
	public void setSelectionListener(final SelectionListener listener) {
		this.orderNumberText.addSelectionListener(listener);
		this.rmaCodeText.addSelectionListener(listener);
		this.firstNameText.addSelectionListener(listener);
		this.lastNameText.addSelectionListener(listener);
	}
	
	@Override
	public void refresh() {
		//do nothing
	}
}
