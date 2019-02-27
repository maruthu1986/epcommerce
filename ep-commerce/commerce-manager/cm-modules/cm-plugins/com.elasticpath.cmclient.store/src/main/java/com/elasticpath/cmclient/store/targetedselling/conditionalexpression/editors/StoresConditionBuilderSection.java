/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
/**
 * 
 */
package com.elasticpath.cmclient.store.targetedselling.conditionalexpression.editors;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.forms.editor.FormPage;

import com.elasticpath.cmclient.conditionbuilder.wizard.conditioncomposite.StoresConditionComposite;
import com.elasticpath.cmclient.conditionbuilder.wizard.model.StoresConditionModelAdapter;
import com.elasticpath.cmclient.conditionbuilder.wizard.model.impl.StoresConditionModelAdapterImpl;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.editors.CancelSaveException;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.ControlStateChangeTargetImpl;
import com.elasticpath.cmclient.policy.ui.IPolicyTargetLayoutComposite;
import com.elasticpath.cmclient.store.targetedselling.TargetedSellingMessages;

/**
 * 
 * Class represents condition builder section for STORES condition.
 * 
 */
public class StoresConditionBuilderSection extends AbstractConditionBuilderSection {

	private StoresConditionComposite storesConditionComposite;

	/**
	 * Custom constructor.
	 * 
	 * @param formPage
	 *            the form page
	 * @param editor
	 *            the editor
	 */
	public StoresConditionBuilderSection(final FormPage formPage,
			final AbstractCmClientFormEditor editor) {
		super(formPage, editor);
	}
	
	@Override
	protected void populateControls() {
		//Empty method
	}

	@Override
	public void commit(final boolean onSave) {
		if (onSave) {
			if (!storesConditionComposite.hasOneOrMoreElements()) {
				MessageDialog.openWarning(getEditor().getEditorSite().getShell(), 
						TargetedSellingMessages.get().CantRemoveStoreTitle,
						TargetedSellingMessages.get().CantRemoveStoreMessage);
				throw new CancelSaveException("Failed to commit!"); //$NON-NLS-1$
			}
			super.commit(onSave);
		}
	}

	@Override
	protected void createControls(final IPolicyTargetLayoutComposite composite) {
		if (storesConditionComposite != null) {
			storesConditionComposite.removeAllEvent();
		}
		
		StoresConditionModelAdapter adapter = new StoresConditionModelAdapterImpl(this.getLogicalOperator());
		
		PolicyActionContainer container = addPolicyActionContainer("storesConditionBuilderSection"); //$NON-NLS-1$
		container.addTarget(new ControlStateChangeTargetImpl(composite.getSwtComposite()));

		storesConditionComposite = new StoresConditionComposite(composite, 
				container, 
				TargetedSellingMessages.get().AvailableStores_Label,
				TargetedSellingMessages.get().SelectedStores_Label,
				() -> {
//						if (!storesConditionComposite.isEmpty()) {
//							storesConditionComposite.saveStores();
//						}
				},
				adapter, true);

		adapter.addPropertyChangeListener(event -> {
			getEditor().pageModified();
			StoresConditionBuilderSection.this.markDirty();
		});
		
		composite.getSwtComposite().layout();
	}
	
}
