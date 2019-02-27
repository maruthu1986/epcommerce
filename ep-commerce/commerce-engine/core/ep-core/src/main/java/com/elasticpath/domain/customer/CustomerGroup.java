/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.customer;

import java.util.Set;

import com.elasticpath.persistence.api.Entity;

/**
 * <code>CustomerGroup</code> represents a customer group.
 *
 */
public interface CustomerGroup extends Entity {
	/** The default customer group name. Every customer is by default in this group. */
	String DEFAULT_GROUP_NAME = "PUBLIC";

	/**
	 * Gets the name of this <code>CustomerGroup</code>.
	 *
	 * @return the name of the customer group.
	 * @domainmodel.property
	 */
	String getName();

	/**
	 * Sets the name for this <code>CustomerGroup</code>.
	 *
	 * @param name the new user identifier.
	 */
	void setName(String name);

	/**
	 * Gets the description of this <code>CustomerGroup</code>.
	 * 
	 * @return the description of the customer group.
	 * @domainmodel.property
	 */
	String getDescription();

	/**
	 * Sets the description for this <code>CustomerGroup</code>.
	 * 
	 * @param description the description of the customer group.
	 */
	void setDescription(String description);

	/**
	 * Gets the enabled flag of this <code>CustomerGroup</code>.
	 * 
	 * @return the enabled flag of the customer group.
	 * @domainmodel.property
	 */
	boolean isEnabled();

	/**
	 * Sets the enabled flag for this <code>CustomerGroup</code>.
	 * 
	 * @param enabled the enabled flag of the customer group.
	 */
	void setEnabled(boolean enabled);

	/**
	 * Gets the <code>CustomerRole</code>s associated with customers in this <code>CustomerGroup</code>.
	 *
	 * @return the set of customerRoles.
	 *
	 */
	Set<CustomerRole> getCustomerRoles();

	/**
	 * Sets the <code>CustomerRole</code>s associated with customers in this <code>CustomerGroup</code>.
	 *
	 * @param customerRoles the new set of customerRoles.
	 */
	void setCustomerRoles(Set<CustomerRole> customerRoles);

}
