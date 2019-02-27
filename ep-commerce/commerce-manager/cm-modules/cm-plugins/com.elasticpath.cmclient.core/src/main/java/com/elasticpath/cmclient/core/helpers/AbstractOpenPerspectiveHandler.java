/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.helpers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.handlers.HandlerUtil;

import com.elasticpath.cmclient.core.service.AuthorizationService;

/**
 * Abstract handler for opening a perspective.
 * TODO: try to use {@link org.eclipse.ui.handlers.ShowPerspectiveHandler} instead.
 */
public abstract class AbstractOpenPerspectiveHandler extends AbstractHandler {

	/**
	 * Executes the handler.
	 * @param event the event
	 * @return null
	 * @throws ExecutionException on error
	 */
	
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		if (isEnabled()) {
			final IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
			openPerspective(getPerspectiveId(), window);
		}
		return null;
	}
	
	/**
	 * Returns perspective ID.
	 * 
	 * @return String
	 */
	protected abstract String getPerspectiveId();

	/**
	 * Opens the perspective with the given identifier.
	 * 
	 * @param perspectiveId
	 *            The perspective to open; must not be <code>null</code>
	 * @throws ExecutionException
	 *             If the perspective could not be opened.
	 */
	private void openPerspective(final String perspectiveId,
			final IWorkbenchWindow activeWorkbenchWindow)
			throws ExecutionException {
		final IWorkbench workbench = PlatformUI.getWorkbench();
		
		IAdaptable input = null;

		final IWorkbenchPage activePage = activeWorkbenchWindow.getActivePage();
		if (activePage != null) {
			input = activePage.getInput();
		}

		try {
			workbench.showPerspective(perspectiveId, activeWorkbenchWindow,
					input);
		} catch (final WorkbenchException e) {
			throw new ExecutionException("Perspective could not be opened.", e); //$NON-NLS-1$
		}
	}

	@Override
	public boolean isEnabled() {
		return AuthorizationService.getInstance().isAuthorizedToAccessPerspective(getPerspectiveId());
	}

}
