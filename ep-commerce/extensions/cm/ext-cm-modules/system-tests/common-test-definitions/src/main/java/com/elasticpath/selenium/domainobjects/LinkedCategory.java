package com.elasticpath.selenium.domainobjects;

/**
 * Linked Category class.
 */
public class LinkedCategory extends Category {
	private String linkedCategoryName;
	private String masterCatalog;
	private String catalog;

	public String getMasterCatalog() {
		return masterCatalog;
	}

	public void setMasterCatalog(final String masterCatalog) {
		this.masterCatalog = masterCatalog;
	}

	public String getCatalog() {
		return catalog;
	}

	public void setCatalog(final String catalog) {
		this.catalog = catalog;
	}

	public String getLinkedCategoryName() {
		return linkedCategoryName;
	}

	public void setLinkedCategoryName(final String linkedCategoryName) {
		this.linkedCategoryName = linkedCategoryName;
	}
}
