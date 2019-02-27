/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.common.validator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Path;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.rest.command.ExecutionResult;

/**
 * Test class for {@link EpConstraintViolationsInterpreterImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class EpConstraintViolationsInterpreterImplTest {

	private static final String VIOLATION_MESSAGE = "Validation constraint violated";
	private static final String VIOLATED_PROPERTY = "violated Property";
	private static final String FIELD_NAME = "fieldName";
	private static final String EXPECTED_VIOLATION_MESSAGE = FIELD_NAME + ": " + VIOLATION_MESSAGE;
	private static final String EXPECTED_VIOLATION_MESSAGE_WITH_NULL_FIELD_MAP = VIOLATED_PROPERTY + ": " + VIOLATION_MESSAGE;
	private static final String EXPECTED_ERROR = "The error should consist of the field name plus the violation message";
	private static final String EXPECTED_ERROR_WITH_NO_FIELD_NAME =
			"The error should consist of the violation property path plus the violation message.";
	private static final String SHOULD_HAVE_FAILED = "The operation should have failed";
	private static final String SHOULD_BE_SUCCESSFUL = "Operation should be successful.";
	private static final String RESULT_SHOULD_BE_NULL = "Result should be null";

	@Mock
	private ConstraintViolation<Void> constraintViolation;

	@Mock
	private Path path;

	/**
	 * Test interpret violations.
	 */
	@Test
	public void testInterpretWithViolations() {

		Map<String, String> fieldNameMap = new HashMap<>();
		fieldNameMap.put(VIOLATED_PROPERTY, FIELD_NAME);
		EpConstraintViolationsInterpreterImpl epConstraintViolationsInterpreterImpl = new EpConstraintViolationsInterpreterImpl(fieldNameMap);

		Set<ConstraintViolation<Void>> violations = setupConstraintViolations();

		ExecutionResult<Void> result = epConstraintViolationsInterpreterImpl.interpret(violations);
		assertTrue(SHOULD_HAVE_FAILED, result.isFailure());
		assertEquals(EXPECTED_ERROR, EXPECTED_VIOLATION_MESSAGE,
				result.getErrorMessage());
	}


	/**
	 * Test interpret with violations and null field name map.
	 */
	@Test
	public void testInterpretWithViolationsAndEmptyFieldMap() {

		EpConstraintViolationsInterpreterImpl epConstraintViolationsInterpreterImpl = new EpConstraintViolationsInterpreterImpl(new HashMap<>());

		Set<ConstraintViolation<Void>> violations = setupConstraintViolations();

		ExecutionResult<Void> result = epConstraintViolationsInterpreterImpl.interpret(violations);
		assertTrue(SHOULD_HAVE_FAILED, result.isFailure());
		assertEquals(EXPECTED_ERROR_WITH_NO_FIELD_NAME, EXPECTED_VIOLATION_MESSAGE_WITH_NULL_FIELD_MAP,
				result.getErrorMessage());
	}

	/**
	 * Test interpret with violation, but no field name found from field name map.
	 */
	@Test
	public void testInterpretWithViolationAndNoFieldNameFound() {

		Map<String, String> fieldNameMap = new HashMap<>();
		EpConstraintViolationsInterpreterImpl epConstraintViolationsInterpreterImpl = new EpConstraintViolationsInterpreterImpl(fieldNameMap);

		Set<ConstraintViolation<Void>> violations = setupConstraintViolations();

		ExecutionResult<Void> result = epConstraintViolationsInterpreterImpl.interpret(violations);
		assertTrue(SHOULD_HAVE_FAILED, result.isFailure());
		assertEquals(EXPECTED_ERROR_WITH_NO_FIELD_NAME, EXPECTED_VIOLATION_MESSAGE_WITH_NULL_FIELD_MAP,
				result.getErrorMessage());
	}

	/**
	 * Test interpret with no violations.
	 */
	@Test
	public void testIntepretWithNoViolations() {
		Map<String, String> fieldNameMap = new HashMap<>();
		fieldNameMap.put(VIOLATED_PROPERTY, FIELD_NAME);
		EpConstraintViolationsInterpreterImpl epConstraintViolationsInterpreterImpl = new EpConstraintViolationsInterpreterImpl(fieldNameMap);

		Set<ConstraintViolation<Integer>> violations = new HashSet<ConstraintViolation<Integer>>();

		ExecutionResult<Void> result = epConstraintViolationsInterpreterImpl.interpret(violations);
		assertTrue(SHOULD_BE_SUCCESSFUL, result.isSuccessful());
		assertNull(RESULT_SHOULD_BE_NULL, result.getData());
	}

	private Set<ConstraintViolation<Void>> setupConstraintViolations() {
		when(path.toString()).thenReturn(VIOLATED_PROPERTY);
		when(constraintViolation.getPropertyPath()).thenReturn(path);
		when(constraintViolation.getMessage()).thenReturn(VIOLATION_MESSAGE);

		Set<ConstraintViolation<Void>> violations = new HashSet<ConstraintViolation<Void>>();
		violations.add(constraintViolation);
		return violations;
	}

}
