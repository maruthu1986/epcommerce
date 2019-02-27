/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.commons.pagination;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;

import com.elasticpath.commons.pagination.impl.PageImpl;
import com.elasticpath.domain.impl.AbstractEpDomainImpl;
import com.elasticpath.persistence.api.LoadTuner;

/**
 * Base paginator implementation. Requires a PaginatorLocator to be set to allow an interface to
 * the database or other source.
 *
 * @param <T> the class this paginator works with
 */
@SuppressWarnings("PMD.GodClass")
public class PaginatorImpl<T> extends AbstractEpDomainImpl implements Paginator<T> {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	private int pageSize;

	private PageImpl<T> currentPage;

	private DirectedSortingField[] sortingFields;

	private String objectId;

	private LoadTuner loadTuner;

	private PaginatorLocator<T> paginatorLocator;

	private long totalItems;

	/**
	 * @return the objectId
	 */
	public String getObjectId() {
		return objectId;
	}

	/**
	 * @param paginationConfig the pagination config
	 */
	@Override
	public void init(final PaginationConfig paginationConfig) {
		if (paginationConfig == null) {
			throw new IllegalArgumentException("Pagination config cannot be null.");
		}
		if (paginationConfig.getPageSize() < 1) {
			throw new IllegalArgumentException("Page size should be greater or equal to one.");
		}
		if (paginationConfig.getObjectId() == null) {
			throw new IllegalArgumentException("Object ID cannot be null.");
		}
		if (ArrayUtils.isEmpty(paginationConfig.getSortingFields())) {
			throw new IllegalArgumentException("At least one sorting field is required.");
		}

		this.pageSize = paginationConfig.getPageSize();
		this.sortingFields = paginationConfig.getSortingFields();
		this.objectId = paginationConfig.getObjectId();
		this.loadTuner = paginationConfig.getLoadTuner();
	}

	/**
	 *
	 * @return the loadTuner the load tuner
	 */
	public LoadTuner getLoadTuner() {
		return loadTuner;
	}

	/**
	 * Gets the page with the given page number and page size, ordered by the ordering field. If the page number is not available than the last
	 * available page will be returned (e.g. if only 2 pages are available and page number 5 is requested, page number 2 will be returned).
	 *
	 * @param page the page
	 * @return a new instance of the page
	 */
	protected Page<T> getPage(final PageImpl<T> page) {

		page.limitToLastPage(getTotalPages());

		List<T> items = Collections.emptyList();
		if (getTotalItems() > 0) {
			items = findItems(page);
		}
		if (items == null) {
			items = Collections.emptyList();
		}
		currentPage = createNewPage(page, items);

		this.totalItems = 0;

		return currentPage;
	}

	/**
	 * @return the total available pages for type T
	 */
	@Override
	public int getTotalPages() {
		double pages = (double) getTotalItems() / (double) getPageSize();
		return Math.max(1, (int) Math.ceil(pages));
	}

	/**
	 * Creates a new page with the given parameters.
	 *
	 * @param page the page
	 * @param items the total items
	 * @return a new page
	 */
	protected PageImpl<T> createNewPage(final PageImpl<T> page, final List<T> items) {
		return new PageImpl<>(items, this, page);
	}

	/**
	 * @return the current page size
	 */
	@Override
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * @return the first page
	 */
	@Override
	public Page<T> first() {
		return getPage(new PageImpl<>(this, 1, pageSize, sortingFields));
	}

	/**
	 * @return the last page
	 */
	@Override
	public Page<T> last() {
		return getPage(new PageImpl<>(this, getTotalPages(), pageSize, sortingFields));
	}

	@Override
	public Page<T> getPage(final int pageNumber) {
		int page = pageNumber;
		if (page < 1) {
			page = 1;
		}
		if (page > getTotalPages()) {
			page = getTotalPages();
		}
		return getPage(new PageImpl<>(this, page, pageSize, sortingFields));
	}

	/**
	 * @return the next page
	 */
	@Override
	public Page<T> next() {
		return getPage(((PageImpl<T>) getCurrentPage()).next());
	}

	/**
	 * @return the previous page
	 */
	@Override
	public Page<T> previous() {
		return getPage(((PageImpl<T>) getCurrentPage()).previous());
	}

	/**
	 * @return the current page
	 */
	@Override
	public Page<T> getCurrentPage() {
		if (currentPage == null) {
			first();
		}
		return currentPage;
	}

	/**
	 * Get the sorting fields.
	 *
	 * @return the sorting fields
	 */
	@Override
	public DirectedSortingField[] getSortingFields() {
		return this.sortingFields.clone();
	}

	/**
	 * Sets the sorting fields to allow different sorting.
	 * @param sortingFields the new fields
	 */
	@Override
	public void setSortingFields(final DirectedSortingField... sortingFields) {
		this.sortingFields = sortingFields;
	}

	/**
	 * Finds elements with the specified criteria.
	 *
	 * @param unpopulatedPage the page to be returned
	 * @return the elements found for the specified criteria. Must not return null.
	 */
	protected List<T> findItems(final Page<T> unpopulatedPage) {
		return paginatorLocator.findItems(unpopulatedPage, getObjectId());
	}

	/**
	 *
	 * @return the total items of type T
	 */
	@Override
	public long getTotalItems() {
		if (this.totalItems == 0) {
			this.totalItems = paginatorLocator.getTotalItems(getObjectId());
		}
		return this.totalItems;
	}

	/**
	 * @param paginatorLocator the paginatorLocator to set.
	 */
	@Override
	public void setPaginatorLocator(final PaginatorLocator<T> paginatorLocator) {
		this.paginatorLocator = paginatorLocator;
	}

	/**
	 * @return the paginator locator.
	 */
	@Override
	public PaginatorLocator<T> getPaginatorLocator() {
		return paginatorLocator;
	}

	@Override
	public void refreshCurrentPage() {
		getPage((PageImpl<T>) getCurrentPage());
	}

}
