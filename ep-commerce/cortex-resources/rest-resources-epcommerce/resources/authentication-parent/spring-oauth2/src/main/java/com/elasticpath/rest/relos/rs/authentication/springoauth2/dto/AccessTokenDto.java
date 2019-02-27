/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.relos.rs.authentication.springoauth2.dto;

import java.util.Date;
import java.util.List;

import com.elasticpath.rest.schema.ResourceEntity;

/**
 * Represents an access token.
 */
public interface AccessTokenDto extends ResourceEntity {

	/**
	 * Gets the token id.
	 *
	 * @return the token id
	 */
	String getTokenId();

	/**
	 * Sets the token id.
	 *
	 * @param tokenId the token id
	 * @return the access token dto
	 */
	AccessTokenDto setTokenId(String tokenId);

	/**
	 * Gets the expiry date.
	 *
	 * @return the expiry date
	 */
	Date getExpiryDate();

	/**
	 * Sets the expiry date.
	 *
	 * @param expiryDate the expiry date
	 * @return the access token dto
	 */
	AccessTokenDto setExpiryDate(Date expiryDate);

	/**
	 * Gets the token type.
	 *
	 * @return the token type
	 */
	String getTokenType();

	/**
	 * Sets the token type.
	 *
	 * @param tokenType the token type
	 * @return the access token dto
	 */
	AccessTokenDto setTokenType(String tokenType);

	/**
	 * Gets the userId.
	 *
	 * @return the userId.
	 */
	String getUserId();

	/**
	 * Sets the user name.
	 *
	 * @param userId the user name
	 * @return the access token dto
	 */
	AccessTokenDto setUserId(String userId);

	/**
	 * Gets the scope.
	 *
	 * @return the scope
	 */
	String getScope();

	/**
	 * Sets the scope.
	 *
	 * @param scope the store code
	 * @return the access token dto
	 */
	AccessTokenDto setScope(String scope);

	/**
	 * Gets the roles.
	 *
	 * @return the role
	 */
	List<String> getRoles();

	/**
	 * Sets the roles.
	 *
	 * @param roles the roles
	 * @return the access token dto
	 */
	AccessTokenDto setRoles(Iterable<String> roles);

	/**
	 * Gets the role.
	 *
	 * @return the role
	 */
	String getRole();

	/**
	 * Sets the role.
	 *
	 * @param role the role
	 * @return the access token dto
	 */
	AccessTokenDto setRole(String role);
}
