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
import com.elasticpath.cmclient.core.dto.catalog.ProductModel;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.Product;

/**
 * This label provider returns the text that should appear in each column for a given <code>Product</code> object.
 * This also determines the icon that should appear in the first column.
 */
@SuppressWarnings({ "PMD.CyclomaticComplexity" })
public class ProductListViewLabelProvider extends LabelProvider implements ITableLabelProvider {

	private static final int COLUMN_3 = 3;

	private static final int COLUMN_4 = 4;

	private static final int COLUMN_5 = 5;

	private static final int COLUMN_6 = 6;

	private static final int COLUMN_7 = 7;

	private final AbstractProductListView productListView;

	/**
	 * Constructor.
	 * @param productListView the product list view
	 */
	public ProductListViewLabelProvider(final AbstractProductListView productListView) {
		this.productListView = productListView;
	}

	@Override
	public Image getColumnImage(final Object element, final int columnIndex) {
		if (columnIndex == 0) {
			final ProductModel productEditorModel = (ProductModel) element;
			
			return CatalogImageRegistry.getSmallImageForProduct(productEditorModel.getProduct());
		}
		return null;
	}

	@Override
	public String getColumnText(final Object element, final int columnIndex) {
		final ProductModel productEditorModel = (ProductModel) element;
		Locale locale = productEditorModel.getProduct().getProductType().getCatalog().getDefaultLocale();


		switch (columnIndex) {
		case 0:
			return ""; //$NON-NLS-1$
		case 1:
			return productEditorModel.getProduct().getCode();
		case 2:
			return productEditorModel.getProduct().getDisplayName(locale);
		case COLUMN_3:
			return productEditorModel.getProduct().getProductType().getName();
		case COLUMN_4:
			if (productEditorModel.getProduct().getBrand() != null) {
				return productEditorModel.getProduct().getBrand().getDisplayName(locale, true);
			}
			return ""; //$NON-NLS-1$
		case COLUMN_5:
			return productEditorModel.getProduct().getDefaultCategory(productEditorModel.getProduct().getMasterCatalog()).getDisplayName(locale);
		case COLUMN_6:
			if (productEditorModel.getProduct().isWithinDateRange(new Date())) {
				return CatalogMessages.get().ProductListView_Active_Yes;
			}
			return CatalogMessages.get().ProductListView_Active_No;
		case COLUMN_7:
			if (this.productListView instanceof BrowseProductListView
				&& ((BrowseProductListView) this.productListView).getBrowseTreeObject() instanceof Category) {
					return getIncludeMsg(productEditorModel.getProduct());
			}
			return CatalogMessages.get().ProductListView_Active_No;
		default:
			return CatalogMessages.get().Product_NotAvailable;
		}
	}


	private String getIncludeMsg(final Product product) {
		Category category = (Category) ((BrowseProductListView) this.productListView).getBrowseTreeObject();

		if (product.isBelongToCategory(category.getUidPk())) {
			return CatalogMessages.get().ProductListView_Active_Yes;
		}
		return CatalogMessages.get().ProductListView_Active_No;
	}

}
