/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.common.selector;

import static com.elasticpath.rest.test.AssertResourceState.assertResourceState;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

import com.elasticpath.rest.definition.controls.SelectorEntity;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;

/**
 * Tests building a single selection Selector.
 */
public class SingleSelectorResourceStateBuilderTest {

	private static final String SELECTOR_URI = "/selector";

	private static final String NAME = "name";

	private static final Integer SELECTION_RULE = 1;

	@Test
	public void testBuild() {
		ResourceLink backLink = ResourceLinkFactory.createUriRel("uri", "backLink");
		ResourceLink choiceOne = ResourceLinkFactory.createUriOnly("uri/one");
		ResourceLink choiceTwo = ResourceLinkFactory.createUriOnly("uri/two");
		ResourceLink expectedChoice = ResourceLink.builderFrom(choiceOne)
				.withRel(SelectorRepresentationRels.CHOICE)
				.withRev(SelectorRepresentationRels.SELECTOR)
				.build();
		ResourceLink expectedChosen = ResourceLink.builderFrom(choiceTwo)
				.withRel(SelectorRepresentationRels.CHOSEN)
				.withRev(SelectorRepresentationRels.SELECTOR)
				.build();
		SelectorEntity expectedEntity = SelectorEntity.builder()
				.withName(NAME)
				.withSelectionRule(SELECTION_RULE)
				.withSelectorId(null)
				.build();
		Self expectedSelf = SelfFactory.createSelf(SELECTOR_URI);

		ResourceState<SelectorEntity> actualResourceState = new SingleSelectorResourceStateBuilder()
				.setName(NAME)
				.setSelfUri(SELECTOR_URI)
				.addChoice(choiceOne)
				.addChoice(choiceTwo)
				.setSelection(choiceTwo)
				.addLink(backLink)
				.build();

		assertEquals(expectedEntity, actualResourceState.getEntity());
		assertResourceState(actualResourceState)
				.containsLinks(Arrays.asList(expectedChoice, expectedChosen, backLink))
				.self(expectedSelf);
	}
}
