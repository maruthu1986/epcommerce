/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.fulfillment.editors.customer;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;

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
import com.elasticpath.cmclient.fulfillment.editors.customer.dialogs.CustomerAddEditAddressDialog;
import com.elasticpath.cmclient.fulfillment.editors.customer.dialogs.CustomerRemoveAddressDialog;
import com.elasticpath.cmclient.fulfillment.event.FulfillmentEventService;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerAddress;

/**
 * Represents the UI of customer details address.
 */
@SuppressWarnings({ "PMD.PrematureDeclaration" })
public class CustomerDetailsAddressMainSection extends AbstractCmClientEditorPageSectionPart implements SelectionListener {

	private static final int COLUMN_WIDTH_PHONE = 90;

	private static final int COLUMN_WIDTH_ADDRESS = 250;

	private static final int COLUMN_WIDTH_NAME = 130;

	private static final String CUSTOMER_DETAILS_ADDRESS_TABLE = "Customer Details Address Table"; //$NON-NLS-1$

	private Button addAddressButton;

	private Button editAddressButton;

	private Button removeAddressButton;

	private Button viewAddressButton;

	private final Customer customer;

	private final ControlModificationListener listener;

	private IEpLayoutComposite mainPane;

	private CustomerAddress addressSelection;

	private TableViewer tableViewer;

	private EpState authorization;

	private boolean authorized;

	private static final String BLANK = " "; //$NON-NLS-1$

	private static final String COMMASEPERATOR = ", "; //$NON-NLS-1$

	private static final String NULLSTRING = ""; //$NON-NLS-1$

	/**
	 * Constructor.
	 * 
	 * @param formPage the formpage
	 * @param editor the CmClientFormEditor that contains the form
	 */
	public CustomerDetailsAddressMainSection(final FormPage formPage, final AbstractCmClientFormEditor editor) {
		super(formPage, editor, ExpandableComposite.TITLE_BAR | ExpandableComposite.EXPANDED);
		this.customer = (Customer) editor.getModel();
		this.listener = editor;
	}

	@Override
	protected void createControls(final Composite client, final FormToolkit toolkit) {

		AuthorizationService authorizationService = AuthorizationService.getInstance();

		authorized = authorizationService.isAuthorizedWithPermission(FulfillmentPermissions.CUSTOMER_EDIT)
						&& authorizationService.isAuthorizedForStore(customer.getStoreCode());

		if (authorized) {
			authorization = EpState.EDITABLE;
		} else {
			authorization = EpState.READ_ONLY;
		}

		mainPane = CompositeFactory.createTableWrapLayoutComposite(client, 2, false);
		mainPane.setLayoutData(new TableWrapData(TableWrapData.FILL, TableWrapData.FILL));

		final IEpLayoutData tableData = mainPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false);

		final IEpTableViewer epTableViewer = mainPane.addTableViewer(false, authorization, tableData, CUSTOMER_DETAILS_ADDRESS_TABLE);

		epTableViewer.addTableColumn(FulfillmentMessages.get().AddressMainSection_Name, COLUMN_WIDTH_NAME);
		epTableViewer.addTableColumn(FulfillmentMessages.get().AddressMainSection_Address, COLUMN_WIDTH_ADDRESS);
		epTableViewer.addTableColumn(FulfillmentMessages.get().AddressMainSection_PhoneNum, COLUMN_WIDTH_PHONE);

		epTableViewer.setLabelProvider(new CustomerAddressTableLabelProvider());
		epTableViewer.setContentProvider(new CustomerAddressTableContentProvider());

		epTableViewer.setInput(this.customer);
		// Add the listener to the table to retrieve address selections
		epTableViewer.getSwtTableViewer().addSelectionChangedListener(event -> {
			if (event.getSelection() instanceof IStructuredSelection) {
				CustomerDetailsAddressMainSection.this.addressSelection =
						(CustomerAddress) ((IStructuredSelection) event.getSelection()).getFirstElement();
				editAddressButton.setEnabled(authorized);
				removeAddressButton.setEnabled(authorized);
				viewAddressButton.setEnabled(true);
			}
		});
		this.tableViewer = epTableViewer.getSwtTableViewer();

