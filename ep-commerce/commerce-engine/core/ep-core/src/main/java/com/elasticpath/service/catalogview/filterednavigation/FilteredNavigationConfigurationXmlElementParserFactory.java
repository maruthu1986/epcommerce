/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.service.catalogview.filterednavigation;

import com.elasticpath.domain.catalogview.FilterType;

/**
 *  Filtered navigation configuration XML element parser factory class for getting instance of 
 *  {@link FilteredNavigationConfigurationXmlElementParser}. 
 *
 */
public interface FilteredNavigationConfigurationXmlElementParserFactory {
	
	/**
	 * @param filterType the filter type
	 * @return a {@link FilteredNavigationConfigurationXmlElementParser} instance for the given filter type
	 */
	FilteredNavigationConfigurationXmlElementParser getFilteredNavigationConfigurationXmlElementParser(FilterType filterType);
}
