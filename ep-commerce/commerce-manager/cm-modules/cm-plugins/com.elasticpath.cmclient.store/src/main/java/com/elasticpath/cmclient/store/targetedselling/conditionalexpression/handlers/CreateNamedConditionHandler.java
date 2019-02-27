/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
/**
 * 
 */
package com.elasticpath.cmclient.store.targetedselling.conditionalexpression.handlers;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogLabelKeys;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.wizard.EpWizardDialog;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareHandler;
import com.elasticpath.cmclient.store.targetedselling.TargetedSellingMessages;
import com.elasticpath.cmclient.store.targetedselling.conditionalexpression.editors.ConditionEditor;
import com.elasticpath.cmclient.store.targetedselling.conditionalexpression.editors.ConditionEditorInput;
import com.elasticpath.cmclient.store.targetedselling.conditionalexpression.wizard.NewConditionalExpressionWizard;
import com.elasticpath.cmclient.store.targetedselling.handlers.CreateHandlerService;
import com.elasticpath.cmclient.store.targetedselling.handlers.serviceadapters.TagConditionCreateHandlerServiceAdapter;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.tags.domain.ConditionalExpression;

/**
 * Create Named Condition Handler.	 
 *
 */
public class CreateNamedConditionHandler extends AbstractPolicyAwareHandler {
	
	/** LOG logger. */
	private static final Logger LOG = Logger.getLogger(CreateNamedConditionHandler.class);
	
	private final PolicyActionContainer handlerContainer = addPolicyActionContainer("createConditionalExpressionHandler"); //$NON-NLS-1$	

	protected CreateHandlerService<ConditionalExpression> getService() {
		return new TagConditionCreateHandlerServiceAdapter(
				ServiceLocator.getService(ContextIdNames.TAG_CONDITION_SERVICE));
	}

	@Override
	public boolean isEnabled() {
		boolean enabled = true;
		if (getStatePolicy() != null) {
			enabled = (EpState.EDITABLE == getStatePolicy().determineState(handlerContainer));
		}
		return enabled;		
	}
	
	/**
	 * Performs save action on dialog.
	 *
	 * @param model - model to be saved 
	 */
	protected void save(final ConditionalExpression model) {
		IEditorInput editorInput = new ConditionEditorInput(model);
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().
				getActivePage().openEditor(editorInput, ConditionEditor.ID_EDITOR);
		} catch (PartInitException e) {
			LOG.error(e.getMessage(), e);
		}
	}
	
	/** 
	 * {@inheritDoc} 
	 * Just dialog size fix. 
	 */
	@Override
	public Object execute(final ExecutionEvent arg0) throws ExecutionException {
		final NewConditionalExpressionWizard wizard = new NewConditionalExpressionWizard();
		final WizardDialog dialog = new EpWizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), wizard) {
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
			
			if (getService().exists(wizard.getModel())) {

				dialog.setErrorMessage(TargetedSellingMessages.get().ConditionalExpressionNameExists);

			} else {

				save(wizard.getModel());

			}
		}
		return wizard.getModel();
	}

	@Override
	public String getTargetIdentifier() {
		return "createConditionalExpressionHandler"; //$NON-NLS-1$
	}	

}
