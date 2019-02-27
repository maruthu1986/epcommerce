package com.elasticpath.selenium.domainobjects;

import java.util.List;

/**
 * Product Type class.
 */
public class ProductType {
	private String productTypeName;
	private List<String> attribute;
	private List<String> cartItemModifierGroup;


	public String getProductTypeName() {
		return productTypeName;
	}

	public void setProductTypeName(final String productTypeName) {
		this.productTypeName = productTypeName;
	}

	public List<String> getAttribute() {
		return attribute;
	}

	public void setAttribute(final List<String> attribute) {
		this.attribute = attribute;
	}

	public List<String> getCartItemModifierGroup() {
		return cartItemModifierGroup;
	}

	public void setCartItemModifierGroup(final List<String> cartItemModifierGroup) {
		this.cartItemModifierGroup = cartItemModifierGroup;
	}

}
