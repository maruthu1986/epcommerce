/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.fulfillment.editors.customer;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;

import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.editors.AbstractCmClientEditorPageSectionPart;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.ControlModificationListener;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;
import com.elasticpath.cmclient.fulfillment.FulfillmentPermissions;
import com.elasticpath.cmclient.fulfillment.FulfillmentPlugin;
import com.elasticpath.domain.customer.Customer;

/**
 * UI representation of the customer details profile basic section.
 */
@SuppressWarnings({ "PMD.CyclomaticComplexity" })
public class CustomerDetailsProfileBasicSection extends AbstractCmClientEditorPageSectionPart {

	private static final String[] STATUS_STRINGS = new String[] { FulfillmentMessages.get().CustomerDetailsStatus_Active,
			FulfillmentMessages.get().CustomerDetailsStatus_Disabled };

	private static final String NA_STRING = "N/A"; //$NON-NLS-1$

	private Text userIdText;

	private CCombo statusCombo;

	private Text firstNameText;

	private Text lastNameText;

	private Text emailText;

	private Text phoneNumberText;

	private Text faxNumberText;

	private Text companyText;

	private final Customer customer;

	private IEpLayoutComposite mainPane;

	private final ControlModificationListener listener;

	private boolean authorized;

	/**
	 * Constructor.
	 * 
	 * @param editor the editor containing this Section Constructor to create a new Section in an editor's FormPage.
	 * @param formPage the formpage
	 */
	public CustomerDetailsProfileBasicSection(final FormPage formPage, final AbstractCmClientFormEditor editor) {
		super(formPage, editor, ExpandableComposite.TITLE_BAR | ExpandableComposite.EXPANDED);
		this.customer = (Customer) editor.getModel();
		this.listener = editor;
	}

	@Override
	protected void createControls(final Composite parentComposite, final FormToolkit toolkit) {
		final EpState authorization;

		final AuthorizationService authorizationService = AuthorizationService.getInstance();

		authorized = authorizationService.isAuthorizedWithPermission(FulfillmentPermissions.CUSTOMER_EDIT)
				&& authorizationService.isAuthorizedForStore(customer.getStoreCode());

		if (authorized) {
			authorization = EpState.EDITABLE;
		} else {
			authorization = EpState.READ_ONLY;
		}

		this.mainPane = CompositeFactory.createTableWrapLayoutComposite(parentComposite, 2, false);
		final TableWrapData data = new TableWrapData(TableWrapData.FILL, TableWrapData.FILL);
		data.grabHorizontal = true;
		this.mainPane.setLayoutData(data);

		final IEpLayoutData labelData = this.mainPane.createLayoutData(IEpLayoutData.END, IEpLayoutData.CENTER);
		final IEpLayoutData fieldData = this.mainPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.BEGINNING, true, false);

		this.mainPane.addLabelBold(FulfillmentMessages.get().ProfileBasicSection_UserId, labelData);
		this.userIdText = this.mainPane.addTextField(EpState.READ_ONLY, fieldData);

		this.mainPane.addLabelBold(FulfillmentMessages.get().ProfileBasicSection_Status, labelData);
		this.statusCombo = this.mainPane.addComboBox(authorization, fieldData);

		addLabel(mainPane, FulfillmentMessages.get().CustomerDetails_FirstNameLabel, authorization, labelData, customer.isFirstNameRequired());
		this.firstNameText = this.mainPane.addTextField(authorization, fieldData);

		addLabel(mainPane, FulfillmentMessages.get().CustomerDetails_LastNameLabel, authorization, labelData, customer.isLastNameRequired());
		this.lastNameText = this.mainPane.addTextField(authorization, fieldData);

		addLabel(mainPane, FulfillmentMessages.get().ProfileBasicSection_Email, authorization, labelData, customer.isEmailRequired());
		this.emailText = this.mainPane.addTextField(authorization, fieldData);

		addLabel(mainPane, FulfillmentMessages.get().ProfileBasicSection_PhoneNum, authorization, labelData, customer.isPhoneNumberRequired());
		this.phoneNumberText = this.mainPane.addTextField(authorization, fieldData);

		addLabel(mainPane, FulfillmentMessages.get().ProfileBasicSection_FaxNum, authorization, labelData, customer.isFaxNumberRequired());
		this.faxNumberText = this.mainPane.addTextField(authorization, fieldData);

