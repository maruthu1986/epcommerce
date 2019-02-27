/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.cmclient.fulfillment.wizards;

import java.math.BigDecimal;
import java.util.Currency;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.dialogs.PageChangingEvent;
import org.eclipse.swt.widgets.Label;

import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.wizard.AbstractEPWizardPage;
import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;
import com.elasticpath.domain.order.OrderReturn;
import com.elasticpath.service.order.ReturnExchangeType;

/**
 * Return method wizard page.
 */
public class ReturnElectronicOrderMethodPage extends AbstractEPWizardPage<OrderReturn> {
	private static final int UPPER_COLUMN_COUNT = 3;

	private static final int LOWER_COLUMN_COUNT = 1;

	private Label totalRefundAmountCurrencyLabel;

	private Label totalRefundAmountValueLabel;

	private RefundOptionsComposite refundOptionsComposite;

	/**
	 * The constructor.
	 * 
	 * @param pageName The page name
	 * @param message The message for this page
	 */
	protected ReturnElectronicOrderMethodPage(final String pageName, final String message) {

		super(1, true, pageName, new DataBindingContext());

		setMessage(message);
	}

	/**
	 * Nothing to execute, cause no binding is necessary.
	 */
	@Override
	protected void bindControls() {
		// Nothing
	}

	@Override
	protected void populateControls() {
		Currency currency = getModel().getCurrency();

		totalRefundAmountCurrencyLabel.setText(currency.getCurrencyCode());

		refundOptionsComposite.setEnabled(true);

		populateRefundTotal();

	}

	private void populateRefundTotal() {
		BigDecimal refundTotal = getModel().getRefundTotal();
		totalRefundAmountValueLabel.setText(refundTotal.toString());
		totalRefundAmountValueLabel.pack(true);
	}

	@Override
	protected void createEpPageContent(final IEpLayoutComposite parent) {
		IEpLayoutComposite upperComposite = parent.addTableWrapLayoutComposite(UPPER_COLUMN_COUNT, false, parent.createLayoutData());
		IEpLayoutComposite lowerComposite = parent.addTableWrapLayoutComposite(LOWER_COLUMN_COUNT, false, parent.createLayoutData());

		upperComposite.addLabelBold(FulfillmentMessages.get().ReturnWizard_TotalRefundAmount_Label, upperComposite.createLayoutData());
		totalRefundAmountCurrencyLabel = upperComposite.addLabel(FulfillmentMessages.EMPTY_STRING, upperComposite.createLayoutData());
		totalRefundAmountValueLabel = upperComposite.addLabel(FulfillmentMessages.EMPTY_STRING, upperComposite.createLayoutData());

		refundOptionsComposite = RefundOptionsComposite.createRefundOptionsComposite(getModel(),
				lowerComposite, null);

		this.setControl(parent.getSwtComposite());
	}

	/**
	 * RefundControl selection done by user.
	 * 
	 * @return refund control selection
	 */
	public ReturnExchangeType getRefundControlResult() {
		ReturnExchangeType res;
		if (refundOptionsComposite.useOriginalPayment()) {
			res = ReturnExchangeType.REFUND_TO_ORIGINAL;
		} else {
			res = ReturnExchangeType.MANUAL_RETURN;
		}

		return res;
	}

	/**
	 * The main action of the ReturnWizard is performed here. In this case this is a payment proceeding.
	 * 
	 * @param event page changing event parameters.
	 * @return if page switching is allowed.
	 */
	@Override
	public boolean beforeNext(final PageChangingEvent event) {
		boolean terminated = true;

		ReturnElectronicOrderWizard returnWizard = (ReturnElectronicOrderWizard) getWizard();
		terminated = returnWizard.createReturn();

		return terminated;
	}

	/**
	 * Updates refund value, cause it depends on the values entered by user in the previous page.
	 * 
	 * @param event page changing event parameters.
	 * @return if page switching is allowed.
	 */
	@Override
	public boolean beforeFromPrev(final PageChangingEvent event) {
		repopulate();

		return true;
	}

	private void repopulate() {
		populateRefundTotal();

		refundOptionsComposite.updateRefundToOriginalPaymentSourceRadioButtonText();
	}
}
