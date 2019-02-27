/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.catalog.actions.product;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Shell;

import com.elasticpath.cmclient.catalog.CatalogImageRegistry;
import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.catalog.wizards.product.create.CreateProductBundleWizard;
import com.elasticpath.domain.catalog.Category;

/**
 * Implementation of {@link AbstractCreateProductAction} that is used for creating bundling products.
 */
public class CreateProductBundleAction  extends AbstractCreateProductAction  {

	private static final Logger LOG = Logger.getLogger(CreateProductBundleAction.class);

	/** Constructor. */
	public CreateProductBundleAction() {
		super(CatalogMessages.get().CatalogBrowseView_Action_CreateProductBundle, CatalogImageRegistry.PRODUCT_BUNDLE_CREATE);
		this.setToolTipText(CatalogMessages.get().CreateProductBundleAction);
	}
	
	@Override
	public String getTargetIdentifier() {
		return "createProductBundleAction"; //$NON-NLS-1$
	}

	@Override
	public void openWizard(final Shell shell, final Category category) {
		LOG.debug("Create Product Bundle Action called."); //$NON-NLS-1$
		
		CreateProductBundleWizard.showWizard(shell, category.getUidPk());
	}	
	 
}
