/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.fulfillment.editors.customer;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.editors.AbstractCmClientEditorPageSectionPart;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.cmclient.core.event.SearchResultEvent;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.ControlModificationListener;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;
import com.elasticpath.cmclient.fulfillment.FulfillmentPermissions;
import com.elasticpath.cmclient.fulfillment.event.CustomerEventListener;
import com.elasticpath.cmclient.fulfillment.event.FulfillmentEventService;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerAddress;

/**
 * Creates the UI of the customer addresses section.
 */
public class CustomerDetailsAddressDefaultSection extends AbstractCmClientEditorPageSectionPart implements CustomerEventListener, DisposeListener {

	private final transient Customer customer;

	private final transient ControlModificationListener listener;

	private transient CCombo shippingCombo;

	private transient CCombo billingCombo;

	private transient IEpLayoutComposite mainPane;

	private transient EpState authorization;

	/**
	 * Constructor to create a new Section in an editor's FormPage.
	 * 
	 * @param formPage the formpage
	 * @param editor the CmClientFormEditor that contains the form
	 */
	public CustomerDetailsAddressDefaultSection(final FormPage formPage, final AbstractCmClientFormEditor editor) {
		super(formPage, editor, ExpandableComposite.TITLE_BAR | ExpandableComposite.EXPANDED);
		this.customer = (Customer) editor.getModel();
		this.listener = editor;
	}

	@Override
	protected void createControls(final Composite client, final FormToolkit toolkit) {
		AuthorizationService authorizationService = AuthorizationService.getInstance();

		final boolean isAuthorized =
				authorizationService.isAuthorizedWithPermission(FulfillmentPermissions.CUSTOMER_EDIT)
						&& authorizationService.isAuthorizedForStore(customer.getStoreCode());

		if (isAuthorized) {
			authorization = EpState.EDITABLE;
		} else {
			authorization = EpState.READ_ONLY;
		}

		this.mainPane = CompositeFactory.createTableWrapLayoutComposite(client, 1, true);
		final IEpLayoutData defaultData = mainPane.createLayoutData();
		mainPane.addLabelBold(FulfillmentMessages.get().AddressDefaultSection_DefaultShippingAddress, defaultData);
		shippingCombo = mainPane.addComboBox(authorization, defaultData);

		mainPane.addLabelBold(FulfillmentMessages.get().AddressDefaultSection_DefaultBillingAddress, defaultData);
		billingCombo = mainPane.addComboBox(authorization, defaultData);
		FulfillmentEventService.getInstance().registerCustomerEventListener(this);
		
		getSection().addDisposeListener(this);
	}

	@Override
	protected String getSectionDescription() {
		return FulfillmentMessages.get().AddressDefaultSection_Description;
	}

	@Override
	protected String getSectionTitle() {
		return FulfillmentMessages.get().AddressDefaultSection_Title;
	}

	@Override
	protected void bindControls(final DataBindingContext bindingContext) {
		final ObservableUpdateValueStrategy shippingUpdateStrategy = new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
				int index = shippingCombo.getSelectionIndex();
				if (index > -1) {
					Address selectedAddress = customer.getAddresses().get(index);
					customer.setPreferredShippingAddress((CustomerAddress) selectedAddress);
				}
				return Status.OK_STATUS;
			}
		};
		EpControlBindingProvider.getInstance().bind(bindingContext, this.shippingCombo, null, null, shippingUpdateStrategy, false);

		final ObservableUpdateValueStrategy billingUpdateStrategy = new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
				int index = billingCombo.getSelectionIndex();
				if (index > -1) {
					Address selectedAddress = customer.getAddresses().get(index);
					customer.setPreferredBillingAddress((CustomerAddress) selectedAddress);
				}
				return Status.OK_STATUS;
			}
		};
		EpControlBindingProvider.getInstance().bind(bindingContext, this.billingCombo, null, null, billingUpdateStrategy, false);

	}

	@Override
	protected void populateControls() {
		this.shippingCombo.removeAll();
		this.billingCombo.removeAll();
		final List< ? > addressList = this.customer.getAddresses();
		final Address preferShipping = this.customer.getPreferredShippingAddress();
		final Address preferBilling = this.customer.getPreferredBillingAddress();
		Address address = null;
		final int addressListSize = addressList.size();
		for (int i = 0; i < addressListSize; i++) {
			address = (Address) addressList.get(i);
			this.shippingCombo.add(formatAddressLabel(address));
			if (preferShipping != null && StringUtils.equals(preferShipping.getGuid(), address.getGuid())) {
				this.shippingCombo.select(this.shippingCombo.getItemCount() - 1);
			}
		}

		for (int i = 0; i < addressListSize; i++) {
			address = (Address) addressList.get(i);
			this.billingCombo.add(formatAddressLabel(address));
			if (preferBilling != null && StringUtils.equals(preferBilling.getGuid(), address.getGuid())) {
				this.billingCombo.select(this.billingCombo.getItemCount() - 1);
			}
		}

		this.mainPane.setControlModificationListener(this.listener);
	}

	private String formatAddressLabel(final Address address) {
		final StringBuilder addressLine = new StringBuilder();
		addressLine.append(address.getFirstName());
		addressLine.append(' ');
		addressLine.append(address.getLastName());
		addressLine.append(", "); //$NON-NLS-1$
		addressLine.append(address.getStreet1());
		if (address.getStreet2() != null && !address.getStreet2().equals("")) { //$NON-NLS-1$
			addressLine.append(", "); //$NON-NLS-1$
			addressLine.append(address.getStreet2());
		}
		addressLine.append(", "); //$NON-NLS-1$
		addressLine.append(address.getCity());
		addressLine.append(", "); //$NON-NLS-1$
		addressLine.append(address.getZipOrPostalCode());
		if (address.getSubCountry() != null && !address.getSubCountry().equals("")) { //$NON-NLS-1$
			addressLine.append(", "); //$NON-NLS-1$
			addressLine.append(address.getSubCountry());
		}

		return addressLine.toString();
	}

	@Override
	public void searchResultsUpdate(final SearchResultEvent<Customer> event) {
		// do nothing
	}

	/**
	 * Notifies for a changed customer.
	 * If the customer changed is the one the page is acting on, repopulate addresses.
	 * 
	 * @param event customer change event
	 */
	@Override
	public void customerChanged(final ItemChangeEvent<Customer> event) {
		if (event.getItem().getGuid().equals(((Customer) getModel()).getGuid())) {
			populateControls();
		}
	}

	@Override
	public void dispose() {
		FulfillmentEventService.getInstance().unregisterCustomerEventListener(this);
	}

	@Override
	public void widgetDisposed(final DisposeEvent event) {
		dispose();
	}

}
