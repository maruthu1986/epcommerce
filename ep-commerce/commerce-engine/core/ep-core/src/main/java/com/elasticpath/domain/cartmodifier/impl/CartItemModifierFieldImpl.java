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
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.ElementDependent;
import org.apache.openjpa.persistence.jdbc.ElementForeignKey;
import org.apache.openjpa.persistence.jdbc.ElementJoinColumn;

import com.elasticpath.domain.cartmodifier.CartItemModifierField;
import com.elasticpath.domain.cartmodifier.CartItemModifierFieldLdf;
import com.elasticpath.domain.cartmodifier.CartItemModifierFieldOption;
import com.elasticpath.domain.cartmodifier.CartItemModifierType;
import com.elasticpath.domain.impl.AbstractLegacyEntityImpl;

/**
 * Implements CartItemModifierField.
 */
@Entity
@Table(name = CartItemModifierFieldImpl.TABLE_NAME)
@SuppressWarnings("PMD.GodClass")
public class CartItemModifierFieldImpl extends AbstractLegacyEntityImpl implements CartItemModifierField {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TCARTITEMMODIFIERFIELD";

	private long uidPk;

	private String code;

	private boolean required;

	private int ordering;

	private Integer maxSize;

	private int attributeTypeId;

	private Set<CartItemModifierFieldLdf> cartItemModifierFieldsLdf = new HashSet<>();

	private Set<CartItemModifierFieldOption> cartItemModifierFieldOptions = new HashSet<>();

	@Override
	@Id
	@Column(name = "UIDPK")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
	@TableGenerator(name = TABLE_NAME, table = "JPA_GENERATED_KEYS", pkColumnName = "ID",
			valueColumnName = "LAST_VALUE", pkColumnValue = TABLE_NAME, allocationSize = HIGH_CONCURRENCY_ALLOCATION_SIZE)
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

	@Override
	@Basic
	@Column(name = "REQUIRED")
	public boolean isRequired() {
		return required;
	}

