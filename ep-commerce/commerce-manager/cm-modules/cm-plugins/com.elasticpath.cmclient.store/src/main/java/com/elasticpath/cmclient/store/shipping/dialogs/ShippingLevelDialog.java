/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.store.shipping.dialogs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.elasticpath.cmclient.catalog.CatalogPlugin;
import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.EpDialogSupport;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.ui.dialog.AbstractEpDialog;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.EpLocalizedPropertyController;
import com.elasticpath.cmclient.core.ui.framework.EpPropertyTableControl;
import com.elasticpath.cmclient.core.ui.framework.EpPropertyTableValueModifiedListener;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.validation.CompoundValidator;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.cmclient.store.StorePlugin;
import com.elasticpath.cmclient.store.shipping.ShippingImageRegistry;
import com.elasticpath.cmclient.store.shipping.ShippingLevelsMessages;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.constants.EpShippingContextIdNames;
import com.elasticpath.domain.shipping.ShippingCostCalculationMethod;
import com.elasticpath.domain.shipping.ShippingCostCalculationParameter;
import com.elasticpath.domain.shipping.ShippingServiceLevel;
import com.elasticpath.domain.shipping.ShippingRegion;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.shipping.ShippingServiceLevelService;
import com.elasticpath.service.shipping.ShippingRegionService;
import com.elasticpath.service.store.StoreService;
import com.elasticpath.settings.SettingsService;

/**
 * Dialog for creating and editing entity.
 */
@SuppressWarnings({ "PMD.TooManyFields", "PMD.GodClass" })
public class ShippingLevelDialog extends AbstractEpDialog { //NOPMD
	private static final String PROPERTY_CLOSE_BRACES = ")"; //$NON-NLS-1$

	private static final String PROPERTY_OPEN_BRACES = " ("; //$NON-NLS-1$

	private static final int MAX_NAME_LENGTH = 255;

	/** This dialog's title. Depends from whether this is create or edit dialog */
	private final String title;

	/** This dialog's image. Depends from whether this is create or edit dialog */
	private final Image image;

	private final DataBindingContext dataBindingContext;

	private final StoreService storeService;

	private final ShippingRegionService shippingRegionService;

	private final ShippingServiceLevelService shippingServiceLevelService;

	private final ShippingServiceLevel shippingServiceLevel;

	private final List<Store> stores;

	private final List<ShippingRegion> shippingRegions;

	private final List<String> carriers;

	private final List<ShippingCostCalculationMethod> calcMethods;

	private List<Locale> supportedLocales;

	private List<Currency> supportedCurrencies;

	private Locale defaultLocale;

	private CCombo storeCombo;

	private CCombo shippingRegionCombo;

	private CCombo carrierCombo;

	private Text nameText;

	private CCombo nameCombo;

	private CCombo calcMethodCombo;

	private EpPropertyTableControl calcMethodPropertyTable;

	private EpLocalizedPropertyController nameController;

	private int currentStoreIndex;

	private int currentCalcMethodIndex;

	private final Map<String, ShippingCostCalculationParameter> restoreMap;

	private final Set<String> propertyTableValidationFailed = new HashSet<>();

	private Button stateCheckbox;

	private boolean newShippingServiceLevel;

	private Text codeText;

	/**
	 * The constructor.
	 * 
	 * @param parentShell the parent Shell
	 * @param shippingServiceLevel the entity
	 * @param image the image for this dialog
	 * @param title the title for this dialog
	 */
	public ShippingLevelDialog(final Shell parentShell, final ShippingServiceLevel shippingServiceLevel, final String title,
			final Image image) {
		super(parentShell, 1, true);

		this.title = title;
		this.image = image;

		this.shippingServiceLevel = shippingServiceLevel;

		dataBindingContext = new DataBindingContext();

		storeService = ServiceLocator.getService(ContextIdNames.STORE_SERVICE);
		shippingRegionService =
				ServiceLocator.getService(EpShippingContextIdNames.SHIPPING_REGION_SERVICE);
		shippingServiceLevelService =
				ServiceLocator.getService(
						EpShippingContextIdNames.SHIPPING_SERVICE_LEVEL_SERVICE);

		stores = storeService.findAllCompleteStores();
		AuthorizationService.getInstance().removeUnathorizedStoresFrom(stores);
		
		shippingRegions = shippingRegionService.list();
		carriers = getAllShipmentCarriers();
		calcMethods = shippingServiceLevelService.getAllShippingCostCalculationMethods();

		restoreMap = new HashMap<>();
	}

