/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.datapopulation.core.context.configurer;

import java.io.File;

/**
 * Provides a sql resource wrapper for {@link com.elasticpath.datapopulation.core.action.impl.RunSqlActionImpl} because
 * it has no knowledge about what sql or context to execute.
 */
public class SqlActionConfiguration {

	private String sqlStatement;
	private File sqlFile;
	private boolean useConnection;
	private boolean readFullScript;

	public String getSqlStatement() {
		return sqlStatement;
	}

	public void setSqlStatement(final String sqlStatement) {
		this.sqlStatement = sqlStatement;
	}

	public File getSqlFile() {
		return sqlFile;
	}

	public void setSqlFile(final File sqlFile) {
		this.sqlFile = sqlFile;
	}

	public boolean isUsingCreatedConnection() {
		return useConnection;
	}

	public void setUsingCreatedConnection(final boolean useConnection) {
		this.useConnection = useConnection;
	}

	public boolean isReadFullScript() {
		return readFullScript;
	}

	public void setReadFullScript(final boolean readFullScript) {
		this.readFullScript = readFullScript;
	}
}
