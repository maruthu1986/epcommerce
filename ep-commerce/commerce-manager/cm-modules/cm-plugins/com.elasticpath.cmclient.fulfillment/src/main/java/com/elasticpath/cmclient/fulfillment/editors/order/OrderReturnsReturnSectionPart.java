/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.fulfillment.editors.order;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;

import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.EpUiException;
import com.elasticpath.cmclient.core.LoginManager;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.editors.AbstractCmClientEditorPageSectionPart;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.ui.framework.IEpTableColumn;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.cmclient.core.util.DateTimeUtilFactory;
import com.elasticpath.cmclient.fulfillment.FulfillmentImageRegistry;
import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;
import com.elasticpath.cmclient.fulfillment.FulfillmentPermissions;
import com.elasticpath.cmclient.fulfillment.dialogs.ResendRMAEmailDialog;
import com.elasticpath.cmclient.fulfillment.views.order.OpenOrderEditorAction;
import com.elasticpath.cmclient.fulfillment.wizards.ExchangeWizard;
import com.elasticpath.cmclient.fulfillment.wizards.ReturnWizard;
import com.elasticpath.cmclient.fulfillment.wizards.SubscribingDialog;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.util.OrderReturnSkuComparatorFactory;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.event.EventOriginator;
import com.elasticpath.domain.event.EventOriginatorHelper;
import com.elasticpath.domain.order.OrderReturn;
import com.elasticpath.domain.order.OrderReturnReceivedState;
import com.elasticpath.domain.order.OrderReturnSku;
import com.elasticpath.domain.order.OrderReturnSkuReason;
import com.elasticpath.domain.order.OrderReturnStatus;
import com.elasticpath.domain.order.OrderReturnType;
import com.elasticpath.money.MoneyFormatter;
import com.elasticpath.service.order.OrderReturnOutOfDateException;
import com.elasticpath.service.order.ReturnAndExchangeService;

/**
 * UI representation of the order return section.
 */
@SuppressWarnings({"PMD.ExcessiveImports", "PMD.GodClass"})
public class OrderReturnsReturnSectionPart extends AbstractCmClientEditorPageSectionPart {

	private static final Logger LOG = Logger.getLogger(OrderReturnsReturnSectionPart.class);

	private static final int COLUMN_WIDTH_SKU_CODE = 75;

	private static final int COLUMN_WIDTH_PRODUCT_NAME = 120;

	private static final int COLUMN_WIDTH_QTY = 40;

	private static final int COLUMN_WIDTH_UNIT_PRICE = 60;

	private static final int COLUMN_WIDTH_REASON = 60;

	private static final int COLUMN_WIDTH_RECEIVED_QTY = 60;

	private static final int COLUMN_WIDTH_RECEIVED_STATE = 65;

	private static final int SKU_CODE_COLUMN_ID = 0;

	private static final int PRODUCT_NAME_COLUMN_ID = 1;

	private static final int RETURN_QTY_COLUMN_ID = 2;

	private static final int UNIT_PRICE_COLUMN_ID = 3;

	private static final int REASON_COLUMN_ID = 4;

	private static final int RECEIVED_QTY_COLUMN_ID = 5;

	private static final int RECEIVED_STATE_COLUMN_ID = 6;

	private static final String SKU_TABLE = "Sku Table"; //$NON-NLS-1$

	private final OrderReturn orderReturn;

	private Text initiatedTimeText;

	private Text createdByText;

	private Text receivedByText;

	private Text statusText;

	private Text exchangeOrderNumberText;

	private Text returnTotalText;

	private Text refundedText;

	private Text owedToCustomerText;

	private Text notesText;

	private Label receivedByLabel;

	private IEpTableViewer skuTable;

	/** Common buttons for returns and exchanges. */
	private Button cancelButton;

	private Button completeButton;

	private Button editButton;

	private Button resendRMAEmailButton;

	private final OrderEditor editor;

	private final ReturnAndExchangeService returnAndExchangeService;

