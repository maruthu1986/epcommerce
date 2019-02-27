/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.cmuser;

import com.elasticpath.domain.EpDomain;
import com.elasticpath.domain.dataimport.ImportJob;

/**
 * <code>CmUserSession</code> represents a user session in commerce manager.
 */
public interface CmUserSession extends EpDomain {

	/**
	 * Returns the <code>CmUser</code> instance.
	 *
	 * @return the <code>CmUser</code> instance
	 */
	CmUser getCmUser();

	/**
	 * Sets the <code>CmUser</code> instance.
	 *
	 * @param cmUser the <code>CmUser</code> instance.
	 */
	void setCmUser(CmUser cmUser);

	/**
	 * Returns the <code>ImportJob</code> instance.
	 *
	 * @return the <code>ImportJob</code> instance.
	 */
	ImportJob getImportJob();

	/**
	 * Sets the <code>ImportJob</code> instance.
	 *
	 * @param importJob the <code>ImportJob</code> instance.
	 */
	void setImportJob(ImportJob importJob);

}
