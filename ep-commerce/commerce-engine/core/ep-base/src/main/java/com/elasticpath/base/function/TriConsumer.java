/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.base.function;

import static java.util.Objects.requireNonNull;

/**
 * Similar to the standard {@link java.util.function.Consumer} and {@link java.util.function.BiConsumer} interfaces, this
 * provides a Functional interface for consuming 3 parameters.
 *
 * This can be useful for example when replacing a {@link java.util.function.BiFunction} with a {@link TriConsumer} where
 * the return value is no longer returned but updated in place and so would be the third parameter of this consumer.
 *
 * @param <T> the type of the first argument to the operation
 * @param <U> the type of the second argument to the operation
 * @param <V> the type of the third argument to the operation
 *
 * @see java.util.function.Consumer
 * @see java.util.function.BiConsumer
 */
@FunctionalInterface
public interface TriConsumer<T, U, V> {
	/**
	 * Performs this operation on the given arguments.
	 *
	 * @param firstArgument the first input argument
	 * @param secondArgument the second input argument
	 * @param thirdArgument the third input argument
	 */
	void accept(T firstArgument, U secondArgument, V thirdArgument);

	/**
	 * Returns a composed {@code TriConsumer} that performs, in sequence, this operation followed by the {@code after} operation.
	 *
	 * @param after the operation to perform after this operation
	 * @return a composed {@code TriConsumer} that performs in sequence this operation followed by the {@code after} operation
	 * @throws NullPointerException if {@code after} is {@code null}.
	 */
	default TriConsumer<T, U, V> andThen(TriConsumer<? super T, ? super U, ? super V> after) {
		requireNonNull(after, "The follow-on consumer was null");

		return (first, second, third) -> {
			accept(first, second, third);
			after.accept(first, second, third);
		};
	}
}
