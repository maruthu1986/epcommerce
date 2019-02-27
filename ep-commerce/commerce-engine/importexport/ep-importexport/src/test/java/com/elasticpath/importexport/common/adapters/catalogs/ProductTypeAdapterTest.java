/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.importexport.common.adapters.catalogs;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.attribute.AttributeGroup;
import com.elasticpath.domain.attribute.AttributeGroupAttribute;
import com.elasticpath.domain.cartmodifier.CartItemModifierGroup;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.catalog.impl.ProductTypeImpl;
import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.importexport.common.adapters.catalogs.helper.AttributeGroupHelper;
import com.elasticpath.importexport.common.caching.CachingService;
import com.elasticpath.importexport.common.dto.catalogs.MultiSkuDTO;
import com.elasticpath.importexport.common.dto.catalogs.ProductTypeDTO;
import com.elasticpath.importexport.common.exception.runtime.PopulationRollbackException;
import com.elasticpath.service.cartitemmodifier.CartItemModifierService;

/**
 * Verify that ProductTypeAdapterTest populates catalog domain object from DTO properly and vice versa. 
 * Nested adapters should be tested separately.
 */
@RunWith(MockitoJUnitRunner.class)
public class ProductTypeAdapterTest {

	private static final String ASSIGNED_SKU_OPTION = "assigned_sku_option";
	private static final String ASSIGNED_SKU_ATTRIBUTE = "assigned_sku_attribute";
	private static final String ASSIGNED_ATTRIBUTE = "assigned_attribute";
	private static final String PRODUCT_TYPE_NAME = "product_name";
	private static final String PRODUCT_TAX_CODE = "tax_code";
	private static final String SKUOPTION_KEY = "sku_key";
	private static final String CARTITEM_MODIFIER_GROUP_CODE = "modifier_group_code";

	@Mock private BeanFactory mockBeanFactory;
	@Mock private CachingService mockCachingService;
	@Mock private CartItemModifierService mockCartItemModifierService;
	@Mock private SkuOption mockSkuOption;
	@Mock private TaxCode mockTaxCode;
	@Mock private AttributeGroupHelper mockAttributeGroupHelper;
	@Mock private CartItemModifierGroup mockCartItemModifierGroup;

	@Before
	public void setUp() throws Exception {
		when(mockTaxCode.getCode()).thenReturn(PRODUCT_TAX_CODE);
		when(mockBeanFactory.getBean(ContextIdNames.PRODUCT_TYPE)).thenReturn(new ProductTypeImpl());
		when(mockCachingService.findTaxCodeByCode(PRODUCT_TAX_CODE)).thenReturn(mockTaxCode);
	}

	/**
	 * Tests PopulateDTO.
	 */
	@Test
	public void testPopulateDTO() {
		final ProductType mockDomain = mock(ProductType.class);

		when(mockDomain.getGuid()).thenReturn("AFD5EF35-F98F-4AC3-8893-8107D1EB0CEA");
		when(mockDomain.getName()).thenReturn(PRODUCT_TYPE_NAME);
		when(mockDomain.getTaxCode()).thenReturn(mockTaxCode);
		when(mockDomain.isMultiSku()).thenReturn(true);
		when(mockDomain.isExcludedFromDiscount()).thenReturn(false);
		when(mockDomain.getCartItemModifierGroups()).thenReturn(ImmutableSet.of(mockCartItemModifierGroup));
		when(mockCartItemModifierGroup.getCode()).thenReturn(CARTITEM_MODIFIER_GROUP_CODE);

		final ProductTypeAdapter productTypeAdapter = new ProductTypeAdapter() {
			@Override
			List<String> createAssignedSkuOptions(final ProductType productType) {
				assertEquals(mockDomain, productType);
				return Collections.singletonList(ASSIGNED_SKU_OPTION);
			}
			@Override
			List<String> createAssignedSkuAttributes(final ProductType productType) {
				assertEquals(mockDomain, productType);
				return Collections.singletonList(ASSIGNED_SKU_ATTRIBUTE);
			}
			@Override
			List<String> createAssignedAttributes(final ProductType productType) {
				assertEquals(mockDomain, productType);
				return Collections.singletonList(ASSIGNED_ATTRIBUTE);
			}
		};
		setUpProductTypeAdapter(productTypeAdapter);

		ProductTypeDTO productTypeDTO = productTypeAdapter.createDtoObject();
		
		productTypeAdapter.populateDTO(mockDomain, productTypeDTO);
		
		assertEquals(PRODUCT_TYPE_NAME, productTypeDTO.getName());
		assertEquals(PRODUCT_TAX_CODE, productTypeDTO.getDefaultTaxCode());
		
		assertEquals(Collections.singletonList(ASSIGNED_SKU_ATTRIBUTE), productTypeDTO.getMultiSku().getAssignedAttributes());
		assertEquals(Collections.singletonList(ASSIGNED_SKU_OPTION), productTypeDTO.getMultiSku().getAssignedSkuOptions());
		assertEquals(Collections.singletonList(ASSIGNED_ATTRIBUTE), productTypeDTO.getAssignedAttributes());

		assertThat(productTypeDTO.getAssignedCartItemModifierGroups(), contains(CARTITEM_MODIFIER_GROUP_CODE));
	}

