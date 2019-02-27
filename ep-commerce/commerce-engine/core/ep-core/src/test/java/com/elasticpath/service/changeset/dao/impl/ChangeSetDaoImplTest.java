/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.service.changeset.dao.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.mockito.Matchers;

import com.elasticpath.domain.changeset.ChangeSetStateCode;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.service.changeset.ChangeSetSearchCriteria;
import com.elasticpath.service.search.query.SortOrder;
import com.elasticpath.service.search.query.StandardSortBy;

/**
 * The junit class for changeSetDaoImpl.
 */

public class ChangeSetDaoImplTest {
	
	private final ChangeSetDaoImpl changeSetDao = new ChangeSetDaoImpl();

	/**
	 * Test query for count not contain order by clause.
	 */
	@Test
	public void testQueryForCountDoesNotContainOrderBy() {
		String queryString = changeSetDao.buildQueryString(new ChangeSetSearchCriteria(), Collections.emptyList(), true);
		assertThat(queryString)
			.as("query for counting should not contain order by clause")
		 	.doesNotContain("ORDER BY");
	}
	
	/**
	 * Test query contain order by clause.
	 */
	@Test
	public void testQueryForContainOrderBy() {
		ChangeSetSearchCriteria criteria = new ChangeSetSearchCriteria();
		criteria.setSortingOrder(SortOrder.ASCENDING);
		criteria.setSortingType(StandardSortBy.NAME);
		String queryString = changeSetDao.buildQueryString(criteria, Collections.emptyList(), false);
		assertThat(queryString)
				.as("query should contain order by clause")
				.contains("ORDER BY");
	}

	@Test
	public void testFindChangeSetExistsByNameAndStates() {
		//Given
		final List<ChangeSetStateCode> changeSetStateCodes = Arrays.asList(ChangeSetStateCode.FINALIZED, ChangeSetStateCode.OPEN);
		final String changeSetName = "SOME NAME";

		final PersistenceEngine persistenceEngine = mock(PersistenceEngine.class);

		changeSetDao.setPersistenceEngine(persistenceEngine);

		given(persistenceEngine.retrieveByNamedQuery(eq("FIND_CHANGE_SET_EXISTS_BY_STATE_AND_CHANGE_SET_NAME"),
				Matchers.anyMapOf(String.class, Object.class))).willReturn(Arrays.asList("SOME RESULT"));

		// When
		Boolean exists = changeSetDao.findChangeSetExistsByStateAndName(changeSetName, changeSetStateCodes);

		// Then
		assertThat(exists).isTrue();
	}

	@Test
	public void testFindChangeSetExistsByNameAndStatesDoesntExist() {
		//Given
		final List<ChangeSetStateCode> changeSetStateCodes = Arrays.asList(ChangeSetStateCode.FINALIZED, ChangeSetStateCode.OPEN);
		final String changeSetName = "SOME NAME";

		final PersistenceEngine persistenceEngine = mock(PersistenceEngine.class);

		changeSetDao.setPersistenceEngine(persistenceEngine);

		given(persistenceEngine.retrieveByNamedQuery(eq("FIND_CHANGE_SET_EXISTS_BY_STATE_AND_CHANGE_SET_NAME"),
				Matchers.anyMapOf(String.class, Object.class))).willReturn(Collections.emptyList());

		// When
		Boolean exists = changeSetDao.findChangeSetExistsByStateAndName(changeSetName, changeSetStateCodes);

		// Then
		assertThat(exists).isFalse();
	}

}
