/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.importexport.common.adapters.productcategories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.importexport.common.caching.CachingService;
import com.elasticpath.importexport.common.dto.productcategory.CatalogCategoriesDTO;
import com.elasticpath.importexport.common.dto.productcategory.ProductCategoriesDTO;
import com.elasticpath.importexport.common.dto.productcategory.ProductCategoryDTO;

/**
 * Verify that ProductCategoryAdapter populates category domain object from DTO properly and vice versa.
 * <br>Nested adapters should be tested separately.
 */
@RunWith(MockitoJUnitRunner.class)
public class ProductCategoryAdapterTest {

	private static final String PRODUCT_CODE = "productCode";

	private static final String GOOD_CATALOG = "goodCatalog";

	private static final String NULL_CATALOG = "nullCatalog";

	private static final String NULL_CATEGORY = "nullCategory";

	private static final String GOOD_CATEGORY = "goodCategory";

	private static final String CATEGORY_CODE = "categoryCode";

	private static final String CATALOG_CODE = "catalogCode";

	@Mock
	private Product mockProduct;
	@Mock
	private Catalog mockCatalog;
	@Mock
	private Category mockCategory;
	@Mock
	private CachingService mockCachingService;

	private ProductCategoryAdapter productCategoryAdapter;

	/**
	 * Setup test.
	 */
	@Before
	public void setUp() throws Exception {
		productCategoryAdapter = new ProductCategoryAdapter();
		setUpProductCategoryAdapter(productCategoryAdapter);
	}

	private void setUpProductCategoryAdapter(final ProductCategoryAdapter productCategoryAdapter) {
		productCategoryAdapter.setCachingService(mockCachingService);
	}

	/**
	 * Tests createDtoObject.
	 */
	@Test
	public void testCreateDtoObject() {
		assertThat(productCategoryAdapter.createDtoObject()).isNotNull();
	}

	/**
	 * Tests populateDTO.
	 */
	@Test
	public void testPopulateDTO() {

		when(mockProduct.getCategories()).thenReturn(Collections.emptySet());
		when(mockProduct.getCode()).thenReturn(PRODUCT_CODE);

		ProductCategoryAdapter adapter = new ProductCategoryAdapter() {
			@Override
			List<CatalogCategoriesDTO> createCatalogCategoriesDTOList(final Product product) {

				assertThat(product.getCategories()).isEmpty();

				return Collections.emptyList();
			}
		};

		setUpProductCategoryAdapter(adapter);

		ProductCategoriesDTO productCategoriesDTO = new ProductCategoriesDTO();

		adapter.populateDTO(mockProduct, productCategoriesDTO);

		assertThat(productCategoriesDTO.getProductCode()).isEqualTo(PRODUCT_CODE);
		assertThat(productCategoriesDTO.getCatalogCategoriesDTOList()).isEmpty();

		verify(mockProduct, times(1)).getCategories();
		verify(mockProduct, times(1)).getCode();

	}

	/**
	 * Tests populateDomain.
	 */
	@Test
	public void testPopulateDomain() {

		when(mockCachingService.findCatalogByCode(GOOD_CATALOG)).thenReturn(mockCatalog);
		when(mockCachingService.findCatalogByCode(NULL_CATALOG)).thenReturn(null);


		ProductCategoriesDTO productCategoriesDTO = new ProductCategoriesDTO();

		final CatalogCategoriesDTO goodCatalogCategoriesDTO = new CatalogCategoriesDTO();
		goodCatalogCategoriesDTO.setCatalogCode(GOOD_CATALOG);

		final CatalogCategoriesDTO nullCatalogCategoriesDTO = new CatalogCategoriesDTO();
		nullCatalogCategoriesDTO.setCatalogCode(NULL_CATALOG);

		productCategoriesDTO.setCatalogCategoriesDTOList(Arrays.asList(goodCatalogCategoriesDTO, nullCatalogCategoriesDTO));

		ProductCategoryAdapter adapter = new ProductCategoryAdapter() {
			@Override
			void populateProductByCatalogCategories(final Product product, final CatalogCategoriesDTO catalogCategoriesDTO) {
				assertThat(catalogCategoriesDTO).isEqualTo(goodCatalogCategoriesDTO);
				product.addCategory(mockCategory);
			}
		};
		setUpProductCategoryAdapter(adapter);

		adapter.populateDomain(productCategoriesDTO, mockProduct);

		verify(mockProduct, times(1)).addCategory(mockCategory);
		verify(mockCachingService, times(1)).findCatalogByCode(GOOD_CATALOG);
		verify(mockCachingService, times(1)).findCatalogByCode(NULL_CATALOG);
	}

