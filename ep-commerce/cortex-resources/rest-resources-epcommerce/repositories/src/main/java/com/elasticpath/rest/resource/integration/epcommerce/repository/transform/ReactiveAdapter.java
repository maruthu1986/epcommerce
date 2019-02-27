/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.transform;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;

import com.elasticpath.rest.command.ExecutionResult;

/**
 * Converts calls to nullable services and legacy repositories to RX java's reactive equivalents.
 */
public interface ReactiveAdapter {

	/**
	 * Creates an Observable with a deferred execution of a nullable CE service call.
	 * <p>In the case the CE service call will return null this method will return an empty Observable.</p>
	 * <p>In the case the CE service call will throw an unchecked exception this exception is captured and registered on
	 * the Observable's error channel.</p>
	 * @param serviceCall service call
	 * @param <T>         service call return type
	 * @return Observable with deferred service call execution
	 */
	<T> Observable<T> fromService(Callable<T> serviceCall);

	/**
	 * Creates an Observable with a deferred execution of a nullable CE service call.
	 * <p>In the case the CE service call will return null a "not found" ResourceOperationFailure
	 * is created and registered on the Observable's error channel.</p>
	 * <p>In the case the CE service call will throw an unchecked exception this exception is captured 
	 * and registered on the Observable's error channel.</p>
	 *
	 * @param serviceCall              service call
	 * @param notFoundExceptionMessage the exception message
	 * @param <T>                      service call return type
	 * @return Observable with deferred service call execution
	 */
	<T> Observable<T> fromService(Callable<T> serviceCall, String notFoundExceptionMessage);

	/**
	 * Creates an Observable with a deferred execution of a nullable CE service call.
	 * <p>In the case the CE service call will return null the passed in observable will be returned.</p>
	 * <p>In the case the CE service call will throw an unchecked exception this exception is captured
	 * and registered on the Observable's error channel.</p>
	 *
	 * @param serviceCall  service call
	 * @param defaultItem the observable to switch to if the service call returns null
	 * @param <T>          service call return type
	 * @return Observable with deferred service call execution
	 */
	<T> Observable<T> fromService(Callable<T> serviceCall, Observable<T> defaultItem);
	
	/**
	 * Creates a Maybe with a deferred execution of a nullable CE service call.
	 * <p>In the case the CE service call will return null this method will return an empty Maybe.</p>
	 * <p>In the case the CE service call will throw an unchecked exception this exception is captured
	 * and registered on the Maybe's error channel.</p>
	 *
	 * @param serviceCall service call
	 * @param <T>         service call return type
	 * @return Maybe with deferred service call execution
	 */
	<T> Maybe<T> fromServiceAsMaybe(Callable<T> serviceCall);

	/**
	 * Creates a Maybe with a deferred execution of a nullable CE service call.
	 * <p>In the case the CE service call will return null the passed in maybe will be returned.</p>
	 * <p>In the case the CE service call will throw an unchecked exception this exception is captured
	 * and registered on the Maybe's error channel.</p>
	 *
	 * @param serviceCall service call
	 * @param defaultItem the maybe to switch to if the service call returns null
	 * @param <T>         service call return type
	 * @return Maybe with deferred service call execution
	 */
	<T> Maybe<T> fromServiceAsMaybe(Callable<T> serviceCall, Maybe<T> defaultItem);

	/**
	 * Creates an Single with a deferred execution of a nullable CE service call.
	 * <p>In the case the CE service call will return null a "not found" ResourceOperationFailure
	 * is created and registered on the Single's error channel.</p> 
	 * <p>In the case the CE service call will throw an unchecked exception this exception is captured 
	 * and registered on the Single's error channel.</p>
	 *
	 * @param serviceCall service call
	 * @param <T>         service call return type
	 * @return Single with deferred service call execution
	 */
	<T> Single<T> fromServiceAsSingle(Callable<T> serviceCall);

