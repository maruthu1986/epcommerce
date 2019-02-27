/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.taxdocument.impl;

import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.order.TaxJournalRecord;
import com.elasticpath.plugin.tax.domain.TaxDocumentId;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.impl.ReactiveAdapterImpl;
import com.elasticpath.service.tax.TaxDocumentService;

/**
 * Tests for {@link TaxDocumentRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class TaxDocumentRepositoryImplTest {

	private static final String TAX_ITEM_CODE = "testTaxItemCode";

	@Mock private TaxDocumentService taxDocumentService;
	@Mock private TaxDocumentId taxDocumentId;
	@Mock private List<TaxJournalRecord> records;
	@InjectMocks private ReactiveAdapterImpl reactiveAdapter;

	private TaxDocumentRepositoryImpl taxDocumentRepositoryImpl;

	@Before
	public void setup() {
		taxDocumentRepositoryImpl = new TaxDocumentRepositoryImpl(taxDocumentService, reactiveAdapter);
	}

	/**
	 * Test {@link TaxDocumentRepositoryImpl#getTaxDocument} success case.
	 */
	@Test
	public void testGetTaxDocumentSuccess() {
		when(taxDocumentService.find(taxDocumentId, TAX_ITEM_CODE)).thenReturn(records);

		taxDocumentRepositoryImpl.getTaxDocument(taxDocumentId, TAX_ITEM_CODE)
				.test()
				.assertValue(records);
	}

	/**
	 * Test {@link TaxDocumentRepositoryImpl#getTaxDocument} case when the call to {@link TaxDocumentService} returns null.
	 */
	@Test
	public void testGetTaxDocumentWithServiceReturningNull() {
		when(taxDocumentService.find(taxDocumentId, TAX_ITEM_CODE)).thenReturn(null);

		taxDocumentRepositoryImpl.getTaxDocument(taxDocumentId, TAX_ITEM_CODE)
				.test()
				.assertError(throwable -> {
					ResourceOperationFailure failure = (ResourceOperationFailure) throwable;

					boolean messageCheck = failure.getLocalizedMessage().equals(TaxDocumentRepositoryImpl.DOCUMENT_NOT_FOUND_MESSAGE);
					boolean statusCheck = failure.getResourceStatus().equals(ResourceStatus.NOT_FOUND);

					return messageCheck && statusCheck;
				});
	}
}
