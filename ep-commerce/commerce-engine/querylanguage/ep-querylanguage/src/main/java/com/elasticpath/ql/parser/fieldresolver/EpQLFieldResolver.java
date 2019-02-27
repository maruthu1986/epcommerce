/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.ql.parser.fieldresolver;

import com.elasticpath.ql.parser.EpQLFieldDescriptor;
import com.elasticpath.ql.parser.EpQLTerm;
import com.elasticpath.ql.parser.EpQuery;
import com.elasticpath.ql.parser.NativeResolvedTerm;
import com.elasticpath.ql.parser.gen.ParseException;

/**
 * Resolves EP QL term to SolrDescriptor which holds assembled Solr field.
 */
public interface EpQLFieldResolver {

	/**
	 * Resolves Ep QL field to SolrDescriptor which holds assembled Solr field.
	 *
	 * @param epQuery epQuery
	 * @param epQLTerm EP QL Term
	 * @param solrTemplateDescriptor Solr template descriptor holding Solr field type and other information.
	 * @return ResolvedSolrField holding assembled Solr field.
	 * @throws ParseException if some parameter is missing or resolution can't be provided.
	 */
	NativeResolvedTerm resolve(EpQuery epQuery, EpQLTerm epQLTerm,
			EpQLFieldDescriptor solrTemplateDescriptor) throws ParseException;
}
