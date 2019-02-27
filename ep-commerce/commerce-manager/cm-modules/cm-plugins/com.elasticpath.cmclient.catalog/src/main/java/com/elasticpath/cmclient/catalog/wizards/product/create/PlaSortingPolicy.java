/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.catalog.wizards.product.create;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

import com.elasticpath.common.dto.pricing.PriceListAssignmentsDTO;

/**
 * Contains sorting utilities. 
 */
@SuppressWarnings({ "PMD.UseSingleton", "PMD.UseUtilityClass" })
public class PlaSortingPolicy {
	/**
	 * Compares PriceListAssignmentsDTOs by their price list names.
	 */
	public static final Comparator<PriceListAssignmentsDTO> PL_NAME_COMPARATOR = (pla1, pla2) -> pla1.getPriceListName()
			.compareToIgnoreCase(pla2.getPriceListName());

	/**
	 * Compares PriceListAssignmentsDTOs by their priorities.
	 */
	public static final Comparator<PriceListAssignmentsDTO> PLA_PRIORITY_COMPARATOR = (pla1, pla2) -> Integer.valueOf(pla1.getPriority())
			.compareTo(pla2.getPriority());
	
	
	/** from all PLAs for every pricelist we select that one that has lowest priority. So after preprocessing we will have the plas with the 
	 *    lowest priority for each pricelist. 
	 *
	 *    @param plas - list of price list assignments
	 *    @return list of price list assignments with lowest priorities
	 **/
	public static List<PriceListAssignmentsDTO> findLowestPriorityPlas(final List<PriceListAssignmentsDTO> plas) {
		Map<String, PriceListAssignmentsDTO> plaMap = new HashMap<>();
		for (PriceListAssignmentsDTO dto : plas) {
			if (plaMap.containsKey(dto.getPriceListName())) {
				if (plaMap.get(dto.getPriceListName()).getPriority() < dto.getPriority()) {
					plaMap.put(dto.getPriceListName(), dto);
				}
			} else {
				plaMap.put(dto.getPriceListName(), dto);
			}
		}
		return new ArrayList<>(plaMap.values());
	}
	
	/**
	 * Sorts the list alphanumerically.
	 * 
	 * @param plas PriceListAssignmentsDTO list
	 * @return sorted list
	 */
	public static List<PriceListAssignmentsDTO> sortAlpha(final List<PriceListAssignmentsDTO> plas) {
		Collections.sort(plas, PL_NAME_COMPARATOR);
		return plas;
	}

	/** 
	 * selecting the pricelist with the lowest priority and that is first alphanumerically if there are several of them. 
	 * 
	*    @param plas - list of price list assignments
	 *    @return index of lowest priority price list assignment
	 */
	public static int findLowestPriorityPlaIndex(final List<PriceListAssignmentsDTO> plas) {
		if (plas == null || plas.isEmpty()) {
			return -1; 
		}
		
		final int lowestPriority = Collections.max(plas, PLA_PRIORITY_COMPARATOR).getPriority();
		PriceListAssignmentsDTO dto = (PriceListAssignmentsDTO) CollectionUtils.find(plas, obj -> {
			PriceListAssignmentsDTO dto1 = (PriceListAssignmentsDTO) obj;
			return dto1.getPriority() == lowestPriority;
		});
		return plas.indexOf(dto);
	}		
}
