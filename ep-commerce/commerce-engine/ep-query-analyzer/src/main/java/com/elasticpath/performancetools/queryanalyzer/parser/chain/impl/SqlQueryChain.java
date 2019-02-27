/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.performancetools.queryanalyzer.parser.chain.impl;

import java.io.BufferedReader;

import com.elasticpath.performancetools.queryanalyzer.beans.JPAQuery;
import com.elasticpath.performancetools.queryanalyzer.beans.Operation;
import com.elasticpath.performancetools.queryanalyzer.beans.QueryStatistics;
import com.elasticpath.performancetools.queryanalyzer.parser.ObjectWrapper;
import com.elasticpath.performancetools.queryanalyzer.parser.chain.AbstractParserChain;
import com.elasticpath.performancetools.queryanalyzer.utils.Markers;
import com.elasticpath.performancetools.queryanalyzer.utils.QueryProcessor;

/**
 * Parser chain that parses SQL query from the provided log line.
 */
public class SqlQueryChain extends AbstractParserChain {

	@Override
	public void parse(final BufferedReader reader,
					  final ObjectWrapper<String> line,
					  final ObjectWrapper<String> jpaLine,
					  final ObjectWrapper<Operation> operation,
					  final QueryStatistics queryStatistics,
					  final ObjectWrapper<JPAQuery> jpaQuery) throws Exception {

		if (isApplicableTo(line.getValue())) {
			line.setValue(QueryProcessor.processSQLQueryLine(line.getValue(), reader, jpaQuery.getValue()));
		}

		getNextChain().parse(reader, line, jpaLine, operation, queryStatistics, jpaQuery);
	}

	@Override
	protected boolean isApplicableTo(final String line) {
		return line != null && line.contains(Markers.SQL_QUERY_MARKER);
	}
}
