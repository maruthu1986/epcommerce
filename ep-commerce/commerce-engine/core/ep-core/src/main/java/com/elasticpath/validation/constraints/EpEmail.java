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

import com.elasticpath.validation.validators.impl.EpEmailValidator;

/**
 * A constraint that should validate an email address by pattern.
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = EpEmailValidator.class)
@Documented
public @interface EpEmail {
	/**
	 * Constraint violation message.
	 *
	 * @return String message, default emailPattern
	 */
	String message() default "{com.elasticpath.validation.constraints.emailPattern}";

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
}
