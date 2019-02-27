/*
 * Copyright (c) Elastic Path Software Inc., 2016
 */

package com.elasticpath.domain.cartmodifier.impl;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import com.elasticpath.domain.cartmodifier.CartItemModifierGroupLdf;
import com.elasticpath.persistence.api.AbstractPersistableImpl;

/**
 * Implements CartItemModifierField.
 */
@Entity
@Table(name = CartItemModifierGroupLdfImpl.TABLE_NAME)
public class CartItemModifierGroupLdfImpl extends AbstractPersistableImpl implements CartItemModifierGroupLdf {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TCARTITEMMODIFIERGROUPLDF";

	private long uidPk;

	private String locale;

	private String displayName;

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
	@Column(name = "LOCALE")
	public String getLocale() {
		return locale;
	}

	@Override
	public void setLocale(final String locale) {
		this.locale = locale;
	}

	@Override
	@Basic
	@Column(name = "DISPLAY_NAME")
	public String getDisplayName() {
		return displayName;
	}

	@Override
	public void setDisplayName(final String displayName) {
		this.displayName = displayName;
	}
}
