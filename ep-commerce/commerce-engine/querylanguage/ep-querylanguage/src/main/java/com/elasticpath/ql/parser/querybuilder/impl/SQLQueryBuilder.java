/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.ql.parser.querybuilder.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.elasticpath.ql.parser.EpQLSortClause;
import com.elasticpath.ql.parser.EpQueryParser;
import com.elasticpath.ql.parser.query.NativeBooleanClause;
import com.elasticpath.ql.parser.query.NativeQuery;
import com.elasticpath.ql.parser.query.SQLBooleanClause;
import com.elasticpath.ql.parser.query.SQLBooleanQuery;
import com.elasticpath.ql.parser.query.SQLQuery;
import com.elasticpath.ql.parser.querybuilder.CompleteQueryBuilder;

/**
 * Represents query sql query builder.
 */
public class SQLQueryBuilder implements CompleteQueryBuilder {
	
	private String queryPrefix = "";
	
	private String queryPostfix = "";

	private static final char SPACE = ' ';

	private static final String WHERE = "WHERE";

	private static final String SQL_AND = "AND";

	private static final String SQL_OR = "OR";

	private static final String SQL_NOT = "NOT";

	@Override
	public void addBooleanClause(final List<NativeBooleanClause> clauses, final int conj, final NativeQuery query, final String operator) {
		String sqlOperation = "";
		switch (conj) {
		case EpQueryParser.CONJ_AND:
			sqlOperation = SQL_AND;
			break;
		case EpQueryParser.CONJ_OR:
			sqlOperation = SQL_OR;
			break;
		case EpQueryParser.CONJ_NOT:
			sqlOperation = SQL_NOT;
			break;
		default:
		}

		clauses.add(new SQLBooleanClause((SQLQuery) query, sqlOperation));
	}

	@Override
	public SQLQuery getBooleanQuery(final List<NativeBooleanClause> clauses) {
		if (clauses.isEmpty()) {
			return null; // all clause words were filtered away by the analyzer.
		}

		final SQLBooleanQuery query = new SQLBooleanQuery();
		query.addClauses(Arrays.asList(clauses.toArray(new SQLBooleanClause[clauses.size()])));
		return query;
	}
	
	@Override
	public void setQueryPrefix(final String prefix) {
		this.queryPrefix = prefix;
	}

	@Override
	public void setSortClauses(final List<EpQLSortClause> sortClauses) {
		List<String> sortStrings = new ArrayList<>();
		for (EpQLSortClause sortClause : sortClauses) {
			sortStrings.add(sortClause.getNativeFieldName() + " " + sortClause.getSortOrder().name());
		}
		queryPostfix = "ORDER BY " + StringUtils.join(sortStrings, ", ");
	}

	@Override
	public NativeQuery checkProcessedQuery(final NativeQuery nativeQuery) {
		final StringBuilder command = new StringBuilder(queryPrefix);
		SQLBooleanQuery booleanQuery = (SQLBooleanQuery) nativeQuery;
		if (booleanQuery == null) {
			booleanQuery = new SQLBooleanQuery();
		} else {
			command.append(SPACE);
			command.append(WHERE);
		}
		booleanQuery.setPrefix(command.toString());
		booleanQuery.setPostfix(queryPostfix);
		return booleanQuery;
	}
}
