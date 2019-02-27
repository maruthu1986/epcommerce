/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.store.promotions.wizard;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.cmclient.core.LoginManager;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.EpValueBinding;
import com.elasticpath.cmclient.core.binding.EpWizardPageSupport;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.conversion.EpIntToStringConverter;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.EpLocalizedPropertyController;
import com.elasticpath.cmclient.core.ui.framework.IEpDateTimePicker;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.validation.CompoundValidator;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.cmclient.core.wizard.AbstractEPWizardPage;
import com.elasticpath.cmclient.store.StorePlugin;
import com.elasticpath.cmclient.store.promotions.PromotionsMessages;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.rules.RuleCondition;
import com.elasticpath.domain.rules.RuleElementType;
import com.elasticpath.domain.rules.RuleParameter;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.catalog.CatalogService;
import com.elasticpath.service.pricing.PriceListAssignmentService;
import com.elasticpath.service.store.StoreService;

/**
 * The new promotions details wizard page.
 */
@SuppressWarnings("PMD.TooManyFields")
public class NewPromotionWizardDetailsPage extends AbstractEPWizardPage<Rule> {

	private static final int DESCRIPTION_TEXT_AREA_HEIGHT = 80;

	private CCombo targetCombo;

	private Text promotionNameText;

	private Text descriptionText;

	private Button visibleInStore;

	private IEpDateTimePicker activeFromDatePicker;

	private IEpDateTimePicker activeToDatePicker;

	private final boolean catalogPromotion;

	private final List<Store> stores;

	private final List<Catalog> catalogs;

	private Label allowedLimitLabel;

	private Spinner allowedLimitSpinner;
	
	private Button limitedUsagePromotion;

	private String limitedUsagePromotionID;
	
	private RuleCondition limitedUsagePromotionRuleCondition;
	
	private RuleParameter limitedUsagePromotionRuleParameter;

	private EpValueBinding limitedUsagePromotionTextBinding;
	
	private EpLocalizedPropertyController nameController;

	private CCombo languageCombo;
	
	private Text displayNameText;
	
	private static final int MAX_NAME_LENGTH = 255;

	private final List<Locale> supportedLocales = new ArrayList<>();
	
	private Locale defaultLocale;

	/**
	 * Default constructor.
	 * 
	 * @param pageName the name of the page
	 * @param title the page title
	 * @param catalogPromotion whether the page should be created for a catalog promotion
	 */
	protected NewPromotionWizardDetailsPage(final String pageName, final String title, final boolean catalogPromotion) {
		super(2, false, pageName, title, PromotionsMessages.get().CreatePromotionWizardDetailsPage_Description, new DataBindingContext());
		this.catalogPromotion = catalogPromotion;

		if (catalogPromotion) {
			final CatalogService catalogService = ServiceLocator.getService(ContextIdNames.CATALOG_SERVICE);
			final PriceListAssignmentService plaService = ServiceLocator.getService(
					ContextIdNames.PRICE_LIST_ASSIGNMENT_SERVICE);
			
			List<String> catalogCodes = plaService.listAssignedCatalogsCodes();
			catalogs = catalogService.listAllCatalogsWithCodes(catalogCodes);
			
			AuthorizationService.getInstance().filterAuthorizedCatalogs(catalogs);
			stores = null;
		} else {
			final StoreService storeService = ServiceLocator.getService(ContextIdNames.STORE_SERVICE);
			stores = storeService.findAllCompleteStores();
			AuthorizationService.getInstance().filterAuthorizedStores(stores);
			catalogs = null;
		}
	}

