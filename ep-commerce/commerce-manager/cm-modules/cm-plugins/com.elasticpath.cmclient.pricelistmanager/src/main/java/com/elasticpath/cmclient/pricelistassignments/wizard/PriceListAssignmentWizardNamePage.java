/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.pricelistassignments.wizard;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.dialogs.PageChangingEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Text;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.EpWizardPageSupport;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareWizardPage;
import com.elasticpath.cmclient.policy.ui.IPolicyTargetLayoutComposite;
import com.elasticpath.cmclient.pricelistassignments.wizard.model.PriceListAssignmentModelAdapter;
import com.elasticpath.cmclient.pricelistmanager.PriceListManagerMessages;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.exception.DuplicateNameException;
import com.elasticpath.domain.pricing.PriceListAssignment;
import com.elasticpath.service.pricing.PriceListAssignmentService;

/**
 * Class represents first page of { {@link PriceListAssignmentWizard} wizard.
 * 
 */
public class PriceListAssignmentWizardNamePage extends AbstractPolicyAwareWizardPage<PriceListAssignmentModelAdapter> {

	private static final int NUM_OF_COLUMNS_ON_THE_PAGE = 2;
	private static final int DESCRIPTION_MAX_LENGTH = 255;
	private static final int DESCRIPTION_TEXT_AREA_HEIGHT = 80;
	private static final int DESCRIPTION_TEXT_AREA_WIDTH = 100;
	private static final int NAME_TEXT_WIDTH = 100;
	private static final boolean HAS_HORIZONTAL_SCROLL = false;
	private static final boolean HAS_VERTICAL_SCROLL = false;
	private static final String NAME = "name";
	private static final String DESCRIPTION = "description";

	/**
	 * Max value for priority.
	 */
	public static final int PRIORITY_MIN_VALUE = 1;

	/**
	 * Min value for priority.
	 */	
	public static final int PRIORITY_MAX_VALUE = 10;

	/**
	 * Default value for priority.
	 */
	public static final int PRIORITY_DEFAULT_VALUE = 5;

	private Text nameText;
	private Text descriptionText;
	private Scale priorityScale;

	/**
	 * Constructor.
	 *
	 * @param pageName    name of the page
	 * @param title       title of the page
	 * @param description description of the page
	 */
	public PriceListAssignmentWizardNamePage(final String pageName, final String title, final String description) {
		super(1, false, pageName, title, description, new DataBindingContext());
	}

	@Override
	public boolean beforeNext(final PageChangingEvent event) {
		return processPriceListValidation();
	}

	@Override
	protected void createPageContents(final IPolicyTargetLayoutComposite parent) {
		PolicyActionContainer otherControlsContainer = addPolicyActionContainer("priceListAssignmentWizardNamePageOtherControls"); //$NON-NLS-1$
		PolicyActionContainer nameControlsContainer = addPolicyActionContainer("priceListAssignmentWizardNamePageNameControls"); //$NON-NLS-1$

		IPolicyTargetLayoutComposite composite = parent.addGridLayoutComposite(NUM_OF_COLUMNS_ON_THE_PAGE, false,
				parent.createLayoutData(IEpLayoutData.CENTER, IEpLayoutData.BEGINNING, true, true),
				otherControlsContainer);

		final IEpLayoutData fieldDataFillBeginning = composite.createLayoutData(
				IEpLayoutData.FILL, IEpLayoutData.BEGINNING, false, false);
		
		final IEpLayoutData fieldDataEndBeginning = composite.createLayoutData(
				IEpLayoutData.END, IEpLayoutData.BEGINNING, false, false);
		
		final IEpLayoutData fieldDataEndCenter = composite.createLayoutData(
				IEpLayoutData.END, IEpLayoutData.CENTER, false, false);
		
		// Add Name text edit control
		composite.addLabelBoldRequired(PriceListManagerMessages.get().Name_Label, fieldDataEndBeginning, nameControlsContainer);
		nameText = composite.addTextField(fieldDataFillBeginning, nameControlsContainer);

		final GridData twdNameText = new GridData();
		
		twdNameText.horizontalAlignment = SWT.FILL;
		twdNameText.widthHint = NAME_TEXT_WIDTH;
		nameText.setLayoutData(twdNameText);
		
		// Add Description text edit control
		composite.addLabelBold(PriceListManagerMessages.get().Description_Label, fieldDataEndBeginning, otherControlsContainer);
		descriptionText = composite.addTextArea(HAS_VERTICAL_SCROLL, HAS_HORIZONTAL_SCROLL, fieldDataFillBeginning, otherControlsContainer);
		descriptionText.setTextLimit(DESCRIPTION_MAX_LENGTH);

		//Priority component
		composite.addLabelBold(PriceListManagerMessages.get().Priority_Label, fieldDataEndCenter, otherControlsContainer);
		
		createScaleGrid(composite, otherControlsContainer);
		
		final GridData twdDescriptionText = new GridData();
		
		twdDescriptionText.horizontalAlignment = SWT.FILL;
		twdDescriptionText.heightHint = DESCRIPTION_TEXT_AREA_HEIGHT;
		twdDescriptionText.widthHint = DESCRIPTION_TEXT_AREA_WIDTH;
		descriptionText.setLayoutData(twdDescriptionText);

		/* MUST be called */
		setControl(parent.getSwtComposite());

		nameText.addModifyListener((ModifyListener) modifyEvent -> PriceListAssignmentWizardNamePage.this.setErrorMessage(null));
	}

