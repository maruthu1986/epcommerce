/*
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.domain.geoip.provider.impl;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.elasticpath.commons.util.Pair;


/**
 * Test <code>DomainNameResolverImpl</code>.
 */
@ContextConfiguration(classes = AnnotationConfigContextLoader.class)
@SuppressWarnings("PMD.AvoidUsingHardCodedIP")
public class DomainNameResolverImplTest extends AbstractJUnit4SpringContextTests {

	private static final String CLIENT_IP_ADDRESS = "130.1.25.65";

	private static final String ORACLE = "oracle";

	private static final String COM = "com";

	private static final String CLIENT_IP_HOST_NAME_1 = "www.oracle.com";

	private static final String CLIENT_IP_HOST_NAME_2 = "com";

	/**
	 * Test getFirstAndSecondLevelDomainNameByHostName method.
	 */
	@Test
	public void testGetFirstAndSecondLevelDomainNameByHostName() {

		DomainNameResolverImpl domainNameResolverImpl = new DomainNameResolverImpl();

		Pair<String, String> firstAndSecondDomainName =
				domainNameResolverImpl.getFirstAndSecondLevelDomainNameByHostName(CLIENT_IP_HOST_NAME_1, CLIENT_IP_ADDRESS);

		assertThat(firstAndSecondDomainName.getFirst())
				.as("First level name is not 'com' but '" + firstAndSecondDomainName.getFirst() + "'")
				.isEqualTo(COM);

		assertThat(firstAndSecondDomainName.getSecond())
				.as("Second level name is not 'oracle' but '" + firstAndSecondDomainName.getSecond() + "'")
				.isEqualTo(ORACLE);


		firstAndSecondDomainName =
				domainNameResolverImpl.getFirstAndSecondLevelDomainNameByHostName(CLIENT_IP_HOST_NAME_2, CLIENT_IP_ADDRESS);

		assertThat(firstAndSecondDomainName.getFirst())
				.as("First level name is not 'com' but '" + firstAndSecondDomainName.getFirst() + "'")
				.isEqualTo(COM);

		assertThat(firstAndSecondDomainName.getSecond())
				.as("Second level name is not NULL but '" + firstAndSecondDomainName.getSecond() + "'")
				.isNull();


		firstAndSecondDomainName =
				domainNameResolverImpl.getFirstAndSecondLevelDomainNameByHostName(CLIENT_IP_ADDRESS, CLIENT_IP_ADDRESS);

		assertThat(firstAndSecondDomainName)
				.as("The first and second level domain name pair is not NULL")
				.isNull();

		firstAndSecondDomainName =
				domainNameResolverImpl.getFirstAndSecondLevelDomainNameByHostName("", CLIENT_IP_ADDRESS);

		assertThat(firstAndSecondDomainName)
				.as("The first and second level domain name pair is not NULL")
				.isNull();

		firstAndSecondDomainName =
				domainNameResolverImpl.getFirstAndSecondLevelDomainNameByHostName(null, CLIENT_IP_ADDRESS);

		assertThat(firstAndSecondDomainName)
				.as("The first and second level domain name pair is not NULL")
				.isNull();
	}
}
