/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.domain.customer.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.junit.Test;

/**
 * Provides contract tests for subclasses of {@link AbstractPaymentMethodImpl}.
 */
public abstract class AbstractPaymentMethodImplTest<T extends AbstractPaymentMethodImpl<T>> {

	/**
	 * Subclasses must override this method to create and return a new instance of the {@link AbstractPaymentMethodImpl} under test.
	 *
	 * @return the new instance
	 */
	protected abstract T create();

	/**
	 * Subclasses may override this method in order to indicate which fields are not copied.
	 *
	 * @return a collection of field names
	 */
	protected Collection<String> getExcludedFieldNames() {
		return Collections.emptySet();
	}

	@Test
	public void ensureUidPkAfterCopyIsZero() {
		T paymentMethod = create();
		paymentMethod.setUidPk(1);

		AbstractPaymentMethodImpl<?> copy = paymentMethod.copy();
		assertEquals("uidpk of copy", 0, copy.getUidPk());
	}

	@Test
	public void ensureNewInstanceIsCreated() {
		T paymentMethod = create();

		AbstractPaymentMethodImpl<?> copy = paymentMethod.copy();
		assertNotSame("copy must not be the same instance as the original", paymentMethod, copy);
	}

	@Test
	public void ensureNonUidPkFieldsAreCopied() {
		T paymentMethod = create();

		ArrayList<String> excludedFieldNames = new ArrayList<>();
		excludedFieldNames.add("uidPk");
		excludedFieldNames.addAll(getExcludedFieldNames());

		String[] excludedFieldNamesArray = excludedFieldNames.toArray(new String[excludedFieldNames.size()]);

		assertTrue("copy fields do not match original. either exclude fields via getExcludedFieldNames or update copy method",
				EqualsBuilder.reflectionEquals(paymentMethod, paymentMethod.copy(), excludedFieldNamesArray));
	}
}
