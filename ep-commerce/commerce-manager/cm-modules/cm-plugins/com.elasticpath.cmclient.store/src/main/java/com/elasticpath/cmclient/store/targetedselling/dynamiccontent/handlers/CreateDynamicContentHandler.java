/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.store.targetedselling.dynamiccontent.handlers;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.event.EventType;
import com.elasticpath.cmclient.core.event.UIEvent;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.store.AbstractEPCampaignWizard;
import com.elasticpath.cmclient.store.StorePlugin;
import com.elasticpath.cmclient.store.targetedselling.TargetedSellingMessages;
import com.elasticpath.cmclient.store.targetedselling.dynamiccontent.model.DynamicContentSearchTabModel;
import com.elasticpath.cmclient.store.targetedselling.dynamiccontent.model.impl.DynamicContentSearchTabModelImpl;
import com.elasticpath.cmclient.store.targetedselling.dynamiccontent.wizard.NewDynamicContentWizard;
import com.elasticpath.cmclient.store.targetedselling.handlers.AbstractCreateHandler;
import com.elasticpath.cmclient.store.targetedselling.handlers.CreateHandlerService;
import com.elasticpath.cmclient.store.targetedselling.handlers.serviceadapters.DynamicContentCreateHandlerServiceAdapter;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.contentspace.DynamicContent;

/**
 * Creates a catalog promotion by opening up the new store promotion wizard. If
 * the wizard is completed successfully, the promotion is saved.
 */
public class CreateDynamicContentHandler extends
		AbstractCreateHandler<DynamicContent, DynamicContent> {

	private final PolicyActionContainer handlerContainer = addPolicyActionContainer("createDynamicContentContainer"); //$NON-NLS-1$

	@Override
	public boolean isEnabled() {
		boolean enabled = true;
		if (getStatePolicy() != null) {
			enabled = (EpState.EDITABLE == getStatePolicy().determineState(handlerContainer));
		}
		return enabled;
	}

	@Override
	protected String getNameExistsMessage() {
		return TargetedSellingMessages.get().DynamicContentNameExists;
	}

	@Override
	protected CreateHandlerService<DynamicContent> getService() {
		return new DynamicContentCreateHandlerServiceAdapter(
				ServiceLocator.getService(
				ContextIdNames.DYNAMIC_CONTENT_SERVICE));
	}

	@Override
	protected AbstractEPCampaignWizard<DynamicContent> createWizardInstace() {
		return new NewDynamicContentWizard();
	}

	@Override
	protected DynamicContent getDomainObjectFromModel(final DynamicContent model) {
		return model;
	}
	
	@Override
	protected void save(final DynamicContent model) {
		super.save(model);

		final EventType eventType = EventType.CREATE;

		UIEvent<DynamicContentSearchTabModel> eventForList = new UIEvent<>(
				new DynamicContentSearchTabModelImpl(),
				eventType,
				false
		);

		UIEvent<DynamicContent> event = new UIEvent<>(
				model,
				eventType,
				false
		);

		StorePlugin.getDefault().getDynamicContentListController().onEvent(eventForList);
		StorePlugin.getDefault().getDynamicContentsController().onEvent(event);
	}

	@Override
	public String getTargetIdentifier() {
		return "createDynamicContentHandler"; //$NON-NLS-1$
	}
	
}
