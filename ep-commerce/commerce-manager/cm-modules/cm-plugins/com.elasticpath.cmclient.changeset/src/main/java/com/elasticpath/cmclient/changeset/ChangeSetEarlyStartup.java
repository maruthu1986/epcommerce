/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.changeset;

import java.util.Collection;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.elasticpath.cmclient.changeset.helpers.ChangeSetsEnabledCondition;
import com.elasticpath.cmclient.changeset.helpers.DialogSupport;
import com.elasticpath.cmclient.changeset.helpers.EditorEventObserver;
import com.elasticpath.cmclient.changeset.helpers.EditorSupport;
import com.elasticpath.cmclient.changeset.helpers.SupportedEditorCondition;
import com.elasticpath.cmclient.changeset.support.SupportedComponent;
import com.elasticpath.cmclient.changeset.support.SupportedComponentsExtPoint;
import com.elasticpath.cmclient.core.registry.ObjectRegistry;

/**
 * Early startup for the following plugin.
 * Class referenced by startup extension in plugin.xml.
 *
 * @since 7.0
 */
public class ChangeSetEarlyStartup implements IStartup {

	@Override
	public void earlyStartup() {

		if (PlatformUI.isWorkbenchRunning()) {
			this.addPartListenerToActiveWorkbenchWindow();

			// dialogs support
			ObjectRegistry.getInstance().addObjectListener(new DialogSupport());
		}
	}

	private void addPartListenerToActiveWorkbenchWindow() {
		// editors support
		SupportedComponentsExtPoint supportedEditorsExtPoint = new SupportedComponentsExtPoint();

		Collection<SupportedComponent> supportedEditors = supportedEditorsExtPoint.getSupportedComponents();
		EditorEventObserver.IEditorListener editorSupport = new EditorSupport();
		EditorEventObserver.IEditorCondition supportedEditorCondition = new SupportedEditorCondition(supportedEditors);
		EditorEventObserver.IEditorCondition changeSetsEnabledCondition = new ChangeSetsEnabledCondition();
		Display.getDefault().asyncExec(() -> {
			IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			if (activeWorkbenchWindow != null) {
				IPartService partService = activeWorkbenchWindow.getPartService();
				if (partService != null) {
					partService.addPartListener(new EditorEventObserver(editorSupport, supportedEditorCondition, changeSetsEnabledCondition));
				}
			}
		});

		//Pass loaded Editors to ChangeSetSupportedEditors instance
		ChangeSetSupportedEditors.getDefault().setSupportedEditorsExtPoint(supportedEditorsExtPoint);
	}
}

