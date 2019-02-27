/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.tags.service.impl;

import org.apache.commons.lang.StringUtils;

/**
 * Expression builder class for constructing a groovy script from DSL string.
 */
public final class GroovyExpressionBuilder {

	/** Method produced by script. */
	public static final String EVAL_METHOD = "runOnMap";

	/* Example completed script:
		def runOnMap(map) {
		   def out =
			{AND
				{ location.contains "us" }
				{ memberType.contains "poor" }
			}
		  }
		  new MapRunner(map).run(out)
		}
	 */
	/**
	 * Defines script prefix that provides scaffold code for evaluating conditions.
	 * All DSL strings are first routed to LogicalOperator class to handle AND/OR/NOT.
	 */
	static final String DEF =
		"import com.elasticpath.tags.engine.*\n"
		+ "def methodMissing(String name, args) {\n"
		+ "  new LogicalOperator().invokeMethod(name, args)\n"
		+ "}\n"
		+ "def " + EVAL_METHOD + "(map) {\n"
		+ "   def out =\n";
	/**
	 * Defines script suffix that provides scaffold code for evaluating conditions.
	 */
	static final String CLOSE = "\n"
								+ "new MapRunner(map).run(out)\n}";

	private GroovyExpressionBuilder() {
		// Do not instantiate
	}

	/**
	 * Build a groovy method script from a conditional string.
	 * Returns a default of "true" if no conditions are given.
	 *
	 * @param script conditional
	 * @return runnable script
	 */
	public static String buildExpression(final String script) {
		if (StringUtils.isEmpty(script)) {
			return DEF + "{ true }" + CLOSE;
		}
		return DEF + script + CLOSE;
	}
}
