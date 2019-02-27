/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.conditionbuilder.valueeditor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.elasticpath.cmclient.core.CmSingletonUtil;
import com.elasticpath.cmclient.core.CorePlugin;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.constants.ValueTypeEnum;
import com.elasticpath.tags.domain.SelectableValue;
import com.elasticpath.tags.domain.TagDefinition;
import com.elasticpath.tags.service.SelectableTagValueFacade;

/**
 * Factory class that resolves symbolic value of {@link com.elasticpath.tags.domain.TagValueType} UI picker
 * into UI helper class.
 */
public final class SelectableValuesProviderFactory {


	private final SelectableTagValueFacade selectableTagValuesFacade;

	/**
	 * UI picker for category code.
	 */
	public static final String CATEGORY_PICKER = "CATEGORY_PICKER"; //$NON-NLS-1$
	/**
	 * UI picker for text fields with auto complete feature.
	 */
	public static final String AUTOCOMPLETE_NON_RESTRICTIVE = "AUTOCOMPLETE_NON_RESTRICTIVE"; //$NON-NLS-1$

	private final Map<String, ValueTypeEnum> tagValueTypeEnum = new HashMap<>();
	private final Map<String, SelectableValueResolver> tagValueResolver = new HashMap<>();
	private final Map<String, SelectableValuesProvider> valuesProviders = new HashMap<>();

	/**
	 * Returns instance of this class.
	 *
	 * @return - instance of this class
	 */
	public static SelectableValuesProviderFactory getInstance() {
		return CmSingletonUtil.getSessionInstance(SelectableValuesProviderFactory.class);
	}

	/**
	 * Constructor.
	 */
	private SelectableValuesProviderFactory() {
		selectableTagValuesFacade = ServiceLocator.getService(ContextIdNames.TAG_SELECTABLE_VALUES_SERVICE);

		tagValueTypeEnum.put(CATEGORY_PICKER, ValueTypeEnum.Category);
		tagValueResolver.put(CATEGORY_PICKER, new CategoryValuesProvider());
	}

	/**
	 * Check if editing support shall be performed via combobox.
	 *
	 * @param tagDefinition - tag definition.
	 * @return true if editing support shall be performed via combobox.
	 */
	public boolean isEditViaComboBox(final TagDefinition tagDefinition) {
		SelectableValuesProvider valueProvider = createValueProvider(tagDefinition);

		return !valueProvider.isEmpty();
	}

	/**
	 * Check if given tag definition has UI Picker.
	 *
	 * @param tagDefinition - tag definition.
	 * @return true if editing support for given tag guid allowed.
	 */
	public boolean hasUIPicker(final TagDefinition tagDefinition) {
		return StringUtils.isNotBlank(tagDefinition.getValueType().getUIPickerKey());
	}

	/**
	 * Get the SelectableValueResolver by given tag.
	 *
	 * @param tagDefinition given tag definition.
	 * @return instance of SelectableValueResolver
	 */
	public SelectableValueResolver getValueResolver(final TagDefinition tagDefinition) {
		if (null != tagDefinition) {
			return tagValueResolver.get(tagDefinition.getValueType().getUIPickerKey());
		}
		return null;
	}


	/**
	 * Get the ValueTypeEnum by given {@link TagDefinition}.
	 *
	 * @param tagDefinition tag definition from tag framework.
	 * @return instance of ValueTypeEnum, if editing support allowed or
	 * existing for given tag, otherwise null
	 */
	public ValueTypeEnum getValueTypeFromTagDefinition(final TagDefinition tagDefinition) {
		if (null != tagDefinition) {
			return tagValueTypeEnum.get(tagDefinition.getValueType().getUIPickerKey());
		}
		return null;
	}

	/**
	 * Get the ValuesProvider by given tag name.
	 *
	 * @param tagDefinition tag definition from tag framework.
	 * @return instance of ValuesProvider, if editing support allowed or
	 * existing for given tag, otherwise null
	 */
	public SelectableValuesProvider createValueProvider(final TagDefinition tagDefinition) {
		SelectableValuesProvider valuesProvider;
		if (valuesProviders.containsKey(tagDefinition.getGuid())) {
			valuesProvider = valuesProviders.get(tagDefinition.getGuid());
		} else {
			// init new provider
			final List<SelectableValue<Object>> list = selectableTagValuesFacade.getSelectableValues(tagDefinition.getValueType(),
					CorePlugin.getDefault().getDefaultLocale(), null);
			if (null == list) {
				// no values provider for given tag definition
				valuesProvider = new SelectableValuesProvider();
			} else {
				valuesProvider = new SelectableValuesProvider(list);
			}
			valuesProviders.put(tagDefinition.getGuid(), valuesProvider);
		}
		return valuesProvider;
	}


}
