/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.changeset.editors;

import java.util.Collection;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.elasticpath.common.dto.ChangeSetDependencyDto;

/**
 * Content provider for the change set members table.
 */
public class ConflictsChangeSetProvider implements IStructuredContentProvider {

	/**
	 * Gets the elements of a change set.
	 * 
	 * @param element the element
	 * @return an array of change set dependency dto
	 */
	@Override
	public Object[] getElements(final Object element) {
		Collection<ChangeSetDependencyDto> changeSetDependencyDtos = (Collection<ChangeSetDependencyDto>) element;
		return changeSetDependencyDtos.toArray();
	}
	
	/**
	 *
	 */
	@Override
	public void dispose() {
		// nothing to dispose
	}
	
	/**
	 *
	 * @param viewer the viewer
	 * @param oldInput the old input
	 * @param newInput the new input
	 */
	@Override
	public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
		// not significant for this implementation 
		
	}
}