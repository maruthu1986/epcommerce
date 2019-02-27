/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.conditionbuilder.extenders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import com.elasticpath.cmclient.conditionbuilder.adapter.ConditionModelAdapter;
import com.elasticpath.cmclient.conditionbuilder.adapter.impl.tag.ConditionModelAdapterImpl;
import com.elasticpath.cmclient.core.EpUiException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.IExtensionRegistry;

import com.elasticpath.tags.domain.Condition;
import com.elasticpath.tags.domain.TagDefinition;
import com.elasticpath.tags.domain.TagOperator;

/**
 * Extender helper for the ConditionBuilder plugin. 
 */
public final class ConditionBuilderPluginHelper {

	private static final String EXTENDER_ID = "ConditionBuilderExtender";

	private static final String PLUGIN_ID = "com.elasticpath.cmclient.conditionbuilder";

	private static final Logger LOG = Logger.getLogger(ConditionBuilderPluginHelper.class);

	private static final String COMPOSITE_FACTORY = "ConditionRowCompositeCreatorFactory";
	private static final String ADAPTOR_FACTORY = "ConditionModelAdapterFactory";
	private static final String CLASS_NAME_ATTRIBUTE = "className";
	
	private ConditionBuilderPluginHelper() {
		// Utility class constructor.
	}

	/**
	 * Gets the row composite creator for the TagDefinition from the extension plugin, if any.
	 * @param <M>  model type
	 * @param <OP>  operator type
	 * @param <M2> parent model adapter type
	 * @param <O2> parent operator type
	 * @param tagDefinition the tag definition
	 * @return the row composite creator
	 * @throws EpUiException when more than one extension plugin register a factory.
	 */
	@SuppressWarnings("PMD.ConsecutiveLiteralAppends")
	public static <M, OP, M2, O2> ConditionRowCompositeCreator<M, OP, M2, O2> getRowCompositeCreator(final TagDefinition tagDefinition) {
		
		final List<ConditionRowCompositeCreatorFactory> compositeFactories = findFactories(COMPOSITE_FACTORY);
		
		if (compositeFactories.isEmpty()) {
			return null;
		} else if (compositeFactories.size() > 1) {
            StringBuilder errorText = new StringBuilder("There are more than one implementation of ConditionRowCompositeCreatorFactory: [");
            compositeFactories
                .forEach(factory -> errorText.append(factory.getClass().getName()).append(','));
            errorText.append(']');
            LOG.error(errorText);
            throw new EpUiException(errorText.toString(), null);
		}

		return compositeFactories.iterator().next().getCreator(tagDefinition);	
	}
	
	/**
	 * Allows extenstion plugins to instantiate a ConditionModelAdapter.  If no interested plugins are present
	 * then the OOTB ConditionModelAdapterImpl is used. 
	 *
	 * @param model the Condition to use in the model adapter
	 * @return the model adapter
	 */
	@SuppressWarnings("PMD.ConsecutiveLiteralAppends")
	public static ConditionModelAdapter<Condition, TagOperator> createAdapter(final Condition model) {
		
		final List<ConditionModelAdapterFactory> compositeFactories = findFactories(ADAPTOR_FACTORY);

		if (compositeFactories.isEmpty()) {
			return new ConditionModelAdapterImpl(model);
		} else if (compositeFactories.size() > 1) {
            StringBuilder errorText = new StringBuilder("There are more than one implementation of ConditionModelAdapterFactory: [");
            compositeFactories
                .forEach(factory -> errorText.append(factory.getClass().getName()).append(','));
            errorText.append(']');
            LOG.error(errorText);
            throw new EpUiException(errorText.toString(), null);
		}
		
		return compositeFactories.iterator().next().createAdapter(model);
	}
	
	private static <T> List<T> findFactories(final String factoryName) {
		List<T> factories = new ArrayList<T>();
		
        List<IConfigurationElement> allConfigElements = getConfigElements();
        allConfigElements.stream()
                .filter(configurationElement -> factoryName.equals(configurationElement.getName()))
                .forEach(configElement -> {
                	T factory = instantiateFactory(configElement);
                    if (factory != null) {
                    	factories.add(factory);
                    }
                });

        return factories;
	}

	private static <T> T instantiateFactory(final IConfigurationElement configElement) {
        try {
            return (T) configElement.createExecutableExtension(CLASS_NAME_ATTRIBUTE);
        } catch (Exception e) {
            LOG.error("Error creating executable extension.", e);
        }
        return null;
    }

    private static List<IConfigurationElement> getConfigElements() {
        List<IConfigurationElement> allConfigElements = new ArrayList<>();
        IExtensionRegistry extensionRegistry = Platform.getExtensionRegistry();
        IExtensionPoint extensionPoint = extensionRegistry.getExtensionPoint(PLUGIN_ID, EXTENDER_ID);

        if (extensionPoint != null) {
            IExtension[] extensions = extensionPoint.getExtensions();
            for (IExtension extension : extensions) {
                allConfigElements.addAll(Arrays.asList(extension.getConfigurationElements()));
            }
        }
        return allConfigElements;
    }
}
