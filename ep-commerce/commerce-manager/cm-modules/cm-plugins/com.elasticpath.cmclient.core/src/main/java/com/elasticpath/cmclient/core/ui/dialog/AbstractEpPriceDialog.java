/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.ui.dialog;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Shell;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.common.dto.pricing.BaseAmountDTO;
import com.elasticpath.common.dto.pricing.PriceListDescriptorDTO;
import com.elasticpath.common.pricing.service.BaseAmountFilter;
import com.elasticpath.common.pricing.service.PriceListHelperService;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.persistence.api.Entity;

/**
 * This abstract class for EP dialogs with prices pages. 
 * Used as base for ProductFinderDialog & SkuFinderDialog.
 * 
 * */
@SuppressWarnings({ "PMD.GodClass" })
public abstract class AbstractEpPriceDialog extends AbstractCatalogObjectFinderDialog {
	
	private static final String PRICE_MANAGEMENT_MANAGE_PRODUCT_PRICING = "PRICE_MANAGEMENT_MANAGE_PRODUCT_PRICING"; //$NON-NLS-1$
	
	/** Base amount product discriminitor. */
	protected static final String PRODUCT_TYPE = "PRODUCT"; //$NON-NLS-1$
	
	/** Base amount product sku discriminitor. */
	protected static final String PRODUCT_SKU_TYPE = "SKU"; //$NON-NLS-1$

	private static final String PRICE_TABLE = "Price Table"; //$NON-NLS-1$

	private static final int TABLE_HEIGHT_MINIMUM = 150;

	private static final int TABLE_HEIGHT_MAXIMUM = 225;

	/** Show ot not price section. */
	private final boolean showPriceListSection;
	
	private BaseAmountSummaryDTO selectedItemPriceSummary;
	
	private PriceListHelperService priceListHelperService;
	
	/**
	 * Logger.
	 */
	protected  static final Logger LOG = Logger.getLogger(AbstractEpPriceDialog.class);
	
	/**
	 * Get the price list helper service.
	 * @return price list helper service
	 */
	protected PriceListHelperService getPriceListHelperService() {
		if (priceListHelperService != null) {
			return priceListHelperService;
		}
		return ServiceLocator.getService(ContextIdNames.PRICE_LIST_HELPER_SERVICE);
	}

	/**
	 * Sets the priceListHelperService.
	 * @param priceListHelperService instance to set
	 */
	void setPriceListHelperService(final PriceListHelperService priceListHelperService) {
		this.priceListHelperService = priceListHelperService;
	}
	
	/**
	 * Price list table viewer.
	 */
	private IEpTableViewer priceTableViewer;
	
	/**
	 * Constructor.
	 * @param parentShell the parent shell of this dialog
	 * @param showPrices show or not price section in derived dialog.
	 */
	public AbstractEpPriceDialog(final Shell parentShell, final boolean showPrices) {
		super(parentShell);
		this.showPriceListSection = showPrices;
	}

	/**
	 * Returns the desired height values of table results.
	 * <p>
	 * The values below will directly affect the ProductFinderDialog and SkuFinderDialog with
	 * prices enabled and prices disabled.
	 * <p>
	 * Tests should be done is varying resolutions with the minimum set at 1366x768.
	 */
	@Override
	protected int getResultTableHeight() {
		if (showPriceListSection) {
			return TABLE_HEIGHT_MINIMUM;
		} else {
			return TABLE_HEIGHT_MAXIMUM;
		}
	}

	/**
	 * Show or not proce section.
	 * @return true if need to show price section in dialog.
	 */
	public boolean isShowPriceListSection() {
		return showPriceListSection;
	}
	
	/**
	 * Clear price information from table.
	 */
	protected void clearPriceInfo() {
		if (isShowPriceListSection()) {
			priceTableViewer.setInput(null);			
		}		
	}


	/**
	 * Show prices for selected object.
	 * @param selectedItem selected product or sku
	 */
	protected void showPriceInfo(final Object selectedItem) {
		if (isShowPriceListSection()) {
			priceTableViewer.setInput(getPricesToShow(selectedItem).toArray());
		}
	}

