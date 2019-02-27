package com.elasticpath.extensions.rest.resource.example.resource.integration.epcommerce.repository;

import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.ResourceStatus;
import io.reactivex.functions.Predicate;

public final class FavErrorPredicate implements Predicate<Throwable> {
    private final String errorMessage;
    private final ResourceStatus resourceStatus;

    public FavErrorPredicate(final String errorMessage, final ResourceStatus resourceStatus) {
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
        return new FavErrorPredicate(errorMessage, resourceStatus);
    }

    @Override
    public boolean test(final Throwable throwable) throws Exception {
        return throwable instanceof ResourceOperationFailure
                && throwable.getLocalizedMessage().equals(errorMessage)
                && ((ResourceOperationFailure) throwable).getResourceStatus().equals(resourceStatus);
    }
}
