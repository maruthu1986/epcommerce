/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.tags.service;

import java.util.List;

import com.elasticpath.tags.domain.ConditionalExpression;

/**
 * Service interface for tag condition.
 */
public interface TagConditionService {

	/**
	 * save or update the tag Condition object.
	 * @param condition tagDictionary.
	 * @return a tag condition.
	 */
	ConditionalExpression saveOrUpdate(ConditionalExpression condition);


	/**
	 * Find a tag conditions by its guid.
	 * @param guid GUID of tag Condition.
	 * @return a tag condition.
	 */
	ConditionalExpression findByGuid(String guid);

	/**
	 * Find a tag condition by its name (for named conditions only).
	 * @param name Name of named tag Condition.
	 * @return a tag condition.
	 */
	ConditionalExpression findByName(String name);


	/**
	 * Delete tag condition by its instance.
	 * @param condition tag condition.
	 */
	void delete(ConditionalExpression condition);

	/**
	 * Get all tag conditions.
	 * @return a list of tag conditions.
	 */
	List<ConditionalExpression> getTagConditions();

	/**
	 * Get named tag conditions. Named mean created explicitly.
	 * @return a list of named tag conditions.
	 */
	List<ConditionalExpression> getNamedTagConditions();

	/**
	 * Get list of named conditions by tag dictionary guid.
	 * @param tagDictionaryGuid tag dictionary guid.
	 * @return a list of named tag conditions.
	 */
	List<ConditionalExpression> getNamedConditions(String tagDictionaryGuid);

	/**
	 * Get the {@link com.elasticpath.tags.domain.ConditionalExpression} with given name, tag dictionary, tag.
	 * Any parameter can get null as value.
	 * @param name that name of {@link com.elasticpath.tags.domain.ConditionalExpression}
	 * @param tagDictionaryGuid the tag dictionary guid.
	 * @param tag that tag in conditional string.
	 * @return a list of named {@link com.elasticpath.tags.domain.ConditionalExpression}
	 */
	List<ConditionalExpression> getNamedConditionsByNameTagDictionaryConditionTag(
			String name,
			String tagDictionaryGuid,
			String tag);

	/**
	 * Get the {@link com.elasticpath.tags.domain.ConditionalExpression} with given name, tag dictionary, tag.
	 * Any parameter can get null as value.
	 * @param name that name of {@link com.elasticpath.tags.domain.ConditionalExpression}
	 * @param tagDictionaryGuid the tag dictionary guid.
	 * @param tag that tag in conditional string.
	 * @param sellingContextGuid the name of dynamic content delivery.
	 * @return a list of named {@link com.elasticpath.tags.domain.ConditionalExpression}
	 */
	List<ConditionalExpression> getNamedConditionsByNameTagDictionaryConditionTagSellingContext(
			String name,
			String tagDictionaryGuid,
			String tag,
			String sellingContextGuid
	);

	/**
	 * ConditionalExcpression Predicate contract, used to identify matches when getting tag conditions
	 * using a predicate based search.
	 */
	interface ConditionalExpressionPredicate {
		/**
		 * @param conditionalExpression expression that is evaluated.
		 * @return true for a match, false for a non-match.
		 */
		boolean apply(ConditionalExpression conditionalExpression);
	}

	/**
	 * Get the {@link com.elasticpath.tags.domain.ConditionalExpression} that match with the given predicate.
	 * @param predicate if predicate(conditionalExpression) evaluates to true then a match is found.
	 * @return all matching conditional expressions.
	 */
	List<ConditionalExpression> getMatchingTagConditions(ConditionalExpressionPredicate predicate);

	/**
	 * Counts the matching {@link com.elasticpath.tags.domain.ConditionalExpression} given the predicate. 
	 * @see com.elasticpath.tags.service.TagConditionService.getMatchingTagConditions(Predicate)
	 * @param predicate if predicate evaluates to true then a match is found.
	 * @return the count of matching expressions.
	 */
	int countMatchingTagConditions(ConditionalExpressionPredicate predicate);

	/**
	 * Counts the matching {@link com.elasticpath.tags.domain.ConditionalExpression} by checking the Condition
	 * String of a conditional expression for the existence of the given regex.
	 *
	 * Be careful when using this method - it is very easy to get a false positive.
	 *
	 * @param regex to match against
	 * @return the count of matching expressions.
	 */
	int countMatchingTagExpressionStrings(String regex);

}
