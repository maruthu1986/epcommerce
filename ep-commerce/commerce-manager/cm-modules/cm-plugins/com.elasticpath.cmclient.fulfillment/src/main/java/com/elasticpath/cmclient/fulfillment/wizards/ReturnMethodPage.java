/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.fulfillment.wizards;

import java.math.BigDecimal;
import java.util.Currency;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.dialogs.PageChangingEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;

import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.wizard.AbstractEPWizardPage;
import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;
import com.elasticpath.domain.order.OrderReturn;
import com.elasticpath.service.order.ReturnExchangeType;

/**
 * Return method wizard page.
 */
public class ReturnMethodPage extends AbstractEPWizardPage<OrderReturn> {
	private static final int UPPER_COLUMN_COUNT = 3;

	private static final int LOWER_COLUMN_COUNT = 1;

	private Label totalRefundAmountCurrencyLabel;

	private Label totalRefundAmountValueLabel;

	private Button physicalReturnRequiredCheckbox;

	private RefundOptionsComposite refundOptionsComposite;

	/**
	 * The constructor.
	 * 
	 * @param pageName The page name
	 * @param message The message for this page
	 */
	protected ReturnMethodPage(final String pageName, final String message) {

		super(1, true, pageName, new DataBindingContext());

		setMessage(message);
	}

	@Override
	protected void bindControls() {
		// Nothing
	}

	@Override
	protected void populateControls() {
		Currency currency = getModel().getCurrency();

		totalRefundAmountCurrencyLabel.setText(currency.getCurrencyCode());

		physicalReturnRequiredCheckbox.setSelection(showPhysicalReturnCheckBox());

		refundOptionsComposite.setEnabled(!physicalReturnRequiredCheckbox.getSelection());

		populateRefundTotal();

	}

	private boolean showPhysicalReturnCheckBox() {
		return ((ReturnWizard) getWizard()).getReturnWizardType() != ReturnWizard.ReturnWizardType.COMPLETE_RETURN;
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

		upperComposite.addLabelBold(FulfillmentMessages.get().ReturnWizard_PhysicalReturnRequired_Label,
				upperComposite.createLayoutData()).setVisible(showPhysicalReturnCheckBox());
		physicalReturnRequiredCheckbox = upperComposite.addCheckBoxButton(FulfillmentMessages.EMPTY_STRING, EpState.EDITABLE, null);
		physicalReturnRequiredCheckbox.setVisible(showPhysicalReturnCheckBox());
		physicalReturnRequiredCheckbox.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				Button button = (Button) event.widget;
				refundOptionsComposite.setEnabled(!button.getSelection());
			}
		});

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
		ReturnExchangeType res = ReturnExchangeType.PHYSICAL_RETURN_REQUIRED;
		if (!physicalReturnRequiredCheckbox.getSelection()) {
			if (refundOptionsComposite.useOriginalPayment()) {
				res = ReturnExchangeType.REFUND_TO_ORIGINAL;
			} else {
				res = ReturnExchangeType.MANUAL_RETURN;
			}
		}

		return res;
	}

	@Override
	public boolean beforeNext(final PageChangingEvent event) {
		boolean terminated = true;

		ReturnWizard returnWizard = (ReturnWizard) getWizard();
		if (returnWizard.getReturnWizardType() == ReturnWizard.ReturnWizardType.CREATE_RETURN) {
			terminated = returnWizard.createReturn();
		} else if (returnWizard.getReturnWizardType() == ReturnWizard.ReturnWizardType.COMPLETE_RETURN) {
			terminated = returnWizard.completeReturn();
		}

		return terminated;
	}

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
