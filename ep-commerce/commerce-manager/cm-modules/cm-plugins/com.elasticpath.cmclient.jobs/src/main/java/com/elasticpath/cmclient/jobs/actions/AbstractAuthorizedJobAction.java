/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.jobs.actions;

import org.eclipse.jface.resource.ImageDescriptor;

import com.elasticpath.cmclient.core.actions.AbstractAuthorizedAction;
import com.elasticpath.cmclient.core.service.AuthorizationService;


/**
 * The standard abstract implementation of an Job action which includes an authorization check and disables 
 * the action and does not allow it to be enabled if not authorized.
 * <p>
 * Subclasses must implement the <code>IAction.run</code> method to carry out the action's semantics.
 * </p>
 */
public class AbstractAuthorizedJobAction extends AbstractAuthorizedAction {

	private final String permission;

	
	/**
	 * Creates a new action with the given text and image. Calls the zero-arg constructor, which disables the action if not authorized, then
	 * <code>setText</code> and <code>setImageDescriptor</code>.
	 * 
 	 * @param text the action's text, or <code>null</code> if there is no text
	 * @param imageDescriptor the action's image, or <code>null</code> if there is no image
	 * @param permission the permission
	 */
	protected AbstractAuthorizedJobAction(final String text, final ImageDescriptor imageDescriptor, final String permission) {
		super(text, imageDescriptor);
		this.permission = permission;
	}
	
	@Override
	protected final boolean isAuthorized() {		
		return AuthorizationService.getInstance().isAuthorizedWithPermission(permission);
	}

}
