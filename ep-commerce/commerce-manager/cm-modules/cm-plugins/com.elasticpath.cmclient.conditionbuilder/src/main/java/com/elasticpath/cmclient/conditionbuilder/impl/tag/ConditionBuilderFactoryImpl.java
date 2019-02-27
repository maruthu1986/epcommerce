/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
/**
 * 
 */
package com.elasticpath.cmclient.conditionbuilder.impl.tag;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

import com.elasticpath.cmclient.conditionbuilder.ConditionBuilderFactory;
import com.elasticpath.cmclient.conditionbuilder.adapter.ConditionModelAdapter;
import com.elasticpath.cmclient.conditionbuilder.adapter.DataAdapter;
import com.elasticpath.cmclient.conditionbuilder.adapter.LogicalOperatorModelAdapter;
import com.elasticpath.cmclient.conditionbuilder.adapter.impl.tag.ConditionModelAdapterImpl;
import com.elasticpath.cmclient.conditionbuilder.adapter.impl.tag.LogicalOperatorModelAdapterImpl;
import com.elasticpath.cmclient.conditionbuilder.adapter.impl.tag.ResourceAdapterFactoryImpl;
import com.elasticpath.cmclient.conditionbuilder.adapter.service.tag.impl.TagConditionModelValidationService;
import com.elasticpath.cmclient.conditionbuilder.component.ActionEventListener;
import com.elasticpath.cmclient.conditionbuilder.component.ConditionBlockComposite;
import com.elasticpath.cmclient.conditionbuilder.component.ConditionRowComposite;
import com.elasticpath.cmclient.conditionbuilder.component.TopLevelComposite;
import com.elasticpath.cmclient.conditionbuilder.extenders.ConditionBuilderPluginHelper;
import com.elasticpath.cmclient.conditionbuilder.extenders.ConditionRowCompositeCreator;
import com.elasticpath.cmclient.conditionbuilder.valueeditor.ConditionRowValueFactory;
import com.elasticpath.cmclient.conditionbuilder.valueeditor.ConditionRowValueFactoryImpl;
import com.elasticpath.cmclient.core.CorePlugin;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.helpers.ConditionCreator;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.tags.domain.Condition;
import com.elasticpath.tags.domain.LogicalOperator;
import com.elasticpath.tags.domain.LogicalOperatorType;
import com.elasticpath.tags.domain.TagDefinition;
import com.elasticpath.tags.domain.TagDictionary;
import com.elasticpath.tags.domain.TagGroup;
import com.elasticpath.tags.domain.TagOperator;
import com.elasticpath.tags.service.GenericService;
import com.elasticpath.tags.service.TagDictionaryService;
import com.elasticpath.tags.service.TagGroupService;

/**
 * ConditionBuilderFactoryImpl.
 *
 */
