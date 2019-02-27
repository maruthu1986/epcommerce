/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.search.index.solr.service.impl;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrServer;
import org.jmock.Expectations;
import org.jmock.Sequence;
import org.jmock.api.Invocation;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.jmock.lib.action.CustomAction;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.search.IndexBuildStatus;
import com.elasticpath.domain.search.IndexNotification;
import com.elasticpath.domain.search.IndexStatus;
import com.elasticpath.domain.search.UpdateType;
import com.elasticpath.domain.search.impl.IndexBuildStatusImpl;
import com.elasticpath.persistence.dao.IndexBuildStatusDao;
import com.elasticpath.search.IndexNotificationProcessor;
import com.elasticpath.search.index.pipeline.impl.IndexBuildStatusUpdater;
import com.elasticpath.search.index.pipeline.stats.impl.IndexingStatsImpl;
import com.elasticpath.search.index.solr.builders.IndexBuilder;
import com.elasticpath.search.index.solr.builders.impl.AbstractIndexBuilder;
import com.elasticpath.search.index.solr.builders.impl.IndexBuilderFactoryImpl;
import com.elasticpath.search.index.solr.service.IndexBuildPolicy;
import com.elasticpath.search.index.solr.service.IndexBuildPolicyContext;
import com.elasticpath.search.index.solr.service.IndexBuildPolicyContextFactory;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.service.search.IndexType;
import com.elasticpath.service.search.solr.SolrManager;

