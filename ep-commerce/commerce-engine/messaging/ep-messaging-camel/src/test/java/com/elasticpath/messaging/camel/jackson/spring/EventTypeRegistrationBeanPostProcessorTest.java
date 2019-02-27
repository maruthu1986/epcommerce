/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.messaging.camel.jackson.spring;

import static org.junit.Assert.assertSame;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.messaging.camel.jackson.EventMessageObjectMapper;
import com.elasticpath.messaging.camel.jackson.SampleEventType;
import com.elasticpath.messaging.spi.EventTypeLookup;
import com.elasticpath.messaging.spi.EventTypeProvider;
import com.elasticpath.messaging.spi.impl.EventTypeProviderImpl;

/**
 * Test class for {@link EventTypeRegistrationBeanPostProcessor}.
 */
public class EventTypeRegistrationBeanPostProcessorTest {

	private EventTypeRegistrationBeanPostProcessor beanPostProcessor;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery() {
		{
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};

	@Mock
	private EventMessageObjectMapper objectMapper;

	@Before
	public void setUp() {
		beanPostProcessor = new EventTypeRegistrationBeanPostProcessor();
		beanPostProcessor.setEventMessageObjectMapper(objectMapper);
	}

	@Test
	public void verifyPostProcessBeforeInitializationReturnsUnmodifiedBean() throws Exception {
		final Object bean = new Object();
		assertSame("Expected unmodified bean to be returned", bean, beanPostProcessor.postProcessBeforeInitialization(bean, "bean"));
	}

	@Test
	public void verifyEventTypeProvidersRegisteredWithEventMessageObjectMapper() throws Exception {
		@SuppressWarnings("unchecked")
		final EventTypeLookup<SampleEventType> eventTypeLookup = context.mock(EventTypeLookup.class);

		final EventTypeProvider<SampleEventType> eventTypeProvider = new EventTypeProviderImpl<>(
			SampleEventType.class,
			eventTypeLookup);

		context.checking(new Expectations() {
			{
				oneOf(objectMapper).registerEventType(eventTypeProvider);
			}
		});

		assertSame("Expected unmodified bean to be returned",
					eventTypeProvider,
					beanPostProcessor.postProcessAfterInitialization(eventTypeProvider, "sampleEventTypeProvider"));
	}

}