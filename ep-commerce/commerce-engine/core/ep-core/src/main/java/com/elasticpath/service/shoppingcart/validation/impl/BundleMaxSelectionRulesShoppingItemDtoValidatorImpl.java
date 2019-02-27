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
import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.SelectionRule;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.service.shoppingcart.validation.ShoppingItemDtoValidationContext;
import com.elasticpath.service.shoppingcart.validation.ShoppingItemDtoValidator;

/**
 * Determines if the shopping item dto being added to cart has constituents that exceed the max limits.
 */
public class BundleMaxSelectionRulesShoppingItemDtoValidatorImpl implements ShoppingItemDtoValidator {

	/**
	 * Message id for this validation.
	 */
	public static final String MESSAGE_ID = "bundle.exceeds.max.constituents";

	@Override
	public Collection<StructuredErrorMessage> validate(final ShoppingItemDtoValidationContext context) {

		Product product = context.getProductSku().getProduct();
		ShoppingItemDto shoppingItemDto = context.getShoppingItemDto();

		SelectedBundleConstituents selectedBundleConstituents = countSelectedItems(product, shoppingItemDto);
		if (selectedBundleConstituents.getSelectedQuantity() > selectedBundleConstituents.getMaxQuantity()) {
			return Collections.singletonList(new StructuredErrorMessage(StructuredErrorMessageType.NEEDINFO, MESSAGE_ID,
					"Bundle contains more than the maximum number of allowed bundle constituents.", ImmutableMap
					.of("item-code", context.getProductSku().getSkuCode(), "max-quantity",
							String.format("%d", selectedBundleConstituents.getMaxQuantity()), "current-quantity",
							String.format("%d", selectedBundleConstituents.getSelectedQuantity())),
					new StructuredErrorResolution(ShoppingItem.class, shoppingItemDto.getGuid())));
		}
		return Collections.emptyList();
	}

	private SelectedBundleConstituents countSelectedItems(final Product product, final ShoppingItemDto shoppingItemDto) {
		if (product instanceof ProductBundle) {
			ProductBundle bundle = (ProductBundle) product;
			int selections = 0;
			for (ShoppingItemDto constituentShoppingItemDto : shoppingItemDto.getConstituents()) {
				if (constituentShoppingItemDto.isSelected()) {
					selections++;
				}
			}

			int maxQuantity;
			int minQuantity;
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
