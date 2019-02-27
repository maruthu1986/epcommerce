/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.catalog.dialogs.catalog;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.catalog.CatalogPlugin;
import com.elasticpath.cmclient.catalog.editors.model.CatalogModel;
import com.elasticpath.cmclient.catalog.editors.model.CatalogModelImpl;
import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.ObjectGuidReceiver;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.EpDialogSupport;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.cmclient.policy.StatePolicy;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareDialog;
import com.elasticpath.cmclient.policy.ui.IPolicyTargetLayoutComposite;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeMultiValueType;
import com.elasticpath.domain.attribute.AttributeType;
import com.elasticpath.domain.attribute.AttributeUsage;
import com.elasticpath.service.attribute.AttributeService;

/**
 * The class of add/edit attribute dialog presentation.
 */
@SuppressWarnings({ "PMD.GodClass" })
public class CatalogAttributesAddEditDialog extends AbstractPolicyAwareDialog implements ObjectGuidReceiver {

	private static final int DIALOG_NUMBER_OF_COLUMN = 1;

	private static final int MAIN_COMPOSITE_NUMBER_OF_COLUMN = 2;

	private Text attrKeyText;

	private Text attrNameText;

	private CCombo attrUsageCombo;

	private CCombo attrTypeCombo;

	private Button multiLangCheck;

	private Button requiredCheck;

	private Button multiValuesCheck;

	private Attribute attribute;

	private Map<String, String> usageMap;

	private Map<String, String> typeMap;

	private boolean editMode;

	private boolean isGlobal;

	private static final String EMPTY_STRING = ""; //$NON-NLS-1$
	
	private AttributeService attributeService;

	private CatalogModel catalogModel;


	private PolicyActionContainer nameFieldContainer;

	private PolicyActionContainer addEditAttributeNonEditableDialogContainer;
	
	private PolicyActionContainer multiLangCheckContainer; 
	
	private PolicyActionContainer multiValuesCheckContainer; 
	
	private final DataBindingContext dataBindingContext = new DataBindingContext();

	/**
	 * @param parentShell
	 *            the parent's shell.
	 * @param attribute
	 *            the attribute to be edited or added.
	 * @param isGlobal whether this is a global attribute
	 * @param catalogModel the {@link com.elasticpath.cmclient.catalog.editors.model.CatalogModel} 
	 */
	public CatalogAttributesAddEditDialog(final Shell parentShell,
		final Attribute attribute,
		final boolean isGlobal,
		final CatalogModel catalogModel) {

		super(parentShell, DIALOG_NUMBER_OF_COLUMN, false);
		this.attributeService = ServiceLocator.getService(ContextIdNames.ATTRIBUTE_SERVICE);
		initializeDialog(attribute, isGlobal, attributeService.getAttributeTypeMap(), getUsageMap());
		editMode = attribute != null;
		this.catalogModel = catalogModel;
	}

	private void initializeDialog(final Attribute attribute, final boolean isGlobal, final Map<String, String> typeMap,
			final Map<String, String> usageMap) {
		this.isGlobal = isGlobal;
		this.attribute = attribute;
		this.usageMap = usageMap;
		this.typeMap = typeMap;

	}
	
	/**
	 * Default constructor.
	 */
	public CatalogAttributesAddEditDialog() {
		super(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 2, false);
	}
	
	/**
	 * Create a custom update strategy to update the model based on the selected attribute type.
	 */
	private class TypeUpdateStrategy extends ObservableUpdateValueStrategy {
		@Override
		protected IStatus doSet(final IObservableValue observableValue,
				final Object newValue) {
			
			if (attrTypeCombo.getSelectionIndex() == 0) {
				return Status.OK_STATUS;
			}
			
			AttributeType attributeType = getAttributeType((String) typeMap.keySet().toArray()[attrTypeCombo
					.getSelectionIndex() - 1]);

			attribute.setAttributeType(attributeType);

			if (!attributeTypeSupportsMultipleLanguages(attributeType)) {
				multiLangCheck.setSelection(false);
				attribute.setLocaleDependant(false);
			}
			
			if (!AttributeType.SHORT_TEXT.equals(attributeType)) {
				multiValuesCheck.setSelection(false);
				attribute.setMultiValueType(AttributeMultiValueType.SINGLE_VALUE);
			}
			
			multiLangCheckContainer.setPolicyDependent(attribute);
			multiValuesCheckContainer.setPolicyDependent(attribute);
			applyStatePolicy(getStatePolicy());

			return Status.OK_STATUS;
		}
	}
	
