/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.domain.cmuser;

import org.springframework.security.core.GrantedAuthority;

import com.elasticpath.persistence.api.Persistable;

/**
 * <code>UserPermission</code> represents an granted authority for a certain operation.
 */
public interface UserPermission extends Persistable, GrantedAuthority {
	/**
	 * Initializes the <code>UserPermission</code> object given its authority. Call setElasticPath before initializing.
	 */
	void init();

	/**
	 * Gets the authority for this <code>UserPermission</code>.
	 *
	 * @return the authority as an identifier of the UserPermission.
	 */
	@Override
	String getAuthority();

	/**
	 * Sets the authority for this <code>UserPermission</code>.
	 *
	 * @param authority the identifier of the UserPermission.
	 */
	void setAuthority(String authority);

}
