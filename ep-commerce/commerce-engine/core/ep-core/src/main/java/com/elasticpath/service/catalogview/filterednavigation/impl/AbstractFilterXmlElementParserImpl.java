/**
 * Copyright (c) Elastic Path Software Inc., 2008-2014
 */
package com.elasticpath.service.catalogview.filterednavigation.impl;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import org.jdom.Element;

import com.elasticpath.service.catalogview.FilterFactory;
import com.elasticpath.service.catalogview.filterednavigation.FilteredNavigationConfigurationXmlElementParser;

/**
 * Abstract class for {@link FilteredNavigationConfigurationXmlElementParser}.
 */
public abstract class AbstractFilterXmlElementParserImpl implements FilteredNavigationConfigurationXmlElementParser, Serializable {

	private static final long serialVersionUID = -6503155373837909475L;

	/** Constant string for filter seo id. */
	protected static final String SEO_ID = "id";

	/** Constant string for filter localized indicator. */
	protected static final String LOCALIZED = "localized";

	/** Constant string for filter language. */
	protected static final String LANGUAGE = "language";
	/** Constant string for TURE. */
	protected static final String TRUE = "true";

	private FilterFactory filterFactory;
	
	/**
	 * Gets an element's children.
	 * 
	 * @param node the element
	 * @return the element's children
	 */
	@SuppressWarnings("unchecked")
	protected List<Element> getChildren(final Element node) {
		final List<Element> children = node.getChildren();
		if (children == null) {
			return Collections.emptyList();
		}
		return children;
	}

	/**
	 * Gets the filter factory.
	 *
	 * @return the filter factory
	 */
	public FilterFactory getFilterFactory() {
		return this.filterFactory;
	}
	
	/**
	 * Sets the filter factory.
	 *
	 * @param filterFactory the new filter factory
	 */
	public void setFilterFactory(final FilterFactory filterFactory) {
		this.filterFactory = filterFactory;
	}
}
