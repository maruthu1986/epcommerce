/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
/**
 * 
 */
package com.elasticpath.cmclient.store.targetedselling.conditionalexpression.views;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.elasticpath.cmclient.store.targetedselling.TargetedSellingImageRegistry;
import com.elasticpath.tags.domain.ConditionalExpression;

/**
 * ConditionalExpressionSearchResultViewLabelProvider is a label provider for search result view.
 *
 */
public class ConditionalExpressionSearchResultViewLabelProvider extends
		LabelProvider implements ITableLabelProvider {

	private static final int COLUMN_IMAGE = 0;
	private static final int COLUMN_NAME = 1;
	private static final int COLUMN_DESCRIPTION = 2;
	private static final int COLUMN_TAG_DICTIONARY = 3;

	@Override
	public Image getColumnImage(final Object element, final int columnIndex) {

		switch (columnIndex) {
		case COLUMN_IMAGE:
			return TargetedSellingImageRegistry.getImage(TargetedSellingImageRegistry.IMAGE_CONDITIONAL_EXPRESSION_LIST);
		default:
			return null;
		}
	}

	@Override
	public String getColumnText(final Object element, final int columnIndex) {

		ConditionalExpression model = (ConditionalExpression) element;
		String result = ""; //$NON-NLS-1$
		switch (columnIndex) {
		case COLUMN_NAME:
			result = model.getName();
			break;
		case COLUMN_DESCRIPTION:
			result = model.getDescription();
			break;
		case COLUMN_TAG_DICTIONARY:
			result = model.getTagDictionaryGuid();
			break;
		default:
		}
		result = StringUtils.defaultString(result);
		return result.replace("\r\n", " ").replace("\t", " ");
	}

}
