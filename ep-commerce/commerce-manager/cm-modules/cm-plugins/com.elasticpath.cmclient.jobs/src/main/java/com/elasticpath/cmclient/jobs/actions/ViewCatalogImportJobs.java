/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.jobs.actions;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;

import com.elasticpath.cmclient.jobs.views.CatalogJobListView;

/**
 * View catalog import jobs action.
 */
public class ViewCatalogImportJobs extends Action implements IWorkbenchWindowActionDelegate {
	
	private static final Logger LOG = Logger.getLogger(ViewCatalogImportJobs.class); 

	private IWorkbenchWindow window;

	private Menu fMenu;

	private void setMenu(final Menu menu) {
		fMenu = menu;
	}

	/**
	 * Gets the menu for toolbar and menubar, calls setMenu.
	 * 
	 * @param parent the control
	 * @return Menu
	 */
	public Menu getMenu(final Control parent) {
		setMenu(new Menu(parent));
		return fMenu;
	}

	/**
	 * Dispose the menu.
	 */
	public void dispose() {
		setMenu(null);

	}

	/**
	 * Initialize the pull down.
	 * 
	 * @param window the IWorkbencWindow
	 */
	public void init(final IWorkbenchWindow window) {
		this.window = window;

	}

	/**
	 * Invoked when menubar item or toolbar item is clicked.
	 * 
	 * @param action the action assigned to the item
	 */
	public void run(final IAction action) {
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(CatalogJobListView.VIEW_ID);
		} catch (Exception e) {
			LOG.debug(e.getMessage());
		}
	}

	/**
	 * Not implemented.
	 * 
	 * @param action the action assigned to the item
	 * @param selection that is selected
	 */
	public void selectionChanged(final IAction action, final ISelection selection) {
		// nothing

	}

	/**
	 * Gets the work bench window.
	 * 
	 * @return IWorkbenchWindow the window
	 */
	public IWorkbenchWindow getWindow() {
		return window;
	}

	/**
	 * Sets the work bench window.
	 * 
	 * @param window the IWorkbenchWindow
	 */
	public void setWindow(final IWorkbenchWindow window) {
		this.window = window;
	}
}