/**
 * Test <code>IndexBuildServiceImpl</code>.
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
public class IndexBuildServiceImplTest {

	/**
	 * Represents a bad uid.
	 */
	protected static final long BAD_UID = 1234L;

	private static final String ALL_SCHEDULED_DELETES_MUST_BE_FLUSHED = "All scheduled deletes must be flushed.";

	private static final String ALL_SCHEDULED_DOCUMENT_CHANGES_MUST_BE_FLUSHED = "All scheduled document changes must be flushed.";

	private IndexBuildServiceImpl indexBuildService;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery() {
		{
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};

	private IndexBuildStatusDao mockIndexBuildStatusDao;

	private TimeService mockTimeService;

	private SolrManager mockSolrManager;

	private SolrServer mockSolrServer;

	private IndexNotificationProcessor mockIndexNotificationProcessor;

	private final IndexType indexType = IndexType.PRODUCT;

	private IndexBuilder indexBuilder;

	private IndexBuildPolicy mockIndexBuildPolicy;

	private IndexBuildPolicyContextFactory mockIndexBuildPolicyFactory;

	/**
	 * Setup test.
	 *
	 * @throws Exception on error
	 */
	@Before
	public void setUp() throws Exception {
		indexBuildService = new IndexBuildServiceImpl();

		indexBuildService.setIndexBuildStatusUpdater(new IndexBuildStatusUpdater());
		indexBuilder = mockIndexBuilder();
		final IndexBuilderFactoryImpl factoryImpl = new IndexBuilderFactoryImpl();
		final Map<String, IndexBuilder> builderMap = new HashMap<>();
		builderMap.put(indexType.getIndexName(), indexBuilder);
		factoryImpl.setIndexBuilderMap(builderMap);
		indexBuildService.setIndexBuilderFactory(factoryImpl);

		mockIndexBuildStatusDao = context.mock(IndexBuildStatusDao.class);

		indexBuildService.setIndexBuildStatusDao(mockIndexBuildStatusDao);

		mockSolrManager = context.mock(SolrManager.class);
		indexBuildService.setSolrManager(mockSolrManager);

		mockSolrServer = context.mock(SolrServer.class);

		mockTimeService = context.mock(TimeService.class);
		context.checking(new Expectations() {
			{
				allowing(mockTimeService).getCurrentTime();
				will(returnValue(new Date()));
			}
		});
		indexBuildService.setTimeService(mockTimeService);

		mockIndexBuildPolicy = context.mock(IndexBuildPolicy.class);
		indexBuildService.setIndexBuildPolicy(mockIndexBuildPolicy);
		mockIndexNotificationProcessor = context.mock(IndexNotificationProcessor.class);
		
		indexBuildService.setIndexingStatistics(new IndexingStatsImpl());

		mockIndexBuildPolicyFactory = context.mock(IndexBuildPolicyContextFactory.class);
		context.checking(new Expectations() {
			{
				allowing(mockIndexBuildPolicyFactory).createIndexBuildPolicyContext();
				will(new CustomAction("Creates new IndexBuildPolicyContext instances") {
					@Override
					public Object invoke(final Invocation invocation) throws Throwable {
						return new IndexBuildPolicyContext();
					}
				});
			}
		});
		indexBuildService.setIndexBuildPolicyContextFactory(mockIndexBuildPolicyFactory);
		indexBuildService.setSearchIndexExistencePredicate(indexType -> true);
	}

	private IndexBuilder mockIndexBuilder() { // NOPMD
		return new AbstractIndexBuilder() {
			private List<Long> deletedUids;

			private List<Long> addedOrModifiedUids;

			private List<Long> allUids;

			@Override
			public String getName() {
				return "testIndexBuildService";
			}

			@Override
			public List<Long> findDeletedUids(final Date lastBuildDate) {
				if (deletedUids == null) {
					deletedUids = new ArrayList<>();
					deletedUids.add(new Long("1"));
				}
				return deletedUids;
			}

			@Override
			public List<Long> findAddedOrModifiedUids(final Date lastBuildDate) {
				if (addedOrModifiedUids == null) {
					addedOrModifiedUids = new ArrayList<>();
					addedOrModifiedUids.add(new Long("2"));
					addedOrModifiedUids.add(new Long("3"));
					addedOrModifiedUids.add(new Long(BAD_UID));
				}
				return addedOrModifiedUids;
			}

			@Override
			public List<Long> findAllUids() {
				if (this.allUids == null) {
					allUids = new ArrayList<>();
					allUids.addAll(this.findAddedOrModifiedUids(null));
					allUids.add(new Long("4"));
				}
				return addedOrModifiedUids;
			}

			@Override
			public IndexType getIndexType() {
				return indexType;
			}

			@Override
			public Collection<Long> findUidsByNotification(final IndexNotification notification) {
				return new HashSet<>();
			}

			@Override
			public IndexNotificationProcessor getIndexNotificationProcessor() {
				return mockIndexNotificationProcessor;
			}

			@Override
			public void submit(final Collection<Long> uids) {
				// TODO Auto-generated method stub
				
			}
		};
	}

	/**
	 * Test method for 'com.elasticpath.service.index.impl.AbstractIndexBuildServiceImpl.buildIndex()'.
	 */
	@Test
	public void testBuildIndex() {
		final IndexBuildStatus buildIndexStatus = new IndexBuildStatusImpl();
		final TestSolrDocumentPublisher publisher = new TestSolrDocumentPublisher();

		buildIndexStatus.setLastBuildDate(new Date());
		buildIndexStatus.setIndexType(IndexType.PRODUCT);
		context.checking(new Expectations() {
			{
				oneOf(mockIndexBuildStatusDao).get(indexType);
				will(returnValue(buildIndexStatus));
				allowing(mockIndexBuildStatusDao).saveOrUpdate(with(any(IndexBuildStatus.class)));
				will(returnValue(buildIndexStatus));
			}
		});

		final IndexNotification mockIndexNot1 = context.mock(IndexNotification.class);
		context.checking(new Expectations() {
			{
				allowing(mockIndexNot1).getUpdateType();
				allowing(mockIndexNot1).getAffectedEntityType();
				will(returnValue("some value"));
			}
		});
		final IndexNotification mockIndexNot2 = context.mock(IndexNotification.class, "second index notification");
		context.checking(new Expectations() {
			{
				allowing(mockIndexNot2).getUpdateType();
				allowing(mockIndexNot2).getAffectedEntityType();
				will(returnValue("another value"));
			}
		});
		final List<IndexNotification> indexNotificationList = Arrays.asList(mockIndexNot1, mockIndexNot2);
		context.checking(new Expectations() {
			{
				Sequence sequence = context.sequence("index notification sequence");
				oneOf(mockIndexNotificationProcessor).findAllNewNotifications(indexType);
				will(returnValue(indexNotificationList));
				inSequence(sequence);
				allowing(mockIndexNotificationProcessor).getNotifications();
				will(returnValue(indexNotificationList));
				inSequence(sequence);
				oneOf(mockIndexNotificationProcessor).removeStoredNotifications();

				oneOf(mockSolrManager).getServer(with(any(IndexType.class)));
				will(returnValue(mockSolrServer));
				atLeast(1).of(mockSolrManager).getDocumentPublisher(with(any(IndexType.class)));
				will(returnValue(publisher));

			}
		});

		indexBuildService.setSearchIndexExistencePredicate(indexType -> true);

		indexBuildService.buildIndex(indexType);
		assertTrue(ALL_SCHEDULED_DELETES_MUST_BE_FLUSHED, publisher.getDeleted().isEmpty());
		assertTrue(ALL_SCHEDULED_DOCUMENT_CHANGES_MUST_BE_FLUSHED, publisher.getUpdated().isEmpty());

	}

	/**
	 * Tests that the rebuild will be invoked if there is a REBUILD update type in the list of notifications.
	 */
	@Test
	public void testRebuildIndex() throws Exception {
		final IndexBuildStatus buildIndexStatus = new IndexBuildStatusImpl();
		final TestSolrDocumentPublisher publisher = new TestSolrDocumentPublisher();
		buildIndexStatus.setLastBuildDate(new Date());
		buildIndexStatus.setIndexType(IndexType.PRODUCT);

		final IndexNotification mockIndexNot1 = context.mock(IndexNotification.class);
		final IndexNotification mockIndexNot2 = context.mock(IndexNotification.class, "second index notification");
		context.checking(new Expectations() {
			{
				oneOf(mockIndexBuildStatusDao).get(indexType);
				will(returnValue(buildIndexStatus));
				allowing(mockIndexBuildStatusDao).saveOrUpdate(with(any(IndexBuildStatus.class)));
				will(returnValue(buildIndexStatus));

				allowing(mockIndexNot1).getUpdateType();
				allowing(mockIndexNot1).getAffectedEntityType();
				will(returnValue("some value"));
				// set one of the index notifications to be of type REBUILD which should trigger the full rebuild on this index
				allowing(mockIndexNot2).getUpdateType();
				will(returnValue(UpdateType.REBUILD));
				allowing(mockIndexNot2).getAffectedEntityType();
				will(returnValue("another value"));
				Sequence sequence = context.sequence("index notification sequence");
				final List<IndexNotification> indexNotificationList = Arrays.asList(mockIndexNot1, mockIndexNot2);
				allowing(mockIndexNotificationProcessor).findAllNewNotifications(indexType);
				will(returnValue(indexNotificationList));
				inSequence(sequence);
				allowing(mockIndexNotificationProcessor).getNotifications();
				will(returnValue(indexNotificationList));
				inSequence(sequence);
				oneOf(mockIndexNotificationProcessor).removeStoredNotifications();

				oneOf(mockSolrServer).deleteByQuery("*:*");

				oneOf(mockSolrManager).getServer(with(any(IndexType.class)));
				will(returnValue(mockSolrServer));

				atLeast(1).of(mockSolrManager).getDocumentPublisher(with(any(IndexType.class)));
				will(returnValue(publisher));
			}
		});

		indexBuildService.buildIndex(indexType);
		assertTrue(ALL_SCHEDULED_DELETES_MUST_BE_FLUSHED, publisher.getDeleted().isEmpty());
		assertTrue(ALL_SCHEDULED_DOCUMENT_CHANGES_MUST_BE_FLUSHED, publisher.getUpdated().isEmpty());

	}

	/**
	 * Tests that the index service will trigger a rebuild if the index build status signifies that the server was last left in IndexStatus#.
	 */
	@Test
	public void testRebuildIndexWhenStatusInProgress() throws Exception {

		final TestSolrDocumentPublisher publisher = new TestSolrDocumentPublisher();
		final IndexBuildStatus buildIndexStatus = new IndexBuildStatusImpl();
		buildIndexStatus.setLastBuildDate(new Date());
		buildIndexStatus.setIndexType(IndexType.PRODUCT);

		buildIndexStatus.setIndexStatus(IndexStatus.REBUILD_IN_PROGRESS);
		context.checking(new Expectations() {
			{
				oneOf(mockIndexBuildStatusDao).get(indexType);
				will(returnValue(buildIndexStatus));
				allowing(mockIndexBuildStatusDao).saveOrUpdate(with(any(IndexBuildStatus.class)));
				will(returnValue(buildIndexStatus));

				oneOf(mockIndexNotificationProcessor).removeStoredNotifications();

				oneOf(mockIndexNotificationProcessor).findAllNewNotifications(indexType);
				will(returnValue(Collections.emptyList()));

				oneOf(mockSolrServer).deleteByQuery("*:*");

				oneOf(mockSolrManager).getServer(with(any(IndexType.class)));
				will(returnValue(mockSolrServer));
				atLeast(1).of(mockSolrManager).getDocumentPublisher(with(any(IndexType.class)));
				will(returnValue(publisher));
			}
		});

		indexBuildService.buildIndex(indexType);
		assertTrue(ALL_SCHEDULED_DELETES_MUST_BE_FLUSHED, publisher.getDeleted().isEmpty());
		assertTrue(ALL_SCHEDULED_DOCUMENT_CHANGES_MUST_BE_FLUSHED, publisher.getUpdated().isEmpty());
	}

	@Test
	public void testRebuildIndexWhenSolrHomeDirectoryNew() throws Exception {
		final TestSolrDocumentPublisher publisher = new TestSolrDocumentPublisher();
		final IndexBuildStatus buildIndexStatus = new IndexBuildStatusImpl();
		buildIndexStatus.setLastBuildDate(new Date());
		buildIndexStatus.setIndexType(IndexType.PRODUCT);

		context.checking(new Expectations() {
			{
				oneOf(mockIndexBuildStatusDao).get(indexType);
				will(returnValue(buildIndexStatus));
				allowing(mockIndexBuildStatusDao).saveOrUpdate(with(any(IndexBuildStatus.class)));
				will(returnValue(buildIndexStatus));

				oneOf(mockIndexNotificationProcessor).removeStoredNotifications();

				allowing(mockIndexNotificationProcessor).findAllNewNotifications(indexType);
				will(returnValue(Collections.emptyList()));
				allowing(mockIndexNotificationProcessor).getNotifications();
				will(returnValue(Collections.emptyList()));

				oneOf(mockSolrServer).deleteByQuery("*:*");

				oneOf(mockSolrManager).getServer(with(any(IndexType.class)));
				will(returnValue(mockSolrServer));
				atLeast(1).of(mockSolrManager).getDocumentPublisher(with(any(IndexType.class)));
				will(returnValue(publisher));
			}
		});

		indexBuildService.setSearchIndexExistencePredicate(indexType -> false);

		indexBuildService.buildIndex(indexType);
		assertTrue(ALL_SCHEDULED_DELETES_MUST_BE_FLUSHED, publisher.getDeleted().isEmpty());
		assertTrue(ALL_SCHEDULED_DOCUMENT_CHANGES_MUST_BE_FLUSHED, publisher.getUpdated().isEmpty());
	}

}
