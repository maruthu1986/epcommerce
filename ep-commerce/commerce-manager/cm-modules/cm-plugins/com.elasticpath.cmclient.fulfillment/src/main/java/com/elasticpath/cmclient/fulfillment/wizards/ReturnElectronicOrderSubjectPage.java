/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.cmclient.fulfillment.wizards;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;

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
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.EpWizardPageSupport;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
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
import com.elasticpath.cmclient.fulfillment.helpers.AbstractValueTrackingEditingSupport;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.order.OrderReturn;
import com.elasticpath.domain.order.OrderReturnSku;
import com.elasticpath.domain.order.OrderReturnSkuReason;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.shoppingcart.ShoppingItemPricingSnapshot;
import com.elasticpath.service.shoppingcart.PricingSnapshotService;

/**
 * Electronic Order Return subject wizard page.
 */
@SuppressWarnings({"PMD.TooManyMethods", "PMD.GodClass"})
public class ReturnElectronicOrderSubjectPage extends AbstractEPWizardPage<OrderReturn> {

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
	protected ReturnElectronicOrderSubjectPage(final String pageName, final String message, 
			final String subjectSectionTitle, final OrderReturn orderReturn, final boolean isInclusiveTax) {
		super(1, true, pageName, new DataBindingContext());
		this.isInclusiveTax = isInclusiveTax;
		
		setMessage(message);

		this.subjectSectionTitle = subjectSectionTitle;

		this.orderReturn = orderReturn;
		
		this.orderShipment = orderReturn.getOrderShipmentForReturn();
	}
	
	/**
	 * Enabling binding to automate setting complete state of the page. This is necessary when the wizard is of the edit type.
	 */
	@Override
	protected void bindControls() {
		EpWizardPageSupport.create(this, getDataBindingContext());

		setPageComplete(true);
	}

	/**
	 * Nothing to populate cause all the controls are placed in their sections.
	 */
	@Override
	protected void populateControls() {
		// Nothing
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

	/**
	 * Page validation is performed exactly before the next page switching.
	 * 
	 * @param event page changing event parameters
	 * @return if page switching is allowed
	 */
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
		return orderSku.getUnitPrice();
	}

	/**
	 * Upper section part.
	 */
	@SuppressWarnings({ "PMD.TooManyFields" })
	private class SubjectSectionPart extends AbstractCmClientFormSectionPart {
		private static final int SUMMARY_INFO_COLUMNS = 3;
		private static final String RETURN_ELECTRONIC_ORDER_TABLE = "Return Electronic Order Table"; //$NON-NLS-1$

		private IEpTableViewer orderTableViewer;

		private IEpTableColumn returnQtyColumn;

		private IEpTableColumn reasonColumn;

		private final Map<String, Integer> reasonIndexMap;

		private final Map<Integer, String> reasonIndexReverseMap;

		private Text itemTotalBeforeTaxCurrencyText;

		private Text itemSubTotalText;

		private Text itemSubTotalCurrencyText;
		
		private Text shipmentDiscountCurrencyText;

		private Text shipmentDiscountText;

		private Text itemTotalBeforeTaxText;

		private Text itemTaxCurrencyText;

		private Text itemTaxText;
		
		private Text totalReturnAmountText;

		private Text totalReturnAmountCurrencyText;
		
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
			
				 
			shipmentDiscountText.addModifyListener((ModifyListener) event -> recalculate());
			
			
			

			binder.bind(bindingContext, shipmentDiscountText, EpValidatorFactory.NON_NEGATIVE_BIG_DECIMAL, null,
	    		  new ObservableUpdateValueStrategy() {
	    		          @Override
	    		          protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
	    		                  return Status.OK_STATUS;
	    		          }
	    		  }, hideDecorationOnFirstValidation);
	      
