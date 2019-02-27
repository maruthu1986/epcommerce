/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.store.promotions.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.PlatformUI;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.helpers.ChangeSetHelper;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.wizard.EpWizardDialog;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareHandler;
import com.elasticpath.cmclient.store.promotions.wizard.NewCatalogPromotionWizard;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.service.changeset.ChangeSetMemberAction;
import com.elasticpath.service.rules.RuleService;

/**
 * Creates a catalog promotion by opening up the new store promotion wizard. If the wizard is
 * completed successfully, the promotion is saved.
 */
public class CreateCatalogPromoHandler extends AbstractPolicyAwareHandler {
	
	private final PolicyActionContainer handlerContainer = addPolicyActionContainer("createCatalogPromoHandler"); //$NON-NLS-1$		

	private final ChangeSetHelper changeSetHelper = ServiceLocator.getService(ChangeSetHelper.BEAN_ID);

	@Override
	public Object execute(final ExecutionEvent arg0) throws ExecutionException {
		final NewCatalogPromotionWizard wizard = new NewCatalogPromotionWizard();
		final WizardDialog dialog = new EpWizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), wizard);

		if (dialog.open() == Window.OK) {
			final RuleService ruleService = ServiceLocator.getService(ContextIdNames.RULE_SERVICE);
			ruleService.add(wizard.getModel());
			changeSetHelper.addObjectToChangeSet(wizard.getModel(), ChangeSetMemberAction.ADD);
		}
		return wizard.getModel();
	}
	
	@Override
	public boolean isEnabled() {
		boolean enabled = true;
		if (getStatePolicy() != null) {
			enabled = (EpState.EDITABLE == getStatePolicy().determineState(handlerContainer));
		}
		return enabled;		
	}

	@Override
	public String getTargetIdentifier() {
		return "createCatalogPromoHandler"; //$NON-NLS-1$
	}
}
