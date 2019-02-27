/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.domain.audit;



/**
 * A <code>ChangeOperation</code> that operates on a single root object.
 */
public interface SingleChangeOperation extends ChangeOperation {

	/**
	 * @return the rootObjectName
	 */
	String getRootObjectName();

	/**
	 * @param rootObjectName the rootObjectName to set
	 */
	void setRootObjectName(String rootObjectName);

	/**
	 * @return the rootObjectUid
	 */
	long getRootObjectUid();

	/**
	 * @param rootObjectUid the rootObjectUid to set
	 */
	void setRootObjectUid(long rootObjectUid);

	/**
	 * @return the rootObjectGuid
	 */
	String getRootObjectGuid();

	/**
	 * @param rootObjectGuid the rootObjectGuid to set
	 */
	void setRootObjectGuid(String rootObjectGuid);

}