/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.epcoretool.mojo;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

import com.elasticpath.epcoretool.LoggerFacade;
import com.elasticpath.epcoretool.logic.AbstractIndexBuildStatus;

/**
 * Display the current search server index rebuild status.
 */
@Mojo(name = "index-status")
public class IndexBuildStatusMojo extends AbstractEpCoreMojo {

	@Override
	public void executeMojo() throws MojoExecutionException, MojoFailureException {
		AbstractIndexBuildStatus indexBuildStatus = new AbstractIndexBuildStatus(getJdbcUrl(), getJdbcUsername(), getJdbcPassword(),
				getJdbcDriverClass(), getJdbcConnectionPoolMinIdle(), getJdbcConnectionPoolMaxIdle()) {
			@Override
			protected LoggerFacade getLogger() {
				return getLoggerFacade();
			}
		};
		try {
			indexBuildStatus.execute();
		} catch (RuntimeException ex) {
			throw new MojoExecutionException(ex.getMessage(), ex);
		} finally {
			indexBuildStatus.close();
		}
	}

}
