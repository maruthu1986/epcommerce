/*
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.cmclient.pricelistassignments.wizard;

import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;

import com.elasticpath.cmclient.conditionbuilder.wizard.pages.AbstractSellingContextConditionWizardPage;
import com.elasticpath.cmclient.conditionbuilder.wizard.pages.SellingContextConditionShopperWizardPage;
import com.elasticpath.cmclient.conditionbuilder.wizard.pages.SellingContextConditionStoresWizardPage;
import com.elasticpath.cmclient.conditionbuilder.wizard.pages.SellingContextConditionTimeWizardPage;
import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.ObjectGuidReceiver;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.event.EventType;
import com.elasticpath.cmclient.core.helpers.ChangeSetHelper;
import com.elasticpath.cmclient.core.ui.IChangeSetEditorAware;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareWizard;
import com.elasticpath.cmclient.pricelistassignments.event.PriceListAssignmentChangeEventUtil;
import com.elasticpath.cmclient.pricelistassignments.wizard.model.PriceListAssignmentModelAdapter;
import com.elasticpath.cmclient.pricelistmanager.PriceListManagerMessages;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.pricing.PriceListAssignment;
import com.elasticpath.domain.sellingcontext.SellingContext;
import com.elasticpath.service.changeset.ChangeSetMemberAction;
import com.elasticpath.service.pricing.PriceListAssignmentService;
import com.elasticpath.tags.domain.ConditionalExpression;
import com.elasticpath.tags.domain.TagDictionary;
import com.elasticpath.tags.service.InvalidConditionTreeException;

/**
 * Wizard for create or edit of {@link PriceListAssignment} object with all properties including selling context and
 * conditions.
 */
