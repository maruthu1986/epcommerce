/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.common.validator;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.groups.Default;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;

/**
 * Test the functionality of {@link EpDomainValidator}.
 */
@RunWith(MockitoJUnitRunner.class)
public class EpDomainValidatorTest {

	private static final String FIELD_NAME = "fieldName";

	@Mock
	private Validator validator;

	@Mock
	private BeanFactory coreBeanFactory;

	@Mock
	private EpConstraintViolationsInterpreter epConstraintViolationsInterpreter;

	@InjectMocks
	private EpDomainValidator epDomainValidator;

	/**
	 * Test validate domain object.
	 */
	@Test
	public void testValidate() {
		DomainObject domainObject = new DomainObject();

		when(coreBeanFactory.getBean("validator")).thenReturn(validator);
		HashSet<ConstraintViolation<DomainObject>> constraintViolations = new HashSet<>();
		when(validator.validate(domainObject, Default.class)).thenReturn(constraintViolations);
		ExecutionResult<Void> validationResult = ExecutionResultFactory.createReadOK(null);
		when(epConstraintViolationsInterpreter.interpret(constraintViolations)).thenReturn(validationResult);

		ExecutionResult<Void> result = epDomainValidator.validate(domainObject, Default.class);

		verify(validator).validate(domainObject, Default.class);
		verify(epConstraintViolationsInterpreter).interpret(constraintViolations);
		assertEquals("Result should be the same returned by the constraint summariser", validationResult, result);
	}

	/**
	 * Test validate property.
	 */
	@Test
	public void testValidateProperty() {
		DomainObject domainObject = new DomainObject();

		when(coreBeanFactory.getBean("validator")).thenReturn(validator);
		HashSet<ConstraintViolation<DomainObject>> constraintViolations = new HashSet<>();
		when(validator.validateProperty(domainObject, FIELD_NAME, Default.class)).thenReturn(constraintViolations);
		ExecutionResult<Void> validationResult = ExecutionResultFactory.createReadOK(null);
		when(epConstraintViolationsInterpreter.interpret(constraintViolations)).thenReturn(validationResult);

		ExecutionResult<Void> result = epDomainValidator.validateProperty(domainObject, FIELD_NAME, Default.class);

		verify(validator).validateProperty(domainObject, FIELD_NAME, Default.class);
		verify(epConstraintViolationsInterpreter).interpret(constraintViolations);
		assertEquals("Result should be the same returned by the constraint summariser", validationResult, result);
	}

	/**
	 * Test validation with null domain object.
	 */
	@Test(expected = AssertionError.class)
	public void testValidationWithNullDomainObject() {
		epDomainValidator.validate(null, Default.class);
	}

	/**
	 * Test validate property validation with null domain object.
	 */
	@Test(expected = AssertionError.class)
	public void testValidatePropertyValidationWithNullDomainObject() {
		epDomainValidator.validateProperty(null, FIELD_NAME, Default.class);
	}

	/**
	 * A simple bean to test validation.
	 */
	private class DomainObject {
		DomainObject() {
			//Do nothing
		}
	}
}