	/**
	 * Creates an Single with a deferred execution of a nullable CE service call.
	 * <p>In the case the CE service call will return null a "not found" ResourceOperationFailure
	 * is created and registered on the Single's error channel.</p> 
	 * <p>In the case the CE service call will throw an unchecked exception this exception is captured 
	 * and registered on the Single's error channel.</p>
	 *
	 * @param serviceCall              service call
	 * @param notFoundExceptionMessage the exception message
	 * @param <T>                      service call return type
	 * @return Single with deferred service call execution
	 */
	<T> Single<T> fromServiceAsSingle(Callable<T> serviceCall, String notFoundExceptionMessage);

	/**
	 * Creates an Single with a deferred execution of a nullable CE service call.
	 * <p>In the case the CE service call will return null the passed in single will be returned.</p>
	 * <p>In the case the CE service call will throw an unchecked exception this exception is captured
	 * and registered on the Single's error channel.</p>
	 *
	 * @param serviceCall service call
	 * @param defaultItem the single to switch to if the service call returns null
	 * @param <T>         service call return type
	 * @return Single with deferred service call execution
	 */
	<T> Single<T> fromServiceAsSingle(Callable<T> serviceCall, Single<T> defaultItem);

	/**
	 * Creates an Completable with a deferred execution of a nullable CE service call.
	 * <p>In the case the CE service call will throw an unchecked exception this exception
	 * is captured and registered on the Completable's error channel.</p>
	 *
	 * @param serviceCall service call
	 * @return Completable with deferred service call execution
	 */
	 Completable fromServiceAsCompletable(Runnable serviceCall);

	/**
	 * Creates an Observable with a deferred execution of a nullable call.
	 * <p>In the case the call will return null a "not found" ResourceOperationFailure
	 * is created and registered on the Observable's error channel.</p>
	 *
	 * @param objectCall               object call
	 * @param notFoundExceptionMessage the exception message
	 * @param <T>                      object call return type
	 * @return Observable with deferred object call execution
	 */
	<T> Observable<T> fromNullable(Callable<T> objectCall, String notFoundExceptionMessage);

	/**
	 * Creates an Single with a deferred execution of a nullable call.
	 * <p>In the case the call will return null a "not found" ResourceOperationFailure
	 * is created and registered on the Single's error channel.</p>
	 *
	 * @param objectCall               object call
	 * @param notFoundExceptionMessage the exception message
	 * @param <T>                      object call return type
	 * @return Single with deferred object call execution
	 */
	<T> Single<T> fromNullableAsSingle(Callable<T> objectCall, String notFoundExceptionMessage);

	/**
	 * Creates an Observable with a deferred execution of a given legacy repository method which emits
	 * an ExecutionResult which is compatible with Observable semantics {@see ExecutionResult#toObservable}.
	 *
	 * @param repositoryCall non reactive repository call
	 * @param <T>            observable return type
	 * @return Observable with deferred repository execution
	 */
	<T> Observable<T> fromRepository(Supplier<ExecutionResult<? extends Iterable<T>>> repositoryCall);

	/**
	 * Creates an Observable with a deferred execution of a given legacy repository method which emits
	 * an ExecutionResult which is compatible with Completable semantics {@see ExecutionResult#toCompletable}.
	 *
	 * @param repositoryCall non reactive repository call
	 * @return Observable with deferred repository execution
	 */
	Completable fromRepositoryAsCompletable(Supplier<ExecutionResult<?>> repositoryCall);

	/**
	 * Creates an Observable with a deferred execution of a given legacy repository method which emits
	 * an ExecutionResult which is compatible with Single semantics {@see ExecutionResult#toSingle}.
	 *
	 * @param repositoryCall non reactive repository call
	 * @param <T>            repository call return type
	 * @return Observable with deferred repository execution
	 */
	<T> Single<T> fromRepositoryAsSingle(Supplier<ExecutionResult<T>> repositoryCall);
}
