/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.catalog.dialogs.categorytype;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.catalog.CatalogPlugin;
import com.elasticpath.cmclient.catalog.editors.model.CatalogModel;
import com.elasticpath.cmclient.catalog.editors.model.CatalogModelImpl;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.ObjectGuidReceiver;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.EpDialogSupport;
import com.elasticpath.cmclient.core.helpers.ChangeSetHelper;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareDialog;
import com.elasticpath.cmclient.policy.ui.IPolicyTargetLayoutComposite;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.util.AttributeComparator;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeGroup;
import com.elasticpath.domain.attribute.AttributeGroupAttribute;
import com.elasticpath.domain.catalog.CategoryType;
import com.elasticpath.service.catalog.CategoryTypeService;
import org.apache.commons.lang.StringUtils;

/**ChangeSetObjects
 * Add/Edit category type dialog.
 */
@SuppressWarnings({ "PMD.GodClass" })
public class CategoryTypeDialog extends AbstractPolicyAwareDialog implements ObjectGuidReceiver {

	private static final int DIALOG_NUMBER_OF_COLUMN = 2;

	private CategoryTypeAttributesDualListBox attributeDualList;

	private final DataBindingContext dataBindingContext;

	private Text categoryTypeNameText;

	private CategoryType categoryType;

	private boolean editMode;

	private List<Attribute> selectedAttributes;

	private CatalogModel catalogModel;

	private String originalName;

	private final ChangeSetHelper changeSetHelper = ServiceLocator.getService(ChangeSetHelper.BEAN_ID);

	/**
	 * Policy container for the dialog controls.
	 */
	private PolicyActionContainer addEditCategoryTypeDialogContainer;

	private final CategoryTypeService categoryTypeService = ServiceLocator.getService(ContextIdNames.CATEGORY_TYPE_SERVICE);