public class ConditionBuilderFactoryImpl
	implements ConditionBuilderFactory<LogicalOperator, Condition, TagDefinition, TagOperator, LogicalOperatorType, TagGroup> {

	private static final int HORIZONTAL_INDENT = 3;
	private Locale locale = CorePlugin.getDefault().getDefaultLocale();
	private GenericService<TagOperator> tagOperatorService;
	private String addButtonText = "add condition"; //$NON-NLS-1$
	private String conditionBuilderTitle;
	private DataBindingContext dataBindingContext;
	
	private ActionEventListener<Object> listenerForRefreshParentComposite;
	private ActionEventListener<Object> listenerForMarkEditorState;
	
	private Set<TagDefinition> tagDefinitionsList;
	private Set<TagGroup> tagGroupsList;
	private final ConditionRowValueFactory conditionRowValueFactory;
	
	private final DataAdapter<TagGroup, TagDefinition> dataAdapter =
        model -> new ArrayList<>(model.getTagDefinitions());
	
	private final ResourceAdapterFactoryImpl resourceAdapterFactory;
	private String dictionary;
	
	/**
	 * 
	 */
	public ConditionBuilderFactoryImpl() {
		super();
		resourceAdapterFactory = new ResourceAdapterFactoryImpl(locale);
		conditionRowValueFactory = new ConditionRowValueFactoryImpl(new TagConditionModelValidationService()); 
	}

	@Override
	public ConditionBlockComposite<LogicalOperator, TagDefinition, LogicalOperatorType, TagGroup> 
		createConditionBlockComposite(
			final TopLevelComposite<LogicalOperator, LogicalOperatorType> parent,
			final int swtStyle, final LogicalOperator model) {
		
		GridData gridData = new GridData(GridData.FILL, GridData.BEGINNING, true, false);

		final LogicalOperatorModelAdapter<LogicalOperator, LogicalOperatorType> 
				logicalOperatorModelAdapter = this.createLogicalOperatorModelAdapter(model);

		final ConditionBlockComposite<LogicalOperator, TagDefinition, LogicalOperatorType, TagGroup> 
		result =
            new ConditionBlockComposite<>(
                parent.getContainerComposite(),
                logicalOperatorModelAdapter,
                parent.getModel(),
                this.resourceAdapterFactory,
                this.dataAdapter,
                this.tagGroupsList,
                this.conditionBuilderTitle);

		result.setLayoutData(gridData);
		
		// listener for delete this composite
		result.addListenerForDelete(
            object -> {
				// delete UI composite
				result.dispose();
				// remove current logical operator
				parent.getModel().removeLogicalOperator(object.getModel());
				// refresh layout
				parent.layout();
				// remove listeners from adapter
				logicalOperatorModelAdapter.removeAllPropertyChangeListeners();
			});
		result.addListenerForDelete(
            object -> listenerForRefreshParentComposite.onEvent(null));

		// listener for add the child row composite
		result.addListenerForAdd(object -> {
			// create new condition
			Condition condition = ConditionCreator.createModel(object, null, ""); //$NON-NLS-1$ 

			model.addCondition(condition);
			// create new UI row
			ConditionBuilderFactoryImpl.this.createConditionRowComposite(result, swtStyle, condition);
			// refresh layout
			parent.getParent().layout();
		});
		result.addListenerForAdd(object -> listenerForRefreshParentComposite.onEvent(null));
		return result;
	}

	@Override
	public ConditionRowComposite<Condition, TagOperator, LogicalOperator, LogicalOperatorType> 
		createConditionRowComposite(
			final ConditionBlockComposite<LogicalOperator, TagDefinition, LogicalOperatorType, TagGroup> parent, 
			final int swtStyle, final Condition model) {

		GridData layoutData = new GridData(GridData.FILL, GridData.BEGINNING, false, false);
		layoutData.horizontalIndent = HORIZONTAL_INDENT;
		layoutData.verticalIndent = 2;
		
		final ConditionModelAdapter<Condition, TagOperator> 
			conditionModelAdapter = this.createConditionModelAdapter(model);
		
		ConditionRowComposite<Condition, TagOperator, LogicalOperator, LogicalOperatorType> result; 
		
		ConditionRowCompositeCreator<Condition, TagOperator, LogicalOperator, LogicalOperatorType> compositeCreator =
				ConditionBuilderPluginHelper.getRowCompositeCreator(model.getTagDefinition());
		
		if (compositeCreator == null) {
			result = new ConditionRowComposite<>(
		            parent.getContainerComposite(),
		            swtStyle,
		            conditionModelAdapter,
		            parent.getModel(),
		            this.dataBindingContext,
		            conditionRowValueFactory);
		} else {
			result = compositeCreator.createConditionRowComposite(
		            parent.getContainerComposite(),
		            swtStyle,
		            conditionModelAdapter,
		            parent.getModel(),
		            this.dataBindingContext,
		            conditionRowValueFactory);
		}

		result.setLayoutData(layoutData);
		parent.getModel().addCondition(model);
		
		result.addListenerForDelete(object -> {
			// delete UI composite
			result.dispose();
			// remove condition
			parent.getModel().removeCondition(model);
			// layout
			parent.getParent().getParent().layout();
			// remove listeners from adapter
			conditionModelAdapter.removeAllPropertyChangeListeners();
		});
		result.addListenerForDelete(
            object -> listenerForRefreshParentComposite.onEvent(null));
		return result;
	}

	@Override
	public TopLevelComposite<LogicalOperator, LogicalOperatorType> createTopLevelComposite(
			final Composite parent, final int swtStyle, final LogicalOperator model) {

		final LogicalOperatorModelAdapter<LogicalOperator, LogicalOperatorType> 
			logicalOperatorModelAdapter = this.createLogicalOperatorModelAdapter(model);
		
		final TopLevelComposite<LogicalOperator, LogicalOperatorType> result =
            new TopLevelComposite<>(parent, swtStyle,
                logicalOperatorModelAdapter,
                this.resourceAdapterFactory.getResourceAdapter(String.class).getLocalizedResource(this.addButtonText));
		
		result.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));

		result.addListenerForAdd(
            object -> {
				// create new logical operator
				LogicalOperator newLogicalOperator = new LogicalOperator(LogicalOperatorType.AND);
				newLogicalOperator.setParentLogicalOperator(model);
				logicalOperatorModelAdapter.addLogicalOperator(newLogicalOperator);
				createConditionBlockComposite(result, SWT.FLAT, newLogicalOperator);

				// refresh layout
				result.layout();
				parent.layout();
			});
		result.addListenerForAdd(
            object -> listenerForRefreshParentComposite.onEvent(null));
		return result;
	}

	@Override
	public LogicalOperatorModelAdapter<LogicalOperator, LogicalOperatorType> createLogicalOperatorModelAdapter(final LogicalOperator model) {

		LogicalOperatorModelAdapterImpl adapter = new LogicalOperatorModelAdapterImpl(model);
		adapter.setResourceAdapterForOperator(resourceAdapterFactory.getResourceAdapter(LogicalOperatorType.class));
		adapter.setLogicalOperatorsList(LogicalOperatorType.getList());
		
		if (this.listenerForMarkEditorState != null) {
			adapter.addPropertyChangeListener(event -> listenerForMarkEditorState.onEvent(event));
		}
		return adapter;
	}
	
	/**
	 * Allow to sort the operator in an ascending order.
	 */
	private class TagOperatorComparator implements Comparator<TagOperator> {

		private final Locale locale = CorePlugin.getDefault().getDefaultLocale();
		
		@Override
		public int compare(final TagOperator left, final TagOperator right) {
			return left.getName(locale).compareToIgnoreCase(right.getName(locale));
		}
		
	}

	@Override
	public ConditionModelAdapter<Condition, TagOperator> createConditionModelAdapter(final Condition model) {
		
		// This cast may a bit of a hack, but we have to rely on extension plugins extending the impl class.
		ConditionModelAdapterImpl adapter = (ConditionModelAdapterImpl) ConditionBuilderPluginHelper.createAdapter(model);
		adapter.setResourceAdapterForOperator(resourceAdapterFactory.getResourceAdapter(TagOperator.class));
		adapter.setResourceAdapterForTagDefinition(resourceAdapterFactory.getResourceAdapter(TagDefinition.class));
		adapter.setTagOperatorService(this.tagOperatorService);
		
		List<TagOperator> tagOperatorsList = new ArrayList<>(adapter.getTagDefinition().getValueType().getOperators());
		tagOperatorsList.sort(new TagOperatorComparator());
		adapter.setOperatorsList(tagOperatorsList);
		
		if (this.listenerForMarkEditorState != null) {
			adapter.addPropertyChangeListener(event -> listenerForMarkEditorState.onEvent(event));
		}
		return adapter;
	}

	/**
	 * @param addButtonText the addButtonText to set
	 */
	public void setAddButtonText(final String addButtonText) {
		this.addButtonText = addButtonText;
	}

	@Override
	public TopLevelComposite<LogicalOperator, LogicalOperatorType> 
		createFullUiFromModel(final Composite parent, final int swtStyle, final LogicalOperator logicalOperatorModel) {
		initializeModel();
		
		ConditionBlockComposite<LogicalOperator, TagDefinition, LogicalOperatorType, TagGroup> block;
		
		TopLevelComposite<LogicalOperator, LogicalOperatorType> top = 
			this.createTopLevelComposite(parent, swtStyle, logicalOperatorModel);
		
		if (logicalOperatorModel.getLogicalOperators() != null) {
			for (LogicalOperator operator : logicalOperatorModel.getLogicalOperators()) {
				block = this.createConditionBlockComposite(top, swtStyle, operator);
				if (operator.getConditions() != null) {
					for (Condition condition : operator.getConditions()) {
						this.createConditionRowComposite(block, swtStyle, condition);
					}
				}
			}
		}
		
		return top;
	}

	private void initializeModel() {
		
		this.tagOperatorService = ServiceLocator.getService(ContextIdNames.TAG_OPERATOR_SERVICE);
		TagDictionaryService tagDictionaryService = ServiceLocator.getService(ContextIdNames.TAG_DICTIONARY_SERVICE);
		TagGroupService tagGroupService = ServiceLocator.getService(ContextIdNames.TAG_GROUP_SERVICE);
		TagDictionary tagDictionary = tagDictionaryService.findByGuid(dictionary);
		
		if (tagDefinitionsList == null) {
			tagDefinitionsList = tagDictionary.getTagDefinitions();
		}
		// temporary solution
		Set<TagGroup> tagSet = new LinkedHashSet<>();
		for (TagDefinition tagDefinition : tagDefinitionsList) {
			if (tagDefinition.getGroup() == null) {
				continue;
			}
			TagGroup tagGroup = tagGroupService.findByGuid(tagDefinition.getGroup().getGuid());
			tagGroup.getTagDefinitions().retainAll(tagDefinitionsList);
			tagSet.add(tagGroup);
		}
		// end tmp
		tagGroupsList = tagSet;
	}

	/**
	 * Set locale.
	 * @param locale the locale to set
	 */
	public void setLocale(final Locale locale) {
		this.locale = locale;
	}

	/**
	 * Set the tag Operator Service.
	 * @param tagOperatorService the tagOperatorService to set
	 */
	public void setTagOperatorService(final GenericService<TagOperator> tagOperatorService) {
		this.tagOperatorService = tagOperatorService;
	}

	/**
	 * Set title.
	 * @param conditionBuilderTitle the conditionBuilderTitle to set
	 */
	public void setConditionBuilderTitle(final String conditionBuilderTitle) {
		this.conditionBuilderTitle = conditionBuilderTitle;
	}

	/**
	 * Set the data binding context.
	 * @param dataBindingContext the data binding context
	 */
	public void setDataBindingContext(final DataBindingContext dataBindingContext) {
		this.dataBindingContext = dataBindingContext;
	}
	

	/**
	 * Set listener for refresh parent composite. it should be invoked for any UI changes(add/remove).
	 * @param listener listener
	 */
	public void setListenerForRefreshParentComposite(final ActionEventListener<Object> listener) {
		this.listenerForRefreshParentComposite = listener;
	}

	/**
	 * @param listenerForMarkEditorState the listenerForMarkEditorState to set
	 */
	public void setListenerForMarkEditorState(final ActionEventListener<Object> listenerForMarkEditorState) {
		this.listenerForMarkEditorState = listenerForMarkEditorState;
	}

	/**
	 * Get ResourceAdapterFactory for changing default resource adapters.
	 * @return the resourceAdapterFactory
	 */
	public ResourceAdapterFactoryImpl getResourceAdapterFactory() {
		return resourceAdapterFactory;
	}

	/**
	 * Sets tag dictionary used for factory.
	 * @param dictionary - dictionary of the tags used.
	 */
	public void setTagDictionary(final String dictionary) {
		this.dictionary = dictionary;
	}

	/**
	 * Set the tag definitions list.
	 * @param tagDefinitionsList list of the tags to be set
	 */
	public void setTagDefinitionsList(final Set<TagDefinition> tagDefinitionsList) {
		this.tagDefinitionsList = tagDefinitionsList;
	}

}
