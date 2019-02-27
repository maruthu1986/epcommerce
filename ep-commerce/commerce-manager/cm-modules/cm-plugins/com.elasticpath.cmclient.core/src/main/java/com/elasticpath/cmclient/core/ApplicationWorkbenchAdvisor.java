/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core;


import java.io.IOException;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.rap.rwt.internal.lifecycle.UITestUtil;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.internal.WorkbenchWindow;

import com.elasticpath.cmclient.core.handlers.ApplicationErrorHandler;
import com.elasticpath.cmclient.core.helpers.TestIdMapManager;
import com.elasticpath.cmclient.core.helpers.EPTestUtilFactory;
import com.elasticpath.cmclient.core.helpers.TestIdUtil;
import com.elasticpath.cmclient.core.presentation.PerspectiveManager;
import com.elasticpath.cmclient.core.presentation.PerspectiveService;
import com.elasticpath.cmclient.core.ui.PerspectiveUtil;

/**
 * The application workbench advisor.
 */
public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

	/**
	 * The extension's permissions element.
	 */
	private static final Logger LOG = Logger.getLogger(ApplicationWorkbenchAdvisor.class);
	private WorkbenchWindow window;
	private ICoolBarManager coolBarManager;
	private PerspectiveService perspectiveService;

	@Override
	public String getInitialWindowPerspectiveId() {
		/**
		 * Dummy perspective is opened by default.
		 * The real perspective is opened at `this.postStartup()` method.
		 * This event is deferred so that ContributionItems will be loaded in the order
		 * described by toolbar extension points.
		 */
		return PerspectiveUtil.EMPTY_PERSPECTIVE_ID;
	}

	@Override
	public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(final IWorkbenchWindowConfigurer configurer) {
		this.window = (WorkbenchWindow) configurer.getWindow();
		this.coolBarManager = window.getCoolBarManager2();
		if (coolBarManager instanceof PerspectiveService) {
			perspectiveService = (PerspectiveService) coolBarManager;
		}

		configurer.getWindow().addPerspectiveListener(new IPerspectiveListener() {
			/**
			 * Prompt user to save all open editors on switching to admin perspective.
			 *
			 * @param page the workbench page
			 * @param perspective the perspective we switch to
			 */
			public void perspectiveActivated(final IWorkbenchPage page, final IPerspectiveDescriptor perspective) {
				if (perspective.getId().equals(PerspectiveUtil.ADMIN_PERSPECTIVE_ID)) {
					page.saveAllEditors(true);
				}
			}

			public void perspectiveChanged(final IWorkbenchPage page, final IPerspectiveDescriptor perspective, final String changeId) {
				//do nothing
			}
		});
		return new ApplicationWorkbenchWindowAdvisor(configurer);
	}

	@Override
	public void eventLoopException(final Throwable exception) {

		try {
			ApplicationErrorHandler.createErrorDialogForException(exception);
			LOG.error("Event Loop Exception", exception); //$NON-NLS-1$
		} catch (Exception e) {
			// One of the log listeners probably failed. Core should have logged the
			// exception since its the first listener.
			LOG.error("Exception while reporting an event loop exception", e); //$NON-NLS-1$
		}
	}

	@Override
	public void preStartup() {
		CorePlugin.runPreStartupCallbacks();
	}

	@Override
	public void postStartup() {
		CorePlugin.runPostStartupCallbacks();

		if (UITestUtil.isEnabled()) {
			TestIdUtil testIdUtil = EPTestUtilFactory.getInstance().getTestIdUtil();

			try {
				testIdUtil.initialize();
			} catch (IOException exception) {
				LOG.warn("Could not instantiate testIdUtils", exception);
			}
			testIdUtil.sendTestIdMapsToClient(TestIdMapManager.getMinifiedMap());
		}

		PerspectiveManager.getDefault().registerPerspectiveListener(window, perspectiveService);
		coolBarManager.update(true);
		deactivateUnusedWorkbenchCommands();
		EPTestUtilFactory.getInstance().getTestIdUtil().setPostLoginWindowId();

		//Opens the preferred perspective which is described by the PerspectiveService
		PerspectiveManager.getDefault().openPerspective(perspectiveService);
	}

	/*
	 * This method is sort of a hack to disable some default RCP workbench commands.
	 */
	private void deactivateUnusedWorkbenchCommands() {
		final ICommandService commandService = PlatformUI.getWorkbench().getService(ICommandService.class);
		// Disable eclipse workbench open search dialog. For more information see RUMBA-332
		commandService.getCommand("org.eclipse.search.ui.openSearchDialog").undefine(); //$NON-NLS-1$

		// Disable some other commands. For more information see RUMBA-333
		commandService.getCommand("org.eclipse.ui.window.quickAccess").undefine(); //$NON-NLS-1$
		commandService.getCommand("org.eclipse.ui.newWizard").undefine(); //$NON-NLS-1$
		commandService.getCommand("org.eclipse.ui.navigate.openResource").undefine(); //$NON-NLS-1$
		commandService.getCommand("org.eclipse.ui.views.showView").undefine(); //$NON-NLS-1$
		commandService.getCommand("org.eclipse.ui.window.showKeyAssist").undefine(); //$NON-NLS-1$
		commandService.getCommand(IWorkbenchCommandConstants.WINDOW_NEW_EDITOR).setHandler(null);
	}
}
