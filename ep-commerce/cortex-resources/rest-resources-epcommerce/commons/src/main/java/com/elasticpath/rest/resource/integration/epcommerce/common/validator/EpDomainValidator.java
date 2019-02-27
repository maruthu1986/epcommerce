/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.common.validator;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.rest.command.ExecutionResult;

/**
 * Class to validate ep core object.
 */
public class EpDomainValidator {

	private final EpConstraintViolationsInterpreter epConstraintViolationsInterpreter;
	private final BeanFactory coreBeanFactory;

	/**
	 * Default constructor.
	 *
	 * @param coreBeanFactory the core bean factory
	 * @param epConstraintViolationsInterpreter the constraint violations interpreter
	 */
	public EpDomainValidator(final BeanFactory coreBeanFactory, final EpConstraintViolationsInterpreter epConstraintViolationsInterpreter) {
		this.epConstraintViolationsInterpreter = epConstraintViolationsInterpreter;
		this.coreBeanFactory = coreBeanFactory;
	}

	/**
	 * Validate domain object.
	 *
	 * @param <T> the generic type of the domain object
	 * @param domainObject the domain Object.
	 * @param groups the validation groups
	 * @return the execution result
	 */
	public <T> ExecutionResult<Void> validate(final T domainObject, final Class<?>... groups) {
		assert domainObject != null : "Domain object cannot be null.";

		final ExecutionResult<Void> result;

		Validator validator = coreBeanFactory.getBean("validator");
		Set<ConstraintViolation<T>> constraintViolations = validator.validate(domainObject, groups);
		result = epConstraintViolationsInterpreter.interpret(constraintViolations);

		return result;
	}

	/**
	 * Validate a property on a domain object.
	 *
	 * @param <T> the generic type of the domain object
	 * @param domainObject the domain Object.
	 * @param propertyName the property name
	 * @param groups the validation groups
	 * @return the execution result
	 */
	public <T> ExecutionResult<Void> validateProperty(final T domainObject, final String propertyName, final Class<?>... groups) {
		assert domainObject != null : "Domain object cannot be null.";

		final ExecutionResult<Void> result;

		Validator validator = coreBeanFactory.getBean("validator");
		Set<ConstraintViolation<T>> constraintViolations = validator.validateProperty(domainObject, propertyName, groups);
		result = epConstraintViolationsInterpreter.interpret(constraintViolations);

		return result;
	}


}