	private void setUpProductTypeAdapter(
			final ProductTypeAdapter productTypeAdapter) {
		productTypeAdapter.setBeanFactory(mockBeanFactory);
		productTypeAdapter.setCachingService(mockCachingService);
		productTypeAdapter.setAttributeGroupHelper(mockAttributeGroupHelper);
		productTypeAdapter.setCartItemModifierService(mockCartItemModifierService);
	}

	private ProductTypeAdapter createDefaultProductTypeAdapter() {
		final ProductTypeAdapter productTypeAdapter = new ProductTypeAdapter();
		setUpProductTypeAdapter(productTypeAdapter);
		return productTypeAdapter;
	}

	private ProductTypeDTO createProductTypeDTO() {
		final MultiSkuDTO multiSku = new MultiSkuDTO();
		multiSku.setAssignedAttributes(Collections.emptyList());
		multiSku.setAssignedSkuOptions(Collections.singletonList(SKUOPTION_KEY));
		
		final ProductTypeDTO dto = new ProductTypeDTO();
		dto.setName(PRODUCT_TYPE_NAME);
		dto.setAssignedAttributes(Collections.emptyList());
		dto.setDefaultTaxCode(PRODUCT_TAX_CODE);
		dto.setMultiSku(multiSku);
		dto.setNoDiscount(Boolean.FALSE);
		dto.setAssignedCartItemModifierGroups(Collections.singletonList(CARTITEM_MODIFIER_GROUP_CODE));
		return dto;
	}

	/**
	 * Check that all required fields for domain object are being set during domain population.
	 */
	@Test
	public void testPopulateDomain() {
		final ProductType mockDomain = mock(ProductType.class);

		final ProductTypeAdapter productTypeAdapter = new ProductTypeAdapter() {
			@Override
			Set<AttributeGroupAttribute> createSkuAttributeGroupAttributes(final ProductType productType) {
				assertEquals(mockDomain, productType);
				return Collections.emptySet();
			}
			@Override
			Set<AttributeGroupAttribute> createAttributeGroupAttributes(final ProductType productType) {
				assertEquals(mockDomain, productType);
				return Collections.emptySet();
			}
			@Override
			TaxCode findDefaultTaxCode(final String defaultTaxCode) {
				assertEquals(PRODUCT_TAX_CODE, defaultTaxCode);
				return mockTaxCode;
			}
			@Override
			SkuOption findSkuOption(final String skuOptionCode) {
				assertEquals(SKUOPTION_KEY, skuOptionCode);
				return mockSkuOption;
			}
		};
		setUpProductTypeAdapter(productTypeAdapter);

		final HashSet<CartItemModifierGroup> cartItemModifierGroups = new HashSet<>();
		when(mockDomain.getCartItemModifierGroups()).thenReturn(cartItemModifierGroups);
		when(mockCartItemModifierService.findCartItemModifierGroupByCode(CARTITEM_MODIFIER_GROUP_CODE)).thenReturn(mockCartItemModifierGroup);
		
		final ProductTypeDTO dto = createProductTypeDTO();
		productTypeAdapter.populateDomain(dto, mockDomain);

		verify(mockDomain).setName(PRODUCT_TYPE_NAME);
		verify(mockDomain).setTaxCode(mockTaxCode);
		verify(mockAttributeGroupHelper).populateAttributeGroupAttributes(
				Collections.emptySet(), dto.getAssignedAttributes(), ContextIdNames.PRODUCT_TYPE_PRODUCT_ATTRIBUTE);
		verify(mockAttributeGroupHelper).populateAttributeGroupAttributes(
				Collections.emptySet(),	dto.getMultiSku().getAssignedAttributes(), ContextIdNames.PRODUCT_TYPE_SKU_ATTRIBUTE);
		verify(mockDomain).setMultiSku(Boolean.TRUE);
		verify(mockDomain).addOrUpdateSkuOption(mockSkuOption);
		verify(mockDomain).setExcludedFromDiscount(Boolean.FALSE);

		assertThat(cartItemModifierGroups, contains(mockCartItemModifierGroup));

	}


