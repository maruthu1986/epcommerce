/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.security;

import org.eclipse.rap.rwt.RWT;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.util.Assert;

/**
 * UI Session Security COntext Holder Strategy Class.
 */
public class UISessionSecurityContextHolderStrategy implements SecurityContextHolderStrategy {

	private static final String SESSION_ATTRIBUTE_NAME = "authContext";

	@Override
	public void clearContext() {
		RWT.getUISession().removeAttribute(SESSION_ATTRIBUTE_NAME);
	}

	@Override
	public SecurityContext getContext() {
		SecurityContext context = (SecurityContext) RWT.getUISession().getAttribute(SESSION_ATTRIBUTE_NAME);

		if (context == null) {
			context = createEmptyContext();
			RWT.getUISession().setAttribute(SESSION_ATTRIBUTE_NAME, context);
		}

		return context;
	}

	@Override
	public void setContext(final SecurityContext context) {
		Assert.notNull(context, "Only non-null SecurityContext instances are permitted");
		RWT.getUISession().setAttribute(SESSION_ATTRIBUTE_NAME, context);
	}

	@Override
	public SecurityContext createEmptyContext() {
		return new SecurityContextImpl();
	}

}
