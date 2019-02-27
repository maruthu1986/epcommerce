/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.catalog.wizards.sku;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.databinding.AggregateValidationStatus;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.ValidationStatusProvider;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.PageChangingEvent;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ExpandableComposite;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.catalog.CatalogPlugin;
import com.elasticpath.cmclient.catalog.dialogs.product.ProductSkuOptionValueDialog;
import com.elasticpath.cmclient.catalog.editors.sku.IProductSkuEventListener;
import com.elasticpath.cmclient.catalog.editors.sku.ProductSkuDigitalAssetViewPart;
import com.elasticpath.cmclient.catalog.editors.sku.ProductSkuShippingViewPart;
import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.CorePlugin;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.EpValueBinding;
import com.elasticpath.cmclient.core.binding.EpWizardPageSupport;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.binding.ValueUpdateHandler;
import com.elasticpath.cmclient.core.helpers.ChangeSetHelper;
import com.elasticpath.cmclient.core.helpers.DataBindingUtil;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpDateTimePicker;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.validation.CompoundValidator;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.cmclient.core.wizard.AbstractEPWizardPage;
import com.elasticpath.cmclient.policy.StatePolicy;
import com.elasticpath.cmclient.policy.common.AbstractStatePolicyImpl;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.DigitalAsset;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.changeset.ChangeSetObjectStatus;
import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;
import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.service.catalog.ProductSkuService;
import com.elasticpath.service.catalog.SkuOptionService;
import com.elasticpath.service.changeset.ChangeSetMemberAction;
import com.elasticpath.service.tax.TaxCodeService;

/**
 * The Store configuration wizard page.
 */
@SuppressWarnings({ "PMD.ExcessiveImports", "PMD.TooManyFields", "PMD.TooManyMethods", "PMD.GodClass" })
public class AddSkuWizardPage1 extends AbstractEPWizardPage<Product> implements SelectionListener, IProductSkuEventListener {

	private static final int PAGE_LAYOUT_NUM_COLUMNS_3 = 3;

	private static final int SKU_OPTION_COLUMN_COUNT = 4;

	private final String title;

	private final ProductSku productSku;

	private final Product product;

	private Text skuCodeText;

	private IEpDateTimePicker enableDateComponent;

	private IEpDateTimePicker disableDateComponent;

	private CCombo taxCodeCombo;

	private final Map<CCombo, List<SkuOptionValue>> skuOptionAndValueMap = new HashMap<>();

	private final SkuOptionService skuOptionService = ServiceLocator.getService(ContextIdNames.SKU_OPTION_SERVICE);

	private final ProductSkuService skuService = ServiceLocator.getService(ContextIdNames.PRODUCT_SKU_SERVICE);

	private ControlDecoration skuCodeDecoration;

	private ValueUpdateHandler skuCodeValueHandler;

	private AggregateValidationStatus status;

	private IStatus currentStatus;

	private final ChangeSetHelper changeSetHelper = ServiceLocator.getService(ChangeSetHelper.BEAN_ID);

	private final Map<SkuOption, Label> skuOptionLockIconMap = new HashMap<>();

	private ExpandableComposite expandableDigitalComposite;

	private ExpandableComposite expandableShippingComposite;

	private IEpLayoutComposite digitalAssetSection;

	private IEpLayoutComposite physicalShippingComposite;

	private ProductSkuDigitalAssetViewPart digitalAssetViewPart;

	private ProductSkuShippingViewPart productSkuShippingViewPart;

	private final DigitalAsset digitalAsset;

	private Button shippableRadioButton;

	private Button digitalAssetRadioButton;

	private Button digitalAssetDownloadable;

	private List<TaxCode> taxCodeList;

