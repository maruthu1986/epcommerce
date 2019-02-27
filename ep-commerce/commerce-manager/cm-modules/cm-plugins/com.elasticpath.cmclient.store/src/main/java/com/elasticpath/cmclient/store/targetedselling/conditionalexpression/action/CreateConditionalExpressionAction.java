/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
/**
 * 
 */
package com.elasticpath.cmclient.store.targetedselling.conditionalexpression.action;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogLabelKeys;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.elasticpath.cmclient.core.wizard.EpWizardDialog;
import com.elasticpath.cmclient.store.targetedselling.conditionalexpression.editors.ConditionEditor;
import com.elasticpath.cmclient.store.targetedselling.conditionalexpression.editors.ConditionEditorInput;
import com.elasticpath.cmclient.store.targetedselling.conditionalexpression.views.ConditionalExpressionSearchResultsView;
import com.elasticpath.cmclient.store.targetedselling.conditionalexpression.wizard.NewConditionalExpressionWizard;
import com.elasticpath.tags.domain.ConditionalExpression;


/**
 * CreateConditionalExpressionAction.
 *
 */
public class CreateConditionalExpressionAction extends BaseConditionalExpressionAction {
	
	/** LOG logger. */
	private static final Logger LOG = Logger.getLogger(CreateConditionalExpressionAction.class);
	
	/**
	 * default constructor for create action.
	 * @param view the view
	 * @param text the text
	 * @param imageDescriptor the icon
	 */
	public CreateConditionalExpressionAction(
			final ConditionalExpressionSearchResultsView view, final String text, final ImageDescriptor imageDescriptor) {
		super(view, text, imageDescriptor);
	}
	
	@Override
	public void run() {
		
		final NewConditionalExpressionWizard wizard = new NewConditionalExpressionWizard();
		final WizardDialog dialog = new EpWizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), wizard)  {
			@Override
			protected Button createButton(final Composite parent, final int buttonId,
					final String label, final boolean defaultButton) {
				String newLabel = label;
				if (buttonId == IDialogConstants.FINISH_ID) {
					newLabel = JFaceResources.getString(IDialogLabelKeys.OK_LABEL_KEY);
				}
				return super.createButton(parent, buttonId, newLabel, defaultButton);
			}
		};
		dialog.setPageSize(NewConditionalExpressionWizard.DEFAULT_WIDTH, NewConditionalExpressionWizard.DEFAULT_HEIGHT);
		dialog.addPageChangingListener(wizard);
		
		if (dialog.open() == Window.OK) {
			final ConditionalExpression model = wizard.getModel();
			IEditorInput editorInput = new ConditionEditorInput(model);
			try {
				IEditorPart editorPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().
					getActivePage().openEditor(editorInput, ConditionEditor.ID_EDITOR);
				ConditionalExpressionEditorsCache.getInstance().addToCache(editorPart);
			} catch (PartInitException e) {
				LOG.error(e.getMessage(), e);
			}
		}
	}

	@Override
	public String getTargetIdentifier() {
		return "createConditionalExpression"; //$NON-NLS-1$
	}
	
}
