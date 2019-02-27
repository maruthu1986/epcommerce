/*
 * Copyright (c) Elastic Path Software Inc., 2011
 */
package com.elasticpath.cmclient.catalog.editors.catalog.tablelabelprovider;

import java.util.Arrays;
import java.util.List;

import com.elasticpath.domain.catalog.ProductType;

/**
 * A concrete TableLabelProviderDecorator for Catalog ProductTypes.
 */
public class CatalogProductTypeTableLabelProviderDecorator extends AbstractTableLabelProviderDecorator {

	private static final String NAME_COLUMN_INDEX = "NAME_COLUMN_INDEX"; //$NON-NLS-1$

	private static final String MULTI_SKU_COLUMN_INDEX = "MULTI_SKU_COLUMN_INDEX"; //$NON-NLS-1$

	private final List<String> productTypeIndexes = Arrays.asList(NAME_COLUMN_INDEX, MULTI_SKU_COLUMN_INDEX);

	/**
	 * Constructor.
	 *
	 * @param decoratedTableLabelProvider the table label provider to be decorated
	 */
	public CatalogProductTypeTableLabelProviderDecorator(final ExtensibleTableLabelProvider decoratedTableLabelProvider) {
		super(decoratedTableLabelProvider);

		// add productType indexes to the list
		getDecoratedTableLabelProvider().addAllColumnIndexRegistry(productTypeIndexes);
	}


	@Override
	public String getColumnText(final Object element, final int columnIndex) {
		String token = getDecoratedTableLabelProvider().getColumnText(element, columnIndex);
		final ProductType productType = (ProductType) element;
		final String columnName = getColumnIndexRegistryName(columnIndex);

		if (NAME_COLUMN_INDEX.equals(columnName)) {
			token = productType.getName();
		} else if (MULTI_SKU_COLUMN_INDEX.equals(columnName)) {
			token = getBooleanMessage(productType.isMultiSku());
		}

		return token;
	}

	@Override
	public void dispose() {
		getDecoratedTableLabelProvider().removeAllColumnIndexRegistry(productTypeIndexes);
		getDecoratedTableLabelProvider().dispose();
	}

}
