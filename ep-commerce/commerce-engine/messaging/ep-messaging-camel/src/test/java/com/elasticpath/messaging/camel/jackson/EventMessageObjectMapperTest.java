/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.messaging.camel.jackson;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.jsontype.SubtypeResolver;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.messaging.EventMessage;
import com.elasticpath.messaging.EventType;
import com.elasticpath.messaging.factory.EventMessageFactory;
import com.elasticpath.messaging.spi.EventTypeLookup;
import com.elasticpath.messaging.spi.EventTypeProvider;
import com.elasticpath.messaging.spi.impl.EventTypeProviderImpl;

/**
 * Test class for {@link com.elasticpath.messaging.camel.jackson.EventMessageObjectMapper}.
 */
public class EventMessageObjectMapperTest {

	private EventMessageObjectMapper objectMapper;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery() {
		{
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};

	private final SimpleModule module = context.mock(SimpleModule.class);

	@SuppressWarnings("unchecked")
	private final JsonDeserializer<SampleEventType> eventTypeDeserializer = context.mock(JsonDeserializer.class, "eventTypeDeserializer");

	@SuppressWarnings("unchecked")
	private final JsonDeserializer<EventMessage> eventMessageDeserializer = context.mock(JsonDeserializer.class, "eventMessageDeserializer");

	@Before
	public void setUp() {
		// Override factory methods to return mocks for verification
		objectMapper = new EventMessageObjectMapper() {
			private static final long serialVersionUID = -9081253602302444287L;

			@Override
			protected SimpleModule createModule() {
				return module;
			}

			@Override
			protected JsonDeserializer<EventMessage> createEventMessageDeserializer(final EventMessageFactory eventMessageFactory) {
				return eventMessageDeserializer;
			}

			@SuppressWarnings("unchecked")
			@Override
			protected <T extends EventType> JsonDeserializer<T> createEventTypeDeserializer(final Class<T> eventTypeClass, final EventTypeLookup<T>
					eventTypeLookup) {
				return (JsonDeserializer<T>) eventTypeDeserializer;
			}
		};

		context.checking(new Expectations() {
			{
				// I really wish I could spy this instead of having to mock it
				ignoring(module).getModuleName();
				ignoring(module).version();
				ignoring(module).setupModule(with(any(Module.SetupContext.class)));
				ignoring(module).getTypeId();
			}
		});
	}

	@Test
	public void verifyInitRegistersEventMessageDeserializer() throws Exception {
		final EventMessageFactory eventMessageFactory = context.mock(EventMessageFactory.class);
		objectMapper.setEventMessageFactory(eventMessageFactory);

		context.checking(new Expectations() {
			{
				oneOf(module).addDeserializer(EventMessage.class, eventMessageDeserializer);
			}
		});

		objectMapper.init();
	}

	@Test
	public void verifyRegisterEventTypeRegisterSubtype() throws Exception {
		initializeObjectMapper();

		final Class<SampleEventType> eventTypeClass = SampleEventType.class;

		final SubtypeResolver subtypeResolver = context.mock(SubtypeResolver.class);

		objectMapper.setSubtypeResolver(subtypeResolver);

		context.checking(new Expectations() {
			{
				ignoring(module);

				// this is what ObjectMapper.registerSubtypes(...) delegates to.
				oneOf(subtypeResolver).registerSubtypes(eventTypeClass);
			}
		});

		objectMapper.registerEventType(new EventTypeProviderImpl<>(eventTypeClass, null));
	}

	@Test
	public void verifyRegisterEventTypeCreatesDeserializer() throws Exception {
		initializeObjectMapper();

		final Class<SampleEventType> eventTypeClass = SampleEventType.class;

		@SuppressWarnings("unchecked")
		final EventTypeLookup<SampleEventType> sampleEventTypeLookup = context.mock(EventTypeLookup.class);

		context.checking(new Expectations() {
			{
				oneOf(module).addDeserializer(eventTypeClass, eventTypeDeserializer);
			}
		});

		objectMapper.registerEventType(new EventTypeProviderImpl<>(eventTypeClass, sampleEventTypeLookup));
	}

	/**
	 * This method has not been implemented as there is currently no convenient way to unregister a deserializer from the Jackson ObjectMapper.  One
	 * strategy could be to keep a shadow copy of all EventTypeProviders in EventMessageObjectMapper,
	 * call setDeserializers() with an empty SimpleDeserializers, then re-register all existing Event Types,
	 * omitting the one intended to be removed.
	 *
	 * @throws UnsupportedOperationException
	 */
	@Test(expected = UnsupportedOperationException.class)
	public void verifyUnregisterEventTypeThrowsUnsupportedOperationException() throws Exception {
		@SuppressWarnings("unchecked")
		final EventTypeProvider<EventType> sampleEventTypeProvider = context.mock(EventTypeProvider.class);
		objectMapper.unregisterEventType(sampleEventTypeProvider);
	}

	private void initializeObjectMapper() {
		context.checking(new Expectations() {
			{
				ignoring(module).addDeserializer(EventMessage.class, eventMessageDeserializer);
			}
		});

		objectMapper.init();
	}

}