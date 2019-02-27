/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.fulfillment.wizards;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.PageChangingEvent;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.CmClientResources;
import com.elasticpath.cmclient.core.CorePlugin;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.EpWizardPageSupport;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.helpers.LocalProductSkuLookup;
import com.elasticpath.cmclient.core.ui.framework.AbstractCmClientFormSectionPart;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.ui.framework.IEpTableColumn;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.cmclient.core.wizard.AbstractEPWizardPage;
import com.elasticpath.cmclient.fulfillment.FulfillmentImageRegistry;
import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;
import com.elasticpath.cmclient.fulfillment.editors.order.OrderSkuOptionRenderer;
import com.elasticpath.cmclient.fulfillment.editors.order.OrderSkuOptionRendererImpl;
import com.elasticpath.cmclient.fulfillment.helpers.AbstractValueTrackingEditingSupport;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.order.OrderReturn;
import com.elasticpath.domain.order.OrderReturnSku;
import com.elasticpath.domain.order.OrderReturnSkuReason;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.order.PhysicalOrderShipment;
import com.elasticpath.money.Money;
import com.elasticpath.service.catalog.ProductSkuLookup;
import com.elasticpath.service.shoppingcart.PricingSnapshotService;

/**
 * Order return subject wizard page.
 */
@SuppressWarnings({"PMD.TooManyMethods", "PMD.GodClass", "PMD.ExcessiveImports"})
public class ReturnSubjectPage extends AbstractEPWizardPage<OrderReturn> {
	private static final int CONTENT_TABLE_HEIGHT = 400;

	private static final int TABLE_HEIGHT = 100;

	private static final int SKUCODE_COLUMN_INDEX = 1;

	private static final int PRODUCTNAME_COLUMN_INDEX = 2;

	private static final int SKUOPTIONS_COLUMN_INDEX = 3;

	private static final int SHIPPEDQTY_COLUMN_INDEX = 4;

	private static final int INVOICE_PRICE_COLUMN_INDEX = 5;

	private static final int RETURN_QTY_COLUMN_INDEX = 6;

	private static final int REASON_COLUMN_INDEX = 7;


	private SubjectSectionPart subjectSectionPart;

	private NoteSectionPart noteSectionPart;

	private final String subjectSectionTitle;

	private final OrderReturn orderReturn;

	private final boolean isInclusiveTax;

	private final OrderShipment orderShipment;
	private ProductSkuLookup productSkuLookup;
	private PricingSnapshotService pricingSnapshotService;

	/**
	 * The constructor.
	 *
	 * @param pageName the page name
	 * @param message the message for this page
	 * @param subjectSectionTitle subject section title
	 * @param orderReturn the order return.
	 * @param isInclusiveTax should the page be relevant for tax inclusive shipment 
	 */
	protected ReturnSubjectPage(final String pageName, final String message,
			final String subjectSectionTitle, final OrderReturn orderReturn, final boolean isInclusiveTax) {
		super(1, true, pageName, new DataBindingContext());
		this.isInclusiveTax = isInclusiveTax;

		// this.resetErrorListener = new ResetErrorListener();

		setMessage(message);

		this.subjectSectionTitle = subjectSectionTitle;
		this.orderReturn = orderReturn;
		this.orderShipment = orderReturn.getOrderShipmentForReturn();
	}

	@Override
	protected void bindControls() {
		EpWizardPageSupport.create(this, getDataBindingContext());

		setPageComplete(false);
	}

	@Override
	protected void populateControls() {
		// Nothing
	}

	/**
	 *
	 * Encapsulates the logic around order return validation.
	 */
	protected static class OrderReturnValidator {
		/**
		 * Checks whether shipping cost is valid by comparing the return shipping cost
		 * to remaining shipping cost available for return.
		 *
		 * @param returnShippingCost - the return shipping cost.
		 * @param remainingShippingCost - the shipping cost available for return.
		 * @return true if the value for shipping cost is valid
		 */
		public boolean isShippingCostValid(final BigDecimal returnShippingCost, final BigDecimal remainingShippingCost) {
			return returnShippingCost.compareTo(remainingShippingCost) <= 0;
		}