	/**
	 * Tests createCatalogCategoriesDTOList.
	 */
	@Test
	public void testCreateCatalogCategoriesDTOList() {
		ProductCategoryAdapter adapter = new ProductCategoryAdapter() {
			@Override
			Map<String, List<ProductCategoryDTO>> createCatalogCategoryMap(final Product product) {
				HashMap<String, List<ProductCategoryDTO>> result = new HashMap<>();
				ProductCategoryDTO productCategoryDTO = new ProductCategoryDTO();
				productCategoryDTO.setCategoryCode(CATEGORY_CODE);
				productCategoryDTO.setFeaturedOrder(1);
				productCategoryDTO.setDefaultCategory(Boolean.FALSE);
				result.put(CATALOG_CODE, Collections.singletonList(productCategoryDTO));
				return result;
			}
		};
		setUpProductCategoryAdapter(adapter);

		List<CatalogCategoriesDTO> catalogCategoriesDTOList = adapter.createCatalogCategoriesDTOList(mockProduct);

		CatalogCategoriesDTO catalogCategoriesDTO = catalogCategoriesDTOList.get(0);
		List<ProductCategoryDTO> productCategoryDTOList = catalogCategoriesDTO.getProductCategoryDTOList();
		ProductCategoryDTO productCategoryDTO = productCategoryDTOList.get(0);

		assertThat(catalogCategoriesDTOList).size().isEqualTo(1);
		assertThat(catalogCategoriesDTO.getCatalogCode()).isEqualTo(CATALOG_CODE);
		assertThat(productCategoryDTOList).size().isEqualTo(1);
		assertThat(productCategoryDTO.getCategoryCode()).isEqualTo(CATEGORY_CODE);
		assertThat(productCategoryDTO.getFeaturedOrder()).isEqualTo(1);
		assertThat(productCategoryDTO.isDefaultCategory()).isFalse();
	}

	/**
	 * Tests createCatalogCategoryMap.
	 */
	@Test
	public void testCreateCatalogCategoryMap() {

		when(mockProduct.getCategories()).thenReturn(Collections.singleton(mockCategory));
		when(mockProduct.getFeaturedRank(mockCategory)).thenReturn(1);
		when(mockProduct.getDefaultCategory(mockCatalog)).thenReturn(mockCategory);
		when(mockCategory.getCode()).thenReturn(CATEGORY_CODE);
		when(mockCategory.getCatalog()).thenReturn(mockCatalog);
		when(mockCatalog.getCode()).thenReturn(CATALOG_CODE);

		Map<String, List<ProductCategoryDTO>> catalogCategoryMap = productCategoryAdapter.createCatalogCategoryMap(mockProduct);

		assertThat(catalogCategoryMap).size().isEqualTo(1);

		List<ProductCategoryDTO> productCategoryDTOList = catalogCategoryMap.get(CATALOG_CODE);
		assertThat(productCategoryDTOList).size().isEqualTo(1);

		ProductCategoryDTO productCategoryDTO = productCategoryDTOList.get(0);
		assertThat(productCategoryDTO.getCategoryCode()).isEqualTo(CATEGORY_CODE);
		assertThat(productCategoryDTO.getFeaturedOrder()).isEqualTo(1);
		assertThat(productCategoryDTO.isDefaultCategory()).isTrue();

		verify(mockProduct, times(1)).getCategories();
		verify(mockProduct, times(1)).getFeaturedRank(mockCategory);
		verify(mockProduct, times(1)).getDefaultCategory(mockCatalog);

		verify(mockCategory, times(1)).getCode();
		verify(mockCategory, times(1)).getCatalog();

		verify(mockCatalog, times(1)).getCode();
	}

