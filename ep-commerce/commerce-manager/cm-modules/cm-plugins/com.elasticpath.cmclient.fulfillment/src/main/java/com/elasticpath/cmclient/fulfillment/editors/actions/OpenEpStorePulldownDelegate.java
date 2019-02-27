/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.fulfillment.editors.actions;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowPulldownDelegate2;
import org.eclipse.ui.PlatformUI;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.store.StoreService;

/**
 * Gets all the stores and creates the pull down menu.
 */
public class OpenEpStorePulldownDelegate extends Action implements IWorkbenchWindowPulldownDelegate2 {

	private IWorkbenchWindow window;

	private Menu fMenu;

	private List<Store> storeList;

	/**
	 * Creates the menu for toolbar.
	 * 
	 * @param parent the control
	 * @return Menu
	 */
	@Override
	public Menu getMenu(final Control parent) {
		if (storeList.size() > 1) {
			setMenu(new Menu(parent));
			fillMenu(fMenu);
			return fMenu;
		}
		return fMenu;
	}

	/**
	 * Creates the menu for the menu bar.
	 * 
	 * @param parent the menu
	 * @return Menu the menu
	 */
	@Override
	public Menu getMenu(final Menu parent) {
		if (storeList.size() > 1) {
			setMenu(new Menu(parent));
			fillMenu(fMenu);
			return fMenu;
		}
		return fMenu;

	}

	private void setMenu(final Menu menu) {
		fMenu = menu;
	}

	/**
	 * Fills the drop-down menu with stores.
	 * 
	 * @param menu the menu to fill
	 */
	protected void fillMenu(final Menu menu) {

		for (Store store : storeList) {
			OpenEpBrowserContributionAction openBrowserAction = new OpenEpBrowserContributionAction(store, getWorkbenchPage());
			addActionToMenu(fMenu, openBrowserAction);
		}

	}

	/**
	 * Adds action to the menu.
	 * 
	 * @param parent the menu
	 * @param action the action
	 */
	protected void addActionToMenu(final Menu parent, final Action action) {
		ActionContributionItem item = new ActionContributionItem(action);
		item.fill(parent, -1);
	}

	/**
	 * Gets the active site.
	 * 
	 * @return IWorkbencPartSite the active stie
	 */
	private IWorkbenchPartSite getWorkbenchPage() {
		if (PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage() == null) {
			return window.getPages()[0].getActivePart().getSite();
		}

		return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart().getSite();
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
	 * @param window the IWorkbencWindow
	 */
	@Override
	public void init(final IWorkbenchWindow window) {
		this.window = window;
		storeList = ((StoreService) ServiceLocator.getService(ContextIdNames.STORE_SERVICE)).findAllCompleteStores();

	}

	/**
	 * Not used.
	 * 
	 * @param action the IAction
	 */
	@Override
	public void run(final IAction action) {

		if (storeList.size() == 1) {

			OpenEpBrowserContributionAction openBrowserAction = new OpenEpBrowserContributionAction(storeList.get(0), getWorkbenchPage());
			openBrowserAction.run();
		}
	}

	/**
	 * Not used.
	 * 
	 * @param action the IAction
	 * @param selection the ISelection
	 */
	@Override
	public void selectionChanged(final IAction action, final ISelection selection) {
		// do nothing

	}

}
