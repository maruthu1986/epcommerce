/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.store.targetedselling.conditionalexpression.wizard;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.store.AbstractEPCampaignWizard;
import com.elasticpath.cmclient.store.targetedselling.TargetedSellingImageRegistry;
import com.elasticpath.cmclient.store.targetedselling.TargetedSellingMessages;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.tags.domain.ConditionalExpression;

/**
 * The wizard for creating catalog promotions.
 */
public class NewConditionalExpressionWizard extends AbstractEPCampaignWizard<ConditionalExpression> {

	private static final String WRAPPER_PAGE_NAME = "NewConditionalExpressionWizardWrapperPage"; //$NON-NLS-1$

	private final ConditionalExpression model;
	
	/**
	 * Width in dialog.
	 */
	public static final int DEFAULT_WIDTH = 300;
	
	/**
	 * Height in dialog.
	 */	
	public static final int DEFAULT_HEIGHT = 70;	

	/**
	 * Default constructor.
	 */
	public NewConditionalExpressionWizard() {
		super(TargetedSellingMessages.get().NewConditionalExpressionCreateWizard_Title,
				null, 
				TargetedSellingImageRegistry.getImage(TargetedSellingImageRegistry.IMAGE_DYNAMIC_CONTENT_CREATE_ACTION));
		model = ServiceLocator.getService(ContextIdNames.TAG_CONDITION);
		model.setNamed(true);
		setNeedsProgressMonitor(true);
	}

	@Override
	public ConditionalExpression getModel() {
		return model;
	}

	@Override
	public String getNameFromModel() {
		
		return model.getName();
	}

	@Override
	public void addPages() {
		addPage(new NewConditionalExpressionWizardWrapperPage(WRAPPER_PAGE_NAME, 
				null));
	}

	@Override
	public String getTargetIdentifier() {
		return "conditionBuilderEditor"; //$NON-NLS-1$
	}
	

}
