/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
/**
 * 
 */
package com.elasticpath.cmclient.fulfillment.editors.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowPulldownDelegate;
import org.eclipse.ui.PlatformUI;

import com.elasticpath.cmclient.fulfillment.editors.order.dialog.StoreSelectionDialog;
import com.elasticpath.domain.store.Store;

/**
 * Action that opens up the ElasticPath store selection dialog.
 * 
 */
public class OpenEpStoreSelectionContributionAction extends Action implements
		IWorkbenchWindowPulldownDelegate {

	private IWorkbenchWindow window;

	private Menu fMenu;

	private void setMenu(final Menu menu) {
		fMenu = menu;
	}

	/**
	 * Gets the menu for toolbar and menubar, calls setMenu.
	 * 
	 * @param parent
	 *            the control
	 * @return Menu
	 */
	@Override
	public Menu getMenu(final Control parent) {
		setMenu(new Menu(parent));
		return fMenu;
	}

	/**
	 * Dispose the menu.
	 */
	@Override
	public void dispose() {
		setMenu(null);

	}

	/**
	 * Initialize the pull down.
	 * 
	 * @param window
	 *            the IWorkbencWindow
	 */
	@Override
	public void init(final IWorkbenchWindow window) {
		this.window = window;

	}

	/**
	 * Invoked when menubar item or toolbar item is clicked.
	 * 
	 * @param action
	 *            the action assigned to the item
	 */
	@Override
	public void run(final IAction action) {
		StoreSelectionDialog storeSelectionDialog = new StoreSelectionDialog(
				PlatformUI.getWorkbench().getDisplay().getActiveShell());
		int result = storeSelectionDialog.open();
		if (result == Window.OK) {
			Store selectedStore = storeSelectionDialog.getSelectedStore();
			if (selectedStore == null) {
				return;
			}
			OpenEpBrowserContributionAction openBrowserAction = new OpenEpBrowserContributionAction(
				storeSelectionDialog.getSelectedStore(), PlatformUI
				.getWorkbench().getActiveWorkbenchWindow()
				.getActivePage().getActivePart().getSite());
			openBrowserAction.run();
		}
	}

	/**
	 * Not implemented.
	 * 
	 * @param action
	 *            the action assigned to the item
	 * @param selection
	 *            that is selected
	 */
	@Override
	public void selectionChanged(final IAction action,
								 final ISelection selection) {
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
	 * @param window
	 *            the IWorkbenchWindow
	 */
	public void setWindow(final IWorkbenchWindow window) {
		this.window = window;
	}

}
