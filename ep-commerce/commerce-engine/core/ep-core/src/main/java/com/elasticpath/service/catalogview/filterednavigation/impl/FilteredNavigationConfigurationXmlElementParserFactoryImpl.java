/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.service.catalogview.filterednavigation.impl;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalogview.FilterType;
import com.elasticpath.service.catalogview.filterednavigation.FilteredNavigationConfigurationXmlElementParser;
import com.elasticpath.service.catalogview.filterednavigation.FilteredNavigationConfigurationXmlElementParserFactory;


/**
 *  Filtered navigation configuration XML element parser factory class for getting instance of 
 *  {@link FilteredNavigationConfigurationXmlElementParser}. 
 *
 */
public class FilteredNavigationConfigurationXmlElementParserFactoryImpl 
				implements FilteredNavigationConfigurationXmlElementParserFactory {
	
	private BeanFactory beanFactory;

	@Override
	public FilteredNavigationConfigurationXmlElementParser getFilteredNavigationConfigurationXmlElementParser(final FilterType filterType) {
		
		if (filterType == null) {
			return null;
		}
		FilteredNavigationConfigurationXmlElementParser xmlElementParser;
		switch (filterType) {
			case ATTRIBUTE_FILTER :
				xmlElementParser = getBeanFactory().getBean(ContextIdNames.ATTRIBUTE_FILTER_PARSER);
				break;
			case ATTRIBUTE_RANGE_FILTER :
				xmlElementParser = getBeanFactory().getBean(ContextIdNames.ATTRIBUTE_RANGE_FILTER_PARSER);
				break;
			case ATTRIBUTE_KEYWORD_FILTER :
				xmlElementParser = getBeanFactory().getBean(ContextIdNames.ATTRIBUTE_KEYWORD_FILTER_PARSER);
				break;
			case PRICE_FILTER :
				xmlElementParser = getBeanFactory().getBean(ContextIdNames.PRICE_FILTER_PARSER);
				break;
			case BRAND_FILTER :
				xmlElementParser = getBeanFactory().getBean(ContextIdNames.BRAND_FILTER_PARSER);
				break;
			default: 
				throw new IllegalArgumentException("Filter parser id is not valid " + filterType);
		}
		return xmlElementParser;
	}

	public BeanFactory getBeanFactory() {
		return beanFactory;
	}

	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}
}
