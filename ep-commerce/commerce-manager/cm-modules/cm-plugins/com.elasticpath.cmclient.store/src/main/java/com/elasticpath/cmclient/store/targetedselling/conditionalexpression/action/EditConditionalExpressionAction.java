/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
/**
 * 
 */
package com.elasticpath.cmclient.store.targetedselling.conditionalexpression.action;

import org.apache.log4j.Logger;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.elasticpath.cmclient.core.editors.GuidEditorInput;
import com.elasticpath.cmclient.store.targetedselling.conditionalexpression.editors.ConditionEditor;
import com.elasticpath.cmclient.store.targetedselling.conditionalexpression.views.ConditionalExpressionSearchResultsView;
import com.elasticpath.tags.domain.ConditionalExpression;


/**
 * EditConditionalExpressionAction.
 *
 */
public class EditConditionalExpressionAction extends BaseConditionalExpressionAction {

	/** The logger. */
	private static final Logger LOG = Logger.getLogger(EditConditionalExpressionAction.class);
	
	private final ConditionalExpressionSearchResultsView view;
	
	/**
	 * default constructor for Edit action.
	 * @param view the view
	 * @param text the text
	 * @param imageDescriptor the icon
	 */
	public EditConditionalExpressionAction(
			final ConditionalExpressionSearchResultsView view, final String text, final ImageDescriptor imageDescriptor) {
		super(view, text, imageDescriptor); 
		this.view = view;
	}

	@Override
	public void run() {
		LOG.debug("EditConditionalExpressionAction called."); //$NON-NLS-1$
		ConditionalExpression expression = view.getSelectedItem();
			IEditorInput editorInput = new GuidEditorInput(expression.getGuid(), ConditionalExpression.class);
			try {
				IEditorPart editorPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().
				getActivePage().openEditor(editorInput, ConditionEditor.ID_EDITOR);
			ConditionalExpressionEditorsCache.getInstance().addToCache(editorPart);
			} catch (PartInitException e) {
				LOG.error(e.getMessage(), e);
			}
	}

	@Override
	public String getTargetIdentifier() {
		return "editConditionalExpression"; //$NON-NLS-1$
	}
}
