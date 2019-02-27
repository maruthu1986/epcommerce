/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.store.promotions.editors;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.ControlModificationListener;
import com.elasticpath.cmclient.core.ui.framework.EpLocalizedPropertyController;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.validation.CompoundValidator;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareEditorPageSectionPart;
import com.elasticpath.cmclient.policy.ui.IPolicyTargetLayoutComposite;
import com.elasticpath.cmclient.policy.ui.PolicyTargetCompositeFactory;
import com.elasticpath.cmclient.store.StorePlugin;
import com.elasticpath.cmclient.store.promotions.PromotionsMessages;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.service.rules.RuleService;

/**
 * UI representation of the customer details profile basic section.
 */
public class PromotionDetailsOverviewPart extends AbstractPolicyAwareEditorPageSectionPart {

	private static final int NUM_COLUMNS = 2;

	private static final int DESCRIPTION_TEXT_AREA_HEIGHT = 80;
	
	private static final String ORIG_PROMO_NAME_KEY = "origPromoNameKey"; //$NON-NLS-1$

	private Text storeText;
	
	private Text catalogText;

	private Text promotionNameText;

	private Text descriptionText;

	private Text createdByText;

	private Text promotionTypeText;

	private final Rule rule;

	private IPolicyTargetLayoutComposite mainPane;

	private final ControlModificationListener controlModificationListener;
	
	private final boolean catalogPromotion;
	
	private EpLocalizedPropertyController nameController;

	private CCombo languageCombo;
	
	private Text displayNameText;
	
	private static final int MAX_NAME_LENGTH = 255;
	
	/**
	 * Constructor.
	 * 
	 * @param editor the editor containing this Section Constructor to create a new Section in an editor's FormPage.
	 * @param formPage the form page
	 * @param catalogPromotion whether to create the part for a catalog promotion
	 */
	public PromotionDetailsOverviewPart(final FormPage formPage, final AbstractCmClientFormEditor editor,
			final boolean catalogPromotion) {
		super(formPage, editor, ExpandableComposite.TITLE_BAR | ExpandableComposite.COMPACT);
		this.rule = (Rule) editor.getModel();
		this.controlModificationListener = editor;
		this.catalogPromotion = catalogPromotion;
	}

	@Override
	protected void createControls(final Composite parentComposite, final FormToolkit toolkit) {
		final PolicyActionContainer overviewDisplayControls = addPolicyActionContainer("overviewDisplayControls"); //$NON-NLS-1$
		final PolicyActionContainer detailControls = addPolicyActionContainer("overviewEditControls"); //$NON-NLS-1$

		this.mainPane = PolicyTargetCompositeFactory.wrapLayoutComposite(
				CompositeFactory.createTableWrapLayoutComposite(parentComposite, NUM_COLUMNS, false));
		final TableWrapData data = new TableWrapData(TableWrapData.FILL, TableWrapData.FILL);
		data.grabHorizontal = true;
		this.mainPane.setLayoutData(data);

		final IEpLayoutData labelData = this.mainPane.createLayoutData(IEpLayoutData.END, IEpLayoutData.BEGINNING);
		final IEpLayoutData fieldData = this.mainPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.BEGINNING, true, false);
		
		mainPane.addLabelBold(PromotionsMessages.get().PromoDetailsOverview_Label_PromotionType, labelData, overviewDisplayControls);
		promotionTypeText = mainPane.addTextField(fieldData, overviewDisplayControls);
		
		if (catalogPromotion) {
			mainPane.addLabelBold(PromotionsMessages.get().Promotion_Catalog, labelData, overviewDisplayControls);
			catalogText = mainPane.addTextField(fieldData, overviewDisplayControls);
		} else {
			mainPane.addLabelBold(PromotionsMessages.get().Promotion_Store, labelData, overviewDisplayControls);
			storeText = mainPane.addTextField(fieldData, overviewDisplayControls);
		}

		this.mainPane.addLabelBoldRequired(PromotionsMessages.get().PromoDetailsOverview_Label_PromotionName, labelData, detailControls);
		this.promotionNameText = this.mainPane.addTextField(fieldData, detailControls);
		
