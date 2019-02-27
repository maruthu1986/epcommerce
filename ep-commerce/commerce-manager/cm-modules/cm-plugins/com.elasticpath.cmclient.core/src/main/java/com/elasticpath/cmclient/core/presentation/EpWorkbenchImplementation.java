/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.presentation;

import org.eclipse.ui.internal.WorkbenchWindow;
import org.eclipse.ui.internal.tweaklets.Workbench3xImplementation;

/**
 *	This class is used to override workbench window.
 *
 * 	Styling of the Coolbar which using WindowComposer and Stack presentations as recommended in RAP Look and feel
 * 	creates a whole bunch of issues. Replacement of the coolbar using recommended approach requires creating your
 * 	own Window Composer and StackPresentation that will layout Views and Editors through PresentationFactory extension point
 *
 * 	EpWorkbenchWindow provides just the right solution it substitutes the Coolbar implementation and at the same time uses
 * 	WorkbenchPresentationFactory (different from PresentationFactory) which relies on TabbedStackPresentation.
 *
 * 	Implementing your own StackPresentation means going away from Workbench nature.
 * 	NOTE: Extending StackPresentation and creating a delegate such as TabbedStackPresentation will not work either.
 * 		ConfigurableStack creates dummy IStackPresentationSite which removes communication between Workbench and StackPresentation,
 * 		which again means creating your own StackPresentation from scratch and not using IStackPresentationSite in it.
 */
public class EpWorkbenchImplementation extends Workbench3xImplementation {

	@Override
	public WorkbenchWindow createWorkbenchWindow(final int newWindowNumber) {
		return new EpWorkbenchWindow(newWindowNumber);
	}
}
