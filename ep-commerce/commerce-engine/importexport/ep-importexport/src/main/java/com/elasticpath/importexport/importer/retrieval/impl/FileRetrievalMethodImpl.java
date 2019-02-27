/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.importexport.importer.retrieval.impl;

import java.io.File;

/**
 * Simple retrieval method reads file from file system. 
 */
public class FileRetrievalMethodImpl extends AbstractRetrievalMethodImpl {
	@Override	
	public File retrieve() {		
		return new File(getSource());		
	}	
}
