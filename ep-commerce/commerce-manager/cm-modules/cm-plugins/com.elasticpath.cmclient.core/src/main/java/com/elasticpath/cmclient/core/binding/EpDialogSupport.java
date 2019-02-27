/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core.binding;

import org.eclipse.core.databinding.AggregateValidationStatus;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IValueChangeListener;
import org.eclipse.core.databinding.observable.value.ValueChangeEvent;
import org.eclipse.core.runtime.IStatus;

import com.elasticpath.cmclient.core.ui.dialog.AbstractEpDialog;

/**
 * Connects the validation result from the given data binding context to the given <code>AbstractEpDialog</code>, updating the dialog's completion
 * state accordingly.
 * 
 * @see org.eclipse.jface.databinding.wizard.WizardPageSupport
 */
public final class EpDialogSupport {

	private AbstractEpDialog dialog;

	private DataBindingContext dbc;

	private AggregateValidationStatus aggregateStatus;

	private IStatus currentStatus;

	/**
	 * Connect the validation result from the given data binding context to the given <code>AbstractEpDialog</code>. Upon creation, the dialog
	 * support will use the context's validation result to determine whether the dialog is complete. Upon any validation result change,
	 * {@link AbstractEpDialog#setComplete(boolean)} will be called reflecting the new validation result, and the dialog's error message will be
	 * updated according to the current validation result.
	 * 
	 * @param dialog the dialog to support validation
	 * @param dbc the data binding context
	 * @return an instance of <code>EpDialogSupport</code>
	 */
	public static EpDialogSupport create(final AbstractEpDialog dialog, final DataBindingContext dbc) {
		return new EpDialogSupport(dialog, dbc);
	}

	private EpDialogSupport(final AbstractEpDialog dialog, final DataBindingContext dbc) {
		this.dialog = dialog;
		this.dbc = dbc;
		init();
	}

	/**
	 * Initialize the aggregate validation status and add a listener to the status so that it can update the dialog complete state appropriately upon
	 * validation result change.
	 */
	private void init() {
	// ---- DOCEpDialogSupportInit
		aggregateStatus = new AggregateValidationStatus(dbc.getBindings(), AggregateValidationStatus.MAX_SEVERITY);
		aggregateStatus.addValueChangeListener(new IValueChangeListener() {
			public void handleValueChange(final ValueChangeEvent event) {
				currentStatus = (IStatus) event.diff.getNewValue();
				handleStatusChanged();
	// ---- DOCEpDialogSupportInit
			}
		});
		currentStatus = (IStatus) aggregateStatus.getValue();
		handleStatusChanged();
	}

	/**
	 * Handle validation status change but updating complete status on dialog according to validation result status.
	 */
	private void handleStatusChanged() {
		if ((currentStatus != null) && (currentStatus.getSeverity() == IStatus.ERROR)) {
			dialog.setComplete(false);
		} else {
			dialog.setComplete(true);
		}
	}

	/**
	 * Disposes of this wizard page support object, removing any listeners it may have attached.
	 */
	public void dispose() {
		aggregateStatus.dispose();
		aggregateStatus = null;
		dbc = null;
		dialog = null;
	}
}
