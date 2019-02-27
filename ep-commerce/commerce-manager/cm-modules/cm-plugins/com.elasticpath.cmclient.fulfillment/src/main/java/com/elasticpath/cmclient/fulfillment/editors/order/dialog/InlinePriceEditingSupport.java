/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.fulfillment.editors.order.dialog;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.binding.EpBindingConfiguration;
import com.elasticpath.cmclient.core.binding.EpBindingConfiguration.ValidationErrorLocation;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.dialog.value.cell.AbstractCellEditorValueWrapper;
import com.elasticpath.cmclient.core.dialog.value.cell.AdvancedTableCellEditor;
import com.elasticpath.cmclient.core.dialog.value.cell.TableValuesProvider;
import com.elasticpath.cmclient.core.helpers.LocalProductSkuLookup;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.ui.framework.AbstractInlineEditingSupport;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;
import com.elasticpath.common.dto.pricing.DisplayPriceDTO;
import com.elasticpath.common.pricing.service.PriceListLookupService;
import com.elasticpath.common.pricing.service.PriceLookupFacade;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.util.Pair;
import com.elasticpath.domain.RecalculableObject;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.PriceTier;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.pricing.PriceListStack;
import com.elasticpath.domain.shoppingcart.ExchangeItem;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.ShoppingItemPricingSnapshot;
import com.elasticpath.service.catalog.ProductSkuLookup;
import com.elasticpath.service.shoppingcart.PricingSnapshotService;

/**
 * Inline editing support for item price.
 */
@SuppressWarnings("PMD.GodClass")
public class InlinePriceEditingSupport extends AbstractInlineEditingSupport {
	
	private static final Logger LOG = Logger.getLogger(InlinePriceEditingSupport.class);
	
	/** Column indices. */
	private static final int INDEX_IMAGE = 0;
	private static final int INDEX_NAME = 1;
	private static final int INDEX_LISTPRICE = 2;
	private static final int INDEX_QTY = 3;
	private static final int INDEX_SALEPRICE = 4;
	
	/** Column sizes. */
	private static final int INDEX_IMAGE_SIZE = 10;
	private static final int INDEX_NAME_SIZE = 120;
	private static final int INDEX_LISTPRICE_SIZE = 90;
	private static final int INDEX_QTY_SIZE = 50;
	private static final int INDEX_SALEPRICE_SIZE = 90;
	
	private static final int ROWS_LIMIT = 5;
	
	private final IEpTableViewer viewer;
	
	private final AdvancedTableCellEditor cellEditor;
	
	private final PriceLookupFacade priceLookupFacade = ServiceLocator.getService(ContextIdNames.PRICE_LOOKUP_FACADE);

	private final PriceListLookupService priceListLookupService = 
		(PriceListLookupService) ServiceLocator.getService(ContextIdNames.PRICE_LIST_LOOKUP_SERVICE);
	private ProductSkuLookup productSkuLookup;

	private Action updatePriceAction;

	private final Currency orderCurrency;

	private PricingSnapshotService pricingSnapshotService;

	/**
	 * Label provider for Cell Editor.
	 */
	class PriceLabelProvider extends LabelProvider implements ITableLabelProvider {

		/**
		 * Returns text for specified column.
		 * 
		 * @param arg0 - model object
		 * @param columnIndex - index of the column starting with 0
		 * @return text value for the column
		 */
		@Override
		public String getColumnText(final Object arg0, final int columnIndex) {
			DisplayPriceDTO dto = (DisplayPriceDTO) arg0;
			switch (columnIndex) {
			case INDEX_IMAGE:
				return StringUtils.EMPTY;
			case INDEX_NAME:
				return dto.getPriceListName();
			case INDEX_LISTPRICE:
				if (dto.getListPrice() == null) {
					return StringUtils.EMPTY;
				}
				return String.valueOf(dto.getListPrice());
			case INDEX_QTY:
				return String.valueOf(dto.getQuantity());
			case INDEX_SALEPRICE:
				return String.valueOf(dto.getLowestPrice().getAmount());				
			default:
				return StringUtils.EMPTY;
			}					
		}

		/**
		 * Returns column image.
		 * 
		 * @param arg0 - argument
		 * @param arg1 - argument
		 * @return image for the column
		 */
		@Override
		public Image getColumnImage(final Object arg0, final int arg1) {
			return null;
		}		
		
	}		

