/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.catalog.editors.product;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

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

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.catalog.CatalogPlugin;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.helpers.BrandComparator;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.ControlModificationListener;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareEditorPageSectionPart;
import com.elasticpath.cmclient.policy.ui.IPolicyTargetLayoutComposite;
import com.elasticpath.cmclient.policy.ui.PolicyTargetCompositeFactory;
import com.elasticpath.cmclient.core.dto.catalog.ProductModel;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.service.catalog.BrandService;
import com.elasticpath.service.catalog.ProductService;
import com.elasticpath.service.tax.TaxCodeRetriever;
import com.elasticpath.service.tax.TaxCodeService;

/**
 * This class implements the section of the Product editor that displays basic product information/details.
 */
public class ProductEditorOverviewSection extends AbstractPolicyAwareEditorPageSectionPart {

	private List<Brand> brandList;

	// Controls
	private Text productCodeText;

	private Text productNameText;

	private Text productTypeText;

	private Text bundlePricing;

	private CCombo taxCodeCombo;

	private CCombo brandCombo;

	private IPolicyTargetLayoutComposite controlPane;

	private final ControlModificationListener controlModificationListener;

	private final FormPage formPage;

	private static final String ORI_PRODUCT_CODE = "oriProductCode"; //$NON-NLS-1$

	private final ProductService productService = (ProductService) ServiceLocator.getService(ContextIdNames.PRODUCT_SERVICE);

	private List<TaxCode> taxCodeList;

	/**
	 * Constructor.
	 *
	 * @param formPage the Eclipse FormPage
	 * @param editor   the editor where the detail section will be placed
	 */
	public ProductEditorOverviewSection(final FormPage formPage, final AbstractCmClientFormEditor editor) {
		super(formPage, editor, ExpandableComposite.TITLE_BAR | ExpandableComposite.EXPANDED);
		this.formPage = formPage;
		this.controlModificationListener = editor;
	}

	@Override
	protected String getSectionTitle() {
		return CatalogMessages.get().ProductEditorSummaySection_Title;
	}

	@Override
	protected void createControls(final Composite parent, final FormToolkit toolkit) {
		PolicyActionContainer codeControls = addPolicyActionContainer("guid"); //$NON-NLS-1$
		PolicyActionContainer productControls = addPolicyActionContainer("productControls"); //$NON-NLS-1$
		PolicyActionContainer relatedControls = addPolicyActionContainer("relatedControls"); //$NON-NLS-1$

		this.controlPane = PolicyTargetCompositeFactory.wrapLayoutComposite(
				CompositeFactory.createTableWrapLayoutComposite(parent, 2, false));
		final IEpLayoutData labelData = this.controlPane.createLayoutData(IEpLayoutData.END, IEpLayoutData.CENTER);
		final IEpLayoutData fieldData = this.controlPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.BEGINNING);

		this.controlPane.addLabelBoldRequired(CatalogMessages.get().ProductEditorSummaySection_ProductCode, labelData, codeControls);
		this.productCodeText = this.controlPane.addTextField(fieldData, codeControls);

		if (isProductBundle()) {
			this.controlPane.addLabelBoldRequired(CatalogMessages.get().ProductDetailsPage_BundlePricing, labelData, codeControls);
			this.bundlePricing = this.controlPane.addTextField(fieldData, codeControls);
			this.bundlePricing.setToolTipText(CatalogMessages.get().ProductDetailsPage_BundlePricing_ToolTip);
		}

		this.controlPane.addLabelBoldRequired(CatalogMessages.get().ProductEditorSummaySection_ProductName, labelData, productControls);
		this.productNameText = this.controlPane.addTextField(fieldData, productControls);

		this.controlPane.addLabelBold(CatalogMessages.get().ProductEditorSummaySection_ProductType, labelData, relatedControls);
		this.productTypeText = this.controlPane.addTextField(fieldData, relatedControls);

		if (!isProductBundle()) {
			this.controlPane.addLabelBold(CatalogMessages.get().ProductEditorSummaySection_TaxCode, labelData, productControls);
			this.taxCodeCombo = this.controlPane.addComboBox(fieldData, productControls);
		}

		this.controlPane.addLabelBold(CatalogMessages.get().ProductEditorSummaySection_Brand, labelData, productControls);
		this.brandCombo = this.controlPane.addComboBox(fieldData, productControls);

