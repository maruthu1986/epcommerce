/**
 * Copyright (c) Elastic Path Software Inc., 2008-2014
 */
package com.elasticpath.service.catalogview.filterednavigation.impl;

import java.io.Serializable;

import org.jdom.Element;

import com.elasticpath.persistence.api.EpPersistenceException;
import com.elasticpath.service.catalogview.filterednavigation.FilteredNavigationConfiguration;

/**
 * Parses the XML representation of the filtered navigation configuration; adds brand codes 
 * defined by the configuration and populates a given FilteredNavigationConfiguration object.
 */
public class BrandFilterXmlElementParserImpl extends AbstractFilterXmlElementParserImpl implements Serializable {
	
	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	private static final String BRAND = "brand";

	private static final String BRAND_KEY = "key";

	@Override
	public void parse(final Element sectionElement, final FilteredNavigationConfiguration config) {
		
		if (sectionElement == null) {
			return;
		}
		
		for (Object obj : sectionElement.getChildren(BRAND)) {
			final String brandCode = ((Element) obj).getAttributeValue(BRAND_KEY);

			if (brandCode == null) {
				throw new EpPersistenceException("Brand key cannot be null");
			}
			config.getAllBrandCodes().add(brandCode);
		}
	}

}
