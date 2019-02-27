/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.importexport.importer.retrieval;

import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.importer.configuration.RetrievalConfiguration;

/**
 * Creates and initializes Retrieval Methods ready to use.
 */
public interface RetrievalMethodFactory {

	/**
	 * Constructs retrieval method from configuration.
	 *
	 * @param retrievalConfiguration the retrieval configuration
	 * @return instance of RetrievalMethod
	 * @throws ConfigurationException if retrieval method couldn't be created
	 */
	RetrievalMethod createRetrievalMethod(RetrievalConfiguration retrievalConfiguration) throws ConfigurationException;
}
