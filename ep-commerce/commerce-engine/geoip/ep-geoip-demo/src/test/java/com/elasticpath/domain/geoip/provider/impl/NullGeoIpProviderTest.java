/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
/**
 *
 */
package com.elasticpath.domain.geoip.provider.impl;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import com.elasticpath.domain.geoip.location.GeoIpLocation;

/**
 * Test for demo geo ip provider impl.
 */
@ContextConfiguration("/testNullGeoIp.xml")
@SuppressWarnings("PMD.AvoidUsingHardCodedIP")
public class NullGeoIpProviderTest extends AbstractJUnit4SpringContextTests {

	@Autowired
	private NullGeoIpProviderImpl nullGeoIpProvider;

	/**
	 * The data below is configured in testNullGeoIp.xml config file. Don't change it.
	 **/
	private static final String IP_ADDRESS_1 = "65.79.32.12";
	private static final String IP_ADDRESS_2 = "12.68.78.1";
	private static final String IP_ADDRESS_3 = "127.23.56.111";
	private static final String IP_ADDRESS_NOT_EXISTS = "10.24.36.32";

	private static final String GEO_LOCATION_1_CITY = "Kiev";
	private static final Float GEO_LOCATION_1_TIMEZONE = 2.0f;

	private static final String GEO_LOCATION_2_CARRIER_NAME = "coaxial";
	private static final String GEO_LOCATION_2_TOP_LEVEL_DOMAIN = "ru";

	private static final String GEO_LOCATION_3_PHONE_NUMBER = "134869786";
	private static final String GEO_LOCATION_3_CONNECTION_SPEED = "10 Mb\\s";

	/**
	 * Sets demo geoIp provider.
	 *
	 * @param nullGeoIpProvider demo geoIp provider
	 */
	public void setNullGeoIpProvider(final NullGeoIpProviderImpl nullGeoIpProvider) {
		this.nullGeoIpProvider = nullGeoIpProvider;
	}

	/**
	 * Test method for {@link com.elasticpath.domain.geoip.provider.impl.NullGeoIpProviderImpl#resolveIPAddress(java.lang.String)}.
	 */
	@Test
	public void testResolveIP() {
		GeoIpLocation location1 = nullGeoIpProvider.resolveIPAddress(IP_ADDRESS_1);
		GeoIpLocation location2 = nullGeoIpProvider.resolveIPAddress(IP_ADDRESS_2);
		GeoIpLocation location3 = nullGeoIpProvider.resolveIPAddress(IP_ADDRESS_3);

		assertThat(location1)
				.isNotNull();
		assertThat(location2)
				.isNotNull();
		assertThat(location3)
				.isNotNull();

		assertThat(location1.getCity())
				.isEqualTo(GEO_LOCATION_1_CITY);
		assertThat(location1.getGmtTimeZone())
				.isEqualTo(GEO_LOCATION_1_TIMEZONE);

		assertThat(location2.getCarrierName())
				.isEqualTo(GEO_LOCATION_2_CARRIER_NAME);
		assertThat(location2.getTopLevelDomain())
				.isEqualTo(GEO_LOCATION_2_TOP_LEVEL_DOMAIN);

		assertThat(location3.getPhoneNumber())
				.isEqualTo(GEO_LOCATION_3_PHONE_NUMBER);
		assertThat(location3.getConnectionSpeed())
				.isEqualTo(GEO_LOCATION_3_CONNECTION_SPEED);

		GeoIpLocation location4 = nullGeoIpProvider.resolveIPAddress(IP_ADDRESS_NOT_EXISTS);
		assertThat(location4)
				.isNull();
	}

}
