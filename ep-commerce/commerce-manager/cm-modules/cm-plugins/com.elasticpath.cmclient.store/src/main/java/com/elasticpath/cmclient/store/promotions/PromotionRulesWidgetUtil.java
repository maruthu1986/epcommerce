/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.store.promotions; //NOPMD

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Currency;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.conversion.Converter;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.AbstractFormPart;
import org.eclipse.ui.forms.widgets.Hyperlink;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.CorePlugin;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.EpValueBinding;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.helpers.BrandComparator;
import com.elasticpath.cmclient.core.helpers.extenders.PluginHelper;
import com.elasticpath.cmclient.core.helpers.extenders.PromotionWidgetCreator;
import com.elasticpath.cmclient.core.ui.dialog.CategoryFinderDialog;
import com.elasticpath.cmclient.core.ui.dialog.ProductFinderDialog;
import com.elasticpath.cmclient.core.ui.dialog.SkuFinderDialog;
import com.elasticpath.cmclient.core.ui.framework.AbstractCmClientFormSectionPart;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.validation.CompoundValidator;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.IPolicyTargetLayoutComposite;
import com.elasticpath.cmclient.store.StorePlugin;
import com.elasticpath.cmclient.store.promotions.editors.PromotionRulesDefinitionPart;
import com.elasticpath.cmclient.store.promotions.wizard.NewPromotionWizardRulesPage;
import com.elasticpath.common.dto.pricing.PriceListDescriptorDTO;
import com.elasticpath.common.pricing.service.PriceListService;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.misc.CurrencyCodeComparator;
import com.elasticpath.domain.rules.CouponConfig;
import com.elasticpath.domain.rules.CouponUsageType;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.rules.RuleAction;
import com.elasticpath.domain.rules.RuleElement;
import com.elasticpath.domain.rules.RuleElementType;
import com.elasticpath.domain.rules.RuleParameter;
import com.elasticpath.domain.rules.RuleParameterNumItemsQuantifier;
import com.elasticpath.domain.rules.RuleScenarios;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.catalog.BrandService;
import com.elasticpath.service.catalog.CategoryLookup;
import com.elasticpath.service.catalog.ProductLookup;
import com.elasticpath.service.rules.CouponConfigService;
import com.elasticpath.service.rules.RuleService;
import com.elasticpath.service.shipping.ShippingOptionService;
import com.elasticpath.shipping.connectivity.dto.ShippingOption;

/**
 * Helper utility class used to create SWT control objects on the Promotion Rules page.
 */
@SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.TooManyMethods", "PMD.ExcessiveClassLength"})
public final class PromotionRulesWidgetUtil {
	private static final Logger LOG = Logger.getLogger(PromotionRulesWidgetUtil.class);

	private static final int NUM_ITEMS_SPINNER_WIDTH = 40;

	private static final int DISCOUNT_PERCENTAGE_TEXT_WIDTH = 40;

	private static final int DISCOUNT_AMOUNT_TEXT_WIDTH = 40;

	private static final int SUBTOTAL_AMOUNT_TEXT_WIDTH = 40;

	private static final int COUPON_PREFIX_TEXT_WIDTH = 120;

	private static final int MINIMUM_NUM_ITEMS = 1;

	private static final int MAXIMUM_NUM_ITEMS = 100000000;

	/**
	 * Parameter value of <i>ANY</i> category.
	 */
	public static final String ANY_CATEGORY = "0"; //$NON-NLS-1$

	/**
	 * Parameter value of <i>ANY</i> sku.
	 */
	public static final String ANY_SKU = "0"; //$NON-NLS-1$

	private static final String RULE_PARAMETER_BIND_VALUE = "value"; //$NON-NLS-1$

	private static final String[] IS_IS_NOT_ITEMS = {PromotionsMessages.get().PromoRulesDefinition_Label_Is,
			PromotionsMessages.get().PromoRulesDefinition_Label_IsNot};

	private static final String[] NUM_ITEMS_QUANTIFIER_ITEMS = {PromotionsMessages.get().getLocalizedName(RuleParameterNumItemsQuantifier.AT_LEAST),
			PromotionsMessages.get().getLocalizedName(RuleParameterNumItemsQuantifier.EXACTLY)};

	private final Store store;

	private final Catalog catalog;

	private final int scenario;

	private final PriceListService priceListService = ServiceLocator.getService(ContextIdNames.PRICE_LIST_CLIENT_SERVICE);

	private final CouponConfigService couponConfigService = ServiceLocator.getService(ContextIdNames.COUPON_CONFIG_SERVICE);

	private final Rule rule;

	private final boolean disallowGiftCertificates;

	/**
	 * Extends MouseAdapter to provide a method to refresh a UI object
	 * after a change.
	 */
	private class LayingOutMouseAdapter extends MouseAdapter {

		private final LayoutRefresher layoutRefresher;

		LayingOutMouseAdapter(final LayoutRefresher layoutRefresher) {
			this.layoutRefresher = layoutRefresher;
		}

		protected void refreshLayout() {
			layoutRefresher.refreshLayout();
		}

	}

	/**
	 * Don't let anyone instantiate this class.
	 */
	private PromotionRulesWidgetUtil(final Rule rule, final int scenario, final Store store, final Catalog catalog) {
		this.rule = rule;
		this.scenario = scenario;
		this.store = store;
		this.catalog = catalog;
		this.disallowGiftCertificates = isDisallowRuleForGiftCertificates();
	}

