/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.store.promotions.editors;

import java.util.List;

import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwarePaginationControl;
import com.elasticpath.cmclient.policy.ui.IPolicyTargetLayoutComposite;
import com.elasticpath.commons.pagination.DirectedSortingField;
import com.elasticpath.commons.pagination.Page;
import com.elasticpath.commons.pagination.Paginator;
import com.elasticpath.commons.pagination.SearchCriterion;
import com.elasticpath.commons.pagination.SearchablePaginatorLocatorAdapter;

/**
 * Pagination for coupons.
 * 
 * @param <T> The type of the model to use.
 */
final class CouponPaginationControl<T> extends
		AbstractPolicyAwarePaginationControl<T> {
	
	private final SearchablePaginatorLocatorAdapter< ? > searchablePaginatorLocatorAdapter;
	
	/**
	 * 
	 */
	private final CouponEditorPart couponEditorPart;

	/**
	 * Constructor.
	 * 
	 * @param couponEditorPart The coupon editor part.
	 * @param parentComposite The composite.
	 * @param layoutData The layout data.
	 * @param container The container.
	 * @param paginator The paginator.
	 */
	CouponPaginationControl(
			final CouponEditorPart couponEditorPart, final IPolicyTargetLayoutComposite parentComposite,
			final IEpLayoutData layoutData, final PolicyActionContainer container,
			final Paginator<T> paginator) {
		super(parentComposite, layoutData, container, paginator);
		searchablePaginatorLocatorAdapter = (SearchablePaginatorLocatorAdapter< ? >) paginator.getPaginatorLocator();
		this.couponEditorPart = couponEditorPart;
	}

	@Override
	public void update(final Page<T> newPage) {
		this.couponEditorPart.getCouponUsageTableViewer().setInput(newPage);
		this.couponEditorPart.getCouponUsageTableViewer().getSwtTable().setFocus();
		if (this.couponEditorPart.getStatePolicy() != null) {
			this.couponEditorPart.applyStatePolicy(this.couponEditorPart.getStatePolicy());
		}
	}
	
	/**
	 * Called when a new search is required.
	 * @param searchCriteria The criteria to use.
	 */
	public void search(final List<SearchCriterion> searchCriteria) {

		searchablePaginatorLocatorAdapter.setSearchCriteria(searchCriteria);
		
		getPaginator().first();
		update(getPaginator().getCurrentPage());
		updateControls();
	}

	/**
	 * Called to change the sort fields and update the table.
	 * @param sortField The new sort field.
	 */
	public void sortBy(final DirectedSortingField sortField) {
		getPaginator().setSortingFields(sortField);
		getPaginator().first();
		update(getPaginator().getCurrentPage());
		updateControls();
	}

	/**
	 * Refreshes the current page from the paginator and updates the table and controls.
	 */
	public void refreshCurrentPage() {
		getPaginator().refreshCurrentPage();
		update(getPaginator().getCurrentPage());
		updateControls();
	}
}