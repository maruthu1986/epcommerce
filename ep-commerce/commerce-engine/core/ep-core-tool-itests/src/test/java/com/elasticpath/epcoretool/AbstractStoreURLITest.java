/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.epcoretool;

import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.impl.StoreImpl;
import com.elasticpath.test.db.DbTestCase;

/**
 * Created with IntelliJ IDEA.
 * User: alex
 * Date: 02/01/14
 * Time: 9:48 AM
 */
public abstract class AbstractStoreURLITest extends DbTestCase {
    protected static final String NEW_STORE_URL = "http://mytestsite.com";

    protected Store getPersistedStore(){
        return getPersistenceEngine().get(StoreImpl.class, getScenario().getStore().getUidPk());
    }
}
