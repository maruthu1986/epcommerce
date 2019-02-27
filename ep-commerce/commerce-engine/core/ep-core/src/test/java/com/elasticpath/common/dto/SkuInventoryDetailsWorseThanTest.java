/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.common.dto;

import static org.hamcrest.MatcherAssert.assertThat;

import static com.elasticpath.domain.catalog.AvailabilityCriteria.ALWAYS_AVAILABLE;
import static com.elasticpath.domain.catalog.AvailabilityCriteria.AVAILABLE_FOR_BACK_ORDER;
import static com.elasticpath.domain.catalog.AvailabilityCriteria.AVAILABLE_FOR_PRE_ORDER;
import static com.elasticpath.domain.catalog.AvailabilityCriteria.AVAILABLE_WHEN_IN_STOCK;
import static com.elasticpath.service.catalogview.impl.InventoryMessage.AVAILABLE_FOR_BACKORDER;
import static com.elasticpath.service.catalogview.impl.InventoryMessage.AVAILABLE_FOR_PREORDER;
import static com.elasticpath.service.catalogview.impl.InventoryMessage.IN_STOCK;
import static com.elasticpath.service.catalogview.impl.InventoryMessage.OUT_OF_STOCK;
import static com.elasticpath.service.catalogview.impl.InventoryMessage.OUT_OF_STOCK_WITH_RESTOCK_DATE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.annotation.Nullable;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import org.apache.commons.lang3.tuple.Pair;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;

import com.elasticpath.domain.catalog.AvailabilityCriteria;
import com.elasticpath.service.catalogview.impl.InventoryMessage;

/**
 * Test for SkuInventoryDetails.worseThan.
 */
public class SkuInventoryDetailsWorseThanTest {

	@Test
	public void worseInventoryMessageContract() {
		List<InventoryMessage> worseToBest = Arrays.asList(
				OUT_OF_STOCK,
				OUT_OF_STOCK_WITH_RESTOCK_DATE,
				AVAILABLE_FOR_PREORDER,
				AVAILABLE_FOR_BACKORDER,
				IN_STOCK
		);

		for (Pair<InventoryMessage, InventoryMessage> pairOfInventoryMessage : everyAdjacentPair(worseToBest)) {
			InventoryMessage leftInventoryMessage = pairOfInventoryMessage.getLeft();
			InventoryMessage rightInventoryMessage = pairOfInventoryMessage.getRight();

			for (Pair<AvailabilityCriteria, AvailabilityCriteria> pairOfAvailabilityCriteria : pairwiseCombination(AvailabilityCriteria.values())) {
				AvailabilityCriteria leftAvailabilityCriteria = pairOfAvailabilityCriteria.getLeft();
				AvailabilityCriteria rightAvailabilityCriteria = pairOfAvailabilityCriteria.getRight();

				assertThat(String.format("%s is worse than %s for all availabilities", leftInventoryMessage, rightInventoryMessage),
							sku(leftInventoryMessage, leftAvailabilityCriteria), worseThan(sku(rightInventoryMessage, rightAvailabilityCriteria)));
			}
		}
	}

	@Test
	public void inStockIsWorseThanAlwaysAvailableContract() {
		for (AvailabilityCriteria availabilityCriteria
				: Arrays.asList(AVAILABLE_WHEN_IN_STOCK, AVAILABLE_FOR_PRE_ORDER, AVAILABLE_FOR_BACK_ORDER)) {
			assertThat("in stock is worse than always available",
					sku(IN_STOCK, availabilityCriteria), worseThan(sku(IN_STOCK, ALWAYS_AVAILABLE)));
		}
	}

	@Test
	public void lessStockIsWorseContract() {
		final int smallStock = 10;
		final int largeStock = 100;
		SkuInventoryDetails lesserAvailableQuantityInStock = sku(IN_STOCK, ALWAYS_AVAILABLE, smallStock);
		SkuInventoryDetails greaterAvailableQuantityInStock = sku(IN_STOCK, ALWAYS_AVAILABLE, largeStock);
		assertThat("when both in stock, least availableQuantityInStock is worse",
				lesserAvailableQuantityInStock, worseThan(greaterAvailableQuantityInStock));
	}

