/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.store.promotions.editors;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;

import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.editors.AbstractCmClientEditorPageSectionPart;
import com.elasticpath.cmclient.core.editors.AbstractCmClientFormEditor;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpDateTimePicker;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.validation.CompoundValidator;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.cmclient.store.StorePlugin;
import com.elasticpath.cmclient.store.StoreUtils;
import com.elasticpath.cmclient.store.promotions.PromotionsMessages;
import com.elasticpath.cmclient.store.promotions.PromotionsPermissions;
import com.elasticpath.domain.rules.Rule;

/**
 * This class implements the section of the Promotion editor that displays store information about
 * a promotion.
 */
public class PromotionCatalogRulesSection extends AbstractCmClientEditorPageSectionPart {
	
	private IEpDateTimePicker activeFromDateTimePicker;
	
	private IEpDateTimePicker activeToDateTimePicker;
	
	private Button visibleInStore;
	
	private Text ruleState;
	
	private final boolean catalogPromotion;
	
	private IEpLayoutComposite mainPane;

	/**
	 * Default constructor.
	 *
	 * @param formPage the form page
	 * @param editor the editor
	 * @param catalogPromotion whether to create the part for catalog promotions
	 */
	public PromotionCatalogRulesSection(final FormPage formPage, final AbstractCmClientFormEditor editor, final boolean catalogPromotion) {
		super(formPage, editor, ExpandableComposite.TITLE_BAR | ExpandableComposite.COMPACT);
		this.catalogPromotion = catalogPromotion;
	}

	@Override
	protected void bindControls(final DataBindingContext bindingContext) {
		final EpControlBindingProvider bindingProvider = EpControlBindingProvider.getInstance();
		
		if (!catalogPromotion) {
			bindingProvider.bind(bindingContext, visibleInStore, null, null, new ObservableUpdateValueStrategy() {
				@Override
				protected IStatus doSet(final IObservableValue observableValue, final Object value) {
					getModel().setEnabled(visibleInStore.getSelection());
					ruleState.setText(StoreUtils.getPromotionState(getModel()));
					return Status.OK_STATUS;
				}
			}, true);
		}

		bindingProvider.bind(bindingContext, activeFromDateTimePicker.getSwtText(), EpValidatorFactory.DATE_TIME_REQUIRED, null,
				new ObservableUpdateValueStrategy() {
					@Override
					protected IStatus doSet(final IObservableValue observableValue, final Object value) {
						getModel().setStartDate(activeFromDateTimePicker.getDate());
						ruleState.setText(StoreUtils.getPromotionState(getModel()));
						return Status.OK_STATUS;
					}
				}, true);

		// active from date
		bindingProvider.bind(bindingContext, activeToDateTimePicker.getSwtText(), new CompoundValidator(new IValidator[] {
				EpValidatorFactory.DATE_TIME, value -> {
			if (activeToDateTimePicker.getDate() != null
					&& activeToDateTimePicker.getDate().before(activeFromDateTimePicker.getDate())) {
				return new Status(IStatus.ERROR, StorePlugin.PLUGIN_ID, IStatus.ERROR,
						PromotionsMessages.get().CreatePromotionWizardDetailsPage_Date_Error, null);
			}
			return Status.OK_STATUS;
		}}), null, new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object value) {
				getModel().setEndDate(activeToDateTimePicker.getDate());
				ruleState.setText(StoreUtils.getPromotionState(getModel()));
				return Status.OK_STATUS;
			}
		}, true);
		
		mainPane.setControlModificationListener(getEditor());
	}

	@Override
	protected void createControls(final Composite parentComposite, final FormToolkit toolkit) {
		EpState epState;
		AuthorizationService authService = AuthorizationService.getInstance();
		boolean isAuthorized = authService.isAuthorizedWithPermission(PromotionsPermissions.PROMOTION_MANAGE)
				&& authService.isAuthorizedForCatalog(getModel().getCatalog()) && authService.isAuthorizedForStore(getModel().getStore());
		if (isAuthorized) {
			epState = EpState.EDITABLE;
		} else {
			epState = EpState.READ_ONLY;
		}
		
		mainPane = CompositeFactory.createTableWrapLayoutComposite(parentComposite, 2, false);
		final TableWrapData data = new TableWrapData(TableWrapData.FILL, TableWrapData.FILL);
		data.grabHorizontal = false;
		mainPane.setLayoutData(data);
		
		final IEpLayoutData labelData = mainPane.createLayoutData(IEpLayoutData.END, IEpLayoutData.CENTER);
		final IEpLayoutData fieldData = mainPane.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.CENTER, true, false);
		
		if (!catalogPromotion) {
			mainPane.addLabelBold(PromotionsMessages.get().PromoDetailsOverview_StoreVisible, labelData);
			visibleInStore = mainPane.addCheckBoxButton("", epState, fieldData); //$NON-NLS-1$
		}
		
		mainPane.addLabelBoldRequired(PromotionsMessages.get().PromoDetailsOverview_Label_ActiveFrom, epState, labelData);
		activeFromDateTimePicker = mainPane.addDateTimeComponent(IEpDateTimePicker.STYLE_DATE_AND_TIME, epState, fieldData);
		
		mainPane.addLabelBold(PromotionsMessages.get().PromoDetailsOverview_Label_ActiveTo, labelData);
		activeToDateTimePicker = mainPane.addDateTimeComponent(IEpDateTimePicker.STYLE_DATE_AND_TIME, epState, fieldData);
		
		mainPane.addLabelBold(PromotionsMessages.get().PromoStoreRules_State, labelData);
		ruleState = mainPane.addTextField(EpState.READ_ONLY, fieldData);
	}

	@Override
	protected void populateControls() {
		activeFromDateTimePicker.setDate(getModel().getStartDate());
		activeToDateTimePicker.setDate(getModel().getEndDate());
		
		if (!catalogPromotion) {
			visibleInStore.setSelection(getModel().isEnabled());
		}
		
		ruleState.setText(StoreUtils.getPromotionState(getModel()));
	}

	@Override
	public Rule getModel() {
		return (Rule) getEditor().getModel();
	}
	
	@Override
	protected String getSectionTitle() {
		return PromotionsMessages.get().PromoDetailsCatalogRules_Title;
	}
}
