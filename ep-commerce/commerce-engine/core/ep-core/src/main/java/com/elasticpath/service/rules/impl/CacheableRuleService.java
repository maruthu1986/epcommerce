/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.service.rules.impl;

import java.util.Collection;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.commons.exception.DuplicateNameException;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.rules.EpRuleBase;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.rules.RuleAction;
import com.elasticpath.domain.rules.RuleCondition;
import com.elasticpath.domain.rules.RuleException;
import com.elasticpath.domain.store.Store;
import com.elasticpath.persistence.api.FetchGroupLoadTuner;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.service.rules.DuplicatePromoCodeException;
import com.elasticpath.service.rules.RuleService;
import com.elasticpath.service.rules.RuleSetService;

/**
 * This class implements the decorator pattern and it is intended for subclassing by
 * caching implementations.
 *
 * It extends {@link RuleService} and delegates all non-overridden calls to the
 * non-caching implementation.
 *
 * It is the responsibility of the sub-class to inject required decorated, non-caching, {@link RuleServiceImpl}.
 *
 */
public interface CacheableRuleService extends RuleService {

	@Override
	default void setPersistenceEngine(PersistenceEngine persistenceEngine) {
		getDecorated().setPersistenceEngine(persistenceEngine);
	}

	@Override
	default PersistenceEngine getPersistenceEngine() {
		return getDecorated().getPersistenceEngine();
	}

	@Override
	default Object getObject(long uid) throws EpServiceException {
		return getDecorated().getObject(uid);
	}

	@Override
	default Object getObject(long uid, Collection<String> fieldsToLoad) throws EpServiceException {
		return getDecorated().getObject(uid, fieldsToLoad);
	}

	@Override
	default Rule add(Rule rule) throws DuplicateNameException, DuplicatePromoCodeException {
		return getDecorated().add(rule);
	}

	@Override
	default Rule update(Rule rule) throws DuplicateNameException, DuplicatePromoCodeException {
		return getDecorated().update(rule);
	}

	@Override
	default void remove(Rule rule) throws EpServiceException {
		getDecorated().remove(rule);
	}

	@Override
	default Rule load(long ruleUid) throws EpServiceException {
		return getDecorated().load(ruleUid);
	}

	@Override
	default Rule get(long ruleUid) throws EpServiceException {
		return getDecorated().get(ruleUid);
	}

	@Override
	default Rule get(long ruleUid, FetchGroupLoadTuner loadTuner) throws EpServiceException {
		return getDecorated().get(ruleUid, loadTuner);
	}

	@Override
	default Rule findByRuleCode(String code) throws EpServiceException {
		return getDecorated().findByRuleCode(code);
	}

	@Override
	default Long findLupByRuleCode(String code) throws EpServiceException {
		return getDecorated().findLupByRuleCode(code);
	}

	@Override
	default List<Long> findAllUids() {
		return getDecorated().findAllUids();
	}

	@Override
	default List<Rule> findByUids(Collection<Long> ruleUids) {
		return getDecorated().findByUids(ruleUids);
	}

	@Override
	default List<Rule> findByUidsWithFetchGroupLoadTuner(Collection<Long> ruleUids, FetchGroupLoadTuner fetchGroupLoadTuner) {
		return getDecorated().findByUidsWithFetchGroupLoadTuner(ruleUids, fetchGroupLoadTuner);
	}

	@Override
	default List<Long> findUidsByModifiedDate(Date date) {
		return getDecorated().findUidsByModifiedDate(date);
	}

	@Override
	default Map<Integer, List<RuleCondition>> getAllConditionsMap() {
		return getDecorated().getAllConditionsMap();
	}

	@Override
	default Map<Integer, List<RuleAction>> getAllActionsMap() {
		return getDecorated().getAllActionsMap();
	}

	@Override
	default Map<Integer, List<RuleException>> getAllExceptionsMap() {
		return getDecorated().getAllExceptionsMap();
	}

	@Override
	default Map<String, String> getPromotionNamesForCouponCodes(Locale locale, Collection<String> codes) {
		return getDecorated().getPromotionNamesForCouponCodes(locale, codes);
	}

	@Override
	default Map<String, Long> getRuleIdsForCouponCodes(Set<String> promoCodes) {
		return getDecorated().getRuleIdsForCouponCodes(promoCodes);
	}

