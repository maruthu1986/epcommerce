/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.catalog.wizards.product.create;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.PageChangingEvent;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ExpandableComposite;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.catalog.CatalogPlugin;
import com.elasticpath.cmclient.catalog.editors.product.StoreRulesViewPart;
import com.elasticpath.cmclient.core.CorePlugin;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.EpWizardPageSupport;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.dto.catalog.ProductModel;
import com.elasticpath.cmclient.core.helpers.BrandComparator;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.validation.CompoundValidator;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.cmclient.core.wizard.AbstractEPWizardPage;
import com.elasticpath.cmclient.policy.common.AbstractStatePolicyImpl;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.catalog.LocaleDependantFields;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.service.catalog.BrandService;
import com.elasticpath.service.catalog.ProductService;
import com.elasticpath.service.catalog.ProductTypeService;
import com.elasticpath.service.tax.TaxCodeService;

/**
 */
@SuppressWarnings({"PMD.GodClass"})
public class ProductDetailsWizardPage1 extends AbstractEPWizardPage<ProductModel> { // NOPMD

	private static final int PAGE_LAYOUT_NUM_COLUMNS = 1;

	private CCombo productTypeCombo;

	private CCombo bundlePricing;

	private CCombo taxCodeCombo;

	private CCombo brandCombo;

	private StoreRulesViewPart storeRulesViewPart;

	private List<TaxCode> taxCodeList;

	private List<Brand> brandList;

	private List<ProductType> productList;

	private Text productCodeText;

	private Text productNameText;

	private final ProductService productService;

	private List<Locale> localeList;

	/**
	 * Page ID.
	 */
	protected static final String PRODUCT_DETAILS_WIZARD_PAGE1 = "ProductDetailsWizardPage1";  //$NON-NLS-1$

	/**
	 * Constructor.
	 *
	 * @param pageName    the page name
	 * @param title       the page title
	 * @param description the page description
	 */
	protected ProductDetailsWizardPage1(final String pageName, final String title, final String description) {
		super(PAGE_LAYOUT_NUM_COLUMNS, true, pageName, new DataBindingContext());

		setDescription(description);
		setTitle(title);
		productService = ServiceLocator.getService(ContextIdNames.PRODUCT_SERVICE);
	}

	@Override
	protected void createEpPageContent(final IEpLayoutComposite pageComposite) {
		final IEpLayoutComposite container = pageComposite.addScrolledGridLayoutComposite(1, true);
		container.setLayoutData(new GridData(GridData.FILL_BOTH));

		createOverviewSectionComposite(container);
		createStoreRulesSectionComposite(container);
		setControl(pageComposite.getSwtComposite());
	}

	/**
	 */
	private void createOverviewSectionComposite(final IEpLayoutComposite container) {
		//create section composite
		final IEpLayoutComposite overviewComposite = container.addGridLayoutSection(3, CatalogMessages.get().CreateProductWizard_Overview,
				ExpandableComposite.TITLE_BAR, container.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false));