	@Override
	protected void createEpPageContent(final IEpLayoutComposite parent) {
		final IEpLayoutData labelData = parent.createLayoutData(IEpLayoutData.END, IEpLayoutData.BEGINNING);
		final IEpLayoutData fieldDataFill = parent.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.BEGINNING, true, false);
		final IEpLayoutData fieldDataSpinner = parent.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.BEGINNING, false, false);
		final IEpLayoutData fieldDataBeginning = parent.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.BEGINNING, true, false);

		if (catalogPromotion) {
			parent.addLabelBoldRequired(PromotionsMessages.get().Promotion_Catalog, EpState.EDITABLE, labelData);
		} else {
			parent.addLabelBoldRequired(PromotionsMessages.get().Promotion_Store, EpState.EDITABLE, labelData);
		}
		targetCombo = parent.addComboBox(EpState.EDITABLE, fieldDataBeginning);
		targetCombo.addSelectionListener(getTargetListener());

		parent.addLabelBoldRequired(PromotionsMessages.get().PromoDetailsOverview_Label_PromotionName, EpState.EDITABLE, labelData);
		promotionNameText = parent.addTextField(EpState.EDITABLE, fieldDataFill);

		parent.addLabelBold(PromotionsMessages.get().PromoDetailsOverview_Label_DisplayName, labelData);
		IEpLayoutComposite nameComposite = parent.addTableWrapLayoutComposite(2, false, fieldDataFill);
		languageCombo = nameComposite.addComboBox(EpState.EDITABLE, nameComposite.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.FILL,
				false, false));
		displayNameText = nameComposite.addTextField(EpState.EDITABLE, nameComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL,
				true, false));
		displayNameText.setTextLimit(MAX_NAME_LENGTH);
		
		nameController = EpLocalizedPropertyController.createEpLocalizedPropertyController(displayNameText, languageCombo,
				Rule.LOCALIZED_PROPERTY_DISPLAY_NAME, false, getDataBindingContext(), EpValidatorFactory.MAX_LENGTH_255);

		parent.addLabelBold(PromotionsMessages.get().PromoDetailsOverview_Label_Description, labelData);
		descriptionText = parent.addTextArea(EpState.EDITABLE, fieldDataFill);
		final GridData twdDescriptionText = new GridData();
		twdDescriptionText.heightHint = DESCRIPTION_TEXT_AREA_HEIGHT;
		twdDescriptionText.horizontalAlignment = SWT.FILL;
		descriptionText.setLayoutData(twdDescriptionText);

		if (catalogPromotion) {
			parent.addLabelBoldRequired(PromotionsMessages.get().PromoDetailsOverview_Label_ActiveFrom, EpState.EDITABLE, labelData);
			activeFromDatePicker = parent.addDateTimeComponent(IEpDateTimePicker.STYLE_DATE_AND_TIME, EpState.EDITABLE, fieldDataFill);

			parent.addLabelBold(PromotionsMessages.get().PromoDetailsOverview_Label_ActiveTo, labelData);
			activeToDatePicker = parent.addDateTimeComponent(IEpDateTimePicker.STYLE_DATE_AND_TIME, EpState.EDITABLE, fieldDataFill);

		} else {
			parent.addLabelBold(PromotionsMessages.get().PromoDetailsOverview_StoreVisible, labelData);
			visibleInStore = parent.addCheckBoxButton("", EpState.EDITABLE, fieldDataBeginning); //$NON-NLS-1$

			parent.addLabelBold(PromotionsMessages.get().PromoDetailsOverview_LimitedUsagePromotion, labelData);
			limitedUsagePromotion = parent.addCheckBoxButton("", EpState.EDITABLE, fieldDataBeginning); //$NON-NLS-1$

			allowedLimitLabel = parent.addLabelBold(PromotionsMessages.get().PromoDetailsOverview_AllowedLimit, labelData);
			
			allowedLimitLabel.setVisible(false);

			allowedLimitSpinner = parent.addSpinnerField(EpState.EDITABLE, fieldDataSpinner);
				
			allowedLimitSpinner.setEnabled(false);
			allowedLimitSpinner.setVisible(false);
			allowedLimitSpinner.setMinimum(1);
			allowedLimitSpinner.setMaximum(Integer.MAX_VALUE);
			
			limitedUsagePromotionID = getModel().getCode();
			
			SelectionListener listener = this.getLupListener();
			
			limitedUsagePromotion.addSelectionListener(listener);
		}

		/* MUST be called */
		setControl(parent.getSwtComposite());
	}

	private SelectionListener getTargetListener() {
		return new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent selectionEvent) {
				refreshLocales();
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent selectionEvent) {
				// Do nothing
			}
		};
	}

	/**
	 * Get the limited usage listener that will be attached to the limited usage promotion check box.
	 *
	 * @return the SelectionListener object
	 */
	private SelectionListener getLupListener() {
		
		/**
		 * Create a SelectionListener that will have overridden methods for actions
		 * to perform when selected or unselected.
		 */
		SelectionListener listener = new SelectionListener() {
			
			/**
			 * There is no need to implement this method.
			 *
			 * @param selectionEvent the selection event
			 */
			@Override
			public void widgetDefaultSelected(final SelectionEvent selectionEvent) {
				// do nothing
			}

			/**
			 * The implementation of what is supposed to occur when the limited usage
			 * promotion button is selected or unselected by the user in the promotion
			 * wizard.
			 *
			 * @param selectionEvent the selection event that was occurred
			 */
			@Override
			public void widgetSelected(final SelectionEvent selectionEvent) {
				Object object = selectionEvent.getSource();

				//Check if it was the button that was the source of the vent
				if (object instanceof Button) {
					//Find out the type of button press it was, if the button is selected or not
					Button limitedUsagePromotionButton = (Button) object;
					
					//If the button is selected then allow the user to see the allowedLimitText text box
					//and add the limited usage condition to the model as a condition and update the validation
					// for the limited promotion text box binding
					if (limitedUsagePromotionButton.getSelection()) {

						allowedLimitSpinner.setEnabled(true);
						allowedLimitSpinner.setVisible(true);
						
						allowedLimitLabel.setVisible(true);

						// add the new condition to the rule
						getModel().addCondition(limitedUsagePromotionRuleCondition);

						RuleParameter lupParamater = getRuleParameterByKey(limitedUsagePromotionRuleCondition, 
								RuleParameter.LIMITED_USAGE_PROMOTION_ID);

						lupParamater.setValue(limitedUsagePromotionID);
						
						validateModel();


					} else {
						//If the selection event was to disable the button, then we must now allow the user to 
						//see the allowedLimitText box and remove and limited usage promotion conditions are
						//present in the model and then update all validation on the limited promotion text box binding
						allowedLimitSpinner.setEnabled(false);
						allowedLimitSpinner.setVisible(false);
						
						allowedLimitLabel.setVisible(false);

						Set<RuleCondition> set = getModel().getConditions();

						for (RuleCondition ruleCondition : set) {

							if (RuleElementType.LIMITED_USAGE_PROMOTION_CONDITION.equals(ruleCondition.getElementType())) {
								getModel().removeCondition(ruleCondition);
								break;
							}
						}
						
						validateModel();
					}
				}
			}

			/**
			 * Method to validate the model to the target and update the target to model.
			 */
			private void validateModel() {
				limitedUsagePromotionTextBinding.getBinding().validateModelToTarget();
				limitedUsagePromotionTextBinding.getBinding().updateTargetToModel();
			}
		};
		
		return listener;
	}
	
	@Override
	protected void populateControls() {
		
		// Populate target combo with either catalogs for catalog promotions or stores for the shopping cart promotions
		if (catalogPromotion) {
			//Populate the target combo with catalogs and the relevant message
			targetCombo.add(PromotionsMessages.get().PromoDetailsOverview_CatalogCombo_InitialMessage);
			for (Catalog catalog : catalogs) {
				targetCombo.add(catalog.getName());
			}
			targetCombo.setData(catalogs);

			// default the from date to the current date
			activeFromDatePicker.setDate(new Date());
			// binding hasn't been done yet, need to set this here
			getModel().setStartDate(new Date());
		} else {
			//Populate the target combo with stores with the relevant message
			targetCombo.add(PromotionsMessages.get().PromoDetailsOverview_StoreCombo_InitialMessage);
			for (Store store : stores) {
				targetCombo.add(store.getName());
			}
			targetCombo.setData(stores);
		}

		
		//Select the first index of the target combo
		targetCombo.select(0);

		refreshLocales();
		
		if (!catalogPromotion) {
			//If a shopping cart promotion then display the visible in store button for use
			visibleInStore.setSelection(getModel().isEnabled());
		}
	}

	@Override
	protected void bindControls() {
		final EpControlBindingProvider bindingProvider = EpControlBindingProvider.getInstance();

		// Sets the CM user for the model
		getModel().setCmUser(LoginManager.getCmUser());

		//Binds the target combo box using data binding
		bindTargetCombo(bindingProvider);

		// Binds the promotion name using data binding
		bindingProvider.bind(getDataBindingContext(), promotionNameText, getModel(), "name", //$NON-NLS-1$
				EpValidatorFactory.STRING_255_REQUIRED, null, true);

		// Binds the description of the promotion using data binding
		bindingProvider.bind(getDataBindingContext(), descriptionText, getModel(), "description", //$NON-NLS-1$
				EpValidatorFactory.MAX_LENGTH_255, null, true);
		
		nameController.bind();

		//Check if this is a catalog promotion, if it is a shopping cart promotion then bind the visible in store 
		//check box and limited usage promotion button to the model
		if (catalogPromotion) {
			// Binds the active from date using data binding
			bindActiveFromDate(bindingProvider);

			// Binds the active to date using data binding
			bindActiveToDate(bindingProvider);
		} else {
			bindingProvider.bind(getDataBindingContext(), visibleInStore, getModel(), "enabled", null, null, false); //$NON-NLS-1$

			// Binds the limited usage promotion text box using data binding
			bindLimitedUsagePromotionText(bindingProvider);
		}

		//Create the new promotions wizard details page
		EpWizardPageSupport.create(NewPromotionWizardDetailsPage.this, getDataBindingContext());
	}

	/**
	 * Binds the combo drop down using data binding. The method creates an
	 * update strategy depending on the type of promotion for either catalog or store
	 * and then finally binds it.
	 * 
	 * @param bindingProvider the provider of the binding
	 */
	private void bindTargetCombo(final EpControlBindingProvider bindingProvider) {
		
		//The observable update value strategy
		ObservableUpdateValueStrategy updateStrat;
		
		if (catalogPromotion) {
			//If the promotion is a catalog promotion then you need to create a drop down that will be populated 
			//with catalogs
			updateStrat = new ObservableUpdateValueStrategy() {
				@Override
				protected IStatus doSet(final IObservableValue observableValue, final Object value) {
					final int selectionIndex = (Integer) value;
					try {
						final Catalog catalog = catalogs.get(selectionIndex - 1);
						getModel().setCatalog(catalog);
						return Status.OK_STATUS;
					} catch (final EpServiceException e) {
						return new Status(IStatus.WARNING, StorePlugin.PLUGIN_ID, "Cannot set the promotion catalog."); //$NON-NLS-1$
					}
				}
			};
		} else {
			//If the promotion is a shopping cart promotion then you need to create a drop down that will be populated
			//with stores
			updateStrat = new ObservableUpdateValueStrategy() {
				@Override
				protected IStatus doSet(final IObservableValue observableValue, final Object value) {
					final int selectionIndex = (Integer) value;
					try {
						final Store store = stores.get(selectionIndex - 1);
						getModel().setStore(store);
						return Status.OK_STATUS;
					} catch (final EpServiceException e) {
						return new Status(IStatus.WARNING, StorePlugin.PLUGIN_ID, "Cannot set the promotion catalog."); //$NON-NLS-1$
					}
				}
			};
		}
		
		//Bind the target combo box so that it is a required combo with the first element not being valid, and update accordingly to the
		//update strategy
		bindingProvider.bind(getDataBindingContext(), targetCombo, EpValidatorFactory.REQUIRED_COMBO_FIRST_ELEMENT_NOT_VALID, null, updateStrat,
				true);
	}

	/**
	 * Bind the active from date using data binding.
	 * 
	 * @param bindingProvider the provider for data binding
	 */
	private void bindActiveFromDate(final EpControlBindingProvider bindingProvider) {
		// Binds the active from date so that the date is required, also creates an observable update value strategy in order
		// to set the date with some value retrieved from the date picker
		bindingProvider.bind(getDataBindingContext(), activeFromDatePicker.getSwtText(), EpValidatorFactory.DATE_TIME_REQUIRED, null,
				new ObservableUpdateValueStrategy() {
					@Override
					protected IStatus doSet(final IObservableValue observableValue, final Object value) {
						getModel().setStartDate(activeFromDatePicker.getDate());
						return Status.OK_STATUS;
					}
				}, true);
	}

	/**
	 * Bind the active to date using data binding.
	 *
	 * @param bindingProvider the provider for data binding
	 */
	private void bindActiveToDate(final EpControlBindingProvider bindingProvider) {
		// Binds the active from date so that you are optionally able to pick a date and is not a null value, does not come before the
		// active from date and only if these two conditions are met is validation passed
		bindingProvider.bind(getDataBindingContext(), activeToDatePicker.getSwtText(), new CompoundValidator(new IValidator[] {
				EpValidatorFactory.DATE_TIME, value -> {
			if (activeToDatePicker.getDate() != null && activeToDatePicker.getDate().before(activeFromDatePicker.getDate())) {
				return new Status(IStatus.ERROR, StorePlugin.PLUGIN_ID, PromotionsMessages.get().CreatePromotionWizardDetailsPage_Date_Error,
						null);
			}
			return Status.OK_STATUS;
		}}), null, new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object value) {
				getModel().setEndDate(activeToDatePicker.getDate());
				return Status.OK_STATUS;
			}
		}, true);
	}

	/**
	 * Bind the limited usage promotion text using data binding.
	 *
	 * @param bindingProvider the provider of data binding
	 */
	private void bindLimitedUsagePromotionText(final EpControlBindingProvider bindingProvider) {
		
		//Obtain the rule condition for the limited usage promotions (also known as the Limited Usage Condition)
		limitedUsagePromotionRuleCondition = ServiceLocator.getService(
				RuleElementType.LIMITED_USAGE_PROMOTION_CONDITION.getPropertyKey());

		//Retrieve the rule parameter by key
		limitedUsagePromotionRuleParameter = getRuleParameterByKey(limitedUsagePromotionRuleCondition, RuleParameter.ALLOWED_LIMIT);

		limitedUsagePromotionTextBinding = bindingProvider.bind(getDataBindingContext(), allowedLimitSpinner, 
				limitedUsagePromotionRuleParameter, "value", null, new EpIntToStringConverter(), true);  //$NON-NLS-1$
	}
	
	private void refreshLocales() {
		supportedLocales.clear();
		int selectionIndex = targetCombo.getSelectionIndex();
		
		if (selectionIndex > 0) {
			selectionIndex -= 1;
		}
		
		if (catalogPromotion) {
			Catalog catalog = catalogs.get(selectionIndex);
			supportedLocales.addAll(catalog.getSupportedLocales());
			defaultLocale = catalog.getDefaultLocale();
		} else {
			Store store = stores.get(selectionIndex);
			supportedLocales.addAll(store.getSupportedLocales());
			defaultLocale = store.getDefaultLocale();
		}
		nameController.populate(supportedLocales, defaultLocale, getModel().getLocalizedProperties());
	}
}