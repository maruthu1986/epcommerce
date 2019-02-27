/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.fulfillment.wizards;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.dialogs.PageChangingEvent;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.ui.framework.AbstractCmClientFormSectionPart;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.wizard.AbstractEPWizardPage;
import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;
import com.elasticpath.cmclient.fulfillment.wizards.ReturnWizard.ReturnWizardType;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderReturn;
import com.elasticpath.money.Money;
import com.elasticpath.money.MoneyFormatter;
import com.elasticpath.service.order.ReturnExchangeType;

/**
 * Summary ReturnWizard page.
 */
public class ReturnSummaryPage extends AbstractEPWizardPage<OrderReturn> {

	private ConfirmationSectionPart confirmationSectionPart;

	private RefundSummaryControl paymentSummary;

	private Label returnSummaryLabel;

	/**
	 * The constructor.
	 *
	 * @param pageName The page name
	 */
	protected ReturnSummaryPage(final String pageName) {
		super(1, true, pageName, new DataBindingContext());
		setPageComplete(false);
	}

	@Override
	protected void createEpPageContent(final IEpLayoutComposite parent) {
		final IEpLayoutComposite composite = parent.addTableWrapLayoutComposite(1, true, parent.createLayoutData(IEpLayoutData.FILL,
				IEpLayoutData.FILL, true, true));

		TableWrapLayout tableWrapLayout = new TableWrapLayout();
		TableWrapData tableWrapData = new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.FILL_GRAB);

		IManagedForm managedForm = EpControlFactory.getInstance().createManagedForm(composite.getSwtComposite());
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

	@Override
	public boolean beforeFromPrev(final PageChangingEvent event) {
		final OrderReturn orderReturn = ((ReturnWizard) getWizard()).getModel();

		final ReturnExchangeType selectionResult = ((ReturnMethodPage) this.getWizard().getPreviousPage(this))
				.getRefundControlResult();

		final Currency currency = orderReturn.getOrder().getCurrency();
		final BigDecimal amount = orderReturn.getRefundedTotal();
		final String amountMoney = getAmountString(amount, currency, orderReturn.getOrder().getLocale());
		final OrderPayment payment = orderReturn.getReturnPayment(); // try to get refund payment

		if (((ReturnWizard) getWizard()).getReturnWizardType() == ReturnWizardType.CREATE_RETURN) {
			returnSummaryLabel.setText(
				NLS.bind(FulfillmentMessages.get().ReturnWizard_Return_Created_Label,
				orderReturn.getRmaCode()));
		} else if (((ReturnWizard) getWizard()).getReturnWizardType() == ReturnWizardType.COMPLETE_RETURN) {
			returnSummaryLabel.setText(
				NLS.bind(FulfillmentMessages.get().ReturnWizard_Return_Completed_Label,
				orderReturn.getRmaCode()));
		}

		if (BigDecimal.ZERO.compareTo(amount) == 0) {
			paymentSummary.setVisible(false);
		} else {
			switch (selectionResult) {
			case PHYSICAL_RETURN_REQUIRED:
				paymentSummary.setVisible(false);
				break;
			case REFUND_TO_ORIGINAL:
				paymentSummary.setRefundValues(amountMoney, payment);
				break;
			case MANUAL_RETURN:
				paymentSummary.setManualRefundValues(amountMoney);
				break;			
			default:
				break;
			}
		}

		confirmationSectionPart.getManagedForm().getForm().pack();
		getShell().pack(true);
		setPageComplete(true);
		return true;
	}

	/**
	 * Section part.
	 */
	private class ConfirmationSectionPart extends AbstractCmClientFormSectionPart {

		ConfirmationSectionPart(final Composite parent, final FormToolkit toolkit, final DataBindingContext dataBindingContext) {
			super(parent, toolkit, dataBindingContext, ExpandableComposite.TITLE_BAR);
		}

		@Override
		protected void createControls(final Composite parent, final FormToolkit toolkit) {
			final IEpLayoutComposite controlPane = CompositeFactory.createGridLayoutComposite(parent, 1, false);
			final IEpLayoutData fieldLayout = controlPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL);
			returnSummaryLabel = controlPane.addLabel(FulfillmentMessages.EMPTY_STRING, fieldLayout);
			paymentSummary = new RefundSummaryControl(controlPane.getSwtComposite(), fieldLayout.getSwtLayoutData());
		}

		@Override
		protected void populateControls() {
			// nothing to populate here.
		}

		@Override
		protected String getSectionTitle() {
			return FulfillmentMessages.get().ReturnWizard_Summary_Section;
		}

		@Override
		protected void bindControls(final DataBindingContext bindingContext) {
			// nothing to bind here.
		}
	}
	
	private static String getAmountString(final BigDecimal amount, final Currency currency, final Locale locale) {
		final Money money = Money.valueOf(amount, currency);
		final MoneyFormatter formatter = ServiceLocator.getService(ContextIdNames.MONEY_FORMATTER);
		return formatter.formatCurrency(money, locale);
	}
}
