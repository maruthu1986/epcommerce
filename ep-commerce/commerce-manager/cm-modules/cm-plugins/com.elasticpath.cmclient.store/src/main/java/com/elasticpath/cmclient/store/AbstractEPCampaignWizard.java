/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.store;

import org.eclipse.swt.graphics.Image;

import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareWizard;

/**
 * Abstract class for the wizard to be used in store. 
 *
 * @param <T>
 */
public abstract class AbstractEPCampaignWizard<T> extends AbstractPolicyAwareWizard<T> {

	/**
	 * Creates wizard with specified title, icon and fills pages titles.
	 * 
	 * @param windowTitle dialog title.
	 * @param pagesTitleBlank blank to make pages titles.
	 * @param wizardImage wizard dialog icon.
	 */
	public AbstractEPCampaignWizard(final String windowTitle, final String pagesTitleBlank,
			final Image wizardImage) {
		super(windowTitle, pagesTitleBlank, wizardImage);		
	}

	@Override
	public abstract T getModel(); 
	
	/**
	 * Gets name from the model.
	 *  
	 * @return - name from the model.
	 */
	public abstract String getNameFromModel(); 


}
