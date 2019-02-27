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
 * The implementation of the <code>Dto</code> interface that contains data of CartItemModifierFieldOptionDTO object.
 */
@XmlAccessorType(XmlAccessType.NONE)
public class CartItemModifierFieldOptionDTO implements Dto {

	private static final long serialVersionUID = 1L;

	@XmlElementWrapper(name = "displayname")
	@XmlElement(name = "value", required = true)
	private List<DisplayValue> values;

	@XmlElement(name = "value", required = true)
	private String value;

	@XmlElement(name = "ordering", required = false)
	private int ordering;

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
	 * Gets the value.
	 *
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets the value.
	 *
	 * @param value the value to set
	 */
	public void setValue(final String value) {
		this.value = value;
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

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
				.append("value", getValue())
				.append("ordering", getOrdering())
				.append("values", getValues())
				.toString();
	}
}
