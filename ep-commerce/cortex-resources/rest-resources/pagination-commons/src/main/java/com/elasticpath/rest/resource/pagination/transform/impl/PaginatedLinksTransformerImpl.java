/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.pagination.transform.impl;

import java.util.ArrayList;
import java.util.Collection;

import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.elasticpath.rest.definition.collections.CollectionsMediaTypes;
import com.elasticpath.rest.definition.collections.PaginatedLinksEntity;
import com.elasticpath.rest.definition.collections.PaginationEntity;
import com.elasticpath.rest.definition.items.ItemsMediaTypes;
import com.elasticpath.rest.resource.dispatch.operator.annotation.PageNumber;
import com.elasticpath.rest.resource.pagination.constant.PaginationResourceConstants;
import com.elasticpath.rest.resource.pagination.integration.dto.PaginationDto;
import com.elasticpath.rest.resource.pagination.rel.PaginationResourceRels;
import com.elasticpath.rest.resource.pagination.transform.PaginatedLinksTransformer;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.uri.ItemsUriBuilderFactory;
import com.elasticpath.rest.schema.util.ElementListFactory;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Implementation of {@link PaginatedLinksTransformer}.
 */
@Component(property = Constants.SERVICE_RANKING + ":Integer=100")
public class PaginatedLinksTransformerImpl implements PaginatedLinksTransformer {

	private static final Logger LOG = LoggerFactory.getLogger(PaginatedLinksTransformerImpl.class);
	private static final int INDEX_LINKS_SIZE = 2;

	@Reference
	private ItemsUriBuilderFactory itemsUriBuilderFactory;


	/**
	 * Transforms a pagination DTO to a {@link ResourceState}.
	 *
	 * @param paginationDto the pagination DTO.
	 * @param scope the scope
	 * @param baseUri the base URI
	 * @return the {@link ResourceState}.
	 */
	public ResourceState<PaginatedLinksEntity> transformToResourceState(final PaginationDto paginationDto, final String scope, final String baseUri) {
		assert paginationDto != null : "The paginationDto cannot be null.";

		PaginationEntity paginationEntity = createPaginationEntity(paginationDto);
		Collection<ResourceLink> itemLinks = buildItemLinks(scope, paginationDto.getPageResults());
		Collection<ResourceLink> pageIndexLinks = buildPageIndexLinks(baseUri, paginationEntity);
		Self self = buildSelf(baseUri, paginationDto.getCurrentPage(), null);

		return createPaginatedLinksResourceState(paginationEntity, self, itemLinks, pageIndexLinks);
	}

	/**
	 * Creates pagination entity from the dto.
	 *
	 * @param paginationDto the dto to build the pagination entity from
	 * @return a pagination entity populated with data from the dto
	 */
	private PaginationEntity createPaginationEntity(final PaginationDto paginationDto) {
		return PaginationEntity.builder()
				.withCurrent(paginationDto.getCurrentPage())
				.withPageSize(paginationDto.getPageSize())
				.withResultsOnPage(paginationDto.getNumberOfResultsOnPage())
				.withPages(paginationDto.getNumberOfPages())
				.withResults(paginationDto.getTotalResultsFound())
				.build();
	}

	/**
	 * Builds the item links.
	 *
	 * @param scope the scope
	 * @param pageResults the page results
	 * @return the collection
	 */
	private Collection<ResourceLink> buildItemLinks(final String scope, final Collection<String> pageResults) {
		Collection<ResourceLink> itemLinks = new ArrayList<>(pageResults.size());
		LOG.debug("adding {} item links", pageResults.size());
		for (String itemId : pageResults) {
			itemLinks.add(buildItemLink(scope, itemId));
		}
		return itemLinks;
	}

	private ResourceLink buildItemLink(final String scope, final String itemId) {
		String resultUri = itemsUriBuilderFactory.get()
				.setItemId(itemId)
				.setScope(scope)
				.build();
		return ElementListFactory.createElement(resultUri, ItemsMediaTypes.ITEM.id());
	}

	/**
	 * Builds the self link including pagination information.
	 * <p/>
	 * For backwards compatibility for resources that changed from being non-paginated
	 * to paginated, we don't include the page info in the uri for the first page.
	 *
	 * @param baseUri base uri to use in the link
	 * @param page the current page
	 * @param type
	 * @return the self resource link (including pagination if needed)
	 */
	private Self buildSelf(final String baseUri, final int page, final String type) {
		String selfUri = page == PaginationResourceConstants.FIRST_PAGE
			? baseUri
			: URIUtil.format(baseUri, PageNumber.URI_PART, Integer.toString(page));

		LOG.debug("creating self uri {}", selfUri);
		return SelfFactory.createSelf(selfUri, type);
	}

	private Collection<ResourceLink> buildPageIndexLinks(final String baseUri, final PaginationEntity paginationEntity) {

		Collection<ResourceLink> result = new ArrayList<>(INDEX_LINKS_SIZE);

		if (paginationEntity.getPages() > PaginationResourceConstants.FIRST_PAGE) {
			int currentPage = paginationEntity.getCurrent();

			if (currentPage != PaginationResourceConstants.FIRST_PAGE) {
				String previousPageUri = URIUtil.format(baseUri, PageNumber.URI_PART, Integer.toString(currentPage - 1));
				ResourceLink previousPageLink = ResourceLinkFactory.createNoRev(previousPageUri, CollectionsMediaTypes.PAGINATED_LINKS.id(),
						PaginationResourceRels.PREVIOUS_PAGE_REL);
				LOG.debug("adding previous page link with uri {}", previousPageUri);
				result.add(previousPageLink);
			}

			if (currentPage != paginationEntity.getPages()) {
				String nextPageUri = URIUtil.format(baseUri, PageNumber.URI_PART, Integer.toString(currentPage + 1));
				ResourceLink nextPageLink =  ResourceLinkFactory.createNoRev(nextPageUri,
						CollectionsMediaTypes.PAGINATED_LINKS.id(),	PaginationResourceRels.NEXT_PAGE_REL);

				LOG.debug("adding next page link with uri {}", nextPageUri);
				result.add(nextPageLink);
			}
		}

		return result;
	}

	private ResourceState<PaginatedLinksEntity> createPaginatedLinksResourceState(final PaginationEntity paginationEntity,
			final Self self, final Collection<ResourceLink> itemLinks, final Collection<ResourceLink> pageIndexLinks) {
		PaginatedLinksEntity paginatedLinksEntity = PaginatedLinksEntity.builder()
				.withPagination(paginationEntity)
				.build();

		return ResourceState.Builder.create(paginatedLinksEntity)
				.withSelf(self)
				.addingLinks(itemLinks)
				.addingLinks(pageIndexLinks)
				.build();
	}
}
