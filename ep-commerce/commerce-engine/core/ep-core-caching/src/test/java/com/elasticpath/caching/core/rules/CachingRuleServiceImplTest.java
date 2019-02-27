/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.caching.core.rules;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.cache.Cache;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.rules.impl.PromotionRuleImpl;
import com.elasticpath.service.rules.RuleService;

@RunWith(MockitoJUnitRunner.class)
public class CachingRuleServiceImplTest {

	private static final Long RULE_UIDPK = 1L;
	private static final String RULE_NAME = "ruleName";
	private static final String RULE_CODE = "ruleCode";

	@Mock
	private Cache<String, Rule> ruleByRuleCodeCache;
	@Mock
	private Cache<String, String> ruleCodeByRuleNameCache;
	@Mock
	private Cache<Long, String> ruleCodeByRuleUidCache;
	@Mock
	private Cache<Collection<Long>, Collection<String>> ruleCodesByRuleUidsCache;
	@Mock
	private RuleService decoratedFallbackService;
	@Mock
	private Rule rule;
	@InjectMocks
	private CachingRuleServiceImpl fixture;

	@Before
	public void setUp() throws Exception {

		when(rule.getUidPk()).thenReturn(RULE_UIDPK);
		when(rule.getCode()).thenReturn(RULE_CODE);
		when(rule.getName()).thenReturn(RULE_NAME);
	}

	@Test
	public void shouldFindRuleCodeByIdInDbOnCacheMiss() {
		final long ruleUidPk = 1L;
		final String expectedRuleCode = RULE_CODE;

		when(ruleCodeByRuleUidCache.get(ruleUidPk)).thenReturn(null);
		when(decoratedFallbackService.findRuleCodeById(ruleUidPk)).thenReturn(expectedRuleCode);

		String actualRuleCode = fixture.findRuleCodeById(ruleUidPk);

		assertThat(actualRuleCode).isEqualTo(expectedRuleCode);

		verify(ruleCodeByRuleUidCache).get(ruleUidPk);
		verify(decoratedFallbackService).findRuleCodeById(ruleUidPk);
		verify(ruleCodeByRuleUidCache).put(ruleUidPk, actualRuleCode);
	}

	@Test
	public void shouldFindRuleCodeByIdInCacheOnCacheHit() {
		final long ruleUidPk = 1L;
		final String expectedRuleCode = RULE_CODE;

		when(ruleCodeByRuleUidCache.get(ruleUidPk)).thenReturn(expectedRuleCode);

		String actualRuleCode = fixture.findRuleCodeById(ruleUidPk);

		assertThat(actualRuleCode).isEqualTo(expectedRuleCode);

		verify(ruleCodeByRuleUidCache).get(ruleUidPk);
		verify(decoratedFallbackService, never()).findRuleCodeById(anyLong());
		verify(ruleCodeByRuleUidCache, never()).put(anyLong(), anyString());
	}

	@Test
	public void shouldFindCodesByUidsInDbOnCacheMiss() {
		final Collection<Long> ruleUidPks = Arrays.asList(1L, 2L);
		final Collection<String> expectedRuleCodes = Arrays.asList("ruleCode1", "ruleCode2");

		when(ruleCodesByRuleUidsCache.get(ruleUidPks)).thenReturn(null);
		when(decoratedFallbackService.findCodesByUids(ruleUidPks)).thenReturn(expectedRuleCodes);

		Collection<String> actualRuleCodes = fixture.findCodesByUids(ruleUidPks);

		assertThat(actualRuleCodes).isEqualTo(expectedRuleCodes);

		verify(ruleCodesByRuleUidsCache).get(ruleUidPks);
		verify(decoratedFallbackService).findCodesByUids(ruleUidPks);
		verify(ruleCodesByRuleUidsCache).put(ruleUidPks, actualRuleCodes);
	}

	@Test
	public void shouldFindCodesByUidsInCacheOnCacheHit() {
		final Collection<Long> ruleUidPks = Arrays.asList(1L, 2L);
		final Collection<String> expectedRuleCodes = Arrays.asList("ruleCode1", "ruleCode2");

		when(ruleCodesByRuleUidsCache.get(ruleUidPks)).thenReturn(expectedRuleCodes);

		Collection<String> actualRuleCodes = fixture.findCodesByUids(ruleUidPks);

		assertThat(actualRuleCodes).isEqualTo(expectedRuleCodes);

		verify(ruleCodesByRuleUidsCache).get(ruleUidPks);
		verify(decoratedFallbackService, never()).findCodesByUids(anyCollection());
		verify(ruleCodesByRuleUidsCache, never()).put(anyCollection(), anyCollection());
	}

