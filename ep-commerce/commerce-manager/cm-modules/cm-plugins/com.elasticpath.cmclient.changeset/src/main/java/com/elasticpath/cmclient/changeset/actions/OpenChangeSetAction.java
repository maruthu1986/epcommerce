/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.changeset.actions;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;

import com.elasticpath.cmclient.changeset.editors.ChangeSetEditor;
import com.elasticpath.cmclient.core.editors.GuidEditorInput;
import com.elasticpath.domain.changeset.ChangeSet;

/**
 * The action to open a change set editor.
 */
public class OpenChangeSetAction extends Action {

	private static final Logger LOG = Logger.getLogger(OpenChangeSetAction.class);
	
	private final TableViewer viewer;
	private final IWorkbenchPartSite workbenchPartSite;

	/**
	 * Constructor.
	 * 
	 * @param viewer the view to be used
	 * @param workbenchPartSite the workbench site to be used
	 * @param imageDescriptor the image descriptor
	 * @param name the action's name
	 */
	public OpenChangeSetAction(final TableViewer viewer, final IWorkbenchPartSite workbenchPartSite, 
			final String name, final ImageDescriptor imageDescriptor) {
		super(name, imageDescriptor);
		this.viewer = viewer;
		this.workbenchPartSite = workbenchPartSite;
	}
	
	@Override
	public void run() {
		final ISelection selection = this.viewer.getSelection();
		final Object obj = ((IStructuredSelection) selection).getFirstElement();

		if (obj instanceof ChangeSet) {
			final ChangeSet changeSet = (ChangeSet) obj;
			final GuidEditorInput editorInput = new GuidEditorInput(changeSet.getGuid(), ChangeSet.class);

			try {
				this.workbenchPartSite.getPage().openEditor(editorInput, ChangeSetEditor.ID_EDITOR);
			} catch (final PartInitException e) {
				LOG.error("Cannot open change set editor", e); //$NON-NLS-1$
			}

		}
	}
}