		addLabel(mainPane, FulfillmentMessages.get().ProfileBasicSection_Company, authorization, labelData, customer.isCompanyRequired());
		this.companyText = this.mainPane.addTextField(authorization, fieldData);
	}

	/**
	 * Creates either required or optional label depending on attribute's required-ness flag.
	 */
	private void addLabel(final IEpLayoutComposite composite, final String labelText, final EpState epState, 
			final IEpLayoutData data, final boolean required) {
		if (required) {
			composite.addLabelBoldRequired(labelText, epState, data);
		} else {
			composite.addLabelBold(labelText, data);
		}
	}

	@Override
	protected void populateControls() {
		this.userIdText.setText(this.customer.getUserId());
		this.statusCombo.setItems(STATUS_STRINGS);

		this.statusCombo.setText(this.resolveStatusText(this.customer.getStatus()));
		this.firstNameText.setText(this.checkString(this.customer.getFirstName()));
		this.lastNameText.setText(this.checkString(this.customer.getLastName()));
		this.emailText.setText(this.checkString(this.customer.getEmail()));
		this.phoneNumberText.setText(this.checkString(this.customer.getPhoneNumber()));

		this.faxNumberText.setText(this.checkString(customer.getFaxNumber()));

		if (this.customer.getCompany() == null) {
			if (!authorized) {
				companyText.setText(NA_STRING);
			}
		} else {
			this.companyText.setText(this.checkString(this.customer.getCompany()));
		}

		// Set the modification listener *after* setting the control values
		// so that controls aren't considered to be modified when the initial value is set
		this.mainPane.setControlModificationListener(this.listener);
	}

	private String checkString(final String stringText) {
		if (stringText != null) {
			return stringText;
		}
		return ""; //$NON-NLS-1$
	}

	@Override
	protected void bindControls(final DataBindingContext bindingContext) { //NOPMD complexity
		final EpControlBindingProvider bindingProvider = EpControlBindingProvider.getInstance();

		// ---- DOCbindCustomerDetails
		bindingProvider.bind(bindingContext, this.statusCombo, null, null, new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object value) {
				int status;
				final int selectionIndex = (Integer) value;
				switch (selectionIndex) {
				case 0:
					status = Customer.STATUS_ACTIVE;
					break;
				case 1:
					status = Customer.STATUS_DISABLED;
					break;
				default:
					return new Status(IStatus.WARNING, FulfillmentPlugin.PLUGIN_ID, "Can not set the customer status."); //$NON-NLS-1$
				}
				customer.setStatus(status);
				return Status.OK_STATUS;
			}

		}, true);
		// ---- DOCbindCustomerDetails

		//No way currently to specify "requiredness" flag for arbitrary validator. So, need to provide if-else logic here. 
		if (customer.isFirstNameRequired()) {
			bindingProvider.bind(bindingContext, this.firstNameText, this.customer, "firstName", //$NON-NLS-1$ 
					EpValidatorFactory.STRING_255_REQUIRED, null, true);
		} else {
			bindingProvider.bind(bindingContext, this.firstNameText, this.customer, "firstName", //$NON-NLS-1$ 
					EpValidatorFactory.MAX_LENGTH_255, null, true);
		}
		if (customer.isLastNameRequired()) {
			bindingProvider.bind(bindingContext, this.lastNameText, this.customer, "lastName", //$NON-NLS-1$ 
					EpValidatorFactory.STRING_255_REQUIRED, null, true);
		} else {
			bindingProvider.bind(bindingContext, this.lastNameText, this.customer, "lastName", //$NON-NLS-1$ 
					EpValidatorFactory.MAX_LENGTH_255, null, true);
		}
		if (customer.isEmailRequired()) {
			bindingProvider.bind(bindingContext, this.emailText, this.customer, "email", //$NON-NLS-1$ 
					EpValidatorFactory.EMAIL_REQUIRED, null, true);
		} else {
			bindingProvider.bind(bindingContext, this.emailText, this.customer, "email", //$NON-NLS-1$ 
					EpValidatorFactory.EMAIL, null, true);
		}
		if (customer.isPhoneNumberRequired()) {
			bindingProvider.bind(bindingContext, this.phoneNumberText, this.customer, "phoneNumber", //$NON-NLS-1$ 
					EpValidatorFactory.PHONE_REQUIRED, null, true);
		} else {
			bindingProvider.bind(bindingContext, this.phoneNumberText, this.customer, "phoneNumber", //$NON-NLS-1$ 
					EpValidatorFactory.PHONE_IGNORE_SPACES, null, true);
		}
		if (customer.isFaxNumberRequired()) {
			bindingProvider.bind(bindingContext, this.faxNumberText, customer, "faxNumber", //$NON-NLS-1$ 
					EpValidatorFactory.FAX_REQUIRED, null, true);
		} else {
			bindingProvider.bind(bindingContext, this.faxNumberText, customer, "faxNumber", //$NON-NLS-1$ 
					EpValidatorFactory.FAX_IGNORE_SPACES, null, true);
		}
		if (customer.isCompanyRequired()) {
			bindingProvider.bind(bindingContext, this.companyText, this.customer, "company", //$NON-NLS-1$ 
					EpValidatorFactory.STRING_255_REQUIRED, null, true);
		} else {
			bindingProvider.bind(bindingContext, this.companyText, this.customer, "company", //$NON-NLS-1$ 
					EpValidatorFactory.MAX_LENGTH_255, null, true);
		}
	}

	private String resolveStatusText(final int status) {
		final String statusText;
		switch (status) {
		case Customer.STATUS_ACTIVE:
			statusText = FulfillmentMessages.get().CustomerDetailsStatus_Active;
			break;
		case Customer.STATUS_DISABLED:
			statusText = FulfillmentMessages.get().CustomerDetailsStatus_Disabled;
			break;
		default:
			statusText = FulfillmentMessages.get().CustomerDetailsStatus_Default;
			break;
		}
		return statusText;
	}

	@Override
	protected String getSectionTitle() {
		return FulfillmentMessages.get().ProfileBasicSection_Title;
	}

}
