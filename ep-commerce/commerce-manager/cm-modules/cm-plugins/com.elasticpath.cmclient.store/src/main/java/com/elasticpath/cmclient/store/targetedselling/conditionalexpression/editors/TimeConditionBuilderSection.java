/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
/**
 * 
 */
package com.elasticpath.cmclient.store.targetedselling.conditionalexpression.editors;

import org.eclipse.ui.forms.editor.FormPage;

import com.elasticpath.cmclient.conditionbuilder.wizard.conditioncomposite.TimeConditionComposite;
import com.elasticpath.cmclient.conditionbuilder.wizard.model.TimeConditionModelAdapter;
import com.elasticpath.cmclient.conditionbuilder.wizard.model.impl.TimeConditionModelAdapterImpl;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.ControlStateChangeTargetImpl;
import com.elasticpath.cmclient.policy.ui.IPolicyTargetLayoutComposite;

/**
 * 
 * Class represents condition builder section for TIME condition.
 * 
 */
public class TimeConditionBuilderSection extends AbstractConditionBuilderSection {
	
	private static final int NUM_OF_COLUMNS_ON_THE_PAGE = 4;

	private TimeConditionComposite timeConditionComposite;
	
	private TimeConditionModelAdapter adapter;
	
	private boolean isCommitOperation; // use to avoid a dirty mark for section for commit operation
	
	/**
	 * Custom constructor.
	 * 
	 * @param formPage
	 *            the form page
	 * @param editor
	 *            the editor
	 */
	public TimeConditionBuilderSection(final FormPage formPage,
			final AbstractCmClientFormEditor editor) {
		super(formPage, editor);
	}
	
	@Override
	protected void populateControls() {
		//Empty method
	}

	/* (non-Javadoc)
	 * @see com.elasticpath.cmclient.store.targetedselling.conditionalexpression.editors.AbstractConditionBuilderSection#commit(boolean)
	 */
	@Override
	public void commit(final boolean onSave) {
		if (onSave) {
			this.isCommitOperation = true;
			super.commit(onSave);
		}
	}

	@Override
	protected void createControls(final IPolicyTargetLayoutComposite composite) {
		this.isCommitOperation = false;
		
		if (timeConditionComposite != null) {
			adapter.removeAllPropertyChangeListeners();
			timeConditionComposite.unbindControls();
		}
		
		adapter = new TimeConditionModelAdapterImpl(this.getLogicalOperator());
		
		PolicyActionContainer container = addPolicyActionContainer("timeConditionBuilderSection"); //$NON-NLS-1$
		container.addTarget(new ControlStateChangeTargetImpl(composite.getSwtComposite()));
		
		timeConditionComposite = new TimeConditionComposite(
				adapter,
				NUM_OF_COLUMNS_ON_THE_PAGE, 
				composite,
				container,
				getEditor().getDataBindingContext(),
				EpControlBindingProvider.getInstance(), true);
		timeConditionComposite.bindControls();

		composite.getSwtComposite().layout();

		adapter.addPropertyChangeListener(event -> {
			if (isCommitOperation) {
				return;
			}
			getEditor().pageModified();
			TimeConditionBuilderSection.this.markDirty();
		});
	}

}
