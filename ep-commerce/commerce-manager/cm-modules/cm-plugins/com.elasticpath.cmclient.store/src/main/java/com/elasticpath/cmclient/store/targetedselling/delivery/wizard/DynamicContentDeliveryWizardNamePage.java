/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.store.targetedselling.delivery.wizard;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.dialogs.PageChangingEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
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
import com.elasticpath.cmclient.store.targetedselling.TargetedSellingMessages;
import com.elasticpath.cmclient.store.targetedselling.delivery.wizard.model.DynamicContentDeliveryModelAdapter;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.exception.DuplicateNameException;
import com.elasticpath.domain.targetedselling.DynamicContentDelivery;
import com.elasticpath.service.targetedselling.DynamicContentDeliveryService;

/**
 * Class represents first page of Dynamic Content Assignment wizard.
 * 
 */
public class DynamicContentDeliveryWizardNamePage extends
	AbstractPolicyAwareWizardPage<DynamicContentDeliveryModelAdapter> {

	private static final int NUM_OF_COLUMNS_ON_THE_PAGE = 3;
	private static final int DESCRIPTION_MAX_LENGTH = 255;
	private static final int DESCRIPTION_TEXT_AREA_HEIGHT = 80;
	private static final int DESCRIPTION_TEXT_AREA_WIDTH = 100;
	private static final int NAME_TEXT_WIDTH = 100;
	private static final boolean HAS_HORISONTAL_SCROLL = false;
	private static final boolean HAS_VERTICAL_SCROLL = false;
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

	private Text dynamicContentAssignmentNameText;
	private Text dynamicContentAssignmentDescriptionText;
	private Scale dynamicContentAssignmentPriorityScale;
	private static final String NAME = "name"; //$NON-NLS-1$
	private static final String DESCRIPTION = "description"; //$NON-NLS-1$

	/**
	 * Constructor.
	 * 
	 * @param pageName -
	 *            name of the page
	 * @param title -
	 *            title of the page
	 * @param description -
	 * 				description of the page           
	 */
	public DynamicContentDeliveryWizardNamePage(final String pageName,
			final String title, final String description) {
		super(
				2,
				false,
				pageName,
				title,
				description,
				new DataBindingContext());
	}

	@Override
	public boolean beforeNext(final PageChangingEvent event) {
		return processDynamicContentValidation();
	}

	/**
	 * Create grid with priority scale and labels. 
	 */
	private void createScaleGrid(final IPolicyTargetLayoutComposite parent, final PolicyActionContainer policyContainer) {
		
		final IEpLayoutData fieldDataFillBegining = parent.createLayoutData(
				IEpLayoutData.BEGINNING, IEpLayoutData.BEGINNING, true, false);
		
		final IEpLayoutData fieldDataFill = parent.createLayoutData(
				IEpLayoutData.FILL, IEpLayoutData.BEGINNING, true, false);
		
		final IEpLayoutData fieldDataCenter = parent.createLayoutData(
				IEpLayoutData.CENTER, IEpLayoutData.BEGINNING, true, false);
		
		final IEpLayoutData fieldDataFillEnd = parent.createLayoutData(
				IEpLayoutData.END, IEpLayoutData.BEGINNING, true, false);
		
		final IPolicyTargetLayoutComposite priorityComposite = parent.addGridLayoutComposite(
				1, 
				false, 
				fieldDataFill, policyContainer);
		
		// 1 row begin		
		final IPolicyTargetLayoutComposite scaleLabelsComposite = priorityComposite.addGridLayoutComposite(
				PRIORITY_MAX_VALUE, 
				true, 
				fieldDataFill, policyContainer);
		
		for (int i = PRIORITY_MAX_VALUE; i >= PRIORITY_MIN_VALUE; i--) {
			scaleLabelsComposite.addLabel(String.valueOf(i), fieldDataCenter, policyContainer);
		}
		// 1 row end
		// 2 row  begin
		dynamicContentAssignmentPriorityScale = priorityComposite.addScale(
				PRIORITY_MIN_VALUE, 
				PRIORITY_MAX_VALUE,				
				1, 
				1,
				SWT.HORIZONTAL, 
				fieldDataFill, policyContainer);
		// 2 row  end		
		// 3 row begin
		final IPolicyTargetLayoutComposite priorityLabelsComposite = priorityComposite.addGridLayoutComposite(
				2, 
				true, 
				fieldDataFill, policyContainer);
		priorityLabelsComposite.addLabel(
				TargetedSellingMessages.get().DCDeliveryWizard_Priority_Lowest_Label,
				fieldDataFillBegining, policyContainer);
		priorityLabelsComposite.addLabel(
				TargetedSellingMessages.get().DCDeliveryWizard_Priority_Highest_Label,
				fieldDataFillEnd, policyContainer
				);
		// 3 row end
		
		
		// So here we create listener for priority
		// We can use standart data binding by next reasons:
		// 1. Scale can not be created with reversed minimum and maximum values
		// 2. I.e. max value on Scale eq min value of priority and vise versa
		
		dynamicContentAssignmentPriorityScale.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {
				getModel().setPriority(calculateInvertedPriority(dynamicContentAssignmentPriorityScale.getSelection()));
			}

			@Override
			public void widgetSelected(final SelectionEvent arg0) {
				getModel().setPriority(calculateInvertedPriority(dynamicContentAssignmentPriorityScale.getSelection()));
			}
			}
		);
		
		dynamicContentAssignmentPriorityScale.addMouseListener(new MouseListener() {
			@Override
			public void mouseUp(final MouseEvent arg0) {
				dynamicContentAssignmentPriorityScale.setSelection(dynamicContentAssignmentPriorityScale.getSelection());
			}

			@Override
			public void mouseDoubleClick(final MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseDown(final MouseEvent arg0) {
				// TODO Auto-generated method stub

			}
		});
		
	}
	
	private int calculateInvertedPriority(final int priority) {
		return PRIORITY_MAX_VALUE + 1 - priority;
	}
	

	@Override
	protected void populateControls() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void bindControls() {
		final EpControlBindingProvider bindingProvider = EpControlBindingProvider
				.getInstance();

		// Binds the dynamic content name using data binding
		bindingProvider.bind(getDataBindingContext(),
				dynamicContentAssignmentNameText, getModel().getDynamicContentDelivery(), NAME,
				EpValidatorFactory.STRING_255_REQUIRED, null, true);

		// Binds the dynamic content name using data binding
		bindingProvider.bind(getDataBindingContext(),
				dynamicContentAssignmentDescriptionText, getModel().getDynamicContentDelivery(), DESCRIPTION,
				EpValidatorFactory.MAX_LENGTH_65535, null, true);
		
		// Binds the dynamic content name using data binding
		//bindingProvider.bind(getDataBindingContext(),
		//		dynamicContentAssignmentPriorityScale, getModel().getDynamicContentDelivery(), PRIORITY);
		

		// Create the new content wizard details page
		EpWizardPageSupport.create(DynamicContentDeliveryWizardNamePage.this,
				getDataBindingContext());

	}

	/**
	 * Checks if dynamicContent with given name already exists.
	 * This check will return true, if given dynamic content name exists in database 
	 * with different GUIDs.
	 * 
	 * @param dcName -
	 *            name of dynamic content to be checked
	 * @return true - if another object with given name exists, false otherwise
	 */
	private boolean dcNameExists(final String dcName) {
		final DynamicContentDeliveryService dcaService = 
			ServiceLocator.getService(ContextIdNames.DYNAMIC_CONTENT_DELIVERY_SERVICE);
		try {
			final DynamicContentDelivery dynamicContentDelivery = dcaService.findByName(dcName);

			if (dynamicContentDelivery != null
				&& !dynamicContentDelivery.getGuid().equals(getModel().getGuid())) {
				return true;
			}
		} catch (DuplicateNameException e) {
			return true;
		}
		return false;
	}

	private boolean processDynamicContentValidation() {
		// check uniqueness of dynamic content name
		String nameToValidate = getModel().getName();
		if (StringUtils.isEmpty(nameToValidate)) {
			return false;
		}
		if (dcNameExists(nameToValidate)) {
			setErrorMessage(TargetedSellingMessages.get().DynamicContentDeliveryNameExists);
			return false;
		}
		return true;
	}

	@Override
	protected void createPageContents(final IPolicyTargetLayoutComposite parent) {
		PolicyActionContainer otherControlsContainer = addPolicyActionContainer("dcdWizardNamePageOtherControls"); //$NON-NLS-1$
		PolicyActionContainer nameControlsContainer = addPolicyActionContainer("dcdWizardNamePageNameControls"); //$NON-NLS-1$

		final IEpLayoutData fieldDataFillBeginning = parent.createLayoutData(
				IEpLayoutData.FILL, IEpLayoutData.BEGINNING, true, false);
		
		final IEpLayoutData fieldDataEndBeginning = parent.createLayoutData(
				IEpLayoutData.END, IEpLayoutData.BEGINNING, true, false);
		
		final IEpLayoutData fieldDataEndCenter = parent.createLayoutData(
				IEpLayoutData.END, IEpLayoutData.CENTER, true, true);
		
		IPolicyTargetLayoutComposite composite = parent.addGridLayoutComposite(NUM_OF_COLUMNS_ON_THE_PAGE, false, 
				fieldDataFillBeginning, otherControlsContainer);
		
		// Add Name text edit control
		composite.addLabelBoldRequired(TargetedSellingMessages.get().DCDeliveryWizard_Name_Label,
				fieldDataEndBeginning, nameControlsContainer);
		dynamicContentAssignmentNameText = composite.addTextField(
				fieldDataFillBeginning, nameControlsContainer);

		final GridData twdNameText = new GridData();
		
		twdNameText.horizontalAlignment = SWT.FILL;
		twdNameText.widthHint = NAME_TEXT_WIDTH;
		dynamicContentAssignmentNameText.setLayoutData(twdNameText);
		
		composite.addEmptyComponent(fieldDataFillBeginning, otherControlsContainer);
		// Add Description text edit control
		composite.addLabelBold(
						TargetedSellingMessages.get().DCDeliveryWizard_Description_Label,
						fieldDataEndBeginning, otherControlsContainer);
		dynamicContentAssignmentDescriptionText = composite.addTextArea(HAS_VERTICAL_SCROLL,
				HAS_HORISONTAL_SCROLL, fieldDataFillBeginning, otherControlsContainer);
		dynamicContentAssignmentDescriptionText.setTextLimit(DESCRIPTION_MAX_LENGTH);
		composite.addEmptyComponent(fieldDataFillBeginning, otherControlsContainer);
		
		composite.addLabelBold(
				TargetedSellingMessages.get().DCDeliveryWizard_Priority_Label,
				fieldDataEndCenter, otherControlsContainer);
		
		createScaleGrid(composite, otherControlsContainer);
		
		
		final GridData twdDescriptionText = new GridData();
		
		twdDescriptionText.horizontalAlignment = SWT.FILL;
		twdDescriptionText.heightHint = DESCRIPTION_TEXT_AREA_HEIGHT;
		twdDescriptionText.widthHint = DESCRIPTION_TEXT_AREA_WIDTH;
		dynamicContentAssignmentDescriptionText.setLayoutData(twdDescriptionText);
		if (StringUtils.isEmpty(getModel().getName())) {
			//hence it is create mode
			getModel().setPriority(PRIORITY_DEFAULT_VALUE);
			dynamicContentAssignmentPriorityScale.setSelection(calculateInvertedPriority(PRIORITY_DEFAULT_VALUE));
		} else {
			String name = getModel().getName();
			dynamicContentAssignmentNameText.setText(name);
			dynamicContentAssignmentNameText.setEnabled(false);
			int priority = getModel().getPriority();
			int scaleValue = calculateInvertedPriority(priority);
			dynamicContentAssignmentPriorityScale.setSelection(scaleValue);
			String description = getModel().getDescription();
			if (StringUtils.isNotEmpty(description)) {
				dynamicContentAssignmentDescriptionText.setText(description);
			}
			
		}
		/* MUST be called */
		setControl(parent.getSwtComposite());

		dynamicContentAssignmentNameText.addModifyListener((ModifyListener) modifyEvent ->
				DynamicContentDeliveryWizardNamePage.this.setErrorMessage(null));
	}
	
	
}