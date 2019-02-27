/*
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.cmclient.store.targetedselling.delivery.wizard;

import java.util.List;

import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;

import com.elasticpath.cmclient.conditionbuilder.wizard.pages.AbstractSellingContextConditionWizardPage;
import com.elasticpath.cmclient.conditionbuilder.wizard.pages.SellingContextConditionShopperWizardPage;
import com.elasticpath.cmclient.conditionbuilder.wizard.pages.SellingContextConditionStoresWizardPage;
import com.elasticpath.cmclient.conditionbuilder.wizard.pages.SellingContextConditionTimeWizardPage;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.ObjectGuidReceiver;
import com.elasticpath.cmclient.core.helpers.ChangeSetHelper;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.store.AbstractEPCampaignWizard;
import com.elasticpath.cmclient.store.targetedselling.TargetedSellingImageRegistry;
import com.elasticpath.cmclient.store.targetedselling.TargetedSellingMessages;
import com.elasticpath.cmclient.store.targetedselling.delivery.actions.SellingContextHelper;
import com.elasticpath.cmclient.store.targetedselling.delivery.wizard.model.DynamicContentDeliveryModelAdapter;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.sellingcontext.SellingContext;
import com.elasticpath.domain.targetedselling.DynamicContentDelivery;
import com.elasticpath.service.changeset.ChangeSetMemberAction;
import com.elasticpath.service.targetedselling.DynamicContentDeliveryService;
import com.elasticpath.tags.domain.ConditionalExpression;
import com.elasticpath.tags.domain.TagDictionary;
import com.elasticpath.tags.service.InvalidConditionTreeException;
import com.elasticpath.tags.service.TagConditionService;

/**
 * Wizard for create or edit of {@link DynamicContentDelivery} object with all properties including selling context and
 * conditions.
 */
public class DynamicContentDeliveryWizard extends AbstractEPCampaignWizard<DynamicContentDeliveryModelAdapter> implements ObjectGuidReceiver {

	/**
	 * Default width.
	 */
	public static final int DEFAULT_WIDTH = 865;

	/**
	 * Default height.
	 */
	public static final int DEFAULT_HEIGHT = 340;

	private static final String NAME_PAGE = "NAME_PAGE"; //$NON-NLS-1$

	private static final String DC_SELECT_PAGE = "DC_SELECT_PAGE"; //$NON-NLS-1$

	private static final String AT_SELECT_PAGE = "AT_SELECT_PAGE"; //$NON-NLS-1$

	private static final String STORES_SELECT_PAGE = "STORES_SELECT_PAGE"; //$NON-NLS-1$

	private static final String DATES_RANGE_SELECT_PAGE = "DATES_RANGE_SELECT_PAGE"; //$NON-NLS-1$

	private static final String STORES_CONDITION_PAGE = "STORES_CONDITION_PAGE"; //$NON-NLS-1$

	private final ChangeSetHelper changeSetHelper = ServiceLocator.getService(ChangeSetHelper.BEAN_ID);

	private DynamicContentDeliveryModelAdapter model;

	private boolean editMode;

	private AbstractSellingContextConditionWizardPage<SellingContext> shopperPage;
	private AbstractSellingContextConditionWizardPage<SellingContext> storesPage;
	private AbstractSellingContextConditionWizardPage<SellingContext> timePage;

	/**
	 * Constructor.
	 */
	public DynamicContentDeliveryWizard() {
		super(TargetedSellingMessages.get().DCDeliveryWizard_Title, null, TargetedSellingImageRegistry
				.getImage(TargetedSellingImageRegistry.IMAGE_DYNAMIC_CONTENT_DELIVERY_CREATE_ACTION));
	}

	/**
	 * Constructor.
	 *
	 * @param model - dynamic content assignment model
	 */
	public DynamicContentDeliveryWizard(final DynamicContentDeliveryModelAdapter model) {
		super(TargetedSellingMessages.get().DCDeliveryWizard_Title, null, TargetedSellingImageRegistry
				.getImage(TargetedSellingImageRegistry.IMAGE_DYNAMIC_CONTENT_DELIVERY_CREATE_ACTION));

		this.model = model;
		this.editMode = model.getDynamicContentDelivery().isPersisted();

		setNeedsProgressMonitor(true);
		if (editMode) {
			setWindowTitle(TargetedSellingMessages.get().DCDeliveryWizard_EditTitle);
			setWizardImage(TargetedSellingImageRegistry
				.getImage(TargetedSellingImageRegistry.IMAGE_DYNAMIC_CONTENT_DELIVERY_EDIT_ACTION));
		}
	}