	/**
	 * Create grid with priority scale and labels. 
	 * @param parent
	 */
	private void createScaleGrid(final IPolicyTargetLayoutComposite parent, final PolicyActionContainer policyContainer) {
		final IEpLayoutData fieldDataFillBegining = parent.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.BEGINNING, true, false);

		final IEpLayoutData fieldData = parent.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.BEGINNING, true, false);

		final IEpLayoutData fieldDataCenter = parent.createLayoutData(IEpLayoutData.CENTER, IEpLayoutData.BEGINNING, true, false);

		final IEpLayoutData fieldDataFillEnd = parent.createLayoutData(IEpLayoutData.END, IEpLayoutData.BEGINNING, true, false);

		final IPolicyTargetLayoutComposite priorityComposite = parent.addGridLayoutComposite(1, false, fieldData, policyContainer);
		
		// 1 row begin		
		final IPolicyTargetLayoutComposite scaleLabelsComposite = priorityComposite
				.addGridLayoutComposite(PRIORITY_MAX_VALUE, true, fieldData, policyContainer);
		
		for (int i = PRIORITY_MAX_VALUE; i >= PRIORITY_MIN_VALUE; i--) {
			scaleLabelsComposite.addLabel(String.valueOf(i), fieldDataCenter, policyContainer);
		}

		priorityScale = priorityComposite.addScale(PRIORITY_MIN_VALUE, PRIORITY_MAX_VALUE, 1, 1, SWT.HORIZONTAL, fieldData, policyContainer);

		final IPolicyTargetLayoutComposite priorityLabelComposite = priorityComposite.addGridLayoutComposite(2, true, fieldData, policyContainer);
		priorityLabelComposite.addLabel(PriceListManagerMessages.get().Priority_Lowest_Label, fieldDataFillBegining, policyContainer);
		priorityLabelComposite.addLabel(PriceListManagerMessages.get().Priority_Highest_Label, fieldDataFillEnd, policyContainer);

		// So here we create listener for priority
		// We can use standart data binding by next reasons:
		// 1. Scale can not be created with reversed minimum and maximum values
		// 2. I.e. max value on Scale eq min value of priority and vise versa
		
		priorityScale.addSelectionListener(new SelectionListener() {
				public void widgetDefaultSelected(final SelectionEvent arg0) {
					getModel().setPriority(calculateInvertedPriority(priorityScale.getSelection()));
				}
	
				public void widgetSelected(final SelectionEvent arg0) {
					getModel().setPriority(calculateInvertedPriority(priorityScale.getSelection()));
				}
			}
		);
		
		priorityScale.addMouseListener(new MouseAdapter() {
			public void mouseUp(final MouseEvent arg0) {
					priorityScale.setSelection(priorityScale.getSelection());
			}
		});
	}
	
	private int calculateInvertedPriority(final int priority) {
		return PRIORITY_MAX_VALUE + 1 - priority;
	}
	

	@Override
	protected void populateControls() {
		if (StringUtils.isEmpty(getModel().getName())) {
			//hence it is create mode
			getModel().setPriority(PRIORITY_DEFAULT_VALUE);
			priorityScale.setSelection(calculateInvertedPriority(PRIORITY_DEFAULT_VALUE));
		} else {
			String name = getModel().getName();
			nameText.setText(name);
			nameText.setEnabled(false);
			int priority = getModel().getPriority();
			int scaleValue = calculateInvertedPriority(priority);
			priorityScale.setSelection(scaleValue);
			String description = getModel().getDescription();
			if (StringUtils.isNotEmpty(description)) {
				descriptionText.setText(description);
			}
		}
	}

	@Override
	protected void bindControls() {
		final EpControlBindingProvider bindingProvider = EpControlBindingProvider
				.getInstance();


		bindingProvider.bind(getDataBindingContext(),
				nameText, getModel().getPriceListAssignment(), NAME,
				EpValidatorFactory.STRING_255_REQUIRED, null, true);


		bindingProvider.bind(getDataBindingContext(),
				descriptionText, getModel().getPriceListAssignment(), DESCRIPTION,
				EpValidatorFactory.MAX_LENGTH_2000, null, true);
		

		// Create the new content wizard details page
		EpWizardPageSupport.create(PriceListAssignmentWizardNamePage.this,
				getDataBindingContext());

	}

	/**
	 * Checks if price list assignment with given name already exists.
	 * This check will return true, if given price list assignment name exists in database 
	 * with different GUIDs.
	 * 
	 * @param plaName -
	 *            name of price list assignment to be checked
	 * @return true - if another object with given name exists, false otherwise
	 */
	private boolean plaNameExists(final String plaName) {
		final PriceListAssignmentService plaService = 
			ServiceLocator.getService(ContextIdNames.PRICE_LIST_ASSIGNMENT_SERVICE);
		try {
			final PriceListAssignment priceListAssignment = plaService.findByName(plaName.trim());

			if (priceListAssignment != null
				&& !priceListAssignment.getGuid().equals(getModel().getGuid())) {
				return true;
			}
		} catch (DuplicateNameException e) {
			return true;
		}
		return false;
	}

	/**
	 * Validate page.
	 * @return true if validation passed
	 */
	private boolean processPriceListValidation() {
		String nameToValidate = getModel().getName();
		if (StringUtils.isEmpty(nameToValidate)) {
			return false;
		}
		if (plaNameExists(nameToValidate)) {
			setErrorMessage(PriceListManagerMessages.get().NameExists);
			return false;
		}
		return true;
	}
	
	
}