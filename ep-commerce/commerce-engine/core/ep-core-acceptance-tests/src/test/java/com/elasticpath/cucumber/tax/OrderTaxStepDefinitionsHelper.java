/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.cucumber.tax;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.common.collect.Sets;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.cucumber.ScenarioContextValueHolder;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderReturn;
import com.elasticpath.domain.order.TaxJournalRecord;
import com.elasticpath.domain.order.impl.TaxJournalRecordImpl;
import com.elasticpath.tax.OrderTaxTestVerifier;

/**
 * Help class for {@link OrderTaxStepDefinitions}.
 */
public class OrderTaxStepDefinitionsHelper {

	@Inject
	@Named("orderHolder")
	private ScenarioContextValueHolder<Order> orderHolder;

	@Inject
	@Named("orderReturnHolder")
	private ScenarioContextValueHolder<OrderReturn> orderReturnHolder;

	@Autowired
	private OrderTaxTestVerifier orderTaxTestVerifier;

	/**
	 * Verifies the tax journal entries for the cancelled order shipment.
	 *
	 * @param taxJournalData the expected tax journals for verifying
	 */
	public void verifyTaxJournalEntriesForOrder(final List<Map<String, String>> taxJournalData) {

		verifyTaxJournalEntriesWithExpectedRecords(orderHolder.get(), taxJournalData);
	}

	/**
	 * Verifies the tax journal entries for the cancelled order shipment.
	 *
	 * @param taxJournalData the expected tax journals for verifying
	 */
	public void verifyTaxJournalEntriesForExchange(final List<Map<String, String>> taxJournalData) {

		verifyTaxJournalEntriesWithExpectedRecords(orderReturnHolder.get().getExchangeOrder(), taxJournalData);
	}

	private void verifyTaxJournalEntriesWithExpectedRecords(final Order order, final List<Map<String, String>> taxJournalData) {

		orderTaxTestVerifier.verifyOrderTaxDocumentWithExpectedRecords(
				order.getOrderNumber(),
				populateTaxJournals(taxJournalData),
				getExcludeFieldNames(taxJournalData));
	}

	/**
	 * Verifies fields on an order.
	 *
	 * @param orderEntriesMapList the expected order fields
	 */
	public void verifyOrderEntries(final List<Map<String, String>> orderEntriesMapList) {
		verifyTaxJournalEntriesForOrder(orderEntriesMapList);

		Order order = orderHolder.get();
		for (Map<String, String> expectedOrderEntriesMap : orderEntriesMapList) {
			assertEquals("Before Tax Subtotal amount did not match the expected value", order.getBeforeTaxSubtotalMoney().getAmount().toString(),
					expectedOrderEntriesMap.get("beforeTaxSubtotal"));
		}
	}

	private List<TaxJournalRecord> populateTaxJournals(final List<Map<String, String>> taxJournalData) {

		List<TaxJournalRecord> taxJournals = new ArrayList<>();

		try {
			for (Map<String, String> properties : taxJournalData) {

				TaxJournalRecord taxJournal = new TaxJournalRecordImpl();
				BeanUtils.populate(taxJournal, properties);
				taxJournals.add(taxJournal);
			}
		} catch (Exception exc) {
			fail(exc.getMessage());
		}

		return taxJournals;
	}

	@SuppressWarnings("unchecked")
	private Set<String> getExcludeFieldNames(final List<Map<String, String>> taxJournalData) {

		Set<String> taxJournalRecordFieldNames = new HashSet<>();

		try {
			taxJournalRecordFieldNames = BeanUtils.describe(new TaxJournalRecordImpl()).keySet();
		} catch (Exception exc) {
			fail(exc.getMessage());
		}

		Set<String> includeFieldNames = new HashSet<>(taxJournalData.get(0).keySet());

		return Sets.symmetricDifference(taxJournalRecordFieldNames, includeFieldNames).immutableCopy();
	}
}
