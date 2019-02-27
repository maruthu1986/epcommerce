/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.fulfillment.editors.order;

import java.util.Date;
import java.util.Locale;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;

import com.elasticpath.cmclient.core.LoginManager;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.util.DateTimeUtilFactory;
import com.elasticpath.cmclient.core.editors.AbstractCmClientEditorPageSectionPart;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.fulfillment.FulfillmentImageRegistry;
import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;
import com.elasticpath.cmclient.fulfillment.event.FulfillmentEventService;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.event.EventOriginator;
import com.elasticpath.domain.event.EventOriginatorHelper;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderReturn;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.order.OrderStatus;
import com.elasticpath.money.MoneyFormatter;
import com.elasticpath.service.cmuser.CmUserService;
import com.elasticpath.service.order.OrderService;

/**
 * UI representation of the order summary overview section.
 */
@SuppressWarnings({"PMD.GodClass"})
public class OrderSummaryOverviewSectionPart extends AbstractCmClientEditorPageSectionPart implements SelectionListener, IPropertyListener {

	private Order order;

	private Text orderUidText;

	private Text storeNameText;

	private Text createdDateText;

	private Text currencyText;

	private Text orderTotalText;

	private Text balanceDueText;

	private Text cmUserNameText;

	private final CmUserService cmUserService;

	private Button cancelOrderButton;

	private Button holdOrderButton;

	private Button releaseOrderButton;

	private Text orderStatusText;

	private Text externalOrderSystemText;

	private Text externalOrderIdText;

	private Text rmaIdText;

	private Text exchangeDiscount;

	private final OrderEditor editor;

	private IEpLayoutComposite mainPane;

	/**
	 * Constructor.
	 * 
	 * @param editor the editor containing this Section Constructor to create a new Section in an editor's FormPage.
	 * @param formPage the formpage
	 */
	public OrderSummaryOverviewSectionPart(final FormPage formPage, final AbstractCmClientFormEditor editor) {
		super(formPage, editor, ExpandableComposite.TITLE_BAR | ExpandableComposite.EXPANDED);
		this.order = (Order) editor.getModel();
		cmUserService = ServiceLocator.getService(ContextIdNames.CMUSER_SERVICE);
		this.editor = (OrderEditor) editor;
		editor.addPropertyListener(this);
	}

