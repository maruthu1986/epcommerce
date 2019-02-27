/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.datapopulation.cli.tool.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark methods to be called after all global CLI options (methods annotated by {@link DpCliOption}) are processed by the
 * {@link com.elasticpath.datapopulation.cli.tool.DataPopulationCliApplication} class.
 *
 * @see BeforeAllGlobalCliOptions
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AfterAllGlobalCliOptions {
}
