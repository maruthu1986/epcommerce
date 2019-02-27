/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.cmclient.fulfillment.editors.customer;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.editors.AbstractCmClientEditorPageSectionPart;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.cmclient.core.util.DateTimeUtilFactory;
import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;
import com.elasticpath.cmclient.fulfillment.FulfillmentPlugin;
import com.elasticpath.cmclient.fulfillment.editors.customer.dialogs.CustomerRemoveDataPolicyDataDialog;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.datapolicy.ConsentAction;
import com.elasticpath.domain.datapolicy.CustomerConsent;
import com.elasticpath.domain.datapolicy.DataPolicy;
import com.elasticpath.service.datapolicy.CustomerConsentService;
import com.elasticpath.service.datapolicy.DataPointValueService;
import com.elasticpath.service.datapolicy.impl.DataPointValue;

/**
 * Represents the data policies section that displays data policies and data point values.
 * This section will be visible only to CSRs with a permission to manage data polices.
 *
 * The CSRs will be able to delete data point values.
 */
public class CustomerDataPoliciesSection extends AbstractCmClientEditorPageSectionPart implements SelectionListener {

	private static final String CUSTOMER_DATA_POLICIES_TABLE = "Customer Data Policies Table"; //$NON-NLS-1$
	private static final CustomerConsentService CUSTOMER_CONSENT_SERVICE =
		(CustomerConsentService) ServiceLocator.getService(ContextIdNames.CUSTOMER_CONSENT_SERVICE);

	private static final DataPointValueService DATA_POINT_VALUE_SERVICE = ServiceLocator.getService(ContextIdNames.DATA_POINT_VALUE_SERVICE);

	private static final int POLICY_NAME_COLUMN_WIDTH = 278;
	private static final int CONSENT_GIVEN_COLUMN_WIDTH = 107;
	private static final int CONSENT_UPDATED_COLUMN_WIDTH = 168;
	private static final int POLICY_STATE_COLUMN_WIDTH = 70;

	private static final int POLICIES_TABLE_MAX_WIDTH = 600;
	private static final int POLICIES_TABLE_HEIGHT_HINT = 110;

	private static final int POINTS_TABLE_MAX_WIDTH = 700;
	private static final int POINTS_TABLE_HEIGHT_HINT = 200;
	private static final int POINTS_COMMON_COLUMN_WIDTH = 150;
	private static final int POINTS_REMOVABLE_COLUMN_WIDTH = 100;

	private static final int TABLE_MAX_HEIGHT = 400;

	private static final int COLUMN_THREE_INDEX = 3;
	private static final int COLUMN_FOUR_INDEX = 4;

	private List<CustomerConsent> customerConsents;
	private final Customer customer;

	private Button showDisabledPolicies;
	private Button deletePolicyDataButton;
	private Button viewDataPointsButton;

	private IEpTableViewer dataPoliciesTableViewer;
	private IEpTableViewer dataPointsTableViewer;

	/**
	 * constructor.
	 *
	 * @param formPage the form page
	 * @param editor the editor containing this Section Constructor to create a new Section in an editor's FormPage.
	 */
	CustomerDataPoliciesSection(final FormPage formPage, final AbstractCmClientFormEditor editor) {
		super(formPage, editor, ExpandableComposite.TITLE_BAR);
		this.customer = (Customer) editor.getModel();
		this.customerConsents = CUSTOMER_CONSENT_SERVICE.findWithActiveDataPoliciesByCustomerGuid(this.customer.getGuid(), false);
	}

	@Override
	protected void bindControls(final DataBindingContext bindingContext) {
		// nothing
	}

