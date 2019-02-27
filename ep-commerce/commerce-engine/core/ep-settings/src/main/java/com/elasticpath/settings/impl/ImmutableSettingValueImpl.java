/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.settings.impl;

import java.util.Date;

import com.elasticpath.settings.domain.SettingValue;

/**
 * An immutable decorator for a {@link SettingValue} object.
 */
public class ImmutableSettingValueImpl implements SettingValue {

	private static final long serialVersionUID = 1L;

	private static final String IMMUTABLE_MESSAGE = "This object is immutable, no properties may be modified.";

	private final SettingValue delegate;

	/**
	 * Wraps a {@link SettingValue} to make it immutable.
	 *
	 * @param delegate the {@link SettingValue} to decorate and delegate calls to.
	 */
	public ImmutableSettingValueImpl(final SettingValue delegate) {
		super();
		this.delegate = delegate;
	}

	@SuppressWarnings("PMD.BooleanGetMethodName")
	@Override
	public boolean getBooleanValue() {
		return delegate.getBooleanValue();
	}

	@SuppressWarnings("PMD.IntegerGetMethodName")
	@Override
	public int getIntegerValue() {
		return delegate.getIntegerValue();
	}

	@Override
	public String getContext() {
		return delegate.getContext();
	}

	@Override
	public String getDefaultValue() {
		return delegate.getDefaultValue();
	}

	@Override
	public Date getLastModifiedDate() {
		return delegate.getLastModifiedDate();
	}

	@Override
	public String getPath() {
		return delegate.getPath();
	}

	@Override
	public String getValue() {
		return delegate.getValue();
	}

	@Override
	public String getValueType() {
		return delegate.getValueType();
	}

	/**
	 * Throws an UnsupportedOperationException.
	 *
	 * @param value the boolean value to set
	 * @throws UnsupportedOperationException
	 */
	@Override
	public void setBooleanValue(final boolean value) {
		throw new UnsupportedOperationException(IMMUTABLE_MESSAGE);
	}

	/**
	 * Throws an UnsupportedOperationException.
	 *
	 * @param value the boolean value to set
	 * @throws UnsupportedOperationException
	 */
	@Override
	public void setIntegerValue(final int value) {
		throw new UnsupportedOperationException(IMMUTABLE_MESSAGE);
	}

	/**
	 * Throws an UnsupportedOperationException.
	 *
	 * @param context the context to set
	 * @throws UnsupportedOperationException
	 */
	@Override
	public void setContext(final String context) {
		throw new UnsupportedOperationException(IMMUTABLE_MESSAGE);
	}

	/**
	 * Throws an UnsupportedOperationException.
	 *
	 * @param value the value to set
	 * @throws UnsupportedOperationException
	 */
	@Override
	public void setValue(final String value) {
		throw new UnsupportedOperationException(IMMUTABLE_MESSAGE);
	}

	@Override
	public long getUidPk() {
		return delegate.getUidPk();
	}

	@Override
	public boolean isPersisted() {
		return delegate.isPersisted();
	}

	/**
	 * Throws an UnsupportedOperationException.
	 *
	 * @param uidPk the uidPk to set
	 * @throws UnsupportedOperationException
	 */
	@Override
	public void setUidPk(final long uidPk) {
		throw new UnsupportedOperationException(IMMUTABLE_MESSAGE);
	}
}
