/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.cmclient.store.targetedselling;

/**
 * Defines the permissions for:
 * 1. Dynamic Content
 * 2. Dynamic Content Delivery
 * 3. Conditional expression.
 * 
 * Content Spaces permissions in other plugin. 
 * 
 */
public class TargetedSellingPermissions {
	
	/**
	 * Permission to
	 *  
	 * a) select
	 * b) manage
	 * c) create
	 * d) delete
	 *   
	 * dynamic content and dynamic content delivery.
	 */
	public static final String DYNAMIC_CONTENT_MANAGE = "DYNAMIC_CONTENT_MANAGE"; //$NON-NLS-1$
	
	/**
	 * Permission to
	 *  
	 * a) select
	 * b) manage
	 * c) create
	 * d) delete
	 *   
	 * named conditional expression.
	 */
	public static final String CONDITIONAL_EXPRESSION_MANAGE = "CONDITIONAL_EXPRESSION_MANAGE"; //$NON-NLS-1$

	/**
	 * Permission to dynamic content delivery.
	 */
	public static final String DYNAMIC_CONTENT_DELIVERY_MANAGE = "DYNAMIC_CONTENT_DELIVERY_MANAGE"; //$NON-NLS-1$

}
