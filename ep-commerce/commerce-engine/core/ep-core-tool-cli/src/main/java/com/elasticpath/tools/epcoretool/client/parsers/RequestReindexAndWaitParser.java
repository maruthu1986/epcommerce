/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.tools.epcoretool.client.parsers;

import com.elasticpath.epcoretool.LoggerFacade;
import com.elasticpath.epcoretool.logic.AbstractRequestReindex;
import com.elasticpath.tools.epcoretool.client.CliDB;
import com.elasticpath.tools.epcoretool.client.CliParser;

/**
 * Wraps AbstractRequestReindex logic class with Cli.
 */
public class RequestReindexAndWaitParser extends CliDB implements CliParser {

	@Override
	public void execute(final String param) {
		AbstractRequestReindex logic = new AbstractRequestReindex(getUrl(), getUsername(), getPassword(), getDriverClass(), getMinIdle(),
				getMaxIdle()) {
			@Override
			protected LoggerFacade getLogger() {
				return getLoggerFacade();
			}
		};
		logic.execute(param, true);
	}

	@Override
	public String help() {
		return "\nrequest-reindex-and-wait [<index>]\n"
				+ "\tAdds a rebuild request to the index notification queue and waits for\n"
				+ "\treindexing to complete.\n";
	}

}
