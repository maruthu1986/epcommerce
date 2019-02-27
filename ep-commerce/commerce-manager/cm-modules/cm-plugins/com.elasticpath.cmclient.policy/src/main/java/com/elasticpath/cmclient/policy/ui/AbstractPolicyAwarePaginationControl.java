/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.cmclient.policy.ui;

import com.elasticpath.cmclient.core.ui.framework.AbstractPaginationControl;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.policy.StateChangeTarget;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.commons.pagination.Paginator;

/**
 * This component constructs a grid of buttons to allow for navigation and display of information.
 * @param <T> the class this pagination works on
 */
public abstract class AbstractPolicyAwarePaginationControl<T> extends AbstractPaginationControl<T> implements StateChangeTarget {

	private final PolicyActionContainer container;

	/**
	 * Constructor.
	 * 
	 * @param parentComposite the parent policy aware composite
	 * @param layoutData the layout data to use
	 * @param container the policy container
	 * @param paginator the paginator
	 */
	public AbstractPolicyAwarePaginationControl(
			final IPolicyTargetLayoutComposite parentComposite, 
			final IEpLayoutData layoutData, 
			final PolicyActionContainer container, 
			final Paginator<T> paginator) {
		super(parentComposite.getLayoutComposite(), layoutData, EpState.READ_ONLY, paginator);
		this.container = container;
		this.container.addTarget(this);
	}

	@Override
	public void setState(final EpState state) {
		super.changeState(state);
	}

	@Override
	public void dispose() {
		super.dispose();
		this.container.removeTarget(this);
	}

}
