/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.epcoretool.mojo;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

import com.elasticpath.epcoretool.LoggerFacade;
import com.elasticpath.epcoretool.logic.AbstractRecompileRuleBase;

/**
 * Recompiles the EP Promo RuleBase.
 */
@Mojo(name = "recompile-rulebase")
public class RecompileRuleBaseMojo extends AbstractEpCoreMojo {

	@Override
	public void executeMojo() throws MojoExecutionException, MojoFailureException {
		AbstractRecompileRuleBase recompileRuleBase = new AbstractRecompileRuleBase(getJdbcUrl(), getJdbcUsername(), getJdbcPassword(),
				getJdbcDriverClass(), getJdbcConnectionPoolMinIdle(), getJdbcConnectionPoolMaxIdle()) {
			@Override
			protected LoggerFacade getLogger() {
				return getLoggerFacade();
			}
		};
		try {
			recompileRuleBase.execute();
		} catch (RuntimeException ex) {
			throw new MojoExecutionException(ex.getMessage(), ex);
		} finally {
			recompileRuleBase.close();
		}
	}
}