		/**
		 * Checks whether shipment total is valid by comparing the return shipmen total
		 * to remaining shipment total.
		 *
		 * @param returnShipmentTotal - the return shipment total.
		 * @param remainingShipmentTotal - the shipment total available for return.
		 * @return true if the value for shipment total is valid
		 */
		public boolean isShipmentTotalValid(final BigDecimal returnShipmentTotal,
				final BigDecimal remainingShipmentTotal) {
			return returnShipmentTotal.compareTo(remainingShipmentTotal) <= 0;
		}


		/**
		 * Checks whether or not the return skus quantity is valid.
		 *
		 * @param returnSkus - the order return skus
		 * @return true if one of the returnSkus quantity is greater than 0
		 */
		public boolean isQuantityToReturnValid(final Set<OrderReturnSku> returnSkus) {
			for (OrderReturnSku currentOrderReturnSku : returnSkus) {
				if (currentOrderReturnSku.getQuantity() > 0) {
					return true;
				}
			}
			return false;
		}
	}



	@Override
	protected void createEpPageContent(final IEpLayoutComposite parent) {
		IEpLayoutComposite composite = parent.addTableWrapLayoutComposite(1, true, parent.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL,
				true, true));

		TableWrapLayout tableWrapLayout = new TableWrapLayout();
		TableWrapData tableWrapData = new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.FILL_GRAB);
		tableWrapData.heightHint = CONTENT_TABLE_HEIGHT;

		IManagedForm managedForm = EpControlFactory.getInstance().createManagedForm(composite.getSwtComposite());
		managedForm.getForm().getBody().setLayout(tableWrapLayout);
		managedForm.getForm().setLayoutData(tableWrapData);

		subjectSectionPart = new SubjectSectionPart(managedForm.getForm().getBody(), managedForm.getToolkit(), getDataBindingContext());
		managedForm.addPart(subjectSectionPart);
		noteSectionPart = new NoteSectionPart(managedForm.getForm().getBody(), managedForm.getToolkit(), getDataBindingContext());
		managedForm.addPart(noteSectionPart);

		this.setControl(parent.getSwtComposite());
	}

	@Override
	public boolean beforeNext(final PageChangingEvent event) {
		return validate();
	}

	/**
	 * Validates contents before finish or going to the next page.
	 *
	 * @return false if validation failed.
	 */
	public boolean validate() {
		return subjectSectionPart.validate() && noteSectionPart.validate();
	}

	private void resetError() {
		setPageComplete(true);
		setErrorMessage(null);
	}

	private void setError(final String message) {
		setPageComplete(false);
		setErrorMessage(message);
	}

	/**
	 * @param orderSku the order SKU
	 * @return the invoice price
	 */
	BigDecimal getInvoicePrice(final OrderSku orderSku) {
		Money discount = getPricingSnapshotService().getPricingSnapshotForOrderSku(orderSku).getDiscount();

		BigDecimal invoicePrice;
		if (discount == null || discount.getAmount() == null) {
			invoicePrice = orderSku.getUnitPrice();
		} else {
			invoicePrice = orderSku.getUnitPrice().subtract(discount.getAmount());
		}

		return invoicePrice;
	}

	/**
	 * Get the (remote) Pricing Snapshot Service.
	 *
	 * @return the pricing snapshot service
	 */
	protected PricingSnapshotService getPricingSnapshotService() {
		if (pricingSnapshotService == null) {
			pricingSnapshotService = ServiceLocator.getService(ContextIdNames.PRICING_SNAPSHOT_SERVICE);
		}
		return pricingSnapshotService;
	}

	/**
	 * Lazy loads a ProductSkuLookup.
	 *
	 * @return a product sku reader.
	 */
	protected ProductSkuLookup getProductSkuLookup() {
		if (productSkuLookup == null) {
			productSkuLookup = new LocalProductSkuLookup();
		}

		return productSkuLookup;
	}

	/**
	 * Upper section part.
	 */
	@SuppressWarnings({ "PMD.TooManyFields" })
	private class SubjectSectionPart extends AbstractCmClientFormSectionPart {
		private static final int SUMMARY_INFO_COLUMNS = 3;
		private static final String RETURN_SUBJECT_PAGE_TABLE = "Return Subject Page Table"; //$NON-NLS-1$

		private IEpTableViewer orderTableViewer;

		private IEpTableColumn returnQtyColumn;

		private IEpTableColumn reasonColumn;

		private final Map<String, Integer> reasonIndexMap;

		private final Map<Integer, String> reasonIndexReverseMap;

		private Text itemTotalBeforeTaxCurrencyText;

		private Text itemSubTotalText;

		private Text itemSubTotalCurrencyText;

		private Text itemShippingCostText;

		private Text itemShippingCostCurrencyText;

		private Text shipmentDiscountCurrencyText;

		private Text shipmentDiscountText;

		private Text itemTotalBeforeTaxText;

		private Text itemTaxCurrencyText;

		private Text itemTaxText;

		private Text shippingTaxCurrencyText;

		private Text shippingTaxText;

		private Text lessRestockingFeeText;

		private Text lessRestockingFreCurrencyText;

		private Text totalReturnAmountText;

		private Text totalReturnAmountCurrencyText;

		private BigDecimal shippingCostAvailableToReturn = BigDecimal.ZERO;

		private BigDecimal shipmentTotalAvailableToReturn = BigDecimal.ZERO;

		SubjectSectionPart(final Composite parent, final FormToolkit toolkit, final DataBindingContext dataBindingContext) {
			super(parent, toolkit, dataBindingContext, ExpandableComposite.TITLE_BAR);
			reasonIndexMap = new HashMap<>();
			reasonIndexReverseMap = new HashMap<>();
		}

		@Override
		protected void bindControls(final DataBindingContext bindingContext) {
			final boolean hideDecorationOnFirstValidation = true;
			final EpControlBindingProvider binder = EpControlBindingProvider.getInstance();

			if (orderShipment instanceof PhysicalOrderShipment) {

				itemShippingCostText.addModifyListener((ModifyListener) event -> recalculate());

				shipmentDiscountText.addModifyListener((ModifyListener) event -> recalculate());
			}

			lessRestockingFeeText.addModifyListener((ModifyListener) event -> recalculate());

			binder.bind(bindingContext, lessRestockingFeeText, EpValidatorFactory.NON_NEGATIVE_BIG_DECIMAL, null,
					new ObservableUpdateValueStrategy() {
						@Override
						protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
							return Status.OK_STATUS;
						}
					}, hideDecorationOnFirstValidation);

			if (orderShipment instanceof PhysicalOrderShipment) {

				binder.bind(bindingContext, itemShippingCostText, EpValidatorFactory.NON_NEGATIVE_BIG_DECIMAL, null,
						new ObservableUpdateValueStrategy() {
							@Override
							protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
								return Status.OK_STATUS;
							}
						}, hideDecorationOnFirstValidation);

				binder.bind(bindingContext, shipmentDiscountText, EpValidatorFactory.NON_NEGATIVE_BIG_DECIMAL, null,
						new ObservableUpdateValueStrategy() {
							@Override
							protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
								return Status.OK_STATUS;
							}
						}, hideDecorationOnFirstValidation);
			}

			bindingContext.updateModels();
		}

		@Override
		protected void createControls(final Composite client, final FormToolkit toolkit) {
			IEpLayoutComposite summaryPane = CompositeFactory.createTableWrapLayoutComposite(client, 1, false);
			summaryPane.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.FILL));

			final IEpLayoutData tableData = summaryPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false);
			orderTableViewer = summaryPane.addTableViewer(false, EpState.EDITABLE, tableData, RETURN_SUBJECT_PAGE_TABLE);
			orderTableViewer.getSwtTable().setLinesVisible(true);

			((TableWrapData) orderTableViewer.getSwtTable().getLayoutData()).maxHeight = TABLE_HEIGHT;

			final int[] orderTableColumnWidths = { 21, 80, 120, 80, 80, 120, 70, 120};

			final String[] orderTableColumnNames = { FulfillmentMessages.EMPTY_STRING, FulfillmentMessages.get().ReturnWizard_SKUCode_Column,
					FulfillmentMessages.get().ReturnWizard_ProductName_Column, FulfillmentMessages.get().ReturnWizard_SKUOptions_Column,
					FulfillmentMessages.get().ReturnWizard_ReturnableQty_Column, FulfillmentMessages.get().ReturnWizard_InvoicePrice_Column,
					FulfillmentMessages.get().ReturnWizard_ReturnQty_Column, FulfillmentMessages.get().ReturnWizard_Reason_Column};

			for (int i = 0; i < orderTableColumnWidths.length; i++) {
				IEpTableColumn column = orderTableViewer.addTableColumn(orderTableColumnNames[i], orderTableColumnWidths[i]);

				switch (i) {
					case RETURN_QTY_COLUMN_INDEX:
						returnQtyColumn = column;
						break;
					case REASON_COLUMN_INDEX:
						reasonColumn = column;
						break;
					default:
						break;
				}
			}

			orderTableViewer.setContentProvider(new ArrayContentProvider());
			orderTableViewer.setLabelProvider(new OrderTableLabelProvider());
			orderTableViewer.setInput(orderReturn.getOrderReturnSkus());

			final IEpLayoutData shippingCostPaneData = summaryPane.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.CENTER, true, false);
			IEpLayoutComposite shipmentSummaryInfoPane = summaryPane.addTableWrapLayoutComposite(SUMMARY_INFO_COLUMNS, false, shippingCostPaneData);

			final IEpLayoutData shipmentSummaryData = shipmentSummaryInfoPane
					.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.CENTER, true, false);
			final IEpLayoutData shipmentSummaryCurrencyData = shipmentSummaryInfoPane.createLayoutData(IEpLayoutData.END, IEpLayoutData.CENTER);
			final IEpLayoutData shipmentSummaryLabelData = summaryPane.createLayoutData(IEpLayoutData.END, IEpLayoutData.CENTER);

			shipmentSummaryInfoPane.addLabelBold(FulfillmentMessages.get().ReturnWizard_ItemSubTotal_Label, shipmentSummaryLabelData);
			this.itemSubTotalCurrencyText = shipmentSummaryInfoPane.addTextField(EpState.READ_ONLY, shipmentSummaryCurrencyData);
			this.itemSubTotalText = shipmentSummaryInfoPane.addTextField(EpState.READ_ONLY, shipmentSummaryData);

			if (orderShipment instanceof PhysicalOrderShipment) {
				shipmentSummaryInfoPane.addLabel(FulfillmentMessages.get().ReturnWizard_ItemShippingCost_Label, shipmentSummaryLabelData);
				this.itemShippingCostCurrencyText = shipmentSummaryInfoPane.addTextField(EpState.READ_ONLY, shipmentSummaryCurrencyData);

				this.itemShippingCostText = shipmentSummaryInfoPane.addTextField(EpState.EDITABLE, shipmentSummaryData);

				shipmentSummaryInfoPane.addLabel(FulfillmentMessages.get().ReturnWizard_ShippingCostDiscount_Label,
						shipmentSummaryLabelData).setForeground(CmClientResources.getColor(CmClientResources.COLOR_RED));
				this.shipmentDiscountCurrencyText = shipmentSummaryInfoPane.addTextField(EpState.DISABLED, shipmentSummaryCurrencyData);

				this.shipmentDiscountText = shipmentSummaryInfoPane.addTextField(EpState.EDITABLE, shipmentSummaryData);

			}

			IEpLayoutData separatorData = shipmentSummaryInfoPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL,
					false, false, SUMMARY_INFO_COLUMNS, 1);
			// separator
			shipmentSummaryInfoPane.addHorizontalSeparator(separatorData);

			// set specific label for the case when we have tax inclusive calculation
			if (isInclusiveTax) {
				shipmentSummaryInfoPane.addLabel(FulfillmentMessages.get().ReturnWizard_ItemTotal_Label, shipmentSummaryLabelData);
			} else {
				shipmentSummaryInfoPane.addLabel(FulfillmentMessages.get().ReturnWizard_ItemTotalBeforeTax_Label, shipmentSummaryLabelData);
			}
			this.itemTotalBeforeTaxCurrencyText = shipmentSummaryInfoPane.addTextField(EpState.READ_ONLY, shipmentSummaryCurrencyData);
			this.itemTotalBeforeTaxText = shipmentSummaryInfoPane.addTextField(EpState.READ_ONLY, shipmentSummaryData);

			shipmentSummaryInfoPane.addLabel(FulfillmentMessages.get().ReturnWizard_ItemTaxes_Label, shipmentSummaryLabelData);
			this.itemTaxCurrencyText = shipmentSummaryInfoPane.addTextField(EpState.READ_ONLY, shipmentSummaryCurrencyData);
			this.itemTaxText = shipmentSummaryInfoPane.addTextField(EpState.READ_ONLY, shipmentSummaryData);

			if (orderShipment instanceof PhysicalOrderShipment) {
				shipmentSummaryInfoPane.addLabel(FulfillmentMessages.get().ReturnWizard_ItemShippingTax_Label, shipmentSummaryLabelData);
				this.shippingTaxCurrencyText = shipmentSummaryInfoPane.addTextField(EpState.READ_ONLY, shipmentSummaryCurrencyData);
				this.shippingTaxText = shipmentSummaryInfoPane.addTextField(EpState.READ_ONLY, shipmentSummaryData);
			}

			// separator
			shipmentSummaryInfoPane.addHorizontalSeparator(separatorData);

			shipmentSummaryInfoPane.addLabel(FulfillmentMessages.get().ReturnWizard_LessRestockingFee_Label, shipmentSummaryLabelData).setForeground(
					CmClientResources.getColor(CmClientResources.COLOR_RED));
			this.lessRestockingFreCurrencyText = shipmentSummaryInfoPane.addTextField(EpState.READ_ONLY, shipmentSummaryCurrencyData);
			this.lessRestockingFeeText = shipmentSummaryInfoPane.addTextField(EpState.EDITABLE, shipmentSummaryData);

			shipmentSummaryInfoPane.addLabelBold(FulfillmentMessages.get().ReturnWizard_TotalReturnAmount_Label, shipmentSummaryLabelData);
			this.totalReturnAmountCurrencyText = shipmentSummaryInfoPane.addTextField(EpState.READ_ONLY, shipmentSummaryCurrencyData);
			this.totalReturnAmountText = shipmentSummaryInfoPane.addTextField(EpState.READ_ONLY, shipmentSummaryData);
		}

		@Override
		protected void populateControls() {
			final Currency currency = orderReturn.getCurrency();
			String currencyCode = currency.getCurrencyCode();

			itemSubTotalCurrencyText.setText(currencyCode);
			itemTaxCurrencyText.setText(currencyCode);
			itemTotalBeforeTaxCurrencyText.setText(currencyCode);
			lessRestockingFreCurrencyText.setText(currencyCode);
			totalReturnAmountCurrencyText.setText(currencyCode);

			if (orderShipment instanceof PhysicalOrderShipment) {
				itemShippingCostCurrencyText.setText(currencyCode);
				shipmentDiscountCurrencyText.setText(currencyCode);
				shippingTaxCurrencyText.setText(currencyCode);

				shipmentDiscountText.setText(BigDecimal.ZERO.setScale(currency.getDefaultFractionDigits())
						.toPlainString());
				itemShippingCostText.setText(orderReturn.getShippingCost().setScale(currency.getDefaultFractionDigits())
						.toPlainString());
			}

			returnQtyColumn.setEditingSupport(new ReturnQtyEditingSupport(orderTableViewer, new TextCellEditor(orderTableViewer.getSwtTable())));

			OrderReturnSkuReason orderReturnSkuReason = ServiceLocator.getService(
					ContextIdNames.ORDER_RETURN_SKU_REASON);

			String[] reasons = new String[orderReturnSkuReason.getReasonMap().size()];

			int index = 0;

			for (String key : orderReturnSkuReason.getReasonMap().keySet()) {
				String value = orderReturnSkuReason.getReasonMap().get(key);

				reasons[index] = value;
				reasonIndexMap.put(key, index);
				reasonIndexReverseMap.put(index, key);

				index++;
			}

			reasonColumn.setEditingSupport(new ReasonEditingSupport(orderTableViewer, new ComboBoxCellEditor(orderTableViewer.getSwtTable(),
					reasons, SWT.DROP_DOWN | SWT.READ_ONLY), reasonIndexMap, reasonIndexReverseMap));

			recalculate();
		}

		@Override
		protected String getSectionTitle() {
			return subjectSectionTitle;
		}

		@SuppressWarnings({ "PMD.NPathComplexity" })
		private void recalculate() {

			BigDecimal lessRestockingFee = BigDecimal.ZERO;
			BigDecimal inputShippingCost = BigDecimal.ZERO;
			BigDecimal shippingCostDiscount = BigDecimal.ZERO;
			final Currency currency = orderReturn.getCurrency();

			try {
				lessRestockingFee = new BigDecimal(lessRestockingFeeText.getText());
			} catch (NumberFormatException ex) {
				lessRestockingFeeText.setText(lessRestockingFee.toString());
			}

			if (orderShipment instanceof PhysicalOrderShipment) {
				try {
					inputShippingCost = new BigDecimal(itemShippingCostText.getText()).setScale(currency.getDefaultFractionDigits());
				} catch (NumberFormatException ex) {
					inputShippingCost = BigDecimal.ZERO.setScale(currency.getDefaultFractionDigits());
				}

				try {
					shippingCostDiscount = new BigDecimal(shipmentDiscountText.getText()).setScale(currency.getDefaultFractionDigits());
				} catch (NumberFormatException ex) {
					shippingCostDiscount = BigDecimal.ZERO.setScale(currency.getDefaultFractionDigits());
				}

				orderReturn.setShippingCost(inputShippingCost);

				orderReturn.setShipmentDiscount(shippingCostDiscount);
			}

			orderReturn.setLessRestockAmount(lessRestockingFee);

			orderReturn.recalculateOrderReturn();

			itemSubTotalText.setText(orderReturn.getSubtotal().toString());
			itemTaxText.setText(orderReturn.getTaxTotal().toString());

			itemTotalBeforeTaxText.setText(orderReturn.getBeforeTaxReturnTotal().toString());

			if (orderShipment instanceof PhysicalOrderShipment) {
				// setting data instead of text otherwise it will fall into an infinite loop trying to recalculate each time
				itemShippingCostText.setData(orderReturn.getShippingCost().toPlainString());

				shippingTaxText.setText(orderReturn.getShippingTax().toPlainString());

				shipmentDiscountText.setData(shippingCostDiscount.toPlainString());
			}

			totalReturnAmountText.setText(orderReturn.getReturnTotal().toString());

			totalReturnAmountText.getParent().pack();

			if (!isQuantityToReturnValid()) {

				setPageComplete(false);

				return;
			}

			if (lessRestockingFee.compareTo(BigDecimal.ZERO) < 0
					|| (orderReturn.getSubtotal() != null && lessRestockingFee.compareTo(orderReturn.getSubtotal()) > 0)) {

				setError(FulfillmentMessages.get().ReturnWizard_LessRestcokingFeeError_Msg);
				return;
			}

			if (orderShipment instanceof PhysicalOrderShipment && !isShippingCostValid()) {
				setError(FulfillmentMessages.get().ReturnWizard_ShippingCostError_Msg  + " " + //$NON-NLS-1$
						orderReturn.getCurrency().getCurrencyCode() + " " + shippingCostAvailableToReturn); //$NON-NLS-1$
				return;
			}

			if (!isReturnTotalValid()) {
				setError(FulfillmentMessages.get().ReturnWizard_ShipmentTotal_Msg  + " " + //$NON-NLS-1$
						orderReturn.getCurrency().getCurrencyCode() + " " + shipmentTotalAvailableToReturn); //$NON-NLS-1$
				return;
			}

			resetError();
		}

		/**
		 *
		 *
		 * @return true if shipping cost value is valid
		 */
		private boolean isShippingCostValid() {
			shippingCostAvailableToReturn = new OrderReturnWizardValidator().calculateShippingCostLeftForReturn(orderReturn, orderShipment);
			return new OrderReturnWizardValidator().isShippingCostValid(orderReturn.getShippingCost(), shippingCostAvailableToReturn);
		}

		/**
		 *
		 *
		 * @return true if return total value is valid
		 */
		private boolean isReturnTotalValid() {
			shipmentTotalAvailableToReturn = new OrderReturnWizardValidator().calculateShipmentTotalLeftForReturn(orderReturn, orderShipment);
			return new OrderReturnWizardValidator().isShipmentTotalValid(orderReturn.getReturnTotal(), shipmentTotalAvailableToReturn);
		}

		/**
		 * Check for valid quantity to return.
		 * @return true if at least one product has non zero quantity to return. 
		 */
		private boolean isQuantityToReturnValid() {
			return new OrderReturnWizardValidator().isQuantityToReturnValid(orderReturn.getOrderReturnSkus());
		}

		/**
		 * Validating the page for input errors.
		 */
		public boolean validate() {
			boolean isValid = isPageComplete() && isQuantityToReturnValid() && isReturnTotalValid();
			if (orderShipment instanceof PhysicalOrderShipment) {
				isValid = isValid && isShippingCostValid();
			}

			return isValid;
		}



		/**
		 * Editing support for Return Qty column.
		 */
		private class ReturnQtyEditingSupport extends AbstractValueTrackingEditingSupport {
			ReturnQtyEditingSupport(final IEpTableViewer orderTableViewer, final CellEditor cellEditor) {
				super(orderTableViewer.getSwtTableViewer(), cellEditor);
			}

			@Override
			protected Object doGetValue(final Object element) {
				return String.valueOf(((OrderReturnSku) element).getQuantity());
			}

			@Override
			protected void finishEditor() {
				recalculate();
			}

			@Override
			protected void doChangeValue(final Object element, final Object value) {
				OrderReturnSku orderReturnSku = (OrderReturnSku) element;

				if (EpValidatorFactory.REQUIRED.validate(value) == ValidationStatus.ok()
						&& EpValidatorFactory.NON_NEGATIVE_INTEGER.validate(value) == ValidationStatus.ok()) {

					int returnQuantity = Integer.valueOf(value.toString());
					final ProductSku productSku = getProductSkuLookup().findByGuid(orderReturnSku.getOrderSku().getSkuGuid());
					if (returnQuantity >= productSku.getProduct().getMinOrderQty()
							&& returnQuantity <= orderReturnSku.getOrderSku().getReturnableQuantity()) {
						orderReturnSku.setQuantity(returnQuantity);
						orderReturnSku.setReturnAmount(orderReturnSku.getAmountMoney().getAmount());
						orderReturn.updateOrderReturnStatus();
						recalculate();
						return;
					}
				}

				setError(FulfillmentMessages.get().ReturnWizard_ReturnQtyError_Msg);
			}
		}

		/**
		 * Editing support for Return Reason column.
		 */
		private class ReasonEditingSupport extends EditingSupport {

			private final ComboBoxCellEditor cellEditor;

			private final Map<String, Integer> reasonIndexMap;

			private final Map<Integer, String> reasonIndexReverseMap;

			ReasonEditingSupport(final IEpTableViewer orderTableViewer, final ComboBoxCellEditor cellEditor,
					final Map<String, Integer> reasonIndexMap, final Map<Integer, String> reasonIndexReverseMap) {

				super(orderTableViewer.getSwtTableViewer());

				this.cellEditor = cellEditor;
				this.reasonIndexMap = reasonIndexMap;
				this.reasonIndexReverseMap = reasonIndexReverseMap;
			}

			@Override
			protected boolean canEdit(final Object element) {
				return true;
			}

			@Override
			protected CellEditor getCellEditor(final Object element) {
				return cellEditor;
			}

			@Override
			protected Object getValue(final Object element) {
				return reasonIndexMap.get(((OrderReturnSku) element).getReturnReason());
			}

			@Override
			protected void setValue(final Object element, final Object value) {
				((OrderReturnSku) element).setReturnReason(reasonIndexReverseMap.get(Integer.valueOf(value.toString())));

				getViewer().update(element, null);
			}
		}
	}

	/**
	 * Lower section part.
	 */
	private class NoteSectionPart extends AbstractCmClientFormSectionPart {
		private static final int NOTES_HEIGHT = 100;

		// private static final int NOTES_WIDTH = 105;

		private Text notes;

		NoteSectionPart(final Composite parent, final FormToolkit toolkit, final DataBindingContext dataBindingContext) {
			super(parent, toolkit, dataBindingContext, ExpandableComposite.TITLE_BAR);
		}

		@Override
		protected void bindControls(final DataBindingContext bindingContext) {
			final boolean hideDecorationOnFirstValidation = true;
			final EpControlBindingProvider binder = EpControlBindingProvider.getInstance();

			binder.bind(bindingContext, notes, orderReturn, "returnComment", //$NON-NLS-1$
					EpValidatorFactory.MAX_LENGTH_2000, null, hideDecorationOnFirstValidation);
		}

		@Override
		protected void createControls(final Composite client, final FormToolkit toolkit) {

			final IEpLayoutComposite controlPane = CompositeFactory.createTableWrapLayoutComposite(client, 1, false);
			controlPane.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.FILL_GRAB));

			notes = controlPane.addTextArea(true, false, EpState.EDITABLE, controlPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL,
					true, true));

			((TableWrapData) notes.getLayoutData()).heightHint = NOTES_HEIGHT;
		}

		@Override
		protected void populateControls() {
			String comment = orderReturn.getReturnComment();
			if (comment == null) {
				notes.setText(FulfillmentMessages.EMPTY_STRING);
			} else {
				notes.setText(comment);
			}

		}

		@Override
		protected String getSectionTitle() {
			return FulfillmentMessages.get().ReturnWizard_Notes_Section;
		}

		/**
		 * Validation is not needed, cause there is no any controls that should be validated. But the method left to keep similarity in section part
		 * implementation.
		 */
		private boolean validate() {
			return true;
		}
	}

	/**
	 * LabelProvider for the main table.
	 */
	private class OrderTableLabelProvider implements ITableLabelProvider {
		private final OrderSkuOptionRenderer skuOptionRenderer;

		OrderTableLabelProvider() {
			ProductSkuLookup productSkuLookup = ServiceLocator.getService(ContextIdNames.PRODUCT_SKU_LOOKUP);

			this.skuOptionRenderer = new OrderSkuOptionRendererImpl(productSkuLookup);
		}

		@Override
		public Image getColumnImage(final Object element, final int columnIndex) {
			Image image = null;

			switch (columnIndex) {
				case 0:
					image = FulfillmentImageRegistry.ICON_ORDERTABLE_ITEM.createImage();
					break;
				case RETURN_QTY_COLUMN_INDEX:
					image = FulfillmentImageRegistry.ICON_ORDERTABLE_RETURN_QTY.createImage();
					break;
				case REASON_COLUMN_INDEX:
					image = FulfillmentImageRegistry.ICON_ORDERTABLE_REASON.createImage();
					break;
				default:
					// Nothing;
					break;
			}

			return image;
		}

		@Override
		public String getColumnText(final Object element, final int columnIndex) {
			OrderReturnSku orderReturnSku = (OrderReturnSku) element;

			String text = FulfillmentMessages.EMPTY_STRING;

			switch (columnIndex) {
				case SKUCODE_COLUMN_INDEX:
					text = orderReturnSku.getOrderSku().getSkuCode();
					break;
				case PRODUCTNAME_COLUMN_INDEX:
					text = orderReturnSku.getOrderSku().getDisplayName();
					break;
				case SKUOPTIONS_COLUMN_INDEX:
					text = skuOptionRenderer.getDisplaySkuOptions(orderReturnSku.getOrderSku(), CorePlugin.getDefault().getDefaultLocale());
					break;
				case SHIPPEDQTY_COLUMN_INDEX:
					text = String.valueOf(orderReturnSku.getOrderSku().getReturnableQuantity());
					break;
				case RETURN_QTY_COLUMN_INDEX:
					text = String.valueOf(orderReturnSku.getQuantity());
					break;
				case REASON_COLUMN_INDEX:
					OrderReturnSkuReason orderReturnSkuReason = ServiceLocator.getService(
							ContextIdNames.ORDER_RETURN_SKU_REASON);
					if (orderReturnSku.getReturnReason() == null) {
						orderReturnSku.setReturnReason(orderReturnSkuReason.getReasonMap().keySet().iterator().next());
					}
					text = orderReturnSkuReason.getReasonMap().get(orderReturnSku.getReturnReason());
					break;
				case INVOICE_PRICE_COLUMN_INDEX:
					BigDecimal invoicePrice = getInvoicePrice(orderReturnSku.getOrderSku());
					text = invoicePrice.toString();
					break;
				default:
					text = FulfillmentMessages.EMPTY_STRING;
					break;
			}

			return text;
		}

		@Override
		public void dispose() {
			// Nothing to dispose
		}

		@Override
		public boolean isLabelProperty(final Object element, final String property) {
			return false;
		}

		@Override
		public void addListener(final ILabelProviderListener listener) {
			// Nothing
		}

		@Override
		public void removeListener(final ILabelProviderListener listener) {
			// Nothing
		}
	}
}
