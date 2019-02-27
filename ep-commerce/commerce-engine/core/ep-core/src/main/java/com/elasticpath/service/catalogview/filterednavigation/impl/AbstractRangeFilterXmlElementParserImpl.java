/**
 * Copyright (c) Elastic Path Software Inc., 2008-2014
 */
package com.elasticpath.service.catalogview.filterednavigation.impl;

import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.LocaleUtils;
import org.jdom.Element;

import com.elasticpath.domain.catalogview.FilterDisplayInfo;
import com.elasticpath.domain.catalogview.RangeFilter;
import com.elasticpath.domain.catalogview.impl.FilterDisplayInfoImpl;

/**
 * Abstract class of {@code FilteredNavigationConfigurationXmlElementParser} for {@code RangeFilter} type.
 */
public abstract class AbstractRangeFilterXmlElementParserImpl extends AbstractFilterXmlElementParserImpl {

	private static final long serialVersionUID = -8350978038848837112L;

	/** Constant string for filter range. */
	protected static final String RANGE = "range";

	/** Constant string for filter range upper value. */
	protected static final String UPPER_VALUE = "upper";

	/** Constant string for filter range lower value. */
	protected static final String LOWER_VALUE = "lower";
	/** Constant string for filter display info. */
	protected static final String DISPLAY_INFO = "display";

	/**
	 * Parse the displayInfo node. Add the displayInfo to the rangeFilter.
	 *
	 * @param rangeFilter the filter which the displayInfo belongs to.
	 * @param childNode the xml node contains displayInfo.
	 * @param <T> the type of comparable range filter
	 * @param <E> the type of comparable object to range on
	 */
	protected <T extends RangeFilter<T, E>, E extends Comparable<E>> void parseDisplayInfo(final RangeFilter<T, E> rangeFilter,
			final Element childNode) {
		String language = childNode.getAttributeValue(LANGUAGE);
		FilterDisplayInfo displayInfo = parseRangeFilterDisplayInfo(childNode);
		Locale locale = LocaleUtils.toLocale(language);
		rangeFilter.addDisplayInfo(locale, displayInfo);
	}

	/**
	 * @param childNode
	 * @return
	 */
	private FilterDisplayInfo parseRangeFilterDisplayInfo(final Element childNode) {
		FilterDisplayInfo displayInfo = new FilterDisplayInfoImpl();
		final List<Element> infoNodes = getChildren(childNode);
		if (infoNodes != null && !infoNodes.isEmpty()) {
			for (Element infoNode : infoNodes) {
				if ("value".equals(infoNode.getName())) {
					displayInfo.setDisplayName(infoNode.getText());
				} else if ("seo".equals(infoNode.getName())) {
					displayInfo.setSeoName(infoNode.getText());
				}
			}
		}
		return displayInfo;
	}
}
