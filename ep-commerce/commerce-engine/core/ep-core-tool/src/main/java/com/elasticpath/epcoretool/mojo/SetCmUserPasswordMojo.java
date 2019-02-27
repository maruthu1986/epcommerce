/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.epcoretool.mojo;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.elasticpath.epcoretool.LoggerFacade;
import com.elasticpath.epcoretool.logic.AbstractSetCmUserPassword;

/**
 * Set the password of the specified CM User.
 */
@Mojo(name = "set-cmuser-password")
public class SetCmUserPasswordMojo extends AbstractEpCoreMojo {

	/**
	 * Username of the CM User.
	 */
	@Parameter(property = "username")
	private String username;

	/**
	 * New plaintext password for the CM user.
	 */
	@Parameter(property = "password")
	private String password;

	@Override
	public void executeMojo() throws MojoExecutionException, MojoFailureException {
		AbstractSetCmUserPassword setCmUserPassword = new AbstractSetCmUserPassword(getJdbcUrl(), getJdbcUsername(), getJdbcPassword(),
				getJdbcDriverClass(), getJdbcConnectionPoolMinIdle(), getJdbcConnectionPoolMaxIdle()) {
			@Override
			protected LoggerFacade getLogger() {
				return getLoggerFacade();
			}
		};
		try {
			setCmUserPassword.execute(username, password);
		} catch (RuntimeException ex) {
			throw new MojoExecutionException(ex.getMessage(), ex);
		} finally {
			setCmUserPassword.close();
		}

	}

}
