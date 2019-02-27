/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.cmclient.policy.ui;

import com.elasticpath.cmclient.core.ui.framework.AbstractEpDualListBoxControl;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.policy.StateChangeTarget;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;

/**
 * An abstract implementation of the dual list box with support for policy state.
 * @param <T> the model class
 */
public abstract class AbstractPolicyAwareDualListBox<T> 
	extends AbstractEpDualListBoxControl<T> implements StateChangeTarget {

	/**
	 * Constructs a new policy aware dual list box.
	 * 
	 * @param parentComposite the parent composite
	 * @param data the layout data
	 * @param container the container to use
	 * @param model the model
	 * @param availableTitle the available box title
	 * @param assignedTitle the assigned box title
	 * @param style the style to use
	 */
	public AbstractPolicyAwareDualListBox(final IPolicyTargetLayoutComposite parentComposite,
			final IEpLayoutData data,
			final PolicyActionContainer container, 
			final T model, 
			final String availableTitle, 
			final String assignedTitle, 
			final int style) {
		super(parentComposite.getLayoutComposite(), model, availableTitle, assignedTitle, style, data, EpState.READ_ONLY);
		container.addTarget(this);
	}

    @Override
	public void setState(final EpState state) {
		super.changeState(state);
	}
	
}
