/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.validation;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * A <code>CompoundValidator</code> is a validator that aggregates one or more other validators. For the overall result of the
 * <code>CompoundValidator</code> to be valid, all aggregated validators must return a positive result (OK Status).
 */
public class CompoundValidator implements IValidator {

	/** List of "atomic" validators. */
	private final List<IValidator> validators = new ArrayList<IValidator>();

	/** Constructor -- creates an empty <code>CompoundValidator</code>. */
	public CompoundValidator() {
		// No implementation needed
	}

	/**
	 * Constructor -- This convenience constructor accepts an array of the atomic validators that must all return OK_STATUS.
	 * 
	 * @param validators an array of <code>IValidator</code>s
	 */
	public CompoundValidator(final IValidator... validators) {
		for (int i = 0; i < validators.length; i++) {
			this.validators.add(validators[i]);
		}
	}

	/**
	 * Adds a new Validator to the collection of validators that must be valid.
	 * 
	 * @param validator the new validator
	 */
	public void addValidator(final IValidator validator) {
		this.validators.add(validator);
	}

	/**
	 * Performs validation using all validators.
	 * 
	 * @param value the value to be validated
	 * @return OK_STATUS if all validators pass
	 */
	public IStatus validate(final Object value) {
		for (final IValidator currValidator : this.validators) {
			final IStatus status = currValidator.validate(value);
			if (!status.isOK()) {
				return status;
			}
		}
		return Status.OK_STATUS;
	}

}
