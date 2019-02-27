/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.cmclient.core;

/**
 * Object GUID aware object.
 */
public interface ObjectGuidReceiver {

	/**
	 * Sets the object GUID to the recipient.
	 * 
	 * @param objectGuid the object GUID
	 */
	void setObjectGuid(String objectGuid);
}
