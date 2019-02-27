/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package liquibase.ext.elasticpath;

import liquibase.logging.LogFactory;
import liquibase.logging.Logger;

/**
 * Version of the MSSQL Database that uses NVARCHAR for VARCHAR fields.
 */
public class MSSQLDatabase extends liquibase.database.core.MSSQLDatabase {
	private static final Logger LOGGER = LogFactory.getInstance().getLog();

	// A value of 10 or higher will override the base implementation.
	private static final int PRIORITY = 10;

	@Override
	public int getPriority() {
		return PRIORITY;
	}

	@Override
	public String escapeDataTypeName(final String dataTypeName) {
		if ("varchar".equals(dataTypeName)) {
			LOGGER.debug("Converting varchar to nvarchar");
			return super.escapeDataTypeName("nvarchar");
		}
		return super.escapeDataTypeName(dataTypeName);
	}
}
