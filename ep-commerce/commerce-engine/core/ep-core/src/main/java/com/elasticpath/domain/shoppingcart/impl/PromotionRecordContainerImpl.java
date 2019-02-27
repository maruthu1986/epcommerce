/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.domain.shoppingcart.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Table;
import org.apache.commons.lang3.StringUtils;

import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.rules.RuleAction;
import com.elasticpath.domain.shoppingcart.DiscountRecord;
import com.elasticpath.domain.shoppingcart.MutablePromotionRecordContainer;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.shipping.connectivity.dto.ShippingOption;

/**
 * Keep track of applied discount records by rules and actions.
 */
public class PromotionRecordContainerImpl implements MutablePromotionRecordContainer {

	private static final long serialVersionUID = 5000000001L;

	private final Table<Long, Long, DiscountRecord> discountRecordTable = HashBasedTable.create();

	private final Map<String, Long> limitedUsagePromotionRuleCodes = new HashMap<>();

	private final ShoppingCart shoppingCart;

	/**
	 * Constructor.
	 *
	 * @param shoppingCart the shopping cart corresponding to this record container
	 */
	public PromotionRecordContainerImpl(final ShoppingCart shoppingCart) {
		this.shoppingCart = shoppingCart;
	}

	@Override
	public void addDiscountRecord(final DiscountRecord discountRecord) {
		discountRecordTable.put(discountRecord.getRuleId(), discountRecord.getActionId(), discountRecord);
	}

	/**
	 * Get a discount record.
	 *
	 * @param ruleId the ID of the rule
	 * @param actionId the ID of the action
	 * @return the discount record corresponding to the given rule and action
	 */
	public DiscountRecord getDiscountRecord(final long ruleId, final long actionId) {
		return discountRecordTable.get(ruleId, actionId);
	}

	@Override
	public DiscountRecord getDiscountRecord(final Rule rule, final RuleAction action) {
		return getDiscountRecord(rule.getUidPk(), action.getUidPk());
	}

	@Override
	public void clear() {
		discountRecordTable.clear();
		limitedUsagePromotionRuleCodes.clear();
	}

	@Override
	public Map<String, Long> getLimitedUsagePromotionRuleCodes() {
		return ImmutableMap.copyOf(limitedUsagePromotionRuleCodes);
	}

	@Override
	public void addLimitedUsagePromotionRuleCode(final String ruleCode, final long ruleId) {
		limitedUsagePromotionRuleCodes.put(ruleCode, ruleId);
	}

	@Override
	public void removeLimitedUsagePromotionRuleCode(final String ruleCode) {
		if (StringUtils.isNotBlank(ruleCode)) {
			limitedUsagePromotionRuleCodes.remove(ruleCode);
		}
	}

	@Override
	public Collection<DiscountRecord> getAllDiscountRecords() {
		return Collections.unmodifiableCollection(discountRecordTable.values());
	}

	@Override
	public Collection<Long> getAppliedRulesByLineItem(final String lineItemId) {
		Set<Long> appliedRuleIds = new HashSet<>();
		for (Table.Cell<Long, Long, DiscountRecord> tableCell : discountRecordTable.cellSet()) {
			DiscountRecord discountRecord = tableCell.getValue();
			if (discountRecord instanceof ItemDiscountRecordImpl) {
				ItemDiscountRecordImpl itemDiscountRecord = (ItemDiscountRecordImpl) discountRecord;
				ShoppingItem shoppingItem = itemDiscountRecord.getShoppingItem();
				if (StringUtils.equals(shoppingItem.getGuid(), lineItemId)) {
					appliedRuleIds.add(tableCell.getRowKey());
				}
			}
		}
		return appliedRuleIds;
	}

	@Override
	public Set<Long> getAppliedRules() {
		return FluentIterable.from(discountRecordTable.cellSet())
				.filter(Predicates.compose(getPredicateForShoppingCartAppliedRules(), new TableCellToDiscountRecordFunction()))
				.transform(new TableCellToRowKeyFunction())
				.toSet();
	}

