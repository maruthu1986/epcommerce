/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.datapopulation.cli.tool.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.elasticpath.datapopulation.cli.tool.DataPopulationCliApplication;

/**
 * Annotation to mark methods or parameters to be processed by the {@link DataPopulationCliApplication}
 * class.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DpCliOption {
	/**
	 * Gets name of the option, which must be unique within this {@link DpCliComponent}.
	 *
	 * @return the name of the option.
	 */
	String key();

	/**
	 * Tells if this annotation should define the corresponding {@link org.apache.commons.cli.Option}'s definition,
	 * or false if this annotated method should just be invoked but should not define the {@link org.apache.commons.cli.Option}.
	 *
	 * @return true if this annotation should define the corresponding {@link org.apache.commons.cli.Option}'s definition, false otherwise.
	 */
	boolean isPrimaryDefinition() default true;

	/**
	 * Tells if this option must be specified one way or the other by the user (defaults to false).
	 *
	 * @return true if mandatory, false otherwise.
	 */
	boolean mandatory() default false;

	/**
	 * Tells if this option can have an argument specified, false if it's used just as a flag argument.
	 *
	 * @return true if this can have an argument specified, false otherwise.
	 */
	boolean hasArgument();

	/**
	 * Gets the default value to use if this option is included by the user, but they didn't specify an
	 * actual value (most commonly used for flags; defaults to an empty array).
	 *
	 * @return specified default value array.
	 */
	String[] specifiedDefaultValue() default {};

	/**
	 * Gets a help message for this option (the default is a blank String, which means there is no help).
	 *
	 * @return a help message for this option.
	 */
	String help() default "";
}
