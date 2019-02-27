/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.catalog.editors.sku;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.TableWrapData;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.catalog.CatalogPlugin;
import com.elasticpath.cmclient.catalog.editors.product.ProductEditor;
import com.elasticpath.cmclient.catalog.wizards.product.create.RepeatableDelayedTask;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.CorePlugin;
import com.elasticpath.cmclient.core.EpUiException;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.EpValueBinding;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.editors.GuidEditorInput;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.ControlModificationListener;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpDateTimePicker;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.validation.CompoundValidator;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.cmclient.policy.StatePolicy;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.IPolicyTargetLayoutComposite;
import com.elasticpath.cmclient.policy.ui.PolicyTargetCompositeFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.service.tax.TaxCodeService;

/**
 * Product SKU overview view part for the UI controls.
 */
@SuppressWarnings({ "PMD.CyclomaticComplexity", "PMD.GodClass" })
public class ProductSkuOverviewViewPart extends AbstractProductSkuOverviewViewPart implements IEpSkuOverviewViewPart {

	private static final Logger LOG = Logger.getLogger(ProductSkuOverviewViewPart.class);
	private static final int DIGITAL_ASSET_DOWNLOADABLE_BOX_WIDTH = 113;

	private IPolicyTargetLayoutComposite leftLayout;

	private Text parentProductText;

	private Text skuConfText;

	private Text skuCodeText;

	private IEpDateTimePicker enableDateTimeComp;

	private IEpDateTimePicker disableDateTimeComp;

	private CCombo taxCodeCombo;

	private Button shippableRadioButton;

	private Button digitalAssetRadioButton;

	private Button digitalAssetDownloadable;

	private Button openParentProductButton;

	private List<TaxCode> taxCodeList;

	private final RepeatableDelayedTask skuCodeValidationDelayedScheduler = new RepeatableDelayedTask(
			new Runnable() {
				@Override
				public void run() {
					getSkuCodeBinding().getBinding().updateTargetToModel();
					getProductSkuEventListener().skuCodeChanged(skuCodeText.getText());
				}
			}, VALIDATION_DELAY_MILLIS);

	/**
	 * Constructs the view part.
	 *
	 * @param productSku the product sku
	 * @param eventListener the event listener for specific product SKU overview view part events
	 * @param checkCodeOnKeyStroke should the SKU code check be enabled on each keystroke
	 */
	public ProductSkuOverviewViewPart(final ProductSku productSku,
			final IProductSkuEventListener eventListener, final boolean checkCodeOnKeyStroke) {
		super(productSku, eventListener, checkCodeOnKeyStroke);
	}

	/**
	 * Constructs the view part.
	 *
	 * @param productSku the product sku
	 * @param eventListener the event listener for specific product SKU overview view part events
	 */
	public ProductSkuOverviewViewPart(final ProductSku productSku,
			final IProductSkuEventListener eventListener) {
		super(productSku, eventListener);
	}

