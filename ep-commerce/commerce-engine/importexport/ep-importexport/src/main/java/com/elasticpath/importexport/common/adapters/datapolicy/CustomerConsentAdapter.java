/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.importexport.common.adapters.datapolicy;

import com.elasticpath.common.dto.datapolicy.CustomerConsentDTO;
import com.elasticpath.common.dto.datapolicy.CustomerConsentDtoAssembler;
import com.elasticpath.domain.datapolicy.CustomerConsent;
import com.elasticpath.importexport.common.adapters.AbstractDomainAdapterImpl;

/**
 * Helper class for mediating data from CustomerConsent entities to CustomerConsentDTO.
 *
 */
public class CustomerConsentAdapter extends AbstractDomainAdapterImpl<CustomerConsent, CustomerConsentDTO>  {

	private CustomerConsentDtoAssembler assembler;

	@Override
	public void populateDTO(final CustomerConsent source, final CustomerConsentDTO target) {
		assembler.assembleDto(source, target);
	}

	@Override
	public CustomerConsentDTO createDtoObject() {
		return assembler.getDtoInstance();
	}

	@Override
	public CustomerConsent createDomainObject() {
		return assembler.getDomainInstance();
	}

	public void setAssembler(final CustomerConsentDtoAssembler assembler) {
		this.assembler = assembler;
	}

	@Override
	public CustomerConsent buildDomain(final CustomerConsentDTO source, final CustomerConsent target) {

		CustomerConsent customerConsent;

		if (target.isPersisted()) {
			assembler.assembleDomain(source, target);
			customerConsent = target;
		} else {
			customerConsent = assembler.assembleDomain(source);
		}

		return customerConsent;
	}

	@Override
	public void populateDomain(final CustomerConsentDTO source, final CustomerConsent target) {
		assembler.assembleDomain(source, target);
	}

}
