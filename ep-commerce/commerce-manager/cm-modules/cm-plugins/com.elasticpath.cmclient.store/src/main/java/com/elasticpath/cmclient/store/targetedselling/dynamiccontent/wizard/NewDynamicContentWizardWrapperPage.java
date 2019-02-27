/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.store.targetedselling.dynamiccontent.wizard;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.PageChangingEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.EpWizardPageSupport;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.cmclient.core.wizard.AbstractEpWizard;
import com.elasticpath.cmclient.core.wizard.page.IBeforeFinishNotifier;
import com.elasticpath.cmclient.policy.StatePolicy;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareWizardPage;
import com.elasticpath.cmclient.policy.ui.IPolicyTargetLayoutComposite;
import com.elasticpath.cmclient.store.StorePlugin;
import com.elasticpath.cmclient.store.targetedselling.TargetedSellingMessages;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.exception.DuplicateNameException;
import com.elasticpath.domain.contentspace.ContentWrapper;
import com.elasticpath.domain.contentspace.DynamicContent;
import com.elasticpath.service.contentspace.DynamicContentService;

/**
 * The new dynamic content wizard first page with name, target and content wrapper selector.
 */
public class NewDynamicContentWizardWrapperPage extends AbstractPolicyAwareWizardPage<DynamicContent>
	implements IBeforeFinishNotifier {
	
	private static final Logger LOG = Logger.getLogger(NewDynamicContentWizardWrapperPage.class);

	private static final String DESCRIPTION = "description"; //$NON-NLS-1$

	private static final int DESCRIPTION_TEXT_AREA_HEIGHT = 80;

	private static final String NAME = "name"; //$NON-NLS-1$

	private CCombo wrappersCombo;

	private Text dynamicContentNameText;

	private Text dynamicContentDescriptionText;

	private List<ContentWrapper> wrappers;

	private ContentWrapper wrapper;

	private final boolean wizardInEditMode;
	
	private boolean parametersInitialised;
	
	private static final Comparator<ContentWrapper> WRAPPER_COMPARATOR = new Comparator<ContentWrapper>() {

		/**
		 * compare content wrappers by their name.
		 * @param cw1 the first wrapper
		 * @param cw2 the second wrapper
		 * @return integer result of comparison
		 */
		@Override
		public int compare(final ContentWrapper cw1, final ContentWrapper cw2) {
			if (cw1 == null || cw2 == null || cw1.getName() == null || cw2.getName() == null) {
				return 1;
			}
			return cw1.getName().compareToIgnoreCase(cw2.getName());
		}
		
	};

	/**
	 * Default constructor.
	 * 
	 * @param pageName the name of the page
	 * @param title the page title
	 * @param wizardInEditMode if true - wizard will be called in <code>DynamicContent</code> edit mode, otherwise in create mode.
	 */
	protected NewDynamicContentWizardWrapperPage(final String pageName, final String title, final boolean wizardInEditMode) {
		super(2, false, pageName, title, TargetedSellingMessages.get().NewDynamicContentWizard_Description, new DataBindingContext());
		this.wizardInEditMode = wizardInEditMode;
	}
	
	@Override
	protected void createPageContents(final IPolicyTargetLayoutComposite parent) {
		PolicyActionContainer controlsContainer = addPolicyActionContainer("newDynamicContentWizardWrapperPageControls"); //$NON-NLS-1$
		
		final IEpLayoutData fieldDataFill = parent.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.BEGINNING, true, false);
		
		// Add Name text edit control
		parent.addLabelBoldRequired(TargetedSellingMessages.get().NewDynamicContentWizard_Name_Label, fieldDataFill, controlsContainer);
		dynamicContentNameText = parent.addTextField(fieldDataFill, controlsContainer);

		parent.addLabelBold(TargetedSellingMessages.get().NewDynamicContentWizard_Description_Label, fieldDataFill, controlsContainer);
		dynamicContentDescriptionText = parent.addTextArea(fieldDataFill, controlsContainer);
		
		final GridData twdDescriptionText = new GridData();
		twdDescriptionText.heightHint = DESCRIPTION_TEXT_AREA_HEIGHT;
		twdDescriptionText.horizontalAlignment = SWT.FILL;
		dynamicContentDescriptionText.setLayoutData(twdDescriptionText);

		parent.addLabelBoldRequired(TargetedSellingMessages.get().NewDynamicContentWizard_Wrapper_Combo_Label, fieldDataFill, controlsContainer);
		
		wrappersCombo = parent.addComboBox(fieldDataFill, controlsContainer);

		/* MUST be called */
		setControl(parent.getSwtComposite());

		dynamicContentNameText.addModifyListener((ModifyListener) modifyEvent -> NewDynamicContentWizardWrapperPage.this.setErrorMessage(null));
	}

	@Override
	public void applyStatePolicy(final StatePolicy statePolicy) {
		super.applyStatePolicy(statePolicy);
		this.wrappersCombo.setEnabled(!wizardInEditMode);		
	}

	@Override
	protected void populateControls() {
		if (wizardInEditMode) {
			if (getModel().getName() != null) {
				dynamicContentNameText.setText(getModel().getName());
			}
			if (getModel().getDescription() != null) {
				dynamicContentDescriptionText.setText(getModel().getDescription());
			}			
		}

		DynamicContentService dynamicContentService = ServiceLocator.getService(
				ContextIdNames.DYNAMIC_CONTENT_SERVICE);

		final Map<String, ContentWrapper> contentWrapperMap = dynamicContentService.getContentWrappersMap(true);

		if (null != contentWrapperMap) {

			this.wrappersCombo.add(TargetedSellingMessages.get().NewDynamicContentWizard_WrapperCombo_InitialMessage);
			this.wrappersCombo.select(0);
			
			fillAndSortWrappersListAndMakeSelection(contentWrapperMap);

		}
	}
	
	/**
	 * fill in wrappers list in a sorted way.
	 * @param contentWrapperMap the unsorted map with all content wrappers
	 */
	private void fillAndSortWrappersListAndMakeSelection(final Map<String, ContentWrapper> contentWrapperMap) {

		Set<ContentWrapper> tempSet = new TreeSet<>(WRAPPER_COMPARATOR);
		
		for (final String currWrapperKey : contentWrapperMap.keySet()) {
			tempSet.add(contentWrapperMap.get(currWrapperKey));
		}

		this.wrappers = new ArrayList<>();
		for (ContentWrapper wrapper : tempSet) {
			addToContentWrapperListAndUpdateComboSelection(wrapper);
		}
		
	}

	private void addToContentWrapperListAndUpdateComboSelection(final ContentWrapper currWrapper) {
		if (null != currWrapper) {
			this.wrappers.add(currWrapper);
			this.wrappersCombo.add(currWrapper.getName());
			if (getModel() != null && getModel().getContentWrapperId() != null
					&& getModel().getContentWrapperId().equals(currWrapper.getWrapperId())) {
				this.wrappersCombo.select(wrappers.size());
				this.setWrapper(currWrapper);
			}
		}
	}

	@Override
	protected void bindControls() {
		final EpControlBindingProvider bindingProvider = EpControlBindingProvider.getInstance();

		// Binds the target combo box using data binding
		bindTargetCombo(bindingProvider);

		// Binds the dynamic content name using data binding
		bindingProvider.bind(getDataBindingContext(), dynamicContentNameText, getModel(), NAME, EpValidatorFactory.STRING_255_REQUIRED, null, true);

		// Binds the dynamic content name using data binding
		bindingProvider.bind(getDataBindingContext(), dynamicContentDescriptionText,
				getModel(), DESCRIPTION, 
				EpValidatorFactory.MAX_LENGTH_65535, null, true);
			
		//Create the new content wizard details page
		EpWizardPageSupport.create(NewDynamicContentWizardWrapperPage.this, getDataBindingContext());

	}

	/**
	 * Binds the content wrapper select combo-box drop down using data binding.
	 * 
	 * @param bindingProvider the provider of the binding
	 */
	private void bindTargetCombo(final EpControlBindingProvider bindingProvider) {

		// The observable update value strategy
		ObservableUpdateValueStrategy updateStrat = new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object value) {
				final int selectionIndex = (Integer) value;
				try {
					final ContentWrapper wrapper = wrappers.get(selectionIndex - 1);
					if (null == getModel().getContentWrapperId() || !wrapper.getWrapperId().equals(getModel().getContentWrapperId())) {
						getModel().setContentWrapperId(wrapper.getWrapperId());
						setWrapper(wrapper);
						((AbstractEpWizard<?>) getWizard()).getWizardDialog().updateButtons();
					}
					return Status.OK_STATUS;
				} catch (final EpServiceException e) {
					return new Status(IStatus.WARNING, StorePlugin.PLUGIN_ID, "Cannot set the content wrapper."); //$NON-NLS-1$
				}
			}
		};

		// Bind the target combo box so that it is a required combo with the
		// first element not being valid, and update accordingly to the
		// update strategy
		if (!wizardInEditMode) {
			bindingProvider.bind(getDataBindingContext(), wrappersCombo, 
					EpValidatorFactory.REQUIRED_COMBO_FIRST_ELEMENT_NOT_VALID,
					null, updateStrat, true);
		}
	}

	@Override
	public boolean beforeNext(final PageChangingEvent event) {
		if (!processDynamicContentValidation()) {
			return false;
		}
		if (!this.parametersInitialised) {
			initializeParameterValues();
		}
		return true;
	}

	/**
	 * initialises parameter values.
	 */
	private void initializeParameterValues() {
		if (wizardInEditMode) {
			((NewDynamicContentWizardParametersPage) getWizard().getNextPage(this)).initParametersListFromModel(getModel(), getWrapper());
		} else {
			((NewDynamicContentWizardParametersPage) getWizard().getNextPage(this)).initParametersListFromWrapper(getModel(), getWrapper());
		}
		this.parametersInitialised = true;
	}

	/**
	 * checks for non-null, non-empty, non-duplicate name.
	 * @return true if name is valid, false otherwise
	 */
	private boolean processDynamicContentValidation() {

		// check uniqueness of dynamic content name
		String nameToValidate = getModel().getName();

		if ((nameToValidate == null) || ("".equals(nameToValidate.trim()))) { //$NON-NLS-1$
			return false;
		}

		if (dcNameExists(nameToValidate)) {
			setErrorMessage(TargetedSellingMessages.get().DynamicContentNameExists);
			return false;
		}
		
		setErrorMessage(null); // unset errors in case of back button
		return true;

	}
	
	/**
	 * Checks if dynamicContent with given name already exists.
	 * check will return true if dynamic content with given name 
	 * exists and GUIDs are different. 
	 * 
	 * @param dcName - name of dynamic content to be checked
	 * @return true - if another object with given name exists, false otherwise
	 */
	private boolean dcNameExists(final String dcName) {
		final DynamicContentService dcService = ServiceLocator.getService(
				ContextIdNames.DYNAMIC_CONTENT_SERVICE);
		try {
			final DynamicContent dynamicContent = dcService.findByName(dcName);
			if (dynamicContent != null && !dynamicContent.getGuid().equals(getModel().getGuid())) {
				return true;
			}
		} catch (DuplicateNameException e) {
			return true;
		}
		return false;
	}

	/**
	 * Returns content wrapper selected on dialog page.
	 *
	 * @return wrapper - selected wrapper
	 */
	public ContentWrapper getWrapper() {
		return wrapper;
	}

	/**
	 * Set Content wrapper selected and initialises parameters.
	 *
	 * @param wrapper Content wrapper selected
	 */
	public void setWrapper(final ContentWrapper wrapper) {
		this.wrapper = wrapper;
		this.parametersInitialised = false;
	}
	
	@Override
	public boolean isPageComplete() {
		final boolean isCompleteSuper = super.isPageComplete();
		
		LOG.debug("First Page is complete? ..." + isCompleteSuper + " (super)"); //$NON-NLS-1$ //$NON-NLS-2$

		return isCompleteSuper;
	}

	@Override
	public boolean enableFinish() {
		final boolean isValid = processDynamicContentValidation();
		LOG.debug("enableFinish Wrapper: " + isValid); //$NON-NLS-1$
		return isValid;
	}

	/**
	 * check if parameters have been initialized upon selection of content wrapper.
	 * the initialisation should be done upon pressing next button.
	 * @return true if initialisation took place, false otherwise
	 */
	public boolean isParametersInitialised() {
		return parametersInitialised;
	}

}
