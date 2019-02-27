/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.customer.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.elasticpath.converter.ConversionMalformedValueException;
import com.elasticpath.rest.identity.Subject;
import com.elasticpath.rest.identity.attribute.UserTraitSubjectAttribute;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.UserTraitsToTagSetTransformer;
import com.elasticpath.tags.Tag;
import com.elasticpath.tags.TagSet;
import com.elasticpath.tags.service.TagFactory;

/**
 * Transforms user traits from a Subject into a CE TagSet.
 */
@Singleton
@Named("userTraitsToTagSetTransformer")
public class SubjectBasedUserTraitsToTagSetTransformer implements UserTraitsToTagSetTransformer {

	private static final Logger LOG = LoggerFactory.getLogger(SubjectBasedUserTraitsToTagSetTransformer.class);

	private final TagFactory tagFactory;

	/**
	 * Creates a SubjectBasedUserTraitsToTagSetTransformer.
	 *
	 * @param tagFactory the tag factory.
	 */
	@Inject
	public SubjectBasedUserTraitsToTagSetTransformer(
			@Named("tagFactory")
			final TagFactory tagFactory) {
		this.tagFactory = tagFactory;
	}

	@Override
	public TagSet transformUserTraitsToTagSet(final Subject subject) {
		TagSet tagSet = new TagSet();

		subject.getAttributes().stream()
			.filter(attribute -> UserTraitSubjectAttribute.TYPE.equals(attribute.getType()))
			.map(attribute -> ((UserTraitSubjectAttribute) attribute).getTrait())
			.forEach(trait -> {
				try {
					Tag tag = tagFactory.createTagFromTagName(trait.getName(), trait.getValue());
					tagSet.addTag(trait.getName(), tag);
				} catch (ConversionMalformedValueException e) {
					LOG.debug("Ignoring trait {} due to error converting to tag: {}", trait, e.getMessage());
				}
			});

		return tagSet;
	}
}