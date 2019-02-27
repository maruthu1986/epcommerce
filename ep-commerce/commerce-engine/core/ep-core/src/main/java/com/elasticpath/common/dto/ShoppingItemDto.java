/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.common.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a snapshot of a {@code ProductBundle} which contains MultiSKU constituent items by looking at
 * user's SKU selection.
 * 
 * A ShoppingItemDto is a node in a possible tree of ShoppingItemDtos, and it is a mirror representation
 * of its corresponding {@code ProductBundle}.
 */
public class ShoppingItemDto implements Dto {

	private static final long serialVersionUID = 1L;

	private String guid;
	private String skuCode;
	private final List<ShoppingItemDto> constituents = new ArrayList<>();
	private int quantity;
	private Map<String, String> itemFields;

	private long shoppingItemUidPk;
	private String productCode;

	private boolean selected = true;

	private boolean productSkuConstituent;

	/**
	 * SKU Code of the current node.
	 *
	 * @param skuCode
	 *            the sku code to be set
	 * @param quantity
	 *            the quantity of the item. Can be zero for bundle constituents.
	 */
	public ShoppingItemDto(final String skuCode, final int quantity) {
		this.skuCode = skuCode;
		this.quantity = quantity;
		itemFields = Collections.emptyMap();
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(final String guid) {
		this.guid = guid;
	}

	/**
	 * Returns the product code for the related product.
	 * @return the product code.
	 */
	public String getProductCode() {
		return productCode;
	}



	/**
	 *
	 * @param productCode The product code to set.
	 */
	public void setProductCode(final String productCode) {
		this.productCode = productCode;
	}



	/**
	 * Adds given {@code ShoppingItemDto} representing a bundle constituent to the child nodes.
	 * 
	 * @param child The ShoppingItemDto to be added.
	 */
	public void addConstituent(final ShoppingItemDto child) {
		constituents.add(child);
	}

	/**
	 * Gets all constituents.
	 * 
	 * @return list of {@link ShoppingItemDto}.
	 */
	public List<ShoppingItemDto> getConstituents() {
		return Collections.unmodifiableList(constituents);
	}

	/**
	 * @return the SKU code
	 */
	public String getSkuCode() {
		return skuCode;
	}

	/**
	 * @param skuCode The skuCode to set
	 */
	public void setSkuCode(final String skuCode) {
		this.skuCode = skuCode;
	}

	/**
	 * @param quantity The quantity to set.
	 */
	public void setQuantity(final int quantity) {
		this.quantity = quantity;
	}

	/**
	 * @return The quantity.
	 */
	public int getQuantity() {
		return quantity;
	}

	/**
	 * @param itemFields The item fields to set.
	 */
	public void setItemFields(final Map<String, String> itemFields) {
		this.itemFields = itemFields;
	}

	/**
	 * @return The itemFields
	 */
	public Map<String, String> getItemFields() {
		return itemFields;
	}

	/**
	 * @return The uidPk of the existing shopping item or null if this is a new shopping item.
	 */
	public long getShoppingItemUidPk() {
		return shoppingItemUidPk;
	}

	/**
	 * @param shoppingItemUidPk The uidPk of the existing shopping item.
	 */
	public void setShoppingItemUidPk(final long shoppingItemUidPk) {
		this.shoppingItemUidPk = shoppingItemUidPk;
	}

	/**
	 * 
	 * @param selected if true then this item has been selected by the user.
	 */
	public void setSelected(final boolean selected) {
		this.selected = selected;
	}

	/**
	 * @return true if this item has been selected by the user.
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * Set productSkuConstituent.
	 * @param productSkuConstituent the flag
	 */
	public void setProductSkuConstituent(final boolean productSkuConstituent) {
		this.productSkuConstituent = productSkuConstituent;
	}

	/**
	 * @return <code>true</code> if this dto belongs to a bundle constituent which is a specific sku of a multi sku product.
	 */
	public boolean isProductSkuConstituent() {
		return productSkuConstituent;
	}

	@Override
	public int hashCode() {
		return Objects.hash(guid, productCode, productSkuConstituent, quantity, selected, shoppingItemUidPk, skuCode,
			constituents, itemFields);
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof ShoppingItemDto)) {
			return false;
		}
		ShoppingItemDto other = (ShoppingItemDto) obj;
		return Objects.equals(guid, other.guid)
			&& Objects.equals(constituents, other.constituents)
			&& Objects.equals(itemFields, other.itemFields)
			&& Objects.equals(productCode, other.productCode)
			&& Objects.equals(productSkuConstituent, other.productSkuConstituent)
			&& Objects.equals(quantity, other.quantity)
			&& Objects.equals(selected, other.selected)
			&& Objects.equals(shoppingItemUidPk, other.shoppingItemUidPk)
			&& Objects.equals(skuCode, other.skuCode);
	}
}
