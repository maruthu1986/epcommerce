/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.catalog.wizards.product.create;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.ValidationStatusProvider;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.PageChangingEvent;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.catalog.editors.sku.IEpSkuOverviewViewPart;
import com.elasticpath.cmclient.catalog.editors.sku.IProductSkuEventListener;
import com.elasticpath.cmclient.catalog.editors.sku.ProductBundleSkuOverviewViewPart;
import com.elasticpath.cmclient.catalog.editors.sku.ProductSkuDigitalAssetViewPart;
import com.elasticpath.cmclient.catalog.editors.sku.ProductSkuOverviewViewPart;
import com.elasticpath.cmclient.catalog.editors.sku.ProductSkuShippingViewPart;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.binding.EpWizardPageSupport;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.wizard.AbstractEPWizardPage;
import com.elasticpath.cmclient.policy.StatePolicy;
import com.elasticpath.cmclient.policy.common.AbstractStatePolicyImpl;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.core.dto.catalog.ProductModel;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.DigitalAsset;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;

/**
 * Single Sku wizard page.
 */
public class SingleSkuWizardPage5 extends AbstractEPWizardPage<ProductModel> implements IProductSkuEventListener {

	private static final String EMPTY_STRING = ""; //$NON-NLS-1$

	private static final int PAGE_LAYOUT_NUM_COLUMNS = 1;

	private static final int PAGE_LAYOUT_NUM_COLUMNS_3 = 3;

	private ProductSku productSku;

	private ExpandableComposite expandableDigitalComposite;

	private ExpandableComposite expandableShippingComposite;

	private IEpLayoutComposite digitalAssetSection;

	private IEpLayoutComposite shippingComposite;

	private ProductSkuDigitalAssetViewPart digitalAssetViewPart;

	private IEpSkuOverviewViewPart productSkuOverviewViewPart;

	private ProductSkuShippingViewPart productSkuShippingViewPart;

	private final DigitalAsset digitalAsset;

	private final boolean shippingDetailSectionAvailable;

	private final boolean digitalAssetsSectionAvailable;

	/** The page ID. **/
	protected static final String SINGLE_SKU_WIZARD_PAGE5 = "SingleSkuWizardPage5"; //$NON-NLS-1$

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

	/**
	 * Constructor.
	 *
	 * @param pageName the page name
	 * @param title the page title
	 * @param description the page description
	 * @param shippingDetailSectionAvailable page will have shipping detail section
	 * @param digitalAssetsSectionAvailable page has a digital section
	 */
	protected SingleSkuWizardPage5(final String pageName,
			final String title,
			final String description,
			final boolean shippingDetailSectionAvailable,
			final boolean digitalAssetsSectionAvailable) {

		super(PAGE_LAYOUT_NUM_COLUMNS, false, pageName, new DataBindingContext());

		digitalAsset = ServiceLocator.getService(ContextIdNames.DIGITAL_ASSET);
		setDescription(description);
		setTitle(title);
		setPageComplete(false);
		this.shippingDetailSectionAvailable = shippingDetailSectionAvailable;
		this.digitalAssetsSectionAvailable = digitalAssetsSectionAvailable;
	}

	/**
	 * @param productSkuOverviewViewPart part to use.
	 */
	public void setProductSkuOverviewViewPart(final IEpSkuOverviewViewPart productSkuOverviewViewPart) {
		this.productSkuOverviewViewPart = productSkuOverviewViewPart;
	}

	/**
	 * @return true if page has a shipping detail section
	 */
	protected boolean isShippingDetailSectionAvailable() {
		return shippingDetailSectionAvailable;
	}

	/**
	 * @return true if page has a digital assets section
	 */
	protected boolean isDigitalAssetsSectionAvailable() {
		return digitalAssetsSectionAvailable;
	}

	@Override
	protected void createEpPageContent(final IEpLayoutComposite pageComposite) {
		productSku = retrieveProductSku();

		createOverviewSectionComposite(pageComposite);

		if (isShippingDetailSectionAvailable()) {
			boolean isShippable = productSku.isShippable();
			createShippingDetailsSectionComposite(pageComposite, isShippable, isShippable);
		}

		if (isDigitalAssetsSectionAvailable()) {
			boolean isDigital = productSku.isShippable();
			createDigitalAssetsComposite(pageComposite, isDigital, isDigital);
		}

		setControl(pageComposite.getSwtComposite());
	}

	private void createOverviewSectionComposite(final IEpLayoutComposite pageComposite) {
		final IEpLayoutComposite overviewComposite = pageComposite.addGridLayoutSection(1,
				CatalogMessages.get().CreateProductWizard_Overview,
				ExpandableComposite.TITLE_BAR | ExpandableComposite.EXPANDED,
				pageComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false));

