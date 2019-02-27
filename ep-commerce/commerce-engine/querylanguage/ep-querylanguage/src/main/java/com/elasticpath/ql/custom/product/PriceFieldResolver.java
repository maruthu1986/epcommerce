/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.ql.custom.product;

import com.elasticpath.ql.parser.EpQLFieldDescriptor;
import com.elasticpath.ql.parser.EpQLTerm;
import com.elasticpath.ql.parser.EpQuery;
import com.elasticpath.ql.parser.NativeResolvedTerm;
import com.elasticpath.ql.parser.fieldresolver.EpQLFieldResolver;
import com.elasticpath.ql.parser.gen.ParseException;

/**
 * Price EP QL field descriptor. This resolver treats parameter 1 as currency code and parameter 2 as store code.
 */

public class PriceFieldResolver implements EpQLFieldResolver {

	@Override
	public NativeResolvedTerm resolve(final EpQuery epQuery, final EpQLTerm epQLTerm, final EpQLFieldDescriptor solrTemplateDescriptor) 
		throws ParseException {

		throw new ParseException("Solr now requires catalog code and pricelist not store and currency");
	}

}
