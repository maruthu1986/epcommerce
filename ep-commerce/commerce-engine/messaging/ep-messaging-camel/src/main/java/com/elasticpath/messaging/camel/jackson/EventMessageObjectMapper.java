/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.messaging.camel.jackson;

import java.util.concurrent.locks.ReentrantLock;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import com.elasticpath.messaging.EventMessage;
import com.elasticpath.messaging.EventType;
import com.elasticpath.messaging.factory.EventMessageFactory;
import com.elasticpath.messaging.spi.EventTypeLookup;
import com.elasticpath.messaging.spi.EventTypeProvider;

/**
 * Extension of {@link ObjectMapper} that allows for customisation of JSON marshalling and unmarshalling.
 */
public class EventMessageObjectMapper extends ObjectMapper {

	private static final long serialVersionUID = 2140903789383885936L;

	private SimpleModule module;

	private EventMessageFactory eventMessageFactory;

	private final ReentrantLock registrationLock = new ReentrantLock();

	/**
	 * Registers all configuration settings and custom serializers and deserializers.
	 */
	public void init() {
		module = createModule();

		registerEventMessageDeserializer();
		addEventTypeMixin();

		registerModule(module);
	}

	/**
	 * Creates and registers a {@link JsonDeserializer} for the {@link EventMessage} class.
	 */
	protected void registerEventMessageDeserializer() {
		// SimpleModule is not thread-safe.  :(
		getRegistrationLock().lock();
		try {
			getModule().addDeserializer(EventMessage.class, createEventMessageDeserializer(getEventMessageFactory()));
		} finally {
			getRegistrationLock().unlock();
		}
	}

	/**
	 * Registers mixin to add serialisation configuration to the {@link com.elasticpath.messaging.EventType EventType} interface.
	 */
	protected void addEventTypeMixin() {
		addMixInAnnotations(EventType.class, EventTypeMixin.class);
	}

	/**
	 * Registers an {@link EventType} subclass with its associated {@link EventTypeLookup}. This provides Jackson with enough information to allow
	 * deserialization from JSON to the appropriate implementation of the {@link EventType} interface.
	 *
	 * @param <T> the type of {@link EventType} to register
	 * @param eventTypeProvider provides the {@link EventType} class and lookup instance
	 */
	public <T extends EventType> void registerEventType(final EventTypeProvider<T> eventTypeProvider) {
		// SimpleModule is not thread-safe.  :(
		getRegistrationLock().lock();
		try {
			registerSubtypes(eventTypeProvider.getEventTypeClass());
			getModule().addDeserializer(
					eventTypeProvider.getEventTypeClass(),
					createEventTypeDeserializer(eventTypeProvider.getEventTypeClass(), eventTypeProvider.getEventTypeLookup()));
		} finally {
			getRegistrationLock().unlock();
		}
	}

	/**
	 * <p>Unregisters an {@link EventType}.</p>
	 * <p>This method has not been implemented as there currently is no convenient way to unregister a deserializer from the Jackson ObjectMapper
	 * .</p>
	 *
	 * @param <T> the type of {@link EventType} to unregister
	 * @param eventTypeProvider provides the {@link EventType} class and lookup instance
	 * @throws UnsupportedOperationException always
	 */
	public <T extends EventType> void unregisterEventType(final EventTypeProvider<T> eventTypeProvider) {
		//	One strategy could be to keep a shadow copy of all EventTypeProviders in EventMessageObjectMapper,
		//	call setDeserializers() with an empty SimpleDeserializers, then re-register all existing Event Types,
		//	omitting the one intended to be removed.
		throw new UnsupportedOperationException("Not implemented.");
	}

	/**
	 * Factory method to create a new {@link SimpleModule} instance, used to register custom serializers and deserializers.
	 *
	 * @return a new {@link SimpleModule} instance
	 */
	protected SimpleModule createModule() {
		return new SimpleModule("EventMessageModule", new Version(1, 0, 0, null, null, null));
	}

	/**
	 * Factory method to create a new {@link JsonDeserializer} instance to deserialize {@link EventMessage} instances.
	 *
	 * @param eventMessageFactory the event message factory used to instantiate {@link EventMessage} instances
	 * @return a new {@link JsonDeserializer} instance
	 */
	protected JsonDeserializer<EventMessage> createEventMessageDeserializer(final EventMessageFactory eventMessageFactory) {
		return new EventMessageDeserializer(this, eventMessageFactory);
	}

	/**
	 * Factory method to create a new {@link JsonDeserializer} instance to deserialize {@link EventType} instances.
	 *
	 * @param eventTypeClass the {@link EventType} subclass to deserialize
	 * @param eventTypeLookup the {@link EventTypeLookup} that can locate instances of {@code eventTypeClass} by name
	 * @param <T> the {@link EventType} subclass type
	 * @return a new {@link JsonDeserializer} instance
	 */
	protected <T extends EventType> JsonDeserializer<T> createEventTypeDeserializer(final Class<T> eventTypeClass,
																					final EventTypeLookup<T> eventTypeLookup) {
		return new EventTypeDeserializer<>(eventTypeLookup);
	}

	protected SimpleModule getModule() {
		return module;
	}

	public void setEventMessageFactory(final EventMessageFactory eventMessageFactory) {
		this.eventMessageFactory = eventMessageFactory;
	}

	protected EventMessageFactory getEventMessageFactory() {
		return eventMessageFactory;
	}

	protected ReentrantLock getRegistrationLock() {
		return registrationLock;
	}

}
