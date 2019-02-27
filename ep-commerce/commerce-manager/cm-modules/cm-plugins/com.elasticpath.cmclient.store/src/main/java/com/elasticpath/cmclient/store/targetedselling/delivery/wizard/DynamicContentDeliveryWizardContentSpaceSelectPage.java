/*
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.cmclient.store.targetedselling.delivery.wizard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.eclipse.core.databinding.DataBindingContext;

import com.elasticpath.cmclient.core.binding.EpWizardPageSupport;
import com.elasticpath.cmclient.core.ui.framework.IDualListChangeListener;
import com.elasticpath.cmclient.core.wizard.AbstractEpWizard;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareWizardPage;
import com.elasticpath.cmclient.policy.ui.IPolicyTargetLayoutComposite;
import com.elasticpath.cmclient.store.targetedselling.TargetedSellingMessages;
import com.elasticpath.cmclient.store.targetedselling.delivery.wizard.duallistboxes.ContentSpaceSelectionDualListBox;
import com.elasticpath.cmclient.store.targetedselling.delivery.wizard.model.DynamicContentDeliveryModelAdapter;
import com.elasticpath.domain.contentspace.ContentSpace;

/**
 * Class represents page for ContentSpaces selector on the Create Dynamic Assignment wizard.
 */
public class DynamicContentDeliveryWizardContentSpaceSelectPage 
			extends AbstractPolicyAwareWizardPage<DynamicContentDeliveryModelAdapter> implements IDualListChangeListener {

	private ContentSpaceSelectionDualListBox listBox;

	/**
	 * Constructor.
	 * 
	 * @param pageName - name of the page.
	 * @param title - title of the page.
	 * @param description - description of the page.
	 */
	public DynamicContentDeliveryWizardContentSpaceSelectPage(final String pageName, final String title, final String description) {
		super(2, false, pageName, title, description, new DataBindingContext());
	}

	@Override
	protected void bindControls() {
		EpWizardPageSupport.create(this, getDataBindingContext());
	}

	@Override
	protected void populateControls() {
		// Auto-generated method stub
	}

	private void saveContentspaces() {
		Collection<ContentSpace> assigned = listBox.getAssigned();
		if (CollectionUtils.isEmpty(assigned)) {
			return;
		}
		Set<ContentSpace> assignmentsSet = new HashSet<>();
		for (ContentSpace assignment : assigned) {
			assignmentsSet.add(assignment);
		}
		getModel().setContentspaces(assignmentsSet);
	}

	@Override
	public boolean isPageComplete() {
		return listBox.validate();
	}

	@Override
	public void listChanged() {
		saveContentspaces();
		((AbstractEpWizard<?>) getWizard()).getWizardDialog().updateButtons();
	}
	
	@Override
	protected void createPageContents(final IPolicyTargetLayoutComposite parent) {
		PolicyActionContainer contentSpacesContainer = addPolicyActionContainer("csContainer"); //$NON-NLS-1$
		List<ContentSpace> contentspacesList = new ArrayList<>();
		if (getModel().getContentspaces() != null) {
			contentspacesList.addAll(getModel().getContentspaces());
		}
		listBox = new ContentSpaceSelectionDualListBox(parent, contentSpacesContainer, contentspacesList, 
				TargetedSellingMessages.get().DCDeliveryWizard_AvailableAT_Label,
				TargetedSellingMessages.get().DCDeliveryWizard_SelectedAT_Label, getWizard());
		listBox.registerChangeListener(this);
		/* MUST be called */
		setControl(parent.getSwtComposite());			
	}

}
		