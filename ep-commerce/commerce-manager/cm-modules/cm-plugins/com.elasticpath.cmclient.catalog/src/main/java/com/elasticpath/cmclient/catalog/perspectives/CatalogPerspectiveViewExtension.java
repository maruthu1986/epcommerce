/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.catalog.perspectives;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

import com.elasticpath.cmclient.core.security.Authorizable;

/**
 * The catalog perspective view extension. * 
 */
public class CatalogPerspectiveViewExtension implements
		ICatalogPerspectiveViewExtension {

	private final String placeholder;
	private final String viewId;
	private final boolean closeable;
	private final boolean moveable;
	private final Authorizable authorizable;

	/**
	 * The constructor. 
	 * 
	 * @param configElement the config element
	 * @throws CoreException the core exception
	 */
	public CatalogPerspectiveViewExtension(final IConfigurationElement configElement) throws CoreException {
		viewId = configElement.getAttribute("viewId"); //$NON-NLS-1$
		authorizable = (Authorizable) configElement.createExecutableExtension("authorizationClass"); //$NON-NLS-1$
		closeable = Boolean.valueOf(configElement.getAttribute("closeable")); //$NON-NLS-1$
		moveable = Boolean.valueOf(configElement.getAttribute("moveable")); //$NON-NLS-1$
		placeholder = configElement.getAttribute("placeholder"); //$NON-NLS-1$
	}

	@Override
	public String getPlaceholder() {
		return placeholder;
	}

	@Override
	public String getViewId() {
		return viewId;
	}

	@Override
	public boolean isAuthorized() {
		return authorizable.isAuthorized();
	}

	@Override
	public boolean isCloseable() {
		return closeable;
	}

	@Override
	public boolean isMovable() {
		return moveable;
	}

}
