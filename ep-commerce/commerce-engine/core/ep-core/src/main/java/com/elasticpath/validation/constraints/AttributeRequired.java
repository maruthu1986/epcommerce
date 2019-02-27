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

import com.elasticpath.validation.validators.impl.AttributeRequiredValidatorForCustomerProfile;

/**
 * A constraint that should check that required system attributes are actually defined and non-blank.
 */
@Target({ ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = AttributeRequiredValidatorForCustomerProfile.class)
@Documented
public @interface AttributeRequired {
	/**
	 * Constraint violation message.
	 *
	 * @return String message, default requiredAttribute
	 */
	String message() default "{com.elasticpath.validation.constraints.requiredAttribute}";

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
