/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
/**
 * 
 */
package com.elasticpath.cmclient.core.conversion;

import org.eclipse.core.databinding.conversion.Converter;

import com.elasticpath.domain.order.OrderStatus;

/**
 * Converter for converting an Enum into String.
 */
public class EpEnumToStringConverter extends Converter {

	/**
	 * Creates a new converter from Enum to String.
	 */
	public EpEnumToStringConverter() {
		super(Enum.class, String.class);
	}
	/**
	 * Converts from Enum to String.
	 * 
	 * @param fromObject object to convert
	 * @return resulting object
	 */
	public Object convert(final Object fromObject) {
		if (!(fromObject instanceof OrderStatus)) {
			throw new IllegalArgumentException("The argument to convert is not Enum"); //$NON-NLS-1$
		}
		return ((OrderStatus) fromObject).getPropertyKey();
	}

}
