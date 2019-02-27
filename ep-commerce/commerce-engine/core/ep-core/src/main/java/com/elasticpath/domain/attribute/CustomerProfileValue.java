/*
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.attribute;

import com.elasticpath.domain.DatabaseCreationDate;
import com.elasticpath.domain.DatabaseLastModifiedDate;

/**
 * <code>CustomerProfileValue</code> represents an <code>Attribute</code> value for a <code>Customer</code>.
 */
public interface CustomerProfileValue extends AttributeValueWithType, DatabaseCreationDate, DatabaseLastModifiedDate {

}
