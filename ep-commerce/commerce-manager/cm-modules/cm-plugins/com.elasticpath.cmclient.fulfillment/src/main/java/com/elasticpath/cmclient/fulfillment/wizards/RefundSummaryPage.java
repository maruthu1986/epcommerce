/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.fulfillment.wizards;

import java.util.Locale;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.dialogs.PageChangingEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.ui.framework.AbstractCmClientFormSectionPart;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.wizard.AbstractEPWizardPage;
import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.money.MoneyFormatter;

/**
 * Page for refund confirmation.
 */
public class RefundSummaryPage extends AbstractEPWizardPage<Order> {

	private RefundSummaryControl refundSummary;

	/**
	 * The constructor.
	 * 
	 * @param pageName the page name
	 */
	protected RefundSummaryPage(final String pageName) {
		super(1, false, pageName, new DataBindingContext());
		setPageComplete(false);
	}

	@Override
	protected void createEpPageContent(final IEpLayoutComposite pageComposite) {
		final IManagedForm managedForm = EpControlFactory.getInstance().createManagedForm(pageComposite.getSwtComposite());
		managedForm.getForm().getBody().setLayout(new TableWrapLayout());
		final ConfirmationSectionPart confirmationSectionPart = new ConfirmationSectionPart(managedForm.getForm().getBody(), managedForm.getToolkit(),
				getDataBindingContext());
		managedForm.addPart(confirmationSectionPart);

		setControl(pageComposite.getSwtComposite());
	}

	@Override
	public boolean beforeFromPrev(final PageChangingEvent event) {
		final OrderPayment payment = getWizard().getProcessedPayment();
		if (payment != null) {
			refundSummary.setRefundValues(getMoneyFormatter().formatCurrency(payment.getAmountMoney(), getLocale(payment)), payment);
		}
		getShell().pack();
		setPageComplete(true);
		return true;
	}
	
	private Locale getLocale(final OrderPayment payment) {
		return payment.getOrder().getLocale();
	}

	@Override
	protected void bindControls() {
		// do nothing
	}

	@Override
	protected void populateControls() {
		// do nothing
	}

	@Override
	public RefundWizard getWizard() {
		return (RefundWizard) super.getWizard();
	}

	protected MoneyFormatter getMoneyFormatter() {
		return ServiceLocator.getService(ContextIdNames.MONEY_FORMATTER);
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
		 * @param dataBindingContext the form data binding context
		 */
		ConfirmationSectionPart(final Composite parent, final FormToolkit toolkit, final DataBindingContext dataBindingContext) {
			super(parent, toolkit, dataBindingContext, ExpandableComposite.TITLE_BAR);
		}

		@Override
		protected void createControls(final Composite parent, final FormToolkit toolkit) {
			final IEpLayoutComposite controlPane = CompositeFactory.createGridLayoutComposite(parent, 1, false);
			final IEpLayoutData fieldLayout = controlPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL);
			refundSummary = new RefundSummaryControl(controlPane.getSwtComposite(), fieldLayout.getSwtLayoutData());
		}

		@Override
		protected void populateControls() {
			// nothing to populate here.
		}

		@Override
		protected String getSectionTitle() {
			return FulfillmentMessages.get().RefundWizard_Confirmation_Section;
		}

		@Override
		protected void bindControls(final DataBindingContext bindingContext) {
			// nothing to bind here.
		}

	}
}
