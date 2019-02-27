/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.domain.shipping.impl;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.shipping.Region;

/**
 * Test cases for <code>RegionImpl</code>.
 */
public class RegionImplTest {

	private static final String CA_REGION_STR = "[CA(AB,BC)]";

	private static final String US_REGRION_STR = "[US()]";

	private static final String COUNTRY_CODE_CA = "CA";

	private static final String SUB_COUNTRY_CODE_AB = "AB";

	private static final String SUB_COUNTRY_CODE_BC = "BC";

	private static final String COUNTRY_CODE_US = "US";

	private static final String EP_DOMAIN_EXCEPTION_EXPECTED = "EpDomainException expected.";


	private RegionImpl regionImpl;

	/**
	 * Prepare for each test.
	 *
	 * @throws Exception on error
	 */
	@Before
	public void setUp() throws Exception {
		this.regionImpl = new RegionImpl();
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.RegionImpl.getCountryCode()'.
	 */
	@Test
	public void testGetSetCountryCode() {
		this.regionImpl.setCountryCode(COUNTRY_CODE_CA);
		assertThat(this.regionImpl.getCountryCode()).isEqualTo(COUNTRY_CODE_CA);

	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.RegionImpl.getSubCountryCodeList()'.
	 */
	@Test
	public void testGetSetSubCountryCodeList() {
		this.regionImpl.setSubCountryCodeList(emptyList());
		assertThat(this.regionImpl.getSubCountryCodeList()).isEmpty();
		assertThatThrownBy(() -> this.regionImpl.setSubCountryCodeList(null)).as(EP_DOMAIN_EXCEPTION_EXPECTED)
				.isInstanceOf(EpDomainException.class);

		this.regionImpl.setSubCountryCodeList(Arrays.asList(SUB_COUNTRY_CODE_AB, SUB_COUNTRY_CODE_BC));
		assertThat(this.regionImpl.getSubCountryCodeList()).contains(SUB_COUNTRY_CODE_AB, SUB_COUNTRY_CODE_BC);
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.RegionImpl.toString()'.
	 */
	@Test
	public void testToString() {
		Region caRegion = new RegionImpl(COUNTRY_CODE_CA,
				Arrays.asList(SUB_COUNTRY_CODE_AB, SUB_COUNTRY_CODE_BC));
		assertThat(caRegion.toString()).isEqualTo(CA_REGION_STR);

		Region usRegion = new RegionImpl(COUNTRY_CODE_US);
		assertThat(usRegion.toString()).isEqualTo(US_REGRION_STR);
	}

	/**
	 * Test method for 'com.elasticpath.domain.impl.RegionImpl.fromString()'.
	 */
	@Test
	public void testFromString() {
		this.regionImpl.fromString(CA_REGION_STR);
		assertThat(this.regionImpl.getCountryCode()).isEqualTo(COUNTRY_CODE_CA);
		assertThat(this.regionImpl.getSubCountryCodeList()).contains(SUB_COUNTRY_CODE_AB, SUB_COUNTRY_CODE_BC);
	}
}
