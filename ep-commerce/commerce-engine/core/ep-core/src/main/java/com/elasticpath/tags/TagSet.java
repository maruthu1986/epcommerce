/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.tags;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.elasticpath.commons.listeners.TagSetListener;

/**
 * Tag sets store a collection of tag definition keys and their tags.
 * Tags can be added to this collection, or their value appended 
 * to existing value if already in collection.
 */
public class TagSet implements Serializable {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;
 
	private final Map<String, Tag> tags = new HashMap<>();
	
	private final Set<TagSetListener> listeners = new LinkedHashSet<>();
	
	/**
	 * @param key {@link com.elasticpath.tags.domain.TagDefinition} key for retrieving the Tag
	 * @return Tag value matching the {@link com.elasticpath.tags.domain.TagDefinition} key
	 */
	public Tag getTagValue(final String key) {
		return tags.get(key);
	}
	
	/**
	 * Add a tag on the given {@link TagDefinition}.
	 * Replace existing values with the new value.
	 * 
	 * @param key the tag definition key we want to tag
	 * @param tag the new tag value
	 */
	public void addTag(final String key, final Tag tag) {
		tags.put(key, tag);
		notifyListeners(key, tag);
	}
	
	/**
	 * @return immutable map of tag keys to tags
	 */
	public Map<String, Tag> getTags() {
		return Collections.unmodifiableMap(this.tags);
	}
	
	/**
	 * @return true is tag set is empty.
	 */
	public boolean isEmpty() {
		return tags.isEmpty();
	}
	
	/**
	 * Note: this method checks if the provided listener is already added prior adding it
	 * to avoid duplication of listeners.
	 * 
	 * @param listener event listener to add to this tag set.
	 * @return true if listener has been added to the listeners collection of this tag set.
	 */
	public boolean addListener(final TagSetListener listener) {
		if (!this.listeners.contains(listener)) {
			return this.listeners.add(listener);
		}
		return false;
	}
	/**
	 * @param listener event listener to remove from this tag set
	 * @return true if listener did exist in listeners collection of this tag set
	 */
	public boolean removeListener(final TagSetListener listener) {
		return this.listeners.remove(listener);
	}
	
	private void notifyListeners(final String key, final Tag tag) {
		if (!this.listeners.isEmpty()) {
			for (TagSetListener listener : this.listeners) {
				listener.onEvent(key, tag);
			}
		}
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(TagSet.class.getName());
		for (final Map.Entry<String, Tag> tagEntry : tags.entrySet()) {
			stringBuilder.append("\nKey = [");
			stringBuilder.append(tagEntry.getKey());
			stringBuilder.append("] Value  = [");
			stringBuilder.append(tagEntry.getValue());
			stringBuilder.append(']');
		}
		return stringBuilder.toString();
	}
	
	
	
}