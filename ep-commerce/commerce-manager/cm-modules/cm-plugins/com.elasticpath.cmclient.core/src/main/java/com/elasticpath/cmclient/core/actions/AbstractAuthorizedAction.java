/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core.actions;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Event;

import com.elasticpath.cmclient.core.handlers.ApplicationErrorHandler;

/**
 * The standard abstract implementation of an action which includes an authorization check and disables the action and does not allow it to be
 * enabled if not authorized.
 * <p>
 * Subclasses must implement the <code>IAction.run</code> method to carry out the action's semantics.
 * </p>
 */
public abstract class AbstractAuthorizedAction extends Action {

	private static final Logger LOG = Logger.getLogger(AbstractAuthorizedAction.class);
	
	/**
	 * Creates a new action with no text and no image.
	 * Disables the action if the user is not authorized.
	 * <p>
	 * Configure the action later using the set methods.
	 * </p>
	 */
	@SuppressWarnings("PMD.ConstructorCallsOverridableMethod")
	protected AbstractAuthorizedAction() {
		super();
		super.setEnabled(isAuthorized());
	}

	/**
	 * Creates a new action with the given text and no image. Calls the zero-arg constructor, which disables the action if not authorized, then
	 * <code>setText</code>.
	 * 
	 * @param text the string used as the text for the action, or <code>null</code> if there is no text
	 * @see #setText
	 */
	protected AbstractAuthorizedAction(final String text) {
		this();
		setText(text);
	}

	/**
	 * Creates a new action with the given text and image. Calls the zero-arg constructor, which disables the action if not authorized, then
	 * <code>setText</code> and <code>setImageDescriptor</code>.
	 * 
	 * @param text the action's text, or <code>null</code> if there is no text
	 * @param image the action's image, or <code>null</code> if there is no image
	 * @see #setText
	 * @see #setImageDescriptor
	 */
	protected AbstractAuthorizedAction(final String text, final ImageDescriptor image) {
		this(text);
		setImageDescriptor(image);
	}

	/**
	 * Returns <code>true</code> if authorized, otherwise <code>false</code>.
	 * 
	 * @return boolean <code>true</code> if authorized, otherwise <code>false</code>
	 */
	protected abstract boolean isAuthorized();

	@Override
	public void setEnabled(final boolean enabled) {
		super.setEnabled(enabled && isAuthorized());
	}
	
	@Override
	public void runWithEvent(final Event event) {
		try {
			run();
		} catch (Exception e) {
			ApplicationErrorHandler.createErrorDialogForException(e);
			LOG.error("Exception occured", e); //$NON-NLS-1$
		}
	}
}
