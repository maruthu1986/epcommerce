/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.pricelistassignments.views;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.elasticpath.common.dto.pricing.PriceListAssignmentsDTO;
import com.elasticpath.cmclient.core.util.DateTimeUtilFactory;

/**
 * 
 * Label provider for (@link PriceListAssigmentsSearchView}.
 *
 */
@SuppressWarnings("restriction")
public class PriceListAssignmentSearchResultsViewLabelProvider extends LabelProvider implements ITableLabelProvider {
	
	/** Column indexes. */
	private static final int INDEX_NAME = 0;
	private static final int INDEX_DESCRIPTION = 1;
	private static final int INDEX_CATALOG = 2;
	private static final int INDEX_PRIORITY = 3;
	private static final int INDEX_PRICELIST = 4;
	private static final int INDEX_STARTDATE = 5;
	private static final int INDEX_ENDDATE = 6;
	
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
		
		PriceListAssignmentsDTO dto =
			(PriceListAssignmentsDTO) element;
			
		switch (columnIndex) {
			case INDEX_NAME:  return dto.getName();
			case INDEX_DESCRIPTION:  return dto.getDescription();
			case INDEX_CATALOG:  return dto.getCatalogName();
			case INDEX_PRIORITY:  return String.valueOf(dto.getPriority());
			case INDEX_PRICELIST:  return dto.getPriceListName();
			case INDEX_STARTDATE:  return DateTimeUtilFactory.getDateUtil().formatAsDate(dto.getStartDate());
			case INDEX_ENDDATE:  return DateTimeUtilFactory.getDateUtil().formatAsDate(dto.getEndDate());
			default: return StringUtils.EMPTY;
		}
		
	}

}

