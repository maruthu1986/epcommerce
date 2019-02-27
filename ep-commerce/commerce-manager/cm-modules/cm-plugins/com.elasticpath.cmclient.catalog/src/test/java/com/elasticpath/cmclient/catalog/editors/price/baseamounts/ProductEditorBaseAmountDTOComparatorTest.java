/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.catalog.editors.price.baseamounts;

import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import org.junit.Test;

import com.elasticpath.common.dto.pricing.BaseAmountDTO;

/**
 * Test that the comparator works as expected.
 */
public class ProductEditorBaseAmountDTOComparatorTest {

	private static final String TYPE2 = "TYPE2"; //$NON-NLS-1$
	private static final String TYPE1 = "TYPE1"; //$NON-NLS-1$
	
	/** Comparator under test. */
	private final ProductEditorBaseAmountDTOComparator comparator =
		new ProductEditorBaseAmountDTOComparator();

	/** Test quantity comparison (first test of compare). **/
	@Test
	public void testQuantity() {
		BaseAmountDTO descriptor1 = createDto(null, null, 1);
		BaseAmountDTO descriptor2 = createDto(null, null, 2);
		assertTrue(comparator.compare(descriptor1, descriptor1) == 0);
		assertTrue(comparator.compare(descriptor1, descriptor2) < 0);
		assertTrue(comparator.compare(descriptor2, descriptor1) > 0);
	}

	/** After quantity comes object type. */
	@Test
	public void testEqualsQuantityDefersToObjectType() {
		BaseAmountDTO descriptor1 = createDto(TYPE1, null, 1);
		BaseAmountDTO descriptor2 = createDto(TYPE2, null, 1);
		assertTrue(comparator.compare(descriptor1, descriptor1) == 0);
		assertTrue(comparator.compare(descriptor1, descriptor2) < 0);
		assertTrue(comparator.compare(descriptor2, descriptor1) > 0);
	}

	/** After quantity and object type comes sku code. */
	@Test
	public void testEqualsQuantityAndObjectTypeDefersToSkuCode() {
		BaseAmountDTO descriptor1 = createDto(TYPE1, "zzz", 1); //$NON-NLS-1$
		BaseAmountDTO descriptor2 = createDto(TYPE1, "aaa", 1); //$NON-NLS-1$
		assertTrue(comparator.compare(descriptor1, descriptor1) == 0);
		assertTrue(comparator.compare(descriptor1, descriptor2) > 0);
		assertTrue(comparator.compare(descriptor2, descriptor1) < 0);
	}

	/**
	 * Test that sku code checks are case insensitive.
	 */
	@Test
	public void testSkuCodeComparedCaseInsentive() {
		BaseAmountDTO descriptor1 = createDto(TYPE1, "zzz", 1); //$NON-NLS-1$ 
		BaseAmountDTO descriptor2 = createDto(TYPE1, "AAA", 1); //$NON-NLS-1$
		assertTrue(comparator.compare(descriptor1, descriptor1) == 0);
		assertTrue(comparator.compare(descriptor1, descriptor2) > 0);
		assertTrue(comparator.compare(descriptor2, descriptor1) < 0);
	}
	
	/** Check null sku code. */
	@Test
	public void testNullSkuCode() {
		BaseAmountDTO descriptor1 = createDto(TYPE1, "zzz", 1); //$NON-NLS-1$
		BaseAmountDTO descriptor2 = createDto(TYPE1, null, 1);
		assertTrue(comparator.compare(descriptor1, descriptor1) == 0);
		assertTrue(comparator.compare(descriptor1, descriptor2) < 0);  // Bad data last
		assertTrue(comparator.compare(descriptor2, descriptor1) > 0);
	}

	
	private BaseAmountDTO createDto(final String type, final String skuCode, final Integer quantity) {
		BaseAmountDTO dto = new BaseAmountDTO();
		dto.setObjectType(type);
		dto.setSkuCode(skuCode);
		if (quantity != null) {
			dto.setQuantity(BigDecimal.valueOf(quantity));
		}
		return dto;
	}
	
}
