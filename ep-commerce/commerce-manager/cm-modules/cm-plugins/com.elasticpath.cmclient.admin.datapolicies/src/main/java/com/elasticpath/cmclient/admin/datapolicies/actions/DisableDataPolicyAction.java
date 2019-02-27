/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.cmclient.admin.datapolicies.actions;

import java.util.Optional;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;

import com.elasticpath.cmclient.admin.datapolicies.AdminDataPoliciesMessages;
import com.elasticpath.cmclient.admin.datapolicies.event.DataPolicyEventService;
import com.elasticpath.cmclient.admin.datapolicies.views.DataPolicyListView;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.datapolicy.DataPolicy;
import com.elasticpath.service.datapolicy.DataPolicyService;

/**
 * Action to disable a data policy.
 */
public class DisableDataPolicyAction extends Action {

	private final DataPolicyListView listView;
	private final DataPolicyService dataPolicyService;

	/**
	 * Constructor.
	 *
	 * @param listView        the customer list view
	 * @param text            the action's text
	 * @param imageDescriptor the action's image
	 */
	public DisableDataPolicyAction(final DataPolicyListView listView, final String text, final ImageDescriptor imageDescriptor) {
		super(text, imageDescriptor);
		this.listView = listView;
		this.dataPolicyService = ServiceLocator.getService(ContextIdNames.DATA_POLICY_SERVICE);
	}

	@Override
	public void run() {
		ServiceLocator.getService(ContextIdNames.DATA_POLICY_SERVICE);
		Optional<DataPolicy> dataPolicyOptional = listView.getSelectedDataPolicy();

		dataPolicyOptional = dataPolicyOptional
				.filter(DataPolicy::isNotDisabled);

		if (dataPolicyOptional.isPresent()) {
			final boolean answerYes = MessageDialog.openConfirm(listView.getSite().getShell(),
					AdminDataPoliciesMessages.get().DataPolicyEditor_DisableDialogTitle,
					AdminDataPoliciesMessages.get().DataPolicyEditor_DisableConfirmation);
			if (answerYes) {
				DataPolicy dataPolicy = dataPolicyOptional.get();
				dataPolicy.disable();
				dataPolicyService.update(dataPolicy);
				DataPolicyEventService.getInstance()
						.fireDataPolicyChanged(new ItemChangeEvent<>(this, dataPolicy, ItemChangeEvent.EventType.REMOVE));
			}
		}
	}
}
