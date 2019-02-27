/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.cmclient.admin.datapolicies.dialogs;

import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.conversion.Converter;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.elasticpath.cmclient.admin.datapolicies.AdminDataPoliciesImageRegistry;
import com.elasticpath.cmclient.admin.datapolicies.AdminDataPoliciesMessages;
import com.elasticpath.cmclient.admin.datapolicies.AdminDataPoliciesPlugin;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.EpDialogSupport;
import com.elasticpath.cmclient.core.ui.dialog.AbstractEpDialog;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.validation.CompoundValidator;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.datapolicy.DataPoint;
import com.elasticpath.service.datapolicy.DataPointService;
import com.elasticpath.service.datapolicy.DataPointValueService;

/**
 * Data point create/view dialog.
 */
public final class DataPointDialog extends AbstractEpDialog {

	private final String title;

	private final Image image;

	private final DataBindingContext context;

	private final DataPointService dataPointService;

	private final DataPointValueService dataPointValueService;

	private final TableViewer tableView;

	private DataPoint dataPoint;

	private Text dataPointName;

	private CCombo dataPointLocation;

	private CCombo dataPointKey;

	private Text dataPointDescriptionKey;

	private Button dataPointRemovable;

	private Map<String, Set<String>> locationAndSupportedFields;

	/**
	 * Constructor.
	 *
	 * @param parentShell parent shell.
	 * @param tableView   table view to add created data point to.
	 * @param title       dialog title.
	 * @param image       dialog image.
	 * @param dataPoint   data point object to operate with.
	 */
	private DataPointDialog(final Shell parentShell, final TableViewer tableView, final String title, final Image image, final DataPoint dataPoint) {
		super(parentShell, 2, false);
		this.tableView = tableView;
		this.dataPoint = dataPoint;

		this.dataPointService = ServiceLocator.getService(ContextIdNames.DATA_POINT_SERVICE);
		this.dataPointValueService = ServiceLocator.getService(ContextIdNames.DATA_POINT_VALUE_SERVICE);

		this.context = new DataBindingContext();
		this.title = title;
		this.image = image;
	}


	/**
	 * Open create dialog convenient method.
	 *
	 * @param parentShell parent shell.
	 * @param tableView   table view to add created data point to.
	 * @return true if dialog opened successfully, otherwise false.
	 */
	public static boolean openCreateDialog(final Shell parentShell, final TableViewer tableView) {
		DataPoint dataPoint = ServiceLocator.getService(ContextIdNames.DATA_POINT);
		dataPoint.initialize();
		final DataPointDialog dialog = new DataPointDialog(parentShell, tableView,
				AdminDataPoliciesMessages.get().DataPolicyEditor_DataPoints_Dialog_CreateTitle,
				AdminDataPoliciesImageRegistry.getImage(AdminDataPoliciesImageRegistry.IMAGE_DATA_POLICY_CREATE), dataPoint);
		return dialog.open() == 0;
	}

	/**
	 * Open view dialog convenient method.
	 *
	 * @param parentShell parent shell.
	 * @param dataPoint   data point to work with.
	 * @return true if dialog opened successfully, otherwise false.
	 */
	public static boolean openViewDialog(final Shell parentShell, final DataPoint dataPoint) {
		final DataPointDialog dialog = new DataPointDialog(parentShell, null,
				AdminDataPoliciesMessages.get().DataPolicyEditor_DataPoints_Dialog_ViewTitle,
				AdminDataPoliciesImageRegistry.getImage(AdminDataPoliciesImageRegistry.IMAGE_DATA_POLICY_CREATE), dataPoint);
		return dialog.open() == 0;
	}

