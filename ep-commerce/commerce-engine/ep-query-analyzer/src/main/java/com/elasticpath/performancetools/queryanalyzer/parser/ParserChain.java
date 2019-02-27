/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.performancetools.queryanalyzer.parser;

import java.io.BufferedReader;

import com.elasticpath.performancetools.queryanalyzer.beans.JPAQuery;
import com.elasticpath.performancetools.queryanalyzer.beans.Operation;
import com.elasticpath.performancetools.queryanalyzer.beans.QueryStatistics;
import com.elasticpath.performancetools.queryanalyzer.parser.chain.AbstractParserChain;
import com.elasticpath.performancetools.queryanalyzer.parser.chain.UnableToParseException;
import com.elasticpath.performancetools.queryanalyzer.parser.chain.impl.DoNothingChain;
import com.elasticpath.performancetools.queryanalyzer.parser.chain.impl.EagerRelationsChain;
import com.elasticpath.performancetools.queryanalyzer.parser.chain.impl.JpaQueryChain;
import com.elasticpath.performancetools.queryanalyzer.parser.chain.impl.JpaTraceQueryChain;
import com.elasticpath.performancetools.queryanalyzer.parser.chain.impl.RelosResourceChain;
import com.elasticpath.performancetools.queryanalyzer.parser.chain.impl.SqlQueryChain;

/**
 * Parser chain that parses Cortex's log file line.
 */
public class ParserChain {

	private final QueryStatistics statistics;
	private final BufferedReader bufferedReader;
	private final ObjectWrapper<Operation> operation;
	private final ObjectWrapper<String> jpaLine;
	private final ObjectWrapper<JPAQuery> jpaQuery;
	private final AbstractParserChain nextChain;

	/**
	 * Default constructor.
	 *
	 * @param statistics     query statistics object to use.
	 * @param bufferedReader buffered reader to read log from.
	 */
	public ParserChain(final QueryStatistics statistics, final BufferedReader bufferedReader) {
		final ObjectWrapper<Operation> operation = new ObjectWrapper<>(new Operation());
		statistics.addOperation(operation.getValue());
		this.statistics = statistics;
		this.bufferedReader = bufferedReader;
		this.operation = operation;
		this.jpaLine = new ObjectWrapper<>();
		this.jpaQuery = new ObjectWrapper<>(new JPAQuery("Unknown JPA query"));

		RelosResourceChain relosResourceChain = new RelosResourceChain();
		relosResourceChain
				.setNextChain(new JpaTraceQueryChain()
						.setNextChain(new JpaQueryChain()
								.setNextChain(new SqlQueryChain()
										.setNextChain(new EagerRelationsChain()
												.setNextChain(new DoNothingChain())))));
		this.nextChain = relosResourceChain;

	}

	/**
	 * Parses provided log line using chain of parsers.
	 *
	 * @param line line to parse.
	 */
	public void parse(final String line) {
		try {
			nextChain.parse(bufferedReader, new ObjectWrapper<>(line), jpaLine, operation, statistics, jpaQuery);
		} catch (Exception exception) {
			throw new UnableToParseException(exception);
		}
	}

}
