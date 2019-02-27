/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */

package com.elasticpath.shipping.connectivity.dto.impl;

import java.io.Serializable;
import java.util.Objects;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.elasticpath.shipping.connectivity.dto.ShippingAddress;

/**
 * Implementation of {@link ShippingAddress}.
 */
public class ShippingAddressImpl implements ShippingAddress, Comparable<ShippingAddress>, Serializable {

	private static final long serialVersionUID = 5000000001L;

	private String guid;

	private String street1;

	private String street2;

	private String city;

	private String subCountry;

	private String zipOrPostalCode;

	private String country;

	@Override
	public String getGuid() {
		return this.guid;
	}

	@Override
	public String getStreet1() {
		return this.street1;
	}

	@Override
	public String getStreet2() {
		return this.street2;
	}

	@Override
	public String getCity() {
		return this.city;
	}

	@Override
	public String getSubCountry() {
		return this.subCountry;
	}

	@Override
	public String getZipOrPostalCode() {
		return this.zipOrPostalCode;
	}

	@Override
	public String getCountry() {
		return this.country;
	}

	public void setGuid(final String guid) {
		this.guid = guid;
	}

	public void setStreet1(final String street1) {
		this.street1 = street1;
	}

	public void setStreet2(final String street2) {
		this.street2 = street2;
	}

	public void setCity(final String city) {
		this.city = city;
	}

	public void setSubCountry(final String subCountry) {
		this.subCountry = subCountry;
	}

	public void setZipOrPostalCode(final String zipOrPostalCode) {
		this.zipOrPostalCode = zipOrPostalCode;
	}

	public void setCountry(final String country) {
		this.country = country;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

	@Override
	public boolean equals(final Object obj) {

		if (this == obj) {
			return true;
		}

		if (!(obj instanceof ShippingAddress)) {
			return false;
		}

		final ShippingAddress other = (ShippingAddress) obj;

		return Objects.equals(getGuid(), other.getGuid())
				&& Objects.equals(getStreet1(), other.getStreet1())
				&& Objects.equals(getStreet2(), other.getStreet2())
				&& Objects.equals(getCity(), other.getCity())
				&& Objects.equals(getSubCountry(), other.getSubCountry())
				&& Objects.equals(getZipOrPostalCode(), other.getZipOrPostalCode())
				&& Objects.equals(getCountry(), other.getCountry());

	}

	@Override
	public int hashCode() {

		return Objects.hash(
				getGuid(),
				getStreet1(),
				getStreet2(),
				getCity(),
				getSubCountry(),
				getZipOrPostalCode(),
				getCountry()
		);
	}

	@Override
	public int compareTo(final ShippingAddress other) {

		if (other == null) {
			return -1;
		}

		if (this == other) {
			return 0;
		}

		return new CompareToBuilder()
				.append(getGuid(), other.getGuid())
				.append(getStreet1(), other.getStreet1())
				.append(getStreet2(), other.getStreet2())
				.append(getCity(), other.getCity())
				.append(getSubCountry(), other.getSubCountry())
				.append(getZipOrPostalCode(), other.getZipOrPostalCode())
				.append(getCountry(), other.getCountry()).toComparison();

	}
}
