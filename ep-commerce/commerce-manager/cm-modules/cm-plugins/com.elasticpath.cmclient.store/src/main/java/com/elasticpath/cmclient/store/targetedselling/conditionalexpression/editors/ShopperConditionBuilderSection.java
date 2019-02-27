/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
/**
 * 
 */
package com.elasticpath.cmclient.store.targetedselling.conditionalexpression.editors;

import java.util.Set;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.TableWrapData;

import com.elasticpath.cmclient.conditionbuilder.component.TopLevelComposite;
import com.elasticpath.cmclient.conditionbuilder.impl.tag.ConditionBuilderFactoryImpl;
import com.elasticpath.cmclient.core.CorePlugin;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.editors.CancelSaveException;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.IPolicyTargetLayoutComposite;
import com.elasticpath.cmclient.store.targetedselling.TargetedSellingMessages;
import com.elasticpath.tags.domain.LogicalOperator;
import com.elasticpath.tags.domain.LogicalOperatorType;

/**
 * 
 * Class represents condition builder section for SHOPPER condition.
 * 
 */
public class ShopperConditionBuilderSection extends AbstractConditionBuilderSection {
	
	private final ConditionBuilderFactoryImpl conditionBuilderFactory;
	
	private TopLevelComposite<LogicalOperator, LogicalOperatorType> topLevelComposite;
	
	private boolean initConditionBuilder;

	private static final String SHOPPER_DICTIONARY = "SHOPPER"; //$NON-NLS-1$

	/**
	 * Custom constructor.
	 * 
	 * @param formPage
	 *            the form page
	 * @param editor
	 *            the editor
	 */
	public ShopperConditionBuilderSection(final FormPage formPage, final AbstractCmClientFormEditor editor) {
		super(formPage, editor);

		conditionBuilderFactory = new ConditionBuilderFactoryImpl();
		conditionBuilderFactory.setLocale(CorePlugin.getDefault().getDefaultLocale());
		conditionBuilderFactory.setDataBindingContext(getEditor().getDataBindingContext());
		conditionBuilderFactory.setAddButtonText("ConditionBuilder_AddConditionButton"); 		 //$NON-NLS-1$
		conditionBuilderFactory.setConditionBuilderTitle("ConditionBuilder_Title"); //$NON-NLS-1$
		conditionBuilderFactory.setTagDictionary(SHOPPER_DICTIONARY);
		
		conditionBuilderFactory.getResourceAdapterFactory().setResourceAdapterForLogicalOperator(
				object -> {
					//TODO Shall we put OR  AND etc localization into DB ?
					return TargetedSellingMessages.get().getMessage(object.getMessageKey());
				});
		
		conditionBuilderFactory.setListenerForRefreshParentComposite(
				object -> {
					getSection().getParent().setRedraw(false);
					getSection().pack();
					getSection().getParent().layout(true);
					getSection().getParent().setRedraw(true);

					getManagedForm().reflow(false);
				});
		conditionBuilderFactory.setListenerForMarkEditorState(
				object -> {
					if (!initConditionBuilder) {
						getEditor().pageModified();
						ShopperConditionBuilderSection.this.markDirty();
					}
				});
	}
	
	@Override
	protected void populateControls() {
		// TODO Auto-generated method stub
	}

	@Override
	public void commit(final boolean onSave) {
		if (onSave) {
			boolean conditionExist = false;
			Set<LogicalOperator> operators = this.topLevelComposite.getModel().getModel().getLogicalOperators(); 
			for (LogicalOperator logicalOperator : operators) {
				if (logicalOperator.hasChildren()) {
					conditionExist = true;
				}
			}
			if (conditionExist) {
				super.commit(onSave);
			} else {
				MessageDialog.openWarning(getEditor().getEditorSite().getShell(), 
						TargetedSellingMessages.get().ConditionalExpressionEditor_WarningDialogTitle,
						TargetedSellingMessages.get().ConditionalExpressionEditor_WarningDialogMessage);
				throw new CancelSaveException("Failed to commit!"); //$NON-NLS-1$
			}
		}
	}

	@Override
	protected void createControls(final IPolicyTargetLayoutComposite composite) {
		initConditionBuilder = true;
		
		if (topLevelComposite != null) {
			topLevelComposite.dispose();
		}
		final LogicalOperator logicalOperator = this.getLogicalOperator();
		
		composite.getSwtComposite().setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));

		topLevelComposite = conditionBuilderFactory.createFullUiFromModel(
				composite.getSwtComposite(),
				SWT.FLAT,
				logicalOperator
				);
		
		initConditionBuilder = false;

		// add policy container
		PolicyActionContainer container = addPolicyActionContainer("shopperConditionBuilderSection"); //$NON-NLS-1$
		container.addTarget(state -> EpControlFactory.changeEpStateForComposite(topLevelComposite, state));

		// refresh view
		getSection().getParent().setRedraw(false);
		getSection().pack();
		getSection().getParent().layout(true);
		getSection().getParent().setRedraw(true);
		
		getManagedForm().reflow(false);
	}
}
