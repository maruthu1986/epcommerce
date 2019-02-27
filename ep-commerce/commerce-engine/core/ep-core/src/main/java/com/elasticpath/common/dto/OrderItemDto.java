/*
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.common.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.elasticpath.commons.tree.TreeNode;
import com.elasticpath.domain.catalog.DigitalAsset;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.inventory.InventoryDto;
import com.elasticpath.money.Money;

/**
 * Data Transfer Object for {@code OrderItem}s (i.e. {@code OrderSku}s).
 */
public class OrderItemDto extends ShoppingItemDto implements TreeNode<OrderItemDto> {

	private static final long serialVersionUID = 4037391018277138627L;

	private DigitalAsset digitalAsset;
	private String displayName;
	private String image;
	
	private String displaySkuOptions;
	private boolean allocated;
	private ProductSku productSku;
	private Price price;
	private Money listPrice;
	private Money unitPrice;
	private Money total;
	private boolean unitLessThanList;
	private Money dollarSavings;
	private boolean isABundle;
	private InventoryDto inventoryDto;
	private boolean calculatedBundle;
	private boolean calculatedBundleItem;

	/**
	 * Default constructor.
	 */
	public OrderItemDto() {
		super("", 0);
	}
	
	/**
	 * Gets the digital asset belong to this order SKU.
	 * 
	 * @return the digital asset belong to this order SKU
	 */
	public DigitalAsset getDigitalAsset() {
		return digitalAsset;
	}
	
	/**
	 * Get the product's display name.
	 * 
	 * @return the product's display name.
	 */
	public String getDisplayName() {
		return displayName;
	}

	
	/**
	 * Get the product's image path.
	 * 
	 * @return the product's image path.
	 */
	public String getImage() {
		return image;
	}
	
	@Override
	public List<OrderItemDto> getChildren() {
		List<OrderItemDto> children = new ArrayList<>();
		for (ShoppingItemDto dto : getConstituents()) {
			OrderItemDto item = (OrderItemDto) dto;
			children.add(item);
		}
		return children;
	}

	@Override
	public void addChild(final OrderItemDto child) {
		addConstituent(child);
	}

