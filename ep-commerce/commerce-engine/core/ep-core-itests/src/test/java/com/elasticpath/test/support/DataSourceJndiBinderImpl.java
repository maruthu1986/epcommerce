/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.test.support;

import javax.sql.DataSource;

import com.elasticpath.test.support.jndi.JndiContextManager;

/**
 * Binds a {@link DataSource} to the default EP JNDI location ({@link DataSourceJndiBinderImpl#JNDI_NAME}. Also provides
 * a method to clear the JNDI context.
 */
public class DataSourceJndiBinderImpl {
	private final JndiContextManager binder;

	/**
	 * The default JNDI name for an EP DataSource.
	 */
	public String JNDI_NAME = "java:comp/env/jdbc/epjndi";

	/**
	 * Creates a new DatasourceJndiBinder().
	 */
	public DataSourceJndiBinderImpl() {
		binder = JndiContextManager.createJndiContextManager();
	}

	/**
	 * Binds the given DataSource in JNDI with the default name.
	 * @param dataSource the DataSource to bind
	 */
	public void bindEpDatasourceInJndi(final DataSource dataSource) {
		binder.bind(JNDI_NAME, dataSource);
	}

	/**
	 * Clears the JNDI binding.
	 */
	public void unbindEpDatasourceInJndi() {
		binder.unbind(JNDI_NAME);
	}
}