/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.commons.exception;

import com.elasticpath.base.exception.structured.StructuredErrorMessageException;

/**
 * Invalid business state exception represents a recoverable error and contains a structured error
 * message to give detailed information about the state which caused the error.
 */
public interface InvalidBusinessStateException extends StructuredErrorMessageException {

}
