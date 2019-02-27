/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.order.impl;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.apache.openjpa.persistence.DataCache;
import org.apache.openjpa.persistence.jdbc.VersionStrategy;

import com.elasticpath.domain.impl.AbstractLegacyPersistenceImpl;
import com.elasticpath.domain.order.OrderTaxValue;
import com.elasticpath.money.Money;

/**
 * Represents order tax information.
 */
@Entity
@VersionStrategy("state-comparison")
@Table(name = OrderTaxValueImpl.TABLE_NAME)
@DataCache(enabled = false)
public class OrderTaxValueImpl extends AbstractLegacyPersistenceImpl implements OrderTaxValue {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TSHIPMENTTAX";

	private String taxCategoryName;

	private String taxCategoryDisplayName;

	private BigDecimal taxValue;

	private long uidPk;

	/**
	 * Get the taxCategory name.
	 *
	 * @return the taxCategory name.
	 */
	@Override
	@Basic
	@Column (name = "TAX_CATEGORY_NAME")
	public String getTaxCategoryName() {
		return this.taxCategoryName;
	}

	/**
	 * Set the taxCategory name.
	 *
	 * @param taxCategoryName the taxCategory name.
	 */
	@Override
	public void setTaxCategoryName(final String taxCategoryName) {
		this.taxCategoryName = taxCategoryName;
	}

	/**
	 * Get the taxCategory display name in order locale.
	 *
	 * @return the taxCategory display name in order locale.
	 */
	@Override
	@Basic
	@Column (name = "TAX_CATEGORY_DISPLAY_NAME")
	public String getTaxCategoryDisplayName() {
		return this.taxCategoryDisplayName;
	}

	/**
	 * Set the taxCategory display name in order locale.
	 *
	 * @param taxCategoryDisplayName - taxCategory display name in order locale.
	 */
	@Override
	public void setTaxCategoryDisplayName(final String taxCategoryDisplayName) {
		this.taxCategoryDisplayName = taxCategoryDisplayName;
	}

	/**
	 * Get the tax value.
	 *
	 * @return the tax value.
	 */
	@Override
	@Basic
	@Column (name = "VALUE", precision = DECIMAL_PRECISION, scale = DECIMAL_SCALE)
	public BigDecimal getTaxValue() {
		return this.taxValue;
	}

	/**
	 * Set the tax value.
	 *
	 * @param taxValue the tax value.
	 */
	@Override
	public void setTaxValue(final BigDecimal taxValue) {
		this.taxValue = taxValue;
	}

	/**
	 * Return the taxValue as Money object, given the order locale.
	 *
	 * @param orderCurrency - the currency the order is placed in.
	 * @return the taxValue as Money object.
	 */
	@Override
	public Money getTaxValueMoney(final Currency orderCurrency) {
		return Money.valueOf(getTaxValue(), orderCurrency);
	}

	/**
	 * Had to override the equals to make sure that persistence layer wouldn't insert and delete
	 * these value objects upon initialization.
	 * @param compareAgainst object to compare.
	 * @return boolean are the objects equals.
	 */
	@Override
	public boolean equals(final Object compareAgainst) {
		if (!(compareAgainst instanceof OrderTaxValueImpl)) {
			return false;
		}
		OrderTaxValueImpl object = (OrderTaxValueImpl) compareAgainst;
		return Objects.equals(this.taxCategoryName, object.taxCategoryName)
				&& Objects.equals(this.taxValue, object.taxValue);
	}

	/**
	 * Had to override the equals to make sure that persistence layer wouldn't insert and delete
	 * these value objects upon initialization.
	 * @return hash code.
	 */
	@Override
	public int hashCode() {
		return Objects.hash(taxCategoryName, taxValue);
	}

	/**
	 * Gets the unique identifier for this domain model object.
	 *
	 * @return the unique identifier.
	 */
	@Override
	@Id
	@Column(name = "UIDPK")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
	@TableGenerator(name = TABLE_NAME, table = "JPA_GENERATED_KEYS", pkColumnName = "ID",
			valueColumnName = "LAST_VALUE", pkColumnValue = TABLE_NAME, allocationSize = HIGH_CONCURRENCY_ALLOCATION_SIZE)
	public long getUidPk() {
		return this.uidPk;
	}

	/**
	 * Sets the unique identifier for this domain model object.
	 *
	 * @param uidPk the new unique identifier.
	 */
	@Override
	public void setUidPk(final long uidPk) {
		this.uidPk = uidPk;
	}
}