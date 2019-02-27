/*
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.cmclient.conditionbuilder.wizard.pages;

import java.util.List;

import com.elasticpath.cmclient.conditionbuilder.plugin.ConditionBuilderMessages;
import com.elasticpath.cmclient.conditionbuilder.wizard.conditioncomposite.StoresConditionComposite;
import com.elasticpath.cmclient.conditionbuilder.wizard.model.impl.StoresConditionModelAdapterImpl;
import com.elasticpath.cmclient.core.wizard.AbstractEpWizard;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.IPolicyTargetLayoutComposite;
import com.elasticpath.domain.sellingcontext.SellingContext;
import com.elasticpath.tags.domain.ConditionalExpression;
import com.elasticpath.tags.domain.LogicalOperator;
import com.elasticpath.tags.domain.TagDictionary;

/**
 * Class represents page for Stores selector on the Create Dynamic Assignment wizard.
 * 
 * @param <T> - class that extends {@link SellingContext}.
 */
public class SellingContextConditionStoresWizardPage <T extends SellingContext> 
			extends AbstractSellingContextConditionWizardPage<SellingContext>  {
	
	private StoresConditionComposite storesConditionComposite;
	private static final String TAG_DICTIONARY_STORES_GUID = TagDictionary.DICTIONARY_STORES_GUID;
	
	/**
	 * Constructor.
	 * 
	 * @param pageName - name of the page
	 * @param title - title of the page
	 * @param description - description of the page
	 * @param conditionsList conditions list
	 * @param sellingContext - selling context
	 */
	public SellingContextConditionStoresWizardPage(
			final String pageName, final String title, final String description,
			final List<ConditionalExpression> conditionsList,
			final SellingContext sellingContext) {
		super(2, pageName, title, description, TAG_DICTIONARY_STORES_GUID, conditionsList, sellingContext);
	}


	@Override
	protected void createConditionExpressionComposite(final IPolicyTargetLayoutComposite parent, final PolicyActionContainer container) {

		LogicalOperator logicalOperator = 
			this.getLogicalOperatorForCurrentTagDictionary(TagDictionary.DICTIONARY_STORES_GUID);

		storesConditionComposite = new StoresConditionComposite(parent,
				container,
				ConditionBuilderMessages.get().AvailableStores_Label,
				ConditionBuilderMessages.get().SelectedStores_Label,
            () -> ((AbstractEpWizard<?>) getWizard()).getWizardDialog().updateButtons(),
				new StoresConditionModelAdapterImpl(logicalOperator),
				false);
	}

	@Override
	protected String getLabelForRadioButtonSavedConditions() {
		return ConditionBuilderMessages.get().ShowStoresSavedConditions;
	}

	@Override
	protected String getLabelForRadioButtonCreateConditions() {
		return ConditionBuilderMessages.get().AssignSpecificStores;
	}

	@Override
	protected String getLabelForRadioButtonsAll() {
		return ConditionBuilderMessages.get().Wizard_Store_Page_Button_All;
	}

	@Override
	protected void bindControls() {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean isPageComplete() {
		if (isCreateConditionButtonSelected()) {
			return !storesConditionComposite.isEmpty();
		} 
		return super.isPageComplete();
	}

	@Override
	protected boolean isCreateCompositeBlock() {
		return false;
	}
}