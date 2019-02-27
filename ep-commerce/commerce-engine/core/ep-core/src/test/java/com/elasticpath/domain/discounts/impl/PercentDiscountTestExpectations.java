/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.domain.discounts.impl;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;

import com.google.common.collect.Lists;
import org.jmock.Expectations;
import org.jmock.Mockery;

import com.elasticpath.domain.discounts.DiscountItemContainer;
import com.elasticpath.domain.discounts.TotallingApplier;
import com.elasticpath.domain.shoppingcart.ShoppingItem;

/**
 * Expectation to simplify percent discount tests.
 */
public class PercentDiscountTestExpectations {

	private final Mockery context;
	private final DiscountItemContainer container;
	private final TotallingApplier totallingApplier;
	private final long ruleId;
	private final long actionId;

	/**
	 * Constructor taking all mandatory info from the calling class.
	 *
	 * @param context the mock context
	 * @param container the discount item container
	 * @param totallingApplier the totalling applier
	 * @param ruleId the ID of the rule
	 * @param actionId the ID of the action
	 */
	public PercentDiscountTestExpectations(final Mockery context, final DiscountItemContainer container, final TotallingApplier totallingApplier,
			final long ruleId, final long actionId) {
		this.context = context;
		this.container = container;
		this.totallingApplier = totallingApplier;
		this.ruleId = ruleId;
		this.actionId = actionId;

		context.checking(new Expectations() {
			{
				ignoring(container).getCatalog();
			}
		});
	}

	/**
	 * Expectations for a totalling applier with a maximum items and whether it applies or not.
	 *
	 * @param maxItems the max items
	 * @param applies whether this applier should apply
	 */
	public void givenATotallingApplierWithMaxItemsThatApplies(final int maxItems, final boolean applies) {
		context.checking(new Expectations() {
			{
				allowing(totallingApplier).setActuallyApply(applies);
				allowing(totallingApplier).initializeMaxItems(maxItems);
				allowing(totallingApplier).setDiscountItemContainer(container);
				allowing(totallingApplier).setRuleId(ruleId);
				allowing(totallingApplier).setActionId(actionId);
			}
		});
	}

	/**
	 * Expectations for a collection of priced shopping items.
	 *
	 * @param itemPriceMap a map of shopping item to price
	 */
	public void givenItemsWithPrices(final Map<ShoppingItem, BigDecimal> itemPriceMap) {
		context.checking(new Expectations() {
			{
				allowing(container).getItemsLowestToHighestPrice();
				will(returnValue(Lists.newArrayList(itemPriceMap.keySet())));

				for (final Map.Entry<ShoppingItem, BigDecimal> entry : itemPriceMap.entrySet()) {
					allowing(container).getPriceAmount(entry.getKey());
					will(returnValue(entry.getValue()));
				}
			}
		});
	}

	/**
	 * Expectations for an empty shopping container.
	 */
	public void givenAnEmptyShoppingContainer() {
		context.checking(new Expectations() {
			{
				oneOf(container).getItemsLowestToHighestPrice();
				will(returnValue(Collections.emptyList()));
			}
		});
	}
}
