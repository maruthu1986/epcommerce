/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.store.targetedselling.dynamiccontent.wizard;

import org.eclipse.osgi.util.NLS;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.ObjectGuidReceiver;
import com.elasticpath.cmclient.core.event.EventType;
import com.elasticpath.cmclient.core.helpers.ChangeSetHelper;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.store.AbstractEPCampaignWizard;
import com.elasticpath.cmclient.store.targetedselling.TargetedSellingImageRegistry;
import com.elasticpath.cmclient.store.targetedselling.TargetedSellingMessages;
import com.elasticpath.cmclient.store.targetedselling.dynamiccontent.actions.DynamicContentChangeEventUtil;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.contentspace.DynamicContent;
import com.elasticpath.service.changeset.ChangeSetMemberAction;
import com.elasticpath.service.contentspace.DynamicContentService;

/**
 * The wizard for creating catalog promotions.
 */
public class NewDynamicContentWizard extends AbstractEPCampaignWizard<DynamicContent> implements ObjectGuidReceiver {

	private static final String WRAPPER_PAGE_NAME = "NewDynamicContentWizardWrapperPage"; //$NON-NLS-1$

	private static final String PARAMETER_PAGE_NAME = "NewDynamicContentWizardParametersPage"; //$NON-NLS-1$

	private final ChangeSetHelper changeSetHelper = ServiceLocator.getService(ChangeSetHelper.BEAN_ID);
	private DynamicContent model;
	private final DynamicContentService dynamicContentService = ServiceLocator.getService(ContextIdNames.DYNAMIC_CONTENT_SERVICE);

	private boolean editMode;

	/**
	 * Default constructor.
	 */
	public NewDynamicContentWizard() {
		super(TargetedSellingMessages.get().NewDynamicContentCreateWizard_Title,
				null,
				TargetedSellingImageRegistry.getImage(TargetedSellingImageRegistry.IMAGE_DYNAMIC_CONTENT_CREATE_ACTION));
		model = ServiceLocator.getService(ContextIdNames.DYNAMIC_CONTENT);
		setNeedsProgressMonitor(true);
	}

	/**
	 * Constructor with parameters.
	 *
	 * @param model - DynamicContent model
	 * @param editMode - if true - wizard will be called in <code>DynamicContent</code> edit mode, otherwise in create mode.
	 */
	public NewDynamicContentWizard(final DynamicContent model, final boolean editMode) {
		super(TargetedSellingMessages.get().NewDynamicContentCreateWizard_Title, null,
				TargetedSellingImageRegistry.getImage(TargetedSellingImageRegistry.IMAGE_DYNAMIC_CONTENT_TAB));
		this.model = model;
		this.editMode = editMode;
		setNeedsProgressMonitor(true);
		if (editMode) {
			setWindowTitle(TargetedSellingMessages.get().NewDynamicContentEditWizard_Title);
			setWizardImage(TargetedSellingImageRegistry.getImage(TargetedSellingImageRegistry.IMAGE_DYNAMIC_CONTENT_EDIT_ACTION));
		}
	}

	@Override
	public DynamicContent getModel() {
		return model;
	}

	@Override
	public String getNameFromModel() {

		return model.getName();
	}

	@Override
	public void addPages() {
		PolicyActionContainer policyActionContainer = addPolicyActionContainer("dynamicContentWizard"); //$NON-NLS-1$

		String wrapperPageTitle = TargetedSellingMessages.get().DynamicContentWizardWrapperPage_Title;
		String parameterPageTitle = TargetedSellingMessages.get().DynamicContentWizardParametersPage_Title;
		if (editMode) {
			wrapperPageTitle = TargetedSellingMessages.get().DynamicContentWizardWrapperPageEdit_Title;
			parameterPageTitle = TargetedSellingMessages.get().DynamicContentWizardParametersPageEdit_Title;
		}
		addPage(new NewDynamicContentWizardWrapperPage(WRAPPER_PAGE_NAME,
				wrapperPageTitle, editMode), policyActionContainer);
		addPage(new NewDynamicContentWizardParametersPage(PARAMETER_PAGE_NAME,
				parameterPageTitle), policyActionContainer);

		// add the wizard as a target to the policy action container so that the state
		// of the finish button is determined by the container itself
		policyActionContainer.addTarget(this);

	}

	@Override
	public String getTargetIdentifier() {
		if (!getModel().isPersisted()) {
			return "newDynamicContentWizard"; //$NON-NLS-1$
		}
		return "dynamicContentWizard"; //$NON-NLS-1$
	}

	@Override
	protected Object getDependentObject() {
		return getModel();
	}

	@Override
	public void setObjectGuid(final String objectGuid) {
		if (objectGuid != null) {
			model = dynamicContentService.findByGuid(objectGuid);
			if (model == null) {
				throw new IllegalArgumentException(

						NLS.bind(CoreMessages.get().Given_Object_Not_Exist,
						new String[]{"Dynamic Content", objectGuid})); //$NON-NLS-1$
			}
			editMode = true;
		}
	}

	@Override
	public boolean performFinish() {
		if (super.performFinish()) {
			DynamicContent dynamicContent = dynamicContentService.saveOrUpdate(getModel());

			if (getModel().isPersisted()) {
				changeSetHelper.addObjectToChangeSet(dynamicContent, ChangeSetMemberAction.EDIT);
			} else {
				changeSetHelper.addObjectToChangeSet(dynamicContent, ChangeSetMemberAction.ADD);
			}

			DynamicContentChangeEventUtil eventUtil = new DynamicContentChangeEventUtil();

			if (getModel().isPersisted()) {
				eventUtil.fireEvent(EventType.UPDATE, dynamicContent);
			} else {
				eventUtil.fireEvent(EventType.CREATE, dynamicContent);
			}
		}

		return super.performFinish();
	}
}