	/**
	 * Tests addToCatalogCategoryMap.
	 */
	@Test
	public void testAddToCatalogCategoryMap() {
		Map<String, List<ProductCategoryDTO>> catalogCategoryMap = new HashMap<>();
		ProductCategoryDTO productCategoryDTO = new ProductCategoryDTO();

		productCategoryAdapter.addToCatalogCategoryMap(catalogCategoryMap, productCategoryDTO, CATALOG_CODE);

		List<ProductCategoryDTO> productCategoryDTOList = catalogCategoryMap.get(CATALOG_CODE);

		assertThat(catalogCategoryMap).size().isEqualTo(1);
		assertThat(productCategoryDTOList).size().isEqualTo(1);
		assertThat(productCategoryDTOList.get(0)).isEqualTo(productCategoryDTO);
	}

	/**
	 * Tests populateProductByCatalogCategories.
	 */
	@Test
	public void testPopulateProductByCatalogCategories() {

		when(mockCachingService.findCategoryByCode(GOOD_CATEGORY, CATALOG_CODE)).thenReturn(mockCategory);
		when(mockCachingService.findCategoryByCode(NULL_CATEGORY, CATALOG_CODE)).thenReturn(null);

		final List<ProductCategoryDTO> productCategoryDTOList = new ArrayList<>();
		productCategoryDTOList.add(createGoodProductCategoryDTO());
		productCategoryDTOList.add(createNullProductCategoryDTO());

		final CatalogCategoriesDTO catalogCategoriesDTO = new CatalogCategoriesDTO();
		catalogCategoriesDTO.setProductCategoryDTOList(productCategoryDTOList);
		catalogCategoriesDTO.setCatalogCode(CATALOG_CODE);

		ProductCategoryAdapter adapter = new ProductCategoryAdapter() {
			@Override
			void populateProductByCategory(final Product product, final Category category, final boolean isDefaultCategory) {
				product.setCategoryAsDefault(category);
			}

			@Override
			void populateProductByFeaturedOrder(final Product product, final Category category, final int featuredOrder) {
				product.setFeaturedRank(category, featuredOrder);
			}
		};
		setUpProductCategoryAdapter(adapter);

		adapter.populateProductByCatalogCategories(mockProduct, catalogCategoriesDTO);

		verify(mockProduct, times(1)).setFeaturedRank(mockCategory, 1);
		verify(mockProduct, times(1)).setCategoryAsDefault(mockCategory);

		verify(mockCachingService, times(1)).findCategoryByCode(GOOD_CATEGORY, CATALOG_CODE);
		verify(mockCachingService, times(1)).findCategoryByCode(NULL_CATEGORY, CATALOG_CODE);
	}

	private ProductCategoryDTO createNullProductCategoryDTO() {
		ProductCategoryDTO nullProductCategoryDTO = new ProductCategoryDTO();
		nullProductCategoryDTO.setCategoryCode(NULL_CATEGORY);
		return nullProductCategoryDTO;
	}

	private ProductCategoryDTO createGoodProductCategoryDTO() {
		ProductCategoryDTO goodProductCategoryDTO = new ProductCategoryDTO();
		goodProductCategoryDTO.setCategoryCode(GOOD_CATEGORY);
		goodProductCategoryDTO.setFeaturedOrder(1);
		goodProductCategoryDTO.setDefaultCategory(true);
		return goodProductCategoryDTO;
	}

	/**
	 * Tests populateProductByCategory with isDefaultCategory == true.
	 */
	@Test
	public void testPopulateProductByCategoryOnDefaultCategory() {
		productCategoryAdapter.populateProductByCategory(mockProduct, mockCategory, true);
		verify(mockProduct, times(1)).setCategoryAsDefault(mockCategory);
	}

	/**
	 * Tests populateProductByCategory with isDefaultCategory == false.
	 */
	@Test
	public void testPopulateProductByCategory() {
		productCategoryAdapter.populateProductByCategory(mockProduct, mockCategory, false);
		verify(mockProduct, times(1)).addCategory(mockCategory);
	}

	/**
	 * Tests populateProductByFeaturedOrder.
	 */
	@Test
	public void testPopulateProductByFeaturedOrder() {
		productCategoryAdapter.populateProductByFeaturedOrder(mockProduct, mockCategory, 1);
		verify(mockProduct, times(1)).setFeaturedRank(mockCategory, 1);
	}
}