	/**
	 * Validator for the combo boxes representing the SKU option values.
	 */
	private static final IValidator OPTION_VALUE_COMBO_VALIDATOR = value -> {
		if (value instanceof Integer && (Integer) value > 0) { // initial index 0 is 'Select...'
			return Status.OK_STATUS; // otherwise it is OK
		}
		return new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, IStatus.ERROR, CoreMessages.get().EpValidatorFactory_ValueRequired, null);
	};

	private final StatePolicy statePolicy = new AbstractStatePolicyImpl() {
		@Override
		public EpState determineState(final PolicyActionContainer targetContainer) {
			return EpState.EDITABLE;
		}

		@Override
		public void init(final Object dependentObject) {
			// not applicable
		}
	};

	private EpValueBinding digitalAssetDownloadableBinding;

	/**
	 * The Constructor.
	 *  @param pageName the page name
	 * @param title the page title
	 * @param databindingContext the data binding context
	 * @param product the <code>Product</code> will have this new sku
	 * @param productSku the <code>ProductSku</code>
	 */
	protected AddSkuWizardPage1(final String pageName,
								final String title,
								final DataBindingContext databindingContext,
								final Product product,
								final ProductSku productSku) {

		super(2, false, pageName, databindingContext);

		digitalAsset = ServiceLocator.getService(ContextIdNames.DIGITAL_ASSET);

		this.title = title;
		setMessage(CatalogMessages.get().AddSkuWizardPage1_Msg);

		this.product = product;
		this.productSku = productSku;

		setPageComplete(false);
	}

	@Override
	protected void createEpPageContent(final IEpLayoutComposite dialogComposite) {
		final IEpLayoutData labelData = dialogComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL);
		final IEpLayoutData fieldData = dialogComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false);

		dialogComposite.addLabelBoldRequired(CatalogMessages.get().ProductSkuCreateDialog_SkuCode, EpState.EDITABLE, labelData);
		skuCodeText = dialogComposite.addTextField(EpState.EDITABLE, fieldData);

		dialogComposite.addLabelBoldRequired(CatalogMessages.get().ProductSkuCreateDialog_EnableDate, EpState.EDITABLE, labelData);
		enableDateComponent = dialogComposite.addDateTimeComponent(IEpDateTimePicker.STYLE_DATE, EpState.EDITABLE, fieldData);

		dialogComposite.addLabelBold(CatalogMessages.get().ProductSkuCreateDialog_DisableDate, labelData);
		disableDateComponent = dialogComposite.addDateTimeComponent(IEpDateTimePicker.STYLE_DATE, EpState.EDITABLE, fieldData);

		dialogComposite.addLabelBold(CatalogMessages.get().ProductSkuCreateDialog_TaxCode, labelData);
		taxCodeCombo = dialogComposite.addComboBox(EpState.EDITABLE, fieldData);

		createSkuOptionDisplaySection(dialogComposite);
		createShippingTypeComposite(dialogComposite);

		IEpLayoutComposite detailsComposite = dialogComposite.addGridLayoutComposite(1,
				false,
				dialogComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false, 2, 1));

		boolean isShippable = productSku.isShippable();
		createShippingDetailsSectionComposite(detailsComposite, isShippable, isShippable);

		boolean isDigital = productSku.isDigital();
		createDigitalAssetsComposite(detailsComposite, isDigital, isDigital);

		setControl(dialogComposite.getSwtComposite());
	}

	private void createShippingTypeComposite(final IEpLayoutComposite dialogComposite) {
		IEpLayoutData shippingData = dialogComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false, 2, 1);

		IEpLayoutComposite shippingComposite = dialogComposite.addGridLayoutSection(2,
				CatalogMessages.get().ProductSkuCreateDialog_ShippableType,
				ExpandableComposite.TITLE_BAR,
				shippingData);

		IEpLayoutData shippingLabelData = shippingComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL);
		IEpLayoutData shippingFieldData = shippingComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false);
		IEpLayoutData radioData = shippingComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL);

		shippingComposite.addLabelBold(CatalogMessages.get().ProductEditorSingleSkuOverview_ShippableType, shippingLabelData);
		final IEpLayoutComposite composite = shippingComposite.addGridLayoutComposite(3, true, shippingFieldData);

		shippableRadioButton = composite.addRadioButton(CatalogMessages.get().ProductEditorSingleSkuOverview_Shippable, EpState.EDITABLE, radioData);
		shippableRadioButton.setSelection(true);

		digitalAssetRadioButton = composite.addRadioButton(CatalogMessages.get().ProductEditorSingleSkuOverview_DigitalAsset,
				EpState.EDITABLE, radioData);
		digitalAssetRadioButton.setSelection(false);

		digitalAssetDownloadable = composite.addCheckBoxButton(CatalogMessages.get().ProductEditorSingleSkuOverview_DigitalAssetDownloadable,
				EpState.EDITABLE,
				radioData);
		digitalAssetDownloadable.setEnabled(false);
	}

	private void createShippingDetailsSectionComposite(final IEpLayoutComposite container, final boolean expanded, final boolean enabled) {
		physicalShippingComposite = container.addGridLayoutSection(PAGE_LAYOUT_NUM_COLUMNS_3,
				CatalogMessages.get().CreateProductWizard_ShippingDetails,
				ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE,
				container.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false));

		expandableShippingComposite = (ExpandableComposite) physicalShippingComposite.getSwtComposite().getParent();

		expandableShippingComposite.setExpanded(expanded);
		expandableShippingComposite.setEnabled(enabled);
		expandableShippingComposite.layout(true);

		productSkuShippingViewPart = new ProductSkuShippingViewPart(productSku);
		productSkuShippingViewPart.createControls(physicalShippingComposite,
				physicalShippingComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false));
	}

	private void createDigitalAssetsComposite(final IEpLayoutComposite container, final boolean expanded, final boolean enabled) {
		digitalAssetSection = container.addGridLayoutSection(PAGE_LAYOUT_NUM_COLUMNS_3,
				CatalogMessages.get().CreateProductWizard_DigitalAsset,
				ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE,
				container.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false));

		expandableDigitalComposite = (ExpandableComposite) digitalAssetSection.getSwtComposite().getParent();

		expandableDigitalComposite.setExpanded(expanded);
		expandableDigitalComposite.setEnabled(enabled);
		expandableDigitalComposite.layout(true);

		digitalAssetViewPart = new ProductSkuDigitalAssetViewPart(productSku, digitalAsset);
		digitalAssetViewPart.createControls(digitalAssetSection, digitalAssetSection.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL));
		digitalAssetViewPart.applyStatePolicy(statePolicy);
	}

	private void createSkuOptionDisplaySection(final IEpLayoutComposite dialogComposite) {
		final IEpLayoutData comboData = dialogComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.BEGINNING, true, false);
		final IEpLayoutData lockLayout = dialogComposite.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.BEGINNING, false, false);
		IEpLayoutComposite skuOptionsComposite = dialogComposite.addGroup(CatalogMessages.get().ProductSkuCreateDialog_SkuOptions,
				SKU_OPTION_COLUMN_COUNT,
				false,
				dialogComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false, 2, 1));

		final Iterator<SkuOption> options = retrieveOptions();
		while (options.hasNext()) {
			final SkuOption option = options.next();

			Label lockIconForOption = skuOptionsComposite.addImage(createBlankImage(dialogComposite), lockLayout);

			skuOptionsComposite.addLabelBoldRequired(option.getOptionKey(), EpState.EDITABLE, null);
			final CCombo skuValuesCombo = skuOptionsComposite.addComboBox(EpState.EDITABLE, comboData);
			final Iterator<SkuOptionValue> values = getSortedSkuOptionValues(option);

			skuValuesCombo.add(CatalogMessages.get().ProductSkuCreateDialog_Select);
			skuValuesCombo.select(0);
			final List<SkuOptionValue> skuValueDropDownList = new ArrayList<>();
			skuOptionAndValueMap.put(skuValuesCombo, skuValueDropDownList);

			while (values.hasNext()) {
				final SkuOptionValue skuOptionValue = values.next();
				addSkuOptionToCombo(skuOptionValue, skuValuesCombo, skuOptionValue == option.getDefaultOptionValue());
			}
			// set reference to the option object
			skuValuesCombo.setData(option);

			bindSkuValueComboBox(skuValuesCombo);

			final Button button = skuOptionsComposite.addPushButton(CatalogMessages.get().ProductSkuCreateDialog_AddValue, EpState.EDITABLE, null);
			button.setImage(CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_ADD));
			button.addSelectionListener(this);
			button.setData(skuValuesCombo);

			if (changeSetHelper.isChangeSetsEnabled()) {
				skuOptionLockIconMap.put(option, lockIconForOption);
				lockIconForOption = setLockIcon(option, lockIconForOption);
				button.setEnabled(shouldEnableButton(option));
			} else {
				lockIconForOption.setImage(null);
			}
		}
	}

	private Image createBlankImage(final IEpLayoutComposite dialogComposite) {
		final Image lockIcon = CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_STATE_LOCKED_SMALL);
		final int width = lockIcon.getBounds().width;
		final int height = lockIcon.getBounds().height;
		return new Image(dialogComposite.getSwtComposite().getDisplay(), width, height);
	}

	private Label setLockIcon(final SkuOption option, final Label label) {
		Label resultingLockIcon = label;
		final Image lockIcon = CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_STATE_LOCKED_SMALL);
		ChangeSetObjectStatus changeSetObjectStatus = changeSetHelper.getChangeSetObjectStatus(option);
		if (changeSetObjectStatus.isLocked()) {
			resultingLockIcon.setImage(lockIcon);
			resultingLockIcon.setToolTipText(option.getOptionKey() + " is locked to a changeset"); //$NON-NLS-1$
		}
		return resultingLockIcon;
	}

	private boolean shouldEnableButton(final SkuOption option) {
		ChangeSetObjectStatus changeSetObjectStatus = changeSetHelper.getChangeSetObjectStatus(option);
		return (!changeSetObjectStatus.isLocked() || changeSetHelper.isMemberOfActiveChangeset(option));
	}

	private Iterator<SkuOptionValue> getSortedSkuOptionValues(final SkuOption option) {
		List<SkuOptionValue> optionValueList = new ArrayList<>(option.getOptionValues());
		Collections.sort(optionValueList, Comparator.comparing(SkuOptionValue::getOptionValueKey));
		return optionValueList.iterator();
	}

	@Override
	public void populateControls() {
		this.enableDateComponent.setDate(new Date());
		populateTaxCode();
	}

	private void populateTaxCode() {
		final TaxCodeService taxCodeService = ServiceLocator.getService(ContextIdNames.TAX_CODE_SERVICE);
		final List<TaxCode> originalCodeList = taxCodeService.list();
		this.taxCodeList = new ArrayList<>(originalCodeList.size() + 1);
		addNullTaxCodeOption(taxCodeCombo, taxCodeList);
		this.taxCodeCombo.select(0);
		addNonShippingTaxCodeOptions(originalCodeList, taxCodeCombo, taxCodeList);
	}

	private void addNullTaxCodeOption(final CCombo taxCodeComboBox, final List<TaxCode> taxCodeList) {
		taxCodeList.add(null);
		taxCodeComboBox.add(CatalogMessages.get().ProductSkuCreateDialog_TaxCodeOption_NotApplicable);
	}

	private void addNonShippingTaxCodeOptions(final List<TaxCode> originalTaxCodeList, final CCombo taxCodeComboBox,
			final List<TaxCode> taxCodeList) {
		for (final TaxCode taxCode : originalTaxCodeList) {
			if (!TaxCode.TAX_CODE_SHIPPING.equals(taxCode.getCode())) {
				taxCodeComboBox.add(taxCode.getCode());
				taxCodeList.add(taxCode);
			}
		}
	}

	@Override
	@SuppressWarnings({ "PMD.NPathComplexity", "PMD.ExcessiveMethodLength" })
	protected void bindControls() {
		final EpControlBindingProvider provider = EpControlBindingProvider.getInstance();
		final DataBindingContext context = getDataBindingContext();

		digitalAssetViewPart.bindControls(context);
		productSkuShippingViewPart.bindControls(context);

		skuCodeDecoration = EpControlBindingProvider.getInstance().addControlDecoration(skuCodeText);
		skuCodeDecoration.hide();
		skuCodeDecoration.setShowHover(false);

		skuCodeValueHandler = new ValueUpdateHandler(skuCodeDecoration, false);

		skuCodeText.addFocusListener(new FocusAdapter() {

			@Override
			public void focusLost(final FocusEvent event) {
				final String skuCode = skuCodeText.getText();
				validateSkuCode(skuCode);
			}

		});

		provider.bind(context, skuCodeText, productSku, "skuCode", EpValidatorFactory.SKU_CODE, null, true); //$NON-NLS-1$

		// make sure always disable date > enable date
		final IValidator disableDateAwareValidator = new CompoundValidator(new IValidator[] { EpValidatorFactory.DATE,
				EpValidatorFactory.createDisableDateValidator(enableDateComponent, disableDateComponent) });

		IValidator requiredEnabledDateValidator = new CompoundValidator(new IValidator[] { EpValidatorFactory.DATE_REQUIRED,
				disableDateAwareValidator });

		provider.bind(context, enableDateComponent.getSwtText(), productSku, "startDate", requiredEnabledDateValidator, null, true); //$NON-NLS-1$

		provider.bind(context, disableDateComponent.getSwtText(), productSku, "endDate", disableDateAwareValidator, null, true); //$NON-NLS-1$

		bindTaxCode(context);

		status = new AggregateValidationStatus(context.getBindings(), AggregateValidationStatus.MAX_SEVERITY);
		status.addValueChangeListener(event -> {

			if (digitalAssetDownloadable.getSelection() && !StringUtils.isEmpty(digitalAssetViewPart.getFilenameText())) {
				// transition from downloadable selection(false) to selection(true)
				// check for previous text in file name field to show next button
				digitalAssetDownloadableBinding.getBinding().getValidationStatus().setValue(Status.OK_STATUS);
			}

			setCurrentStatus(event.diff.getNewValue());

			if (getWizard().getContainer().getCurrentPage() != null) {
				getWizard().getContainer().updateButtons();
			}

		});

		provider.bind(context, shippableRadioButton, getProductSku(), "shippable", //$NON-NLS-1$
				value -> {
					if (shippableRadioButton.getSelection() || digitalAssetRadioButton.getSelection()) {
						return Status.OK_STATUS;
					}
					return new Status(IStatus.ERROR, CatalogPlugin.PLUGIN_ID, CatalogMessages.get().ProductEditor_RequireProductShipType);
				},
				null,
				true);

		digitalAssetDownloadableBinding = provider.bind(context, digitalAssetDownloadable, value -> Status.OK_STATUS, null,
				new ObservableUpdateValueStrategy() {
					@Override
					protected IStatus doSet(final IObservableValue observableValue, final Object value) {
						Boolean selected = (Boolean) value;
						if (selected && StringUtils.isEmpty(digitalAssetViewPart.getFilenameText())) {
							// notice this is set on the digitalAssetDownloadableBinding
							// it must consequently be manipulated later to toggle the next status
							return Status.CANCEL_STATUS;
						}
						return Status.OK_STATUS;
					}
				}, false);

		SelectionAdapter digitalSelectionAdapter = new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				digitalAssetOptionSelected(digitalAssetRadioButton.getSelection(), digitalAssetDownloadable.getSelection());
				if (!digitalAssetDownloadable.getSelection()) {
					getProductSku().setDigitalAsset(null);
				}
			}
		};

		SelectionAdapter shippableTypeSelectionAdapter = new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				if (event.getSource() == shippableRadioButton) {
					radioShippableSelected(shippableRadioButton.getSelection());
					digitalAssetDownloadable.setSelection(false);
				} else if (event.getSource() == digitalAssetRadioButton) {
					radioDigitalAssetSelected(digitalAssetRadioButton.getSelection(), digitalAssetDownloadable.getSelection());
				}

				if (!digitalAssetDownloadable.getSelection()) {
					digitalAssetViewPart.getFilenameTextBinding().getBinding().getValidationStatus().setValue(Status.OK_STATUS);
				}

				super.widgetSelected(event);
			}

		};
		shippableRadioButton.addSelectionListener(shippableTypeSelectionAdapter);
		digitalAssetRadioButton.addSelectionListener(shippableTypeSelectionAdapter);

		digitalAssetDownloadable.addSelectionListener(digitalSelectionAdapter);
		digitalAssetRadioButton.addSelectionListener(digitalSelectionAdapter);

		currentStatus = status.getValue();

		EpWizardPageSupport.create(this, context);
	}

	/**
	 * Create a custom update strategy to update the productSku based on the selected taxCode.
	 *
	 * @param bindingContext the binding context
	 */
	private void bindTaxCode(final DataBindingContext bindingContext) {
		final ObservableUpdateValueStrategy taxCodeUpdateStrategy = new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
				return checkAndApplyTaxCode(getProductSku());
			}
		};
		EpControlBindingProvider.getInstance().bind(bindingContext, this.taxCodeCombo, null, null, taxCodeUpdateStrategy, false);
	}

	private IStatus checkAndApplyTaxCode(final ProductSku productSku) {
		final int selectedTaxIndex = taxCodeCombo.getSelectionIndex();
		if (selectedTaxIndex >= 0) {
			final TaxCode selectedTaxCode = taxCodeList.get(selectedTaxIndex);
			productSku.setTaxCodeOverride(selectedTaxCode);
		}
		return Status.OK_STATUS;
	}

	@Override
	public void widgetDefaultSelected(final SelectionEvent event) {
		// not used
	}

	/**
	 * Opens the ProductSkuOptionValueDialog when the "Add Value" button is clicked, and adds any created OptionValue to the product's ProductType.
	 *
	 * @param event the button click event
	 */
	// ---- DOCwidgetSelected
	@Override
	public void widgetSelected(final SelectionEvent event) {
		if (event.getSource() instanceof Button) {
			final Button button = (Button) event.getSource();
			final CCombo combo = (CCombo) button.getData();
			final SkuOption skuOption = (SkuOption) combo.getData();

			final ProductSkuOptionValueDialog dialog = new ProductSkuOptionValueDialog(getShell(), skuOption, null);
			if (dialog.open() == Window.OK) {
				final SkuOptionValue skuOptionValue = dialog.getSkuOptionValue();

				skuOptionValue.setOrdering(skuOption.getMaxOrdering() + 1);
				skuOption.addOptionValue(skuOptionValue);

				// persist the SkuOption so that we have the new SkuOptionValue persisted through cascade
				// SkuOptionValue objects are required to be already persisted before set to a ProductSku
				final SkuOption updatedSkuOption = skuOptionService.saveOrUpdate(skuOption);
				if (changeSetHelper.isChangeSetsEnabled()) {
					updateChangeSetInfo(skuOption, updatedSkuOption);
				}
				// the sku option has to be updated onto the product type
				// because otherwise the product type will hold an old reference to
				// the SkuOption and therefore will try to save again the newly added SkuOption value
				// which would lead to a try to save it twice in the database
				final ProductType productType = product.getProductType();
				productType.addOrUpdateSkuOption(updatedSkuOption);

				addSkuOptionToCombo(skuOptionValue, combo, true);
				combo.setData(updatedSkuOption);

				getDataBindingContext().updateModels();

			}
		}
	}
	// ---- DOCwidgetSelected

	private void updateChangeSetInfo(final SkuOption skuOption, final SkuOption updatedSkuOption) {
		changeSetHelper.addObjectToChangeSet(updatedSkuOption, ChangeSetMemberAction.EDIT);
		Label lockLabel = skuOptionLockIconMap.get(skuOption);
		lockLabel = setLockIcon(updatedSkuOption, lockLabel);
		lockLabel.redraw();
	}

	/**
	 * Sets the properties of the product SKU and sets it to belong to the parent product.
	 */
	private void setSku() {
		productSku.setSkuCode(skuCodeText.getText());
		productSku.setStartDate(enableDateComponent.getDate());
		productSku.setEndDate(disableDateComponent.getDate());
	}

	private IStatus checkSelectedSkuCombinationUnused() {
		// Get the OptionValueKey combination that have been selected in the combo box, and
		// check if we already have that combo
		if (productSkuExistsWithSkuOptionValueKeys(getSkuOptionValueKeyList(skuOptionAndValueMap))) {
			return new Status(IStatus.ERROR, CatalogPlugin.PLUGIN_ID, IStatus.ERROR, CatalogMessages.get().ProductSkuCreateDialog_Sku_Exist, null);
		}
		return Status.OK_STATUS;
	}

	/**
	 * Checks whether a product sku exists with the given combination of sku option value keys.<br>
	 * (i.e. does a sku already exist that is Yellow and size large?)
	 *
	 * @param skuOptionValueKeyCombination the combination of keys to search for (e.g. {YY, LL})
	 * @return true if one exists, false if not.
	 */
	private boolean productSkuExistsWithSkuOptionValueKeys(final List<String> skuOptionValueKeyCombination) {
		ProductSku foundSku = product.findSkuWithOptionValueCodes(skuOptionValueKeyCombination);
		return (foundSku != null);
	}

	/**
	 * Gets the list of SkuOptionValueKeys that have been selected from the combo boxes.
	 *
	 * @param skuOptionAndValueMap the map of combo boxes to SkuOptionValues that is displayed
	 * @return the list of SkuOptionValueKeys that have been selected from the combo boxes.
	 */
	private List<String> getSkuOptionValueKeyList(final Map<CCombo, List<SkuOptionValue>> skuOptionAndValueMap) {
		final List<String> skuOptionValueKeyList = new ArrayList<>();

		for (final CCombo combo : skuOptionAndValueMap.keySet()) {
			if (combo.getSelectionIndex() > 0) {
				final SkuOptionValue skuOptionValue = skuOptionAndValueMap.get(combo).get(combo.getSelectionIndex() - 1);
				skuOptionValueKeyList.add(skuOptionValue.getOptionValueKey());
			}
		}

		Collections.sort(skuOptionValueKeyList);
		return skuOptionValueKeyList;
	}

	/**
	 * Utility method for adding sku option value to a combo box.
	 */
	private void addSkuOptionToCombo(final SkuOptionValue skuOptionValue, final CCombo combo, final boolean focusOnItem) {
		combo.add(skuOptionValue.getOptionValueKey()
				+ " - " + skuOptionValue.getDisplayName(CorePlugin.getDefault().getDefaultLocale(), true)); //$NON-NLS-1$

		skuOptionAndValueMap.get(combo).add(skuOptionValue);

		if (focusOnItem) {
			combo.select(combo.getItemCount() - 1);
		}
	}

	private void updateSkuOptionValue(final CCombo comboBoxSkuValues) {
		final int index = comboBoxSkuValues.getSelectionIndex() - 1; // -1 because of the 'Select...' string in the beginning
		final SkuOption skuOption = (SkuOption) comboBoxSkuValues.getData();
		productSku.setSkuOptionValue(skuOption, skuOptionAndValueMap.get(comboBoxSkuValues).get(index).getOptionValueKey());
	}

	private void bindSkuValueComboBox(final CCombo comboBoxSkuValues) {
		final EpControlBindingProvider provider = EpControlBindingProvider.getInstance();
		final DataBindingContext context = getDataBindingContext();

		provider.bind(context, comboBoxSkuValues, OPTION_VALUE_COMBO_VALIDATOR, null, new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object value) {
				updateSkuOptionValue(comboBoxSkuValues);
				return Status.OK_STATUS;
			}

		}, true);
	}

	private IStatus validateSkuCode(final String skuCode) {
		IStatus skuCodeValidateStatus;
		if (skuCode.length() == 0) {
			skuCodeValidateStatus = new Status(IStatus.ERROR, CatalogPlugin.PLUGIN_ID, IStatus.ERROR,
					CatalogMessages.get().ProductSkuCreateDialog_SkuCode_Required, null);
		} else {
			boolean skuCodeExists = false;

			// zero means no such SKU code
			long skuUid = skuService.findUidBySkuCode(skuCode);
			if (skuUid == 0) {
				// didn't find any existing sku with that code in the database, but we also need to check the skus in the product in case a sku with
				// that code had just been added and not persisted yet
				for (ProductSku productSku : product.getProductSkus().values()) {
					if (productSku.getSkuCode().equals(skuCode)) {
						skuCodeExists = true;
						break;
					}
				}
			} else {
				skuCodeExists = true;
			}

			if (skuCodeExists) {
				skuCodeValidateStatus = new Status(IStatus.ERROR, CatalogPlugin.PLUGIN_ID, IStatus.ERROR,

						NLS.bind(CatalogMessages.get().ProductSkuCreateDialog_SkuCode_Exist,
						skuCode), null);
			} else {
				skuCodeValidateStatus = Status.OK_STATUS;
			}
		}
		skuCodeValueHandler.inputValidated(skuCodeValidateStatus);

		return skuCodeValidateStatus;
	}

	@Override
	public boolean beforeNext(final PageChangingEvent event) {

		// check the data binding validations
		final List<IStatus> errorStatusList = DataBindingUtil.getInstance().getBindingContextErrorStatus(getDataBindingContext());

		// check the combination
		final IStatus skuCombinationStatus = checkSelectedSkuCombinationUnused();
		if (!skuCombinationStatus.isOK()) {
			errorStatusList.add(skuCombinationStatus);
		}

		// check the sku code
		final IStatus skuCodeStatus = validateSkuCode(skuCodeText.getText());
		if (!skuCodeStatus.isOK()) {
			errorStatusList.add(skuCodeStatus);
		}

		if (!errorStatusList.isEmpty()) {
			DataBindingUtil.getInstance().showValidationDialog(getShell(), errorStatusList);
			return false;
		}

		setSku();
		return true;
	}

	@Override
	public boolean canFlipToNextPage() {
		if (getCurrentStatus() == null) {
			return true;
		}
		return getCurrentStatus().isOK();
	}

	private Iterator<SkuOption> retrieveOptions() {
		return product.getProductType().getSkuOptions().iterator();
	}

	@Override
	protected String getTitlePage() {
		return title;
	}

	/**
	 * Get the current binding status.
	 *
	 * @return IStatus
	 */
	public IStatus getCurrentStatus() {
		return currentStatus;
	}

	private void setCurrentStatus(final IStatus currentStatus) {
		this.currentStatus = currentStatus;
	}

	/**
	 * Return the product sku.
	 *
	 * @return product sku
	 */
	public ProductSku getProductSku() {
		return productSku;
	}

	@Override
	public void dispose() {
		super.dispose();

		status.dispose();
		status = null;
	}

	@Override
	public void digitalAssetOptionSelected(final boolean digital, final boolean downloadable) {
		expandableDigitalComposite.setExpanded(downloadable);
		expandableDigitalComposite.setEnabled(downloadable);
		expandableDigitalComposite.layout(true);
		digitalAssetViewPart.setTextValidationEnabled(downloadable);
		productSku.setDigital(digital);
		if (downloadable) {
			productSku.setDigitalAsset(digitalAsset);
			// check validation on the filenameText field
			if (StringUtils.isEmpty(digitalAssetViewPart.getFilenameText())) {
				// need to error on both bindings do to the doSet override on digitalAssetDownloadableBinding
				digitalAssetViewPart.getFilenameTextBinding().getBinding().getValidationStatus().setValue(Status.CANCEL_STATUS);
				digitalAssetDownloadableBinding.getBinding().getValidationStatus().setValue(Status.CANCEL_STATUS);
			}
		} else {
			productSku.setDigitalAsset(null);
			// need to Ok on both bindings do to the doSet override on digitalAssetDownloadableBinding
			digitalAssetViewPart.getFilenameTextBinding().getBinding().getValidationStatus().setValue(Status.OK_STATUS);
			digitalAssetDownloadableBinding.getBinding().getValidationStatus().setValue(Status.OK_STATUS);
		}
		updatePageComplete();
	}

	@Override
	public void shippableOptionSelected(final boolean selected) {
		expandableShippingComposite.setExpanded(selected);
		expandableShippingComposite.setEnabled(selected);
		digitalAssetViewPart.setTextValidationEnabled(false);
		expandableShippingComposite.layout(true);
	}

	@Override
	public void skuCodeChanged(final String skuCodeText) {
		updatePageComplete();
	}

	private void updatePageComplete() {
		final List<IStatus> errorStatusList = new ArrayList<>();
		for (final ValidationStatusProvider provider : (Iterable<ValidationStatusProvider>) getDataBindingContext().getValidationStatusProviders()) {
			final IStatus currStatus = (IStatus) provider.getValidationStatus().getValue();
			if (!currStatus.isOK()) {
				errorStatusList.add(currStatus);
			}
		}
		setPageComplete(errorStatusList.isEmpty());
	}

	private void radioShippableSelected(final boolean selected) {
		shippableOptionSelected(selected);
		if (selected) {
			digitalAssetOptionSelected(false, false);
			digitalAssetDownloadable.setEnabled(false);
		}
	}

	/**
	 * @param digital true if product is digital
	 * @param downloadable true if digital product is downloadable
	 */
	private void radioDigitalAssetSelected(final boolean digital, final boolean downloadable) {
		digitalAssetDownloadable.setEnabled(true);
		digitalAssetDownloadable.setSelection(downloadable);
		digitalAssetOptionSelected(digital, downloadable);
		if (digital) {
			shippableOptionSelected(false);
		}
	}
}