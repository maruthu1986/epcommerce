/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.advancedsearch.actions;

import org.eclipse.jface.resource.ImageDescriptor;

import com.elasticpath.cmclient.advancedsearch.AdvancedSearchPermissions;
import com.elasticpath.cmclient.core.actions.AbstractAuthorizedAction;
import com.elasticpath.cmclient.core.service.AuthorizationService;

/**
 * An Abstract Implementation for Query Actions.
 */
public abstract class AbstractQueryAction extends AbstractAuthorizedAction {

	/**
	 * Performs current action work.
	 */
	abstract void performWork();

	/**
	 * Constructs a abstract query action.
	 * 
	 * @param text - the text of the action
	 * @param imageDescriptor - current image descriptor
	 */
	protected AbstractQueryAction(final String text, final ImageDescriptor imageDescriptor) {
		super(text, imageDescriptor);
	}


	@Override
	public void run() {
		performWork();
	}

	/**
	 * Checks If CREATE_QUERIES permission is authorized.
	 * @return true if CREATE_QUERIES permission authorized
	 */
	protected boolean isCreateAuthorized() {
		return AuthorizationService.getInstance().isAuthorizedWithPermission(AdvancedSearchPermissions.CREATE_QUERIES);
	}

	/**
	 * Checks If MANAGE_QUERIES permission is authorized.
	 * @return true if MANAGE_QUERIES permission authorized
	 */
	protected boolean isManageAuthorized() {
		return AuthorizationService.getInstance().isAuthorizedWithPermission(AdvancedSearchPermissions.MANAGE_QUERIES);
	}
}