/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.common.dto.datapolicy;

import com.elasticpath.common.dto.assembler.AbstractDtoAssembler;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.datapolicy.ConsentAction;
import com.elasticpath.domain.datapolicy.CustomerConsent;
import com.elasticpath.service.datapolicy.DataPolicyService;

/**
 * Assembler for CustomerConsent domain objects and their associated DTOs.
 */
public class CustomerConsentDtoAssembler extends AbstractDtoAssembler<CustomerConsentDTO, CustomerConsent> {

	private BeanFactory beanFactory;

	private DataPolicyService dataPolicyService;

	@Override
	public CustomerConsent getDomainInstance() {
		return beanFactory.getBean(ContextIdNames.CUSTOMER_CONSENT);
	}

	@Override
	public CustomerConsentDTO getDtoInstance() {
		return new CustomerConsentDTO();
	}

	@Override
	public void assembleDto(final CustomerConsent source, final CustomerConsentDTO target) {
		target.setGuid(source.getGuid());
		target.setDataPolicyGuid(source.getDataPolicy().getGuid());
		target.setAction(source.getAction().getName());
		target.setConsentDate(source.getConsentDate());
		target.setCustomerGuid(source.getCustomerGuid());
	}

	@Override
	public void assembleDomain(final CustomerConsentDTO source, final CustomerConsent target) {
		target.setGuid(source.getGuid());
		target.setDataPolicy(dataPolicyService.findByGuid(source.getDataPolicyGuid()));
		target.setAction(ConsentAction.valueOf(source.getAction()));
		target.setConsentDate(source.getConsentDate());
		target.setCustomerGuid(source.getCustomerGuid());
	}

	/**
	 * @return the beanFactory
	 */
	public BeanFactory getBeanFactory() {
		return beanFactory;
	}

	/**
	 * @param beanFactory the beanFactory to set
	 */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * @return the dataPolicyService.
	 */
	public DataPolicyService getDataPolicyService() {
		return dataPolicyService;
	}

	/**
	 * @param dataPolicyService the dataPolicyService to set
	 */
	public void setDataPolicyService(final DataPolicyService dataPolicyService) {
		this.dataPolicyService = dataPolicyService;
	}
}
