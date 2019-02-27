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
 * Parser chain that parses RelOS part from the provided cortex log line.
 */
public class RelosResourceChain extends AbstractParserChain {

	@Override
	public void parse(final BufferedReader reader,
					  final ObjectWrapper<String> line,
					  final ObjectWrapper<String> jpaLine,
					  final ObjectWrapper<Operation> operation,
					  final QueryStatistics queryStatistics,
					  final ObjectWrapper<JPAQuery> jpaQuery) throws Exception {
		if (isApplicableTo(line.getValue())) {
			final int colonIdx = line.getValue().indexOf(Markers.URI_MARKER);
			final String uri = line.getValue().substring(colonIdx + Markers.URI_MARKER.length() + 1);
			String[] resourceURIAndTypeTokens = uri.split(" ");
			Utils.setOperationTimestamp(Patterns.TIMESTAMP_PATTERN.matcher(line.getValue()), operation.getValue(), true);

			if (jpaLine.getValue() == null) {
				jpaLine.setValue(line);
			}

			Utils.setOperationJpaStartStopTimestamp(Patterns.TIMESTAMP_PATTERN.matcher(jpaLine.getValue()), operation.getValue(), true);

			//at this point, previous JPA query has been completed
			operation.setValue(new Operation(resourceURIAndTypeTokens[1], resourceURIAndTypeTokens[0]));
			Utils.setOperationThreadName(Patterns.THREAD_PATTERN.matcher(line.getValue()), operation.getValue());
			Utils.setOperationTimestamp(Patterns.TIMESTAMP_PATTERN.matcher(line.getValue()), operation.getValue(), false);
			queryStatistics.addOperation(operation.getValue());
		} else {
			getNextChain().parse(reader, line, jpaLine, operation, queryStatistics, jpaQuery);
		}
	}

	@Override
	protected boolean isApplicableTo(final String line) {
		return line.contains(Markers.RELOS_RESOURCE_URI_MARKER);
	}
}
