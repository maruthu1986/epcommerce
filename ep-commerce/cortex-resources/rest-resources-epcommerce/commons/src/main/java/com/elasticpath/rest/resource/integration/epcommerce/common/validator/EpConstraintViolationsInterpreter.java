/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.common.validator;

import java.util.Set;

import javax.validation.ConstraintViolation;

import com.elasticpath.rest.command.ExecutionResult;

/**
 * Interprets constraint violations into human readable messages.
 */
public interface EpConstraintViolationsInterpreter {

	/**
	 * Interprets the given constraint violations into a readable error message.
	 *
	 * @param constraintViolations the constraint violations
	 * @param <T> the generic type of the domain object
	 * @return the execution result with the interpreted error message.
	 * @deprecated not needed anymore due to the introduction of structured error message.
	 */
	@Deprecated
	<T> ExecutionResult<Void> interpret(Set<ConstraintViolation<T>> constraintViolations);

}
