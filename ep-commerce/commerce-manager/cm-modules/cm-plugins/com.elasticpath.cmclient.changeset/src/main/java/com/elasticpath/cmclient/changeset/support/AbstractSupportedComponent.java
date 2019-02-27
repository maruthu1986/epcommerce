/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.cmclient.changeset.support;


/**
 * Abstract implementation for {@link SupportedComponent}.
 */
public abstract class AbstractSupportedComponent implements SupportedComponent {

	private final String objectType;
	private final String componentId;

	/**
	 * @param objectType the object type
	 * @param componentId the component ID
	 *
	 */
	public AbstractSupportedComponent(final String componentId, final String objectType) {
		this.componentId = componentId;
		this.objectType = objectType;
	}

	/**
	 *
	 * @return the component ID
	 */
	@Override
	public String getComponentId() {
		return componentId;
	}

	/**
	 *
	 * @return the object type
	 */
	@Override
	public String getObjectType() {
		return objectType;
	}

}