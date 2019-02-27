/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.test.db;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.persistence.openjpa.support.JpqlQueryBuilder;
import com.elasticpath.persistence.openjpa.support.JpqlQueryBuilderWhereGroup;
import com.elasticpath.persistence.openjpa.support.JpqlQueryBuilderWhereGroup.ConjunctionType;
import com.elasticpath.test.integration.BasicSpringContextTest;

/**
 * Test class for {@link com.elasticpath.persistence.openjpa.support.JpqlQueryBuilder}.
 */
public class JpqlQueryBuilderTest extends BasicSpringContextTest {

	private static final String CUST_ALIAS = "cust";
	private static final String CUST_TABLE = "CustomerImpl";

	@Autowired
	private PersistenceEngine persistenceEngine;

	/**
	 * Test that a query with an arbitrary inner join will execute without errors.
	 */
	@Test
	public void testBuilderWithInnerThetaJoin() {
		final JpqlQueryBuilder query = new JpqlQueryBuilder("OrderSkuImpl", "sku");
		query.appendInnerJoin("sku.shipment", "osh");
		query.appendInnerJoin("DataChangedImpl", "dc", "dc.objectName = 'OrderSkuImpl' AND dc.objectUid = sku.uidPk");

		persistenceEngine.retrieve(query.buildQuery());
	}

	/**
	 * Test that a query with inner joins will execute without errors.
	 */
	@Test
	public void testBuilderWithInnerJoins() {
		final JpqlQueryBuilder query = new JpqlQueryBuilder("OrderSkuImpl", "sku");
		query.appendInnerJoin("sku.shipment", "osh");
		query.appendInnerJoin("osh.orderInternal", "o");
		query.appendInnerJoin("o.billingAddress", "addr");
		query.createNewWhereGroup().appendWhere("addr.city", "=", "Vancouver", JpqlQueryBuilderWhereGroup.JpqlMatchType.AS_IS);

		persistenceEngine.retrieve(query.buildQuery());
	}

	/**
	 * Test that that appending the builder with a join clause and a few AND clauses having a nested OR clause
	 * will execute without errors.
	 */
	@Test
	public void testBuilderWithAndNestedOrJoin() {
		JpqlQueryBuilder query = new JpqlQueryBuilder(CUST_TABLE, CUST_ALIAS);

		// Append inner joins
		query.appendInnerJoin("cust.preferredBillingAddressInternal", "addr");

		// Append where clause
		JpqlQueryBuilderWhereGroup andGroup = query.getDefaultWhereGroup();
		andGroup.appendWhereEquals("cust.status", Customer.STATUS_ACTIVE);
		JpqlQueryBuilderWhereGroup orGroup = new JpqlQueryBuilderWhereGroup(ConjunctionType.OR);
		orGroup.appendLikeWithWildcards("cust.userId", "Smith");
		orGroup.appendLikeWildcardOnStart("cust.userId", "Jones");
		andGroup.appendWhereGroup(orGroup);
		andGroup.appendLikeWildcardOnEnd("cust.userId", "Edwards");

		// Append order by clause
		query.appendOrderBy("addr.country", true);
		query.appendOrderBy("addr.subCountry", true);
		query.appendOrderBy("addr.city", true);

		persistenceEngine.retrieve(query.buildQuery(), query.buildParameterList().toArray());
	}

	/**
	 * Test that a builder utilizing distinct will execute without errors.
	 */
	@Test
	public void testBuilderWithDistinct() {
		JpqlQueryBuilder query = new JpqlQueryBuilder(CUST_TABLE, CUST_ALIAS);
		query.distinct();

		persistenceEngine.retrieve(query.buildQuery(), query.buildParameterList().toArray());
	}

	/**
	 * Test that a builder utilizing count will execute without errors.
	 */
	@Test
	public void testBuilderWithCount() {
		JpqlQueryBuilder query = new JpqlQueryBuilder(CUST_TABLE, CUST_ALIAS);
		query.count();

		persistenceEngine.retrieve(query.buildQuery(), query.buildParameterList().toArray());
	}

}
