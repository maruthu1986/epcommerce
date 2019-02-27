/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
/**
 * 
 */
package com.elasticpath.cmclient.store.targetedselling.dynamiccontent.views;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.elasticpath.domain.contentspace.DynamicContent;

/**
 * DynamicContentSearchResultsViewLabelProvider
 * the model for the DynamicContentSearchResultsView.
 *
 */
public class DynamicContentSearchResultsViewLabelProvider extends LabelProvider implements ITableLabelProvider {

	/**
	 * Column Name Index.
	 */
	public static final int COLUMN_NAME = 0;
	/**
	 * Column Description Index.
	 */
	public static final int COLUMN_DESCRIPTION = 1;
	
	@Override
	public Image getColumnImage(final Object object, final int index) {
		return null;
	}

	@Override
	public String getColumnText(final Object object, final int index) {
		DynamicContent model = (DynamicContent) object;
		String result = ""; //$NON-NLS-1$
		switch (index) {
		case COLUMN_NAME:
			result = model.getName();
			break;
		case COLUMN_DESCRIPTION:
			result = model.getDescription();
			break;
		default:
		}
		result = StringUtils.defaultString(result);
		return result.replace("\r\n", " ").replace("\t", " ");
	}

}
