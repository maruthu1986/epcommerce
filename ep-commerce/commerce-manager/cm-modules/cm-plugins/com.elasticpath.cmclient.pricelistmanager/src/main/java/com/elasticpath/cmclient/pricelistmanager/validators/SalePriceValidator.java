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
import com.elasticpath.cmclient.core.validation.NonNegativeBigDecimalValidator;
import com.elasticpath.cmclient.pricelistmanager.PriceListManagerMessages;
import com.elasticpath.commons.util.Callback;

/**
 * Validates that sale prices must be a non-negative big decimal, and must be less than the sale price.
 */
public class SalePriceValidator extends CompoundValidator {

	private final Callback<BigDecimal> listPriceCallback;
	private ListPriceValidator listPriceValidator;
	private EpValueBinding listPriceBinding;
	private IStatus status;
	private BigDecimalValidatorForComparator relativePriceValidator;


	/**
	 * Create a new SalePriceValidator.
	 * @param listPriceField the Text field from which to retrieve the list price
	 */
	public SalePriceValidator(final Text listPriceField) {
		this(new Callback<BigDecimal>() {
			public BigDecimal callback() {
				final String text = listPriceField.getText();
				try {
					return new BigDecimal(text);
				} catch (NumberFormatException e) {
					return null;
				}
			}
		});
	}

	/**
	 * Create a new SalePriceValidator.
	 * @param listPriceCallback callback used to retrieve the list price to validate against
	 */
	public SalePriceValidator(final Callback<BigDecimal> listPriceCallback) {
		super(new NonNegativeBigDecimalValidator(PriceListManagerMessages.get().validator_baseAmount_salePriceNonNegative,
				PriceListManagerMessages.get().validator_baseAmount_salePriceBigDecimal));
		this.listPriceCallback = listPriceCallback;
		relativePriceValidator = new BigDecimalValidatorForComparator(new ListPriceComparator(),
				PriceListManagerMessages.get().validator_baseAmount_salePriceIsMoreThenListPrice);
	}

	/**
	 * Initialize validator for cross-field validation.
	 * @param listPriceValidator validator for list price
	 * @param listPriceBinding binding for list price
	 */
	public void init(final ListPriceValidator listPriceValidator, final EpValueBinding listPriceBinding) {
		this.listPriceValidator = listPriceValidator;
		this.listPriceBinding = listPriceBinding;
	}

	@Override
	public IStatus validate(final Object value) {
		status = super.validate(value);
		if (status.isOK() && relativePriceValidator != null) {
			status = relativePriceValidator.validate(value);
		}
		if (status.isOK() && listPriceValidator != null && !listPriceValidator.getStatus().isOK()) {
			listPriceBinding.getBinding().updateTargetToModel();
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
			BigDecimal value = listPriceCallback.callback();
			if (value == null) {
				value = BigDecimal.ZERO;
			}
			return value.compareTo(first);
		}

	}

}
