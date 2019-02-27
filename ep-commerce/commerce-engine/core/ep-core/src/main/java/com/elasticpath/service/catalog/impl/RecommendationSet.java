/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.service.catalog.impl;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import com.elasticpath.base.exception.EpServiceException;

/**
 * Represents a set of product recommendations. The number of times a product recommendation is made is recorded and this class provides access
 * to a distinct collection of product recommendations, ordered by the number of occurrences, and limited by the <code>maxRecommendations</code>
 * parameter passed to the constructor. The extra complexity of this implementation is intended to avoid the need to sort the entire
 * recommendation set for performance reasons.
 */
public class RecommendationSet {

	/** Maps product ids to their occurrence count. */
	private final Multiset<Long> recommendationMultiset = HashMultiset.create();

	/** The maximum number of recommendations for any given source product. */
	private final int maxRecommendations;

	/**
	 * Maintains a sorted list of the ids of top N most frequently recommended products where N = maxRecommendations.
	 */
	private final List<Long> topRecommendations;

	/** The lowest number of recommendations for products in the topRecommendations list. */
	private int minRecommendationCount;

	/**
	 * Constructor.
	 *
	 * @param maxRecommendations pass in the maximum number of recommendations. This is expected to be a small number, say < 10. to be returned
	 *            from this <code>RecommendationSet</code>
	 */
	public RecommendationSet(final int maxRecommendations) {
		if (maxRecommendations <= 0) {
			throw new EpServiceException("Max number of recommendations must be > 0");
		}
		this.maxRecommendations = maxRecommendations;
		this.topRecommendations = new ArrayList<>(maxRecommendations);
	}

	/**
	 * Add a product recommendation to the set.
	 *
	 * @param recommendedProductId the UID of the recommended product.
	 */
	public void addRecommendation(final long recommendedProductId) {
		int recommendationCount = updateRecommendationCount(recommendedProductId);
		if (recommendationCount >= this.minRecommendationCount) {
			addToTopRecommendations(recommendedProductId, recommendationCount);
		}
	}

	/**
	 * Adds a recommended product Id to the top recommendations list. The id will be added in order of recommendation count and if the list grows
	 * larger than maxRecommendations, it will be truncated. This will also update the minRecommendation
	 *
	 * @param recommendedProductId the id of the product to add to the top recommendations
	 * @param recommendationCount the number of times the product has been recommended
	 */
	private void addToTopRecommendations(final long recommendedProductId, final int recommendationCount) {

		topRecommendations.remove(recommendedProductId);

		// Add the recommendation
		int topRecSize = 0;
		if (topRecommendations.isEmpty()) {
			topRecommendations.add(recommendedProductId);
		} else {
			// Insert it in order of recommendation count
			boolean recommendationInserted = false;
			topRecSize = topRecommendations.size();
			for (int i = 0; i < topRecSize; i++) {
				int currProductRecommendationCount = recommendationMultiset.count(topRecommendations.get(i));
				if (recommendationCount > currProductRecommendationCount) {
					topRecommendations.add(i, recommendedProductId);
					topRecSize++;
					recommendationInserted = true;
					break;
				}
			}
			if (!recommendationInserted) {
				topRecommendations.add(recommendedProductId);
				topRecSize++;
			}
		}

		// truncate if needed
		topRecSize = topRecommendations.size();
		if (topRecSize > this.maxRecommendations) {
			topRecommendations.remove(topRecSize - 1);
			topRecSize--;
		}

		// Re-set the minRecommendationCount to the count for the last item in the list
		this.minRecommendationCount = recommendationMultiset.count(topRecommendations.get(topRecSize - 1));
	}

	/**
	 * Updates the map of recommended product ids to their recommendation count.
	 *
	 * @param recommendedProductId the ID of a new recommended product
	 */
	private int updateRecommendationCount(final long recommendedProductId) {
		recommendationMultiset.add(recommendedProductId);
		return recommendationMultiset.count(recommendedProductId);
	}

	/**
	 * Get the list of recommended products.
	 *
	 * @return a <code>List</code> of <code>Long</code>s
	 */
	public List<Long> getRecommendations() {
		return topRecommendations;
	}

	/**
	 * Determines whether this collection of recommendations contains the given target product uidpk.
	 * @param targetProductUidPk the UidPk of the product to search for
	 * @return true if this collection of recommendations contains a product with the given uidPk, false if not.
	 */
	public boolean contains(final long targetProductUidPk) {
		return topRecommendations.contains(targetProductUidPk);
	}

	/**
	 * Returns a string representation of the top recommendations (a list of their ids) for testing.
	 *
	 * @return a list of ids as a <code>String</code>
	 */
	@Override
	public String toString() {
		StringBuilder out = new StringBuilder();
		out.append("( ");
		for (Long currLong : this.topRecommendations) {
			out.append(currLong).append(' ');
		}
		out.append(')');
		return out.toString();
	}
}
