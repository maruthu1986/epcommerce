/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.domain.contentspace;

import com.elasticpath.persistence.api.Entity;

/**
 * Defines an area where a piece of {@link DynamicContent} can be displayed.
 */
public interface ContentSpace extends Entity {

	/**
	 * Get the name of the content space.
	 * @return target id
	 */
	String getTargetId();

	/**
	 * Set the name of the content space.
	 * @param targetId target id.
	 */
	void setTargetId(String targetId);

	/**
	 * Get the description of target.
	 * @return description.
	 */
	String getDescription();

	/**
	 * Set target description.
	 * @param description - target description.
	 */
	void setDescription(String description);


}
