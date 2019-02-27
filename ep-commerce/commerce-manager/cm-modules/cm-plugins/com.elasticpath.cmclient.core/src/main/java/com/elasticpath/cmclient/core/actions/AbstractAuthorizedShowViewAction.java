/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;

/**
 * Abstract authorized action for showing a view.
 */
public abstract class AbstractAuthorizedShowViewAction extends AbstractShowViewAction {
	
	private boolean firstLoad = true;
	
	@Override
	public void selectionChanged(final IAction action, final ISelection selection) {
		if (firstLoad) {
			action.setEnabled(isAuthorized());
			firstLoad = false;
		}
		
	}
	
	/**
	 * Returns whether the user is authorized.
	 * 
	 * @return true if authorized false otherwise
	 */
	public abstract boolean isAuthorized();

}
