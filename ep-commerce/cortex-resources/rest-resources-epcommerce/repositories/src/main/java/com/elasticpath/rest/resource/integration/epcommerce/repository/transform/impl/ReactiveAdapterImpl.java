/**
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.transform.impl;

import java.util.concurrent.Callable;
import java.util.function.Supplier;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.functions.Function;

import com.elasticpath.base.exception.structured.EpValidationException;
import com.elasticpath.commons.exception.InvalidBusinessStateException;
import com.elasticpath.commons.exception.UnavailableException;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.ExceptionTransformer;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.ReactiveAdapter;

/**
 * Reactive Adapter implementation.
 */
@Singleton
@Named("reactiveAdapter")
public class ReactiveAdapterImpl implements ReactiveAdapter {

	private final ExceptionTransformer exceptionTransformer;

	/**
	 * Constructor.
	 *
	 * @param exceptionTransformer the exception transformer
	 */
	@Inject
	public ReactiveAdapterImpl(
			@Named("exceptionTransformer")
			final ExceptionTransformer exceptionTransformer) {
		this.exceptionTransformer = exceptionTransformer;
	}

	@Override
	public <T> Observable<T> fromService(final Callable<T> serviceCall) {
		return fromService(serviceCall, handleNullAsObservable(Observable.empty()));
	}

	@Override
	public <T> Observable<T> fromService(final Callable<T> serviceCall, final String notFoundExceptionMessage) {
		return fromService(serviceCall, handleNullAsError(notFoundExceptionMessage));
	}

	@Override
	public <T> Observable<T> fromService(final Callable<T> serviceCall, final Observable<T> defaultItem) {
		return fromService(serviceCall, handleNullAsObservable(defaultItem));
	}

	private <T> Observable<T> fromService(final Callable<T> serviceCall, final Function<Throwable, ObservableSource<? extends T>> nullHandler) {
		return Observable.fromCallable(serviceCall)
				.onErrorResumeNext(handleServiceExceptionsAsObservable())
				.onErrorResumeNext(nullHandler);
	}

	@Override
	public <T> Maybe<T> fromServiceAsMaybe(final Callable<T> serviceCall) {
		return fromService(serviceCall)
				.singleElement();
	}

	@Override
	public <T> Maybe<T> fromServiceAsMaybe(final Callable<T> serviceCall, final Maybe<T> defaultItem) {
		return fromService(serviceCall, defaultItem.toObservable())
				.singleElement();
	}
	
	@Override
	public <T> Single<T> fromServiceAsSingle(final Callable<T> serviceCall) {
		return fromServiceAsSingle(serviceCall, "");
	}

	@Override
	public <T> Single<T> fromServiceAsSingle(final Callable<T> serviceCall, final String notFoundExceptionMessage) {
		return fromService(serviceCall, notFoundExceptionMessage)
				.singleElement().toSingle();
	}

	@Override
	public <T> Single<T> fromServiceAsSingle(final Callable<T> serviceCall, final Single<T> defaultItem) {
		return fromService(serviceCall, defaultItem.toObservable())
				.singleElement().toSingle();
	}

	@Override
	public Completable fromServiceAsCompletable(final Runnable serviceCall) {
		return Completable.fromRunnable(serviceCall)
				.onErrorResumeNext(handleServiceExceptionsAsCompletable());
	}

	@Override
	public <T> Observable<T> fromNullable(final Callable<T> objectCall, final String notFoundExceptionMessage) {
		return Observable.fromCallable(objectCall)
				.onErrorResumeNext(handleNullAsError(notFoundExceptionMessage));
	}

	@Override
	public <T> Single<T> fromNullableAsSingle(final Callable<T> objectCall, final String notFoundExceptionMessage) {
		return fromNullable(objectCall, notFoundExceptionMessage)
				.singleElement().toSingle();
	}
	
	@Override
	public <T> Observable<T> fromRepository(final Supplier<ExecutionResult<? extends Iterable<T>>> repositoryCall) {
		return Observable.defer(() -> repositoryCall.get().toObservable());
	}

	@Override
	public Completable fromRepositoryAsCompletable(final Supplier<ExecutionResult<?>> repositoryCall) {
		return Completable.defer(() -> repositoryCall.get().toCompletable());
	}

	@Override
	public <T> Single<T> fromRepositoryAsSingle(final Supplier<ExecutionResult<T>> repositoryCall) {
		return Single.defer(() -> repositoryCall.get().toSingle());
	}

	private <T extends Throwable> Throwable transformToResourceOperationFailureIfPossible(final T throwable) {
		if (throwable instanceof InvalidBusinessStateException) {
			return exceptionTransformer.getResourceOperationFailure((InvalidBusinessStateException) throwable);
		} else if (throwable instanceof EpValidationException) {
			return exceptionTransformer.getResourceOperationFailure((EpValidationException) throwable);
		} else if (throwable instanceof UnavailableException) {
			return exceptionTransformer.getResourceOperationFailure((UnavailableException) throwable);
		}
		return throwable;
	}

	private <T> Function<Throwable, Observable<? extends T>> handleServiceExceptionsAsObservable() {
		return throwable -> Observable.error(transformToResourceOperationFailureIfPossible(throwable));
	}

	private Function<Throwable, Completable> handleServiceExceptionsAsCompletable() {
		return throwable -> Completable.error(transformToResourceOperationFailureIfPossible(throwable));
	}

	private <T> Function<Throwable, ObservableSource<? extends T>> handleNullAsError(final String notFoundExceptionMessage) {
		return throwable -> handleNull(Observable.error(ResourceOperationFailure.notFound(notFoundExceptionMessage, throwable)), throwable);
	}

	private <T> Function<Throwable, ObservableSource<? extends T>> handleNullAsObservable(final Observable<T> observable) {
		return throwable -> handleNull(observable, throwable);
	}

	private <T> ObservableSource<? extends T> handleNull(final Observable<T> observable, final Throwable throwable) {
		if (throwable instanceof NullPointerException) {
			return observable;
		}
		return Observable.error(throwable);
	}
}