	@Override
	protected void createEpDialogContent(final IEpLayoutComposite dialogComposite) {
		final IEpLayoutData labelData = dialogComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL);
		final IEpLayoutData fieldData = dialogComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false);

		EpState state = dataPoint.isPersisted() ? EpState.DISABLED : EpState.EDITABLE;

		dialogComposite.addLabelBoldRequired(AdminDataPoliciesMessages.get().DataPolicyEditor_DataPoints_Dialog_Label_Name,
				state, labelData);
		dataPointName = dialogComposite.addTextField(state, fieldData);

		dialogComposite.addLabelBoldRequired(AdminDataPoliciesMessages.get().DataPolicyEditor_DataPoints_Dialog_Label_DataLocation,
				state, labelData);
		dataPointLocation = dialogComposite.addComboBox(state, fieldData);

		dialogComposite.addLabelBoldRequired(AdminDataPoliciesMessages.get().DataPolicyEditor_DataPoints_Dialog_Label_DataKey,
				state, labelData);
		dataPointKey = dialogComposite.addComboBox(state, fieldData);

		dialogComposite.addLabelBoldRequired(AdminDataPoliciesMessages.get().DataPolicyEditor_DataPoints_Dialog_Label_DescriptionKey,
				state, labelData);
		dataPointDescriptionKey = dialogComposite.addTextField(state, fieldData);

		dialogComposite.addLabelBold(AdminDataPoliciesMessages.get().DataPolicyEditor_DataPoints_Dialog_Label_Removable,
				labelData);
		dataPointRemovable = dialogComposite.addCheckBoxButton("", state, fieldData);
	}

	@Override
	protected String getPluginId() {
		return AdminDataPoliciesPlugin.PLUGIN_ID;
	}

	@Override
	public Object getModel() {
		return dataPoint;
	}

	@Override
	protected void populateControls() {
		boolean persisted = dataPoint.isPersisted();
		getOkButton().setVisible(!persisted);

		if (persisted) {
			dataPointName.setText(dataPoint.getName());

			dataPointLocation.add(dataPoint.getDataLocation());
			dataPointLocation.select(0);
			dataPointKey.add(dataPoint.getDataKey());
			dataPointKey.select(0);

			dataPointDescriptionKey.setText(dataPoint.getDescriptionKey());
			dataPointRemovable.setSelection(dataPoint.isRemovable());
		} else {
			locationAndSupportedFields = dataPointValueService.getLocationAndSupportedFields();
			locationAndSupportedFields.keySet().forEach(dataPointLocation::add);

			dataPointRemovable.setSelection(true); //enabled by default

			dataPointLocation.addModifyListener((ModifyListener) modifyEvent -> {
				String location = ((CCombo) modifyEvent.getSource()).getText();
				clearDataPointKeySelection();
				Set<String> strings = locationAndSupportedFields.get(location);
				dataPointKey.setEditable(strings.isEmpty());
				strings.forEach(dataPointKey::add);
			});
		}
	}

	private void clearDataPointKeySelection() {
		dataPointKey.deselect(dataPointKey.getSelectionIndex());
		dataPointKey.clearSelection();
		dataPointKey.removeAll();
		dataPoint.setDataKey(null);
	}

	@Override
	protected void bindControls() {
		EpControlBindingProvider binder = EpControlBindingProvider.getInstance();

		IValidator validatorMax255Required = new CompoundValidator(EpValidatorFactory.MAX_LENGTH_255, EpValidatorFactory.REQUIRED);

		binder.bind(context, dataPointName, dataPoint, "name", //$NON-NLS-1$
				validatorMax255Required, null, true);

		binder.bind(context, dataPointLocation, dataPoint, "dataLocation", EpValidatorFactory.REQUIRED,
				new Converter(Integer.class, String.class) {
					@Override
					public Object convert(final Object input) {
						return dataPointLocation.getItems()[Integer.parseInt(String.valueOf(input))];
					}
				}, true);

		binder.bind(context, dataPointDescriptionKey, dataPoint, "descriptionKey", //$NON-NLS-1$
				validatorMax255Required, null, true);

		binder.bind(context, dataPointRemovable, dataPoint, "removable");
		EpDialogSupport.create(this, context);
	}

	@Override
	protected void okPressed() {
		if (!dataPoint.isPersisted()) {
			final String name = dataPointName.getText();
			if (!StringUtils.isEmpty(name)) {
				final DataPoint existingDataPoint = dataPointService.findByName(name);
				if (existingDataPoint != null) {
					openWarningDialog(AdminDataPoliciesMessages.get().DataPolicyEditor_DataPoints_DuplicateNameTitle,
							AdminDataPoliciesMessages.get().DataPolicyEditor_DataPoints_DuplicateNameMessage);
					return;
				}
			}
			final String dataKey = dataPointKey.getText();
			if (StringUtils.isEmpty(dataKey)) {
				openWarningDialog(AdminDataPoliciesMessages.get().DataPolicyEditor_DataPoints_KeyRequiredTitle,
						AdminDataPoliciesMessages.get().DataPolicyEditor_DataPoints_KeyRequiredMessage);
				return;
			} else if (!dataPointValueService.validateKeyForLocation(dataPoint.getDataLocation(), dataKey)) {
				openWarningDialog(AdminDataPoliciesMessages.get().DataPolicyEditor_DataPoints_KeyIsNotValidTitle,
						AdminDataPoliciesMessages.get().DataPolicyEditor_DataPoints_KeyIsNotValidMessage);
				return;
			}
			final DataPoint existingDataPoint = dataPointService.findByDataLocationAndDataKey(dataPoint.getDataLocation(), dataKey);
			if (existingDataPoint != null) {
				openWarningDialog(AdminDataPoliciesMessages.get().DataPolicyEditor_DataPoints_KeyAlreadyInUseTitle,
						String.format(AdminDataPoliciesMessages.get().DataPolicyEditor_DataPoints_KeyAlreadyInUseMessage,
								existingDataPoint.getName()));
				return;
			}
			dataPoint.setDataKey(dataKey);
			dataPoint = dataPointService.save(dataPoint);
			tableView.add(dataPoint);
		}
		super.okPressed();
	}

	@Override
	protected String getTitle() {
		return title;
	}

	@Override
	protected String getWindowTitle() {
		return title;
	}

	@Override
	protected Image getWindowImage() {
		return image;
	}

	@Override
	protected String getInitialMessage() {
		return ""; //$NON-NLS-1$
	}

	private void openWarningDialog(final String title, final String message) {
		MessageDialog.openWarning(this.getShell(), title, message);
	}
}
