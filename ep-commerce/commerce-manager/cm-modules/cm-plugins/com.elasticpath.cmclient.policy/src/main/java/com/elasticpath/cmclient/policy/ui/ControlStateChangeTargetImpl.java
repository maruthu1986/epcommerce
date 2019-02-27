/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.policy.ui;

import org.eclipse.swt.widgets.Control;

import com.elasticpath.cmclient.core.ui.framework.EpControlFactory;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.policy.StateChangeTarget;

/**
 * A <code>StatePolicy</code> target for SWT Controls.
 */
public class ControlStateChangeTargetImpl implements StateChangeTarget {

	private final Control control;
	
	/**
	 * Wrap the given control as a policy target.
	 * 
	 * @param control the control to wrap.
	 */
	public ControlStateChangeTargetImpl(final Control control) {
		super();
		this.control = control;
	}

	@Override
	public void setState(final EpState state) {
		EpControlFactory.changeEpState(control, state);
	}

}
