/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.domain.objectgroup.impl;

import java.util.Objects;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;

/**
 * The default implementation of the descriptor.
 */
public class BusinessObjectDescriptorImpl implements BusinessObjectDescriptor {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	private String objectIdentifier;
	private String objectType;

	@Override
	public String getObjectIdentifier() {
		return objectIdentifier;
	}

	@Override
	public String getObjectType() {
		return objectType;
	}

	/**
	 *
	 * @param objectIdentifier the objectIdentifier to set
	 */
	@Override
	public void setObjectIdentifier(final String objectIdentifier) {
		this.objectIdentifier = objectIdentifier;
	}

	/**
	 *
	 * @param objectType the objectType to set
	 */
	@Override
	public void setObjectType(final String objectType) {
		this.objectType = objectType;
	}

	/**
	 * Compares this object with the one passed
	 * by their object identifiers and types.
	 * 
	 * @param obj the object to compare with
	 * @return true if objects are equal
	 */
	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof BusinessObjectDescriptor)) {
			return false;
		}
		BusinessObjectDescriptor otherDesc = (BusinessObjectDescriptor) obj;
		
		return Objects.equals(getObjectIdentifier(), otherDesc.getObjectIdentifier())
			&& Objects.equals(getObjectType(), otherDesc.getObjectType());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getObjectIdentifier(), getObjectType());
	}

	/**
	 * Returns object state string, for example: BusinessObjectDescriptorImpl[objectType=Product,objectIdentifier=123].
	 * 
	 * @return Object state as string.
	 */
	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
			.append("objectType", getObjectType())
			.append("objectIdentifier", getObjectIdentifier())
			.toString();
	}
	
	
}
