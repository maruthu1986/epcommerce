/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.catalog.dialogs.categorytype;

import java.util.List;

import com.elasticpath.cmclient.catalog.policy.AbstractPolicyAwareAttributesDualListBox;
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
public class CategoryTypeAttributesDualListBox extends AbstractPolicyAwareAttributesDualListBox {

	private final Catalog catalog;

	/**
	 * Constructor.
	 *
	 * @param parentComposite the parent composite of the dual list box.
	 * @param data the layout data to create the dual list box
	 * @param container the policy container
	 * @param model the model object.
	 * @param availableTitle the title string text of the available list.
	 * @param assignedTitle the title string of the selected list.
	 * @param catalog the catalog
	 */
	public CategoryTypeAttributesDualListBox(final IPolicyTargetLayoutComposite parentComposite, final IEpLayoutData data,
			final PolicyActionContainer container, final List<Attribute> model, final String availableTitle, final String assignedTitle,
			final Catalog catalog) {
		super(parentComposite, data, container, model, availableTitle, assignedTitle);
		this.catalog = catalog;
	}

	@Override
	public List<Attribute> getAvailableAttributesList(final AttributeService attributeService) {
		return attributeService.findByCatalogAndUsage(getCatalog().getUidPk(), AttributeUsage.CATEGORY);
	}

	/**
	 * Gets the assigned catalog.
	 *
	 * @return the catalog
	 */
	public Catalog getCatalog() {
		return catalog;
	}

}