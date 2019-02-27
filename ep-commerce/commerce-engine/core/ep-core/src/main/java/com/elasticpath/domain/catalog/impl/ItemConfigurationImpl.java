/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.domain.catalog.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;

import com.elasticpath.domain.catalog.ItemConfiguration;
import com.elasticpath.domain.catalog.ItemConfigurationValidationException;
import com.elasticpath.service.catalog.ItemConfigurationBuilder;
import com.elasticpath.service.catalog.ItemConfigurationValidationResult;
import com.elasticpath.service.catalog.ItemConfigurationValidationResult.ItemConfigurationValidationStatus;
import com.elasticpath.service.catalog.ItemConfigurationValidator;

/**
 * Default implementation of a {@link ItemConfiguration}.
 */
public class ItemConfigurationImpl implements ItemConfiguration {

	private boolean selected;
	private final String itemId;
	private final SortedMap<String, ItemConfiguration> children =
		new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

	private String skuCode;

	/**
	 * Constructor.
	 *
	 * @param skuCode the SKU code
	 * @param children the children
	 * @param selected whether this item is selected
	 * @param itemId this item's ID, bundle constituent's GUID in the case of a bundle constituent.
	 */
	public ItemConfigurationImpl(final String skuCode, final Map<String, ItemConfiguration> children,
								 final boolean selected, final String itemId) {
		this.skuCode = skuCode;
		this.children.putAll(children);
		this.selected = selected;
		this.itemId = itemId;
	}

	/**
	 * Copy constructor.
	 *
	 * @param original the original object to copy
	 */
	public ItemConfigurationImpl(final ItemConfigurationImpl original) {
		Map<String, ItemConfiguration> newChildren = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		for (Map.Entry<String, ItemConfiguration> child : original.children.entrySet()) {
			ItemConfigurationImpl newChild = new ItemConfigurationImpl((ItemConfigurationImpl) child.getValue());
			newChildren.put(child.getKey(), newChild);
		}
		this.skuCode = original.getSkuCode();
		this.children.putAll(newChildren);
		this.selected = original.isSelected();
		this.itemId =  original.getItemId();
	}

	@Override
	public List<ItemConfiguration> getChildren() {
		List<ItemConfiguration> values = new ArrayList<>(children.values());
		return Collections.unmodifiableList(values);
	}

	@Override
	public ItemConfiguration getChildById(final String childId) {
		return children.get(childId);
	}

	@Override
	public ItemConfiguration getChildByPath(final List<String> childPath) {
		ItemConfiguration current = this;
		for (String childId : childPath) {
			current = current.getChildById(childId);
			if (current == null) {
				return null;
			}
		}
		return current;
	}

	@Override
	public String getSkuCode() {
		return skuCode;
	}

	/**
	 * Gets a map of the children. Implemented to allow JSON de-serialization. Do *NOT* use in other contexts.
	 * @return the children, as an unmodifiable map.
	 */
	public Map<String, ItemConfiguration> getChildrenMap() {
		return Collections.unmodifiableMap(children);
	}

	/**
	 * Sets the SKU code.
	 *
	 * @param skuCode the new SKU code
	 */
	private void setSkuCode(final String skuCode) {
		this.skuCode = skuCode;
	}

	private void setSelected(final boolean selected) {
		this.selected = selected;
	}

	@Override
	public boolean isSelected() {
		return selected;
	}

	public String getItemId() {
		return itemId;
	}

	@Override
	public int hashCode() {
		return Objects.hash(skuCode, selected, itemId, children);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof ItemConfigurationImpl)) {
			return false;
		}
		ItemConfigurationImpl other = (ItemConfigurationImpl) obj;
		return Objects.equals(skuCode, other.skuCode)
			&& Objects.equals(selected, other.selected)
			&& Objects.equals(itemId, other.itemId)
			&& Objects.equals(children, other.children);
	}

	/**
	 * Builds {@link ItemConfigurationImpl}s.
	 */
	public static class Builder implements ItemConfigurationBuilder {

		private ItemConfigurationImpl rootItem;
		private final ItemConfigurationValidator validator;
		private ItemConfigurationValidationResult validationResult;
		private boolean dirty;

		/**
		 * Instantiates a new builder.
		 *
		 * @param rootItem the root item
		 * @param validator the validator
		 */
		public Builder(final ItemConfigurationImpl rootItem, final ItemConfigurationValidator validator) {
			this.rootItem = rootItem;
			this.validator = validator;
		}

		private void markDirty() {
			validationResult = null;
			if (!dirty) {
				dirty = true;
				rootItem = new ItemConfigurationImpl(rootItem);
			}
		}

		private void markClean() {
			dirty = false;
		}

		@Override
		public ItemConfiguration build() {
			ItemConfigurationValidationStatus status = validate().getStatus();
			if (status.isSuccessful()) {
				markClean();
				return rootItem;
			}
			throw new ItemConfigurationValidationException("Could not validate the item configuration because of: " + status);
		}

		@Override
		public ItemConfigurationBuilder select(final List<String> path, final String skuCode) {
			markDirty();
			getChildAt(path, true).setSkuCode(skuCode);
			return this;
		}

		@Override
		public ItemConfigurationBuilder deselect(final List<String> path) {
			markDirty();
			getChildAt(path, false).setSelected(false);
			return this;
		}

		private ItemConfigurationImpl getChildAt(final List<String> path, final boolean setSelected) {
			ItemConfigurationImpl currentItem = rootItem;
			int index = 0;
			for (String pathSegment : path) {
				ItemConfigurationImpl child = (ItemConfigurationImpl) currentItem.getChildById(pathSegment);
				if (child == null) {
					throw new ItemConfigurationValidationException(String.format("Invalid path segment %s at index %d. The passed path was %s.",
							pathSegment, index, path));
				}
				if (setSelected) {
					child.setSelected(true);
				}
				currentItem = child;
				++index;
			}
			return currentItem;
		}

		@Override
		public ItemConfigurationValidationResult validate() {
			if (validationResult == null) {
				validationResult = validator.validate(rootItem);
			}
			return validationResult;
		}
	}
}
