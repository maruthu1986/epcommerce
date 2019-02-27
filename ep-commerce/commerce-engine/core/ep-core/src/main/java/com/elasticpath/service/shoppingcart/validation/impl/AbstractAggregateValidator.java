/**
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shoppingcart.validation.impl;

import java.util.Collection;
import java.util.stream.Collectors;

import com.elasticpath.base.common.dto.StructuredErrorMessage;
import com.elasticpath.service.shoppingcart.validation.Validator;

/**
 * Aggregates several validators into one.
 * @param <T> the type the inner validators validate.
 * @param <S> the type the aggregate should validate.
 */
public abstract class AbstractAggregateValidator<T, S extends T> implements Validator<S> {

	private Collection<Validator<T>> validators;

	@Override
	public Collection<StructuredErrorMessage> validate(final S context) {
		return validators.stream()
				.map(strategy -> strategy.validate(context))
				.flatMap(Collection::stream)
				.collect(Collectors.toList());
	}

	protected Collection<Validator<T>> getValidators() {
		return validators;
	}

	public void setValidators(final Collection<Validator<T>> validators) {
		this.validators = validators;
	}
}
