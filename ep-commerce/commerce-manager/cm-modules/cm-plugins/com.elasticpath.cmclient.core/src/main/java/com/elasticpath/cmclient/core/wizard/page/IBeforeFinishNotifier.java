/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core.wizard.page;

/**
 * interface denoting that wizard page is engaged in validating
 * <code>AbstractEpWizard.performFinish</code>.
 */
public interface IBeforeFinishNotifier {

	/**
	 * defined whether this wizard page allows finish button 
	 * to be enabled.
	 * @return true if allows enabled Finish button, false otherwise
	 */
	boolean enableFinish();
	
}
