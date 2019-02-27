/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.catalog.wizards.sku;

import java.util.List;
import java.util.Locale;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.dialogs.MessageDialog;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.CorePlugin;
import com.elasticpath.cmclient.core.wizard.AbstractEpWizard;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;

/**
 * The wizard for creating and editing a new ProductSku.
 */
@SuppressWarnings({ "PMD.PrematureDeclaration" })
public class AddSkuWizard extends AbstractEpWizard <Product> {
	
	private final Product product;
	private AddSkuWizardPage1 skuDetailPage;
	private AddSkuWizardPage2 skuDetailPage2;
	private ProductSku productSku;
	
	/**
	 * Constructor.
	 * @param title the wizard title
	 * @param product the product that will have new sku
	 */
	public AddSkuWizard(final String title, final Product product) {
		super(title, null, null);
		this.product = product;
	}

	@Override
	public boolean performFinish() {
		final ProductSku productSku = ((AddSkuWizardPage2) getContainer().getCurrentPage()).getProductSku();
		productSku.setProduct(product);

		Locale locale =	getLocaleFromCatalog();
		
		
		final List<AttributeValue> attributeValueList = productSku.getFullAttributeValues(locale);
		
		for (final AttributeValue attributeValue : attributeValueList) {
			if (attributeValue.getAttribute().isRequired() && !attributeValue.isDefined()) {
				MessageDialog.openWarning(this.getShell(), CatalogMessages.get().AddSkuWizard_Validation_Error_Title,
						CatalogMessages.get().AddSkuWizard_Required_Attribute_Msg);
				return false;
			}
		}
		
		return true;
	}

	@Override
	public boolean canFinish() {
		final boolean result = super.canFinish();
		if (getNextPage(getContainer().getCurrentPage()) != null) {
			return false;
		}
		return result;
	}
	
	/** 
	 * Gets the locale from the master catalog. If no master catalog found, uses the Default locale.
	 * @return the locale.
	 */
	private Locale getLocaleFromCatalog() {
		for (Catalog catalog : this.product.getCatalogs()) {
			if (catalog.isMaster()) {
				return catalog.getDefaultLocale();
			}
		}
		return CorePlugin.getDefault().getDefaultLocale();
	}
	
	@Override
	public Product getModel() {
		return this.product;
	}

	@Override
	public void addPages() {
				
		productSku = createNewSku();
		skuDetailPage = new AddSkuWizardPage1(AddSkuWizardPage1.class.getSimpleName(), CatalogMessages.get().AddSkuWizard_Step1,
				new DataBindingContext(), this.getModel(), productSku);
		
		skuDetailPage2 = new AddSkuWizardPage2(AddSkuWizardPage2.class.getSimpleName(), CatalogMessages.get().AddSkuWizard_Step2,
				new DataBindingContext(), this.getModel(), productSku);
		
		addPage(skuDetailPage);
		addPage(skuDetailPage2);
	}
	
	private ProductSku createNewSku() {
		return (ProductSku) ServiceLocator.getService(ContextIdNames.PRODUCT_SKU);
	}
	
}
