/*
 * Copyright © 2018 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.performancetools.queryanalyzer.parser.chain;

import java.io.BufferedReader;

import com.elasticpath.performancetools.queryanalyzer.beans.JPAQuery;
import com.elasticpath.performancetools.queryanalyzer.beans.Operation;
import com.elasticpath.performancetools.queryanalyzer.beans.QueryStatistics;
import com.elasticpath.performancetools.queryanalyzer.parser.ObjectWrapper;

/**
 * Abstract parse chain.
 */
public abstract class AbstractParserChain {

	/**
	 * Next chain.
	 */
	private AbstractParserChain nextChain;

	/**
	 * Get next chain.
	 *
	 * @return next chain object.
	 */
	public AbstractParserChain getNextChain() {
		return nextChain;
	}

	/**
	 * Set next chain. This is a builder-type setter.
	 *
	 * @param nextChain next chain reference.
	 * @return this object.
	 */
	public AbstractParserChain setNextChain(final AbstractParserChain nextChain) {
		this.nextChain = nextChain;
		return this;
	}

	/**
	 * Parse Cortex's log file line and store parsed statistics into {@link QueryStatistics} object.
	 *
	 * @param reader          reader used to read all the lines.
	 * @param line            {@link String} log line wrapped into {@link ObjectWrapper} for mutability purposes.
	 * @param jpaLine         {@link String} jpa log line wrapped into {@link ObjectWrapper} for mutability purposes.
	 * @param operation       {@link Operation} object wrapped into {@link ObjectWrapper} for mutability purposes.
	 * @param queryStatistics query statistics.
	 * @param jpaQuery        {@link JPAQuery} jpaQuery wrapped into {@link ObjectWrapper} for mutability purposes.
	 * @throws Exception an exception.
	 */
	public abstract void parse(BufferedReader reader,
							   ObjectWrapper<String> line,
							   ObjectWrapper<String> jpaLine,
							   ObjectWrapper<Operation> operation,
							   QueryStatistics queryStatistics,
							   ObjectWrapper<JPAQuery> jpaQuery) throws Exception;

	/**
	 * Checks whether this chain is applicable to passed line.
	 *
	 * @param line line to check.
	 * @return true if line is applicable, false otherwise.
	 */
	protected abstract boolean isApplicableTo(String line);

}