			bindingContext.updateModels();
		}

		@Override
		protected void createControls(final Composite client, final FormToolkit toolkit) {
			IEpLayoutComposite summaryPane = CompositeFactory.createTableWrapLayoutComposite(client, 1, false);
			summaryPane.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.FILL));

			final IEpLayoutData tableData = summaryPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false);
			orderTableViewer = summaryPane.addTableViewer(false, EpState.EDITABLE, tableData, RETURN_ELECTRONIC_ORDER_TABLE);
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

			shipmentSummaryInfoPane.addLabel(FulfillmentMessages.get().ReturnWizard_ShippingCostDiscount_Label,
					shipmentSummaryLabelData).setForeground(CmClientResources.getColor(CmClientResources.COLOR_RED));
			this.shipmentDiscountCurrencyText = shipmentSummaryInfoPane.addTextField(EpState.DISABLED, shipmentSummaryCurrencyData);

			this.shipmentDiscountText = shipmentSummaryInfoPane.addTextField(EpState.EDITABLE, shipmentSummaryData);

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

			// separator
			shipmentSummaryInfoPane.addHorizontalSeparator(separatorData);

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
			totalReturnAmountCurrencyText.setText(currencyCode);
			
			shipmentDiscountCurrencyText.setText(currencyCode);
			shipmentDiscountText.setText(BigDecimal.ZERO.setScale(currency.getDefaultFractionDigits())
						.toPlainString());

			returnQtyColumn.setEditingSupport(new ReturnQtyEditingSupport(orderTableViewer, new TextCellEditor(orderTableViewer.getSwtTable())));

			OrderReturnSkuReason orderReturnSkuReason = ServiceLocator.getService(ContextIdNames.ORDER_RETURN_SKU_REASON);
			
			Map<String, String> returnReasonMap = orderReturnSkuReason.getReasonMap();

			String[] reasons = new String[returnReasonMap.size()];

			int index = 0;

			for (String key : returnReasonMap.keySet()) {
				String value = returnReasonMap.get(key);

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

			final Currency currency = orderReturn.getCurrency();

			BigDecimal shippmentDiscount;
			try {
				shippmentDiscount = new BigDecimal(shipmentDiscountText.getText()).setScale(currency.getDefaultFractionDigits());
			} catch (NumberFormatException ex) {
				shippmentDiscount = BigDecimal.ZERO.setScale(currency.getDefaultFractionDigits());
			}

			orderReturn.setShipmentDiscount(shippmentDiscount);
			
			orderReturn.recalculateOrderReturn();
			
			itemSubTotalText.setText(orderReturn.getSubtotal().toString());
			itemTaxText.setText(orderReturn.getTaxTotal().toString());

			itemTotalBeforeTaxText.setText(orderReturn.getBeforeTaxReturnTotal().toString());

			totalReturnAmountText.setText(orderReturn.getReturnTotal().toString());

			totalReturnAmountText.getParent().pack();

			if (!isQuantityToReturnValid()) {

				setPageComplete(false);
				
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
		 * @return true if return total value is valid
		 */
		private boolean isReturnTotalValid() {
			shipmentTotalAvailableToReturn = new OrderReturnWizardValidator().calculateShipmentTotalLeftForReturn(orderReturn, orderShipment);
			return new OrderReturnWizardValidator().isShipmentTotalValid(orderReturn.getRefundTotal(), shipmentTotalAvailableToReturn);
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
			return isPageComplete() && isQuantityToReturnValid() && isReturnTotalValid();			
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
					if (returnQuantity >= 0 && returnQuantity <= orderReturnSku.getOrderSku().getReturnableQuantity()) {
						orderReturnSku.setQuantity(returnQuantity);
						orderReturnSku.setReturnAmount(BigDecimal.valueOf(returnQuantity)
								.multiply(getInvoicePrice(orderReturnSku.getOrderSku())));
						orderReturnSku.setReceivedQuantity(returnQuantity);
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
				text = orderReturnSku.getOrderSku().getDisplaySkuOptions();
				break;
			case SHIPPEDQTY_COLUMN_INDEX:
				text = String.valueOf(orderReturnSku.getOrderSku().getReturnableQuantity());
				break;
			case RETURN_QTY_COLUMN_INDEX:
				text = String.valueOf(orderReturnSku.getQuantity());
				break;
			case REASON_COLUMN_INDEX:
				OrderReturnSkuReason orderReturnSkuReason = ServiceLocator.getService(ContextIdNames.ORDER_RETURN_SKU_REASON);
				if (orderReturnSku.getReturnReason() == null) {
					orderReturnSku.setReturnReason(orderReturnSkuReason.getReasonMap().keySet().iterator().next());
				}
				text = orderReturnSkuReason.getReasonMap().get(orderReturnSku.getReturnReason());
				break;
			case INVOICE_PRICE_COLUMN_INDEX:
				OrderSku orderSku = orderReturnSku.getOrderSku();
				BigDecimal invoicePrice;
				ShoppingItemPricingSnapshot pricingSnapshot = getPricingSnapshotService().getPricingSnapshotForOrderSku(orderSku);
				if (pricingSnapshot.getDiscount() == null || pricingSnapshot.getDiscount().getAmount() == null) {
					invoicePrice = pricingSnapshot.getPriceCalc().forUnitPrice().getAmount();
				} else {
					invoicePrice = pricingSnapshot.getPriceCalc().forUnitPrice().getAmount().subtract(pricingSnapshot.getDiscount().getAmount());
				}
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

	/**
	 * Get the pricing snapshot service.
	 *
	 * @return the pricing snapshot service
	 */
	protected PricingSnapshotService getPricingSnapshotService() {
		if (pricingSnapshotService == null) {
			pricingSnapshotService = ServiceLocator.getService(ContextIdNames.PRICING_SNAPSHOT_SERVICE);
		}
		return pricingSnapshotService;
	}
}
