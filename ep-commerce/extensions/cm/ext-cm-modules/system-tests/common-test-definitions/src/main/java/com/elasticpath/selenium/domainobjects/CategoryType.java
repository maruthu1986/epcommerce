package com.elasticpath.selenium.domainobjects;

import java.util.List;

/**
 * Category Type Class.
 */
public class CategoryType {
	private String categoryTypeName;
	private List<String> attribute;

	public String getCategoryTypeName() {
		return categoryTypeName;
	}

	public void setCategoryTypeName(final String categoryTypeName) {
		this.categoryTypeName = categoryTypeName;
	}

	public List<String> getAttribute() {
		return attribute;
	}

	public void setAttribute(final List<String> attribute) {
		this.attribute = attribute;
	}
}
