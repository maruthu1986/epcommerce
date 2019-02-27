/**
 * Copyright (c) Elastic Path Software Inc., 2011
 */
package com.elasticpath.commons.listeners;

import com.elasticpath.tags.Tag;

/**
 * Interface for listeners of tag set events.
 */
public interface TagSetListener {

	/**
	 * generic event handler.
	 * @param key the Tag guid
	 * @param tag the tag
	 */
	void onEvent(String key, Tag tag);
	
}
