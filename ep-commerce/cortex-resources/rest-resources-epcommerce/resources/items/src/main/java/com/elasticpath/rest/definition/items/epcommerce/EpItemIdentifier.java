/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.definition.items.epcommerce;

import java.util.Map;

import com.elasticpath.rest.definition.items.ItemIdentifier;
import com.elasticpath.rest.definition.items.ItemsIdentifier;
import com.elasticpath.rest.id.IdentifierPart;

/**
 * Typed {@link ItemIdentifier} adaptation.
 */
public interface EpItemIdentifier extends ItemIdentifier {

	@Override
	ItemsIdentifier getItems();

	@Override
	IdentifierPart<Map<String, String>> getItemId();
}