	@Override
	public Set<Long> getAppliedRulesByShippingOption(final ShippingOption shippingOption) {
		return FluentIterable.from(discountRecordTable.cellSet())
				.filter(Predicates.compose(new IsShippingRecordTypePredicate(), new TableCellToDiscountRecordFunction()))
				.filter(Predicates.compose(new MatchesShippingOptionPredicate(shippingOption),
										   new TableCellToShippingDiscountRecordFunction()))
				.filter(Predicates.compose(getPredicateForShippingOptionAppliedRules(), new TableCellToShippingDiscountRecordFunction()))
				.transform(new TableCellToRowKeyFunction())
				.toSet();
	}

	/**
	 * Function that takes as input a cell of the discount record table, and returns the row ID.
	 */
	private static class TableCellToRowKeyFunction implements Function<Table.Cell<Long, Long, DiscountRecord>, Long> {

		@Override
		public Long apply(final Table.Cell<Long, Long, DiscountRecord> input) {
			return input.getRowKey();
		}

	}

	/**
	 * Function that takes as input a cell of the discount record table, and returns the discount record.
	 */
	private static class TableCellToDiscountRecordFunction implements Function<Table.Cell<Long, Long, DiscountRecord>, DiscountRecord> {

		@Override
		public DiscountRecord apply(final Table.Cell<Long, Long, DiscountRecord> input) {
			return input.getValue();
		}

	}

	/**
	 * Predicate that returns true when the given record can be cast to a {@link ShippingDiscountRecordImpl}.
	 */
	private static class IsShippingRecordTypePredicate implements Predicate<DiscountRecord> {

		@Override
		public boolean apply(final DiscountRecord input) {
			return input instanceof ShippingDiscountRecordImpl;
		}

	}

	/**
	 * Function that takes as input a cell of the discount record table, and returns the discount record cast as a {@link ShippingDiscountRecordImpl}.
	 */
	private static class TableCellToShippingDiscountRecordFunction
			implements Function<Table.Cell<Long, Long, DiscountRecord>, ShippingDiscountRecordImpl> {

		@Override
		public ShippingDiscountRecordImpl apply(final Table.Cell<Long, Long, DiscountRecord> input) {
			return (ShippingDiscountRecordImpl) input.getValue();
		}

	}

	/**
	 * Predicate that returns true if the given shipping discount record matches the provided shipping option.
	 */
	private static class MatchesShippingOptionPredicate implements Predicate<ShippingDiscountRecordImpl> {

		private final ShippingOption shippingOption;

		/**
		 * Constructor.
		 *
		 * @param shippingOption the shipping option against which inputs will be compared
		 */
		MatchesShippingOptionPredicate(final ShippingOption shippingOption) {
			this.shippingOption = shippingOption;
		}

		@Override
		public boolean apply(final ShippingDiscountRecordImpl input) {
			return input.getShippingOptionCode().equals(shippingOption.getCode());
		}

	}

	/**
	 * Factory method for returning a predicate instance used to filter the records returned by
	 * {@link #getAppliedRules()}.
	 *
	 * @return a predicate instance
	 */
	protected Predicate<DiscountRecord> getPredicateForShoppingCartAppliedRules() {
		return input -> {
			if (input instanceof ShippingDiscountRecordImpl) {
				final Optional<ShippingOption> selectedShippingOption = shoppingCart.getSelectedShippingOption();

				return selectedShippingOption.isPresent()
						&& ((ShippingDiscountRecordImpl) input).getShippingOptionCode().equals(selectedShippingOption.get().getCode());
			}

			return true;
		};
	}

	/**
	 * Factory method for returning a predicate instance used to filter the records returned by
	 * {@link #getAppliedRulesByShippingOption(ShippingOption)}.
	 *
	 * @return a predicate instance
	 */
	protected Predicate<ShippingDiscountRecordImpl> getPredicateForShippingOptionAppliedRules() {
		return input -> !input.isSuperceded();
	}

}
