/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.domain.misc.impl;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Arrays;

import org.assertj.core.internal.ComparatorBasedComparisonStrategy;
import org.junit.Before;
import org.junit.Test;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.domain.shipping.Region;
import com.elasticpath.domain.shipping.impl.RegionImpl;

/**
 * Test <code>RegionCodeComparatorImpl</code>.
 */
public class RegionCodeComparatorImplTest {

	private static final String COUNTRY_CODE_CA = "CA";

	private static final String SUB_COUNTRY_CODE_AB = "AB";

	private static final String SUB_COUNTRY_CODE_BC = "BC";

	private static final String COUNTRY_CODE_US = "US";

	private RegionCodeComparatorImpl regionCountryCodeComparatorImpl;

	@Before
	public void setUp() throws Exception {
		this.regionCountryCodeComparatorImpl = new RegionCodeComparatorImpl();
	}

	/**
	 * Test method for 'compare(Object, Object)'.
	 */
	@Test
	public void testCompare() {

		Region region1 = new RegionImpl(COUNTRY_CODE_CA,
				Arrays.asList(SUB_COUNTRY_CODE_AB, SUB_COUNTRY_CODE_BC));
		Region region2 = new RegionImpl(COUNTRY_CODE_US);
		Region region3 = new RegionImpl(COUNTRY_CODE_CA);

		ComparatorBasedComparisonStrategy comparisonAssert = new ComparatorBasedComparisonStrategy(regionCountryCodeComparatorImpl);
		comparisonAssert.isLessThan(region1, region2);
		comparisonAssert.isGreaterThan(region2, region1);
		comparisonAssert.areEqual(region1, region3);
	}

	/**
	 * Test method for 'compare(Object, Object)'.
	 */
	@Test
	public void testCompareError() {
		Region emptyRegion = new RegionImpl();
		emptyRegion.setCountryCode(null);
		Region validRegion = new RegionImpl("CA");

		assertThatThrownBy(() -> regionCountryCodeComparatorImpl.compare(emptyRegion, validRegion))
			.isInstanceOf(EpSystemException.class)
			.withFailMessage("Failed to compare Region object with null countryCode.");

		assertThatThrownBy(() -> regionCountryCodeComparatorImpl.compare(validRegion, emptyRegion))
			.isInstanceOf(EpSystemException.class)
			.withFailMessage("Failed to compare Region object with null countryCode.");

		assertThatThrownBy(() -> regionCountryCodeComparatorImpl.compare(null, validRegion))
			.isInstanceOf(EpSystemException.class)
			.withFailMessage("Failed to compare Region object with null countryCode.");

	}
}

