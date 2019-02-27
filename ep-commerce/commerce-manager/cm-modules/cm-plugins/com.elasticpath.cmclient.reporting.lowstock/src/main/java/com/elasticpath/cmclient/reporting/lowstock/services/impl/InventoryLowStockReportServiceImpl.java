/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.reporting.lowstock.services.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.elasticpath.cmclient.core.LoginManager;
import com.elasticpath.cmclient.reporting.ReportType;
import com.elasticpath.cmclient.reporting.lowstock.InventoryLowStockReportMessages;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.inventory.InventoryDto;
import com.elasticpath.service.catalog.ProductInventoryManagementService;
import com.elasticpath.service.reporting.ReportService;

/**
 *
 */
public class InventoryLowStockReportServiceImpl {

	private static final int THREE = 3;

	private static final int SEVEN = 7;

	private static final String SKU_CODE_LIST = "skuCodeList"; //$NON-NLS-1$

	private static final String SKU_CODE = "skuCode"; //$NON-NLS-1$

	private static final String WAREHOUSEUID = "warehouseuid"; //$NON-NLS-1$

	private final ReportService reportService = 
		LoginManager.getInstance().getBean(ContextIdNames.REPORT_SERVICE);
	
	private final ProductInventoryManagementService productInventoryManagementService = 
		LoginManager.getInstance().getBean(ContextIdNames.PRODUCT_INVENTORY_MANAGEMENT_SERVICE);
	
	/** Criteria for low stock report. */
	private static final String LOW_STOCK_BASE = 
			" select psi.skuCodeInternal, p.code, b.code, psi.uidPk from ProductSkuImpl psi "  //$NON-NLS-1$
			+ " inner join psi.productInternal as p inner join p.brand as b"  //$NON-NLS-1$
			+ " where psi.skuCodeInternal IN (:skuCodeList)"; //$NON-NLS-1$
	private static final String LOW_STOCK_PRODUCT_NAME = 
		"select psi.skuCodeInternal, lm.displayName from ProductSkuImpl psi "  //$NON-NLS-1$
		+ " inner join psi.productInternal as p left outer join p.localeDependantFieldsMap as lm"  //$NON-NLS-1$
		+ " where psi.skuCodeInternal IN (:skuCodeList) and lm.locale='";  //$NON-NLS-1$
	
	private static final String LOW_STOCK_BRAND_NAME = 
		"select psi.skuCodeInternal, bp.value from ProductSkuImpl psi "  //$NON-NLS-1$
		+ " inner join psi.productInternal as p inner join p.brand as b left outer join b.localizedPropertiesMap as bp"  //$NON-NLS-1$
		+ " where psi.skuCodeInternal IN (:skuCodeList) and bp.localizedPropertyKey='brandDisplayName_";  //$NON-NLS-1$
	
	private List<Object> paramList;

	
	private String skuCode;
	
	private long warehouseUid;
	
	private String brand;
	
	private Locale locale;
	
	private static final Logger LOG = Logger.getLogger(InventoryLowStockReportServiceImpl.class);
	
	private void retrieveReportParams() {
		Map<String, Object> params = null;
		
		for (ReportType reportType : ReportTypeManager.getInstance().getReportTypes()) {
			if (reportType.getName().equalsIgnoreCase(InventoryLowStockReportMessages.report)) {
				params = reportType.getReport().getParameters();
			}
		}
		
		if (params == null) {
			return;
		}
		
		for (Entry<String, Object> entry : params.entrySet()) {
			String key = entry.getKey();
			Object param = entry.getValue();

			if (key.equalsIgnoreCase(SKU_CODE)) {
				skuCode = (String) param;
			} else if (key.equalsIgnoreCase(WAREHOUSEUID)) {
				warehouseUid = (Long) param;
			} else if ("brand".equalsIgnoreCase(key)) { //$NON-NLS-1$
				if (!param.toString().equalsIgnoreCase("all brands")) { //NOPMD //$NON-NLS-1$
					brand = (String) param;					
				}
			} else if ("locale".equalsIgnoreCase(key)) { //$NON-NLS-1$
					locale = (Locale) param;					
			}
		}
		
	}
	
	private String getLowStockQuery(final String baseQuery) {
		StringBuffer query = new StringBuffer(baseQuery);
		
		query.append(locale.getLanguage()).append("' "); //$NON-NLS-1$
		

		addBrandParamToQuery(query);
		
		LOG.error(query.toString());
		return query.toString();
	}

