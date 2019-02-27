/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core.helpers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;

import com.elasticpath.cmclient.core.CoreMessages;
/**
 * The utility class to provide some handy method about binding context.
 */
public final class DataBindingUtil {
	
	private static final DataBindingUtil INSTANCE = new DataBindingUtil();
	
	private DataBindingUtil() { };
	
	/**
	 * Get the instance.
	 * @return data binding utility instance
	 */
	public static DataBindingUtil getInstance() {
		return INSTANCE;
	}

	/**
	 * Return the error status list.
	 * @param bindingContext the binding context
	 * @return the list the error status
	 */
	public List<IStatus> getBindingContextErrorStatus(final DataBindingContext bindingContext) {
		final List<IStatus> errorStatusList = new ArrayList<IStatus>();
		bindingContext.updateModels();
		for (final Iterator<IStatus> iterator = bindingContext.getValidationStatusMap().values().iterator(); iterator.hasNext();) {
			final IStatus currStatus = iterator.next();
			if (!currStatus.isOK()) {
				errorStatusList.add(currStatus);
			}
		}
		return errorStatusList;
		
	}

	/**
	 * Show the warning message dialog, the content is from the list of <code>IStatus</code>.
	 * @param parent the parent shell
	 * @param validationStatus the status list
	 */
	public void showValidationDialog(final Shell parent, final List<IStatus> validationStatus) {
		final StringBuilder errorListString = new StringBuilder();
		errorListString.append('\n');
		for (final IStatus status : validationStatus) {
			errorListString.append('\n');
			errorListString.append(status.getMessage());
		}
		final String message =
			NLS.bind(CoreMessages.get().AbstractCmClientFormEditor_Error_save,
			errorListString.toString());

		MessageDialog.openWarning(parent, CoreMessages.get().AbstractCmClientFormEditor_ErrorTitle_save, message);
	}
	
}