	@Override
	public DynamicContentDeliveryModelAdapter getModel() {
		return this.model;
	}

	@Override
	public String getNameFromModel() {
		return model.getName();
	}

	@Override
	public void addPages() {

		TagConditionService tagConditionService = ServiceLocator.getService(ContextIdNames.TAG_CONDITION_SERVICE);

		List<ConditionalExpression> shopperNameConditions = tagConditionService.getNamedConditions(TagDictionary.DICTIONARY_SHOPPER_GUID);
		List<ConditionalExpression> timeNameConditions = tagConditionService.getNamedConditions(TagDictionary.DICTIONARY_TIME_GUID);
		List<ConditionalExpression> storesNameConditions = tagConditionService.getNamedConditions(TagDictionary.DICTIONARY_STORES_GUID);

		String namePageTitle = TargetedSellingMessages.get().DCDeliveryWizard_NamePage_Title;
		String dynamicContentPageTitle = TargetedSellingMessages.get().DCDeliveryWizard_DC_Page_Title;
		String contentSpacePageTitle = TargetedSellingMessages.get().DCDeliveryWizard_AT_Page_Title;
		String storesPageTitle = TargetedSellingMessages.get().DCDeliveryWizard_Stores_Page_Title;
		String timePageTitle = TargetedSellingMessages.get().DCDeliveryWizard_Time_Range_Page_Title;
		String shopperPageTitle = TargetedSellingMessages.get().DCDeliveryWizard_Shopper_Page_Title;

		if (editMode) {
			namePageTitle = TargetedSellingMessages.get().DCDeliveryWizard_EditNamePage_Title;
			dynamicContentPageTitle = TargetedSellingMessages.get().DCDeliveryWizard_EditDC_Page_Title;
			contentSpacePageTitle = TargetedSellingMessages.get().DCDeliveryWizard_EditAT_Page_Title;
			storesPageTitle = TargetedSellingMessages.get().DCDeliveryWizard_EditStore_Page_Title;
			timePageTitle = TargetedSellingMessages.get().DCDeliveryWizard_EditDates_Range_Page_Title;
			shopperPageTitle = TargetedSellingMessages.get().DCDeliveryWizard_Edit_Shopper_Page_Title;
		}

		PolicyActionContainer policyActionContainer = addPolicyActionContainer(getTargetIdentifier());

		addPage(new DynamicContentDeliveryWizardNamePage(NAME_PAGE, namePageTitle,
						TargetedSellingMessages.get().DCDeliveryWizard_NamePage_Description),
				policyActionContainer);
		addPage(new DynamicContentDeliveryWizardDynamicContentSelectPage(
				DC_SELECT_PAGE,
				dynamicContentPageTitle,
				TargetedSellingMessages.get().DCDeliveryWizard_DC_Page_Description),
				policyActionContainer);
		addPage(new DynamicContentDeliveryWizardContentSpaceSelectPage(
				AT_SELECT_PAGE,
				contentSpacePageTitle,
				TargetedSellingMessages.get().DCDeliveryWizard_AT_Page_Description),
				policyActionContainer);

		shopperPage = new SellingContextConditionShopperWizardPage<>(
				STORES_CONDITION_PAGE,
				shopperPageTitle,
				TargetedSellingMessages.get().DCDeliveryWizard_Shopper_Page_Description,
				shopperNameConditions, getModel().getSellingContext(), TagDictionary.DICTIONARY_SHOPPER_GUID);
		this.addPage(shopperPage, policyActionContainer);

		timePage = new SellingContextConditionTimeWizardPage<>(DATES_RANGE_SELECT_PAGE,
				timePageTitle,
				TargetedSellingMessages.get().DCDeliveryWizard_Dates_Range_Page_Description,
				timeNameConditions, getModel().getSellingContext());
		this.addPage(timePage, policyActionContainer);

		storesPage = new SellingContextConditionStoresWizardPage<>(STORES_SELECT_PAGE,
				storesPageTitle,
				TargetedSellingMessages.get().DCDeliveryWizard_Store_Page_Description,
				storesNameConditions, getModel().getSellingContext());
		this.addPage(storesPage, policyActionContainer);

		policyActionContainer.addTarget(this);
	}

