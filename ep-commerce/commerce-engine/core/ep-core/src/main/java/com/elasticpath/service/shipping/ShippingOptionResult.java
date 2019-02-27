/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shipping;

import java.util.List;
import java.util.Optional;

import org.apache.log4j.Logger;

import com.elasticpath.base.common.dto.StructuredErrorMessage;
import com.elasticpath.shipping.connectivity.dto.ShippingCalculationResult;
import com.elasticpath.shipping.connectivity.dto.ShippingOption;
import com.elasticpath.shipping.connectivity.exceptions.ShippingOptionServiceException;

/**
 * Interface describing the response objects returned by {@link com.elasticpath.service.shipping.ShippingOptionService}.
 */
public interface ShippingOptionResult {

	/**
	 * Returns whether the result represents a successful request or not.
	 *
	 * @return {@code true} if {@link #getErrorInformation()#isPresent()} is {@code false} and {@link #getAvailableShippingOptions()}
	 * is not {@code null}; {@code false} otherwise.
	 */
	default boolean isSuccessful() {
		return !getErrorInformation().isPresent() && getAvailableShippingOptions() != null;
	}

	/**
	 * Returns available shipping options if the result was successful. Should only be called if {@link #isSuccessful()} returns {@code true}.
	 *
	 * @return a non-null list of {link ShippingOption}s if the request was successful, otherwise result is undefined.
	 */
	List<ShippingOption> getAvailableShippingOptions();

	/**
	 * Returns additional error information if the shipping calculation was not successful.
	 *
	 * @return additional error information if the result was not successful, or {@link Optional#empty()} if successful.
	 */
	Optional<ShippingCalculationResult.ErrorInformation> getErrorInformation();

	/**
	 * Returns an error description by calling {@link ShippingCalculationResult.ErrorInformation#getErrorDescription()} if
	 * {@link #getErrorInformation()} is present, or an empty string or placeholder text if {@code emptyStringIfNoErrorInformation} is
	 * {@code true} or {@code false} respectively.
	 *
	 * @param emptyStringIfNoErrorInformation whether an empty string should be returned if no error information is available; otherwise the
	 *                                        default text of 'No error information provided.'.
	 * @return an error description String; never {@code null}.
	 */
	default String getErrorDescription(boolean emptyStringIfNoErrorInformation) {
		final Optional<ShippingCalculationResult.ErrorInformation> errorInformation = getErrorInformation();
		if (errorInformation.isPresent()) {
			return errorInformation.get().getErrorDescription();
		} else {
			return emptyStringIfNoErrorInformation ? "" : "No error information provided.";
		}
	}

	/**
	 * Checks {@link #isSuccessful()} and if {@code false} calls {@link #throwException(String, List<StructuredErrorMessage>)}.
	 *
	 * @param errorMessage            the error message which will be appended to with additional error information.
	 * @param structuredErrorMessages the structured error messages.
	 * @throws ShippingOptionServiceException containing the error message and additional error information.
	 */
	default void throwExceptionIfUnsuccessful(String errorMessage, List<StructuredErrorMessage> structuredErrorMessages) {
		if (!isSuccessful()) {
			throwException(errorMessage, structuredErrorMessages);
		}
	}

	/**
	 * Throws an {@link ShippingOptionServiceException} with the given message along with additional error information contained in this object.
	 * It also attaches any cause in {@link #getErrorInformation()} if there is one, so the stacktrace is available.
	 * <p>
	 * This method of course should only be called if {@link #isSuccessful()} is determined to be {@code false}.
	 * If it's is called when it is {@code true} then an {@link IllegalStateException} is thrown instead.
	 *
	 * @param errorMessage the error message which will be appended to with additional error information.
	 * @param structuredErrorMessages the structured error messages.
	 * @throws ShippingOptionServiceException containing the error message and additional error information.
	 */
	default void throwException(String errorMessage, List<StructuredErrorMessage> structuredErrorMessages) {
		if (isSuccessful()) {
			throw new IllegalStateException("Requested to throw an exception when result was successful (should only call when !isSuccessful()).");
		}

		Throwable cause = null;
		final Optional<ShippingCalculationResult.ErrorInformation> errorInformationOptional = getErrorInformation();
		if (errorInformationOptional.isPresent()) {
			cause = errorInformationOptional.get().getCause().orElse(null);
		}

		throw new ShippingOptionServiceException(
				errorMessage + " " + getErrorDescription(false),
				structuredErrorMessages,
				cause);
	}

	/**
	 * Logs at error level with the given logger the given message along with additional error information contained in this object. It also includes
	 * any cause in {@link #getErrorInformation()} if there is one, so the stacktrace is available.
	 * <p>
	 * This method of course should only be called if {@link #isSuccessful()} is determined to be {@code false}. If it's is called when it is
	 * {@code true} then an {@link IllegalStateException} is thrown instead.
	 *
	 * @param log          the log to output an error level log message to
	 * @param errorMessage the error message which will be appended to with additional error information.
	 * @see #logError(Logger, String, boolean) for the method that this method delegates to.
	 */
	default void logError(Logger log, String errorMessage) {
		logError(log, errorMessage, true);
	}

	/**
	 * Logs at error level with the given logger the given message along with additional error information contained in this object. It optionally
	 * includes any cause in {@link #getErrorInformation()} if there is one and it is requested, so the stacktrace is available.
	 * <p>
	 * This method of course should only be called if {@link #isSuccessful()} is determined to be {@code false}. If it's is called when it is
	 * {@code true} then an {@link IllegalStateException} is thrown instead.
	 *
	 * @param log          the log to output an error level log message to
	 * @param errorMessage the error message which will be appended to with additional error information.
	 * @param includeCause {@code true} if any {@link Throwable} cause should be included in the error message logged.
	 */
	default void logError(Logger log, String errorMessage, boolean includeCause) {
		if (isSuccessful()) {
			throw new IllegalStateException("Requested to log an error when the result was successful (should only call when !isSuccessful()).");
		}

		final String errorDescription = getErrorDescription(false);
		if (includeCause) {
			Throwable cause = null;
			final Optional<ShippingCalculationResult.ErrorInformation> errorInformationOptional = getErrorInformation();
			if (errorInformationOptional.isPresent()) {
				cause = errorInformationOptional.get().getCause().orElse(null);
			}

			log.error(errorMessage + " " + errorDescription, cause);
		} else {
			log.error(errorMessage + " " + errorDescription);
		}
	}
}
