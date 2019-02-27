/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */

package com.elasticpath.cmclient.catalog.dialogs.cartitemmodifier.tablelabelcontentprovider;

import java.util.Locale;
import java.util.Set;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.domain.cartmodifier.CartItemModifierField;
import com.elasticpath.domain.cartmodifier.CartItemModifierFieldLdf;

/**
 * Label provider for AddEditCartItemModifierFieldDialog TableViewer.
 */
public class CartItemModifierGroupLabelProvider extends LabelProvider implements ITableLabelProvider {

	private final Locale defaultLocal;

	private static final int COLUMN_CODE = 0; //$NON-NLS-1$

	private static final int COLUMN_DISPLAY_NAME = 1; //$NON-NLS-1$

	private static final int COLUMN_FIELD_TYPE = 2; //$NON-NLS-1$

	private static final int COLUMN_REQUIRED = 3; //$NON-NLS-1$

	private static final String FIELDTYPE_NAME_PREFIX = "CartItemModifierFieldTypeName_";  //$NON-NLS-1$

	/**
	 * Constructor.
	 *
	 * @param defaultLocal selected locale.
	 */
	public CartItemModifierGroupLabelProvider(final Locale defaultLocal) {
		this.defaultLocal = defaultLocal;
	}

	@Override
	public Image getColumnImage(final Object element, final int columnIndex) {
		return null;
	}

	@Override
	public String getColumnText(final Object element, final int columnIndex) {
		String result = "";
		String dispName = "";
		CartItemModifierField field = (CartItemModifierField) element;
		Set<CartItemModifierFieldLdf> ldfs = field.getCartItemModifierFieldsLdf();
		for (CartItemModifierFieldLdf tmp : ldfs) {
			if (tmp.getLocale().trim().equals(this.defaultLocal.toString().trim())) {
				dispName = tmp.getDisplayName();
			}
		}
		if (columnIndex == COLUMN_CODE) {
			result = field.getCode();
		} else if (columnIndex == COLUMN_DISPLAY_NAME) {
			result = dispName;
		} else if (columnIndex == COLUMN_FIELD_TYPE) {
			result = CatalogMessages.get().getMessage(FIELDTYPE_NAME_PREFIX + field.getFieldType().getName());
		} else if (columnIndex == COLUMN_REQUIRED) {
			result = String.valueOf(field.isRequired());
		}
		return result;
	}
}
