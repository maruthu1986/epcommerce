/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.taxdocument.impl;

import java.util.Collection;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import io.reactivex.Single;

import com.elasticpath.domain.order.TaxJournalRecord;
import com.elasticpath.plugin.tax.domain.TaxDocumentId;
import com.elasticpath.rest.cache.CacheResult;
import com.elasticpath.rest.resource.integration.epcommerce.repository.taxdocument.TaxDocumentRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.ReactiveAdapter;
import com.elasticpath.service.tax.TaxDocumentService;

/**
 * Default implementation of {@link TaxDocumentRepository}.
 */
@Singleton
@Named("taxDocumentRepository")
public class TaxDocumentRepositoryImpl implements TaxDocumentRepository {

	/**
	 * Message for document not found.
	 */
	static final String DOCUMENT_NOT_FOUND_MESSAGE = "No tax document was found.";

	private final TaxDocumentService taxDocumentService;
	private final ReactiveAdapter reactiveAdapter;

	/**
	 * Constructor. 
	 * 
	 * @param taxDocumentService a {@link TaxDocumentService}
	 * @param reactiveAdapter reactive adapter
	 *
	 */
	@Inject
	public TaxDocumentRepositoryImpl(
			@Named("taxDocumentService") final TaxDocumentService taxDocumentService,
			@Named("reactiveAdapter") final ReactiveAdapter reactiveAdapter) {

		this.taxDocumentService = taxDocumentService;
		this.reactiveAdapter = reactiveAdapter;
	}

	@Override
	@CacheResult
	public Single<Collection<TaxJournalRecord>> getTaxDocument(final TaxDocumentId taxDocumentId, final String itemTaxCode) {
		return reactiveAdapter.fromServiceAsSingle(() -> taxDocumentService.find(taxDocumentId, itemTaxCode), DOCUMENT_NOT_FOUND_MESSAGE);
	}
}
