/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.changeset.editors;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.forms.IManagedForm;

import com.elasticpath.cmclient.changeset.ChangeSetMessages;
import com.elasticpath.cmclient.changeset.ChangeSetPlugin;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareEditorPage;

/**
 * Represents the change set editor objects page.
 */
public class ChangeSetEditorObjectsPage extends AbstractPolicyAwareEditorPage {

	/**
	 * This page ID.
	 */
	private static final String OBJECTS_PAGE_ID = "ObjectsPage"; //$NON-NLS-1$

	/**
	 * Constructs a new page.
	 * 
	 * @param editor the cm client editor
	 */
	public ChangeSetEditorObjectsPage(final AbstractCmClientFormEditor editor) {
		super(editor, OBJECTS_PAGE_ID, ChangeSetMessages.get().ChangeSetEditor_ObjectsPageTitle);
	}

	@Override
	protected void addEditorSections(final AbstractCmClientFormEditor editor, final IManagedForm managedForm) {
		ChangeSetEditorObjectsSection objectsSection = new ChangeSetEditorObjectsSection(this, editor);

		PolicyActionContainer container = addPolicyActionContainer("objectsPage"); //$NON-NLS-1$
		addPart(container, managedForm, objectsSection);
		addExtensionEditorSections(editor, managedForm, ChangeSetPlugin.PLUGIN_ID, this.getClass().getSimpleName());
	}

	@Override
	protected void addToolbarActions(final IToolBarManager toolBarManager) {
		// no toolbar actions to add
	}

	@Override
	protected int getFormColumnsCount() {
		return 1;
	}

	@Override
	protected String getFormTitle() {
		return ChangeSetMessages.get().ChangeSetEditor_ObjectsFormTitle;
	}
}
