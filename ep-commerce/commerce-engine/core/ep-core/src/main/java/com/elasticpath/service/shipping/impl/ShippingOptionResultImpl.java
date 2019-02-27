/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shipping.impl;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.elasticpath.service.shipping.ShippingOptionResult;
import com.elasticpath.shipping.connectivity.dto.ShippingCalculationResult.ErrorInformation;
import com.elasticpath.shipping.connectivity.dto.ShippingOption;


/**
 * Default implementation of {@link ShippingOptionResult}.
 */
public class ShippingOptionResultImpl implements ShippingOptionResult {
	private List<ShippingOption> availableShippingOptions;

	private ErrorInformation errorInformation;

	@Override
	public List<ShippingOption> getAvailableShippingOptions() {
		return this.availableShippingOptions;
	}

	public void setAvailableShippingOptions(final List<ShippingOption> availableShippingOptions) {
		this.availableShippingOptions = availableShippingOptions;
	}

	@Override
	public Optional<ErrorInformation> getErrorInformation() {
		return Optional.ofNullable(this.errorInformation);
	}

	public void setErrorInformation(final ErrorInformation errorInformation) {
		this.errorInformation = errorInformation;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof ShippingOptionResult)) {
			return false;
		}

		final ShippingOptionResult other = (ShippingOptionResult) obj;

		return Objects.equals(getAvailableShippingOptions(), other.getAvailableShippingOptions())
				&& Objects.equals(getErrorInformation(), other.getErrorInformation());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getAvailableShippingOptions(), getErrorInformation());
	}
}
