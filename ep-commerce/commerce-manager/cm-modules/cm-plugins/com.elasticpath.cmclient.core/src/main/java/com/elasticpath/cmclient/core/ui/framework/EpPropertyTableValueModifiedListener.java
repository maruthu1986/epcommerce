/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core.ui.framework;

import java.util.Map.Entry;

/**
 * Listener for <code>EpPropertyTableControl</code>. Listens for major events while property table modification.
 */
public interface EpPropertyTableValueModifiedListener {
	/**
	 * This method is fired when specific Entry's value is going to be modified by user. Implementation can provide flushing error message.
	 * 
	 * @param entry properties' entry which is subject for modification.
	 */
	void onPrepareForModification(Entry<String, String> entry);

	/**
	 * This method is fired when specific Entry's value is being modified by user. Implementation can provide new property value validation or
	 * flushing error message.
	 * 
	 * @param entry properties' entry which is being modified.
	 * @param newPropertyValue new string value that will be assigned to the entry's value.
	 * @return true if the newPropertyValue can be assigned to entry, false otherwise. In this case initial value for the property will be left.
	 */
	boolean onModification(Entry<String, String> entry, String newPropertyValue);
	
	/**
	 * This method is fired when specific Entry's value was modified.
	 * 
	 * @param entry properties' entry which was modified.
	 */
	void onPostModification(Entry<String, String> entry);
}
