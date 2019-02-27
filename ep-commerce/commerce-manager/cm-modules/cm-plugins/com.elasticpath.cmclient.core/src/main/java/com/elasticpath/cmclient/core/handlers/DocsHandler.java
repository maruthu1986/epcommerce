/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.rap.rwt.client.service.UrlLauncher;

import com.elasticpath.cmclient.core.util.ServiceUtil;

/**
 * Used open documentation.
 */
public class DocsHandler extends AbstractHandler {

	private static final String DOCS_INDEX = "help/html/index.html"; //$NON-NLS-1$

	@Override
	public Object execute(final ExecutionEvent executionEvent) throws ExecutionException {
		UrlLauncher launcher = ServiceUtil.getUrlLauncherService();
		launcher.openURL(DOCS_INDEX);
		return null;
	}
}
