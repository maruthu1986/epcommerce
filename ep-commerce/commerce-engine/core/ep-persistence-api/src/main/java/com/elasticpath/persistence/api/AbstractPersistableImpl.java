/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.persistence.api;


/**
 * The default implementation of <code>Persistable</code>.
 */
public abstract class AbstractPersistableImpl implements Persistable {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000002L;

	/**
	 * Allocation size for JPA_GENERATED_KEYS id.
	 * To be used for high concurrency tables.
	 */
	protected static final int HIGH_CONCURRENCY_ALLOCATION_SIZE = 10000;

	/** Precision to be used for persisting decimal fields. */
	public static final int DECIMAL_PRECISION = 19;

	/** Scale to be used for persisting decimal fields. */
	public static final int DECIMAL_SCALE = 2;

	/**
	 * True if the object has previously been persisted.
	 * @return true if the object has previously been persisted.
	 */
	@Override
	public boolean isPersisted() {
		return getUidPk() > 0;
	}

}
