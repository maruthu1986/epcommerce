/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.cmclient.fulfillment.editors.customer;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.editors.AbstractCmClientEditorPageSectionPart;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.ControlModificationListener;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;
import com.elasticpath.cmclient.fulfillment.FulfillmentPermissions;
import com.elasticpath.cmclient.fulfillment.editors.customer.dialogs.CustomerAddCustomerSegmentDialog;
import com.elasticpath.cmclient.fulfillment.editors.customer.dialogs.CustomerRemoveCustomerSegmentDialog;
import com.elasticpath.cmclient.fulfillment.event.FulfillmentEventService;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerGroup;
import com.elasticpath.service.customer.CustomerGroupService;

/**
 * Customer groups section.
 */
public class CustomerDetailsCustomerSegmentsSection extends AbstractCmClientEditorPageSectionPart implements SelectionListener {

	private static final int COLUMN_WIDTH_SEGMENT_NAME = 250;

	private static final String CUSTOMER_SEGMENTS_TABLE = "Customer Segments Table"; //$NON-NLS-1$

	private final transient Customer customer;

	private final transient ControlModificationListener listener;

	private transient TableViewer tableViewer;

	private transient IEpLayoutComposite mainPane;

	private transient EpState authorization;

	private transient CustomerGroup selectedCustomerGroup;

	private transient Button addCustomerSegmentButton;

	private transient Button removeCustomerSegmentButton;

	private transient CustomerGroupService customerGroupService;

	/**
	 * Construct the CSR customer segments section.
	 * 
	 * @param formPage the form page
	 * @param editor the editor
	 */
	public CustomerDetailsCustomerSegmentsSection(final FormPage formPage, final AbstractCmClientFormEditor editor) {
		super(formPage, editor, ExpandableComposite.TITLE_BAR | ExpandableComposite.EXPANDED);
		this.customer = (Customer) editor.getModel();
		this.listener = editor;
	}

	/**
	 * Gets the section title.
	 * 
	 * @return string
	 */
	@Override
	protected String getSectionTitle() {
		return FulfillmentMessages.get().CustomerSegmentsPage_Title;
	}

	/**
	 * Creates the customer group controls in the dialog.
	 * 
	 * @param client the composite
	 * @param toolkit the form toolkit
	 */
	@Override
	protected void createControls(final Composite client, final FormToolkit toolkit) {
		if (customerGroupService == null) {
			customerGroupService = ServiceLocator.getService(ContextIdNames.CUSTOMER_GROUP_SERVICE);
		}

		final AuthorizationService authorizationService = AuthorizationService.getInstance();

		boolean isAuthorized =
				authorizationService.isAuthorizedWithPermission(FulfillmentPermissions.CUSTOMER_EDIT);

		if (isAuthorized) {
			isAuthorized = authorizationService.isAuthorizedForStore(customer.getStoreCode());
		}

		if (isAuthorized) {
			authorization = EpState.EDITABLE;
		} else {
			authorization = EpState.READ_ONLY;
		}

		mainPane = CompositeFactory.createTableWrapLayoutComposite(client, 2, false);
		mainPane.setLayoutData(new TableWrapData(TableWrapData.FILL, TableWrapData.FILL));

		final IEpLayoutData tableData = mainPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false);

		final IEpTableViewer epTableViewer = mainPane.addTableViewer(false, authorization, tableData, CUSTOMER_SEGMENTS_TABLE);

		epTableViewer.addTableColumn(FulfillmentMessages.get().CustomerSegmentsPage_GroupName, COLUMN_WIDTH_SEGMENT_NAME);

		epTableViewer.setLabelProvider(new CustomerSegmentsTableLabelProvider());
		epTableViewer.setContentProvider(new CustomerSegmentsTableContentProvider());

		epTableViewer.setInput(this.customer);
		// Add the listener to the table to retrieve selections
		epTableViewer.getSwtTableViewer().addSelectionChangedListener(event -> {
			if (event.getSelection() instanceof IStructuredSelection) {
				CustomerDetailsCustomerSegmentsSection.this.selectedCustomerGroup =
						(CustomerGroup) ((IStructuredSelection) event.getSelection()).getFirstElement();
				setButtonsState();
			}
		});
		this.tableViewer = epTableViewer.getSwtTableViewer();

