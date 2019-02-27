/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.util;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.service.ClientService;
import org.eclipse.rap.rwt.client.service.UrlLauncher;

/**
 * Util class for creating various RWT client services.
 */
public final class ServiceUtil {

	private ServiceUtil() {
		//private constructor
	}

	/**
	 * Generic method for creating any supported RWT client service.
	 *
	 * @param serviceClass the client service class
	 * @param <T> the service type
	 * @return the instance of the requested service.
	 */
	public static <T extends ClientService> T getRWTService(final Class<T> serviceClass) {
		return RWT.getClient().getService(serviceClass);
	}

	/**
	 * .
	 * @return {@link UrlLauncher} service
	 */
	public static UrlLauncher getUrlLauncherService() {
		return getRWTService(UrlLauncher.class);
	}
}
