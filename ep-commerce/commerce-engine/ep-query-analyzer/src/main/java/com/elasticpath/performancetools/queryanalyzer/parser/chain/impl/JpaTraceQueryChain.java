/*
 * Copyright © 2018 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.performancetools.queryanalyzer.parser.chain.impl;

import java.io.BufferedReader;

import com.elasticpath.performancetools.queryanalyzer.beans.JPAQuery;
import com.elasticpath.performancetools.queryanalyzer.beans.Operation;
import com.elasticpath.performancetools.queryanalyzer.beans.QueryStatistics;
import com.elasticpath.performancetools.queryanalyzer.parser.ObjectWrapper;
import com.elasticpath.performancetools.queryanalyzer.parser.chain.AbstractParserChain;
import com.elasticpath.performancetools.queryanalyzer.utils.Markers;
import com.elasticpath.performancetools.queryanalyzer.utils.Patterns;
import com.elasticpath.performancetools.queryanalyzer.utils.Utils;

/**
 * Parser chain that parses JPA TRACE part from the provided log line.
 */
public class JpaTraceQueryChain extends AbstractParserChain {

	@Override
	public void parse(final BufferedReader reader,
					  final ObjectWrapper<String> line,
					  final ObjectWrapper<String> jpaLine,
					  final ObjectWrapper<Operation> operation,
					  final QueryStatistics queryStatistics,
					  final ObjectWrapper<JPAQuery> jpaQuery) throws Exception {

		if (isApplicableTo(line.getValue())) {
			jpaLine.setValue(line.getValue());
			if (operation.getValue().getJpaKickedInAt() == null) {
				Utils.setOperationJpaStartStopTimestamp(Patterns.TIMESTAMP_PATTERN.matcher(line.getValue()), operation.getValue(), false);
			}
		}
		getNextChain().parse(reader, line, jpaLine, operation, queryStatistics, jpaQuery);
	}

	@Override
	protected boolean isApplicableTo(final String line) {
		return line.contains(Markers.JPA_TRACE_QUERY_MARKER);
	}
}
