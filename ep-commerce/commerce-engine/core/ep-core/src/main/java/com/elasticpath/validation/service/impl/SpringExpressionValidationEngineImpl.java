/**
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.validation.service.impl;

import java.lang.reflect.Array;
import java.util.Collection;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.EmailValidator;
import org.apache.log4j.Logger;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import com.elasticpath.tags.domain.Condition;
import com.elasticpath.tags.domain.TagDefinition;
import com.elasticpath.tags.domain.TagValueType;
import com.elasticpath.validation.domain.ValidationConstraint;
import com.elasticpath.validation.domain.ValidationResult;
import com.elasticpath.validation.domain.impl.FailedValidationResult;
import com.elasticpath.validation.service.ValidationEngine;

/**
 * Spring's Valang validation engine that performs validation on a declarative constraint.
 */
public class SpringExpressionValidationEngineImpl implements ValidationEngine {

	private static final Logger LOG = Logger.getLogger(SpringExpressionValidationEngineImpl.class);

	private final ExpressionParser expressionParser = new SpelExpressionParser();

	private final StandardEvaluationContext context = new StandardEvaluationContext();

	/**
	 * Constructor.
	 */
	public SpringExpressionValidationEngineImpl() {
		try {
			context.registerFunction("isValidConditionType", SpringExpressionValidationEngineImpl.class
						.getMethod("isValidConditionType", Object.class));
			context.registerFunction("length", SpringExpressionValidationEngineImpl.class.getMethod("length", Object.class));
			context.registerFunction("email", SpringExpressionValidationEngineImpl.class.getMethod("email", Object.class));
		} catch (NoSuchMethodException exception) {
			LOG.error("No method found: " + exception.getMessage());
		}
	}

	@Override
	public ValidationResult validate(final Object valueToValidate,
									 final ValidationConstraint constraint) {

		if (constraint != null && StringUtils.isNotBlank(constraint.getConstraint())) {
			context.setRootObject(valueToValidate);
			try {
				final Expression expression = expressionParser.parseExpression(constraint.getConstraint());
				Boolean valid = expression.getValue(context, Boolean.class);
				// no easy way to get the specific validaton that failed
				return valid ? ValidationResult.VALID : new FailedValidationResult(constraint,
						ImmutableList.of(String.format("Failed to validate constraint: %s", constraint.getConstraint())));
			} catch (SpelEvaluationException exception) {
				LOG.error("Spring expression evaluation error: " + exception.getMessage());
				return new FailedValidationResult(constraint, ImmutableList.of(exception));
			}
		}

		return ValidationResult.VALID;
	}

	/**
	 * Checks if the object is a valid Condition class used as a defined function which needs to be declared as public and static.
	 * @param object object
	 * @return true if object is a valid Condition class
	 */
	public static boolean isValidConditionType(final Object object) {
		if (object instanceof Condition) {

			final Condition tagCondition = (Condition) object;
			TagDefinition tagDefinition = tagCondition.getTagDefinition();
			Object tagValue = tagCondition.getTagValue();

			if (tagDefinition == null || tagValue == null) {
				return false;
			}

			TagValueType tagValueType = tagDefinition.getValueType();
			try {
				return tagValueType != null && tagValueType.getJavaType() != null &&  Class.forName(tagValueType.getJavaType()).isInstance(tagValue);
			} catch (ClassNotFoundException e) {
				LOG.error("Class not found: " + e.getLocalizedMessage());
				return false;
			}
		}
		return false;
	}

	/**
	 * A Valang specific function that checks the length of an array or String which needs to be declared as public and static.
	 * @param object object
	 * @return the length
	 */
	public static int length(final Object object) {
		if (object.getClass().isArray()) {
			return Array.getLength(object);
		} else if (object instanceof Collection) {
			return ((Collection) object).size();
		}
		return object.toString().length();
	}

	/**
	 * A Valang specific function that validates email which needs to be declared as public and static.
	 * @param object object
	 * @return true if the email is valid
	 */
	public static boolean email(final Object object) {
		return EmailValidator.getInstance().isValid(object.toString());
	}

}
