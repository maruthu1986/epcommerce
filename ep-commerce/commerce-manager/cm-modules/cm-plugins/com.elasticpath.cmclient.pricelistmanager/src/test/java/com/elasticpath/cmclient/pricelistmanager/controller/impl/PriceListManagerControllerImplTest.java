/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
/**
 * 
 */
package com.elasticpath.cmclient.pricelistmanager.controller.impl;

import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import com.elasticpath.cmclient.pricelistmanager.model.PriceListManagerSearchResultsModel;
import com.elasticpath.common.dto.pricing.PriceListDescriptorDTO;


/**
 * Tests for the PriceListManagerController class.
 */
public class PriceListManagerControllerImplTest {

	@Rule
	public final MockitoRule rule = MockitoJUnit.rule();

	@Mock
	private PriceListManagerSearchResultsModel searchResultsModel;
	private PriceListSearchControllerImpl controller;

	/**
	 * Test that updateSearchResultsModel() updates the model
	 * with the given DTOs.
	 */
	@Test
	public void testUpdateSearchResultsModel() {
		final Collection<PriceListDescriptorDTO> dtos = new ArrayList<>();
		controller = new PriceListSearchControllerImpl() {
			@Override
			PriceListManagerSearchResultsModel getSearchResultsModel() {
				return searchResultsModel;
			}
		};
		controller.updateSearchResultsModel(dtos);
		verify(searchResultsModel).setPriceListDescriptorSearchResults(dtos);
	}
}
