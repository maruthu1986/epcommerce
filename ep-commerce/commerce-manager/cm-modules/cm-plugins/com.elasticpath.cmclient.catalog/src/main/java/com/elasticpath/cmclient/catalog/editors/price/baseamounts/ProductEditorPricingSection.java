/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.catalog.editors.price.baseamounts;

import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.catalog.editors.model.ProductModelController;
import com.elasticpath.cmclient.core.dto.catalog.AbstractProductModel;
import com.elasticpath.cmclient.core.dto.catalog.PriceListEditorModel;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.service.CatalogEventService;
import com.elasticpath.cmclient.core.ui.TableSelectionProvider;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareEditorPage;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareEditorPageSectionPart;
import com.elasticpath.cmclient.policy.ui.IPolicyTargetLayoutComposite;
import com.elasticpath.cmclient.policy.ui.PolicyTargetCompositeFactory;
import com.elasticpath.cmclient.pricelistmanager.controller.PriceListEditorController;
import com.elasticpath.cmclient.pricelistmanager.editors.baseamountsection.BaseAmountSection;
import com.elasticpath.cmclient.pricelistmanager.editors.baseamountsection.BaseAmountTableProperties;
import com.elasticpath.cmclient.pricelistmanager.editors.baseamountsection.DefaultBaseAmountTableProperties;
import com.elasticpath.cmclient.pricelistmanager.model.impl.BaseAmountType;
import com.elasticpath.common.dto.ChangeSetObjects;
import com.elasticpath.common.dto.pricing.BaseAmountDTO;
import com.elasticpath.common.dto.pricing.PriceListDescriptorDTO;
import com.elasticpath.common.pricing.service.BaseAmountFilter;

/**
 * Class represents Product pricing part of the page with three controls.
 * - product name label
 * - drop-down combo widget of price list descriptors
 * - table with base amounts for selected price list descriptor.
 */
public class ProductEditorPricingSection extends AbstractPolicyAwareEditorPageSectionPart {

	private BaseAmountSection control;
	private final AbstractCmClientFormEditor editor;
	private static final int COLUMNS = 2;
	private final PriceListEditorController controller;
	private final AbstractProductModel productModel;
	private PriceListDropDownPart dropDownPart;
	private SelectionListener selectionListener;
	private final PolicyActionContainer editorContainer;
	private final TableSelectionProvider baseAmountTableSelectionProvider;
	private PricingSectionState pricingSectionState;
	private final AbstractPolicyAwareEditorPage page;

	/**
	 * Constructor.
	 *
	 * @param formPage                         - page for the form location.
	 * @param editor                           - editor for this section
	 * @param editorContainer                  - policy container
	 * @param baseAmountTableSelectionProvider - selection provider.
	 * @param pricingSectionState              - state of the pricing section
	 * @param locale                           locale
	 */
	public ProductEditorPricingSection(final AbstractPolicyAwareEditorPage formPage, final AbstractCmClientFormEditor editor,
									   final PolicyActionContainer editorContainer, final TableSelectionProvider baseAmountTableSelectionProvider,
									   final PricingSectionState pricingSectionState, final Locale locale) {
		super(formPage, editor, ExpandableComposite.EXPANDED);
		this.page = formPage;
		this.editor = editor;
		this.editorContainer = editorContainer;
		this.baseAmountTableSelectionProvider = baseAmountTableSelectionProvider;
		String plGuid = StringUtils.EMPTY;
		if (pricingSectionState != null) {
			plGuid = pricingSectionState.getSelectedPL();
			this.pricingSectionState = pricingSectionState;
		}
		this.controller = new ProductEditorPriceListEditorControllerImpl(plGuid);
		controller.setCurrentLocale(locale);
		productModel = (AbstractProductModel) getModel();
	}

	@Override
	protected void bindControls(final DataBindingContext bindingContext) {
		control.bindControls(bindingContext);
	}

