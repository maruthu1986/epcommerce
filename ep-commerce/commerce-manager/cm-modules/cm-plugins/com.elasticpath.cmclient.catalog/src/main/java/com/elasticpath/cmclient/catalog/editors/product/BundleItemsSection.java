/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.catalog.editors.product;

import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.catalog.dialogs.product.ProductBundleConstituentsDialog;
import com.elasticpath.cmclient.catalog.editors.sku.ProductSkuEditor;
import com.elasticpath.cmclient.catalog.helpers.BundleConstituentTableContentProvider;
import com.elasticpath.cmclient.catalog.helpers.BundleConstituentTableLabelProvider;
import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.CorePlugin;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.EpValueBinding;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.editors.GuidEditorInput;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.cmclient.core.validation.CompoundValidator;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareEditorPageSectionPart;
import com.elasticpath.cmclient.policy.ui.IPolicyTargetLayoutComposite;
import com.elasticpath.cmclient.policy.ui.PolicyTargetCompositeFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.BundleConstituent;
import com.elasticpath.domain.catalog.ConstituentItem;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.SelectionRule;

/**
 * ProductEditor Constituent Section.
 */
@SuppressWarnings({"PMD.TooManyMethods", "PMD.ExcessiveImports", "PMD.GodClass"})

public class BundleItemsSection extends AbstractPolicyAwareEditorPageSectionPart {

	private static final Logger LOG = Logger.getLogger(BundleItemsSection.class);

	private static final int COLUMN_WIDTH_QTY = 60;

	private static final int COLUMN_WIDTH_PRODUCT_TYPE = 150;

	private static final int COLUMN_WIDTH_PRODUCT_NAME = 140;

	private static final int COLUMN_WIDTH_PRODUCT_CODE = 120;

	private static final int COLUMN_WIDTH_ICON = 32;

	private static final int COLUMN_WIDTH_SKU_CODE = 120;

	private static final int COLUMN_WIDTH_SKU_CONFIGURATION_NAME = 130;

	private static final int TABLE_SIZE = 100;

	private static final String CONSTITUENTS_TABLE = "Constituents"; //$NON-NLS-1$

	private IEpTableViewer productConstituentsTableViewer;

	private IPolicyTargetLayoutComposite controlPane;

	private PolicyActionContainer defaultControls;

	private PolicyActionContainer editControls;

	private Button addButton;

	private Button editButton;

	private Button openButton;

	private Button removeButton;

	private Button moveUpButton;

	private Button moveDownButton;

	private final BundleItemsPage formPage;

	private static final String[] AVAILABILITY_STRINGS_EDITOR_MODE = new String[]{
		CatalogMessages.get().Bundle_Selection_Rule_All,
		CatalogMessages.get().Bundle_Selection_Rule_One,
		CatalogMessages.get().Bundle_Selection_Rule_X
	};

	private Text selectionRuleParam;

	private CCombo selectRuleCombo;

	private Label selectionRuleLabel;

	private EpValueBinding selectionBinding;

	private final EpControlFactory controlFactory = EpControlFactory.getInstance();

	/**
	 * Constructs this section.
	 *
	 * @param constituentsPage the parent form page
	 * @param editor the editor
	 */
	public BundleItemsSection(final BundleItemsPage constituentsPage, final ProductEditor editor) {
		super(constituentsPage, editor, ExpandableComposite.NO_TITLE);

		this.formPage = constituentsPage;
	}

	@Override
	public void refreshLayout() {
		if (!controlPane.getSwtComposite().isDisposed()) {
			controlPane.getSwtComposite().layout();
			refreshButtonsBySelection();
		}
	}

