/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.actions;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;

import com.elasticpath.cmclient.core.EpAuthorizationException;


/**
 * Abstract action for showing a view.
 */
public abstract class AbstractShowViewAction implements IWorkbenchWindowActionDelegate {

	private static final Logger LOG = Logger.getLogger(AbstractShowViewAction.class);
	private IWorkbenchWindow window;
	
	
	/**
	 * To be overloaded if needed.
	 */
	public void dispose() {
		// not used
		
	}

	@Override
	public void init(final IWorkbenchWindow window) {
		this.window = window;
	}

	@Override
	public void selectionChanged(final IAction action, final ISelection selection) {
		// not used
	}

	/**
	 * Open the view.
	 * 
	 * @param action the associated action
	 */
	public void run(final IAction action) {
		try {
			openView(getViewId());
		} catch (final PartInitException e) {
			LOG.warn("Error on opening a view with ID:" + getViewId(), e); //$NON-NLS-1$
		} catch (final EpAuthorizationException e) {
			LOG.info("Authorization failure - insufficient permissions to open view " + e.getMessage()); //$NON-NLS-1$
		}
	}
	
	/**
	 * Should return the view ID to be opened.
	 * @return String
	 */
	protected abstract String getViewId();
	
	
	/**
	 * Opens the view with the given identifier.
	 * 
	 * @param viewId
	 *            The view to open; must not be <code>null</code>
	 * @throws PartInitException
	 *             If the part could not be initialized.
	 */
	private void openView(final String viewId)
			throws PartInitException {

		if (window != null) {
			final IWorkbenchPage activePage = window.getActivePage();
			if (activePage != null) {
				activePage.showView(viewId);
			}
		}		
	}
	
	/**
	 * Returns associated window.
	 * 
	 * @return <code>IWorkbenchWindow</code>
	 */
	public IWorkbenchWindow getWindow() {
		return window;
	}

}