	@Override
	protected void createControls(final Composite client, final FormToolkit toolkit) {

		showDisabledPolicies = toolkit.createButton(client, FulfillmentMessages.get().ShowDisabledPolicies_Label, SWT.CHECK);
		showDisabledPolicies.setLayoutData(new TableWrapData(TableWrapData.LEFT, TableWrapData.FILL, 1, 2));

		final IEpLayoutComposite mainPane = CompositeFactory.createTableWrapLayoutComposite(client, 2, false);
		((TableWrapLayout) mainPane.getSwtComposite().getLayout()).leftMargin = 0; //left align table with checkbox

		mainPane.setLayoutData(new TableWrapData(TableWrapData.FILL, TableWrapData.FILL));

		final IEpLayoutData tableData = mainPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false);
		final IEpLayoutData dataPointsTableData = mainPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true);

		//data policies table
		dataPoliciesTableViewer = mainPane.addTableViewer(false, EpControlFactory.EpState.READ_ONLY, tableData, CUSTOMER_DATA_POLICIES_TABLE);

		((TableWrapData) dataPoliciesTableViewer.getSwtTable().getLayoutData()).maxHeight = TABLE_MAX_HEIGHT;
		((TableWrapData) dataPoliciesTableViewer.getSwtTable().getLayoutData()).heightHint = POLICIES_TABLE_HEIGHT_HINT;
		((TableWrapData) dataPoliciesTableViewer.getSwtTable().getLayoutData()).maxWidth = POLICIES_TABLE_MAX_WIDTH;

		dataPoliciesTableViewer.addTableColumn(FulfillmentMessages.get().DataPolicyName_Label, POLICY_NAME_COLUMN_WIDTH);
		dataPoliciesTableViewer.addTableColumn(FulfillmentMessages.get().DataPolicyState_Label, POLICY_STATE_COLUMN_WIDTH);
		dataPoliciesTableViewer.addTableColumn(FulfillmentMessages.get().DataPolicyConsentGiven_Label, CONSENT_GIVEN_COLUMN_WIDTH);
		dataPoliciesTableViewer.addTableColumn(FulfillmentMessages.get().DataPolicyConsentUpdated_Label, CONSENT_UPDATED_COLUMN_WIDTH);

		dataPoliciesTableViewer.setLabelProvider(new DataPolicyLabelProvider());
		dataPoliciesTableViewer.setContentProvider(new DataPolicyContentProvider());
		dataPoliciesTableViewer.setInput(this.customerConsents);
		dataPoliciesTableViewer.getSwtTableViewer().setSorter(new ViewerSorter() {
			@Override
			public int compare(final Viewer viewer, final Object obj1, final Object obj2) {
				return ((CustomerConsent) obj1).getDataPolicy().getPolicyName().compareTo(((CustomerConsent) obj2).getDataPolicy().getPolicyName());
			}
		});

		dataPoliciesTableViewer.getSwtTableViewer().addSelectionChangedListener(event -> {
				CustomerDataPoliciesSection.this.deletePolicyDataButton.setEnabled(true);
				CustomerDataPoliciesSection.this.viewDataPointsButton.setEnabled(true);
				CustomerDataPoliciesSection.this.dataPointsTableViewer.getSwtTable().removeAll();
				CustomerDataPoliciesSection.this.dataPointsTableViewer.getSwtTable().setVisible(false);
			}
		);

		final IEpLayoutData buttonPaneData = mainPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL);
		final IEpLayoutComposite buttonsPane = mainPane.addTableWrapLayoutComposite(1, true, buttonPaneData);
		((TableWrapLayout) buttonsPane.getSwtComposite().getLayout()).topMargin = 0; //top align buttons with table
		final IEpLayoutData buttonData = mainPane.createLayoutData();

		this.deletePolicyDataButton = buttonsPane.addPushButton(FulfillmentMessages.get().DeletePolicyData_Title, CoreImageRegistry
			.getImage(CoreImageRegistry.IMAGE_REMOVE), EpControlFactory.EpState.DISABLED, buttonData);

		this.viewDataPointsButton = buttonsPane.addPushButton(FulfillmentMessages.get().ViewDataPoints_Label, CoreImageRegistry
			.getImage(CoreImageRegistry.IMAGE_ADDRESS), EpControlFactory.EpState.DISABLED, buttonData);

		//data points table
		dataPointsTableViewer = mainPane.addTableViewer(false, EpControlFactory.EpState.READ_ONLY, dataPointsTableData,
			"Customer Data Points Table"); //$NON-NLS-1$

		((TableWrapData) dataPointsTableViewer.getSwtTable().getLayoutData()).maxHeight = TABLE_MAX_HEIGHT;
		((TableWrapData) dataPointsTableViewer.getSwtTable().getLayoutData()).heightHint = POINTS_TABLE_HEIGHT_HINT;
		((TableWrapData) dataPointsTableViewer.getSwtTable().getLayoutData()).maxWidth = POINTS_TABLE_MAX_WIDTH;

		dataPointsTableViewer.addTableColumn(FulfillmentMessages.get().DataPointName_Label, POINTS_COMMON_COLUMN_WIDTH);
		dataPointsTableViewer.addTableColumn(FulfillmentMessages.get().DataPointRemovable_Label, POINTS_REMOVABLE_COLUMN_WIDTH);
		dataPointsTableViewer.addTableColumn(FulfillmentMessages.get().DataPointValue_Label, POINTS_COMMON_COLUMN_WIDTH);
		dataPointsTableViewer.addTableColumn(FulfillmentMessages.get().DataPointValueCreated_Label, POINTS_COMMON_COLUMN_WIDTH);
		dataPointsTableViewer.addTableColumn(FulfillmentMessages.get().DataPointValueLastUpdated_Label, POINTS_COMMON_COLUMN_WIDTH);

		dataPointsTableViewer.setLabelProvider(new DataPointLabelProvider());
		dataPointsTableViewer.setContentProvider(new DataPointContentProvider());
		dataPointsTableViewer.getSwtTableViewer().setSorter(new ViewerSorter() {
			@Override
			public int compare(final Viewer viewer, final Object obj1, final Object obj2) {
				return ((DataPointValue) obj1).getDataPointName().compareTo(((DataPointValue) obj2).getDataPointName());
			}
		});

		dataPointsTableViewer.getSwtTable().setVisible(false);
	}

	@Override
	protected void populateControls() {
		this.deletePolicyDataButton.addSelectionListener(this);
		this.viewDataPointsButton.addSelectionListener(this);
		this.showDisabledPolicies.addSelectionListener(this);
	}

	/**
	 * This label provider returns the text that should appear in each column for a given <code>CustomerConsent</code> object.
	 */
	class DataPolicyLabelProvider extends LabelProvider implements ITableLabelProvider {

		@Override
		public Image getColumnImage(final Object element, final int columnIndex) {

			return null;
		}

		@Override
		public String getColumnText(final Object element, final int columnIndex) {
			final CustomerConsent consent = (CustomerConsent) element;

			switch (columnIndex) {
				case 0:
					return consent.getDataPolicy().getPolicyName();
				case 1:
					return consent.getDataPolicy().getState().getName();
				case 2:
					return convertBooleanToYesNo(consent.getAction() == ConsentAction.GRANTED);
				case COLUMN_THREE_INDEX:
					return DateTimeUtilFactory.getDateUtil().formatAsDateTime(consent.getConsentDate());
				default:
					return "unknown @ column" + columnIndex; //$NON-NLS-1$
			}
		}
	}

	/**
	 * The content provider class is responsible for providing objects to the view. It can wrap existing objects in adapters or
	 * simply return objects as-is. These objects may be sensitive to the current input of the view, or ignore it and always show
	 * the same content.
	 */
	class DataPolicyContentProvider implements IStructuredContentProvider {

		/**
		 * Gets the order from the list of order for each row.
		 * 
		 * @param inputElement the input order element
		 * @return Object[] the returned input
		 */
		@Override
		@SuppressWarnings("synthetic-access")
		public Object[] getElements(final Object inputElement) {
			return ((List<CustomerConsent>) inputElement).toArray();
		}

		/**
		 * dispose the provider.
		 */
		@Override
		public void dispose() {
			// does nothing
		}

		/**
		 * Notify the provider the input has changed.
		 * 
		 * @param viewer the epTableViewer
		 * @param oldInput the previous input
		 * @param newInput the current selected input
		 */
		@Override
		public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
			// does nothing
		}
	}

	/**
	 * The data point label provider.
	 */
	class DataPointLabelProvider extends LabelProvider implements ITableLabelProvider {

		@Override
		public Image getColumnImage(final Object element, final int columnIndex) {

			return null;
		}

		@Override
		public String getColumnText(final Object element, final int columnIndex) {
			final DataPointValue dataPointPointValue = (DataPointValue) element;

			switch (columnIndex) {
				case 0:
					return dataPointPointValue.getDataPointName();
				case 1:
					return convertBooleanToYesNo(dataPointPointValue.isRemovable());
				case 2:
					return StringUtils.defaultString(dataPointPointValue.getValue(), "");
				case COLUMN_THREE_INDEX:
					return dataPointPointValue.getCreatedDate() == null
						? ""
						: DateTimeUtilFactory.getDateUtil().formatAsDateTime(dataPointPointValue.getCreatedDate());
				case COLUMN_FOUR_INDEX:
					return dataPointPointValue.getLastModifiedDate() == null
						? ""
						: DateTimeUtilFactory.getDateUtil().formatAsDateTime(dataPointPointValue.getLastModifiedDate());
				default:
					return "unknown @ column" + columnIndex; //$NON-NLS-1$
			}
		}
	}

	/**
	 * The data point content provider.
	 */
	class DataPointContentProvider implements IStructuredContentProvider {

		/**
		 * Gets the order from the list of order for each row.
		 *
		 * @param inputElement the input order element
		 * @return Object[] the returned input
		 */
		@Override
		@SuppressWarnings("synthetic-access")
		public Object[] getElements(final Object inputElement) {
			return ((List<DataPointValue>) inputElement).toArray();
		}

		/**
		 * dispose the provider.
		 */
		@Override
		public void dispose() {
			// does nothing
		}

		/**
		 * Notify the provider the input has changed.
		 *
		 * @param viewer the epTableViewer
		 * @param oldInput the previous input
		 * @param newInput the current selected input
		 */
		@Override
		public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
			// does nothing
		}
	}

	@Override
	protected String getSectionDescription() {
		return ""; //$NON-NLS-1$
	}

	/**
	 * Not used.
	 * 
	 * @param event the event object
	 */
	@Override
	public void widgetDefaultSelected(final SelectionEvent event) {
		// nothing
	}

	/**
	 * Invoked on selection event.
	 * 
	 * @param event the event object
	 *
	 */
	@Override
	public void widgetSelected(final SelectionEvent event) {

		if (event.getSource() == this.viewDataPointsButton) {

			CustomerConsent selectedCustomerConsent  = getSelectedCustomerConsent();
			DataPolicy dataPolicy = selectedCustomerConsent.getDataPolicy();

			try {

				initDataPointValuesTableWithData(dataPolicy.getGuid());

			} catch (Exception e) {
				Status status = new Status(IStatus.ERROR, FulfillmentPlugin.PLUGIN_ID, e.getMessage(), e);
				ErrorDialog.openError(new Shell(Display.getCurrent()), FulfillmentMessages.get().Error_Title,
					String.format(FulfillmentMessages.get().DataPointValueRemovalError_Message, dataPolicy.getPolicyName()), status);
			}

		} else if (event.getSource() == this.deletePolicyDataButton) {

			CustomerConsent selectedCustomerConsent  = getSelectedCustomerConsent();
			DataPolicy dataPolicy = selectedCustomerConsent.getDataPolicy();

			try {

				/* must take the fresh set of data points because the data points and their values can be updated from another browser or DB client
				as well as this window (once data point values are removed for the selected policy, there is no point deleting them again)
				 */
				Collection<DataPointValue> dataPointValues = getCustomerDataPointValuesByPolicyGuidFromDb(dataPolicy.getGuid()).stream()
					.filter(this::isDataPointValueRemovableAndNonEmpty)
					.collect(Collectors.toList());

				if (dataPointValues.isEmpty()) {

					MessageDialog.openInformation(new Shell(Display.getCurrent()), FulfillmentMessages.get().DeletePolicyData_Title,
						FulfillmentMessages.get().NoRemovableDataPointValues_Message);

					return;
				}

				Dialog dialog = new CustomerRemoveDataPolicyDataDialog(this.getManagedForm().getForm().getShell(), customer.getGuid(),
					dataPolicy.getPolicyName(), dataPolicy.getGuid(), dataPointValues);

				if (dialog.open() == CustomerRemoveDataPolicyDataDialog.DELETE_BUTTON_INDEX) { //delete selected
					initDataPointValuesTableWithData(dataPolicy.getGuid());
				}

			} catch (Exception e) {
				Status status = new Status(IStatus.ERROR, FulfillmentPlugin.PLUGIN_ID, e.getMessage(), e);
				ErrorDialog.openError(new Shell(Display.getCurrent()), FulfillmentMessages.get().Error_Title,
					String.format(FulfillmentMessages.get().DataPointValueRemovalError_Message, dataPolicy.getPolicyName()),
					status);
			}

		} else if (event.getSource() == this.showDisabledPolicies) {

			boolean isSelected = showDisabledPolicies.getSelection();

			this.customerConsents = CUSTOMER_CONSENT_SERVICE.findWithActiveDataPoliciesByCustomerGuid(this.customer.getGuid(), isSelected);

			this.dataPoliciesTableViewer.setInput(customerConsents);
			this.dataPoliciesTableViewer.getSwtTableViewer().refresh();

			if (!isSelected) {
				this.deletePolicyDataButton.setEnabled(false);
				this.viewDataPointsButton.setEnabled(false);
			}
		}
	}

	private boolean isDataPointValueRemovableAndNonEmpty(final DataPointValue dataPointValue) {
		return dataPointValue.isRemovable() && StringUtils.isNotBlank(dataPointValue.getValue());
	}

	private Collection<DataPointValue> getCustomerDataPointValuesByPolicyGuidFromDb(final String dataPolicyGuid) {
		return DATA_POINT_VALUE_SERVICE.getCustomerDataPointValuesForStoreByPolicyGuid(customer.getGuid(), customer.getStoreCode(), dataPolicyGuid);
	}

	private void initDataPointValuesTableWithData(final String dataPolicyGuid) {
		Collection<DataPointValue> dataPointValues = getCustomerDataPointValuesByPolicyGuidFromDb(dataPolicyGuid);

		this.dataPointsTableViewer.setInput(dataPointValues);
		this.dataPointsTableViewer.getSwtTableViewer().refresh();
		this.dataPointsTableViewer.getSwtTable().setVisible(true);
	}

	private CustomerConsent getSelectedCustomerConsent() {
		return (CustomerConsent) this.dataPoliciesTableViewer.getSwtTable().getSelection()[0].getData();
	}


	private String convertBooleanToYesNo(final Boolean booleanToConvert) {
		return StringUtils.replaceEach(booleanToConvert.toString(),
			new String[] {"true", "false"}, new String[] {FulfillmentMessages.get().Yes_Text, FulfillmentMessages.get().No_Text});
	}
}
