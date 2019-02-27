/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.store.promotions.editors;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.elasticpath.common.dto.CouponModelDto;
import com.elasticpath.commons.pagination.Page;

/**
 * Provides the rows for the CouponUsageModelDto table.
 */
public class CouponModelDtoProvider implements IStructuredContentProvider {

	/**
	 * Gets the elements of a change set.
	 *
	 * @param element the element
	 * @return an array of {@link com.elasticpath.domain.changeset.ChangeSetMember}s
	 */
	@Override
	public Object[] getElements(final Object element) {
		Page<CouponModelDto> page = (Page<CouponModelDto>) element;
		return page.getItems().toArray();
	}

	@Override
	public void dispose() {
		// nothing to dispose
	}

	@Override
	public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
		// not significant for this implementation 

	}

}
