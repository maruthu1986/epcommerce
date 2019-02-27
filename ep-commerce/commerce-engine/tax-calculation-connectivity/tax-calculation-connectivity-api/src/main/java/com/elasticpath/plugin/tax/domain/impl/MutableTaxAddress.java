/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.plugin.tax.domain.impl;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.elasticpath.plugin.tax.domain.TaxAddress;

/**
 * Mutable implementation of {@TaxAddress}.
 */
public class MutableTaxAddress implements TaxAddress, Serializable {

	private static final long serialVersionUID = 50000000001L;
	
	private String street1;
	private String street2;
	private String city;
	private String subCountry;
	private String zipOrPostalCode;
	private String country;
	private String firstName;
	private String lastName;
	private String phoneNumber;

	@Override
	public String getStreet1() {
		return street1;
	}

	@Override
	public String getStreet2() {
		return street2;
	}

	@Override
	public String getCity() {
		return city;
	}

	@Override
	public String getSubCountry() {
		return subCountry;
	}

	@Override
	public String getZipOrPostalCode() {
		return zipOrPostalCode;
	}

	@Override
	public String getCountry() {
		return country;
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
		return EqualsBuilder.reflectionEquals(this, obj);
	}
	
	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public String getFirstName() {
		return firstName;
	}

	@Override
	public String getLastName() {
		return lastName;
	}

	public void setFirstName(final String firstName) {
		this.firstName = firstName;
	}

	public void setLastName(final String lastName) {
		this.lastName = lastName;
	}

	@Override
	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(final String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

}