/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.actions;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PerspectiveAdapter;
import org.eclipse.ui.PlatformUI;

/**
 * Display.getDefault().asyncExec(new HideActionSetRunnable("action.set.id").
 */
public class HideActionSetRunnable implements Runnable {

	private static final Logger LOG = Logger.getLogger(HideActionSetRunnable.class);
	private final Set<String> actionSetIds = new HashSet<>();
	private final HideCondition hideCondition;

	private IPerspectiveListener hideListener;

	/**
	 * Constructor.
	 * Action set will be hidden automatically when run method will be called.
	 * It will be always removed as no hideCondition was specified.
	 *
	 * @param actionSetIds ids of action sets that will be hidden from CoolBar
	 */
	public HideActionSetRunnable(final String... actionSetIds) {
		this.hideCondition = () -> true;
		this.actionSetIds.addAll(Arrays.asList(actionSetIds));
	}

	/**
	 * Constructor.
	 * HideCondition will be executed at later point.
	 * It is useful as some configuration which determine if actionSet should be hidden
	 * might have not been yet prepared when this class is constructed.
	 *
	 * @param hideCondition condition that is used as a check in run method
	 * @param actionSetIds  ids of action sets that will be hidden from CoolBar
	 */
	public HideActionSetRunnable(final HideCondition hideCondition, final String... actionSetIds) {
		this.hideCondition = hideCondition;
		this.actionSetIds.addAll(Arrays.asList(actionSetIds));
	}

	@Override
	public void run() {
		//Hide condition determines if Hiding of the action set should take place
		final IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

		if (workbenchWindow == null) {
			LOG.warn("Couldn't hide actions as workbenchWindow is not yet initialized");
		} else {

			if (hideListener == null) {
				hideListener = createListener();
			}

			boolean hideOrShow = hideCondition.evaluate();
			if (hideOrShow) {
				workbenchWindow.addPerspectiveListener(hideListener);
			} else {
				workbenchWindow.removePerspectiveListener(hideListener);
			}

			//Hide actions for the current perspective or show them
			hideOrShowActions(workbenchWindow, hideOrShow);

		}
	}

	private IPerspectiveListener createListener() {
		return new PerspectiveAdapter() {
			@Override
			public void perspectiveActivated(final IWorkbenchPage page, final IPerspectiveDescriptor perspective) {
				for (String actionSetId : actionSetIds) {
					page.hideActionSet(actionSetId);
				}
			}
		};
	}

	private void hideOrShowActions(final IWorkbenchWindow workbenchWindow, final boolean hideOrShow) {
		if (workbenchWindow.getActivePage() != null) {
			for (String actionSetId : actionSetIds) {
				if (hideOrShow) {
					workbenchWindow.getActivePage().hideActionSet(actionSetId);
				} //otherwise do nothing
			}
		}
	}

	/**
	 * Interface that is used by HideActionSetRunnable class.
	 * The return value of the evaluate method will either let HideActionSetRunnable to hide action sets or do nothing.
	 */
	public interface HideCondition {
		/**
		 * Evaluates the condition which determines whether the Hiding of Action Sets should take place.
		 * @return true if hiding of action set must happen
		 */
		boolean evaluate();
	}
}
