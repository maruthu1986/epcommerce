/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.fulfillment.editors.customer;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Currency;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.util.DateTimeUtilFactory;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.editors.AbstractCmClientEditorPageSectionPart;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.ControlModificationListener;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpDateTimePicker;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;
import com.elasticpath.cmclient.fulfillment.FulfillmentPermissions;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.store.Store;
import com.elasticpath.persistence.api.FetchGroupLoadTuner;
import com.elasticpath.persistence.support.FetchGroupConstants;
import com.elasticpath.service.customer.CustomerRegistrationService;
import com.elasticpath.service.store.StoreService;

/**
 * UI representation of the customer details profile registration section.
 */
@SuppressWarnings({ "PMD.GodClass" })
public class CustomerDetailsProfileRegistrationSection extends AbstractCmClientEditorPageSectionPart {

	private static final String[] USER_TYPE_STRINGS = new String[] { FulfillmentMessages.get().ProfileRegistrationSection_TypeRegistered,
			FulfillmentMessages.get().ProfileRegistrationSection_TypeGuest };

	private static final String[] GENDER_STRINGS = new String[] { FulfillmentMessages.get().ProfileRegistrationSection_Male,
			FulfillmentMessages.get().ProfileRegistrationSection_Female, FulfillmentMessages.get().ProfileRegistrationSection_Gender_Not_Available };

	private static final String[] YES_NO_STRINGS = new String[] { FulfillmentMessages.get().ProfileRegistrationSection_Yes,
			FulfillmentMessages.get().ProfileRegistrationSection_No };

	private final Customer customer;

	private final Store customerStore;

	private Text dateRegisteredText;

	private Text storeRegisteredText;

	private CCombo userTypeCombo;

	private CCombo preferredLocaleCombo;

	private CCombo preferredCurrencyCombo;

	private IEpDateTimePicker birthDateComponent;

	private CCombo genderCombo;

	private CCombo htmlEmailCombo;

	private CCombo newsLetterCombo;

	private Text businessNumberText;

	private Text taxExemptionIdText;

	private IEpLayoutComposite mainPane;

	private final ControlModificationListener listener;

	private Set<Locale> localesSet;

	private List<Locale> localesList;

	private Set<Currency> currencySet;

	private StoreService storeService;

	/**
	 * Constructor to create a new Section in an editor's FormPage.
	 * 
	 * @param formPage the formpage
	 * @param editor the CmClientFormEditor that contains the form
	 */
	public CustomerDetailsProfileRegistrationSection(final FormPage formPage, final AbstractCmClientFormEditor editor) {
		super(formPage, editor, ExpandableComposite.TITLE_BAR | ExpandableComposite.EXPANDED);
		this.customer = (Customer) editor.getModel();
		this.customerStore = getStoreService().findStoreWithCode(customer.getStoreCode());
		this.listener = editor;
	}

	@Override
	protected void createControls(final Composite parentComposite, final FormToolkit toolkit) {
		this.mainPane = CompositeFactory.createTableWrapLayoutComposite(parentComposite, 2, false);
		final TableWrapData data = new TableWrapData(TableWrapData.FILL, TableWrapData.FILL);
		data.grabHorizontal = true;
		this.mainPane.setLayoutData(data);

		final EpState authorization;

		final AuthorizationService authorizationService = AuthorizationService.getInstance();

		final boolean authorized = authorizationService.isAuthorizedWithPermission(FulfillmentPermissions.CUSTOMER_EDIT)
				&& authorizationService.isAuthorizedForStore(customer.getStoreCode());

		if (authorized) {
			authorization = EpState.EDITABLE;
		} else {
			authorization = EpState.READ_ONLY;
		}

		final IEpLayoutData labelData = this.mainPane.createLayoutData(IEpLayoutData.END, IEpLayoutData.CENTER);
		final IEpLayoutData fieldData = this.mainPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.BEGINNING, true, false);

