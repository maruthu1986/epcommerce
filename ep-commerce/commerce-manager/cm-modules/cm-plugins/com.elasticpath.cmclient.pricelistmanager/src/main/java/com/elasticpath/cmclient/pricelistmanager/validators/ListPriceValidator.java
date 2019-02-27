/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.pricelistmanager.validators;

import java.math.BigDecimal;
import java.util.Comparator;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.widgets.Text;

import com.elasticpath.cmclient.core.binding.EpValueBinding;
import com.elasticpath.cmclient.core.validation.CompoundValidator;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory.BigDecimalValidatorForComparator;
import com.elasticpath.cmclient.core.validation.RequiredValidator;
import com.elasticpath.cmclient.core.validation.NonNegativeBigDecimalValidator;
import com.elasticpath.cmclient.pricelistmanager.PriceListManagerMessages;
import com.elasticpath.commons.util.Callback;

/**
 * Validates that list prices are required, must be a non-negative big decimal, and must be greater than the sale price.
 */
public class ListPriceValidator extends CompoundValidator {

	private final Callback<BigDecimal> salePriceCallback;
	private SalePriceValidator salePriceValidator;
	private EpValueBinding salePriceBinding;
	private IStatus status;
	private BigDecimalValidatorForComparator relativePriceValidator;

	/**
	 * Create a new ListPriceValidator.
	 * @param salePriceField the Test field from which to retrieve the sale price
	 */
	public ListPriceValidator(final Text salePriceField) {
		this(new Callback<BigDecimal>() {
			public BigDecimal callback() {
				final String text = salePriceField.getText();
				try {
					return new BigDecimal(text);
				} catch (NumberFormatException e) {
					return null;
				}
			}
		});
	}

	/**
	 * Create a new ListPriceValidator.
	 * @param salePriceCallback callback used to retrieve the sale price to validate against
	 */
	public ListPriceValidator(final Callback<BigDecimal> salePriceCallback) {
		super(new RequiredValidator(0, null, PriceListManagerMessages.get().validator_baseAmount_listPriceRequired),
				new NonNegativeBigDecimalValidator(PriceListManagerMessages.get().validator_baseAmount_listPriceNonNegative,
						PriceListManagerMessages.get().validator_baseAmount_listPriceBigDecimal));
		this.salePriceCallback = salePriceCallback;
		relativePriceValidator = new BigDecimalValidatorForComparator(new ListPriceComparator(),
				PriceListManagerMessages.get().validator_baseAmount_salePriceIsMoreThenListPrice);
	}

	/**
	 * Initialize validator for cross-field validation.
	 * @param salePriceValidator validator for sale price
	 * @param salePriceBinding binding for sale price
	 */
	public void init(final SalePriceValidator salePriceValidator, final EpValueBinding salePriceBinding) {
		this.salePriceValidator = salePriceValidator;
		this.salePriceBinding = salePriceBinding;
	}

	@Override
	public IStatus validate(final Object value) {
		status = super.validate(value);
		if (status.isOK() && salePriceValidator != null && salePriceValidator.getStatus().isOK()) {
			status = relativePriceValidator.validate(value);
		}
		if (status.isOK() && salePriceValidator != null && !salePriceValidator.getStatus().isOK()) {
			salePriceBinding.getBinding().updateTargetToModel();
		}
		return status;
	}

	/**
	 * Return the last validation status.
	 * @return last validation status.
	 */
	public IStatus getStatus() {
		return status;
	}

	/**
	 * Comparator for comparing list price to sale price.
	 */
	private class ListPriceComparator implements Comparator<BigDecimal> {

		public int compare(final BigDecimal first, final BigDecimal second) {
			if (first == null) {
				return 1;
			}
			BigDecimal value = salePriceCallback.callback();
			if (value == null) {
				value = BigDecimal.ZERO;
			}
			return first.compareTo(value);
		}

	}

}
