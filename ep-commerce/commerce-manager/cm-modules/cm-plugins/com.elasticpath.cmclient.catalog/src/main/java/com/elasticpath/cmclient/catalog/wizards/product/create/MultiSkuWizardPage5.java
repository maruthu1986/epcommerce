/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.catalog.wizards.product.create;


import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.dialogs.PageChangingEvent;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;

import com.elasticpath.cmclient.catalog.editors.sku.ProductMultiSkuViewPart;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.wizard.AbstractEPWizardPage;
import com.elasticpath.cmclient.core.dto.catalog.ProductModel;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;

/**
 * 
 * @author shallinan
 *
 */
public class MultiSkuWizardPage5 extends AbstractEPWizardPage<ProductModel> implements SelectionListener {

	private static final int PAGE_LAYOUT_NUM_COLUMNS = 2;

	private ProductMultiSkuViewPart multiSkuViewPart;

	/**
	 * Page ID.
	 */
	protected static final String MULTI_SKU_WIZARD_PAGE5 = "MultiSkuWizardPage5"; //$NON-NLS-1$
	
	/**
	 * Constructor.
	 * 
	 * @param pageName the page name
	 * @param title the page title
	 * @param description the page description
	 */
	protected MultiSkuWizardPage5(final String pageName, final String title, final String description) {
		super(PAGE_LAYOUT_NUM_COLUMNS, false, pageName, new DataBindingContext());

		this.setDescription(description);
		this.setTitle(title);
		setPageComplete(false);
	}
	
	@Override
	protected void bindControls() {
		multiSkuViewPart.bindControls(getDataBindingContext());
	}

	@Override
	protected void createEpPageContent(final IEpLayoutComposite pageComposite) {
		multiSkuViewPart = new ProductMultiSkuViewPart(getProduct(), true, null);
		multiSkuViewPart.createControls(pageComposite, null);
		multiSkuViewPart.addButtonsSelectionListener(this);
		
		this.setControl(pageComposite.getSwtComposite());
	}

	@Override
	protected void populateControls() {
		multiSkuViewPart.populateControls();
	}
	
	@Override
	public boolean beforeFromPrev(final PageChangingEvent event) {
		setPageComplete(getProduct().getProductSkus().size() > 0);
		multiSkuViewPart.refreshTable();
		((Composite) getControl()).layout(true);
		return true;
	}

	@Override
	public boolean beforeFromNext(final PageChangingEvent event) {
		setPageComplete(getProduct().getProductSkus().size() > 0);
		multiSkuViewPart.refreshTable();
		((Composite) getControl()).layout(true);
		return true;
	}

	@Override
	public void widgetDefaultSelected(final SelectionEvent event) {
		// not used
	}
	
	@Override
	public IWizardPage getNextPage() {
		// Pricing is not relevant for calculated bundles
		if (getProduct() instanceof ProductBundle && ((ProductBundle) getProduct()).isCalculated()) {
			return null;
		}
		
		PricingWizardPage6 pricingPage = (PricingWizardPage6) getWizard().getPage(PricingWizardPage6.PRICING_WIZARD_PAGE6);
		if (pricingPage != null) {
			pricingPage.preloadPricingTable();
		}
		return pricingPage;
	}

	@Override
	public void widgetSelected(final SelectionEvent event) {
		setPageComplete(getProduct().getProductSkus().size() > 0);
	}
	
	@Override
	public boolean isPageComplete() {
		if (!getProduct().getProductType().isMultiSku()) {
			return true;
		}
		return super.isPageComplete();
	}
	
	private Product getProduct() {
		return getModel().getProduct();
	}

}
