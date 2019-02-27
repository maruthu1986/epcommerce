/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
/**
 * 
 */
package com.elasticpath.cmclient.store.targetedselling.conditionalexpression.views;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Text;

import com.elasticpath.cmclient.core.CorePlugin;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.event.EventType;
import com.elasticpath.cmclient.core.event.UIEvent;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.ui.framework.IEpTabFolder;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.cmclient.store.StorePlugin;
import com.elasticpath.cmclient.store.targetedselling.TargetedSellingImageRegistry;
import com.elasticpath.cmclient.store.targetedselling.TargetedSellingMessages;
import com.elasticpath.cmclient.store.targetedselling.conditionalexpression.model.ComboViewerModelBuilder;
import com.elasticpath.cmclient.store.targetedselling.conditionalexpression.model.ConditionalExpressionSearchTabModel;
import com.elasticpath.cmclient.store.targetedselling.conditionalexpression.model.impl.ConditionalExpressionModelImpl;
import com.elasticpath.cmclient.store.targetedselling.conditionalexpression.model.impl.TagDefinitionModelBuilderImpl;
import com.elasticpath.cmclient.store.targetedselling.conditionalexpression.model.impl.TagDictionaryModelBuilderImpl;
import com.elasticpath.cmclient.store.views.IStoreMarketingInnerTab;
import com.elasticpath.cmclient.store.views.SearchView;
import com.elasticpath.tags.domain.TagDefinition;
import com.elasticpath.tags.domain.TagDictionary;

/**
 * ConditionalExpressionSearchTab provides view with filtering conditions for tags expressions.
 */
@SuppressWarnings({ "PMD.CyclomaticComplexity", "PMD.ExcessiveMethodLength", "pmd.noFailureOnViolation" })
public class ConditionalExpressionSearchTab implements IStoreMarketingInnerTab {

	@SuppressWarnings("unused")
	private final SearchView searchView; //NOPMD

	private ConditionalExpressionSearchTabModel model;

	private DataBindingContext dataBindingCtx;

	private Text textConditionalExpressionName;
	private ComboViewer comboTagDictionaries;
	private ComboViewer comboTags;

	private final int tabIndex;

	/**
	 * Constructor.
	 * 
	 * @param tabFolder parent's searchView tab folder.
	 * @param tabIndex index of this tab into tabFolder.
	 * @param searchView parent SearchView.
	 */
	public ConditionalExpressionSearchTab(final IEpTabFolder tabFolder, final int tabIndex, final SearchView searchView) {

		Image tabImage = TargetedSellingImageRegistry.IMAGE_CONDITIONAL_EXPRESSION_TAB.createImage();
		IEpLayoutComposite tabComposite = tabFolder.addTabItem(
				TargetedSellingMessages.get().ConditionalExpressionTabTitle,
				tabImage,
				tabIndex, 
				1, 
				false);
		this.searchView = searchView;
		this.tabIndex = tabIndex;

		createTabItem(tabComposite);
	}

	/**
	 * Creates all sections of the tag expression tab.
	 * @param tabComposite the Layout Composite 
	 */
	@SuppressWarnings("checkstyle.MethodLength")
	private void createTabItem(final IEpLayoutComposite tabComposite) {

		this.model = getModel();
		this.dataBindingCtx = new DataBindingContext();
		IEpLayoutData layoutData = tabComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false);
		final IEpLayoutComposite epLayoutComposite = tabComposite.addGroup(
				TargetedSellingMessages.get().FindConditionalExpression,
				1, 
				false, 
				layoutData);

		// conditional expression name
		epLayoutComposite.addLabelBold(TargetedSellingMessages.get().ConditionalExpressionName, null);
		textConditionalExpressionName = epLayoutComposite.addTextField(EpState.EDITABLE, layoutData);
		
		// tag dictionary
		epLayoutComposite.addLabelBold(TargetedSellingMessages.get().ConditionalExpressionType, null);
		comboTagDictionaries = new ComboViewer(epLayoutComposite.getSwtComposite());
		comboTagDictionaries.setContentProvider(new ArrayContentProvider());
		comboTagDictionaries.setLabelProvider(new LabelProvider() {
			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
			 */
			@Override
			public String getText(final Object element) {
				TagDictionary model = (TagDictionary) element;
				return model.getName();
			}

		});
		comboTagDictionaries.setInput(new TagDictionaryModelBuilderImpl().getModel());
		comboTagDictionaries.getControl().setLayoutData(layoutData.getSwtLayoutData());