	@Override
	protected void bindControls(final DataBindingContext bindingContext) {
		this.productConstituentsTableViewer.getSwtTableViewer().addSelectionChangedListener(createSelectionChangedListener());
		this.productConstituentsTableViewer.getSwtTableViewer().addDoubleClickListener(createDoubleClickListener());

		this.moveUpButton.addSelectionListener(createMoveUpAction());
		this.moveDownButton.addSelectionListener(createMoveDownAction());
		this.addButton.addSelectionListener(createAddAction());
		this.editButton.addSelectionListener(createEditAction());
		this.openButton.addSelectionListener(createOpenAction());
		this.removeButton.addSelectionListener(createRemoveAction());
		this.selectRuleCombo.addSelectionListener(createSelectionRuleComboListener());
		this.selectionRuleParam.addModifyListener(createSelectionRuleParamListener());
		this.controlPane.setControlModificationListener(getEditor());
	}

	private SelectionRule getSelectionRuleFromRuleInput(final ProductBundle bundle) {
		SelectionRule selectionRule = bundle.getSelectionRule();
		if (selectionRule == null) {
			selectionRule = ServiceLocator.getService(ContextIdNames.BUNDLE_SELECTION_RULE);
		}

		if (selectRuleCombo.getSelectionIndex() > 1) {
			String text = selectionRuleParam.getText();
			if (StringUtils.isEmpty(text) || !StringUtils.isNumeric(text)) {
				selectionRule.setParameter(0);
				return selectionRule;
			}

			int parseInt = Integer.parseInt(text);
			selectionRule.setParameter(parseInt);
		} else {
			selectionRule.setParameter(selectRuleCombo.getSelectionIndex());
		}
		return selectionRule;
	}

	@Override
	protected void createControls(final Composite client, final FormToolkit toolkit) {
		this.defaultControls = addPolicyActionContainer("defaultConstituentsControls"); //$NON-NLS-1$
		this.editControls = addPolicyActionContainer("constituentEditControls"); //$NON-NLS-1$

		this.controlPane = PolicyTargetCompositeFactory.wrapLayoutComposite(CompositeFactory.createGridLayoutComposite(client, 2, false));
		this.controlPane.setLayoutData(createLayoutData(true, true).getSwtLayoutData());

		createSelectionRuleComposite(controlPane);

		this.productConstituentsTableViewer = controlPane.addTableViewer(false, createLayoutData(true, true), defaultControls,
			CONSTITUENTS_TABLE);
		this.productConstituentsTableViewer.addTableColumn("", COLUMN_WIDTH_ICON); //$NON-NLS-1$
		this.productConstituentsTableViewer.addTableColumn(CatalogMessages.get().ProductConstituentsSection_ProductType, COLUMN_WIDTH_PRODUCT_TYPE);
		this.productConstituentsTableViewer.addTableColumn(CatalogMessages.get().ProductConstituentsSection_ProductCode, COLUMN_WIDTH_PRODUCT_CODE);
		this.productConstituentsTableViewer.addTableColumn(CatalogMessages.get().ProductConstituentsSection_ProductName, COLUMN_WIDTH_PRODUCT_NAME);
		this.productConstituentsTableViewer.addTableColumn(CatalogMessages.get().ProductConstituentsSection_SkuCode, COLUMN_WIDTH_SKU_CODE);
		this.productConstituentsTableViewer.addTableColumn(CatalogMessages.get().ProductConstituentsSection_SkuConfiguration,
				COLUMN_WIDTH_SKU_CONFIGURATION_NAME);
		this.productConstituentsTableViewer.addTableColumn(CatalogMessages.get().ProductConstituentsSection_Qty, COLUMN_WIDTH_QTY);

		this.productConstituentsTableViewer.setContentProvider(new BundleConstituentTableContentProvider());
		this.productConstituentsTableViewer.setLabelProvider(new BundleConstituentTableLabelProvider() {
			@Override
			public Locale getSelectedLocale(final Product product) {
				return formPage.getSelectedLocale();
			}
		});

		((GridData) this.productConstituentsTableViewer.getSwtTable().getLayoutData()).heightHint = TABLE_SIZE;

		IEpLayoutData buttonsLayoutData = controlPane.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL, false, true);
		final IPolicyTargetLayoutComposite buttonsComposite = controlPane.addGridLayoutComposite(1, false,
				buttonsLayoutData, defaultControls);

