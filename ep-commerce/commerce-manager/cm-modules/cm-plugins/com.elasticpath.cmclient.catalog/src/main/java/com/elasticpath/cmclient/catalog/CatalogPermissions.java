/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.catalog;

/**
 * Permissions class for the catalog plugin.
 */
public class CatalogPermissions {

	/**
	 * Permissions to manager Product/SKU.
	 */
	public static final String MANAGE_PRODUCT_SKU = "MANAGE_PRODUCT_SKU"; //$NON-NLS-1$
	
	/**
	 * Permissions to manage product pricing.
	 */
	//public static final String MANAGE_PRODUCT_PRICING = "MANAGE_PRODUCT_PRICING"; //$NON-NLS-1$
	
	/**
	 * Permission to edit product merchandising associations.
	 */
	public static final String MANAGE_PRODUCT_MERCHANDISING = "MANAGE_PRODUCT_MERCHANDISING"; //$NON-NLS-1$
	
	
	/**
	 * Permission to manage virtual catalog and linked category.
	 */
	public static final String MANAGE_VIRTUAL_CATALOG_LINK_CATEGORY = "MANAGE_VIRTUAL_CATALOG_LINK_CATEGORY"; //$NON-NLS-1$
	

	/**
	 * Permission to manage categories.
	 */
	public static final String CATEGORY_MANAGE = "CATEGORY_MANAGE"; //$NON-NLS-1$
	
	/**
	 * Permission to include/exclude linked categories.
	 */
	public static final String CATEGORY_INCLUDE_EXCLUDE = "CATEGORY_INCLUDE_EXCLUDE"; //$NON-NLS-1$

	/**
	 * Permission to edit catalogs.
	 */
	public static final String CATALOG_MANAGE = "CATALOG_MANAGE"; //$NON-NLS-1$

	
	/**
	 * Permission to include/exclude product.
	 */
	public static final String INCLUDE_EXCLUDE_PRODUCT = "INCLUDE_EXCLUDE_PRODUCT"; //$NON-NLS-1$
	

	/** 
	 * Permission to edit global attributes. 
	 */
	public static final String GLOBAL_ATTRIBUTE_EDIT = "GLOBAL_ATTRIBUTE_EDIT"; //$NON-NLS-1$
}
