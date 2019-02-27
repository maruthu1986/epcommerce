/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.pagination.transform;

import com.elasticpath.rest.definition.collections.PaginatedLinksEntity;
import com.elasticpath.rest.resource.pagination.integration.dto.PaginationDto;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Transforms a {@link PaginationDto} into a {@link PaginatedLinksEntity}.
 */
public interface PaginatedLinksTransformer {

	/**
	 * Transforms a pagination DTO to a {@link ResourceState}.
	 *
	 * @param paginationDto the pagination DTO.
	 * @param scope the scope
	 * @param baseUri the base URI
	 * @return the {@link ResourceState}.
	 */
	ResourceState<PaginatedLinksEntity> transformToResourceState(PaginationDto paginationDto, String scope, String baseUri);
}
