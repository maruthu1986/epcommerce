/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.helpers.extenders;

import com.elasticpath.commons.util.extenum.ExtensibleEnum;

/**
 * Interface for getting the localized name.
 */
public interface MessageReader {

    /**
     * Get localized name.
     * @param anEnum the key.
     * @return the localized name.
     */
    String getLocalizedName(ExtensibleEnum anEnum);
}
