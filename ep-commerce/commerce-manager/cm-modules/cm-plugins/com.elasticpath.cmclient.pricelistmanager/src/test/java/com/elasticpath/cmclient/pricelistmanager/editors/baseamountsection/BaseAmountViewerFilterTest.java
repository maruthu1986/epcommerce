/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.pricelistmanager.editors.baseamountsection;

import static com.elasticpath.domain.pricing.BaseAmountObjectType.PRODUCT;
import static com.elasticpath.domain.pricing.BaseAmountObjectType.SKU;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import org.junit.Test;

import com.elasticpath.common.dto.pricing.BaseAmountDTO;
import com.elasticpath.common.pricing.service.BaseAmountFilterExt;
import com.elasticpath.common.pricing.service.impl.BaseAmountFilterExtImpl;
import com.elasticpath.domain.pricing.BaseAmountObjectType;

/**
 * Tests the BaseAmountViewerFilter works as expected.
 */
@SuppressWarnings({"PMD.FinalFieldCouldBeStatic", "restriction"})
public class BaseAmountViewerFilterTest {

	// Checkstyle problem avoidance - magic numbers
	private static final int I_7 = 7;
	private static final int I_5 = 5;
	private static final int I_8 = 8;
	private static final int I_12 = 12;
	private static final int I_6 = 6;
	private static final int I_10 = 10;
	private static final int I_9 = 9;
	private static final int I_4 = 4;
	
	
	// The base amounts we will be attempting to filter
	private final BaseAmountDTO nullDto = null;
	private final BaseAmountDTO emptyDto = createDto("empty", null, null, null, null, null, null); //$NON-NLS-1$
	private final BaseAmountDTO typicalProductDTO = createDto("product", PRODUCT, "100800", null, 1, 5, 2);  //$NON-NLS-1$ //$NON-NLS-2$ 	
	private final BaseAmountDTO typicalSkuDTO = createDto("sku", SKU, "200800", "abc", 2, 10, 7);  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	
	
	/** A null filter should pass everything. **/
	@Test
	public void testNullFilterLetsEverythingPass() {
		assertFiltering(null, new BaseAmountDTO[] { emptyDto, typicalProductDTO, typicalSkuDTO }, new BaseAmountDTO[] { nullDto }); // null elements always filtered
	}

	/** An empty (null fields) filters should pass everything. */
	@Test
	public void testEmptyFilterLetsEverythingPass() {
		assertFiltering(new BaseAmountFilterExtImpl(), new BaseAmountDTO[] { emptyDto, typicalProductDTO, typicalSkuDTO }, new BaseAmountDTO[] { nullDto });
	}
	
	/** Only items with matching types should pass (because no code is specified). */
	@Test
	public void testTypeFilteringWithNoCode() {
		assertFiltering(codeFilter(PRODUCT, null), new BaseAmountDTO[] { typicalProductDTO }, new BaseAmountDTO[] { nullDto, emptyDto, typicalSkuDTO });
		assertFiltering(codeFilter(PRODUCT, ""), new BaseAmountDTO[] { typicalProductDTO }, new BaseAmountDTO[] { nullDto, emptyDto, typicalSkuDTO }); //$NON-NLS-1$
		assertFiltering(codeFilter(SKU, null), new BaseAmountDTO[] { typicalSkuDTO }, new BaseAmountDTO[] { nullDto, emptyDto, typicalProductDTO });
		assertFiltering(codeFilter(SKU, ""), new BaseAmountDTO[] { typicalSkuDTO }, new BaseAmountDTO[] { nullDto, emptyDto, typicalProductDTO }); //$NON-NLS-1$
	}

	/** Only items with matching codes should pass (includes skus which have matching product code). */
	@Test
	public void testProductCodeFiltering() {
		assertFiltering(codeFilter(PRODUCT, null), new BaseAmountDTO[] { typicalProductDTO }, new BaseAmountDTO[] { nullDto, emptyDto, typicalSkuDTO });
		assertFiltering(codeFilter(PRODUCT, "800"), new BaseAmountDTO[] { typicalProductDTO, typicalSkuDTO }, new BaseAmountDTO[] { nullDto, emptyDto }); //$NON-NLS-1$
		assertFiltering(codeFilter(PRODUCT, "100"), new BaseAmountDTO[] { typicalProductDTO }, new BaseAmountDTO[] { nullDto, emptyDto, typicalSkuDTO }); //$NON-NLS-1$
		assertFiltering(codeFilter(PRODUCT, "200"), new BaseAmountDTO[] { typicalSkuDTO }, new BaseAmountDTO[] { nullDto, emptyDto, typicalProductDTO }); //$NON-NLS-1$
		assertFiltering(codeFilter(PRODUCT, "abc"), new BaseAmountDTO[] { }, new BaseAmountDTO[] { nullDto, emptyDto, typicalProductDTO, typicalSkuDTO }); //$NON-NLS-1$
	}