	/**
	 * Create a custom update strategy to update the model based on the selected attribute usage.
	 */
	private class UsageUpdateStrategy extends ObservableUpdateValueStrategy {
		@Override
		protected IStatus doSet(final IObservableValue observableValue,
				final Object newValue) {
			if (attrUsageCombo.getSelectionIndex() == 0) {
				return Status.OK_STATUS;
			}
			String typekey = (String) usageMap.keySet().toArray()[attrUsageCombo
					.getSelectionIndex() - 1];
			attribute.setAttributeUsage(((AttributeUsage) ServiceLocator.getService(ContextIdNames.ATTRIBUTE_USAGE))
							.getAttributeUsageById(Integer.parseInt(typekey)));
			return Status.OK_STATUS;
		}
	}	

	@Override
	protected void createEpButtonsForButtonsBar(
			final ButtonsBarType buttonsBarType, final Composite parent) {
		if (editMode) {
			createEpOkButton(parent,
					CoreMessages.get().AbstractEpDialog_ButtonOK, null);
		} else {
			createEpOkButton(parent,
					CatalogMessages.get().AttributeAddDialog_Btn_Add, null);
		}
		createEpCancelButton(parent);
	}


	@Override
	protected String getPluginId() {
		return CatalogPlugin.PLUGIN_ID;
	}

	@Override
	public Object getModel() {
		return attribute;
	}

	@Override
	public void populateControls() {
		attrUsageCombo.add(CatalogMessages.get().AttributeAddDialog_DefaultUsageComboEntry);
		attrTypeCombo.add(CatalogMessages.get().AttributeAddDialog_DefaultTypeComboEntry);
		for (String usage : usageMap.values()) {
			attrUsageCombo.add(usage);
		}
		for (String type : typeMap.values()) {
			attrTypeCombo.add(CoreMessages.get().getMessage(type));
		}
		attrUsageCombo.setVisibleItemCount(attrUsageCombo.getItemCount());
		attrTypeCombo.setVisibleItemCount(attrTypeCombo.getItemCount());

		if (editMode) {
			attrKeyText.setText(attribute.getKey());
			attrNameText.setText(attribute.getName());

			attrUsageCombo.setText(attribute.getAttributeUsage().toString());
			attrTypeCombo.setText(CoreMessages.get().getMessage(attribute
					.getAttributeType().getNameMessageKey()));

			multiLangCheck.setSelection(attribute.isLocaleDependant());
			requiredCheck.setSelection(attribute.isRequired());
			multiValuesCheck.setSelection(attribute.isMultiValueEnabled());
			return;
		}

		// add new attributes
		attrUsageCombo.select(0);
		attrTypeCombo.select(0);
		getAttribute().setAttributeType(
				getAttributeType((String) typeMap.keySet().toArray()[0]));
		getAttribute().setAttributeUsage(((AttributeUsage) ServiceLocator.getService(ContextIdNames.ATTRIBUTE_USAGE))
						.getAttributeUsageById(Integer.parseInt((String) usageMap.keySet().toArray()[0])));
	}

	@Override
	protected String getInitialMessage() {
		if (!editMode) {
			return CatalogMessages.get().AttributeAddDialog_InitMsg_AddNewAttribute;
		}
		return CatalogMessages.get().AttributeAddDialog_InitMsg_EditAnAttribute;
	}

	@Override
	protected String getTitle() {
		if (!editMode) {
			return CatalogMessages.get().AttributeAddDialog_Title_Add;
		}
		return CatalogMessages.get().AttributeAddDialog_Title_Edit;
	}

	@Override
	protected String getWindowTitle() {

		if (!editMode) {
			return CatalogMessages.get().AttributeAddDialog_WinTitle_Add;
		}
		return CatalogMessages.get().AttributeAddDialog_WinTitle_Edit;

	}

