/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core.editors;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartConstants;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.EditorActionBarContributor;

/**
 * A contributor that provides default actions for children of AbstractCmClientFormEditor such as
 * the Reload action.
 */
public class BaseCmClientEditorContributor extends EditorActionBarContributor {

	private IEditorPart activeEditor;

	// Used to deregister listeners when a new editor becomes active.
	private IEditorPart previousActiveEditor;


	/**
	 * Implement editor reload action - to be tied to the global toolbar refresh action.
	 */
	private final IAction reloadAction = new Action("reload") {

		public void run() {
			AbstractCmClientFormEditor editor = ((AbstractCmClientFormEditor) activeEditor);

			// Don't refresh dirty editors - we'd lose those changes.
			if (!editor.isDirty()) {
				editor.reloadModel();
				editor.refreshEditorPages();
			}
		}
	};

	private final IPropertyListener dirtyEditorListener = new IPropertyListener() {

		@Override
		public void propertyChanged(final Object source, final int propId) {
			if (propId == IWorkbenchPartConstants.PROP_DIRTY) {
				reloadAction.setEnabled(!((AbstractCmClientFormEditor) source).isDirty());
			}
		}
	};



	@Override
	public void init(final IActionBars bars, final IWorkbenchPage page) {
		super.init(bars, page);

		// Hook our reload action into the global workbench toolbar
		getActionBars().setGlobalActionHandler(ActionFactory.REFRESH.getId(), reloadAction);
	}

	@Override
	public void setActiveEditor(final IEditorPart targetEditor) {
		activeEditor = targetEditor;

		reloadActionTargetChanged(previousActiveEditor, activeEditor);

		previousActiveEditor = activeEditor;  // remember for clean up next time
	}

	private void reloadActionTargetChanged(final IEditorPart previousEditor, final IEditorPart newlyActiveEditor) {
		if (previousEditor != null) {
			previousEditor.removePropertyListener(dirtyEditorListener);
		}

		reloadAction.setEnabled(!newlyActiveEditor.isDirty());  // Set initial action state form editor state
		newlyActiveEditor.addPropertyListener(dirtyEditorListener);   // Respond to a change of editor state
	}

}