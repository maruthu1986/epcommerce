/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.catalog.wizards.product.create;


import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.dialogs.PageChangingEvent;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.osgi.util.NLS;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.catalog.common.ProductAttributeValueSorter;
import com.elasticpath.cmclient.catalog.editors.attribute.AttributesViewPart;
import com.elasticpath.cmclient.core.dto.catalog.ProductModel;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.wizard.AbstractEPWizardPage;
import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.domain.catalog.AttributeValueIsRequiredException;
import com.elasticpath.domain.catalog.Product;


/**
 * AttributeValues wizard page, designed to be page 4 of the {@link CreateProductWizard}.
 * Allows editing and clearing of attribute values on products.
 */
public class AttributeValuesWizardPage4 extends AbstractEPWizardPage<ProductModel> {

	private static final int PAGE_LAYOUT_NUM_COLUMNS = 2;
	private static final String NEW_LINE = "\n"; //$NON-NLS-1$

	private AttributesViewPart attributesViewPart;

	private long productTypeUidPk;

	/**
	 * Page ID.
	 */
	protected static final String ATTRIBUTE_VALUES_WIZARD_PAGE4 = "AttributeValuesWizardPage4"; //$NON-NLS-1$

	/**
	 * Constructor.
	 *
	 * @param pageName the page name
	 * @param title the page title
	 * @param description the page description
	 */
	protected AttributeValuesWizardPage4(final String pageName, final String title, final String description) {
		super(PAGE_LAYOUT_NUM_COLUMNS, false, pageName, new DataBindingContext());

		this.setDescription(description);
		this.setTitle(title);
	}

	@Override
	protected void bindControls() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void createEpPageContent(final IEpLayoutComposite pageComposite) {
		attributesViewPart = new AttributesViewPart(getModel(), EpState.EDITABLE, null);
		attributesViewPart.createControls(pageComposite);

		this.setControl(pageComposite.getSwtComposite());
	}

	/**
	 * Retrieves the product attribute values.
	 */
	private AttributeValue[] getAttributes() {

		final Product product = getProduct();
		final Locale productDefaultLocale = product.getMasterCatalog().getDefaultLocale();
		return new ProductAttributeValueSorter(product, productDefaultLocale).getOrderedAttributeValues();
	}

	@Override
	public boolean beforeFromPrev(final PageChangingEvent event) {
		if (!attributesViewPart.isInitialized() || getProduct().getProductType().getUidPk() != productTypeUidPk) {
			attributesViewPart.setInput(getAttributes());
			productTypeUidPk = getProduct().getProductType().getUidPk();
		}
		return true;
	}

	@Override
	public boolean beforeNext(final PageChangingEvent event) {
		return validateRequired();
	}

	private boolean validateRequired() {
		final Locale productDefaultLocale = getProduct().getMasterCatalog().getDefaultLocale();
		final Set<Locale> locales = new HashSet<>(Arrays.asList(productDefaultLocale));
		// Ensure that all Required Attributes are specified
		try {
			this.getProduct().validateRequiredAttributes(locales);
		} catch (AttributeValueIsRequiredException e) {
			setErrorMessage(

					NLS.bind(CatalogMessages.get().ProductSaveMissingValueForRequiredAttributeMessage,
					e.getAttributesAsString(NEW_LINE)));
			return false;
		}
		setErrorMessage(null);
		return true;
	}

	@Override
	public IWizardPage getNextPage() {

		IWizardPage nextPage;
		if (getProduct().getProductType().isMultiSku()) {
			nextPage = this.getWizard().getPage(MultiSkuWizardPage5.MULTI_SKU_WIZARD_PAGE5);
		} else {
			nextPage = this.getWizard().getPage(SingleSkuWizardPage5.SINGLE_SKU_WIZARD_PAGE5);
		}
		return nextPage;
	}
	
	private Product getProduct() {
		return getModel().getProduct();
	}

	@Override
	protected void populateControls() {
		// product does not have values at this point in time
	}
	/**
	 * Refresh the attributes that get displayed.
	 */
	public void refreshData() {
		attributesViewPart.setInput(getAttributes());
	}

}
