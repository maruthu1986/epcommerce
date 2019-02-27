/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
/**
 * 
 */
package com.elasticpath.cmclient.pricelistmanager.controller.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import com.elasticpath.cmclient.core.dto.catalog.PriceListEditorModel;
import com.elasticpath.common.dto.pricing.BaseAmountDTO;
import com.elasticpath.common.pricing.service.BaseAmountFilter;
import com.elasticpath.common.pricing.service.PriceListService;
import com.elasticpath.commons.constants.ContextIdNames;


/**
 * Tests for the PriceListManagerController class.
 */
public class PriceListEditorControllerImplTest {

	private static final int NUM_3 = 3;
	private static final String BA_DTO2 = "baDto2"; //$NON-NLS-1$
	private static final String OBJ_GUID = "prod1"; //$NON-NLS-1$
	private static final String PRODUCT = "PRODUCT"; //$NON-NLS-1$
	private static final String PRICE_LIST_DESCRIPTOR_GUID = "123"; //$NON-NLS-1$

	@Rule
	public final MockitoRule rule = MockitoJUnit.rule();

	@Mock
	private PriceListService priceListService;

	@Mock
	private BaseAmountFilter baseAmountFilter;

	@Mock
	private PriceListEditorModel priceListModel;

	private PriceListEditorControllerImpl controller;

	/**
	 * Set up for test case.
	 */
	@Before
	public void setUp() {
		controller = new PriceListEditorControllerImpl(PRICE_LIST_DESCRIPTOR_GUID) {
			@Override
			protected Object getBean(final String beanId) {
				if (beanId.equals(ContextIdNames.PRICE_LIST_CLIENT_SERVICE)) {
					return priceListService;
				} else if (beanId.equals(ContextIdNames.BASE_AMOUNT_FILTER)) {
					return baseAmountFilter;
				}
				
				return null;
			}
			
			@Override
			public PriceListEditorModel getModel() {
				return priceListModel;
			}
		}; 
	}
	
	private BaseAmountDTO createNewBaseAmount(final String objectGuid, final String objectType, 
			final BigDecimal quantity, final BigDecimal listValue, final BigDecimal saleValue) {
		BaseAmountDTO baseAmountDto = new BaseAmountDTO();
		
		baseAmountDto.setObjectGuid(objectGuid);
		baseAmountDto.setObjectType(objectType);
		baseAmountDto.setQuantity(quantity);
		baseAmountDto.setListValue(listValue);
		baseAmountDto.setSaleValue(saleValue);
		baseAmountDto.setPriceListDescriptorGuid(PRICE_LIST_DESCRIPTOR_GUID);
		
		return baseAmountDto;
	}

