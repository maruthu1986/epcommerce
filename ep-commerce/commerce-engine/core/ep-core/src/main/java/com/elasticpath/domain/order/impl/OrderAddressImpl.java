/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.order.impl;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.apache.openjpa.persistence.DataCache;

import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.customer.impl.AbstractAddressImpl;
import com.elasticpath.domain.order.OrderAddress;

/**
 * Address that is stored as part of a completed order.
 */
@Entity
@Table(name = OrderAddressImpl.TABLE_NAME)
@DataCache(enabled = false)
public class OrderAddressImpl extends AbstractAddressImpl implements OrderAddress {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TORDERADDRESS";

	private long uidPk;

	/**
	 * Initialize this order address with the information from the specified customer address.
	 *
	 * @param customerAddress the address with information to load into this order addresss
	 */
	@Override
	public void init(final Address customerAddress) {
		this.setCreationDate(customerAddress.getCreationDate());
		this.setLastModifiedDate(customerAddress.getLastModifiedDate());
		this.setFirstName(customerAddress.getFirstName());
		this.setLastName(customerAddress.getLastName());
		this.setStreet1(customerAddress.getStreet1());
		this.setStreet2(customerAddress.getStreet2());
		this.setCity(customerAddress.getCity());
		this.setCountry(customerAddress.getCountry());
		this.setSubCountry(customerAddress.getSubCountry());
		this.setPhoneNumber(customerAddress.getPhoneNumber());
		this.setFaxNumber(customerAddress.getFaxNumber());
		this.setZipOrPostalCode(customerAddress.getZipOrPostalCode());
		this.setCommercialAddress(customerAddress.isCommercialAddress());
		this.setOrganization(customerAddress.getOrganization());
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

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append(getFirstName()).append(' ')
				.append(getLastName()).append('\n')
				.append(getStreet1()).append('\n')
				.append(getStreet2()).append('\n')
				.append(getCity()).append('\n');
		if (getSubCountry() != null && getSubCountry().length() > 0) {
			result.append(getSubCountry()).append(", ");
		}
		result.append(getCountry()).append(' ').append(getZipOrPostalCode()).append("\nPhone: ").append(getPhoneNumber());
		if (getFaxNumber() != null) {
			result.append("\nFax: ").append(getFaxNumber());
		}
		if (getOrganization() != null) {
			result.append("\nOrganization: ").append(getOrganization());
		}
		return result.toString();
	}
}