	/**
	 * Tests findDefaultTaxCode.
	 */
	@Test(expected = PopulationRollbackException.class)
	public void ensureFindDefaultTaxCodeThrowsExceptionForNull() {
		final ProductTypeAdapter productTypeAdapter = createDefaultProductTypeAdapter();
		
		// Throw on null code
		productTypeAdapter.findDefaultTaxCode(null);
	}
	@Test(expected = PopulationRollbackException.class)
	public void ensureFindDefaultTaxCodeThrowsExceptionForBadTaxCode() {
		final ProductTypeAdapter productTypeAdapter = createDefaultProductTypeAdapter();
		// Throw on bad code
		final String badTaxCode = "bad_tax_code";

		when(mockCachingService.findTaxCodeByCode(badTaxCode)).thenReturn(null);
		productTypeAdapter.findDefaultTaxCode(badTaxCode);
	}

	@Test
	public void testFindDefaultTaxCode() {
		final ProductTypeAdapter productTypeAdapter = createDefaultProductTypeAdapter();
		assertEquals("Should work fine.", mockTaxCode, productTypeAdapter.findDefaultTaxCode(PRODUCT_TAX_CODE));
	}
	
	/**
	 * Tests FindSkuOption.
	 */
	@Test(expected = PopulationRollbackException.class)
	public void testFindSkuOptionThrowsExceptionForBadSku() {
		final ProductTypeAdapter productTypeAdapter = createDefaultProductTypeAdapter();
		
		final String badSkuOptionKey = "bad_sku_option_key";

		when(mockCachingService.findSkuOptionByKey(badSkuOptionKey)).thenReturn(null);
		productTypeAdapter.findSkuOption(badSkuOptionKey);
	}

	@Test

	public void testFindSkuOption() {
		final ProductTypeAdapter productTypeAdapter = createDefaultProductTypeAdapter();
		final String goodSkuOptionKey = "good_sku_option_key";

		when(mockCachingService.findSkuOptionByKey(goodSkuOptionKey)).thenReturn(mockSkuOption);
		productTypeAdapter.findSkuOption(goodSkuOptionKey);
	}
	
	/**
	 * Tests createAssignedSkuOptions.
	 */
	@Test
	public void testCreateAssignedSkuOptions() {
		final ProductTypeAdapter productTypeAdapter = createDefaultProductTypeAdapter();
				
		final Set<SkuOption> skuOptions = new HashSet<>();
		skuOptions.add(mockSkuOption);
		
		final ProductType mockDomain = mock(ProductType.class);
		when(mockDomain.getSkuOptions()).thenReturn(skuOptions);
		when(mockSkuOption.getOptionKey()).thenReturn(SKUOPTION_KEY);

		List<String> result = productTypeAdapter.createAssignedSkuOptions(mockDomain);
		
		assertEquals(Collections.singletonList(SKUOPTION_KEY), result);
	}
	