		this.mainPane.addLabelBold(PromotionsMessages.get().PromoDetailsOverview_Label_DisplayName, labelData, detailControls);
		IPolicyTargetLayoutComposite nameComposite = mainPane.addTableWrapLayoutComposite(2, false, 
				mainPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true,	true), detailControls);
		languageCombo = nameComposite.addComboBox(nameComposite.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.FILL,
				false, false), detailControls);
		displayNameText = nameComposite.addTextField(nameComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL,
				true, false), detailControls);
		displayNameText.setTextLimit(MAX_NAME_LENGTH);
		
		nameController = EpLocalizedPropertyController.createEpLocalizedPropertyController(displayNameText, languageCombo,
				Rule.LOCALIZED_PROPERTY_DISPLAY_NAME, false, null, EpValidatorFactory.MAX_LENGTH_255);
		
		this.mainPane.addLabelBold(PromotionsMessages.get().PromoDetailsOverview_Label_Description, labelData, detailControls);
		this.descriptionText = this.mainPane.addTextArea(fieldData, detailControls);
		final TableWrapData twdDescriptionText = new TableWrapData();
		twdDescriptionText.heightHint = DESCRIPTION_TEXT_AREA_HEIGHT;
		this.descriptionText.setLayoutData(twdDescriptionText);

		this.mainPane.addLabelBold(PromotionsMessages.get().PromoDetailsOverview_Label_CreatedBy, labelData, overviewDisplayControls);
		this.createdByText = this.mainPane.addTextField(fieldData, overviewDisplayControls);
		
		addCompositesToRefresh(mainPane.getSwtComposite().getParent(), mainPane.getSwtComposite());
	}

	@Override
	protected void populateControls() {
		// Populate Promotion Type
		promotionTypeText.setText(rule.getRuleSet().getName());

		List<Locale> supportedLocales = new ArrayList<>();
		Locale defaultLocale;

		// Set catalog or store specific information
		if (catalogPromotion) {
			catalogText.setText(rule.getCatalog().getName());
			supportedLocales.addAll(rule.getCatalog().getSupportedLocales());
			defaultLocale = rule.getCatalog().getDefaultLocale();
		} else {
			storeText.setText(rule.getStore().getName());
			supportedLocales.addAll(rule.getStore().getSupportedLocales());
			defaultLocale = rule.getStore().getDefaultLocale();
		}

		this.promotionNameText.setText(checkString(this.rule.getName()));
		this.promotionNameText.setData(ORIG_PROMO_NAME_KEY, this.rule.getName());
		this.descriptionText.setText(checkString(this.rule.getDescription()));
		this.createdByText.setText(getCmUserInfo());

		nameController.populate(supportedLocales, defaultLocale, rule.getLocalizedProperties());
		languageCombo.select(supportedLocales.indexOf(defaultLocale));
		
		// Set the modification listener *after* setting the control values
		// so that controls aren't considered to be modified when the initial value is set
		this.mainPane.setControlModificationListener(this.controlModificationListener);
		nameController.setControlModificationListener(controlModificationListener);
	}

	private String getCmUserInfo() {
		final CmUser cmUser = this.rule.getCmUser();
		if (cmUser == null) {
			return PromotionsMessages.get().PromotionDetailsPage_None;
		}
		
		return checkString(cmUser.getFirstName()) + " " + checkString(cmUser.getLastName()); //$NON-NLS-1$
	}

	private String checkString(final String stringText) {
		if (stringText != null) {
			return stringText;
		}
		return ""; //$NON-NLS-1$
	}

	@Override
	protected void bindControls(final DataBindingContext context) {
		final EpControlBindingProvider bindingProvider = EpControlBindingProvider.getInstance();

		// promotion name
		bindingProvider.bind(context, this.promotionNameText, this.rule, "name", //$NON-NLS-1$
				new CompoundValidator(new IValidator[]{EpValidatorFactory.STRING_255_REQUIRED, value -> {
					final String stringValue = (String) value;
					return isUniquePromotionName(stringValue);
				}}), null, true);
		
		// description
		bindingProvider.bind(context, this.descriptionText, this.rule, "description", //$NON-NLS-1$
				EpValidatorFactory.MAX_LENGTH_255, null, true);
		
		nameController.bind(context);
	}
	
	/**
	 * Validates whether or not the promotion name is unique.
	 */
	private IStatus isUniquePromotionName(final String stringValue) {
		final RuleService ruleService = ServiceLocator.getService(ContextIdNames.RULE_SERVICE);
		
		// check unique
		if (!stringValue.equals(promotionNameText.getData(ORIG_PROMO_NAME_KEY))) {
			markDirty();
			if ((ruleService.findByName(promotionNameText.getText()) != null)) {
				return new Status(IStatus.ERROR, StorePlugin.PLUGIN_ID, IStatus.ERROR, 
						PromotionsMessages.get().PromoDetailsOverview_Error_DuplicateName,
					null);
			}
		}
		return Status.OK_STATUS;
	}

	@Override
	protected String getSectionDescription() {
		return PromotionsMessages.get().PromoDetailsOverview_Description;
	}

	@Override
	protected String getSectionTitle() {
		return PromotionsMessages.get().PromoDetailsOverview_Title;
	}

	@Override
	public void commit(final boolean onSave) {
		if (onSave) {
			promotionNameText.setData(ORIG_PROMO_NAME_KEY, promotionNameText.getText());
			markStale();
		}
		super.commit(onSave);
	}
}
