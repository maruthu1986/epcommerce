/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.relos.rs.authentication.springoauth2.tokenstore.impl;

import static com.elasticpath.rest.test.AssertExecutionResult.assertExecutionResult;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.ResourceTypeFactory;
import com.elasticpath.rest.relos.rs.authentication.springoauth2.dto.AccessTokenDto;
import com.elasticpath.rest.relos.rs.authentication.springoauth2.tokenstore.TokenStoreStrategy;

/**
 * Tests {@link InMemoryTokenStoreStrategyImpl}.
 */
public class InMemoryTokenStoreStrategyImplTest {

	private static final String VALID_TOKEN = "VALID_TOKEN";
	private static final String NOT_FOUND_TOKEN = "NOT_FOUND_TOKEN";

	private TokenStoreStrategy tokenStoreStrategy;

	private AccessTokenDto accessTokenDto;

	@Before
	public void setUp() {
		tokenStoreStrategy = new InMemoryTokenStoreStrategyImpl();
		accessTokenDto = createAccessTokenDto(VALID_TOKEN);
	}

	/**
	 * Test read access token when given null.
	 */
	@Test
	public void testReadAccessTokenGivenNull() {
		//@formatter:off
		assertExecutionResult(tokenStoreStrategy.readAccessToken(null))
				.hasErrorMessage()
				.resourceStatus(ResourceStatus.NOT_FOUND);
		//@formatter:on
	}

	/**
	 * Ensure that the access token can be stored and retrieved.
	 */
	@Test
	public void testThatAccessTokenCanBeStoredAndRetrieved() {
		//@formatter:off
		tokenStoreStrategy.storeToken(accessTokenDto);
		assertExecutionResult(tokenStoreStrategy.readAccessToken(VALID_TOKEN))
				.hasNoErrorMessage()
				.resourceStatus(ResourceStatus.READ_OK)
				.data(accessTokenDto);
		//@formatter:on
	}

	/**
	 * Test read access token when the access token value is not found.
	 */
	@Test
	public void testReadAccessTokenWhenTokenValueNotFound() {
		//@formatter:off
		tokenStoreStrategy.storeToken(accessTokenDto);
		assertExecutionResult(tokenStoreStrategy.readAccessToken(NOT_FOUND_TOKEN))
				.hasErrorMessage()
				.resourceStatus(ResourceStatus.NOT_FOUND);
		//@formatter:on
	}

	/**
	 * Test storing an access token when the access token DTO is null.
	 */
	@Test
	public void testStoreAccessTokenWithNullAccessTokenDto() {
		//@formatter:off
		assertExecutionResult(tokenStoreStrategy.storeToken(null))
				.hasErrorMessage()
				.resourceStatus(ResourceStatus.BAD_REQUEST_BODY);
		//@formatter:on
	}

	/**
	 * Test storing an access token DTO that has a null token ID.
	 */
	@Test
	public void testStoreAccessTokenWithNullTokenId() {
		//@formatter:off
		assertExecutionResult(tokenStoreStrategy.storeToken(createAccessTokenDto(null)))
				.hasErrorMessage()
				.resourceStatus(ResourceStatus.BAD_REQUEST_BODY);
		//@formatter:on
	}

	/**
	 * Test remove access token given null.
	 */
	@Test
	public void testRemoveAccessTokenGivenNull() {
		//@formatter:off
		assertExecutionResult(tokenStoreStrategy.removeAccessToken(null))
				.hasErrorMessage()
				.resourceStatus(ResourceStatus.NOT_FOUND);
		//@formatter:on
	}

	/**
	 * Test that removing a valid access token is not found on subsequent reads.
	 */
	@Test
	public void testRemovingValidAccessTokenIsNotFoundOnSubsequentRead() {
		//@formatter:off
		tokenStoreStrategy.storeToken(accessTokenDto);

		assertExecutionResult(tokenStoreStrategy.removeAccessToken(VALID_TOKEN))
				.hasNoErrorMessage()
				.resourceStatus(ResourceStatus.DELETE_OK);

		assertExecutionResult(tokenStoreStrategy.readAccessToken(VALID_TOKEN))
				.hasErrorMessage()
				.resourceStatus(ResourceStatus.NOT_FOUND);
		//@formatter:on
	}

	/**
	 * Test remove access token with not found token value.
	 */
	@Test
	public void testRemoveAccessTokenWithNotFoundTokenValue() {
		//@formatter:off
		assertExecutionResult(tokenStoreStrategy.removeAccessToken(NOT_FOUND_TOKEN))
				.hasNoErrorMessage()
				.resourceStatus(ResourceStatus.DELETE_OK);
		//@formatter:on
	}

	private AccessTokenDto createAccessTokenDto(final String tokenId) {
		//@formatter:off
		return ResourceTypeFactory.createResourceEntity(AccessTokenDto.class)
				.setTokenId(tokenId);
		//@formatter:on
	}

}