	/**
	 * @return the attribute to be edited or added.
	 */
	public Attribute getAttribute() {
		if (attribute == null) {
			attribute = ServiceLocator.getService(ContextIdNames.ATTRIBUTE);
			attribute.setGlobal(isGlobal);
		}
		return attribute;
	}

	@Override
	protected void bindControls() {
		EpControlBindingProvider.getInstance().bind(dataBindingContext,
				this.attrNameText, getAttribute(),
				"name", EpValidatorFactory.STRING_255_REQUIRED, //$NON-NLS-1$
				null, true);

		EpControlBindingProvider.getInstance().bind(dataBindingContext,
				this.attrKeyText, getAttribute(),
				"key", EpValidatorFactory.ATTRIBUTE_KEY, //$NON-NLS-1$
				null, true);

		// Bind attribute usage combo.
		final ObservableUpdateValueStrategy usageUpdateStrategy = new UsageUpdateStrategy();
		EpControlBindingProvider.getInstance().bind(dataBindingContext,
				this.attrUsageCombo, EpValidatorFactory.REQUIRED_COMBO_FIRST_ELEMENT_NOT_VALID, null, usageUpdateStrategy, true);

		// Bind attribute type combo.
		final ObservableUpdateValueStrategy typeUpdateStrategy = new TypeUpdateStrategy();
		EpControlBindingProvider.getInstance().bind(dataBindingContext,
				this.attrTypeCombo, EpValidatorFactory.REQUIRED_COMBO_FIRST_ELEMENT_NOT_VALID, null, typeUpdateStrategy, true);

		// Binding attribute local language dependent checkbox.
		final ObservableUpdateValueStrategy localeDepUpdateStrategy = new ObservableUpdateValueStrategy() {
			// -- Create a custom update strategy to update the model based on
			// the selected attribute usage.
			@Override
			protected IStatus doSet(final IObservableValue observableValue,
					final Object newValue) {
				attribute.setLocaleDependant(multiLangCheck.getSelection());
				return Status.OK_STATUS;
			}
		};
		EpControlBindingProvider.getInstance()
				.bind(dataBindingContext, this.multiLangCheck, null, null,
						localeDepUpdateStrategy, false);

		// Binding attribute multi value enabled indicator checkbox.
		final ObservableUpdateValueStrategy multiValueUpdateStrategy = new ObservableUpdateValueStrategy() {
			// -- Create a custom update strategy to update the model based on
			// the selected attribute usage.
			@Override
			protected IStatus doSet(final IObservableValue observableValue,
					final Object newValue) {
				if (multiValuesCheck.getSelection()) {
					attribute.setMultiValueType(AttributeMultiValueType.RFC_4180);
				} else {
					attribute.setMultiValueType(AttributeMultiValueType.SINGLE_VALUE);
				}
				return Status.OK_STATUS;
			}
		};
		EpControlBindingProvider.getInstance().bind(dataBindingContext,
				this.multiValuesCheck, null, null, multiValueUpdateStrategy,
				false);

		// Binding attribute required indicator checkbox.
		final ObservableUpdateValueStrategy requiredUpdateStrategy = new ObservableUpdateValueStrategy() {
			// -- Create a custom update strategy to update the model based on
			// the selected attribute usage.
			@Override
			protected IStatus doSet(final IObservableValue observableValue,
					final Object newValue) {
				attribute.setRequired(requiredCheck.getSelection());
				return Status.OK_STATUS;
			}
		};
		EpControlBindingProvider.getInstance().bind(dataBindingContext,
				this.requiredCheck, null, null, requiredUpdateStrategy, false);
		EpDialogSupport.create(this, dataBindingContext);
	}

	@Override
	protected Image getWindowImage() {
		// no image available for the dialog.
		return null;
	}

	private AttributeType getAttributeType(final String typekey) {
		return AttributeType.valueOf(Integer.parseInt(typekey));
	}
	
	@Override
	protected void okPressed() {
		this.setErrorMessage(null);
		if (editMode) {
			if (isNameExist()) {
				return;	
			}
		} else {
			if (isKeyExist() || isNameExist()) {
				return;	
			}		
		}

		performSaveOperation();
		super.okPressed();
	}
	

