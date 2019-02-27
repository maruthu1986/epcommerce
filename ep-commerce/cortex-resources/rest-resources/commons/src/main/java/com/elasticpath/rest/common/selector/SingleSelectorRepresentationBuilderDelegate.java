/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.common.selector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import com.elasticpath.rest.schema.ResourceEntity;
import com.elasticpath.rest.schema.ResourceLink;

/**
 * Composite class that holds the re-usable logic for  SingleSelectorResourceStateBuilder.
 */
public class SingleSelectorRepresentationBuilderDelegate {

	// Key = option, Value = selector
	private final Collection<ResourceLink> choiceLinks = new ArrayList<>();

	private final Collection<ResourceLink> links = new HashSet<>();

	private ResourceLink selection;

	private String selfUri;

	private String name;

	private String displayName;

	private String selectorId;
	private Class<? extends ResourceEntity> selectorType;
	private String scope;

	/**
	 * Adds the given {@link ResourceLink} to the choice collection.
	 *
	 * @param choice The choice to add.
	 */
	public void addChoice(final ResourceLink choice) {
		choiceLinks.add(choice);
	}

	public void setSelection(final ResourceLink choice) {
		selection = choice;
	}

	public void setSelfUri(final String selfUri) {
		this.selfUri = selfUri;
	}

	/**
	 * Adds the given {@link ResourceLink} to the link collection.
	 *
	 * @param link The link to add.
	 */
	public void addLink(final ResourceLink link) {
		links.add(link);
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setDisplayName(final String displayName) {
		this.displayName = displayName;
	}

	public Collection<ResourceLink> getChoiceLinks() {
		return Collections.unmodifiableCollection(choiceLinks);
	}

	public Collection<ResourceLink> getLinks() {
		return Collections.unmodifiableCollection(links);
	}

	public ResourceLink getSelection() {
		return selection;
	}

	public String getSelfUri() {
		return selfUri;
	}

	public String getName() {
		return name;
	}

	public String getDisplayName() {
		return displayName;
	}

	/**
	 * Builds the choice/chosen links.
	 *
	 * @return The link collection.
	 */
	public Collection<ResourceLink> buildCompletedLinks() {
		Collection<ResourceLink> completedLinks = new ArrayList<>(choiceLinks.size());
		for (ResourceLink choice : choiceLinks) {
			ResourceLink.Builder builder = ResourceLink.builderFrom(choice);
			if (choice.equals(selection)) {
				builder.withRel(SelectorRepresentationRels.CHOSEN);
			} else {
				builder.withRel(SelectorRepresentationRels.CHOICE);
			}
			builder.withRev(SelectorRepresentationRels.SELECTOR);
			completedLinks.add(builder.build());
		}
		return completedLinks;
	}

	/**
	 * Verifies the properties and selection.
	 */
	public void assertValidity() {
		assertProperties();
		verifySelection();
	}

	/**
	 * Checks the non-nullable properties were set.
	 */
	protected void assertProperties() {
		assert name != null : "name should not be null.";
		assert selfUri != null : "the self URI should not be null.";
	}

	/**
	 * Verifies the selection has a valid choice.
	 */
	protected void verifySelection() {
		if (selection == null) {
			return;
		}
		if (choiceLinks.isEmpty()) {
			throw new IllegalStateException("Selector has no choices.");
		}
		if (!choiceLinks.contains(selection)) {
			throw new IllegalStateException("Selection not added as choice.");
		}
	}

	/**
	 * Gets selectorId.
	 *
	 * @return selectorId
	 */
	public String getSelectorId() {

		return selectorId;
	}

	/**
	 * Gets selectorType.
	 *
	 * @return selectorType
	 */
	public Class<? extends ResourceEntity> getSelectorType() {

		return selectorType;
	}

	/**
	 * Gets scope.
	 *
	 * @return scope
	 */
	public String getScope() {

		return scope;
	}

	/**
	 * Sets selectorId.
	 *
	 * @param selectorId String
	 */
	public void setSelectorId(final String selectorId) {

		this.selectorId = selectorId;
	}

	/**
	 * Sets selectorType.
	 *
	 * @param selectorType Type
	 */
	public void setSelectorType(final Class<? extends ResourceEntity> selectorType) {

		this.selectorType = selectorType;
	}

	/**
	 * Sets scope.
	 *
	 * @param scope String
	 */
	public void setScope(final String scope) {

		this.scope = scope;
	}
}