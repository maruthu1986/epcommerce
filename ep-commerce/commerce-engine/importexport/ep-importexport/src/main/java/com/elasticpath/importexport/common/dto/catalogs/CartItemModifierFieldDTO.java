/*
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.importexport.common.dto.catalogs;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.elasticpath.common.dto.DisplayValue;
import com.elasticpath.common.dto.Dto;

/**
 * The implementation of the <code>Dto</code> interface that contains data of CartItemModifierGroup object.
 */
@XmlAccessorType(XmlAccessType.NONE)
public class CartItemModifierFieldDTO implements Dto {
	private static final long serialVersionUID = 1L;

	@XmlElement(name = "code", required = true)
	private String code;

	@XmlElement(name = "type", required = true)
	private String type;

	@XmlElement(name = "required", required = true)
	private boolean required;

	@XmlElement(name = "maxSize", required = false, nillable = true)
	private Integer maxSize;

	@XmlElement(name = "ordering", required = false)
	private int ordering;

	@XmlElementWrapper(name = "displayname")
	@XmlElement(name = "value", required = true)
	private List<DisplayValue> values;

	@XmlElementWrapper(name = "options")
	@XmlElement(name = "option", required = false)
	private List<CartItemModifierFieldOptionDTO> cartItemModifierFieldOptions;

	/**
	 * Gets the code.
	 *
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * Sets the code.
	 *
	 * @param code the code to set
	 */
	public void setCode(final String code) {
		this.code = code;
	}

	/**
	 * Gets the values.
	 *
	 * @return the values
	 */
	public List<DisplayValue> getValues() {
		return values;
	}

	/**
	 * Sets the values.
	 *
	 * @param values the values to set
	 */
	public void setValues(final List<DisplayValue> values) {
		this.values = values;
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets the type.
	 *
	 * @param type the type to set
	 */
	public void setType(final String type) {
		this.type = type;
	}

	/**
	 * Gets the required.
	 *
	 * @return the required
	 */
	public boolean isRequired() {
		return required;
	}

	/**
	 * Sets the required.
	 *
	 * @param required the required to set
	 */
	public void setRequired(final boolean required) {
		this.required = required;
	}

	/**
	 * Gets the maxSize.
	 *
	 * @return the maxSize
	 */
	public Integer getMaxSize() {
		return maxSize;
	}

	/**
	 * Sets the maxSize.
	 *
	 * @param maxSize the maxSize to set
	 */
	public void setMaxSize(final Integer maxSize) {
		this.maxSize = maxSize;
	}

	/**
	 * Gets the ordering.
	 *
	 * @return the ordering
	 */
	public int getOrdering() {
		return ordering;
	}

	/**
	 * Sets the ordering.
	 *
	 * @param ordering the ordering to set
	 */
	public void setOrdering(final int ordering) {
		this.ordering = ordering;
	}

	/**
	 * Gets the cartItemModifierFieldOptions.
	 *
	 * @return the cartItemModifierFieldOptions
	 */
	public List<CartItemModifierFieldOptionDTO> getCartItemModifierFieldOptions() {
		return cartItemModifierFieldOptions;
	}

	/**
	 * Sets the cartItemModifierFieldOptions.
	 *
	 * @param cartItemModifierFieldOptions the cartItemModifierFieldOptions to set
	 */
	public void setCartItemModifierFieldOptions(final List<CartItemModifierFieldOptionDTO> cartItemModifierFieldOptions) {
		this.cartItemModifierFieldOptions = cartItemModifierFieldOptions;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
				.append("code", getCode())
				.append("ordering", getOrdering())
				.append("isRequired", isRequired())
				.append("maxSize", getMaxSize())
				.append("type", getType())
				.append("values", getValues())
				.append("cartItemModifierFieldOptions", getCartItemModifierFieldOptions())
				.toString();
	}
}
