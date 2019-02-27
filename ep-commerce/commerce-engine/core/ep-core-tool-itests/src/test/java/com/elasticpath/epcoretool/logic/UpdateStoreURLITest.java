/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.epcoretool.logic;

import static junit.framework.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.TestLogger;
import com.elasticpath.epcoretool.AbstractStoreURLITest;
import com.elasticpath.epcoretool.LoggerFacade;
import com.elasticpath.test.integration.DirtiesDatabase;

/**
 * Created with IntelliJ IDEA.
 * User: alex
 * Date: 31/12/13
 * Time: 11:40 AM
 */
public class UpdateStoreURLITest extends AbstractStoreURLITest {

    private AbstractSetStoreURL updateStoreURL;


    @Before
    public void initTestClass(){
        //we can ignore all of these db arguments because were using the test context
        updateStoreURL = new AbstractSetStoreURL(null, null, null, null, null, null) {
            @Override
            protected LoggerFacade getLogger() {
                return new TestLogger();
            }
        };
    }

    @DirtiesDatabase
    @Test
    public void testUpdateStoreURLHappy(){
        updateStoreURL.execute(getScenario().getStore().getCode(), NEW_STORE_URL);

        assertEquals("The store url was not updated correctly", NEW_STORE_URL, getPersistedStore().getUrl());
    }

    @DirtiesDatabase
    @Test(expected = IllegalArgumentException.class)
    public void testUpdateStoreURLBadStoreCode(){
        updateStoreURL.execute("badStoreCode", NEW_STORE_URL);

    }

    @DirtiesDatabase
    @Test(expected = IllegalArgumentException.class)
    public void testUpdateStoreURLInvalidURL(){
        updateStoreURL.execute(scenario.getStore().getCode(), "notAUrl");

    }
}
