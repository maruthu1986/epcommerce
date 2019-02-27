/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.fulfillment.wizards;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;
import com.elasticpath.cmclient.fulfillment.domain.OrderPaymentPresenter;
import com.elasticpath.cmclient.fulfillment.domain.impl.OrderPaymentPresenterFactory;
import com.elasticpath.domain.order.OrderPayment;

/**
 * Control to display refund summary info.
 */
public class RefundSummaryControl {

	private static final String EMPTY_STRING = ""; //$NON-NLS-1$

	private final IEpLayoutComposite mainComposite;
	
	private final IEpLayoutComposite transactionComposite;

	private final Label actionBeginLabel;
	
	private final Label actionEndLabel;
	
	private final Label amountLabel;

	private final Label cardLabel;

	private final Label transactonLabel;

	/**
	 * The constructor.
	 * 
	 * @param parent parent composite.
	 * @param layoutData layout data for this control.
	 */
	public RefundSummaryControl(final Composite parent, final Object layoutData) {

		final int columnNumber = 3;
		mainComposite = CompositeFactory.createTableWrapLayoutComposite(parent, columnNumber, false);
		mainComposite.setLayoutData(layoutData);
		actionBeginLabel = mainComposite.addLabel(EMPTY_STRING, mainComposite.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.BEGINNING));
		
		TableWrapLayout mainLayout = (TableWrapLayout) mainComposite.getSwtComposite().getLayout();
		mainLayout.verticalSpacing = 2 * mainLayout.verticalSpacing;
		amountLabel = mainComposite.addLabelBold(EMPTY_STRING, mainComposite.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.BEGINNING));
		
		actionEndLabel = mainComposite.addLabel(EMPTY_STRING, mainComposite.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.BEGINNING));
		
		//mainComposite.addLabel(FulfillmentMessages.get().Payment_End, mainComposite.createLayoutData(IEpLayoutData.FILL,
			//	IEpLayoutData.BEGINNING));

		cardLabel = mainComposite.addLabel(EMPTY_STRING, mainComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.BEGINNING, false, false,
				columnNumber, 1));

		transactionComposite = mainComposite.addTableWrapLayoutComposite(2, false, mainComposite.createLayoutData(
				IEpLayoutData.FILL, IEpLayoutData.BEGINNING, false, false, columnNumber, 1));
		final TableWrapLayout transactionLayout = (TableWrapLayout) transactionComposite.getSwtComposite().getLayout();
		transactionLayout.leftMargin = 0;
		transactionLayout.topMargin = 0;
		transactionComposite.addLabel(FulfillmentMessages.get().Payment_Confirmation_Number, transactionComposite.createLayoutData(
				IEpLayoutData.BEGINNING, IEpLayoutData.BEGINNING));
		transactonLabel = transactionComposite.addLabelBold(EMPTY_STRING, transactionComposite.createLayoutData(IEpLayoutData.BEGINNING,
				IEpLayoutData.BEGINNING));

	}

	/**
	 * Show refund summary values.
	 * 
	 * @param amount amount string.
	 * @param orderPayment credit card info.
	 */
	public void setRefundValues(final String amount, final OrderPayment orderPayment) {	
		actionBeginLabel.setText(FulfillmentMessages.get().Refund_Payment_Begin);
		setPaymentValues(amount, orderPayment);
	}
	
	/**
	 * Show manual refund summary values.
	 * 
	 * @param amount amount string.
	 */
	public void setManualRefundValues(final String amount) {	
		actionBeginLabel.setText(FulfillmentMessages.get().Manual_Refund_Payment_Begin);
		transactionComposite.getSwtComposite().setVisible(false);
		amountLabel.setText(amount);
		actionEndLabel.setText(FulfillmentMessages.get().Manual_Payment_End);
	}
	
	/**
	 * Show additional authorization summary values.
	 * 
	 * @param amount amount string.
	 * @param orderPayment credit card info.
	 */
	public void setAdditionalAuthValues(final String amount, final OrderPayment orderPayment) {	
		actionBeginLabel.setText(FulfillmentMessages.get().Additional_Payment_Begin);
		setPaymentValues(amount, orderPayment);
	}

	private void setPaymentValues(final String amount, final OrderPayment orderPayment) {
		amountLabel.setText(amount);
		actionEndLabel.setText(FulfillmentMessages.get().Payment_End);
		OrderPaymentPresenter presenter = new OrderPaymentPresenterFactory().getOrderPaymentPresenter(orderPayment);
		cardLabel.setText(presenter.getDisplayPaymentDetails());
		transactonLabel.setText(String.valueOf(presenter.getDisplayTransactionId()));
		transactionComposite.getSwtComposite().setVisible(true);
		mainComposite.getSwtComposite().pack();
	}
	/**
	 * Marks the receiver as visible if the argument is <code>true</code>, and marks it invisible otherwise.
	 * 
	 * @param visible the new visibility state.
	 */
	public void setVisible(final boolean visible) {
		mainComposite.getSwtComposite().setVisible(visible);
	}

}
