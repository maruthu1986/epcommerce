/**
 * Copyright (c) Elastic Path Software Inc., 2011
 */
package com.elasticpath.cmclient.catalog.editors.catalog.tablelabelprovider;

import java.util.Arrays;
import java.util.List;

import com.elasticpath.domain.catalog.CategoryType;

/**
 * A concrete TableLabelProviderDecorator for Catalog CategoryTypes.
 */
public class CatalogCategoryTypeTableLabelProviderDecorator extends AbstractTableLabelProviderDecorator {

	private static final String NAME_COLUMN_INDEX = "NAME_COLUMN_INDEX"; //$NON-NLS-1$

	private final List<String> categoryTypeIndexes = Arrays.asList(NAME_COLUMN_INDEX);

	/**
	 * Constructor.
	 *
	 * @param decoratedTableLabelProvider the table label provider to be decorated
	 */
	public CatalogCategoryTypeTableLabelProviderDecorator(final ExtensibleTableLabelProvider decoratedTableLabelProvider) {
		super(decoratedTableLabelProvider);

		// add categoryType indexes to the list
		getDecoratedTableLabelProvider().addAllColumnIndexRegistry(categoryTypeIndexes);
	}

	@Override
	public String getColumnText(final Object element, final int columnIndex) {
		String token = getDecoratedTableLabelProvider().getColumnText(element, columnIndex);
		final CategoryType categoryType = (CategoryType) element;
		final String columnName = getColumnIndexRegistryName(columnIndex);

		if (NAME_COLUMN_INDEX.equals(columnName)) {
			token = categoryType.getName();
		}

		return token;
	}

	@Override
	public void dispose() {
		getDecoratedTableLabelProvider().removeAllColumnIndexRegistry(categoryTypeIndexes);
		getDecoratedTableLabelProvider().dispose();
	}

}
