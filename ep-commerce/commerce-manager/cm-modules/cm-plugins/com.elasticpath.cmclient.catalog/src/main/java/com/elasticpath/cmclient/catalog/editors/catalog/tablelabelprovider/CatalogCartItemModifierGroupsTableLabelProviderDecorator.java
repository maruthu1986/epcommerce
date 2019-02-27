/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */

package com.elasticpath.cmclient.catalog.editors.catalog.tablelabelprovider;

import java.util.Arrays;
import java.util.Locale;
import java.util.Set;

import com.elasticpath.domain.cartmodifier.CartItemModifierGroup;
import com.elasticpath.domain.cartmodifier.CartItemModifierGroupLdf;

/**
 * A concrete TableLabelProviderDecorator for Catalog ProductTypes.
 */
public class CatalogCartItemModifierGroupsTableLabelProviderDecorator extends AbstractTableLabelProviderDecorator {

	private static final String CODE_COLUMN_INDEX = "CODE_COLUMN_INDEX"; //$NON-NLS-1$

	private static final String NAME_COLUMN_INDEX = "NAME_COLUMN_INDEX"; //$NON-NLS-1$

	private final String[] cartItemModifierGroupIndexes = {CODE_COLUMN_INDEX, NAME_COLUMN_INDEX};

	private final Locale locale;

	/**
	 * Constructor.
	 * @param decoratedTableLabelProvider the table label provider to be decorated.
	 * @param locale Selected Locale.
	 */
	public CatalogCartItemModifierGroupsTableLabelProviderDecorator(final ExtensibleTableLabelProvider decoratedTableLabelProvider,
			final Locale locale) {
		super(decoratedTableLabelProvider);

		this.locale = locale;

		// add AddEditCartItemModifierGroupDialog indexes to the list
		getDecoratedTableLabelProvider().addAllColumnIndexRegistry(Arrays.asList(cartItemModifierGroupIndexes));
	}

	@Override
	public String getColumnText(final Object element, final int columnIndex) {
		String token = getDecoratedTableLabelProvider().getColumnText(element, columnIndex);
		String dspName = "";
		final String columnName = getColumnIndexRegistryName(columnIndex);
		final CartItemModifierGroup group = (CartItemModifierGroup) element;
		Set<CartItemModifierGroupLdf> ldfs = group.getCartItemModifierGroupLdf();
		for (CartItemModifierGroupLdf ldf : ldfs) {
			if (ldf.getLocale().trim().equals(this.locale.toString().trim())) {
				dspName = ldf.getDisplayName();
			}
		}
		if (CODE_COLUMN_INDEX.equals(columnName)) {
			token = group.getCode();
		} else if (NAME_COLUMN_INDEX.equals(columnName)) {
			token = dspName;
		}

		return token;
	}

	@Override
	public void dispose() {
		getDecoratedTableLabelProvider().removeAllColumnIndexRegistry(Arrays.asList(cartItemModifierGroupIndexes));
		getDecoratedTableLabelProvider().dispose();
	}

}
