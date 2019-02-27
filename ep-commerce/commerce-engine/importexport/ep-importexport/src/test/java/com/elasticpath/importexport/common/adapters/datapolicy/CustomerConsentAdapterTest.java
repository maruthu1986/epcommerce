/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.importexport.common.adapters.datapolicy;

import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.common.dto.datapolicy.CustomerConsentDTO;
import com.elasticpath.common.dto.datapolicy.CustomerConsentDtoAssembler;
import com.elasticpath.domain.datapolicy.CustomerConsent;
import com.elasticpath.domain.datapolicy.impl.CustomerConsentImpl;

/**
 * Tests for CustomerConsentAdapter.
 */
@RunWith(MockitoJUnitRunner.class)
public class CustomerConsentAdapterTest {

	@Mock
	private CustomerConsentDtoAssembler customerConsentDtoAssembler;


	@InjectMocks
	private CustomerConsentAdapter customerConsentAdapter;

	/**
	 * Tests populateDTO.
	 */
	@Test
	public void testPopulateDTO() {
		final CustomerConsent source = null;
		final CustomerConsentDTO target = new CustomerConsentDTO();

		customerConsentAdapter.populateDTO(source, target);

		verify(customerConsentDtoAssembler).assembleDto(source, target);
	}

	/**
	 * Tests buildDomain is not persisted.
	 */
	@Test
	public void testBuildDomainIsNotPersisted() {
		final CustomerConsentDTO source = null;
		final CustomerConsent target = new CustomerConsentImpl();

		customerConsentAdapter.buildDomain(source, target);

		verify(customerConsentDtoAssembler).assembleDomain(source);
	}

	/**
	 * Tests buildDomain is persisted.
	 */
	@Test
	public void testBuildDomainIsPersisted() {
		final CustomerConsentDTO source = null;
		final CustomerConsent target = new CustomerConsentImpl();
		target.setUidPk(1);

		customerConsentAdapter.buildDomain(source, target);

		verify(customerConsentDtoAssembler).assembleDomain(source, target);
	}

	/**
	 * Tests createDomainObject.
	 */
	@Test
	public void testCreateDomainObject() {
		customerConsentAdapter.createDomainObject();

		verify(customerConsentDtoAssembler).getDomainInstance();
	}

	/**
	 * Tests createDtoObject.
	 */
	@Test
	public void testCreateDtoObject() {
		customerConsentAdapter.createDtoObject();

		verify(customerConsentDtoAssembler).getDtoInstance();
	}
}
