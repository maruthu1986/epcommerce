/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.fulfillment.editors.order;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.TableWrapData;

import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.editors.AbstractCmClientEditorPageSectionPart;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;
import com.elasticpath.cmclient.fulfillment.editors.customer.CustomerDetailsEditor;
import com.elasticpath.cmclient.fulfillment.editors.customer.CustomerDetailsEditorInput;
import com.elasticpath.cmclient.fulfillment.editors.customer.CustomerDetailsProfilePage;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderStatus;
import com.elasticpath.cmclient.core.adapters.EmailHyperlinkAdapter;

/**
 * UI representation of the order summary customer information section.
 */
public class OrderSummaryCustomerInformationSectionPart extends AbstractCmClientEditorPageSectionPart implements IPropertyListener {

	private static final Logger LOG = Logger.getLogger(OrderSummaryCustomerInformationSectionPart.class);

	private IEpLayoutComposite mainPane;

	private final Customer customer;

	private Text userIdText;

	private Text customerNameText;

	private Text phoneNumberText;

	private Button editCustomerButton;

	private final Order order;
	
	private final OrderEditor editor;
	
	/**
	 * Constructor.
	 * 
	 * @param editor the editor containing this Section Constructor to create a new Section in an editor's FormPage.
	 * @param formPage the formpage
	 */
	public OrderSummaryCustomerInformationSectionPart(final FormPage formPage, final AbstractCmClientFormEditor editor) {
		super(formPage, editor, ExpandableComposite.TITLE_BAR | ExpandableComposite.EXPANDED);
		order = (Order) editor.getModel();
		this.customer = order.getCustomer();
		this.editor = (OrderEditor) editor;
		editor.addPropertyListener(this);
	}

	@Override
	protected void createControls(final Composite parentComposite, final FormToolkit toolkit) {

		this.mainPane = CompositeFactory.createTableWrapLayoutComposite(parentComposite, 2, false);
		final TableWrapData data = new TableWrapData(TableWrapData.FILL, TableWrapData.FILL);
		data.grabHorizontal = true;
		this.mainPane.setLayoutData(data);

		final IEpLayoutData compositeData = this.mainPane.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.BEGINNING);
		final IEpLayoutComposite customerInformationComposite = this.mainPane.addTableWrapLayoutComposite(2, false, compositeData);
		final IEpLayoutComposite editCustomerButtonComposite = this.mainPane.addTableWrapLayoutComposite(1, false, compositeData);
		final IEpLayoutComposite emailComposite;

		final IEpLayoutData labelData = this.mainPane.createLayoutData(IEpLayoutData.END, IEpLayoutData.CENTER);
		final IEpLayoutData fieldData = this.mainPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.BEGINNING, true, false);

		customerInformationComposite.addLabelBold(FulfillmentMessages.get().CustomerDetails_UserIdLabel, labelData);
		userIdText = customerInformationComposite.addTextField(EpState.READ_ONLY, fieldData);

		customerInformationComposite.addLabelBold(FulfillmentMessages.get().OrderSummaryCustomerInformationSection_CustomerName, labelData);
		customerNameText = customerInformationComposite.addTextField(EpState.READ_ONLY, fieldData);

		customerInformationComposite.addLabelBold(FulfillmentMessages.get().OrderSummaryCustomerInformationSection_EmailAddress, labelData);

		emailComposite = customerInformationComposite.addTableWrapLayoutComposite(2, false, compositeData);
		final Image emailImage = CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_EMAIL_SEND);
		final Label emailLabel = emailComposite.addLabel("email icon", fieldData); //$NON-NLS-1$
		emailLabel.setToolTipText(getEmailToolTipText());
		emailLabel.setImage(emailImage);

		final Hyperlink emailHyperlink = emailComposite.addHyperLinkText(this.customer.getEmail(), EpState.EDITABLE, fieldData);
		emailHyperlink.setToolTipText(getEmailToolTipText());
		emailHyperlink.addHyperlinkListener(new EmailHyperlinkAdapter());

		customerInformationComposite.addLabelBold(FulfillmentMessages.get().OrderSummaryCustomerInformationSection_PhoneNumber, labelData);
		phoneNumberText = customerInformationComposite.addTextField(EpState.READ_ONLY, fieldData);

		final Image customerEditImage = CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_USER);
		editCustomerButton = editCustomerButtonComposite.addPushButton(FulfillmentMessages.get().
				OrderSummaryCustomerInformationSection_EditCustomerBtn, customerEditImage, EpState.EDITABLE, fieldData);
		editCustomerButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(final SelectionEvent event) {
				widgetSelected(event);
			}

			@Override
			public void widgetSelected(final SelectionEvent event) {
				final IEditorInput editorInput = new CustomerDetailsEditorInput(customer.getUidPk());
				try {
					IEditorPart editorPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().
						getActivePage().openEditor(editorInput, CustomerDetailsEditor.ID_EDITOR);
					if (editorPart instanceof FormEditor) {
						((FormEditor) editorPart).setActivePage(CustomerDetailsProfilePage.PAGE_ID);						
					}
				} catch (final PartInitException e) {
					LOG.error("Can not open customer details editor", e); //$NON-NLS-1$
				}
			}
		});
	}

	@Override
	protected void populateControls() {
		this.userIdText.setText(String.valueOf(this.customer.getUserId()));
		this.customerNameText.setText(this.customer.getFullName());
		String phoneNumber = ""; //$NON-NLS-1$
		if (this.customer.getPhoneNumber() != null) {
			phoneNumber = this.customer.getPhoneNumber();
		}
		this.phoneNumberText.setText(phoneNumber);
	}

	@Override
	protected void bindControls(final DataBindingContext bindingContext) {
		// not required
	}

	@Override
	protected String getSectionDescription() {
		return FulfillmentMessages.get().OrderSummaryCustomerInformationSection_Description;
	}

	@Override
	protected String getSectionTitle() {
		return FulfillmentMessages.get().OrderSummaryCustomerInformationSection_Title;
	}

	private String getEmailToolTipText() {
		return FulfillmentMessages.get().OrderSummaryCustomerInformationSection_Email + this.customer.getFullName();
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
		if (propId == OrderEditor.PROP_REFRESH_PARTS && !editCustomerButton.isDisposed()) {
			if (OrderStatus.CANCELLED.equals(order.getStatus())) {
				editCustomerButton.setEnabled(false);
			} else {
				editCustomerButton.setEnabled(true);
			}
		}
	}

}
