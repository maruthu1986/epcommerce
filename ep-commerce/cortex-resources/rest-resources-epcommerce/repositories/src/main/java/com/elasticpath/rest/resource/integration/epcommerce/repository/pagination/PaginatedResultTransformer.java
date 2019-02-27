/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.pagination;

import java.util.Collection;
import java.util.Locale;

import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.ResourceTypeFactory;
import com.elasticpath.rest.resource.pagination.integration.dto.PaginationDto;
import com.elasticpath.rest.resource.transform.AbstractDomainTransformer;

/**
 * Transforms {@link PaginatedResult} into a {@link PaginationDto}, and vice versa.
 */
@Singleton
@Named("paginatedResultTransformer")
public class PaginatedResultTransformer extends AbstractDomainTransformer<PaginatedResult, PaginationDto> {

	@Override
	public PaginatedResult transformToDomain(final PaginationDto paginationDto, final Locale locale) {
		throw new UnsupportedOperationException("This operation is not implemented.");
	}

	@Override
	public PaginationDto transformToEntity(final PaginatedResult paginatedResult, final Locale locale) {
		Collection<String> resultIds = paginatedResult.getResultIds();
		return ResourceTypeFactory.createResourceEntity(PaginationDto.class)
				.setCurrentPage(paginatedResult.getCurrentPage())
				.setNumberOfPages(paginatedResult.getNumberOfPages())
				.setTotalResultsFound(paginatedResult.getTotalNumberOfResults())
				.setNumberOfResultsOnPage(resultIds.size())
				.setPageSize(paginatedResult.getResultsPerPage())
				.setPageResults(resultIds);
	}
}
