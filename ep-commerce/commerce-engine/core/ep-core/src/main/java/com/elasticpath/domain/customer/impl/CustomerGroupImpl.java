/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.customer.impl;

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

import org.apache.openjpa.persistence.DataCache;
import org.apache.openjpa.persistence.jdbc.ElementJoinColumn;

import com.elasticpath.domain.customer.CustomerGroup;
import com.elasticpath.domain.customer.CustomerRole;
import com.elasticpath.domain.impl.AbstractLegacyEntityImpl;

/**
 * The default implementation of <code>CustomerGroup</code>.
 *
 */
@Entity
@Table(name = CustomerGroupImpl.TABLE_NAME)
@DataCache(enabled = false)
public class CustomerGroupImpl extends AbstractLegacyEntityImpl implements CustomerGroup {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000003L;

	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TCUSTOMERGROUP";

	private String name;

	private String description;

	private boolean enabled;

	private Set<CustomerRole> customerRoles;

	private long uidPk;

	private String guid;

	/** Gets the name for this <code>CustomerGroup</code>.
	 *
	 * @return the name of the customer group.
	 */
	@Override
	@Basic
	@Column(name = "NAME")
	public String getName() {
		return this.name;
	}

	/**
	 * Sets the name for this <code>CustomerGroup</code>.
	 *
	 * @param name the new user identifier.
	 */
	@Override
	public void setName(final String name) {
		this.name = name;
	}

	/** Gets the description for this <code>CustomerGroup</code>.
	 *
	 * @return a description of the customer group.
	 */
	@Override
	@Basic
	@Column(name = "DESCRIPTION")
	public String getDescription() {
		return this.description;
	}

	/**
	 * Sets the description for this <code>CustomerGroup</code>.
	 *
	 * @param description a description of this customer group.
	 */
	@Override
	public void setDescription(final String description) {
		this.description = description;
	}

	/** Gets the enabled flag for this <code>CustomerGroup</code>.
	 *
	 * @return true if this customer group is enabled, else false.
	 */
	@Override
	@Basic
	@Column(name = "ENABLED")
	public boolean isEnabled() {
		return this.enabled;
	}

	/**
	 * Sets the enabled flag on this <code>CustomerGroup</code> is enabled.
	 *
	 * @param enabled if this customer group is enabled.
	 */
	@Override
	public void setEnabled(final boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * Gets the <code>CustomerRole</code>s associated with customers in this <code>CustomerGroup</code>.
	 *
	 * @return the set of customerRoles.ROLE_CUSTOMER
	 */
	@Override
	@OneToMany(targetEntity = CustomerRoleImpl.class, cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	@ElementJoinColumn(name = "CUSTOMER_GROUP_UID", nullable = false)
	public Set<CustomerRole> getCustomerRoles() {
		return this.customerRoles;
	}
	
	/**
	 * Sets the <code>CustomerRole</code>s associated with customers in this <code>CustomerGroup</code>.
	 *
	 * @param customerRoles the new set of customerRoles.
	 */
	@Override
	public void setCustomerRoles(final Set<CustomerRole> customerRoles) {
		this.customerRoles = customerRoles;
	}
	
	/**
	 * Return the guid.
	 *
	 * @return the guid.
	 */
	@Override
	@Basic
	@Column(name = "GUID", nullable = false, length = GUID_LENGTH)
	public String getGuid() {
		return guid;
	}

	/**
	 * Set the guid.
	 *
	 * @param guid the guid to set.
	 */
	@Override
	public void setGuid(final String guid) {
		this.guid = guid;
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
	@TableGenerator(name = TABLE_NAME, table = "JPA_GENERATED_KEYS", pkColumnName = "ID", valueColumnName = "LAST_VALUE", pkColumnValue = TABLE_NAME)
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
