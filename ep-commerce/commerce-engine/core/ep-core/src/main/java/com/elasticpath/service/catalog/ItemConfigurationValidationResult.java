/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.service.catalog;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Represents the result of a validation on an {@link com.elasticpath.domain.catalog.ItemConfiguration}.
 */
public class ItemConfigurationValidationResult {

	/** The success result. */
	public static final ItemConfigurationValidationResult SUCCESS =
		new ItemConfigurationValidationResult(ItemConfigurationValidationStatus.OK, Collections.<String>emptyList());

	/** The error path. */
	private List<String> errorPath;

	/** The status. */
	private ItemConfigurationValidationStatus status;

	/**
	 * Constructor.
	 *
	 * @param status the status
	 * @param errorPath the error path
	 */
	public ItemConfigurationValidationResult(final ItemConfigurationValidationStatus status, final List<String> errorPath) {
		this.status = status;
		this.errorPath = errorPath;
	}

	/**
	 * If the status is OK then the returned list will be empty.
	 * Otherwise it will be a list of child IDs leading to the item with the error.
	 *
	 * @return A list of node IDs.
	 */
	public List<String> getErrorPath() {
		return Collections.unmodifiableList(errorPath);
	}

	/**
	 * Gets the status.
	 *
	 * @return The validation status.
	 */
	public ItemConfigurationValidationStatus getStatus() {
		return status;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof ItemConfigurationValidationResult) {
			ItemConfigurationValidationResult other = (ItemConfigurationValidationResult) obj;
			return Objects.equals(status, other.status) && Objects.equals(errorPath, other.errorPath);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(status, errorPath);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

	/**
	 * The status of the validation result.
	 */
	public enum ItemConfigurationValidationStatus {
		/** Validation passed. */
		OK(true),

		/**
		 * The SKU code of the item is not a valid SKU of the constituent, because the SKU code does not belong to the product for the constituent.
		 */
		INVALID_SKU_CODE(false),

		/**
		 * The SKU code of the item is not a valid choice for that constituent, because the constituent is a specific SKU of a multi-SKU product,
		 * but an invalid SKU code or the code of a different SKU of that product is selected.
		 */
		INVALID_SKU_CODE_FOR_SKU_CONSTITUENT(false),

		/**
		 * The structure of the bundle has changed after the item is created.
		 */
		BUNDLE_DEFINITION_CHANGED(false),

		/** The number of selected items does not match the selection rule. */
		SELECTION_RULE_VIOLATED(false);

		/** The successful. */
		private boolean successful;

		/**
		 * Instantiates a new item configuration validation status.
		 *
		 * @param successful the successful
		 */
		ItemConfigurationValidationStatus(final boolean successful) {
			this.successful = successful;
		}

		/**
		 * Checks if is successful.
		 *
		 * @return true, if is successful
		 */
		public boolean isSuccessful() {
			return successful;
		}
	}
}