		final IEpLayoutData buttonPaneData = mainPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL);
		final IEpLayoutComposite buttonsPane = mainPane.addTableWrapLayoutComposite(1, true, buttonPaneData);
		final IEpLayoutData buttonData = mainPane.createLayoutData();

		this.addCustomerSegmentButton = buttonsPane.addPushButton(FulfillmentMessages.get().CustomerSegmentsPage_Add,
				CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_ADD), authorization, buttonData);

		this.removeCustomerSegmentButton = buttonsPane.addPushButton(FulfillmentMessages.get().CustomerSegmentsPage_Remove,
				CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_REMOVE), authorization, buttonData);

		setButtonsState();
	}

	private void setButtonsState() {
		final boolean canEdit = EpState.EDITABLE.equals(authorization);
		this.addCustomerSegmentButton.setEnabled(canEdit);
		this.removeCustomerSegmentButton.setEnabled(canEdit && canRemove(selectedCustomerGroup));
	}

	private boolean canRemove(final CustomerGroup customerGroup) {
		return selectedCustomerGroup != null && !customerGroupService.checkIfSystemGroup(customerGroup);
	}

	@Override
	protected void populateControls() {
		this.addCustomerSegmentButton.addSelectionListener(this);
		this.removeCustomerSegmentButton.addSelectionListener(this);
		this.mainPane.setControlModificationListener(listener);
	}

	@Override
	protected void bindControls(final DataBindingContext bindingContext) {
		// Not used
	}

	/**
	 * Handle button push events.
	 * 
	 * @param event the selection event
	 */
	@Override
	public void widgetDefaultSelected(final SelectionEvent event) {
		// Nothing to do
	}

	/**
	 * Handle button events on this page. Open dialog for add/remove customer group. Refresh the table if saved.
	 * 
	 * @param event the selection event
	 */
	@Override
	public void widgetSelected(final SelectionEvent event) {
		int returnVal = 0;
		if (event.getSource() == this.addCustomerSegmentButton) {
			final CustomerAddCustomerSegmentDialog dialog =
					new CustomerAddCustomerSegmentDialog(this.getManagedForm().getForm().getShell(), this.tableViewer);
			returnVal = dialog.open();
		} else if (event.getSource() == this.removeCustomerSegmentButton && canRemove(this.selectedCustomerGroup)) {
			final CustomerRemoveCustomerSegmentDialog dialog =
					new CustomerRemoveCustomerSegmentDialog(this.getManagedForm().getForm().getShell(), this.customer, this.selectedCustomerGroup);
			returnVal = dialog.open();
		} else {
			return;
		}
		if (returnVal == Window.OK) {
			// RCPRAP - experiment to get onto appropriate thread
			Display.getDefault().asyncExec(
			new Runnable() {
				@Override
				public void run() {
					tableViewer.refresh();
					FulfillmentEventService.getInstance().fireCustomerChangeEvent(new ItemChangeEvent<>(this, customer));
					setButtonsState();
					listener.controlModified();
				}
			});
		}
	}

	/**
	 * Content Provider class. Gets the customer's segments.
	 */
	class CustomerSegmentsTableContentProvider implements IStructuredContentProvider {
		/**
		 * Return the addresses of customer as array.
		 * 
		 * @param element Customer input
		 * @return the addresses
		 */
		@Override
		public Object[] getElements(final Object element) {
			Object[] result = null;
			if (element instanceof Customer) {
				result = ((Customer) element).getCustomerGroups().toArray();
			}
			return result;
		}

		/**
		 * Not needed.
		 */
		@Override
		public void dispose() {
			// nothing to dispose
		}

		/**
		 * Not needed unless customer search results sharing same view.
		 * 
		 * @param viewer the view
		 * @param oldObject the old object
		 * @param newObject the new object
		 */
		@Override
		public void inputChanged(final Viewer viewer, final Object oldObject, final Object newObject) {
			// not needed
		}
	}

	/**
	 * Provides the columns for the customer segments table.
	 */
	class CustomerSegmentsTableLabelProvider extends LabelProvider implements ITableLabelProvider {

		/**
		 * Get the column image.
		 * 
		 * @param element not used
		 * @param columnIndex the column to create an image for
		 * @return the image
		 */
		@Override
		public Image getColumnImage(final Object element, final int columnIndex) {
			return null;
		}

		/**
		 * Get the column text from selected address.
		 * 
		 * @param element the data input element
		 * @param columnIndex the column index
		 * @return text
		 */
		@Override
		public String getColumnText(final Object element, final int columnIndex) {
			final CustomerGroup customerGroup = (CustomerGroup) element;

			switch (columnIndex) {
			case 0:
				return customerGroup.getName();
			default:
				return ""; //$NON-NLS-1$
			}
		}
	}
}
