/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.ql.parser.query;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents JPQL boolean query.
 */
public class SQLBooleanQuery extends SQLQuery {

	private static final char SPACE = ' ';

	private String prefix = "";
	
	private String postfix = "";

	private final List<SQLBooleanClause> clauses;

	/**
	 * Constructs sql boolean query.
	 */
	public SQLBooleanQuery() {
		clauses = new ArrayList<>();
	}

	@Override
	public String getNativeQuery() {
		StringBuilder queryString = new StringBuilder(prefix);

		for (int i = 0; i < clauses.size(); i++) {
			SQLBooleanClause clause = clauses.get(i);
			queryString.append(SPACE);
			queryString.append(clause.getOperator());
			queryString.append(SPACE);

			final String nativeQuery = clause.getQuery().getNativeQuery();

			queryString.append('(');
			queryString.append(nativeQuery);
			queryString.append(')');

			if (i != clauses.size() - 1) {
				queryString.append(' ');
			}
		}

		queryString.append(SPACE);
		queryString.append(postfix);
		return queryString.toString();
	}

	/**
	 * Adds clause to query.
	 * 
	 * @param clause the clause
	 */
	public void addClause(final SQLBooleanClause clause) {
		clauses.add(clause);
	}

	/**
	 * Adds clause list to query.
	 * 
	 * @param clauseList the clause list
	 */
	public void addClauses(final List<SQLBooleanClause> clauseList) {
		clauses.addAll(clauseList);
	}

	/**
	 * Sets the prefix for query.
	 * 
	 * @param prefix the query prefix
	 */
	public void setPrefix(final String prefix) {
		this.prefix = prefix;
	}
	
	/**
	 * Sets the postfix for query.
	 * 
	 * @param postfix the query postfix
	 */
	public void setPostfix(final String postfix) {
		this.postfix = postfix;
	}
}
