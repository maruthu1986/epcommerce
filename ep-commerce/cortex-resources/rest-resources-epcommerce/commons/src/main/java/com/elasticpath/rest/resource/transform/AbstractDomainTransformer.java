/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.transform;

import java.util.Locale;

import org.osgi.framework.Constants;

import com.elasticpath.rest.schema.ResourceEntity;

/**
 * Base transformer.
 *
 * @param <D> The domain type to transform.
 * @param <E> the Entity type.
 */
public abstract class AbstractDomainTransformer<D, E extends ResourceEntity> {

	/**
	 * Declarative Services default service ranking value for Domain Transformers.
	 */
	public static final String DS_SERVICE_RANKING = Constants.SERVICE_RANKING + ":Integer=100";

	/**
	 * Transforms the given {@link ResourceEntity} to a new domain object.
	 *
	 * @param resourceEntity the adaptable resource entity
	 * @return the new domain object
	 */
	public D transformToDomain(final E resourceEntity) {
		return transformToDomain(resourceEntity, null);
	}

	/**
	 * Transforms the given {@link ResourceEntity} to a new domain object.
	 * Includes locale for localized properties.
	 *
	 * @param resourceEntity the adaptable resource entity
	 * @param locale the locale
	 * @return the new domain object
	 */
	public abstract D transformToDomain(final E resourceEntity, final Locale locale);

	/**
	 * Transforms the given domainObject to a new {@link ResourceEntity}.
	 *
	 * @param domainObject the domain object.
	 * @return the new entity instance.
	 */
	public E transformToEntity(final D domainObject) {
		return transformToEntity(domainObject, null);
	}

	/**
	 * Transforms the given domainObject to a new {@link ResourceEntity}.
	 * Includes locale for localized properties.
	 *
	 * @param domainObject the domain object.
	 * @param locale the locale
	 * @return the new entity instance.
	 */
	public abstract E transformToEntity(final D domainObject, final Locale locale);
}
