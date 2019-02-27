/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.store.promotions.editors;

import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;

import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.EpUiException;
import com.elasticpath.cmclient.policy.StatePolicy;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareFormEditor;
import com.elasticpath.cmclient.store.StorePlugin;
import com.elasticpath.cmclient.store.promotions.PromotionsMessages;
import com.elasticpath.cmclient.store.promotions.event.PromotionsChangeEvent;
import com.elasticpath.cmclient.store.promotions.event.PromotionsEventService;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.rules.RuleElement;
import com.elasticpath.domain.rules.RuleException;
import com.elasticpath.domain.rules.RuleParameter;
import com.elasticpath.service.rules.RuleService;

/**
 * Implements a multi-page editor for displaying/editing catalog promotions.
 */
public class CatalogPromotionsEditor extends AbstractPolicyAwareFormEditor {

	/**
	 * ID of the editor. It is the same as the class name.
	 */
	public static final String ID_EDITOR = "com.elasticpath.cmclient.store.promotions.editors.PromotionsEditor.Catalog"; //$NON-NLS-1$

	private static final int TOTAL_WORK_UNITS = 3;

	private static final int RULES_PAGE_INDEX = 1;

	private Rule rule;

	private RuleService ruleService;

	@Override
	public void initEditor(final IEditorSite site, final IEditorInput input) {
		this.ruleService = ServiceLocator.getService(ContextIdNames.RULE_SERVICE);
		String guid = input.getAdapter(String.class);
		this.rule = ruleService.findByRuleCode(guid);
		setPartName(rule.getName());
	}

	@Override
	protected void addPages() {
		final PolicyActionContainer pageContainer = addPolicyActionContainer("catalogPromotionEditor"); //$NON-NLS-1$

		try {
			addPage(new PromotionDetailsPage(this, true), pageContainer);
			addPage(new PromotionRulesPage(this, true), pageContainer);
			addExtensionPages(getClass().getSimpleName(), StorePlugin.PLUGIN_ID);
		} catch (final PartInitException e) {
			throw new EpUiException(e);
		}
	}

	@Override
	public void reloadModel() {
		rule = ruleService.get(rule.getUidPk());
	}

	@Override
	protected void saveModel(final IProgressMonitor monitor) {
		monitor.beginTask(PromotionsMessages.get().PromotionEditor_Save_StatusBarMsg, TOTAL_WORK_UNITS);
		try {
			if (!checkProductCategorySkuSelected()) {
				monitor.setCanceled(true);
				return;
			}
			monitor.worked(1);
			Rule updatedRule = ruleService.update(rule);
			monitor.worked(1);
			PromotionsEventService.getInstance().firePromotionsChangeEvent(new PromotionsChangeEvent(this, updatedRule));
		} finally {
			monitor.done();
		}
	}

	/**
	 * Returns true if all rule elements with Product/Category/SKU values have valid values (i.e. valid means the user has selected a specific
	 * Product/Category/SKU; false otherwise.
	 *
	 * @return true if all rule elements with Product/Category/SKU values have valid values (i.e. valid means the user has selected a specific
	 * Product/Category/SKU; false otherwise.
	 */
	private boolean checkProductCategorySkuSelected() {
		// Iterate through the Rule's rule-elements
		for (RuleElement currRuleElement : getModel().getRuleElements()) {
			// Check the parameters of the rule-elements
			for (RuleParameter currElementParam : currRuleElement.getParameters()) {
				if (!isRuleParameterComplete(currElementParam)) {
					this.setActivePage(RULES_PAGE_INDEX);
					MessageDialog.openWarning(this.getSite().getShell(), PromotionsMessages.get().PromoRulesDefintion_Error_Title,
							PromotionsMessages.get().PromoRulesDefintion_Error_Select_Links);
					return false;
				}
			}

			// Check the parameters of rule-exceptions
			for (RuleException currRuleException : currRuleElement.getExceptions()) {
				for (RuleParameter currExceptionParam : currRuleException.getParameters()) {
					if (!isRuleParameterComplete(currExceptionParam)) {
						this.setActivePage(RULES_PAGE_INDEX);
						MessageDialog.openWarning(this.getSite().getShell(), PromotionsMessages.get().PromoRulesDefintion_Error_Title,
								PromotionsMessages.get().PromoRulesDefintion_Error_Select_Links);
						return false;
					}
				}
			}
		}

		return true;
	}

	/**
	 * Returns true if all Product/Category/SKU values have been selected by the user; false otherwise.
	 *
	 * @param parameter the RuleParameter to check
	 * @return true if all Product/Category/SKU values have been selected by the user; false otherwise.
	 */
	private boolean isRuleParameterComplete(final RuleParameter parameter) {
		final boolean productIsNull = parameter.getKey().equals(RuleParameter.PRODUCT_CODE_KEY) && (parameter.getValue() == null);
		final boolean categoryIsNull = parameter.getKey().equals(RuleParameter.CATEGORY_CODE_KEY) && (parameter.getValue() == null);
		final boolean skuIsNull = parameter.getKey().equals(RuleParameter.SKU_CODE_KEY) && (parameter.getValue() == null);

		return !productIsNull && !categoryIsNull && !skuIsNull;
	}

	@Override
	public Rule getModel() {
		return rule;
	}

	@Override
	public Collection<Locale> getSupportedLocales() {
		return Collections.emptyList();
	}

	@Override
	public Locale getDefaultLocale() {
		return null;
	}

	@Override
	protected String getSaveOnCloseMessage() {
		return
			NLS.bind(PromotionsMessages.get().PromotionEditor_OnSavePrompt,
			getEditorName());
	}

	@Override
	public void applyStatePolicy(final StatePolicy statePolicy) {
		statePolicy.init(getModel());
		super.applyStatePolicy(statePolicy);
	}

	@Override
	public String getTargetIdentifier() {
		return "catalogPromotionEditor"; //$NON-NLS-1$
	}

	@Override
	protected String getEditorName() {
		String name = ""; //$NON-NLS-1$
		if (rule != null) {
			name = rule.getName();
		}
		return name;
	}

	@Override
	protected String getEditorToolTip() {
		return getEditorName();
	}

}