	/** Only items with matching sku codes should pass. */
	@Test
	public void testSkuCodeFiltering() {
		assertFiltering(codeFilter(SKU, null), new BaseAmountDTO[] { typicalSkuDTO }, new BaseAmountDTO[] { nullDto, emptyDto, typicalProductDTO });
		assertFiltering(codeFilter(SKU, ""), new BaseAmountDTO[] { typicalSkuDTO }, new BaseAmountDTO[] { nullDto, emptyDto, typicalProductDTO }); //$NON-NLS-1$
		assertFiltering(codeFilter(SKU, "100"), new BaseAmountDTO[] { }, new BaseAmountDTO[] { nullDto, emptyDto, typicalSkuDTO, typicalProductDTO }); //$NON-NLS-1$
		assertFiltering(codeFilter(SKU, "200"), new BaseAmountDTO[] { }, new BaseAmountDTO[] { nullDto, emptyDto, typicalProductDTO, typicalSkuDTO }); //$NON-NLS-1$
		assertFiltering(codeFilter(SKU, "bc"), new BaseAmountDTO[] { typicalSkuDTO }, new BaseAmountDTO[] { nullDto, emptyDto, typicalProductDTO }); //$NON-NLS-1$
	}
	
	/** Code fragments can match anywhere in either sku or product code. **/
	@Test
	public void testAllCodeFiltering() {
		assertFiltering(codeFilter(null, "800"), new BaseAmountDTO[] { typicalProductDTO, typicalSkuDTO }, new BaseAmountDTO[] { nullDto, emptyDto }); //$NON-NLS-1$
		assertFiltering(codeFilter(null, "100800"), new BaseAmountDTO[] { typicalProductDTO }, new BaseAmountDTO[] { nullDto, emptyDto, typicalSkuDTO }); //$NON-NLS-1$
		assertFiltering(codeFilter(null, "100"), new BaseAmountDTO[] { typicalProductDTO }, new BaseAmountDTO[] { nullDto, emptyDto, typicalSkuDTO }); //$NON-NLS-1$
		assertFiltering(codeFilter(null, "200"), new BaseAmountDTO[] { typicalSkuDTO }, new BaseAmountDTO[] { nullDto, emptyDto, typicalProductDTO }); //$NON-NLS-1$
		assertFiltering(codeFilter(null, "abc"), new BaseAmountDTO[] { typicalSkuDTO }, new BaseAmountDTO[] { nullDto, emptyDto, typicalProductDTO }); //$NON-NLS-1$

	}
	
	/** Quantity filtering is an exact match. */
	@Test
	public void testQuantityFiltering() {
		assertFiltering(quantityFilter(1), new BaseAmountDTO[] { typicalProductDTO }, new BaseAmountDTO[] { nullDto, emptyDto, typicalSkuDTO });
		assertFiltering(quantityFilter(2), new BaseAmountDTO[] { typicalSkuDTO }, new BaseAmountDTO[] { nullDto, emptyDto, typicalProductDTO });
	}
	
	/** If lowest price is set, then everything with a list or sale price bigger (or equal) should pass. */ 
	@Test
	public void testLowestPriceFiltering() {
		assertFiltering(priceFilter(I_4, null), new BaseAmountDTO[] { typicalProductDTO, typicalSkuDTO }, new BaseAmountDTO[] { nullDto, emptyDto });
		assertFiltering(priceFilter(I_9, null), new BaseAmountDTO[] { typicalSkuDTO }, new BaseAmountDTO[] { nullDto, emptyDto, typicalProductDTO });
		assertFiltering(priceFilter(I_10, null), new BaseAmountDTO[] { typicalSkuDTO }, new BaseAmountDTO[] { nullDto, emptyDto, typicalProductDTO });
	}

	/**
	 * If the highest price is set, then everything with a list or sale price smaller (or equals) should pass.
	 */
	@Test
	public void testHighestPriceFiltering() {
		assertFiltering(priceFilter(null, I_6), new BaseAmountDTO[] { typicalProductDTO }, new BaseAmountDTO[] { nullDto, emptyDto, typicalSkuDTO });
		assertFiltering(priceFilter(null, I_12), new BaseAmountDTO[] { typicalSkuDTO, typicalProductDTO }, new BaseAmountDTO[] { nullDto, emptyDto });
	}

	/** If both lowest and highest are set then everything in the range (inclusive) should be returned. */
	@Test
	public void testPriceRangeFiltering() {
		assertFiltering(priceFilter(2, I_6), new BaseAmountDTO[] { typicalProductDTO }, new BaseAmountDTO[] { nullDto, emptyDto, typicalSkuDTO });
		assertFiltering(priceFilter(I_8, I_12), new BaseAmountDTO[] { typicalSkuDTO }, new BaseAmountDTO[] { nullDto,emptyDto, typicalProductDTO });
		assertFiltering(priceFilter(1, I_12), new BaseAmountDTO[] { typicalProductDTO, typicalSkuDTO }, new BaseAmountDTO[] { nullDto, emptyDto });
		assertFiltering(priceFilter(I_5, I_7), new BaseAmountDTO[] { typicalProductDTO, typicalSkuDTO }, new BaseAmountDTO[] { nullDto, emptyDto });
	}
	
