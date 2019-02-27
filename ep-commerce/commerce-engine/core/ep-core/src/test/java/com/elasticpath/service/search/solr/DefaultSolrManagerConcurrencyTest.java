/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.service.search.solr;

import static org.junit.Assert.assertEquals;

import java.util.List;

import com.google.code.tempusfugit.concurrency.RepeatingRule;
import com.google.code.tempusfugit.concurrency.annotations.Repeating;
import org.apache.solr.client.solrj.SolrServer;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.jmock.lib.concurrent.Synchroniser;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.misc.SearchConfig;
import com.elasticpath.service.search.IndexType;
import com.elasticpath.service.search.SearchConfigFactory;
import com.elasticpath.test.concurrent.ConcurrencyTestUtils;
import com.elasticpath.test.concurrent.ConcurrencyTestUtils.RunnableFactory;

/**
 * Tests {@link DefaultSolrManager} against concurrency issues. As such, this test may spontaneously pass when it should
 * fail, but we've tried to run the tests enough times to show the problem.
 */
public class DefaultSolrManagerConcurrencyTest {

	@Rule
	public RepeatingRule repeatedly = new RepeatingRule();

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery() {
		{
			setThreadingPolicy(new Synchroniser());
		}
	};

	/**
	 * This number was chosen at random to exercise the concurrency issue. A pre-determined number can be invalidated by
	 * code changes in {@link DefaultSolrManager} itself.
	 */
	private static final int NUM_TEST_REPEATS = 101;
	private final SolrDocumentPublisherFactory solrDocumentPublisherFactory = context.mock(SolrDocumentPublisherFactory.class);
	private final SearchConfigFactory searchConfigFactory = context.mock(SearchConfigFactory.class);
	private final SearchConfig searchConfig = context.mock(SearchConfig.class);

	/** Test initialization. */
	@Before
	public void initialize() {
		context.checking(new Expectations() {
			{
				allowing(searchConfig).getSearchHost();
				will(returnValue("http://localhost/"));

				allowing(searchConfigFactory).getSearchConfig(with(any(String.class)));
				will(returnValue(searchConfig));
				allowing(searchConfigFactory);
				allowing(solrDocumentPublisherFactory);
			}
		});
	}

	/** Tests getting a server. */
	@Test
	@Repeating(repetition = NUM_TEST_REPEATS)
	public void getServer() {
		final DefaultSolrManager manager = createDefaultSolrManager();
		List<GetServerTestRunnable> runnables = ConcurrencyTestUtils.executeTest(new RunnableFactory<GetServerTestRunnable>() {
			@Override
			public GetServerTestRunnable createRunnable() {
				return new GetServerTestRunnable(manager);
			}
		});

		SolrServer firstServer = runnables.get(0).server;
		for (int i = 0; i < runnables.size(); ++i) {
			assertEquals(String.format("Test server[0] against server[%d] failed (all should be the same)", i), firstServer,
					runnables.get(i).server);
		}
	}

	/** {@link Runnable} for {@link DefaultSolrManagerConcurrencyTest#getServer()}. */
	private static class GetServerTestRunnable implements Runnable {
		private SolrServer server;
		private final SolrProvider provider;

		GetServerTestRunnable(final SolrProvider provider) {
			this.provider = provider;
		}

		@Override
		public void run() {
			server = provider.getServer(IndexType.PRODUCT);
		}
	}

	private DefaultSolrManager createDefaultSolrManager() {
		final DefaultSolrManager manager = new DefaultSolrManager();
		manager.setSearchConfigFactory(searchConfigFactory);
		manager.setSolrDocumentPublisherFactory(solrDocumentPublisherFactory);
		return manager;
	}
}
