/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.shipping.connectivity.dto.impl;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.elasticpath.shipping.connectivity.dto.ShippingCalculationResult;

/**
 * Default implementation of {@link ShippingCalculationResult.ErrorInformation}.
 */
public class ShippingCalculationResultErrorInformationImpl implements ShippingCalculationResult.ErrorInformation, Serializable {

	private static final long serialVersionUID = 5000000001L;

	private final String errorCode;
	private Throwable cause;
	private Object additionalErrorInformation;

	/**
	 * Default constructor just taking in an error code to allow callers to distinguish between types of errors to be able to handle them
	 * appropriately.
	 *
	 * @param errorCode the error code
	 */
	public ShippingCalculationResultErrorInformationImpl(final String errorCode) {
		this.errorCode = errorCode;
	}

	/**
	 * Constructor taking in a cause.
	 *
	 * @param errorCode the error code
	 * @param cause     the cause of the error.
	 */
	public ShippingCalculationResultErrorInformationImpl(final String errorCode, final Throwable cause) {
		this(errorCode);
		setCause(cause);
	}

	/**
	 * Constructor taking in additional error information with no underlying {@link Throwable} cause.
	 *
	 * @param errorCode                  the error code
	 * @param additionalErrorInformation the additional error information for diagnostics.
	 */
	public ShippingCalculationResultErrorInformationImpl(final String errorCode, final Object additionalErrorInformation) {
		this(errorCode);
		setAdditionalErrorInformation(additionalErrorInformation);
	}

	/**
	 * Constructor taking in a cause and additional error information for diagnostics.
	 *
	 * @param errorCode                  the error code
	 * @param cause                      the cause.
	 * @param additionalErrorInformation additional error information.
	 */
	public ShippingCalculationResultErrorInformationImpl(final String errorCode, final Throwable cause, final Object additionalErrorInformation) {
		this(errorCode, cause);
		setAdditionalErrorInformation(additionalErrorInformation);
	}

	@Override
	public String getErrorCode() {
		return this.errorCode;
	}

	@Override
	public Optional<Throwable> getCause() {
		return Optional.ofNullable(this.cause);
	}

	public final void setCause(final Throwable cause) {
		this.cause = cause;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> Optional<T> getAdditionalErrorInformation() {
		return Optional.ofNullable((T) this.additionalErrorInformation);
	}

	/**
	 * Setter for any additional error information.
	 *
	 * @param additionalErrorInformation the error information.
	 * @param <T>                        the type of error information.
	 */
	public final <T> void setAdditionalErrorInformation(final T additionalErrorInformation) {
		this.additionalErrorInformation = additionalErrorInformation;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof ShippingCalculationResult.ErrorInformation)) {
			return false;
		}

		final ShippingCalculationResult.ErrorInformation other = (ShippingCalculationResult.ErrorInformation) obj;

		return Objects.equals(getCause(), other.getCause())
				&& Objects.equals(getAdditionalErrorInformation(), other.getAdditionalErrorInformation());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getCause(), getAdditionalErrorInformation());
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append(getAdditionalErrorInformation())
				.append(getCause())
				.toString();
	}
}
