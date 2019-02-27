/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.service.search.solr;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.service.search.index.QueryComposer;
import com.elasticpath.service.search.query.SearchCriteria;

/**
 * Test case for {@link QueryComposerFactoryImpl}.
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
@RunWith(MockitoJUnitRunner.class)
public class QueryComposerFactoryImplTest {

	private QueryComposerFactoryImpl queryComposerFactory;

	@Before
	public void setUp() throws Exception {
		queryComposerFactory = new QueryComposerFactoryImpl();
	}
	
	/**
	 * Test method for {@link QueryComposerFactoryImpl#getComposerForCriteria(SearchCriteria)}.
	 */
	@Test
	public void testGetComposerForCriteria() {
		final SearchCriteria searchCriteria = mock(SearchCriteria.class, "SearchCriteria1");
		final SearchCriteria searchCriteria2 = mock(ExtendedSearchCriteria.class, "SearchCriteria2");

		final QueryComposer queryComposer = mock(QueryComposer.class, "Composer1");

		final Map<Class<? extends SearchCriteria>, QueryComposer> map = new HashMap<>();
		map.put(searchCriteria.getClass(), queryComposer);
		
		queryComposerFactory.setQueryComposerMappings(map);
		assertThat(queryComposerFactory.getComposerForCriteria(searchCriteria)).isSameAs(queryComposer);

		assertThatThrownBy(() -> queryComposerFactory.getComposerForCriteria(searchCriteria2))
			.isInstanceOf(EpSystemException.class)
			.hasMessageStartingWith("Unable to find composer mapping for class");
	}

	private interface ExtendedSearchCriteria extends SearchCriteria { };
}
