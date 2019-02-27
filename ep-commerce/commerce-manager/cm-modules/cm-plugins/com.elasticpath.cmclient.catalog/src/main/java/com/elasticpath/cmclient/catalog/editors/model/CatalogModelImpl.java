/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */

package com.elasticpath.cmclient.catalog.editors.model;

import java.io.Serializable;

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
 * The catalog model implementation.
 */
public class CatalogModelImpl implements Serializable, CatalogModel {

	private static final long serialVersionUID = 1L;

	private Catalog catalog;

	private TableItems<SkuOption> skuOptionTableItems;

	private TableItems<SkuOptionValue> skuOptionValueTableItems;

	private TableItems<CategoryType> categoryTypeTableItems;

	private TableItems<Attribute> attributeTableItems;

	private TableItems<CartItemModifierGroup> cartItemModifierGroupTableItems;

	private TableItems<ProductType> productTypeTableItems;

	private TableItems<Brand> brandTableItems;

	/**
	 * Default constructor for the catalog model.
	 * @param catalog the catalog
	 */
	public CatalogModelImpl(final Catalog catalog) {
		this.catalog = catalog;
		initializeTableItems();
	}

	private void initializeTableItems() {
		skuOptionTableItems = new TableItems<>();
		skuOptionValueTableItems = new TableItems<>();
		categoryTypeTableItems = new TableItems<>();
		attributeTableItems = new TableItems<>();
		cartItemModifierGroupTableItems = new TableItems<>();
		productTypeTableItems = new TableItems<>();
		brandTableItems = new TableItems<>();
	}

	@Override
	public Catalog getCatalog() {
		return catalog;
	}

	@Override
	public void setCatalog(final Catalog catalog) {
		this.catalog = catalog;
	}

	@Override
	public TableItems<SkuOption> getSkuOptionTableItems() {
		return skuOptionTableItems;
	}

	@Override
	public TableItems<SkuOptionValue> getSkuOptionValueTableItems() {
		return skuOptionValueTableItems;
	}

	@Override
	public TableItems<CategoryType> getCategoryTypeTableItems() {
		return categoryTypeTableItems;
	}

	@Override
	public TableItems<Attribute> getAttributeTableItems() {
		return attributeTableItems;
	}

	@Override
	public TableItems<ProductType> getProductTypeTableItems() {
		return productTypeTableItems;
	}

	@Override
	public TableItems<CartItemModifierGroup> getCartItemModifierGroupTableItems() {
		return cartItemModifierGroupTableItems;
	}

	@Override
	public TableItems<Brand> getBrandTableItems() {
		return brandTableItems;
	}

	@Override
	public void clearAllChangeSets() {
		initializeTableItems();
	}

}
