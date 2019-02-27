/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.fulfillment.editors.order;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;

import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.editors.AbstractCmClientEditorPageSectionPart;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.ControlModificationListener;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;
import com.elasticpath.cmclient.fulfillment.editors.order.dialog.OrderAddEditAddressDialog;
import com.elasticpath.cmclient.fulfillment.util.AddressUtil;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderAddress;
import com.elasticpath.domain.order.OrderStatus;

/**
 * UI representation of the order summary billing address section.
 */
public class OrderSummaryBillingAddressSectionPart extends AbstractCmClientEditorPageSectionPart implements IPropertyListener {

	private final OrderAddress billingAddress;

	private IEpLayoutComposite mainPane;

	private final ControlModificationListener controlModificationListener;

	private Text nameText;

	private Text addressText;

	private Text phoneNumberText;

	private Button editBillingAddressButton;

	private final AbstractCmClientFormEditor editor;

	private final EpState rolePermission;
	
	private final OrderEditor orderEditor;

	/**
	 * Constructor.
	 * 
	 * @param editor the editor containing this Section Constructor to create a new Section in an editor's FormPage.
	 * @param formPage the formpage
	 */
	public OrderSummaryBillingAddressSectionPart(final FormPage formPage, final AbstractCmClientFormEditor editor) {
		super(formPage, editor, ExpandableComposite.TITLE_BAR | ExpandableComposite.EXPANDED);
		Order order = (Order) editor.getModel();
		this.editor = editor;
		this.billingAddress = order.getBillingAddress();
		this.controlModificationListener = editor;
		this.orderEditor = (OrderEditor) editor;
	
		if (orderEditor.isAuthorizedAndAvailableForEdit() && !order.getStatus().equals(OrderStatus.COMPLETED)) {
			rolePermission = EpState.EDITABLE;
		} else {
			rolePermission = EpState.READ_ONLY;
		}
		editor.addPropertyListener(this);
	}

	@Override
	protected void createControls(final Composite parentComposite, final FormToolkit toolkit) {

		this.mainPane = CompositeFactory.createTableWrapLayoutComposite(parentComposite, 2, false);
		final TableWrapData data = new TableWrapData(TableWrapData.FILL, TableWrapData.FILL);
		data.grabHorizontal = true;
		this.mainPane.setLayoutData(data);

		final IEpLayoutData compositeData = this.mainPane.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.BEGINNING);
		final IEpLayoutComposite billingAddressComposite = this.mainPane.addTableWrapLayoutComposite(2, false, compositeData);
		final IEpLayoutComposite editBillingAddressButtonComposite = this.mainPane.addTableWrapLayoutComposite(1, false, compositeData);

		final IEpLayoutData addressLableData = this.mainPane.createLayoutData(IEpLayoutData.END, IEpLayoutData.BEGINNING);
		final IEpLayoutData labelData = this.mainPane.createLayoutData(IEpLayoutData.END, IEpLayoutData.CENTER);
		final IEpLayoutData fieldData = this.mainPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.BEGINNING, true, false);

		billingAddressComposite.addLabelBold(FulfillmentMessages.get().OrderSummaryBillingAddressSection_Name, labelData);
		this.nameText = billingAddressComposite.addTextField(EpState.READ_ONLY, fieldData);

		billingAddressComposite.addLabelBold(FulfillmentMessages.get().OrderSummaryBillingAddressSection_Address, addressLableData);
		this.addressText = billingAddressComposite.addTextField(EpState.READ_ONLY, fieldData);

		billingAddressComposite.addLabelBold(FulfillmentMessages.get().OrderSummaryBillingAddressSection_PhoneNumber, labelData);
		this.phoneNumberText = billingAddressComposite.addTextField(EpState.READ_ONLY, fieldData);

		final Image addressEditImage = CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_ADDRESS_EDIT);
		editBillingAddressButton = editBillingAddressButtonComposite.addPushButton(
			FulfillmentMessages.get().OrderSummaryBillingAddressSection_EditBillingAddressBtn, addressEditImage, rolePermission, fieldData);

		editBillingAddressButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(final SelectionEvent event) {
				widgetSelected(event);
			}

			@Override
			public void widgetSelected(final SelectionEvent event) {
				final OrderAddEditAddressDialog dialog = new OrderAddEditAddressDialog(editor.getSite().getShell(),
					(Order) editor.getModel(), null, false, false);
				if (dialog.open() == Window.OK) {
					controlModificationListener.controlModified();
					populateControls();
				}
			}
		});
	}

	@Override
	protected void populateControls() {
		mainPane.setControlModificationListener(this.controlModificationListener);
		if (billingAddress == null) {
			this.nameText.setText(FulfillmentMessages.get().OrderSummaryPage_BillingAddress_PayPal);
			this.addressText.setText(FulfillmentMessages.get().OrderSummaryPage_BillingAddress_PayPal);
		} else {
			this.nameText.setText(AddressUtil.getFullCustomerName(this.billingAddress));
			this.addressText.setText(AddressUtil.formatAddress(this.billingAddress, false));
		}
		
		String phoneNumber = ""; //$NON-NLS-1$
		//MSC-7117 the first check is for pay pal order
		if (this.billingAddress != null && this.billingAddress.getPhoneNumber() != null) {
			phoneNumber = this.billingAddress.getPhoneNumber();
		}
		this.phoneNumberText.setText(phoneNumber);
		Order order = (Order) editor.getModel();
		editBillingAddressButton.setEnabled(order.getStatus() != OrderStatus.CANCELLED
				&& EpState.EDITABLE == rolePermission && billingAddress != null);
	}

	@Override
	protected void bindControls(final DataBindingContext bindingContext) {
		// not required
	}

	@Override
	protected String getSectionDescription() {
		return FulfillmentMessages.get().OrderSummaryBillingAddressSection_Description;
	}

	@Override
	protected String getSectionTitle() {
		return FulfillmentMessages.get().OrderSummaryBillingAddressSection_Title;
	}

	@Override
	public void refresh() {
		populateControls();
		super.refresh();
	}

	/**
	 * Gets the table's modification listener.
	 * 
	 * @return ControlModificationListener the modification listener attached to table viewer
	 */
	public ControlModificationListener getControlModificationListener() {
		return controlModificationListener;
	}
	
	@Override
	public void sectionDisposed() {
		editor.removePropertyListener(this);
	}

	/**
	 * Callback for prop change events.
	 * 
	 * @param source the source object that has initiated the event 
	 * @param propId the id
	 */
	@Override
	public void propertyChanged(final Object source, final int propId) {
		if (propId == OrderEditor.PROP_REFRESH_PARTS) {
			Order order = (Order) editor.getModel();
			editBillingAddressButton.setEnabled(orderEditor.isAuthorizedAndAvailableForEdit() && order.getStatus() != OrderStatus.CANCELLED);
		}
	}
}
