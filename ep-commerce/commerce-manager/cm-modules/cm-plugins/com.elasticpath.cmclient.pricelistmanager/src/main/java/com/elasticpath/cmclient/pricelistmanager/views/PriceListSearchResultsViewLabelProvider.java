/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.pricelistmanager.views;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.elasticpath.common.dto.pricing.PriceListDescriptorDTO;

/**
 * Provides images and text for the columns in the {@link PriceListSearchResultsView}.
 */
public class PriceListSearchResultsViewLabelProvider  extends LabelProvider implements ITableLabelProvider {
	
	/** Column indices. */
	private static final int INDEX_NAME = 0;
	private static final int INDEX_CURRENCY = 1;
	private static final int INDEX_DESCRIPTION = 2;

	
	
	/**
	 * Gets the image to put in a column.
	 * @param element the row object
	 * @param columnIndex the column index
	 * @return the Image to put in the column
	 */
	@Override
	public Image getColumnImage(final Object element, final int columnIndex) {
		return null;
	}

	/**
	 * Get the text to put in each column.
	 *
	 * @param element the row object
	 * @param columnIndex the column index
	 * @return the String to put in the column
	 */
	@Override
	public String getColumnText(final Object element, final int columnIndex) {
		final PriceListDescriptorDTO plDto = (PriceListDescriptorDTO) element;
		switch (columnIndex) {
			case INDEX_NAME:
				return plDto.getName();
			case INDEX_CURRENCY:
				return plDto.getCurrencyCode();
			case INDEX_DESCRIPTION:
				return plDto.getDescription();
			default:
				return StringUtils.EMPTY;
			}
		}
	}

