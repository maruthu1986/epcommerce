/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.changeset.editors;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import com.elasticpath.domain.changeset.ChangeSetMember;
import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;

/**
 *	a changeset object type filter. 
 */
public class ChangeSetObjectTypeFilter extends ViewerFilter {

	
	private final String objectType;
	/**
	 * 
	 *Constructor.
	 * @param objectType the object type to filter for.
	 */
	public ChangeSetObjectTypeFilter(final String objectType) {
		this.objectType = objectType;
	}
	
	
	@Override
	/**
	 * {@inheritDoc}
	 */
	public boolean select(final Viewer viewer, final Object parentElement, final Object element) {

		if (element instanceof ChangeSetMember) {
			ChangeSetMember member = (ChangeSetMember) element;
			BusinessObjectDescriptor businessObjectDescriptor = member.getBusinessObjectDescriptor();
			if (objectType.equals(businessObjectDescriptor.getObjectType())) { 
				return false;
			}
		}
		return true;
	}

}
