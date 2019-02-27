/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.validation.constraints;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

import com.elasticpath.validation.validators.impl.SimpleLengthValidator;

/**
 * A constraint for correct field size.
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = SimpleLengthValidator.class)
@Documented
public @interface LengthConstraint {
	/**
	 * Constraint violation message.
	 *
	 * @return String message, default notBlank
	 */
	String message() default "{field.invalid.size}";

	/**
	 * Groups associated to this constraint.
	 *
	 * @return Class[] groups, default empty
	 */
	Class<?>[] groups() default {};

	/**
	 * Payload for the constraint.
	 *
	 * @return Class[] payload, default empty
	 */
	Class<? extends Payload>[] payload() default {};

	/**
	 * Name of the field being validated.
	 *
	 * @return field name.
	 */
	String fieldName() default "";

	/**
	 * Required minimum size.
	 *
	 * @return 0 if not specified.
	 */
	int min() default 0;

	/**
	 * Required maximum size.
	 *
	 * @return {@link Integer#MAX_VALUE} if not specified.
	 */
	int max() default Integer.MAX_VALUE;
}
