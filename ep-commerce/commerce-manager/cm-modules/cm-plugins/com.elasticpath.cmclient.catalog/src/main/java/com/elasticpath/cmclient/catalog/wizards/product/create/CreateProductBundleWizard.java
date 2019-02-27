/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.catalog.wizards.product.create;

import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.commons.constants.ContextIdNames;

/**
 * The Wizard for creating a new ProductBundle.
 */
public class CreateProductBundleWizard extends AbstractCreateProductWizard {

	private static final int TOTAL_PAGES = 4;

	/**
	 * @param selectedCategoryUid the selected category UID
	 */
	protected CreateProductBundleWizard(final long selectedCategoryUid) {
		super(CatalogMessages.get().CreateBundleWizard_WindowTitle, selectedCategoryUid, getBean(ContextIdNames.PRODUCT_BUNDLE));
	}

	/**
	 * Opens the Product Bundle creation wizard dialog.
	 *
	 * @param shell       the shell
	 * @param categoryUid the parent category UID
	 * @return result Window.OK or CANCEL
	 */
	public static int showWizard(final Shell shell, final long categoryUid) {
		return openWizard(shell, new CreateProductBundleWizard(categoryUid));
	}

	@Override
	public void addPages() {
		addPage(new ProductDetailsWizardPage1(
				ProductDetailsWizardPage1.PRODUCT_DETAILS_WIZARD_PAGE1,

				NLS.bind(CatalogMessages.get().ProductBundleCreateWizard_PageTitle,
				new Object[]{getCreationType(), 1}),
				CatalogMessages.get().ProductCreateWizard_ProductDetailsDescription));

		addPage(new BundleConstituentsWizardPage6(
				BundleConstituentsWizardPage6.CREATE_BUNDLE_CONSTITUENTS_WIZARD_PAGE6,

				NLS.bind(CatalogMessages.get().ProductBundleCreateWizard_PageTitle,
				new Object[]{getCreationType(), 2}),
				CatalogMessages.get().ProductCreateWizard_CreateBundleConstituentsDescription));

		final int page3 = 3;
		addPage(new AttributeValuesWizardPage4(
				AttributeValuesWizardPage4.ATTRIBUTE_VALUES_WIZARD_PAGE4,

				NLS.bind(CatalogMessages.get().ProductBundleCreateWizard_PageTitle,
				new Object[]{getCreationType(), page3}),
				CatalogMessages.get().ProductCreateWizard_AttributeValuesDescription));

		final int page4 = 4;
		addPage(new MultiSkuWizardPage5(
				MultiSkuWizardPage5.MULTI_SKU_WIZARD_PAGE5,

				NLS.bind(CatalogMessages.get().ProductBundleCreateWizard_PageTitle,
				new Object[]{getCreationType(), page4}),
				CatalogMessages.get().ProductCreateWizard_MultiSkuDescription));

		addPage(new SingleSkuWizardPage5(
				SingleSkuWizardPage5.SINGLE_SKU_WIZARD_PAGE5,

				NLS.bind(CatalogMessages.get().ProductBundleCreateWizard_PageTitle,
				new Object[]{getCreationType(), page4}),
				CatalogMessages.get().ProductCreateWizard_CreateSingleSkuDescription,
				false,
				false)
		);

		if (isHasPricingPage()) {
			final int page5 = 5;
			addPage(new PricingWizardPage6(getAssignmentsForCatalog(),
					PricingWizardPage6.PRICING_WIZARD_PAGE6,

					NLS.bind(CatalogMessages.get().ProductBundleCreateWizard_PageTitle,
					new Object[]{getCreationType(), page5}),
					CatalogMessages.get().BundleCreateWizard_PricingInformation));
		}
	}

	@Override
	protected int getTotalPages() {
		if (isHasPricingPage()) {
			return TOTAL_PAGES + 1;
		}
		return TOTAL_PAGES;
	}

	@Override
	protected String getCreationType() {
		return CatalogMessages.get().ProductCreateWizard_CreationType_Bundle;
	}
}
