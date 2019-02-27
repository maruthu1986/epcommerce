/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.ql.parser.querybuilder.impl;

import static com.elasticpath.ql.parser.EpQLOperator.EQUAL;
import static com.elasticpath.ql.parser.EpQLOperator.LESS;
import static com.elasticpath.ql.parser.EpQLOperator.LESS_OR_EQUAL;
import static com.elasticpath.ql.parser.EpQLOperator.MORE;
import static com.elasticpath.ql.parser.EpQLOperator.MORE_OR_EQUAL;
import static com.elasticpath.ql.parser.EpQLOperator.NOT_EQUAL;

import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermRangeQuery;

import com.elasticpath.ql.parser.EpQLOperator;
import com.elasticpath.ql.parser.EpQLTerm;
import com.elasticpath.ql.parser.NativeResolvedTerm;
import com.elasticpath.ql.parser.gen.ParseException;

/**
 * <code>RangeQueryBuilder</code> considers field type and provides a range query depending on the operator parameter. Helper class.
 */
public class LuceneRangeSubQueryBuilder {

	/**
	 * If the query is not a range query considering the operator, return null. Otherwise tries to resolve range query considering field's type.
	 *
	 * @param resolvedSolrField SolrFieldDescriptor containing type information
	 * @param queryText analyzed text
	 * @param epQLTerm EP QL Term
	 * @return Range query
	 * @throws ParseException in case if range query can not be provided for the specified field.
	 */
	@SuppressWarnings("fallthrough")
	public Query getRangeQuery(final NativeResolvedTerm resolvedSolrField, final EpQLTerm epQLTerm, final String queryText) throws ParseException {
		if (EQUAL == epQLTerm.getOperator() || NOT_EQUAL == epQLTerm.getOperator()) {
			return null;
		}
		if (LESS != epQLTerm.getOperator() && LESS_OR_EQUAL != epQLTerm.getOperator() && MORE != epQLTerm.getOperator()
				&& MORE_OR_EQUAL != epQLTerm.getOperator()) {
			throw new ParseException("Invalid operator for range query: " + epQLTerm.getOperator());
		}
		switch (resolvedSolrField.getFieldDescriptor().getType()) {
		case DATE:
		case FLOAT:
			return getRangeQuery(resolvedSolrField, queryText, epQLTerm.getOperator());
		default:
			break;
		}
		throw new ParseException("Range query is not supported for this field: " + epQLTerm.getEpQLField().getFieldName() + " of type: "
				+ resolvedSolrField.getFieldDescriptor().getType());
	}

	/**
	 * Get range query.
	 *
	 * @param solrFieldDescriptor ResolvedSolrField containing resolved field name.
	 * @param queryText analyzed text
	 * @param operator range or equal operator.
	 * @return Range query
	 */
	Query getRangeQuery(final NativeResolvedTerm solrFieldDescriptor, final String queryText, final EpQLOperator operator) {
		boolean inclusive;
		inclusive = LESS_OR_EQUAL == operator || MORE_OR_EQUAL == operator;
		String field = solrFieldDescriptor.getResolvedField();
		if (LESS.equals(operator) || LESS_OR_EQUAL.equals(operator)) {
			return TermRangeQuery.newStringRange(field, null, queryText, inclusive, inclusive);
		}
		return TermRangeQuery.newStringRange(field, queryText, null, inclusive, inclusive);
	}
}
