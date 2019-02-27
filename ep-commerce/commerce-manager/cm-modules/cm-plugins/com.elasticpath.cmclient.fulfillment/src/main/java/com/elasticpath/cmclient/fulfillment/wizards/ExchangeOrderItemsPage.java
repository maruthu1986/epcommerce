/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.fulfillment.wizards;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.PageChangingEvent;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import com.elasticpath.cmclient.core.CmClientResources;
import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.LoginManager;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.EpWizardPageSupport;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.helpers.LocalProductSkuLookup;
import com.elasticpath.cmclient.core.helpers.PriceHelper;
import com.elasticpath.cmclient.core.ui.dialog.SkuFinderDialog;
import com.elasticpath.cmclient.core.ui.framework.AbstractCmClientFormSectionPart;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.ui.framework.IEpTableColumn;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.cmclient.core.validation.CompoundValidator;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.cmclient.core.wizard.AbstractEPWizardPage;
import com.elasticpath.cmclient.fulfillment.FulfillmentImageRegistry;
import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;
import com.elasticpath.cmclient.fulfillment.FulfillmentPlugin;
import com.elasticpath.cmclient.fulfillment.ProductSkuChecker;
import com.elasticpath.cmclient.fulfillment.editors.order.dialog.InlinePriceEditingSupport;
import com.elasticpath.common.dto.pricing.BaseAmountDTO;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.customer.CustomerAddress;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderAddress;
import com.elasticpath.domain.order.OrderReturn;
import com.elasticpath.domain.order.OrderReturnSku;
import com.elasticpath.domain.shoppingcart.ExchangeItem;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartPricingSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingCartTaxSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;
import com.elasticpath.domain.tax.TaxJurisdiction;
import com.elasticpath.money.Money;
import com.elasticpath.service.catalog.ProductSkuLookup;
import com.elasticpath.service.order.ReturnAndExchangeService;
import com.elasticpath.service.shoppingcart.PricingSnapshotService;
import com.elasticpath.service.shoppingcart.TaxSnapshotService;
import com.elasticpath.service.tax.TaxCalculationResult;
import com.elasticpath.service.tax.TaxJurisdictionService;
import com.elasticpath.service.tax.adapter.TaxAddressAdapter;
import com.elasticpath.shipping.connectivity.dto.ShippingOption;
import com.elasticpath.service.shipping.ShippingOptionService;

/**
 * Order new items exchange wizard page.
 */
@SuppressWarnings({"PMD.TooManyFields", "PMD.ExcessiveImports", "PMD.ExcessiveClassLength", "PMD.TooManyMethods",
	"PMD.GodClass", "PMD.PrematureDeclaration" })
public class ExchangeOrderItemsPage extends AbstractEPWizardPage<OrderReturn> {

	/** The logger. */
	private static final Logger LOG = Logger.getLogger(AbstractEPWizardPage.class);

	private static final int CONTENT_HEIGHT_HINT = 400;

	/** Current getOrder(). */
	// private final Order order;
	/** Ordered Items IEpTableViewer. */
	private IEpTableViewer orderedItemsTableViewer;

	/** List of cart items to getOrder(). */
	private final List<ExchangeItem> cartItems;

	private ShoppingCart shoppingCart;

	/** Address to deliver order exchange. */
	private OrderAddress shippingAddress;

	private ShippingOption shippingOption;

	private BigDecimal shippingCost;

	private BigDecimal shipmentDiscount;

	private final ReturnAndExchangeService orderExchangeService;
	private ProductSkuLookup productSkuLookup;

	private ShippingInfoSectionPart shippingInfoSection;

	private SummarySectionPart summarySection;

	private OrderedItemsSectionPart orderedItemsSection;

	private IManagedForm managedForm;

	private final TaxJurisdictionService taxJurisdictionService;

	private boolean isConverted;

	private PricingSnapshotService pricingSnapshotService;

	private TaxSnapshotService taxSnapshotService;

	/**
	 * OrderPopulateStep defines step of modifications that should be performed.
	 */
	private enum OrderPopulateStep {
		SHIPPING_ADDRESS_MODIFIED, SHIPPING_METHOD_MODIFIED, SHIPPING_COST_MODIFIED, SHIPMENT_DISCOUNT_MODIFIED, SHOPPING_CART_MODIFIED
	}

	/**
	 * The constructor.
	 *
	 * @param pageName the page name
	 * @param message the message for this page
	 */
	protected ExchangeOrderItemsPage(final String pageName, final String message) {
		super(1, true, pageName, new DataBindingContext());
		this.cartItems = new ArrayList<>();
		orderExchangeService = ServiceLocator.getService(ContextIdNames.ORDER_RETURN_SERVICE);
		taxJurisdictionService = ServiceLocator.getService(ContextIdNames.TAX_JURISDICTION_SERVICE);
		setMessage(message);
	}

	@Override
	protected void bindControls() {
		// nothing to bind here.
		EpWizardPageSupport.create(this, getDataBindingContext());
	}

	@Override
	protected void populateControls() {
		// nothing to populate here.
	}

