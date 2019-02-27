/**
 * Copyright (c) Elastic Path Software Inc., 2011
 */
package com.elasticpath.cmclient.catalog.editors.catalog.tablelabelprovider;

import java.util.Arrays;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.domain.attribute.Attribute;

/**
 * A concrete TableLabelProviderDecorator for Catalog Attributes.
 */
public class CatalogAttributeTableLabelProviderDecorator extends AbstractTableLabelProviderDecorator {

	private static final String KEY_COLUMN_INDEX = "KEY_COLUMN_INDEX"; //$NON-NLS-1$

	private static final String NAME_COLUMN_INDEX = "NAME_COLUMN_INDEX"; //$NON-NLS-1$

	private static final String TYPE_COLUMN_INDEX = "TYPE_COLUMN_INDEX"; //$NON-NLS-1$

	private static final String USAGE_COLUMN_INDEX = "USAGE_COLUMN_INDEX"; //$NON-NLS-1$

	private static final String REQUIRED_COLUMN_INDEX = "REQUIRED_COLUMN_INDEX"; //$NON-NLS-1$

	private static final String GLOBAL_COLUMN_INDEX = "GLOBAL_COLUMN_INDEX"; //$NON-NLS-1$

	private final String[] attributeIndexes = { KEY_COLUMN_INDEX, NAME_COLUMN_INDEX, TYPE_COLUMN_INDEX, USAGE_COLUMN_INDEX, REQUIRED_COLUMN_INDEX,
			GLOBAL_COLUMN_INDEX };

	/**
	 * Constructor.
	 *
	 * @param decoratedTableLabelProvider the table label provider to be decorated
	 */
	public CatalogAttributeTableLabelProviderDecorator(final ExtensibleTableLabelProvider decoratedTableLabelProvider) {
		super(decoratedTableLabelProvider);

		// add categoryType indexes to the list
		getDecoratedTableLabelProvider().addAllColumnIndexRegistry(Arrays.asList(attributeIndexes));
	}

	@Override
	public String getColumnText(final Object element, final int columnIndex) {
		String token = getDecoratedTableLabelProvider().getColumnText(element, columnIndex);
		final Attribute attribute = (Attribute) element;
		final String columnName = getColumnIndexRegistryName(columnIndex);

		if (KEY_COLUMN_INDEX.equals(columnName)) {
			token = attribute.getKey();
		} else if (NAME_COLUMN_INDEX.equals(columnName)) {
			token = attribute.getName();
		} else if (TYPE_COLUMN_INDEX.equals(columnName)) {
			token = getAttributeTypeMessage(attribute);
		} else if (USAGE_COLUMN_INDEX.equals(columnName)) {
			token = attribute.getAttributeUsage().toString();
		} else if (REQUIRED_COLUMN_INDEX.equals(columnName)) {
			token = getBooleanMessage(attribute.isRequired());
		} else if (GLOBAL_COLUMN_INDEX.equals(columnName)) {
			token = getBooleanMessage(attribute.isGlobal());
		}

		return token;
	}

	private String getAttributeTypeMessage(final Attribute attribute) {
		String token = CoreMessages.get().getMessage(attribute.getAttributeType().getNameMessageKey());
		if (attribute.isMultiValueEnabled()) {
			token = token + ' ' + CatalogMessages.get().ProductEditorAttributeSection_MultiValue;
		}
		return token;
	}

	@Override
	public void dispose() {
		getDecoratedTableLabelProvider().removeAllColumnIndexRegistry(Arrays.asList(attributeIndexes));
		getDecoratedTableLabelProvider().dispose();
	}

}
