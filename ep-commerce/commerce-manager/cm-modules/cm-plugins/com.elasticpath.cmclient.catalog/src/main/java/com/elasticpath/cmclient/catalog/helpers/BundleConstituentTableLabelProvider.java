/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.catalog.helpers;

import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.elasticpath.cmclient.catalog.CatalogImageRegistry;
import com.elasticpath.cmclient.core.CorePlugin;
import com.elasticpath.domain.catalog.BundleConstituent;
import com.elasticpath.domain.catalog.ConstituentItem;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;

/**
 * Provides label for BundleConstituent table viewer.
 */
public class BundleConstituentTableLabelProvider extends LabelProvider implements ITableLabelProvider {

	private static final int COLUMN_INDEX_ICON = 0;
	private static final int COLUMN_INDEX_PRODUCT_TYPE = 1;
	private static final int COLUMN_INDEX_PRODUCT_CODE = 2;
	private static final int COLUMN_INDEX_PRODUCT_NAME = 3;
	private static final int COLUMN_SKU_CODE = 4;
	private static final int COLUMN_SKU_CONFIGURATION = 5;
	private static final int COLUMN_INDEX_QTY = 6;

	/**
	 * Get the column image.
	 *
	 * @param element not used
	 * @param columnIndex the column to create an image for
	 * @return the image
	 */
	@Override
	public final Image getColumnImage(final Object element, final int columnIndex) {
		if (columnIndex == COLUMN_INDEX_ICON) {
			final ConstituentItem item = ((BundleConstituent) element).getConstituent();
			if (item.isProductSku()) {
				return CatalogImageRegistry.getSmallImageForProductSku(item.getProductSku());
			}
			return CatalogImageRegistry.getSmallImageForProduct(item.getProduct());
		}

		return null;
	}

	/**
	 * Get the column text from selected row.
	 *
	 * @param element the data input element
	 * @param columnIndex the column index
	 * @return text
	 */
	@Override
	public final String getColumnText(final Object element, final int columnIndex) {
		final BundleConstituent constituent = (BundleConstituent) element;

		final ConstituentItem item = constituent.getConstituent();

		switch (columnIndex) {
		case COLUMN_INDEX_ICON:
			return StringUtils.EMPTY;
		case COLUMN_INDEX_PRODUCT_CODE:
			String productCode = StringUtils.EMPTY;
			if (item.isProductSku() && item.getProductSku().getProduct() != null) {
				productCode = item.getProductSku().getProduct().getCode();
			}  else if (item.isProduct()) {
				productCode = item.getCode();
			}
			return productCode;
		case COLUMN_SKU_CODE:
			if (item.isProductSku()) {
				return item.getProductSku().getSkuCode();
			}
			return StringUtils.EMPTY;
		case COLUMN_SKU_CONFIGURATION:
			if (item.isProductSku()) {
				return formatSkuConfiguration(item.getProductSku());
			}
			return  StringUtils.EMPTY;
		case COLUMN_INDEX_PRODUCT_NAME:
			String productName = StringUtils.EMPTY;
			if (item.isProductSku() && item.getProductSku().getProduct() != null) {
				Locale locale = getSelectedLocale(item.getProductSku().getProduct());
				productName = item.getProductSku().getProduct().getDisplayName(locale);
			} else if (item.isProduct()) {
				productName = item.getDisplayName(getSelectedLocale(item.getProduct()));
			}
			return productName;
		case COLUMN_INDEX_PRODUCT_TYPE:
			return item.getProduct().getProductType().getName();
		case COLUMN_INDEX_QTY:
			return constituent.getQuantity().toString();
		default:
			return StringUtils.EMPTY;
		}
	}

	private String formatSkuConfiguration(final ProductSku productSku) {
		if (productSku == null || productSku.getOptionValues() == null) {
			return StringUtils.EMPTY;
		}
		boolean hasComma = false;
		StringBuilder builder = new StringBuilder();
		for (SkuOptionValue value : productSku.getOptionValues()) {
			if (hasComma) {
				builder.append(", "); //$NON-NLS-1$
			}
			builder.append(value.getDisplayName(CorePlugin.getDefault().getDefaultLocale(), true));
			hasComma = true;
		}
		return builder.toString();
	}

	/**
	 * Finds Selected {@link Locale}, this can be overridden to change the locale behaviour.
	 *
	 * @param product selected product.
	 * @return selected locale (by default is master catalog's default locale)
	 */
	public Locale getSelectedLocale(final Product product) {
		return product.getMasterCatalog().getDefaultLocale();
	}
}