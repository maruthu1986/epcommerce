/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.cmclient.admin.datapolicies.views;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.elasticpath.cmclient.admin.datapolicies.AdminDataPoliciesImageRegistry;
import com.elasticpath.cmclient.admin.datapolicies.AdminDataPoliciesMessages;
import com.elasticpath.cmclient.admin.datapolicies.AdminDataPoliciesPlugin;
import com.elasticpath.cmclient.admin.datapolicies.actions.CreateDataPolicyAction;
import com.elasticpath.cmclient.admin.datapolicies.actions.DisableDataPolicyAction;
import com.elasticpath.cmclient.admin.datapolicies.actions.EditDataPolicyAction;
import com.elasticpath.cmclient.admin.datapolicies.event.DataPolicyEventListener;
import com.elasticpath.cmclient.admin.datapolicies.event.DataPolicyEventService;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.cmclient.core.util.DateTimeUtilFactory;
import com.elasticpath.cmclient.core.views.AbstractListView;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.datapolicy.DataPolicy;
import com.elasticpath.domain.datapolicy.DataPolicyState;
import com.elasticpath.service.datapolicy.DataPolicyService;

/**
 * The Data Policy list view controller.
 */
public class DataPolicyListView extends AbstractListView implements DataPolicyEventListener {

	/**
	 * The View's ID.
	 */
	public static final String VIEW_ID = "com.elasticpath.cmclient.admin.datapolicies.views.DataPolicyListView"; //$NON-NLS-1$

	private static final String DATA_POLICY_LIST_TABLE = "Data policy list";

	private static final int INDEX_NAME = 0;

	private static final int INDEX_STATE = 1;

	private static final int INDEX_START_DATE = 2;

	private static final int INDEX_END_DATE = 3;

	private static final int DATAPOLICY_NAME_COLUMN_WIDTH = 250;

	private static final int DATAPOLICY_STATE_COLUMN_WIDTH = 100;

	private static final int DATAPOLICY_START_DATE_COLUMN_WIDTH = 200;

	private static final int DATAPOLICY_END_DATE_COLUMN_WIDTH = 200;

	private final DataPolicyService dataPolicyService;

	private Action editViewDataPolicyAction;

	private Action createDataPolicyAction;

	private Action disableDataPolicyAction;

	/**
	 * The constructor.
	 */
	public DataPolicyListView() {
		super(false, DATA_POLICY_LIST_TABLE);
		this.dataPolicyService = ServiceLocator.getService(ContextIdNames.DATA_POLICY_SERVICE);
		DataPolicyEventService.getInstance().registerDataPolicyEventListener(this);
	}

	@Override
	public void dispose() {
		DataPolicyEventService.getInstance().unregisterDataPolicyEventListener(this);
		super.dispose();
	}

	@Override
	protected String getPluginId() {
		return AdminDataPoliciesPlugin.PLUGIN_ID;
	}

	@Override
	protected void initializeViewToolbar() {
		final Separator attributeActionGroup = new Separator("dataPolicyActionGroup"); //$NON-NLS-1$
		getToolbarManager().add(attributeActionGroup);

		editViewDataPolicyAction = new EditDataPolicyAction(this, AdminDataPoliciesMessages
				.get().EditDataPolicy, AdminDataPoliciesImageRegistry.IMAGE_DATA_POLICY_EDIT);
		addDoubleClickAction(editViewDataPolicyAction);

		createDataPolicyAction = new CreateDataPolicyAction(this, AdminDataPoliciesMessages.get().CreateDataPolicy,
				AdminDataPoliciesImageRegistry.IMAGE_DATA_POLICY_CREATE);

		disableDataPolicyAction = new DisableDataPolicyAction(this, AdminDataPoliciesMessages.get().DisableDataPolicy,
				AdminDataPoliciesImageRegistry.IMAGE_DATA_POLICY_DISABLE);
		disableDataPolicyAction.setEnabled(false);

		final ActionContributionItem editDataPolicyActionContributionItem = new ActionContributionItem(editViewDataPolicyAction);
		final ActionContributionItem createDataPolicyActionContributionItem = new ActionContributionItem(createDataPolicyAction);
		final ActionContributionItem disableDataPolicyActionContributionItem = new ActionContributionItem(disableDataPolicyAction);

		editDataPolicyActionContributionItem.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		createDataPolicyActionContributionItem.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		disableDataPolicyActionContributionItem.setMode(ActionContributionItem.MODE_FORCE_TEXT);

		getToolbarManager().appendToGroup(attributeActionGroup.getGroupName(), editDataPolicyActionContributionItem);
		getToolbarManager().appendToGroup(attributeActionGroup.getGroupName(), createDataPolicyActionContributionItem);
		getToolbarManager().appendToGroup(attributeActionGroup.getGroupName(), disableDataPolicyActionContributionItem);

		this.getViewer().addSelectionChangedListener(event -> {
			final Optional<DataPolicy> dataPolicyOptional = getSelectedDataPolicy();
			if (dataPolicyOptional.isPresent()) {
				DataPolicy dataPolicy = dataPolicyOptional.get();
				toggleActions(dataPolicy.isEditable(), dataPolicy.isNotDisabled());
			}
		});
	}

