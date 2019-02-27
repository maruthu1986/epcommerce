/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.domain.catalog.impl;

import java.util.Objects;

import com.elasticpath.domain.catalog.PriceSchedule;
import com.elasticpath.domain.catalog.PriceScheduleType;
import com.elasticpath.domain.subscriptions.PaymentSchedule;

/**
 * Basic price schedule.
 */
public class PriceScheduleImpl implements PriceSchedule {

	private PriceScheduleType priceScheduleType;
	private PaymentSchedule paymentSchedule;
	
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;
	
	@Override
	public void setType(final PriceScheduleType priceScheduleType) {
		this.priceScheduleType = priceScheduleType;
	}
	
	@Override
	public void setPaymentSchedule(final PaymentSchedule paymentSchedule) {
		this.paymentSchedule = paymentSchedule;
	}
	
	@Override
	public PriceScheduleType getType() {
		return this.priceScheduleType;
	}
	
	@Override
	public PaymentSchedule getPaymentSchedule() {
		return this.paymentSchedule;
	}

	@Override
	public int hashCode() {
		return Objects.hash(paymentSchedule, priceScheduleType);
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof PriceScheduleImpl)) {
			return false;
		}
		
		PriceScheduleImpl other = (PriceScheduleImpl) obj;
		
		return Objects.equals(paymentSchedule, other.paymentSchedule)
			&& Objects.equals(priceScheduleType, other.priceScheduleType);
	}

	@Override
	public int compareTo(final PriceSchedule other) {
		int result = getType().compareTo(other.getType());
		if (result == 0 && getPaymentSchedule() != null) {
			return getPaymentSchedule().compareTo(other.getPaymentSchedule());
		}
		return result;
	}

}
