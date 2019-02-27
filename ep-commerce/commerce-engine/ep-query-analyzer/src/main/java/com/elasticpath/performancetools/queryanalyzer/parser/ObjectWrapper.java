/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.performancetools.queryanalyzer.parser;

/**
 * Mutable object wrapper.
 *
 * @param <V> type of the object to wrap.
 */
public class ObjectWrapper<V> {

	private V value;

	/**
	 * Default constructor.
	 */
	public ObjectWrapper() {
		//default constructor
	}

	/**
	 * Constructor.
	 *
	 * @param value wrappers value.
	 */
	public ObjectWrapper(final V value) {
		this.value = value;
	}

	/**
	 * Get wrappers value.
	 *
	 * @return value.
	 */
	public V getValue() {
		return value;
	}

	/**
	 * Set wrappers value.
	 *
	 * @param value value.
	 */
	public void setValue(final ObjectWrapper<V> value) {
		this.value = value.getValue();
	}

	/**
	 * Set wrappers value.
	 *
	 * @param value value.
	 */
	public void setValue(final V value) {
		this.value = value;
	}
}