	private void addBrandParamToQuery(final StringBuffer query) {
		paramList = new ArrayList<Object>();
		if (StringUtils.isNotBlank(brand)) { 
			query.append(" and psi.productInternal.brand.code = ?1"); //$NON-NLS-1$
			paramList.add(brand);
		}
	}
	
	/**
	 * List sku details for low stock items.
	 * 
	 * @return list of low stock items sorted (descending) by restock date.
	 */
	public List<Object[]> lowStockReport() {
		
		retrieveReportParams();
		
		Set<String> skuCodes = new HashSet<String>();
		
		if (StringUtils.isNotBlank(skuCode)) {
			skuCodes.add(skuCode);
		}

		List<InventoryDto> lowStockInventories = productInventoryManagementService.findLowStockInventories(skuCodes, warehouseUid);
		
		final Map<String, InventoryDto> inventoryMap = convert(lowStockInventories);
		
		final Set<String> skuCodesForLowStockInventories = inventoryMap.keySet();
		final StringBuffer query = new StringBuffer(LOW_STOCK_BASE);
		addBrandParamToQuery(query);
		
		final List<Object[]> skus = reportService.executeWithList(query.toString(), SKU_CODE_LIST, 
															new ArrayList<String>(skuCodesForLowStockInventories), paramList.toArray());
		
		final Map<String, String> productNameForSkuCode =  getNameMap(skuCodesForLowStockInventories, LOW_STOCK_PRODUCT_NAME);
		final Map<String, String> brandNameForSkuCode =  getNameMap(skuCodesForLowStockInventories, LOW_STOCK_BRAND_NAME);
		
		final int qtyOnHandIndex = THREE;
		final int availableQtyIndex = 4;
		final int allocatedQtyIndex = 5;
		final int reservedQtyIndex = 6;
		final int reorderMinimumIndex = SEVEN;
		final int reorderQtyIndex = 8;
		final int restockDateIndex = 9;
		
		List<Object[]> result = new ArrayList<Object[]>(skus.size());
		
		for (Object[] sku : skus) {
			Object[] skuWithInventory = new Object[sku.length + SEVEN];
			skuWithInventory[0] = sku[2];
			if (brandNameForSkuCode.containsKey(sku[0])) {
					skuWithInventory[0] = brandNameForSkuCode.get(sku[0]); 
			} 
			skuWithInventory[1] = sku[0];
			skuWithInventory[2] = sku[1];
			
			if (productNameForSkuCode.containsKey(sku[0])) {
				skuWithInventory[2] = productNameForSkuCode.get(sku[0]);
			}
			InventoryDto inventory = inventoryMap.get(sku[0]);
			skuWithInventory[qtyOnHandIndex] = inventory.getQuantityOnHand();
			skuWithInventory[allocatedQtyIndex] = inventory.getAllocatedQuantity();
			skuWithInventory[reservedQtyIndex] = inventory.getReservedQuantity(); 
			skuWithInventory[availableQtyIndex] = inventory.getAvailableQuantityInStock();
			
			
			skuWithInventory[reorderMinimumIndex] = inventory.getReorderMinimum();
			skuWithInventory[reorderQtyIndex] = inventory.getReorderQuantity();
			skuWithInventory[restockDateIndex] = inventory.getRestockDate();
			
			skuWithInventory[skuWithInventory.length - 1] = sku[THREE];
			result.add(skuWithInventory);
		}
	
		return result;

	}

	private Map<String, String> getNameMap(final Set<String> skuCodesForLowStockInventories, final String query) {
		final List<Object[]> dbSelectValues = reportService.executeWithList(getLowStockQuery(query), SKU_CODE_LIST, 
				new ArrayList<String>(skuCodesForLowStockInventories), paramList.toArray()); 
		final Map<String, String> productName =  new LinkedHashMap<String, String>();
		
		for (Object[] value : dbSelectValues) {
			productName.put((String) value[0], (String) value[1]);
		}
		return productName;
	}

	private Map<String, InventoryDto> convert(
			final List<InventoryDto> lowStockInventories) {
		Map<String, InventoryDto> invMap = new HashMap<String, InventoryDto>();
		for (InventoryDto dto : lowStockInventories) {
			invMap.put(dto.getSkuCode(), dto);
		}
		return invMap;
	}

}