	@Override
	protected void createEpPageContent(final IEpLayoutComposite parent) {
		IEpLayoutComposite composite = parent.addTableWrapLayoutComposite(1, true, parent.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL,
				true, true));
		TableWrapLayout tableWrapLayout = new TableWrapLayout();
		TableWrapData tableWrapData = new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.FILL_GRAB);
		tableWrapData.heightHint = CONTENT_HEIGHT_HINT;

		managedForm = EpControlFactory.getInstance().createManagedForm(composite.getSwtComposite());
		managedForm.getForm().getBody().setLayout(tableWrapLayout);
		managedForm.getForm().setLayoutData(tableWrapData);
		orderedItemsSection = new OrderedItemsSectionPart(managedForm.getForm().getBody(), managedForm.getToolkit(), getDataBindingContext());
		managedForm.addPart(orderedItemsSection);
		shippingInfoSection = new ShippingInfoSectionPart(managedForm.getForm().getBody(), managedForm.getToolkit(), getDataBindingContext());
		managedForm.addPart(shippingInfoSection);


		OrderReturn orderReturn = getModel();

		summarySection = new SummarySectionPart(managedForm.getForm().getBody(), managedForm.getToolkit(),
				getDataBindingContext(), orderReturn.isInclusiveTax());
		managedForm.addPart(summarySection);
		this.setControl(parent.getSwtComposite());
	}

	private boolean isInclusiveTax(final Address address) {
		if (address != null) {
			TaxAddressAdapter addressAdapter = new TaxAddressAdapter();

			TaxJurisdiction taxJurisdiction =
					this.taxJurisdictionService.retrieveEnabledInStoreTaxJurisdiction(getOrder().getStore().getCode(),
							addressAdapter.toTaxAddress(address));
			if (null != taxJurisdiction) {

				return taxJurisdiction.getPriceCalculationMethod().equals(TaxJurisdiction.PRICE_CALCULATION_INCLUSIVE);
			}
		}
		return false;
	}

	/**
	 * Refresh cart items list view.
	 */
	private void refreshCartItems() {
		orderedItemsTableViewer.getSwtTableViewer().refresh();
		orderedItemsSection.getSection().pack(true);
	}

	/**
	 * Get selected Cart Item.
	 *
	 * @return the selected cart item
	 */
	private ShoppingItem getSelectedCartItem() {
		IStructuredSelection selection = (IStructuredSelection) orderedItemsTableViewer.getSwtTableViewer().getSelection();
		ShoppingItem selectedCartItem = null;
		if (selection.isEmpty()) {
			return null;
		}
		selectedCartItem = (ShoppingItem) selection.getFirstElement();
		return selectedCartItem;
	}

	private boolean isAbleToPopulate() {
		return shippingInfoSection.isShippingMethodSelected() && !cartItems.isEmpty();
	}

	private void populateShoppingCart(final OrderPopulateStep step) {
		if (isAbleToPopulate()) {
			int sizeBeforePopulating = cartItems.size();
			switch (step) {
				case SHIPPING_METHOD_MODIFIED:
					shoppingCart = orderExchangeService.populateShoppingCart(getModel(), cartItems, shippingOption,
							shippingAddress);
					break;
				case SHIPPING_ADDRESS_MODIFIED:

					break;
				case SHIPMENT_DISCOUNT_MODIFIED:
					shoppingCart = orderExchangeService.populateShoppingCart(getModel(), cartItems, shippingOption,
							shippingCost, shipmentDiscount, shippingAddress);
					break;
				case SHIPPING_COST_MODIFIED:
					shoppingCart = orderExchangeService.populateShoppingCart(getModel(), cartItems, shippingOption,
							shippingCost, shipmentDiscount, shippingAddress);
					break;
				case SHOPPING_CART_MODIFIED:
					shoppingCart = orderExchangeService.populateShoppingCart(getModel(), cartItems, shippingOption,
							shippingAddress);
					break;
				default:
					break;
			}

			shoppingCart.setCmUserUID(LoginManager.getCmUserId());
			final ShoppingCartPricingSnapshot pricingSnapshot = getPricingSnapshotService().getPricingSnapshotForCart(shoppingCart);
			final ShoppingCartTaxSnapshot taxSnapshot = getTaxSnapshotService().getTaxSnapshotForCart(shoppingCart, pricingSnapshot);
			getModel().setExchangeShoppingCart(shoppingCart, taxSnapshot);
			getModel().setExchangeCustomerSession(shoppingCart.getCustomerSession());

			LOG.debug(sizeBeforePopulating + "   " + shoppingCart.getRootShoppingItems().size()); //$NON-NLS-1$
			// --- workaround decision of bug when CartItem was removed from shopping cart(if it unavailable)
			if (shoppingCart.getRootShoppingItems().size() != sizeBeforePopulating) {
				MessageDialog.openInformation(this.getShell(), FulfillmentMessages.get().ExchangeWizard_ItemsUnavailableTitle,
						FulfillmentMessages.get().ExchangeWizard_ItemsUnavailableText);
				orderedItemsTableViewer.setInput(shoppingCart.getRootShoppingItems());
				refreshCartItems();
			}
			if (!shoppingCart.getRootShoppingItems().isEmpty()) {
				summarySection.updatePrices();
				//refresh the controls
				summarySection.getParent().layout(true, true);
				setErrorMessage(null);
			}
			// ---
		} else {
			LOG.debug("can't populate"); //$NON-NLS-1$
			summarySection.populateControls();
		}
	}

	/**
	 * This section contains Cart Items to be ordered.
	 */
	@SuppressWarnings("PMD.CyclomaticComplexity")
	class OrderedItemsSectionPart extends AbstractCmClientFormSectionPart {

		private static final int ORDER_SKU_TABLE_HEIGHT = 100;

		private static final int COLUMN_WIDTH_IMAGE = 20;

		private static final int COLUMN_WIDTH_SKU_CODE = 80;

		private static final int COLUMN_WIDTH_PRODUCT_NAME = 120;

		private static final int COLUMN_WIDTH_SKU_OPTIONS = 80;

		private static final int COLUMN_WIDTH_ORDER_QUANTITY = 80;

		private static final int COLUMN_WIDTH_UNIT_PRICE = 120;
		private static final String EXCHANGE_ORDER_ITEMS_TABLE = "Exchange Order Items Table"; //$NON-NLS-1$

		private Button createItemButton;

		private Button deleteItemButton;

		private Action createItemAction;

		private Action deleteItemAction;

		/**
		 * Constructor.
		 *
		 * @param parent the parent form
		 * @param toolkit the form toolkit
		 * @param dataBindingContext the form databinding context
		 */
		OrderedItemsSectionPart(final Composite parent, final FormToolkit toolkit, final DataBindingContext dataBindingContext) {
			super(parent, toolkit, dataBindingContext, ExpandableComposite.TITLE_BAR);
		}

		@Override
		protected void createControls(final Composite client, final FormToolkit toolkit) {
			IEpLayoutComposite tablePane = CompositeFactory.createTableWrapLayoutComposite(orderedItemsSection.getSection(), 2, false);
			tablePane.setLayoutData(new TableWrapData(TableWrapData.FILL, TableWrapData.FILL));
			createOrderSkuTableControl(tablePane);
			createTableButtons(tablePane);

			orderedItemsTableViewer.setContentProvider(new ArrayContentProvider());
			orderedItemsSection.getSection().setClient(tablePane.getSwtComposite());
		}

		private void createOrderSkuTableControl(final IEpLayoutComposite mainPane) {
			final IEpLayoutData tableData = mainPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, false, false);
			orderedItemsTableViewer = mainPane.addTableViewer(false, EpState.EDITABLE, tableData, EXCHANGE_ORDER_ITEMS_TABLE);
			TableWrapData layout = new TableWrapData();
			layout.maxHeight = ORDER_SKU_TABLE_HEIGHT;
			orderedItemsTableViewer.getSwtTable().setLayoutData(layout);
			initializeTableViewer();
		}

		private IEpLayoutData createTableButtons(final IEpLayoutComposite tablePane) {
			final IEpLayoutData buttonPaneData = tablePane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL);
			final IEpLayoutComposite buttonsPane = tablePane.addTableWrapLayoutComposite(1, true, buttonPaneData);
			final IEpLayoutData buttonData = tablePane.createLayoutData();
			createItemButton = addPushButton(buttonsPane, buttonData, FulfillmentMessages.get().ExchangeWizard_AddItem_Button,
					CoreImageRegistry.IMAGE_ADD, true);
			deleteItemButton = addPushButton(buttonsPane, buttonData, FulfillmentMessages.get().ExchangeWizard_RemoveItem_Button,
					CoreImageRegistry.IMAGE_REMOVE, false);
			createItemAction = new AddCartItemAction();
			deleteItemAction = new RemoveCartItemAction();
			addListeners();
			return buttonData;
		}

		/*
		 * This method initialize table viewer.
		 */
		@SuppressWarnings({ "PMD.ExcessiveMethodLength", "PMD.CyclomaticComplexity" })
		private void initializeTableViewer() {
			final IEpTableColumn skuImage = orderedItemsTableViewer.addTableColumn("", //$NON-NLS-1$
					COLUMN_WIDTH_IMAGE);
			skuImage.setLabelProvider(new ColumnLabelProvider() {
				@Override
				public Image getImage(final Object element) {
					return FulfillmentImageRegistry.getImage(FulfillmentImageRegistry.ICON_ORDERTABLE_ITEM);
				}
			});
			final IEpTableColumn skuCodeColumn = orderedItemsTableViewer.addTableColumn(FulfillmentMessages.get().ExchangeWizard_SKUCode_Column,
					COLUMN_WIDTH_SKU_CODE);
			skuCodeColumn.setLabelProvider(new ColumnLabelProvider() {
				@Override
				public String getText(final Object element) {
					final ShoppingItem cartItem = (ShoppingItem) element;
					return getProductSku(cartItem).getSkuCode();
				}

			});
			final IEpTableColumn productNameColumn = orderedItemsTableViewer.
					addTableColumn(FulfillmentMessages.get().ExchangeWizard_ProductName_Column, COLUMN_WIDTH_PRODUCT_NAME);
			productNameColumn.setLabelProvider(new ColumnLabelProvider() {
				@Override
				public String getText(final Object element) {
					final ShoppingItem cartItem = (ShoppingItem) element;
					final ProductSku productSku = getProductSkuLookup().findByGuid(cartItem.getSkuGuid());
					return productSku.getProduct().getDisplayName(getOrder().getLocale());
				}
			});
			final IEpTableColumn skuOptionsColumn = orderedItemsTableViewer.addTableColumn(FulfillmentMessages.get().ExchangeWizard_SKUOptions_Column,
					COLUMN_WIDTH_SKU_OPTIONS);
			skuOptionsColumn.setLabelProvider(new ColumnLabelProvider() {
				@Override
				public String getText(final Object element) {
					final ShoppingItem cartItem = (ShoppingItem) element;
					return getItemOptions(cartItem);
				}

				private String getItemOptions(final ShoppingItem item) {
					StringBuilder builder = new StringBuilder();
					for (SkuOptionValue option : getProductSku(item).getOptionValues()) {
						builder.append(option.getDisplayName(getOrder().getLocale(), true));
						builder.append(FulfillmentMessages.SPACE);
					}
					return builder.toString();
				}
			});
			final IEpTableColumn skuQuantityColumn = orderedItemsTableViewer.addTableColumn(FulfillmentMessages.get().ExchangeWizard_OrderQty_Column,
					COLUMN_WIDTH_ORDER_QUANTITY);
			skuQuantityColumn.setLabelProvider(new ColumnLabelProvider() {
				@Override
				public Image getImage(final Object element) {
					return CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_EDIT);
				}

				@Override
				public String getText(final Object element) {
					final ShoppingItem cartItem = (ShoppingItem) element;
					return String.valueOf(cartItem.getQuantity());
				}
			});
			final IEpTableColumn skuPriceColumn = orderedItemsTableViewer.addTableColumn(FulfillmentMessages.get().ExchangeWizard_UnitPrice_Column,
					COLUMN_WIDTH_UNIT_PRICE);
			skuPriceColumn.setLabelProvider(new ColumnLabelProvider() {
				@Override
				public Image getImage(final Object element) {
					return CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_EDIT);
				}

				@Override
				public String getText(final Object element) {
					final ExchangeItem shoppingItem = (ExchangeItem) element;
					if (shoppingItem.getSaleUnitPrice() != null) {
						return shoppingItem.getSaleUnitPrice().getAmount().toString();
					}
					if (shoppingItem.getListUnitPrice() != null) {
						return shoppingItem.getListUnitPrice().getAmount().toString();
					}
					return StringUtils.EMPTY;
				}
			});
			skuQuantityColumn.setEditingSupport(new AbstractEditingSupport(orderedItemsTableViewer.getSwtTableViewer(), new TextCellEditor(
					orderedItemsTableViewer.getSwtTable())) {
				@Override
				protected Object getValue(final Object element) {
					return String.valueOf(((ShoppingItem) element).getQuantity());
				}

				@Override
				protected void doSetValue(final Object element, final Object value) {
					ExchangeItem item = (ExchangeItem) element;
					try {
						Integer orderQuantity = Integer.valueOf(value.toString());
						if (EpValidatorFactory.POSITIVE_INTEGER.validate(orderQuantity) == ValidationStatus.ok()
								&& orderQuantity >= getProductSku(item).getProduct().getMinOrderQty()) {
							item.setPrice(orderQuantity, item.getPrice());
							populateShoppingCart(OrderPopulateStep.SHOPPING_CART_MODIFIED);
						}
					} catch (NumberFormatException exception) {
						LOG.debug("wrong format"); //$NON-NLS-1$
					}
				}
			});

			skuPriceColumn.setEditingSupport(new InlinePriceEditingSupport(orderedItemsTableViewer, getModel().getOrderShipmentForReturn(),
					new UpdatePriceFromEditorAction(ExchangeOrderItemsPage.this), getDataBindingContext()));
		}

		/**
		 * Updates the prices in the model after editing.
		 */
		protected class UpdatePriceFromEditorAction extends Action {
			private final ExchangeOrderItemsPage page;

			/**
			 * @param page this page
			 */
			public UpdatePriceFromEditorAction(final ExchangeOrderItemsPage page) {
				this.page = page;
			}

			@Override
			public void run() {
				page.populateShoppingCart(OrderPopulateStep.SHOPPING_CART_MODIFIED);
			}
		}

		private Button addPushButton(final IEpLayoutComposite composite, final IEpLayoutData layout, final String text,
				final ImageDescriptor descriptor, final boolean enabled) {
			Button taxButton = composite.addPushButton("", EpState.EDITABLE, layout); //$NON-NLS-1$
			taxButton.setImage(CoreImageRegistry.getImage(descriptor));
			taxButton.setText(text);
			taxButton.setEnabled(enabled);
			return taxButton;
		}

		private void addListeners() {
			orderedItemsTableViewer.getSwtTableViewer().addSelectionChangedListener(event -> {
				final IStructuredSelection strSelection = (IStructuredSelection) event.getSelection();
				Object firstSelection = strSelection.getFirstElement();
				deleteItemButton.setEnabled(firstSelection != null);
			});
			createItemButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(final SelectionEvent event) {
					createItemAction.run();
				}
			});
			deleteItemButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(final SelectionEvent event) {
					deleteItemAction.run();
				}
			});
		}

		@Override
		protected void populateControls() {
			orderedItemsTableViewer.setInput(cartItems);
		}

		@Override
		protected String getSectionTitle() {
			return FulfillmentMessages.get().ExchangeWizard_ItemsToBeOrdered_Section;
		}

		@Override
		protected void bindControls(final DataBindingContext bindingContext) {
			// nothing to bound here.
		}

		private boolean validate() {
			if (cartItems.isEmpty()) {
				setErrorMessage(FulfillmentMessages.get().ExchangeWizard_NoSkusToExchnage_Message);
				return false;
			}
			for (ExchangeItem orderSku : cartItems) {
				if (orderSku.getListUnitPrice() == null && orderSku.getSaleUnitPrice() == null) {
					setErrorMessage(
							NLS.bind(FulfillmentMessages.get().Exchange_AddingItem_Message_NoPrice,
									new Object[]{getProductSku(orderSku).getSkuCode()}));
					return false;
				}
			}
			return true;
		}

		/**
		 * Abstract class for Editing Support.
		 */
		protected abstract class AbstractEditingSupport extends EditingSupport {

			private final CellEditor editor;

			/**
			 * The constructor.
			 *
			 * @param viewer a new viewer
			 * @param cellEditor <code>CellEditor</code> implementation: such as <code>TextCellEditor</code>, <code>ComboBoxCellEditor</code>
			 *            and <code>CheckboxCellEditor</code>.
			 */
			public AbstractEditingSupport(final TableViewer viewer, final CellEditor cellEditor) {
				super(viewer);
				this.editor = cellEditor;
			}

			@Override
			protected boolean canEdit(final Object element) {
				return true;
			}

			@Override
			protected CellEditor getCellEditor(final Object element) {
				return editor;
			}

			@Override
			protected void setValue(final Object element, final Object value) {
				doSetValue(element, value);
				getViewer().update(element, null);
			}

			/**
			 * This method should perform some logic when element will be changed.
			 *
			 * @param element the model element
			 * @param value the new value
			 */
			protected abstract void doSetValue(final Object element, final Object value);
		}
	}

	/**
	 * This section contains information about shipping.
	 */
	class ShippingInfoSectionPart extends AbstractCmClientFormSectionPart {

		private static final String COMMA = ", "; //$NON-NLS-1$

		private static final String WHITE_SPACE = " "; //$NON-NLS-1$

		/** Shipping address of the customer associated with current getOrder(). */
		private CCombo addressCombo;

		/** Shipping type for delivering order exchange to the customer. */
		private CCombo shippingMethodCombo;

		private final String defaultAddressPrompt = FulfillmentMessages.get().ExchangeWizard_SelectAddress_Combo;

		private Label shippingMethodLabel;

		private final String defaultMethodPrompt = FulfillmentMessages.get().ExchangeWizard_SelectShippingMethod_Combo;

		/**
		 * The Constructor.
		 *
		 * @param parent the parent form
		 * @param toolkit the form toolkit
		 * @param dataBindingContext the form databinding context
		 */
		ShippingInfoSectionPart(final Composite parent, final FormToolkit toolkit, final DataBindingContext dataBindingContext) {
			super(parent, toolkit, dataBindingContext, ExpandableComposite.TITLE_BAR);
		}

		@Override
		protected void createControls(final Composite parent, final FormToolkit toolkit) {
			final IEpLayoutComposite controlPane = CompositeFactory.createGridLayoutComposite(parent, 1, false);
			final IEpLayoutData fieldData = controlPane.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.FILL, true, true);
			controlPane.addLabelBold(FulfillmentMessages.get().ExchangeWizard_ShippingAddress_Label, fieldData);
			addressCombo = controlPane.addComboBox(EpState.EDITABLE, fieldData);

			shippingMethodLabel = controlPane.addLabelBold(FulfillmentMessages.get().ExchangeWizard_ShippingMethod_Label, fieldData);
			shippingMethodLabel.setEnabled(false);

			shippingMethodCombo = controlPane.addComboBox(EpState.READ_ONLY, fieldData);
			addListeners();
		}

		@Override
		protected void populateControls() {
			List<CustomerAddress> adressList = getOrder().getCustomer().getAddresses();
			addressCombo.add(defaultAddressPrompt);
			addressCombo.select(0);
			for (CustomerAddress customerAddress : adressList) {
				OrderAddress orderAddress = ServiceLocator.getService(ContextIdNames.ORDER_ADDRESS);
				orderAddress.init(customerAddress);
				String fullAddress = getFullAddress(orderAddress);
				addressCombo.setData(fullAddress, orderAddress);
				addressCombo.add(fullAddress);
			}

			// see shipping method population in updateMethod
			shippingMethodCombo.add(defaultMethodPrompt);
			shippingMethodCombo.select(0);
		}

		private String getFullAddress(final Address customerAddress) {
			return customerAddress.getFirstName()
					+ WHITE_SPACE
					+ customerAddress.getLastName()
					+ COMMA
					+ customerAddress.getZipOrPostalCode()
					+ COMMA
					+ customerAddress.getCity();
		}

		@Override
		protected String getSectionTitle() {
			return FulfillmentMessages.get().ExchangeWizard_ShippingInformation_Section;
		}

		@Override
		protected void bindControls(final DataBindingContext bindingContext) {
			final boolean hideDecorationOnFirstValidation = true;
			final EpControlBindingProvider binder = EpControlBindingProvider.getInstance();
			binder.bind(bindingContext, addressCombo, EpValidatorFactory.REQUIRED, null, new ObservableUpdateValueStrategy() {
				@Override
				protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
					return Status.OK_STATUS;
				}
			}, hideDecorationOnFirstValidation);
			binder.bind(bindingContext, shippingMethodCombo, EpValidatorFactory.REQUIRED, null, new ObservableUpdateValueStrategy() {
				@Override
				protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
					return Status.OK_STATUS;
				}
			}, hideDecorationOnFirstValidation);

		}

		private void addListeners() {
			addressCombo.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(final SelectionEvent event) {
					shippingAddress = (OrderAddress) addressCombo.getData(addressCombo.getText());
					boolean isShippingAddressSelected = shippingInfoSection.isShippingAddressSelected();

					updateShippingMethod();
					updateOrderSummary();
					setErrorMessage(null);

					shippingMethodCombo.setEnabled(isShippingAddressSelected);
					shippingMethodLabel.setEnabled(isShippingAddressSelected);
				}
			});

			shippingMethodCombo.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(final SelectionEvent event) {
					shippingOption = (ShippingOption) shippingMethodCombo.getData(shippingMethodCombo.getText());
					populateShoppingCart(OrderPopulateStep.SHIPPING_METHOD_MODIFIED);
					if (isShippingAddressSelected()) {
						setErrorMessage(null);
					}
				}
			});
		}

		/**
		 * Populate shippingMethodCombo with available Shipping Methods for selected address.
		 */
		private void updateShippingMethod() {
			shippingMethodCombo.removeAll();
			shippingMethodCombo.add(defaultMethodPrompt);
			shippingMethodCombo.select(0);

			final ShippingOptionService shippingOptionService = ServiceLocator.getService(ContextIdNames.SHIPPING_OPTION_SERVICE);
			final List<ShippingOption> shippingOptions = shippingOptionService.getShippingOptions(shippingAddress,
					getOrder().getStore().getCode(),
					getOrder().getLocale())
					.getAvailableShippingOptions();
			for (ShippingOption availableShippingOption : shippingOptions) {
				String methodName = availableShippingOption.getDisplayName(getOrder().getLocale()).orElse(null);
				shippingMethodCombo.setData(methodName, availableShippingOption);
				shippingMethodCombo.add(methodName);
			}
		}

		private void updateOrderSummary() {
			summarySection.setInclusiveTax(isInclusiveTax(shippingAddress));
			summarySection.dispose();
			summarySection.createControls(null, summarySection.getToolkit());
			summarySection.populateControls();
			summarySection.bindControls(getDataBindingContext());
			summarySection.getSection().getParent().layout(true);
		}

		private boolean validate() {
			if (!isShippingAddressSelected()) {
				setErrorMessage(FulfillmentMessages.get().ExchangeWizard_AddressShouldBeSelected_Message);
				return false;
			}

			if (!isShippingMethodSelected()) {
				setErrorMessage(FulfillmentMessages.get().ExchangeWizard_MethodShouldBeSelected_Message);
				return false;
			}
			return true;
		}

		private boolean isShippingMethodSelected() {
			return !defaultMethodPrompt.equals(shippingMethodCombo.getText());
		}

		private boolean isShippingAddressSelected() {
			return !defaultAddressPrompt.equals(addressCombo.getText());
		}

	}

	/**
	 * This Section contains summary information about new getOrder().
	 */
	class SummarySectionPart extends AbstractCmClientFormSectionPart {

		private static final int HORIZONTAL_SPAN = 3;

		/** Price information. */
		private Text shippingCostText;

		private Text shipmentDiscountText;

		private Text itemSubTotalLabel;

		private Text itemTaxesLabel;

		private Text shippingTaxesLabel;

		private Text orderTotalLabel;

		private Text totalBeforeTaxLabel;

		private boolean inclusiveTax;

		private final Composite parent;

		private final FormToolkit toolkit;

		private IEpLayoutComposite controlPane;

		private DataBindingContext bindingContex;

		/**
		 * Constructor.
		 *
		 * @param parent the parent form
		 * @param toolkit the form toolkit
		 * @param dataBindingContext the form databinding context
		 * @param inclusiveTax true if the price is tax inclusive
		 */
		SummarySectionPart(final Composite parent, final FormToolkit toolkit,
				final DataBindingContext dataBindingContext, final boolean inclusiveTax) {
			super(parent, toolkit, dataBindingContext, ExpandableComposite.TITLE_BAR);
			this.inclusiveTax = inclusiveTax;
			this.parent = parent;
			this.toolkit = toolkit;
		}

		@Override
		protected void createControls(final Composite parent, final FormToolkit toolkit) {
			Composite localParent = this.parent;
			if (parent != null) {
				localParent = parent;
			}

			controlPane = CompositeFactory.createTableWrapLayoutComposite(localParent, 1, false);
			controlPane.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.FILL));

			final IEpLayoutData shippingCostPaneData = controlPane.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.CENTER, true, false);
			IEpLayoutComposite shipmentSummaryInfoPane = controlPane.addTableWrapLayoutComposite(HORIZONTAL_SPAN, false, shippingCostPaneData);


			final IEpLayoutData labelData = controlPane.createLayoutData(IEpLayoutData.END, IEpLayoutData.CENTER);
			final IEpLayoutData currencyData = shipmentSummaryInfoPane.createLayoutData(IEpLayoutData.END, IEpLayoutData.CENTER);
			final IEpLayoutData priceData = shipmentSummaryInfoPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.CENTER, true, false);

			shipmentSummaryInfoPane.addLabelBold(FulfillmentMessages.get().ExchangeWizard_ItemSubTotal_Label, labelData);
			createCurrencyTextControl(shipmentSummaryInfoPane, currencyData);
			itemSubTotalLabel = shipmentSummaryInfoPane.addTextField(EpState.READ_ONLY, priceData);

			Label shippingCostLabel = shipmentSummaryInfoPane.addLabelBold(FulfillmentMessages.get().ExchangeWizard_ShippingCost_Label, labelData);
			shippingCostLabel.setForeground(CmClientResources.getColor(CmClientResources.COLOR_RED));
			createCurrencyTextControl(shipmentSummaryInfoPane, currencyData);
			shippingCostText = shipmentSummaryInfoPane.addTextField(EpState.EDITABLE, priceData);

			Label shipmentDiscountLabel = shipmentSummaryInfoPane.addLabelBold(FulfillmentMessages
					.get().ExchangeWizard_ShipmentDiscount_Label, labelData);
			shipmentDiscountLabel.setForeground(CmClientResources.getColor(CmClientResources.COLOR_RED));
			createCurrencyTextControl(shipmentSummaryInfoPane, currencyData);
			shipmentDiscountText = shipmentSummaryInfoPane.addTextField(EpState.EDITABLE, priceData);

			if (!inclusiveTax) {
				shipmentSummaryInfoPane.addLabelBold(FulfillmentMessages.get().ExchangeWizard_TotalBeforeTax_Label, labelData);
				createCurrencyTextControl(shipmentSummaryInfoPane, currencyData);
				totalBeforeTaxLabel = shipmentSummaryInfoPane.addTextField(EpState.READ_ONLY, priceData);
			}

			shipmentSummaryInfoPane.addLabelBold(FulfillmentMessages.get().ExchangeWizard_ItemTaxes_Label, labelData);
			createCurrencyTextControl(shipmentSummaryInfoPane, currencyData);
			itemTaxesLabel = shipmentSummaryInfoPane.addTextField(EpState.READ_ONLY, priceData);

			shipmentSummaryInfoPane.addLabelBold(FulfillmentMessages.get().ExchangeWizard_ShippingTaxes_Label, labelData);
			createCurrencyTextControl(shipmentSummaryInfoPane, currencyData);
			shippingTaxesLabel = shipmentSummaryInfoPane.addTextField(EpState.READ_ONLY, priceData);

			shipmentSummaryInfoPane.addLabelBold(FulfillmentMessages.get().ExchangeWizard_OrderTotal_Label, labelData);
			createCurrencyTextControl(shipmentSummaryInfoPane, currencyData);
			orderTotalLabel = shipmentSummaryInfoPane.addTextField(EpState.READ_ONLY, priceData);

			addFocusListenerForEditableFields();
		}

		@Override
		public void dispose() {

			bindingContex.dispose();
			controlPane.getSwtComposite().dispose();
		}

		/**
		 * Add the focus listener for editable fields.
		 */
		protected void addFocusListenerForEditableFields() {
			shippingCostText.addFocusListener(new FocusAdapter() {
				@Override
				public void focusLost(final FocusEvent event) {
					populateShoppingCart(OrderPopulateStep.SHIPPING_COST_MODIFIED);
				}
			});

			shipmentDiscountText.addFocusListener(new FocusAdapter() {
				@Override
				public void focusLost(final FocusEvent event) {
					String price = ((Text) event.getSource()).getText();
					try {
						shipmentDiscount = new BigDecimal(price);
					} catch (NumberFormatException exception) {
						shipmentDiscount = BigDecimal.ZERO;
						LOG.debug("wrong number"); //$NON-NLS-1$
					}
					populateShoppingCart(OrderPopulateStep.SHIPMENT_DISCOUNT_MODIFIED);

				}
			});
		}

		@Override
		protected void populateControls() {
			Currency currency = getOrder().getCurrency();

			shippingCostText.setText(formatPriceForCurrency(BigDecimal.ZERO, currency));
			shipmentDiscountText.setText(formatPriceForCurrency(BigDecimal.ZERO, currency));
			shipmentDiscount = BigDecimal.ZERO;
			itemSubTotalLabel.setText(formatPriceForCurrency(BigDecimal.ZERO, currency));
			itemTaxesLabel.setText(formatPriceForCurrency(BigDecimal.ZERO, currency));
			shippingTaxesLabel.setText(formatPriceForCurrency(BigDecimal.ZERO, currency));
			orderTotalLabel.setText(formatPriceForCurrency(BigDecimal.ZERO, currency));

			if (!inclusiveTax) {
				totalBeforeTaxLabel.setText(BigDecimal.ZERO.toString());
			}
		}

		@Override
		protected String getSectionTitle() {
			return FulfillmentMessages.get().ExchangeWizard_ExchangeOrderSummary_Section;
		}

		@Override
		protected void bindControls(final DataBindingContext bindingContext) {
			this.bindingContex = bindingContext;
			final boolean hideDecorationOnFirstValidation = true;
			final EpControlBindingProvider binder = EpControlBindingProvider.getInstance();

			IValidator validator = new CompoundValidator(
					new IValidator[] { EpValidatorFactory.NON_NEGATIVE_BIG_DECIMAL, EpValidatorFactory.REQUIRED });
			binder.bind(bindingContext, shippingCostText, validator, null, new ObservableUpdateValueStrategy() {
				@Override
				protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
					shippingCost = new BigDecimal((String) newValue);
					return Status.OK_STATUS;
				}
			}, hideDecorationOnFirstValidation);

			IValidator discountValidator = new CompoundValidator(
					new IValidator[] { EpValidatorFactory.NON_NEGATIVE_BIG_DECIMAL, EpValidatorFactory.REQUIRED, value -> {
						BigDecimal discount = new BigDecimal((String) value);
						BigDecimal cost = new BigDecimal(itemSubTotalLabel.getText());
						if (discount.compareTo(cost) > 0) {
							return new Status(IStatus.ERROR, FulfillmentPlugin.PLUGIN_ID, IStatus.ERROR,
									FulfillmentMessages.get().ExchangeWizard_TooBigDiscount_Message, null);
						}
						return Status.OK_STATUS;
					}});
			binder.bind(bindingContext, shipmentDiscountText, discountValidator, null, new ObservableUpdateValueStrategy() {
				@Override
				protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
					return Status.OK_STATUS;
				}
			}, hideDecorationOnFirstValidation);
			bindingContext.updateModels();
		}

		/**
		 * Update information about prices/taxes/shipment.
		 */
		protected void updatePrices() {
			ShoppingCartPricingSnapshot pricingSnapshot = getPricingSnapshotService().getPricingSnapshotForCart(shoppingCart);
			ShoppingCartTaxSnapshot taxSnapshot = getTaxSnapshotService().getTaxSnapshotForCart(shoppingCart, pricingSnapshot);
			TaxCalculationResult taxCalculationResult = taxSnapshot.getTaxCalculationResult();
			itemSubTotalLabel.setText(pricingSnapshot.getSubtotal().toString());
			shippingCostText.setText(pricingSnapshot.getShippingCost().getAmount().toString());
			shipmentDiscountText.setText(pricingSnapshot.getSubtotalDiscount().toString());

			if (!inclusiveTax) {
				totalBeforeTaxLabel.setText(pricingSnapshot.getBeforeTaxTotal().getAmount().toString());
			}
			itemTaxesLabel.setText(taxCalculationResult.getTotalItemTax().getAmount().toString());
			shippingTaxesLabel.setText(taxCalculationResult.getShippingTax().getAmount().toString());
			orderTotalLabel.setText(taxSnapshot.getTotal().toString());
		}

		private void createCurrencyTextControl(final IEpLayoutComposite controlPane, final IEpLayoutData currencyData) {
			String currencyCode = getOrder().getCurrency().getCurrencyCode();

			Text shippingCostCurrencyTxt = controlPane.addTextField(EpState.READ_ONLY, currencyData);
			shippingCostCurrencyTxt.setText(currencyCode);
		}

		private String formatPriceForCurrency(final BigDecimal price, final Currency currency) {
			return price.setScale(currency.getDefaultFractionDigits()).toPlainString();
		}

		/**
		 * Check if the session is inclusive tax.
		 * @return true if inclusive tax
		 */
		public boolean isInclusiveTax() {
			return inclusiveTax;
		}

		/**
		 * Set the session is inclusive tax or not.
		 * @param inclusiveTax true if inclusive tax
		 */
		public void setInclusiveTax(final boolean inclusiveTax) {
			this.inclusiveTax = inclusiveTax;
		}

		/**
		 * Get the composite that contains the order summary.
		 * @return the composite
		 */
		public IEpLayoutComposite getControlPane() {
			return this.controlPane;
		}

		/**
		 * Get the parent composite.
		 * @return parent composite
		 */
		public Composite getParent() {
			return parent;
		}

		/**
		 * Get toolkit.
		 * @return toolkit the formToolkit
		 */
		public FormToolkit getToolkit() {
			return toolkit;
		}



	}



	/**
	 * Add <code>CartItem</code> to the <code>ShoppingCart</code> Action.
	 */
	protected class AddCartItemAction extends Action {
		@Override
		public void run() {
			SkuFinderDialog dialog = new SkuFinderDialog(getShell(), getOrder().getStore().getCatalog(), true);
			if (dialog.open() == Window.OK) {
				ExchangeItem cartItem = returnCartItemWithProductSku(dialog);
				if (cartItem == null) {
					return;
				}
				ExchangeItem item = getCartItemByProductSkuUid(getProductSku(cartItem).getUidPk());
				if (item == null) { // when ProductSku is already exists in
					// cartItems we should increase it quantity.
					LOG.debug("we got new sku: " + getProductSku(cartItem).getSkuCode()); //$NON-NLS-1$
					cartItem = enrichItemWithDilaogSelectedPrice(cartItem, dialog.getSelectedItemPriceSummary(),
							Currency.getInstance(dialog.getCurrentCurrencyCode()));
					cartItems.add(cartItem);
					refreshCartItems();
				} else {
					int incr = item.getQuantity() + 1;
					LOG.debug("quantity was increased: " + incr); //$NON-NLS-1$
					orderedItemsTableViewer.getSwtTableViewer().update(item, null);
				}
				populateShoppingCart(OrderPopulateStep.SHOPPING_CART_MODIFIED);
			}
		}

		private ExchangeItem enrichItemWithDilaogSelectedPrice(final ExchangeItem item, final BaseAmountDTO dto, final Currency priceCurrency) {
			PriceHelper priceHelper = new PriceHelper();
			item.setPrice(dto.getQuantity().intValue(), priceHelper.createPriceFromBaseAmountDto(dto, priceCurrency));
			return item;
		}

		/*
		 * This method retrieves ProductSku from SkuFinderDialog and returns cart item instance. IMPORTANT: in case if Product was retrieved default
		 * product SKU Item will be set for the cart item.
		 */
		private ExchangeItem returnCartItemWithProductSku(final SkuFinderDialog dialog) {
			ExchangeItem cartItem = ServiceLocator.getService(ContextIdNames.EXCHANGE_ITEM);
			ProductSku productSku = null;
			if (dialog.getSelectedObject() instanceof Product) {
				Product product = (Product) dialog.getSelectedObject();
				LOG.debug("product was returned: " + product.getDisplayName(getOrder().getLocale())); //$NON-NLS-1$
				productSku = product.getDefaultSku();
			}
			if (dialog.getSelectedObject() instanceof ProductSku) {
				productSku = (ProductSku) dialog.getSelectedObject();
				LOG.debug("productSku was returned: " + productSku.getDisplayName(getOrder().getLocale())); //$NON-NLS-1$

				ProductSkuChecker psc = new ProductSkuChecker(getModel().getOrder());
				if (psc.isRecurringSku(productSku)) {
					MessageDialog.openError(null, FulfillmentMessages.get().Exchange_AddingItem_NoRecurring_Title,
							FulfillmentMessages.get().Exchange_AddingItem_NoRecurring_Message);
					return null;
				}
			}
			cartItem.setSkuGuid(productSku.getGuid());
			return cartItem;
		}
	}

	/**
	 * Remove <code>CartItem</code> from the <code>ShoppingCart</code> Action.
	 */
	protected class RemoveCartItemAction extends Action {

		@Override
		public void run() {
			ShoppingItem selectedItem = getSelectedCartItem();
			boolean confirmed =
				MessageDialog.openConfirm(getShell(), FulfillmentMessages.get().ExchangeWizard_RemoveLineItemConfirmTitle,

						NLS.bind(FulfillmentMessages.get().ExchangeWizard_RemoveLineItemConfirmText,
						getProductSku(selectedItem).getProduct().getDisplayName(getOrder().getLocale())));

			if (confirmed) {
				cartItems.remove(selectedItem);
				populateShoppingCart(OrderPopulateStep.SHOPPING_CART_MODIFIED);
				refreshCartItems();
			}
		}

	}

	@Override
	public boolean beforeFromPrev(final PageChangingEvent event) {
		List<ExchangeItem> exchangeItemsFromReturn = new ArrayList<>();
		if (!isConverted) {
			for (OrderReturnSku cartItem : getModel().getOrderReturnSkus()) {
				exchangeItemsFromReturn.add(convertOrderReturnSkuToCartItem(cartItem));
			}
			//important: the convertation of price from order to shopping itema is performed only once
			//because we shouldn't lose changes to the price that we made if the Step2->Step1 and then
			//back to Step2 is done
			isConverted = true;
		}
		for (ExchangeItem exchangeItem : exchangeItemsFromReturn) {
			if (getCartItemByProductSkuUid(getProductSku(exchangeItem).getUidPk()) == null) {
				//In case when current product sku doesn't exist in our cart and the qty is positive than we should add it.
				if (exchangeItem.getQuantity() > 0) {
					cartItems.add(exchangeItem);
				}
			} else {
				//In case when current product sku already exist in cart we should set quantity that was changed on first page.
				getCartItemByProductSkuUid(getProductSku(exchangeItem).getUidPk()).setPrice(exchangeItem.getQuantity(), exchangeItem.getPrice());
			}
		}
		refreshCartItems();
		return super.beforeFromPrev(event);
	}

	@Override
	public boolean beforeNext(final PageChangingEvent event) {
		return orderedItemsSection.validate() && shippingInfoSection.validate() && super.beforeNext(event);
	}

	/*
	 * This method should convert OrderReturnSku to OrderReturnSku. IMPORTANT: quantity will be set to 1.
	 */
	private ExchangeItem convertOrderReturnSkuToCartItem(final OrderReturnSku orderReturnSku) {
		ExchangeItem exchangeItem = ServiceLocator.getService(ContextIdNames.EXCHANGE_ITEM);
		exchangeItem.setSkuGuid(orderReturnSku.getSkuGuid());
		exchangeItem.setTaxAmount(orderReturnSku.getTax());

		// MSC-6967
		Money unitPrice = orderReturnSku.getOrderSku().getUnitPriceMoney();
		Price price = ServiceLocator.getService(ContextIdNames.PRICE);
		price.setCurrency(getOrder().getCurrency());
		price.setListPrice(unitPrice);
		exchangeItem.setPrice(orderReturnSku.getQuantity(), price);
		return exchangeItem;
	}

	/*
	 * Returns CartItem from cartItems by uidPk.
	 */
	private ExchangeItem getCartItemByProductSkuUid(final long uidPk) {
		for (ExchangeItem item : cartItems) {
			if (getProductSku(item).getUidPk() == uidPk) {
				return item;
			}
		}
		return null;
	}

	private Order getOrder() {
		return getModel().getOrder();
	}
	
	/**
	 * Returns the given cart item's product sku.
	 *
	 * @param cartItem the cart item
	 * @return the given cart item's product sku
	 */
	protected ProductSku getProductSku(final ShoppingItem cartItem) {
		return getProductSkuLookup().findByGuid(cartItem.getSkuGuid());
	}

	/**
	 * Lazy loads a ProductSkuLookup.
	 *
	 * @return a product sku reader.
	 */
	protected ProductSkuLookup getProductSkuLookup() {
		if (productSkuLookup == null) {
			productSkuLookup = new LocalProductSkuLookup();
		}
		
		return productSkuLookup;
	}

	/**
	 * Get the pricing snapshot service.
	 *
	 * @return the pricing snapshot service
	 */
	protected PricingSnapshotService getPricingSnapshotService() {
		if (pricingSnapshotService == null) {
			pricingSnapshotService = ServiceLocator.getService(ContextIdNames.PRICING_SNAPSHOT_SERVICE);
		}
		return pricingSnapshotService;
	}

	/**
	 * Get the tax snapshot service.
	 *
	 * @return the tax snapshot service
	 */
	protected TaxSnapshotService getTaxSnapshotService() {
		if (taxSnapshotService == null) {
			taxSnapshotService = ServiceLocator.getService(ContextIdNames.TAX_SNAPSHOT_SERVICE);
		}
		return taxSnapshotService;
	}
}
