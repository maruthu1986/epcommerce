/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.service.catalogview.filterednavigation;

import org.jdom.Element;

/**
 * Parses the persistent XML element representation of the intelligent-browsing configuration.
 */
public interface FilteredNavigationConfigurationXmlElementParser {

	/**
	 * Parses the given configuration element section and uses it to populate the given configuration object.
	 * @param sectionElement the element containing the configuration
	 * @param configurationObject the configuration object to populate
	 */
	void parse(Element sectionElement, FilteredNavigationConfiguration configurationObject);
	
}