		openButton = buttonsComposite.addPushButton(CatalogMessages.get().ProductEditorContituentSection_OpenButton,
			CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_OPEN),
				controlPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL), editControls);

		buttonsComposite.addEmptyComponent(null, defaultControls);

		editButton = buttonsComposite.addPushButton(CatalogMessages.get().ProductEditorContituentSection_EditButton,
			CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_EDIT),
				controlPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL), editControls);

		addButton = buttonsComposite.addPushButton(CatalogMessages.get().ProductEditorContituentSection_AddButton,
				CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_ADD),
				controlPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL), defaultControls);

		removeButton = buttonsComposite.addPushButton(CatalogMessages.get().ProductEditorContituentSection_RemoveButton,
			CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_REMOVE),
				controlPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL), editControls);

		buttonsComposite.addEmptyComponent(null, defaultControls);

		moveUpButton = buttonsComposite.addPushButton(CatalogMessages.get().ProductEditorContituentSection_MoveUpButton,
			CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_UP_ARROW),
				controlPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL), editControls);

		moveDownButton = buttonsComposite.addPushButton(CatalogMessages.get().ProductEditorContituentSection_MoveDownButton,
			CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_DOWN_ARROW),
				controlPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL), editControls);

	}

	private void createSelectionRuleComposite(final IPolicyTargetLayoutComposite pageComposite) {
		final IEpLayoutData labelData = pageComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL);
		final IEpLayoutData secondLabelData = pageComposite.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.BEGINNING);
		final IEpLayoutData fieldData = pageComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, false, false, 1, 1);
		final IEpLayoutData titleData = pageComposite.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.BEGINNING, false, false, 3, 1);

		IEpLayoutData layoutData = this.controlPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.BEGINNING, true, false, 2, 1);
		final IPolicyTargetLayoutComposite selectionRulesComposite = pageComposite.addGridLayoutComposite(3, false, layoutData, defaultControls);

		// title
		Label titleLabel = controlFactory.createLabelBold(selectionRulesComposite.getSwtComposite(),
			CatalogMessages.get().Bundle_Selection_Rule_Title, SWT.BOLD, titleData.getSwtLayoutData());
		titleLabel.setBackground(selectionRulesComposite.getSwtComposite().getBackground());

		// other rows
		selectionRulesComposite.addLabel(CatalogMessages.get().Bundle_Selection_Rule,	labelData, defaultControls);
		selectRuleCombo = selectionRulesComposite.addComboBox(fieldData, defaultControls);
		selectRuleCombo.setItems(AVAILABILITY_STRINGS_EDITOR_MODE);

		selectionRulesComposite.addLabel(CatalogMessages.get().Bundle_Selection_Rule2, secondLabelData, defaultControls);

		selectionRuleLabel = selectionRulesComposite.addLabel(CatalogMessages.get().Bundle_Selection_Parameter, labelData, defaultControls);
		selectionRuleParam = selectionRulesComposite.addTextField(fieldData, defaultControls);
	}


	/**
	 * Sets dependent order limit components enabled/disabled.
	 *
	 * @param index availability combo selection index
	 */
	private void setSelectionParamBoxEnabled(final int index, final Control ...controls) {

		if (selectionBinding == null) {
			ObservableUpdateValueStrategy strategy = getSelectionUpdateStrategy();
			selectionBinding = EpControlBindingProvider.getInstance().bind(getBindingContext(), selectionRuleParam,
					new CompoundValidator(EpValidatorFactory.POSITIVE_INTEGER, EpValidatorFactory.REQUIRED), null, strategy, true);
		}

		final boolean enabled = (index  > 1);
		for (Control control : controls) {
			control.setEnabled(enabled);
			control.setVisible(enabled);
		}

		getBindingContext().removeBinding(selectionBinding.getBinding());

		if (enabled) {
			getBindingContext().addBinding(selectionBinding.getBinding());
		}
	}

	private ObservableUpdateValueStrategy getSelectionUpdateStrategy() {
		return new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object value) {
				ProductBundle bundle = getProductBundleModel();

				bundle.setSelectionRule(getSelectionRuleFromRuleInput(bundle));
				return Status.OK_STATUS;
			}

		};
	}

	private SelectionListener createOpenAction() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				openNewEditorFor(getSelectedBundleConstituent());
			}
		};
	}

	private SelectionListener createMoveUpAction() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				BundleConstituent selectedBundleConstituent = getSelectedBundleConstituent();
				if (selectedBundleConstituent != null) {
					getProductBundleModel().moveConstituentUp(selectedBundleConstituent);

					refreshTableViewer();
					refreshButtonsBySelection();
				}
			}
		};
	}

	private void refreshButtonsBySelection() {
		// disables up/down buttons
		this.moveUpButton.setEnabled(false);
		this.moveDownButton.setEnabled(false);

		int selectionIndex = this.productConstituentsTableViewer.getSwtTable().getSelectionIndex();
		if (selectionIndex >= 0) {
			this.openButton.setEnabled(true);
		}

		if (isAuthorized() && this.productConstituentsTableViewer.getSwtTable().getItemCount() > 1) {
			int lastItemIndex = this.productConstituentsTableViewer.getSwtTable().getItemCount() - 1;

			if (selectionIndex == 0) {
				this.moveDownButton.setEnabled(true);
			} else if (selectionIndex > 0 && selectionIndex < lastItemIndex) {
				this.moveUpButton.setEnabled(true);
				this.moveDownButton.setEnabled(true);
			} else if (selectionIndex == lastItemIndex) {
				this.moveUpButton.setEnabled(true);
			}
		}
	}

	private boolean isAuthorized() {
		return (getStatePolicy() != null && EpState.EDITABLE.equals(getStatePolicy().determineState(editControls)));
	}

	private SelectionListener createMoveDownAction() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				BundleConstituent selectedBundleConstituent = getSelectedBundleConstituent();
				if (selectedBundleConstituent != null) {
					getProductBundleModel().moveConstituentDown(selectedBundleConstituent);

					refreshTableViewer();
					refreshButtonsBySelection();
				}
			}
		};
	}

	private SelectionAdapter createAddAction() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				BundleConstituent bundleConstituent = ServiceLocator.getService(ContextIdNames.BUNDLE_CONSTITUENT);
				ProductBundleConstituentsDialog dialog = new ProductBundleConstituentsDialog(getShell(), bundleConstituent, false,
						getProductBundleModel());
				if (dialog.open() == Window.OK) {
					getProductBundleModel().addConstituent(bundleConstituent);

					refreshTableViewer();
				}
			}
		};
	}

	private SelectionAdapter createEditAction() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				ProductBundleConstituentsDialog dialog = new ProductBundleConstituentsDialog(getShell(), getSelectedBundleConstituent(), true,
						getProductBundleModel());
				if (dialog.open() == Window.OK) {
					refreshTableViewer();
				}
			}
		};
	}

	private SelectionAdapter createRemoveAction() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				BundleConstituent selectedBundleConstituent = getSelectedBundleConstituent();
				if (selectedBundleConstituent != null) {
					final ConstituentItem item = selectedBundleConstituent.getConstituent();
					String removeMessage =
						NLS.bind(CatalogMessages.get().ProductEditorContituentSection_DialogRemoveMsg,
						new Object[]{item.getCode(), item.getDisplayName(CorePlugin.getDefault().getDefaultLocale())});
					if (MessageDialog.openConfirm(getShell(),
							CatalogMessages.get().ProductEditorContituentSection_DialogRemoveTitle, removeMessage)) {
						getProductBundleModel().removeConstituent(selectedBundleConstituent);
						
						refreshTableViewer();
					}				
				}
			}
		};
	}
	
	private BundleConstituent getSelectedBundleConstituent() {
		IStructuredSelection selection = (IStructuredSelection) productConstituentsTableViewer.getSwtTableViewer().getSelection();
		if (selection == null || selection.isEmpty()) {
			return null;
		}
		return (BundleConstituent) selection.getFirstElement();
	}
	
	private ProductBundle getProductBundleModel() {
		return (ProductBundle) ((ProductEditor) getEditor()).getModel().getProduct();
	}

	private IDoubleClickListener createDoubleClickListener() {
		return event -> {
			if (event.getSelection() != null && !event.getSelection().isEmpty()) {
				openNewEditorFor((BundleConstituent) ((IStructuredSelection) event.getSelection()).getFirstElement());
			}
		};
	}

	private ISelectionChangedListener createSelectionChangedListener() {
		return event -> {
			boolean validSelection = event.getSelection() != null && !event.getSelection().isEmpty();
			if (validSelection) {
				editControls.setPolicyDependent(((IStructuredSelection) event.getSelection()).getFirstElement());
			} else {
				editControls.setPolicyDependent(null);
			}
			applyStatePolicy(getStatePolicy());
			refreshButtonsBySelection();
		};
	}

	private IEpLayoutData createLayoutData(final boolean grabExcessHSpace, final boolean grabExcessVSpace) {
		return controlPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, grabExcessHSpace, grabExcessVSpace);
	}
	
	@Override
	protected void populateControls() {
		productConstituentsTableViewer.setInput(getProductBundleModel());
		SelectionRule selectionRule = getProductBundleModel().getSelectionRule();
		if (selectionRule == null) {
			selectRuleCombo.select(0);
			setSelectionParamBoxEnabled(selectRuleCombo.getSelectionIndex(), selectionRuleParam, selectionRuleLabel);
		} else {
			int parameter = selectionRule.getParameter();
			if (parameter <= 1) {
				selectRuleCombo.select(parameter);
				setSelectionParamBoxEnabled(selectRuleCombo.getSelectionIndex(), selectionRuleParam, selectionRuleLabel);
			} else {
				selectRuleCombo.select(2);
				selectionRuleParam.setText(String.valueOf(parameter));
			}			
		}
	}
	
	@Override
	protected Layout getLayout() {
		return new GridLayout(1, true);
	}
	
	@Override
	protected Object getLayoutData() {
		return new GridData(GridData.FILL, GridData.FILL, true, true);
	}
	
	private Shell getShell() {
		return getEditor().getEditorSite().getShell();
	}

	private void refreshTableViewer() {
		productConstituentsTableViewer.getSwtTableViewer().refresh();
		getEditor().controlModified();
	}

	private void openNewEditorFor(final BundleConstituent constituent) {
		if (constituent != null) {
			ConstituentItem constituentItem = constituent.getConstituent();
			if (constituentItem.isProductSku()) {
				GuidEditorInput guidEditorInput = new GuidEditorInput(constituentItem.getProductSku().getGuid(), ProductSku.class);
				try {
					getEditor().getSite().getPage().openEditor(guidEditorInput, ProductSkuEditor.PART_ID);
				} catch (final PartInitException exception) {
					LOG.error("Error opening the SKU Editor", exception);  //$NON-NLS-1$
				}
			} else {
				GuidEditorInput guidEditorInput = new GuidEditorInput(constituentItem.getCode(), Product.class);
				try {
					getEditor().getSite().getPage().openEditor(guidEditorInput, ProductEditor.PART_ID);
				} catch (final PartInitException exception) {
					LOG.error("Error opening the Product Editor", exception);  //$NON-NLS-1$
				}
			}
		}
	}
	
	private SelectionListener createSelectionRuleComboListener() {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				updateSelectionRule();
			}
			
			@Override
			public void widgetDefaultSelected(final SelectionEvent event) {
				widgetSelected(event);
			}
		};
	}
	
	private ModifyListener createSelectionRuleParamListener() {
		return (ModifyListener) arg0 -> updateSelectionRule();
	}
	
	private void updateSelectionRule() {
		setSelectionParamBoxEnabled(selectRuleCombo.getSelectionIndex(), selectionRuleParam, selectionRuleLabel);
		ProductBundle bundle = getProductBundleModel();
		bundle.setSelectionRule(getSelectionRuleFromRuleInput(bundle));
		getEditor().controlModified();
	}
}
