/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.catalog.editors.sku;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.forms.IManagedForm;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.catalog.CatalogPlugin;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareEditorPage;
import com.elasticpath.cmclient.core.dto.catalog.ProductModel;
import com.elasticpath.domain.catalog.ProductBundle;

/**
 * Product SKUs detail page.
 */
public class ProductSkuDetailsPage extends AbstractPolicyAwareEditorPage {
	
	/**
	 * The Page ID.
	 */
	public static final String PAGE_ID = "productSingleSkuPage"; //$NON-NLS-1$

	private final boolean hasShippingSection;
	
	private final boolean hasDigitalAssetSection;	

	private ProductSkuDetailsPageDigitalAssetSection digitalAssetSection;
	
	private ProductSkuDetailsPageShippingSection shippingSection;

	private ProductSkuDetailsPageOverviewSection overviewSection;
	
	private boolean isBundleSku;

	/**
	 * Constructor.
	 * 
	 * @param editor the parent editor
	 * @param hasDigitalAssetSection true if page has digital assets section
	 * @param hasShippingSection true if page has shipping section
	 */
	public ProductSkuDetailsPage(final AbstractCmClientFormEditor editor, 
			final boolean hasShippingSection, 
			final boolean hasDigitalAssetSection) {
		super(editor, PAGE_ID, CatalogMessages.get().ProductSingleSkuPage_Title, true);
		this.hasShippingSection = hasShippingSection;
		this.hasDigitalAssetSection = hasDigitalAssetSection;
	}
	
	
	/** 
	 * @return true if page has shipping section.
	 */
	public boolean isHasShippingSection() {
		return hasShippingSection;
	}



	/** 
	 * @return true if page has digital assets section.
	 */
	public boolean isHasDigitalAssetSection() {
		return hasDigitalAssetSection;
	}



	@Override
	protected void addEditorSections(final AbstractCmClientFormEditor editor, final IManagedForm managedForm) {
		PolicyActionContainer container = addPolicyActionContainer("section"); //$NON-NLS-1$
		
		isBundleSku = editor.getModel() instanceof ProductModel 
				&& ((ProductModel) editor.getModel()).getProduct() instanceof ProductBundle;
		
		// Create the sections
		overviewSection = new ProductSkuDetailsPageOverviewSection(this, editor);
		if (isHasShippingSection()) {
			shippingSection = new ProductSkuDetailsPageShippingSection(this, editor);			
		}
		
		if (isHasDigitalAssetSection()) {
			digitalAssetSection = new ProductSkuDetailsPageDigitalAssetSection(this, editor);			
		}
		

		addPart(container, managedForm, overviewSection);
		if (isHasShippingSection()) {
			addPart(container, managedForm, shippingSection);
		}
		if (isHasDigitalAssetSection()) {
			addPart(container, managedForm, digitalAssetSection);
		}
		
		if (isBundleSku) {
			setDigitalAssetSectionEnabled(false);
			setShippingSectionEnabled(false);
		}
		addExtensionEditorSections(editor, managedForm, CatalogPlugin.PLUGIN_ID, this.getClass().getSimpleName());
	}

	@Override
	protected void addToolbarActions(final IToolBarManager toolBarManager) {
		// TODO Auto-generated method stub

	}

	@Override
	protected int getFormColumnsCount() {
		return 1;
	}

	@Override
	protected String getFormTitle() {
		return CatalogMessages.get().ProductSingleSkuPage_Title;
	}

	/**
	 * Enables/disables the digital asset section.
	 * 
	 * @param enabled should the section get enabled
	 */
	public void setDigitalAssetSectionEnabled(final boolean enabled) {
		if (isHasDigitalAssetSection()) {
			digitalAssetSection.getSection().setEnabled(!isBundleSku && enabled);
			digitalAssetSection.getSection().setExpanded(!isBundleSku && enabled);
		}		
	}

	/**
	 * Enables/Disables the shipping section.
	 * 
	 * @param enabled should the section get enabled
	 */
	public void setShippingSectionEnabled(final boolean enabled) {
		if (isHasShippingSection()) {
			shippingSection.getSection().setEnabled(!isBundleSku && enabled);
			shippingSection.getSection().setExpanded(!isBundleSku && enabled);			
		}
	}

	/**
	 * Gets the digital asset section.
	 *
	 * @return the section
	 */
	ProductSkuDetailsPageDigitalAssetSection getDigitalAssetSection() {
		return digitalAssetSection;
	}
}