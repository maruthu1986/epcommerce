/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.email.handler.order.helper.impl;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.store.Store;
import com.elasticpath.email.domain.EmailProperties;
import com.elasticpath.email.handler.order.helper.OrderEmailPropertyHelper;
import com.elasticpath.sellingchannel.presentation.OrderPresentationHelper;
import com.elasticpath.service.store.StoreService;

/**
 * Helper for processing email properties for Order e-mails.
 */
public class OrderEmailPropertyHelperImpl implements OrderEmailPropertyHelper {

	private OrderPresentationHelper orderPresentationHelper;
	private StoreService storeService;

	private static final String LOCALE_KEY_FOR_VM_TEMPLATE = "locale";
	private static final String SHIPMENT_RELEASE_FAILURE_TEMPLATE_TXT = "shipmentReleaseFailure.txt";
	private static final String ORDER_CONF_EMAIL_HTML_TEMPLATE = "orderConf.html";
	private static final String ORDER_CONF_EMAIL_TXT_TEMPLATE = "orderConf.txt";
	private static final String SHIPMENT_CONF_EMAIL_TXT_TEMPLATE = "shipmentConf.txt";

	private BeanFactory beanFactory;

	@Override
	public EmailProperties getOrderConfirmationEmailProperties(final Order order) {
		final EmailProperties emailProperties = getEmailPropertiesBeanInstance();
		emailProperties.getTemplateResources().put("order", order);
		emailProperties.getTemplateResources().put(LOCALE_KEY_FOR_VM_TEMPLATE, order.getLocale());
		emailProperties.setDefaultSubject("Order Confirmation");
		emailProperties.setLocaleDependentSubjectKey("order.confirmation.emailSubject");
		emailProperties.setEmailLocale(order.getLocale());
		emailProperties.setHtmlTemplate(ORDER_CONF_EMAIL_HTML_TEMPLATE);
		emailProperties.setTextTemplate(ORDER_CONF_EMAIL_TXT_TEMPLATE);
		emailProperties.setRecipientAddress(order.getCustomer().getEmail());
		emailProperties.setStoreCode(order.getStoreCode());
		emailProperties.getTemplateResources().put("orderItemFormBeanMap", getOrderPresentationHelper().createOrderItemFormBeanMap(order));
		return emailProperties;
	}

	@Override
	public EmailProperties getShipmentConfirmationEmailProperties(final Order order, final OrderShipment orderShipment) {
		final EmailProperties emailProperties = getEmailPropertiesBeanInstance();
		emailProperties.getTemplateResources().put("order", order);
		emailProperties.getTemplateResources().put("orderShipment", orderShipment);
		emailProperties.getTemplateResources().put(LOCALE_KEY_FOR_VM_TEMPLATE, order.getLocale());
		emailProperties.setDefaultSubject("Shipment Confirmation");
		emailProperties.setLocaleDependentSubjectKey("shipment.confirmation.emailSubject");
		emailProperties.setEmailLocale(order.getLocale());
		emailProperties.setTextTemplate(SHIPMENT_CONF_EMAIL_TXT_TEMPLATE);
		emailProperties.setRecipientAddress(order.getCustomer().getEmail());
		emailProperties.setStoreCode(order.getStoreCode());
		emailProperties.getTemplateResources().put("orderItemFormBeanList", getOrderPresentationHelper().createOrderItemFormBeanList(orderShipment));

		return emailProperties;
	}

	/**
	 * @return
	 */
	private EmailProperties getEmailPropertiesBeanInstance() {
		return getBeanFactory().getBean(ContextIdNames.EMAIL_PROPERTIES);
	}

	@Override
	public EmailProperties getFailedShipmentPaymentEmailProperties(final OrderShipment shipment, final String errorMessage) {
		final EmailProperties emailProperties = getEmailPropertiesBeanInstance();
		emailProperties.getTemplateResources().put("shipment", shipment);
		emailProperties.getTemplateResources().put("errorMessage", errorMessage);
		emailProperties.getTemplateResources().put(LOCALE_KEY_FOR_VM_TEMPLATE, shipment.getOrder().getLocale());
		emailProperties.setDefaultSubject("Payment Confirmation");
		emailProperties.setLocaleDependentSubjectKey("shipment.release.failed.emailSubject");
		emailProperties.setEmailLocale(shipment.getOrder().getLocale());
		emailProperties.setTextTemplate(SHIPMENT_RELEASE_FAILURE_TEMPLATE_TXT);
		Store store = getStoreService().findStoreWithCode(shipment.getOrder().getStoreCode());
		emailProperties.setRecipientAddress(store.getStoreAdminEmailAddress());
		emailProperties.setStoreCode(store.getCode());

		return emailProperties;
	}

	/**
	 * @param orderPresentationHelper the orderPresentationHelper to set
	 */
	public void setOrderPresentationHelper(final OrderPresentationHelper orderPresentationHelper) {
		this.orderPresentationHelper = orderPresentationHelper;
	}

	/**
	 * @return the orderPresentationHelper
	 */
	protected OrderPresentationHelper getOrderPresentationHelper() {
		return orderPresentationHelper;
	}

	public void setStoreService(final StoreService storeService) {
		this.storeService = storeService;
	}

	protected StoreService getStoreService() {
		return storeService;
	}

	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	protected BeanFactory getBeanFactory() {
		return beanFactory;
	}
}