	/**
	 * @param viewer the column viewer
	 * @param shipment - shipment
	 * @param updatePriceAction - the action that updates the prices in the model after it's changed in the editor.
	 * @param bindingContext data binding context
	 */
	public InlinePriceEditingSupport(final IEpTableViewer viewer, final OrderShipment shipment, final Action updatePriceAction,
			final DataBindingContext bindingContext) {
		this(viewer, shipment, bindingContext);
		this.updatePriceAction = updatePriceAction;
	}
	/**
	 * @param viewer the column viewer
	 * @param shipment - shipment
	 * @param bindingContext data binding context
	 */
	public InlinePriceEditingSupport(final IEpTableViewer viewer, final OrderShipment shipment, final DataBindingContext bindingContext) {
		super(viewer.getSwtTableViewer(), bindingContext);
		orderCurrency = shipment.getOrder().getCurrency();
		this.viewer = viewer;

		List<Pair<String, Integer>> columns = new ArrayList<>();
		columns.add(new Pair<>(StringUtils.EMPTY, INDEX_IMAGE_SIZE));
		columns.add(new Pair<>(FulfillmentMessages.get().priceListName, INDEX_NAME_SIZE));
		columns.add(new Pair<>(FulfillmentMessages.get().listPrice, INDEX_LISTPRICE_SIZE));
		columns.add(new Pair<>(FulfillmentMessages.get().quantity, INDEX_QTY_SIZE));
		columns.add(new Pair<>(FulfillmentMessages.get().salePrice, INDEX_SALEPRICE_SIZE));
		
		final TableValuesProvider tableValuesProvider = cellElement -> getTableCellValue(shipment, cellElement);

		IStructuredContentProvider contentProvider = new IStructuredContentProvider() {
			@Override
			public Object[] getElements(final Object inputElement) {
				if (inputElement instanceof Object[]) {
					LOG.debug("TableViewer input set to array of Objects"); //$NON-NLS-1$
					return (Object[]) inputElement;
				}
				return new Object[0];
			}
			@Override
			public void dispose() { //NOPMD
			}
			@Override
			public void inputChanged(final Viewer arg0, final Object arg1, final Object arg2) { //NOPMD
			}
		};

		ITableLabelProvider labelProvider = new PriceLabelProvider();

		cellEditor = new AdvancedTableCellEditor(
				(Composite) viewer.getSwtTableViewer().getControl(),
				contentProvider,
				labelProvider,
				columns,
				tableValuesProvider,
				null
				/* new int [] {2, 4} */					
		);
		
		cellEditor.setMaxRows(ROWS_LIMIT);
	}

	private Object[] getTableCellValue(final OrderShipment shipment, final Object cellElement) {
		try {
			ProductSku productSku;
			int quantity;
			if (cellElement instanceof String) {
				IStructuredSelection selection =
					(IStructuredSelection) InlinePriceEditingSupport.this.viewer.getSwtTableViewer().getSelection();
				ShoppingItem orderSku = (ShoppingItem) selection.getFirstElement();
				quantity = orderSku.getQuantity();
				productSku = getProductSkuLookup().findByGuid(orderSku.getSkuGuid());
			} else if (cellElement instanceof AbstractCellEditorValueWrapper) {
				final ShoppingItem shoppingItem = ((AbstractCellEditorValueWrapper) cellElement).getWrappedValue();
				quantity = shoppingItem.getQuantity();
				productSku = getProductSkuLookup().findByGuid(shoppingItem.getSkuGuid());
			} else {
				return new Object[0];
			}

			String catalogCode = shipment.getOrder().getStore().getCatalog().getCode();

			PriceListStack priceListStack = priceListLookupService.getPriceListStack(catalogCode, orderCurrency, null);

			priceListStack = checkPriceListStackAuthorization(priceListStack);

			List<DisplayPriceDTO> applicablePricesForCurrentOrderSku = priceLookupFacade.getPricesForOrderSku(productSku,
					priceListStack, quantity, shipment.getOrder().getStore());

			LOG.info("Found " + applicablePricesForCurrentOrderSku.size() + " prices"); //$NON-NLS-1$ //$NON-NLS-2$

			return applicablePricesForCurrentOrderSku.toArray(
					new DisplayPriceDTO[applicablePricesForCurrentOrderSku.size()]);

		} catch (ClassCastException cce) {
			LOG.error("Selected element is not an AbstractCellEditorValueWrapper with ShoppingItem in it!!!!"); //$NON-NLS-1$
		}
		return new Object[0];
	}

	private PriceListStack checkPriceListStackAuthorization(final PriceListStack priceListStack) {
		List<String> authorizedPriceLists = new ArrayList<>();
		for (String priceListGuid : priceListStack.getPriceListStack()) {
			if (AuthorizationService.getInstance().isAuthorizedForPriceList(priceListGuid)) {
				authorizedPriceLists.add(priceListGuid);
			}
		}
		priceListStack.setStack(authorizedPriceLists);
		return priceListStack;
	}
	
	
	private String getPriceStringOrDefault(final BigDecimal price, final String defaultString) {
		if (price != null) {
			return price.toString();
		}
		
		return defaultString;
	}

