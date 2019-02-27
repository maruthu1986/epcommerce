/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.messaging.camel.test.support;

import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.Endpoint;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.assertj.core.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Base unit test for Camel Route Builders.
 *
 * @param <M> Message type to be sent.
 * @param <OM> ObjectMapper class to be used.
 */
@RunWith(MockitoJUnitRunner.class)
public abstract class AbstractCamelRouteBuilderTest<M, OM extends ObjectMapper> extends CamelTestSupport {

	private static final Long TIMEOUT_SECONDS = 5L;

	private JacksonDataFormat jacksonDataFormat;

	private Endpoint sourceEndpoint;

	/**
	 * Creates a new Jackson Data Format capable of marshaling and unmarshaling objects of type {@code M} using an Object Mapper of type {@code OM}.
	 *
	 * @param classToMap Message class to be mapped by the Data Format.
	 * @param objectMapperClass Object Mapper to be used by the Data Format.
	 * @return the produced Jackson Data Format.
	 * @throws InstantiationException thrown in case the provided class cannot be instantiated. Provide a class that can or overwrite
	 * {@link #newObjectMapper(Class)}.
	 * @throws IllegalAccessException thrown in case the default constructor is not accessible. Make it accessible or overwrite
	 * {@link #newObjectMapper(Class)}.
	 */
	protected JacksonDataFormat givenJacksonDataFormat(final Class<M> classToMap, final Class<OM> objectMapperClass)
			throws InstantiationException, IllegalAccessException {
		final OM objectMapper = createObjectMapper(objectMapperClass);

		setJacksonDataFormat(Mockito.spy(new JacksonDataFormat(objectMapper, classToMap)));
		return getJacksonDataFormat();
	}

	/**
	 * Creates new configured object mapper of the specified type.
	 *
	 * @param objectMapperClass ObjectMapper concrete type to use.
	 * @return The ObjectMapper created.
	 * @throws IllegalAccessException thrown in case the default constructor is not accessible. Make it accessible or overwrite
	 * {@link #newObjectMapper(Class)}.
	 * @throws InstantiationException thrown in case the provided class cannot be instantiated. Provide a class that can or overwrite
	 * {@link #newObjectMapper(Class)}.
	 */
	protected OM createObjectMapper(final Class<OM> objectMapperClass)
			throws IllegalAccessException, InstantiationException {
		OM objectMapper = newObjectMapper(objectMapperClass);
		objectMapper = configureObjectMapper(objectMapper);

		return objectMapper;
	}

	/**
	 * Configures the object mapper according to its use. For example, an EventType might be registered if sending an Event Message.
	 *
	 * @param objectMapper the object mapper to be configured.
	 */
	protected OM configureObjectMapper(final OM objectMapper) {
		return objectMapper;
	}

	/**
	 * Instantiates an ObjectMapper of the provided class.
	 *
	 * @param objectMapperClass The class to be instantiated.
	 * @return a new object of the provided class.
	 * @throws InstantiationException thrown in case the provided class cannot be instantiated. Provide a class that can or overwrite this method.
	 * @throws IllegalAccessException thrown in case the default constructor is not accessible. Make it accessible or overwrite this method.
	 */
	protected OM newObjectMapper(final Class<OM> objectMapperClass)
			throws InstantiationException, IllegalAccessException {
		return objectMapperClass.newInstance();
	}

	/**
	 * Produces a JSON string representation of the {@code message} argument using an {@link com.fasterxml.jackson.databind.ObjectMapper} of the
	 * specified type.
	 *
	 * @param message object to be translated to JSON.
	 * @param objectMapperClass type of the {@link com.fasterxml.jackson.databind.ObjectMapper} to be used.
	 * @return String representation of the message.
	 * @throws JsonProcessingException thrown in case of JSON interpretation errors.
	 * @throws InstantiationException thrown in case the provided class cannot be instantiated. Provide a class that can or overwrite
	 * {@link #newObjectMapper(Class)}.
	 * @throws IllegalAccessException thrown in case the default constructor is not accessible. Make it accessible or overwrite
	 * {@link #newObjectMapper(Class)}.
	 */
	protected String  getJson(final M message, final Class<OM> objectMapperClass)
			throws JsonProcessingException, InstantiationException, IllegalAccessException {
		final OM epObjectMapper = createObjectMapper(objectMapperClass);
		return epObjectMapper.writeValueAsString(message);
	}

	/**
	 * Sends a JSON message to the {@link #sourceEndpoint}.
	 * @param message Message to be sent in object format.
	 * @param objectMapperClass {@link com.fasterxml.jackson.databind.ObjectMapper} class to be used in the translation to JSON.
	 * @throws Exception thrown exception.
	 */
	protected void sendMessage(final M message, final Class<OM> objectMapperClass) throws Exception {
		final NotifyBuilder notifyBuilder = new NotifyBuilder(context())
				.from(getSourceEndpoint().getEndpointUri())
				.whenDone(1)
				.create();

		template().sendBody(getSourceEndpoint(), getJson(message, objectMapperClass));

		Assertions.assertThat(notifyBuilder.matches(TIMEOUT_SECONDS, TimeUnit.SECONDS))
				.isTrue()
				.withFailMessage("Did not receive message(s) on endpoint within " + TIMEOUT_SECONDS + " seconds");
	}

	protected JacksonDataFormat getJacksonDataFormat() {
		return jacksonDataFormat;
	}

	protected void setJacksonDataFormat(final JacksonDataFormat jacksonDataFormat) {
		this.jacksonDataFormat = jacksonDataFormat;
	}

	protected Endpoint getSourceEndpoint() {
		return sourceEndpoint;
	}

	protected void setSourceEndpoint(final Endpoint sourceEndpoint) {
		this.sourceEndpoint = sourceEndpoint;
	}

}
