/**
 * Copyright (c) Elastic Path Software Inc., 2008-2014
 */
package com.elasticpath.service.catalogview.filterednavigation.impl;

import org.apache.log4j.Logger;
import org.jdom.Element;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalogview.AttributeKeywordFilter;
import com.elasticpath.persistence.api.EpPersistenceException;
import com.elasticpath.service.catalogview.filterednavigation.FilteredNavigationConfiguration;

/**
 * Parses the XML representation of the filtered navigation configuration; creates {@link AttributeKeywordFilter} 
 * defined by the configuration and populates a given FilteredNavigationConfiguration object.
 */
public class AttributeKeywordFilterXmlElementParserImpl extends AbstractFilterXmlElementParserImpl {
	
	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	private static final Logger LOG = Logger.getLogger(AttributeKeywordFilterXmlElementParserImpl.class);
	
	private static final String ATTRIBUTE_KEY = "key";

	@Override
	public void parse(final Element sectionElement, final FilteredNavigationConfiguration config) {

		if (sectionElement == null) {
			return;
		}
		
		final String attributeKey = sectionElement.getAttributeValue(ATTRIBUTE_KEY);
		if (config.getAllAttributeKeywords().get(attributeKey) != null) {
			throw new EpPersistenceException("Attribute keyword has been defined multiple times:" + attributeKey);
		}
		AttributeKeywordFilter attrKeywordFilter = getFilterFactory().getFilterBean(ContextIdNames.ATTRIBUTE_KEYWORD_FILTER);
		attrKeywordFilter.setId(attributeKey);
		attrKeywordFilter.setAttributeKey(attributeKey);
		// If attribute not found in database, just ignore it.
		if (attrKeywordFilter.getAttribute() == null) {
			LOG.error("Attribute not found: " + attributeKey);
			return;
		}

		config.getAllAttributeKeywords().put(attrKeywordFilter.getId(), attrKeywordFilter);
		config.getAllAttributesMap().put(attributeKey, attrKeywordFilter.getAttribute());
	}
}
