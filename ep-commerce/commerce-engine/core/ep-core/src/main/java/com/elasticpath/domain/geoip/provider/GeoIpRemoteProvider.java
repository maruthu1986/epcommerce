/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.domain.geoip.provider;

/**
 * Remote GeoIP provider interface.
 * Use this interface for providers that provide their services using Web services.
 */
public interface GeoIpRemoteProvider extends GeoIpProvider {

	/**
	 * Sets end point for remote call.
	 *
	 * @param endpoint -  end point for remote call
	 */
	void setEndpoint(String endpoint);

	/**
	 * Sets user name.
	 *
	 * @param username - user name.
	 */
	void setUsername(String username);

	/**
	 *	Sets password.
	 *
	 * @param password - password.
	 */
	void setPassword(String password);

}
