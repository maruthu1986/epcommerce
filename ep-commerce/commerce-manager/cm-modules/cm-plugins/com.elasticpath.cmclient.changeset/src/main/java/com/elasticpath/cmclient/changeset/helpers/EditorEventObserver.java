/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.cmclient.changeset.helpers;

import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;

import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;

/**
 * An observer of part events that also distributes them as editor events.
 */
public class EditorEventObserver implements IPartListener {

	/**
	 * A condition for whether events should be distributed.
	 */
	public interface IEditorCondition {
		
		/**
		 * Checks whether the condition is met which is 
		 * a precondition for firing editor events.
		 * 
		 * @param editor the editor
		 * @return true if the editor condition is fulfilled
		 */
		boolean isConditionFulfilled(AbstractCmClientFormEditor editor);
	}

	/**
	 * Editor listener for open/close/activate/deactivate events.
	 */
	public interface IEditorListener {
		
		/**
		 * Editor activated event.
		 * 
		 * @param editor the editor
		 */
		void editorActivated(AbstractCmClientFormEditor editor);

		/**
		 * Editor opened event.
		 * 
		 * @param editor the editor
		 */
		void editorOpened(AbstractCmClientFormEditor editor);
		
		/**
		 * Editor deactivated event.
		 * 
		 * @param editor the editor
		 */
		void editorDeactivated(AbstractCmClientFormEditor editor);

		/**
		 * Editor closed event.
		 * 
		 * @param editor the editor
		 */
		void editorClosed(AbstractCmClientFormEditor editor);
	}

	private final IEditorListener editorListener;
	private final IEditorCondition[] conditions;
	
	/**
	 * Creates a new observer.
	 * 
	 * @param editorListener the editor listener
	 * @param conditions the editor condition
	 */
	public EditorEventObserver(final IEditorListener editorListener, final IEditorCondition... conditions) {
		this.editorListener = editorListener;
		this.conditions = conditions;
	}
	
	@Override
	public void partActivated(final IWorkbenchPart part) {
		if (isEditor(part)) {
			AbstractCmClientFormEditor editor = (AbstractCmClientFormEditor) part;
			if (isConditionFulfilled(editor)) {
				this.editorListener.editorActivated(editor);
			}
		}
	}

	private boolean isConditionFulfilled(final AbstractCmClientFormEditor editor) {
		for (IEditorCondition condition : conditions) {
			if (!condition.isConditionFulfilled(editor)) {
				return false;
			}
		}
		return true;
	}

	private boolean isEditor(final IWorkbenchPart part) {
		return part instanceof AbstractCmClientFormEditor;
	}
	
	@Override
	public void partBroughtToTop(final IWorkbenchPart part) {
		// not interested
	}

	@Override
	public void partClosed(final IWorkbenchPart part) {
		if (isEditor(part)) {
			AbstractCmClientFormEditor editor = (AbstractCmClientFormEditor) part;
			if (isConditionFulfilled(editor)) {
				this.editorListener.editorClosed(editor);
			}
		}
	}

	@Override
	public void partDeactivated(final IWorkbenchPart part) {
		if (isEditor(part)) {
			AbstractCmClientFormEditor editor = (AbstractCmClientFormEditor) part;
			if (isConditionFulfilled(editor)) {
				this.editorListener.editorDeactivated(editor);
			}
		}
	}

	@Override
	public void partOpened(final IWorkbenchPart part) {
		if (isEditor(part)) {
			AbstractCmClientFormEditor editor = (AbstractCmClientFormEditor) part;
			if (isConditionFulfilled(editor)) {
				this.editorListener.editorOpened(editor);
			}
		}
	}

}
