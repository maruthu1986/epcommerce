/*
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.fulfillment.editors.order.ui.impl;

import com.elasticpath.cmclient.core.EpAuthorizationException;
import com.elasticpath.cmclient.fulfillment.editors.order.ui.ManagedModel;
import com.elasticpath.cmclient.fulfillment.editors.order.ui.UiProperty;

/**
 * An implementation of ManagedModel. 
 *
 */
public class ManagedModelImpl implements ManagedModel<String, String> {

	private final String key;
	private String value;
	private final UiProperty uiProperty;

	/**
	 * @param key a row key 
	 * @param value a row value
	 * @param uiProperty a UI property
	 */
	protected ManagedModelImpl(final String key, final String value, final UiProperty uiProperty) {
		super();
		this.key = key;
		this.value = value;
		this.uiProperty = uiProperty;
	}

	/**
	 * A default constructor from the same object but with different ui property.
	 * @param managedRow a managed row source
	 * @param uiProperty a UI property
	 */
	protected ManagedModelImpl(final ManagedModel<String, String> managedRow, final UiProperty uiProperty) {
		super();
		this.key = managedRow.getKey();
		this.value = managedRow.getValue();
		this.uiProperty = uiProperty;
	}

	@Override
	public String getKey() {
		return this.key;
	}

	@Override
	public String getValue() {
		return this.value;
	}

	@Override
	public UiProperty getUiProperty() {
		return this.uiProperty;
	}

	@Override
	public void setValue(final String value) {
		if (UiProperty.READ_WRITE != this.uiProperty) {
			throw new EpAuthorizationException("Operation not allowed."); //$NON-NLS-1$
		}
		this.value = value;
	}

	@Override
	public String getValueForUI() {
		return this.value;
	}

	@Override
	@SuppressWarnings("PMD.ConsecutiveLiteralAppends")
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ManagedModelImpl [uiProperty=") //$NON-NLS-1$
			.append(uiProperty)
			.append(", key=") //$NON-NLS-1$
			.append(key)
			.append(", value="); //$NON-NLS-1$
			
		try {
			builder.append(getValueForUI());
		} catch (Exception e) {
			builder.append("****"); //$NON-NLS-1$
		}
		builder.append(']'); //$NON-NLS-1$
		return builder.toString();
	}

	
}