	/**
	 * Constructor.
	 *
	 * @param editor the editor containing this Section Constructor to create a new Section in an editor's FormPage.
	 * @param formPage the form page.
	 * @param orderReturn order return description.
	 */
	public OrderReturnsReturnSectionPart(final FormPage formPage, final AbstractCmClientFormEditor editor, final OrderReturn orderReturn) {
		super(formPage, editor, ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR | ExpandableComposite.EXPANDED);
		this.orderReturn = orderReturn;
		this.editor = (OrderEditor) editor;
		returnAndExchangeService = ServiceLocator.getService(ContextIdNames.ORDER_RETURN_SERVICE);
		orderReturn.getOrder().setModifiedBy(getEventOriginator());
	}

	@Override
	protected void createControls(final Composite client, final FormToolkit toolkit) {
		IEpLayoutComposite mainPane = CompositeFactory.createTableWrapLayoutComposite(client, 2, false);
		mainPane.setLayoutData(new TableWrapData(TableWrapData.FILL, TableWrapData.FILL));
		/*
		 * Color clr = new Color(this.getSection().getDisplay(), 0, 255, 0); mainPane.getSwtComposite().setBackground(clr);
		 */

		IEpLayoutData returnInfoCompositeLayoutData = mainPane.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.FILL, false, true);
		IEpLayoutComposite returnInfoComposite = mainPane.addTableWrapLayoutComposite(2, false, returnInfoCompositeLayoutData);
		/*
		 * Color clr0 = new Color(this.getSection().getDisplay(), 0, 0, 255); returnInfoComposite.getSwtComposite().setBackground(clr0);
		 */