		final IEpLayoutData buttonPaneData = mainPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL);
		final IEpLayoutComposite buttonsPane = mainPane.addTableWrapLayoutComposite(1, true, buttonPaneData);
		final IEpLayoutData buttonData = mainPane.createLayoutData();

		this.editAddressButton =
				buttonsPane.addPushButton(FulfillmentMessages.get().AddressMainSection_EditAddressButton, CoreImageRegistry
						.getImage(CoreImageRegistry.IMAGE_EDIT), authorization, buttonData);
		this.addAddressButton =
				buttonsPane.addPushButton(FulfillmentMessages.get().AddressMainSection_AddAddressButton, CoreImageRegistry
						.getImage(CoreImageRegistry.IMAGE_ADD), authorization, buttonData);
		this.removeAddressButton =
				buttonsPane.addPushButton(FulfillmentMessages.get().AddressMainSection_RemoveAddressButton, CoreImageRegistry
						.getImage(CoreImageRegistry.IMAGE_REMOVE), authorization, buttonData);

		this.viewAddressButton =
				buttonsPane.addPushButton(FulfillmentMessages.get().AddressMainSection_ViewAddressButton, CoreImageRegistry
						.getImage(CoreImageRegistry.IMAGE_ADDRESS), EpState.EDITABLE, buttonData);

		editAddressButton.setEnabled(false);
		removeAddressButton.setEnabled(false);
		viewAddressButton.setEnabled(false);

		tableViewer.addDoubleClickListener(new IDoubleClickListener() {
			/**
			 * Handle double clicks on table selection. Opens up selected address for edit.
			 * 
			 * @param event the doubleclick event
			 */
			@Override
			public void doubleClick(final DoubleClickEvent event) {
				final CustomerAddEditAddressDialog dialog =
						new CustomerAddEditAddressDialog(getManagedForm().getForm().getShell(), tableViewer, false, !authorized);
				if (dialog.open() == IStatus.OK && authorized) {
					tableViewer.refresh();
					FulfillmentEventService.getInstance().fireCustomerChangeEvent(new ItemChangeEvent<>(this, customer));
				}
			}
		});

	}

	@Override
	protected String getSectionDescription() {
		return FulfillmentMessages.get().AddressMainSection_Description;
	}

	@Override
	protected String getSectionTitle() {
		return FulfillmentMessages.get().AddressMainSection_Title;
	}

	@Override
	protected void bindControls(final DataBindingContext bindingContext) {
		// not needed
	}

	@Override
	protected void populateControls() {
		this.addAddressButton.addSelectionListener(this);
		this.editAddressButton.addSelectionListener(this);
		this.removeAddressButton.addSelectionListener(this);
		this.viewAddressButton.addSelectionListener(this);
		this.mainPane.setControlModificationListener(listener);
	}

	/**
	 * Not used.
	 * 
	 * @param event the event object
	 */
	@Override
	public void widgetDefaultSelected(final SelectionEvent event) {
		// not needed
	}

	/**
	 * Invoked on selection event. Buttons are registered for selection, and will fire event when pressed.
	 * 
	 * @param event the event object
	 */
	@Override
	public void widgetSelected(final SelectionEvent event) {
		int returnValue;

		final Dialog dialog;
		if (event.getSource() == this.addAddressButton) {
			dialog = new CustomerAddEditAddressDialog(this.getManagedForm().getForm().getShell(), this.tableViewer, true, false);
		} else if (addressSelection == null) {
			return;
		} else if (event.getSource() == this.editAddressButton) {
			dialog = new CustomerAddEditAddressDialog(this.getManagedForm().getForm().getShell(), this.tableViewer, false, !authorized);
		} else if (event.getSource() == this.removeAddressButton) {
			dialog = new CustomerRemoveAddressDialog(this.getManagedForm().getForm().getShell(), this.customer, this.addressSelection);
		} else if (event.getSource() == this.viewAddressButton) {
			dialog = new CustomerAddEditAddressDialog(this.getManagedForm().getForm().getShell(), this.tableViewer, false, true);
		} else {
			return;
		}

		returnValue = dialog.open();

		if (authorized && (returnValue == IStatus.OK)) {
			tableViewer.refresh();
			FulfillmentEventService.getInstance().fireCustomerChangeEvent(new ItemChangeEvent<>(this, customer));
			this.listener.controlModified();
		}
	}

	/**
	 * Content Provider class.
	 */
	class CustomerAddressTableContentProvider implements IStructuredContentProvider {

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
				result = ((Customer) element).getAddresses().toArray();
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
	 * Provides the column image for the address table.
	 */
	class CustomerAddressTableLabelProvider extends LabelProvider implements ITableLabelProvider {

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
			final CustomerAddress address = (CustomerAddress) element;

			switch (columnIndex) {
			case 0:
				return address.getFirstName() + BLANK + address.getLastName();
			case 1:
				final StringBuilder builder = new StringBuilder();
				builder.append(address.getStreet1());
				if (address.getStreet2() != null && !StringUtils.isBlank(address.getStreet2())) {
					builder.append(BLANK);
					builder.append(address.getStreet2());
				}
				builder.append(COMMASEPERATOR);
				builder.append(address.getCity());
				if (address.getSubCountry() != null && !StringUtils.isBlank(address.getSubCountry())) {
					builder.append(COMMASEPERATOR);
					builder.append(address.getSubCountry());
				}
				builder.append(COMMASEPERATOR);
				builder.append(address.getZipOrPostalCode());
				return builder.toString();
			case 2:
				return address.getPhoneNumber();
			default:
				return NULLSTRING;
			}
		}
	}

}
