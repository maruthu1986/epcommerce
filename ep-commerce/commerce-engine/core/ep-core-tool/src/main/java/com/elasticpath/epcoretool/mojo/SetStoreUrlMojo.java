/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.epcoretool.mojo;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.elasticpath.epcoretool.LoggerFacade;
import com.elasticpath.epcoretool.logic.AbstractSetStoreURL;

/**
 * Update the store url for the specified store.
 */
@Mojo(name = "set-store-url")
public class SetStoreUrlMojo extends AbstractEpCoreMojo {

	/**
	 * Storecode of the store to modify.
	 */
	@Parameter(property = "storecode")
	private String storecode;

	/**
	 * New url for the store.
	 */
	@Parameter(property = "url")
	private String url;

	@Override
	void executeMojo() throws MojoExecutionException, MojoFailureException {
		AbstractSetStoreURL updateStoreURL = new AbstractSetStoreURL(getJdbcUrl(), getJdbcUsername(), getJdbcPassword(),
				getJdbcDriverClass(), getJdbcConnectionPoolMinIdle(), getJdbcConnectionPoolMaxIdle()) {

			@Override
			protected LoggerFacade getLogger() {
				return getLoggerFacade();
			}
		};

		try {
			updateStoreURL.execute(storecode, url);
		} catch (RuntimeException ex) {
			throw new MojoExecutionException(ex.getMessage(), ex);
		} finally {
			updateStoreURL.close();
		}
	}
}
