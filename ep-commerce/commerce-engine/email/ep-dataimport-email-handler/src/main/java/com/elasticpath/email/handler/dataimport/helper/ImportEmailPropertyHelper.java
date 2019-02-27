/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.email.handler.dataimport.helper;

import java.util.Locale;

import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.dataimport.ImportJobStatus;
import com.elasticpath.email.domain.EmailProperties;

/**
 * Helper for constructing email properties.
 */
public interface ImportEmailPropertyHelper {

	/**
	 * Creates new {@link EmailProperties} and sets them.
	 * 
	 * @param runningJob {@link ImportJobStatus}
	 * @param cmUser {@link CmUser}
	 * @param locale of the error messages
	 * @return {@link EmailProperties}
	 */
	EmailProperties getEmailProperties(ImportJobStatus runningJob, CmUser cmUser, Locale locale);

}