	/**
	 * @return a sample base amount DTO
	 */
	private BaseAmountDTO getSampleBaseAmount() {
		return createNewBaseAmount("SKU-1", "T1", BigDecimal.ONE, BigDecimal.TEN, BigDecimal.TEN); 	 //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Tests that isUniqueBaseAmountDTO() calls the service and finds the uniqueness of the base amount.
	 */
	@Test
	public void testBaseAmountDTOUniqueness() {
		final BaseAmountDTO baseAmountDTO = getSampleBaseAmount();

		when(priceListModel.logicalEquals(baseAmountDTO, baseAmountDTO)).thenReturn(true);
		when(priceListModel.getBaseAmounts()).thenReturn(new ArrayList<>());
		when(priceListService.getBaseAmounts(baseAmountFilter)).thenReturn(Collections.<BaseAmountDTO>emptyList());

		assertTrue(controller.isUniqueBaseAmountDTO(baseAmountDTO, baseAmountDTO));
		verify(priceListModel).logicalEquals(baseAmountDTO, baseAmountDTO);
		verify(priceListModel).getBaseAmounts();

	}
	
	/**
	 * Tests that if controller can add a new <code>BaseAmountDTO</code>.
	 */
	@Test
	public void testAddingBaseAmountDTO() {
		final BaseAmountDTO baseAmountDTO = getSampleBaseAmount();
		
		controller.addBaseAmountDTO(baseAmountDTO);
		verify(priceListModel).addBaseAmount(baseAmountDTO);

	}
	
	/**
	 * Tests that if controller can update an existing <code>BaseAmountDTO</code>.
	 */
	@Test
	public void testUpdatingBaseAmountDTO() {
		final BaseAmountDTO baseAmountDTO = getSampleBaseAmount();

		controller.updateBaseAmountDTO(baseAmountDTO, baseAmountDTO);
		verify(priceListModel).updateBaseAmount(baseAmountDTO, baseAmountDTO);
	}
	
	/**
	 * Tests that if controller can remove an existing <code>BaseAmountDTO</code>.
	 */
	@Test
	public void testRemovingBaseAmountDTO() {
		final BaseAmountDTO baseAmountDTO = getSampleBaseAmount();

		controller.deleteBaseAmountDTO(baseAmountDTO);
		verify(priceListModel).removeBaseAmount(baseAmountDTO);
	}
	
	/**
	 * Tests that base amount duplicates on the client side are detected correctly (when you check the duplicates over newly added 
	 * and non-persistent base amounts). 
	 */
	@Test
	public void testClientSideBaseAmountDuplicateExists() {
		List<BaseAmountDTO> clientSideProductBaseAmounts = createProductBaseAmountsList();
		BaseAmountDTO baseAmountDTO = createDto(PRODUCT, OBJ_GUID, 2, "baDto3");  //$NON-NLS-1$
		assertTrue(controller.clientSideDuplicatesExist(baseAmountDTO, clientSideProductBaseAmounts));
		List<BaseAmountDTO> clientSideMixedBaseAmounts = createMixedBaseAmountsList();
		assertTrue(controller.clientSideDuplicatesExist(baseAmountDTO, clientSideMixedBaseAmounts));
	}

	/**
	 * Tests that base amount duplicates on the client side are detected correctly (when you check the duplicates over newly added 
	 * and non-persistent base amounts). 
	 */
	@Test
	public void testClientSideBaseAmountDuplicateDoNotExist() {
		List<BaseAmountDTO> clientSideProductBaseAmounts = createProductBaseAmountsList();
		BaseAmountDTO baseAmountDTO = createDto(PRODUCT, OBJ_GUID, NUM_3, "baDto3"); //$NON-NLS-1$ 
		assertFalse(controller.clientSideDuplicatesExist(baseAmountDTO, clientSideProductBaseAmounts));
		List<BaseAmountDTO> clientSideMixedBaseAmounts = createMixedBaseAmountsList();
		BaseAmountDTO baseAmountDTO2 = createDto(PRODUCT, OBJ_GUID, 1, "baDto4"); //$NON-NLS-1$ 
		assertFalse(controller.clientSideDuplicatesExist(baseAmountDTO2, clientSideMixedBaseAmounts));
	}

	/**
	 * Tests that base amount duplicates with null object guids on the client side are detected correctly (when you check the duplicates over newly 
	 * added and non-persistent base amounts). 
	 */
	@Test
	public void testClientSideBaseAmountDuplicateWithNullObjectGuid() {
		List<BaseAmountDTO> clientSideProductBaseAmounts = createProductBaseAmountsListWithNullObjectGuid();
		BaseAmountDTO baseAmountDTO = createDto(PRODUCT, null, 2, "baDto3"); //$NON-NLS-1$ 
		assertTrue(controller.clientSideDuplicatesExist(baseAmountDTO, clientSideProductBaseAmounts));
		BaseAmountDTO baseAmountDTO2 = createDto(PRODUCT, null, NUM_3, "baDto4"); //$NON-NLS-1$
		assertFalse(controller.clientSideDuplicatesExist(baseAmountDTO2, clientSideProductBaseAmounts));
	}

	
	private List<BaseAmountDTO> createProductBaseAmountsList() {
		List<BaseAmountDTO> clientSideProductBaseAmounts = new ArrayList<>(Arrays.asList(
				createDto(PRODUCT, OBJ_GUID, 1, "baDto1"), //$NON-NLS-1$
				createDto(PRODUCT, OBJ_GUID, 2, BA_DTO2)
		));
		return clientSideProductBaseAmounts;
	}

	private List<BaseAmountDTO> createProductBaseAmountsListWithNullObjectGuid() {
		List<BaseAmountDTO> clientSideProductBaseAmounts = new ArrayList<>(Arrays.asList(
				createDto(PRODUCT, OBJ_GUID, 1, "baDto1"), //$NON-NLS-1$
				createDto(PRODUCT, null, 2, BA_DTO2)
		));
		return clientSideProductBaseAmounts;
	}

	
	private List<BaseAmountDTO> createMixedBaseAmountsList() {
		List<BaseAmountDTO> clientSideProductBaseAmounts = new ArrayList<>(Arrays.asList(
				createDto("SKU", "sku1", 1, "baDto1"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				createDto(PRODUCT, OBJ_GUID, 2, BA_DTO2)
		));
		return clientSideProductBaseAmounts;
	}
	
	private BaseAmountDTO createDto(final String type, final String objectGuid, final Integer quantity, final String guid) {
		BaseAmountDTO dto = new BaseAmountDTO();
		dto.setObjectType(type);
		dto.setObjectGuid(objectGuid);
		dto.setGuid(guid);
		if (quantity != null) {
			dto.setQuantity(BigDecimal.valueOf(quantity));
		}
		return dto;
	}

}
