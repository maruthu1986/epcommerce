/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.epcoretool.logic;

import java.util.Date;

import com.elasticpath.service.misc.TimeService;
import com.elasticpath.service.rules.RecompilingRuleEngine;
import com.elasticpath.service.rules.impl.DBCompilingRuleEngineImpl;


/**
 * The Class AbstractRecompileRuleBase.
 */
public abstract class AbstractRecompileRuleBase extends AbstractEpCore {

	/**
	 * Instantiates a new abstract recompile rule base.
	 *
	 * @param jdbcUrl the jdbc url
	 * @param jdbcUsername the jdbc username
	 * @param jdbcPassword the jdbc password
	 * @param jdbcDriverClass the jdbc driver class
	 * @param jdbcConnectionPoolMinIdle the jdbc connection pool min idle
	 * @param jdbcConnectionPoolMaxIdle the jdbc connection pool max idle
	 */
	public AbstractRecompileRuleBase(final String jdbcUrl, final String jdbcUsername, final String jdbcPassword, final String jdbcDriverClass,
			final Integer jdbcConnectionPoolMinIdle, final Integer jdbcConnectionPoolMaxIdle) {
		super(jdbcUrl, jdbcUsername, jdbcPassword, jdbcDriverClass, jdbcConnectionPoolMinIdle, jdbcConnectionPoolMaxIdle);
	}
	
	/**
	 * Execute.
	 */
	public void execute() {
		RecompilingRuleEngine ruleEngine = epCore().getRuleEngine();
		((DBCompilingRuleEngineImpl) ruleEngine).setTimeService(new StubTimeService());
		ruleEngine.recompileRuleBase();
	}

	/**
	 * A stub implementation of {@link TimeService} to ensure that the rules are built on every invocation of this command.
	 */
	private static class StubTimeService implements TimeService {
		@Override
		public Date getCurrentTime() {
			return new Date(0);
		}
	}
}
