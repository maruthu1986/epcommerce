/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.epcoretool.cli;

import static junit.framework.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.epcoretool.AbstractStoreURLITest;
import com.elasticpath.test.integration.DirtiesDatabase;
import com.elasticpath.tools.epcoretool.client.parsers.SetStoreURLParser;

/**
 * Created with IntelliJ IDEA.
 * User: alex
 * Date: 02/01/14
 * Time: 9:31 AM
 */
public class UpdateStoreURLParserITest extends AbstractStoreURLITest{

    private SetStoreURLParser urlParser;

    @Before
    public void initParser(){
        urlParser = new SetStoreURLParser();
    }

    @DirtiesDatabase
    @Test
    public void updateStoreURLHappy(){
        String input = getScenario().getStore().getCode() + "=" + NEW_STORE_URL;
        urlParser.execute(input);

        assertEquals("The store url was not updated correctly", NEW_STORE_URL, getPersistedStore().getUrl());
    }

    @DirtiesDatabase
    @Test(expected = IllegalArgumentException.class)
    public void testStoreURLBadStoreCode(){
        String input = "BadStoreCode=" + NEW_STORE_URL;
        urlParser.execute(input);

        assertEquals("The store url should not have been updated", getScenario().getStore().getUrl(), getPersistedStore().getUrl());
    }

    @DirtiesDatabase
    @Test(expected = IllegalArgumentException.class)
    public void testStoreURLBadURL(){
        String input = getScenario().getStore().getCode() + "=notAurl";
        urlParser.execute(input);

        assertEquals("The store url should not have been updated", getScenario().getStore().getUrl(), getPersistedStore().getUrl());
    }
}
