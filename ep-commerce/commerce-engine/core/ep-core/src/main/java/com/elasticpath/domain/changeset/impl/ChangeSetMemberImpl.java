/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.domain.changeset.impl;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import com.elasticpath.domain.changeset.ChangeSetMember;
import com.elasticpath.domain.changeset.ChangeSetMemberMutator;
import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;

/**
 * Change Set Member Implementation.
 */
public class ChangeSetMemberImpl implements ChangeSetMember, ChangeSetMemberMutator {
	
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	private BusinessObjectDescriptor businessObjectDescriptor;
	private Map<String, String> metadata;


	@Override
	public BusinessObjectDescriptor getBusinessObjectDescriptor() {
		return businessObjectDescriptor;
	}

	/**
	 * {@inheritDoc}
	 * The returned value will never be null.
	 */
	@Override
	public Map<String, String> getMetadata() {
		if (metadata == null) {
			return Collections.emptyMap();
		}
		return metadata;
	}

	@Override	
	public void setBusinessObjectDescriptor(final BusinessObjectDescriptor businessObjectDescriptor) {
		this.businessObjectDescriptor = businessObjectDescriptor;	
	}
	
	@Override
	public void setMetadata(final Map<String, String> metadataMap) {
		this.metadata = metadataMap;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(businessObjectDescriptor);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof ChangeSetMemberImpl)) {
			return false;
		}
		ChangeSetMemberImpl other = (ChangeSetMemberImpl) obj;
		return Objects.equals(businessObjectDescriptor, other.businessObjectDescriptor);
	}
	
	
}
