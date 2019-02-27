/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */

package com.elasticpath.cmclient.catalog.dialogs.cartitemmodifier.tablelabelcontentprovider;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.elasticpath.domain.cartmodifier.CartItemModifierFieldOption;
import com.elasticpath.domain.cartmodifier.CartItemModifierFieldOptionLdf;

/**
 * Label provider for AddEditCartItemModifierFieldDialog TableViewer.
 */
public class CartItemModifierFieldLabelProvider extends LabelProvider implements ITableLabelProvider {

	private final Locale defaultLocal;

	private static final int COLUMN_CODE = 0; //$NON-NLS-1$

	private static final int COLUMN_DISPLAY_NAME = 1; //$NON-NLS-1$

	/**
	 * Constructor.
	 *
	 * @param defaultLocal selected locale.
	 */
	public CartItemModifierFieldLabelProvider(final Locale defaultLocal) {
		this.defaultLocal = defaultLocal;
	}

	@Override
	public Image getColumnImage(final Object element, final int columnIndex) {
		return null;
	}

	@Override
	public String getColumnText(final Object element, final int columnIndex) {
		String result = null;
		String dispName = "";
		CartItemModifierFieldOption opt = (CartItemModifierFieldOption) element;
		Set<CartItemModifierFieldOptionLdf> ldfs = new HashSet<>();

		ldfs.addAll(opt.getCartItemModifierFieldOptionsLdf());
		if (!ldfs.isEmpty()) {
			for (CartItemModifierFieldOptionLdf tmp : ldfs) {
				if (tmp.getLocale().trim().equals(this.defaultLocal.toString().trim())) {
					dispName = tmp.getDisplayName();
				}
			}
		}
		if (columnIndex == COLUMN_CODE) {
			result = opt.getValue();
		} else if (columnIndex == COLUMN_DISPLAY_NAME) {
			result = dispName;
		}
		return result;
	}
}
