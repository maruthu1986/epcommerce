/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */

package com.elasticpath.service.shipping.predicates;

import java.net.ConnectException;
import java.util.function.Predicate;

/**
 * A {@link java.util.function.Predicate} implementation which returns whether a given {@link Throwable} thrown when attempting to
 * get shipping options should have its stack trace logged, or whether just the message is sufficient.
 * <p>
 * For certain events such as {@link ConnectException} the stack trace does not provide any extra information since it is already known
 * that the endpoint is not reachable. Therefore by omitting the stack trace in the logs, we avoid spamming them.
 */
public class DefaultShippingOptionResultExceptionLogPredicateImpl implements Predicate<Throwable> {

	@Override
	public boolean test(final Throwable throwable) {
		return !(throwable instanceof ConnectException);
	}
}
