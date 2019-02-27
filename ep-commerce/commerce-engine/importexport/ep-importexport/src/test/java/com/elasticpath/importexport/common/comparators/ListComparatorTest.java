/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.importexport.common.comparators;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import org.springframework.util.comparator.ComparableComparator;

/**
 * Test for {@link com.elasticpath.importexport.common.comparators.ListComparator}.
 */
public class ListComparatorTest {

	private static final int ONE = 1;
	private static final int TWO = 2;
	private static final int THREE = 3;
	private static final int ONE_THOUSAND = 1000;

	/**
	 * Test comparing two equal lists.
	 */
	@Test
	public void testCompareListsEqual() {
		List<Integer> list1 = new ArrayList<>(Arrays.asList(ONE, TWO, THREE));
		List<Integer> list2 = new ArrayList<>(Arrays.asList(ONE, TWO, THREE));
		assertThat(new ListComparator<>(new ComparableComparator<Integer>()).compare(list1, list2)).isEqualTo(0);
	}

	/**
	 * Test comparing two equal lists, except one has an additional element.
	 */
	@Test
	public void testCompareListsDifferentSize() {
		List<Integer> list1 = new ArrayList<>(Arrays.asList(ONE, TWO, THREE, ONE));
		List<Integer> list2 = new ArrayList<>(Arrays.asList(ONE, TWO, THREE));
		assertThat(new ListComparator<>(new ComparableComparator<Integer>()).compare(list1, list2)).isEqualTo(ONE);
	}

	/**
	 * Test comparing two lists of where the second element is larger in the first list.
	 */
	@Test
	public void testCompareListsLargerSecondElement() {
		List<Integer> list1 = new ArrayList<>(Arrays.asList(ONE, TWO, THREE));
		List<Integer> list2 = new ArrayList<>(Arrays.asList(ONE, ONE, ONE_THOUSAND));
		assertThat(new ListComparator<>(new ComparableComparator<Integer>()).compare(list1, list2)).isEqualTo(ONE);
	}

	/**
	 * Test comparing two lists of where the second element is smaller in the first list.
	 */
	@Test
	public void testCompareListsSmallerSecondElement() {
		List<Integer> list1 = new ArrayList<>(Arrays.asList(ONE, TWO, THREE));
		List<Integer> list2 = new ArrayList<>(Arrays.asList(ONE, ONE_THOUSAND, ONE));
		assertThat(new ListComparator<>(new ComparableComparator<Integer>()).compare(list1, list2)).isEqualTo(-ONE);
	}
}