		addCompositesToRefresh(controlPane.getSwtComposite().getParent());
	}

	@Override
	protected void populateControls() {
		this.productCodeText.setText(getProduct().getCode());
		this.productCodeText.setData(ORI_PRODUCT_CODE, getProduct().getCode());

		final Locale selectedLocale = ((ProductSummaryPage) this.formPage).getSelectedLocale();
		final String productDisplayName = getProduct().getDisplayName(selectedLocale);
		if (productDisplayName != null) {
			this.productNameText.setText(productDisplayName);
		}
		this.productTypeText.setText(getProduct().getProductType().getName());

		if (isProductBundle()) {
			if (((ProductBundle) getProduct()).isCalculated()) {
				this.bundlePricing.setText(CatalogMessages.get().ProductDetailsPage_BundlePricing_Calculated);
			} else {
				this.bundlePricing.setText(CatalogMessages.get().ProductDetailsPage_BundlePricing_Assigned);
			}
		}

		// Populate taxCode combo
		populateTaxCode();

		// Populate brand combo
		populateBrandNames(selectedLocale);

		// Set the modification listener *after* setting the control values
		// so that controls aren't considered to be modified when the initial value is set
		this.controlPane.setControlModificationListener(this.controlModificationListener);
	}

	private void populateTaxCode() {

		if (isProductBundle()) {
			return;
		}

		final TaxCodeService taxCodeService = ServiceLocator.getService(ContextIdNames.TAX_CODE_SERVICE);
		final TaxCodeRetriever taxCodeRetriever = ServiceLocator.getService(ContextIdNames.TAX_CODE_RETRIEVER);
		final List<TaxCode> codeList = taxCodeService.list();
		this.taxCodeList = new ArrayList<>();
		for (final TaxCode currTaxCode : codeList) {
			if (!TaxCode.TAX_CODE_SHIPPING.equals(currTaxCode.getCode())) {
				this.taxCodeCombo.add(currTaxCode.getCode());
				this.taxCodeList.add(currTaxCode);
				if (currTaxCode.equals(taxCodeRetriever.getEffectiveTaxCode(getProduct()))) {
					this.taxCodeCombo.select(this.taxCodeCombo.getItemCount() - 1);
				}
			}
		}
	}

	private void populateBrandNames(final Locale selectedLocale) {
		final BrandService brandService =
				ServiceLocator.getService(ContextIdNames.BRAND_SERVICE);
		this.brandList = brandService.findAllBrandsFromCatalog(getProduct().getMasterCatalog().getUidPk());
		Collections.sort(brandList, new BrandComparator(selectedLocale));
		brandCombo.setItems(new String[0]);
		for (final Brand brand : brandList) {
			String displayName = String.valueOf(brand.getDisplayName(selectedLocale, true));
			brandCombo.setData(displayName, brand);
			brandCombo.add(displayName);
			if (brand.equals(getProduct().getBrand())) {
				brandCombo.select(brandCombo.indexOf(displayName));
			}
		}
	}

	@Override
	protected void bindControls(final DataBindingContext bindingContext) {

		// Product Code
		EpControlBindingProvider.getInstance().bind(bindingContext, this.productCodeText, getProduct(), "code", //$NON-NLS-1$
				value -> {
					final String stringValue = (String) value;
					return checkProductCode(stringValue);
				}, null, false);

		// Product Name -- Bind the text box to the control using the custom update strategy
		EpControlBindingProvider.getInstance().bind(bindingContext, this.productNameText, EpValidatorFactory.STRING_255_REQUIRED, null,
				productNameUpdateStrategy, false);

		if (!isProductBundle()) {
			// TaxCode -- Create a custom update strategy to update the product based on the selected taxCode
			final ObservableUpdateValueStrategy taxCodeUpdateStrategy = new ObservableUpdateValueStrategy() {
				@Override
				protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
					return checkAndApplyTaxCode(getProduct());
				}
			};
			// TaxCode -- Bind the combo box to the control using the custom update strategy

			EpControlBindingProvider.getInstance().bind(bindingContext, this.taxCodeCombo, null, null, taxCodeUpdateStrategy, false);
		}

		// ---- DOCbindControls
		// Brand -- Create a custom update strategy to update the product based on the selected brand
		final ObservableUpdateValueStrategy brandUpdateStrategy = new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
				return checkAndApplyBrandName(getProduct());
			}
		};
		// Brand -- Bind the combo box to the control using the custom update strategy
		EpControlBindingProvider.getInstance().bind(bindingContext, this.brandCombo, null, null, brandUpdateStrategy, false);

	}
	// ---- DOCbindControls

	private IStatus checkProductCode(final String stringValue) {
		// check required
		final IStatus status = EpValidatorFactory.PRODUCT_CODE.validate(stringValue);
		if (!status.isOK()) {
			return status;
		}

		// check unique
		if (!stringValue.equals(productCodeText.getData(ORI_PRODUCT_CODE)) && productService.guidExists(stringValue)) {

			return new Status(IStatus.ERROR, CatalogPlugin.PLUGIN_ID, IStatus.ERROR, CatalogMessages.get().ProductEditorSummaySection_Duplicate_Code,
					null);

		}
		return Status.OK_STATUS;
	}

	private IStatus checkAndApplyTaxCode(final Product product) {
		final int selectedTaxIndex = taxCodeCombo.getSelectionIndex();
		if (selectedTaxIndex >= 0) {
			final TaxCode selectedTaxCode = taxCodeList.get(selectedTaxIndex);
			product.setTaxCodeOverride(selectedTaxCode);
		}
		return Status.OK_STATUS;
	}

	private IStatus checkAndApplyBrandName(final Product product) {
		final int selectedBrandIndex = brandCombo.getSelectionIndex();
		if (selectedBrandIndex >= 0) {
			final Brand selectedBrand = ProductEditorOverviewSection.this.brandList.get(selectedBrandIndex);
			product.setBrand(selectedBrand);
		}
		return Status.OK_STATUS;
	}

	// Product Name -- Create a custom update strategy to update the product based on the selected locale
	private final ObservableUpdateValueStrategy productNameUpdateStrategy = new ObservableUpdateValueStrategy() {
		@Override
		protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
			Locale selectedLocale = ((ProductSummaryPage) ProductEditorOverviewSection.this.formPage).getSelectedLocale();
			getProduct().setDisplayName((String) newValue, selectedLocale);
			return Status.OK_STATUS;
		}
	};

	private Product getProduct() {
		return ((ProductModel) getModel()).getProduct();
	}

	private boolean isProductBundle() {
		return getProduct() instanceof ProductBundle;
	}
}