		if (getWizard() instanceof CreateProductBundleWizard) {
			productSkuOverviewViewPart = new ProductBundleSkuOverviewViewPart(productSku, this, true);
		} else {
			productSkuOverviewViewPart = new ProductSkuOverviewViewPart(productSku, this, true);
		}
		productSkuOverviewViewPart.createControls(overviewComposite, overviewComposite.createLayoutData());
		productSkuOverviewViewPart.applyStatePolicy(statePolicy);
	}

	private void createShippingDetailsSectionComposite(final IEpLayoutComposite container, final boolean expanded, final boolean enabled) {
		shippingComposite = container.addGridLayoutSection(PAGE_LAYOUT_NUM_COLUMNS_3,
				CatalogMessages.get().CreateProductWizard_ShippingDetails,
				ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE,
				container.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false));

		expandableShippingComposite = (ExpandableComposite) shippingComposite.getSwtComposite().getParent();

		expandableShippingComposite.setExpanded(expanded);
		expandableShippingComposite.setEnabled(enabled);
		expandableShippingComposite.layout(true);

		productSkuShippingViewPart = new ProductSkuShippingViewPart(productSku);

		productSkuShippingViewPart.createControls(shippingComposite,
				shippingComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false));
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

	@Override
	protected void bindControls() {
		productSkuOverviewViewPart.bindControls(getDataBindingContext());

		if (isDigitalAssetsSectionAvailable()) {
			digitalAssetViewPart.bindControls(getDataBindingContext());
		}

		if (isShippingDetailSectionAvailable()) {
			productSkuShippingViewPart.bindControls(getDataBindingContext());
		}

		EpWizardPageSupport.create(this, getDataBindingContext());
	}

	@Override
	protected void populateControls() {
		if (isShippingDetailSectionAvailable()) {
			expandableShippingComposite.setEnabled(false);
		}

		if (isDigitalAssetsSectionAvailable()) {
			expandableDigitalComposite.setEnabled(false);
		}
	}

	/**
	 * Retrieves the product SKU.
	 *
	 * @return {@link ProductSku}
	 */
	private ProductSku retrieveProductSku() {
		ProductSku prodSku = getProduct().getDefaultSku();

		if (prodSku == null) {
			prodSku = ServiceLocator.getService(ContextIdNames.PRODUCT_SKU);
			prodSku.setStartDate(getProduct().getStartDate());
			prodSku.setSkuCode(EMPTY_STRING);
		}

		return prodSku;
	}

	@Override
	public void digitalAssetOptionSelected(final boolean digital, final boolean downloadable) {
		if (isDigitalAssetsSectionAvailable()) {
			expandableDigitalComposite.setExpanded(downloadable);
			expandableDigitalComposite.setEnabled(downloadable);
			expandableDigitalComposite.layout(true);
			digitalAssetViewPart.setTextValidationEnabled(downloadable);
			productSku.setDigital(digital);
			if (downloadable) {
				productSku.setDigitalAsset(digitalAsset);
			} else {
				productSku.setDigitalAsset(null);
			}
			updatePageComplete();
		}
	}

	@Override
	public void shippableOptionSelected(final boolean selected) {
		if (isShippingDetailSectionAvailable()) {
			expandableShippingComposite.setExpanded(selected);
			expandableShippingComposite.setEnabled(selected);
			expandableShippingComposite.layout(true);
		}
	}

	@Override
	public void skuCodeChanged(final String skuCodeText) {
		updatePageComplete();
	}

	@Override
	public boolean beforeFromPrev(final PageChangingEvent event) {
		return init();
	}

	@Override
	public boolean beforeFromNext(final PageChangingEvent event) {
		init();
		boolean isShippable = productSku.isShippable();
		shippableOptionSelected(isShippable);
		boolean isDigital = productSku.isDigital();
		boolean isDownloadable = productSku.isDownloadable();
		digitalAssetOptionSelected(isDigital, isDownloadable);
		return true;
	}

	private boolean init() {
		if (getProduct().getDefaultSku() == null) {
			getProduct().setDefaultSku(productSku);
		} else {
			productSku = getProduct().getDefaultSku();
			populateControls();
		}
		productSku.setProduct(getProduct());

		return true;
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

	@Override
	public IWizardPage getNextPage() {

		// Pricing is not relevant for calculated bundles
		if (getProduct() instanceof ProductBundle && ((ProductBundle) getProduct()).isCalculated()) {
			return null;
		}

		PricingWizardPage6 pricingPage = (PricingWizardPage6) super.getNextPage();
		if (pricingPage != null) {
			// does some initialization work before going to the next page
			pricingPage.preloadPricingTable();
		}
		return pricingPage;
	}

	@Override
	public boolean isPageComplete() {
		if (getProduct().getProductType().isMultiSku()) {
			return true;
		}

		if ((isShippingDetailSectionAvailable() || isDigitalAssetsSectionAvailable())
				&& (productSkuOverviewViewPart instanceof ProductSkuOverviewViewPart)) {
			ProductSkuOverviewViewPart overviewPart = (ProductSkuOverviewViewPart) productSkuOverviewViewPart;

			// if downloadable selected make sure there is a filename entered
			if (overviewPart.isDownloadableButtonSelected() && StringUtils.isEmpty(digitalAssetViewPart.getFilenameText())) {
				return false;
			}

			// also make sure that a valid sku code has been entered
			if (StringUtils.isEmpty(overviewPart.getSkuCodeText())) {
				return false;
			} else {
				return overviewPart.isStatusOk();
			}

		} else {
			// use the default page complete mechanism
			return super.isPageComplete();
		}

	}

	private Product getProduct() {
		return getModel().getProduct();
	}

}
