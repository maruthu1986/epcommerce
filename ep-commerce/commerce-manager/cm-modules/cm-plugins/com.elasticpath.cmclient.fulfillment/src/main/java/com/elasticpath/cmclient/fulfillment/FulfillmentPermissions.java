/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.fulfillment;

/**
 * Permissions class for the fulfillment plugin.
 */
public class FulfillmentPermissions {

	/**
	 * Permissions to edit an order object.
	 */
	public static final String ORDER_EDIT = "FULFILLMENT_ORDER_EDIT"; //$NON-NLS-1$

	/**
	 * Permissions to edit a customer.
	 */
	public static final String CUSTOMER_EDIT = "CUSTOMER_EDIT"; //$NON-NLS-1$

	/**
	 * Permissions to view the full credit card numbers.
	 */
	public static final String VIEW_FULL_CREDITCARD_NUMBER = "VIEW_CREDITCARD_NUMBER"; //$NON-NLS-1$

	/**
	 * Permissions to search for all the stores.
	 */
	public static final String SEARCH_ALL_STORES = "SEARCH_ALL_STORES"; //$NON-NLS-1$
	
	/**
	 * Permissions to unlock orders.
	 */
	public static final String ORDER_UNLOCK = "ORDER_UNLOCK"; //$NON-NLS-1$

	/**
	 * Permissions to create / edit returns.
	 */
	public static final String CREATE_EDIT_RETURNS = "CREATE_EDIT_RETURNS"; //$NON-NLS-1$
	
	/**
	 * Permissions to create / edit exchanges.
	 */
	public static final String CREATE_EDIT_EXCHANGES = "CREATE_EDIT_EXCHANGES"; //$NON-NLS-1$
	
	/**
	 * Permissions to create refunds.
	 */
	public static final String CREATE_REFUND = "CREATE_REFUND"; //$NON-NLS-1$
	
	/**
	 * Permissions to edit gift certificate recipient in e-shipment section.
	 */
	public static final String EDIT_GIFT_CERTIFICATE_RECIPIENT = "EDIT_GIFT_CERTIFICATE_RECIPIENT";  //$NON-NLS-1$
	
	/**
	 * Permission to manage customer segment associations.
		*/
	public static final String ASSIGN_CUSTOMER_SEGMENTS = "ASSIGN_CUSTOMER_SEGMENTS";  //$NON-NLS-1$

	/**
	 * Permission to manage customer personal details.
	 */
	public static final String DATA_POLICIES_MANAGE = "ADMIN_DATA_POLICIES_MANAGE";  //$NON-NLS-1$
}