	/**
	 * Convenience method to open a create dialog.
	 * 
	 * @param parentShell the parent Shell
	 * @param shippingServiceLevel the entity to create
	 * @return <code>true</code> if the user presses the OK button, <code>false</code> otherwise
	 */
	public static boolean openCreateDialog(final Shell parentShell, final ShippingServiceLevel shippingServiceLevel) {
		final ShippingLevelDialog dialog =
				new ShippingLevelDialog(parentShell, shippingServiceLevel, ShippingLevelsMessages.get().CreateShippingLevelDialogTitle,
						ShippingImageRegistry.getImage(ShippingImageRegistry.IMAGE_SHIPPING_LEVEL_CREATE));

		dialog.setCreateNewShippingLevel(true);
		return (dialog.open() == 0);
	}

	private void setCreateNewShippingLevel(final boolean newShippingServiceLevel) {
		this.newShippingServiceLevel = newShippingServiceLevel;
	}

	/**
	 * Convenience method to open an edit dialog.
	 * 
	 * @param parentShell the parent Shell
	 * @param shippingServiceLevel the entity to edit
	 * @return <code>true</code> if the user presses the OK button, <code>false</code> otherwise
	 */
	public static boolean openEditDialog(final Shell parentShell, final ShippingServiceLevel shippingServiceLevel) {
		final ShippingLevelDialog dialog =
				new ShippingLevelDialog(parentShell, shippingServiceLevel, ShippingLevelsMessages.get().EditShippingLevelDialogTitle,
						ShippingImageRegistry.getImage(ShippingImageRegistry.IMAGE_SHIPPING_LEVEL));

		return (dialog.open() == 0);
	}

	@Override
	protected String getInitialMessage() {
		return ShippingLevelsMessages.get().ShippingLevelDialogInitialMessage;
	}

	@Override
	protected String getTitle() {
		return title;
	}

	@Override
	protected Image getWindowImage() {
		return image;
	}

	@Override
	protected String getWindowTitle() {
		return getTitle();
	}

	@Override
	protected void createEpDialogContent(final IEpLayoutComposite dialogComposite) {
		IEpLayoutComposite composite =
				dialogComposite.addTableWrapLayoutComposite(2, false, dialogComposite.createLayoutData(IEpLayoutData.FILL,
						IEpLayoutData.FILL, true, true));

		EpState state = EpState.EDITABLE;
		if (shippingServiceLevel.isPersisted()) {
			state = EpState.DISABLED;
		}

		final IEpLayoutData labelData = composite.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL, false, false);
		
		composite.addLabelBoldRequired(ShippingLevelsMessages.get().ShippingLevelDialogStoreTitle, EpState.EDITABLE, labelData);

