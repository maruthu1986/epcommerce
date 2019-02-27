/*
 * Copyright © 2018 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.performancetools.queryanalyzer.parser.chain.impl;

import java.io.BufferedReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.elasticpath.performancetools.queryanalyzer.beans.JPAQuery;
import com.elasticpath.performancetools.queryanalyzer.beans.Operation;
import com.elasticpath.performancetools.queryanalyzer.beans.QueryStatistics;
import com.elasticpath.performancetools.queryanalyzer.parser.ObjectWrapper;
import com.elasticpath.performancetools.queryanalyzer.parser.chain.AbstractParserChain;
import com.elasticpath.performancetools.queryanalyzer.utils.Markers;
import com.elasticpath.performancetools.queryanalyzer.utils.Patterns;
import com.elasticpath.performancetools.queryanalyzer.utils.Utils;

/**
 * Parser chain that parses JPA part from the provided log line.
 */
public class JpaQueryChain extends AbstractParserChain {

	@Override
	public void parse(final BufferedReader reader,
					  final ObjectWrapper<String> line,
					  final ObjectWrapper<String> jpaLine,
					  final ObjectWrapper<Operation> operation,
					  final QueryStatistics queryStatistics,
					  final ObjectWrapper<JPAQuery> jpaQuery) throws Exception {

		if (isApplicableTo(line.getValue())) {
			final StringBuilder queryBuffer = new StringBuilder(line.getValue());
			line.setValue(Utils.processMultiLineQueries(reader, queryBuffer));

			Matcher matcher = getJPAQueryPattern(line.getValue()).matcher(queryBuffer.toString());

			if (matcher.find()) {
				final String query = matcher.group(1);

				jpaQuery.setValue(new JPAQuery(query));
				Utils.setJpaQueryStartTimestamp(Patterns.TIMESTAMP_PATTERN.matcher(line.getValue()), jpaQuery.getValue());
				operation.getValue().addJPAQuery(jpaQuery.getValue());
			}
		}

		getNextChain().parse(reader, line, jpaLine, operation, queryStatistics, jpaQuery);
	}

	private Pattern getJPAQueryPattern(final String line) {
		if (line == null) {
			return Patterns.JPA_QUERY_PATTERN_DATE_OPTIONAL;
		}

		return Patterns.JPA_QUERY_PATTERN;
	}

	@Override
	protected boolean isApplicableTo(final String line) {
		return line.contains(Markers.JPA_QUERY_MARKER);
	}
}
