/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.rules.impl;

import java.io.FileNotFoundException;
import java.io.Reader;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.drools.core.impl.InternalKnowledgeBase;
import org.kie.api.KieBase;
import org.kie.api.io.ResourceType;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;
import org.kie.internal.builder.KnowledgeBuilderError;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.rules.EpRuleBase;
import com.elasticpath.domain.rules.RuleScenarios;
import com.elasticpath.domain.rules.RuleSet;
import com.elasticpath.domain.store.Store;
import com.elasticpath.persistence.PropertiesDao;
import com.elasticpath.persistence.api.EpPersistenceException;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.service.rules.RecompilingRuleEngine;
import com.elasticpath.service.rules.RuleSetService;
import com.elasticpath.service.rules.RulesPackageCompilationException;
import com.elasticpath.service.store.StoreService;

/**
 * Exposes methods to compile the rule base and store them in the database. It also provides the
 * ability to read rules from the database. This rules engine is implemented using Drools Rules
 * 3.0.
 */
public class DBCompilingRuleEngineImpl extends AbstractRuleEngineImpl implements RecompilingRuleEngine {

	private static final Logger LOG = Logger.getLogger(DBCompilingRuleEngineImpl.class);

	/** The property name for the last rule update/recompile date. */
	private static final String LAST_COMPILATION_BEGIN_DATE_PROP = "LastSuccessfulCompilationBeginDate";

	/** File name of the properties file where the last update date is stored. */
	private static final String UPDATE_PROP_FILE = "ruleUpdate";