	@Override
	protected void initializeCellEditorValue(final CellEditor cellEditor, final ViewerCell cell) {
		cellEditor.setValue(new AbstractCellEditorValueWrapper(cell.getElement()) {
			@Override
			public String getDisplayValue() {
				Object object = getWrappedValue();
				BigDecimal invoicePrice;
				if (object instanceof OrderSku) {
					invoicePrice = ((OrderSku) object).getUnitPrice();
				} else {
					invoicePrice = ((ExchangeItem) getWrappedValue()).getPriceCalc().forUnitPrice().getAmount();
				}
				return getPriceStringOrDefault(invoicePrice, ""); //$NON-NLS-1$
			}
		});		
				
		final EpControlBindingProvider bindingProvider = EpControlBindingProvider.getInstance();
		final EpBindingConfiguration bindingConfig = new EpBindingConfiguration(getBindingContext(), 
				((AdvancedTableCellEditor) cellEditor).getTextControl());
		bindingConfig.configureUiToModelBinding(null, EpValidatorFactory.NON_NEGATIVE_BIG_DECIMAL, 
				new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue,
					final Object value) {
				setValue(cell.getElement(), value);
				return Status.OK_STATUS;
			}
		}, true);
		bindingConfig.setErrorLocation(ValidationErrorLocation.LEFT);
		setBinding(bindingProvider.bind(bindingConfig));
		getViewer().getColumnViewerEditor().addEditorActivationListener(getActivationListener());
		
	}
	
	@Override
	protected boolean canEdit(final Object arg0) {
		return true;
	}
	@Override
	protected CellEditor getCellEditor(final Object arg0) {
		return cellEditor;
	}
	@Override
	protected Object getValue(final Object arg0) {
		return null;
	}

	private void updatePrice(final OrderSku orderSku, final BigDecimal listPrice, final BigDecimal salePrice, final BigDecimal unitPrice) {
		PriceTier tier = ServiceLocator.getService(ContextIdNames.PRICE_TIER);
		Price price =  ServiceLocator.getService(ContextIdNames.PRICE);
		if (orderSku.getCurrency() == null) {
			price.setCurrency(orderCurrency);
		} else {
			price.setCurrency(orderSku.getCurrency());
		}
		
		tier.setListPrice(listPrice);
		tier.setSalePrice(salePrice);
		tier.setComputedPriceIfLower(unitPrice);
		tier.setMinQty(0);
		price.addOrUpdatePriceTier(tier);
		orderSku.setPrice(orderSku.getQuantity(), price);
		BigDecimal lowestUnitPrice = getPricingSnapshotService().getPricingSnapshotForOrderSku(orderSku).getPriceCalc().forUnitPrice().getAmount();
		if (unitPrice == null && lowestUnitPrice != null) {
			orderSku.setUnitPrice(lowestUnitPrice);
		} else {
			orderSku.setUnitPrice(unitPrice);
		}
		if (updatePriceAction != null) {
			updatePriceAction.run();
		}
		if (orderSku instanceof RecalculableObject) {		
			((RecalculableObject) orderSku).enableRecalculation();
		}
		viewer.getSwtTableViewer().update(orderSku, null);
	}
	
	@Override
	protected void setValue(final Object element, final Object selectionResult) {
		
		if (!(element instanceof OrderSku)) {
			return;
		}

		OrderSku orderSku = (OrderSku) element;

		if (selectionResult instanceof String) {
			final ShoppingItemPricingSnapshot pricingSnapshot = getPricingSnapshotService().getPricingSnapshotForOrderSku(orderSku);

			BigDecimal unitPrice = null;
			if (StringUtils.isNotEmpty((String) selectionResult)) {
				unitPrice = new BigDecimal((String) selectionResult).setScale(2);
			}
			BigDecimal listPrice = pricingSnapshot.getListUnitPrice().getAmount();
			BigDecimal salePrice = null;
			if (orderSku.isPersisted() && pricingSnapshot.getSaleUnitPrice() != null) {
				salePrice = pricingSnapshot.getSaleUnitPrice().getAmount();
			}
			this.updatePrice(orderSku, listPrice, salePrice, unitPrice);
		} else if (selectionResult instanceof DisplayPriceDTO) {
			final DisplayPriceDTO dto = (DisplayPriceDTO) selectionResult;
			this.updatePrice(orderSku, dto.getListPrice(), dto.getSalePrice(), dto.getLowestPrice().getAmount());
		}
	}
	
	@Override
	protected void saveCellEditorValue(final CellEditor cellEditor, final ViewerCell cell) {
		Object value = cellEditor.getValue();
		setValue(cell.getElement(), value);
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