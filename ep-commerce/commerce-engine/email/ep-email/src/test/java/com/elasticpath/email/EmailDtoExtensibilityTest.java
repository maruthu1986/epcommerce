/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.email;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.Test;

/**
 * Test class that verifies and demonstrates that {@link EmailDto} may be extended with additional fields.
 */
public class EmailDtoExtensibilityTest {

	@Test
	public void testEmailDtoCanBeSubclassedWithAdditionalFields() throws Exception {
		final String newField = UUID.randomUUID().toString();

		final EmailDtoExt emailDtoExt = EmailDtoExt.builder()
				.withNewField(newField)
				.build();

		assertThat(emailDtoExt.getNewField()).isEqualTo(newField);
	}

}

@SuppressWarnings("PMD.TestClassWithoutTestCases")
final class EmailDtoExt extends EmailDto {
	private static final long serialVersionUID = -7831066649838057242L;

	private final String newField;

	/**
	 * Constructor.
	 *
	 * @param builder the builder that is used to populate this DTO instance
	 * @param <T> the Builder type
	 */
	<T extends Builder<T>> EmailDtoExt(final Builder<T> builder) {
		super(builder);
		this.newField = builder.newField;
	}

	String getNewField() {
		return newField;
	}

	static class Builder<T extends Builder<T>> extends EmailDto.Builder<T> {

		private String newField;

		T withNewField(final String newField) {
			this.newField = newField;
			return self();
		}

		@Override
		public EmailDtoExt build() {
			return new EmailDtoExt(this);
		}

	}

	/**
	 * Factory method to return a Builder capable of producing new EmailDto instances.
	 *
	 * @return a Builder
	 */
	public static Builder<?> builder() {
		return new Builder<>();
	}

}
