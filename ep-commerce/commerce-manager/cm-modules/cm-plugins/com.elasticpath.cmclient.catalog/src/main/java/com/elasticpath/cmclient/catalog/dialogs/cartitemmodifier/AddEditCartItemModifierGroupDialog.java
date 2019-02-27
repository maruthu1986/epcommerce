/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.catalog.dialogs.cartitemmodifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.catalog.CatalogPlugin;
import com.elasticpath.cmclient.catalog.dialogs.cartitemmodifier.tablelabelcontentprovider.CartItemModifierGroupLabelProvider;
import com.elasticpath.cmclient.catalog.editors.model.CatalogModel;
import com.elasticpath.cmclient.catalog.editors.model.CatalogModelImpl;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.ObjectGuidReceiver;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.EpDialogSupport;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.helpers.ChangeSetHelper;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.cmclient.policy.StatePolicy;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareDialog;
import com.elasticpath.cmclient.policy.ui.IPolicyTargetLayoutComposite;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.cartmodifier.CartItemModifierField;
import com.elasticpath.domain.cartmodifier.CartItemModifierGroup;
import com.elasticpath.domain.cartmodifier.CartItemModifierGroupLdf;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.service.cartitemmodifier.CartItemModifierService;

/**
 * The dialog UI class for Adding/Editing CartItemModifierGroup.
 */
