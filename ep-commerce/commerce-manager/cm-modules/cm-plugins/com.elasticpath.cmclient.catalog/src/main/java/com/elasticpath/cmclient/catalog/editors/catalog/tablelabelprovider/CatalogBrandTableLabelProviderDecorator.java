/**
 * Copyright (c) Elastic Path Software Inc., 2011
 */
package com.elasticpath.cmclient.catalog.editors.catalog.tablelabelprovider;

import java.util.Arrays;
import java.util.Locale;

import com.elasticpath.domain.catalog.Brand;

/**
 * A concrete TableLabelProviderDecorator for Catalog Brands.
 */
public class CatalogBrandTableLabelProviderDecorator extends AbstractTableLabelProviderDecorator {

	private static final String CODE_COLUMN_INDEX = "CODE_COLUMN_INDEX"; //$NON-NLS-1$

	private static final String NAME_COLUMN_INDEX = "NAME_COLUMN_INDEX"; //$NON-NLS-1$

	private static final String IMAGE_COLUMN_INDEX = "IMAGE_COLUMN_INDEX"; //$NON-NLS-1$

	private final String[] brandIndexes = { CODE_COLUMN_INDEX, NAME_COLUMN_INDEX, IMAGE_COLUMN_INDEX };

	private final Locale locale;

	/**
	 * Constructor.
	 *
	 * @param decoratedTableLabelProvider the table label provider to be decorated
	 * @param locale the locale to apply to the column text
	 */
	public CatalogBrandTableLabelProviderDecorator(final ExtensibleTableLabelProvider decoratedTableLabelProvider, final Locale locale) {
		super(decoratedTableLabelProvider);

		this.locale = locale;

		// add categoryType indexes to the list
		getDecoratedTableLabelProvider().addAllColumnIndexRegistry(Arrays.asList(brandIndexes));
	}

	@Override
	public String getColumnText(final Object element, final int columnIndex) {
		String token = getDecoratedTableLabelProvider().getColumnText(element, columnIndex);
		final Brand brand = (Brand) element;
		final String columnName = getColumnIndexRegistryName(columnIndex);

		if (CODE_COLUMN_INDEX.equals(columnName)) {
			token = brand.getCode();
		} else if (NAME_COLUMN_INDEX.equals(columnName)) {
			token = brand.getDisplayName(locale, true);
		} else if (IMAGE_COLUMN_INDEX.equals(columnName)) {
			token = brand.getImageUrl();
		}

		return token;
	}

	@Override
	public void dispose() {
		getDecoratedTableLabelProvider().removeAllColumnIndexRegistry(Arrays.asList(brandIndexes));
		getDecoratedTableLabelProvider().dispose();
	}

}
