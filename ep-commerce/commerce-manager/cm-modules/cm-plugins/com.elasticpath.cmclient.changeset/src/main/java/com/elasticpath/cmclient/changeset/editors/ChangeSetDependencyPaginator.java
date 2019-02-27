/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.changeset.editors;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.elasticpath.common.dto.ChangeSetDependencyDto;
import com.elasticpath.commons.pagination.DirectedSortingField;
import com.elasticpath.commons.pagination.Page;
import com.elasticpath.commons.pagination.PaginationConfig;
import com.elasticpath.commons.pagination.PaginatorImpl;
import com.elasticpath.commons.pagination.SortingDirection;
import com.elasticpath.commons.pagination.SortingField;

/**
 * The paginator class for change set dependency.
 */
public class ChangeSetDependencyPaginator extends PaginatorImpl<ChangeSetDependencyDto> {
	
	private final List<ChangeSetDependencyDto> changeSetDependencies; 

	/**
	 * The constructor.
	 * 
	 * @param changeSetDependencies the change set dependencies
	 */
	public ChangeSetDependencyPaginator(final List<ChangeSetDependencyDto> changeSetDependencies) {
		this.changeSetDependencies = changeSetDependencies;
	}
	
	/**
	 * The init method.
	 * It sets the pagination config which contains page size and sorting field and direction.
	 * sort the collection after the sorting field and direction is set 
	 * 
	 * @param paginationConfig the pagination config
	 */
	@Override
	public void init(final PaginationConfig paginationConfig) {
		super.init(paginationConfig);
		Collections.sort(changeSetDependencies, 
				new ChangeSetDependencyComparator(paginationConfig.getSortingFields()[0].getSortingField()));
	}
	
	@Override
	public long getTotalItems() {
		return this.changeSetDependencies.size();
	}
	
	@Override
	protected List<ChangeSetDependencyDto> findItems(final Page<ChangeSetDependencyDto> unpopulatedPage) {
		int startIndex = unpopulatedPage.getPageStartIndex() - 1;
		int toIndex = startIndex + unpopulatedPage.getPageSize();
		
		//For the last page, the toIndex may be greater than the total records number
		//pick up the minimum number  
		toIndex = Math.min(toIndex, changeSetDependencies.size());
		return changeSetDependencies.subList(startIndex, toIndex);
	}

	/**
	 * Set the sorting field. 
	 * If it is the same sorting field, it reverses the sorting 
	 * 
	 * @param newSortingField the new sorting field
	 */
	public void setSortingField(final SortingField newSortingField) {
		DirectedSortingField[] sortingFields = getSortingFields();
		SortingDirection newSortingDirection = SortingDirection.ASCENDING;
		if (sortingFields[0].getSortingField().equals(newSortingField)) {
			//sort on the same column means reverse the sorting...
			Collections.reverse(changeSetDependencies);
			newSortingDirection = reverseSortingDirection(sortingFields[0].getSortingDirection());
		} else {
			//sort on the sorting filed ascending
			Collections.sort(changeSetDependencies, new ChangeSetDependencyComparator(newSortingField));
		}
		DirectedSortingField directedSortingField = new DirectedSortingField(newSortingField, newSortingDirection);
		this.setSortingFields(directedSortingField);
	}

	private SortingDirection reverseSortingDirection(final SortingDirection sortingDirection) {
		SortingDirection newSortingDirection;
		if (sortingDirection.equals(SortingDirection.ASCENDING)) {
			newSortingDirection = SortingDirection.DESCENDING;
		} else {
			newSortingDirection = SortingDirection.ASCENDING;
		}
		return newSortingDirection;
	}

	/**
	 * The change set dependency comparator.
	 */
	class ChangeSetDependencyComparator implements Comparator<ChangeSetDependencyDto>, Serializable {

		private static final long serialVersionUID = 1L;
		private final SortingField sortingField;

		/**
		 * The constructor.
		 * 
		 * @param newSortingField the new sorting field
		 */
		ChangeSetDependencyComparator(final SortingField newSortingField) {
			this.sortingField = newSortingField;
		}

		@Override
		public int compare(final ChangeSetDependencyDto dto1, final ChangeSetDependencyDto dto2) {
			return getValue(dto1, sortingField).compareTo(getValue(dto2, sortingField));
		}
	}

	/**
	 * Get value for change set dependency dto for the sorting field.
	 * 
	 * @param dto the change set dependency dto.
	 * @param sortingField the sorting field.
	 * @return the value
	 */
	protected String getValue(final ChangeSetDependencyDto dto, final SortingField sortingField) {
		if (sortingField.equals(ChangeSetDependencySortingField.SOURCE_OBJECT_NAME)) {
			return dto.getSourceObjectName();
		}
		
		if (sortingField.equals(ChangeSetDependencySortingField.SOURCE_OBJECT_TYPE)) {
			return dto.getSourceObjectType();
		}
		
		if (sortingField.equals(ChangeSetDependencySortingField.DEPENDENCY_OBJECT_NAME)) {
			return dto.getDependencyObjectName();
		}
		
		if (sortingField.equals(ChangeSetDependencySortingField.DEPENDENCY_OBJECT_TYPE)) {
			return dto.getDependencyObjectType();
		}
		
		if (sortingField.equals(ChangeSetDependencySortingField.CHANGE_SET_NAME)) {
			return dto.getDependencyChangeSetName();
		}
		
		return null;
	}
}
