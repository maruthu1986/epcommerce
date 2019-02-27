/**
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shoppingcart.validation.impl;

import java.util.Collection;
import java.util.Collections;

import com.google.common.collect.ImmutableMap;

import com.elasticpath.base.common.dto.StructuredErrorMessage;
import com.elasticpath.base.common.dto.StructuredErrorMessageType;
import com.elasticpath.base.common.dto.StructuredErrorResolution;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.SelectionRule;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.service.catalog.ProductSkuLookup;
import com.elasticpath.service.shoppingcart.validation.ShoppingItemValidationContext;
import com.elasticpath.service.shoppingcart.validation.ShoppingItemValidator;

/**
 * Determines if the bundle in the cart meets the minimum requirements for constituents.
 */
public class BundleMinSelectionRulesShoppingItemValidatorImpl implements ShoppingItemValidator {

	/**
	 * Message id for this validation.
	 */
	public static final String MESSAGE_ID = "bundle.does.not.contain.min.constituents";

	private ProductSkuLookup productSkuLookup;

	/**
	 * Validates the object.
	 *
	 * @param context object to be validated.
	 * @return a collection of Structured Error Messages containing validation errors, or an
	 * empty collection if the validation is successful.
	 */
	@Override
	public Collection<StructuredErrorMessage> validate(final ShoppingItemValidationContext context) {

		Product product = context.getProductSku().getProduct();
		ShoppingItem shoppingItem = context.getShoppingItem();

		SelectedBundleConstituents selectedBundleConstituents = countSelectedItems(product, shoppingItem);
		if (selectedBundleConstituents.getSelectedQuantity() < selectedBundleConstituents.getMinQuantity()) {
			return Collections.singletonList(new StructuredErrorMessage(StructuredErrorMessageType.NEEDINFO, MESSAGE_ID,
					"Bundle does not contain the minimum number of required bundle constituents.", ImmutableMap
					.of("item-code", context.getProductSku().getSkuCode(), "min-quantity",
							String.format("%d", selectedBundleConstituents.getMinQuantity()), "current-quantity",
							String.format("%d", selectedBundleConstituents.getSelectedQuantity())),
					new StructuredErrorResolution(ShoppingItem.class, shoppingItem.getGuid())));
		}
		return Collections.emptyList();
	}

	private SelectedBundleConstituents countSelectedItems(final Product product, final ShoppingItem shoppingItem) {
		if (product instanceof ProductBundle) {
			ProductBundle bundle = (ProductBundle) product;
			int maxQuantity;
			int minQuantity;
			int selections = shoppingItem.getBundleItems(productSkuLookup).size();

			SelectionRule selectionRule = bundle.getSelectionRule();
			if (selectionRule == null || selectionRule.getParameter() == 0) {
				maxQuantity = bundle.getConstituents().size();
				minQuantity = bundle.getConstituents().size();
			} else {
				maxQuantity = selectionRule.getParameter();
				minQuantity = selectionRule.getParameter();
			}

			return new SelectedBundleConstituents(minQuantity, maxQuantity, selections);
		}
		return new SelectedBundleConstituents(0, 0, 0);

	}

	protected ProductSkuLookup getProductSkuLookup() {
		return productSkuLookup;
	}

	public void setProductSkuLookup(final ProductSkuLookup productSkuLookup) {
		this.productSkuLookup = productSkuLookup;
	}

	/**
	 * Inner class to hold min, max and current number of selected bundle constituents.
	 */
	private static final class SelectedBundleConstituents {

		private final int minQuantity;

		private final int maxQuantity;

		private final int selectedQuantity;

		public int getMinQuantity() {
			return minQuantity;
		}

		public int getSelectedQuantity() {
			return selectedQuantity;
		}

		public int getMaxQuantity() {
			return maxQuantity;
		}

		SelectedBundleConstituents(final int minQuantity, final int maxQuantity, final int selectedQuantity) {
			this.minQuantity = minQuantity;
			this.maxQuantity = maxQuantity;
			this.selectedQuantity = selectedQuantity;
		}

	}
}