		IEpLayoutData buttonsCompositeLayoutData = mainPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, false, true, 1, 2);
		IEpLayoutComposite buttonsComposite = mainPane.addTableWrapLayoutComposite(1, true, buttonsCompositeLayoutData);
		/*
		 * Color clr1 = new Color(this.getSection().getDisplay(), 255, 0, 0); buttonsComposite.getSwtComposite().setBackground(clr1);
		 */
		createButtons(buttonsComposite);

		IEpLayoutData skuTableLayoutData = mainPane.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.FILL, false, true);
		skuTable = mainPane.addTableViewer(false, EpState.READ_ONLY, skuTableLayoutData, SKU_TABLE);
		createSkuTableContent();

		// returnInfoComposite content:
		IEpLayoutData returnDetailsCompositeLayoutData = returnInfoComposite.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.FILL, false,
				true);
		IEpLayoutComposite returnDetailsComposite = returnInfoComposite.addTableWrapLayoutComposite(2, false, returnDetailsCompositeLayoutData);

		IEpLayoutData notesCompositeLayoutData = returnInfoComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true);
		IEpLayoutComposite notesComposite = returnInfoComposite.addTableWrapLayoutComposite(1, false, notesCompositeLayoutData);

		// returnDetailsComposite content:
		IEpLayoutData labelsReturnDetailsCompositeLayoutData = returnDetailsComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.BEGINNING);
		IEpLayoutData editsReturnDetailsCompositeLayoutData = returnDetailsComposite.createLayoutData(IEpLayoutData.BEGINNING,
				IEpLayoutData.BEGINNING, true, true);

		returnDetailsComposite.addLabelBold(FulfillmentMessages.get().OrderReturnSection_DateTimeInitiated, labelsReturnDetailsCompositeLayoutData);
		initiatedTimeText = returnDetailsComposite.addTextField(EpState.READ_ONLY, editsReturnDetailsCompositeLayoutData);

		returnDetailsComposite.addLabelBold(FulfillmentMessages.get().OrderReturnSection_CreatedBy, returnDetailsComposite.createLayoutData(
				IEpLayoutData.END, IEpLayoutData.CENTER));
		IEpLayoutComposite userComposite = returnDetailsComposite.addTableWrapLayoutComposite(2, false, editsReturnDetailsCompositeLayoutData);
		userComposite.addImage(CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_USER), userComposite.createLayoutData(IEpLayoutData.BEGINNING,
				IEpLayoutData.FILL));
		createdByText = userComposite.addTextField(EpState.READ_ONLY, userComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true,
				true));
		returnDetailsComposite.addLabelBold(FulfillmentMessages.get().OrderReturnSection_ReceivedBy, returnDetailsComposite.createLayoutData(
				IEpLayoutData.END, IEpLayoutData.CENTER));
		userComposite = returnDetailsComposite.addTableWrapLayoutComposite(2, false, editsReturnDetailsCompositeLayoutData);
		receivedByLabel = userComposite.addImage(CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_USER), userComposite.createLayoutData(
				IEpLayoutData.BEGINNING, IEpLayoutData.FILL));
		receivedByText = userComposite.addTextField(EpState.READ_ONLY, userComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true,
				true));
		returnDetailsComposite.addLabelBold(FulfillmentMessages.get().OrderReturnSection_Status, labelsReturnDetailsCompositeLayoutData);
		statusText = returnDetailsComposite.addTextField(EpState.READ_ONLY, editsReturnDetailsCompositeLayoutData);

		if (isExchange()) {
			returnDetailsComposite.addLabelBold(FulfillmentMessages.get().OrderReturnSection_ExchangeOrderNumber,
					labelsReturnDetailsCompositeLayoutData);
			exchangeOrderNumberText = returnDetailsComposite.addTextField(EpState.READ_ONLY, editsReturnDetailsCompositeLayoutData);
		} else {

			returnDetailsComposite.addLabelBold(FulfillmentMessages.get().OrderReturnSection_Total, labelsReturnDetailsCompositeLayoutData);
			returnTotalText = returnDetailsComposite.addTextField(EpState.READ_ONLY, editsReturnDetailsCompositeLayoutData);

			returnDetailsComposite.addLabelBold(FulfillmentMessages.get().OrderReturnSection_Refunded, labelsReturnDetailsCompositeLayoutData);
			refundedText = returnDetailsComposite.addTextField(EpState.READ_ONLY, editsReturnDetailsCompositeLayoutData);

			returnDetailsComposite.addLabelBold(FulfillmentMessages.get().OrderReturnSection_BalanceOwed, labelsReturnDetailsCompositeLayoutData);
			owedToCustomerText = returnDetailsComposite.addTextField(EpState.READ_ONLY, editsReturnDetailsCompositeLayoutData);
		}
		// end of returnDetailsComposite content

		// notesComposite content:
		notesComposite.addLabelBold(FulfillmentMessages.get().OrderReturnSection_Notes, notesComposite.createLayoutData(IEpLayoutData.FILL,
				IEpLayoutData.BEGINNING));
		notesText = notesComposite.addTextArea(EpState.READ_ONLY, notesComposite
				.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true));
		// end of notesComposite content
		// end of returnInfoComposite content:
	}

	private void createButtons(final IEpLayoutComposite buttonsComposite) {

		IEpLayoutData buttonLayoutData = buttonsComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.BEGINNING, true, false);

		if (isExchange()) {
			/** Exchange */
			cancelButton = buttonsComposite.addPushButton(FulfillmentMessages.get().OrderReturnSection_ExchangeCancelBtn, FulfillmentImageRegistry
					.getImage(FulfillmentImageRegistry.IMAGE_EXCHANGE_CANCEL), EpState.EDITABLE, buttonLayoutData);
			completeButton = buttonsComposite.addPushButton(FulfillmentMessages.get().OrderReturnSection_ExchangeCompleteBtn, FulfillmentImageRegistry
					.getImage(FulfillmentImageRegistry.IMAGE_EXCHANGE_COMPLETE), EpState.EDITABLE, buttonLayoutData);
			final Button openExchnageOrderButton = buttonsComposite.addPushButton(FulfillmentMessages.get().OrderReturnSection_ExchangeOpenOrderBtn,
					FulfillmentImageRegistry.getImage(FulfillmentImageRegistry.IMAGE_EXCHANGE_OPEN_ORDER), EpState.EDITABLE, buttonLayoutData);

			openExchnageOrderButton.addSelectionListener(new ExchangeOrderButtonListener());

		} else {
			/** Return */
			editButton = buttonsComposite.addPushButton(FulfillmentMessages.get().OrderReturnSection_ReturnEditBtn, FulfillmentImageRegistry
					.getImage(FulfillmentImageRegistry.IMAGE_RETURN_EDIT), EpState.EDITABLE, buttonLayoutData);
			cancelButton = buttonsComposite.addPushButton(FulfillmentMessages.get().OrderReturnSection_ReturnCancelBtn, FulfillmentImageRegistry
					.getImage(FulfillmentImageRegistry.IMAGE_RETURN_DELETE), EpState.EDITABLE, buttonLayoutData);
			completeButton = buttonsComposite.addPushButton(FulfillmentMessages.get().OrderReturnSection_ReturnCompleteBtn, FulfillmentImageRegistry
					.getImage(FulfillmentImageRegistry.IMAGE_RETURN_COMPLETE), EpState.EDITABLE, buttonLayoutData);

			editButton.addSelectionListener(new ReturnEditButtonListener());
		}

		resendRMAEmailButton = buttonsComposite.addPushButton(FulfillmentMessages.get().OrderReturnSection_ResendRMABtn, FulfillmentImageRegistry
				.getImage(FulfillmentImageRegistry.IMAGE_RESEND_RMA_EMAIL), EpState.EDITABLE, buttonLayoutData);

		resendRMAEmailButton.addSelectionListener(new ResendRMAEmailListener());
		cancelButton.addSelectionListener(new CancelButtonListener());
		completeButton.addSelectionListener(new ReturnCompleteButtonListener());
	}

	private void createSkuTableContent() {
		TableColumn column;

		column = skuTable.addTableColumn(FulfillmentMessages.get().OrderReturnSection_TableTitle_SKUCode, COLUMN_WIDTH_SKU_CODE,
				IEpTableColumn.TYPE_NONE).getSwtTableColumn();
		column.setData(SKU_CODE_COLUMN_ID);

		column = skuTable.addTableColumn(FulfillmentMessages.get().OrderReturnSection_TableTitle_ProductName, COLUMN_WIDTH_PRODUCT_NAME,
				IEpTableColumn.TYPE_NONE).getSwtTableColumn();
		column.setData(PRODUCT_NAME_COLUMN_ID);

		column = skuTable.addTableColumn(FulfillmentMessages.get().OrderReturnSection_TableTitle_Qty, COLUMN_WIDTH_QTY, IEpTableColumn.TYPE_NONE)
				.getSwtTableColumn();
		column.setData(RETURN_QTY_COLUMN_ID);

		if (!isExchange()) {
			column = skuTable.addTableColumn(FulfillmentMessages.get().OrderReturnSection_TableTitle_UnitPrice, COLUMN_WIDTH_UNIT_PRICE,
					IEpTableColumn.TYPE_NONE).getSwtTableColumn();
			column.setData(UNIT_PRICE_COLUMN_ID);
		}
		column = skuTable.addTableColumn(FulfillmentMessages.get().OrderReturnSection_TableTitle_Reason,
				COLUMN_WIDTH_REASON, IEpTableColumn.TYPE_NONE).getSwtTableColumn();
		column.setData(REASON_COLUMN_ID);

		column = skuTable.addTableColumn(FulfillmentMessages.get().OrderReturnSection_TableTitle_ReceivedQty, COLUMN_WIDTH_RECEIVED_QTY,
				IEpTableColumn.TYPE_NONE).getSwtTableColumn();
		column.setData(RECEIVED_QTY_COLUMN_ID);

		column = skuTable.addTableColumn(FulfillmentMessages.get().OrderReturnSection_TableTitle_ReceivedState, COLUMN_WIDTH_RECEIVED_STATE,
				IEpTableColumn.TYPE_NONE).getSwtTableColumn();
		column.setData(RECEIVED_STATE_COLUMN_ID);

		skuTable.setLabelProvider(new SkuTableLabelProvider());
		skuTable.setContentProvider(new ArrayContentProvider());

	}

	@Override
	protected void bindControls(final DataBindingContext bindingContext) {
		// do nothing

	}

	private String getStatus() {
		return CoreMessages.get().getMessage(orderReturn.getReturnStatus().getPropertyKey());
	}

	@Override
	protected void populateControls() {
		this.initiatedTimeText.setText(formatDate(orderReturn.getCreatedDate()));
		this.createdByText.setText(getDisplayableUserName(orderReturn.getCreatedByCmUser()));
		boolean isReceived = orderReturn.getReceivedByCmUser() != null;
		receivedByLabel.setVisible(isReceived);
		if (isReceived) {
			this.receivedByText.setText(getDisplayableUserName(orderReturn.getReceivedByCmUser()));
		}
		if (orderReturn.getReturnComment() != null) {
			notesText.setText(orderReturn.getReturnComment());
		}
		this.statusText.setText(getStatus());

		AuthorizationService authorizationService = AuthorizationService.getInstance();

		OrderReturnStatus status = orderReturn.getReturnStatus();
		boolean authorized = ((OrderEditor) getEditor()).isAuthorizedAndAvailableForEdit();

		if (isExchange()) {
			exchangeOrderNumberText.setText(orderReturn.getExchangeOrder().getOrderNumber());
			boolean authorizedExchange = authorized && authorizationService.isAuthorizedWithPermission(FulfillmentPermissions.CREATE_EDIT_EXCHANGES);
			List<OrderReturnSku> skuList = new LinkedList<>(orderReturn.getOrderReturnSkus());
			skuList.sort(OrderReturnSkuComparatorFactory.getOrderReturnSkuComparator());
			skuTable.setInput(skuList);
			cancelButton.setEnabled(isReturnOrExchangeCancellable(status, authorizedExchange));
			completeButton.setEnabled(authorizedExchange && status == OrderReturnStatus.AWAITING_COMPLETION);
		} else {
			final Locale locale = getLocale();
			this.returnTotalText.setText(getMoneyFormatter().formatCurrency(orderReturn.getReturnTotalMoney(), locale));
			refundedText.setText(getMoneyFormatter().formatCurrency(orderReturn.getRefundedTotalMoney(), locale));
			owedToCustomerText.setText(getMoneyFormatter().formatCurrency(orderReturn.getOwedToCustomerMoney(), locale));
			boolean authorizedReturn = authorized && authorizationService.isAuthorizedWithPermission(FulfillmentPermissions.CREATE_EDIT_RETURNS);
			List<OrderReturnSku> skuList = new LinkedList<>(orderReturn.getOrderReturnSkus());
			skuList.sort(OrderReturnSkuComparatorFactory.getOrderReturnSkuComparator());
			skuTable.setInput(skuList);

			cancelButton.setEnabled(isReturnOrExchangeCancellable(status, authorizedReturn));
			completeButton.setEnabled(authorizedReturn && status == OrderReturnStatus.AWAITING_COMPLETION);
			/** only for returns. */
			if (editButton != null) {
				editButton.setEnabled(authorizedReturn && status == OrderReturnStatus.AWAITING_STOCK_RETURN);
			}
		}

		resendRMAEmailButton.setEnabled(isPhysicalReturnEnabled() && status == OrderReturnStatus.AWAITING_STOCK_RETURN);
		getSection().setText(getSectionTitle());
		getSection().pack(true);
	}

	private boolean isReturnOrExchangeCancellable(final OrderReturnStatus status, final boolean authorizedExchange) {
		boolean awaitingStockReturn = status == OrderReturnStatus.AWAITING_STOCK_RETURN;
		return authorizedExchange && awaitingStockReturn && !orderReturn.isPartiallyReceived();
	}

	private Locale getLocale() {
		if (this.orderReturn != null && this.orderReturn.getOrder() != null) {
			return this.orderReturn.getOrder().getLocale();
		}
		return null;
	}

	@Override
	protected String getSectionDescription() {
		return getSectionTitle();
	}

	@Override
	protected String getSectionTitle() {
		if (isExchange()) {
			return
				NLS.bind(FulfillmentMessages.get().OrderReturnExchangeSection_Title,
				orderReturn.getRmaCode(), getStatus());
		}
		return
			NLS.bind(FulfillmentMessages.get().OrderReturnReturnSection_Title,
			orderReturn.getRmaCode(), getStatus());
	}

	private static String getDisplayableUserName(final CmUser user) {
		return user.getFirstName()
				+ ' ' //$NON-NLS-1$
				+ user.getLastName();
	}

	private String formatDate(final Date date) {
		return DateTimeUtilFactory.getDateUtil().formatAsDateTime(date);
	}

	protected MoneyFormatter getMoneyFormatter() {
		return ServiceLocator.getService(ContextIdNames.MONEY_FORMATTER);
	}

	/**
	 * Label provider for table of returned SKU's.
	 */
	@SuppressWarnings("PMD.CyclomaticComplexity")
	class SkuTableLabelProvider implements ITableLabelProvider {

		@Override
		public Image getColumnImage(final Object element, final int columnIndex) {
			return null;
		}

		@Override
		@SuppressWarnings("PMD.CyclomaticComplexity")
		public String getColumnText(final Object element, final int columnIndex) {
			OrderReturnSku orderReturnSku = (OrderReturnSku) element;
			int columnId = (Integer) skuTable.getSwtTable().getColumn(columnIndex).getData();

			switch (columnId) {
			case SKU_CODE_COLUMN_ID:
				return orderReturnSku.getOrderSku().getSkuCode();

			case PRODUCT_NAME_COLUMN_ID:
				return orderReturnSku.getOrderSku().getDisplayName();

			case RETURN_QTY_COLUMN_ID:
				return String.valueOf(orderReturnSku.getQuantity());

			case UNIT_PRICE_COLUMN_ID:
				return String.valueOf(orderReturnSku.getOrderSku().getUnitPrice());

			case REASON_COLUMN_ID:
				OrderReturnSkuReason orderReturnSkuReason = ServiceLocator.getService(
						ContextIdNames.ORDER_RETURN_SKU_REASON);
				return orderReturnSkuReason.getReasonMap().get(orderReturnSku.getReturnReason());

			case RECEIVED_QTY_COLUMN_ID:
				return String.valueOf(orderReturnSku.getReceivedQuantity());

			case RECEIVED_STATE_COLUMN_ID:
				if (orderReturnSku.getReceivedState() == null) {
					return ""; //$NON-NLS-1$
				}
				OrderReturnReceivedState orderReturnState = ServiceLocator.getService(
						ContextIdNames.ORDER_RETURN_RECEIVED_STATE);
				return orderReturnState.getStateMap().get(orderReturnSku.getReceivedState());

				// case RECEIVED_BY_COLUMN_ID:
				// if (orderReturnSku.getCmUser() == null) {
				// return ""; //$NON-NLS-1$
				// }
				// return "retrieve it from order return";//getDisplayableUserName(orderReturnSku.getCmUser()); //$NON-NLS-1$

			default:
				throw new EpUiException("Unknown column index (" + columnIndex + ")", null); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}

		@Override
		public void addListener(final ILabelProviderListener listener) {
			// do nothing

		}

		@Override
		public void dispose() {
			// do nothing
		}

		@Override
		public boolean isLabelProperty(final Object element, final String property) {
			// do nothing
			return false;
		}

		@Override
		public void removeListener(final ILabelProviderListener listener) {
			// do nothing
		}
	}

	/**
	 * Listener for return edit button.
	 */
	class ReturnEditButtonListener extends SelectionAdapter {
		@Override
		public void widgetSelected(final SelectionEvent event) {
			if (!editor.openDirtyEditorWarning(FulfillmentMessages.get().OrderReturnSection_EditorDirtyEditReturnTitle,
					FulfillmentMessages.get().OrderReturnSection_EditorDirtyEditReturnMessage)) {

				orderReturn.recalculateOrderReturn();
				ReturnWizard returnEditWizard = ReturnWizard.editReturnWizard(orderReturn);
				SubscribingDialog dialog = new SubscribingDialog(getSection().getShell(), returnEditWizard);

				if (dialog.open() == Window.OK) {
					reloadModel();
				}
			}
		}
	}

	/**
	 * Listener for return complete button.
	 */
	class ReturnCompleteButtonListener extends SelectionAdapter {
		@Override
		public void widgetSelected(final SelectionEvent event) {
			SubscribingDialog dialog;
			if (!editor.openDirtyEditorWarning(FulfillmentMessages.get().OrderReturnSection_EditorDirtyCompleteReturnTitle,
					FulfillmentMessages.get().OrderReturnSection_EditorDirtyCompleteReturnMessage)) {
				if (isExchange()) {
					ExchangeWizard completeExchangeWizard = ExchangeWizard.completeExchangeWizard(orderReturn);
					dialog = new SubscribingDialog(getSection().getShell(), completeExchangeWizard);
				} else {
					ReturnWizard completeReturnWizard = ReturnWizard.completeReturnWizard(orderReturn);
					dialog = new SubscribingDialog(getSection().getShell(), completeReturnWizard);
				}

				if (dialog.open() == Window.OK) {
					reloadModel();
				}
			}
		}
	}

	/**
	 * Listener for open exchange order button.
	 */
	class ExchangeOrderButtonListener extends SelectionAdapter {
		@Override
		public void widgetSelected(final SelectionEvent event) {
			OpenOrderEditorAction.showEditor(orderReturn.getExchangeOrder(), editor.getSite());
		}
	}

	/**
	 * Listener for open exchange order button.
	 */
	class CancelButtonListener extends SelectionAdapter {
		@Override
		public void widgetSelected(final SelectionEvent event) {
			String title, message;
			if (!editor.openDirtyEditorWarning(FulfillmentMessages.get().OrderReturnSection_EditorDirtyCancelReturnTitle,
					FulfillmentMessages.get().OrderReturnSection_EditorDirtyCancelReturnMessage)) {
				if (isExchange()) {
					title = FulfillmentMessages.get().OrderReturnSection_CancelExchangeTitle;
					message =
						NLS.bind(FulfillmentMessages.get().OrderReturnSection_CancelExchangeMessage,
						orderReturn.getRmaCode());
				} else {
					title = FulfillmentMessages.get().OrderReturnSection_CancelReturnTitle;
					message =
						NLS.bind(FulfillmentMessages.get().OrderReturnSection_CancelReturnMessage,
						orderReturn.getRmaCode());
				}

				if (MessageDialog.openConfirm(getSection().getShell(), title, message)) {
					try {
						returnAndExchangeService.cancelReturnExchange(orderReturn);
					} catch (OrderReturnOutOfDateException e) {
						MessageDialog.openError(getSection().getShell(), 
								FulfillmentMessages.get().OrderReturn_ErrDlgCollisionTitle,
								FulfillmentMessages.get().OrderReturn_ErrDlgCollisionMessage);
						return;
					}
					reloadModel();
				}
			}
		}
	}

	 /**
	 * Listener for return complete button.
	 */
	class ResendRMAEmailListener extends SelectionAdapter {
		@Override
		public void widgetSelected(final SelectionEvent event) {
			final ResendRMAEmailDialog dialog = new ResendRMAEmailDialog(null, orderReturn.getOrder().getCustomer().getEmail());
			if (dialog.open() == IDialogConstants.OK_ID) {
				resendEmail(dialog.getRecipientEmail());
			}
		}
	}
	
	/**
	 * Resends the Gift Certificate email to the specified address.
	 * 
	 * @param recipientEmail the email address to resend the email to
	 */
	protected void resendEmail(final String recipientEmail) {

		String message;

		try {
			returnAndExchangeService.resendReturnExchangeNotification(orderReturn.getUidPk(), recipientEmail);
			message = FulfillmentMessages.get().ResendRMAEmailSuccess;

		} catch (final Exception e) {
			LOG.error("Exception sending email ", e); //$NON-NLS-1$
			message = FulfillmentMessages.get().ResendRMAEmailFailure;
		}
		MessageDialog.openInformation(getSection().getShell(),
				FulfillmentMessages.get().ResendRMAEmailDialog_Title, message);
	}
	private boolean isExchange() {
		return orderReturn.getReturnType() == OrderReturnType.EXCHANGE;
	}
	
	private boolean isPhysicalReturnEnabled() {
		return orderReturn.getPhysicalReturn();
	}

	private void reloadModel() {
		editor.reloadModel();
		editor.refreshEditorPages();
	}

	private EventOriginator getEventOriginator() {
		EventOriginatorHelper helper = ServiceLocator.getService(
				ContextIdNames.EVENT_ORIGINATOR_HELPER);

		return helper.getCmUserOriginator(LoginManager.getCmUser());
	}
}
