/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.fulfillment.wizards;

import java.util.List;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.dialogs.PageChangingEvent;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.ui.framework.AbstractCmClientFormSectionPart;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.ui.framework.IEpTableColumn;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.cmclient.core.wizard.AbstractEPWizardPage;
import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;
import com.elasticpath.cmclient.fulfillment.domain.impl.OrderPaymentPresenterFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.order.Order;
import com.elasticpath.money.Money;
import com.elasticpath.money.MoneyFormatter;
import com.elasticpath.plugin.payment.exceptions.PaymentProcessingException;

/**
 * Reauthorization summary page. Displays list of successful or failed reauthorizations.
 */
public class ReAuthSummaryPage extends AbstractEPWizardPage<Order> {

	private IEpTableViewer table;

	private Label resultLabel;

	private AuthResultSection resultsSection;

	/**
	 * The constructor.
	 * 
	 * @param pageName wizard page name. Required by super.
	 * @param dataBindingContext not used.
	 */
	protected ReAuthSummaryPage(final String pageName, final DataBindingContext dataBindingContext) {
		super(1, false, pageName, dataBindingContext);
	}

	@Override
	protected void bindControls() {
		// nothing

	}

	@Override
	protected void createEpPageContent(final IEpLayoutComposite pageComposite) {

		setControl(pageComposite.getSwtComposite());

		final IManagedForm managedForm = EpControlFactory.getInstance().createManagedForm(pageComposite.getSwtComposite());
		final TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 1;
		final int margin = 10;
		layout.leftMargin = margin;
		layout.rightMargin = margin;

		final ScrolledForm scrolledForm = managedForm.getForm();
		scrolledForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		scrolledForm.getBody().setLayout(layout);

		resultsSection = new AuthResultSection(scrolledForm.getBody(), managedForm.getToolkit(), getDataBindingContext());
		managedForm.addPart(resultsSection);

	}

	@Override
	protected void populateControls() {
		// nothing

	}

	@Override
	public boolean beforeFromPrev(final PageChangingEvent event) {
		resultsSection.setData(getWizard().getReAuthorizationList());
		this.getControl().pack();
		this.getShell().pack();
		return super.beforeFromPrev(event);
	}

	@Override
	public ReAuthWizard getWizard() {
		return (ReAuthWizard) super.getWizard();
	}

	/**
	 * Display reauthorization result - list of succeeded or failed reauthorizations.
	 */
	class AuthResultSection extends AbstractCmClientFormSectionPart {

		private static final int COLUMN_WIDTH_SHIPMENT = 67;

		private static final int COLUMN_INDEX_SHIPMENT = 0;

		private static final int COLUMN_WIDTH_AMOUNT = 125;

		private static final int COLUMN_INDEX_AMOUNT = 1;

		private static final int COLUMN_WIDTH_PAYMENT_SOURCE = 250;

		private static final int COLUMN_INDEX_PAYMENT_SOURCE = 2;

		private static final int COLUMN_WIDTH_TRANSACTION_ID = 100;

		private static final int COLUMN_INDEX_TRANSACTION_ID = 3;

		private static final int COLUMN_WIDTH_ERROR = 120;

		private static final int COLUMN_INDEX_ERROR = 4;

		private static final String AUTH_SUMMARY_PAGE_TABLE = "Auth Summary Page Table"; //$NON-NLS-1$

		private Label adviceLabel;

		private IEpTableColumn errorColumn;

		/**
		 * The constructor.
		 * 
		 * @param parent parent composite.
		 * @param toolkit will be passed to super.
		 * @param dataBindingContext not used. will be passed to super.
		 */
		AuthResultSection(final Composite parent, final FormToolkit toolkit, final DataBindingContext dataBindingContext) {
			super(parent, toolkit, dataBindingContext, ExpandableComposite.TITLE_BAR);
		}

		/**
		 * Refresh results section content with new list of reauthorizations.
		 * 
		 * @param reAuthList list of reauthorization data for each shipment.
		 */
		public void setData(final List<ReAuthorizationItem> reAuthList) {
			table.setInput(getWizard().getReAuthorizationList());

			if (getWizard().isAllReAuthsSuccessful()) {
				resultsSection.getSection().setText(FulfillmentMessages.get().ReAuthWizard_Successful_SectionTitle);
				resultLabel.setText(FulfillmentMessages.get().ReAuthWizard_Successful_Message);
				adviceLabel.setVisible(false);
				errorColumn.getSwtTableColumn().setWidth(0);
				errorColumn.getSwtTableColumn().setResizable(false);
			} else {
				resultsSection.getSection().setText(FulfillmentMessages.get().ReAuthWizard_Failed_SectionTitle);
				resultLabel.setText(FulfillmentMessages.get().ReAuthWizard_Failed_Message);
				adviceLabel.setVisible(true);
				errorColumn.getSwtTableColumn().setWidth(COLUMN_WIDTH_ERROR);
				errorColumn.getSwtTableColumn().setResizable(true);
			}

		}

