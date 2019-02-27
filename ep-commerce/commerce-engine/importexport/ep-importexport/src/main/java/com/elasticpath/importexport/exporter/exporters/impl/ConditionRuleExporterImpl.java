/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.importexport.exporter.exporters.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.elasticpath.domain.rules.Rule;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.adapters.promotion.RuleAdapter;
import com.elasticpath.importexport.common.dto.promotion.rule.RuleDTO;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.exporter.context.ExportContext;
import com.elasticpath.service.rules.RuleService;

/**
 * Determines data required for export of condition rules.
 */
public class ConditionRuleExporterImpl extends AbstractExporterImpl<Rule, RuleDTO, String> {

	private RuleAdapter ruleAdapter;

	private RuleService ruleService;

	@Override
	protected void initializeExporter(final ExportContext context) {
		// do nothing
	}

	@Override
	protected List<Rule> findByIDs(final List<String> subList) {
		List<Rule> results = new ArrayList<>(ruleService.findByRuleCodes(subList));
		Collections.sort(results, Comparator.comparing(Rule::getCode));
		return results;
	}

	@Override
	protected DomainAdapter<Rule, RuleDTO> getDomainAdapter() {
		return ruleAdapter;
	}

	@Override
	protected Class<? extends RuleDTO> getDtoClass() {
		return RuleDTO.class;
	}

	@Override
	public Class<?>[] getDependentClasses() {
		return new Class<?>[] {Rule.class};
	}

	@Override
	protected List<String> getListExportableIDs() {
		return new ArrayList<>(getContext().getDependencyRegistry().getDependentGuids(Rule.class));
	}

	/**
	 * @return  condition rule export job type.
	 */
	@Override
	public JobType getJobType() {
		return JobType.CONDITIONRULE;
	}

	/**
	 * @param ruleAdapter condition rule adapter
	 */
	public void setRuleAdapter(final RuleAdapter ruleAdapter) {
		this.ruleAdapter = ruleAdapter;
	}

	/**
	 * @param ruleService the ruleService to set
	 */
	public void setRuleService(final RuleService ruleService) {
		this.ruleService = ruleService;
	}
}
