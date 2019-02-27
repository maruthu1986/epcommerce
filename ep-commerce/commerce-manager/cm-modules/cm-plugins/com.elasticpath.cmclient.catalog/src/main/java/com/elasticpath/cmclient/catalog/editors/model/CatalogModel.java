/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.catalog.editors.model;

import com.elasticpath.cmclient.core.editors.TableItems;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.cartmodifier.CartItemModifierGroup;
import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.CategoryType;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;

/**
 * Model for the catalog editor.
 */
public interface CatalogModel {

	/**
	 * Gets the catalog.
	 *
	 * @return the catalog
	 */
	Catalog getCatalog();

	/**
	 * Sets the catalog.
	 *
	 * @param catalog the catalog
	 */
	void setCatalog(Catalog catalog);

	/**
	 * Gets the sku option table items.
	 * @return the sku option table items
	 * */
	TableItems<SkuOption> getSkuOptionTableItems();

	/**
	 * Gets the sku option value table items.
	 * @return the sku option value table items
	 * */
	TableItems<SkuOptionValue> getSkuOptionValueTableItems();

	/**
	 * Gets the category type table items.
	 * @return the category type table items
	 * */
	TableItems<CategoryType> getCategoryTypeTableItems();

	/**
	 * Gets the attribute table items.
	 * @return the attribute table items
	 * */
	TableItems<Attribute> getAttributeTableItems();

	/**
	* Gets the Cart Item Modifiers Groups table items.
	 * @return the attribute table items
	 * */
	TableItems<CartItemModifierGroup> getCartItemModifierGroupTableItems();

	/**
	 * Gets the product type table items.
	 * @return the product type table items
	 * */
	TableItems<ProductType> getProductTypeTableItems();

	/**
	 * Gets the brand table items.
	 * @return the brand table items
	 * */
	TableItems<Brand> getBrandTableItems();

	/**
	 * Clears all change sets.
	 */
	void clearAllChangeSets();

}