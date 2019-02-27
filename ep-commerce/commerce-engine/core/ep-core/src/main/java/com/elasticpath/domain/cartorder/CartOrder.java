/*
 * Copyright (c) Elastic Path Software Inc., 2011.
 */
package com.elasticpath.domain.cartorder;

import java.util.Collection;
import java.util.Set;

import com.elasticpath.persistence.api.Entity;
import com.elasticpath.plugin.payment.dto.PaymentMethod;

/**
 * A CartOrder contains information that is required by the workflow that a customer goes through on the way to creating an order.
 * CartOrder should not be used in versions of EP prior to 6.4.
 */
public interface CartOrder extends Entity {
	
	/**
	 * @return The billing address GUID.
	 */
	String getBillingAddressGuid();
	
	/**
	 * @param guid The billing address GUID.
	 */
	void setBillingAddressGuid(String guid);
	
	/**
	 * @return The shopping cart GUID.
	 */
	String getShoppingCartGuid();
	
	/**
	 * @param guid The shopping cart GUID.
	 * @throws IllegalArgumentException If the given guid is null.
	 */
	void setShoppingCartGuid(String guid);
	
	/**
	 * @return The shipping address GUID.
	 */
	String getShippingAddressGuid();

	/**
	 * @param shippingAddressGuid The shipping address GUID.
	 */
	void setShippingAddressGuid(String shippingAddressGuid);

	/**
	 * @return The shipping option code.
	 */
	String getShippingOptionCode();

	/**
	 * @param shippingOptionCode The shipping option code.
	 */
	void setShippingOptionCode(String shippingOptionCode);

	/**
	 * Gets the payment method.
	 * @return the payment method
	 */
	PaymentMethod getPaymentMethod();

	/**
	 * Sets the payment method.
	 * @param paymentMethod the payment method
	 * @deprecated use {@link CartOrder#usePaymentMethod(PaymentMethod) to set the {@link PaymentMethod}
	 * and {@link CartOrder#clearPaymentMethod() to clear it.
	 */
	@Deprecated
	void setPaymentMethod(PaymentMethod paymentMethod);

	/**
	 * Clears the {@link PaymentMethod} on the {@link CartOrder). 
	 */
	void clearPaymentMethod();

	/**
	 * Selects the {@link PaymentMethod} to be used on the {@link CartOrder).
	 * @param paymentMethod the {@link PaymentMethod}
	 */
	void usePaymentMethod(PaymentMethod paymentMethod);

	/**
	 * Remove the coupon with code from cart order.
	 *
	 * @param couponCode coupon code to remove
	 * @return true if removed, false otherwise.
	 */
	boolean removeCoupon(String couponCode);

	/**
	 * Add a coupon to the cart order.
	 *
	 * @param couponCode coupon code to add.
	 * @return true if the coupon was added, false otherwise.
	 */
	boolean addCoupon(String couponCode);

	/**
	 * Remove the coupons from cart order.
	 *
	 * @param couponCodes coupon codes to remove
	 * @return true if codes are removed
	 */
	boolean removeCoupons(Collection<String> couponCodes);

	/**
	 * Add coupons to the cart order.
	 *
	 * @param couponCodes coupon codes to add.
	 * @return true if the coupons were added
	 */
	boolean addCoupons(Collection<String> couponCodes);

	/**
	 * Getter for coupon codes. Returns an unmodifiable Set of coupon codes.
	 * 
	 * @return coupon codes
	 */
	Set<String> getCouponCodes();
}
