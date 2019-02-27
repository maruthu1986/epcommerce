/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.epcoretool.mojo;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.elasticpath.epcoretool.LoggerFacade;
import com.elasticpath.epcoretool.logic.AbstractSetSetting;

/**
 * Updates the setting value in the Elastic Path database. If a value already exists, it will be removed.
 */
@Mojo(name = "set-setting")
public class SetSettingMojo extends AbstractEpCoreMojo {

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
	 * New value.
	 */
	@Parameter(property = "settingValue", required = true)
	private String settingValue;

	/**
	 * Look up a {@code SettingsService}, then fetch the specified setting+context. If the value returned is flagged as persistence, then request
	 * that it's deleted before calling update.
	 *
	 * @throws MojoExecutionException the mojo execution exception
	 * @throws MojoFailureException the mojo failure exception
	 */
	@Override
	public void executeMojo() throws MojoExecutionException, MojoFailureException {
		AbstractSetSetting setSetting = new AbstractSetSetting(getJdbcUrl(), getJdbcUsername(), getJdbcPassword(), getJdbcDriverClass(),
				getJdbcConnectionPoolMinIdle(), getJdbcConnectionPoolMaxIdle()) {
			@Override
			protected LoggerFacade getLogger() {
				return getLoggerFacade();
			}
		};
		try {
			setSetting.execute(settingName, settingContext, settingValue);
		} catch (RuntimeException ex) {
			throw new MojoExecutionException(ex.getMessage(), ex);
		} finally {
			setSetting.close();
		}
	}

}
