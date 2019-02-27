/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.fulfillment.editors.order.dialog;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.EpDialogSupport;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.ui.dialog.AbstractEpDialog;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.EpCountrySelectorControl;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;
import com.elasticpath.cmclient.fulfillment.FulfillmentPlugin;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderAddress;
import com.elasticpath.domain.order.PhysicalOrderShipment;

/**
 * Dialog for adding new address to order addresses.
 */

public class OrderAddEditAddressDialog extends AbstractEpDialog {
	private static final String DEFAULT_COUNTRY = "US"; //$NON-NLS-1$

	private final OrderAddress address;

	//Used for binding. Will copy to address if okPressed(). Otherwise changes are discarded. 
	private final OrderAddress addressProxy;
	
	private final Order order;

	/** instance of <code>EpCountrySelectorControl</code> which manages logic of State and Country combo. */
	private final EpCountrySelectorControl stateCountryManager;

	private Text firstNameText;

	private Text lastNameText;

	private Text address1Text;

	private Text address2Text;

	private Text cityText;

	private CCombo stateCombo;

	private Text zipText;

	private CCombo countryCombo;

	private Text phoneText;

	private Text faxText;

	private Button commecialBox;

	private final DataBindingContext bindingContext;

	private final boolean addAddress;

	private final boolean viewOnly;

	private final EpState authorization;

	
	private static void copyFields(final OrderAddress fromAddress, final OrderAddress toAddress) {
		toAddress.init(fromAddress);
	}

	/**
	 * Constructs the dialog.
	 * 
	 * @param parentShell the parent Shell
	 * @param order the order where order address belongs to
	 * @param shipment the physical order shipment
	 * @param isAdd if the dialog is for adding
	 * @param viewOnly if the dialog is open in view mode
	 */
	public OrderAddEditAddressDialog(final Shell parentShell, final Order order, final PhysicalOrderShipment shipment, final boolean isAdd,
		final boolean viewOnly) {
		super(parentShell, 2, false);

		this.order = order;

		OrderAddress newAddress;
		if (shipment == null) {
			newAddress = order.getBillingAddress();
		} else {
			newAddress = shipment.getShipmentAddress();
		}
		
		addressProxy = ServiceLocator.getService(ContextIdNames.ORDER_ADDRESS);
	
		this.viewOnly = viewOnly;
		if (isAdd) {
			this.address = ServiceLocator.getService(ContextIdNames.ORDER_ADDRESS);
			this.address.setCountry(DEFAULT_COUNTRY);
			addAddress = true;
		} else {
			this.address = newAddress;
			addAddress = false;
			if (this.address != null) {
				copyFields(address, addressProxy);
			}
		}
		this.bindingContext = new DataBindingContext();

		if (viewOnly) {
			authorization = EpState.READ_ONLY;
		} else {
			authorization = EpState.EDITABLE;
		}
		stateCountryManager = new EpCountrySelectorControl();

	}
	
	@Override
	protected void createButtonsForButtonBar(final Composite parent) {
		if (this.viewOnly) {
			createEpButtonsForButtonsBar(ButtonsBarType.OK, parent);
		} else {
			createEpButtonsForButtonsBar(ButtonsBarType.SAVE, parent);
		}
	}

	@Override
	protected String getPluginId() {
		return FulfillmentPlugin.PLUGIN_ID;
	}

	@Override
	public Object getModel() {
		return address;
	}

	@Override
	protected void populateControls() {

		stateCountryManager.populateStateCountryCombo();
		stateCountryManager.selectCountryCombo(address.getCountry());

		if (addAddress) {
			// set defaults for non required fields so they don't end up null
			address2Text.setText(""); //$NON-NLS-1$
			faxText.setText(""); //$NON-NLS-1$
			return;
		}

		firstNameText.setText(address.getFirstName());
		lastNameText.setText(address.getLastName());
		address1Text.setText(address.getStreet1());
		stateCountryManager.selectStateCombo(address.getSubCountry());

		address2Text.setText(StringUtils.defaultString(address.getStreet2()));
		zipText.setText(address.getZipOrPostalCode());
		cityText.setText(address.getCity());

		phoneText.setText(StringUtils.defaultString(address.getPhoneNumber()));
		faxText.setText(StringUtils.defaultString(address.getFaxNumber()));
		commecialBox.setSelection(address.isCommercialAddress());
	}