	private boolean isDisallowRuleForGiftCertificates() {
		//it should not be possible to add a gift certificate 
		//to a promotion of type = CART_N_FREE_SKUS_ACTION
		if (this.rule != null) {
			for (RuleAction ruleAction : this.rule.getActions()) {
				if (ObjectUtils.equals(ruleAction.getElementType(), RuleElementType.CART_N_FREE_SKUS_ACTION)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Gets an instance of this utility with set properties for the rule supplied.
	 *
	 * @param rule the rule
	 * @return the utility instance
	 */
	public static PromotionRulesWidgetUtil getInstance(final Rule rule) {
		return new PromotionRulesWidgetUtil(rule, rule.getRuleSet().getScenario(),
				rule.getStore(), rule.getCatalog());
	}

	/**
	 * Adds to the given ruleComposite <code>IEpLayoutComposite</code> a number-of-items spinner.
	 *
	 * @param ruleParameter         the <code>RuleParameter</code> object to display
	 * @param ruleComposite         the <code>IEpLayoutComposite</code> to which the number-of-items spinner should be added to
	 * @param dataBindingContext    the <code>DataBindingContext</code>
	 * @param policyActionContainer the <code>PolicyActionContainer</code> managing component state
	 */
	public void addNumItemsSpinner(final RuleParameter ruleParameter, final IPolicyTargetLayoutComposite ruleComposite,
								   final DataBindingContext dataBindingContext, final PolicyActionContainer policyActionContainer) {
		// set default value of the rule parameter
		if (ruleParameter.getValue() == null) {
			ruleParameter.setValue(String.valueOf(MINIMUM_NUM_ITEMS));
		}

		// create the spinner
		final Spinner numItemsSpinner = ruleComposite.addSpinnerField(null, policyActionContainer);
		final GridData gridData = new GridData();
		gridData.widthHint = NUM_ITEMS_SPINNER_WIDTH;
		numItemsSpinner.setLayoutData(gridData);

		// set bounds
		numItemsSpinner.setMinimum(MINIMUM_NUM_ITEMS);
		numItemsSpinner.setMaximum(MAXIMUM_NUM_ITEMS);

		if (ruleParameter.getValue() != null) {
			// populate the spinner
			numItemsSpinner.setSelection(Integer.parseInt(ruleParameter.getValue()));
		}

		// bind the spinner
		if (ruleParameter != null) {
			final EpControlBindingProvider bindingProvider = EpControlBindingProvider.getInstance();
			final EpValueBinding binding = bindingProvider.bind(dataBindingContext, numItemsSpinner, ruleParameter, RULE_PARAMETER_BIND_VALUE,
					EpValidatorFactory.POSITIVE_INTEGER, new Converter(Integer.class, String.class) {
						@Override
						public String convert(final Object fromObject) {
							return fromObject.toString();
						}
					}, true);

			addDisposeListener(numItemsSpinner, dataBindingContext, binding);
		}
	}

	/**
	 * Adds to the given ruleComposite <code>IEpLayoutComposite</code> a discount percentage text field.
	 *
	 * @param ruleParameter         the <code>RuleParameter</code> object to display
	 * @param ruleComposite         the <code>IEpLayoutComposite</code> to which the discount percentage text field should be added to
	 * @param dataBindingContext    the <code>DataBindingContext</code>
	 * @param policyActionContainer the <code>PolicyActionContainer</code> managing component state
	 */
	public void addDiscountPercentageText(final RuleParameter ruleParameter, final IPolicyTargetLayoutComposite ruleComposite,
										  final DataBindingContext dataBindingContext, final PolicyActionContainer policyActionContainer) {
		// create the text field
		final Text discountPercentageText = ruleComposite.addTextField(null, policyActionContainer);
		final GridData gridData = new GridData();
		gridData.widthHint = DISCOUNT_PERCENTAGE_TEXT_WIDTH;
		discountPercentageText.setLayoutData(gridData);

		if (ruleParameter.getValue() != null) {
			// populate the text field
			discountPercentageText.setText(ruleParameter.getValue());
		}

		// ensure that input is numeric
		ensureNumericInput(discountPercentageText);

		// bind the text field
		if (ruleParameter != null) {
			final EpControlBindingProvider bindingProvider = EpControlBindingProvider.getInstance();
			final EpValueBinding binding = bindingProvider.bind(dataBindingContext, discountPercentageText, ruleParameter,
					RULE_PARAMETER_BIND_VALUE,
					new CompoundValidator(
							new IValidator[]{EpValidatorFactory.PERCENTAGE, EpValidatorFactory.REQUIRED, EpValidatorFactory.MAX_LENGTH_5}),
					null, true);
			addDisposeListener(discountPercentageText, dataBindingContext, binding);
		}
	}

	/**
	 * Adds to the given ruleComposite <code>IEpLayoutComposite</code> a discount amount text field.
	 *
	 * @param ruleParameter         the <code>RuleParameter</code> object to display
	 * @param ruleComposite         the <code>IEpLayoutComposite</code> to which the discount amount text field should be added to
	 * @param dataBindingContext    the <code>DataBindingContext</code>
	 * @param policyActionContainer the <code>PolicyActionContainer</code> managing component state
	 */
	public void addDiscountAmountText(final RuleParameter ruleParameter, final IPolicyTargetLayoutComposite ruleComposite,
									  final DataBindingContext dataBindingContext, final PolicyActionContainer policyActionContainer) {
		// create the text field
		final Text discountAmountText = ruleComposite.addTextField(null, policyActionContainer);
		final GridData gridData = new GridData();
		gridData.widthHint = DISCOUNT_AMOUNT_TEXT_WIDTH;
		discountAmountText.setLayoutData(gridData);

		if (ruleParameter.getValue() != null) {
			// populate the text field
			discountAmountText.setText(ruleParameter.getValue());
		}

		// ensure that input is numeric
		ensureNumericInput(discountAmountText);

		// bind the text field
		if (ruleParameter != null) {
			final EpControlBindingProvider bindingProvider = EpControlBindingProvider.getInstance();
			final EpValueBinding binding = bindingProvider.bind(dataBindingContext, discountAmountText, ruleParameter, RULE_PARAMETER_BIND_VALUE,
					EpValidatorFactory.NON_NEGATIVE_NON_ZERO_BIG_DECIMAL_REQUIRED, null, true);
			addDisposeListener(discountAmountText, dataBindingContext, binding);
		}
	}

	/**
	 * Adds to the given ruleComposite <code>IEpLayoutComposite</code> a subtotal amount text field.
	 *
	 * @param ruleParameter         the <code>RuleParameter</code> object to display
	 * @param ruleComposite         the <code>IEpLayoutComposite</code> to which the subtotal amount text field be added to
	 * @param dataBindingContext    the <code>DataBindingContext</code>
	 * @param policyActionContainer the <code>PolicyActionContainer</code> managing component state
	 */
	public void addSubtotalAmountText(final RuleParameter ruleParameter, final IPolicyTargetLayoutComposite ruleComposite,
									  final DataBindingContext dataBindingContext, final PolicyActionContainer policyActionContainer) {
		// create the text field
		final Text subtotalAmountText = ruleComposite.addTextField(null, policyActionContainer);
		final GridData gridData = new GridData();
		gridData.widthHint = SUBTOTAL_AMOUNT_TEXT_WIDTH;
		subtotalAmountText.setLayoutData(gridData);

		if (ruleParameter.getValue() != null) {
			// populate the text field
			subtotalAmountText.setText(ruleParameter.getValue());
		}

		// ensure that input is numeric
		ensureNumericInput(subtotalAmountText);

		// bind the text field
		if (ruleParameter != null) {
			final EpControlBindingProvider bindingProvider = EpControlBindingProvider.getInstance();
			final EpValueBinding binding = bindingProvider.bind(dataBindingContext, subtotalAmountText, ruleParameter, RULE_PARAMETER_BIND_VALUE,
					EpValidatorFactory.NON_NEGATIVE_NON_ZERO_BIG_DECIMAL_REQUIRED, null, true);
			addDisposeListener(subtotalAmountText, dataBindingContext, binding);
		}
	}

	/**
	 * Adds to the given ruleComposite <code>IEpLayoutComposite</code> a currency combo box.
	 *
	 * @param ruleParameter         the <code>RuleParameter</code> object to display
	 * @param ruleComposite         the <code>IEpLayoutComposite</code> to which the currency combo box should be added to
	 * @param dataBindingContext    the <code>DataBindingContext</code>
	 * @param policyActionContainer the <code>PolicyActionContainer</code> managing component state
	 */
	public void addCurrencyCombo(final RuleParameter ruleParameter, final IPolicyTargetLayoutComposite ruleComposite,
								 final DataBindingContext dataBindingContext, final PolicyActionContainer policyActionContainer) {
		// create the combo box
		final CCombo currencyCombo = ruleComposite.addComboBox(null, policyActionContainer);
		currencyCombo.pack();

		Collection<Currency> currencies = getSupportedCurrencies();
		int selectedIndex = 0;
		int currCurrencyIndex = 0;

		for (final Currency currCurrency : currencies) {
			currencyCombo.add(currCurrency.getCurrencyCode());
			if ((ruleParameter.getValue() != null) && (ruleParameter.getValue().equals(currCurrency.getCurrencyCode()))) {
				selectedIndex = currCurrencyIndex;
			}
			currCurrencyIndex++;
		}
		currencyCombo.select(selectedIndex);

		// set default value of the rule parameter to the first currency in the combo
		if (ruleParameter.getValue() == null) {
			ruleParameter.setValue(currencies.iterator().next().getCurrencyCode());
		}

		// bind the combo box
		if (ruleParameter != null) {
			final EpControlBindingProvider bindingProvider = EpControlBindingProvider.getInstance();
			final EpValueBinding binding = bindingProvider.bind(dataBindingContext, currencyCombo, null, null, new ObservableUpdateValueStrategy() {
				@Override
				protected IStatus doSet(final IObservableValue observableValue, final Object value) {
					final int selectionIndex = (Integer) value;

					try {
						if (selectionIndex >= 0) {
							ruleParameter.setValue(currencyCombo.getText());
						}
						return Status.OK_STATUS;
					} catch (final EpServiceException e) {
						return new Status(IStatus.WARNING, StorePlugin.PLUGIN_ID, "Cannot set the rule parameter currency."); //$NON-NLS-1$
					}
				}
			}, true);

			addDisposeListener(currencyCombo, dataBindingContext, binding);
		}
	}


	/**
	 * Get supported currencies. For obtain catalog's supported currency
	 * price list and price list assignment will be used.
	 *
	 * @return collection of supported currency.
	 */
	private Collection<Currency> getSupportedCurrencies() {
		List<Currency> currencies = new ArrayList<>();
		if (scenario == RuleScenarios.CART_SCENARIO) {
			currencies.addAll(store.getSupportedCurrencies());
		} else {
			Set<Currency> uniqueCurrencies = new HashSet<>();
			List<PriceListDescriptorDTO> descriptors = priceListService.listByCatalog(catalog);
			for (PriceListDescriptorDTO dto : descriptors) {
				uniqueCurrencies.add(Currency.getInstance(dto.getCurrencyCode()));
			}
			currencies.addAll(uniqueCurrencies);
		}
		CurrencyCodeComparator comparator = ServiceLocator.getService(ContextIdNames.CURRENCYCODE_COMPARATOR);
		Collections.sort(currencies, comparator);
		return currencies;
	}

	/**
	 * Adds to the given ruleComposite <code>IEpLayoutComposite</code> a boolean key combo box.
	 *
	 * @param ruleParameter         the <code>RuleParameter</code> object to display
	 * @param ruleComposite         the <code>IEpLayoutComposite</code> to which the boolean key combo box should be added to
	 * @param dataBindingContext    the <code>DataBindingContext</code>
	 * @param policyActionContainer the <code>PolicyActionContainer</code> managing component state
	 */
	public void addBooleanKeyCombo(final RuleParameter ruleParameter, final IPolicyTargetLayoutComposite ruleComposite,
								   final DataBindingContext dataBindingContext, final PolicyActionContainer policyActionContainer) {
		final String isValue = "true"; //$NON-NLS-1$
		final String isNotValue = "false"; //$NON-NLS-1$
		final int isIndex = 0;
		final int isNotIndex = 1;

		// set default value of the rule parameter
		if (ruleParameter.getValue() == null) {
			ruleParameter.setValue(isValue);
		}

		// create the combo
		final CCombo booleanKeyCombo = ruleComposite.addComboBox(null, policyActionContainer);
		booleanKeyCombo.pack();

		// populate the combo box
		booleanKeyCombo.setItems(IS_IS_NOT_ITEMS);
		if (ruleParameter.getValue() == null) {
			booleanKeyCombo.select(isIndex);
		} else {
			if (ruleParameter.getValue().equalsIgnoreCase(isValue)) {
				booleanKeyCombo.select(isIndex);
			} else if (ruleParameter.getValue().equalsIgnoreCase(isNotValue)) {
				booleanKeyCombo.select(isNotIndex);
			}
		}

		// bind the combo
		if (ruleParameter != null) {
			final EpControlBindingProvider bindingProvider = EpControlBindingProvider.getInstance();
			final EpValueBinding binding = bindingProvider.bind(dataBindingContext, booleanKeyCombo, null, null,
					new ObservableUpdateValueStrategy() {
						@Override
						protected IStatus doSet(final IObservableValue observableValue, final Object value) {
							final int selectionIndex = (Integer) value;

							try {
								if (selectionIndex == isIndex) {
									ruleParameter.setValue(isValue);
								} else if (selectionIndex == isNotIndex) {
									ruleParameter.setValue(isNotValue);
								}
								return Status.OK_STATUS;
							} catch (final EpServiceException e) {
								return new Status(IStatus.WARNING, StorePlugin.PLUGIN_ID, "Cannot set the rule parameter boolean key.");
								//$NON-NLS-1$
							}
						}
					}, true);

			addDisposeListener(booleanKeyCombo, dataBindingContext, binding);
		}
	}

	/**
	 * Adds to the given ruleComposite <code>IEpLayoutComposite</code> a numItems quantifier combo box, which contains the choices "at least" and
	 * "exactly".
	 *
	 * @param ruleParameter         the <code>RuleParameter</code> object to display
	 * @param ruleComposite         the <code>IEpLayoutComposite</code> to which the numItems quantifier combo box should be added to
	 * @param dataBindingContext    the <code>DataBindingContext</code>
	 * @param policyActionContainer the <code>PolicyActionContainer</code> managing component state
	 */
	public void addNumItemsQuantifierCombo(final RuleParameter ruleParameter, final IPolicyTargetLayoutComposite ruleComposite,
										   final DataBindingContext dataBindingContext, final PolicyActionContainer policyActionContainer) {
		final int atLeastIndex = 0;
		final int exactlyIndex = 1;

		// set default value of the rule parameter
		if (ruleParameter.getValue() == null) {
			ruleParameter.setValue(RuleParameterNumItemsQuantifier.AT_LEAST.toString());
		}

		// create the combo
		final CCombo numItemsQuantifierCombo = ruleComposite.addComboBox(null, policyActionContainer);
		numItemsQuantifierCombo.pack();

		// populate the combo box
		numItemsQuantifierCombo.setItems(NUM_ITEMS_QUANTIFIER_ITEMS);
		if (ruleParameter.getValue() == null) {
			numItemsQuantifierCombo.select(0);
		} else {
			if (ruleParameter.getValue().equalsIgnoreCase(RuleParameterNumItemsQuantifier.AT_LEAST.toString())) {
				numItemsQuantifierCombo.select(atLeastIndex);
			} else if (ruleParameter.getValue().equalsIgnoreCase(RuleParameterNumItemsQuantifier.EXACTLY.toString())) {
				numItemsQuantifierCombo.select(exactlyIndex);
			}
		}

		// bind the combo
		if (ruleParameter != null) {
			final EpControlBindingProvider bindingProvider = EpControlBindingProvider.getInstance();
			final EpValueBinding binding = bindingProvider.bind(dataBindingContext, numItemsQuantifierCombo, null, null,
					new ObservableUpdateValueStrategy() {
						@Override
						protected IStatus doSet(final IObservableValue observableValue, final Object value) {
							final int selectionIndex = (Integer) value;

							try {
								if (selectionIndex == atLeastIndex) {
									ruleParameter.setValue(RuleParameterNumItemsQuantifier.AT_LEAST.toString());
								} else if (selectionIndex == exactlyIndex) {
									ruleParameter.setValue(RuleParameterNumItemsQuantifier.EXACTLY.toString());
								}
								return Status.OK_STATUS;
							} catch (final EpServiceException e) {
								return new Status(IStatus.WARNING, StorePlugin.PLUGIN_ID,
										"Cannot set the number of items quantifier key rule parameter."); //$NON-NLS-1$
							}
						}
					}, true);

			addDisposeListener(numItemsQuantifierCombo, dataBindingContext, binding);
		}
	}

	/**
	 * Adds to the given ruleComposite <code>IEpLayoutComposite</code> a brand combo box.
	 *
	 * @param ruleParameter         the <code>RuleParameter</code> object to display
	 * @param ruleComposite         the <code>IEpLayoutComposite</code> to which the brand combo box should be added to
	 * @param dataBindingContext    the <code>DataBindingContext</code>
	 * @param policyActionContainer the <code>PolicyActionContainer</code> managing component state
	 */
	public void addBrandCombo(final RuleParameter ruleParameter, final IPolicyTargetLayoutComposite ruleComposite,
							  final DataBindingContext dataBindingContext, final PolicyActionContainer policyActionContainer) {

		// create the combo box
		final CCombo brandCombo = ruleComposite.addComboBox(null, policyActionContainer);
		brandCombo.pack();

		final BrandService brandService = ServiceLocator.getService(ContextIdNames.BRAND_SERVICE);
		final List<Brand> brands;
		if (scenario == RuleScenarios.CATALOG_BROWSE_SCENARIO) {
			if (catalog.isMaster()) {
				brands = brandService.findAllBrandsFromCatalog(catalog.getUidPk());
			} else {
				//Virtual catalogs need to be able to create promos on any and all brands possible in case products are added later.
				brands = brandService.list();
			}

		} else {
			if (store.getCatalog().isMaster()) {
				brands = brandService.findAllBrandsFromCatalog(store.getCatalog().getUidPk());
			} else {
				brands = brandService.list();
			}
		}
		Collections.sort(brands, new BrandComparator(CorePlugin.getDefault().getDefaultLocale()));
		// populate the combo box
		int selectedIndex = 0;
		if (ruleParameter.getValue() != null) {
			selectedIndex = -1;
		}
		int currBrandIndex = 0;

		for (final Brand currBrand : brands) {
			String brandName = currBrand.getDisplayName(CorePlugin.getDefault().getDefaultLocale(), true);
			if (brandName == null) {
				brandName = StringUtils.EMPTY;
				LOG.debug("No brand name could be found for brand with code " + currBrand.getCode()); //$NON-NLS-1$
			}
			brandCombo.add(brandName);
			if ((ruleParameter.getValue() != null) && (ruleParameter.getValue().equals(currBrand.getCode()))) {
				selectedIndex = currBrandIndex;
			}
			currBrandIndex++;
		}
		brandCombo.select(selectedIndex);

		// bind the combo box
		if (ruleParameter != null) {
			if (selectedIndex != -1) {
				setRulesBrand(ruleParameter, brands, selectedIndex);
			}
			final EpControlBindingProvider bindingProvider = EpControlBindingProvider.getInstance();
			final EpValueBinding binding = bindingProvider.bind(dataBindingContext, brandCombo, null, null, new ObservableUpdateValueStrategy() {
				@Override
				protected IStatus doSet(final IObservableValue observableValue, final Object value) {
					final int selectionIndex = (Integer) value;
					return setRulesBrand(ruleParameter, brands, selectionIndex);
				}

			}, true);

			addDisposeListener(brandCombo, dataBindingContext, binding);
		}
	}

	private IStatus setRulesBrand(final RuleParameter ruleParameter, final List<Brand> brands, final int selectionIndex) {
		try {
			ruleParameter.setValue(brands.get(selectionIndex).getCode());
			return Status.OK_STATUS;
		} catch (final EpServiceException e) {
			return new Status(IStatus.WARNING, StorePlugin.PLUGIN_ID, "Cannot set the rule parameter brand.");  //$NON-NLS-1$
		}
	}


	/**
	 * Adds to the given ruleComposite <code>IEpLayoutComposite</code> a shipping option combo box.
	 *
	 * @param ruleParameter         the <code>RuleParameter</code> object to display
	 * @param ruleComposite         the <code>IEpLayoutComposite</code> to which the shipping option combo box should be added to
	 * @param dataBindingContext    the <code>DataBindingContext</code>
	 * @param policyActionContainer the <code>PolicyActionContainer</code> managing component state
	 */
	public void addShippingOptionCombo(final RuleParameter ruleParameter, final IPolicyTargetLayoutComposite ruleComposite,
											 final DataBindingContext dataBindingContext, final PolicyActionContainer policyActionContainer) {
		// create the combo box
		final CCombo shippingOptionCombo = ruleComposite.addComboBox(null, policyActionContainer);
		shippingOptionCombo.pack();
		final ShippingOptionService shippingOptionService = ServiceLocator.getService(ContextIdNames.SHIPPING_OPTION_SERVICE);
		final List<ShippingOption> shippingOptions = shippingOptionService.getAllShippingOptions(
				store.getCode(),
				CorePlugin.getDefault().getDefaultLocale()).getAvailableShippingOptions();

		// populate the combo box
		int selectedIndex = 0;
		int currShippingOptionIndex = 0;

		for (final ShippingOption currShippingOption : shippingOptions) {
			String shippingOptionName = currShippingOption.getDisplayName(CorePlugin.getDefault().getDefaultLocale()).orElse(null);
			if (shippingOptionName == null) {
				LOG.debug("ShippingOptionName is null for CODE = " + currShippingOption.getCode()); //$NON-NLS-1$
				shippingOptionName = StringUtils.EMPTY;
			}
			shippingOptionCombo.add(currShippingOption.getDisplayName(CorePlugin.getDefault().getDefaultLocale()).orElse(null));
			if ((ruleParameter.getValue() != null) && (ruleParameter.getValue().equals(currShippingOption.getCode()))) {
				selectedIndex = currShippingOptionIndex;
			}
			currShippingOptionIndex++;
		}
		shippingOptionCombo.select(selectedIndex);

		// set default value of the rule parameter to the first shipping option in the combo
		if (ruleParameter.getValue() == null) {
			if (shippingOptions.isEmpty()) {
				shippingOptionCombo.setEditable(false);
				shippingOptionCombo.setEnabled(false);
				Display.getCurrent().asyncExec(() -> showErrorDialog(PromotionsMessages.get().promotionNotAvailable));

				return;
			}
			ruleParameter.setValue(shippingOptions.get(0).getCode());
		}

		// bind the combo box
		if (ruleParameter != null) {
			final EpControlBindingProvider bindingProvider = EpControlBindingProvider.getInstance();
			final EpValueBinding binding = bindingProvider.bind(dataBindingContext, shippingOptionCombo, null, null,
					new ObservableUpdateValueStrategy() {
						@Override
						protected IStatus doSet(final IObservableValue observableValue, final Object value) {
							final int selectionIndex = (Integer) value;
							try {
								if (selectionIndex >= 0) {
									ruleParameter.setValue(shippingOptions.get(selectionIndex).getCode());
								}
								return Status.OK_STATUS;
							} catch (final EpServiceException e) {
								return new Status(IStatus.WARNING, StorePlugin.PLUGIN_ID, "Cannot set the rule parameter shipping."); //$NON-NLS-1$
							}
						}
					}, true);

			addDisposeListener(shippingOptionCombo, dataBindingContext, binding);
		}
	}

	private void showErrorDialog(final String message) {
		MessageDialog.openError(null, CoreMessages.get().SystemErrorTitle, message);
	}

	/**
	 * Adds to the given ruleComposite <code>IEpLayoutComposite</code> a coupon prefix text field.
	 *
	 * @param ruleParameter         the <code>RuleParameter</code> object to display
	 * @param ruleComposite         the <code>IEpLayoutComposite</code> to which the coupon prefix text field be added to
	 * @param dataBindingContext    the <code>DataBindingContext</code>
	 * @param policyActionContainer the <code>PolicyActionContainer</code> managing component state
	 */
	public void addCouponPrefixText(final RuleParameter ruleParameter, final IPolicyTargetLayoutComposite ruleComposite,
									final DataBindingContext dataBindingContext, final PolicyActionContainer policyActionContainer) {
		// create the text field
		final Text couponPrefixText = ruleComposite.addTextField(null, policyActionContainer);
		final GridData gridData = new GridData();
		gridData.widthHint = COUPON_PREFIX_TEXT_WIDTH;
		couponPrefixText.setLayoutData(gridData);

		if (ruleParameter.getValue() != null) {
			// populate the text field
			couponPrefixText.setText(ruleParameter.getValue());
		}

		// bind the text field
		if (ruleParameter != null) {
			final EpControlBindingProvider bindingProvider = EpControlBindingProvider.getInstance();
			final EpValueBinding binding = bindingProvider.bind(dataBindingContext, couponPrefixText, ruleParameter, RULE_PARAMETER_BIND_VALUE,
					new CompoundValidator(EpValidatorFactory.MAX_LENGTH_16, EpValidatorFactory.ALPHANUMERIC_REQUIRED),
					null, false);
			addDisposeListener(couponPrefixText, dataBindingContext, binding);
		}
	}

	/**
	 * Adds to the given ruleComposite <code>IEpLayoutComposite</code> a coupon assignment combo box.
	 *
	 * @param ruleParameter         the <code>RuleParameter</code> object to display
	 * @param ruleComposite         the <code>IEpLayoutComposite</code> to which the coupon assignment combo box should be added to
	 * @param dataBindingContext    the <code>DataBindingContext</code>
	 * @param policyActionContainer the <code>PolicyActionContainer</code> managing component state
	 * @param wizardPage            wizard page.
	 * @param sectionPart           section part.
	 */
	public void addPromotionCombo(final RuleParameter ruleParameter, final IPolicyTargetLayoutComposite ruleComposite,
								  final DataBindingContext dataBindingContext, final PolicyActionContainer policyActionContainer,
								  final PromotionRulesDefinitionPart sectionPart, final NewPromotionWizardRulesPage wizardPage) {
		// create the combo viewer
		final ComboViewer promotionCombo = ruleComposite.addComboViewer(ruleComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true,
				true, 1, 2), policyActionContainer);
		promotionCombo.setContentProvider(new ArrayContentProvider());
		promotionCombo.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(final Object element) {
				Rule rule = (Rule) element;
				return rule.getName();
			}
		});
		promotionCombo.setFilters(createViewerFilters());

		// set input
		final Map<String, Rule> rules = getRulesByScenarioAndStore(scenario, store);
		promotionCombo.setInput(rules.values().toArray());

		// set default selection
		Rule selectedRule = rules.get(ruleParameter.getValue());
		if (selectedRule == null && promotionCombo.getCCombo().getItemCount() > 0) {
			selectedRule = (Rule) promotionCombo.getElementAt(0);
		}

		if (selectedRule != null) {
			promotionCombo.setSelection(new StructuredSelection(selectedRule));
			ruleParameter.setValue(selectedRule.getCode());
		}

		if (wizardPage != null) {
			wizardPage.setPageComplete(wizardPage.isPageComplete());
		}

		// bind the combo box
		final EpControlBindingProvider bindingProvider = EpControlBindingProvider.getInstance();
		final EpValueBinding binding = bindingProvider.bind(dataBindingContext, promotionCombo.getControl(), null, null,
				new ObservableUpdateValueStrategy() {
					@Override
					protected IStatus doSet(final IObservableValue observableValue, final Object value) {
						final int selectedIndex = (Integer) value;
						try {
							if (selectedIndex >= 0) {
								Rule selectedRule = (Rule) promotionCombo.getElementAt(selectedIndex);
								ruleParameter.setValue(selectedRule.getCode());

								return Status.OK_STATUS;
							}
							return new Status(IStatus.ERROR, StorePlugin.PLUGIN_ID, "Cannot create a promotion without a coupon code.");
							//$NON-NLS-1$
						} catch (final EpServiceException e) {
							return new Status(IStatus.WARNING, StorePlugin.PLUGIN_ID, "Cannot set the rule parameter coupon."); //$NON-NLS-1$
						}
					}
				}, true);

		addDisposeListener(promotionCombo.getControl(), dataBindingContext, binding);
	}

	private ViewerFilter[] createViewerFilters() {
		List<ViewerFilter> filters = new ArrayList<>();
		filters.add(createLimitedUseCouponElementFilter());
		filters.add(createParentPromotionFilter(rule));
		filters.add(createLimitedPerSpecificUserCouponFilter());

		return filters.toArray(new ViewerFilter[filters.size()]);
	}

	// A non user-specific limited use coupon would give the customer a coupon
	// that another customer might use up.
	private ViewerFilter createLimitedPerSpecificUserCouponFilter() {
		return new ViewerFilter() {
			@Override
			public boolean select(final Viewer viewer, final Object parentElement, final Object element) {
				Rule rule = (Rule) element;
				CouponConfig couponConfig = couponConfigService.findByRuleCode(rule.getCode());
				return !(couponConfig == null || !CouponUsageType.LIMIT_PER_SPECIFIED_USER.equals(couponConfig.getUsageType()));
			}
		};
	}

	// filter non limited use coupon
	private ViewerFilter createLimitedUseCouponElementFilter() {
		return new ViewerFilter() {
			@Override
			public boolean select(final Viewer viewer, final Object parentElement, final Object element) {
				Rule rule = (Rule) element;
				for (RuleElement ruleElement : rule.getRuleElements()) {
					if (ruleElement.getElementType().equals(RuleElementType.LIMITED_USE_COUPON_CODE_CONDITION)) {
						return true;
					}
				}

				return false;
			}
		};
	}

	private ViewerFilter createParentPromotionFilter(final Rule parentRule) {
		return new ViewerFilter() {
			@Override
			public boolean select(final Viewer viewer, final Object parentElement, final Object element) {
				Rule rule = (Rule) element;
				return !parentRule.equals(rule);
			}
		};
	}

	private Map<String, Rule> getRulesByScenarioAndStore(final int scenario, final Store store) {
		Map<String, Rule> result = new HashMap<>();
		final RuleService ruleService = ServiceLocator.getService(ContextIdNames.RULE_SERVICE);

		Collection<Rule> rules = ruleService.findByScenarioAndStore(scenario, store.getCode());
		for (Rule rule : rules) {
			result.put(rule.getCode(), rule);
		}

		return result;
	}

	/**
	 * Adds to the given ruleComposite <code>IEpLayoutComposite</code> a product link, which opens a product find dialog when clicked.
	 *
	 * @param ruleParameter         the <code>RuleParameter</code> object to display
	 * @param ruleComposite         the <code>IEpLayoutComposite</code> to which the product link should be added to
	 * @param abstractFormPart      the <code>AbstractFormPart</code>
	 * @param policyActionContainer the <code>PolicyActionContainer</code> managing component state
	 * @param wizardPage            the parent wizard page if the link is on a wizard page, null otherwise
	 * @param layoutRefresher       Called when a ui layout refresh is required.
	 */
	public void addProductFinderLink(final RuleParameter ruleParameter, final IPolicyTargetLayoutComposite ruleComposite,
									 final AbstractFormPart abstractFormPart, final PolicyActionContainer policyActionContainer, final WizardPage
											 wizardPage,
									 final LayoutRefresher layoutRefresher) {

		// create the hyperlink
		final Hyperlink productFinderLink = ruleComposite.addHyperLinkText(PromotionsMessages.get().PromoRulesDefinition_Label_Select,
				ruleComposite.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.CENTER, false, false), policyActionContainer);
		productFinderLink.pack();
		productFinderLink.setToolTipText(PromotionsMessages.get().PromoRulesDefinition_Tooltip_EditValue);

		// set the hyperlink text
		if (ruleParameter.getValue() == null) {
			productFinderLink.setText(PromotionsMessages.get().PromoRulesDefinition_Label_Select);
		} else {
			final ProductLookup productLookup = ServiceLocator.getService(
					ContextIdNames.PRODUCT_LOOKUP);
			final Product product = productLookup.findByGuid(ruleParameter.getValue());
			if (product != null) {
				productFinderLink.setText(product.getDisplayName(CorePlugin.getDefault().getDefaultLocale()));
				productFinderLink.setToolTipText(product.getDisplayName(CorePlugin.getDefault().getDefaultLocale()));
			}
		}

		// add the listeners
		productFinderLink.addMouseListener(new LayingOutMouseAdapter(layoutRefresher) {
			@Override
			public void mouseDown(final MouseEvent mouseEvent) {
				Catalog catalogToSearch = catalog;
				if (scenario == RuleScenarios.CART_SCENARIO) {
					catalogToSearch = store.getCatalog();
				}
				final ProductFinderDialog productFindDialog = new ProductFinderDialog(
						ruleComposite.getSwtComposite().getShell(), catalogToSearch, true);
				final int result = productFindDialog.open();
				if (result == Window.OK) {
					final Product selectedProduct = (Product) productFindDialog.getSelectedObject();
					if (selectedProduct != null) {
						ruleParameter.setValue(selectedProduct.getCode());
						productFinderLink.setText(String.valueOf(selectedProduct.getDisplayName(CorePlugin.getDefault().getDefaultLocale())));
						productFinderLink.setToolTipText(selectedProduct.getDisplayName(CorePlugin.getDefault().getDefaultLocale()));

						refreshLayout();
					}
					if (abstractFormPart != null) {
						abstractFormPart.markDirty();
					}

					// after the finder dialog closes, directly call the wizard page's 
					// setPageComplete method to enable or disable the finish button
					if (wizardPage != null) {
						wizardPage.setPageComplete(wizardPage.isPageComplete());
					}
				}
			}
		});
		ruleComposite.getSwtComposite().pack();
		ruleComposite.getSwtComposite().getParent().getParent().layout(true);
	}

