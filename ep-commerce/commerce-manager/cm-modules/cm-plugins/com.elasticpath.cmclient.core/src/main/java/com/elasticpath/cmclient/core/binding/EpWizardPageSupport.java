/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core.binding;

import java.util.Iterator;

import org.eclipse.core.databinding.AggregateValidationStatus;
import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.ChangeEvent;
import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.core.databinding.observable.list.IListChangeListener;
import org.eclipse.core.databinding.observable.list.ListChangeEvent;
import org.eclipse.core.databinding.observable.list.ListDiff;
import org.eclipse.core.databinding.observable.list.ListDiffEntry;
import org.eclipse.core.databinding.observable.value.IValueChangeListener;
import org.eclipse.core.databinding.observable.value.ValueChangeEvent;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.wizard.WizardPage;

/**
 * Very similar to {@link org.eclipse.jface.databinding.wizard.WizardPageSupport},
 * except that it doesn't set the wizard dialog's error message during validation
 * failure.
 * @see org.eclipse.jface.databinding.wizard.WizardPageSupport
 */
public final class EpWizardPageSupport {
	
	private WizardPage wizardPage;
	private DataBindingContext dbc;
	private AggregateValidationStatus aggregateStatus;
	private boolean uiChanged;

	/**
	 * Connect the validation result from the given data binding context to the
	 * given wizard page. Upon creation, the wizard page support will use the
	 * context's validation result to determine whether the page is complete.
	 * The page's error message will not be set at this time ensuring that the
	 * wizard page does not show an error right away. Upon any validation result
	 * change, {@link WizardPage#setPageComplete(boolean)} will be called
	 * reflecting the new validation result.
	 * 
	 * @param wizardPage the wizard page to which databinding and validation support will be added
	 * @param dbc the databinding context for the page
	 * @return an instance of EpWizardPageSupport
	 */
	public static EpWizardPageSupport create(final WizardPage wizardPage,
			final DataBindingContext dbc) {
		return new EpWizardPageSupport(wizardPage, dbc);
	}

	private EpWizardPageSupport(final WizardPage wizardPage, final DataBindingContext dbc) {
		this.wizardPage = wizardPage;
		this.dbc = dbc;
		init();
	}

	private IChangeListener uiChangeListener = new IChangeListener() {
		public void handleChange(final ChangeEvent event) {
			handleUIChanged();
		}
	};
	private IListChangeListener bindingsListener = new IListChangeListener() {
		public void handleListChange(final ListChangeEvent event) {
			ListDiff diff = event.diff;
			ListDiffEntry[] differences = diff.getDifferences();
			for (ListDiffEntry listDiffEntry : differences) {
				Binding binding = (Binding) listDiffEntry.getElement();
				if (listDiffEntry.isAddition()) {
					binding.getTarget().addChangeListener(uiChangeListener);
				} else {
					binding.getTarget().removeChangeListener(uiChangeListener);
				}
			}
		}
	};
	private IStatus currentStatus;

	/**
	 * Initialize wizardpage support.
	 */
	private void init() {
		aggregateStatus = new AggregateValidationStatus(dbc.getBindings(),
				AggregateValidationStatus.MAX_SEVERITY);
		aggregateStatus.addValueChangeListener(new IValueChangeListener() {
			public void handleValueChange(final ValueChangeEvent event) {

				currentStatus = (IStatus) event.diff.getNewValue();
				handleStatusChanged();
			}
		});
		currentStatus = (IStatus) aggregateStatus.getValue();
		handleStatusChanged();
		dbc.getBindings().addListChangeListener(bindingsListener);
		for (Iterator<Binding> it = dbc.getBindings().iterator(); it.hasNext();) {
			Binding binding = it.next();
			binding.getTarget().addChangeListener(uiChangeListener);
		}
	}

	/**
	 * Handle changes to the UI.
	 */
	private void handleUIChanged() {
		uiChanged = true;
		if (currentStatus != null) {
			handleStatusChanged();
		}
		dbc.getBindings().removeListChangeListener(bindingsListener);
		for (Iterator<Binding> it = dbc.getBindings().iterator(); it.hasNext();) {
			Binding binding = it.next();
			binding.getTarget().removeChangeListener(uiChangeListener);
		}
	}

	/**
	 * Handle changes to the validation status. In the original WizardPageSupport class,
	 * this method would set the wizard page's error message, but in this class
	 * we don't do that because the error is shown as part of the field decorator.
	 */
	private void handleStatusChanged() {
		if (currentStatus != null
				&& currentStatus.getSeverity() == IStatus.ERROR) {
			wizardPage.setPageComplete(false);
		} else {
			wizardPage.setPageComplete(true);
		}
	}

	/**
	 * Disposes of this wizard page support object, removing any listeners it
	 * may have attached.
	 */
	public void dispose() {
		aggregateStatus.dispose();
		if (!uiChanged) {
			for (Iterator<Binding> it = dbc.getBindings().iterator(); it.hasNext();) {
				Binding binding = it.next();
				binding.getTarget().removeChangeListener(uiChangeListener);
			}
			dbc.getBindings().removeListChangeListener(bindingsListener);
		}
		aggregateStatus = null;
		dbc = null;
		uiChangeListener = null;
		bindingsListener = null;
		wizardPage = null;
	}

}
