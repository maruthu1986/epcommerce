/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.importexport.importer.importers.impl;

import com.elasticpath.persistence.api.Persistable;

/**
 * Default implementation of <code>LifecycleListener</code> interface.
 */
class DefaultLifecycleListener implements LifecycleListener {
	@Override
	public void afterSave(final Persistable persistable) {
		// default empty implementation
	}

	@Override
	public void beforeSave(final Persistable persistable) {
		// default empty implementation
	}

	@Override
	public void beforePopulate(final Persistable persistable) {
		// default empty implementation
	}
}
