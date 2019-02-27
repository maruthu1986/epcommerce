/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.binding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.databinding.DataBindingContext;

import com.elasticpath.cmclient.core.CmSingletonUtil;

/**
 * Unbinds the controls from Dialogs.
 * 
 * The method registerForUnbind is called from EpControlBindingProvider during the bind process.
 * When the Dialog is closed by pressing the cancel button or the ESC key, the class AbstractPolicyAwareDialog
 * calls unbindAll for all binders.
 * 
 * This prevents the "Widget is disposed" error when the dialog is closed and the binders are still alive. 
 */
public final class EPControlUnbinder {

	private final Map<DataBindingContext, List<EpValueBinding>> contextMap;
	
	private EPControlUnbinder() {
		contextMap = new HashMap<DataBindingContext, List<EpValueBinding>>();
	}
	
	/**
	 * Gets a Session instance.
	 * @return the EPControlUnbinder session instance
	 */
	public static EPControlUnbinder getInstance() {
		return  CmSingletonUtil.getSessionInstance(EPControlUnbinder.class);
	}
	
	/**
	 * Register a binder within a context.
	 * @param dataBindingContext the data binding context
	 * @param epValueBinding the binder 
	 */
	public void registerForUnbind(final DataBindingContext dataBindingContext, final EpValueBinding epValueBinding) {
		List<EpValueBinding> binderList = contextMap.get(dataBindingContext);
		if (binderList == null) {
			binderList = new ArrayList<EpValueBinding>();
			contextMap.put(dataBindingContext, binderList);
		}
		binderList.add(epValueBinding);
	}
	
	/**
	 * Unbinds all the controls.
	 * @param dataBindingContext the data binding context
	 */
	public void unbindAll(final DataBindingContext dataBindingContext) {
		List<EpValueBinding> binderList = contextMap.get(dataBindingContext);
		if (binderList == null) {
			return;
		}
		
		for (EpValueBinding binder : binderList) {
			EpControlBindingProvider.removeEpValueBinding(dataBindingContext, binder);
		}
		contextMap.remove(dataBindingContext);
	}
	
}
