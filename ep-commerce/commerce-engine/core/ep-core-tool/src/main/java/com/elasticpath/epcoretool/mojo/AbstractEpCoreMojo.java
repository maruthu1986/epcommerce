/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.epcoretool.mojo;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;

import com.elasticpath.epcoretool.LoggerFacade;

/**
 * Extends {@code AbstractMojo} to include both the jdbc settings for EP and a mechanism to fetch a {@code EpCoreManager}.
 */
public abstract class AbstractEpCoreMojo extends AbstractMojo {

	// this lock is used to prevent concurrency when using -T option in maven.
	// this is to workaround the issue having multiple ep-cores running
	// at the same time is unsupported
	private static final Object JVM_LOCK = new Object();

	@Parameter(required = true, defaultValue = "false")
	private boolean skip;

	/**
	 * JDBC URL.
	 */
	@Parameter(property = "epdb.url", required = true)
	private String jdbcUrl;

	/**
	 * Database Username.
	 */
	@Parameter(property = "epdb.username")
	private String jdbcUsername;

	/**
	 * Database Password.
	 */
	@Parameter(property = "epdb.password")
	private String jdbcPassword;

	/**
	 * Database JDBC Driver Class.
	 */
	@Parameter(property = "epdb.jdbc.driver", required = true)
	private String jdbcDriverClass;

	/**
	 * Minimum number of idle connections permitted in the connection pool. Set this property and jdbcConnectionPoolMaxIdle to 0 to prohibit idle
	 * connections.
	 */
	@Parameter(property = "epdb.jdbc.min.idle")
	private Integer jdbcConnectionPoolMinIdle;

	/**
	 * Maximum number of idle connections permitted in the connection pool, or negative for no limit. Set this property and jdbcConnectionPoolMinIdle
	 * to 0 to prohibit idle connections.
	 */
	@Parameter(property = "epdb.jdbc.max.idle")
	private Integer jdbcConnectionPoolMaxIdle;

	private final LoggerFacade logger = new LoggerFacade() {

		@Override
		public void error(final String message) {
			getLog().error(message);
		}

		@Override
		public void warn(final String message) {
			getLog().warn(message);
		}

		@Override
		public void info(final String message) {
			getLog().info(message);
		}

		@Override
		public void debug(final String message) {
			getLog().debug(message);
		}
	};

	/**
	 * Execute.
	 *
	 * @throws MojoExecutionException the mojo execution exception
	 * @throws MojoFailureException the mojo failure exception
	 */
	@Override
	public final void execute() throws MojoExecutionException, MojoFailureException {
		if (skip) {
			getLog().info("Skipping ep-core-tool execution");
			return;
		}
		synchronized (JVM_LOCK) {
			executeMojo();
		}
	}

	/**
	 * Execute mojo.
	 * 
	 * @throws MojoExecutionException the mojo execution exception
	 * @throws MojoFailureException the mojo failure exception
	 */
	abstract void executeMojo() throws MojoExecutionException, MojoFailureException;

	/**
	 * Gets the jdbc url.
	 * 
	 * @return the jdbc url
	 */
	protected String getJdbcUrl() {
		return jdbcUrl;
	}

	/**
	 * Gets the jdbc username.
	 * 
	 * @return the jdbc username
	 */
	protected String getJdbcUsername() {
		return jdbcUsername;
	}

	/**
	 * Gets the jdbc password.
	 * 
	 * @return the jdbc password
	 */
	protected String getJdbcPassword() {
		return jdbcPassword;
	}

	/**
	 * Gets the jdbc driver class.
	 * 
	 * @return the jdbc driver class
	 */
	protected String getJdbcDriverClass() {
		return jdbcDriverClass;
	}

	/**
	 * Gets the jdbc connection pool min idle.
	 * 
	 * @return the jdbc connection pool min idle
	 */
	protected Integer getJdbcConnectionPoolMinIdle() {
		return jdbcConnectionPoolMinIdle;
	}

	/**
	 * Gets the jdbc connection pool max idle.
	 * 
	 * @return the jdbc connection pool max idle
	 */
	protected Integer getJdbcConnectionPoolMaxIdle() {
		return jdbcConnectionPoolMaxIdle;
	}

	/**
	 * Checks if is skip.
	 *
	 * @return true, if is skip
	 */
	protected boolean isSkip() {
		return skip;
	}

	/**
	 * Gets the logger facade.
	 * 
	 * @return the logger facade
	 */
	protected LoggerFacade getLoggerFacade() {
		return logger;
	}
}