	@Override
	protected void createEpDialogContent(final IEpLayoutComposite dialogComposite) {

		final IEpLayoutData labelData = dialogComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL);
		final IEpLayoutData fieldData = dialogComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.BEGINNING, true, false);

		dialogComposite.addLabelBoldRequired(FulfillmentMessages.get().AddressDialog_FirstName, authorization, labelData);
		firstNameText = dialogComposite.addTextField(authorization, fieldData);

		dialogComposite.addLabelBoldRequired(FulfillmentMessages.get().AddressDialog_LastName, authorization, labelData);
		lastNameText = dialogComposite.addTextField(authorization, fieldData);

		dialogComposite.addLabelBoldRequired(FulfillmentMessages.get().AddressDialog_AddressLine1, authorization, labelData);
		address1Text = dialogComposite.addTextField(authorization, fieldData);

		dialogComposite.addLabelBold(FulfillmentMessages.get().AddressDialog_AddressLine2, labelData);
		address2Text = dialogComposite.addTextField(authorization, fieldData);

		dialogComposite.addLabelBoldRequired(FulfillmentMessages.get().AddressDialog_City, authorization, labelData);
		cityText = dialogComposite.addTextField(authorization, fieldData);

		dialogComposite.addLabelBold(FulfillmentMessages.get().AddressDialog_State, labelData);
		stateCombo = dialogComposite.addComboBox(authorization, fieldData);
		stateCountryManager.setStateCombo(stateCombo);

		dialogComposite.addLabelBoldRequired(FulfillmentMessages.get().AddressDialog_Zip, authorization, labelData);
		zipText = dialogComposite.addTextField(authorization, fieldData);

		dialogComposite.addLabelBoldRequired(FulfillmentMessages.get().AddressDialog_Country, authorization, labelData);
		countryCombo = dialogComposite.addComboBox(authorization, fieldData);
		stateCountryManager.setCountryCombo(countryCombo);

		dialogComposite.addLabelBoldRequired(FulfillmentMessages.get().AddressDialog_Phone, authorization, labelData);
		phoneText = dialogComposite.addTextField(authorization, fieldData);

		dialogComposite.addLabelBold(FulfillmentMessages.get().AddressDialog_Fax, labelData);
		faxText = dialogComposite.addTextField(authorization, fieldData);

		dialogComposite.addLabelBold(FulfillmentMessages.get().AddressDialog_Commercial, labelData);
		commecialBox = dialogComposite.addCheckBoxButton("", authorization, fieldData); //$NON-NLS-1$

		stateCountryManager.initStateCountryCombo(authorization);
	}

	@Override
	protected void bindControls() {
		if (viewOnly) {
			return;
		}
		// Since validation is performed at time of control binding by default, ensure that
		// Control Decorations indicating failed validation are not shown on bind.
		// We want to validate on load when adding an address
		final boolean hideDecorationOnFirstValidation = true;

		// FirstName
		EpControlBindingProvider.getInstance().bind(this.bindingContext, this.firstNameText, this.addressProxy, "firstName", //$NON-NLS-1$
				EpValidatorFactory.STRING_255_REQUIRED, null, hideDecorationOnFirstValidation);

		// LastName
		EpControlBindingProvider.getInstance().bind(this.bindingContext, this.lastNameText, this.addressProxy, "lastName", //$NON-NLS-1$
				EpValidatorFactory.STRING_255_REQUIRED, null, hideDecorationOnFirstValidation);

		// Street Line 1
		EpControlBindingProvider.getInstance().bind(this.bindingContext, this.address1Text, this.addressProxy, "street1", //$NON-NLS-1$
				EpValidatorFactory.STRING_255_REQUIRED, null, hideDecorationOnFirstValidation);

		// Street Line 2
		EpControlBindingProvider.getInstance().bind(this.bindingContext, this.address2Text, this.addressProxy, "street2", //$NON-NLS-1$
				null, null, hideDecorationOnFirstValidation);

		// City
		EpControlBindingProvider.getInstance().bind(this.bindingContext, this.cityText, this.addressProxy, "city", //$NON-NLS-1$
				EpValidatorFactory.STRING_255_REQUIRED, null, hideDecorationOnFirstValidation);

		final ObservableUpdateValueStrategy stateUpdateStrategy = new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
				String selectedState = stateCountryManager.getStateComboItem();
				addressProxy.setSubCountry(selectedState);
				return Status.OK_STATUS;
			}
		};

		// State
		EpControlBindingProvider.getInstance().bind(this.bindingContext, this.stateCombo, null, null, stateUpdateStrategy,
				hideDecorationOnFirstValidation);

		final ObservableUpdateValueStrategy countryUpdateStrategy = new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
				String selectedCountry = stateCountryManager.getCountryComboItem();
				addressProxy.setCountry(selectedCountry);
				return Status.OK_STATUS;
			}
		};

		// Country
		EpControlBindingProvider.getInstance().bind(this.bindingContext, this.countryCombo, null, null, countryUpdateStrategy,
				hideDecorationOnFirstValidation);

		// Zip
		EpControlBindingProvider.getInstance().bind(this.bindingContext, this.zipText, this.addressProxy, "zipOrPostalCode", //$NON-NLS-1$
				EpValidatorFactory.STRING_255_REQUIRED, null, hideDecorationOnFirstValidation);

		// Phone
		EpControlBindingProvider.getInstance().bind(this.bindingContext, this.phoneText, this.addressProxy, "phoneNumber", //$NON-NLS-1$
				EpValidatorFactory.PHONE_REQUIRED, null, hideDecorationOnFirstValidation);

		// Fax
		EpControlBindingProvider.getInstance().bind(this.bindingContext, this.faxText, this.addressProxy, "faxNumber", //$NON-NLS-1$
				EpValidatorFactory.PHONE_IGNORE_SPACES, null, hideDecorationOnFirstValidation);

		// Commercial
		EpControlBindingProvider.getInstance().bind(this.bindingContext, this.commecialBox, this.addressProxy, "commercialAddress", //$NON-NLS-1$
				null, null, hideDecorationOnFirstValidation);

		EpDialogSupport.create(this, this.bindingContext);
	}

	@Override
	protected void okPressed() {
		if (!viewOnly) {
			bindingContext.updateModels();
			if (addAddress) {
				order.setBillingAddress(this.address);
			}
			copyFields(addressProxy, address);
		}
		super.okPressed();
	}

	@Override
	protected String getTitle() {
		if (addAddress) {
			return FulfillmentMessages.get().AddressDialog_AddAddressTitle;
		} else if (viewOnly) {
			return FulfillmentMessages.get().AddressDialog_ViewAddressTitle;
		} else {
			return FulfillmentMessages.get().AddressDialog_EditAddressTitle;
		}
	}

	@Override
	protected String getWindowTitle() {
		if (addAddress) {
			return FulfillmentMessages.get().AddressDialog_AddAddressTitle;
		} else if (viewOnly) {
			return FulfillmentMessages.get().AddressDialog_ViewAddressTitle;
		} else {
			return FulfillmentMessages.get().AddressDialog_EditAddressTitle;
		}
	}

	@Override
	protected Image getWindowImage() {
		return null;
	}

	@Override
	protected String getInitialMessage() {
		return null;
	}

	/**
	 * Gets the address as string.
	 * 
	 * @return the address
	 */
	public String getAddressAsString() {
		return address.getFirstName() + " " + address.getLastName() + ", " //$NON-NLS-1$//$NON-NLS-2$
			+ address.getStreet1() + " " + address.getCity() + ", " //$NON-NLS-1$//$NON-NLS-2$
			+ address.getSubCountry() + ", " + address.getZipOrPostalCode() + " " //$NON-NLS-1$//$NON-NLS-2$
			+ address.getCountry();
	}
	
	/**
	 * Gets the shipping address.
	 *
	 * @return OrderAddress the shipping address
	 */
	public OrderAddress getShippingAddress() {
		return this.address;
	}
}