		final IEpLayoutData labelData = overviewComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL);
		//final IEpLayoutData fieldData = overviewComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false);
		final IEpLayoutData fieldData2 = overviewComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false, 2, 1);

		//add fields
		overviewComposite.addLabelBold(CatalogMessages.get().CreateProductWizard_PrimaryCategory, labelData);
		final String selectedCategory = getProduct().getDefaultCategory(getProduct().getMasterCatalog()).
				getDisplayName(CorePlugin.getDefault().getDefaultLocale());
		overviewComposite.addLabel(selectedCategory, fieldData2);

		overviewComposite.addLabelBoldRequired(CatalogMessages.get().ProductDetailsPage_ProductCode, EpState.EDITABLE, labelData);
		productCodeText = overviewComposite.addTextField(EpState.EDITABLE, fieldData2);

		// product name
		overviewComposite.addLabelBoldRequired(CatalogMessages.get().ProductDetailsPage_ProductName, EpState.EDITABLE, labelData);
		//languageCombo = overviewComposite.addComboBox(EpState.EDITABLE, null);
		productNameText = overviewComposite.addTextField(EpState.EDITABLE, fieldData2);

		if (isProductBundle()) {
			// bundle pricing
			overviewComposite.addLabelBoldRequired(CatalogMessages.get().ProductDetailsPage_BundlePricing, EpState.EDITABLE, labelData);
			bundlePricing = overviewComposite.addComboBox(EpState.EDITABLE, fieldData2);
			bundlePricing.add(CatalogMessages.get().ProductDetailsPage_BundlePricing_Assigned);
			bundlePricing.add(CatalogMessages.get().ProductDetailsPage_BundlePricing_Calculated);
			bundlePricing.select(0);
			bundlePricing.setEditable(false);
			bundlePricing.setToolTipText(CatalogMessages.get().ProductDetailsPage_BundlePricing_ToolTip);
		}

		// product type
		overviewComposite.addLabelBoldRequired(CatalogMessages.get().ProductDetailsPage_ProductType, EpState.EDITABLE, labelData);
		productTypeCombo = overviewComposite.addComboBox(EpState.EDITABLE, fieldData2);
		productTypeCombo.add(CatalogMessages.get().CreateProductWizard_SelectProductType);
		productTypeCombo.select(0);
		productTypeCombo.setEditable(false);

		if (!isProductBundle()) {
			// tax code
			overviewComposite.addLabelBoldRequired(CatalogMessages.get().ProductDetailsPage_TaxCode, EpState.EDITABLE, labelData);
			taxCodeCombo = overviewComposite.addComboBox(EpState.EDITABLE, fieldData2);
			taxCodeCombo.add(CatalogMessages.get().CreateProductWizard_SelectTaxCode);
			taxCodeCombo.select(0);
			taxCodeCombo.setEditable(false);
		}

		// brand
		overviewComposite.addLabelBoldRequired(CatalogMessages.get().ProductDetailsPage_Brand, EpState.EDITABLE, labelData);
		brandCombo = overviewComposite.addComboBox(EpState.EDITABLE, fieldData2);
		brandCombo.add(CatalogMessages.get().CreateProductWizard_SelectBrand);
		brandCombo.select(0);
		brandCombo.setEditable(false);
	}

	/**
	 */
	private void createStoreRulesSectionComposite(final IEpLayoutComposite container) {
		final IEpLayoutComposite storeRulesComposite = container.addGridLayoutSection(1,
				CatalogMessages.get().CreateProductWizard_StoreRules,
				CatalogMessages.get().ProductEditorStoreRuleSection_AllStoresMessage,
				ExpandableComposite.TITLE_BAR, container.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false));

		storeRulesViewPart = new StoreRulesViewPart(getProduct(), true);
		storeRulesViewPart.createControls(storeRulesComposite,
				storeRulesComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false));

		storeRulesViewPart.applyStatePolicy(new AbstractStatePolicyImpl() {
			@Override
			public EpState determineState(final PolicyActionContainer targetContainer) {
				return EpState.EDITABLE;
			}

			@Override
			public void init(final Object dependentObject) {
				// not applicable
			}
		});

	}

	@Override
	protected void populateControls() {
		populateProductTypes();
		populateBundlePricing();
		populateTaxCode();
		populateBrand();
		storeRulesViewPart.populateForCreateProductWizard();

		localeList = new ArrayList<>();

		for (final Locale currLocale : getProduct().getMasterCatalog().getSupportedLocales()) {
			localeList.add(currLocale);
		}
	}

	/**
	 */
	private void populateTaxCode() {
		if (isProductBundle()) {
			return;
		}

		// Populate taxCode combo
		final TaxCodeService taxCodeService =
				ServiceLocator.getService(ContextIdNames.TAX_CODE_SERVICE);
		final List<TaxCode> codeList = taxCodeService.list();
		this.taxCodeList = new ArrayList<>();
		for (final TaxCode currTaxCode : codeList) {
			if (!TaxCode.TAX_CODE_SHIPPING.equals(currTaxCode.getCode())) {
				this.taxCodeCombo.add(currTaxCode.getCode());
				this.taxCodeList.add(currTaxCode);
			}
		}
	}

	/**
	 */
	private void populateProductTypes() {
		final ProductTypeService productTypeService =
				ServiceLocator.getService(ContextIdNames.PRODUCT_TYPE_SERVICE);
		final long catalogUidPk = getProduct().getMasterCatalog().getUidPk();

		if (isProductBundle()) {
			productList = new ArrayList<>();
			for (final ProductType currProductType : productTypeService.findAllProductTypeFromCatalog(catalogUidPk)) {
				if (!currProductType.isMultiSku()) {
					productList.add(currProductType);
				}
			}
		} else {
			productList = productTypeService.findAllProductTypeFromCatalog(catalogUidPk);
		}

		for (final ProductType currProductType : productList) {
			this.productTypeCombo.add(currProductType.getName());
		}
	}

	/**
	 */
	private void populateBundlePricing() {
		// Nothing to do.
	}

	/**
	 */
	private void populateBrand() {
		// Populate brand combo
		final BrandService brandService =
				ServiceLocator.getService(ContextIdNames.BRAND_SERVICE);
		this.brandList = brandService.findAllBrandsFromCatalog(getProduct().getMasterCatalog().getUidPk());

		Collections.sort(brandList, new BrandComparator(CorePlugin.getDefault().getDefaultLocale()));
		for (final Brand brand : brandList) {
			String displayName = String.valueOf(brand.getDisplayName(CorePlugin.getDefault().getDefaultLocale(), true));
			brandCombo.setData(displayName, brand);
			brandCombo.add(displayName);
		}
	}

	@Override
	public boolean beforeNext(final PageChangingEvent event) {
		Locale categoryDefaultLocale = getProduct().getMasterCatalog().getDefaultLocale();

		LocaleDependantFields ldf = getProduct().getLocaleDependantFieldsWithoutFallBack(categoryDefaultLocale);
		if (ldf == null || ldf.getDisplayName() == null) {
			setErrorMessage(
				NLS.bind(CatalogMessages.get().ProductDetailsPage_DisplayNameRequired,
				categoryDefaultLocale.getDisplayName()));
			return false;
		}

		setErrorMessage(null);
		return true;
	}

	// BINDINGS

	/**
	 */
	@Override
	protected void bindControls() {
		bindProductCode();
		bindProductName();
		bindBundlePricing();
		bindProductType();
		bindTaxCode();
		bindBrand();
		storeRulesViewPart.bindControls(getDataBindingContext());
		EpWizardPageSupport.create(this, getDataBindingContext());
	}

	/**
	 */
	private void bindProductCode() {
		// Product Code	
		EpControlBindingProvider.getInstance().bind(getDataBindingContext(), this.productCodeText, getProduct(), "code",  //$NON-NLS-1$
				new CompoundValidator(EpValidatorFactory.REQUIRED, EpValidatorFactory.PRODUCT_CODE,
						value -> {
							final String stringValue = (String) value;
							// check unique
							if (getProductService().guidExists(stringValue)) {
								return new Status(
										IStatus.ERROR,
										CatalogPlugin.PLUGIN_ID,
										IStatus.ERROR,
										CatalogMessages.get().ProductEditorSummaySection_Duplicate_Code,
										null);
							}
							return Status.OK_STATUS;
						}), null, true);
	}

	/**
	 */
	private void bindProductName() {
		// Product Name -- Bind the text box to the control using the custom update strategy
		final ObservableUpdateValueStrategy productNameUpdateStrategy = new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
				getProduct().setDisplayName((String) newValue, getProduct().getMasterCatalog().getDefaultLocale());
				return Status.OK_STATUS;
			}
		};

		EpControlBindingProvider.getInstance().bind(getDataBindingContext(), this.productNameText,
				new CompoundValidator(EpValidatorFactory.STRING_255_REQUIRED), null, productNameUpdateStrategy, true);
	}

	/**
	 */
	private void bindBundlePricing() {
		if (isProductBundle()) {
			// Bundle Pricing -- Bind the dropdown to the control using the custom update strategy
			final ObservableUpdateValueStrategy bundlePricingUpdateStrategy = new ObservableUpdateValueStrategy() {
				@Override
				protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
					if (isProductBundle()) {
						Integer value = (Integer) newValue;
						if (value >= 0) {

							if (bundlePricing.getItem(value).equals(CatalogMessages.get().ProductDetailsPage_BundlePricing_Calculated)) {
								((ProductBundle) getProduct()).setCalculated(true);
							} else {
								((ProductBundle) getProduct()).setCalculated(false);
							}
						}
					}
					return Status.OK_STATUS;
				}
			};

			EpControlBindingProvider.getInstance().bind(getDataBindingContext(), this.bundlePricing,
					EpValidatorFactory.REQUIRED, null, bundlePricingUpdateStrategy, true);
		}
	}

	/**
	 */
	private void bindProductType() {
		// Product Type -- Bind the text box to the control using the custom update strategy
		final ObservableUpdateValueStrategy productTypeUpdateStrategy = new ObservableUpdateValueStrategy() {

			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
				final int selectedProductType = productTypeCombo.getSelectionIndex();
				if (selectedProductType > 0) {
					final ProductType selectedType = productList.get(selectedProductType - 1);
					getProduct().setProductType(selectedType);
					return Status.OK_STATUS;
				}
				return new Status(IStatus.ERROR, CatalogPlugin.PLUGIN_ID, CatalogMessages.get().ProductEditorSummaySection_Duplicate_Code);
			}
		};
		EpControlBindingProvider.getInstance().bind(getDataBindingContext(), this.productTypeCombo,
				EpValidatorFactory.REQUIRED_COMBO_FIRST_ELEMENT_NOT_VALID, null,
				productTypeUpdateStrategy, true);
	}

	/**
	 */
	private void bindTaxCode() {
		if (isProductBundle()) {
			return;
		}

		// TaxCode -- Create a custom update strategy to update the product based on the selected taxCode
		final ObservableUpdateValueStrategy taxCodeUpdateStrategy = new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
				final int selectedTaxIndex = taxCodeCombo.getSelectionIndex();
				if (selectedTaxIndex > 0) {
					final TaxCode selectedTaxCode = taxCodeList.get(selectedTaxIndex - 1);
					getProduct().setTaxCodeOverride(selectedTaxCode);
					return Status.OK_STATUS;
				}
				return new Status(IStatus.ERROR, CatalogPlugin.PLUGIN_ID, CatalogMessages.get().ProductEditorSummaySection_Duplicate_Code);
			}
		};
		EpControlBindingProvider.getInstance().bind(getDataBindingContext(), this.taxCodeCombo,
				EpValidatorFactory.REQUIRED_COMBO_FIRST_ELEMENT_NOT_VALID, null,
				taxCodeUpdateStrategy, true);
	}

	/**
	 */
	private void bindBrand() {
		// Brand -- Create a custom update strategy to update the product based on the selected brand
		final ObservableUpdateValueStrategy brandUpdateStrategy = new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
				final int selectedBrandIndex = brandCombo.getSelectionIndex();
				if (selectedBrandIndex > 0) {
					final Brand selectedBrand = brandList.get(selectedBrandIndex - 1);
					getProduct().setBrand(selectedBrand);
					return Status.OK_STATUS;
				}
				return new Status(IStatus.ERROR, CatalogPlugin.PLUGIN_ID, CatalogMessages.get().ProductEditorSummaySection_Duplicate_Code);
			}
		};
		EpControlBindingProvider.getInstance().bind(getDataBindingContext(), this.brandCombo,
				EpValidatorFactory.REQUIRED_COMBO_FIRST_ELEMENT_NOT_VALID, null,
				brandUpdateStrategy, true);
	}

	/**
	 * @return the product service.
	 */
	public ProductService getProductService() {
		return productService;
	}

	/**
	 */
	private Product getProduct() {
		return getModel().getProduct();
	}

	/**
	 */
	private boolean isProductBundle() {
		return getProduct() instanceof ProductBundle;
	}
}
