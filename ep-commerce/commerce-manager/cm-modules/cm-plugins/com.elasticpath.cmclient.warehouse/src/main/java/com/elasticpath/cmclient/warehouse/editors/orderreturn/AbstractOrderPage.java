/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.warehouse.editors.orderreturn;

import org.eclipse.jface.action.IToolBarManager;

import com.elasticpath.cmclient.core.editors.AbstractCmClientEditorPage;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;

/**
 * The abstract order editor page, provide the unified action bar functionality.
 */
public abstract class AbstractOrderPage extends AbstractCmClientEditorPage { //NOPMD
	private final AbstractCmClientFormEditor editor;

	/**
	 * Constructs the editor page.
	 * 
	 * @param editor The EP FormEditor
	 * @param partId the id for the editor page
	 * @param title the title of the editor page
	 */
	public AbstractOrderPage(final AbstractCmClientFormEditor editor, final String partId, final String title) {
		super(editor, partId, title);

		this.editor = editor;
	}

	@Override
	protected void addToolbarActions(final IToolBarManager toolBarManager) {
		//No buttons or anything else to add in the toolbar.
	}

	/**
	 * Gets the editor.
	 *
	 * @return AbstractCmClientFormEditor the editor
	 */
	@Override
	public AbstractCmClientFormEditor getEditor() {
		return editor;
	}
}