/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.cmclient.pricelistmanager.dialogs;

import org.eclipse.swt.widgets.Composite;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.cmclient.core.event.ItemChangeEvent.EventType;
import com.elasticpath.cmclient.core.helpers.ChangeSetHelper;
import com.elasticpath.cmclient.core.service.BaseAmountEventService;
import com.elasticpath.common.dto.ChangeSetObjects;
import com.elasticpath.common.dto.category.ChangeSetObjectsImpl;
import com.elasticpath.common.dto.pricing.BaseAmountDTO;

/**
 * A dialog used to handle the opening of base amounts from a change set editor.
 */
public class UpdatableBaseAmountDialog extends BaseAmountDialog {

	private final ChangeSetHelper changeSetHelper = ServiceLocator.getService(ChangeSetHelper.BEAN_ID);

	@Override
	protected void createButtonsForButtonBar(final Composite parent) {
		createEpButtonsForButtonsBar(ButtonsBarType.SAVE, parent);
	}
	
	@Override
	protected boolean performSaveOperation(final BaseAmountDTO baseAmount) {
		ChangeSetObjects<BaseAmountDTO> dtoChangeSet = new ChangeSetObjectsImpl<>();
		dtoChangeSet.addToUpdateList(baseAmount);
		getPriceListService().modifyBaseAmountChangeSet(dtoChangeSet);
		
		//this will change the "change type" of the object in change set

		changeSetHelper.addObjectsToChangeSet(dtoChangeSet);

		ItemChangeEvent<BaseAmountDTO> baseAmountChangedEvent = new ItemChangeEvent<>(this, getBaseAmountDto(),
				EventType.CHANGE);
		BaseAmountEventService.getInstance().fireBaseAmountChangedEvent(baseAmountChangedEvent);
		
		return true;
	}
}