		this.mainPane.addLabelBold(FulfillmentMessages.get().ProfileRegistrationSection_RegDate, labelData);
		this.dateRegisteredText = this.mainPane.addTextField(EpState.READ_ONLY, fieldData);

		this.mainPane.addLabelBold(FulfillmentMessages.get().ProfileRegistrationSection_StoreReg, labelData);
		this.storeRegisteredText = this.mainPane.addTextField(EpState.READ_ONLY, fieldData);

		this.mainPane.addLabelBold(FulfillmentMessages.get().ProfileRegistrationSection_UserType, labelData);
		this.userTypeCombo = this.mainPane.addComboBox(authorization, fieldData);

		this.mainPane.addLabelBold(FulfillmentMessages.get().ProfileRegistrationSection_LocalePref, labelData);
		this.preferredLocaleCombo = this.mainPane.addComboBox(authorization, fieldData);

		this.mainPane.addLabelBold(FulfillmentMessages.get().ProfileRegistrationSection_CurrencyPref, labelData);
		this.preferredCurrencyCombo = this.mainPane.addComboBox(authorization, fieldData);

		this.mainPane.addLabelBold(FulfillmentMessages.get().ProfileRegistrationSection_BirthDate, labelData);
		this.birthDateComponent = this.mainPane.addDateTimeComponent(IEpDateTimePicker.STYLE_DATE, authorization, fieldData);

		this.mainPane.addLabelBold(FulfillmentMessages.get().ProfileRegistrationSection_Gender, labelData);
		this.genderCombo = this.mainPane.addComboBox(authorization, fieldData);

		this.mainPane.addLabelBold(FulfillmentMessages.get().ProfileRegistrationSection_RecHttpMail, labelData);
		this.htmlEmailCombo = this.mainPane.addComboBox(authorization, fieldData);

		this.mainPane.addLabelBold(FulfillmentMessages.get().ProfileRegistrationSection_RecNewsletter, labelData);
		this.newsLetterCombo = this.mainPane.addComboBox(authorization, fieldData);

		this.mainPane.addLabelBold(FulfillmentMessages.get().ProfileRegistrationSection_BusinessNumber, labelData);
		this.businessNumberText = this.mainPane.addTextField(authorization, fieldData);

