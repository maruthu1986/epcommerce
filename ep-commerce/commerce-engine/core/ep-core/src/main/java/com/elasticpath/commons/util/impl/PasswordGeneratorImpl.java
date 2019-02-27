/*
 * Copyright (c) Elastic Path Software Inc., 2005
 */
package com.elasticpath.commons.util.impl;

import java.security.SecureRandom;

import com.elasticpath.commons.util.PasswordGenerator;

/**
 * This password generator generate random passwords with only letters and digits.
 */
public class PasswordGeneratorImpl implements PasswordGenerator {
	/** The random number generator. */
	private static SecureRandom random = new SecureRandom();

	/** Minimum length for a decent password. */
	static final int DEFAULT_LENGTH = 10;

	private int minimumPasswordLength = DEFAULT_LENGTH;

	/**
	 * Set of characters that is valid. Must be printable, memorable, and "won't
	 * break HTML" (i.e., not ' <', '>', '&', '=', ...). or break shell commands
	 * (i.e., not ' <', '>', '$', '!', ...). I, L and O are good to leave out,
	 * as are numeric zero and one.
	 */
	private static char[] goodChar = { 'a', 'b', 'c', 'd', 'e', 'f', 'g',
			'h', 'j', 'k', 'm', 'n', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
			'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K',
			'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };

	/**
	 * Good digits provide generated password with digits.
	 */
	private static char[] goodDigit = { '2', '3', '4', '5', '6', '7', '8', '9' };

	/**
	 * Return a random password of length equal to the <code>minimumPasswordLength</code>.
	 * @return a random password
	 */
	@Override
	public String getPassword() {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < minimumPasswordLength; i++) {
			builder.append(goodChar[random.nextInt(goodChar.length)]);
		}
		int numOfDigits = random.nextInt(builder.length() / 2) + 1;
		for (int i = 0; i < numOfDigits; i++) {
			builder.setCharAt(random.nextInt(builder.length()), goodDigit[random.nextInt(goodDigit.length)]);
		}
		return builder.toString();
	}

	/**
	 * Sets the minimum password length.
	 * @param valueOf the minimum password length
	 */
	@Override
	public void setMinimumPasswordLength(final Integer valueOf) {
		this.minimumPasswordLength = valueOf;
	}

}