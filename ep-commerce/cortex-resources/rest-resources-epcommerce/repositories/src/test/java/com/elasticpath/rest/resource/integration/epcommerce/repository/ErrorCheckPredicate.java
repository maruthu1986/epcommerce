/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository;

import io.reactivex.functions.Predicate;

import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.ResourceStatus;

/**
 * Predicate for checking error message and status.
 */
public final class ErrorCheckPredicate implements Predicate<Throwable> {

	private final String errorMessage;
	private final ResourceStatus resourceStatus;

	private ErrorCheckPredicate(final String errorMessage, final ResourceStatus resourceStatus) {
		this.errorMessage = errorMessage;
		this.resourceStatus = resourceStatus;
	}

	/**
	 * Creates a predicate for asserting errors.
	 *
	 * @param errorMessage   error message
	 * @param resourceStatus resource status
	 * @return error predicate
	 */
	public static Predicate<Throwable> createErrorCheckPredicate(final String errorMessage, final ResourceStatus resourceStatus) {
		return new ErrorCheckPredicate(errorMessage, resourceStatus);
	}

	@Override
	public boolean test(final Throwable throwable) throws Exception {
		return throwable instanceof ResourceOperationFailure
				&& throwable.getLocalizedMessage().equals(errorMessage)
				&& ((ResourceOperationFailure) throwable).getResourceStatus().equals(resourceStatus);
	}
}