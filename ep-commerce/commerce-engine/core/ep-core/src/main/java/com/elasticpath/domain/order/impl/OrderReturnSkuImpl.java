/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.order.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Map;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import org.apache.openjpa.persistence.DataCache;
import org.apache.openjpa.persistence.jdbc.ForeignKey;

import com.elasticpath.domain.impl.AbstractLegacyEntityImpl;
import com.elasticpath.domain.order.OrderReturnSku;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.money.Money;

/**
 * The default implementation of <code>OrderReturnSku</code>.
 */
@Entity
@Table(name = OrderReturnSkuImpl.TABLE_NAME)
@DataCache(enabled = false)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class OrderReturnSkuImpl extends AbstractLegacyEntityImpl implements OrderReturnSku {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TORDERRETURNSKU";
	
	private static final int CALCULATION_SCALE = 10;

	private OrderSku orderSku;

	private int quantity;

	private int receivedQuantity;

	private BigDecimal returnAmount;

	private String returnReason;

	private String receivedState;

	private long uidPk;

	private BigDecimal tax;

	private String guid;

	@Override
	@ManyToOne(targetEntity = OrderSkuImpl.class, fetch = FetchType.EAGER, cascade = { CascadeType.MERGE, CascadeType.REFRESH },
			optional = false)
	@JoinColumn(name = "ORDER_SKU_UID", nullable = false)
	@ForeignKey
	public OrderSku getOrderSku() {
		return this.orderSku;
	}

	@Override
	public void setOrderSku(final OrderSku orderSku) {
		this.orderSku = orderSku;
	}

	@Override
	@Basic
	@Column(name = "QUANTITY")
	public int getQuantity() {
		return this.quantity;
	}

	@Override
	public void setQuantity(final int quantity) {
		this.quantity = quantity;
	}

	@Override
	@Transient
	public BigDecimal getReturnAmount() {
		if (getReturnAmountInternal() == null) {
			setReturnAmountInternal(getAmountMoney().getAmount());
		}
		return getReturnAmountInternal();
	}
	
	@Basic
	@Column(name = "RETURN_AMOUNT", scale = DECIMAL_PRECISION, precision = DECIMAL_SCALE)
	protected BigDecimal getReturnAmountInternal() {
		return this.returnAmount;
	}

	@Override
	public Money getReturnAmountMoney(final Currency currency) {
		if (getReturnAmount() == null) {
			return null;
		}

		return Money.valueOf(getReturnAmount(), currency);
	}

	@Override
	public void setReturnAmount(final BigDecimal returnAmount) {
		setReturnAmountInternal(returnAmount);
	}
	
	protected void setReturnAmountInternal(final BigDecimal returnAmount) {
		this.returnAmount = returnAmount;
	}

	@Override
	@Id
	@Column(name = "UIDPK")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
	@TableGenerator(name = TABLE_NAME, table = "JPA_GENERATED_KEYS", pkColumnName = "ID", valueColumnName = "LAST_VALUE", pkColumnValue = TABLE_NAME)
	public long getUidPk() {
		return this.uidPk;
	}

	@Override
	public void setUidPk(final long uidPk) {
		this.uidPk = uidPk;
	}

	@Override
	@Basic
	@Column(name = "RETURN_REASON")
	public String getReturnReason() {
		return returnReason;
	}

	@Override
	public void setReturnReason(final String returnReason) {
		this.returnReason = returnReason;
	}

	@Override
	@Basic
	@Column(name = "RECEIVED_QUANTITY")
	public int getReceivedQuantity() {
		return receivedQuantity;
	}

	@Override
	public void setReceivedQuantity(final int receiveQuantity) {
		this.receivedQuantity = receiveQuantity;
	}

	@Override
	@Basic
	@Column(name = "RECEIVED_STATE")
	public String getReceivedState() {
		return receivedState;
	}

	@Override
	public void setReceivedState(final String receivedState) {
		this.receivedState = receivedState;
	}

	/**
	 * @return true if the quantity received is the same as the quantity expected.
	 */
	@Override
	@Transient
	public boolean isFullyReceived() {
		return getQuantity() == getReceivedQuantity();
	}

	@Override
	public void initialize() {
		super.initialize();
		setQuantity(0);
	}

	@Override
	@Transient
	public Money getAmountMoney() {
		
		final OrderSku orderSku = getOrderSku();
		
		BigDecimal itemUnitDiscount = orderSku.getDiscountBigDecimal().divide(
				BigDecimal.valueOf(orderSku.getQuantity()), CALCULATION_SCALE, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP);
		BigDecimal itemCost = orderSku.getUnitPrice().subtract(itemUnitDiscount);
		BigDecimal itemTotal = itemCost.multiply(new BigDecimal(getQuantity()));
		return Money.valueOf(itemTotal, orderSku.getCurrency());
	}

	@Override
	@Transient
	public String getSkuGuid() {
		return getOrderSku().getSkuGuid();
	}

	/**
	 * Get all Fields.
	 *
	 * @return Map<String, String> fields.
	 */
	@Transient
	public Map<String, String> getFields() {
		//FIXME: Implement when Order Returns are updated to handle item data.
		return null;
	}

	/**
	 * Get the tax amount.
	 *
	 * @return the tax amount
	 */
	@Override
	@Transient
	public BigDecimal getTax() {
		if (tax == null) {
			tax = BigDecimal.ZERO;
		}
		return this.tax;
	}

	/**
	 * Set the tax amount.
	 *
	 * @param tax the tax amount
	 */
	@Override
	public void setTax(final BigDecimal tax) {
		this.tax = tax;
	}

	/**
	 * Return the guid.
	 *
	 * @return the guid.
	 */
	@Override
	@Basic
	@Column(name = "GUID")
	public String getGuid() {
		return guid;
	}

	/**
	 * Set the guid.
	 *
	 * @param guid the guid to set.
	 */
	@Override
	public void setGuid(final String guid) {
		this.guid = guid;
	}


	@Override
	public boolean equals(final Object other) {
		if (!(other instanceof OrderReturnSkuImpl)) {
			return false;
		}
		return super.equals(other);
	}

	@Override
	@SuppressWarnings("PMD.UselessOverridingMethod")
	public int hashCode() {
		return super.hashCode();
	}
}