	@Override
	public void bindControls(final DataBindingContext bindingContext) {
		final EpControlBindingProvider provider = EpControlBindingProvider.getInstance();

		final IValidator compoundSkuValidator = new CompoundValidator(new IValidator[] { EpValidatorFactory.SKU_CODE, getSkuValidator() });

		EpValueBinding binding = provider.bind(bindingContext, skuCodeText, compoundSkuValidator, null,
				new SkuValidationUpdateValueStrategy(getProductSku()), true);
		setSkuCodeBinding(binding);
		skuCodeText.addModifyListener(this);

		binding.getBinding().validateTargetToModel();

		if (isSkuPartOfMultiSkuProduct()) {
			bindStartAndEndDate(bindingContext);
			bindTaxCode(bindingContext);
		}

		provider.bind(bindingContext, shippableRadioButton, getProductSku(), "shippable", //$NON-NLS-1$
				value -> {
					if (shippableRadioButton.getSelection()
							|| digitalAssetRadioButton.getSelection()) {
						return Status.OK_STATUS;
					}
					return new Status(IStatus.ERROR, CatalogPlugin.PLUGIN_ID, CatalogMessages.get().ProductEditor_RequireProductShipType);
				}, null, true);

		SelectionAdapter digitalSelectionAdapter = new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				getProductSkuEventListener().digitalAssetOptionSelected(digitalAssetRadioButton.getSelection(),
						digitalAssetDownloadable.getSelection());
				if (getControlModificationListener() != null) {
					getControlModificationListener().controlModified();
				}
				if (!digitalAssetDownloadable.getSelection()) {
					getProductSku().setDigitalAsset(null);
				}
			}
		};

		digitalAssetDownloadable.addSelectionListener(digitalSelectionAdapter);
		digitalAssetRadioButton.addSelectionListener(digitalSelectionAdapter);
	}

	/**
	 * Get the downloadable button selection status.
	 *
	 * @return the selection state of the downloadable button
	 */
	public boolean isDownloadableButtonSelected() {
		return digitalAssetDownloadable.getSelection();
	}

	/**
	 * Binds the Enable and Disable date time pickers.
	 *
	 * @param bindingContext bindingContext
	 */
	private void bindStartAndEndDate(final DataBindingContext bindingContext) {
		// Enable date
		enableDateTimeComp.bind(bindingContext, EpValidatorFactory.DATE_TIME_REQUIRED, getProductSku(), "startDate"); //$NON-NLS-1$

		// from-to date interbinding for from before to date validation
		final ModifyListener updateModels = (ModifyListener) event -> {
			bindingContext.updateModels(); // re-validate bound events
		};
		enableDateTimeComp.getSwtText().addModifyListener(updateModels);
		disableDateTimeComp.getSwtText().addModifyListener(updateModels);

		// Disable Date
		IValidator disableDateValidator = new CompoundValidator(new IValidator[] { EpValidatorFactory.DATE_TIME,
				EpValidatorFactory.createDisableDateValidator(enableDateTimeComp, disableDateTimeComp) });
		disableDateTimeComp.bind(bindingContext, disableDateValidator, getProductSku(), "endDate"); //$NON-NLS-1$
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
	public void setControlModificationListener(final ControlModificationListener listener) {
		super.setControlModificationListener(listener);
		leftLayout.setControlModificationListener(listener);
	}

	@Override
	public void createControls(final IEpLayoutComposite mainPane, final IEpLayoutData data) {
		super.createControls(mainPane, data);

		PolicyActionContainer codeControls = addPolicyActionContainer("guid"); //$NON-NLS-1$
		PolicyActionContainer skuControls = addPolicyActionContainer("skuControls"); //$NON-NLS-1$
		PolicyActionContainer relatedControls = addPolicyActionContainer("skuRelatedControls"); //$NON-NLS-1$
		PolicyActionContainer layoutControls = addPolicyActionContainer("layoutControls"); //$NON-NLS-1$

		final IEpLayoutData labelData = getOverviewSectionComposite().createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL);
		final IEpLayoutData fieldData = getOverviewSectionComposite().createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false);
		final IEpLayoutData radioData = getOverviewSectionComposite().createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.FILL, true, false);

		final IPolicyTargetLayoutComposite mainEpComposite = PolicyTargetCompositeFactory.wrapLayoutComposite(
				CompositeFactory.createTableWrapLayoutComposite(mainPane.getSwtComposite(), 2, false));

		leftLayout = mainEpComposite.addTableWrapLayoutComposite(2, false, null, layoutControls);
		final IPolicyTargetLayoutComposite rightLayout = mainEpComposite.addTableWrapLayoutComposite(1, false, null, layoutControls);

		final TableWrapData mainLayoutData = new TableWrapData(TableWrapData.FILL_GRAB);
		mainLayoutData.valign = TableWrapData.BOTTOM;
		mainLayoutData.grabHorizontal = true;
		mainLayoutData.grabVertical = true;
		leftLayout.setLayoutData(mainLayoutData);

		if (isSkuPartOfMultiSkuProduct()) {
			// row #1
			leftLayout.addLabelBold(CatalogMessages.get().ProductEditorSingleSkuOverview_ParentProduct, labelData, relatedControls);
			parentProductText = leftLayout.addTextField(fieldData, relatedControls);
			final IEpLayoutData buttonData = getOverviewSectionComposite().
			createLayoutData(IEpLayoutData.FILL, IEpLayoutData.BEGINNING, true, false, 1, 5);
			openParentProductButton = rightLayout.getLayoutComposite().addPushButton(CatalogMessages.get().MultipleSku_OpenParentProduct,
			CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_PRODUCT), EpState.EDITABLE, buttonData);
			openParentProductButton.addSelectionListener(this);

			// row #2
			leftLayout.addLabelBold(CatalogMessages.get().ProductEditorSingleSkuOverview_SkuConfiguration, labelData, relatedControls);
			skuConfText = leftLayout.addTextField(fieldData, relatedControls);
		}

		// row #3
		leftLayout.addLabelBold(CatalogMessages.get().ProductEditorSingleSkuOverview_SkuCode, labelData, codeControls);
		skuCodeText = leftLayout.addTextField(fieldData, codeControls);

		if (isSkuPartOfMultiSkuProduct()) {
			// row #4
			leftLayout.addLabelBold(CatalogMessages.get().ProductEditorSingleSkuOverview_EnableDateTime, labelData, skuControls);
			enableDateTimeComp = leftLayout.addDateTimeComponent(IEpDateTimePicker.STYLE_DATE_AND_TIME, null, skuControls);

			// row #5
			leftLayout.addLabelBold(CatalogMessages.get().ProductEditorSingleSkuOverview_DisableDateTime, labelData, skuControls);
			disableDateTimeComp = leftLayout.addDateTimeComponent(IEpDateTimePicker.STYLE_DATE_AND_TIME, null, skuControls);

			// row #6
			leftLayout.addLabelBold(CatalogMessages.get().ProductEditorSingleSkuOverview_TaxCode, labelData, skuControls);
			taxCodeCombo = leftLayout.addComboBox(fieldData, skuControls);
		}

		// row #7
		leftLayout.addLabelBold(CatalogMessages.get().ProductEditorSingleSkuOverview_ShippableType, labelData, skuControls);
		final IPolicyTargetLayoutComposite composite = leftLayout.addGridLayoutComposite(3, true, data, skuControls);
		shippableRadioButton = composite.addRadioButton(CatalogMessages.get().ProductEditorSingleSkuOverview_Shippable, radioData, skuControls);
		shippableRadioButton.addSelectionListener(this);
		shippableRadioButton.setSelection(true);

		digitalAssetRadioButton = composite.addRadioButton(CatalogMessages.get().ProductEditorSingleSkuOverview_DigitalAsset,
				radioData, skuControls);
		digitalAssetDownloadable = composite.addCheckBoxButton(CatalogMessages.get().ProductEditorSingleSkuOverview_DigitalAssetDownloadable,
				radioData, skuControls);
		((GridData) digitalAssetDownloadable.getLayoutData()).widthHint = DIGITAL_ASSET_DOWNLOADABLE_BOX_WIDTH;
		digitalAssetDownloadable.setEnabled(false);
		digitalAssetRadioButton.addSelectionListener(this);
	}

	private boolean isSkuPartOfMultiSkuProduct() {
		return getProductSku() != null && getProductSku().getProduct() != null && getProductSku().getProduct().hasMultipleSkus();
	}

	@Override
	public ProductSku getModel() {
		return super.getProductSku();
	}

	@Override
	public void populateControls() {
		if (isSkuPartOfMultiSkuProduct()) {
			parentProductText.setText(getProductDisplayName());
			final String skuDisplayName = getProductSku().getDisplayName(CorePlugin.getDefault().getDefaultLocale());
			skuConfText.setText(skuDisplayName);
			enableDateTimeComp.setDate(getProductSku().getStartDate());
			disableDateTimeComp.setDate(getProductSku().getEndDate());
		}
		skuCodeText.setText(getProductSku().getSkuCode());

		determineShippableOption();

		populateTaxCode();
	}

	private void populateTaxCode() {
		if (taxCodeCombo != null) {
			final TaxCodeService taxCodeService = ServiceLocator.getService(ContextIdNames.TAX_CODE_SERVICE);
			final List<TaxCode> originalCodeList = taxCodeService.list();
			this.taxCodeList = new ArrayList<>(originalCodeList.size());
	
			addNullTaxCodeOption(taxCodeCombo, taxCodeList);
			addNonShippingTaxCodeOptions(originalCodeList, taxCodeCombo, taxCodeList);
			selectTaxCode(getProductSku().getTaxCodeOverride(), taxCodeCombo, taxCodeList);
		}
	}

	private void addNullTaxCodeOption(final CCombo taxCodeComboBox, final List<TaxCode> taxCodeList) {
		taxCodeList.add(null);
		taxCodeComboBox.add(CatalogMessages.get().ProductEditorSingleSkuOverview_TaxCodeOption_NotApplicable);
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

	private void selectTaxCode(final TaxCode taxCode, final CCombo taxCodeComboBox, final List<TaxCode> taxCodeList) {
		int taxCodeIndex = taxCodeList.indexOf(taxCode);
		if (taxCodeIndex < 0) {
			taxCodeIndex = 0;
		}
		taxCodeComboBox.select(taxCodeIndex);
	}

	private String getProductDisplayName() {
		String displayName = getProductSku().getProduct().getDisplayName(CorePlugin.getDefault().getDefaultLocale());

		//The domain level display name fall back strategy will not work (by design)
		//When the display name is null
		//get display name with the master catalog default locale
		if (displayName == null) {
			Locale defaultLocale = getProductSku().getProduct().getMasterCatalog().getDefaultLocale();
			displayName = getProductSku().getProduct().getDisplayName(defaultLocale);
		}
		return displayName;
	}

	private void determineShippableOption() {
		if (getProductSku().isShippable()) {
			shippableRadioButton.setSelection(true);
			digitalAssetRadioButton.setSelection(false);
			radioShippableSelected(true);
		} else if (getProductSku().isDigital()) {
			digitalAssetRadioButton.setSelection(true);
			shippableRadioButton.setSelection(false);
			radioDigitalAssetSelected(true, getProductSku().isDownloadable());
		}
	}

	@Override
	public void widgetDefaultSelected(final SelectionEvent event) {
		// not used
	}

	@Override
	public void widgetSelected(final SelectionEvent event) {
		if (event.getSource() == shippableRadioButton) {
			radioShippableSelected(shippableRadioButton.getSelection());
			digitalAssetDownloadable.setSelection(false);
		} else if (event.getSource() == digitalAssetRadioButton) {
			radioDigitalAssetSelected(
					digitalAssetRadioButton.getSelection(),
					digitalAssetDownloadable.getSelection()
					);
		} else if (event.getSource() == openParentProductButton) {
			openProductEditor();
			return;
		}
		super.widgetSelected(event);
	}

	private void openProductEditor() {
		Product product = getProductSku().getProduct();
		final IEditorInput editorInput = new GuidEditorInput(product.getGuid(), Product.class);

		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.openEditor(editorInput, ProductEditor.PART_ID);
		} catch (final PartInitException e) {
			// Log the error and throw an unchecked exception
			LOG.error(e.getStackTrace());
			throw new EpUiException("Could not create Product Editor", e); //$NON-NLS-1$
		}
	}

	private void radioShippableSelected(final boolean selected) {
		getProductSkuEventListener().shippableOptionSelected(selected);
		if (selected) {
			getProductSkuEventListener().digitalAssetOptionSelected(false, false);
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
		getProductSkuEventListener().digitalAssetOptionSelected(digital, downloadable);
		if (digital) {
			getProductSkuEventListener().shippableOptionSelected(false);
		}
	}

	/**
	 * Updates the text fields.
	 *
	 * @param locale the locale to be used
	 */
	public void updateTextFields(final Locale locale) {
		if (getProductSku().getProduct().hasMultipleSkus()) {
			String productName = getProductSku().getProduct().getDisplayName(locale);
			if (productName != null) {
				parentProductText.setText(productName);
			}
			final String skuDisplayName = getProductSku().getDisplayName(locale);
			if (skuDisplayName != null) {
				skuConfText.setText(skuDisplayName);
			}
		}
	}

	@Override
	public void modifyText(final ModifyEvent event) {
		if (event.getSource() == skuCodeText) {
			getSkuCodeBinding().getBinding().getValidationStatus().setValue(Status.CANCEL_STATUS);
			getProductSkuEventListener().skuCodeChanged(skuCodeText.getText());

			skuCodeValidationDelayedScheduler.schedule();

			if (getControlModificationListener() != null) {
				getControlModificationListener().controlModified();
			}
		}
	}

	@Override
	public void applyStatePolicy(final StatePolicy policy) {
		super.applyStatePolicy(policy);
		boolean state = digitalAssetDownloadable.getEnabled() && digitalAssetRadioButton.getSelection();
		digitalAssetDownloadable.setEnabled(state);
	}

	/**
	 * Get the text in the sku code text box in this control.
	 *
	 * @return the text from the sku code text box.
	 */
	public String getSkuCodeText() {
		return skuCodeText.getText();
	}

	/**
	 * Check the validation status of the control.
	 *
	 * @return the validation status of the control
	 */
	public boolean isStatusOk() {
		if (getSkuCodeBinding().getBinding().getValidationStatus().getValue() instanceof IStatus) {
			return ((IStatus) (getSkuCodeBinding().getBinding().getValidationStatus().getValue())).isOK();
		}
		return false;
	}

}
