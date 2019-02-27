/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.catalog.views;

import java.util.Date;
import java.util.Locale;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.elasticpath.cmclient.catalog.CatalogImageRegistry;
import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.domain.catalog.ProductSku;

/**
 * This label provider returns the text that should appear in each column for a given <code>Product</code> object.
 * This also determines the icon that should appear in the first column.
 */
@SuppressWarnings({ "PMD.CyclomaticComplexity" })
public class SkuListViewLabelProvider extends LabelProvider implements ITableLabelProvider {
	
	private static final int COLUMN_INDEX_SKU_CODE = 1;

	private static final int COLUMN_INDEX_PRODUCT_NAME = 2;

	private static final int COLUMN_INDEX_SKU_CONFIG = 3;

	private static final int COLUMN_INDEX_BRAND = 4;

	private static final int COLUMN_INDEX_ACTIVE = 5;

	@Override
	public Image getColumnImage(final Object element, final int columnIndex) {
		if (columnIndex == 0) {
			return CatalogImageRegistry.getSmallImageForProductSku((ProductSku) element);
		}
		return null;
	}

	@Override
	public String getColumnText(final Object element, final int columnIndex) {
		ProductSku sku = (ProductSku) element;
		Locale locale = sku.getProduct().getProductType().getCatalog().getDefaultLocale();

		switch (columnIndex) {
		case 0:
			return ""; //$NON-NLS-1$
		case COLUMN_INDEX_SKU_CODE:
			return sku.getSkuCode();
		case COLUMN_INDEX_PRODUCT_NAME:
			return sku.getProduct().getDisplayName(locale);
		case COLUMN_INDEX_SKU_CONFIG:
			return sku.getDisplayName(locale);
		case COLUMN_INDEX_BRAND:
			if (sku.getProduct().getBrand() != null) {
				return sku.getProduct().getBrand().getDisplayName(locale, true);
			}
			return CatalogMessages.get().Product_NotAvailable;
		case COLUMN_INDEX_ACTIVE:
			//Rule 1: if single sku product.. then date depends on product not sku.
			//Rule 2: if multi sku product.. to be active both the sku and its product must be active - eg.. within their respective date ranges.
			if (sku.getProduct().isWithinDateRange(new Date())) {
				if (sku.getProduct().getProductType().isMultiSku()) {
					if (sku.isWithinDateRange()) {
						return CatalogMessages.get().SkuListView_Active_Yes;
					}
				} else {
					return CatalogMessages.get().SkuListView_Active_Yes;
				}
			}
			return CatalogMessages.get().SkuListView_Active_No;
		default:
			return CatalogMessages.get().Product_NotAvailable;
		}
	}
}
