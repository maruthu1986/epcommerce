/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.common.dto.sellingchannel.impl;

import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.common.dto.sellingchannel.ShoppingItemDtoFactory;
import com.elasticpath.domain.catalog.BundleConstituent;
import com.elasticpath.domain.catalog.ConstituentItem;
import com.elasticpath.domain.catalog.ItemConfiguration;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.service.catalog.BundleIdentifier;
import com.elasticpath.service.catalog.ProductSkuLookup;

/**
 * Shopping Item Dto Factory implementation.
 *
 */
public class ShoppingItemDtoFactoryImpl implements ShoppingItemDtoFactory {

	private BundleIdentifier bundleIdentifier;

	private ProductSkuLookup productSkuLookup;

	@Override
	public ShoppingItemDto createDto(final String skuCode, final int qty) {
		return createDto(skuCode, qty, null);
	}

	private ShoppingItemDto createDto(final String skuCode, final int qty, final ItemConfiguration itemConfiguration) {
		ShoppingItemDto dto = new ShoppingItemDto(skuCode, qty);
		ProductSku productSku = productSkuLookup.findBySkuCode(skuCode);
		return createShoppingItemDtoTree(productSku.getProduct(), dto, qty, itemConfiguration);
	}

	@Override
	public ShoppingItemDto createDto(final Product product, final int qty) {
		ShoppingItemDto dto = createDto(product.getDefaultSku().getSkuCode(), qty);
		dto.setProductCode(product.getCode());
		return dto;
	}

	@Override
	public ShoppingItemDto createDto(final Product product, final int qty, final ItemConfiguration itemConfiguration) {
		ShoppingItemDto dto;
		if (getBundleIdentifier().isBundle(product)) {
			dto = createDto(itemConfiguration.getSkuCode(), qty, itemConfiguration);
		} else {
			dto = createDto(itemConfiguration.getSkuCode(), qty);
		}

		dto.setProductCode(product.getCode());
		return dto;
	}

	/**
	 * Creates a subtree of ShoppingItemDto objects given a Product and a parent DTO.
	 * Where a Product is a multi-sku product, the product's default sku will be used to create the DTO.
	 *
	 * @param product the product represented by the DTO
	 * @param parentDto the parent item (must not be null)
	 * @param qty the quantity of the product to be specified on the dto
	 * @param itemConfiguration The ItemConfiguration for the given product. May be null in which case the defaults are chosen.
	 * @return the subtree
	 */
	private ShoppingItemDto createShoppingItemDtoTree(
			final Product product, final ShoppingItemDto parentDto, final int qty, final ItemConfiguration itemConfiguration) {
		if (getBundleIdentifier().isBundle(product)) {
			ShoppingItemDto childDto = null;
			ProductBundle bundle = (ProductBundle) product;

			for (BundleConstituent bundleConstituent : bundle.getConstituents()) {
				ConstituentItem constituent = bundleConstituent.getConstituent();
				final ItemConfiguration childItemConfiguration;
				if (itemConfiguration == null) {
					childItemConfiguration = null;
				} else {
					childItemConfiguration = itemConfiguration.getChildById(bundleConstituent.getGuid());
				}

				if (childItemConfiguration == null) {
					childDto = createDto(constituent.getProductSku().getSkuCode(), qty);
					childDto.setSelected(bundle.isConstituentAutoSelectable(bundleConstituent));
				} else {
					childDto = createDto(childItemConfiguration.getSkuCode(), qty, childItemConfiguration);
					childDto.setSelected(childItemConfiguration.isSelected());
				}
				childDto.setProductCode(constituent.getProduct().getCode());
				childDto.setProductSkuConstituent(bundleConstituent.getConstituent().isProductSku());
				parentDto.addConstituent(childDto);
			}
		}
		return parentDto;
	}

	private BundleIdentifier getBundleIdentifier() {
		return bundleIdentifier;
	}

	public void setBundleIdentifier(final BundleIdentifier bundleIdentifier) {
		this.bundleIdentifier = bundleIdentifier;
	}

	public void setProductSkuLookup(final ProductSkuLookup productSkuLookup) {
		this.productSkuLookup = productSkuLookup;
	}
}
