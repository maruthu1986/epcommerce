/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.service.catalog.impl;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.domain.catalog.ProductAssociationType;
import com.elasticpath.service.search.query.ProductAssociationSearchCriteria;

/**
 * Unit test for ProductAssociation query building.
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class ProductAssociationQueryBuilderTest {

	private ProductAssociationQueryBuilder builder;
	private ProductAssociationSearchCriteria allCriteria;
	private Date expectedDate;

	@Before
	public void setUp() {
		builder = new ProductAssociationQueryBuilder();
		allCriteria = new ProductAssociationSearchCriteria();
		allCriteria.setAssociationType(ProductAssociationType.CROSS_SELL);
		allCriteria.setCatalogCode("TestCatalogCode");
		allCriteria.setSourceProductCode("TestSourceProductCode");
		allCriteria.setTargetProductCode("TestTargetProductCode");
		allCriteria.setWithinCatalogOnly(true);
		expectedDate = new Date();
		allCriteria.setStartDateBefore(expectedDate);
		allCriteria.setEndDateAfter(expectedDate);

	}

	@Test
	public void testBuildCountQueryWithAllCriteria() throws Exception {
		ProductAssociationQuery query = builder.buildCountQuery(allCriteria);
		String queryString = query.getQueryString();
		Object[] queryParams = query.getQueryParameters().toArray();

		assertEquals("SELECT COUNT(pa.uidPk)"
					+ " FROM ProductAssociationImpl pa,"
					+ 		" IN(pa.sourceProduct.productCategories) spc,"
					+ 		" IN(pa.targetProduct.productCategories) tpc"
					+ " WHERE"
					+ 		" tpc.category.catalog = pa.catalog"
					+ 		" AND spc.category.catalog = pa.catalog"
					+ 		" AND pa.associationType = ?1"
					+ 		" AND pa.sourceProduct.code = ?2"
					+ 		" AND pa.targetProduct.code = ?3"
					+		" AND pa.targetProduct.hidden = ?4"
					+		" AND pa.targetProduct.notSoldSeparately = ?5"
					+ 		" AND pa.catalog.code = ?6"
					+ 		" AND (pa.startDateInternal IS NULL OR pa.startDateInternal <= ?7)"
					+ 		" AND (pa.endDateInternal IS NULL OR pa.endDateInternal > ?8)",
				queryString);
		assertEquals(new Object[] {
				ProductAssociationType.CROSS_SELL,
				"TestSourceProductCode",
				"TestTargetProductCode",
				false,
				false,
				"TestCatalogCode",
				expectedDate,
				expectedDate
				},
				queryParams);
	}

	@Test
	public void testBuildCountQueryWithAllCriteriaAcrossCatalogs() throws Exception {
		allCriteria.setWithinCatalogOnly(false);
		ProductAssociationQuery query = builder.buildCountQuery(allCriteria);
		String queryString = query.getQueryString();
		assertEquals("SELECT COUNT(pa.uidPk)"
				+ " FROM ProductAssociationImpl pa"
				+ " WHERE"
				+ 		" pa.associationType = ?1"
				+ 		" AND pa.sourceProduct.code = ?2"
				+ 		" AND pa.targetProduct.code = ?3"
			 	+		" AND pa.targetProduct.hidden = ?4"
			 	+		" AND pa.targetProduct.notSoldSeparately = ?5"
				+ 		" AND pa.catalog.code = ?6"
				+ 		" AND (pa.startDateInternal IS NULL OR pa.startDateInternal <= ?7)"
				+ 		" AND (pa.endDateInternal IS NULL OR pa.endDateInternal > ?8)",
				queryString);
	}

	@Test
	public void testBuildCountQueryWithNoAssocTypeCriteria() throws Exception {
		allCriteria.setWithinCatalogOnly(false);
		allCriteria.setAssociationType(null);
		ProductAssociationQuery query = builder.buildCountQuery(allCriteria);
		String queryString = query.getQueryString();
		assertEquals("SELECT COUNT(pa.uidPk)"
				+ " FROM ProductAssociationImpl pa"
				+ " WHERE"
				+ 		" pa.sourceProduct.code = ?1"
				+ 		" AND pa.targetProduct.code = ?2"
				+		" AND pa.targetProduct.hidden = ?3"
				+		" AND pa.targetProduct.notSoldSeparately = ?4"
				+ 		" AND pa.catalog.code = ?5"
				+ 		" AND (pa.startDateInternal IS NULL OR pa.startDateInternal <= ?6)"
				+ 		" AND (pa.endDateInternal IS NULL OR pa.endDateInternal > ?7)",
				queryString);
	}

	@Test
	public void testBuildCountQueryWithNoCatalogCriteria() throws Exception {
		allCriteria.setWithinCatalogOnly(false);
		allCriteria.setCatalogCode(null);
		ProductAssociationQuery query = builder.buildCountQuery(allCriteria);
		String queryString = query.getQueryString();
		assertEquals("SELECT COUNT(pa.uidPk)"
				+ " FROM ProductAssociationImpl pa"
				+ " WHERE"
				+ 		" pa.associationType = ?1"
				+ 		" AND pa.sourceProduct.code = ?2"
				+ 		" AND pa.targetProduct.code = ?3"
				+		" AND pa.targetProduct.hidden = ?4"
				+		" AND pa.targetProduct.notSoldSeparately = ?5"
				+ 		" AND (pa.startDateInternal IS NULL OR pa.startDateInternal <= ?6)"
				+ 		" AND (pa.endDateInternal IS NULL OR pa.endDateInternal > ?7)",
				queryString);
	}

	@Test
	public void testBuildCountQueryWithNoEndDateCriteria() throws Exception {
		allCriteria.setWithinCatalogOnly(false);
		allCriteria.setEndDateAfter(null);
		ProductAssociationQuery query = builder.buildCountQuery(allCriteria);
		String queryString = query.getQueryString();
		assertEquals("SELECT COUNT(pa.uidPk)"
				+ " FROM ProductAssociationImpl pa"
				+ " WHERE"
				+ 		" pa.associationType = ?1"
				+ 		" AND pa.sourceProduct.code = ?2"
				+ 		" AND pa.targetProduct.code = ?3"
				+		" AND pa.targetProduct.hidden = ?4"
				+		" AND pa.targetProduct.notSoldSeparately = ?5"
				+ 		" AND pa.catalog.code = ?6"
				+ 		" AND (pa.startDateInternal IS NULL OR pa.startDateInternal <= ?7)",
				queryString);
	}

	@Test
	public void testBuildCountQueryWithNoStartDateCriteria() throws Exception {
		allCriteria.setWithinCatalogOnly(false);
		allCriteria.setStartDateBefore(null);
		ProductAssociationQuery query = builder.buildCountQuery(allCriteria);
		String queryString = query.getQueryString();
		assertEquals("SELECT COUNT(pa.uidPk)"
				+ " FROM ProductAssociationImpl pa"
				+ " WHERE"
				+ 		" pa.associationType = ?1"
				+ 		" AND pa.sourceProduct.code = ?2"
				+ 		" AND pa.targetProduct.code = ?3"
				+		" AND pa.targetProduct.hidden = ?4"
				+		" AND pa.targetProduct.notSoldSeparately = ?5"
				+ 		" AND pa.catalog.code = ?6"
				+ 		" AND (pa.endDateInternal IS NULL OR pa.endDateInternal > ?7)",
				queryString);
	}

	@Test
	public void testBuildCountQueryWithNoDateCriteria() throws Exception {
		allCriteria.setStartDateBefore(null);
		allCriteria.setEndDateAfter(null);
		ProductAssociationQuery query = builder.buildCountQuery(allCriteria);
		String queryString = query.getQueryString();
		assertEquals("SELECT COUNT(pa.uidPk)"
				+ " FROM ProductAssociationImpl pa,"
				+ 		" IN(pa.sourceProduct.productCategories) spc,"
				+ 		" IN(pa.targetProduct.productCategories) tpc"
				+ " WHERE"
				+ 		" tpc.category.catalog = pa.catalog"
				+ 		" AND spc.category.catalog = pa.catalog"
				+ 		" AND pa.associationType = ?1"
				+ 		" AND pa.sourceProduct.code = ?2"
				+ 		" AND pa.targetProduct.code = ?3"
				+		" AND pa.targetProduct.hidden = ?4"
				+		" AND pa.targetProduct.notSoldSeparately = ?5"
				+ 		" AND pa.catalog.code = ?6",
				queryString);
	}
}
