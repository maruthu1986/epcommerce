/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */

package com.elasticpath.domain.shipping.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.impl.ElasticPathImpl;
import com.elasticpath.domain.shipping.Region;
import com.elasticpath.shipping.connectivity.dto.ShippingAddress;

/**
 * Test cases for <code>ShippingRegionImpl</code>.
 */
@SuppressWarnings("PMD.DontUseElasticPathImplGetInstance")
@RunWith(MockitoJUnitRunner.class)
public class ShippingRegionImplTest {

	private static final String REGION_STR = "[CA(AB,BC)][US()]";

	private static final int REGION_MAP_SIZE = 2;

	private static final String COUNTRY_CODE_CA = "CA";

	private static final String SUB_COUNTRY_CODE_AB = "AB";

	private static final String SUB_COUNTRY_CODE_BC = "BC";

	private static final String INVALID_SUB_COUNTRY_CODE_ON = "ON";

	private static final String COUNTRY_CODE_US = "US";

	private static final String INVALID_COUNTRY_CODE_CN = "CN";

	private ShippingRegionImpl shippingRegionImpl;

	@Mock
	private ShippingAddress address;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private BeanFactory beanFactory;

	/**
	 * Prepares for tests.
	 */
	@Before
	public void setUp() {
		((ElasticPathImpl) ElasticPathImpl.getInstance()).setBeanFactory(beanFactory);
		when(beanFactory.getBean(ContextIdNames.REGION)).thenAnswer(invocation -> new RegionImpl());
		this.shippingRegionImpl = new ShippingRegionImpl();
		this.shippingRegionImpl.setRegionStr(REGION_STR);

		when(address.getCountry()).thenReturn(COUNTRY_CODE_CA);
		when(address.getSubCountry()).thenReturn(SUB_COUNTRY_CODE_BC);

	}

	@After
	public void tearDown() {
		((ElasticPathImpl) ElasticPathImpl.getInstance()).setBeanFactory(null);
	}

	/**
	 * Test method for 'com.elasticpath.domain.shipping.impl.ShippingRegionImpl.getName()'.
	 */
	@Test
	public void testGetSetName() {
		final String testName = "Canada BC";
		shippingRegionImpl.setName(testName);
		assertThat(shippingRegionImpl.getName()).isEqualTo(testName);
	}

	/**
	 * Test method for 'com.elasticpath.domain.shipping.impl.ShippingRegionImpl.getRegionMap()'.
	 */
	@Test
	public void testGetRegionMap() {
		shippingRegionImpl.setRegionStr(REGION_STR);
		Map<String, Region> regionMap = this.shippingRegionImpl.getRegionMap();
		assertThat(regionMap).hasSize(REGION_MAP_SIZE);
		assertThat(regionMap.get(COUNTRY_CODE_CA).getCountryCode()).isEqualTo(COUNTRY_CODE_CA);
		assertThat(regionMap.get(COUNTRY_CODE_CA).getSubCountryCodeList()).contains(SUB_COUNTRY_CODE_AB, SUB_COUNTRY_CODE_BC);
		assertThat(regionMap.get(COUNTRY_CODE_US).getCountryCode()).isEqualTo(COUNTRY_CODE_US);
		assertThat(regionMap.get(COUNTRY_CODE_US).getSubCountryCodeList()).isEmpty();
	}

	/**
	 * Test method for 'com.elasticpath.domain.shipping.impl.ShippingRegionImpl.setRegionMap()'.
	 */
	@Test
	public void testSetRegionMap() {
		Map<String, Region> regionMap = new LinkedHashMap<>();
		Region caRegion = new RegionImpl(COUNTRY_CODE_CA,
				Arrays.asList(SUB_COUNTRY_CODE_AB, SUB_COUNTRY_CODE_BC));
		regionMap.put(COUNTRY_CODE_CA, caRegion);

		regionMap.put(COUNTRY_CODE_US, new RegionImpl("US"));

		this.shippingRegionImpl.setRegionMap(regionMap);
		assertThat(shippingRegionImpl.getRegionMap()).isSameAs(regionMap);

		//Check the regionStr
		assertThat(shippingRegionImpl.getRegionStr()).contains("[CA(AB,BC)]", "[US()]");
	}

	@Test
	public void testIsInShippingRegionWithValidCountryCode() {
		when(address.getCountry()).thenReturn(COUNTRY_CODE_US);
		assertThat(shippingRegionImpl.isInShippingRegion(address)).isTrue();
	}

	@Test
	public void testIsInShippingRegionWithValidCountryAndSubCountryCode() {
		assertThat(this.shippingRegionImpl.isInShippingRegion(address)).isTrue();
	}

	@Test
	public void testIsInShippingRegionWithInvalidSubCountryCode() {
		when(address.getSubCountry()).thenReturn(INVALID_SUB_COUNTRY_CODE_ON);
		assertThat(shippingRegionImpl.isInShippingRegion(address)).isFalse();
	}

	@Test
	public void testIsInShippingRegionWithInvalidCountryCode() {
		when(address.getCountry()).thenReturn(INVALID_COUNTRY_CODE_CN);
		assertThat(shippingRegionImpl.isInShippingRegion(address)).isFalse();
	}

	@Test
	public void testIsInShippingRegionWithoutSubCountryCode() {

		thrown.expect(EpDomainException.class);
		thrown.expectMessage("Invalid shippingAddress - must contain subcountry info.");

		when(address.getSubCountry()).thenReturn(null);
		shippingRegionImpl.isInShippingRegion(address);
	}
}
