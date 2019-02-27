/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
/**
 * 
 */
package com.elasticpath.cmclient.jobs.wizards;

import org.eclipse.jface.wizard.IWizardPage;

/**
 * A page change listener,
 * subscribed to DirectCsvImportBaseAmountWizard to handle a next page change event.
 */
public interface PageChangeListener {

	/**
	 * Page changed to the new.
	 * @param page new active page
	 */
	void pageChanged(IWizardPage page);
}
