/*
 * Copyright (c) Elastic Path Software Inc., 2011.
 */
package com.elasticpath.domain.cartorder.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import org.apache.openjpa.persistence.DataCache;
import org.apache.openjpa.persistence.Dependent;
import org.apache.openjpa.persistence.PersistentCollection;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.customer.impl.AbstractPaymentMethodImpl;
import com.elasticpath.persistence.api.AbstractEntityImpl;
import com.elasticpath.plugin.payment.dto.PaymentMethod;

/**
 * Implementation of CartOrder, CartOrder should not be used in versions of EP prior to 6.4.
 */
@Entity
@Table(name = CartOrderImpl.TABLE_NAME)
@DataCache(enabled = false)
public class CartOrderImpl extends AbstractEntityImpl implements CartOrder {
	
	private static final long serialVersionUID = 641L;
	
	/** The name of the table & generator to use for persistence. */
	public static final String TABLE_NAME = "TCARTORDER";
	
	private String billingAddressGuid;
	
	private String shippingAddressGuid;
	
	private String shoppingCartGuid;

	private String shippingOptionCode;
	
	private long uidPk;

	private String guid;

	private PaymentMethod paymentMethod;

	private Set<String> couponCodes = new HashSet<>();

	@Override
	@Basic
	@Column(name = "BILLING_GUID", nullable = true)
	public String getBillingAddressGuid() {
		return this.billingAddressGuid;
	}

	@Override
	public void setBillingAddressGuid(final String guid) {
		this.billingAddressGuid = guid;
	}
	
	@Override
	@Id
	@Column(name = "UIDPK")
	@GeneratedValue(
			strategy = GenerationType.TABLE,
			generator = TABLE_NAME)
	@TableGenerator(
			name = TABLE_NAME,
			table = "JPA_GENERATED_KEYS",
			pkColumnName = "ID",
			valueColumnName = "LAST_VALUE",
			pkColumnValue = TABLE_NAME,
			allocationSize = HIGH_CONCURRENCY_ALLOCATION_SIZE)
	public long getUidPk() {
		return uidPk;
	}

	@Override
	public void setUidPk(final long uidPk) {
		this.uidPk = uidPk;
	}
	
	/**
	 * @return The ShoppingCart GUID.
	 */
	@Basic
	@Column(name = "SHOPPINGCART_GUID", nullable = false, unique = true)
	protected String getShoppingCartGuidInternal() {
		return this.shoppingCartGuid;
	}
	
	/**
	 * @param guid The ShoppingCart GUID.
	 */
	protected void setShoppingCartGuidInternal(final String guid) {
		this.shoppingCartGuid = guid;
	}
	
	@Override
	@Transient
	public String getShoppingCartGuid() {
		return getShoppingCartGuidInternal();
	}
	
	@Override
	@Transient
	public void setShoppingCartGuid(final String guid) {
		if (guid == null) {
			throw new IllegalArgumentException("Parameter [guid] cannot be null.");
		}
		setShoppingCartGuidInternal(guid);
	}

	@ManyToOne(targetEntity = AbstractPaymentMethodImpl.class, cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
	@JoinColumn(name = "PAYMENT_METHOD_UID")
	@Dependent
	protected PaymentMethod getPaymentMethodInternal() {
		return paymentMethod;
	}

	protected void setPaymentMethodInternal(final PaymentMethod paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	@Override
	public PaymentMethod getPaymentMethod() {
		return getPaymentMethodInternal();
	}
	
	@Override
	public void clearPaymentMethod() {
		setPaymentMethodInternal(null);
	}

	@Override
	public void usePaymentMethod(final PaymentMethod paymentMethod) {
		if (paymentMethod == null) {
			throw new IllegalArgumentException("payment method must not be null");
		}

		if (!(paymentMethod instanceof AbstractPaymentMethodImpl)) {
			throw new IllegalArgumentException("payment method must be a subclass of " + AbstractPaymentMethodImpl.class.getSimpleName());
		}

		AbstractPaymentMethodImpl<?> abstractPaymentMethod = (AbstractPaymentMethodImpl<?>) paymentMethod;
		setPaymentMethodInternal(abstractPaymentMethod.copy());
	}

	@Override
	@Deprecated
	public void setPaymentMethod(final PaymentMethod paymentMethod) {
		usePaymentMethod(paymentMethod);
	}

	@Override
	@Basic
	@Column(name = "GUID", nullable = false, unique = true)
	public String getGuid() {
		return guid;
	}

	@Override
	public void setGuid(final String guid) {
		this.guid = guid;
	}
	
	@Override
	@Basic
	@Column(name = "SHIPPING_ADDRESS_GUID", nullable = true)
	public String getShippingAddressGuid() {
		return shippingAddressGuid;
	}
	
	@Override
	public void setShippingAddressGuid(final String shippingAddressGuid) {
		this.shippingAddressGuid = shippingAddressGuid;
	}
	
	@Override
	@Basic
	@Column(name = "SHIPPING_OPTION_CODE", nullable = true)
	public String getShippingOptionCode() {
		return shippingOptionCode;
	}
	
	@Override
	public void setShippingOptionCode(final String shippingOptionCode) {
		this.shippingOptionCode = shippingOptionCode;
	}

	@Override
	@Transient
	public int hashCode() {
		return Objects.hashCode(getGuid());
	}

	@Override
	@Transient
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof CartOrderImpl) {
			CartOrderImpl other = (CartOrderImpl) obj;
			return Objects.equals(other.getGuid(), this.getGuid());
		}
		return false;
	}

	@Override
	public boolean addCoupon(final String couponCode) {
		return getInternalCouponCodes().add(couponCode);
	}

	@Override
	public boolean removeCoupon(final String couponCode) {
		return  getInternalCouponCodes().remove(couponCode);
	}

	@Override
	public boolean addCoupons(final Collection<String> couponCodes) {
		return getInternalCouponCodes().addAll(couponCodes);
	}

	@Override
	public boolean removeCoupons(final Collection<String> couponCodes) {
		return getInternalCouponCodes().removeAll(couponCodes);
	}

	/**
	 * Getter for coupon codes. Returns an unmodifiable Set.
	 *
	 * @return an unmodifiable list of coupon codes.
	 */
	@Transient
	@Override
	public Set<String> getCouponCodes() {
		return Collections.unmodifiableSet(getInternalCouponCodes());
	}

	/**
	 * Internal getter for couponCodes.
	 *
	 * @return the coupon codes.
	 */
	@PersistentCollection(elementCascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	@JoinTable(name = "TCARTORDERCOUPON", joinColumns = { @JoinColumn(name = "CARTORDER_UID") },
		inverseJoinColumns = @JoinColumn(name = "COUPON_GUID", nullable = false))
	protected Set<String> getInternalCouponCodes() {
		return couponCodes;
	}
	
	protected void setInternalCouponCodes(final Set<String> couponCodes) {
		this.couponCodes = couponCodes;
	}
}
