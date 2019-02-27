/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.datapopulation.cli.tool.validators;

import java.lang.reflect.Method;

import com.google.common.base.Predicate;
import org.apache.log4j.Logger;

/**
 * A {@link Predicate} which validates whether a {@link Method} is valid to be invoked by
 * {@link com.elasticpath.datapopulation.cli.tool.DataPopulationCliApplication as a global configuration method.
 */
public class GlobalConfigurationMethodValidator implements Predicate<Method> {
	private static final Logger LOG = Logger.getLogger(GlobalConfigurationMethodValidator.class);

	/**
	 * Checks whether the {@link Method} is either a zero-arg method, or contains 1 parameter and that parameter is either a
	 * String parameter, or a String[] parameter (which also means a String var-args parameter is supported instead).
	 *
	 * @param method the method to validate.
	 * @return whether the method is valid per the rules described above.
	 */
	@Override
	public boolean apply(final Method method) {
		boolean result = false;

		if (method != null) {
			// We only support a global configuration method currently that has either zero or a single argument.
			// That single argument can either be a String, String[], or String... argument (which is converted to a String[] under the covers).

			final Class<?>[] parameterTypes = method.getParameterTypes();

			if (parameterTypes.length == 0) {
				result = true;
			} else if (parameterTypes.length == 1) {
				final Class<?> firstAndOnlyArgumentType = parameterTypes[0];

				if (firstAndOnlyArgumentType == String.class || firstAndOnlyArgumentType == String[].class) {
					result = true;
				} else {
					LOG.warn("Invalid global configuration method " + method + " contained parameter type " + firstAndOnlyArgumentType.getName()
							+ ", expected: String, String[] or String varargs.");
				}
			} else {
				LOG.warn("Invalid global configuration method " + method + " contains " + parameterTypes.length + " parameter types, expected: 1.");
			}
		}

		return result;
	}
}