	private void toggleActions(final boolean isEditable, final boolean isNotDisabled) {
		disableDataPolicyAction.setEnabled(isNotDisabled);
		if (isEditable) {
			String editDataPolicy = AdminDataPoliciesMessages.get().EditDataPolicy;
			ImageDescriptor imageDataPolicyEdit = AdminDataPoliciesImageRegistry.IMAGE_DATA_POLICY_EDIT;
			editViewDataPolicyAction.setText(editDataPolicy);
			editViewDataPolicyAction.setImageDescriptor(imageDataPolicyEdit);
		} else {
			String viewDataPolicy = AdminDataPoliciesMessages.get().ViewDataPolicy;
			ImageDescriptor imageDataPolicyOpen = AdminDataPoliciesImageRegistry.IMAGE_DATA_POLICY_OPEN;
			editViewDataPolicyAction.setText(viewDataPolicy);
			editViewDataPolicyAction.setImageDescriptor(imageDataPolicyOpen);
		}
	}

	@Override
	protected Object[] getViewInput() {
		List<DataPolicy> dataPolicyList = dataPolicyService.list();
		DataPolicy[] dataPolicies = dataPolicyList.toArray(new DataPolicy[dataPolicyList.size()]);
		Arrays.sort(dataPolicies, Comparator.comparing(DataPolicy::getUidPk));
		return dataPolicies;
	}

	@Override
	protected void initializeTable(final IEpTableViewer epTableViewer) {
		String[] columnNames = new String[]{
				AdminDataPoliciesMessages.get().DataPolicyListView_Column_DataPolicyName,
				AdminDataPoliciesMessages.get().DataPolicyListView_Column_DataPolicyState,
				AdminDataPoliciesMessages.get().DataPolicyListView_Column_DataPolicyStartDate,
				AdminDataPoliciesMessages.get().DataPolicyListView_Column_DataPolicyEndDate
		};

		final int[] columnWidths = new int[]{
				DATAPOLICY_NAME_COLUMN_WIDTH,
				DATAPOLICY_STATE_COLUMN_WIDTH,
				DATAPOLICY_START_DATE_COLUMN_WIDTH,
				DATAPOLICY_END_DATE_COLUMN_WIDTH
		};

		for (int i = 0; i < columnNames.length; i++) {
			epTableViewer.addTableColumn(columnNames[i], columnWidths[i]);
		}
	}

	@Override
	protected String getPartId() {
		return VIEW_ID;
	}

	@Override
	protected ITableLabelProvider getViewLabelProvider() {
		return new DataPolicyListLabelProvider();
	}

	/**
	 * Get selected Data policy from table view.
	 *
	 * @return optional of selected data policy.
	 */
	public Optional<DataPolicy> getSelectedDataPolicy() {
		final IStructuredSelection selection = (IStructuredSelection) getViewer().getSelection();
		return Optional.ofNullable(selection)
				.filter(sel -> !sel.isEmpty())
				.map(sel -> (DataPolicy) sel.getFirstElement());
	}

	@Override
	public void dataPolicyChanged(final ItemChangeEvent<DataPolicy> event) {
		final DataPolicy dataPolicy = event.getItem();
		switch (event.getEventType()) {
			case ADD:
				getViewer().add(dataPolicy);
				break;
			case CHANGE:
			case REMOVE:
				getViewer().update(dataPolicy, null);
				getViewer().refresh(dataPolicy, true);
				break;
			default:
				break;
		}
//		refresh selection to notify selection listener to update action buttons
		getViewer().setSelection(getViewer().getSelection());
	}

	private String resolveStateLabel(final DataPolicyState state) {
		switch (state.getName()) {
			case DataPolicyState.ACTIVE_NAME:
				return AdminDataPoliciesMessages.get().DataPolicyEditor_SummaryPage_State_Active;
			case DataPolicyState.DISABLED_NAME:
				return AdminDataPoliciesMessages.get().DataPolicyEditor_SummaryPage_State_Disabled;
			default:
				return AdminDataPoliciesMessages.get().DataPolicyEditor_SummaryPage_State_Draft;
		}
	}

	/**
	 * Data policy list view label provider.
	 */
	private class DataPolicyListLabelProvider extends LabelProvider implements ITableLabelProvider {

		@Override
		public Image getColumnImage(final Object element, final int columnIndex) {
			return null;
		}

		@Override
		public String getColumnText(final Object element, final int columnIndex) {
			DataPolicy dataPolicy = (DataPolicy) element;

			switch (columnIndex) {
				case INDEX_NAME:
					return dataPolicy.getPolicyName();
				case INDEX_STATE:
					return resolveStateLabel(dataPolicy.getState());
				case INDEX_START_DATE:
					return DateTimeUtilFactory.getDateUtil().formatAsDateTime(dataPolicy.getStartDate());
				case INDEX_END_DATE:
					return Optional.ofNullable(DateTimeUtilFactory.getDateUtil().formatAsDateTime(dataPolicy.getEndDate()))
							.orElse("");
				default:
					return ""; //$NON-NLS-1$
			}
		}
	}
}
