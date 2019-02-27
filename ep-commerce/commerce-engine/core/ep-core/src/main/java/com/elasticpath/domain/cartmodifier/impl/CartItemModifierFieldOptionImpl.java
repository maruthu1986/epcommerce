/*
 * Copyright (c) Elastic Path Software Inc., 2016
 */

package com.elasticpath.domain.cartmodifier.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.ElementDependent;
import org.apache.openjpa.persistence.jdbc.ElementForeignKey;
import org.apache.openjpa.persistence.jdbc.ElementJoinColumn;

import com.elasticpath.domain.cartmodifier.CartItemModifierFieldOption;
import com.elasticpath.domain.cartmodifier.CartItemModifierFieldOptionLdf;
import com.elasticpath.persistence.api.AbstractPersistableImpl;

/**
 * Implements CartItemModifierField.
 */
@Entity
@Table(name = CartItemModifierFieldOptionImpl.TABLE_NAME)
public class CartItemModifierFieldOptionImpl extends AbstractPersistableImpl implements CartItemModifierFieldOption {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TCARTITEMMODIFIERFIELDOPTION";

	private long uidPk;

	private String value;

	private int ordering;

	private Set<CartItemModifierFieldOptionLdf> cartItemModifierFieldOptionsLdf = new HashSet<>();

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
	@Column(name = "OPTION_VALUE")
	public String getValue() {
		return value;
	}

	@Override
	public void setValue(final String value) {
		this.value = value;
	}

	@Override
	@Basic
	@Column(name = "ORDERING")
	public int getOrdering() {
		return ordering;
	}

	@Override
	public void setOrdering(final int ordering) {
		this.ordering = ordering;
	}

	@OneToMany(targetEntity = CartItemModifierFieldOptionLdfImpl.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@ElementJoinColumn(name = "CART_ITEM_MOD_FIELD_OPTION_UID", nullable = false)
	@ElementForeignKey(name = "TCARTITEMMODFIELDOPTLDF_IBFK_1")
	@ElementDependent
	protected Set<CartItemModifierFieldOptionLdf> getCartItemModifierFieldOptionsLdfInternal() {
		return cartItemModifierFieldOptionsLdf;
	}

	protected void setCartItemModifierFieldOptionsLdfInternal(final Set<CartItemModifierFieldOptionLdf> cartItemModifierFieldOptionsLdf) {
		this.cartItemModifierFieldOptionsLdf = cartItemModifierFieldOptionsLdf;
	}

	/**
	 * See interface javadoc.
	 *
	 * @return a CartItemModifierFieldOptionLdf set
	 */
	@Override
	public Set<CartItemModifierFieldOptionLdf> getCartItemModifierFieldOptionsLdf() {
		return Collections.unmodifiableSet(getCartItemModifierFieldOptionsLdfInternal());
	}

	@Override
	public void addCartItemModifierFieldOptionLdf(final CartItemModifierFieldOptionLdf cartItemModifierFieldOptionLdf) {
		if (StringUtils.isEmpty(cartItemModifierFieldOptionLdf.getDisplayName())) {
			throw new IllegalArgumentException("Cannot have cartItemModifierFieldOptionLdf with empty display name");
		}
		if (StringUtils.isEmpty(cartItemModifierFieldOptionLdf.getLocale())) {
			throw new IllegalArgumentException("Cannot have cartItemModifierFieldOptionLdf with empty locale");
		}

		for (CartItemModifierFieldOptionLdf existentCartItemModifierFieldOptionLdf : this.getCartItemModifierFieldOptionsLdfInternal()) {
			if (existentCartItemModifierFieldOptionLdf.getLocale().equals(cartItemModifierFieldOptionLdf.getLocale())) {
				throw new IllegalArgumentException("Cannot have two cartItemModifierFieldOptionLdf with the same locale");
			}
		}

		this.getCartItemModifierFieldOptionsLdfInternal().add(cartItemModifierFieldOptionLdf);
	}

	@Override
	public void removeCartItemModifierFieldOptionLdf(final CartItemModifierFieldOptionLdf cartItemModifierFieldOptionLdf) {
		boolean remove = this.getCartItemModifierFieldOptionsLdfInternal().remove(cartItemModifierFieldOptionLdf);
		if (!remove) {
			throw new IllegalStateException("Cannot remove cartItemModifierFieldOptionLdf");
		}
	}

	@Override
	public CartItemModifierFieldOptionLdf getCartItemModifierFieldOptionsLdfByLocale(final String locale) {
		for (CartItemModifierFieldOptionLdf existentCartItemModifierFieldOptionLdf : this.getCartItemModifierFieldOptionsLdfInternal()) {
			if (existentCartItemModifierFieldOptionLdf.getLocale().equals(locale)) {
				return existentCartItemModifierFieldOptionLdf;
			}
		}

		return null;
	}

	/*
	 * Sorting on ordering is natural sorting for CartItemModifierFieldOption.
     */
	@Override
	public int compareTo(final CartItemModifierFieldOption obj) {
		return this.ordering - obj.getOrdering();
	}
}
