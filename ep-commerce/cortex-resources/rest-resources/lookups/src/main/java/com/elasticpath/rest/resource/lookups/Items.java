/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.lookups;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.elasticpath.rest.resource.dispatch.operator.annotation.PathParam;

/**
 * While it will never be used as an annotation on an operator method, it is in the annotations
 * package to provide visibility to developers who are examining the annotations package.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@PathParam(Items.URI_PART)
public @interface Items {

	/** The string name of the uri part, useful for constructing URIs. */
	String URI_PART = "items";

	/** {@link com.elasticpath.rest.resource.dispatch.operator.annotation.Path} pattern to identify the segment. */
	String PATH_PART = URI_PART;
}
