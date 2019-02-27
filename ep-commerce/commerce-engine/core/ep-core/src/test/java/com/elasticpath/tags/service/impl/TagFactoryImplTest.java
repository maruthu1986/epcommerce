/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.tags.service.impl;

import static org.junit.Assert.assertSame;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.tags.Tag;
import com.elasticpath.tags.domain.TagDefinition;
import com.elasticpath.tags.service.TagDefinitionReader;
import com.elasticpath.tags.service.TagTypeValueConverter;

/**
 * Test class for {@link TagFactoryImpl}.
 */
public class TagFactoryImplTest {

	private static final String TAG_STRING_VALUE = "12345";
	private static final String TAG_NAME = "tagName";
	private static final String EXPECTED_TAG_VALUE_DOES_NOT_MATCH_RESULT = "Expected tag value does not match result.";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private final TagTypeValueConverter tagTypeValueConverter = context.mock(TagTypeValueConverter.class);

	private final TagDefinitionReader tagDefinitionReader = context.mock(TagDefinitionReader.class);

	private final TagDefinition tagDefinition = context.mock(TagDefinition.class);

	private TagFactoryImpl tagFactory;

	@Before
	public void setUp() {
		tagFactory = new TagFactoryImpl();
		tagFactory.setTagTypeValueConverter(tagTypeValueConverter);
		tagFactory.setTagDefinitionReader(tagDefinitionReader);
	}

	@Test
	public void testCreateTagFromTagDefinition() {
		final String tagStringValue = TAG_STRING_VALUE;
		ensureTypedTagReturnedFromConverter(tagStringValue);

		Tag resultTag = tagFactory.createTagFromTagDefinition(tagDefinition, tagStringValue);

		assertSame(EXPECTED_TAG_VALUE_DOES_NOT_MATCH_RESULT, tagStringValue, resultTag.getValue());
	}

	@Test
	public void testCreateTagFromTagName() {
		final String tagStringValue = TAG_STRING_VALUE;
		ensureTypedTagReturnedFromConverter(tagStringValue);
		ensureTagDefinitionReaderReturnsTagDefinition(TAG_NAME);

		Tag resultTag = tagFactory.createTagFromTagName(TAG_NAME, TAG_STRING_VALUE);

		assertSame(EXPECTED_TAG_VALUE_DOES_NOT_MATCH_RESULT, tagStringValue, resultTag.getValue());
	}

	private void ensureTypedTagReturnedFromConverter(final String tagStringValue) {
		context.checking(new Expectations() {
			{
				allowing(tagTypeValueConverter).convertValueTypeToTagJavaType(tagDefinition, tagStringValue);
				will(returnValue(tagStringValue));
			}
		});
	}

	private void ensureTagDefinitionReaderReturnsTagDefinition(final String tagName) {
		context.checking(new Expectations() {
			{
				allowing(tagDefinitionReader).findByName(tagName);
				will(returnValue(tagDefinition));
			}
		});
	}


}
