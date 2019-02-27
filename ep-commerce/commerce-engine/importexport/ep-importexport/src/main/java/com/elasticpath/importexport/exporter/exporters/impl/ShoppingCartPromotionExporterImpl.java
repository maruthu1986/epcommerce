/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.importexport.exporter.exporters.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import com.elasticpath.domain.rules.Rule;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.adapters.promotion.PromotionAdapter;
import com.elasticpath.importexport.common.dto.promotion.cart.PromotionDTO;
import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.exporter.context.DependencyRegistry;
import com.elasticpath.importexport.exporter.context.ExportContext;
import com.elasticpath.importexport.exporter.search.ImportExportSearcher;
import com.elasticpath.ql.parser.EPQueryType;
import com.elasticpath.service.rules.RuleService;

/**
 * Prepares the list of promotion rules based on EPQL query and performs export.
 */
public class ShoppingCartPromotionExporterImpl extends AbstractExporterImpl<Rule, PromotionDTO, Long> {

	private RuleService ruleService;

	private List<Long> ruleUidPkList = Collections.emptyList();

	private ImportExportSearcher importExportSearcher;

	private PromotionAdapter promotionAdapter;

	private static final Logger LOG = Logger.getLogger(ShoppingCartPromotionExporterImpl.class);

	/**
	 * {@inheritDoc} throws RuntimeException can be thrown if rule UID list could not be initialized.
	 */
	@Override
	protected void initializeExporter(final ExportContext context) throws ConfigurationException {
		ruleUidPkList = importExportSearcher.searchUids(getContext().getSearchConfiguration(), EPQueryType.PROMOTION);
		LOG.info("The UidPk list for " + ruleUidPkList.size() + " rules are retrieved from database.");
	}

	@Override
	protected List<Long> getListExportableIDs() {
		final List<Long> promotionsToCheckIfAreDependent = new LinkedList<>();
		if (getContext().getDependencyRegistry().supportsDependency(Rule.class)) {
			List<Long> ruleUidPks = convertRuleCodesToUids(getContext().getDependencyRegistry().getDependentGuids(Rule.class));
			promotionsToCheckIfAreDependent.addAll(ruleUidPks); // add the ones existent
		}
		promotionsToCheckIfAreDependent.addAll(ruleUidPkList);
		// find all promotions on which this ones depend on in a tree order
		final Set<Long> resultSet = retrieveDependentRulesAndInsertThemBefore(promotionsToCheckIfAreDependent);
		
		// add initial promotions uids for export
		resultSet.addAll(ruleUidPkList);
		
		return Arrays.asList(resultSet.toArray(new Long[resultSet.size()]));
	}

	private List<Long> convertRuleCodesToUids(final NavigableSet<String> ruleCodes) {
		List<Rule> rules = ruleService.findByRuleCodes(ruleCodes);
		List<Long> uids = new ArrayList<>();
		for (Rule rule : rules) {
			uids.add(rule.getUidPk());
		}
		return uids;
	}

	/**
	 * The dependent promotions should be exported before, 
	 * so at the import the depend will already be in the database.
	 * 
	 * @param ruleUidList - the rules that need to be chaked that are dependent on other promotions
	 *
	 * @return all the promotion that will be exported in a specific order
	 */
	private Set<Long> retrieveDependentRulesAndInsertThemBefore(final List<Long> ruleUidList) {
		return ruleService.retrievePromotionDependencies(new LinkedHashSet<>(ruleUidList));
	}

	@Override
	protected List<Rule> findByIDs(final List<Long> subList) {
		List<Rule> results = new ArrayList<>(ruleService.findByUids(subList));
		Collections.sort(results, Comparator.comparing(Rule::getCode));
		return results;
	}

	@Override
	protected DomainAdapter<Rule, PromotionDTO> getDomainAdapter() {
		return promotionAdapter;
	}

	@Override
	protected Class<? extends PromotionDTO> getDtoClass() {
		return PromotionDTO.class;
	}

	@Override
	public Class<?>[] getDependentClasses() {
		return new Class<?>[] {Rule.class};
	}

	@Override
	public JobType getJobType() {
		return JobType.PROMOTION;
	}

	/**
	 * @param ruleService the ruleService to set
	 */
	public void setRuleService(final RuleService ruleService) {
		this.ruleService = ruleService;
	}

	/**
	 * @param importExportSearcher the ImportExportSearcher
	 */
	public void setImportExportSearcher(final ImportExportSearcher importExportSearcher) {
		this.importExportSearcher = importExportSearcher;
	}

	/**
	 * @param promotionAdapter the promotionAdapter to set
	 */
	public void setPromotionAdapter(final PromotionAdapter promotionAdapter) {
		this.promotionAdapter = promotionAdapter;
	}

	@Override
	protected void addDependencies(final List<Rule> objects, final DependencyRegistry dependencyRegistry) {
		final NavigableSet<String> dependentRuleCodes = new TreeSet<>();
		for (Rule rule : objects) {
			dependentRuleCodes.add(rule.getCode());
		}
		dependencyRegistry.addGuidDependencies(Rule.class, dependentRuleCodes);
	}
}
