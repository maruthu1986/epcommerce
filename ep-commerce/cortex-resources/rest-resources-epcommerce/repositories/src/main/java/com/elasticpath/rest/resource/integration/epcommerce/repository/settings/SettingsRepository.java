/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.settings;

import io.reactivex.Maybe;

/**
 * Repository for Settings Values.
 */
public interface SettingsRepository {

	/**
	 * Retrieves the setting value for a setting path.
	 *
	 * @param path    the path to setting
	 * @param context the context
	 * @param <T>     the setting value type
	 * @return the string setting value
	 */
	<T> Maybe<T> getSetting(String path, String context);

}
