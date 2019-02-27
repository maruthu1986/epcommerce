/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.domain.customer.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.tags.dao.TagDictionaryDao;

/**
 * Test <code>TagSetInvalidationDeterminerImpl</code>.
 */
public class TagSetInvalidationDeterminerImplTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private TagDictionaryDao tagDictionaryDao;
	private final List<String> tagDictionaries = Arrays.asList("TIME", "STORES", "PLA_SHOPPER", "PROMOTIONS_SHOPPER");
	private final TagSetInvalidationDeterminerImpl fixture = new TagSetInvalidationDeterminerImpl();

	@Before
	public void setUp() {
		tagDictionaryDao = context.mock(TagDictionaryDao.class);

		fixture.setTagDictionaryDao(tagDictionaryDao);
		fixture.setTagDictionaries(tagDictionaries);

		context.checking(new Expectations() { {
			allowing(tagDictionaryDao).getUniqueTagDefinitionGuidsByTagDictionaryGuids(with(tagDictionaries));
			will(returnValue(Arrays.asList("SHOPPING_START_TIME", "SELLING_CHANNEL", "REGISTERED_CUSTOMER", "INSTORE_SEARCH_TERMS")));
		} });
	}

	@Test
	public void shouldNeedToInvalidateWhenKeyIsValidTagGUID() {
		fixture.initTagGuids();
		assertTrue("Must invalidate for correct tag", fixture.needInvalidate("REGISTERED_CUSTOMER"));
	}

	@Test
	public void shouldNotNeedToInvalidateWhenKeyIsInvalidTagGUID() {
		fixture.initTagGuids();
		assertFalse("Must not invalidate when tag is wrong", fixture.needInvalidate("WRONG_TAG_GUID"));
	}
}
