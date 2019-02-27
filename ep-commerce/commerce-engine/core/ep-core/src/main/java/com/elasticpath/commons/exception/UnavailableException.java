/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.commons.exception;

import com.elasticpath.base.exception.structured.StructuredErrorMessageException;

/**
 * Unavailable exception represents an error when an object being looked up cannot be found or is
 * not available, and contains a structured error message to give detailed information about the error.
 */
public interface UnavailableException extends StructuredErrorMessageException {

}