		// tags
		epLayoutComposite.addLabelBold(TargetedSellingMessages.get().ConditionalExpressionTag, null);

		comboTags = new ComboViewer(epLayoutComposite.getSwtComposite());
		comboTags.setContentProvider(new ArrayContentProvider());
		comboTags.setLabelProvider(new LabelProvider() {
			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
			 */
			@Override
			public String getText(final Object element) {
				TagDefinition model = (TagDefinition) element;
				return model.getLocalizedName(CorePlugin.getDefault().getDefaultLocale());
			}

		});
		comboTags.setInput(new TagDefinitionModelBuilderImpl().getModel());
		comboTags.getControl().setLayoutData(layoutData.getSwtLayoutData());

		comboTagDictionaries.getCombo().select(0);
		comboTags.getCombo().select(0);

		EpControlBindingProvider.getInstance().bind(
				dataBindingCtx, 
				textConditionalExpressionName, 
				getModel(), 
				"name",   //$NON-NLS-1$
				EpValidatorFactory.MAX_LENGTH_255, 
				null, 
				true);

		this.initListeners();
	}
	
	private void initListeners() {
		// Setup this class as the listener for the buttons
		this.comboTagDictionaries.addSelectionChangedListener(event -> {
			IStructuredSelection selection = (IStructuredSelection) event.getSelection();
			TagDictionary tagDictionary = (TagDictionary) selection.getFirstElement();
			if (tagDictionary != null && tagDictionary.getUidPk() > 0) {
				model.setTagDictionary(tagDictionary);
			} else {
				model.setTagDictionary(null);
			}
			model.setTagDefinition(null);
		});

		this.comboTags.addSelectionChangedListener(event -> {
			IStructuredSelection selection = (IStructuredSelection) event.getSelection();
			TagDefinition tagDefinition = (TagDefinition) selection.getFirstElement();
			if (tagDefinition != null && tagDefinition.getUidPk() > 0) {
				model.setTagDefinition(tagDefinition);
			} else {
				model.setTagDefinition(null);
			}
		});

		this.model.addPropertyListener(event -> {
			if ("tagDictionary".equals(event.getPropertyName())) { //$NON-NLS-1$
				TagDictionary tagDictionary = (TagDictionary) event.getNewValue();
				ComboViewerModelBuilder<TagDefinition> modelBuilder;
				if (tagDictionary == null) {
					modelBuilder = new TagDefinitionModelBuilderImpl();
				} else {
					modelBuilder = new TagDefinitionModelBuilderImpl(tagDictionary.getTagDefinitions());
				}
				comboTags.setInput(modelBuilder.getModel());
				comboTags.getCombo().select(0);
			}
		});
	}

	/**
	 * Get the model.
	 * @return The Search Tab model instance
	 */
	private ConditionalExpressionSearchTabModel getModel() {
		if (model == null) {
			return new ConditionalExpressionModelImpl();
		}
		return model;
	}

	/**
	 * Dispose object.
	 */
	public void dispose() {
		this.model = null;
	}

	@Override
	public void search() {
		UIEvent<ConditionalExpressionSearchTabModel> searchEvent =
				new UIEvent<>(model, EventType.SEARCH, true);
		StorePlugin.getDefault().getConditionalExpressionListController().onEvent(searchEvent);
	}

	@Override
	public void clear() {
		ConditionalExpressionSearchTab.this.model.setName(null);
		ConditionalExpressionSearchTab.this.model.setTagDictionary(null);
		ConditionalExpressionSearchTab.this.model.setTagDefinition(null);
		ConditionalExpressionSearchTab.this.model.setDynamicContentDelivery(null);
		textConditionalExpressionName.setText(""); //$NON-NLS-1$
		comboTagDictionaries.getCombo().select(0);
		comboTags.getCombo().select(0);
	}

	@Override
	public boolean isDisplaySearchButton() {
		return true;
	}

	@Override
	public int getTabIndex() {
		return tabIndex;
	}

}
