/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.schema.uri;

/**
 * Builder of item lookup URIs.
 */
public interface ItemLookupUriBuilder extends ReadFromOtherUriBuilder<ItemLookupUriBuilder> {

	/**
	 * Set the form path of the uri.
	 * @return the item lookup uri builder
	 */
	ItemLookupUriBuilder setFormPart();

	/**
	 * Set the items part of the uri.
	 * @return the item lookup uri builder
	 */
	ItemLookupUriBuilder setItemsPart();

	/**
	 * Set the scope part of the uri.
	 * @param scope the scope.
	 * @return the item lookup uri builder
	 */
	ItemLookupUriBuilder setScope(String scope);

}
