/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.tags.service.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import com.elasticpath.tags.dao.ConditionalExpressionDao;
import com.elasticpath.tags.domain.ConditionalExpression;
import com.elasticpath.tags.service.TagConditionService;

/**
 *Implementation of TagConditionService. 
 *
 */
public class TagConditionServiceImpl implements TagConditionService {

	private ConditionalExpressionDao tagConditionDao;


	@Override
	public void delete(final ConditionalExpression condition) {
		tagConditionDao.remove(condition);
		
	}

	@Override
	public ConditionalExpression findByGuid(final String guid) {
		return tagConditionDao.findByGuid(guid);
	}

	@Override
	public ConditionalExpression findByName(final String name) {
		return tagConditionDao.findByName(name);
	}

	@Override
	public ConditionalExpression saveOrUpdate(final ConditionalExpression condition) {
		return tagConditionDao.saveOrUpdate(condition);		
	}
	
	@Override
	public List<ConditionalExpression> getTagConditions() {
		return tagConditionDao.getConditions();
	}
	
	@Override
	public List<ConditionalExpression> getNamedTagConditions() {
		return tagConditionDao.getNamedConditions();
	}
	
	@Override	
	public List<ConditionalExpression> getNamedConditions(final String tagDictionaryGuid) {
		return tagConditionDao.getNamedConditions(tagDictionaryGuid);
	}
	
	
	/**
	 *Tag coditionDao injection method.
	 * @param tagConditionDao tag condition.
	 */
	public void setTagConditionDao(final ConditionalExpressionDao tagConditionDao) {
		this.tagConditionDao = tagConditionDao;
	}
	
	@Override	
	public List<ConditionalExpression> getNamedConditionsByNameTagDictionaryConditionTag(
			final String name,
			final String tagDictionaryGuid,
			final String tag) {
		
		return tagConditionDao.getNamedConditionsByNameTagDictionaryConditionTag(name, tagDictionaryGuid, tag);
		
	}
	
	@Override	
	public List<ConditionalExpression> getNamedConditionsByNameTagDictionaryConditionTagSellingContext(
			final String name,
			final String tagDictionaryGuid,
			final String tag,
			final String sellingContextGuid
			) {
		return tagConditionDao.getNamedConditionsByNameTagDictionaryConditionTagSellingContext(name, tagDictionaryGuid, tag, sellingContextGuid);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * This will result in evaluation of every conditional expression in the system so is unlikely to be an
	 * efficient call to make when there are a lot of rules.
	 */
	@Override
	public List<ConditionalExpression> getMatchingTagConditions(final ConditionalExpressionPredicate predicate) {
		final List<ConditionalExpression> expressions = new LinkedList<>();
		if (predicate != null) {
			for (final ConditionalExpression expression: getTagConditions()) {
				if (predicate.apply(expression)) {
					expressions.add(expression);
				}
			}
		}
		return expressions;
	}

	@Override
	public int countMatchingTagConditions(final ConditionalExpressionPredicate predicate) {
		return getMatchingTagConditions(predicate).size();
	}

	@Override
	public int countMatchingTagExpressionStrings(final String regex) {
		return countMatchingTagConditions(
			new ConditionalExpressionPredicate() {

				private final Pattern cachedPattern = Pattern.compile(regex);

				@Override
				public boolean apply(final ConditionalExpression conditionalExpression) {
					return cachedPattern.matcher(conditionalExpression.getConditionString()).find();
				}
			}
		);
	}

}
