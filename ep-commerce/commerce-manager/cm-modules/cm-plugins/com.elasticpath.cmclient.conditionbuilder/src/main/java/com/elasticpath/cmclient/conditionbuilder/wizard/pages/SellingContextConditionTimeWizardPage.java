/*
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.cmclient.conditionbuilder.wizard.pages;

import java.util.List;

import com.elasticpath.cmclient.conditionbuilder.plugin.ConditionBuilderMessages;
import com.elasticpath.cmclient.conditionbuilder.wizard.conditioncomposite.TimeConditionComposite;
import com.elasticpath.cmclient.conditionbuilder.wizard.model.impl.TimeConditionModelAdapterImpl;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.IPolicyTargetLayoutComposite;
import com.elasticpath.domain.sellingcontext.SellingContext;
import com.elasticpath.tags.domain.ConditionalExpression;
import com.elasticpath.tags.domain.LogicalOperator;
import com.elasticpath.tags.domain.TagDictionary;

/**
 * Class represents page on Dynamic Content Assignment wizard with Start end End date for the Assignment.
 *  @param <T> - class that extends {@link SellingContext}.
 */
public class SellingContextConditionTimeWizardPage <T extends SellingContext> extends AbstractSellingContextConditionWizardPage<SellingContext>  {

	private static final int NUM_OF_COLUMNS_ON_THE_PAGE = 4;

	private TimeConditionComposite timeConditionComposite;

	/**
	 * Constructor.
	 * 
	 * @param pageName - name of the page
	 * @param title - title of the page
	 * @param description - description of the page
	 * @param conditionsList conditions list
	 * @param sellingContext - selling context
	 */
	public SellingContextConditionTimeWizardPage(
			final String pageName, final String title, final String description,
			final List<ConditionalExpression> conditionsList, final SellingContext sellingContext) {
		super(2, pageName, title, description, TagDictionary.DICTIONARY_TIME_GUID, conditionsList,  sellingContext);
	}

	@Override
	protected void createConditionExpressionComposite(final IPolicyTargetLayoutComposite parent, final PolicyActionContainer container) {
		
		LogicalOperator logicalOperator = 
			this.getLogicalOperatorForCurrentTagDictionary(TagDictionary.DICTIONARY_TIME_GUID);

		timeConditionComposite = new TimeConditionComposite(
				new TimeConditionModelAdapterImpl(logicalOperator), 
				NUM_OF_COLUMNS_ON_THE_PAGE, 
				parent,
				container,
				getDataBindingContext(), getBindingProvider(), false);

		timeConditionComposite.bindControls();
	}

	@Override
	protected void populateControls() {
		// Empty method
	}

	@Override
	protected String getLabelForRadioButtonSavedConditions() {
		return ConditionBuilderMessages.get().ShowTimeSavedConditions;
	}

	@Override
	protected String getLabelForRadioButtonCreateConditions() {
		return ConditionBuilderMessages.get().Wizard_Dates_Range_Page_Button_Range;
	}

	@Override
	protected String getLabelForRadioButtonsAll() {
		return ConditionBuilderMessages.get().Wizard_Dates_Range_Page_Button_All;
	}

	@Override
	protected void bindControls() {
		// TODO Auto-generated method stub
	}

	@Override
	protected boolean isCreateCompositeBlock() {
		return false;
	}

}