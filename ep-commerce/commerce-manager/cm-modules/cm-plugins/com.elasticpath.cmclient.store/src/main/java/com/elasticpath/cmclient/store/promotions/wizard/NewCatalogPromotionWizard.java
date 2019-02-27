/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.store.promotions.wizard;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.wizard.AbstractEpWizard;
import com.elasticpath.cmclient.store.promotions.PromotionsImageRegistry;
import com.elasticpath.cmclient.store.promotions.PromotionsMessages;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.exception.DuplicateNameException;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.rules.RuleScenarios;
import com.elasticpath.service.rules.RuleService;
import com.elasticpath.service.rules.RuleSetService;

/**
 * The wizard for creating catalog promotions.
 */
public class NewCatalogPromotionWizard extends AbstractEpWizard<Rule> {

	private static final String DETAILS_PAGE_NAME = "NewPromotionWizardStep1Page"; //$NON-NLS-1$

	private static final String RULES_PAGE_NAME = "NewPromotionWizardRulesPage"; //$NON-NLS-1$

	private final Rule model;

	/**
	 * Default constructor.
	 */
	public NewCatalogPromotionWizard() {
		super(PromotionsMessages.get().NewCatalogPromotionWizard_Title, null, PromotionsImageRegistry
				.getImage(PromotionsImageRegistry.PROMOTION_CATALOG_CREATE));
		model = ServiceLocator.getService(ContextIdNames.PROMOTION_RULE);
		final RuleSetService ruleSetService = ServiceLocator.getService(
				ContextIdNames.RULE_SET_SERVICE);
		model.setRuleSet(ruleSetService.findByScenarioId(RuleScenarios.CATALOG_BROWSE_SCENARIO));
		model.setEnabled(true);
	}
	
	@Override
	public boolean performFinish() {
		// ensure rule name uniqueness
		if (ruleNameExists(model.getName())) {
			NewPromotionWizardDetailsPage newPromoDetailsPage = ((NewPromotionWizardDetailsPage) this.getPage(DETAILS_PAGE_NAME));
			newPromoDetailsPage.setErrorMessage(PromotionsMessages.get().CreatePromotionWizardDetailsPage_Name_Uniqueness_Error);
			getContainer().showPage(newPromoDetailsPage);
			return false;
		}
	
		return true;
	}
	
	
	private boolean ruleNameExists(final String ruleName) {
		final RuleService ruleService = ServiceLocator.getService(ContextIdNames.RULE_SERVICE);
		try {
			final Rule rule = ruleService.findByName(ruleName);
			if (rule != null) {
				return true;
			}
		} catch (DuplicateNameException e) {
			return true;
		}
		return false;
	}

	@Override
	public Rule getModel() {
		return model;
	}

	@Override
	public void addPages() {
		addPage(new NewPromotionWizardDetailsPage(DETAILS_PAGE_NAME,
				PromotionsMessages.get().CreateCatalogPromotionWizardDetailsPage_Catalog_Title, true));
		addPage(new NewPromotionWizardRulesPage(RULES_PAGE_NAME, PromotionsMessages.get().CreateCatalogPromotionWizardRulesPage_Title,
				true));
	}
}