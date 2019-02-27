/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.tools.sync.job.impl;

import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.tools.sync.job.JobEntry;
import com.elasticpath.tools.sync.job.descriptor.impl.TransactionJobDescriptorEntryImpl;

/**
 * Default implementation of JobEntry.
 */
public class JobEntryImpl extends TransactionJobDescriptorEntryImpl implements JobEntry {

	/**
	 * The serial version UID.
	 */
	private static final long serialVersionUID = 50000000001L;
	
	private Persistable sourceObject;

	private String transactionJobUnitName;

	/**
	 * @return the sourceObject
	 */
	@Override
	public Persistable getSourceObject() {
		return sourceObject;
	}

	/**
	 * @param sourceObject the sourceObject to set
	 */
	@Override
	public void setSourceObject(final Persistable sourceObject) {
		this.sourceObject = sourceObject;
	}

	/**
	 * Gets the parent transaction job unit name.
	 * 
	 * @return the transaction job unit name
	 */
	@Override
	public String getTransactionJobUnitName() {
		return transactionJobUnitName;
	}

	/**
	 * Sets the parent's name of this entry.
	 * 
	 * @param name the transaction job unit name
	 */
	@Override
	public void setTransactionJobUnitName(final String name) {
		this.transactionJobUnitName = name;
	}		
}
