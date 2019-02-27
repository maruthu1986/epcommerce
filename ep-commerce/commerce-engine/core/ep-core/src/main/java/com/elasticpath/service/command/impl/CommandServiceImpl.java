/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.service.command.impl;

import com.elasticpath.service.command.Command;
import com.elasticpath.service.command.CommandResult;
import com.elasticpath.service.command.CommandService;

/**
 * Implements command service executing commands.
 */
public class CommandServiceImpl implements CommandService {

	@Override
	public CommandResult execute(final Command command) {
		return command.execute();
	}
}
