/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.changeset.helpers;

import org.apache.log4j.Logger;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PartInitException;

import com.elasticpath.cmclient.changeset.ChangeSetImageRegistry;
import com.elasticpath.cmclient.changeset.editors.support.ChangeSetInfoEditorPage;
import com.elasticpath.cmclient.changeset.helpers.EditorEventObserver.IEditorListener;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.changeset.ChangeSetObjectStatus;
import com.elasticpath.service.changeset.ChangeSetService;

/**
 * An editor observer for decorating or modifying an editor.
 */
public class EditorSupport implements IEditorListener {

	private static final Logger LOG = Logger.getLogger(EditorSupport.class);
	private final ChangeSetService changeSetService;
	
	
	/**
	 * Constructor.
	 */
	public EditorSupport() {
		this.changeSetService = ServiceLocator.getService(ContextIdNames.CHANGESET_SERVICE);
	}

	@Override
	public void editorActivated(final AbstractCmClientFormEditor editor) {
		// Nothing
	}

	@Override
	public void editorClosed(final AbstractCmClientFormEditor editor) {
		// Nothing	
	}

	@Override
	public void editorDeactivated(final AbstractCmClientFormEditor workbenchPart) {
		// Nothing		
	}

	@Override
	public void editorOpened(final AbstractCmClientFormEditor formEditor) {
		
		// adds the change set info page
		addChangeSetInfoPage(formEditor);

		// decorates the title image of the editor
		decorateEditorImageIfLocked(formEditor, changeSetService);
	}

	/**
	 * Adds a form page to the given editor if the editor is supported.
	 * 
	 * @param formEditor the form editor
	 */
	protected void addChangeSetInfoPage(final AbstractCmClientFormEditor formEditor) {
		try {
			formEditor.addPage(new ChangeSetInfoEditorPage(formEditor));
		} catch (PartInitException exc) {
			LOG.error("Could not add the change set info page to editor", exc); //$NON-NLS-1$
		}
	}

	/**
	 * Changes the editor image depending on the change set status of the object.
	 * Decorates an editor image with a lock icon.
	 * 
	 * @param formEditor the form editor to decorate
	 * @param changeSetService change set service that identifies if editor is locked
	 */
	public static void decorateEditorImageIfLocked(final AbstractCmClientFormEditor formEditor, final ChangeSetService changeSetService) {
		Object model = formEditor.getDependentObject();
		ChangeSetObjectStatus status = changeSetService.getStatus(model);

		if (status.isLocked()) {
			Image decoratedImage = ChangeSetImageRegistry.decorateImage(formEditor.getTitleImage(), ChangeSetImageRegistry.CHANGESET_LOCK_DECORATOR);
			formEditor.setEditorTitleImage(decoratedImage);
		}
	}

}
