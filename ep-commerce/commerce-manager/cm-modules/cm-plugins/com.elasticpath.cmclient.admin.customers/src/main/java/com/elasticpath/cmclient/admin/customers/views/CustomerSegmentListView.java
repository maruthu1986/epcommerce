/**
 * Copyright (c) Elastic Path Software Inc., 2013
 *
 */
package com.elasticpath.cmclient.admin.customers.views;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.elasticpath.cmclient.admin.customers.AdminCustomersImageRegistry;
import com.elasticpath.cmclient.admin.customers.AdminCustomersMessages;
import com.elasticpath.cmclient.admin.customers.AdminCustomersPlugin;
import com.elasticpath.cmclient.admin.customers.actions.CreateCustomerSegmentAction;
import com.elasticpath.cmclient.admin.customers.actions.DeleteCustomerSegmentAction;
import com.elasticpath.cmclient.admin.customers.actions.EditCustomerSegmentAction;
import com.elasticpath.cmclient.admin.customers.event.CustomerSegmentEventListener;
import com.elasticpath.cmclient.admin.customers.event.CustomerSegmentEventService;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.cmclient.core.views.AbstractListView;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.customer.CustomerGroup;
import com.elasticpath.service.customer.CustomerGroupService;

/**
 * View to show and allow the manipulation of the available customer segments in CM.
 */
public class CustomerSegmentListView extends AbstractListView implements CustomerSegmentEventListener {
	/** The view ID. */
	public static final String VIEW_ID = "com.elasticpath.cmclient.admin.customers.views.CustomerSegmentListView"; //$NON-NLS-1$	

	private static final String CUSTOMER_SEGMENT_LIST_TABLE = "Customer Segment List"; //$NON-NLS-1$

	private static final Logger LOG = Logger.getLogger(CustomerSegmentListView.class);

	// Column indices
	private static final int INDEX_SEGMENT_IMAGE = 0;

	private static final int INDEX_SEGMENT_NAME = 1;
	
	private static final int INDEX_SEGMENT_DESCRIPTION = 2;
	
	private static final int INDEX_SEGMENT_ENABLED = 3;

	private Action createCustomerSegmentAction;
	
	private Action editCustomerSegmentAction;

	private Action deleteCustomerSegmentAction;
	
	private final CustomerGroupService customerGroupService;

	/**
	 * The constructor.
	 */
	public CustomerSegmentListView() {
		super(false, CUSTOMER_SEGMENT_LIST_TABLE);
		customerGroupService = ServiceLocator.getService(ContextIdNames.CUSTOMER_GROUP_SERVICE);
		CustomerSegmentEventService.getInstance().registerCustomerSegmentEventListener(this);
	}

	@Override
	public void dispose() {
		CustomerSegmentEventService.getInstance().unregisterCustomerSegmentEventListener(this);
		super.dispose();
	}

	@Override
	public void customerSegmentChanged(final ItemChangeEvent<CustomerGroup> event) {
		final CustomerGroup changedCustomerGroup = event.getItem();
		switch (event.getEventType()) {
		case ADD:
			getViewer().add(changedCustomerGroup);
			break;
		case CHANGE:
			updateTable(changedCustomerGroup);
			break;
		case REMOVE:
			getViewer().remove(changedCustomerGroup);
			closeMatchingSegmentEditor(changedCustomerGroup);
			break;
		default:
			break;
		}

		//refresh selection to notify selection listener to update action buttons
		getViewer().setSelection(getViewer().getSelection());		
	}

	private void closeMatchingSegmentEditor(final CustomerGroup customerGroup) {
		final CustomerSegmentEditorInput tempEditorInput = new CustomerSegmentEditorInput(
				customerGroup.getName(), customerGroup.getUidPk(), CustomerGroup.class);
		
		IEditorReference[] editorReferences = activePage().getEditorReferences();
		for (IEditorReference iEditorReference : editorReferences) {
			try {
				if (iEditorReference.getEditorInput().equals(tempEditorInput)) {
					activePage().closeEditors(new IEditorReference[] {iEditorReference}, false);
					break;
				}
			} catch (final PartInitException e) {
				LOG.error("Workbench part cannot be initialized correctly", e); //$NON-NLS-1$
			}
		}
	}

	private void updateTable(final CustomerGroup changedCustomerGroup) {
		for (final TableItem currTableItem : getViewer().getTable().getItems()) {
			final CustomerGroup customerGroup = (CustomerGroup) currTableItem.getData();
			if (customerGroup.getUidPk() == changedCustomerGroup.getUidPk()) {
				currTableItem.setData(changedCustomerGroup);				
				break;
			}
		}
		getViewer().update(changedCustomerGroup, null);
	}

	private IWorkbenchPage activePage() {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
	}

	@Override
	protected String getPluginId() {
		return AdminCustomersPlugin.PLUGIN_ID;
	}

