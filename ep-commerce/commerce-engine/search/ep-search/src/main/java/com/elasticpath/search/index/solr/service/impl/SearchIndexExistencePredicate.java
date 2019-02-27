/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.search.index.solr.service.impl;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.store.FSDirectory;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.search.index.solr.service.SearchIndexLocator;
import com.elasticpath.service.search.IndexType;

/**
 * Predicate that uses the Solr SDK to identify the presence or absence of an index at a particular location.
 */
public class SearchIndexExistencePredicate implements Predicate<IndexType> {

	private static final String SOLR_INDEX_SUBDIRECTORY = "index";
	private static final long SOLR_INDEX_INITIAL_VERSION = 1L;

	private SearchIndexLocator searchIndexLocator;

	@Override
	public boolean test(final IndexType indexType) {
		final File searchIndexParentDirectory = getSearchIndexLocator().getSearchIndexLocation(indexType);
		final File actualIndexDirectory = getActualIndexDirectory(searchIndexParentDirectory);

		try (final IndexVersionNumberSupplier indexVersionNumberSupplier = createIndexVersionNumberSupplier(actualIndexDirectory)) {
			return indexVersionNumberSupplier.get() > SOLR_INDEX_INITIAL_VERSION;
		} catch (final IOException ioe) {
			throw new EpServiceException("Unable to determine the existence of search index for type [" + indexType + "]", ioe);
		}
	}

	/**
	 * Factory method for creating an {@link IndexVersionNumberSupplier} instance.
	 *
	 * @param solrIndexDirectory the directory that may contain a Solr index
	 * @return a supplier that provides the version number of the Solr index
	 * @throws IOException if the file cannot be read or other unexpected low-level I/O issue occurs
	 */
	protected IndexVersionNumberSupplier createIndexVersionNumberSupplier(final File solrIndexDirectory) throws IOException {
		return new IndexVersionNumberSupplier(solrIndexDirectory);
	}

	/**
	 * Confusingly, Solr creates an 'index' subdirectory within the directory configured to be the index directory.
	 *
	 * @param searchIndexLocation the directory that Solr was told to use as an index directory
	 * @return the actual directory that Solr uses to store the index
	 */
	protected File getActualIndexDirectory(final File searchIndexLocation) {
		return new File(searchIndexLocation, SOLR_INDEX_SUBDIRECTORY);
	}

	protected SearchIndexLocator getSearchIndexLocator() {
		return searchIndexLocator;
	}

	public void setSearchIndexLocator(final SearchIndexLocator searchIndexLocator) {
		this.searchIndexLocator = searchIndexLocator;
	}

	/**
	 * Retrieves the version number of a Solr index.
	 */
	protected static class IndexVersionNumberSupplier implements Supplier<Long>, Closeable {

		private final DirectoryReader directoryReader;

		/**
		 * Constructor.
		 *
		 * @param solrIndexDirectory the directory that may contain a Solr index
		 * @throws IOException if the file cannot be read or other unexpected low-level I/O issue occurs
		 */
		protected IndexVersionNumberSupplier(final File solrIndexDirectory) throws IOException {
			final FSDirectory directory = FSDirectory.open(solrIndexDirectory);

			this.directoryReader = DirectoryReader.open(directory);
		}

		@Override
		public Long get() {
			return directoryReader.getVersion();
		}

		@Override
		public void close() throws IOException {
			directoryReader.close();
		}

	}

}
