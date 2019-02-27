/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.store.targetedselling.conditionalexpression.editors;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.elasticpath.cmclient.conditionbuilder.wizard.conditions.handlers.ConditionHandler;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.editors.CancelSaveException;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareEditorPageSectionPart;
import com.elasticpath.cmclient.policy.ui.IPolicyTargetLayoutComposite;
import com.elasticpath.cmclient.policy.ui.PolicyTargetCompositeFactory;
import com.elasticpath.cmclient.store.targetedselling.TargetedSellingMessages;
import com.elasticpath.tags.domain.ConditionalExpression;
import com.elasticpath.tags.domain.LogicalOperator;

/**
 * AbstractConditionBuilderSection
 * base class for condition builder sections.
 */
@SuppressWarnings("PMD.AbstractClassWithoutAbstractMethod")
public abstract class AbstractConditionBuilderSection extends AbstractPolicyAwareEditorPageSectionPart {
	
	private static final Logger LOG = Logger.getLogger(AbstractConditionBuilderSection.class);
	
	private final ConditionHandler conditionHandler = new ConditionHandler();

	private final LogicalOperator logicalOperator;

	/**
	 * Custom constructor.
	 * 
	 * @param formPage
	 *            the form page
	 * @param editor
	 *            the editor
	 */
	public AbstractConditionBuilderSection(final FormPage formPage,
			final AbstractCmClientFormEditor editor) {
		super(formPage, editor, ExpandableComposite.TITLE_BAR | ExpandableComposite.EXPANDED);
		
		ConditionalExpression expression = this.getEditor().getDependentObject();
		this.logicalOperator = this.conditionHandler.convertConditionExpressionStringToLogicalOperator(expression);
	}
	
	/**
	 * Get the LogicalOperator.
	 * @return LogicalOperator
	 */
	protected LogicalOperator getLogicalOperator() {
		return this.logicalOperator;
	}
	
	@Override
	public ConditionEditor getEditor() {
		return (ConditionEditor) super.getEditor();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.AbstractFormPart#commit(boolean)
	 */
	@Override
	public void commit(final boolean onSave) {
		
		try {
			if (isAllValidatorsValid()) {
			
				ConditionalExpression expression = this.getEditor().getDependentObject();
				String conditionString = this.conditionHandler.convertLogicalOperatorToConditionExpressionString(logicalOperator);

				expression.setConditionString(conditionString);
				LOG.info("Expression generated: [" + conditionString + "]"); //$NON-NLS-1$ //$NON-NLS-2$
				
				this.markStale();
				super.commit(onSave);
			}
		} catch (final CancelSaveException exception) {
			showErrorDialog(exception.getMessage());
			throw exception;
		} catch (final Exception exception) {
			showErrorDialog(exception.getLocalizedMessage());
			throw new CancelSaveException(exception);
		} 
	}
	
	private boolean isAllValidatorsValid() {
		
		final char newLine = '\n';
		final StringBuilder message = new StringBuilder();
		boolean bindingOk = true;
		
		for (IStatus status : (Collection<IStatus>) this.getBindingContext().getValidationStatusMap().values()) {
			if (!status.isOK()) {
				message.append(status.getMessage());
				message.append(newLine);
				bindingOk = false;
			}
		}
		
		if (!bindingOk) {
			throw new CancelSaveException(message.toString());
		}
		return bindingOk;
	}
	
	private void showErrorDialog(final String errorMessage) {
		
		MessageDialog.openWarning(getManagedForm().getForm().getShell(), 
				TargetedSellingMessages.get().ConditionalExpressionEditor_BlankExpression_Title, errorMessage);
	}

	@Override
	protected void bindControls(final DataBindingContext bindingContext) {
		// by default there is nothing to bind
	}
	
	@Override
	protected void createControls(final Composite parent, final FormToolkit toolkit) {

		IEpLayoutComposite epLayoutComposite = CompositeFactory.createGridLayoutComposite(parent, 1, false);
		((GridLayout) epLayoutComposite.getSwtComposite().getLayout()).horizontalSpacing = 0;
		((GridLayout) epLayoutComposite.getSwtComposite().getLayout()).marginWidth = 0;
		
		IPolicyTargetLayoutComposite composite = PolicyTargetCompositeFactory.wrapLayoutComposite(epLayoutComposite);
		
		createControls(composite);
	}

	/**
	 * Creates the controls by using the supplied policy target composite.
	 * 
	 * @param composite the policy target composite
	 */
	protected abstract void createControls(IPolicyTargetLayoutComposite composite);

}
