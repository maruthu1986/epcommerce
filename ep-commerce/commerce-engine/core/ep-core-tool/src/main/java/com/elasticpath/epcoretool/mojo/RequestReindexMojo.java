/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.epcoretool.mojo;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.elasticpath.epcoretool.LoggerFacade;
import com.elasticpath.epcoretool.logic.AbstractRequestReindex;

/**
 * Adds a rebuild request to the index notification queue.
 */
@Mojo(name = "request-reindex")
public class RequestReindexMojo extends AbstractEpCoreMojo {

	/**
	 * Name of index.
	 */
	@Parameter(property = "index")
	private String index;

	/**
	 * Should execution continue until the requested indexes have been rebuild?
	 */
	@Parameter(property = "wait")
	private boolean wait;

	@Override
	public void executeMojo() throws MojoExecutionException {

		AbstractRequestReindex requestReindex = new AbstractRequestReindex(getJdbcUrl(), getJdbcUsername(), getJdbcPassword(), getJdbcDriverClass(),
				getJdbcConnectionPoolMinIdle(), getJdbcConnectionPoolMaxIdle()) {
			@Override
			protected LoggerFacade getLogger() {
				return getLoggerFacade();
			}
		};
		try {
			requestReindex.execute(index, wait);
		} catch (RuntimeException ex) {
			throw new MojoExecutionException(ex.getMessage(), ex);
		} finally {
			requestReindex.close();
		}
	}
}
