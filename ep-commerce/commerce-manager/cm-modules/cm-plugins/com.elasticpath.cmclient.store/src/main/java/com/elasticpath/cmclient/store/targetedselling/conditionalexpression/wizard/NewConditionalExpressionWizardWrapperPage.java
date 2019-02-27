/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.store.targetedselling.conditionalexpression.wizard;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.custom.CCombo;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.EpWizardPageSupport;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.cmclient.core.wizard.AbstractEPWizardPage;
import com.elasticpath.cmclient.core.wizard.AbstractEpWizard;
import com.elasticpath.cmclient.core.wizard.page.IBeforeFinishNotifier;
import com.elasticpath.cmclient.store.StorePlugin;
import com.elasticpath.cmclient.store.targetedselling.TargetedSellingMessages;
import com.elasticpath.cmclient.store.targetedselling.conditionalexpression.model.impl.TagDictionaryModelBuilderImpl;
import com.elasticpath.tags.domain.ConditionalExpression;
import com.elasticpath.tags.domain.TagDictionary;

/**
 * The new conditional expression wizard page where user is able to define the dictionary for the condition.
 */
public class NewConditionalExpressionWizardWrapperPage extends AbstractEPWizardPage<ConditionalExpression>
	implements IBeforeFinishNotifier {
	
	private CCombo dictionariesCombo;

	private List<TagDictionary> dictionaries;
	
	private static final Comparator<TagDictionary> DICTIONARY_COMPARATOR = new Comparator<TagDictionary>() {

		/**
		 * compare tag dictionaries by their name.
		 * @param td1 the first tag dictionary
		 * @param td2 the second tag dictionary
		 * @return integer result of comparison
		 */
		@Override
		public int compare(final TagDictionary td1, final TagDictionary td2) {
			if (td1 == null || td2 == null || td1.getName() == null || td2.getName() == null) {
				return 1;
			}
			return td1.getName().compareToIgnoreCase(td2.getName());
		}
		
	};

	/**
	 * Default constructor.
	 * 
	 * @param pageName the name of the page
	 * @param title the page title
	 */
	protected NewConditionalExpressionWizardWrapperPage(final String pageName, final String title) {
		super(2, false, pageName, title, TargetedSellingMessages.get().NewConditionalExpressionWizard_Description, new DataBindingContext());
	}

	@Override
	protected void createEpPageContent(final IEpLayoutComposite parent) {

		final IEpLayoutData fieldDataFill = parent.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.BEGINNING, true, false);

		parent.addLabelBoldRequired(TargetedSellingMessages.get().NewConditionalExpressionWizard_Dictionary_Combo_Label,
				EpState.EDITABLE, fieldDataFill);

		dictionariesCombo = parent.addComboBox(EpState.EDITABLE, fieldDataFill);

		/* MUST be called */
		setControl(parent.getSwtComposite());
		
	}

	@Override
	protected void populateControls() {

		TagDictionaryModelBuilderImpl tagDictionaryModelBuilder = new TagDictionaryModelBuilderImpl();

		final List<TagDictionary> dictionaries = tagDictionaryModelBuilder.getDictionariesForSavedConditions();
		
		if (null != dictionaries) {

			int selection = fillAndSortWrappersListAndMakeSelection(dictionaries);
			this.dictionariesCombo.select(selection); 
			this.dictionariesCombo.setEnabled(true); 
			
			// preselect
			//final TagDictionary tagDictionary = this.dictionaries.get(0);
			final TagDictionary tagDictionary = this.dictionaries.get(selection);
			getModel().setTagDictionaryGuid(tagDictionary.getGuid());

		}
	}
	
	/**
	 * fill in wrappers list in a sorted way.
	 * @param dictionaries the unsorted map with all content wrappers
	 */
	private int fillAndSortWrappersListAndMakeSelection(final List<TagDictionary> dictionaries) {

		final Set<TagDictionary> tempSet = new TreeSet<>(DICTIONARY_COMPARATOR);
		
		// sorting by name
		for (TagDictionary dictionary : dictionaries) {
			tempSet.add(dictionary);                                      
		}
		
		
		int index = 0;                                  
		int preselectDictionary = 0;					 
		String preselectDictionatyGuid = "SHOPPER";  //$NON-NLS-1$ 

		this.dictionaries = new ArrayList<>();
		for (TagDictionary dictionary : tempSet) {
			addToContentWrapperListAndUpdateComboSelection(dictionary);
			if (preselectDictionatyGuid.equals(dictionary.getGuid())) {    // to be removed
				preselectDictionary = index;							   // to be removed
			}															   // to be removed
			index++;													   // to be removed
		}
		return preselectDictionary;						// to be removed
	}
	
	private void addToContentWrapperListAndUpdateComboSelection(final TagDictionary dictionary) {
		if (null != dictionary) {
			this.dictionaries.add(dictionary);
			this.dictionariesCombo.add(dictionary.getName());
		}
	}

	@Override
	protected void bindControls() {
		final EpControlBindingProvider bindingProvider = EpControlBindingProvider.getInstance();

		// Binds the target combo box using data binding
		bindTargetCombo(bindingProvider);
			
		//Create the new content wizard details page
		EpWizardPageSupport.create(NewConditionalExpressionWizardWrapperPage.this, getDataBindingContext());

	}

	/**
	 * Binds the content wrapper select combo-box drop down using data binding.
	 * 
	 * @param bindingProvider the provider of the binding
	 */
	private void bindTargetCombo(final EpControlBindingProvider bindingProvider) {

		// The observable update value strategy
		ObservableUpdateValueStrategy updateStrat = new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object value) {
				final int selectionIndex = (Integer) value;
				try {
					final TagDictionary tagDictionary = dictionaries.get(selectionIndex);
					getModel().setTagDictionaryGuid(tagDictionary.getGuid());
					((AbstractEpWizard<?>) getWizard()).getWizardDialog().updateButtons();
					return Status.OK_STATUS;
				} catch (final EpServiceException e) {
					return new Status(IStatus.WARNING, StorePlugin.PLUGIN_ID, "Cannot set the content wrapper."); //$NON-NLS-1$
				}
			}
		};

		// Bind the target combo box so that it is a required combo with the
		// first element not being valid, and update accordingly to the
		// update strategy
			bindingProvider.bind(getDataBindingContext(), dictionariesCombo, 
					EpValidatorFactory.REQUIRED,
					null, updateStrat, true);
	}

	@Override
	public boolean enableFinish() {
		return true;
	}

}
