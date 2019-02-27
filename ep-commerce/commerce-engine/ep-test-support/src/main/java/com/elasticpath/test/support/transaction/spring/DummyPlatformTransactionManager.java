/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.test.support.transaction.spring;

import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;

/**
 * Dummy Platform Transaction Manager class used in tests that require a transaction manager to be present.
 */
public class DummyPlatformTransactionManager extends AbstractPlatformTransactionManager {

	private static final long serialVersionUID = 6721932469639778126L;

	@Override
	protected Object doGetTransaction() throws TransactionException {
		return null;
	}

	@Override
	protected void doBegin(final Object transaction, final TransactionDefinition definition) throws TransactionException {
		// do nothing
	}

	@Override
	protected void doCommit(final DefaultTransactionStatus status) throws TransactionException {
		// do nothing
	}

	@Override
	protected void doRollback(final DefaultTransactionStatus status) throws TransactionException {
		// do nothing
	}

}