	/**
	 * Required date format for SOLR dates. Store single instance rather than create a new one all
	 * the time.
	 */
	private final DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);

	private RuleSetService ruleSetService;

	private PropertiesDao propertiesDao;

	private Properties cacheUpdateProperties;

	private TimeService timeService;

	private StoreService storeService;

	private final Map<String, KieBase> catalogRuleBaseMap = new ConcurrentHashMap<>();

	private final Map<String, KieBase> cartRuleBaseMap = new ConcurrentHashMap<>();

	private Properties configProps;

	private BeanFactory beanFactory;

	/**
	 * Default constructor.
	 */
	public DBCompilingRuleEngineImpl() {
		// setup up required properties for the date formatter.
		dateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
	}

	@Override
	protected KieBase getCartRuleBase(final Store store) {
		final KieBase ruleBase;
		if (cartRuleBaseMap.containsKey(store.getCode())) {
			ruleBase = cartRuleBaseMap.get(store.getCode());
		} else {
			ruleBase = readRuleBase(RuleScenarios.CART_SCENARIO, store);
			cartRuleBaseMap.put(store.getCode(), ruleBase);
		}
		return ruleBase;
	}

	@Override
	protected KieBase getCatalogRuleBase(final Store store) {
		final KieBase ruleBase;
		if (catalogRuleBaseMap.containsKey(store.getCode())) {
			ruleBase = catalogRuleBaseMap.get(store.getCode());
		} else {
			ruleBase = readRuleBase(RuleScenarios.CATALOG_BROWSE_SCENARIO, store);
			catalogRuleBaseMap.put(store.getCode(), ruleBase);
		}
		return ruleBase;
	}

	/**
	 * Reads in a rule set from a file. This is a temporary method and will be replaced by a call
	 * to service code that will get the rules from the DB.
	 *
	 * @param scenarioId the id of the scenario for which the rule set is to be retrieved
	 * @return a RuleBase object corresponding to the rule set file.
	 */
	private KieBase readRuleBase(final int scenarioId, final Store store) {
		EpRuleBase ruleBase = null;
		switch (scenarioId) {
		case RuleScenarios.CART_SCENARIO:
			ruleBase = getRuleService().findRuleBaseByScenario(store, null, scenarioId);
			break;
		case RuleScenarios.CATALOG_BROWSE_SCENARIO:
			ruleBase = getRuleService().findRuleBaseByScenario(null, store.getCatalog(), scenarioId);
			break;
		default:
			LOG.debug("Unknown rule scenario ID, assuming store based");
			ruleBase = getRuleService().findRuleBaseByScenario(store, null, scenarioId);
			break;
		}

		if (ruleBase == null) {
			RuleSet ruleSet = ruleSetService.findByScenarioId(scenarioId);
			return recompileRuleBase(ruleSet, store);
		}

		return ruleBase.getRuleBase();
	}

	/**
	 * Synchronized so we only try to recompile the rule base once at a time.   This may be able to be
	 * pushed down to {@code #storeRuleBase(RuleBase, RuleSet, Store, Catalog)} if it becomes an issue.
	 *
	 * @param ruleSet rule set to compile
	 * @param store store used to create rules
	 * @return compiled ruleBase
	 */
	public synchronized KieBase recompileRuleBase(final RuleSet ruleSet, final Store store) { //NOPMD
		final String ruleCode = ruleSet.getRuleCode(store);
		LOG.debug(ruleCode);
		final Reader source = new StringReader(ruleCode);

		final InternalKnowledgeBase ruleBase;
		try {
			// Use package builder to build up a rule package.
			// An alternative lower level class called "DrlParser" can also be used...
			final KnowledgeBuilderConfiguration conf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration(
					getRuleEngineConfigProps(),
					(ClassLoader[]) null);

			final KnowledgeBuilder builder = KnowledgeBuilderFactory.newKnowledgeBuilder(conf);

			// this will parse and compile in one step
			// NOTE: There are 2 methods here, the one argument one is for normal DRL.
			builder.add(ResourceFactory.newReaderResource(source), ResourceType.DRL);

			//ruleBase should not try to add a knowledge base with errors
			if (!builder.getErrors().isEmpty()) {
				String errorMsg = "Drools code did not compile. Errors: ";
				for (KnowledgeBuilderError error : builder.getErrors()) {
					errorMsg = errorMsg.concat(error.getMessage() + " ");
				}

				throw new RulesPackageCompilationException(errorMsg);
			}
			
			// add the package to a rulebase (deploy the rule package).
			ruleBase = createRuleBase();
			ruleBase.addPackages(builder.getKnowledgePackages());
		} catch (RulesPackageCompilationException rpce) { //NOPMD necessary because of below catch for Exception
			throw rpce;
		} catch (Exception e) {
			throw new EpServiceException("Failed to readRuleBase.", e);
		}
		
		if (ruleSet.getScenario() == RuleScenarios.CART_SCENARIO) {
			storeRuleBase(ruleBase, ruleSet, store, null);
		} else if (ruleSet.getScenario() == RuleScenarios.CATALOG_BROWSE_SCENARIO) {
			storeRuleBase(ruleBase, ruleSet, null, store.getCatalog());
		} else {
			LOG.debug("Unknown rule scenario ID, assuming store based");
			storeRuleBase(ruleBase, ruleSet, store, null);
		}
		
		LOG.info("Successfully re-compiled rule base for scenario " + ruleSet.getScenario());
		return ruleBase;
	}

	/**
	 * Retrieve the last update date from the properties file. This implementation formats the
	 * date with a UTC XML date string.
	 * 
	 * @return the last update date
	 */
	protected Date getLastSuccessfulCompilationBeginDate() {
		try {
			cacheUpdateProperties = propertiesDao.getPropertiesFile(UPDATE_PROP_FILE);
		} catch (EpPersistenceException e) {
			if (!(e.getCause() instanceof FileNotFoundException)) {
				throw e;
			}
			cacheUpdateProperties = new Properties();
		}
		Date date = null;
		final String dateString = cacheUpdateProperties.getProperty(LAST_COMPILATION_BEGIN_DATE_PROP);
		if (dateString != null && dateString.length() > 0) {
			synchronized (dateFormatter) {
				try {
					date = dateFormatter.parse(dateString);
				} catch (ParseException e) {
					LOG.warn(dateString + " is a bad date string. The format is: yyyy-MM-dd'T'HH:mm:ss'Z'. Ignoring.", e);
				}
			}
		}
		return date;
	}

	/**
	 * Set the date of the last update to the current date/time. This implementation formats the date with a UTC XML date string.
	 * 
	 * @param compilationBeginDate compilation begin date
	 */
	protected void setLastSuccessfulCompilationBeginDate(final Date compilationBeginDate) {
		synchronized (dateFormatter) {
			cacheUpdateProperties.setProperty(LAST_COMPILATION_BEGIN_DATE_PROP, dateFormatter.format(compilationBeginDate));
		}
		propertiesDao.storePropertiesFile(cacheUpdateProperties, UPDATE_PROP_FILE);
	}

	/**
	 * Set the rule set service that will provide rules.
	 *
	 * @param ruleSetService the <code>RuleSetService</code>
	 */
	public void setRuleSetService(final RuleSetService ruleSetService) {
		this.ruleSetService = ruleSetService;
	}

	/**
	 * Regenerates the rule base using a new set of rules retrieved from the persistence layer.
	 * If the date is not set in the properties files then we set the current date.
	 */
	@Override
	public void recompileRuleBase() {
		final long startTime = System.currentTimeMillis();
		LOG.info("Start recompile rule base quartz job at: " + new Date(startTime));
		
		Date compilationBeginDate = getTimeService().getCurrentTime();
		Date lastSuccessfulCompilationBeginDate = getLastSuccessfulCompilationBeginDate();
		if (lastSuccessfulCompilationBeginDate == null) {
				lastSuccessfulCompilationBeginDate = new Date(0);
		}
		List<RuleSet> ruleSets = ruleSetService.findByModifiedDate(lastSuccessfulCompilationBeginDate);
		List<Store> stores = storeService.findAllCompleteStores();
		LOG.debug("Checking for modified rule sets");

		for (Store store : stores) {
			for (RuleSet currRuleSet : ruleSets) {
				if (currRuleSet.getScenario() == RuleScenarios.CART_SCENARIO) {
					LOG.info("Re-compiling rule set for the shopping cart scenario");
					KieBase cartRuleBase = recompileRuleBase(currRuleSet, store);
					cartRuleBaseMap.put(store.getCode(), cartRuleBase);
				} else {
					LOG.info("Re-compiling rule set for the browse catalog scenario");
					KieBase catalogRuleBase = recompileRuleBase(currRuleSet, store);
					catalogRuleBaseMap.put(store.getCode(), catalogRuleBase);
				}
			}
		}

		setLastSuccessfulCompilationBeginDate(compilationBeginDate);
		LOG.info("Recompile rule base quartz job completed in (ms): " + (System.currentTimeMillis() - startTime));
	}
	
	/**
	 * Stores the given {@link InternalKnowledgeBase} for later retrieval. If a rule base already exists,
	 * updates the existing rule base. Generally either the {@code store} or {@code catalog}
	 * should be {@code null}, but not both.
	 * 
	 * @param ruleBase the compiled rule base to store
	 * @param ruleSet the rule set to this rule base is based on
	 * @param store the store of the rule base
	 * @param catalog the catalog of the rule base
	 * @return the stored rule base
	 */
	protected EpRuleBase storeRuleBase(final KieBase ruleBase, final RuleSet ruleSet, final Store store, final Catalog catalog) {

		// first try and find the existing rule base
		EpRuleBase epRuleBase = getRuleService().findRuleBaseByScenario(store, catalog, ruleSet.getScenario());
		if (epRuleBase == null) {
			epRuleBase = beanFactory.getBean(ContextIdNames.EP_RULE_BASE);
		}

		epRuleBase.setRuleBase(ruleBase);
		epRuleBase.setScenarioId(ruleSet.getScenario());
		epRuleBase.setCatalog(catalog);
		epRuleBase.setStore(store);

		return getRuleService().saveOrUpdateRuleBase(epRuleBase);
	}

	/**
	 * Reset cache of rule bases.
	 */
	public void resetRuleBaseCache() {
		this.cartRuleBaseMap.clear();
		this.catalogRuleBaseMap.clear();
	}
	
	/**
	 * Set the DAO used to load properties.
	 *
	 * @param propertiesDao the DAO used to load properties.
	 */
	public void setPropertiesDao(final PropertiesDao propertiesDao) {
		this.propertiesDao = propertiesDao;
	}

	/**
	 * Sets the {@link TimeService} instance to use.
	 *
	 * @param timeService the {@link TimeService} instance to use
	 */
	public void setTimeService(final TimeService timeService) {
		this.timeService = timeService;
	}


	/**
	 * Get the injected {@link TimeService} instance.
	 *
	 * @return injected {@link TimeService} instance
	 */
	protected TimeService getTimeService() {
		return this.timeService;
	}

	/**
	 * Sets the {@link StoreService} instance to use.
	 *
	 * @param storeService the {@link StoreService} instance to use
	 */
	public void setStoreService(final StoreService storeService) {
		this.storeService = storeService;
	}
		
	/**
	 * @return configuration properties for drools rule engine.
	 */
	protected Properties getRuleEngineConfigProps() {
		return configProps;
	}
	
	/**
	 * @param props configuration properties for drools rule engine.
	 */
	public void setRuleEngineConfigProps(final Properties props) {
		this.configProps = props;
	}

	@Override
	public void setBeanFactory(final BeanFactory beanFactory) {
		super.setBeanFactory(beanFactory);
		this.beanFactory = beanFactory;
	}
}
