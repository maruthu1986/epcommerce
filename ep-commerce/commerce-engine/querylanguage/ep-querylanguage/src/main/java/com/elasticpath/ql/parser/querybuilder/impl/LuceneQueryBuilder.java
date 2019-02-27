/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.ql.parser.querybuilder.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.TermQuery;
import org.apache.solr.client.solrj.SolrQuery;

import com.elasticpath.ql.parser.EpQLOperator;
import com.elasticpath.ql.parser.EpQLSortClause;
import com.elasticpath.ql.parser.EpQLSortOrder;
import com.elasticpath.ql.parser.EpQueryParser;
import com.elasticpath.ql.parser.query.LuceneBooleanClause;
import com.elasticpath.ql.parser.query.LuceneQuery;
import com.elasticpath.ql.parser.query.NativeBooleanClause;
import com.elasticpath.ql.parser.query.NativeQuery;
import com.elasticpath.ql.parser.querybuilder.CompleteQueryBuilder;

/**
 * This class represents lucene query builder.
 */
public class LuceneQueryBuilder implements CompleteQueryBuilder {

	private static final LuceneQuery MATCH_ALL_QUERY = new LuceneQuery(new TermQuery(new Term("*", "*")));

	private List<SolrQuery.SortClause> sortClauses;

	@Override
	public LuceneQuery getBooleanQuery(final List<NativeBooleanClause> clauses) {
		if (clauses.isEmpty()) {
			return null; // all clause words were filtered away by the analyzer.
		}
		BooleanQuery query = new BooleanQuery(false);
		boolean once = true;
		for (int i = 0; i < clauses.size(); i++) {
			final LuceneBooleanClause clause = (LuceneBooleanClause) clauses.get(i);
			if (once && clause.getOccur() == Occur.MUST_NOT) {
				query.add(new TermQuery(new Term("*", "*")), Occur.SHOULD);
				once = false;
			}
			query.add(clause);
		}
		return new LuceneQuery(query);
	}

	@Override
	public void addBooleanClause(final List<NativeBooleanClause> clauses, final int conj, final NativeQuery query, final String operator) {

		setLastClauseOccurrence(clauses, conj);

		// We might have been passed a null query; the term might have been
		// filtered away by the analyzer.
		if (query == null) {
			return;
		}

		boolean prohibited = EpQLOperator.NOT_EQUAL == EpQLOperator.getEpQLOperator(operator);
		if (conj == EpQueryParser.CONJ_NOT) {
			prohibited ^= true;
		}
		boolean required = !prohibited && conj != EpQueryParser.CONJ_OR;

		final LuceneQuery luceneQuery = (LuceneQuery) query;
		if (required) {
			clauses.add(new LuceneBooleanClause(luceneQuery, BooleanClause.Occur.MUST));
		} else if (prohibited) {
			clauses.add(new LuceneBooleanClause(luceneQuery, Occur.MUST_NOT));
		} else {
			clauses.add(new LuceneBooleanClause(luceneQuery, Occur.SHOULD));
		}
	}

	/**
	 * Updates occurrence of the last boolean clause depending on conjunctive operator.
	 *
	 * @param clauses the list of clauses already parsed
	 * @param conj conjunctive operator: OR or AND or NOT or NONE (only OR and AND are significant here)
	 */
	void setLastClauseOccurrence(final List<NativeBooleanClause> clauses, final int conj) {
		// If this term is introduced by AND, make the preceding term required,
		// unless it's already prohibited
		if (!clauses.isEmpty() && conj == EpQueryParser.CONJ_AND) {
			LuceneBooleanClause clause = (LuceneBooleanClause) clauses.get(clauses.size() - 1);
			if (!clause.isProhibited()) {
				clause.setOccur(BooleanClause.Occur.MUST);
			}
		}

		if (!clauses.isEmpty() && conj == EpQueryParser.CONJ_OR) {
			// If this term is introduced by OR, make the preceding term optional,
			// unless it's prohibited (that means we leave -a OR b but +a OR b-->a OR b)
			// notice if the input is a OR b, first term is parsed as required; without
			// this modification a OR b would parsed as +a OR b
			LuceneBooleanClause clause = (LuceneBooleanClause) clauses.get(clauses.size() - 1);
			if (!clause.isProhibited()) {
				clause.setOccur(BooleanClause.Occur.SHOULD);
			}
		}
	}

	@Override
	public void setSortClauses(final List<EpQLSortClause> sortClauses) {
		Map<EpQLSortOrder, SolrQuery.ORDER> sortOrderMapping = ImmutableMap.of(
				EpQLSortOrder.ASC, SolrQuery.ORDER.asc,
				EpQLSortOrder.DESC, SolrQuery.ORDER.desc
		);
		this.sortClauses = new ArrayList<>();
		for (EpQLSortClause sortClause : sortClauses) {
			SolrQuery.SortClause solrSortClause = new SolrQuery.SortClause(sortClause.getNativeFieldName(),
					sortOrderMapping.get(sortClause.getSortOrder()));
			this.sortClauses.add(solrSortClause);
		}
	}

	@Override
	public NativeQuery checkProcessedQuery(final NativeQuery nativeQuery) {
		LuceneQuery luceneQuery = (LuceneQuery) nativeQuery;
		if (nativeQuery == null) {
			luceneQuery = MATCH_ALL_QUERY;
		}
		luceneQuery.setSortClauses(sortClauses);
		return luceneQuery;
	}

	@Override
	public void setQueryPrefix(final String prefix) {
		//do nothing
	}
}