	/**
	 * Gets prices to populate the pricing tab view.
	 * 
	 * @param selectedItem product or sku to get prices for
	 * @return List of BaseAmountSummaryDTO 
	 */
	protected List<BaseAmountSummaryDTO> getPricesToShow(final Object selectedItem) {
		BaseAmountFilter filter = ServiceLocator.getService(ContextIdNames.BASE_AMOUNT_FILTER);
		populateFilter(selectedItem, filter);
		
		Map<PriceListDescriptorDTO, List<BaseAmountDTO>> itemPricesMap = getItemPricesMap(filter);
				
		itemPricesMap = resolveForSingleSkuPricingOverrides(selectedItem, filter, itemPricesMap);
		itemPricesMap = fallbackToProductPriceIfSkuPriceDoesNotExist(selectedItem, filter, itemPricesMap);
		
		return adapt(itemPricesMap);
	}

	/**
	 * If the current currency code is set, the result is filtered by this currency code.
	 * 
	 * @param selectedItem sku to get fallback price for
	 * @param filter base amount filter
	 * @param itemPricesMap map with results
	 * @return filtered map with results
	 */
	Map<PriceListDescriptorDTO, List<BaseAmountDTO>> fallbackToProductPriceIfSkuPriceDoesNotExist(final Object selectedItem,
			final BaseAmountFilter filter, final Map<PriceListDescriptorDTO, List<BaseAmountDTO>> itemPricesMap) {
		if (itemPricesMap.isEmpty() && selectedItem instanceof ProductSku) { 
			//happens only in case of if a sku that belongs to the multisku product is selected and it has no price. In this case the product price 
			//is queried and used
			Product product = ((ProductSku) selectedItem).getProduct();
			populateFilter(product, filter);
			return getItemPricesMap(filter);
		}
		return itemPricesMap;
	}

	/**
	 * Resolves if a single sku product has an overridden price on the sku level.
	 * 
	 * @param selectedItem single sku product
	 * @param filter base amount filter
	 * @param itemPricesMap map with results
	 * @return product pricing information if there's no overrides, sku pricing information in other case
	 */
	Map<PriceListDescriptorDTO, List<BaseAmountDTO>> resolveForSingleSkuPricingOverrides(final Object selectedItem, final BaseAmountFilter filter,
			final Map<PriceListDescriptorDTO, List<BaseAmountDTO>> itemPricesMap) {
		// If the selected item is a single sku product we need to check if it has overridden prices on the sku level and use these prices if it has.
		if (selectedItem instanceof Product && !((Product) selectedItem).hasMultipleSkus()) {
			Map<PriceListDescriptorDTO, List<BaseAmountDTO>> overriddenPricesMap = 
				getSingleSkuProductPricesOverrides(((Product) selectedItem).getDefaultSku(), filter);
			if (!overriddenPricesMap.isEmpty()) {
				return overriddenPricesMap;
			}
		}
		return itemPricesMap;
	}
	
	private Map<PriceListDescriptorDTO, List<BaseAmountDTO>> getSingleSkuProductPricesOverrides(final ProductSku sku, 
			final BaseAmountFilter filter) {		
		populateFilter(sku, filter);
		return getItemPricesMap(filter);
	}

	private void populateFilter(final Object selectedItem, final BaseAmountFilter filter) {
		filter.setObjectGuid(getBaseAmountObjectGuid(selectedItem));
		filter.setObjectType(getBaseAmountObjectType(selectedItem));
	}

	private Map<PriceListDescriptorDTO, List<BaseAmountDTO>> getItemPricesMap(final BaseAmountFilter filter) {
		return getPriceListHelperService().getPriceListMap(filter, getCurrentCurrencyCode());
	}

	/**
	 * Adapt given map of price lists and base amounts to <code>List</code> of {@link BaseAmountSummaryDTO}.
	 * Apply security filter to given map and create sorted by price lists by currency / price list name  
	 * <code>List</code> of of {@link BaseAmountSummaryDTO}.
	 * @param itemPricesMap given collection of price lists.
	 * @return list for display it in product / sku picker dialogs.
	 */	
	private List<BaseAmountSummaryDTO> adapt(
			final Map<PriceListDescriptorDTO, List<BaseAmountDTO>> itemPricesMap) {		
		final List<BaseAmountSummaryDTO> result = new ArrayList<BaseAmountSummaryDTO>();		
		for (PriceListDescriptorDTO priceListDescriptorDTO : itemPricesMap.keySet()) {
			if (isAccessable(priceListDescriptorDTO.getGuid())) {
				addToResult(itemPricesMap, result, priceListDescriptorDTO);
			}
		}
		Collections.sort(result, new BaseAmountDTOComparator());
		return result;
	}

