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
import com.elasticpath.performancetools.queryanalyzer.utils.Patterns;
import com.elasticpath.performancetools.queryanalyzer.utils.Utils;

/**
 * Parser chain that parses eager relations of the query from the provided log line.
 */
public class EagerRelationsChain extends AbstractParserChain {

	@Override
	public void parse(final BufferedReader reader,
					  final ObjectWrapper<String> line,
					  final ObjectWrapper<String> jpaLine,
					  final ObjectWrapper<Operation> operation,
					  final QueryStatistics queryStatistics,
					  final ObjectWrapper<JPAQuery> jpaQuery) throws Exception {

		if (isApplicableTo(line.getValue())) {
			Utils.setJPAQueryEagerRelations(Patterns.EAGER_RELATIONS_PATTERN.matcher(line.getValue()), jpaQuery.getValue());
		}

		getNextChain().parse(reader, line, jpaLine, operation, queryStatistics, jpaQuery);
	}

	@Override
	protected boolean isApplicableTo(final String line) {
		return line != null && line.contains(Markers.EAGER_RELATIONS_MARKER);
	}
}