	/**
	 * Adds to the given ruleComposite <code>IEpLayoutComposite</code> a SKU link, which opens a product find dialog when clicked.
	 *
	 * @param ruleParameter         the <code>RuleParameter</code> object to display
	 * @param ruleComposite         the <code>IEpLayoutComposite</code> to which the sku link should be added to
	 * @param abstractFormPart      the <code>AbstractFormPart</code>
	 * @param policyActionContainer the <code>PolicyActionContainer</code> managing component state
	 * @param wizardPage            the parent wizard page if the link is on a wizard page, null otherwise
	 * @param layoutRefresher       layout refresher
	 */
	public void addSkuFinderLink(final RuleParameter ruleParameter, final IPolicyTargetLayoutComposite ruleComposite,
								 final AbstractFormPart abstractFormPart, final PolicyActionContainer policyActionContainer, final WizardPage
										 wizardPage,
								 final LayoutRefresher layoutRefresher) {

		// create the hyperlink
		final Hyperlink skuFinderLink = ruleComposite.addHyperLinkText(PromotionsMessages.get().PromoRulesDefinition_Label_Select,
				ruleComposite.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.CENTER, false, false), policyActionContainer);
		skuFinderLink.pack();
		skuFinderLink.setToolTipText(PromotionsMessages.get().PromoRulesDefinition_Tooltip_EditValue);