	/**
	 * Create the toolbar buttons specific to this view.
	 */
	@Override
	protected void initializeViewToolbar() {
		
		final Separator customerSegmentActionGroup = new Separator("customerSegmentActionGroup"); //$NON-NLS-1$
		getToolbarManager().add(customerSegmentActionGroup);

		createCustomerSegmentAction = new CreateCustomerSegmentAction(this, AdminCustomersMessages.get().CreateCustomerSegment,
				AdminCustomersImageRegistry.IMAGE_CUSTOMER_SEGMENT_CREATE);
		createCustomerSegmentAction.setToolTipText(AdminCustomersMessages.get().CreateCustomerSegment);

		editCustomerSegmentAction = new EditCustomerSegmentAction(this, AdminCustomersMessages.get().EditCustomerSegment,
				AdminCustomersImageRegistry.IMAGE_CUSTOMER_SEGMENT_EDIT);
		editCustomerSegmentAction.setToolTipText(AdminCustomersMessages.get().EditCustomerSegment);
		editCustomerSegmentAction.setEnabled(false);
		addDoubleClickAction(editCustomerSegmentAction);
		
		deleteCustomerSegmentAction = new DeleteCustomerSegmentAction(this, AdminCustomersMessages.get().DeleteCustomerSegment,
				AdminCustomersImageRegistry.IMAGE_CUSTOMER_SEGMENT_DELETE);
		deleteCustomerSegmentAction.setToolTipText(AdminCustomersMessages.get().DeleteCustomerSegment);
		deleteCustomerSegmentAction.setEnabled(false);

		final ActionContributionItem createCustomerSegmentActionContributionItem = new ActionContributionItem(createCustomerSegmentAction);
		final ActionContributionItem editCustomerSegmentActionContributionItem = new ActionContributionItem(editCustomerSegmentAction);
		final ActionContributionItem removeCustomerSegmentActionContributionItem = new ActionContributionItem(deleteCustomerSegmentAction);

		createCustomerSegmentActionContributionItem.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		editCustomerSegmentActionContributionItem.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		removeCustomerSegmentActionContributionItem.setMode(ActionContributionItem.MODE_FORCE_TEXT);

		getToolbarManager().appendToGroup(customerSegmentActionGroup.getGroupName(), editCustomerSegmentActionContributionItem);
		getToolbarManager().appendToGroup(customerSegmentActionGroup.getGroupName(), createCustomerSegmentActionContributionItem);
		getToolbarManager().appendToGroup(customerSegmentActionGroup.getGroupName(), removeCustomerSegmentActionContributionItem);
	}

	/**
	 * Initializes the tableViewer's table.
	 * 
	 * @param table the tableviewer's table
	 */
	@Override
	protected void initializeTable(final IEpTableViewer table) {

		final String[] columnNames = new String[] {
				"", //$NON-NLS-1$
				AdminCustomersMessages.get().CustomerSegmentName,
				AdminCustomersMessages.get().CustomerSegmentDescription,
				AdminCustomersMessages.get().CustomerSegmentEnabledFlag
		};

		final int[] columnWidths = new int[] { 1, 170, 250, 80 };

		for (int i = 0; i < columnNames.length; i++) {
			table.addTableColumn(columnNames[i], columnWidths[i]);
		}
		// ---- DOCselectionChanged
// ---- DOCselectionChanged
		table.getSwtTableViewer().addSelectionChangedListener(event -> {
			boolean editable = getSelectedCustomerGroup() != null;
			editCustomerSegmentAction.setEnabled(editable && !isSystemCustomerGroup(getSelectedCustomerGroup()));
			deleteCustomerSegmentAction.setEnabled(editable && !isSystemCustomerGroup(getSelectedCustomerGroup()));
		});
	}

	private boolean isSystemCustomerGroup(final CustomerGroup customerGroup) {
		return customerGroupService.checkIfSystemGroup(customerGroup);
	}

	/**
	 * Return a copy of the table's selected customer group item.
	 * 
	 * @return the copy of the selected customer group
	 */
	public CustomerGroup getSelectedCustomerGroup() {
		final IStructuredSelection selection = (IStructuredSelection) getViewer().getSelection();
		CustomerGroup customerGroup = null;
		if (!selection.isEmpty()) {
			customerGroup = (CustomerGroup) selection.getFirstElement();
		}
		return customerGroup;
	}

	/**
	 * Get the array of objects that will be used as input to the view's TableViewer.
	 * 
	 * @return array of objects that will be used as input to the view's TableViewer
	 */
	@Override
	protected Object[] getViewInput() {
		final List< ? > customerGroups = customerGroupService.list();
		final CustomerGroup[] customerGroupArray = customerGroups.toArray(new CustomerGroup[customerGroups.size()]);
		Arrays.sort(customerGroupArray, Comparator.comparing(CustomerGroup::getName));
		return customerGroupArray;
	}

	@Override
	protected ITableLabelProvider getViewLabelProvider() {
		return new CustomerSegmentListViewLabelProvider();
	}

	/**
	 * Label provider for the view.
	 */
	protected class CustomerSegmentListViewLabelProvider extends LabelProvider implements ITableLabelProvider {

		/**
		 * Get the image to put in each column.
		 * 
		 * @param element the row object
		 * @param columnIndex the column index
		 * @return the Image to put in the column
		 */
		@Override
		public Image getColumnImage(final Object element, final int columnIndex) {
			return null;
		}

		/**
		 * Get the text to put in each column.
		 * 
		 * @param element the row object
		 * @param columnIndex the column index
		 * @return the String to put in the column
		 */
		@Override
		public String getColumnText(final Object element, final int columnIndex) {
			final CustomerGroup customerGroup = (CustomerGroup) element;
			switch (columnIndex) {
			case CustomerSegmentListView.INDEX_SEGMENT_IMAGE:
				return ""; //$NON-NLS-1$;
			case CustomerSegmentListView.INDEX_SEGMENT_NAME:
				return customerGroup.getName();
			case CustomerSegmentListView.INDEX_SEGMENT_DESCRIPTION:
				return customerGroup.getDescription();
			case CustomerSegmentListView.INDEX_SEGMENT_ENABLED:
				return String.valueOf(customerGroup.isEnabled());				
			default:
				return ""; //$NON-NLS-1$
			}
		}
	}

	@Override
	protected String getPartId() {
		return VIEW_ID;
	}
}
