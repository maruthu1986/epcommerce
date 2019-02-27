/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.fulfillment.wizards;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;
import com.elasticpath.cmclient.fulfillment.domain.impl.OrderPaymentPresenterFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderReturn;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.plugin.payment.PaymentType;
import com.elasticpath.service.payment.PaymentService;

/**
 * Refund Options Composite.
 */
public final class RefundOptionsComposite {
	private static final String CLOSE_BRACE = ")"; //$NON-NLS-1$

	private static final String OPEN_BRACE = "("; //$NON-NLS-1$

	private final OrderReturn orderReturn;

	private Button refundToOriginalPaymentSourceRadioButton;

	private Button manualRefundRadioButton;

	private CautionLabelContainer cautionLabel;

	private final IEpLayoutComposite mainComposite;

	/**
	 * The constructor.
	 * 
	 * @param orderReturn
	 *            order return.
	 * @param pageComposite
	 *            composite.
	 * @param layoutData
	 *            layout.
	 */
	private RefundOptionsComposite(final OrderReturn orderReturn, final IEpLayoutComposite pageComposite, final IEpLayoutData layoutData) {
		this.orderReturn = orderReturn;
		this.mainComposite = pageComposite.addGridLayoutComposite(1, false, layoutData);
	}

	/**
	 * The RefundOptionsComposite factory method.
	 * 
	 * @param orderReturn
	 *            order return.
	 * @param pageComposite
	 *            composite.
	 * @param layoutData
	 *            layout.
	 * @return refund options composite.
	 */
	public static RefundOptionsComposite createRefundOptionsComposite(final OrderReturn orderReturn, final IEpLayoutComposite pageComposite,
			final IEpLayoutData layoutData) {

		RefundOptionsComposite refundOptionsComposite = new RefundOptionsComposite(orderReturn, pageComposite, layoutData);
		refundOptionsComposite.createEpPageContent();
		refundOptionsComposite.populateControls();

		return refundOptionsComposite;
	}

