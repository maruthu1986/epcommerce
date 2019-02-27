/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.cmclient.fulfillment.editors.customer.dialogs;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogLabelKeys;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory;
import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;
import com.elasticpath.cmclient.fulfillment.FulfillmentPlugin;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.datapolicy.DataPoint;
import com.elasticpath.service.datapolicy.DataPointService;
import com.elasticpath.service.datapolicy.DataPointValueService;
import com.elasticpath.service.datapolicy.impl.DataPointValue;

/**
 * Dialog for removing customer data.
 */
public class CustomerRemoveDataPolicyDataDialog extends MessageDialog {

	private static final DataPointService DATA_POINT_SERVICE =
		(DataPointService) ServiceLocator.getService(ContextIdNames.DATA_POINT_SERVICE);

	private static final DataPointValueService DATA_POINT_VALUE_SERVICE =
		(DataPointValueService) ServiceLocator.getService(ContextIdNames.DATA_POINT_VALUE_SERVICE);

	private static final String[] BUTTONS = {
		FulfillmentMessages.get().Delete_Label,
		JFaceResources.getString(IDialogLabelKeys.CANCEL_LABEL_KEY) };

	/** Delete button index. */
	public static final int DELETE_BUTTON_INDEX = 0;

	private Button deleteFromAll;
	private EpControlFactory instance;
	private boolean isDeleteFromAll;
	private final String dataPolicyName;
	private final String dataPolicyGuid;
	private final String customerGuid;

	private final Collection<DataPointValue> dataPointValues;

	/**
	 * Custom constructor.
	 *
	 * @param parentShell the shell
	 * @param customerGuid the customer guid
	 * @param dataPolicyName the data policy name
	 * @param dataPolicyGuid the data policy guid
	 * @param dataPointValues the data point values
	 */
	public CustomerRemoveDataPolicyDataDialog(final Shell parentShell, final String customerGuid, final String dataPolicyName,
		final String dataPolicyGuid, final Collection<DataPointValue> dataPointValues) {

		super(parentShell, FulfillmentMessages.get().DeletePolicyData_Confirm, null,
			String.format(FulfillmentMessages.get().DeleteCustomerData_Question, dataPolicyName),
			QUESTION, BUTTONS, 0);

		this.dataPointValues = dataPointValues;
		this.dataPolicyName = dataPolicyName;
		this.dataPolicyGuid = dataPolicyGuid;
		this.customerGuid = customerGuid;
	}

	@Override
	protected Control createDialogArea(final Composite parent) {
		final Composite dialogArea = (Composite) super.createDialogArea(parent);

		deleteFromAll = this.getEpControlFactory().createButton(dialogArea,
			FulfillmentMessages.get().IncludeDataPointsWithGrantedConsent_Label, SWT.CHECK, EpControlFactory.EpState.EDITABLE);

		return dialogArea;
	}

	private EpControlFactory getEpControlFactory() {
		if (instance == null) {
			instance = EpControlFactory.getInstance();
		}

		return instance;
	}

	@Override
	protected void buttonPressed(final int buttonId) {
		if (buttonId == 0) {
			isDeleteFromAll = deleteFromAll.getSelection();
		}

		super.buttonPressed(buttonId);
	}

	@Override
	public int open() {

		final int result = super.open();
		if (result == DELETE_BUTTON_INDEX) {
			try {
				int numOfRemovedDataPointValues;
				if (isDeleteFromAll) {

					numOfRemovedDataPointValues = DATA_POINT_VALUE_SERVICE.removeValues(dataPointValues);

				} else {
					List<DataPoint> removableDataPoints = DATA_POINT_SERVICE
						.findUniqueRemovableForDataPolicyAndCustomer(dataPolicyGuid, customerGuid);

					Map<String, Collection<DataPoint>> customerGuidToDataPoints = new HashMap<>(1);
					customerGuidToDataPoints.put(customerGuid, removableDataPoints);

					Collection<DataPointValue> dataPointValues = DATA_POINT_VALUE_SERVICE.getValues(customerGuidToDataPoints);
					numOfRemovedDataPointValues = DATA_POINT_VALUE_SERVICE.removeValues(dataPointValues);
				}

				if (numOfRemovedDataPointValues == 0) {
					MessageDialog.openInformation(new Shell(Display.getCurrent()), FulfillmentMessages.get().DeletePolicyData_Title,
						FulfillmentMessages.get().NoRemovableDataPointValues_Message);

				} else {
					MessageDialog.openInformation(new Shell(Display.getCurrent()), FulfillmentMessages.get().DeletePolicyData_Title,
						String.format(FulfillmentMessages.get().DataPointValueSuccessfulDeletion_Message, numOfRemovedDataPointValues,
							dataPolicyName));
				}

			} catch (Exception e) {
				String errMessage = String.format(FulfillmentMessages.get().DataPointValueRemovalError_Message, dataPolicyName);

				Status status = new Status(IStatus.ERROR, FulfillmentPlugin.PLUGIN_ID, e.getMessage(), e);
				ErrorDialog.openError(new Shell(Display.getCurrent()), FulfillmentMessages.get().Error_Title, errMessage, status);
			}
		}
		return result;
	}
}


