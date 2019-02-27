/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.email.handler.order.helper.impl;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderReturn;
import com.elasticpath.email.domain.EmailProperties;
import com.elasticpath.email.handler.order.helper.ReturnExchangeEmailPropertyHelper;

/**
 * Helper for constructing {@link OrderReturn} email properties.
 */
public class ReturnExchangeEmailPropertyHelperImpl implements ReturnExchangeEmailPropertyHelper {

	private static final String PHYSICAL_RMA_EMAIL_HTML_TEMPLATE = "RMA.html";

	private static final String PHYSICAL_RMA_EMAIL_TXT_TEMPLATE = "RMA.txt";
	
	private static final String GENERAL_RMA_EMAIL_HTML_TEMPLATE = "GRMA.html";

	private static final String GENERAL_RMA_EMAIL_TXT_TEMPLATE = "GRMA.txt";

	private static final String LOCALE_KEY_FOR_VM_TEMPLATE = "locale";

	private BeanFactory beanFactory;

	@Override
	public EmailProperties getOrderReturnEmailProperties(final OrderReturn orderReturn) {
		final Order order = orderReturn.getOrder();
		EmailProperties emailProperties = getBeanFactory().getBean(ContextIdNames.EMAIL_PROPERTIES);
		emailProperties.getTemplateResources().put("orderReturn", orderReturn);
		emailProperties.getTemplateResources().put(LOCALE_KEY_FOR_VM_TEMPLATE, order.getLocale());
		emailProperties.setDefaultSubject("Order Return Confirmation");
		emailProperties.setLocaleDependentSubjectKey("RMA.emailSubject");
		emailProperties.setEmailLocale(order.getLocale());
		emailProperties.setStoreCode(order.getStoreCode());
		emailProperties.setRecipientAddress(order.getCustomer().getEmail());

		setEmailTemplate(emailProperties, orderReturn);
		
		return emailProperties;
	}

	private void setEmailTemplate(final EmailProperties emailProperties, final OrderReturn orderReturn) {
		
		if (orderReturn.getPhysicalReturn()) {
			emailProperties.setHtmlTemplate(PHYSICAL_RMA_EMAIL_HTML_TEMPLATE);
			emailProperties.setTextTemplate(PHYSICAL_RMA_EMAIL_TXT_TEMPLATE);		
		} else {
			emailProperties.setHtmlTemplate(GENERAL_RMA_EMAIL_HTML_TEMPLATE);
			emailProperties.setTextTemplate(GENERAL_RMA_EMAIL_TXT_TEMPLATE);
		}
	}

	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	protected BeanFactory getBeanFactory() {
		return beanFactory;
	}
}
