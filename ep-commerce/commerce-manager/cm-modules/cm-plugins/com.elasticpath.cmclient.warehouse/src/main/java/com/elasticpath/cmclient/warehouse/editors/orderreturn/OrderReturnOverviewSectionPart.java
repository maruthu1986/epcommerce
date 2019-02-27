/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.warehouse.editors.orderreturn;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;

import com.elasticpath.cmclient.core.LoginManager;
import com.elasticpath.cmclient.core.editors.AbstractCmClientEditorPageSectionPart;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.util.DateTimeUtilFactory;
import com.elasticpath.cmclient.fulfillment.views.order.OpenOrderEditorAction;
import com.elasticpath.cmclient.warehouse.WarehouseImageRegistry;
import com.elasticpath.cmclient.warehouse.WarehouseMessages;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.order.OrderReturn;

/**
 * UI representation of the order return overview section.
 */
public class OrderReturnOverviewSectionPart extends AbstractCmClientEditorPageSectionPart {

	private IEpLayoutComposite mainPane;

	private final OrderReturn orderReturn;

	private Text rmaTypeText;

	private Text rmaNumberText;

	private Text orderNumberText;

	private Text rmaDateText;

	private Text rmaStatusText;

	private Text receivedByText;
	
	private final EpState editMode;

	/**
	 * Constructor.
	 * 
	 * @param editor the editor containing this Section Constructor to create a new Section in an editor's FormPage.
	 * @param formPage the formpage
	 * @param editMode if this part is read only
	 */
	public OrderReturnOverviewSectionPart(final FormPage formPage, final AbstractCmClientFormEditor editor, final EpState editMode) {
		super(formPage, editor, ExpandableComposite.TITLE_BAR | ExpandableComposite.EXPANDED);

		this.orderReturn = (OrderReturn) editor.getModel();
		this.editMode = editMode;
	}

	@Override
	protected void createControls(final Composite parentComposite, final FormToolkit toolkit) {
		IEpLayoutComposite superPane = CompositeFactory.createTableWrapLayoutComposite(parentComposite, 2, false);
		superPane.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.FILL));
		
		mainPane = superPane.addTableWrapLayoutComposite(2, false, null);
		mainPane.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.FILL));

		IEpLayoutData labelLayoutData = mainPane.createLayoutData(IEpLayoutData.END, IEpLayoutData.CENTER);
		IEpLayoutData fieldLayoutData = mainPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.BEGINNING, true, false);

		mainPane.addLabelBold(WarehouseMessages.get().OrderReturnSummaryOverviewSection_RMAType, labelLayoutData);
		rmaTypeText = mainPane.addTextField(EpState.READ_ONLY, fieldLayoutData);

		mainPane.addLabelBold(WarehouseMessages.get().OrderReturnSummaryOverviewSection_RMANumber, labelLayoutData);
		rmaNumberText = mainPane.addTextField(EpState.READ_ONLY, fieldLayoutData);

		mainPane.addLabelBold(WarehouseMessages.get().OrderReturnSummaryOverviewSection_OrderNumber, labelLayoutData);
		orderNumberText = mainPane.addTextField(EpState.READ_ONLY, fieldLayoutData);

		mainPane.addLabelBold(WarehouseMessages.get().OrderReturnSummaryOverviewSection_RMADate, labelLayoutData);
		rmaDateText = mainPane.addTextField(EpState.READ_ONLY, fieldLayoutData);

		mainPane.addLabelBold(WarehouseMessages.get().OrderReturnSummaryOverviewSection_RMAStatus, labelLayoutData);
		rmaStatusText = mainPane.addTextField(EpState.READ_ONLY, fieldLayoutData);

		mainPane.addLabelBold(WarehouseMessages.get().OrderReturnSummaryOverviewSection_ReceivedBy, labelLayoutData);
		receivedByText = mainPane.addTextField(EpState.READ_ONLY, fieldLayoutData);

		Button button = superPane.addPushButton(WarehouseMessages.get().OrderReturnSummaryOverviewSection_OpenButton, editMode,
				superPane.createLayoutData(IEpLayoutData.END, IEpLayoutData.BEGINNING, false, false));
		button.setImage(WarehouseImageRegistry.getImage(WarehouseImageRegistry.IMAGE_OPEN_ORIGINAL_ORDER_BUTTON));
		button.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(final SelectionEvent event) {
				//Nothing
			}

			@Override
			public void widgetSelected(final SelectionEvent event) {
				OpenOrderEditorAction.showEditor(orderReturn.getOrder(), PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
						.getActiveEditor().getSite());
			}
			
		});
		
		
	}

	@Override
	protected void populateControls() {
		rmaTypeText.setText(WarehouseMessages.get().getLocalizedOrderReturnType(orderReturn.getReturnType()));
		rmaNumberText.setText(orderReturn.getRmaCode());
		orderNumberText.setText(orderReturn.getOrder().getOrderNumber());
		rmaDateText.setText(DateTimeUtilFactory.getDateUtil().formatAsDate(orderReturn.getCreatedDate()));
		rmaStatusText.setText(WarehouseMessages.get().getLocalizedOrderReturnStatus(orderReturn.getReturnStatus()));
		
		setReceivedBy(false);
	}

	@Override
	protected void bindControls(final DataBindingContext bindingContext) {
		//Binding is not needed
	}

	@Override
	protected String getSectionDescription() {
		return WarehouseMessages.get().OrderReturnSummaryOverviewSection_Description;
	}

	@Override
	protected String getSectionTitle() {
		return WarehouseMessages.get().OrderReturnSummaryOverviewSection_Title;
	}

	/**
	 * Invokes RMA status update.
	 */
	public void updateStatus() {
		rmaStatusText.setText(WarehouseMessages.get().getLocalizedOrderReturnStatus(orderReturn.getReturnStatus()));

		setReceivedBy(true);
	}

	private void setReceivedBy(final boolean mode) {
		CmUser cmUser = LoginManager.getCmUser();
		String status = WarehouseMessages.EMPTY_STRING;
		switch (orderReturn.getReturnStatus()) {
		case AWAITING_STOCK_RETURN:
			status = WarehouseMessages.EMPTY_STRING;
			break;
		case AWAITING_COMPLETION:
			if (mode) {
				status = getFullName(cmUser);
				break;
			}
		case CANCELLED:
		case COMPLETED:
			cmUser = orderReturn.getReceivedByCmUser();
			status = getFullName(cmUser);
			break;
		default:
			break;
		}
		
		receivedByText.setText(status);
		orderReturn.setReceivedByCmUser(cmUser);
	}

	private String getFullName(final CmUser cmUser) {
		if (cmUser == null) {
			return WarehouseMessages.EMPTY_STRING;
		}
		
		return cmUser.getFirstName() + WarehouseMessages.SPACE + cmUser.getLastName();
	}
}