	@Override
	default Rule findByName(String name) throws DuplicateNameException {
		return getDecorated().findByName(name);
	}

	@Override
	default Rule findByPromoCode(String promoCode) throws DuplicatePromoCodeException {
		return getDecorated().findByPromoCode(promoCode);
	}

	@Override
	default Rule getLimitedUseRule(String promoCode) {
		return getDecorated().getLimitedUseRule(promoCode);
	}

	@Override
	default Map<String, Rule> getLimitedUseRulesByPromotionCodes(Collection<String> promoCodes) {
		return getDecorated().getLimitedUseRulesByPromotionCodes(promoCodes);
	}

	@Override
	default Long getAllowedLimit(long ruleId) {
		return getDecorated().getAllowedLimit(ruleId);
	}

	@Override
	default void setAllConditions(List<String> conditions) {
		getDecorated().setAllConditions(conditions);
	}

	@Override
	default void setAllActions(List<String> actions) {
		getDecorated().setAllActions(actions);
	}

	@Override
	default void setAllExceptions(List<String> exceptions) {
		getDecorated().setAllExceptions(exceptions);
	}

	@Override
	default void setRuleSetService(RuleSetService ruleSetService) {
		getDecorated().setRuleSetService(ruleSetService);
	}

	@Override
	default Collection<Long> findChangedPromoUids(Date excludeFrom, int ruleScenario) throws EpServiceException {
		return getDecorated().findChangedPromoUids(excludeFrom, ruleScenario);
	}

	@Override
	default EpRuleBase findRuleBaseByScenario(Store store, Catalog catalog, int scenarioId) {
		return getDecorated().findRuleBaseByScenario(store, catalog, scenarioId);
	}

	@Override
	default EpRuleBase findChangedStoreRuleBases(String storeCode, int scenarioId, Date date) {
		return getDecorated().findChangedStoreRuleBases(storeCode, scenarioId, date);
	}

	@Override
	default EpRuleBase findChangedCatalogRuleBases(String catalogCode, int scenarioId, Date date) {
		return getDecorated().findChangedCatalogRuleBases(catalogCode, scenarioId, date);
	}

	@Override
	default EpRuleBase saveOrUpdateRuleBase(EpRuleBase ruleBase) {
		return getDecorated().saveOrUpdateRuleBase(ruleBase);
	}

	@Override
	default void deleteRuleBase(EpRuleBase ruleBase) {
		getDecorated().deleteRuleBase(ruleBase);
	}

	@Override
	default String findRuleCodeById(long ruleId) {
		return getDecorated().findRuleCodeById(ruleId);
	}

	@Override
	default Collection<Rule> findByScenarioAndStore(int scenarioId, String storeCode) {
		return getDecorated().findByScenarioAndStore(scenarioId, storeCode);
	}

	@Override
	default List<Object[]> getPromotionUsageData(Collection<Long> storeUids, Currency currency, Date startDate, Date endDate,
			Boolean withCouponCodesOnly) {
		return getDecorated().getPromotionUsageData(storeUids, currency, startDate, endDate, withCouponCodesOnly);
	}

	@Override
	default List<Object[]> findActiveRuleIdSellingContextByScenarioAndStore(int scenario, String storeCode) {
		return getDecorated().findActiveRuleIdSellingContextByScenarioAndStore(scenario, storeCode);
	}

	@Override
	default Set<Long> retrievePromotionDependencies(Set<Long> promos) {
		return getDecorated().retrievePromotionDependencies(promos);
	}

	@Override
	default RuleValidationResultEnum isRuleValid(Rule rule, String storeCode) {
		return getDecorated().isRuleValid(rule, storeCode);
	}

	@Override
	default Collection<String> findCodesByUids(Collection<Long> ruleIds) throws EpServiceException {
		return getDecorated().findCodesByUids(ruleIds);
	}

	@Override
	default List<Rule> findByRuleCodes(Collection<String> codes) {
		return getDecorated().findByRuleCodes(codes);
	}

	/**
	 * Set a {@link RuleService} to decorate.
	 * @param decorated {@link RuleService}.
	 */
	void setDecorated(RuleService decorated);

	/**
	 * Get decorated {@link RuleService}.
	 * @return Decorated {@link RuleService}.
	 */
	RuleService getDecorated();
}
