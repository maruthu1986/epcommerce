/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.jms.utilities;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

/**
 * ClassMapper class for mapping JsonObject to class.
 *
 * @param <T> generic type
 */
public class ClassMapper<T> {

	private static final Logger LOGGER = Logger.getLogger(ClassMapper.class);
	private final List<T> rootClassList = new ArrayList<>();

	/**
	 * Gets the list of root classes of type <T> that map to the Json objects in the Json object list.
	 *
	 * @param jsonObjectList the json object list
	 * @param rootClass      the root class
	 * @return list of root classes obtained from json object list
	 */
	public List<T> getRootClassList(final List<JSONObject> jsonObjectList, final Class<T> rootClass) {

		boolean objectExists = false;
		try {
			ObjectMapper mapper = new ObjectMapper();
			for (JSONObject jsonObject : jsonObjectList) {

				mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
				final T object = mapper.readValue(jsonObject.toJSONString(), rootClass);
				rootClassList.add(object);

				objectExists = true;
			}

			assertThat(objectExists)
					.as("JSONObject doesn't exist")
					.isTrue();

		} catch (IOException e) {
			LOGGER.debug(e.getMessage());
		}

		return rootClassList;
	}

	/**
	 * Finds the first match from the collection after filtering based on the predicate.
	 *
	 * @param collection the collection to search in
	 * @param predicate  the filter predicate
	 * @param <T>        the type of items in collection
	 * @return the first match found in collection
	 */
	public <T> Optional<T> findMatch(final Collection<T> collection, final Predicate<T> predicate) {
		return collection.stream()
				.filter(predicate)
				.findFirst();
	}

}
