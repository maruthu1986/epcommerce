/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.tags.service.impl;

import com.elasticpath.tags.Tag;
import com.elasticpath.tags.domain.TagDefinition;
import com.elasticpath.tags.service.TagDefinitionReader;
import com.elasticpath.tags.service.TagFactory;
import com.elasticpath.tags.service.TagTypeValueConverter;

/**
 * Factory for creating tags.
 */
public class TagFactoryImpl implements TagFactory {

	private TagTypeValueConverter tagTypeValueConverter;
	private TagDefinitionReader tagDefinitionReader;

	@Override
	public Tag createTagFromTagDefinition(final TagDefinition tagDefinition, final String tagValue) {
		Object object = tagTypeValueConverter.convertValueTypeToTagJavaType(tagDefinition, tagValue);
		return new Tag(object);
	}

	@Override
	public Tag createTagFromTagName(final String tagName, final String tagValue) {
		TagDefinition tagDefinition = tagDefinitionReader.findByName(tagName);
		return createTagFromTagDefinition(tagDefinition, tagValue);
	}

	public void setTagTypeValueConverter(final TagTypeValueConverter tagTypeValueConverter) {
		this.tagTypeValueConverter = tagTypeValueConverter;
	}

	public void setTagDefinitionReader(final TagDefinitionReader tagDefinitionReader) {
		this.tagDefinitionReader = tagDefinitionReader;
	}
}
