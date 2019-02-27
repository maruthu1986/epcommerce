/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core.binding;

import org.eclipse.core.databinding.AggregateValidationStatus;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IValueChangeListener;
import org.eclipse.core.databinding.observable.value.ValueChangeEvent;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.forms.AbstractFormPart;


/**
 * Connects the validation result from the given data binding context to the given FormPart, mark form dirty.
 * 
 */
public final class EpFormPartSupport {

	private AbstractFormPart formPart;

	private DataBindingContext dbc;

	private AggregateValidationStatus aggregateStatus;

	private IStatus currentStatus;

	/**
	 * Connect the validation result from the given data binding context to the given <code>AbstractFormPart</code>. 
	 * Upon any validation result change,
	 * {@link com.elasticpath.cmclient.core.ui.dialog.AbstractEpDialog#setComplete(boolean)} 
	 * will be called reflecting the new validation result, and the dialog's error message will be
	 * updated according to the current validation result.
	 * 
	 * @param formPart the AbstractFormPart to support validation
	 * @param dbc the data binding context
	 * @return an instance of <code>EpDialogSupport</code>
	 */
	public static EpFormPartSupport create(final AbstractFormPart formPart, final DataBindingContext dbc) {
		return new EpFormPartSupport(formPart, dbc);
	}

	private EpFormPartSupport(final AbstractFormPart fromPart, final DataBindingContext dbc) {
		this.formPart = fromPart;
		this.dbc = dbc;
		init();
	}

	/**
	 * Initialize the aggregate validation status and add a listener to the status so that it can update the dialog complete state appropriately upon
	 * validation result change.
	 */
	private void init() {
		aggregateStatus = new AggregateValidationStatus(dbc.getBindings(), AggregateValidationStatus.MAX_SEVERITY);
		aggregateStatus.addValueChangeListener(new IValueChangeListener() {
			public void handleValueChange(final ValueChangeEvent event) {
				currentStatus = (IStatus) event.diff.getNewValue();
//					handleStatusChanged();
			}
		});
		currentStatus = (IStatus) aggregateStatus.getValue();
	}

	/**
	 * Handle validation status change but updating complete status on dialog according to validation result status.
	 */
	void handleStatusChanged() {
		// just keep status object for future
		if (currentStatus == Status.OK_STATUS) { 
			formPart.markDirty();
		} else {
			formPart.markDirty();
		}
	}

	/**
	 * Disposes of this wizard page support object, removing any listeners it may have attached.
	 */
	public void dispose() {
		aggregateStatus.dispose();
		aggregateStatus = null;
		dbc = null;
		formPart = null;
	}
}
