/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.tools.sync.job.transaction.impl;

import com.elasticpath.tools.sync.job.descriptor.TransactionJobDescriptorEntry;

/**
 * Transaction group by type iterator implementation, return job descriptor entries.
 */
public class TrasnactionGroupByTypeIterator extends TrasnactionGroupByCommandAndTypeIterator {
	
	@Override
	protected boolean addToResult(final TransactionJobDescriptorEntry firstDescriptorEntry, final TransactionJobDescriptorEntry descriptorEntry) {
		return firstDescriptorEntry.getType().equals(descriptorEntry.getType());
	}

}
