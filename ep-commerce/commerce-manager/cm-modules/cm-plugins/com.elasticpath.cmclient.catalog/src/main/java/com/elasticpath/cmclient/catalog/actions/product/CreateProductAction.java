/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.catalog.actions.product;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Shell;

import com.elasticpath.cmclient.catalog.CatalogImageRegistry;
import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.catalog.wizards.product.create.CreateProductWizard;
import com.elasticpath.domain.catalog.Category;

/**
 * Implementation of {@link AbstractCreateProductAction} that is used for creating regular products.
 */
public class CreateProductAction extends AbstractCreateProductAction {

	private static final Logger LOG = Logger.getLogger(CreateProductAction.class);
		
	/** Constructor. */
	public CreateProductAction() {
		super(CatalogMessages.get().CatalogBrowseView_Action_CreateProduct, CatalogImageRegistry.PRODUCT_CREATE);
		this.setToolTipText(CatalogMessages.get().CreateProductAction);
	}
	
	@Override
	public String getTargetIdentifier() {
		return "createProductAction"; //$NON-NLS-1$
	}

	@Override
	public void openWizard(final Shell shell, final Category category) {
		LOG.debug("Create Regular Product Action called."); //$NON-NLS-1$
		
		CreateProductWizard.showWizard(shell, category.getUidPk());		
	}
}
