/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.messaging.camel.jackson.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import com.elasticpath.messaging.EventType;
import com.elasticpath.messaging.camel.jackson.EventMessageObjectMapper;
import com.elasticpath.messaging.spi.EventTypeProvider;

/**
 * {@link BeanPostProcessor} that automatically registers beans of type {@link EventTypeProvider} with the {@link EventMessageObjectMapper}.
 */
public class EventTypeRegistrationBeanPostProcessor implements BeanPostProcessor {

	private EventMessageObjectMapper eventMessageObjectMapper;

	@Override
	public Object postProcessBeforeInitialization(final Object bean, final String beanName) throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(final Object bean, final String beanName) throws BeansException {
		if (bean instanceof EventTypeProvider) {
			@SuppressWarnings("unchecked") final EventTypeProvider<EventType> eventTypeProvider = (EventTypeProvider<EventType>) bean;
			getEventMessageObjectMapper().registerEventType(eventTypeProvider);
		}

		return bean;
	}

	public void setEventMessageObjectMapper(final EventMessageObjectMapper eventMessageObjectMapper) {
		this.eventMessageObjectMapper = eventMessageObjectMapper;
	}

	protected EventMessageObjectMapper getEventMessageObjectMapper() {
		return eventMessageObjectMapper;
	}

}