	@Override
	public void setRequired(final boolean required) {
		this.required = required;
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

	@Override
	@Basic
	@Column(name = "MAX_SIZE")
	public Integer getMaxSize() {
		return maxSize;
	}

	@Override
	public void setMaxSize(final Integer maxSize) {
		this.maxSize = maxSize;
	}

	@Override
	@Transient
	public CartItemModifierType getFieldType() {
		if (getAttributeTypeId() == 0) {
			return null;
		}
		return CartItemModifierType.valueOf(getAttributeTypeId());
	}

	@Override
	public void setFieldType(final CartItemModifierType fieldType) {
		if (fieldType == null) {
			setAttributeTypeId(0);
		} else {
			setAttributeTypeId(fieldType.getOrdinal());
		}
	}

	@Basic
	@Column(name = "ATTRIBUTE_TYPE")
	protected int getAttributeTypeId() {
		return attributeTypeId;
	}

	protected void setAttributeTypeId(final int attributeTypeId) {
		this.attributeTypeId = attributeTypeId;
	}

	@OneToMany(targetEntity = CartItemModifierFieldLdfImpl.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@ElementJoinColumn(name = "CART_ITEM_MODIFIER_FIELD_UID", nullable = false)
	@ElementForeignKey(name = "TCARTITEMMODFIELDLDF_IBFK_1")
	@ElementDependent
	protected Set<CartItemModifierFieldLdf> getCartItemModifierFieldsLdfInternal() {
		return cartItemModifierFieldsLdf;
	}

	protected void setCartItemModifierFieldsLdfInternal(final Set<CartItemModifierFieldLdf> cartItemModifierFieldsLdf) {
		this.cartItemModifierFieldsLdf = cartItemModifierFieldsLdf;
	}

	@OneToMany(targetEntity = CartItemModifierFieldOptionImpl.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@ElementJoinColumn(name = "CART_ITEM_MODIFIER_FIELD_UID", nullable = false)
	@ElementForeignKey(name = "TCARTITEMMODFIELDOPTION_IBFK_1")
	@ElementDependent
	protected Set<CartItemModifierFieldOption> getCartItemModifierFieldOptionsInternal() {
		return cartItemModifierFieldOptions;
	}

	protected void setCartItemModifierFieldOptionsInternal(final Set<CartItemModifierFieldOption> cartItemModifierFieldOptions) {
		this.cartItemModifierFieldOptions = cartItemModifierFieldOptions;
	}

	@Override
	public Set<CartItemModifierFieldLdf> getCartItemModifierFieldsLdf() {
		return Collections.unmodifiableSet(getCartItemModifierFieldsLdfInternal());
	}

	@Override
	public Set<CartItemModifierFieldOption> getCartItemModifierFieldOptions() {
		return Collections.unmodifiableSet(getCartItemModifierFieldOptionsInternal());
	}

	@Override
	public void addCartItemModifierFieldLdf(final CartItemModifierFieldLdf cartItemModifierFieldLdf) {
		if (StringUtils.isEmpty(cartItemModifierFieldLdf.getDisplayName())) {
			throw new IllegalArgumentException("Cannot have a cartItemModifierFieldLdf with empty display name");
		}
		if (StringUtils.isEmpty(cartItemModifierFieldLdf.getLocale())) {
			throw new IllegalArgumentException("Cannot have a cartItemModifierFieldLdf with empty locale");
		}

		for (CartItemModifierFieldLdf existentCartItemModifierFieldLdf : this.getCartItemModifierFieldsLdfInternal()) {
			if (cartItemModifierFieldLdf.getLocale().equals(existentCartItemModifierFieldLdf.getLocale())) {
				throw new IllegalArgumentException("Cannot have two cartItemModifierFieldLdf with the same locale");
			}
		}

		this.getCartItemModifierFieldsLdfInternal().add(cartItemModifierFieldLdf);
	}

	@Override
	public void removeCartItemModifierFieldLdf(final CartItemModifierFieldLdf cartItemModifierFieldLdf) {
		boolean remove = this.getCartItemModifierFieldsLdfInternal().remove(cartItemModifierFieldLdf);
		if (!remove) {
			throw new IllegalStateException("Cannot remove cartItemModifierFieldLdf");
		}
	}

	@Override
	public void addCartItemModifierFieldOption(final CartItemModifierFieldOption cartItemModifierFieldOption) {
		if (StringUtils.isEmpty(cartItemModifierFieldOption.getValue())) {
			throw new IllegalArgumentException("Cannot have empty cartItemModifierFieldOption.value");
		}

		for (CartItemModifierFieldOption existentCartItemModifierFieldOption : this.getCartItemModifierFieldOptionsInternal()) {
			if (existentCartItemModifierFieldOption.getOrdering() == cartItemModifierFieldOption.getOrdering()) {
				throw new IllegalArgumentException("Cannot have cartItemModifierFieldOption with the same ordering as existent object"
						+ cartItemModifierFieldOption.getOrdering());
			}
		}

		this.getCartItemModifierFieldOptionsInternal().add(cartItemModifierFieldOption);
	}

	@Override
	public void removeCartItemModifierFieldOption(final CartItemModifierFieldOption cartItemModifierFieldOption) {
		boolean remove = this.getCartItemModifierFieldOptionsInternal().remove(cartItemModifierFieldOption);
		if (!remove) {
			throw new IllegalStateException("Cannot remove cartItemModifierFieldOption");
		}
	}

	@Override
	public CartItemModifierFieldLdf findCartItemModifierFieldLdfByLocale(final String language) {
		for (CartItemModifierFieldLdf existentCartItemModifierFieldLdf : this.getCartItemModifierFieldsLdfInternal()) {
			if (language.equals(existentCartItemModifierFieldLdf.getLocale())) {
				return existentCartItemModifierFieldLdf;
			}
		}
		return null;
	}

	@Override
	public CartItemModifierFieldOption findCartItemModifierFieldOptionByValue(final String value) {
		for (CartItemModifierFieldOption existentCartItemModifierFieldOption : this.getCartItemModifierFieldOptionsInternal()) {
			if (value.equals(existentCartItemModifierFieldOption.getValue())) {
				return existentCartItemModifierFieldOption;
			}
		}
		return null;
	}

	/*
	 * Sorting on ordering is natural sorting for CartItemModifierField.
     */
	@Override
	public int compareTo(final CartItemModifierField obj) {
		return this.ordering - obj.getOrdering();
	}
}