	private void createEpPageContent() {
		IEpLayoutComposite refundComposite = mainComposite;

		refundToOriginalPaymentSourceRadioButton = refundComposite.addRadioButton(
				"", EpState.EDITABLE, refundComposite.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.BEGINNING)); //$NON-NLS-1$
		refundToOriginalPaymentSourceRadioButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				cautionLabel.setVisible(refundToOriginalPaymentSourceRadioButton.getSelection());
			}

		});
		
		manualRefundRadioButton = refundComposite.addRadioButton(
				"", EpState.EDITABLE, refundComposite.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.BEGINNING)); //$NON-NLS-1$
		manualRefundRadioButton.setText(FulfillmentMessages.get().RefundOptionsComposite_ManualRefund_RadioButton);

		cautionLabel = CautionLabelContainer.create(refundComposite);
		cautionLabel.setVisible(false);
	}

	private void populateControls() {
		updateRefundToOriginalPaymentSourceRadioButtonText();
		refundToOriginalPaymentSourceRadioButton.setSelection(isRefundToOriginalPossible());
		manualRefundRadioButton.setSelection(!isRefundToOriginalPossible());
		manualRefundRadioButton.setEnabled(!isRefundToOriginalPossible());
	}

	private boolean isRefundToOriginalPossible() {
		OrderPayment originalCapturePayment = getOriginalCapturePayment();
		final PaymentService paymentService = ServiceLocator.getService(ContextIdNames.PAYMENT_SERVICE);
		return paymentService.isOrderPaymentRefundable(originalCapturePayment);
	}
	
	/**
	 * Only Manual Refund enabled.
	 * @param enabled if control is enabled.
	 */
	public void enableOnlyManualRefund(final boolean enabled) {
		refundToOriginalPaymentSourceRadioButton.setSelection(false);
		refundToOriginalPaymentSourceRadioButton.setEnabled(false);
		manualRefundRadioButton.setSelection(true);
		manualRefundRadioButton.setEnabled(enabled);
	}

	/**
	 * Enables composite and controls if the argument is <code>true</code>,
	 * and disables it otherwise.
	 * 
	 * @param enabled
	 *            if control should be enabled.
	 */
	public void setEnabled(final boolean enabled) {
		mainComposite.getSwtComposite().setEnabled(enabled);
		refundToOriginalPaymentSourceRadioButton.setEnabled(enabled && isRefundToOriginalPossible());
		manualRefundRadioButton.setEnabled(enabled);
		cautionLabel.setEnabled(enabled);

		if (!isRefundToOriginalPossible()) {
			refundToOriginalPaymentSourceRadioButton.setSelection(false);
			manualRefundRadioButton.setSelection(true);
		}

		cautionLabel.setVisible(enabled && refundToOriginalPaymentSourceRadioButton.getSelection());
	}

	/**
	 * @return true if original payment is selected.
	 */
	public boolean useOriginalPayment() {
		return refundToOriginalPaymentSourceRadioButton.getSelection();
	}

	/**
	 * Updates refundToOriginalPaymentSourceRadioButton text.
	 */
	public void updateRefundToOriginalPaymentSourceRadioButtonText() {
		OrderPayment originalCapturePayment = getOriginalCapturePayment();
		StringBuilder orderPaymentDescription = new StringBuilder(StringUtils.EMPTY);
		if (originalCapturePayment != null) {
			OrderPaymentPresenterFactory presenterFactory = new OrderPaymentPresenterFactory();
			orderPaymentDescription.append(presenterFactory.getOrderPaymentPresenter(getOriginalCapturePayment()).getDisplayPaymentDetails());
			if (orderPaymentDescription.length() > 0) {
				orderPaymentDescription.insert(0, FulfillmentMessages.SPACE);
				orderPaymentDescription.insert(1, OPEN_BRACE);
				orderPaymentDescription.append(CLOSE_BRACE);
			}
		}

		refundToOriginalPaymentSourceRadioButton.setText(FulfillmentMessages.get().RefundOptionsComposite_ReturnToOriginal_RadioButton
				+ orderPaymentDescription);
	}
		
	/**
	 * Gets original capture payment.
	 * @return the original payment capture for the order that is being refunded, or null if there was no original
	 * capture on this order (perhaps it was created as the result of an exchange).
	 */
	private OrderPayment getOriginalCapturePayment() {		
		final OrderShipment returnShipment = orderReturn.getOrderShipmentForReturn();
		final Order order = orderReturn.getOrder();
		for (OrderPayment orderPayment : order.getOrderPayments()) {
			if (orderPayment.getTransactionType().equals(OrderPayment.CAPTURE_TRANSACTION) 
					&& orderPayment.getPaymentMethod() != PaymentType.GIFT_CERTIFICATE
					&& orderPayment.getOrderShipment() != null && orderPayment.getOrderShipment().getUidPk() == returnShipment.getUidPk()) {
				return orderPayment;
			}
		}
		return null;
	}

	/**
	 * Caution label container. Needed to provide bold "Note" part of the label.  
	 */
	private static final class CautionLabelContainer {
		private final IEpLayoutComposite composite;

		private Label cautionLabel;

		private Label cautionHeaderLabel;

		private IEpLayoutComposite labelComposite;

		private CautionLabelContainer(final IEpLayoutComposite composite) {
			this.composite = composite;
		}
		
		public static CautionLabelContainer create(final IEpLayoutComposite composite) {
			CautionLabelContainer cautionLabelContainer = new CautionLabelContainer(composite);
			cautionLabelContainer.createControls();
			
			return cautionLabelContainer;
		}

		public void createControls() {
			labelComposite = composite.addTableWrapLayoutComposite(2, false, composite.createLayoutData(IEpLayoutData.BEGINNING,
					IEpLayoutData.BEGINNING));

			cautionHeaderLabel = labelComposite.addLabelBold(FulfillmentMessages.get().RefundOptionsComposite_CautionHeader_Label, labelComposite
					.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.BEGINNING));
			cautionLabel = labelComposite.addLabel(FulfillmentMessages.get().RefundOptionsComposite_Caution_Label, labelComposite.createLayoutData(
					IEpLayoutData.BEGINNING, IEpLayoutData.BEGINNING));
		}

		public void setVisible(final boolean visible) {
			cautionHeaderLabel.setVisible(visible);
			cautionLabel.setVisible(visible);
		}

		public void setEnabled(final boolean enabled) {
			cautionHeaderLabel.setEnabled(enabled);
			cautionLabel.setEnabled(enabled);
		}
	}
}