	/**
	 * Currently does nothing, but should be overridden if the dialog should actually save the categoryType.
	 *
	 */
	protected void performSaveOperation() {
		// do nothing
	}
	
	/**
	 * Returns <code>true</code> if the entered attribute name already exists 
	 * within attribute usage scope, false otherwise.
	 * 
	 * @return <code>true</code> if the entered attribute name already exists 
	 * within attribute usage scope, false otherwise.
	 */
	private boolean isNameExist() {
		boolean nameExists = attributeService.nameExistsInAttributeUsage(attribute);
		if (nameExists) {
			setErrorMessage(
				NLS.bind(CatalogMessages.get().AttributeAddDialog_NameExists_ErrMsg,
				attribute.getName()));
			return true;
		}
		
		//validates the model (newly added items)
		if (catalogModel != null) {
			Set<Attribute> addedItems = catalogModel.getAttributeTableItems().getAddedItems();
			for (Attribute addedItem : addedItems) {
				if (addedItem.getName().equals(attribute.getName())) {
					setErrorMessage(
						NLS.bind(CatalogMessages.get().AttributeAddDialog_NameExists_ErrMsg,
						attribute.getName()));
					return true;
				}
			}
		}
		
		return false;
	}	
	
	/**
	 * Returns <code>true</code> if the entered attribute key already exists; false otherwise.
	 * 
	 * @return <code>true</code> if the entered attribute key already exists; false otherwise.
	 */
	private boolean isKeyExist() {
		boolean keyExists = attributeService.keyExists(attribute);
		if (keyExists) {
			setErrorMessage(
				NLS.bind(CatalogMessages.get().AttributeAddDialog_KeyExists_ErrMsg,
				attribute.getKey()));
			return true;
		}
		
		//validates the model (newly added items)
		if (catalogModel != null) {
			Set<Attribute> addedItems = catalogModel.getAttributeTableItems().getAddedItems();
			for (Attribute addedItem : addedItems) {
				if (addedItem.getKey().equals(attribute.getKey())) {
					setErrorMessage(
						NLS.bind(CatalogMessages.get().AttributeAddDialog_KeyExists_ErrMsg,
						attribute.getKey()));
					return true;
				}
			}
		}		
		
		return false;
	}

	@Override
	public String getTargetIdentifier() {
		return "addEditCatalogAttributeDialog";
	}

	@Override
	protected void createDialogContent(final IPolicyTargetLayoutComposite dialogComposite) {
		nameFieldContainer = addPolicyActionContainer("CatalogAttributeNameFieldContainer");

		addEditAttributeNonEditableDialogContainer = addPolicyActionContainer("addEditAttributeNonEditableDialog");

		multiLangCheckContainer = addPolicyActionContainer("CatalogAttributeMultiLangCheckboxContainer");
		multiValuesCheckContainer = addPolicyActionContainer("CatalogAttributeMultiValuesCheckboxContainer");

		nameFieldContainer.setPolicyDependent(attribute);
		addEditAttributeNonEditableDialogContainer.setPolicyDependent(attribute);
		multiLangCheckContainer.setPolicyDependent(attribute);
		multiValuesCheckContainer.setPolicyDependent(attribute);

		IPolicyTargetLayoutComposite mainComposite = dialogComposite.addGridLayoutComposite(MAIN_COMPOSITE_NUMBER_OF_COLUMN, false,
				dialogComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true),
				addEditAttributeNonEditableDialogContainer);

