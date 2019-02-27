/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.cucumber;

/**
 * {@code CucumberConstants} contains field names used by Cucumber step definitions to pass testing data from Cucumber feature.
 */
public class CucumberConstants {
	
	/** Ordering of 1 for Cucumber hooked method. **/
	public static final int CUCUMBER_HOOK_METHOD_ORDERING_1 = 1;
	
	/** Ordering of 100 for Cucumber hooked method. **/
	public static final int CUCUMBER_HOOK_METHOD_ORDERING_100 = 100;
	
	/**  Field name of price for product, product sku, delivery option, etc. **/
	public static final String FIELD_PRICE = "price";
	
	/**  Field name of skuCode for product sku, shopping item, returning item and exchanging item, etc. **/
	public static final String FIELD_SKU_CODE = "skuCode";
	
	/**  Field name of discount for shopping item, order sku, order return sku, etc. **/
	public static final String FIELD_DISCOUNT = "discount";
	
	/**  Field name of type. **/
	public static final String FIELD_TYPE = "type";
	
	/**  Field name of digital. **/
	public static final String FIELD_DIGITAL = "digital";
	
	/**  Field name of physical. **/
	public static final String FIELD_PHYSICAL = "physical";
	
	/**  Field name of region for tax jurisdiction, shipping region, etc. **/
	public static final String FIELD_REGION = "region";
	
	/**  Field name of shipping region string. **/
	public static final String FIELD_REGION_STRING = "regionString";
	
	/**  Field name of shipping service level name. **/
	public static final String FIELD_SHIPPING_SERVICE_LEVEL_CODE = "shipping service level code";
	
}
