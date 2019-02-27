/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.catalog.wizards.product;

import java.util.List;

import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.IPolicyTargetLayoutComposite;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeUsage;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.service.attribute.AttributeService;

/**
 * The class to display the dual assignment list for product attribute
 * assignment for adding/Editing product type.
 */
public class ProductTypeSkuAttributesDualList extends
		AbstractAttributesDualList {

	/**
	 * @param parentComposite the parent composite of the dual list box.
	 * @param model the model object.
	 * @param data the layout data to create the dual list box
	 * @param container the PolicyActionContainer passed in.
	 * @param availableTitle the title string text of the available list.
	 * @param selectedTitle the title string of the selected list.
	 * @param catalog the catalog
	 */
	public ProductTypeSkuAttributesDualList(
			final IPolicyTargetLayoutComposite parentComposite, 
			final PolicyActionContainer container,
			final List<Attribute> model,
			final String availableTitle, final String selectedTitle,
			final IEpLayoutData data, final Catalog catalog) {
		super(parentComposite, container, model, availableTitle, selectedTitle, data, catalog);
	}

	@Override
	public List<Attribute> getAvailableAttributesList(
			final AttributeService attributeService) {
		return attributeService.findByCatalogAndUsage(this.getCatalog().getUidPk(), AttributeUsage.SKU);
	}
}