	@Test
	public void shouldFindRulesByUidsInDbOnCacheMiss() {


		final Collection<Long> ruleUidPks = Collections.singletonList(RULE_UIDPK);

		final List<Rule> expectedRules = Collections.singletonList(rule);

		when(ruleCodeByRuleUidCache.get(anyLong())).thenReturn(null);
		when(decoratedFallbackService.findByUids(ruleUidPks)).thenReturn(expectedRules);

		Collection<Rule> actualRules = fixture.findByUids(ruleUidPks);

		assertThat(actualRules).isEqualTo(expectedRules);

		verify(ruleCodeByRuleUidCache).get(anyLong());
		verify(ruleByRuleCodeCache, never()).get(anyString());
		verify(decoratedFallbackService).findByUids(ruleUidPks);
		verify(ruleCodeByRuleUidCache).put(RULE_UIDPK, RULE_CODE);
		verify(ruleByRuleCodeCache).put(RULE_CODE, rule);
	}

	@Test
	public void shouldFindRulesByUidsInCacheOnCacheHit() {

		final Collection<Long> ruleUidPks = Collections.singletonList(RULE_UIDPK);
		final List<Rule> expectedRules = Collections.singletonList(rule);

		when(ruleCodeByRuleUidCache.get(RULE_UIDPK)).thenReturn(RULE_CODE);
		when(ruleByRuleCodeCache.get(RULE_CODE)).thenReturn(rule);

		Collection<Rule> actualRules = fixture.findByUids(ruleUidPks);

		assertThat(actualRules).isEqualTo(expectedRules);

		verify(ruleCodeByRuleUidCache).get(anyLong());
		verify(ruleByRuleCodeCache).get(anyString());
		verify(decoratedFallbackService, never()).findCodesByUids(anyCollection());
		verify(ruleCodesByRuleUidsCache, never()).put(anyCollection(), anyCollection());
	}

	@Test
	public void shouldFindByRuleCodeInDbOnCacheMiss() {

		when(ruleByRuleCodeCache.get(RULE_CODE)).thenReturn(null);
		when(decoratedFallbackService.findByRuleCode(RULE_CODE)).thenReturn(rule);

		Rule actualRule = fixture.findByRuleCode(RULE_CODE);

		assertThat(actualRule).isEqualTo(rule);

		verify(ruleByRuleCodeCache).get(RULE_CODE);
		verify(decoratedFallbackService).findByRuleCode(RULE_CODE);
		verify(ruleCodeByRuleUidCache).put(RULE_UIDPK, RULE_CODE);
		verify(ruleByRuleCodeCache).put(RULE_CODE, rule);
		verify(ruleCodeByRuleNameCache).put(RULE_NAME, RULE_CODE);
	}

	@Test
	public void shouldFindByRuleCodeInCacheOnCacheHit() {

		when(ruleByRuleCodeCache.get(RULE_CODE)).thenReturn(rule);

		Rule actualRule = fixture.findByRuleCode(RULE_CODE);

		assertThat(actualRule).isEqualTo(rule);

		verify(ruleByRuleCodeCache).get(RULE_CODE);
		verify(decoratedFallbackService, never()).findByRuleCode(RULE_CODE);
		verify(ruleCodeByRuleUidCache, never()).put(RULE_UIDPK, RULE_CODE);
		verify(ruleByRuleCodeCache, never()).put(RULE_CODE, rule);
		verify(ruleCodeByRuleNameCache, never()).put(RULE_NAME, RULE_CODE);
	}

	@Test
	public void shouldFindByNameInDbOnCacheMissByRuleName() {

		when(ruleCodeByRuleNameCache.get(RULE_NAME)).thenReturn(null);
		when(decoratedFallbackService.findByName(RULE_NAME)).thenReturn(rule);

		Rule actualRule = fixture.findByName(RULE_NAME);

		assertThat(actualRule).isEqualTo(rule);

		verify(ruleCodeByRuleNameCache).get(RULE_NAME);
		verify(decoratedFallbackService).findByName(RULE_NAME);
		verify(ruleCodeByRuleUidCache).put(RULE_UIDPK, RULE_CODE);
		verify(ruleByRuleCodeCache).put(RULE_CODE, rule);
		verify(ruleCodeByRuleNameCache).put(RULE_NAME, RULE_CODE);
	}

