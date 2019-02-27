/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.fulfillment.views;

import static com.elasticpath.cmclient.core.validation.EpValidatorFactory.EMAIL;
import static com.elasticpath.cmclient.core.validation.EpValidatorFactory.MAX_LENGTH_255;
import static com.elasticpath.cmclient.core.validation.EpValidatorFactory.MAX_LENGTH_50;
import static com.elasticpath.cmclient.core.validation.EpValidatorFactory.NO_SPECIAL_CHARACTERS;
import static com.elasticpath.cmclient.core.validation.EpValidatorFactory.NO_SPECIAL_CHARACTERS_EXCEPT_APOSTROPHE;
import static com.elasticpath.cmclient.core.validation.EpValidatorFactory.NO_SPECIAL_CHARACTERS_EXCEPT_AT_SIGN;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.elasticpath.cmclient.core.validation.CompoundValidator;

/**
 * Validators used by the search tabs.
 */
class SearchFieldsValidators {

	/**
	 * Customer names validator.
	 */
	protected static final IValidator NAME_VALIDATOR = new CompoundValidator(MAX_LENGTH_255, NO_SPECIAL_CHARACTERS_EXCEPT_APOSTROPHE);
	/**
	 * Zip code validator.
	 */
	protected static final IValidator ZIP_CODE_VALIDATOR = new CompoundValidator(MAX_LENGTH_50, NO_SPECIAL_CHARACTERS);

	/**
	 * Email/userId pattern validator.
	 */
	protected static final IValidator EMAIL_PATTERN_USERID_VALIDATOR = new IValidator() {
		@Override
		public IStatus validate(final Object value) {
			final IStatus validationSpecialCharacters = NO_SPECIAL_CHARACTERS_EXCEPT_AT_SIGN.validate(value);
			final IStatus validationEmail = EMAIL.validate(value);
			if (validationSpecialCharacters.isOK() || validationEmail.isOK()) {
				return validationSpecialCharacters;
			}
			return Status.CANCEL_STATUS;
		}
	};
}
