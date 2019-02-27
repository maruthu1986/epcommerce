/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.catalog.wizards.product.create;

import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.commons.constants.ContextIdNames;

/**
 * The Wizard for creating a new Product.
 */
@SuppressWarnings("PMD.UseSingleton")
public class CreateProductWizard extends AbstractCreateProductWizard {

	private static final int TOTAL_PAGES = 3;

	/**
	 * @param selectedCategoryUid the selected category UID
	 */
	protected CreateProductWizard(final long selectedCategoryUid) {
		super(CatalogMessages.get().CreateProductWizard_WindowTitle, selectedCategoryUid, getBean(ContextIdNames.PRODUCT));
	}

	/**
	 * Opens the Product creation wizard dialog.
	 *
	 * @param shell       the shell
	 * @param categoryUid the parent category UID
	 * @return result Window.OK or CANCEL
	 */
	public static int showWizard(final Shell shell, final long categoryUid) {
		return openWizard(shell, new CreateProductWizard(categoryUid));
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
		return CatalogMessages.get().ProductCreateWizard_CreationType_Product;
	}

	@Override
	public void addPages() {
		super.addPages();
		if (isHasPricingPage()) {
			final int page4 = 4;
			addPage(new PricingWizardPage6(getAssignmentsForCatalog(),
					PricingWizardPage6.PRICING_WIZARD_PAGE6,

					NLS.bind(CatalogMessages.get().ProductCreateWizard_PageTitle,
					new Object[]{getCreationType(), page4, getTotalPages()}),
					CatalogMessages.get().ProductCreateWizard_PricingInformation));
		}
	}

}