@SuppressWarnings({"PMD.GodClass", "PMD.TooManyFields"})
public class AddEditCartItemModifierGroupDialog extends AbstractPolicyAwareDialog
		implements ObjectGuidReceiver, SelectionListener, ISelectionChangedListener, IDoubleClickListener {
	private static final int DIALOG_NUMBER_OF_COLUMN = 1;

	private static final int MAIN_COMPOSITE_NUMBER_OF_COLUMN = 4;

	private static final int BINDING_CALLER = 0; //$NON-NLS-1$

	private static final int LANGUAGE_CHANGED_CALLER = 1; //$NON-NLS-1$
	private static final String CART_MODIFIER_GROUP_TABLE = "Cart Modifier Group"; //$NON-NLS-1$

	private Text groupCodeText;

	private Text groupDisplayNameText;

	private CCombo groupLanguageCombo;

	private Button addFieldBtt;

	private Button editFieldBtt;

	private Button removeFieldBtt;

	private Button orderUpBtt;

	private Button orderDownBtt;

	private boolean editMode;

	private CatalogModel catalogModel;

	private CartItemModifierGroup cartItemModifierGroup;

	private final CartItemModifierService cartItemModifierService =
			ServiceLocator.getService(ContextIdNames.CART_ITEM_MODIFIER_SERVICE);

	private Locale selectedLocale;

	private String originalCode;

	private final ChangeSetHelper changeSetHelper = ServiceLocator.getService(ChangeSetHelper.BEAN_ID);

	private List<String> allLocalesTags;

	private IEpTableViewer cartItemModifierFieldsTableViewer;

	private IStructuredSelection sel;

	private final DataBindingContext dataBindingContext;

	private PolicyActionContainer addEditCartItemModifierGroupContainer;

	/**
	 * Default Constructor.
	 */
	public AddEditCartItemModifierGroupDialog() {
		super(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), DIALOG_NUMBER_OF_COLUMN, false);
		dataBindingContext = new DataBindingContext();
	}

	/**
	 * Constructor.
	 *
	 * @param selectedLocale         the selected locale.
	 * @param cartItemModifierGroup the cart item modifier Group to be edited o added
	 *                              {@link com.elasticpath.domain.cartmodifier.CartItemModifierGroup}
	 * @param catalogModel          the default catalog model.
	 */
	public AddEditCartItemModifierGroupDialog(final Locale selectedLocale, final CartItemModifierGroup cartItemModifierGroup,
											  final CatalogModel catalogModel) {

		this();
		this.catalogModel = catalogModel;
		this.editMode = cartItemModifierGroup != null;
		this.selectedLocale = selectedLocale;

		if (editMode) {
			initializeDialog(cartItemModifierGroup);
		} else {
			createCartItemModifierGroup();
		}
	}

	private void initializeDialog(final CartItemModifierGroup cartItemModifierGroup) {
		this.cartItemModifierGroup = cartItemModifierGroup;

		// remember the original code for validation purposes
		this.originalCode = this.cartItemModifierGroup.getCode();
	}

	/**
	 * @return the current CartItemModifierGroup object edited or added.
	 */
	public CartItemModifierGroup getCartItemModifierGroup() {
		if (this.cartItemModifierGroup == null) {
			createCartItemModifierGroup();
		}
		if (this.cartItemModifierGroup.getCode() != null) {
			this.originalCode = this.cartItemModifierGroup.getCode();
		}
		return cartItemModifierGroup;
	}

	private void createCartItemModifierGroup() {
		this.cartItemModifierGroup = ServiceLocator.getService(ContextIdNames.CART_ITEM_MODIFIER_GROUP);
		this.cartItemModifierGroup.setCatalog(this.catalogModel.getCatalog());
		this.originalCode = "";
	}

	@Override
	protected void bindControls() {
		final EpControlBindingProvider bindingProvider = EpControlBindingProvider.getInstance();
		// update strategy for the cartItemModifierGroup code control
		final ObservableUpdateValueStrategy valueUpdateStrategy = new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
				final String code = groupCodeText.getText();
				getCartItemModifierGroup().setCode(code);
				return Status.OK_STATUS;
			}
		};
		// update strategy for the cartItemModifierGroupLDF
		final ObservableUpdateValueStrategy displayNameUpdateStrategy = new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
				checkLDFAndBind(selectedLocale.toString(), groupDisplayNameText.getText(), BINDING_CALLER);
				return Status.OK_STATUS;
			}
		};
		// CartItemModifierFieldOption value
		bindingProvider.bind(dataBindingContext, this.groupCodeText,
				EpValidatorFactory.CARTITEM_MODIFIER_GROUP_CODE, null, valueUpdateStrategy, true);
		// CartItemModifierFieldOption displayName
		bindingProvider.bind(dataBindingContext, this.groupDisplayNameText, EpValidatorFactory.STRING_255_REQUIRED, null,
				displayNameUpdateStrategy, true);
		EpDialogSupport.create(this, dataBindingContext);

		updateEditRemoveUpDownButtons();
	}

	@Override
	protected String getTitle() {
		if (editMode) {
			return CatalogMessages.get().GroupAddEditDialog_EditGroup;
		}
		return CatalogMessages.get().GroupAddEditDialog_AddGroup;
	}

	@Override
	protected String getInitialMessage() {
		if (editMode) {
			return CatalogMessages.get().GroupAddEditDialog_Edit_InitMsg;
		}
		return CatalogMessages.get().GroupAddEditDialog_Add_InitMsg;
	}

	@Override
	protected Image getWindowImage() {
		return null;
	}

	@Override
	protected String getWindowTitle() {
		return getTitle();
	}

	@Override
	protected void okPressed() {
		setErrorMessage(null);
		if (!validate()) {
			return;
		}

		if (changeSetHelper.isChangeSetsEnabled() && changeSetHelper.getActiveChangeSet() != null) {
			if (editMode) {
				// case 1: edit dialog
				catalogModel.getCartItemModifierGroupTableItems().addModifiedItem(getCartItemModifierGroup());
				dataBindingContext.updateModels();

			} else {
				// case 2: new dialog and then implement a check if the code already exists
				// if so then create an error telling the user to use a different code.
				catalogModel.getCartItemModifierGroupTableItems().addAddedItem(getCartItemModifierGroup());
				dataBindingContext.updateModels();
			}
		}

		super.okPressed();
	}

	/**
	 * Validate the cartitemmodifier fields.
	 *
	 * @return <code>true</code> if the brand fields are valid, false otherwise.
	 */
	private boolean validate() {
		final String groupCode = groupCodeText.getText();
		final String groupName = groupDisplayNameText.getText();
		if (groupCode.trim().isEmpty()) {
			setErrorMessage(CatalogMessages.get().GroupAddEditDialog_ErrorDialog_noCode_desc);
			return false;
		} else if (groupName.trim().isEmpty()) {
			setErrorMessage(CatalogMessages.get().GroupAddEditDialog_ErrorDialog_noName_desc);
			return false;
		} else {
			CartItemModifierGroup group = this.cartItemModifierService.findCartItemModifierGroupByCode(groupCode);
			if (group != null) {
				if (editMode && group.getCode().equals(originalCode)) {
					return true;
				}
				setErrorMessage(CatalogMessages.get().GroupAddEditDialog_ErrorDialog_AddInUse_desc);
				return false;
			}
			//validates the model (newly added items) to not have 2 values with same locale
			if (catalogModel != null) {
				for (CartItemModifierGroup itemModifierGroup : catalogModel.getCartItemModifierGroupTableItems().getAddedItems()) {
					if (editMode && itemModifierGroup.getCode().equals(originalCode)) {
						return true;
					} else if ((!editMode && itemModifierGroup.getCode().equals(groupCode))
							&& itemModifierGroup.getCartItemModifierGroupLdfByLocale(this.selectedLocale.toString()) != null) {
						setErrorMessage(CatalogMessages.get().GroupAddEditDialog_ErrorDialog_AddInUse_Langdesc);
						return false;
					}
				}
			}
		}
		return true;
	}

	@Override
	protected void createDialogContent(final IPolicyTargetLayoutComposite dialogComposite) {

		final PolicyActionContainer codeFieldContainer = addPolicyActionContainer("cartItemModifierGroupCodeField"); //$NON-NLS-1$

		IPolicyTargetLayoutComposite mainComposite = dialogComposite.addGridLayoutComposite(MAIN_COMPOSITE_NUMBER_OF_COLUMN, false,
				dialogComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true), codeFieldContainer);

		this.addEditCartItemModifierGroupContainer = addPolicyActionContainer("addEditCartItemModifierGroupDialog"); //$NON-NLS-1$
		if (this.editMode) {
			this.addEditCartItemModifierGroupContainer.setPolicyDependent(this.cartItemModifierGroup);
			codeFieldContainer.setPolicyDependent(this.cartItemModifierGroup);
		} else {
			this.addEditCartItemModifierGroupContainer.setPolicyDependent(null);
			codeFieldContainer.setPolicyDependent(null);
		}

		final IEpLayoutData labelData = mainComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL, false, false);
		final IEpLayoutData fieldData1 = mainComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, false, false, 1, 1);
		final IEpLayoutData fieldData2 = mainComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false, 2, 1);
		final IEpLayoutData fieldData3 = mainComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false, 1, 1);
		final IEpLayoutData tableLayoutData = mainComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true, 3, 7);
		IEpLayoutData buttonLayout = mainComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false);

		mainComposite.addLabelBoldRequired(CatalogMessages.get().GroupAddEditDialog_GroupCode, labelData, codeFieldContainer);
		groupCodeText = mainComposite.addTextField(fieldData2, codeFieldContainer);

		mainComposite.addEmptyComponent(fieldData3, this.addEditCartItemModifierGroupContainer);

		mainComposite.addLabelBoldRequired(CatalogMessages.get().GroupAddEditDialog_GroupName, labelData, this.addEditCartItemModifierGroupContainer);
		groupLanguageCombo = mainComposite.addComboBox(fieldData1, this.addEditCartItemModifierGroupContainer);
		groupLanguageCombo.addSelectionListener(this);
		groupDisplayNameText = mainComposite.addTextField(fieldData2, this.addEditCartItemModifierGroupContainer);

		cartItemModifierFieldsTableViewer = mainComposite.addTableViewer(false, tableLayoutData, this.addEditCartItemModifierGroupContainer,
				CART_MODIFIER_GROUP_TABLE);
		addColumns(cartItemModifierFieldsTableViewer);
		cartItemModifierFieldsTableViewer.getSwtTableViewer().addSelectionChangedListener(this);
		cartItemModifierFieldsTableViewer.getSwtTableViewer().addDoubleClickListener(this);
		cartItemModifierFieldsTableViewer.setContentProvider(new ArrayContentProvider());

		// create edit button for invoking the editing
		Image myimg = CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_EDIT);
		editFieldBtt = mainComposite.addPushButton(CatalogMessages.get().GroupAddEditDialog_TableEditButton, myimg,
				buttonLayout, this.addEditCartItemModifierGroupContainer);
		editFieldBtt.addSelectionListener(this);

		// create add button for invoking the editing
		myimg = CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_ADD);
		addFieldBtt = mainComposite.addPushButton(CatalogMessages.get().GroupAddEditDialog_TableAddButton, myimg,
				buttonLayout, this.addEditCartItemModifierGroupContainer);
		addFieldBtt.addSelectionListener(this);

		// create remove button for invoking the editing
		myimg = CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_REMOVE);
		removeFieldBtt = mainComposite.addPushButton(CatalogMessages.get().GroupAddEditDialog_TableRemoveButton, myimg,
				buttonLayout, this.addEditCartItemModifierGroupContainer);
		removeFieldBtt.addSelectionListener(this);

		// create order up button for invoking the editing
		myimg = CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_UP_ARROW);
		orderUpBtt = mainComposite.addPushButton(CoreMessages.get().button_MoveUp, myimg,
				buttonLayout, this.addEditCartItemModifierGroupContainer);
		orderUpBtt.addSelectionListener(this);

		// create order down button for invoking the editing
		myimg = CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_DOWN_ARROW);
		orderDownBtt = mainComposite.addPushButton(CoreMessages.get().button_MoveDown, myimg,
				buttonLayout, this.addEditCartItemModifierGroupContainer);
		orderDownBtt.addSelectionListener(this);
	}

	@Override
	protected Object getDependentObject() {
		return this.cartItemModifierGroup;
	}

	@Override
	protected void refreshLayout() {
		// Do nothing
	}

	@Override
	protected PolicyActionContainer getOkButtonPolicyActionContainer() {
		return this.addEditCartItemModifierGroupContainer;
	}

	@Override
	protected void populateControls() {
		this.groupCodeText.setText(this.originalCode);
		this.allLocalesTags = new ArrayList<>();
		List<Locale> allLocales = new ArrayList<>(this.catalogModel.getCatalog().getSupportedLocales());
		this.groupLanguageCombo.removeAll();
		for (Locale lang : allLocales) {
			allLocalesTags.add(lang.toLanguageTag());
			this.groupLanguageCombo.add(lang.getDisplayName());
		}
		groupLanguageCombo.select(allLocales.indexOf(this.selectedLocale));
		groupLanguageCombo.notifyListeners(SWT.Selection, new Event());
	}

	private String setButtonLabel() {
		String bttLabel = CatalogMessages.get().AddEditCartItemModifierFieldOptionDialog_Add;
		if (editMode) {
			bttLabel = CoreMessages.get().AbstractEpDialog_ButtonOK;
		}
		return bttLabel;
	}

	@Override
	public String getTargetIdentifier() {
		return "addEditCartItemModifierGroupDialog"; //$NON-NLS-1$
	}

	@Override
	public void setObjectGuid(final String objectGuid) {
		if (objectGuid == null) {
			createCartItemModifierGroup();
			editMode = false;
		} else {
			CartItemModifierGroup foundCartItemModifierGroup = cartItemModifierService.findCartItemModifierGroupByCode(objectGuid);
			initializeDialog(foundCartItemModifierGroup);

			Catalog catalog = this.cartItemModifierGroup.getCatalog();
			selectedLocale = catalog.getDefaultLocale();

			editMode = true;

			// On opening dialog in the change set object editor,
			// we need to populate the model used in the dialog
			if (catalogModel == null) {
				catalogModel = new CatalogModelImpl(catalog);
			}
		}
	}

	@Override
	public DataBindingContext getDataBindingContext() {
		return this.dataBindingContext;
	}

	@Override
	protected void createButtonsForButtonBar(final Composite parent) {
		createEpOkButton(parent, setButtonLabel(), null);
		createEpCancelButton(parent);
	}

	/**
	 * Check CartItemModifierOption LDF and Bind.
	 *
	 * @param lang    The current locale.
	 * @param display The current display name.
	 */
	private void checkLDFAndBind(final String lang, final String display, final int caller) {
		//If called from binding (ui to model bind)
		if (caller == BINDING_CALLER) {
			if (!display.isEmpty()) {
				CartItemModifierGroupLdf currentGroupLDF = this.getCartItemModifierGroup().getCartItemModifierGroupLdfByLocale(lang);
				if (currentGroupLDF == null) {
					currentGroupLDF = ServiceLocator.getService(ContextIdNames.CART_ITEM_MODIFIER_GROUP_LDF);
					currentGroupLDF.setDisplayName(display);
					currentGroupLDF.setLocale(lang);
					this.getCartItemModifierGroup().addCartItemModifierGroupLdf(currentGroupLDF);
				} else {
					currentGroupLDF.setDisplayName(display);
					currentGroupLDF.setLocale(lang);
				}
			}
		} else if (caller == LANGUAGE_CHANGED_CALLER) { // If called when language changed (model to ui bind)
			groupDisplayNameText.setText(CatalogMessages.EMPTY_STRING);
			CartItemModifierGroupLdf currentGroupLDF = getCartItemModifierGroup().getCartItemModifierGroupLdfByLocale(lang);
			if (currentGroupLDF != null) {
				groupDisplayNameText.setText(currentGroupLDF.getDisplayName());
			}
		}
	}

	@Override
	public void widgetSelected(final SelectionEvent selectionEvent) {

		//Language ComboBox
		if (selectionEvent.getSource().equals(this.groupLanguageCombo)) {
			this.selectedLocale = Locale.forLanguageTag(this.allLocalesTags.get(this.groupLanguageCombo.getSelectionIndex()));
			checkLDFAndBind(selectedLocale.toString(), groupDisplayNameText.getText(), LANGUAGE_CHANGED_CALLER);
			refreshTableViewer();
		} else if (selectionEvent.getSource().equals(this.addFieldBtt)) { //Add field button
			List<Locale> locales = new ArrayList<>(catalogModel.getCatalog().getSupportedLocales());
			final AddEditCartItemModifierFieldDialog dialog = new AddEditCartItemModifierFieldDialog(locales, this.selectedLocale, null,
					this.getCartItemModifierGroup());
			if (dialog.open() == Window.OK) {
				add(dialog.getCartItemModifierField());
			}
		} else if (selectionEvent.getSource().equals(this.editFieldBtt) && sel != null) { //Edit field button
			CartItemModifierField selectedField = (CartItemModifierField) sel.getFirstElement();
			this.edit(selectedField);
		} else if (selectionEvent.getSource().equals(this.removeFieldBtt) && sel != null) { //Remove field button
			CartItemModifierField selectedField = (CartItemModifierField) sel.getFirstElement();
			if (selectedField != null) {
				cartItemModifierGroup.removeCartItemModifierField(selectedField);
				refreshTableViewer();
				sel = null;
			}
		} else if (selectionEvent.getSource().equals(this.orderUpBtt)) { //Order up button
			CartItemModifierField selectedField = (CartItemModifierField) sel.getFirstElement();
			this.changeFieldOrder(selectedField, -1);
			refreshTableViewer();
			this.cartItemModifierFieldsTableViewer.getSwtTable().setFocus();
			this.cartItemModifierFieldsTableViewer.getSwtTableViewer().setSelection(sel);
		} else if (selectionEvent.getSource().equals(this.orderDownBtt)) { //Order down button
			CartItemModifierField selectedField = (CartItemModifierField) sel.getFirstElement();
			this.changeFieldOrder(selectedField, +1);
			refreshTableViewer();
			this.cartItemModifierFieldsTableViewer.getSwtTable().setFocus();
			this.cartItemModifierFieldsTableViewer.getSwtTableViewer().setSelection(sel);
		}
		updateEditRemoveUpDownButtons();
	}

	@Override
	public void doubleClick(final DoubleClickEvent doubleClickEvent) {
		CartItemModifierField selectedField = (CartItemModifierField) sel.getFirstElement();
		this.edit(selectedField);
	}

	@Override
	public void selectionChanged(final SelectionChangedEvent selectionChangedEvent) {

		sel = (IStructuredSelection) selectionChangedEvent.getSelection();

		if (ignoreTableSelection(getStatePolicy(), getOkButtonPolicyActionContainer())) {
			return;
		}

		updateEditRemoveUpDownButtons();
	}

	@Override
	public void updateButtons() {
		super.updateButtons();
		updateEditRemoveUpDownButtons();
	}

	@Override
	protected String getPluginId() {
		return CatalogPlugin.PLUGIN_ID;
	}

	@Override
	public Object getModel() {
		return Arrays.asList(this.catalogModel, this.cartItemModifierGroup);
	}

	/**
	 * Processes the states policies to see if we are restricted to a "viewing-only" mode. If so, we can ignore the table selection events.
	 *
	 * @param policy The policy (most restrictive composite)
	 * @param targetContainer the OK button container
	 * @return True if we are can ignore table selections
	 */
	static boolean ignoreTableSelection(final StatePolicy policy, final PolicyActionContainer targetContainer) {

		if (policy == null || targetContainer == null) {
			return false;
		}

		EpControlFactory.EpState state = policy.determineState(targetContainer);
		return (EpControlFactory.EpState.READ_ONLY.equals(state) || EpControlFactory.EpState.DISABLED.equals(state));
	}

	@Override
	public void widgetDefaultSelected(final SelectionEvent selectionEvent) {
		// Not used.
	}

	/**
	 * Set the columns name to the table viewer.
	 *
	 * @param table table of the table viewer.
	 */
	private void addColumns(final IEpTableViewer table) {
		final String[] columnNames = {CatalogMessages.get().GroupAddEditDialog_TableCodeColumn,
				CatalogMessages.get().GroupAddEditDialog_TableDisplayNameColumn, CatalogMessages.get().GroupAddEditDialog_TableDisplayTypeColumn,
				CatalogMessages.get().GroupAddEditDialog_TableDisplayRequiredColumn};
		final int[] columnWidths = {80, 180, 150, 80};

		for (int i = 0; i < columnNames.length; ++i) {
			table.addTableColumn(columnNames[i], columnWidths[i]);
		}
	}

	/**
	 * Add Cart Item Modifer Group Field  to Group and refresh table viewer data.
	 *
	 * @param field CartItemModifierField to be added to CartItemModifierGroup.
	 */
	private void add(final CartItemModifierField field) {
		this.getCartItemModifierGroup().addCartItemModifierField(field);
		refreshTableViewer();
	}

	/**
	 * Edit Cart Item Modifer Group Field  to Group and refresh table viewer data.
	 *
	 * @param selectedField CartItemModifierField to be edited of a CartItemModifierGroup.
	 */
	private void edit(final CartItemModifierField selectedField) {
		if (selectedField != null) {
			List<Locale> locales = new ArrayList<>(catalogModel.getCatalog().getSupportedLocales());
			final AddEditCartItemModifierFieldDialog dialog = new AddEditCartItemModifierFieldDialog(locales, this.selectedLocale,
					selectedField, getCartItemModifierGroup());
			if (dialog.open() == Window.OK) {
				refreshTableViewer();
			}
		}
	}

	/**
	 * Method to refresh the content of the tableviewer.
	 */
	private void refreshTableViewer() {
		if (cartItemModifierFieldsTableViewer != null) {
			this.cartItemModifierFieldsTableViewer.setLabelProvider(new CartItemModifierGroupLabelProvider(this.selectedLocale));
			List<CartItemModifierField> cartItemModifierFieldsOrdered =
					new ArrayList<>(this.getCartItemModifierGroup().getCartItemModifierFields());
			Collections.sort(cartItemModifierFieldsOrdered);
			this.cartItemModifierFieldsTableViewer.setInput(cartItemModifierFieldsOrdered);
		}
	}

	/**
	 * Method to reorder the fields on the table viewer.
	 *
	 * @param selectedField the selected field on the table viewer
	 * @param swapOperation -1=order up +1= order down
	 */
	private void changeFieldOrder(final CartItemModifierField selectedField, final int swapOperation) {
		List<CartItemModifierField> cartItemModifierFieldsOrdered =
				new ArrayList<>(this.getCartItemModifierGroup().getCartItemModifierFields());
		Collections.sort(cartItemModifierFieldsOrdered);
		int selectedFieldIndex = cartItemModifierFieldsOrdered.indexOf(selectedField);
		CartItemModifierField fieldToSwap = cartItemModifierFieldsOrdered.get(selectedFieldIndex + swapOperation);
		int selectedFieldOrder = selectedField.getOrdering();
		int fieldToSwapOrder = fieldToSwap.getOrdering();
		for (CartItemModifierField cartItemModifierField : getCartItemModifierGroup().getCartItemModifierFields()) {
			if (cartItemModifierField.equals(selectedField)) {
				cartItemModifierField.setOrdering(fieldToSwapOrder);
			}
			if (cartItemModifierField.equals(fieldToSwap)) {
				cartItemModifierField.setOrdering(selectedFieldOrder);
			}
		}
	}

	/**
	 * Helper method to update edit, remove, up and down buttons.
	 */
	private void updateEditRemoveUpDownButtons() {

		int items;

		try {
			items = this.cartItemModifierFieldsTableViewer.getSwtTableViewer().getTable().getItemCount();
		} catch (SWTException e) {
			return;
		}

		if (sel == null || items < 1) { // no table item selected
			this.editFieldBtt.setEnabled(false);
			this.removeFieldBtt.setEnabled(false);
			this.orderUpBtt.setEnabled(false);
			this.orderDownBtt.setEnabled(false);
		} else if (items > 1) {
			CartItemModifierField selectedOpt = (CartItemModifierField) sel.getFirstElement();
			this.editFieldBtt.setEnabled(selectedOpt != null);
			this.removeFieldBtt.setEnabled(selectedOpt != null);
			final int selectedItem = this.cartItemModifierFieldsTableViewer.getSwtTableViewer().getTable().getSelectionIndex() + 1;
			if (selectedItem == 1) {
				if (this.orderUpBtt.isEnabled()) {
					this.orderUpBtt.setEnabled(false);
				}
				this.orderDownBtt.setEnabled(true);
			} else if (selectedItem == items) {
				if (this.orderDownBtt.isEnabled()) {
					this.orderDownBtt.setEnabled(false);
				}
				this.orderUpBtt.setEnabled(true);
			} else if (selectedItem > 1 && selectedItem < items) {
				if (!this.orderUpBtt.isEnabled()) {
					this.orderUpBtt.setEnabled(true);
				}
				if (!this.orderDownBtt.isEnabled()) {
					this.orderDownBtt.setEnabled(true);
				}
			}
		} else if (items == 1) {
			this.orderUpBtt.setEnabled(false);
			this.orderDownBtt.setEnabled(false);
			this.editFieldBtt.setEnabled(true);
			this.removeFieldBtt.setEnabled(true);
		}
	}
}