	@Test
	public void laterStockDateIsWorseContract() {
		for (InventoryMessage inventoryMessage
				: Arrays.asList(OUT_OF_STOCK, OUT_OF_STOCK_WITH_RESTOCK_DATE, AVAILABLE_FOR_PREORDER, AVAILABLE_FOR_BACKORDER)) {
			for (AvailabilityCriteria availabilityCriteria
					: Arrays.asList(AVAILABLE_WHEN_IN_STOCK, AVAILABLE_FOR_PRE_ORDER, AVAILABLE_FOR_BACK_ORDER)) {
				SkuInventoryDetails earlierStockDate = sku(inventoryMessage, availabilityCriteria, now());
				SkuInventoryDetails laterStockDate = sku(inventoryMessage, availabilityCriteria, nextMonth());
				assertThat("later stockDate is worse",
						laterStockDate, worseThan(earlierStockDate));
			}
		}
	}

	private <E> Collection<Pair<E, E>> everyAdjacentPair(final List<E> elements) {
		Collection<Pair<E, E>> result = new ArrayList<>();
		for (int i = 0; i < elements.size() - 1; i++) {
			result.add(Pair.of(elements.get(i), elements.get(i + 1)));
		}
		return result;
	}

	private <E> Collection<Pair<E, E>> pairwiseCombination(final E[] elements) {
		return Collections2.transform(
				Collections2.filter(
						Collections2.permutations(
								Arrays.asList(elements)), this.<E>sizeOf(2)), this.<E>listToPair());
	}

	private <E> Function<? super List<E>, Pair<E, E>> listToPair() {
		return new Function<List<E>, Pair<E, E>>() {
			@Nullable
			@Override
			public Pair<E, E> apply(@Nullable final List<E> list) {
				if (list == null) {
					return null;
				}
				assert list.size() == 2;
				return Pair.of(list.get(0), list.get(1));
			}
		};
	}

	private <E> Predicate<? super Collection<E>> sizeOf(final int size) {
		return new Predicate<Collection<E>>() {
			@Override
			public boolean apply(@Nullable final Collection<E> list) {
				return list != null && list.size() == size;
			}
		};
	}

	private Date now() {
		return new GregorianCalendar().getTime();
	}

	private Date nextMonth() {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.add(GregorianCalendar.MONTH, 1);
		return calendar.getTime();
	}

	private SkuInventoryDetails sku(
			final InventoryMessage inventoryMessage, final AvailabilityCriteria availabilityCriteria) {
		return sku(inventoryMessage, availabilityCriteria, 0, null);
	}

	private SkuInventoryDetails sku(
			final InventoryMessage inventoryMessage, final AvailabilityCriteria availabilityCriteria, final int availableQuantityInStock) {
		return sku(inventoryMessage, availabilityCriteria, availableQuantityInStock, null);
	}

	private SkuInventoryDetails sku(
			final InventoryMessage inventoryMessage, final AvailabilityCriteria availabilityCriteria, final Date stockDate) {
		return sku(inventoryMessage, availabilityCriteria, 0, stockDate);
	}

	private SkuInventoryDetails sku(
			final InventoryMessage inventoryMessage,
			final AvailabilityCriteria availabilityCriteria,
			final int availableQuantityInStock,
			final Date stockDate) {
		SkuInventoryDetails sku = new SkuInventoryDetails();
		sku.setMessageCode(inventoryMessage);
		sku.setAvailabilityCriteria(availabilityCriteria);
		sku.setAvailableQuantityInStock(availableQuantityInStock);
		sku.setStockDate(stockDate);
		return sku;
	}

	private Matcher<SkuInventoryDetails> worseThan(final SkuInventoryDetails right) {
		return new TypeSafeMatcher<SkuInventoryDetails>() {
			@Override
			protected boolean matchesSafely(final SkuInventoryDetails left) {
				return left.worseThan(right);
			}

			@Override
			public void describeMismatchSafely(final SkuInventoryDetails left, final Description mismatchDescription) {
				mismatchDescription.appendValue(left).appendText(" was not worse than ")
						.appendValue(right);
			}

			@Override
			public void describeTo(final Description description) {
				description.appendText("worse than ").appendValue(right);
			}
		};
	}

}
