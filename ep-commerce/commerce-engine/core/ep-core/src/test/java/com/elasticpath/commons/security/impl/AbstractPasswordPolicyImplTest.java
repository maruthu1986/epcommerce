/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.commons.security.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.elasticpath.commons.security.PasswordHolder;
import com.elasticpath.commons.security.ValidationResult;
import com.elasticpath.commons.util.PasswordGenerator;
import com.elasticpath.commons.util.impl.PasswordGeneratorImpl;
import com.elasticpath.settings.test.support.SimpleSettingValueProvider;

/**
 * Test class for {@link AbstractPasswordPolicyImpl}.
 */
public class AbstractPasswordPolicyImplTest {

	private final AbstractPasswordPolicyImpl passwordPolicy = new AbstractPasswordPolicyImpl() {
		@Override
		public ValidationResult validate(final PasswordHolder passwordHolder) {
			return null;
		}
	};

	@Test
	public void testGetPasswordGeneratorSetsMinimumPasswordLength() throws Exception {
		final int minimumPasswordLength = 17;
		passwordPolicy.setMinimumPasswordLengthProvider(new SimpleSettingValueProvider<>(minimumPasswordLength));
		passwordPolicy.setPasswordGenerator(new PasswordGeneratorImpl());

		final PasswordGenerator passwordGenerator = passwordPolicy.getPasswordGenerator();

		assertEquals("Unexpected password length",
				minimumPasswordLength,
				passwordGenerator.getPassword().length());
	}

}