	@Override
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof OrderItemDto)) {
			return false;
		}
		if (!super.equals(other)) {
			return false;
		}

		OrderItemDto that = (OrderItemDto) other;
		return allocated == that.allocated
			   && unitLessThanList == that.unitLessThanList
			   && isABundle == that.isABundle
			   && calculatedBundle == that.calculatedBundle
			   && calculatedBundleItem == that.calculatedBundleItem
			   && Objects.equals(digitalAsset, that.digitalAsset)
			   && Objects.equals(displayName, that.displayName)
			   && Objects.equals(image, that.image)
			   && Objects.equals(displaySkuOptions, that.displaySkuOptions)
			   && Objects.equals(productSku, that.productSku)
			   && Objects.equals(price, that.price)
			   && Objects.equals(listPrice, that.listPrice)
			   && Objects.equals(unitPrice, that.unitPrice)
			   && Objects.equals(total, that.total)
			   && Objects.equals(dollarSavings, that.dollarSavings)
			   && Objects.equals(inventoryDto, that.inventoryDto);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), digitalAsset, displayName, image, displaySkuOptions, allocated, productSku, price,
			listPrice, unitPrice, total, unitLessThanList, dollarSavings, isABundle, inventoryDto, calculatedBundle, calculatedBundleItem);
	}

	/**
	 * 
	 * @param displayName The display name.
	 */
	public void setDisplayName(final String displayName) {
		this.displayName = displayName;
	}

	/**
	 * 
	 * @param image The name of the image.
	 */
	public void setImage(final String image) {
		this.image = image;
	}

	/**
	 * 
	 * @param displaySkuOptions The display sku options.
	 */
	public void setDisplaySkuOptions(final String displaySkuOptions) {
		this.displaySkuOptions = displaySkuOptions;
	}

	
	/**
	 * 
	 * @param allocated True if inventory is allocated.
	 */
	public void setAllocated(final boolean allocated) {
		this.allocated = allocated;
	}

	/**
	 * 
	 * @param productSku The product sku to set.
	 */
	public void setProductSku(final ProductSku productSku) {
		this.productSku = productSku;
	}

	/**
	 * 
	 * @param digitalAsset The digital asset.
	 */
	public void setDigitalAsset(final DigitalAsset digitalAsset) {
		this.digitalAsset = digitalAsset;
	}


	/**
	 * Getter for {@link Price}.
	 *
	 * @return {@link Price}
	 */
	public Price getPrice() {
		return price;
	}

	/**
	 * Setter for {@link Price}.
	 *
	 * @param price {@link Price}
	 */
	public void setPrice(final Price price) {
		this.price = price;
	}

	/**
	 * 
	 * @param listPrice The list price.
	 */
	public void setListPrice(final Money listPrice) {
		this.listPrice = listPrice;
	}

	/**
	 * 
	 * @param unitPrice The unit price.
	 */
	public void setUnitPrice(final Money unitPrice) {
		this.unitPrice = unitPrice;
	}

	/**
	 * 
	 * @param unitLessThanList True if unit less than list.
	 */
	public void setUnitLessThanList(final boolean unitLessThanList) {
		this.unitLessThanList = unitLessThanList;
	}

	/**
	 * Gets for the total of the {@link com.elasticpath.domain.shoppingcart.ShoppingItem}.
	 * @return money {@link Money}.
	 */
	public Money getTotal() {
		return total;
	}

	/**
	 * Setter for the total of the {@link com.elasticpath.domain.shoppingcart.ShoppingItem}.
	 * @param total {@link Money}.
	 */
	public void setTotal(final Money total) {
		this.total = total;
	}

	/**
	 * 
	 * @param dollarSavings The dollar savings. 
	 */
	public void setDollarSavings(final Money dollarSavings) {
		this.dollarSavings = dollarSavings;
	}

	/**
	 * 
	 * @return The display sku options.
	 */
	public String getDisplaySkuOptions() {
		return displaySkuOptions;
	}

	/**
	 * 
	 * @return True if inventory has been allocated.
	 */
	public boolean isAllocated() {
		return allocated;
	}

	/**
	 * 
	 * @return the product sku
	 */
	public ProductSku getProductSku() {
		return productSku;
	}

	/**
	 * 
	 * @return The list price.
	 */
	public Money getListPrice() {
		return listPrice;
	}

	/**
	 * 
	 * @return The unit price.
	 */
	public Money getUnitPrice() {
		return unitPrice;
	}

	/**
	 * 
	 * @return True if the unit price is less than the list price.
	 */
	public boolean isUnitLessThanList() {
		return unitLessThanList;
	}

	/**
	 * 
	 * @return The difference between the unit price and the list price.
	 */
	public Money getDollarSavings() {
		return dollarSavings;
	}
	
	/**
	 * 
	 * @param isBundle Is this a ProductBundle OrderItem?
	 */
	public void setIsBundle(final boolean isBundle) {
		this.isABundle = isBundle;
	}
	
	/**
	 * 
	 * @return true if this is a ProductBundle OrderItem.
	 */
	public boolean isBundle() {
		return this.isABundle;
	}
	
	/**
	 * @return the inventory
	 */
	public InventoryDto getInventory() {
		return inventoryDto;
	}
	
	/**
	 * @param inventoryDto the inventory to set
	 */
	public void setInventory(final InventoryDto inventoryDto) {
		this.inventoryDto = inventoryDto;
	}
	
	/**
	 * @return true if this OrderItemDto is a calculated bundle.
	 */
	public boolean isCalculatedBundle() {
		return calculatedBundle;
	}

	/**
	 * @param calculatedBundle whether this OrderItemDto is a calculated bundle or not.
	 */
	public void setCalculatedBundle(final boolean calculatedBundle) {
		this.calculatedBundle = calculatedBundle;
	}

	/**
	 * @return true if this OrderItemDto is a calculated bundle item.
	 */
	public boolean isCalculatedBundleItem() {
		return calculatedBundleItem;
	}

	/**
	 * @param calculatedBundleItem whether this OrderItemDto is a calculated bundle item or not.
	 */
	public void setCalculatedBundleItem(final boolean calculatedBundleItem) {
		this.calculatedBundleItem = calculatedBundleItem;
	}
}
