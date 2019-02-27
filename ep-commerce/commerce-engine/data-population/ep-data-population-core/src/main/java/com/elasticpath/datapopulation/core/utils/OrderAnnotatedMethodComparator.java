/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.datapopulation.core.utils;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Comparator;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * A {@link Comparator} implementation which compares two {@link Method} objects, primarily by their {@link Order} annotation
 * if specified, or if not, arbitrarily based on the declaring class and method signature.
 * It's arbitrary because if a {@link Method} is not annotated with {@link Order} then they haven't explicitly assigned themselves an order value.
 */
public class OrderAnnotatedMethodComparator implements Comparator<Method>, Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Compares the two {@link Method} objects. First by their {@link Order} value (see {@link #getOrder(java.lang.reflect.Method)}
	 * for how that is retrieved); then if 0, by the methods' declaring class' name, finally if still 0, by the methods' signature.
	 *
	 * @param method1 the first {@link Method} object to compare.
	 * @param method2 the second {@link Method} object to compare.
	 * @return the comparison value as described above.
	 */
	@Override
	@SuppressWarnings("PMD.ConfusingTernary")
	public int compare(final Method method1, final Method method2) {
		int result = 0;

		if (method1 != null && method2 != null) {
			// First compare the methods by the @Order annotations on the method if present
			// If they are the same, or both don't have them, then sort by declaring class name
			// If they are both the same, sort by fully qualified method signature (provided by toString())
			// This caters for overloaded methods to ensure they don't compare to 0.

			result = new CompareToBuilder()
					.append(getOrder(method1), getOrder(method2))
					.append(method1.getDeclaringClass().getName(), method2.getDeclaringClass().getName())
					.append(method1.toString(), method2.toString())
					.toComparison();
		} else if (method1 != null) {
			result = -1;
		} else if (method2 != null) {
			result = 1;
		}

		return result;
	}

	/**
	 * Returns the value from the {@link Order} annotation if the {@link Method} is annotated with one,
	 * or {@link Ordered#LOWEST_PRECEDENCE} if not.
	 *
	 * @param method the {@link Method} to inspect.
	 * @return the value from the {@link Order} annotation if the {@link Method} is annotated with one,
	 * or {@link Ordered#LOWEST_PRECEDENCE} if not.
	 */
	protected Integer getOrder(final Method method) {
		final Order order = method.getAnnotation(Order.class);
		if (order != null) {
			return order.value();
		}
		return Ordered.LOWEST_PRECEDENCE;
	}
}