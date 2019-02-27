/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.validation.constraints;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

import com.elasticpath.validation.validators.impl.LongTextValueSizeValidator;

/**
 * A constraint that checks the long text value size.
 */
@Target({ ElementType.TYPE, ElementType.ANNOTATION_TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = LongTextValueSizeValidator.class)
@Documented
public @interface LongTextValueSize {

	/**
	 * Return the size the element must be higher or equal to.
	 *
	 * @return int min, default 0
	 */
	int min() default 0;

	/**
	 *
	 * Return the size the element must be lower or equal to.
	 *
	 * @return int max, default Integer.MAX_VALUE
	 */
	int max() default Integer.MAX_VALUE;

	/**
	 * Constraint violation message.
	 *
	 * @return String message, default LongTextValueSize.message
	 */
	String message() default "{com.elasticpath.validation.constraints.LongTextValueSize.message}";

	/**
	 * Groups associated to this constraint.
	 *
	 * @return Class[] groups, default empty
	 */
	Class<?>[] groups() default { };

	/**
	 * Payload for the constraint.
	 *
	 * @return Class[] payload, default empty
	 */
	Class<? extends Payload>[] payload() default { };
}
