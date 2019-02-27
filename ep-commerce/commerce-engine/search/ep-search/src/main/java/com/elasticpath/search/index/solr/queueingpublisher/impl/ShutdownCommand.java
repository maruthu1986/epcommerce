/**
 * Copyright (c) Elastic Path Software Inc., 2011
 */
package com.elasticpath.search.index.solr.queueingpublisher.impl;

import org.apache.solr.client.solrj.SolrServer;

/**
 * Shut down command for the {@code QueueingSolrDocumentPublisher}.
 */
public class ShutdownCommand implements SolrPublishCommand {

	/**
	 * Applies the command to the specified {@link SolrServer}.
	 * 
	 * @param server the specified {@link SolrServer}.
	 * @throws InterruptedException which the will shutdown the thread.
	 * @see QueueingSolrDocumentPublisher
	 */
	@Override
	public void apply(final SolrServer server) throws InterruptedException {
		throw new InterruptedException();
	}

}
