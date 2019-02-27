/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.email.test.support;

import org.assertj.core.api.Assertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Test class for {@link com.elasticpath.email.test.support.EmailContentAssert}.
 */
public class EmailContentAssertTest {

	@Rule
	public final ExpectedException expectedException = ExpectedException.none();

	@Test
	public void testAssertEmailContentContainsOrderNumberSucceedsWhenOrderNumberExistsWithinContents() throws Exception {
		final String orderNumber = "[ORDERNUMBER]";

		final String message = "Assertion should hold true when order number exists in the String contents";
		EmailContentAssert.assertEmailContentContainsOrderNumber(message, "[ORDERNUMBER] at the beginning of the contents", orderNumber);
		EmailContentAssert.assertEmailContentContainsOrderNumber(message, "the end of the content contains the [ORDERNUMBER]", orderNumber);
		EmailContentAssert.assertEmailContentContainsOrderNumber(message, "The [ORDERNUMBER] exists within the content", orderNumber);
	}

	@Test
	public void testAssertEmailContentContainsOrderNumberFailsOnEmptyContentsString() throws Exception {
		final String message = "expected";

		expectAssertionErrorWithMessage(message);

		EmailContentAssert.assertEmailContentContainsOrderNumber(message, "", "20000");
	}

	@Test
	public void testAssertEmailContentContainsOrderNumberFailsWhenOrderNumberNotPresentInContents() throws Exception {
		final String message = "expected exception message";

		Assertions.assertThatThrownBy(() -> EmailContentAssert.assertEmailContentContainsOrderNumber(message, "foo", "bar"))
				.isInstanceOf(AssertionError.class)
				.as(message);
	}

	@Test
	public void testAssertEmailContentDoesNotContainAnyUnresolvedVelocityCodeSucceedsWhenContentsContainsNoVelocityCode() throws Exception {
		final String contents = "An example message of <strong>rendered</strong> text.";
		EmailContentAssert.assertEmailContentDoesNotContainAnyUnresolvedVelocityCode(
				"Assertion should hold true when no Velocity markup exists in the String contents", contents);
	}

	@Test
	public void testAssertEmailContentDoesNotContainAnyUnresolvedVelocityCodeSucceedsWhenContentsContainsNumericalDollarPrice() throws Exception {
		final String contents = "Dollar signs in prices, such as $123.45, should not match the variable-checker regex.";
		EmailContentAssert.assertEmailContentDoesNotContainAnyUnresolvedVelocityCode(
				"Assertion should hold true when no Velocity markup exists in the String contents", contents);
	}

	@Test
	public void testAssertEmailContentDoesNotContainAnyUnresolvedVelocityCodeFailsWhenContentsContainsLongFormVelocityVariable() throws Exception {
		final String contents = "An example message of ${unrendered} text.";

		final String message = "expected when ${var} is present in contents";

		expectAssertionErrorWithMessage(message);

		EmailContentAssert.assertEmailContentDoesNotContainAnyUnresolvedVelocityCode(message, contents);
	}

	@Test
	public void testAssertEmailContentDoesNotContainAnyUnresolvedVelocityCodeFailsWhenContentsContainsShortFormVelocityVariable() throws Exception {
		final String contents = "An example message of $unrendered text.";
		final String message = "expected when $var is present in contents";

		expectAssertionErrorWithMessage(message + " - Detected unrendered Velocity variable [$unrendered]");

		EmailContentAssert.assertEmailContentDoesNotContainAnyUnresolvedVelocityCode(message, contents);
	}

	private void expectAssertionErrorWithMessage(final String message) {
		expectedException.handleAssertionErrors();
		expectedException.expect(AssertionError.class);
		expectedException.expectMessage(message);
	}

}