	@Override
	protected void createControls(final Composite parentComposite, final FormToolkit toolkit) {

		EpState cancelButtonState;
		if (editor.isAuthorizedAndAvailableForEdit()) {
			cancelButtonState = EpState.EDITABLE;
		} else {
			cancelButtonState = EpState.READ_ONLY;
		}
		EpState onHoldButtonState;
		if (editor.isAuthorizedAndNotLocked()) {
			onHoldButtonState = EpState.EDITABLE;
		} else {
			onHoldButtonState = EpState.READ_ONLY;
		}

		mainPane = CompositeFactory.createTableWrapLayoutComposite(parentComposite, 2, false);
		final TableWrapData data = new TableWrapData(TableWrapData.FILL, TableWrapData.FILL);
		data.grabHorizontal = true;
		mainPane.setLayoutData(data);

		final IEpLayoutData compositeData = mainPane.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.BEGINNING);
		final IEpLayoutData labelData = mainPane.createLayoutData(IEpLayoutData.END, IEpLayoutData.CENTER);
		final IEpLayoutData fieldData = mainPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.BEGINNING, true, false);
		final IEpLayoutComposite cmUserComposite;

		final IEpLayoutComposite infoPane = mainPane.addTableWrapLayoutComposite(2, false, null);
		infoPane.addLabelBold(FulfillmentMessages.get().OrderSummaryOverviewSection_OrderStatus, labelData);
		orderStatusText = infoPane.addTextField(EpState.READ_ONLY, fieldData);

		infoPane.addLabelBold(FulfillmentMessages.get().OrderSummaryOverviewSection_OrderUid, labelData);
		orderUidText = infoPane.addTextField(EpState.READ_ONLY, fieldData);

		infoPane.addLabelBold(FulfillmentMessages.get().OrderSummaryOverviewSection_StoreName, labelData);
		storeNameText = infoPane.addTextField(EpState.READ_ONLY, fieldData);

		infoPane.addLabelBold(FulfillmentMessages.get().OrderSummaryOverviewSection_CreatedDate, labelData);
		createdDateText = infoPane.addTextField(EpState.READ_ONLY, fieldData);

		infoPane.addLabelBold(FulfillmentMessages.get().OrderSummaryOverviewSection_Currency, labelData);
		currencyText = infoPane.addTextField(EpState.READ_ONLY, fieldData);

		infoPane.addLabelBold(FulfillmentMessages.get().OrderSummaryOverviewSection_OrderTotal, labelData);
		orderTotalText = infoPane.addTextField(EpState.READ_ONLY, fieldData);

		infoPane.addLabelBold(FulfillmentMessages.get().OrderSummaryOverviewSection_BalanceDue, labelData);
		balanceDueText = infoPane.addTextField(EpState.READ_ONLY, fieldData);

		infoPane.addLabelBold(FulfillmentMessages.get().OrderSummaryOverviewSection_ExtOrderSystem, labelData);
		externalOrderSystemText = infoPane.addTextField(EpState.READ_ONLY, fieldData);

		if (order.isExchangeOrder()) {
			infoPane.addLabelBold(FulfillmentMessages.get().OrderSummaryOverviewSection_ExtOrder, labelData);
			externalOrderIdText = infoPane.addTextField(EpState.READ_ONLY, fieldData);

			infoPane.addLabelBold(FulfillmentMessages.get().OrderSummaryOverviewSection_RMA, labelData);
			rmaIdText = infoPane.addTextField(EpState.READ_ONLY, fieldData);

			infoPane.addLabelBold(FulfillmentMessages.get().OrderSummaryOverviewSection_DueToRMA, labelData);
			exchangeDiscount = infoPane.addTextField(EpState.READ_ONLY, fieldData);
		}

		// some orders not created through CSR
		if (this.order.getCmUserUID() != null) {
			infoPane.addLabelBold(FulfillmentMessages.get().OrderSummaryOverviewSection_CreatedBy, labelData);
			cmUserComposite = infoPane.addTableWrapLayoutComposite(2, false, compositeData);
			final Image cmUserImage = CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_USER);
			cmUserComposite.addImage(cmUserImage, fieldData);
			this.cmUserNameText = cmUserComposite.addTextField(EpState.READ_ONLY, fieldData);
		}

		final IEpLayoutComposite buttonsPane = mainPane.addTableWrapLayoutComposite(1, false, null);

		final Image cancelOrderImage = FulfillmentImageRegistry.getImage(FulfillmentImageRegistry.IMAGE_CANCEL_ORDER);
		cancelOrderButton = buttonsPane.addPushButton(FulfillmentMessages.get().OrderSummaryOverviewSection_CancelOrder, cancelOrderImage,
				cancelButtonState, null);
		cancelOrderButton.addSelectionListener(this);

		final Image holdOrderImage = FulfillmentImageRegistry.getImage(FulfillmentImageRegistry.IMAGE_HOLD_ORDER);
		holdOrderButton = buttonsPane.addPushButton(FulfillmentMessages.get().OrderSummaryOverviewSection_HoldOrder,
				holdOrderImage, onHoldButtonState, null);
		holdOrderButton.addSelectionListener(this);

		final Image releaseOrderImage = FulfillmentImageRegistry.getImage(FulfillmentImageRegistry.IMAGE_RELEASE_ORDER);
		releaseOrderButton = buttonsPane.addPushButton(FulfillmentMessages.get().OrderSummaryOverviewSection_ReleaseOrder, releaseOrderImage,
				onHoldButtonState, null);
		releaseOrderButton.addSelectionListener(this);
	}

	@Override
	protected void populateControls() {
		final Locale locale = this.order.getLocale();
		this.orderUidText.setText(String.valueOf(this.order.getOrderNumber()));
		this.storeNameText.setText(this.order.getStore().getName());
		this.createdDateText.setText(formatDate(this.order.getCreatedDate()));
		this.currencyText.setText(this.order.getCurrency().toString());
		this.orderTotalText.setText(getMoneyFormatter().formatCurrency(this.order.getTotalMoney(), locale));
		this.balanceDueText.setText(getMoneyFormatter().formatCurrency(this.order.getBalanceMoney(), locale));
		if (this.order.getCmUserUID() != null) {
			this.cmUserNameText.setText(cmUserService.get(this.order.getCmUserUID()).getFirstName()
					+ " " + cmUserService.get(this.order.getCmUserUID()).getLastName()); //$NON-NLS-1$
		}
		orderStatusText.setText(FulfillmentMessages.get().getLocalizedName(this.order.getStatus()));
		if (this.order.getOrderSource() != null) {
			externalOrderSystemText.setText(this.order.getOrderSource());
		}
		// String externalOrderNumber = this.order.getExternalOrderNumber(); //TODO: if this usage had the reason??
		if (externalOrderIdText != null) {
			OrderReturn exchange = order.getExchange();
			if (exchange != null) {
				externalOrderIdText.setText(String.valueOf(exchange.getOrder().getOrderNumber()));
			}

		}
		if (rmaIdText != null) {
			rmaIdText.setText(getRmaId(this.order));
		}
		if (exchangeDiscount != null) {
			exchangeDiscount.setText(getMoneyFormatter().formatCurrency(this.order.getDueToRMAMoney(), order.getLocale()));
		}

		updateButtons();
	}

	/**
	 *
	 */
	private void updateButtons() {
		if (editor.isAuthorizedAndNotLocked() && !this.getSection().isDisposed()) {
			cancelOrderButton.setEnabled(editor.isAuthorizedAndAvailableForEdit() && this.order.isCancellable()
					&& this.isShipmentsCanBeCanceled(order));
			holdOrderButton.setEnabled(editor.isAuthorizedAndNotLocked() && this.order.isHoldable());
			releaseOrderButton.setEnabled(editor.isAuthorizedAndNotLocked() && this.order.getStatus().equals(OrderStatus.ONHOLD));
		}
	}

	private boolean isShipmentsCanBeCanceled(final Order order) {
		for (OrderShipment shipment : order.getAllShipments()) {
			if (!shipment.isCancellable()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns the rma id if the order is a return order type.
	 * 
	 * @return RMA id or empty string if none
	 */
	private String getRmaId(final Order order) {
		OrderReturn exchange = order.getExchange();
		if (exchange != null) {
			return exchange.getRmaCode();
		}
		return ""; //$NON-NLS-1$
	}

	@Override
	protected void bindControls(final DataBindingContext bindingContext) {
		// not required
	}

	@Override
	protected String getSectionDescription() {
		return FulfillmentMessages.get().OrderSummaryOverviewSection_Description;
	}

	@Override
	protected String getSectionTitle() {
		return FulfillmentMessages.get().OrderSummaryOverviewSection_Title;
	}

	private String formatDate(final Date date) {
		return DateTimeUtilFactory.getDateUtil().formatAsDateTime(date);
	}

	@Override
	public void widgetDefaultSelected(final SelectionEvent event) {
		// not used
	}

	protected MoneyFormatter getMoneyFormatter() {
		return ServiceLocator.getService(ContextIdNames.MONEY_FORMATTER);
	}

	/**
	 * Implementers can be passed to checkAndAsk method as modifier parameter.
	 */
	interface IOrderModifier {

		/**
		 * Modify order.
		 */
		void modify();
	}

	private void checkAndAsk(final String warnTitle, final String warnMessage, final String confirmTitle, final String confirmMessage,
			final IOrderModifier modifier) {
		if (editor.openDirtyEditorWarning(warnTitle, warnMessage)) {
			return;
		}
	
		if (MessageDialog.openConfirm(this.getSection().getShell(), confirmTitle, confirmMessage + getOrderNumberString())) {
			this.order.setModifiedBy(getEventOriginator());

			modifier.modify();

			FulfillmentEventService.getInstance().fireOrderChangeEvent(new ItemChangeEvent<>(this, order));
			editor.reloadModel();
			editor.refreshEditorPages();
		}
	}

	/**
	 * Callback on selection event received.
	 * 
	 * @param event the event object
	 */
	@Override
	public void widgetSelected(final SelectionEvent event) {
		if (event.getSource() == cancelOrderButton) {
			checkAndAsk(FulfillmentMessages.get().OrderSummaryPage_EditorDirtyCancelOrder_Title,
					FulfillmentMessages.get().OrderSummaryPage_EditorDirtyCancelOrder_Message,
					FulfillmentMessages.get().OrderSummaryOverviewSection_DialogCancelTitle,
					FulfillmentMessages.get().OrderSummaryOverviewSection_DialogCancelMessage, () -> order = getOrderService().cancelOrder(order));
		} else if (event.getSource() == releaseOrderButton) {
			checkAndAsk(FulfillmentMessages.get().OredrSummaryPage_EditorDirtyRemoveHoldOrder_Title,
					FulfillmentMessages.get().OrderSummaryPage_EditorDirtyRemoveHoldOrder_Message,
					FulfillmentMessages.get().OrderSummaryOverviewSection_DialogReleaseTitle,
					FulfillmentMessages.get().OrderSummaryOverviewSection_DialogReleaseMessage,
					() -> order = getOrderService().releaseHoldOnOrder(order));
		} else if (event.getSource() == holdOrderButton) {
			checkAndAsk(FulfillmentMessages.get().OrderSummaryPage_EditorDirtyPlaceOnHoldOrder_Title,
					FulfillmentMessages.get().OrderSummaryPage_EditorDirtyPlaceOnHoldOrder_Message,
					FulfillmentMessages.get().OrderSummaryOverviewSection_DialogHoldTitle,
					FulfillmentMessages.get().OrderSummaryOverviewSection_DialogHoldMessage, () -> order = getOrderService().holdOrder(order));
		}
	}

	private EventOriginator getEventOriginator() {
		EventOriginatorHelper helper = ServiceLocator.getService(
				ContextIdNames.EVENT_ORIGINATOR_HELPER);

		return helper.getCmUserOriginator(LoginManager.getCmUser());
	}

	private OrderService getOrderService() {
		return (OrderService) ServiceLocator.getService(ContextIdNames.ORDER_SERVICE);

	}

	/**
	 * Returns the order number in a specific string format.
	 */
	private String getOrderNumberString() {
		return "\n\n#" + order.getOrderNumber(); //$NON-NLS-1$
	}

	@Override
	public void sectionDisposed() {
		editor.removePropertyListener(this);
	}
	
	@Override
	public void propertyChanged(final Object source, final int propId) {
		if (propId == IEditorPart.PROP_DIRTY) {
			updateButtons();
		}
		if (propId == OrderEditor.PROP_REFRESH_PARTS) {
			this.balanceDueText.setText(getMoneyFormatter().formatCurrency(this.order.getBalanceMoney(), order.getLocale()));
			this.orderTotalText.setText(getMoneyFormatter().formatCurrency(this.order.getTotalMoney(), order.getLocale()));
			mainPane.getSwtComposite().layout();
		}
	}
}
