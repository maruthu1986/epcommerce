/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.domain.audit;

import com.elasticpath.persistence.api.ChangeType;
import com.elasticpath.persistence.api.Persistable;

/**
 *  An operation that results in changes.
 */
public interface ChangeOperation extends Persistable {

	/**
	 * Get the business transaction this operation belongs to.
	 *
	 * @return the changeTransaction
	 */
	ChangeTransaction getChangeTransaction();

	/**
	 * Set the business transaction this operation belongs to.
	 *
	 * @param changeTransaction the changeTransaction to set
	 */
	void setChangeTransaction(ChangeTransaction changeTransaction);

	/**
	 * @return the order
	 */
	int getOperationOrder();

	/**
	 * @param operationOrder the order to set
	 */
	void setOperationOrder(int operationOrder);


	/**
	 * Get the change type.
	 *
	 * @return the changeType
	 */
	ChangeType getChangeType();

	/**
	 * Set the change type.
	 *
	 * @param changeType the changeType to set
	 */
	void setChangeType(ChangeType changeType);

}
