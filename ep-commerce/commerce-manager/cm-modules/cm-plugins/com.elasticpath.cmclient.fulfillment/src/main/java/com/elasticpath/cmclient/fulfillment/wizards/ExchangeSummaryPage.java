/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.fulfillment.wizards;

import java.util.Currency;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.dialogs.PageChangingEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import com.elasticpath.cmclient.core.ui.framework.AbstractCmClientFormSectionPart;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.wizard.AbstractEPWizardPage;
import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;
import com.elasticpath.cmclient.fulfillment.wizards.ExchangeWizard.ExchangeWizardType;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderReturn;
import com.elasticpath.money.Money;

/**
 * Summary information about exchange wizard page.
 */
public class ExchangeSummaryPage extends AbstractEPWizardPage<OrderReturn> {

	private static final Logger LOG = Logger.getLogger(ExchangeSummaryPage.class);

	private static final String EMPTY_STRING = ""; //$NON-NLS-1$

	private RefundSummaryControl paymentSummary;

	private Label exchangeSummaryLabel;
	
	private Label exchangeSummaryAmountLabel;
	
	private Label originalOrderLabel;

	private Label originalOrderNumberLabel;

	private ConfirmationSectionPart confirmationSectionPart;

	/**
	 * The constructor.
	 * 
	 * @param pageName the page name
	 * @param message the message for this page
	 */
	protected ExchangeSummaryPage(final String pageName, final String message) {
		super(1, false, pageName, new DataBindingContext());
		LOG.debug("summary constructor"); //$NON-NLS-1$		
		setMessage(message);
		setPageComplete(false);
	}

	@Override
	protected void createEpPageContent(final IEpLayoutComposite parent) {
		IEpLayoutComposite composite = parent.addTableWrapLayoutComposite(1, true, parent.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL,
				true, true));
		TableWrapLayout tableWrapLayout = new TableWrapLayout();
		TableWrapData tableWrapData = new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.FILL_GRAB);
		final IManagedForm managedForm = EpControlFactory.getInstance().createManagedForm(composite.getSwtComposite());
		managedForm.getForm().getBody().setLayout(tableWrapLayout);
		managedForm.getForm().setLayoutData(tableWrapData);
		confirmationSectionPart = new ConfirmationSectionPart(managedForm.getForm().getBody(), managedForm.getToolkit(), getDataBindingContext());
		managedForm.addPart(confirmationSectionPart);
		this.setControl(parent.getSwtComposite());
	}
	
	@Override
	protected void bindControls() {
		// default implementation.
	}

	@Override
	protected void populateControls() {
		// nothing to populate here.
	}

	/**
	 * Confirmation section.
	 */
	class ConfirmationSectionPart extends AbstractCmClientFormSectionPart {

		/**
		 * Constructor.
		 * 
		 * @param parent the parent form
		 * @param toolkit the form toolkit
		 * @param dataBindingContext the form databinding context
		 */
		ConfirmationSectionPart(final Composite parent, final FormToolkit toolkit, final DataBindingContext dataBindingContext) {
			super(parent, toolkit, dataBindingContext, ExpandableComposite.TITLE_BAR);
			LOG.debug("SP confirmation constructor"); //$NON-NLS-1$
		}

		@Override
		protected void createControls(final Composite parent, final FormToolkit toolkit) {
			final IEpLayoutComposite controlPane = CompositeFactory.createGridLayoutComposite(parent, 1, false);
			final IEpLayoutData paneLayout = controlPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL);
			
			final IEpLayoutComposite confirmComposite = controlPane.addGridLayoutComposite(2, false, null);
			final IEpLayoutData fieldLayout = confirmComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL);
			final IEpLayoutData numberLayout = confirmComposite.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.FILL);
			exchangeSummaryLabel = confirmComposite.addLabel(EMPTY_STRING, fieldLayout);
			exchangeSummaryAmountLabel = confirmComposite.addLabelBold(EMPTY_STRING, numberLayout);
			originalOrderLabel = confirmComposite.addLabel(EMPTY_STRING, fieldLayout);
			originalOrderNumberLabel = confirmComposite.addLabel(EMPTY_STRING, numberLayout);
			
			paymentSummary = new RefundSummaryControl(controlPane.getSwtComposite(), paneLayout.getSwtLayoutData());
		}

		@Override
		protected void populateControls() {
			// nothing to populate here.
		}

		@Override
		protected String getSectionTitle() {
			return FulfillmentMessages.get().ExchangeWizard_Confirmation_Section;
		}

		@Override
		protected void bindControls(final DataBindingContext bindingContext) {
			// nothing to bind here.
		}

	}

	@Override
	public boolean beforeFromPrev(final PageChangingEvent event) { // NOPMD
		final OrderReturn exchange = getModel();

		final String orderNumber = exchange.getExchangeOrder().getOrderNumber();
		final Currency currency = exchange.getOrder().getCurrency();		
		OrderPayment payment = exchange.getReturnPayment(); // try to get refund payment
		if (payment == null && exchange.getExchangeOrder() != null) { 
			// if there is no one - try to get exchange's order default auth payment.
			payment = exchange.getExchangeOrder().getOrderPayment();
		}

		if (((ExchangeWizard) getWizard()).getExchangeWizardType() == ExchangeWizardType.CREATE_EXCHANGE) {
			exchangeSummaryLabel.setText(FulfillmentMessages.get().ExchangeWizard_NewExchangeCreated_Label);
			exchangeSummaryAmountLabel.setText(exchange.getRmaCode());
			originalOrderLabel.setText(FulfillmentMessages.get().ExchangeWizard_NewOrderCreated_Label);
			originalOrderNumberLabel.setText(orderNumber);
			
		} else if (((ExchangeWizard) getWizard()).getExchangeWizardType() == ExchangeWizardType.COMPLETE_EXCHANGE) {
			exchangeSummaryLabel.setText(FulfillmentMessages.get().ExchangeWizard_Exchange_Completed_Label);
			exchangeSummaryAmountLabel.setText(exchange.getRmaCode());
		}

		switch (((ExchangePaymentPage) this.getWizard().getPreviousPage(this))
				.getSelectionResult()) {
		case CREATE_WO_PAYMENT:
			
		case PHYSICAL_RETURN_REQUIRED:
			paymentSummary.setVisible(false);
			break;
		case REFUND_TO_ORIGINAL:
			paymentSummary.setRefundValues(FulfillmentMessages.get().formatMoneyAsString(
					Money.valueOf(payment.getAmount(), currency), exchange.getOrder().getLocale()), payment);
			break;
		case MANUAL_RETURN:
			paymentSummary.setManualRefundValues(FulfillmentMessages.get().formatMoneyAsString(
					Money.valueOf(payment.getAmount(), currency), exchange.getOrder().getLocale()));
			break;
		case ORIGINAL_PAYMENT:

		case NEW_PAYMENT:
			paymentSummary.setAdditionalAuthValues(FulfillmentMessages.get().formatMoneyAsString(
					Money.valueOf(payment.getAmount(), currency), exchange.getOrder().getLocale()), payment);
			break;
		default:
			break;
		}
		

		confirmationSectionPart.getManagedForm().getForm().pack();
		getShell().pack(true);
		setPageComplete(true);
		return true;
	}
}
