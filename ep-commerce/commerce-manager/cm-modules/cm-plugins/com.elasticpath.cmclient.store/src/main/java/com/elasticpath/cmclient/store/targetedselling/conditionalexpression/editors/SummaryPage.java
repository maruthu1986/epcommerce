/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
/**
 * 
 */
package com.elasticpath.cmclient.store.targetedselling.conditionalexpression.editors;

import com.elasticpath.cmclient.store.StorePlugin;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.forms.IManagedForm;

import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareEditorPage;
import com.elasticpath.cmclient.store.targetedselling.TargetedSellingMessages;

/**
 * Summary page.
 * 
 */
public class SummaryPage extends AbstractPolicyAwareEditorPage {

	/**
	 * Custom constructor.
	 * @param editor named condition editor
	 */
	public SummaryPage(final AbstractCmClientFormEditor editor) {
		super(editor, "ConditionSummaryPage", TargetedSellingMessages.get().Summary); //$NON-NLS-1$
	}

	@Override
	protected void addEditorSections(final AbstractCmClientFormEditor editor,
			final IManagedForm managedForm) {
		PolicyActionContainer container = addPolicyActionContainer("conditionBuilderSummaryPage"); //$NON-NLS-1$
		
		addPart(container, managedForm, new SummarySection(this, editor));
		addExtensionEditorSections(editor, managedForm, StorePlugin.PLUGIN_ID, getClass().getSimpleName());
	}

	@Override
	protected void addToolbarActions(final IToolBarManager toolBarManager) {
		// do nothing
	}

	@Override
	protected int getFormColumnsCount() {
		return 2;
	}

	@Override
	protected String getFormTitle() {
		return null;
	}

}