	/** Typical combination testing. */
	@Test
	public void testTypicalCombinationFiltering() {
		FilterBuilder builder = new FilterBuilder();
		
		builder = builder.code("00"); //$NON-NLS-1$
		assertFiltering(builder.filter(), new BaseAmountDTO[] { typicalSkuDTO, typicalProductDTO }, new BaseAmountDTO[] { nullDto, emptyDto });

		builder = builder.lowestPrice(I_7);
		assertFiltering(builder.filter(), new BaseAmountDTO[] { typicalSkuDTO }, new BaseAmountDTO[] { nullDto, emptyDto, typicalProductDTO });

		builder = builder.quantity(2);
		assertFiltering(builder.filter(), new BaseAmountDTO[] { typicalSkuDTO }, new BaseAmountDTO[] { nullDto, emptyDto, typicalProductDTO });

		builder = builder.quantity(I_5);
		assertFiltering(builder.filter(), new BaseAmountDTO[] { }, new BaseAmountDTO[] { nullDto, emptyDto, typicalProductDTO, typicalSkuDTO });
	}
	
	
	private void assertFiltering(final BaseAmountFilterExt filter, final BaseAmountDTO [] passes, final BaseAmountDTO [] filtered) {
		BaseAmountViewerFilter viewerFilter = new BaseAmountViewerFilter(filter);
		for (BaseAmountDTO dto : passes) {
			String name = getNameOrNull(dto);
			assertTrue("Something should have passed but was filtered: " + name, viewerFilter.select(null, null, dto)); //$NON-NLS-1$
		}
		for (BaseAmountDTO dto : filtered) {
			String name = getNameOrNull(dto);
			assertFalse("Something was passed that should have been filtered: " + name, viewerFilter.select(null, null, dto)); //$NON-NLS-1$
		}
	}

	private String getNameOrNull(final BaseAmountDTO dto) {
		if (dto == null) {
			return null;
		}
		return dto.getProductName();
	}
	
	
	private BaseAmountFilterExt quantityFilter(final Integer quantity) {
		return new FilterBuilder().quantity(quantity).filter();
	}
		
	private BaseAmountFilterExt codeFilter(final BaseAmountObjectType type, final String code) {
		return new FilterBuilder().type(type).code(code).filter();
	}
	
	private BaseAmountFilterExt priceFilter(final Integer lowestPrice, final Integer highestPrice) {
		FilterBuilder builder = new FilterBuilder();
		if (lowestPrice != null) {
			builder = builder.lowestPrice(lowestPrice);
		}
		if (highestPrice != null) {
			builder = builder.highestPrice(highestPrice);
		}
		return builder.filter();
	}

	/** Simple builder class for base amount filters. */
	private class FilterBuilder {
		private final BaseAmountFilterExt theFilter = new BaseAmountFilterExtImpl();

		public FilterBuilder quantity(final Integer quantity) {
			theFilter.setQuantity(BigDecimal.valueOf(quantity));
			return this;
		}
		
		public FilterBuilder type(final BaseAmountObjectType type) {
			if (type != null) {
				theFilter.setObjectType(type.toString());
			}
			return this;
		}
		
		public FilterBuilder code(final String code) {
			theFilter.setObjectGuid(code);
			return this;
		}
		
		public FilterBuilder lowestPrice(final Integer lowestPrice) {
			theFilter.setLowestPrice(BigDecimal.valueOf(lowestPrice));
			return this;
		}
		
		public FilterBuilder highestPrice(final Integer highestPrice) {
			theFilter.setHighestPrice(BigDecimal.valueOf(highestPrice));
			return this;
		}
		
		public BaseAmountFilterExt filter() {
			return theFilter;
		}
	}
	
	
	private BaseAmountDTO createDto(final String name, final BaseAmountObjectType type, final String productCode, 
			final String skuCode, final Integer quantity, final Integer listPrice, final Integer salePrice) {
		
		BaseAmountDTO dto = new BaseAmountDTO();
		
		dto.setProductName(name);
		if (type != null) {
			dto.setObjectType(type.toString());
		}
		dto.setProductCode(productCode);
		dto.setSkuCode(skuCode);
		if (quantity != null) {
			dto.setQuantity(BigDecimal.valueOf(quantity));
		}
		if (listPrice != null) {
			dto.setListValue(BigDecimal.valueOf(listPrice));
		}
		if (salePrice != null) {
			dto.setSaleValue(BigDecimal.valueOf(salePrice));
		}
		return dto;
	}

}