		this.mainPane.addLabelBold(FulfillmentMessages.get().ProfileRegistrationSection_TaxExemptionId, labelData);
		this.taxExemptionIdText = this.mainPane.addTextField(authorization, fieldData);
	}

	@Override
	protected void populateControls() {
		this.dateRegisteredText.setText(String.valueOf(this.customer.getCreationDate()));
		this.storeRegisteredText.setText(customerStore.getName());

		this.userTypeCombo.setItems(USER_TYPE_STRINGS);

		if (this.customer.isRegistered()) {
			this.userTypeCombo.setText(USER_TYPE_STRINGS[0]);
			this.userTypeCombo.setEnabled(false);
		} else {
			this.userTypeCombo.setText(USER_TYPE_STRINGS[1]);
		}

		initPreferredLocaleCombo();

		if (this.customer.getPreferredLocale() != null) {
			this.preferredLocaleCombo.setText(this.customer.getPreferredLocale().getDisplayName());
		}

		initPreferredCurrencyCombo();

		if (this.customer.getPreferredCurrency() != null) {
			this.preferredCurrencyCombo.setText(this.customer.getPreferredCurrency().getCurrencyCode());
		}
		this.birthDateComponent.setDate(this.customer.getDateOfBirth());

		initGenderCombo();

		this.htmlEmailCombo.setItems(YES_NO_STRINGS);
		if (customer.isHtmlEmailPreferred()) {
			htmlEmailCombo.setText(YES_NO_STRINGS[0]);
		} else {
			htmlEmailCombo.setText(YES_NO_STRINGS[1]);
		}

		this.newsLetterCombo.setItems(YES_NO_STRINGS);
		if (this.customer.isToBeNotified()) {
			this.newsLetterCombo.setText(YES_NO_STRINGS[0]);
		} else {
			this.newsLetterCombo.setText(YES_NO_STRINGS[1]);
		}

		if (this.customer.getBusinessNumber() != null) {
			this.businessNumberText.setText(this.customer.getBusinessNumber());
		}

		if (this.customer.getTaxExemptionId() != null) {
			this.taxExemptionIdText.setText(this.customer.getTaxExemptionId());
		}

		// Set the modification listener *after* setting the control values
		// so that controls aren't considered to be modified when the initial value is set
		this.mainPane.setControlModificationListener(this.listener);
	}

	private void initPreferredLocaleCombo() {
		this.localesList = new ArrayList<>();
		this.localesSet = new HashSet<>();
		this.localesSet.addAll(customerStore.getSupportedLocales());
		final Collection<Store> sharedStores = getSharedLoginStores(customerStore);
		if (sharedStores != null) {
			for (Store store : sharedStores) {
				this.localesSet.addAll(store.getSupportedLocales());
			}
		}
		for (Locale locale : localesSet) {
			localesList.add(locale);
			preferredLocaleCombo.add(locale.getDisplayName());
		}
	}

	private void initPreferredCurrencyCombo() {
		this.currencySet = new HashSet<>();
		this.currencySet.addAll(customerStore.getSupportedCurrencies());
		final Collection<Store> sharedStores = getSharedLoginStores(customerStore);
		if (sharedStores != null) {
			for (Store store : sharedStores) {
				this.currencySet.addAll(store.getSupportedCurrencies());
			}
		}
		for (Currency curr : currencySet) {
			preferredCurrencyCombo.add(curr.getCurrencyCode());
		}
	}

	private Collection<Store> getSharedLoginStores(final Store registeredStore) {
		final StoreService storeService = ServiceLocator.getService(
				ContextIdNames.STORE_SERVICE);
		final FetchGroupLoadTuner loadTuner =
				ServiceLocator.getService(ContextIdNames.FETCH_GROUP_LOAD_TUNER);
		loadTuner.addFetchGroup(FetchGroupConstants.STORE_SHARING);
		return storeService.getTunedStores(registeredStore.getAssociatedStoreUids(), loadTuner);
	}

	private void initGenderCombo() {
		this.genderCombo.setItems(GENDER_STRINGS);
		final char genderChar = this.customer.getGender();
		if (Customer.GENDER_MALE == genderChar) {
			this.genderCombo.setText(GENDER_STRINGS[0]);
		} else if (Customer.GENDER_FEMALE == genderChar) {
			this.genderCombo.setText(GENDER_STRINGS[1]);
		} else {
			this.genderCombo.setText(GENDER_STRINGS[2]); // gender not specified
		}
	}

	private IStatus setSelectedLocale(final int selectedIndex) {
		if (selectedIndex >= 0) {
			final Locale locale = localesList.get(selectedIndex);
			this.customer.setPreferredLocale(locale);
		}
		return Status.OK_STATUS;
	}

	@Override
	protected void bindControls(final DataBindingContext bindingContext) {
		final EpControlBindingProvider bindingProvider = EpControlBindingProvider.getInstance();

		bindingProvider.bind(bindingContext, this.preferredLocaleCombo, null, null, new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object value) {
				return setSelectedLocale((Integer) value);
			}
		}, true);

		bindingProvider.bind(bindingContext, this.userTypeCombo, null, null, new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object value) {
				updateUserType(value);
				return Status.OK_STATUS;
			}
		}, true);

		bindingProvider.bind(bindingContext, this.preferredCurrencyCombo, null, null, new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object value) {
				final int intVal = (Integer) value;
				if (intVal > -1) {
					final String currencyCode = preferredCurrencyCombo.getItem(intVal);
					customer.setPreferredCurrency(Currency.getInstance(currencyCode));
				}
				return Status.OK_STATUS;
			}
		}, true);

		bindingProvider.bind(bindingContext, this.birthDateComponent.getSwtText(), EpValidatorFactory.DATE, null,
				new ObservableUpdateValueStrategy() {
					@Override
					protected IStatus doSet(final IObservableValue observableValue, final Object value) {
						return setDate(value);
					}
				}, true);

		bindingProvider.bind(bindingContext, this.genderCombo, null, null, new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object value) {
				final int gender = (Integer) value;
				setCustomerGender(gender);
				return Status.OK_STATUS;
			}
		}, true);

		bindingProvider.bind(bindingContext, this.htmlEmailCombo, null, null, new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object value) {
				final boolean toReceiveHtmlEmail = (Integer) value == 0;
				customer.setHtmlEmailPreferred(toReceiveHtmlEmail);
				return Status.OK_STATUS;
			}
		}, true);

		bindingProvider.bind(bindingContext, this.newsLetterCombo, null, null, new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object value) {
				final boolean toBeNotified = (Integer) value == 0;
				customer.setToBeNotified(toBeNotified);
				return Status.OK_STATUS;
			}
		}, true);

		bindingProvider.bind(bindingContext, this.businessNumberText, null, null, new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object value) {
				customer.setBusinessNumber(String.valueOf(value));
				return Status.OK_STATUS;
			}
		}, true);

		bindingProvider.bind(bindingContext, this.taxExemptionIdText, null, null, new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object value) {
				customer.setTaxExemptionId(String.valueOf(value));
				return Status.OK_STATUS;
			}
		}, true);
	}

	/**
	 * Sets the customer gender to the model object.
	 * 
	 * @param comboIndex the gender combo index
	 */
	private void setCustomerGender(final int comboIndex) {
		char genderChar;
		switch (comboIndex) {
		case 0:
			genderChar = Customer.GENDER_MALE;
			break;
		case 1:
			genderChar = Customer.GENDER_FEMALE;
			break;
		default:
			genderChar = Customer.GENDER_NOT_SELECTED;
			break;
		}
		customer.setGender(genderChar);
	}

	private IStatus setDate(final Object value) {
		if (StringUtils.isBlank((String) value)) {
			customer.setDateOfBirth(null);
			return Status.OK_STATUS;
		}

		try {
			final Date date = DateTimeUtilFactory.getDateUtil().parseDate((String) value);
			customer.setDateOfBirth(date);
		} catch (final ParseException e) {
			// return ok anyway
			return Status.OK_STATUS;
		}
		return Status.OK_STATUS;
	}

	@Override
	protected String getSectionTitle() {
		return FulfillmentMessages.get().ProfileRegistrationSection_Title;
	}

	@Override
	public void commit(final boolean onSave) {
		if (onSave && USER_TYPE_STRINGS[0].equals(userTypeCombo.getText())) {
			// if the editor is open and the user set the customer to registered
			// disable the combo as it should not be changed back to anonymous
			userTypeCombo.setEnabled(!customer.isRegistered());
			final CustomerRegistrationService customerRegistrationService = ServiceLocator.getService(
					ContextIdNames.CUSTOMER_REGISTRATION_SERVICE);
			customerRegistrationService.registerCustomerAndSendPassword(customer);

			MessageDialog.openInformation(getSection().getShell(), FulfillmentMessages.get().CustomerDetailsPage_CreatePassInfoTitle,
					FulfillmentMessages.get().CustomerDetailsPage_CreatePassInfoMessage);

		}
		super.commit(onSave);
	}

	private void updateUserType(final Object value) {
		if (userTypeCombo.isEnabled()) {
			final boolean isGuest = ((Integer) value == 1);
			customer.setAnonymous(isGuest);
			((CustomerDetailsEditor) getEditor()).fireUpdateActions();

			markDirty();
		}
	}

	/**
	 * Gets the store service.
	 *
	 * @return the store service
	 */
	protected StoreService getStoreService() { 
		if (storeService == null) {
			storeService = ServiceLocator.getService(ContextIdNames.STORE_SERVICE);
		}
		return storeService;
	}
}
