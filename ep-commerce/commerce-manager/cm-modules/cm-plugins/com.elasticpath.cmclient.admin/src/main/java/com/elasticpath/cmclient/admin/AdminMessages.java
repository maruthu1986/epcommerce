/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.admin;

import com.elasticpath.cmclient.core.nls.LocalizedMessagePostProcessor;

/**
 * Messages class for the admin plugin.
 */
public final class AdminMessages {

    static final String BUNDLE_NAME = "com.elasticpath.cmclient.admin.AdminPluginResources"; //$NON-NLS-1$

    private AdminMessages() {
    }

    public static final String REMOVE_ME = null; // fix PMD warning... remove it when other fields available


    /**
     * Gets the NLS localize message class.
     * @return the localized message class.
     */
    public static AdminMessages get() {
        return LocalizedMessagePostProcessor.getUTF8Encoded(BUNDLE_NAME, AdminMessages.class);
    }

}