public class PriceListAssignmentWizard extends
		AbstractPolicyAwareWizard<PriceListAssignmentModelAdapter> implements
		ObjectGuidReceiver, IChangeSetEditorAware {

	/**
	 * Default width.
	 */
	public static final int DEFAULT_WIDTH = 865;

	/**
	 * Default height.
	 */
	public static final int DEFAULT_HEIGHT = 400;

	private static final int CONDITION_STRING_MAX_LENGTH = 4000;

	private static final String NAME_PRIORITY_PAGE = "NAME_PRIORITY_PAGE"; //$NON-NLS-1$

	private static final String PRICE_LIST_SELECT_PAGE = "PRICE_LIST_SELECT_PAGE"; //$NON-NLS-1$

	private static final String CATALOG_SELECT_PAGE = "CATALOG_SELECT_PAGE"; //$NON-NLS-1$

	private static final String STORES_SELECT_PAGE = "STORES_SELECT_PAGE"; //$NON-NLS-1$

	private static final String DATES_RANGE_SELECT_PAGE = "DATES_RANGE_SELECT_PAGE"; //$NON-NLS-1$

	private static final String SHOPPER_CONDITION_PAGE = "SHOPPER_CONDITION_PAGE"; //$NON-NLS-1$

	private final ChangeSetHelper changeSetHelper = ServiceLocator.getService(ChangeSetHelper.BEAN_ID);

	private PriceListAssignmentModelAdapter model;

	private boolean editMode;

	private AbstractSellingContextConditionWizardPage<SellingContext> shopperPage;
	private AbstractSellingContextConditionWizardPage<SellingContext> storesPage;
	private AbstractSellingContextConditionWizardPage<SellingContext> timePage;

	private boolean openedFromChangeSetEditor;

	/**
	 * Constructor.
	 */
	public PriceListAssignmentWizard() {
		super(PriceListManagerMessages.get().PLA_Wizard_Title, null, null);

		setNeedsProgressMonitor(true);
	}

	@Override
	public PriceListAssignmentModelAdapter getModel() {
		return this.model;
	}

	public String getNameFromModel() {
		return model.getName();
	}

	@Override
	public void addPages() {

		if (editMode) {
			setWindowTitle(PriceListManagerMessages.get().PLA_Wizard_Title_Edit);
		}

		PolicyActionContainer policyActionContainer = addPolicyActionContainer(getTargetIdentifier());

		String namePageTitle = PriceListManagerMessages.get().PLA_Wizard_Name_Page_Title;
		String priceListSelectionPageTitle = PriceListManagerMessages.get().PLA_Wizard_PriceListSelection_Page_Title;
		String catalogSelectionPageTitle = PriceListManagerMessages.get().PLA_Wizard_CatalogSelection_Page_Title;
		String storesPageTitle = PriceListManagerMessages.get().PLA_Wizard_Stores_Page_Title;
		String timePageTitle = PriceListManagerMessages.get().PLA_Wizard_Time_Range_Page_Title;
		String shopperPageTitle = PriceListManagerMessages.get().PLA_Wizard_Shopper_Page_Title;

		if (editMode) {
			namePageTitle = PriceListManagerMessages.get().PLA_Wizard_Name_Page_Title_Edit;
			priceListSelectionPageTitle = PriceListManagerMessages.get().PLA_Wizard_PriceListSelection_Page_Title_Edit;
			catalogSelectionPageTitle = PriceListManagerMessages.get().PLA_Wizard_CatalogSelection_Page_Title_Edit;
			storesPageTitle = PriceListManagerMessages.get().PLA_Wizard_Stores_Page_Title_Edit;
			timePageTitle = PriceListManagerMessages.get().PLA_Wizard_Time_Range_Page_Title_Edit;
			shopperPageTitle = PriceListManagerMessages.get().PLA_Wizard_Shopper_Page_Title_Edit;
		}

		addPage(new PriceListAssignmentWizardNamePage(NAME_PRIORITY_PAGE, namePageTitle,
						PriceListManagerMessages.get().Name_Priorty_Page_Description),
				policyActionContainer);
		addPage(new PriceListAssignmentWizardPriceListSelectPage(
				PRICE_LIST_SELECT_PAGE,
				priceListSelectionPageTitle,
				PriceListManagerMessages.get().PLA_Wizard_PriceListSelection_Page_Description),
				policyActionContainer);
		addPage(new PriceListAssignmentWizardCatalogSelectPage(
				CATALOG_SELECT_PAGE,
				catalogSelectionPageTitle,
				PriceListManagerMessages.get().PLA_Wizard_CatalogSelection_Page_Description),
				policyActionContainer);

		shopperPage = new SellingContextConditionShopperWizardPage<>(
				SHOPPER_CONDITION_PAGE,
				shopperPageTitle,
				PriceListManagerMessages.get().PLA_Wizard_Shopper_Page_Description,
				null, model.getSellingContext(), TagDictionary.DICTIONARY_PLA_SHOPPER_GUID);
		this.addPage(shopperPage, policyActionContainer);

		timePage = new SellingContextConditionTimeWizardPage<>(DATES_RANGE_SELECT_PAGE,
				timePageTitle,
				PriceListManagerMessages.get().PLA_Wizard_Time_Range_Page_Description,
				null, model.getSellingContext());
		this.addPage(timePage, policyActionContainer);

		storesPage = new SellingContextConditionStoresWizardPage<>(STORES_SELECT_PAGE,
				storesPageTitle,
				PriceListManagerMessages.get().PLA_Wizard_Stores_Page_Description,
				null, model.getSellingContext());
		this.addPage(storesPage, policyActionContainer);

		// add the wizard as a target to the policy action container so that the state
		// of the finish button is determined by the container itself
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
			storesCE = this.storesPage.getConditionalExpressionForStores();
		} catch (InvalidConditionTreeException icte) {
			hasInvalidConditions = true;
			errorMessage.append(icte.getLocalizedMessage());
		}

		this.getModel().getSellingContext().setCondition(TagDictionary.DICTIONARY_PLA_SHOPPER_GUID, shopperCE);
		this.getModel().getSellingContext().setCondition(TagDictionary.DICTIONARY_TIME_GUID, timeCE);
		this.getModel().getSellingContext().setCondition(TagDictionary.DICTIONARY_STORES_GUID, storesCE);

		if (hasInvalidConditions) {
			showErrorDialog(errorMessage.toString());
			return false;
		}

		if (!(isConditionalExpressionValid(shopperCE)
			&& isConditionalExpressionValid(timeCE)
			&& isConditionalExpressionValid(storesCE))) {
			showErrorDialog(PriceListManagerMessages.get().TotalLengthOfConditionsReached);
			return false;
		}
		// save the price list assignment
		savePriceListAssignment();

		if (!openedFromChangeSetEditor) {
			// If we were opened from the ChangeSetEditor then we don't want to open
			// the PriceListAssignment search results view.
			if (editMode) {
				PriceListAssignmentChangeEventUtil.fireEvent(EventType.UPDATE, getModel().getPriceListAssignment());
			} else {
				PriceListAssignmentChangeEventUtil.fireEvent(EventType.CREATE, getModel().getPriceListAssignment());
			}
		}

		return true;
	}

	/**
	 * Saves the price list assignment and fires an update event.
	 */
	private void savePriceListAssignment() {
		PriceListAssignmentModelAdapter editedDcaWrapper = getModel();
		PriceListAssignmentService service = ServiceLocator.getService(ContextIdNames.PRICE_LIST_ASSIGNMENT_SERVICE);

		PriceListAssignment priceListAssignment = service.saveOrUpdate(editedDcaWrapper.getPriceListAssignment());

		if (!changeSetHelper.isMemberOfActiveChangeset(priceListAssignment)) {
			changeSetHelper.addObjectToChangeSet(priceListAssignment, ChangeSetMemberAction.ADD);
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
			return (conditionString == null || conditionString.length() <= CONDITION_STRING_MAX_LENGTH);
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
	public void setObjectGuid(final String objectGuid) {

		PriceListAssignment priceListAssignment;
		if (objectGuid == null) {
			priceListAssignment = ServiceLocator.getService(ContextIdNames.PRICE_LIST_ASSIGNMENT);
		} else {
			PriceListAssignmentService service = ServiceLocator.getService(ContextIdNames.PRICE_LIST_ASSIGNMENT_SERVICE);
			priceListAssignment = service.findByGuid(objectGuid);
			if (priceListAssignment == null) {
				throw new IllegalArgumentException(

						NLS.bind(CoreMessages.get().Given_Object_Not_Exist,
						new String[]{"Price List Assignment", objectGuid})); //$NON-NLS-1$
			}
		}
		this.model = new PriceListAssignmentModelAdapter(priceListAssignment);
		this.editMode = priceListAssignment.isPersisted();
	}

	@Override
	public String getTargetIdentifier() {
		if (!getModel().getPriceListAssignment().isPersisted()) {
			return "priceListAssignmentWizardNewEntity"; //$NON-NLS-1$
		}
		return "priceListAssignmentWizard"; //$NON-NLS-1$
	}

	@Override
	protected Object getDependentObject() {
		return getModel().getPriceListAssignment();
	}

	@Override
	public void setOpenedFromChangeSetEditor() {
		this.openedFromChangeSetEditor  = true;
	}
	
	
}