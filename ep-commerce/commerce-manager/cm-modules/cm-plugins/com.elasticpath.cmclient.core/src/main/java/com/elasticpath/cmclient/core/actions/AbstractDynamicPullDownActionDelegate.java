/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowPulldownDelegate2;
import org.eclipse.ui.actions.ActionDelegate;

import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.cmclient.core.common.IRefreshableObject;
import com.elasticpath.cmclient.core.util.PullDownDelegateUtil;

/**
 * A base implementation of a drop down action delegate that handles menu objects to be
 * displayed as action items.
 * @param <ACTION> the type of menu objects used inside the drop down list
 * @param <PERSISTABLE> the persistable object to manage
 */
public abstract class AbstractDynamicPullDownActionDelegate<ACTION extends AbstractDynamicPullDownAction, PERSISTABLE extends Persistable>
		extends ActionDelegate
		implements IWorkbenchWindowPulldownDelegate2, IRefreshableObject {

	private List<ACTION> pullDownActionList;
	private MenuManager menuManager;
	private IAction action;

	/**
	 * Creates new menu for control.
	 * @param parent the parent control
	 * @return the populated menu
	 */
	@Override
	public Menu getMenu(final Control parent) {
		if (!isEnabled()) {
			return null;
		}
		if (menuManager.getMenu() == null) {
			menuManager.createContextMenu(parent);
		}
		return menuManager.getMenu();
	}

	/**
	 * Creates a menu with items.
	 * @param parent eclipse menu object
	 * @return populated menu
	 */
	@Override
	public Menu getMenu(final Menu parent) {
		if (!isEnabled()) {
			return null;
		}
		return menuManager.getMenu();
	}

	/**
	 * Initializes the action delegate.
	 * @param workbenchWindow the work bench window
	 */
	@Override
	public final void init(final IWorkbenchWindow workbenchWindow) {
		preInitialize(workbenchWindow);
		menuManager = new MenuManager();
		refresh();
	}

	@Override
	public void run(final IAction action) {
		action.setEnabled(isEnabled());
	}

	@Override
	public void init(final IAction action) {
		this.action = action;
		action.setEnabled(isEnabled());
	}

	@Override
	public void refresh() {
		createOrUpdatePullDownActionList();

		removeStaleItems();

		addNewlyCreatedItems();
	}

	private List<ACTION> createOrUpdatePullDownActionList() {
		pullDownActionList = new ArrayList<>();
		for (PERSISTABLE menuObject : getAvailableMenuObjects()) {
			ACTION pullDownAction = createPullDownAction(menuObject);
			pullDownActionList.add(pullDownAction);
		}
		return pullDownActionList;
	}

	/**
	 * remove items from the menu manager (items that have been removed after the menu manager was created).
	 */
	private void removeStaleItems() {
		for (IContributionItem item : menuManager.getItems()) {
			ActionContributionItem actionContributionItem = (ActionContributionItem) item;
			AbstractDynamicPullDownAction abstractDynamicPullDownAction = (AbstractDynamicPullDownAction) actionContributionItem.getAction();
			if (!pullDownActionList.contains(abstractDynamicPullDownAction)) {
				menuManager.remove(item);
			}
		}
	}

	/**
	 * add menus that have been added (items that have been added after the menu manager was created).
	 */
	private void addNewlyCreatedItems() {
		for (ACTION menuObject : pullDownActionList) {
			if (!menuManagerHasAction(menuObject)) {
				menuManager.add(menuObject);
			}
		}
	}

	private boolean menuManagerHasAction(final ACTION menuObject) {
		for (IContributionItem item : menuManager.getItems()) {
			ActionContributionItem actionContributionItem = (ActionContributionItem) item;
			AbstractDynamicPullDownAction abstractDynamicPullDownAction = (AbstractDynamicPullDownAction) actionContributionItem.getAction();
			if (abstractDynamicPullDownAction.equals(menuObject)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public void runWithEvent(final IAction action, final Event event) {
		PullDownDelegateUtil.runWithEvent(event);
	}

	/**
	 * Gets the underlying action behind the action delegate.
	 *
	 * @return an instance of IAction
	 */
	protected IAction getAction() {
		return this.action;
	}

	/**
	 * A hook method for extension to be used for extending class.
	 * @param workbenchWindow the work bench window
	 */
	protected void preInitialize(final IWorkbenchWindow workbenchWindow) {
		// does nothing by default
	}

	/**
	 * returns an Action that contains the given persistable object.
	 * @param menuObject the menu object
	 * @return a pull down action
	 * */
	protected abstract ACTION createPullDownAction(PERSISTABLE menuObject);

	/**
	 * returns the active menu object at the time.
	 * @return the active menu object
	 */
	protected abstract PERSISTABLE getActiveMenuObject();

	/**
	 * gets all available menu objects to be displayed.
	 * @return a collection of menu objects
	 */
	protected abstract Collection<PERSISTABLE> getAvailableMenuObjects();

	/**
	 * Returns the state of this delegate action.
	 *
	 * @return true if it should be enabled
	 */
	protected abstract boolean isEnabled();

}
