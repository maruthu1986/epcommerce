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

import com.elasticpath.validation.validators.impl.CountryValidatorForAddress;
import com.elasticpath.validation.validators.impl.CountryValidatorForString;

/**
 * A constraint that checks for a valid sub-country.
 */
@Target({ ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = { CountryValidatorForString.class, CountryValidatorForAddress.class })
@Documented
public @interface ValidCountry {
	/**
	 * Constraint violation message.
	 *
	 * @return String message, default validCountry
	 */
	String message() default "{com.elasticpath.validation.constraints.validCountry}";

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
