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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.ElementDependent;
import org.apache.openjpa.persistence.jdbc.ElementForeignKey;
import org.apache.openjpa.persistence.jdbc.ElementJoinColumn;

import com.elasticpath.domain.cartmodifier.CartItemModifierField;
import com.elasticpath.domain.cartmodifier.CartItemModifierGroup;
import com.elasticpath.domain.cartmodifier.CartItemModifierGroupLdf;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.impl.CatalogImpl;
import com.elasticpath.domain.impl.AbstractLegacyEntityImpl;

/**
 * Implements CartItemModifierField.
 */
@Entity
@Table(name = CartItemModifierGroupImpl.TABLE_NAME)
public class CartItemModifierGroupImpl extends AbstractLegacyEntityImpl implements CartItemModifierGroup {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TCARTITEMMODIFIERGROUP";

	private long uidPk;

	private String code;

	private Catalog catalog;

	private Set<CartItemModifierGroupLdf> cartItemModifierGroupsLdf = new HashSet<>();

	private Set<CartItemModifierField> cartItemModifierFields = new HashSet<>();

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
	@Transient
	public String getGuid() {
		return getCode();
	}

	@Override
	public void setGuid(final String guid) {
		setCode(guid);
	}

	@Override
	@Basic
	@Column(name = "CODE")
	public String getCode() {
		return code;
	}

	@Override
	public void setCode(final String code) {
		this.code = code;
	}

	@OneToMany(targetEntity = CartItemModifierGroupLdfImpl.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@ElementJoinColumn(name = "CART_ITEM_MODIFIER_GROUP_UID", nullable = false)
	@ElementForeignKey(name = "TCARTITEMMODIFIERGROUP_IBFK_1")
	@ElementDependent
	protected Set<CartItemModifierGroupLdf> getCartItemModifierGroupsLdfInternal() {
		return cartItemModifierGroupsLdf;
	}

	protected void setCartItemModifierGroupsLdfInternal(final Set<CartItemModifierGroupLdf> cartItemModifierGroupsLdf) {
		this.cartItemModifierGroupsLdf = cartItemModifierGroupsLdf;
	}

	@OneToMany(targetEntity = CartItemModifierFieldImpl.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@ElementJoinColumn(name = "CART_ITEM_MODIFIER_GROUP_UID", nullable = false)
	@ElementForeignKey(name = "TCARTITEMMODGROUPLDF_IBFK_1")
	@ElementDependent
	protected Set<CartItemModifierField> getCartItemModifierFieldsInternal() {
		return cartItemModifierFields;
	}

	protected void setCartItemModifierFieldsInternal(final Set<CartItemModifierField> cartItemModifierFields) {
		this.cartItemModifierFields = cartItemModifierFields;
	}

	@ManyToOne(optional = true, targetEntity = CatalogImpl.class)
	@JoinColumn(name = "CATALOG_UID", nullable = false)
	@Override
	public Catalog getCatalog() {
		return catalog;
	}

	@Override
	public void setCatalog(final Catalog catalog) {
		this.catalog = catalog;
	}

	/**
	 * See interface javadoc.
	 *
	 * @return a CartItemModifierGroupLdf set
	 */
	@Override
	public Set<CartItemModifierGroupLdf> getCartItemModifierGroupLdf() {
		return Collections.unmodifiableSet(getCartItemModifierGroupsLdfInternal());
	}

	/**
	 * See interface javadoc.
	 *
	 * @return a CartItemModifierField set
	 */
	@Override
	public Set<CartItemModifierField> getCartItemModifierFields() {
		return Collections.unmodifiableSet(getCartItemModifierFieldsInternal());
	}

	@Override
	public void addCartItemModifierField(final CartItemModifierField newCartItemModifierField) {
		if (StringUtils.isEmpty(newCartItemModifierField.getCode())) {
			throw new IllegalArgumentException("Cannot add a newCartItemModifierField with empty code");
		}

		CartItemModifierField cartItemModifierField1 = getCartItemModifierFieldByCode(newCartItemModifierField.getCode());
		if (cartItemModifierField1 != null) {
			return;
		}

		for (CartItemModifierField existentCartItemModifierField : this.getCartItemModifierFieldsInternal()) {
			if (existentCartItemModifierField.getOrdering() == newCartItemModifierField.getOrdering()) {
				throw new IllegalArgumentException(
						"Cannot have two newCartItemModifierField with the same ordering: " + newCartItemModifierField.getOrdering()
								+ ". Existent guid: " + existentCartItemModifierField.getCode() + ", New Guid: "
								+ newCartItemModifierField.getCode());
			}
		}

		this.getCartItemModifierFieldsInternal().add(newCartItemModifierField);
	}

	@Override
	public void removeCartItemModifierField(final CartItemModifierField cartItemModifierField) {
		boolean remove = this.getCartItemModifierFieldsInternal().remove(cartItemModifierField);
		if (!remove) {
			throw new IllegalStateException("Cannot remove CartItemModifierField");
		}
	}

	@Override
	public void addCartItemModifierGroupLdf(final CartItemModifierGroupLdf cartItemModifierGroupLdf) {
		if (StringUtils.isEmpty(cartItemModifierGroupLdf.getDisplayName())) {
			throw new IllegalArgumentException("Cannot have a cartItemModifierGroupLdf with empty display name");
		}
		if (StringUtils.isEmpty(cartItemModifierGroupLdf.getLocale())) {
			throw new IllegalArgumentException("Cannot a cartItemModifierGroupLdf with empty locale");
		}

		for (CartItemModifierGroupLdf existentCartItemModifierGroupLdf : this.getCartItemModifierGroupsLdfInternal()) {
			if (existentCartItemModifierGroupLdf.equals(cartItemModifierGroupLdf)) {
				return;
			}

			if (existentCartItemModifierGroupLdf.getLocale().equals(cartItemModifierGroupLdf.getLocale())) {
				throw new IllegalArgumentException("Cannot have two cartItemModifierGroupLdf with the same locale");
			}
		}

		this.getCartItemModifierGroupsLdfInternal().add(cartItemModifierGroupLdf);
	}

	@Override
	public void removeCartItemModifierGroupLdf(final CartItemModifierGroupLdf cartItemModifierGroupLdf) {
		boolean remove = this.getCartItemModifierGroupsLdfInternal().remove(cartItemModifierGroupLdf);
		if (!remove) {
			throw new IllegalStateException("Cannot remove CartItemModifierGroupLdf");
		}
	}

	@Override
	public void removeAllCartItemModifierGroupLdf() {
		this.getCartItemModifierGroupsLdfInternal().clear();
	}

	@Override
	public void removeAllCartItemModifierFields() {
		this.getCartItemModifierFieldsInternal().clear();
	}

	@Override
	public CartItemModifierGroupLdf getCartItemModifierGroupLdfByLocale(final String language) {
		for (CartItemModifierGroupLdf cartItemModifierGroupLdf : this.getCartItemModifierGroupsLdfInternal()) {
			if (cartItemModifierGroupLdf.getLocale().equals(language)) {
				return cartItemModifierGroupLdf;
			}
		}
		return null;
	}

	@Override
	public CartItemModifierField getCartItemModifierFieldByCode(final String code) {
		for (CartItemModifierField existentCartItemModifierField : this.getCartItemModifierFieldsInternal()) {
			if (existentCartItemModifierField.getCode().equals(code)) {
				return existentCartItemModifierField;
			}
		}
		return null;
	}
}
