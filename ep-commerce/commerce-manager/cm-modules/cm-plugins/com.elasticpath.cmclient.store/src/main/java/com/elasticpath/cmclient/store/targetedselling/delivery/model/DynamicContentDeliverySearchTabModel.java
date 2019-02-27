/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.store.targetedselling.delivery.model;


/**
 * View model, represent search criteria for dynamic content assignment list. 
 */
public interface DynamicContentDeliverySearchTabModel {

	/**
	 * Get the name of {@link com.elasticpath.domain.targetedselling.DynamicContentDelivery}.
	 * @return name of {@link com.elasticpath.domain.targetedselling.DynamicContentDelivery}.
	 */
	String getName();

	/**
	 * Set the name of {@link com.elasticpath.domain.targetedselling.DynamicContentDelivery}.
	 * @param name name of {@link com.elasticpath.domain.targetedselling.DynamicContentDelivery}.
	 */
	void setName(String name);

	/**
	 * Get the name of Dynamic Content instance.
	 * @return name of Dynamic Content instance.
	 */
	String getDynamicContentName();

	/**
	 * Set name of Dynamic Content instance.
	 * @param dynamicContentName name of Dynamic Content instance.
	 */
	void setDynamicContentName(String dynamicContentName);

	/**
	 * Get the name of Assignment Target (Content space).
	 * @return name of Assignment Target (Content space)
	 */
	String getContentspaceId();

	/**
	 * Set the name of Assignment Target (Content space).
	 * @param contentspaceId name of Assignment Target (Content space)
	 */
	void setContentspaceId(String contentspaceId);
	
}