		@Override
		protected void bindControls(final DataBindingContext bindingContext) {
			// nothing

		}

		@Override
		protected void createControls(final Composite parent, final FormToolkit toolkit) {
			final IEpLayoutComposite controlPane = CompositeFactory.createTableWrapLayoutComposite(parent, 1, false);
			resultLabel = controlPane.addLabel("", controlPane.createLayoutData()); //$NON-NLS-1$

			IEpLayoutData layoutData = controlPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true);
			table = controlPane.addTableViewer(false, EpState.READ_ONLY, layoutData, AUTH_SUMMARY_PAGE_TABLE);
			table.addTableColumn(FulfillmentMessages.get().ReAuthWizard_Shipment_ColumnTitle, COLUMN_WIDTH_SHIPMENT);
			table.addTableColumn(FulfillmentMessages.get().ReAuthWizard_Amount_ColumnTitle, COLUMN_WIDTH_AMOUNT);
			table.addTableColumn(FulfillmentMessages.get().ReAuthWizard_PaymentSource_ColumnTitle, COLUMN_WIDTH_PAYMENT_SOURCE);
			table.addTableColumn(FulfillmentMessages.get().ReAuthWizard_TransactionID_ColumnTitle, COLUMN_WIDTH_TRANSACTION_ID);
			errorColumn = table.addTableColumn(FulfillmentMessages.get().ReAuthWizard_Error_ColumnTitle, COLUMN_WIDTH_ERROR);

			table.setContentProvider(new ArrayContentProvider());

			table.setLabelProvider(new ResultsTableLableProvider());

			adviceLabel = controlPane.addLabel(FulfillmentMessages.get().ReAuthWizard_AdviceOnError_Note, controlPane.createLayoutData());
		}

		@Override
		protected void populateControls() {
			// nothing

		}

		/**
		 * Label provider for results table.
		 */
		class ResultsTableLableProvider implements ITableLabelProvider {

			@Override
			public Image getColumnImage(final Object element, final int columnIndex) {
				return null;
			}

			@Override
			public String getColumnText(final Object element, final int columnIndex) {
				final ReAuthorizationItem reAuthTuple = (ReAuthorizationItem) element;

				switch (columnIndex) {
				case COLUMN_INDEX_SHIPMENT:
					return reAuthTuple.getShipment().getShipmentNumber();

				case COLUMN_INDEX_AMOUNT:
					final Money money = Money.valueOf(reAuthTuple.getShipment().getTotal(), getModel().getCurrency());
					final MoneyFormatter formatter = ServiceLocator.getService(ContextIdNames.MONEY_FORMATTER);
					return formatter.formatCurrency(money, getModel().getLocale());
				case COLUMN_INDEX_PAYMENT_SOURCE:
					if (reAuthTuple.getNewPayment() == null) {
						return ""; //$NON-NLS-1$
					}
					OrderPaymentPresenterFactory presenterFactory = new OrderPaymentPresenterFactory();
					return presenterFactory.getOrderPaymentPresenter(reAuthTuple.getNewPayment()).getDisplayPaymentDetails();
				case COLUMN_INDEX_TRANSACTION_ID:
					if (reAuthTuple.getNewPayment() == null) {
						return ""; //$NON-NLS-1$
					}
					return reAuthTuple.getNewPayment().getAuthorizationCode();

				case COLUMN_INDEX_ERROR:
					return getErrorLabel(reAuthTuple.getError());
				default:
					return null;
				}
			}

			private String getErrorLabel(final PaymentProcessingException error) {
				if (error == null) {
					return ""; //$NON-NLS-1$
				}
				return FulfillmentMessages.get().getCreditCardErrorMessage(error);
			}

			@Override
			public void addListener(final ILabelProviderListener listener) {
				// nothing
			}

			@Override
			public void dispose() {
				// nothing
			}

			@Override
			public boolean isLabelProperty(final Object element, final String property) {
				return false;
			}

			@Override
			public void removeListener(final ILabelProviderListener listener) {
				// nothing
			}

		}

	}

}