		// set the hyperlink text
		if (ruleParameter.getValue() == null) {
			skuFinderLink.setText(PromotionsMessages.get().PromoRulesDefinition_Label_Select);
		} else {
			skuFinderLink.setText(ruleParameter.getValue());
		}

		// add the listeners
		skuFinderLink.addMouseListener(new LayingOutMouseAdapter(layoutRefresher) {
			@Override
			public void mouseDown(final MouseEvent mouseEvent) {
				Catalog cat = catalog;
				if (scenario == RuleScenarios.CART_SCENARIO) {
					cat = store.getCatalog();
				}
				final SkuFinderDialog skuFinderDialog = new SkuFinderDialog(ruleComposite.getSwtComposite().getShell(), cat, true, false);

				final int result = skuFinderDialog.open();
				if (result == Window.OK) {

					final Object selectedObject = skuFinderDialog.getSelectedObject();
					ProductSku selectedProductSku = null;
					String linkText = PromotionsMessages.get().PromoRulesDefinition_Label_Select;
					String value = null;

					if (selectedObject != null) {
						if (selectedObject instanceof Product) {
							selectedProductSku = ((Product) skuFinderDialog.getSelectedObject()).getDefaultSku();
						} else if (selectedObject instanceof ProductSku) {
							selectedProductSku = (ProductSku) skuFinderDialog.getSelectedObject();
						}
						if (disallowGiftCertificates && isGiftCertificate(selectedProductSku)) {
							if (wizardPage != null) {
								wizardPage.setErrorMessage(PromotionsMessages.get().CreatePromotion_GiftCertificateError);
							}
							skuFinderLink.setToolTipText(PromotionsMessages.get().CreatePromotion_GiftCertificateError);
						} else {
							linkText = selectedProductSku.getSkuCode();
							value = String.valueOf(selectedProductSku.getSkuCode());
							skuFinderLink.setToolTipText(selectedProductSku.getDisplayName(CorePlugin.getDefault().getDefaultLocale()));
						}
					}

					ruleParameter.setValue(value);
					skuFinderLink.setText(linkText);

					refreshLayout();

					if (abstractFormPart != null) {
						abstractFormPart.markDirty();
					}

					// after the SkuFinderDialog closes, directly call the wizard page's 
					// setPageComplete method to enable or disable the finish button
					if (wizardPage != null) {
						wizardPage.setPageComplete(wizardPage.isPageComplete());
					}
				}
			}
		});
		ruleComposite.getSwtComposite().pack();
		ruleComposite.getSwtComposite().getParent().getParent().layout(true);
	}

	private boolean isGiftCertificate(final ProductSku productSku) {
		return productSku.getProduct().getProductType().isGiftCertificate();
	}

	/**
	 * Adds to the given ruleComposite <code>IEpLayoutComposite</code> a category link, which opens a category find dialog when clicked.
	 *
	 * @param ruleParameter         the <code>RuleParameter</code> object to display
	 * @param ruleComposite         the <code>IEpLayoutComposite</code> to which the category link should be added to
	 * @param abstractFormPart      the <code>AbstractFormPart</code>
	 * @param policyActionContainer the <code>PolicyActionContainer</code> managing component states
	 * @param wizardPage            the parent wizard page if the link is on a wizard page, null otherwise
	 * @param layoutRefresher       the layout refresher
	 */
	public void addCategoryFinderLink(final RuleParameter ruleParameter, final IPolicyTargetLayoutComposite ruleComposite,
									  final AbstractFormPart abstractFormPart, final PolicyActionContainer policyActionContainer, final WizardPage
											  wizardPage,
									  final LayoutRefresher layoutRefresher) {

		// create the hyperlink
		final Hyperlink categoryFinderLink = ruleComposite.addHyperLinkText(PromotionsMessages.get().PromoRulesDefinition_Label_Select,
				ruleComposite.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.CENTER, false, false), policyActionContainer);
		categoryFinderLink.pack();
		categoryFinderLink.setToolTipText(PromotionsMessages.get().PromoRulesDefinition_Tooltip_EditValue);

		// set the hyperlink text
		if (ruleParameter.getValue() == null) {
			categoryFinderLink.setText(PromotionsMessages.get().PromoRulesDefinition_Label_Select);
		} else {
			categoryFinderLink.setText(ruleParameter.getValue());

			final CategoryLookup categoryLookup = ServiceLocator.getService(
					ContextIdNames.CATEGORY_LOOKUP);
			final Category category = categoryLookup.findByCompoundCategoryAndCatalogCodes(ruleParameter.getValue());
			if (category != null) {
				categoryFinderLink.setText(category.getDisplayName(CorePlugin.getDefault().getDefaultLocale()));
				categoryFinderLink.setToolTipText(category.getDisplayName(CorePlugin.getDefault().getDefaultLocale()));
			}
		}

		// add the listeners
		categoryFinderLink.addMouseListener(new LayingOutMouseAdapter(layoutRefresher) {
			@Override
			public void mouseDown(final MouseEvent mouseEvent) {
				Catalog catalogToSearch = getCatalog();
				final CategoryFinderDialog categoryFinderDialog =
						new CategoryFinderDialog(ruleComposite.getSwtComposite().getShell(),
								catalogToSearch);
				final int result = categoryFinderDialog.open();
				if (result == Window.OK) {
					final Category selectedCategory = (Category) categoryFinderDialog.getSelectedObject();

					if (selectedCategory == null) {
						ruleParameter.setValue(null);
					} else {
						ruleParameter.setValue(selectedCategory.getCompoundGuid());
						categoryFinderLink.setText(String.valueOf(selectedCategory.getDisplayName(CorePlugin.getDefault().getDefaultLocale())));

						refreshLayout();
						categoryFinderLink.setToolTipText(selectedCategory.getDisplayName(CorePlugin.getDefault().getDefaultLocale()));
					}
					if (abstractFormPart != null) {
						abstractFormPart.markDirty();
					}

					// after the finder dialog closes, directly call the wizard page's 
					// setPageComplete method to enable or disable the finish button
					if (wizardPage != null) {
						wizardPage.setPageComplete(wizardPage.isPageComplete());
					}
				}
			}

		});
		ruleComposite.getSwtComposite().pack();
		ruleComposite.getSwtComposite().getParent().getParent().layout(true);
	}

	private Catalog getCatalog() {
		Catalog catalogToSearch = catalog;
		if (scenario == RuleScenarios.CART_SCENARIO) {
			catalogToSearch = store.getCatalog();
		}
		return catalogToSearch;
	}

	/**
	 * Adds a listener to the given <code>Text</code> widget that ensures that all input is numeric.
	 *
	 * @param text the <code>Text</code> widget used for numeric input
	 */
	private void ensureNumericInput(final Text text) {
		// verify that input is numeric
		text.addListener(SWT.Verify, (Listener) event -> {
			final String string = event.text;
			final char[] chars = new char[string.length()];
			string.getChars(0, chars.length, chars, 0);
			for (char aChar : chars) {
				if (!(('0' <= aChar && aChar <= '9') || (aChar == '.'))) {
					event.doit = false;
					return;
				}
			}
		});
	}

	/**
	 * Creates the appropriate SWT widget depending on the type of rule parameter passed.
	 *
	 * @param parentWidget          the parent widget, it's either an instance of {@link NewPromotionWizardRulesPage} or
	 *                              {@link PromotionRulesDefinitionPart}
	 * @param ruleParameter         the <code>RuleParameter</code> object to bind the widget to
	 * @param parentComposite       the <code>Composite</code> to which the number of items link should be added to
	 * @param policyActionContainer the policy action container to add to.
	 */
	public void createRuleParameterControl(final Object parentWidget, final RuleParameter ruleParameter,
										   final IPolicyTargetLayoutComposite parentComposite, final PolicyActionContainer policyActionContainer) {
		DataBindingContext bindingContext = null;
		NewPromotionWizardRulesPage epWizardPage = null;
		PromotionRulesDefinitionPart epSectionPart = null;

		LayoutRefresher layoutRefresher = null;

		if (parentWidget instanceof NewPromotionWizardRulesPage) {
			epWizardPage = (NewPromotionWizardRulesPage) parentWidget;
			bindingContext = epWizardPage.getDataBindingContext();
			layoutRefresher = epWizardPage;
		}
		if (parentWidget instanceof AbstractCmClientFormSectionPart) {
			epSectionPart = (PromotionRulesDefinitionPart) parentWidget;
			bindingContext = epSectionPart.getBindingContext();
			layoutRefresher = epSectionPart;
		}

		String paramKey = ruleParameter.getKey();

		switch (paramKey) {
			case RuleParameter.CATEGORY_CODE_KEY:
				addCategoryFinderLink(ruleParameter, parentComposite, epSectionPart, policyActionContainer, epWizardPage, layoutRefresher);
				break;
			case RuleParameter.PRODUCT_CODE_KEY:
				addProductFinderLink(ruleParameter, parentComposite, epSectionPart, policyActionContainer, epWizardPage, layoutRefresher);
				break;
			case RuleParameter.DISCOUNT_AMOUNT_KEY:
				addDiscountAmountText(ruleParameter, parentComposite, bindingContext, policyActionContainer);
				break;
			case RuleParameter.DISCOUNT_PERCENT_KEY:
				addDiscountPercentageText(ruleParameter, parentComposite, bindingContext, policyActionContainer);
				break;
			case RuleParameter.CURRENCY_KEY:
				addCurrencyCombo(ruleParameter, parentComposite, bindingContext, policyActionContainer);
				break;
			case RuleParameter.SUBTOTAL_AMOUNT_KEY:
				addSubtotalAmountText(ruleParameter, parentComposite, bindingContext, policyActionContainer);
				break;
			case RuleParameter.NUM_ITEMS_KEY:
				addNumItemsSpinner(ruleParameter, parentComposite, bindingContext, policyActionContainer);
				break;
			case RuleParameter.SKU_CODE_KEY:
				addSkuFinderLink(ruleParameter, parentComposite, epSectionPart, policyActionContainer, epWizardPage, layoutRefresher);
				break;
			case RuleParameter.SHIPPING_OPTION_CODE_KEY:
				addShippingOptionCombo(ruleParameter, parentComposite, bindingContext, policyActionContainer);
				break;
			case RuleParameter.BOOLEAN_KEY:
				addBooleanKeyCombo(ruleParameter, parentComposite, bindingContext, policyActionContainer);
				break;
			case RuleParameter.NUM_ITEMS_QUANTIFIER_KEY:
				addNumItemsQuantifierCombo(ruleParameter, parentComposite, bindingContext, policyActionContainer);
				break;
			case RuleParameter.BRAND_CODE_KEY:
				addBrandCombo(ruleParameter, parentComposite, bindingContext, policyActionContainer);
				break;
			case RuleParameter.RULE_CODE_KEY :
				addPromotionCombo(ruleParameter, parentComposite, bindingContext, policyActionContainer, epSectionPart, epWizardPage);
				break;
			case RuleParameter.COUPON_PREFIX:
				addCouponPrefixText(ruleParameter, parentComposite, bindingContext, policyActionContainer);
				break;
			default:

				List<PromotionWidgetCreator> promotionWidgetCreators = PluginHelper.findPromotionWidgetCreators(StorePlugin.PLUGIN_ID, paramKey);
				for (PromotionWidgetCreator promotionWidgetCreator : promotionWidgetCreators) {
					promotionWidgetCreator.execute(this, ruleParameter, parentComposite, bindingContext, policyActionContainer);
				}
				break;
		}


	}

	private void addDisposeListener(final Control control, final DataBindingContext bindingConext, final EpValueBinding binding) {
		control.addDisposeListener((DisposeListener) disposeEvent -> bindingConext.removeBinding(binding.getBinding()));
	}
}
