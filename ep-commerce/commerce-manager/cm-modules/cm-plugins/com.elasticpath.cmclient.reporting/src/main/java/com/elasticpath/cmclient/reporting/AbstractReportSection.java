/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.reporting;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.ValidationStatusProvider;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.PlatformUI;

import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.reporting.views.ReportingNavigationView;

/**
 * Helper class for constructing an {@link IReport}.
 */
public abstract class AbstractReportSection implements IReportExtension {

	private final List<DataBindingContext> contexts = new ArrayList<DataBindingContext>();

	/**
	 * Binds the controls.
	 * 
	 * @param bindingProvider the {@link EpControlBindingProvider}
	 * @param context a {@link DataBindingContext}
	 */
	protected abstract void bindControlsInternal(EpControlBindingProvider bindingProvider, DataBindingContext context);

	@Override
	public void bindControls(final EpControlBindingProvider bindingProvider, final DataBindingContext context) {
		contexts.add(context);
		bindControlsInternal(bindingProvider, context);
		updateButtonsStatus();
	}

	@Override
	public boolean isInputValid() {
		for (DataBindingContext context : contexts) {
			if (context.getValidationRealm().isCurrent()) {
				for (Object provider : context.getValidationStatusProviders()) {
					ValidationStatusProvider statusProvider = (ValidationStatusProvider) provider;
					Object status = statusProvider.getValidationStatus().getValue();
					if (status != null && !((IStatus) status).isOK()) {
						return false;
					}
				}
			}
		}
		return true;
	}

	/**
	 * @return the parent {@link ReportingNavigationView} or {@code null} if it cannot be found
	 */
	protected ReportingNavigationView getReportingView() {
		IViewReference[] viewRef = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().getViewReferences();
		for (int i = 0; i < viewRef.length; i++) {
			if (viewRef[i].getId().equals(ReportingNavigationView.VIEW_ID)) {
				return (ReportingNavigationView) viewRef[i].getView(false);
			}
		}
		return null;
	}

	/**
	 * Update submission buttons with status of the controls. This method merely checks the status of controls which
	 * used the {@link DataBindingContext} in
	 * {@link #bindControlsInternal(EpControlBindingProvider, DataBindingContext)}.
	 */
	protected void updateButtonsStatus() {
		ReportingNavigationView navView = getReportingView();
		if (isInputValid()) {
			navView.enableButtons();
		} else {
			navView.disableButtons();
		}
		refreshLayout();
	}
}
