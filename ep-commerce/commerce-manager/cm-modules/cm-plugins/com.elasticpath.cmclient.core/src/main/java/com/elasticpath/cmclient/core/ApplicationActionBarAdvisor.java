/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core;

import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.ToolBarContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

/**
 * Adds actions to the menu bar and toolbar.
 */
public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

	private static final String TOOLBAR_ID = "save"; //$NON-NLS-1$

	private IWorkbenchAction saveAction;

	private IWorkbenchAction saveAllAction;

	private IWorkbenchAction reloadEditorAction;

	/**
	 * Constructor.
	 *
	 * @param configurer an <code>IActionBarConfigurer</code>
	 */
	public ApplicationActionBarAdvisor(final IActionBarConfigurer configurer) {
		super(configurer);
	}

	@Override
	protected void makeActions(final IWorkbenchWindow window) {
		// Creates the actions and registers them.
		// Registering is needed to ensure that key bindings work.
		// The corresponding commands' keybindings are defined in the plugin.xml file.
		// Registering also provides automatic disposal of the actions when the window is closed.

		// TODO: Can we pull these strings from a default nl1? i.e. org.eclipse.ui.workbench.nl1
		IWorkbenchAction logoutAction = ActionFactory.QUIT.create(window);
		logoutAction.setText(CoreMessages.get().ApplicationActionBarAdvisor_FileMenu_Logout);
		register(logoutAction);

		saveAction = ActionFactory.SAVE.create(window);
		saveAction.setImageDescriptor(CoreImageRegistry.IMAGE_SAVE_ACTIVE_LARGE);
		saveAction.setDisabledImageDescriptor(CoreImageRegistry.IMAGE_SAVE_LARGE);
		register(saveAction);

		saveAllAction = ActionFactory.SAVE_ALL.create(window);
		saveAllAction.setImageDescriptor(CoreImageRegistry.IMAGE_SAVE_ALL_ACTIVE_LARGE);
		saveAllAction.setDisabledImageDescriptor(CoreImageRegistry.IMAGE_SAVE_ALL_LARGE);
		register(saveAllAction);

		reloadEditorAction = ActionFactory.REFRESH.create(window);
		reloadEditorAction.setText(CoreMessages.get().RefreshAction_Name);
		reloadEditorAction.setToolTipText(CoreMessages.get().RefreshAction_Tooltip);
		reloadEditorAction.setImageDescriptor(CoreImageRegistry.IMAGE_REFRESH_ACTIVE_LARGE);
		reloadEditorAction.setDisabledImageDescriptor(CoreImageRegistry.IMAGE_REFRESH_LARGE);
		register(reloadEditorAction);

		register(ActionFactory.UNDO.create(window));
		register(ActionFactory.REDO.create(window));
		register(ActionFactory.CUT.create(window));
		register(ActionFactory.COPY.create(window));
		register(ActionFactory.PASTE.create(window));
		register(ActionFactory.DELETE.create(window));
		register(ActionFactory.SELECT_ALL.create(window));

		IWorkbenchAction fileRight = ActionFactory.PREFERENCES.create(window);
		fileRight.setImageDescriptor(CoreImageRegistry.IMAGE_USER);

		register(fileRight);
	}

	@Override
	protected void fillCoolBar(final ICoolBarManager coolBar) {
		final IToolBarManager toolbar = new ToolBarManager(SWT.FLAT | SWT.RIGHT);
		coolBar.add(new ToolBarContributionItem(toolbar, TOOLBAR_ID));
		toolbar.add(saveAction);
		toolbar.add(saveAllAction);
		toolbar.add(reloadEditorAction);
	}
}