		final IEpLayoutData labelData = mainComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL);
		final IEpLayoutData fieldData = mainComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false);

		final IEpLayoutData nameFieldComposite = mainComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false, 4, 1);
		final int numberOfColumnsInDialog = 3;
		mainComposite.addGridLayoutComposite(numberOfColumnsInDialog, false, nameFieldComposite, addEditAttributeNonEditableDialogContainer);

		mainComposite.addLabelBoldRequired(CatalogMessages.get().AttributeAddDialog_AttributeKey,
				labelData, addEditAttributeNonEditableDialogContainer);
		attrKeyText = mainComposite.addTextField(fieldData, addEditAttributeNonEditableDialogContainer);

		mainComposite.addLabelBoldRequired(CatalogMessages.get().AttributeAddDialog_AttributeName, labelData, nameFieldContainer);
		attrNameText = mainComposite.addTextField(fieldData, nameFieldContainer);

		mainComposite.addLabelBoldRequired(CatalogMessages.get().AttributeAddDialog_AttributeUsage, labelData,
				addEditAttributeNonEditableDialogContainer);
		attrUsageCombo = mainComposite.addComboBox(fieldData, addEditAttributeNonEditableDialogContainer);

		mainComposite.addLabelBoldRequired(CatalogMessages.get().AttributeAddDialog_AttributeType, labelData,
				addEditAttributeNonEditableDialogContainer);
		attrTypeCombo = mainComposite.addComboBox(fieldData, addEditAttributeNonEditableDialogContainer);

		mainComposite.addLabelBold(CatalogMessages.get().AttributeAddDialog_MultiLanguage, labelData,
				addEditAttributeNonEditableDialogContainer);
		multiLangCheck = mainComposite.addCheckBoxButton(EMPTY_STRING, fieldData, multiLangCheckContainer);

		mainComposite.addLabelBold(CatalogMessages.get().AttributeAddDialog_RequiredAttribute, labelData,
				addEditAttributeNonEditableDialogContainer);
		requiredCheck = mainComposite.addCheckBoxButton(EMPTY_STRING, fieldData, addEditAttributeNonEditableDialogContainer);

		mainComposite.addLabelBold(CatalogMessages.get().AttributeAddDialog_MultiValuesAllowed, labelData,
				addEditAttributeNonEditableDialogContainer);
		multiValuesCheck = mainComposite.addCheckBoxButton(EMPTY_STRING, fieldData, multiValuesCheckContainer);
	}

	@Override
	protected Object getDependentObject() {
		return attribute;
	}

	@Override
	protected void refreshLayout() {
		// TODO  possibly reapply state policy?
	}

	@Override
	protected PolicyActionContainer getOkButtonPolicyActionContainer() {
		return nameFieldContainer;
	}


	@Override
	public void setObjectGuid(final String objectGuid) {
		if (objectGuid == null) {
			attribute = ServiceLocator.getService(ContextIdNames.ATTRIBUTE);
			editMode = false;
		} else {
			attributeService = ServiceLocator.getService(ContextIdNames.ATTRIBUTE_SERVICE);
			attribute = attributeService.findByKey(objectGuid);
			initializeDialog(attribute, false, attributeService.getAttributeTypeMap(), attributeService.getAttributeUsageMap());
			editMode = true;
			if (catalogModel == null) {
				catalogModel = new CatalogModelImpl(attribute.getCatalog());
			}
		}
	}

	@Override
	protected void initializeStatePolicy() {
		StatePolicy statePolicy = getStatePolicy(); 
		statePolicy.init(editMode);
	}
	
	/**
	 *
	 * @return the attributeService
	 */
	protected AttributeService getAttributeService() {
		return attributeService;
	}
	
	@Override
	protected DataBindingContext getDataBindingContext() {
		return dataBindingContext;
	}

	private boolean attributeTypeSupportsMultipleLanguages(final AttributeType attributeType) {
		return attributeType == AttributeType.SHORT_TEXT 
				|| attributeType == AttributeType.LONG_TEXT
				|| attributeType == AttributeType.FILE 
				|| attributeType == AttributeType.IMAGE;
	}
	private Map <String, String> getUsageMap() {
		Map <String, String> usageWithoutCustomerProfileMap = new LinkedHashMap<>(getAttributeService().getAttributeUsageMap());
		usageWithoutCustomerProfileMap.remove(String.valueOf(AttributeUsage.CUSTOMERPROFILE));
		return usageWithoutCustomerProfileMap;
	}


	/**
	 * Gets the edit mode.
	 * @return the edit mode.
	 */
	protected boolean isEditMode() {
		return editMode;
	}

	/**
	 * Gets the catalog model.
	 * @return the catalog model.
	 */
	protected CatalogModel getCatalogModel() {
		return catalogModel;
	}

}
