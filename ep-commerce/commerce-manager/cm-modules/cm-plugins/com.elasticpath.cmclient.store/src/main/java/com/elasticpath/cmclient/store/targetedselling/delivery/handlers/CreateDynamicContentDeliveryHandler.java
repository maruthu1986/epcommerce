/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.store.targetedselling.delivery.handlers;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.event.EventType;
import com.elasticpath.cmclient.core.event.UIEvent;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.store.AbstractEPCampaignWizard;
import com.elasticpath.cmclient.store.StorePlugin;
import com.elasticpath.cmclient.store.targetedselling.TargetedSellingMessages;
import com.elasticpath.cmclient.store.targetedselling.delivery.actions.SellingContextHelper;
import com.elasticpath.cmclient.store.targetedselling.delivery.model.DynamicContentDeliverySearchTabModel;
import com.elasticpath.cmclient.store.targetedselling.delivery.model.impl.DynamicContentDeliverySearchTabModelImpl;
import com.elasticpath.cmclient.store.targetedselling.delivery.wizard.DynamicContentDeliveryWizard;
import com.elasticpath.cmclient.store.targetedselling.delivery.wizard.model.DynamicContentDeliveryModelAdapter;
import com.elasticpath.cmclient.store.targetedselling.handlers.AbstractCreateHandler;
import com.elasticpath.cmclient.store.targetedselling.handlers.CreateHandlerService;
import com.elasticpath.cmclient.store.targetedselling.handlers.serviceadapters.DynamicContentDeliveryCreateHandlerServiceAdapter;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.targetedselling.DynamicContentDelivery;

/**
 * Creates a dynamic content assignment wizard. If the wizard is completed successfully, the dynamic content assignment is saved.
 */
public class CreateDynamicContentDeliveryHandler extends 
	AbstractCreateHandler<DynamicContentDeliveryModelAdapter, DynamicContentDelivery> {
	
	private final PolicyActionContainer handlerContainer = addPolicyActionContainer("createDcdHandler"); //$NON-NLS-1$

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
		return TargetedSellingMessages.get().DynamicContentDeliveryNameExists;
	}

	@Override
	protected CreateHandlerService<DynamicContentDelivery> getService() {
		return new DynamicContentDeliveryCreateHandlerServiceAdapter(
				ServiceLocator.getService(ContextIdNames.DYNAMIC_CONTENT_DELIVERY_SERVICE));
	}

	@Override
	protected AbstractEPCampaignWizard<DynamicContentDeliveryModelAdapter> createWizardInstace() {

		DynamicContentDeliveryModelAdapter model = 
			new DynamicContentDeliveryModelAdapter(
					ServiceLocator.getService(
							ContextIdNames.DYNAMIC_CONTENT_DELIVERY));
		return new DynamicContentDeliveryWizard(model);
	}
	
	@Override
	public int getDefaultWidth() {
		return DynamicContentDeliveryWizard.DEFAULT_WIDTH;
	}
	
	@Override
	public int getDefaultHeight() {
		return DynamicContentDeliveryWizard.DEFAULT_HEIGHT;
	}	

	@Override
	protected void save(final DynamicContentDelivery model) {
		saveSellingContextManually(getWizard().getModel());
		super.save(model);
	
		final EventType eventType = EventType.CREATE;

		UIEvent<DynamicContentDeliverySearchTabModel> eventForList = new UIEvent<>(
				new DynamicContentDeliverySearchTabModelImpl(),
				eventType,
				false
		);

		UIEvent<DynamicContentDelivery> event = new UIEvent<>(
				model,
				eventType,
				false
		);

		StorePlugin.getDefault().getDynamicContentDeliveryListController().onEvent(eventForList);
		StorePlugin.getDefault().getDynamicContentDeliveryController().onEvent(event);
	}
	
	
	/**
	 * must save selling context manually because currently selling context is only
	 * editable through delivery wizard only.
	 *
	 * @param dcaWrapper the delivery wrapper containing selling context to persist
	 */
	private void saveSellingContextManually(final DynamicContentDeliveryModelAdapter dcaWrapper) {
		SellingContextHelper.saveSellingContextManually(dcaWrapper);
	}

	@Override
	protected DynamicContentDelivery getDomainObjectFromModel(final DynamicContentDeliveryModelAdapter model) {
		return model.getDynamicContentDelivery();
	}

	@Override
	public String getTargetIdentifier() {
		return "createDcdHandler"; //$NON-NLS-1$
	}
	
}