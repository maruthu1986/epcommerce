/**
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.transform.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.base.exception.structured.EpValidationException;
import com.elasticpath.commons.exception.InvalidBusinessStateException;
import com.elasticpath.commons.exception.UserIdExistException;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.ExceptionTransformer;

@RunWith(MockitoJUnitRunner.class)
public class ReactiveAdapterImplTest {

	private static final String HELLO_WORLD = "Hello World!";
	private static final String DEFAULT_ITEM = "default item";
	private static final String NOT_FOUND_EXCEPTION_MESSAGE = "Not found.";

	@InjectMocks
	private ReactiveAdapterImpl reactiveAdapter;

	@Mock
	private ExceptionTransformer exceptionTransformer;

	@Test
	public void fromServiceShouldDeferExecution() {

		reactiveAdapter.fromService(() -> HELLO_WORLD)
				.test()
				.assertNoErrors()
				.assertValue(HELLO_WORLD);
	}

	@Test
	public void fromServiceShouldDeferExecutionAndHandleNull() {

		reactiveAdapter.fromService(() -> null)
				.test()
				.assertNoErrors()
				.assertValueCount(0);
	}

	@Test
	public void fromServiceShouldDeferExecutionAndHandleNullAsError() {

		reactiveAdapter.fromService(() -> null, NOT_FOUND_EXCEPTION_MESSAGE)
				.test()
				.assertError(ResourceOperationFailure.class)
				.assertError(throwable -> NOT_FOUND_EXCEPTION_MESSAGE.equals(throwable.getMessage()));
	}

	@Test
	public void fromServiceShouldDeferExecutionAndHandleNullAsDefaultItem() {

		reactiveAdapter.fromService(() -> null, Observable.just(DEFAULT_ITEM))
				.test()
				.assertNoErrors()
				.assertValue(DEFAULT_ITEM);
	}

	@Test
	public void fromServiceShouldDeferExecutionAndHandleExceptions() {

		reactiveAdapter.fromService(() -> {
			throw new IllegalArgumentException();
		})
				.test()
				.assertError(IllegalArgumentException.class);
	}

	@Test
	public void fromServiceShouldDeferExecutionAndHandleValidationExceptions() {
		when(exceptionTransformer.getResourceOperationFailure(any(EpValidationException.class)))
				.thenReturn(ResourceOperationFailure.badRequestBody());

		reactiveAdapter.fromService(() -> {
			throw new EpValidationException("Exception Message", Collections.emptyList());
		})
				.test()
				.assertError(ResourceOperationFailure.badRequestBody());
	}

	@Test
	public void fromServiceShouldDeferExecutionAndHandleInvalidBusinessStateExceptions() {
		when(exceptionTransformer.getResourceOperationFailure(any(InvalidBusinessStateException.class)))
				.thenReturn(ResourceOperationFailure.stateFailure());

		reactiveAdapter.fromService(() -> {
			throw new UserIdExistException("Exception Message", Collections.emptyList());
		})
				.test()
				.assertError(ResourceOperationFailure.stateFailure());
	}

	@Test
	public void fromServiceAsMaybeShouldDeferExecution() {

		reactiveAdapter.fromServiceAsMaybe(() -> HELLO_WORLD)
				.test()
				.assertNoErrors()
				.assertValue(HELLO_WORLD);
	}

	@Test
	public void fromServiceAsMaybeShouldDeferExecutionAndHandleNull() {

		reactiveAdapter.fromServiceAsMaybe(() -> null)
				.test()
				.assertNoErrors()
				.assertValueCount(0);
	}

	@Test
	public void fromServiceAsMaybeShouldDeferExecutionAndHandleNullAsDefaultItem() {

		reactiveAdapter.fromServiceAsMaybe(() -> null, Maybe.just(DEFAULT_ITEM))
				.test()
				.assertNoErrors()
				.assertValue(DEFAULT_ITEM);
	}

	@Test
	public void fromServiceAsSingleShouldDeferExecution() {

		reactiveAdapter.fromServiceAsSingle(() -> HELLO_WORLD)
				.test()
				.assertNoErrors()
				.assertValue(HELLO_WORLD);
	}
	
	@Test
	public void fromServiceAsSingleShouldDeferExecutionAndHandleNull() {
		reactiveAdapter.fromServiceAsSingle(() -> null)
				.test()
				.assertError(ResourceOperationFailure.notFound());
	}

	@Test
	public void fromServiceAsSingleShouldDeferExecutionAndHandleNullAsError() {

		reactiveAdapter.fromServiceAsSingle(() -> null, NOT_FOUND_EXCEPTION_MESSAGE)
				.test()
				.assertError(ResourceOperationFailure.class)
				.assertError(throwable -> NOT_FOUND_EXCEPTION_MESSAGE.equals(throwable.getMessage()));
	}

	@Test
	public void fromNullableShouldDeferExecution() {
		reactiveAdapter.fromNullable(() -> HELLO_WORLD, "")
				.test()
				.assertNoErrors()
				.assertValue(HELLO_WORLD);
	}

	@Test
	public void fromNullableShouldDeferExecutionAndHandleNull() {
		String notFound = "Not found error.";
		reactiveAdapter.fromNullable(() -> null, notFound)
				.test()
				.assertError(ResourceOperationFailure.notFound(notFound));
	}

	@Test
	public void fromNullableAdSingleShouldDeferExecution() {
		reactiveAdapter.fromNullableAsSingle(() -> HELLO_WORLD, "")
				.test()
				.assertNoErrors()
				.assertValue(HELLO_WORLD);
	}

	@Test
	public void fromNullableAsSingleShouldDeferExecutionAndHandleNull() {
		String notFound = "Not found error.";
		reactiveAdapter.fromNullable(() -> null, notFound)
				.test()
				.assertError(ResourceOperationFailure.notFound(notFound));
	}
	
	@Test
	public void fromRepositoryShouldDeferExecution() throws Exception {
		reactiveAdapter.fromRepository(() -> ExecutionResultFactory
				.createReadOK(Arrays.asList("test1", "test2")))
				.test()
				.assertValueCount(2)
				.assertValues("test1", "test2");
	}

	@Test
	public void fromRepositoryAsCompletableShouldDeferExecution() throws Exception {
		reactiveAdapter.<String>fromRepositoryAsCompletable(() -> ExecutionResultFactory.createReadOK(null))
				.test()
				.assertNoErrors()
				.assertComplete();
	}

	@Test
	public void fromRepositoryAsSingleShouldDeferExecution() throws Exception {
		reactiveAdapter.fromRepositoryAsSingle(() -> ExecutionResultFactory.createReadOK("test"))
				.test()
				.assertValue("test");
	}

}