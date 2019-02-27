/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.fulfillment.editors.customer;

import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.elasticpath.cmclient.core.LoginManager;
import com.elasticpath.cmclient.core.editors.AbstractCmClientEditorPageSectionPart;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.fulfillment.FulfillmentFeatureEnablementPropertyTester;
import com.elasticpath.cmclient.fulfillment.FulfillmentImageRegistry;
import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;
import com.elasticpath.cmclient.fulfillment.FulfillmentPermissions;
import com.elasticpath.cmclient.fulfillment.editors.actions.OpenEpBrowserContributionAction;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.store.Store;

/**
 * Creates a section for user to select store and launch a browser inside RCPCM to view the store.
 */
public class CustomerDetailsCreateOrderSection extends AbstractCmClientEditorPageSectionPart implements SelectionListener {

	private Store selectedStore;

	private Button createOrderButton;

	private CCombo storeComboBox;

	private final Customer customer;

	private static final String SECURITY_FILTER_URL = "/cmclient-signin.ep"; //$NON-NLS-1$

	private static final String CMUSERNAME = "?username="; //$NON-NLS-1$

	private static final String CMPASSWORD = "&password="; //$NON-NLS-1$

	private static final String CUSTUSERNAME = "&custusername="; //$NON-NLS-1$

	private EpState authorization;

	private Set<Store> storeList;

	private final CustomerDetailsOrdersModel orderModel;

	/**
	 * Constructor.
	 * 
	 * @param formPage the page
	 * @param editor the editor
	 */
	public CustomerDetailsCreateOrderSection(final FormPage formPage, final AbstractCmClientFormEditor editor) {
		super(formPage, editor, ExpandableComposite.TITLE_BAR);
		orderModel = new CustomerDetailsOrdersModel();
		customer = (Customer) editor.getModel();
	}

	@Override
	protected void bindControls(final DataBindingContext bindingContext) {
		// nothing
	}

	@Override
	protected void createControls(final Composite client, final FormToolkit toolkit) {
		if (allowCreateOrder()) {
			authorization = EpState.EDITABLE;
		} else {
			authorization = EpState.READ_ONLY;
		}

		final IEpLayoutComposite mainPane = CompositeFactory.createGridLayoutComposite(client, 3, false);

		final IEpLayoutData defaultData = mainPane.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.BEGINNING, false, false);

		final IEpLayoutData spanThreeCols = mainPane.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.BEGINNING, true, true, 3, 1);
		mainPane.addLabel(FulfillmentMessages.get().OrderCreate_Description, spanThreeCols);
		mainPane.addEmptyComponent(spanThreeCols);

		mainPane.addLabelBoldRequired(FulfillmentMessages.get().OrderCreate_StoreCombo, EpState.EDITABLE, null);
		this.storeComboBox = mainPane.addComboBox(authorization, defaultData);

		this.storeComboBox.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(final SelectionEvent event) {
				// nothing
			}

			@Override
			public void widgetSelected(final SelectionEvent event) {
				final String selectedStoreName = CustomerDetailsCreateOrderSection.this.storeComboBox.getText();
				getSelectedStoreByName(selectedStoreName);
			}
		});

		this.createOrderButton = mainPane.addPushButton(FulfillmentMessages.get().OrderCreate_CreateOrderButton,
				FulfillmentImageRegistry.getImage(FulfillmentImageRegistry.IMAGE_CREATE_ORDER), authorization, defaultData);
	}

	@Override
	protected void populateControls() {
		this.createOrderButton.addSelectionListener(this);

		storeList = new TreeSet<>();
		String[] storeListNames = orderModel.getAccessableStoreNames(customer, storeList);
		this.storeComboBox.setItems(storeListNames);
		this.storeComboBox.select(0);
		// sets the store to first item in the combo box
		final String firstStoreName = this.storeComboBox.getItem(0);
		getSelectedStoreByName(firstStoreName);
		changeCreateOrderButtonStatus(allowCreateOrder());
	}

	/**
	 * enable or disable the create order button.
	 * 
	 * @param buttonStatus the new status to enable or disable the button
	 */
	protected void changeCreateOrderButtonStatus(final boolean buttonStatus) {
		createOrderButton.setEnabled(buttonStatus);
	}

	/**
	 * Determine if the user is authorized to create the order.
	 * If the customer is anonymous (type='Guest'), order is not allowed to be generated.
	 * 
	 * @return true if creating order is allowed
	 */
	protected boolean allowCreateOrder() {
		AuthorizationService authorizationService = AuthorizationService.getInstance();
		return FulfillmentFeatureEnablementPropertyTester.ENABLE_CREATE_ORDER
			&& authorizationService.isAuthorizedWithPermission(FulfillmentPermissions.ORDER_EDIT)
			&& !customer.isAnonymous();
	}

	private void getSelectedStoreByName(final String selectedStoreName) {
		for (final Store store : storeList) {
			if (selectedStoreName.equals(store.getName())) {
				selectedStore = store;
			}
		}
	}

	@Override
	protected String getSectionDescription() {
		return FulfillmentMessages.get().OrderCreate_Description;
	}

	@Override
	protected String getSectionTitle() {
		return FulfillmentMessages.get().OrderCreate_Title;
	}

	/**
	 * Not used.
	 * 
	 * @param event the event object
	 */
	@Override
	public void widgetDefaultSelected(final SelectionEvent event) {
		// nothing
	}

	// ---- DOCwidgetSelected
	/**
	 * Invoked on selection event to open a browser.
	 * 
	 * @param event the event object
	 */
	@Override
	public void widgetSelected(final SelectionEvent event) {
		if (event.getSource() == this.createOrderButton) {

			final String queryURL =
					SECURITY_FILTER_URL + CMUSERNAME + LoginManager.getCmUserUsername() + CMPASSWORD
							+ LoginManager.getCmUser().getPassword() + CUSTUSERNAME + customer.getGuid();

			final OpenEpBrowserContributionAction openBrowserAction =
					new OpenEpBrowserContributionAction(this.selectedStore, IWorkbenchBrowserSupport.AS_EDITOR
							| IWorkbenchBrowserSupport.NAVIGATION_BAR, queryURL, this.customer, PlatformUI.getWorkbench().getActiveWorkbenchWindow()
							.getActivePage().getActiveEditor().getSite());
			openBrowserAction.run();
		}
	}
	// ---- DOCwidgetSelected
}
