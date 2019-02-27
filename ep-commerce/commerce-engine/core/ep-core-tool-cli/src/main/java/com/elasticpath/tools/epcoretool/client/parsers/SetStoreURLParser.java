/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.tools.epcoretool.client.parsers;

import com.elasticpath.epcoretool.LoggerFacade;
import com.elasticpath.epcoretool.logic.AbstractSetStoreURL;
import com.elasticpath.tools.epcoretool.client.CliDB;
import com.elasticpath.tools.epcoretool.client.CliParser;

/**
 * Update the store url in the cli.
 */
public class SetStoreURLParser extends CliDB implements CliParser {
	private static final String SET_STORE_URL_FORMAT = "set-store-url <storecode>=<url>";

	@Override
	public void execute(final String param) {
		AbstractSetStoreURL logic = new AbstractSetStoreURL(getUrl(), getUsername(), getPassword(), getDriverClass(), getMinIdle(),
				getMaxIdle()) {

			@Override
			protected LoggerFacade getLogger() {
				return getLoggerFacade();
			}
		};

		String[] params = param.split("=");
		if (params.length == 2) {
			logic.execute(params[0], params[1]);
		} else {
			throw new IllegalArgumentException("set-store-url should have the format: " + SET_STORE_URL_FORMAT);
		}
	}

	@Override
	public String help() {
		return "\n" + SET_STORE_URL_FORMAT + "\n" + "\tUpdate store url.\n";
	}
}
