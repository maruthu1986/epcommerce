/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.datapopulation.core.action.impl;

import java.io.File;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.datapopulation.core.action.AbstractDataSourceAccessAction;
import com.elasticpath.datapopulation.core.context.DataPopulationContext;
import com.elasticpath.datapopulation.core.context.configurer.SqlActionConfiguration;
import com.elasticpath.datapopulation.core.exceptions.SqlActionException;
import com.elasticpath.datapopulation.core.service.SqlService;

/**
 * This action calls SqlService to perform the execution on a sql file or statement.
 */
public class RunSqlActionImpl extends AbstractDataSourceAccessAction {


	@Autowired
	private SqlService sqlService;

	@Override
	public void execute(final DataPopulationContext context) {
		Object wrapper = context.getActionConfiguration();
		if (!(wrapper instanceof SqlActionConfiguration)) {
			throw new SqlActionException("Error: The Sql Action Configuration is null or not of the required type.");
		}

		SqlActionConfiguration sqlActionConfiguration = (SqlActionConfiguration) wrapper;
		String sqlStatement = sqlActionConfiguration.getSqlStatement();
		File sqlFile = sqlActionConfiguration.getSqlFile();
		boolean useConnection = sqlActionConfiguration.isUsingCreatedConnection();
		boolean readFullScript = sqlActionConfiguration.isReadFullScript();

		// initialize the sql service data source connections
		DataSource dataSource = createDataSource(useConnection);
		sqlService.executeSql(sqlStatement, sqlFile, dataSource, readFullScript);
	}

}
