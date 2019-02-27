/**
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shoppingcart.validation.impl;

import java.util.Collection;
import java.util.Collections;

import com.google.common.collect.ImmutableMap;

import org.apache.commons.lang.StringUtils;

import com.elasticpath.base.common.dto.StructuredErrorMessage;
import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.domain.catalog.BundleConstituent;
import com.elasticpath.domain.catalog.ConstituentItem;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.service.catalog.ProductLookup;
import com.elasticpath.service.shoppingcart.validation.ShoppingItemDtoValidationContext;
import com.elasticpath.service.shoppingcart.validation.ShoppingItemDtoValidator;

/**
 * Determines if the cart item contains dependent line items that are invalid for the defined bundle structure.
 */
public class BundleStructureShoppingItemDtoValidatorImpl implements ShoppingItemDtoValidator {

	private ProductLookup productLookup;

	@Override
	public Collection<StructuredErrorMessage> validate(final ShoppingItemDtoValidationContext context) {
		Product product = context.getProductSku().getProduct();
		ShoppingItemDto shoppingItemDto = context.getShoppingItemDto();
		if (verifyDtoStructureEqualsBundleStructure(product, shoppingItemDto)) {
			return Collections.emptyList();
		}
		return Collections.singletonList(new StructuredErrorMessage("item.invalid.bundle.structure",
				"Requested item configuration does not have a valid bundle structure.",
				ImmutableMap.of("item-code", context.getProductSku().getSkuCode())));

	}

	private boolean verifyDtoStructureEqualsBundleStructure(final Product product, final ShoppingItemDto dto) {
		if (product instanceof ProductBundle && !dto.getConstituents().isEmpty()) {
			ProductBundle bundle = (ProductBundle) product;
			int constituentIndex = 0;
			for (BundleConstituent bundleItem : bundle.getConstituents()) {
				ConstituentItem constituent = bundleItem.getConstituent();
				ShoppingItemDto correspondingDto = dto.getConstituents().get(constituentIndex);
				if (constituent.isProductSku() && !correspondingDto.getSkuCode().equals(constituent.getCode())) {
					return false;
				}

				if (constituent.isProduct() && !verifyDtoStructureEqualsBundleStructure(constituent.getProduct(), correspondingDto)) {
					return false;
				}
				constituentIndex++;
			}

			return true;
		}
		return getSkuFromProduct(product, dto.getSkuCode()) != null;
	}

	private ProductSku getSkuFromProduct(final Product product, final String skuCode) {
		Product productWithSkus = product;
		if (product.getProductSkus() != null && product.getProductSkus().isEmpty()) {
			productWithSkus = getProductWithSkus(product.getCode());
		}

		ProductSku productSku;
		if (StringUtils.isBlank(skuCode)) {
			productSku = productWithSkus.getDefaultSku();
		} else {
			productSku = productWithSkus.getSkuByCode(skuCode);
		}

		return productSku;
	}

	private Product getProductWithSkus(final String productCode) {
		return getProductLookup().findByGuid(productCode);
	}

	protected ProductLookup getProductLookup() {
		return productLookup;
	}

	public void setProductLookup(final ProductLookup productLookup) {
		this.productLookup = productLookup;
	}
}
