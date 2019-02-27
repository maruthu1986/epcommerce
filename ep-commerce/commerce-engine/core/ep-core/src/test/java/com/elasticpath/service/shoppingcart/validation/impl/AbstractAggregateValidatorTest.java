/**
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shoppingcart.validation.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.util.Collection;
import java.util.Collections;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.base.common.dto.StructuredErrorMessage;
import com.elasticpath.service.shoppingcart.validation.Validator;

/**
 * Unit tests for {@link AbstractAggregateValidator}.
 */

@RunWith(MockitoJUnitRunner.class)
public class AbstractAggregateValidatorTest {

	private static final String DATA_KEY = "key";

	private static final StructuredErrorMessage FIRST_ERROR_MESSAGE = new StructuredErrorMessage("first.error", "first.debug.message",
			ImmutableMap.of(DATA_KEY, "first.value"));

	private static final StructuredErrorMessage SECOND_ERROR_MESSAGE = new StructuredErrorMessage("second.error", "second.debug.message",
			ImmutableMap.of(DATA_KEY, "second.value"));

	private static final StructuredErrorMessage THIRD_ERROR_MESSAGE = new StructuredErrorMessage("third.error", "third.debug.message",
			ImmutableMap.of(DATA_KEY, "third.value"));

	private static final StructuredErrorMessage FOURTH_ERROR_MESSAGE = new StructuredErrorMessage("fourth.error", "fourth.debug.message",
			ImmutableMap.of(DATA_KEY, "fourth.value"));

	@InjectMocks
	private ConcreteAggregateValidator aggregateValidator;

	@Mock
	private Validator<ClassThatNeedsValidation> firstValidator;

	@Mock
	private Validator<ClassThatNeedsValidation> secondValidator;

	@Mock
	private ChildClassThatNeedsValidation objectThatNeedsValidation;


	private static class ClassThatNeedsValidation {
	}

	private static class ChildClassThatNeedsValidation extends ClassThatNeedsValidation {
	}

	private static class ConcreteAggregateValidator extends AbstractAggregateValidator<ClassThatNeedsValidation, ChildClassThatNeedsValidation> {
	}


	@Before
	public void setup() {
		aggregateValidator.setValidators(ImmutableList.of(firstValidator, secondValidator));
	}

	@Test
	public void testBothValidatorsReturningErrors() {
		// Given
		given(firstValidator.validate(objectThatNeedsValidation))
				.willReturn(ImmutableList.of(FIRST_ERROR_MESSAGE, SECOND_ERROR_MESSAGE));

		given(secondValidator.validate(objectThatNeedsValidation))
				.willReturn(ImmutableList.of(THIRD_ERROR_MESSAGE, FOURTH_ERROR_MESSAGE));

		// When
		Collection<StructuredErrorMessage> errorMessages = aggregateValidator.validate(objectThatNeedsValidation);


		// Then
		assertThat(errorMessages)
				.containsExactlyInAnyOrder(FIRST_ERROR_MESSAGE, SECOND_ERROR_MESSAGE, THIRD_ERROR_MESSAGE, FOURTH_ERROR_MESSAGE);
	}

	@Test
	public void testOneValidatorsReturningErrors() {
		// Given
		given(firstValidator.validate(objectThatNeedsValidation))
				.willReturn(ImmutableList.of(FIRST_ERROR_MESSAGE, SECOND_ERROR_MESSAGE));

		given(secondValidator.validate(objectThatNeedsValidation))
				.willReturn(Collections.emptyList());

		// When
		Collection<StructuredErrorMessage> errorMessages = aggregateValidator.validate(objectThatNeedsValidation);


		// Then
		assertThat(errorMessages)
				.containsExactlyInAnyOrder(FIRST_ERROR_MESSAGE, SECOND_ERROR_MESSAGE);
	}

	@Test
	public void testNoValidatorsReturningErrors() {
		// Given
		given(firstValidator.validate(objectThatNeedsValidation))
				.willReturn(Collections.emptyList());

		given(secondValidator.validate(objectThatNeedsValidation))
				.willReturn(Collections.emptyList());

		// When
		Collection<StructuredErrorMessage> errorMessages = aggregateValidator.validate(objectThatNeedsValidation);


		// Then
		assertThat(errorMessages)
				.isEmpty();
	}

}
