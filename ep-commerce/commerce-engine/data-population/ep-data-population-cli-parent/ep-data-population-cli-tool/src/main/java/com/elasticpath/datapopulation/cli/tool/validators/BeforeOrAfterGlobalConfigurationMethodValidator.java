/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.datapopulation.cli.tool.validators;

import java.lang.reflect.Method;

import com.google.common.base.Predicate;
import org.apache.log4j.Logger;

/**
 * A {@link Predicate} which validates whether a {@link Method} is valid to be invoked by
 * {@link com.elasticpath.datapopulation.cli.tool.DataPopulationCliApplication either before or after global configuration methods have been run.
 */
public class BeforeOrAfterGlobalConfigurationMethodValidator implements Predicate<Method> {
	private static final Logger LOG = Logger.getLogger(BeforeOrAfterGlobalConfigurationMethodValidator.class);

	/**
	 * Checks whether the {@link Method} is a zero-arg method, since that is the only method signature supported for invocation
	 * before or after global configuration methods have been invoked.
	 *
	 * @param method the method to validate.
	 * @return whether the method is a zero-arg method or not.
	 */
	@Override
	public boolean apply(final Method method) {
		boolean result = false;

		if (method != null) {
			// We only support a zero-arg methods
			final Class<?>[] parameterTypes = method.getParameterTypes();
			if (parameterTypes.length == 0) {
				result = true;
			} else {
				LOG.warn("Invalid global configuration lifecycle method " + method + ". Only zero-arg methods are supported, this method contains "
						+ parameterTypes.length + " arguments.");
			}
		}

		return result;
	}
}
