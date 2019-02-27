/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core;

import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.internal.WorkbenchWindow;

import com.elasticpath.cmclient.core.eventlistener.CoolbarEventManager;
import com.elasticpath.cmclient.core.eventlistener.CoolbarListener;


/**
 * The default applicationWorkbenchAdvisor.
 */
public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor implements CoolbarListener {

	private static final Point INITIAL_SIZE = new Point(1200, 700);

	/**
	 * Constructor.
	 *
	 * @param configurer the window configurer
	 */
	public ApplicationWorkbenchWindowAdvisor(final IWorkbenchWindowConfigurer configurer) {
		super(configurer);
	}

	@Override
	public ActionBarAdvisor createActionBarAdvisor(final IActionBarConfigurer configurer) {
		return new ApplicationActionBarAdvisor(configurer);
	}

	@Override
	public void preWindowOpen() {
		final IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		configurer.setInitialSize(INITIAL_SIZE);
		configurer.setShowCoolBar(true);
		configurer.setShellStyle(SWT.NO_TRIM);
		configurer.setShowMenuBar(false);
		configurer.setShowStatusLine(false);
		configurer.setShowProgressIndicator(false);
		CoolbarEventManager.getInstance().addListener(this);
	}

	@Override
	public void postWindowCreate() {
		Shell shell = getWindowConfigurer().getWindow().getShell();
		shell.setMaximized(true);

		ApplicationLockManager.getInstance().registerSession(RWT.getUISession());
		Display.getCurrent().setData(RWT.MNEMONIC_ACTIVATOR, "ALT");
	}

	@Override
	public void postWindowClose() {
		EpExitConfirmation.forceReload();
	}

	@Override
	public void updateRequested() {
		WorkbenchWindow window = (WorkbenchWindow) getWindowConfigurer().getWindow();
		ICoolBarManager coolBarManager = window.getCoolBarManager2();

		if (coolBarManager != null) {
			coolBarManager.update(true);
		}
	}
}
