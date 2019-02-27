/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.warehouse;

/**
 * Permissions class for the com.elasticpath.cmclient.warehouse plugin.
 */
public class WarehousePermissions {

	/**
	 * Permissions to edit an order return object.
	 */
	public static final String WAREHOUSE_ORDER_RETURN_EDIT = "WAREHOUSE_ORDER_RETURN_EDIT"; //$NON-NLS-1$

	/**
	 * Permissions to complete shipping.
	 */
	public static final String WAREHOUSE_ORDER_SHIPMENT_COMPLETE = "WAREHOUSE_ORDER_SHIPMENT_COMPLETE"; //$NON-NLS-1$
	
	/**
	 * Permissions to force complete shipping.
	 */
	public static final String WAREHOUSE_FORCE_ORDER_SHIPMENT_COMPLETE = "WAREHOUSE_FORCE_ORDER_SHIPMENT_COMPLETE"; //$NON-NLS-1$
	
	/**
	 * Permissions to manage inventory.
	 */
	public static final String WAREHOUSE_MANAGE_INVENTORY = "WAREHOUSE_MANAGE_INVENTORY"; //$NON-NLS-1$
	
	/**
	 * Permissions to receive inventory.
	 */
	public static final String WAREHOUSE_RECEIVE_INVENTORY = "WAREHOUSE_RECEIVE_INVENTORY"; //$NON-NLS-1$		
}
