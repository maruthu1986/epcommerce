/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.catalog;

import java.io.Serializable;

/**
 * Object that wraps the result of a "canDelete" check on the service
 * level.
 */
public interface CanDeleteObjectResult extends Serializable {
	
	/**
	 * @return true if object can be deleted (false otherwise).
	 */
	boolean canDelete();
	
	/**
	 * @return integer identifier that defined the reason why this
	 * object cannot be deleted. Best practices advice: for {@link #canDelete} = true
	 * the reason should return zero, for {@link #canDelete} = false the reason
	 * should be greater than 0.
	 */
	int getReason();
	
	/**
	 * @return additional message for reason (if one exists)
	 *         if {@link #canDelete()} is true then message is null.
	 */
	String getMessage();

}
