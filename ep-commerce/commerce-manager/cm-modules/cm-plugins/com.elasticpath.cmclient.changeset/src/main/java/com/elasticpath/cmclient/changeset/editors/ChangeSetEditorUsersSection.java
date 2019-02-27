/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.changeset.editors;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;

import com.elasticpath.cmclient.changeset.ChangeSetMessages;
import com.elasticpath.cmclient.changeset.helpers.UserViewFormatter;
import com.elasticpath.cmclient.changeset.views.UserAssignmentDualListBox;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareEditorPageSectionPart;
import com.elasticpath.cmclient.policy.ui.IPolicyTargetLayoutComposite;
import com.elasticpath.cmclient.policy.ui.PolicyTargetCompositeFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.changeset.ChangeSet;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.service.cmuser.CmUserService;

/**
 * The change set section for the users.
 */
public class ChangeSetEditorUsersSection extends AbstractPolicyAwareEditorPageSectionPart {

	private final CmUserService cmUserService = ServiceLocator.getService(ContextIdNames.CMUSER_SERVICE);

	/**
	 * Constructs a new section.
	 * 
	 * @param formPage the form page
	 * @param editor the editor
	 */
	public ChangeSetEditorUsersSection(final FormPage formPage, final AbstractCmClientFormEditor editor) {
		super(formPage, editor, ExpandableComposite.NO_TITLE);
	}

	@Override
	protected void bindControls(final DataBindingContext bindingContext) {
		// nothing to bind
	}

	@Override
	protected void createControls(final Composite client, final FormToolkit toolkit) {

		IPolicyTargetLayoutComposite mainComposite = PolicyTargetCompositeFactory
			.wrapLayoutComposite(CompositeFactory.createTableWrapLayoutComposite(client, 1, false));
		mainComposite.getSwtComposite().setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, 
																		TableWrapData.FILL_GRAB));

		PolicyActionContainer container = addPolicyActionContainer("usersSection"); //$NON-NLS-1$
		final IEpLayoutData labelData = mainComposite.createLayoutData(IEpLayoutData.FILL, 
																		IEpLayoutData.CENTER,
																		false,
																		false,
																		1,
																		1);
		
		// Add the main message
		toolkit.createLabel(mainComposite.getSwtComposite(), 
							ChangeSetMessages.get().ChangeSetEditor_SelectUsersMessage, SWT.WRAP);
			
		IPolicyTargetLayoutComposite labelComposite = mainComposite.addGridLayoutComposite(2, false, null, container);
		
		// Add the labels for the creator
		labelComposite.addLabelBold(ChangeSetMessages.get().ChangeSetsView_ChangeSetOwner, labelData, container);
		
		CmUser createdByUser = cmUserService.findByGuid(getModel().getCreatedByUserGuid());
		if (createdByUser != null) {
			labelComposite.addLabel(UserViewFormatter.formatWithName(createdByUser), labelData, container);
		}
		 		
		IEpLayoutData data = mainComposite.createLayoutData(IEpLayoutData.FILL, 
					IEpLayoutData.FILL,
					true,
					false);
		
		// Add the users permissions section here
		UserAssignmentDualListBox userAssignmentDualListBox = 
			new UserAssignmentDualListBox(
				mainComposite,
					data,
					container,
					getModel(),
					ChangeSetMessages.get().ChangeSetEditor_AvailableUsers,
					ChangeSetMessages.get().ChangeSetEditor_AssignedUsers);
		
		userAssignmentDualListBox.createControls();
		userAssignmentDualListBox.setControlModificationListener(getEditor());
		
		addCompositesToRefresh(mainComposite.getSwtComposite());
	}	
	
	@Override
	public ChangeSet getModel() {
		return (ChangeSet) super.getModel();
	}

	@Override
	protected void populateControls() {
		
		// Nothing to populate
	}
}