	@Override
	public boolean performFinish() {

		final StringBuilder errorMessage = new StringBuilder();
		boolean hasInvalidConditions = false;

		ConditionalExpression shopperCE = null;
		try {
			shopperCE = this.shopperPage.getModelAdapter().getModel();
		} catch (InvalidConditionTreeException icte) {
			hasInvalidConditions = true;
			errorMessage.append(icte.getLocalizedMessage());
		}
		ConditionalExpression timeCE = null;
		try {
			timeCE = this.timePage.getModelAdapter().getModel();
		} catch (InvalidConditionTreeException icte) {
			hasInvalidConditions = true;
			errorMessage.append(icte.getLocalizedMessage());
		}
		ConditionalExpression storesCE = null;
		try {
			storesCE = this.storesPage.getModelAdapter().getModel();
		} catch (InvalidConditionTreeException icte) {
			hasInvalidConditions = true;
			errorMessage.append(icte.getLocalizedMessage());
		}

		this.getModel().getSellingContext().setCondition(TagDictionary.DICTIONARY_SHOPPER_GUID, shopperCE);
		this.getModel().getSellingContext().setCondition(TagDictionary.DICTIONARY_TIME_GUID, timeCE);
		this.getModel().getSellingContext().setCondition(TagDictionary.DICTIONARY_STORES_GUID, storesCE);

		if (hasInvalidConditions) {
			showErrorDialog(errorMessage.toString());
			return false;
		}

		if (!(isConditionalExpressionValid(shopperCE)
				&& isConditionalExpressionValid(timeCE)
				&& isConditionalExpressionValid(storesCE))) {
			showErrorDialog(TargetedSellingMessages.get().TotalLengthOfConditionsReached);
			return false;
		}

		DynamicContentDelivery dynamicContentDelivery = saveDynamicContentDelivery();
		addToChangeSet(dynamicContentDelivery);

		return true;
	}

	private void addToChangeSet(final DynamicContentDelivery dynamicContentDelivery) {
		if (!changeSetHelper.isMemberOfActiveChangeset(dynamicContentDelivery)) {
			changeSetHelper.addObjectToChangeSet(dynamicContentDelivery, ChangeSetMemberAction.ADD);
		}
	}

	private void showErrorDialog(final String message) {
		MessageBox messageBox = new MessageBox(this.getShell(), SWT.OK);
		messageBox.setMessage(message);
		messageBox.open();
	}


	private boolean isConditionalExpressionValid(final ConditionalExpression conditionalExpression) {
		if (conditionalExpression != null) {
			return isConditionLengthValid(conditionalExpression.getConditionString());
		}
		return true;
	}

	private boolean isConditionLengthValid(final String conditionString) {
			return (conditionString == null || conditionString.length() <= ConditionalExpression.CONDITION_STRING_MAX_LENGTH);
	}

	private DynamicContentDelivery saveDynamicContentDelivery() {
		DynamicContentDeliveryModelAdapter dcaWrapper = getModel();
		SellingContextHelper.saveSellingContextManually(dcaWrapper);
		DynamicContentDeliveryService service = ServiceLocator.getService(
				ContextIdNames.DYNAMIC_CONTENT_DELIVERY_SERVICE);
		return service.saveOrUpdate(dcaWrapper.getDynamicContentDelivery());
	}

	/**
	 * Returns flag that indicates if wizard is in edit mode.
	 *
	 * @return true if wizard is in edit mode.
	 */
	public final boolean isEditMode() {
		return editMode;
	}

	@Override
	public String getTargetIdentifier() {
		if (!getModel().getDynamicContentDelivery().isPersisted()) {
			return "dcdWizardNewEntity"; //$NON-NLS-1$
		}
		return "dcdWizard"; //$NON-NLS-1$
	}

	@Override
	public void setObjectGuid(final String objectGuid) {
		DynamicContentDelivery dcd;
		if (objectGuid == null) {
			dcd = ServiceLocator.getService(ContextIdNames.DYNAMIC_CONTENT_DELIVERY);
		} else {
			DynamicContentDeliveryService dcdService = ServiceLocator.getService(ContextIdNames.DYNAMIC_CONTENT_DELIVERY_SERVICE);
			dcd = dcdService.findByGuid(objectGuid);
			if (dcd == null) {
				throw new IllegalArgumentException(

						NLS.bind(CoreMessages.get().Given_Object_Not_Exist,
						new String[]{"Dynamic Content Delivery", objectGuid})); //$NON-NLS-1$
			}
		}
		this.model = new DynamicContentDeliveryModelAdapter(dcd);
		this.editMode = dcd.isPersisted();
	}

	@Override
	protected Object getDependentObject() {
		return model.getDynamicContentDelivery();
	}
	
}