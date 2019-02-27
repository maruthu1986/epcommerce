/**
 * Copyright (c) Elastic Path Software Inc., 2011
 */
package com.elasticpath.cmclient.catalog.editors.catalog.tablelabelprovider;

import java.util.Arrays;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;

/**
 * A concrete TableLabelProviderDecorator for Catalog SkuOptions.
 */
public class CatalogSkuOptionTableLabelProviderDecorator extends AbstractTableLabelProviderDecorator {

	private static final String TREE_DROP_DOWN_COLUMN_INDEX = "TREE_DROP_DOWN_COLUMN_INDEX"; //$NON-NLS-1$

	private static final String SKU_OPTION_VALUE_COLUMN_INDEX = "SKU_OPTION_VALUE_COLUMN_INDEX"; //$NON-NLS-1$

	private static final String DISPLAY_NAME_COLUMN_INDEX = "DISPLAY_NAME_COLUMN_INDEX"; //$NON-NLS-1$

	private static final String IMAGE_COLUMN_INDEX = "IMAGE_COLUMN_INDEX"; //$NON-NLS-1$

	private static final String INDENTATION = "      "; //$NON-NLS-1$

	private final String[] skuOptionIndexes = { SKU_OPTION_VALUE_COLUMN_INDEX, DISPLAY_NAME_COLUMN_INDEX, IMAGE_COLUMN_INDEX };

	private final Locale locale;

	/**
	 * Constructor.
	 *
	 * @param decoratedTableLabelProvider the table label provider to be decorated
	 * @param locale the locale to apply to column text
	 */
	public CatalogSkuOptionTableLabelProviderDecorator(final ExtensibleTableLabelProvider decoratedTableLabelProvider, final Locale locale) {
		super(decoratedTableLabelProvider);

		this.locale = locale;

		// force addition of the tree dropdown to the beginning of the registry
		// needed to be handled separately as the decoration is placed in column 0,
		// and potentially change set column positioning could conflict with this
		getDecoratedTableLabelProvider().forceAddToStartColumnIndexRegistry(TREE_DROP_DOWN_COLUMN_INDEX);

		// add categoryType indexes to the list
		getDecoratedTableLabelProvider().addAllColumnIndexRegistry(Arrays.asList(skuOptionIndexes));
	}

	@Override
	public String getColumnText(final Object element, final int columnIndex) {
		String token = getDecoratedTableLabelProvider().getColumnText(element, columnIndex);
		final String columnName = getColumnIndexRegistryName(columnIndex);

		if (element instanceof SkuOption) {
			final SkuOption skuOption = (SkuOption) element;

			if (SKU_OPTION_VALUE_COLUMN_INDEX.equals(columnName)) {
				token = skuOption.getOptionKey();
			} else if (DISPLAY_NAME_COLUMN_INDEX.equals(columnName)) {
				token = skuOption.getDisplayName(locale, true);
			}
		} else if (element instanceof SkuOptionValue) {
			final SkuOptionValue skuOptionValue = (SkuOptionValue) element;

			if (SKU_OPTION_VALUE_COLUMN_INDEX.equals(columnName)) {
				token = INDENTATION + skuOptionValue.getOptionValueKey();
			} else if (DISPLAY_NAME_COLUMN_INDEX.equals(columnName)) {
				token = INDENTATION + skuOptionValue.getDisplayName(locale, true);
			} else if (IMAGE_COLUMN_INDEX.equals(columnName)) {
				token = INDENTATION + getImagePathFromSkuOptionValue(skuOptionValue);
			}
		}

		return token;
	}

	private String getImagePathFromSkuOptionValue(final SkuOptionValue skuOptionValue) {
		String imagePath = skuOptionValue.getImage();
		if (imagePath == null) {
			imagePath = StringUtils.EMPTY;
		}
		return imagePath;
	}

	@Override
	public void dispose() {
		getDecoratedTableLabelProvider().removeColumnIndexRegistry(TREE_DROP_DOWN_COLUMN_INDEX);
		getDecoratedTableLabelProvider().removeAllColumnIndexRegistry(Arrays.asList(skuOptionIndexes));
		getDecoratedTableLabelProvider().dispose();
	}

}
