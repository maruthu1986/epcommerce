/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.fulfillment.views;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.CmClientResources;
import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.search.SafeSearchCodes;
import com.elasticpath.cmclient.core.search.impl.SafeSearchCodesImpl;
import com.elasticpath.cmclient.core.ui.framework.ControlModificationListener;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.EpSortingCompositeControl;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.ui.framework.IEpTabFolder;
import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;
import com.elasticpath.cmclient.fulfillment.helpers.CustomerSearchRequestJob;
import com.elasticpath.cmclient.fulfillment.views.customer.CustomerSearchResultsView;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.search.query.CustomerSearchCriteria;
import com.elasticpath.service.search.query.StandardSortBy;

/**
 * The customer search tab in the search view.
 */
public class CustomerSearchTab implements ISearchTab {

	private static final String EMPTY_STRING = ""; //$NON-NLS-1$

	private Text emailUserIdText;

	private Text firstNameText;

	private Text lastNameText;

	private Text zipPostalCodeText;

	private Text phoneNumberText;

	private CCombo storeCombo;

	private final EpState epState = EpState.EDITABLE;

	private IEpLayoutComposite searchTermsGroup;

	private List<Store> stores;

	private final CustomerSearchCriteria searchCriteria = ServiceLocator.getService(ContextIdNames.CUSTOMER_SEARCH_CRITERIA);

	private final CustomerSearchRequestJob searchJob = new CustomerSearchRequestJob();

	private final Composite errorComposite;
	
	private DataBindingContext dataBindingContext;

	private EpSortingCompositeControl sortingControl;

	/**
	 * Construct the customer search tab.
	 * 
	 * @param tabFolder the tabFolder
	 * @param tabIndex the tab index
	 */
	public CustomerSearchTab(final IEpTabFolder tabFolder, final int tabIndex) {

		final IEpLayoutComposite customersTab = tabFolder.addTabItem(FulfillmentMessages.get().SearchView_CustomersTab,
				CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_USER), tabIndex, 1, false);

		// begin - error composite
		final IEpLayoutComposite epErrorComposite = customersTab.addTableWrapLayoutComposite(2, false,
				customersTab.createLayoutData(IEpLayoutData.BEGINNING,
						IEpLayoutData.FILL, true, false));
		errorComposite = epErrorComposite.getSwtComposite();
		errorComposite.setVisible(false);
		
