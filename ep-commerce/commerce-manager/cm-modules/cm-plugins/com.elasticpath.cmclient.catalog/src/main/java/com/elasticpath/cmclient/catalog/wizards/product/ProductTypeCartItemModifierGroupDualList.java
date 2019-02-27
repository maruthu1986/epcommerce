/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */

package com.elasticpath.cmclient.catalog.wizards.product;

import java.util.List;

import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.IPolicyTargetLayoutComposite;
import com.elasticpath.domain.cartmodifier.CartItemModifierGroup;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.service.cartitemmodifier.CartItemModifierService;

/**
 * The class to display the dual assignment list for product cart item modifier group
 * assignment for adding/Editing cart item modifier group.
 */
public class ProductTypeCartItemModifierGroupDualList extends AbstractCartItemModifierGroupDualList {

	/**
	 * @param parentComposite the parent composite of the dual list box.
	 * @param model           the model object.
	 * @param data            the layout data to create the dual list box
	 * @param container       the PolicyActionContainer object passed in.
	 * @param availableTitle  the title string text of the available list.
	 * @param selectedTitle   the title string of the selected list.
	 * @param catalog         the catalog
	 */
	public ProductTypeCartItemModifierGroupDualList(final IPolicyTargetLayoutComposite parentComposite,
			final PolicyActionContainer container,
			final List<CartItemModifierGroup> model,
			final String availableTitle,
			final String selectedTitle,
			final IEpLayoutData data,
			final Catalog catalog) {
		super(parentComposite, container, model, availableTitle, selectedTitle, data, catalog);

	}

	@Override
	public List<CartItemModifierGroup> getAvailableCartItemModifierGroupsList(final CartItemModifierService cartItemModifierService) {
		return cartItemModifierService.findCartItemModifierGroupByCatalogUid(this.getCatalog().getUidPk());
	}
}