	@Test
	public void shouldFindByNameInDbOnCacheMissByRuleCode() {

		when(ruleCodeByRuleNameCache.get(RULE_NAME)).thenReturn(RULE_CODE);
		when(ruleByRuleCodeCache.get(RULE_CODE)).thenReturn(null);
		when(decoratedFallbackService.findByRuleCode(RULE_CODE)).thenReturn(rule);

		Rule actualRule = fixture.findByName(RULE_NAME);

		assertThat(actualRule).isEqualTo(rule);

		verify(ruleCodeByRuleNameCache).get(RULE_NAME);
		verify(decoratedFallbackService).findByRuleCode(RULE_CODE);
		verify(decoratedFallbackService, never()).findByName(RULE_NAME);
		verify(ruleCodeByRuleUidCache).put(RULE_UIDPK, RULE_CODE);
		verify(ruleByRuleCodeCache).put(RULE_CODE, rule);
		verify(ruleCodeByRuleNameCache).put(RULE_NAME, RULE_CODE);
	}

	@Test
	public void shouldFindByNameInCacheOnCacheHit() {

		when(ruleCodeByRuleNameCache.get(RULE_NAME)).thenReturn(RULE_CODE);
		when(ruleByRuleCodeCache.get(RULE_CODE)).thenReturn(rule);

		Rule actualRule = fixture.findByName(RULE_NAME);

		assertThat(actualRule).isEqualTo(rule);

		verify(ruleCodeByRuleNameCache).get(RULE_NAME);
		verify(decoratedFallbackService, never()).findByName(RULE_NAME);
		verify(ruleCodeByRuleUidCache, never()).put(RULE_UIDPK, RULE_CODE);
		verify(ruleByRuleCodeCache, never()).put(RULE_CODE, rule);
		verify(ruleCodeByRuleNameCache, never()).put(RULE_NAME, RULE_CODE);
	}

	@Test
	public void shouldReturnNewBeanInstanceWhenRuleUidIsZero() {
		final Long ruleUidPk = 0L;

		final Rule rule = new PromotionRuleImpl();

		when(decoratedFallbackService.getObject(ruleUidPk)).thenReturn(rule);

		Rule actualRule = fixture.getObject(ruleUidPk);

		assertThat(actualRule).isSameAs(rule);

		verify(decoratedFallbackService).getObject(ruleUidPk);
		verifyNoMoreInteractions(decoratedFallbackService);
		verifyZeroInteractions(ruleCodeByRuleNameCache);
		verifyZeroInteractions(ruleCodeByRuleUidCache);
		verifyZeroInteractions(ruleByRuleCodeCache);
	}

	@Test
	public void shouldReturnNewBeanInstanceWhenRuleUidIsLessThanZero() {
		final Long ruleUidPk = -1L;

		final Rule rule = new PromotionRuleImpl();

		when(decoratedFallbackService.getObject(ruleUidPk)).thenReturn(rule);

		Rule actualRule = fixture.getObject(ruleUidPk);

		assertThat(actualRule).isSameAs(rule);

		verify(decoratedFallbackService).getObject(ruleUidPk);
		verifyNoMoreInteractions(decoratedFallbackService);
		verifyZeroInteractions(ruleCodeByRuleNameCache);
		verifyZeroInteractions(ruleCodeByRuleUidCache);
		verifyZeroInteractions(ruleByRuleCodeCache);
	}

	@Test
	public void shouldGetObjectByUidFromDbOnCacheMiss() {

		when(ruleCodeByRuleUidCache.get(RULE_UIDPK)).thenReturn(null);
		when(decoratedFallbackService.getObject(RULE_UIDPK)).thenReturn(rule);

		Rule actualRule = fixture.getObject(RULE_UIDPK);

		assertThat(actualRule).isSameAs(rule);

		verify(decoratedFallbackService).getObject(RULE_UIDPK);
		verify(ruleCodeByRuleUidCache).get(RULE_UIDPK);
		verifyNoMoreInteractions(decoratedFallbackService);
		verify(ruleCodeByRuleUidCache).put(RULE_UIDPK, RULE_CODE);
		verify(ruleByRuleCodeCache).put(RULE_CODE, rule);
		verify(ruleCodeByRuleNameCache).put(RULE_NAME, RULE_CODE);
	}

