/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.common.validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Path;

import org.apache.commons.lang3.StringUtils;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.util.collection.CollectionUtil;

/**
 * Interprets constraint violations into human readable messages.
 */
public class EpConstraintViolationsInterpreterImpl implements EpConstraintViolationsInterpreter {

	private final Map<String, String> fieldNameMap;

	/**
	 * Default Constructor.
	 *
	 * @param fieldNameMap map with ep field names as keys, and cortex field names as values
	 */
	public EpConstraintViolationsInterpreterImpl(final Map<String, String> fieldNameMap) {
		this.fieldNameMap = fieldNameMap;
	}

	/**
	 * Interprets the given constraint violations into a readable error message.
	 *
	 * @param constraintViolations the constraint violations
	 * @param <T> the generic type of the domain object
	 * @return the execution result with the summarised error message.
	 */
	public <T> ExecutionResult<Void> interpret(final Set<ConstraintViolation<T>> constraintViolations) {
		ExecutionResult<Void> result;
		if (CollectionUtil.isNotEmpty(constraintViolations)) {
			List<String> constraintMessages = new ArrayList<>(constraintViolations.size());

			for (ConstraintViolation<T> constraintViolation : constraintViolations) {
				String constraintMessage = getFieldName(constraintViolation.getPropertyPath()) + ": " + constraintViolation.getMessage();
				constraintMessages.add(constraintMessage);
			}
			result = ExecutionResultFactory.createBadRequestBody(StringUtils.join(constraintMessages, "; "));
		} else {
			result = ExecutionResultFactory.createUpdateOK();
		}
		return result;
	}

	private String getFieldName(final Path propertyPath) {
		String coreField = propertyPath.toString();
		String result = coreField;
		if (fieldNameMap != null) {
			String fieldName = fieldNameMap.get(coreField);
			if (fieldName != null) {
				result = fieldName;
			}
		}
		return result;
	}

}
