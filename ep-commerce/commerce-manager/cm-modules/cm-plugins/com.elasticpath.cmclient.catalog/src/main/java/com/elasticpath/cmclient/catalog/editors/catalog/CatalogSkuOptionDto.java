/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.catalog.editors.catalog;

import java.util.List;

import com.elasticpath.cmclient.core.editors.TableItems;
import com.elasticpath.domain.skuconfiguration.SkuOption;

/**
 * A DTO class which encapsulates skuOptionList & tableItems info from CatalogSkuOptionsSection,
 * which then gets retained in it's parent i.e. CatalogSkuOptionsPage. 
 *
 */
public class CatalogSkuOptionDto {
	
	private final List<SkuOption> skuOptionList;
	private final TableItems<SkuOption> tableItems;
	
	/**
	 * Default constructor.
	 * 
	 * @param skuOptionList the skuOptionList 
	 * @param tableItems the tableItems
	 */
	public CatalogSkuOptionDto(final List<SkuOption> skuOptionList, final TableItems<SkuOption> tableItems) {
		this.skuOptionList = skuOptionList;
		this.tableItems = tableItems;
	}
	
	/**
	 * Gets skuOptionList.
	 * 
	 * @return skuOptionList
	 */
	public List<SkuOption> getSkuOptionList() {
		return skuOptionList;
	}

	/**
	 * Gets tableItems.
	 * 
	 * @return tableItems 
	 */
	public TableItems<SkuOption> getTableItems() {
		return tableItems;
	}
}