	/**
	 * Tests createAttributeGroupAttributes.
	 */
	@Test
	public void testCreateAttributeGroupAttributes() {
		final ProductTypeAdapter productTypeAdapter = createDefaultProductTypeAdapter();
		
		final Set<AttributeGroupAttribute> attributeGroupAttributes = new HashSet<>();
		
		final ProductType mockDomain = mock(ProductType.class);
		when(mockDomain.getProductAttributeGroupAttributes()).thenReturn(null);

		productTypeAdapter.createAttributeGroupAttributes(mockDomain);
		verify(mockDomain).setProductAttributeGroupAttributes(attributeGroupAttributes);

	}
	
	/**
	 * Tests createSkuAttributeGroupAttributes.
	 */
	@Test
	public void testCreateSkuAttributeGroupAttributes() {
		final ProductTypeAdapter productTypeAdapter = createDefaultProductTypeAdapter();
		
		final Set<AttributeGroupAttribute> attributeGroupAttributes = new HashSet<>();
		
		final ProductType mockDomain = mock(ProductType.class);
		final AttributeGroup mockAttributeGroup = mock(AttributeGroup.class);

		when(mockDomain.getSkuAttributeGroup()).thenReturn(mockAttributeGroup);
		when(mockAttributeGroup.getAttributeGroupAttributes()).thenReturn(null);

		productTypeAdapter.createSkuAttributeGroupAttributes(mockDomain);
		verify(mockAttributeGroup).setAttributeGroupAttributes(attributeGroupAttributes);
		verify(mockDomain).setSkuAttributeGroup(mockAttributeGroup);
	}

	/**
	 * Tests createAssignedAttributes.
	 */
	@Test
	public void testCreateAssignedAttributes() {
		final ProductTypeAdapter productTypeAdapter = createDefaultProductTypeAdapter();
		
		final Set<AttributeGroupAttribute> attributeGroupAttributes = new HashSet<>();
		
		final ProductType mockDomain = mock(ProductType.class);

		when(mockDomain.getProductAttributeGroupAttributes()).thenReturn(attributeGroupAttributes);
		when(mockAttributeGroupHelper.createAssignedAttributes(attributeGroupAttributes)).thenReturn(Collections.emptyList());

		List<String> result = productTypeAdapter.createAssignedAttributes(mockDomain);
		
		assertEquals(Collections.emptyList(), result);
	}
	
	/**
	 * Tests createAssignedSkuAttributes.
	 */
	@Test
	public void testCreateAssignedSkuAttributes() {
		final ProductTypeAdapter productTypeAdapter = createDefaultProductTypeAdapter();
		
		final Set<AttributeGroupAttribute> attributeGroupAttributes = new HashSet<>();
		
		final ProductType mockDomain = mock(ProductType.class);
		final AttributeGroup mockAttributeGroup = mock(AttributeGroup.class);

		when(mockDomain.getSkuAttributeGroup()).thenReturn(mockAttributeGroup);
		when(mockAttributeGroup.getAttributeGroupAttributes()).thenReturn(attributeGroupAttributes);
		when(mockAttributeGroupHelper.createAssignedAttributes(attributeGroupAttributes)).thenReturn(Collections.emptyList());

		List<String> result = productTypeAdapter.createAssignedSkuAttributes(mockDomain);
		
		assertEquals(Collections.emptyList(), result);
	}
	
	/**
	 * Check that CreateDtoObject works. 
	 */
	@Test
	public void testCreateDomainObject() {
		final ProductTypeAdapter productTypeAdapter = createDefaultProductTypeAdapter();
		
		assertNotNull(productTypeAdapter.createDomainObject());
	}

	/**
	 * Check that createDomainObject works.
	 */
	@Test
	public void testCreateDtoObject() {
		final ProductTypeAdapter productTypeAdapter = createDefaultProductTypeAdapter();
		
		assertNotNull(productTypeAdapter.createDtoObject());
	}

}