	private void addToResult(final Map<PriceListDescriptorDTO, List<BaseAmountDTO>> itemPricesMap, final List<BaseAmountSummaryDTO> result,
			final PriceListDescriptorDTO priceListDescriptorDTO) {
		for (BaseAmountDTO baseAmountDTO : itemPricesMap.get(priceListDescriptorDTO)) {
			result.add(new BaseAmountSummaryDTO(baseAmountDTO, priceListDescriptorDTO)); 
		}
	}

	/**
	 * @return the currency code that is used for price selection
	 */
	protected String getCurrentCurrencyCode() {
		return null;
	}
	
	/**
	 * Whether is given price list accessible by current cmuser.  
	 * @param priceListGuid price list guid
	 * @return true is user has access to price list.
	 */
	protected boolean isAccessable(final String priceListGuid) {
		return AuthorizationService.getInstance().isAuthorizedWithPermission(PRICE_MANAGEMENT_MANAGE_PRODUCT_PRICING)
		&& AuthorizationService.getInstance().isAuthorizedForPriceList(priceListGuid);
	}
	
	
	/**
	 * Create price table viewer.
	 * @param resultsComposite the results composite.
	 */
	protected void createPriceTableViewer(
			final IEpLayoutComposite resultsComposite) {
		
		if (isShowPriceListSection()) {
			final int columnWidthPriceListName = 100;
			final int columnWidthPriceListCurrencyCode = 120;
			final int columnWidthQuantity = 80;
			final int columnWidthPrice = 90;
			final int tableViewerHeight = 200;

			IEpLayoutData priceTableLayoutData = resultsComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true);
			((GridData) priceTableLayoutData.getSwtLayoutData()).heightHint = tableViewerHeight;
			((GridData) priceTableLayoutData.getSwtLayoutData()).minimumHeight = tableViewerHeight;

			priceTableViewer = resultsComposite.addTableViewer(false, EpState.READ_ONLY, priceTableLayoutData, PRICE_TABLE);

			priceTableViewer.addTableColumn(CoreMessages.get().ProductFinderDialog_PriceListName, columnWidthPriceListName);
			priceTableViewer.addTableColumn(CoreMessages.get().ProductFinderDialog_CurrencyCode, columnWidthPriceListCurrencyCode);
			priceTableViewer.addTableColumn(CoreMessages.get().ProductFinderDialog_Quantity, columnWidthQuantity);
			priceTableViewer.addTableColumn(CoreMessages.get().ProductFinderDialog_ListPrice, columnWidthPrice);
			priceTableViewer.addTableColumn(CoreMessages.get().ProductFinderDialog_SalePrice, columnWidthPrice);
			priceTableViewer.setContentProvider(
					new PriceListViewContentProvider()
					);
			priceTableViewer.setLabelProvider(
					new PriceListViewLabelProvider()
					);
			
			priceTableViewer.getSwtTableViewer().addSelectionChangedListener(new ISelectionChangedListener() {
				public void selectionChanged(final SelectionChangedEvent event) {
					selectedItemPriceSummary = (BaseAmountSummaryDTO) ((StructuredSelection) event.getSelection()).getFirstElement();	
					getOkButton().setEnabled(isComplete());	
				}
			});
			
		}		
	}	
	
	/**
	 * @return the selected price information
	 */
	public BaseAmountSummaryDTO getSelectedItemPriceSummary() {
		return selectedItemPriceSummary;
	}
	
	/**
	 * Get the particular object type (discriminator), for retrieve price - SKU or PRODUCT.
	 * @param selectedItem selected sku or product for 
	 * @return object type
	 */
	protected abstract String getBaseAmountObjectType(final Object selectedItem);
	
	/**
	 * Get the object guid for retrieve price.
	 * @param selectedItem selected sku or product for 
	 * @return guid of selected object.
	 */
	protected String getBaseAmountObjectGuid(final Object selectedItem) {
		return ((Entity) selectedItem).getGuid();	
	}
	
	
	/**
	 * 
	 * Label Provider for price list descriptor and base amount values.
	 *
	 */
	class PriceListViewLabelProvider extends LabelProvider implements ITableLabelProvider {

		private static final int COLUMN_INDEX_PRICE_LIST_NAME = 0;
		private static final int COLUMN_INDEX_PRICE_LIST_CURRENCY_CODE = 1;
		private static final int COLUMN_INDEX_PRICE_LIST_QUANTITY = 2;
		private static final int COLUMN_INDEX_PRICE_LIST_PRICE = 3;
		private static final int COLUMN_INDEX_PRICE_LIST_SALE_PRICE = 4;
		
		
		/**
		 * Over ride method. Get the column image.
		 * 
		 * @param element the element object to be displayed.
		 * @param columnIndex the column index.
		 * @return the column image object.
		 */
		public Image getColumnImage(final Object element, final int columnIndex) {
			return null;
		}
		
		/**
		 * Get column text.
		 * 
		 * @param element the product element object to be displayed by the table.
		 * @param columnIndex the column index.
		 * @return the column text content string.
		 */
		public String getColumnText(final Object element, final int columnIndex) {
			final BaseAmountSummaryDTO dto = (BaseAmountSummaryDTO) element;
			switch (columnIndex) {
			case COLUMN_INDEX_PRICE_LIST_NAME: return dto.getPriceListDescriptorName();
			case COLUMN_INDEX_PRICE_LIST_CURRENCY_CODE: return dto.getPriceListCurrencyCode();
			case COLUMN_INDEX_PRICE_LIST_QUANTITY: 
				return dto.getQuantity().toString();
			case COLUMN_INDEX_PRICE_LIST_PRICE: 
				return getPriceValue(dto.getListValue());
			case COLUMN_INDEX_PRICE_LIST_SALE_PRICE: 
				return getPriceValue(dto.getSaleValue());
			default: return StringUtils.EMPTY;
			}
		}
		
		private String getPriceValue(final BigDecimal listValue) {
			if (listValue == null) {
				return StringUtils.EMPTY;
			}
			return listValue.setScale(2, RoundingMode.HALF_EVEN).toString();
		}

	}
	
	/**
	 * The content provider class is responsible for providing objects to the view. It wraps existing objects in adapters or simply return objects
	 * as-is. These objects may be sensitive to the current input of the view, or ignore it and always show the same content.
	 */
	class PriceListViewContentProvider implements IStructuredContentProvider {		

		@Override
		public Object[] getElements(final Object inputElement) {
			if (inputElement instanceof Object[]) {
				LOG.debug("TableViewer input set to array of Objects"); //$NON-NLS-1$
				return (Object[]) inputElement;
			}
			return new Object[0];
		}
		
		@Override
		public void dispose() {
			// Do nothing
		}
		
		@Override
		public void inputChanged(final Viewer arg0, final Object arg1, final Object arg2) {
			// Do nothing
		}

	}
	
	/**
	 * Default comparator for sort product/sku price information.
	 */
	class BaseAmountDTOComparator implements Comparator<BaseAmountSummaryDTO> {
		
		@Override
		public int compare(final BaseAmountSummaryDTO dto1, final BaseAmountSummaryDTO dto2) {
			int result = dto1.getPriceListCurrencyCode().compareTo(dto2.getPriceListCurrencyCode());
			if (result == 0) {
				// the same currency, lets use PL name 
				result = dto1.getPriceListDescriptorName().compareTo(dto2.getPriceListDescriptorName());
			}
			return result;
		}
		
	}
	
	/**
	 * Extended base amount dto with price list and currency code. 
	 */
	class BaseAmountSummaryDTO extends BaseAmountDTO {
		
		private final String priceListCurrencyCode;		
		private final String priceListDescriptorName;

		/** 
		 * @return price list currency code.
		 */
		public String getPriceListCurrencyCode() {
			return priceListCurrencyCode;		
		}
		
		/**
		 * @return price list name.
		 */
		public String getPriceListDescriptorName() {
			return priceListDescriptorName;
		}
		
		/**
		 * Constructor.
		 * @param baseAmountDTO base amount 
		 * @param pldDTO price list descriptor
		 */
		BaseAmountSummaryDTO(final BaseAmountDTO baseAmountDTO, 
				final PriceListDescriptorDTO pldDTO) {
			setGuid(baseAmountDTO.getGuid());
			setObjectGuid(baseAmountDTO.getObjectGuid());
			setObjectType(baseAmountDTO.getObjectType());
			setQuantity(baseAmountDTO.getQuantity());
			setListValue(baseAmountDTO.getListValue());
			setSaleValue(baseAmountDTO.getSaleValue());
			setPriceListDescriptorGuid(baseAmountDTO.getPriceListDescriptorGuid());
			this.priceListDescriptorName = pldDTO.getName();
			this.priceListCurrencyCode = pldDTO.getCurrencyCode();
		}
		
	}	
	
	


}