	/**
	 * Constructs the CategoryType dialog.
	 */
	public CategoryTypeDialog() {
		super(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), DIALOG_NUMBER_OF_COLUMN, false);
		dataBindingContext = new DataBindingContext();
	}

	/**
	 * Constructor for the CategoryType add and edit dialog.
	 *  @param categoryType the category type passed in.
	 * @param catalogModel the catalog model
	 */
	public CategoryTypeDialog(final CategoryType categoryType, final CatalogModel catalogModel) {
		this();
		this.catalogModel = catalogModel;
		editMode = categoryType != null;
		if (editMode) {
			initializeDialog(categoryType);
		} else {
			this.categoryType = ServiceLocator.getService(ContextIdNames.CATEGORY_TYPE);
			this.categoryType.setCatalog(catalogModel.getCatalog());
			selectedAttributes = new ArrayList<>();
			originalName = null;
		}

	}

	@Override
	protected void bindControls() {
		final EpControlBindingProvider bindingProvider = EpControlBindingProvider.getInstance();

		// name
		bindingProvider.bind(dataBindingContext, categoryTypeNameText, getCategoryType(), "name", //$NON-NLS-1$
				EpValidatorFactory.STRING_255_REQUIRED, null, true);

		EpDialogSupport.create(this, dataBindingContext);
	}

	/**
	 * @return the product type of the wizard.
	 */
	public CategoryType getCategoryType() {
		return categoryType;
	}

	private List<Attribute> getCategoryTypeAttributes() {
		final AttributeGroup attributeGroup = categoryType.getAttributeGroup();
		final List<AttributeGroupAttribute> groupAttList = new ArrayList<>();
		selectedAttributes = new ArrayList<>();

		for (final AttributeGroupAttribute groupAttr : attributeGroup.getAttributeGroupAttributes()) {
			groupAttList.add(groupAttr);
		}
		groupAttList.sort(new AttributeComparator());

		for (final AttributeGroupAttribute groupAttr : groupAttList) {
			selectedAttributes.add(groupAttr.getAttribute());
		}

		return selectedAttributes;
	}

	private Set<AttributeGroupAttribute> getGroupAttributeFromList(final List<Attribute> attributes) {
		int order = 0;
		final Set<AttributeGroupAttribute> attributeGroupAttributes = new HashSet<>();

		for (final Attribute attribute : attributes) {
			final AttributeGroupAttribute groupAttribute = ServiceLocator.getService(
					ContextIdNames.CATEGORY_TYPE_ATTRIBUTE);
			groupAttribute.setAttribute(attribute);
			groupAttribute.setOrdering(order++);
			attributeGroupAttributes.add(groupAttribute);
		}
		return attributeGroupAttributes;
	}

	@Override
	protected String getTitle() {
		if (editMode) {
			return CatalogMessages.get().CategoryTypeAddEditDialog_EditWindowTitle;
		}
		return CatalogMessages.get().CategoryTypeAddEditDialog_AddWindowTitle;
	}

	@Override
	protected String getInitialMessage() {
		if (editMode) {
			return CatalogMessages.get().CategoryTypeAddEditDialog_Edit_InitMsg;
		}
		return CatalogMessages.get().CategoryTypeAddEditDialog_Add_InitMsg;
	}

	@Override
	protected Image getWindowImage() {
		// no image required for the window.
		return null;
	}

	@Override
	public String getWindowTitle() {
		return getTitle();
	}

	@Override
	protected void okPressed() {
		setErrorMessage(null);
		final boolean result = validateName();
		if (!result) {
			return;
		}
		setCategoryTypeAttributes((List<Attribute>) attributeDualList.getAssigned());

		if (changeSetHelper.isChangeSetsEnabled() && changeSetHelper.getActiveChangeSet() != null) {
			if (editMode) {
				catalogModel.getCategoryTypeTableItems().addModifiedItem(getCategoryType());
			} else {
				catalogModel.getCategoryTypeTableItems().addAddedItem(getCategoryType());
			}

			performSaveOperation();
		}
		super.okPressed();
	}

	/**
	 * Currently does nothing, but should be overridden if the dialog should actually save the categoryType.
	 */
	protected void performSaveOperation() {
		// do nothing
	}

	/**
	 * Returns <code>false</code> if the entered category type name already exists; true otherwise.
	 *
	 * @return <code>false</code> if the entered category type name already exists; true otherwise
	 */
	private boolean validateName() {
		//validates in the db
		final CategoryType type = categoryTypeService.findCategoryType(categoryTypeNameText.getText());
		
		if (type != null) {
			if (editMode && categoryType.getName().equals(originalName)) {
				return true;
			}
			setErrorMessage(CatalogMessages.get().CategoryTypeAddEditDialog_NameExists_ErrMsg);
			return false;
		}

		//validates the model (newly added items)
		if (catalogModel != null) {
			Set<CategoryType> addedItems = catalogModel.getCategoryTypeTableItems().getAddedItems();
			for (CategoryType addedItem : addedItems) {
				if (addedItem.getName().equals(categoryTypeNameText.getText())) {
					setErrorMessage(CatalogMessages.get().CategoryTypeAddEditDialog_NameExists_ErrMsg);
					return false;
				}
			}
		}

		return true;
	}

	@Override
	protected String getPluginId() {
		return CatalogPlugin.PLUGIN_ID;
	}

	@Override
	public Object getModel() {
		return categoryType;
	}

	@Override
	protected void populateControls() {
		setButtonLabel();

		String categoryTypeName = this.categoryType.getName();
		if (StringUtils.isNotBlank(categoryTypeName)) {
			categoryTypeNameText.setText(categoryTypeName);
		}
	}

	private void setButtonLabel() {
		if (editMode) {
			getOkButton().setText(CoreMessages.get().AbstractEpDialog_ButtonOK);
		} else {
			getOkButton().setText(CatalogMessages.get().ProductMerchandisingAssociationDialog_Add);
		}
		getOkButton().setImage(null);
		getOkButton().setAlignment(SWT.CENTER);
		getOkButton().redraw();
	}

	private void setCategoryTypeAttributes(final List<Attribute> assignedAttributes) {

		final Set<AttributeGroupAttribute> groupAttributeSet = getGroupAttributeFromList(assignedAttributes);
		final AttributeGroup attributeGroup = ServiceLocator.getService(ContextIdNames.ATTRIBUTE_GROUP);

		attributeGroup.setAttributeGroupAttributes(groupAttributeSet);

		getCategoryType().setAttributeGroup(attributeGroup);
	}

	@Override
	public String getTargetIdentifier() {
		return "addEditCategoryTypeDialog"; //$NON-NLS-1$
	}

	@Override
	public void setObjectGuid(final String objectGuid) {
		if (objectGuid == null) {
			categoryType = ServiceLocator.getService(ContextIdNames.CATEGORY_TYPE);
			editMode = false;
		} else {
			initializeDialog(getCategoryTypeService().findByGuid(objectGuid));

			editMode = true;

			// On opening dialog in the change set object editor,
			// we need to populate the model used in createDialogContent attributeDualList
			if (catalogModel == null) {
				catalogModel = new CatalogModelImpl(categoryType.getCatalog());
			}
		}
	}

	private void initializeDialog(final CategoryType categoryType) {
		this.categoryType = categoryType;

		selectedAttributes = getCategoryTypeAttributes();

		// remember the original name for validation purposes
		originalName = categoryType.getName();
	}

	/**
	 * Get the category type service.
	 *
	 * @return the instance of the category type service
	 */
	protected CategoryTypeService getCategoryTypeService() {
		return categoryTypeService;
	}

	@Override
	protected void createDialogContent(final IPolicyTargetLayoutComposite dialogComposite) {

		addEditCategoryTypeDialogContainer = addPolicyActionContainer("addEditCategoryTypeDialog"); //$NON-NLS-1$
		if (editMode) {
			// force state policy to check if category type is in current change set
			addEditCategoryTypeDialogContainer.setPolicyDependent(categoryType);
		} else {
			// force state policy to *not* check if category type in current change set
			addEditCategoryTypeDialogContainer.setPolicyDependent(null);
		}

		final IEpLayoutData labelData = dialogComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.CENTER, false, false);
		final IEpLayoutData fieldData = dialogComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.CENTER, true, false);

		final IPolicyTargetLayoutComposite fieldComposite = dialogComposite.addGridLayoutComposite(3, false,
				dialogComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false, 4, 1), addEditCategoryTypeDialogContainer);

		fieldComposite.addLabelBoldRequired(CatalogMessages.get().CategoryTypeAddEditDialog_Name, labelData, addEditCategoryTypeDialogContainer);
		categoryTypeNameText = fieldComposite.addTextField(fieldData, addEditCategoryTypeDialogContainer);
		fieldComposite.addEmptyComponent(null, addEditCategoryTypeDialogContainer);

		attributeDualList = new CategoryTypeAttributesDualListBox(dialogComposite,
				dialogComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true, 2, 1),
				addEditCategoryTypeDialogContainer,
				selectedAttributes,
				CatalogMessages.get().CategoryTypeAddEditDialog_AvailableAttributes,
				CatalogMessages.get().CategoryTypeAddEditDialog_AssignedAttributes,
				catalogModel.getCatalog());
		attributeDualList.createControls();
	}

	@Override
	protected Object getDependentObject() {
		return categoryType;
	}

	@Override
	protected void refreshLayout() {
		// Do nothing
	}

	@Override
	protected PolicyActionContainer getOkButtonPolicyActionContainer() {
		return addEditCategoryTypeDialogContainer;
	}

	
	@Override
	public DataBindingContext getDataBindingContext() {
		return dataBindingContext;
	}

}