		epErrorComposite.addImage(CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_ERROR_SMALL), null);

		// adding a wrapping label
		final Label errorMessageLabel = epErrorComposite.getFormToolkit().createLabel(epErrorComposite.getSwtComposite(),
				FulfillmentMessages.get().SearchView_Error_NoSearchTerms, SWT.WRAP);
		errorMessageLabel.setForeground(CmClientResources.getColor(CmClientResources.COLOR_RED));
		// end - error composite

		// Create search group
		this.createSearchTermsGroup(customersTab);

		// Create optional filters group
		this.createFiltersGroup(customersTab);
		
		// Create customer group
		this.createSortingGroup(customersTab);
		
		searchCriteria.setUserIdAndEmailMutualSearch(true);
	}

	/**
	 * Creates the filters group.
	 * 
	 * @param parentComposite the parent EP composite
	 */
	private void createFiltersGroup(final IEpLayoutComposite parentComposite) {
		final IEpLayoutComposite groupComposite = parentComposite.addGroup(FulfillmentMessages.get().SearchView_FiltersGroup, 1, false,
				null);

		final IEpLayoutData data = groupComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false);

		groupComposite.addLabelBold(FulfillmentMessages.get().SearchView_Filter_Stores, null);
		storeCombo = groupComposite.addComboBox(epState, data);
		storeCombo.setEnabled(true);
		fillStoresCombo();
	}

	/**
	 * Creates the customer search terms group.
	 */
	private void createSearchTermsGroup(final IEpLayoutComposite parentComposite) {
		final IEpLayoutData data = parentComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false);
		
		searchTermsGroup = parentComposite.addGroup(FulfillmentMessages.get().CustomerSearchTab_SearchTermsGroup, 1, false, data);


		searchTermsGroup.addLabelBold(FulfillmentMessages.get().SearchView_EmailUserId, null);

		this.emailUserIdText = searchTermsGroup.addTextField(epState, data);

		searchTermsGroup.addLabelBold(FulfillmentMessages.get().CustomerDetails_FirstNameLabel, null);

		this.firstNameText = searchTermsGroup.addTextField(epState, data);

		searchTermsGroup.addLabelBold(FulfillmentMessages.get().CustomerDetails_LastNameLabel, data);

		this.lastNameText = searchTermsGroup.addTextField(epState, data);

		searchTermsGroup.addLabelBold(FulfillmentMessages.get().SearchView_PostalCode, null);

		this.zipPostalCodeText = searchTermsGroup.addTextField(epState, data);

		searchTermsGroup.addLabelBold(FulfillmentMessages.get().SearchView_PhoneNumber, null);

		this.phoneNumberText = searchTermsGroup.addTextField(epState, data);

	}

	private void createSortingGroup(final IEpLayoutComposite parentComposite) {
		this.sortingControl = new EpSortingCompositeControl(parentComposite, searchCriteria);
		this.sortingControl.addSortTypeItem(FulfillmentMessages.get().CustomerSearchResultsView_DefaultBillingAddress, StandardSortBy.ADDRESS);
		this.sortingControl.addSortTypeItem(FulfillmentMessages.get().CustomerSearchResultsView_UserId, StandardSortBy.USER_ID, true);
		this.sortingControl.addSortTypeItem(FulfillmentMessages.get().CustomerSearchResultsView_EmailUserId, StandardSortBy.EMAIL);
		this.sortingControl.addSortTypeItem(FulfillmentMessages.get().CustomerSearchResultsView_FirstName, StandardSortBy.FIRST_NAME);
		this.sortingControl.addSortTypeItem(FulfillmentMessages.get().CustomerSearchResultsView_LastName, StandardSortBy.LAST_NAME);
		this.sortingControl.addSortTypeItem(FulfillmentMessages.get().CustomerSearchResultsView_TelephoneNum, StandardSortBy.PHONE);
	}
	
	/**
	 *
	 */
	private void clearFields() {
		this.sortingControl.clear();
		this.emailUserIdText.setText(EMPTY_STRING);
		this.firstNameText.setText(EMPTY_STRING);
		this.lastNameText.setText(EMPTY_STRING);
		this.zipPostalCodeText.setText(EMPTY_STRING);
		this.phoneNumberText.setText(EMPTY_STRING);
		this.storeCombo.clearSelection();
		this.storeCombo.select(0);		
	}

	/**
	 * Sets the focus to an internal UI control.
	 */
	@Override
	public void setFocus() {
		if (this.emailUserIdText != null) {
			this.emailUserIdText.setFocus();
		}
	}

	/**
	 * Gets the model object.
	 * 
	 * @return model object
	 */
	protected final CustomerSearchCriteria getModel() {
		return searchCriteria;
	}


	private String getTextValueFromTextWidget(final Text text) {
		return text.isDisposed() ? StringUtils.EMPTY : text.getText();
	}
	private boolean hasSearchTerms() {

		return hasSearchTermsFor(getTextValueFromTextWidget(emailUserIdText)) || hasSearchTermsForCustomer();
	}

	private boolean hasSearchTermsForCustomer() {
		return hasSearchTermsFor(getTextValueFromTextWidget(emailUserIdText))
				|| hasSearchTermsFor(getTextValueFromTextWidget(firstNameText)) || hasSearchTermsFor(getTextValueFromTextWidget(lastNameText))
				|| hasSearchTermsFor(getTextValueFromTextWidget(zipPostalCodeText))
				|| hasSearchTermsFor(getTextValueFromTextWidget(phoneNumberText));
	}

	private boolean hasSearchTermsFor(final String value) {
		return StringUtils.isNotBlank(value);
	}

	@Override
	public void clear() {
		this.clearFields();
		this.dataBindingContext.updateModels();
	}
	
	@Override
	public void search() {
		sortingControl.updateSearchCriteriaValues();
		if (hasSearchTermEntered()) {
			searchJob.setSearchCriteria(getModel());
			searchJob.executeSearch(null);
		}
	}

	/**
	 *
	 */
	@Override
	public void tabActivated() {
		// TODO Auto-generated method stub

	}

	private void setSearchStoreCodeList(final int select) {
		// The first item in this box will be a "select all" option if there is more than one
		// store.
		SafeSearchCodes storeCodes = new SafeSearchCodesImpl();
		
		if (select == 0) {
			storeCodes.extractAndAdd(stores, "code"); //$NON-NLS-1$     
		} else {
			//FIXME: select or select - 1
			storeCodes.extractAndAdd(stores.get(select), "code");  //$NON-NLS-1$
		}
		getModel().setStoreCodes(storeCodes.asSet());
	}

	@Override
	public void bindControls(final EpControlBindingProvider bindingProvider, final DataBindingContext context) {
		this.dataBindingContext = context;
		final ObservableUpdateValueStrategy storeUpdateStrategy = new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
				setSearchStoreCodeList((Integer) newValue);
				return Status.OK_STATUS;
			}
		};
		bindingProvider.bind(context, storeCombo, null, null, storeUpdateStrategy, false);
		setSearchStoreCodeList(0);
		storeCombo.select(0);
		
		bindingProvider.bind(context, this.emailUserIdText, this.getModel(),
				"userId", null, null, false); //$NON-NLS-1$
		bindingProvider.bind(context, this.emailUserIdText, this.getModel(),
				"email", SearchFieldsValidators.EMAIL_PATTERN_USERID_VALIDATOR, null, false); //$NON-NLS-1$
		bindingProvider.bind(context, this.firstNameText, this.getModel(),
				"firstName", null, null, true); //$NON-NLS-1$
		bindingProvider.bind(context, this.lastNameText, this.getModel(),
				"lastName", null, null, true); //$NON-NLS-1$
		bindingProvider.bind(context, this.zipPostalCodeText, this.getModel(),
				"zipOrPostalCode", null, null, true); //$NON-NLS-1$
		bindingProvider.bind(context, this.phoneNumberText, this.getModel(), 
				"phoneNumber", null, null, true); //$NON-NLS-1$

	}

	@Override
	public String getResultViewId() {
		return CustomerSearchResultsView.VIEW_ID;
	}

	@Override
	public boolean hasSearchTermEntered() {
		final boolean hasSearchTerms = hasSearchTerms();
		if (!errorComposite.isDisposed()) {
			errorComposite.setVisible(!hasSearchTerms);
		}
		return hasSearchTerms;
	}

	
	@Override
	public void setControlModificationListener(final ControlModificationListener listener) {
		searchTermsGroup.setControlModificationListener(listener);
	}
	
	private void fillStoresCombo() {
		stores = StoreFilterHelper.getAvailableStores();
		storeCombo.setItems(StoreFilterHelper.getAvailableStoreNames(stores));
		storeCombo.select(0);
		setSearchStoreCodeList(0);
	}
	
	/**
	 * Store combo box re-initialization.
	 */
	public void reInitializationStores() {
		String text = storeCombo.getText();
		this.storeCombo.removeAll();
		fillStoresCombo();
		int index = storeCombo.indexOf(text); 
		if (index >= 0) {
			storeCombo.select(index);
			setSearchStoreCodeList(index);
		} 	
	}

	/**
	 * @param listener selection listener
	 */
	@Override
	public void setSelectionListener(final SelectionListener listener) {
		this.emailUserIdText.addSelectionListener(listener);
		this.firstNameText.addSelectionListener(listener);
		this.lastNameText.addSelectionListener(listener);
		this.zipPostalCodeText.addSelectionListener(listener);
		this.phoneNumberText.addSelectionListener(listener);
	}


	@Override
	public boolean isWidgetDisposed() {
		return errorComposite.isDisposed();
	}

}