		storeCombo = composite.addComboBox(state, composite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false));
		storeCombo.addSelectionListener(new StoreSelectionListener());

		composite.addLabelBoldRequired(ShippingLevelsMessages.get().ShippingLevelDialogShippingRegionTitle, EpState.EDITABLE, labelData);
		shippingRegionCombo = composite.addComboBox(state, composite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false));

		composite.addLabelBoldRequired(ShippingLevelsMessages.get().ShippingLevelDialogCarrierTitle, EpState.EDITABLE, labelData);
		carrierCombo =
				composite.addComboBox(EpState.EDITABLE, composite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false));

		composite.addLabelBoldRequired(ShippingLevelsMessages.get().ShippingLevelDialogCodeTitle, EpState.EDITABLE, labelData);

		codeText = composite.addTextField(state, composite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false));
		
		composite.addLabelBoldRequired(ShippingLevelsMessages.get().ShippingLevelDialogNameTitle, EpState.EDITABLE, labelData);
				
		IEpLayoutComposite nameComposite =
				composite.addTableWrapLayoutComposite(2, false, composite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true,
						true));

		nameCombo =
				nameComposite.addComboBox(EpState.EDITABLE, nameComposite.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.FILL,
						false, false));
		nameText =
				nameComposite.addTextField(EpState.EDITABLE, nameComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true,
						false));
		nameText.setTextLimit(MAX_NAME_LENGTH);

		composite.addLabelBoldRequired(ShippingLevelsMessages.get().ShippingLevelDialogCalculationMethodTitle, EpState.EDITABLE, labelData);
		calcMethodCombo =
				composite.addComboBox(EpState.EDITABLE, composite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false));
		calcMethodCombo.addSelectionListener(new CalcMethodSelectionListener());

		composite.addLabelBoldRequired(ShippingLevelsMessages.get().ShippingLevelDialogPropertiesTitle, EpState.EDITABLE, labelData);
		calcMethodPropertyTable =
				EpPropertyTableControl.createPropertyModifierControl(composite,
						ShippingLevelsMessages.get().ShippingLevelDialogPropertiesKeyColumn,
						ShippingLevelsMessages.get().ShippingLevelDialogPropertiesValueColumn, EpValidatorFactory.REQUIRED,
						new CalcMethodPropertyTableValueModifiedListener());

		// state checkbox
		composite.addLabelBold(ShippingLevelsMessages.get().Active, labelData);
		stateCheckbox = composite.addCheckBoxButton(ShippingLevelsMessages.EMPTY_STRING, EpState.EDITABLE, null);
		
		nameController =
				EpLocalizedPropertyController.createEpLocalizedPropertyController(nameText, nameCombo,
						ShippingServiceLevel.LOCALIZED_PROPERTY_NAME, true, dataBindingContext);
	}

	@Override
	protected void bindControls() {
		EpControlBindingProvider bindingProvider = EpControlBindingProvider.getInstance();
		
		bindingProvider.bind(dataBindingContext, storeCombo, null, null, new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
				shippingServiceLevel.setStore(stores.get((Integer) newValue));

				return Status.OK_STATUS;
			}
		}, true);

		bindingProvider.bind(dataBindingContext, shippingRegionCombo, null, null,
				new ObservableUpdateValueStrategy() {
					@Override
					protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
						shippingServiceLevel.setShippingRegion(shippingRegions.get((Integer) newValue));

						return Status.OK_STATUS;
					}
				}, true);

		bindingProvider.bind(dataBindingContext, carrierCombo, null, null, new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
				shippingServiceLevel.setCarrier(carriers.get((Integer) newValue));

				return Status.OK_STATUS;
			}
		}, true);		
	
		bindingProvider.bind(dataBindingContext, calcMethodCombo, null, null, new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
				Set<ShippingCostCalculationParameter> parameters = new HashSet<>();

				Enumeration<String> propertyNames = (Enumeration<String>) calcMethodPropertyTable.getProperties().propertyNames();
				while (propertyNames.hasMoreElements()) {
					String propertyName = propertyNames.nextElement();
					ShippingCostCalculationParameter originalParameter = restoreMap.get(propertyName);

					ShippingCostCalculationParameter parameter =
							ServiceLocator.getService(
									EpShippingContextIdNames.SHIPPING_COST_CALCULATION_PARAMETER);

					parameter.setKey(originalParameter.getKey());
					parameter.setCurrency(originalParameter.getCurrency());
					parameter.setValue(calcMethodPropertyTable.getProperties().getProperty(propertyName));

					parameters.add(parameter);
				}

				ShippingCostCalculationMethod originalCalcMethod = calcMethods.get((Integer) newValue);
				ShippingCostCalculationMethod newCalcMethod =
						ServiceLocator.getService(originalCalcMethod.getType());

				newCalcMethod.setParameters(parameters);

				shippingServiceLevel.setShippingCostCalculationMethod(newCalcMethod);
				return Status.OK_STATUS;
			}
		}, true);

		nameController.bind();

		bindingProvider.bind(dataBindingContext, stateCheckbox, shippingServiceLevel, "enabled"); //$NON-NLS-1$
		
		EpDialogSupport.create(this, dataBindingContext);

		dataBindingContext.updateModels();

		// bind codeText AFTER updating the model to prevent firing validation for the code.
		if (!shippingServiceLevel.isPersisted()) {
		bindingProvider.bind(dataBindingContext, this.codeText, shippingServiceLevel, "code",  //$NON-NLS-1$
				new CompoundValidator(EpValidatorFactory.REQUIRED, EpValidatorFactory.PRODUCT_CODE,
						value -> {
							final String stringValue = (String) value;
							return checkShippingServiceLevelCode(stringValue);
						}), null, true);
		}
	}
	
	private IStatus checkShippingServiceLevelCode(final String code) {
		// check uniqueness		
		if (shippingServiceLevelService.codeExists(code)) {			
			return new Status(IStatus.ERROR,
					CatalogPlugin.PLUGIN_ID, IStatus.ERROR, ShippingLevelsMessages.get().ShippingServiceLeve_Duplicate_Code, null);
		}
		return Status.OK_STATUS;
	}

	@Override
	protected String getPluginId() {
		return StorePlugin.PLUGIN_ID;
	}

	@Override
	public Object getModel() {
		return shippingServiceLevel;
	}

	@Override
	protected void populateControls() { //NOPMD
		for (Store store : stores) {
			storeCombo.add(store.getName());
		}
		Store store = shippingServiceLevel.getStore();
		if (store == null) {
			currentStoreIndex = 0;
			storeCombo.select(currentStoreIndex);
		} else {
			storeCombo.setText(store.getName());
			currentStoreIndex = storeCombo.getSelectionIndex();
		}

		for (ShippingRegion shippingRegion : shippingRegions) {
			shippingRegionCombo.add(shippingRegion.getName());
		}
		ShippingRegion shippingRegion = shippingServiceLevel.getShippingRegion();
		if (shippingRegion == null) {
			shippingRegionCombo.select(0);
		} else {
			shippingRegionCombo.setText(shippingRegion.getName());
		}

		for (String carrier : carriers) {
			carrierCombo.add(carrier);
		}
		String carrier = shippingServiceLevel.getCarrier();
		if (carrier == null) {
			carrierCombo.select(0);
		} else {
			carrierCombo.setText(carrier);
		}
		if (shippingServiceLevel.getCode() != null) {
			codeText.setText(shippingServiceLevel.getCode());
		}

		for (ShippingCostCalculationMethod method : calcMethods) {
			calcMethodCombo.add(ShippingLevelsMessages.get().localizeCalcParam(method));
		}
		ShippingCostCalculationMethod shippingCostCalculationMethod = shippingServiceLevel.getShippingCostCalculationMethod();
		if (shippingCostCalculationMethod == null) {
			currentCalcMethodIndex = 0;
			calcMethodCombo.select(currentCalcMethodIndex);
		} else {
			calcMethodCombo.setText(ShippingLevelsMessages.get().localizeCalcParam(shippingCostCalculationMethod));
			currentCalcMethodIndex = calcMethodCombo.getSelectionIndex();
		}

		stateCheckbox.setSelection(newShippingServiceLevel || shippingServiceLevel.isEnabled());
		
		reloadName();
		reloadCalcMethodProperties();
	}

	@Override
	protected void okPressed() {
		dataBindingContext.updateModels();

		if (propertyTableValidationFailed.isEmpty() && validateCalcMethodPropertyTable()) {
			super.okPressed();
		}
	}
	
	private boolean validateCalcMethodPropertyTable() {
		boolean error = false;
		if (!calcMethodPropertyTable.isPropertiesValidated()) {
			setErrorMessage(ShippingLevelsMessages.get().PropertiesAreRequiredMsg);

			error = true;
		}

		return !error;
	}

	private void reloadName() {
		refreshLocales();

		nameController.populate(supportedLocales, defaultLocale, shippingServiceLevel.getLocalizedProperties());
	}

	private void reloadCalcMethodProperties() {
		refreshCurrencies();

		calcMethodPropertyTable.setProperties(getCalcMethodProperties(calcMethodCombo.getSelectionIndex()));
	}

	private void refreshLocales() {
		Store store = stores.get(storeCombo.getSelectionIndex());
		supportedLocales = new ArrayList<>(store.getSupportedLocales());
		defaultLocale = store.getDefaultLocale();
	}

	private void refreshCurrencies() {
		supportedCurrencies = new ArrayList<>(stores.get(storeCombo.getSelectionIndex()).getSupportedCurrencies());
	}

	private Properties getCalcMethodProperties(final int index) {
		Iterable<ShippingCostCalculationParameter> parameters;

		// If properties for calcMethod, which is set for current shippingLevel, are requested.
		final ShippingCostCalculationMethod currentCalculationMethod = shippingServiceLevel.getShippingCostCalculationMethod();
		if ((shippingServiceLevel.isPersisted()) && (calcMethods.get(index).getType().equals(currentCalculationMethod.getType()))) {
			final Set<ShippingCostCalculationParameter> savedParams = currentCalculationMethod.getParameters();
			final List<ShippingCostCalculationParameter> defaultParameters = currentCalculationMethod.getDefaultParameters(supportedCurrencies);

			// we are missing some default parameters
			if (defaultParameters.size() > savedParams.size()) {
				CollectionUtils.filter(defaultParameters, obj -> {
					final ShippingCostCalculationParameter param = (ShippingCostCalculationParameter) obj;
					return !(param.isCurrencyAware() && currentCalculationMethod.hasParameter(param.getKey(), param.getCurrency())
							|| !param.isCurrencyAware() && currentCalculationMethod.hasParameter(param.getKey()));
				});
				savedParams.addAll(defaultParameters);
			}

			parameters = savedParams;
		} else {
			parameters = calcMethods.get(index).getDefaultParameters(supportedCurrencies);
		}

		Properties properties = new Properties();
		populateProperties(properties, parameters);
		
		return properties;
	}

	private void populateProperties(final Properties properties, final Iterable<ShippingCostCalculationParameter> parameters) {

		for (ShippingCostCalculationParameter parameter : parameters) {
			String key = CoreMessages.get().getMessage(parameter.getKey());
			if (parameter.isCurrencyAware()) {
				key = key + PROPERTY_OPEN_BRACES + parameter.getCurrency().getCurrencyCode() + PROPERTY_CLOSE_BRACES;
			}
			String value = parameter.getValue();
			properties.setProperty(key, ShippingLevelsMessages.get().objectToString(value));
			restoreMap.put(key, parameter);
		}
	}	
	
	/**
	 * Return all supported shipment carrier.
	 * 
	 * @return all supported shipment carrier.
	 */
	private List<String> getAllShipmentCarriers() {
		SettingsService settingsService = ServiceLocator.getService(ContextIdNames.SETTINGS_SERVICE);
		final String valueStr =
			settingsService.getSettingValue("COMMERCE/SYSTEM/SHIPPING/carriers").getValue(); //$NON-NLS-1$

		final String[] carriers = valueStr.split(","); //$NON-NLS-1$
		List<String> allCarriers = new ArrayList<>();
		for (String element : carriers) {
			allCarriers.add(element.trim());
		}
		Collections.sort(allCarriers);

		return allCarriers;
	}

	/**
	 * Store SelectionListener.
	 */
	private class StoreSelectionListener implements SelectionListener {
		@Override
		public void widgetDefaultSelected(final SelectionEvent event) {
			// Nothing
		}

		@Override
		public void widgetSelected(final SelectionEvent event) {
			if ((calcMethodPropertyTable.isPropertiesModified() || nameController.isNameModified())
					&& !MessageDialog.openQuestion(getShell(), ShippingLevelsMessages.get().ShippingLevelNameAndPropertiesModifiedTitle,
					ShippingLevelsMessages.get().ShippingLevelNameAndPropertiesModifiedText)) {

				storeCombo.select(currentStoreIndex);

				return;
			}

			currentStoreIndex = storeCombo.getSelectionIndex();

			reloadName();
			reloadCalcMethodProperties();
		}

	}

	/**
	 * CalcMethod SelectionListener.
	 */
	private class CalcMethodSelectionListener implements SelectionListener {
		@Override
		public void widgetDefaultSelected(final SelectionEvent event) {
			// Nothing
		}

		@Override
		public void widgetSelected(final SelectionEvent event) {
			if (calcMethodPropertyTable.isPropertiesModified()
					&& !MessageDialog.openQuestion(getShell(), ShippingLevelsMessages.get().ShippingLevelPropertiesModifiedTitle,
					ShippingLevelsMessages.get().ShippingLevelPropertiesModifiedText)) {

				calcMethodCombo.select(currentCalcMethodIndex);

				return;
			}

			currentCalcMethodIndex = calcMethodCombo.getSelectionIndex();

			reloadCalcMethodProperties();
		}
	}

	/**
	 * CalcMethod PropertyTableValueModifiedListener.
	 */
	private class CalcMethodPropertyTableValueModifiedListener implements EpPropertyTableValueModifiedListener {
		@Override
		public boolean onModification(final Entry<String, String> entry, final String newPropertyValue) {
			ShippingCostCalculationParameter parameter = restoreMap.get(entry.getKey());
			if (parameter.isCurrencyAware()) {
				try {
					if (newPropertyValue.trim().length() > 0) {
						Double.valueOf(newPropertyValue);
					}
					propertyTableValidationFailed.remove(entry.getKey());
					if (propertyTableValidationFailed.isEmpty()) {
						setErrorMessage(null);
					}
				} catch (NumberFormatException ex) {
					propertyTableValidationFailed.add(entry.getKey());
					calcMethodCombo.setFocus();
					setErrorMessage(ShippingLevelsMessages.get().WrongNumberFormatMsg);
					return false;
				}
			}
			return true;
		}

		@Override
		public void onPrepareForModification(final Entry<String, String> entry) {
			// Nothing
		}

		@Override
		@SuppressWarnings("PMD.UnnecessaryWrapperObjectCreation")
		public void onPostModification(final Entry<String, String> entry) {
			ShippingCostCalculationParameter parameter = restoreMap.get(entry.getKey());
			if (parameter.isCurrencyAware() && StringUtils.isNotEmpty(entry.getValue())) {
				entry.setValue(Double.valueOf(entry.getValue()).toString());
			}
		}
	}

}
