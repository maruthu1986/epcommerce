/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.epcoretool.mojo;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.elasticpath.epcoretool.LoggerFacade;
import com.elasticpath.epcoretool.logic.AbstractUnSetSetting;

/**
 * Updates the setting value in the Elastic Path database. If a value already exists, it will be removed.
 */
@Mojo(name = "unset-setting")
public class UnSetSettingMojo extends AbstractEpCoreMojo {

	/**
	 * Name of setting.
	 */
	@Parameter(property = "settingName", required = true)
	private String settingName;

	/**
	 * Context for the setting (eg, store code).
	 */
	@Parameter(property = "settingContext")
	private String settingContext;

	/**
	 * Look up a {@code SettingsService}, then fetch the specified setting+context. If the value returned is flagged as persistence, then request
	 * that it's deleted. If the value is not persistent, nothing is done.
	 * 
	 * @throws MojoExecutionException the mojo execution exception
	 * @throws MojoFailureException the mojo failure exception
	 */
	@Override
	public void executeMojo() throws MojoExecutionException, MojoFailureException {
		AbstractUnSetSetting setSetting = new AbstractUnSetSetting(getJdbcUrl(), getJdbcUsername(), getJdbcPassword(), getJdbcDriverClass(),
				getJdbcConnectionPoolMinIdle(), getJdbcConnectionPoolMaxIdle()) {
			@Override
			protected LoggerFacade getLogger() {
				return getLoggerFacade();
			}
		};
		try {
			setSetting.execute(settingName, settingContext);
		} catch (RuntimeException ex) {
			throw new MojoExecutionException(ex.getMessage(), ex);
		} finally {
			setSetting.close();
		}
	}

}