	@Test
	public void shouldGetObjectByRuleCodeFromDbOnCacheMiss() {

		when(ruleCodeByRuleUidCache.get(RULE_UIDPK)).thenReturn(RULE_CODE);
		when(ruleByRuleCodeCache.get(RULE_CODE)).thenReturn(null);
		when(decoratedFallbackService.findByRuleCode(RULE_CODE)).thenReturn(rule);

		Rule actualRule = fixture.getObject(RULE_UIDPK);

		assertThat(actualRule).isSameAs(rule);

		verify(decoratedFallbackService).findByRuleCode(RULE_CODE);
		verify(ruleCodeByRuleUidCache).get(RULE_UIDPK);
		verify(ruleByRuleCodeCache).get(RULE_CODE);

		verifyNoMoreInteractions(decoratedFallbackService);

		verify(ruleCodeByRuleUidCache).put(RULE_UIDPK, RULE_CODE);
		verify(ruleByRuleCodeCache).put(RULE_CODE, rule);
		verify(ruleCodeByRuleNameCache).put(RULE_NAME, RULE_CODE);
	}

	@Test
	public void shouldGetObjectByRuleCodeFromCacheOnCacheHit() {

		when(ruleCodeByRuleUidCache.get(RULE_UIDPK)).thenReturn(RULE_CODE);
		when(ruleByRuleCodeCache.get(RULE_CODE)).thenReturn(rule);

		Rule actualRule = fixture.getObject(RULE_UIDPK);

		assertThat(actualRule).isSameAs(rule);

		verifyZeroInteractions(decoratedFallbackService);
		verify(ruleCodeByRuleUidCache).get(RULE_UIDPK);
		verify(ruleByRuleCodeCache).get(RULE_CODE);

		verifyNoMoreInteractions(ruleCodeByRuleUidCache);
		verifyNoMoreInteractions(ruleByRuleCodeCache);
		verifyZeroInteractions(ruleCodeByRuleNameCache);
	}

	@Test
	public void shouldAddRuleToCache() {
		final Rule newRule = mock(Rule.class);
		when(decoratedFallbackService.add(newRule)).thenReturn(rule);

		fixture.add(newRule);

		verify(decoratedFallbackService).add(newRule);
		verifyNoMoreInteractions(decoratedFallbackService);

		verify(ruleCodeByRuleUidCache).put(RULE_UIDPK, RULE_CODE);
		verify(ruleByRuleCodeCache).put(RULE_CODE, rule);
		verify(ruleCodeByRuleNameCache).put(RULE_NAME, RULE_CODE);
	}

	@Test
	public void shouldUpdateRuleToCache() {

		final String updatedRuleName = "UpdatedName";
		final String updatedRuleCode = "UpdatedCode";
		final Rule updatedRule = mock(Rule.class);

		when(updatedRule.getUidPk()).thenReturn(RULE_UIDPK);
		when(updatedRule.getCode()).thenReturn(updatedRuleCode);
		when(updatedRule.getName()).thenReturn(updatedRuleName);
		when(decoratedFallbackService.update(rule)).thenReturn(updatedRule);

		fixture.update(rule);

		verify(decoratedFallbackService).update(rule);
		verifyNoMoreInteractions(decoratedFallbackService);

		verify(ruleCodeByRuleUidCache).put(RULE_UIDPK, updatedRuleCode);
		verify(ruleByRuleCodeCache).put(updatedRuleCode, updatedRule);
		verify(ruleCodeByRuleNameCache).put(updatedRuleName, updatedRuleCode);
	}

	@Test
	public void shouldRemoveRuleFromAllThreeCaches() {

		fixture.remove(rule);

		verify(decoratedFallbackService).remove(rule);
		verifyNoMoreInteractions(decoratedFallbackService);

		verify(ruleCodeByRuleUidCache).remove(RULE_UIDPK);
		verify(ruleByRuleCodeCache).remove(RULE_CODE);
		verify(ruleCodeByRuleNameCache).remove(RULE_NAME);
	}

	@Test
	public void shouldRemoveRuleFromTwoCachesWhenRuleCodeIsNull() {

		when(rule.getCode()).thenReturn(null);

		fixture.remove(rule);

		verify(decoratedFallbackService).remove(rule);
		verifyNoMoreInteractions(decoratedFallbackService);

		verify(ruleCodeByRuleUidCache).remove(RULE_UIDPK);
		verifyZeroInteractions(ruleByRuleCodeCache);
		verify(ruleCodeByRuleNameCache).remove(RULE_NAME);
	}

	@Test
	public void shouldRemoveRuleFromOneCacheWhenRuleCodeAndRuleNamesAreNull() {

		when(rule.getCode()).thenReturn(null);
		when(rule.getName()).thenReturn(null);

		fixture.remove(rule);

		verify(decoratedFallbackService).remove(rule);
		verifyNoMoreInteractions(decoratedFallbackService);

		verify(ruleCodeByRuleUidCache).remove(RULE_UIDPK);
		verifyZeroInteractions(ruleByRuleCodeCache);
		verifyZeroInteractions(ruleCodeByRuleNameCache);
	}
}