	@Override
	protected void createControls(final Composite client, final FormToolkit toolkit) {
		IEpLayoutComposite mainComposite = CompositeFactory.createGridLayoutComposite(client, COLUMNS, false);
		IPolicyTargetLayoutComposite mainPartComposite = PolicyTargetCompositeFactory.wrapLayoutComposite(mainComposite);

		BaseAmountFilter filter = controller.getBaseAmountsFilter();
		if (productModel.getModelType().equals(ProductModelController.PRODUCT_TYPE)) {
			filter.setObjectType(BaseAmountType.PRODUCT.getType());
			filter.setObjectGuid(productModel.getObjectGuid());
		} else {
			filter.setObjectType(BaseAmountType.SKU.getType());
			filter.setObjectGuid(productModel.getProductSku().getSkuCode());
		}

		selectionListener = handleDropDownSelectionChanged(client);

		dropDownPart = new PriceListDropDownPart(controller, productModel, selectionListener, page.getSelectedLocale());
		dropDownPart.createControls(mainComposite, null);

		BaseAmountTableProperties baseAmountProperties = new DefaultBaseAmountTableProperties();
		baseAmountProperties.setNameWidth(0); // On price Editor hiding Name column
		baseAmountProperties.setAddButtonCaption(CatalogMessages.get().ProductEditorSection_AddPriceTierButton);
		baseAmountProperties.setDeleteButtonCaption(CatalogMessages.get().ProductEditorSection_RemovePriceTierButton);
		baseAmountProperties.setEditButtonCaption(CatalogMessages.get().ProductEditorSection_EditPriceTierButton);

		control = new ProductEditorBaseAmountSection(editor, controller, baseAmountTableSelectionProvider, productModel, baseAmountProperties);
		control.createControls(mainPartComposite.getLayoutComposite(), null);
		CatalogEventService.getInstance().addProductListener(control);
		CatalogEventService.getInstance().addProductSkuListener(control);
		// delegate the call to apply the policy when needed
		editorContainer.addTarget(state -> control.reApplyStatePolicy());

	}

	/**
	 * Performs model save operation.
	 */
	public void saveModel() {
		controller.saveModel();
	}

	@Override
	protected void populateControls() {
		dropDownPart.populateControls();
	}

	/**
	 * Returns of the section controls state.
	 *
	 * @return section controls state
	 */
	public PricingSectionState getState() {
		PriceListEditorModel model = controller.getModel();
		String currentGuid = model.getPriceListDescriptor().getGuid();
		pricingSectionState.setSelectedPL(currentGuid);
		return pricingSectionState;
	}

	@Override
	protected Object getLayoutData() {
		return new GridData(GridData.FILL, GridData.FILL, true, true);
	}

	@Override
	protected Layout getLayout() {
		return new GridLayout(1, false);
	}


	private SelectionListener handleDropDownSelectionChanged(final Composite client) {
		return new SelectionListener() {

			/**
			 * Logic of drop-down handling extracted to let {@link PriceListDropDownPart} be reusable and logic-independent
			 */
			@Override
			public void widgetSelected(final SelectionEvent selectionEvent) {
				ChangeSetObjects<BaseAmountDTO> baseAmountChangeSet = controller.getModel().getChangeSet();
				boolean changesExist = !baseAmountChangeSet.getAdditionList().isEmpty()
						|| !baseAmountChangeSet.getUpdateList().isEmpty() || !baseAmountChangeSet.getRemovalList().isEmpty();
				if (!changesExist) {
					refresh(selectionEvent.text);
					return;
				}

				CCombo combo = (CCombo) selectionEvent.widget;
				int selected = combo.getSelectionIndex();
				PriceListDescriptorDTO dto = controller.getModel().getPriceListDescriptor();

				/**   if changes were made while looking at the current price list selection and
				 * 	the selection is changed the user should be prompted to save:
				 *		"Price List Name" price list has been modified. Save changes?
				 *		Yes No Cancel
				 **/

				int style = SWT.ICON_WARNING | SWT.YES | SWT.NO | SWT.CANCEL;
				MessageBox messageBox = new MessageBox(client.getShell(), style);
				messageBox.setMessage(
					NLS.bind(CatalogMessages.get().ProductPricePage_PriceListSelectionConfirmation,
					dto.getName()));
				int result = messageBox.open();

				switch (result) {
					case SWT.YES:
						//When changes saved - need to put editor into original state
						pricingSectionState.setSelectedPL(selectionEvent.text);
						saveModel();
						refresh(selectionEvent.text);
						combo.select(selected);
						break;
					case SWT.CANCEL:
						//Staying on the same page and do not go anywhere
						((CCombo) selectionEvent.widget).select(selectionEvent.detail);
						return;
					case SWT.NO:
						//Switching to the new price list without save
						refresh(selectionEvent.text);
						break;
					default:
				}
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {
				//
			}


			private void refresh(final String guid) {
				controller.setPriceListDescriptorGuid(guid);
				controller.reloadModel();
				control.refreshTableViewer();
				control.reApplyStatePolicy();
			}
		};
	}

	public PriceListEditorController getController() {
		return controller;
	}

}
